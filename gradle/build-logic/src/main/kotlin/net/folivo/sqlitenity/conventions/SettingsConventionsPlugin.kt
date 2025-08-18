package net.folivo.sqlitenity.conventions

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create

class SettingsConventionsPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        settings.extensions.create<ConventionsExtension>("conventions")

        settings.gradle.beforeProject { apply<ProjectConventionsPlugin>() }
    }
}
