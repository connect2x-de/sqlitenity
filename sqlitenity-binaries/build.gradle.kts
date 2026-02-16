@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinNativeCompilerOptions
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.konan.target.KonanTarget

kotlin {
    withAll()
    sourceSets {
        androidMain.dependencies {
            implementation(project(path, configuration = "jvmRuntimeElements"))
        }
    }
}

val nativeDirectory = rootProject.isolated.projectDirectory.dir("libsqlitenity")

val archiveDirectory = nativeDirectory.dir("archive")
val androidDirectory = nativeDirectory.dir("lib/android")
val jvmDirectory = nativeDirectory.dir("lib/jvm")

@Suppress("UnnecessaryFullyQualifiedName")
kotlin.targets.withType<KotlinJvmTarget>().configureEach {
    compilations
        .named { it == "main" }
        .configureEach { defaultSourceSet.resources.srcDir(jvmDirectory) }
}

@Suppress("UnnecessaryFullyQualifiedName")
kotlin.targets.withType<KotlinNativeTarget>().configureEach {
    compilerOptions { includeBinary(staticLibrary(konanTarget)) }
}

androidComponents.onVariants { variant ->
    checkNotNull(variant.sources.jniLibs)
        .addStaticSourceDirectory(androidDirectory.asFile.canonicalPath)
}

fun staticLibrary(target: KonanTarget) = archiveDirectory.dir(target.name).file("libsqlitenity.a")

fun KotlinNativeCompilerOptions.includeBinary(file: RegularFile) =
    freeCompilerArgs.addAll("-include-binary", file.asFile.canonicalPath)
