kotlin {
    withAll()

    sourceSets {
        commonMain.dependencies {
            api(projects.sqlitenityApi)

            implementation(projects.sqlitenityBindings)
            implementation(projects.sqlitenityBinaries)
        }

        commonTest.dependencies { implementation(libs.kotlin.test) }

        androidDeviceTest.dependencies { implementation(libs.androidx.test.runner) }
    }
}
