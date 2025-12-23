
apply(from = "../version.gradle.kts")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    `maven-publish`
    signing
}

kotlin {
    jvm()
    
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":summon-core"))
                implementation(libs.aether.core)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
