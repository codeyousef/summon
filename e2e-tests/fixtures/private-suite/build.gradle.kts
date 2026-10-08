plugins {
    kotlin("multiplatform") version "2.3.0"
}
repositories { mavenCentral() }
dependencyLocking { lockAllConfigurations() }
kotlin {
    jvmToolchain(21)
    jvm {
        compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) }
    }
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
            implementation("codes.yousef:summon:0.8.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
        }
        jvmMain.dependencies {
            implementation("codes.yousef:summon-aether:0.8.0")
        }
        commonTest.dependencies { implementation(kotlin("test")) }
    }
}
tasks.register<JavaExec>("renderPublicShell") {
    val compilation = kotlin.targets.getByName("jvm").compilations.getByName("main")
    dependsOn(compilation.compileTaskProvider)
    mainClass.set("codes.yousef.summon.fixture.PublicShellKt")
    classpath(compilation.output.allOutputs, compilation.runtimeDependencyFiles)
}
