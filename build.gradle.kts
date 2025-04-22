plugins {
    kotlin("multiplatform") version "2.2.0-Beta1"
    kotlin("plugin.serialization") version "2.2.0-Beta1"
    `maven-publish`
}

group = "code.yousef"
version = "0.2.4.3"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

configurations {
    create("quarkusIntegration")
    create("quarkusDeployment")
    create("ktorIntegration")
    create("springBootIntegration")
}

kotlin {
    jvm {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
                }
            }
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.10.1")
                implementation(kotlin("stdlib-jdk8"))

                // Quarkus dependencies for integration
                implementation("io.quarkus:quarkus-core:3.6.5")
                implementation("io.quarkus:quarkus-qute:3.6.5")
                implementation("io.quarkus:quarkus-kotlin:3.6.5")
                implementation("io.quarkus:quarkus-vertx-http:3.6.5")
                implementation("io.quarkus:quarkus-resteasy-reactive:3.6.5")
                implementation("io.quarkus:quarkus-resteasy-reactive-jackson:3.6.5")
                implementation("io.quarkus:quarkus-websockets:3.6.5")
                implementation("io.quarkus:quarkus-arc:3.6.5")

                // Quarkus deployment dependencies
                implementation("io.quarkus:quarkus-core-deployment:3.6.5")
                implementation("io.quarkus:quarkus-arc-deployment:3.6.5")
                implementation("io.quarkus:quarkus-security-deployment:3.6.5")

                // Ktor dependencies
                implementation("io.ktor:ktor-server-core:2.3.7")
                implementation("io.ktor:ktor-server-netty:2.3.7")
                implementation("io.ktor:ktor-server-html-builder:2.3.7")
                implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")

                // Spring Boot dependencies
                implementation("org.springframework.boot:spring-boot-starter-web:3.2.3")
                implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.2.3")
                implementation("org.springframework.boot:spring-boot-starter-webflux:3.2.3")
                implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")

                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.12.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.8.1")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.12.0")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-js:2025.4.6")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions:1.0.1-pre.823")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:2025.4.6")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:2025.4.6-19.1.0")
                implementation(npm("core-js", "3.31.0"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.10.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-js:1.8.1")
            }
        }
        val jsTest by getting
    }
}

dependencies {
    // Quarkus integration dependencies
    "quarkusIntegration"("io.quarkus:quarkus-core:3.6.5")
    "quarkusIntegration"("io.quarkus:quarkus-qute:3.6.5")
    "quarkusIntegration"("io.quarkus:quarkus-kotlin:3.6.5")
    "quarkusIntegration"("io.quarkus:quarkus-vertx-http:3.6.5")
    "quarkusIntegration"("io.quarkus:quarkus-resteasy-reactive:3.6.5")
    "quarkusIntegration"("io.quarkus:quarkus-resteasy-reactive-jackson:3.6.5")
    "quarkusIntegration"("io.quarkus:quarkus-websockets:3.6.5")
    "quarkusIntegration"("io.quarkus:quarkus-arc:3.6.5")

    // Add deployment dependencies to the quarkusDeployment configuration
    "quarkusDeployment"("io.quarkus:quarkus-core-deployment:3.6.5")
    "quarkusDeployment"("io.quarkus:quarkus-arc-deployment:3.6.5")
    "quarkusDeployment"("io.quarkus:quarkus-security-deployment:3.6.5")

    // Add Ktor dependencies to the ktorIntegration configuration
    "ktorIntegration"("io.ktor:ktor-server-core:2.3.7")
    "ktorIntegration"("io.ktor:ktor-server-netty:2.3.7")
    "ktorIntegration"("io.ktor:ktor-server-html-builder:2.3.7")
    "ktorIntegration"("io.ktor:ktor-server-content-negotiation:2.3.7")
    "ktorIntegration"("io.ktor:ktor-serialization-kotlinx-json:2.3.7")

    // Add Spring Boot dependencies to the springBootIntegration configuration
    "springBootIntegration"("org.springframework.boot:spring-boot-starter-web:3.2.3")
    "springBootIntegration"("org.springframework.boot:spring-boot-starter-thymeleaf:3.2.3")
    "springBootIntegration"("org.springframework.boot:spring-boot-starter-webflux:3.2.3")
    "springBootIntegration"("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
}

kotlin.sourceSets.all {
    languageSettings {
        optIn("kotlin.RequiresOptIn")
        optIn("kotlinx.serialization.ExperimentalSerializationApi")
        enableLanguageFeature("ExpectActualClasses")
    }
}

tasks.getByName<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>("jsBrowserProductionWebpack") {
    mainOutputFileName = "summon.js"
}

tasks.register("verifyQuarkusIntegration") {
    doLast {
        println("Verifying Quarkus Integration dependencies...")
        configurations["quarkusIntegration"].files.forEach {
            println(" - ${it.name}")
        }
    }
}

tasks.register("verifyQuarkusDeployment") {
    doLast {
        println("Verifying Quarkus Deployment dependencies...")
        configurations["quarkusDeployment"].files.forEach {
            println(" - ${it.name}")
        }
    }
}

tasks.register("verifyKtorIntegration") {
    doLast {
        println("Verifying Ktor Integration dependencies...")
        configurations["ktorIntegration"].files.forEach {
            println(" - ${it.name}")
        }
    }
}

tasks.register("verifySpringBootIntegration") {
    doLast {
        println("Verifying Spring Boot Integration dependencies...")
        configurations["springBootIntegration"].files.forEach {
            println(" - ${it.name}")
        }
    }
}

// --- Custom Tasks & Publishing will be enabled later --- 

// Configure maven publishing
publishing {
    publications {
        // Publishing is automatically configured by the Kotlin Multiplatform plugin
        // This will create publications for all targets (jvm, js)
    }

    // Configure repositories if needed
    // repositories {
    //     maven {
    //         name = "MyRepo"
    //         url = uri("https://example.com/repository")
    //     }
    // }
} 
