plugins {
    kotlin("jvm") version "2.1.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.codeyousef:summon-jvm:0.3.1.0")
    implementation("io.ktor:ktor-server-core:2.3.12")
    implementation("io.ktor:ktor-server-netty:2.3.12")
    implementation("io.ktor:ktor-server-html-builder:2.3.12")
    implementation("io.ktor:ktor-server-status-pages:2.3.12")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
    implementation("ch.qos.logback:logback-classic:1.4.14")
}

application {
    mainClass.set("code.yousef.example.todo.SimpleAppKt")
}

kotlin {
    jvmToolchain(17)
}