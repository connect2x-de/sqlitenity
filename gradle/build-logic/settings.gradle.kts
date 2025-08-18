@file:Suppress("UnstableApiUsage")

rootProject.name = "build-logic"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }

    versionCatalogs { register("libs") { from(files("../libs.versions.toml")) } }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }
