@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyBuilder

fun KotlinHierarchyBuilder.withAndroid() {
    withCompilations { it.target.name == "android" }
}
