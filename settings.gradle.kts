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
    id("de.connect2x.sqlitenity.conventions")
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

conventions {
    group = "de.connect2x.sqlitenity"
    version = "0.0.5"

    android {
        compileSdk = 36
        minSdk = 28
    }

    java {
        languageVersion = 11
    }

    publishing {
        pom {
            name = "SQLitenity"
            description = "Kotlin Multiplatform SQLite bindings"
            url = "https://gitlab.com/connect2x/sqlitenity"
            inceptionYear = "2025"

            licenses {
                license {
                    name = "Apache License 2.0"
                    url = "https://www.apache.org/licenses/LICENSE-2.0"
                }
            }

            developers {
                developer {
                    id = "connect2x"
                    name = "connect2x GmbH"
                    url = "https://gitlab.com/connect2x"
                }
                developer {
                    id = "fhilgers"
                    name = "Felix Hilgers"
                }
            }

            scm {
                url = "https://gitlab.com/connect2x/sqlitenity"
            }
        }
    }
}
