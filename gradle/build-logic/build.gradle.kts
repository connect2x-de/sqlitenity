plugins {
    `kotlin-dsl`
    alias(libs.plugins.ktfmt)
    alias(libs.plugins.detekt)
}

kotlin { jvmToolchain(21) }

ktfmt {
    useClassloaderIsolation = true
    kotlinLangStyle()
}

detekt {
    buildUponDefaultConfig = true
    allRules = true
    source.setFrom(files(projectDir))
    config.from("../../detekt.yml")
}

gradlePlugin {
    plugins {
        register("conventions") {
            id = "net.folivo.sqlitenity.conventions"
            version = "builtin"
            implementationClass = "net.folivo.sqlitenity.conventions.SettingsConventionsPlugin"
        }
    }
}

dependencies {
    implementation(libs.plugins.kotlin.multiplatform.asLibrary())
    implementation(libs.plugins.android.multiplatform.library.asLibrary())
    implementation(libs.plugins.ktfmt.asLibrary())
    implementation(libs.plugins.detekt.asLibrary())
    implementation(libs.plugins.publish.asLibrary())
}

fun Provider<PluginDependency>.asLibrary() = map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}
