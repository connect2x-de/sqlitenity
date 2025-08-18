kotlin {
    withAndroidxCompatible()

    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.sqlite)
            api(projects.sqlitenityApi)
        }
    }
}
