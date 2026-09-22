@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)
@file:Suppress("UnstableApiUsage")

import com.android.build.api.withAndroid
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

kotlin {
    withAndroidxCompatible()
    js { browser() }
    wasmJs { browser() }

    applyDefaultHierarchyTemplate {
        common {
            group("nonWeb") {
                withJvm()
                withAndroid()
                withNative()
            }
        }
    }

    sourceSets {
        val nonWebMain by getting

        commonMain.dependencies { api(libs.androidx.sqlite) }
        nonWebMain.dependencies {
            implementation(projects.sqlitenityBundled)
            implementation(projects.sqlitenityCompat)
        }
        webMain.dependencies {
            implementation(projects.sqlitenityWebWorker)
            implementation(libs.androidx.sqliteWeb)
        }
    }
}
