plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.allopen") version "2.1.20"
    id("io.quarkus") version "3.6.5"
}

repositories {
    mavenCentral()
    mavenLocal() // For Summon library published locally
}

val quarkusVersion = "3.6.5"

dependencies {
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:${quarkusVersion}"))
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    implementation("io.quarkus:quarkus-qute")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-websockets")
    implementation("io.quarkus:quarkus-scheduler")
    implementation("io.quarkus:quarkus-cache")
    implementation("io.quarkus:quarkus-security")
    implementation("io.quarkus:quarkus-undertow") // Add undertow for servlet support

    // Kotlin dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Summon dependencies
    implementation("code.yousef:summon:0.2.3.0")
    implementation("code.yousef:summon-jvm:0.2.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.12.0")

    // Jakarta Servlet API
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
}

// Apply the allopen plugin for Quarkus
allOpen {
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.ws.rs.Path")
    annotation("io.quarkus.qute.TemplateExtension")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        javaParameters = true
        freeCompilerArgs = listOf("-Xskip-metadata-version-check")
    }
} 
