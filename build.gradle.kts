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
                enabled = true
            }
            binaries.executable()
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }
    
    sourceSets {
        var htmlVersion = "0.12.0"
        var coroutinesVersion = "1.7.3"
        var serializationVersion = "1.6.0"
        var quarkusVersion = "3.2.0.Final"
        var quarkusExtensionSdkVersion = "1.0.0" // For Quarkus extension development

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
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$htmlVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutinesVersion")
                
                // Quarkus integration dependencies (optional)
                compileOnly("io.quarkus:quarkus-core:$quarkusVersion")
                compileOnly("io.quarkus:quarkus-qute:$quarkusVersion")
                compileOnly("io.quarkus:quarkus-resteasy:$quarkusVersion")
                compileOnly("io.quarkus:quarkus-resteasy-jackson:$quarkusVersion")
                compileOnly("io.quarkus:quarkus-arc:$quarkusVersion")
                compileOnly("jakarta.enterprise:jakarta.enterprise.cdi-api:3.0.0")
                
                // Quarkus Extension Development SDK (for extension development)
                compileOnly("io.quarkus:quarkus-extension-processor:$quarkusVersion")
                compileOnly("io.quarkus:quarkus-arc-deployment:$quarkusVersion")
                compileOnly("io.quarkus:quarkus-core-deployment:$quarkusVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.jsoup:jsoup:1.15.3")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:$htmlVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:1.0.0-pre.632")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$coroutinesVersion")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:$htmlVersion")
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
} 