kotlin {
    withAll()
    sourceSets {
        native.configureEach { languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi") }

        commonMain.dependencies { implementation(projects.sqlitenityInterop) }
    }
}
