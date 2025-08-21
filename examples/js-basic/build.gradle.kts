import org.gradle.api.file.DuplicatesStrategy
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

// Apply version helper to get Summon version information
// NOTE: If using this example standalone (downloaded separately), you can replace this with:
// val summonVersion = "0.2.9.1" // Use latest version
apply(from = "../../version-helper.gradle.kts")

plugins {
    kotlin("multiplatform") version "2.2.0-RC2"
    kotlin("plugin.serialization") version "2.2.0-Beta1"
}

repositories {
    mavenCentral()
    // Using standalone implementation - no GitHub Packages required
    // maven {
    //     url = uri("https://maven.pkg.github.com/codeyousef/summon")
    //     credentials {
    //         username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
    //         password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
    //     }
    // }
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
                mainOutputFileName = "js.js"
            }
            runTask {
                mainOutputFileName = "js.js"
                devServerProperty = KotlinWebpackConfig.DevServer(
                    port = 8080,
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
                // Use local project via composite build substitution
                implementation("io.github.codeyousef:summon-js:0.2.9.1")

                // Standard JS dependencies
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.12.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.10.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-js:1.8.1")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:1.0.0-pre.682")
            }
            resources.srcDirs("src/jsMain/resources")
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-js"))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:1.0.0-pre.682")
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

// Handle duplicates in distribution task
tasks.withType<Copy> {
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

// Task to copy compiled JS to processedResources for dev server
tasks.register<Copy>("copyCompiledJs") {
    from("${layout.buildDirectory.get().asFile}/kotlin-webpack/js/developmentExecutable")
    into("${layout.buildDirectory.get().asFile}/processedResources/js/main")
    include("*.js")
    
    dependsOn("jsBrowserDevelopmentWebpack")
}

// Make sure the JS file is copied before running the dev server
tasks.named("jsBrowserDevelopmentRun") {
    dependsOn("copyCompiledJs")
}

// Removed the reference to jsBrowserRun task since it doesn't exist
