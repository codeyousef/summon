import org.gradle.api.publish.maven.MavenPublication
import java.util.Properties

// Apply version management
apply(from = "version.gradle.kts")

plugins {
    kotlin("multiplatform") version "2.2.0-RC2"
    kotlin("plugin.serialization") version "2.2.0-RC2"
    `maven-publish`
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
                implementation("org.jetbrains.kotlinx:atomicfu:0.23.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2") // Or the latest compatible version

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
    "quarkusIntegration"("io.quarkus:quarkus-core:3.23.0")
    "quarkusIntegration"("io.quarkus:quarkus-qute:3.23.0")
    "quarkusIntegration"("io.quarkus:quarkus-kotlin:3.23.0")
    "quarkusIntegration"("io.quarkus:quarkus-vertx-http:3.23.0")
    "quarkusIntegration"("io.quarkus:quarkus-resteasy-reactive:3.23.0")
    "quarkusIntegration"("io.quarkus:quarkus-resteasy-reactive-jackson:3.23.0")
    "quarkusIntegration"("io.quarkus:quarkus-websockets:3.23.0")
    "quarkusIntegration"("io.quarkus:quarkus-arc:3.23.0")

    // Add deployment dependencies to the quarkusDeployment configuration
    "quarkusDeployment"("io.quarkus:quarkus-core-deployment:3.23.0")
    "quarkusDeployment"("io.quarkus:quarkus-arc-deployment:3.23.0")
    "quarkusDeployment"("io.quarkus:quarkus-security-deployment:3.23.0")

    // Add Ktor dependencies to the ktorIntegration configuration
    "ktorIntegration"("io.ktor:ktor-server-core:3.1.3")
    "ktorIntegration"("io.ktor:ktor-server-netty:3.1.3")
    "ktorIntegration"("io.ktor:ktor-server-html-builder:3.1.3")
    "ktorIntegration"("io.ktor:ktor-server-content-negotiation:3.1.3")
    "ktorIntegration"("io.ktor:ktor-serialization-kotlinx-json:3.1.3")

    // Add Spring Boot dependencies to the springBootIntegration configuration
    "springBootIntegration"("org.springframework.boot:spring-boot-starter-web:3.5.0")
    "springBootIntegration"("org.springframework.boot:spring-boot-starter-thymeleaf:3.5.0")
    "springBootIntegration"("org.springframework.boot:spring-boot-starter-webflux:3.5.0")
    "springBootIntegration"("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.3")
}

kotlin.sourceSets.all {
    languageSettings {
        optIn("kotlin.RequiresOptIn")
        optIn("kotlinx.serialization.ExperimentalSerializationApi")
        optIn("kotlin.ExperimentalMultiplatform")
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

// Load properties from local.properties if it exists
val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        load(localFile.inputStream())
    }
}

// Get GitHub credentials with priority: local.properties > gradle.properties > environment variables
val githubUser = localProperties.getProperty("gpr.user") 
    ?: project.findProperty("gpr.user") as String? 
    ?: System.getenv("GITHUB_ACTOR")

val githubToken = localProperties.getProperty("gpr.key") 
    ?: project.findProperty("gpr.key") as String? 
    ?: System.getenv("GITHUB_TOKEN")

// Configure publishing for GitHub Packages only
publishing {
    publications {
        withType<MavenPublication> {
            pom {
                name.set("Summon")
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

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/codeyousef/summon")
            credentials {
                username = githubUser
                password = githubToken
            }
        }
    }
}

// Javadoc JAR task (optional for GitHub Packages)
tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from("$projectDir/docs") // You can put documentation here
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
    delete(file("${rootProject.buildDir}/js/node_modules"))
    delete(file("${rootProject.buildDir}/js/packages"))
}

// Make clean task depend on cleanNodeModules
tasks.named("clean") {
    dependsOn("cleanNodeModules")
}


// Make publish depend on tests (optional)
// tasks.withType<PublishToMavenRepository> {
//     dependsOn("testAll")
// } 
