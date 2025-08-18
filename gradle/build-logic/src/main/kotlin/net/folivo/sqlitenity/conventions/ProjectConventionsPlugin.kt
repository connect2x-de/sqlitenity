@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

package net.folivo.sqlitenity.conventions

import com.android.build.api.dsl.KotlinMultiplatformAndroidDeviceTestCompilation
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
import com.android.build.gradle.api.KotlinMultiplatformAndroidPlugin
import com.ncorti.ktfmt.gradle.KtfmtExtension
import com.ncorti.ktfmt.gradle.KtfmtPlugin
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import conventions
import formattedVersion
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.credentials.HttpHeaderCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.authentication.http.HttpHeaderAuthentication
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import withAndroid

class ProjectConventionsPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            group = conventions.group.get()
            version = formattedVersion.get()

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
        buildUponDefaultConfig = true
        allRules = true
        source.setFrom(files(projectDir))
        config.from(rootDir.resolve("detekt.yml"))
    }
}

private fun Project.configurePublishing() {
    apply<MavenPublishPlugin>()
    apply<SigningPlugin>()

    extensions.configure<PublishingExtension> {
        repositories.maven {
            name = "GitLab"
            url = uri(conventions.publishing.registry.get())

            authentication.register<HttpHeaderAuthentication>("header")

            credentials(HttpHeaderCredentials::class) {
                name = "Job-Token"
                value = providers.environmentVariable("CI_JOB_TOKEN").orNull
            }
        }
    }

    extensions.configure<SigningExtension> {
        isRequired = false
        sign(extensions.getByType<PublishingExtension>().publications)
    }

    extensions.configure<MavenPublishBaseExtension> {
        publishToMavenCentral(automaticRelease = true)
    }
}
