@file:Suppress("UnstableApiUsage")

rootProject.name = "sqlitenity"

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

    includeBuild("gradle/build-logic")
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
}

include("sqlitenity-api")
include("sqlitenity-binaries")
include("sqlitenity-interop")
include("sqlitenity-bindings")
include("sqlitenity-bundled")
include("sqlitenity-compat")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
    id("net.folivo.sqlitenity.conventions")
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

conventions {
    group = "net.folivo"
    version = "0.0.3"

    android {
        compileSdk = 36
        minSdk = 28
    }

    java {
        languageVersion = 11
    }

    publishing {
        registry = "https://gitlab.com/api/v4/projects/72850047/packages/maven"
    }
}
