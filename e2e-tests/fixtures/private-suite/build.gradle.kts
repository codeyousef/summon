plugins {
    kotlin("multiplatform") version "2.3.0"
}
repositories { mavenCentral() }
dependencyLocking { lockAllConfigurations() }
kotlin {
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
            implementation("codes.yousef:summon:0.7.0.4")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
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
