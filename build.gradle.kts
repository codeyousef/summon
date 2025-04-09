plugins {
    kotlin("multiplatform") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"

}

group = "code.yousef"
version = "0.1.6"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

configurations {
    create("quarkusIntegration")
    create("quarkusDeployment")
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
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

                implementation("io.quarkus:quarkus-core:3.21.1")
                implementation("io.quarkus:quarkus-qute:3.21.1")
                implementation("io.quarkiverse.web-bundler:quarkus-web-bundler:1.8.1")
                implementation("io.quarkus:quarkus-kotlin:3.21.1")
                implementation("io.quarkus:quarkus-rest:3.21.1")
                implementation("io.quarkus:quarkus-vertx-http:3.21.1")
                
                // Add Quarkus deployment dependencies
                implementation("io.quarkus:quarkus-core-deployment:3.21.1")
                implementation("io.quarkus:quarkus-arc-deployment:3.21.1")

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
    "quarkusIntegration"("io.quarkus:quarkus-core:3.21.1")
    "quarkusIntegration"("io.quarkus:quarkus-qute:3.21.1")
    "quarkusIntegration"("io.quarkiverse.web-bundler:quarkus-web-bundler:1.8.1")
    "quarkusIntegration"("io.quarkus:quarkus-kotlin:3.21.1")
    "quarkusIntegration"("io.quarkus:quarkus-rest:3.21.1")
    "quarkusIntegration"("io.quarkus:quarkus-vertx-http:3.21.1")
    
    // Add deployment dependencies to the quarkusDeployment configuration
    "quarkusDeployment"("io.quarkus:quarkus-core-deployment:3.21.1")
    "quarkusDeployment"("io.quarkus:quarkus-arc-deployment:3.21.1")
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

// --- Custom Tasks & Publishing will be enabled later --- 