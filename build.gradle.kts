plugins {
    kotlin("multiplatform") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
}

group = "code.yousef"
version = "0.1.0"

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
            binaries.executable()
        }
    }
    
    sourceSets {
        var htmlVersion = "0.12.0"
        var coroutinesVersion = "1.7.3"
        var serializationVersion = "1.6.0"

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html:$htmlVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$htmlVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutinesVersion")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:$htmlVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:1.0.0-pre.632")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$coroutinesVersion")
            }
        }
        val jsTest by getting
    }

    // Enable expect/actual classes feature
    targets.all {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + "-Xexpect-actual-classes"
            }
        }
    }
} 