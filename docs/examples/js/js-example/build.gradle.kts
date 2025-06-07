import org.gradle.api.file.DuplicatesStrategy
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform") version "2.2.0-Beta1"
    kotlin("plugin.serialization") version "2.2.0-Beta1"
}

repositories {
    mavenCentral()
    // GitHub Packages - required for Summon library
    maven {
        url = uri("https://maven.pkg.github.com/codeyousef/summon")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
            // Configure webpack dev server
            webpackTask {
                mainOutputFileName = "js-example.js"
            }
            runTask {
                mainOutputFileName = "js-example.js"
                devServerProperty = KotlinWebpackConfig.DevServer(
                    port = 8082,
                    static = mutableListOf("${layout.buildDirectory.get().asFile}/processedResources/js/main")
                )
            }
            // Include i18n resources
            @OptIn(ExperimentalDistributionDsl::class)
            distribution {
                outputDirectory = File(layout.buildDirectory.get().asFile, "distributions")
            }
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                // Depend on the Summon library
                implementation("io.github.codeyousef:summon:0.2.7")

                // Standard JS dependencies
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.12.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.10.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-js:1.8.1")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:2025.4.6")
            }
            resources.srcDirs("src/jsMain/resources")
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-js"))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:2025.4.6")
            }
        }
    }
}

// Copy i18n resources to the build directory
tasks.register<Copy>("copyI18nResources") {
    from("i18n")
    into("${layout.buildDirectory.get().asFile}/processedResources/js/main/i18n")
}

// Make sure the resources are copied before the webpack task runs
tasks.named("jsBrowserDevelopmentWebpack") {
    dependsOn("copyI18nResources")
}

tasks.named("jsBrowserProductionWebpack") {
    dependsOn("copyI18nResources")
}

// Add dependency for jsProductionExecutableCompileSync task
tasks.named("jsProductionExecutableCompileSync") {
    dependsOn("copyI18nResources")
}

// Add dependency for jsDevelopmentExecutableCompileSync task
tasks.named("jsDevelopmentExecutableCompileSync") {
    dependsOn("copyI18nResources")
}

// Configure jsProcessResources task to handle duplicate files
tasks.named<Copy>("jsProcessResources").configure {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Add dependency for jsJar task
tasks.named("jsJar") {
    dependsOn("copyI18nResources")
}

// Add dependency for jsTestTestDevelopmentExecutableCompileSync task
tasks.named("jsTestTestDevelopmentExecutableCompileSync") {
    dependsOn("copyI18nResources")
}

// Add dependency for compileTestDevelopmentExecutableKotlinJs task
tasks.named("compileTestDevelopmentExecutableKotlinJs") {
    dependsOn("copyI18nResources")
}