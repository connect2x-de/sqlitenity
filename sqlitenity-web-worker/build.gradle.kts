@file:OptIn(ExperimentalWasmDsl::class)
@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

kotlin {
    js {
        browser()
        useEsModules()
    }
    wasmJs {
        browser()
        useEsModules()
    }

    sourceSets {
        commonMain {
            resources.srcDir("worker")
            resources.srcDir(
                rootProject.isolated.projectDirectory.dir("libsqlitenity/archive/wasm")
            )
        }

        commonMain.dependencies { api(libs.kotlinx.browser) }
    }
}
