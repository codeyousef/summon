plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
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
                implementation("io.github.codeyousef:summon-core:0.4.0.4")
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