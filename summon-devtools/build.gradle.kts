apply(from = "../version.gradle.kts")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.binary.compatibility.validator)
    `maven-publish`
}

kotlin {
    withSourcesJar()

    jvm {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
                }
            }
        }
    }

    js(IR) {
        browser()
        nodejs()
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":summon-core"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
        val webMain by creating {
            dependsOn(commonMain.get())
        }
        jsMain {
            dependsOn(webMain)
            dependencies {
                implementation(libs.kotlin.browser)
            }
        }
        wasmJsMain {
            dependsOn(webMain)
            dependencies {
                implementation(libs.kotlinx.browser)
            }
        }
    }
}
