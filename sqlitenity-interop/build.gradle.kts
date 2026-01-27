@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

kotlin {
    withAll()

    sourceSets.native.configureEach { languageSettings.optIn("kotlin.native.SymbolNameIsInternal") }
}
