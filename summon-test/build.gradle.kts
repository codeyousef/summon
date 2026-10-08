apply(from = "../version.gradle.kts")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.binary.compatibility.validator)
    `maven-publish`
}

kotlin {
    withSourcesJar()

    jvm {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            }
        }
    }
    js(IR) { browser(); nodejs() }
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs { browser(); nodejs() }

    sourceSets {
        commonMain.dependencies {
            api(project(":summon-core"))
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies { implementation(kotlin("test")) }
        jvmMain.dependencies {
            implementation("org.jsoup:jsoup:1.18.3")
            implementation(libs.kotlinx.serialization.json)
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
