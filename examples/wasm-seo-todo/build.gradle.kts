plugins {
    kotlin("multiplatform")
}

kotlin {
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "todo-app.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        val wasmJsMain by getting {
            dependencies {
                implementation(project(":summon-core"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xopt-in=kotlin.ExperimentalStdlibApi",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
}