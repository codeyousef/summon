import java.security.MessageDigest
import java.util.*

// Apply version management
apply(from = "../version.gradle.kts")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    `maven-publish`
    signing
}

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
    withSourcesJar()
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
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.devtool = "source-map"
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(libs.kotlin.stdlib.common)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.html)
                implementation(libs.kotlinx.datetime)
                implementation(libs.atomicfu)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.jdk8)
                implementation(libs.kotlin.stdlib.jdk8)

                // Quarkus dependencies for integration
                implementation(libs.quarkus.core.jvm)
                implementation(libs.quarkus.qute.jvm)
                implementation(libs.quarkus.kotlin.jvm)
                implementation(libs.quarkus.vertx.http.jvm)
                implementation(libs.quarkus.resteasy.reactive.jvm)
                implementation(libs.quarkus.resteasy.reactive.jackson.jvm)
                implementation(libs.quarkus.websockets.jvm)
                implementation(libs.quarkus.arc.jvm)

                // Quarkus deployment dependencies
                implementation(libs.quarkus.core.deployment.jvm)
                implementation(libs.quarkus.arc.deployment.jvm)
                implementation(libs.quarkus.security.deployment.jvm)

                // Ktor dependencies
                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.server.html.builder)
                implementation(libs.ktor.server.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                // Spring Boot dependencies
                implementation(libs.spring.boot.starter.web.jvm)
                implementation(libs.spring.boot.starter.thymeleaf.jvm)
                implementation(libs.spring.boot.starter.webflux.jvm)
                implementation(libs.reactor.kotlin.extensions.jvm)

                implementation(libs.kotlinx.html.jvm)
                implementation(libs.kotlinx.serialization.json.jvm)
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation(libs.kotlinx.html.js)
                implementation(libs.kotlin.js)
                implementation(libs.kotlin.extensions)
                implementation(libs.kotlin.browser)
                implementation(libs.kotlin.react.dom)
                implementation(npm("core-js", "3.31.0"))
                implementation(libs.kotlinx.coroutines.core.js)
                implementation(libs.kotlinx.serialization.json.js)
                implementation(libs.kotlin.stdlib.js)
                implementation(libs.kotlin.stdlib.common)
            }
        }
        val jsTest by getting
    }
}

dependencies {
    // Quarkus integration dependencies
    "quarkusIntegration"(libs.quarkus.core)
    "quarkusIntegration"(libs.quarkus.qute)
    "quarkusIntegration"(libs.quarkus.kotlin)
    "quarkusIntegration"(libs.quarkus.vertx.http)
    "quarkusIntegration"(libs.quarkus.resteasy.reactive)
    "quarkusIntegration"(libs.quarkus.resteasy.reactive.jackson)
    "quarkusIntegration"(libs.quarkus.websockets)
    "quarkusIntegration"(libs.quarkus.arc)

    // Add deployment dependencies to the quarkusDeployment configuration
    "quarkusDeployment"(libs.quarkus.core.deployment)
    "quarkusDeployment"(libs.quarkus.arc.deployment)
    "quarkusDeployment"(libs.quarkus.security.deployment)

    // Add Ktor dependencies to the ktorIntegration configuration
    "ktorIntegration"(libs.ktor.server.core)
    "ktorIntegration"(libs.ktor.server.netty)
    "ktorIntegration"(libs.ktor.server.html.builder)
    "ktorIntegration"(libs.ktor.server.content.negotiation)
    "ktorIntegration"(libs.ktor.serialization.kotlinx.json)

    // Add Spring Boot dependencies to the springBootIntegration configuration
    "springBootIntegration"(libs.spring.boot.starter.web)
    "springBootIntegration"(libs.spring.boot.starter.thymeleaf)
    "springBootIntegration"(libs.spring.boot.starter.webflux)
    "springBootIntegration"(libs.reactor.kotlin.extensions)
}

kotlin.sourceSets.all {
    languageSettings {
        optIn("kotlin.RequiresOptIn")
        optIn("kotlinx.serialization.ExperimentalSerializationApi")
        optIn("kotlin.ExperimentalMultiplatform")
        optIn("kotlin.js.ExperimentalJsExport")
    }
}

tasks.getByName<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>("jsBrowserProductionWebpack") {
    mainOutputFileName = "summon-hydration.js"
}

// Copy the generated JS bundle to JVM resources for serving
tasks.register<Copy>("copyJsHydrationBundle") {
    dependsOn("jsBrowserDistribution")
    
    val jsOutputFile = layout.buildDirectory.file("dist/js/productionExecutable/summon-hydration.js")
    from(jsOutputFile)
    into(layout.buildDirectory.dir("resources/jvm/main/static"))
    
    // Only run if the JS file exists
    onlyIf {
        jsOutputFile.get().asFile.exists()
    }
    
    doLast {
        println("Copied Summon hydration bundle to JVM resources")
    }
}

// Ensure the JS bundle is copied before JVM resources are processed
tasks.named<ProcessResources>("jvmProcessResources") {
    dependsOn("copyJsHydrationBundle")
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

// Load properties from local.properties if it exists (fallback)
val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        load(localFile.inputStream())
    }
}

// Get credentials with priority: local.properties > gradle.properties/-P > environment variables
val githubUser = localProperties.getProperty("gpr.user")
    ?: project.findProperty("gpr.user") as String?
    ?: System.getenv("GITHUB_ACTOR")

val githubToken = localProperties.getProperty("gpr.key")
    ?: project.findProperty("gpr.key") as String?
    ?: System.getenv("GITHUB_TOKEN")

// Configure publishing for Maven Central only
publishing {
    publications {
        withType<MavenPublication> {
            artifactId = "summon-core"
            pom {
                name.set("Summon Core")
                description.set("A Kotlin Multiplatform UI framework for building web applications")
                url.set("https://github.com/codeyousef/summon")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("codeyousef")
                        name.set("codeyousef")
                        url.set("https://github.com/codeyousef/")
                    }
                }
                scm {
                    url.set("https://github.com/codeyousef/summon/")
                    connection.set("scm:git:git://github.com/codeyousef/summon.git")
                    developerConnection.set("scm:git:ssh://git@github.com/codeyousef/summon.git")
                }
            }
        }
    }
}

// Task to run all tests before publishing
tasks.register("testAll") {
    dependsOn("allTests")
    group = "verification"
    description = "Run all tests for all targets"
}

// Task to clean node_modules directory
tasks.register<Delete>("cleanNodeModules") {
    group = "build"
    description = "Delete the node_modules directory"
    delete(file("${buildDir}/js/node_modules"))
    delete(file("${buildDir}/js/packages"))
}

// Make clean task depend on cleanNodeModules
tasks.named("clean") {
    dependsOn("cleanNodeModules")
}

// Global Kotlin compiler flags to reduce warnings
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes",
            "-Xuse-fir-lt=false",
        )
    }
}