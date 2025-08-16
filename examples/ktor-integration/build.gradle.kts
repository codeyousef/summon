// Apply version helper to get Summon version information
apply(from = "../../version-helper.gradle.kts")

plugins {
    kotlin("jvm") version "2.2.0-RC2"
    kotlin("plugin.serialization") version "2.2.0-Beta1"
    id("io.ktor.plugin") version "3.0.2"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Summon JVM library
    implementation(project.extra["summonJvmDependency"] as String)
    
    // Ktor server dependencies
    implementation("io.ktor:ktor-server-core:3.0.2")
    implementation("io.ktor:ktor-server-netty:3.0.2")
    implementation("io.ktor:ktor-server-html-builder:3.0.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.0.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.2")
    implementation("io.ktor:ktor-server-sessions:3.0.2")
    implementation("io.ktor:ktor-server-websockets:3.0.2")
    implementation("io.ktor:ktor-server-sse:3.0.2")
    implementation("io.ktor:ktor-server-cors:3.0.2")
    implementation("io.ktor:ktor-server-call-logging:3.0.2")
    implementation("io.ktor:ktor-server-default-headers:3.0.2")
    implementation("io.ktor:ktor-server-status-pages:3.0.2")
    
    // Database dependencies
    implementation("org.jetbrains.exposed:exposed-core:0.50.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.50.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.1")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.50.1")
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.8")
    
    // BCrypt for password hashing
    implementation("org.mindrot:jbcrypt:0.4")
    
    // Kotlinx DateTime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    
    // Testing
    testImplementation("io.ktor:ktor-server-test-host:3.0.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.2.0-RC2")
}

application {
    mainClass.set("code.yousef.summon.examples.ktor.ApplicationKt")
}

ktor {
    fatJar {
        archiveFileName.set("ktor-todo-app.jar")
    }
    docker {
        jreVersion.set(io.ktor.plugin.features.JreVersion.JRE_17)
        localImageName.set("ktor-todo-app")
        imageTag.set("latest")
        portMappings.set(listOf(
            io.ktor.plugin.features.DockerPortMapping(
                80,
                8080,
                io.ktor.plugin.features.DockerPortMappingProtocol.TCP
            )
        ))
    }
}

// Configure compile task for better error reporting
tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.compileTestKotlin {
    kotlinOptions {
        jvmTarget = "17"
    }
}