plugins {
    kotlin("multiplatform") version "2.3.0"
}
repositories { mavenCentral() }
dependencyLocking { lockAllConfigurations() }
kotlin {
    js(IR) {
        browser { commonWebpackConfig { outputFileName = "fixture.js" } }
        binaries.executable()
    }
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser { commonWebpackConfig { outputFileName = "fixture.js" } }
        binaries.executable()
    }
    sourceSets {
        commonMain.dependencies {
            implementation("codes.yousef:summon:0.7.0.4")
            implementation("codes.yousef:summon-test:0.7.0.4")
        }
        val webMain by creating { dependsOn(commonMain.get()) }
        jsMain { dependsOn(webMain) }
        wasmJsMain {
            dependsOn(webMain)
            dependencies { implementation("org.jetbrains.kotlinx:kotlinx-browser:0.5.0") }
        }
    }
}
