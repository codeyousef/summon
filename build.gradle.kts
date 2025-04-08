plugins {
    kotlin("multiplatform") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
    id("maven-publish")
}

group = "code.yousef"
version = "0.1.6"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm()
    js {
        browser()
    }
    
    sourceSets {
        val htmlVersion = "0.12.0"
        val coroutinesVersion = "1.7.3"
        val serializationVersion = "1.6.0"
        val composeVersion = "1.5.10"

        val commonMain by getting {
            dependencies {
                // Core Kotlin libraries
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
                
                // Keep kotlinx.html for common code that uses it
                implementation("org.jetbrains.kotlinx:kotlinx-html:$htmlVersion")
            }
        }
        
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$htmlVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutinesVersion")
            }
        }
        
        val jsMain by getting {
            dependencies {
                // Compose HTML is only available for JS target
                implementation("org.jetbrains.compose.html:html-core:$composeVersion")
                implementation("org.jetbrains.compose.html:html-svg:$composeVersion")
                
                // JS-specific dependencies
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:$htmlVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:1.0.0-pre.632")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$coroutinesVersion")
            }
        }
    }

    // Enable expect/actual classes feature
    targets.all {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + "-Xexpect-actual-classes"
            }
        }
    }

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
}

// --- Keep Custom Tasks & Publishing commented out for now ---
// We'll uncomment these once we have the build working
// task runDev...
// task buildProd...
// publishing... 