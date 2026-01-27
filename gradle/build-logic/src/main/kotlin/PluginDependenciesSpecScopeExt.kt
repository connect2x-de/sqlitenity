@file:Suppress("MissingPackageDeclaration")

import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.PluginDependenciesSpecScope
import org.gradle.plugin.use.PluginDependency
import org.gradle.plugin.use.PluginDependencySpec

fun PluginDependenciesSpecScope.builtin(alias: Provider<PluginDependency>): PluginDependencySpec =
    id(alias.get().pluginId)
