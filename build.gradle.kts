plugins {
    kotlin("multiplatform") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
    id("maven-publish")
}

group = "code.yousef"
version = "0.1.6"

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js(IR) {
        browser {
            binaries.executable()
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        nodejs()
    }
    
    sourceSets {
        var htmlVersion = "0.12.0"
        var coroutinesVersion = "1.7.3"
        var serializationVersion = "1.6.0"
        var quarkusVersion = "3.2.0.Final"
        // For Quarkus extension development

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html:$htmlVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
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
        
        val jsMain by getting {
            dependencies {
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

// Define standard build tasks
tasks.named("check") {
    dependsOn("jsJar")
}

// Define custom tasks for different run configurations
tasks.register("runDev") {
    group = "application"
    description = "Run the application in development mode"
    dependsOn("jsBrowserDevelopmentRun")
}

tasks.register("buildProd") {
    group = "application"
    description = "Build the application for production"
    dependsOn("jsBrowserProductionWebpack")
}

// Maven Publishing Configuration
publishing {
    publications {
        // Configure all publications to have the right Maven coordinates
        withType<MavenPublication> {
            // Configure Maven POM
            pom {
                name.set("Summon")
                description.set("A Kotlin Multiplatform UI library for creating web applications with a Compose-like syntax")
                url.set("https://github.com/yebaital/summon")
                
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                
                developers {
                    developer {
                        id.set("yousef")
                        name.set("Yousef")
                        email.set("contact@yousef.code")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/yebaital/summon.git")
                    developerConnection.set("scm:git:ssh://github.com:yebaital/summon.git")
                    url.set("https://github.com/yebaital/summon")
                }
            }
            
            // Ensure Gradle metadata is published
            suppressAllPomMetadataWarnings()
        }
    }
}

// Create a specific task for publishToMavenLocal that refreshes metadata
tasks.register("publishToMavenLocalWithMetadata") {
    group = "publishing"
    description = "Publishes to Maven Local with complete metadata"
    dependsOn("publishToMavenLocal")
} 