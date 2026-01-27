@file:OptIn(ExperimentalKotlinGradlePluginApi::class)
@file:Suppress("MissingPackageDeclaration")

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyBuilder

fun KotlinHierarchyBuilder.withAndroid() {
    withCompilations { it.target.name == "android" }
}
