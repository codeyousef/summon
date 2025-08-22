import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "2.2.0"
}

// Apply version helper to get Summon version from parent project
// NOTE: If using this example standalone (downloaded separately), you can replace this with:
// val summonVersion = "0.2.9.1" // Use latest version
apply(from = "../../version-helper.gradle.kts")

group = "code.yousef.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // Spring Boot dependencies
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    
    // JWT dependencies
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
    
    // H2 Database
    implementation("com.h2database:h2")
    
    // BCrypt for password hashing
    implementation("org.springframework.security:spring-security-crypto")
    
    // Kotlin dependencies
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // DateTime handling
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    
    // Summon JVM library - using local Maven repository
    // IMPORTANT: Run './gradlew :summon-core:publishToMavenLocal' from the root project first
    implementation("io.github.codeyousef:summon-jvm:0.2.9.1")
    
    // kotlinx-html for SimpleComponents.kt (fallback HTML generation)
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.12.0")
    
    // Development dependencies
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Configure main class for Spring Boot
springBoot {
    mainClass.set("code.yousef.example.springboot.SpringBootTodoApplicationKt")
}