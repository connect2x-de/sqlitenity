@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

package de.connect2x.sqlitenity.conventions

import com.android.build.api.dsl.KotlinMultiplatformAndroidDeviceTestCompilation
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
import com.android.build.gradle.api.KotlinMultiplatformAndroidPlugin
import com.ncorti.ktfmt.gradle.KtfmtExtension
import com.ncorti.ktfmt.gradle.KtfmtPlugin
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import commitSha
import conventions
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.plugin.DetektPlugin
import environment
import formattedVersion
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.credentials.HttpHeaderCredentials
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.authentication.http.HttpHeaderAuthentication
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.tasks.LogHtmlPublicationLinkTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import withAndroid

class ProjectConventionsPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            group = conventions.group.get()
            version = formattedVersion.get()

            configureDokka()

            if (this != rootProject) configureSubProject()
        }
    }
}

private fun Project.configureSubProject() {
    configureKotlinMultiplatform()
    configureKtfmt()
    configureDetekt()
    configurePublishing()
}

private fun Project.configureKotlinMultiplatform() {
    apply<KotlinMultiplatformPluginWrapper>()
    apply<KotlinMultiplatformAndroidPlugin>()

    extensions.configure<KotlinMultiplatformExtension> {
        jvmToolchain {
            languageVersion.set(conventions.java.languageVersion.map(JavaLanguageVersion::of))
        }

        applyDefaultHierarchyTemplate {
            common {
                group("jni") {
                    withJvm()
                    withAndroid()
                }
            }
        }

        targets.withType<KotlinMultiplatformAndroidLibraryTarget>().configureEach {
            withHostTest {}
            withDeviceTestBuilder { sourceSetTreeName = "test" }

            compilations.withType<KotlinMultiplatformAndroidDeviceTestCompilation> {
                instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }
        }
    }

    extensions.configure<KotlinMultiplatformAndroidComponentsExtension> {
        finalizeDsl {
            it.namespace = "${group}.${name.replace("-", ".")}"
            it.compileSdk = conventions.android.compileSdk.get()
            it.minSdk = conventions.android.minSdk.get()
        }
    }
}

private fun Project.configureKtfmt() {
    apply<KtfmtPlugin>()

    extensions.configure<KtfmtExtension> {
        useClassloaderIsolation.set(true)
        kotlinLangStyle()
    }
}

private fun Project.configureDetekt() {
    apply<DetektPlugin>()

    extensions.configure<DetektExtension> {
        buildUponDefaultConfig.set(true)
        allRules.set(true)
        source.setFrom(files(projectDir))
        config.from(rootDir.resolve("detekt.yml"))
    }
}

private fun Project.configureDokka() {
    apply<DokkaPlugin>()

    if (this == rootProject) {
        for (subproject in subprojects) dependencies.add("dokka", subproject)
    }

    extensions.configure<DokkaExtension> {
        dokkaGeneratorIsolation.set(ClassLoaderIsolation())

        val relativePath = projectDir.relativeTo(rootDir).path

        val packageList =
            tasks
                .register<WriteAndroidxPackageList>("generateAndroidxPackageList")
                .flatMap(WriteAndroidxPackageList::packageList)
                .map { "${it.asFile.toURI()}" }

        dokkaSourceSets.configureEach {
            sourceLink {
                val subDirectory = "src/$name/kotlin"
                val baseUrl = "https://gitlab.com/connect2x/sqlitenity/-/blob"

                localDirectory.set(layout.projectDirectory.dir(subDirectory))
                remoteLineSuffix.set("#L")

                when (environment.get()) {
                    EnvironmentKind.LOCAL -> remoteUrl("file://${projectDir}/$subDirectory")
                    EnvironmentKind.DEV ->
                        remoteUrl("$baseUrl/${commitSha.get()}/$relativePath/$subDirectory")
                    EnvironmentKind.RELEASE ->
                        remoteUrl("$baseUrl/v$version/$relativePath/$subDirectory")
                }
            }

            externalDocumentationLinks.register("androidx.sqlite") {
                url("https://developer.android.com/reference/kotlin/androidx/sqlite/")
                packageListUrl(packageList)
            }
        }
    }

    tasks.withType<LogHtmlPublicationLinkTask>().configureEach { enabled = false }
}

private fun Project.configurePublishing() {
    apply<MavenPublishPlugin>()
    apply<SigningPlugin>()

    when (environment.get()) {
        EnvironmentKind.LOCAL -> {}
        EnvironmentKind.DEV -> {
            configureGitlabPublishing()
        }
        EnvironmentKind.RELEASE -> {
            configureGitlabPublishing()
            configureMavenCentralRelease()
        }
    }
}

private fun Project.configureMavenCentralRelease() {
    extensions.configure<MavenPublishBaseExtension> {
        publishToMavenCentral()
        signAllPublications()
    }

    tasks.named { it == "publish" }.configureEach { dependsOn("publishAndReleaseToMavenCentral") }
}

private fun Project.configureGitlabPublishing() {
    val api = providers.environmentVariable("CI_API_V4_URL").get()
    val id = providers.environmentVariable("CI_PROJECT_ID").get()
    val jobToken = providers.environmentVariable("CI_JOB_TOKEN").get()

    extensions.configure<PublishingExtension> {
        repositories.maven {
            name = "GitLab"
            url = uri("$api/projects/$id/packages/maven")

            authentication.register<HttpHeaderAuthentication>("header")

            credentials(HttpHeaderCredentials::class) {
                name = "Job-Token"
                value = jobToken
            }
        }

        publications.withType<MavenPublication>().configureEach {
            pom { conventions.publishing.configurePom(this) }
        }
    }
}

private open class WriteAndroidxPackageList
@Inject
constructor(projectLayout: ProjectLayout, objectFactory: ObjectFactory) : DefaultTask() {

    @get:OutputFile
    val packageList: RegularFileProperty =
        objectFactory
            .fileProperty()
            .convention(projectLayout.buildDirectory.file("androidx-package-list"))

    @TaskAction
    fun generate() {
        packageList
            .get()
            .asFile
            .writeText(
                $$"""
                $dokka.format:html-v1
                $dokka.linkExtension:html
                $dokka.location:androidx.sqlite/SQLiteConnection///PointingToDeclaration/SQLiteConnection
                $dokka.location:androidx.sqlite/SQLiteDriver///PointingToDeclaration/SQLiteDriver
                $dokka.location:androidx.sqlite/SQLiteStatement///PointingToDeclaration/SQLiteStatement
                androidx.sqlite
                """
                    .trimIndent()
            )
    }
}
