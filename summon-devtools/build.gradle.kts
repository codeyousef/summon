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
        nodejs {
            testTask {
                useMocha { timeout = "30s" }
                val setupScript = rootProject.file("summon-core/src/jsTest/resources/setup-happydom.cjs").absolutePath
                val existingNodeOptions = environment["NODE_OPTIONS"]?.takeIf { it.isNotBlank() }
                environment("NODE_OPTIONS", listOfNotNull(existingNodeOptions, "--require=$setupScript").joinToString(" "))
            }
        }
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
        jsTest.dependencies {
            implementation(npm("happy-dom", "14.10.3"))
        }
        val webMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.kotlinx.browser)
            }
        }
        jsMain {
            dependsOn(webMain)
        }
        wasmJsMain {
            dependsOn(webMain)
        }
    }
}
