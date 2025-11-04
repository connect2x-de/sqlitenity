@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    builtin(libs.plugins.kotlin.multiplatform)
    builtin(libs.plugins.android.multiplatform.library)
    builtin(libs.plugins.ktfmt)
}

kotlin {
    withAll()

    sourceSets.native.configureEach { languageSettings.optIn("kotlin.native.SymbolNameIsInternal") }
}
