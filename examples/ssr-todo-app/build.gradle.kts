plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

repositories {
    mavenCentral()
    // mavenLocal() // Comment out for production, only use if testing local builds
}

val ktorVersion = "2.3.12"

dependencies {
    implementation("io.github.codeyousef:summon-jvm:0.3.2.2")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Serialization support
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("io.ktor:ktor-serialization-kotlinx:$ktorVersion")

    // Testing dependencies
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}

application {
    mainClass.set("code.yousef.example.todo.TodoAppKt")
}

kotlin {
    jvmToolchain(17)
}