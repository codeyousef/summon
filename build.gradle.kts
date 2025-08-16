plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

allprojects {
    group = "io.github.codeyousef"
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

tasks.register("buildCore") {
    dependsOn(":summon-core:build")
    description = "Build core library only"
}

tasks.register("buildCli") {
    dependsOn(":summon-cli:build")
    description = "Build CLI tool only"
}

tasks.register("buildAll") {
    dependsOn(":summon-core:build", ":summon-cli:build")
    description = "Build all core modules"
}

tasks.register("publishLocal") {
    dependsOn(":summon-core:publishToMavenLocal", ":summon-cli:publishToMavenLocal")
    description = "Publish all publications to the local Maven repository"
}

tasks.register("buildCliExecutables") {
    group = "build"
    description = "Build CLI tool native executables and Shadow JAR"
    dependsOn(":summon-cli:buildNativeExecutable", ":summon-cli:shadowJar")

    doLast {
        println("✅ CLI tool executables built successfully!")
        println("📁 Shadow JAR: summon-cli/build/libs/")
        println("🔧 Native executable: summon-cli/build/native/nativeCompile/")
        println("💡 Use these artifacts for GitHub Releases")
    }
}