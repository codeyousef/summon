plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/releases/")
}

kotlin {
    js(IR) {
        compilerOptions {
            moduleName.set("portfolio-example")
        }
        browser {
            commonWebpackConfig {
                outputFileName = "portfolio.js"
                cssSupport {
                    enabled.set(true)
                }
            }
            webpackTask {
                mainOutputFileName.set("portfolio.js")
            }
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                // Use project reference when building as part of the workspace
                implementation(project(":summon-core"))
                
                // Kotlinx serialization for data
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            }
        }
    }
}

tasks.named<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>("jsBrowserDevelopmentRun") {
    devServerProperty.set(
        devServerProperty.getOrElse(org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.DevServer()).copy(
            open = false,
            port = 8082
        )
    )
}
