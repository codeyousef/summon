import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "2.2.0"
}

// Use fixed version instead of version-helper to avoid parent project conflicts
// NOTE: Using JVM-specific artifact as the multiplatform core artifact (0.3.0.0) only contains
// Kotlin Native metadata (.knm files) and lacks JVM bytecode needed for Spring Boot
val summonVersion = "0.2.9.1"

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
    // NOTE: Using summon-jvm artifact instead of summon-core because the multiplatform
    // summon-core:0.3.0.0 only contains .knm files, not JVM .class files
    // IMPORTANT: Run './gradlew :summon-jvm:publishToMavenLocal' from the root project first
    implementation("io.github.codeyousef:summon-jvm:$summonVersion")
    
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