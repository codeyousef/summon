plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":summon-core"))
    
    // Ktor server - use version catalog
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.html.builder)
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.11")
}

application {
    mainClass.set("codes.yousef.example.ssrhydration.MainKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
