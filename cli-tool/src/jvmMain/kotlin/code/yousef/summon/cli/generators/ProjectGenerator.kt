package code.yousef.summon.cli.generators

import code.yousef.summon.cli.templates.ProjectTemplate
import code.yousef.summon.cli.templates.TemplateEngine
import code.yousef.summon.cli.templates.TemplateHelpers
import java.io.File

/**
 * Generates Summon projects from templates
 */
class ProjectGenerator(private val template: ProjectTemplate) {

    private val templateEngine = TemplateEngine()

    data class Config(
        val projectName: String,
        val packageName: String,
        val targetDirectory: File,
        val templateType: String,
        val includeExamples: Boolean = false,
        val includeAuth: Boolean = false,
        val includeDocker: Boolean = false,
        val minimal: Boolean = false,
        val overwrite: Boolean = false
    )

    /**
     * Generate a new project based on the template and configuration
     */
    fun generate(config: Config) {
        // Create target directory
        if (!config.targetDirectory.exists()) {
            config.targetDirectory.mkdirs()
        }

        // Prepare template variables
        val variables = prepareVariables(config)

        // Generate project structure based on template type
        when (config.templateType) {
            "js" -> generateJsProject(config, variables)
            "quarkus" -> generateQuarkusProject(config, variables)
            "spring-boot" -> generateSpringBootProject(config, variables)
            "ktor" -> generateKtorProject(config, variables)
            "library" -> generateLibraryProject(config, variables)
            "example" -> generateExampleProject(config, variables)
            else -> generateBasicProject(config, variables)
        }

        println("✅ Project '${config.projectName}' generated successfully in ${config.targetDirectory.absolutePath}")
    }

    private fun prepareVariables(config: Config): Map<String, String> {
        val packagePath = config.packageName.replace(".", "/")

        return mapOf(
            "PROJECT_NAME" to config.projectName,
            "PACKAGE_NAME" to config.packageName,
            "PACKAGE_PATH" to packagePath,
            "APP_TITLE" to TemplateHelpers.transformName(config.projectName, "pascalcase"),
            "APP_CLASS" to TemplateHelpers.transformName(config.projectName, "pascalcase") + "App",
            "SUMMON_VERSION" to "0.2.9.1",
            "KOTLIN_VERSION" to "2.2.0",
            "INCLUDE_EXAMPLES" to if (config.includeExamples && !config.minimal) "true" else "false",
            "INCLUDE_AUTH" to if (config.includeAuth) "true" else "false",
            "INCLUDE_DOCKER" to if (config.includeDocker) "true" else "false",
            "MINIMAL" to if (config.minimal) "true" else "false"
        )
    }

    private fun generateJsProject(config: Config, variables: Map<String, String>) {
        val sourceTemplate = getBuiltinTemplate("js")
        copyTemplate(sourceTemplate, config.targetDirectory, variables)

        // Generate specific JS files
        generateBuildGradleKts(config.targetDirectory, variables, "js")
        generateSettingsGradleKts(config.targetDirectory, variables)
        generateIndexHtml(config.targetDirectory, variables)
        generateMainKt(config.targetDirectory, variables, "js")
        generateGradleWrapper(config.targetDirectory)
    }

    private fun generateQuarkusProject(config: Config, variables: Map<String, String>) {
        val sourceTemplate = getBuiltinTemplate("quarkus")
        copyTemplate(sourceTemplate, config.targetDirectory, variables)

        generateBuildGradleKts(config.targetDirectory, variables, "quarkus")
        generateSettingsGradleKts(config.targetDirectory, variables)
        generateApplicationProperties(config.targetDirectory, variables, "quarkus")
        generateMainKt(config.targetDirectory, variables, "quarkus")
        generateGradleWrapper(config.targetDirectory)
    }

    private fun generateSpringBootProject(config: Config, variables: Map<String, String>) {
        val sourceTemplate = getBuiltinTemplate("spring-boot")
        copyTemplate(sourceTemplate, config.targetDirectory, variables)

        generateBuildGradleKts(config.targetDirectory, variables, "spring-boot")
        generateSettingsGradleKts(config.targetDirectory, variables)
        generateApplicationProperties(config.targetDirectory, variables, "spring-boot")
        generateMainKt(config.targetDirectory, variables, "spring-boot")
        generateGradleWrapper(config.targetDirectory)
    }

    private fun generateKtorProject(config: Config, variables: Map<String, String>) {
        val sourceTemplate = getBuiltinTemplate("ktor")
        copyTemplate(sourceTemplate, config.targetDirectory, variables)

        generateBuildGradleKts(config.targetDirectory, variables, "ktor")
        generateSettingsGradleKts(config.targetDirectory, variables)
        generateApplicationProperties(config.targetDirectory, variables, "ktor")
        generateMainKt(config.targetDirectory, variables, "ktor")
        generateGradleWrapper(config.targetDirectory)
    }

    private fun generateLibraryProject(config: Config, variables: Map<String, String>) {
        generateBuildGradleKts(config.targetDirectory, variables, "library")
        generateSettingsGradleKts(config.targetDirectory, variables)
        generateLibraryStructure(config.targetDirectory, variables)
        generateGradleWrapper(config.targetDirectory)
    }

    private fun generateExampleProject(config: Config, variables: Map<String, String>) {
        generateJsProject(config, variables) // Base on JS project
        if (!config.minimal) {
            generateExampleComponents(config.targetDirectory, variables)
        }
    }

    private fun generateBasicProject(config: Config, variables: Map<String, String>) {
        generateBuildGradleKts(config.targetDirectory, variables, "multiplatform")
        generateSettingsGradleKts(config.targetDirectory, variables)
        generateMultiplatformStructure(config.targetDirectory, variables)
        generateGradleWrapper(config.targetDirectory)
    }

    private fun copyTemplate(sourceDir: File, targetDir: File, variables: Map<String, String>) {
        if (!sourceDir.exists()) return

        sourceDir.walkTopDown().forEach { sourceFile ->
            if (sourceFile.isFile) {
                val relativePath = sourceFile.relativeTo(sourceDir).path
                val targetFile = File(targetDir, templateEngine.processFileName(relativePath, variables))

                targetFile.parentFile?.mkdirs()

                if (shouldProcessAsTemplate(sourceFile)) {
                    templateEngine.processTemplateFile(sourceFile, targetFile, variables)
                } else {
                    sourceFile.copyTo(targetFile, overwrite = true)
                }
            }
        }
    }

    private fun shouldProcessAsTemplate(file: File): Boolean {
        val textExtensions =
            setOf("kt", "kts", "html", "css", "js", "json", "xml", "yml", "yaml", "properties", "md", "txt")
        return textExtensions.contains(file.extension)
    }

    private fun generateBuildGradleKts(targetDir: File, variables: Map<String, String>, type: String) {
        val buildFile = File(targetDir, "build.gradle.kts")
        val content = when (type) {
            "js" -> generateJsBuildGradle(variables)
            "quarkus" -> generateQuarkusBuildGradle(variables)
            "spring-boot" -> generateSpringBootBuildGradle(variables)
            "ktor" -> generateKtorBuildGradle(variables)
            "library" -> generateLibraryBuildGradle(variables)
            else -> generateMultiplatformBuildGradle(variables)
        }
        buildFile.writeText(content)
    }

    private fun generateJsBuildGradle(variables: Map<String, String>): String {
        return """
plugins {
    kotlin("multiplatform") version "${variables["KOTLIN_VERSION"]}"
    kotlin("plugin.serialization") version "${variables["KOTLIN_VERSION"]}"
}

repositories {
    mavenCentral()
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
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("io.github.codeyousef:summon:${variables["SUMMON_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")
                implementation(npm("core-js", "3.31.0"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

tasks.named<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>("jsBrowserProductionWebpack") {
    mainOutputFileName = "${variables["PROJECT_NAME"]}.js"
}
        """.trimIndent()
    }

    private fun generateQuarkusBuildGradle(variables: Map<String, String>): String {
        return """
plugins {
    kotlin("multiplatform") version "${variables["KOTLIN_VERSION"]}"
    kotlin("plugin.serialization") version "${variables["KOTLIN_VERSION"]}"
    id("io.quarkus") version "3.23.0"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/codeyousef/summon")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}

kotlin {
    jvm {
        withJava()
    }
    js(IR) {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.codeyousef:summon:${variables["SUMMON_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.quarkus:quarkus-kotlin")
                implementation("io.quarkus:quarkus-resteasy-reactive")
                implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
                implementation("io.quarkus:quarkus-qute")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")
                implementation(npm("core-js", "3.31.0"))
            }
        }
    }
}
        """.trimIndent()
    }

    private fun generateSpringBootBuildGradle(variables: Map<String, String>): String {
        return """
plugins {
    kotlin("multiplatform") version "${variables["KOTLIN_VERSION"]}"
    kotlin("plugin.serialization") version "${variables["KOTLIN_VERSION"]}"
    kotlin("plugin.spring") version "${variables["KOTLIN_VERSION"]}"
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.4"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/codeyousef/summon")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}

kotlin {
    jvm {
        withJava()
    }
    js(IR) {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.codeyousef:summon:${variables["SUMMON_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.springframework.boot:spring-boot-starter-web")
                implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
                implementation("org.springframework.boot:spring-boot-starter-webflux")
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
                implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")
                implementation(npm("core-js", "3.31.0"))
            }
        }
    }
}
        """.trimIndent()
    }

    private fun generateKtorBuildGradle(variables: Map<String, String>): String {
        return """
plugins {
    kotlin("multiplatform") version "${variables["KOTLIN_VERSION"]}"
    kotlin("plugin.serialization") version "${variables["KOTLIN_VERSION"]}"
    application
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/codeyousef/summon")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}

kotlin {
    jvm {
        withJava()
    }
    js(IR) {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.codeyousef:summon:${variables["SUMMON_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core:2.3.7")
                implementation("io.ktor:ktor-server-netty:2.3.7")
                implementation("io.ktor:ktor-server-html-builder:2.3.7")
                implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")
                implementation(npm("core-js", "3.31.0"))
            }
        }
    }
}

application {
    mainClass.set("${variables["PACKAGE_NAME"]}.ApplicationKt")
}
        """.trimIndent()
    }

    private fun generateLibraryBuildGradle(variables: Map<String, String>): String {
        return """
plugins {
    kotlin("multiplatform") version "${variables["KOTLIN_VERSION"]}"
    kotlin("plugin.serialization") version "${variables["KOTLIN_VERSION"]}"
    `maven-publish`
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/codeyousef/summon")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}

kotlin {
    jvm()
    js(IR) {
        browser()
        nodejs()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.github.codeyousef:summon:${variables["SUMMON_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            groupId = "${variables["PACKAGE_NAME"]}"
            artifactId = "${variables["PROJECT_NAME"]}"
            version = "1.0.0"
        }
    }
}
        """.trimIndent()
    }

    private fun generateMultiplatformBuildGradle(variables: Map<String, String>): String {
        return """
plugins {
    kotlin("multiplatform") version "${variables["KOTLIN_VERSION"]}"
    kotlin("plugin.serialization") version "${variables["KOTLIN_VERSION"]}"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/codeyousef/summon")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}

kotlin {
    jvm {
        withJava()
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
                implementation("io.github.codeyousef:summon:${variables["SUMMON_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")
                implementation(npm("core-js", "3.31.0"))
            }
        }
        val jsTest by getting
    }
}
        """.trimIndent()
    }

    private fun generateSettingsGradleKts(targetDir: File, variables: Map<String, String>) {
        val settingsFile = File(targetDir, "settings.gradle.kts")
        settingsFile.writeText("rootProject.name = \"${variables["PROJECT_NAME"]}\"\n")
    }

    private fun generateIndexHtml(targetDir: File, variables: Map<String, String>) {
        val resourcesDir = File(targetDir, "src/jsMain/resources")
        resourcesDir.mkdirs()
        val indexFile = File(resourcesDir, "index.html")

        val content = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${variables["APP_TITLE"]}</title>
</head>
<body>
    <div id="root"></div>
    <script src="${variables["PROJECT_NAME"]}.js"></script>
</body>
</html>
        """.trimIndent()

        indexFile.writeText(content)
    }

    private fun generateMainKt(targetDir: File, variables: Map<String, String>, type: String) {
        when (type) {
            "js" -> generateJsMain(targetDir, variables)
            "quarkus" -> generateQuarkusMain(targetDir, variables)
            "spring-boot" -> generateSpringBootMain(targetDir, variables)
            "ktor" -> generateKtorMain(targetDir, variables)
        }
    }

    private fun generateJsMain(targetDir: File, variables: Map<String, String>) {
        val sourceDir = File(targetDir, "src/jsMain/kotlin/${variables["PACKAGE_PATH"]}")
        sourceDir.mkdirs()
        val mainFile = File(sourceDir, "Main.kt")

        val isMinimal = variables["MINIMAL"] == "true"

        val content = if (isMinimal) {
            generateMinimalJsMain(variables)
        } else {
            generateFullJsMain(variables)
        }

        mainFile.writeText(content)
    }

    private fun generateMinimalJsMain(variables: Map<String, String>): String {
        return """
package ${variables["PACKAGE_NAME"]}

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.core.BasicText
import code.yousef.summon.runtime.PlatformRenderer
import kotlinx.browser.document
import kotlinx.browser.window

@Composable
fun App() {
    BasicText(text = "Hello, ${variables["APP_TITLE"]}!")
}

fun main() {
    window.onload = {
        val rootElement = document.getElementById("root")
        if (rootElement != null) {
            PlatformRenderer.render(rootElement) {
                App()
            }
        }
    }
}
        """.trimIndent()
    }

    private fun generateFullJsMain(variables: Map<String, String>): String {
        return """
package ${variables["PACKAGE_NAME"]}

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.core.BasicText
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.input.Button
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.padding
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.state.remember
import kotlinx.browser.document
import kotlinx.browser.window

@Composable
fun App() {
    val count = remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier.padding(16)
    ) {
        BasicText(
            text = "Welcome to ${variables["APP_TITLE"]}!",
            modifier = Modifier.padding(bottom = 16)
        )
        
        BasicText(
            text = "Count: ${'$'}{count.value}",
            modifier = Modifier.padding(bottom = 16)
        )
        
        Button(
            text = "Click me!",
            onClick = { count.value++ }
        )
    }
}

fun main() {
    window.onload = {
        val rootElement = document.getElementById("root")
        if (rootElement != null) {
            PlatformRenderer.render(rootElement) {
                App()
            }
        }
    }
}
        """.trimIndent()
    }

    private fun generateQuarkusMain(targetDir: File, variables: Map<String, String>) {
        // Generate JVM main
        val jvmSourceDir = File(targetDir, "src/jvmMain/kotlin/${variables["PACKAGE_PATH"]}")
        jvmSourceDir.mkdirs()

        // Generate JS main similar to above but with Quarkus integration
        generateJsMain(targetDir, variables)
    }

    private fun generateSpringBootMain(targetDir: File, variables: Map<String, String>) {
        // Generate JVM main for Spring Boot
        val jvmSourceDir = File(targetDir, "src/jvmMain/kotlin/${variables["PACKAGE_PATH"]}")
        jvmSourceDir.mkdirs()

        generateJsMain(targetDir, variables)
    }

    private fun generateKtorMain(targetDir: File, variables: Map<String, String>) {
        // Generate JVM main for Ktor
        val jvmSourceDir = File(targetDir, "src/jvmMain/kotlin/${variables["PACKAGE_PATH"]}")
        jvmSourceDir.mkdirs()

        generateJsMain(targetDir, variables)
    }

    private fun generateApplicationProperties(targetDir: File, variables: Map<String, String>, type: String) {
        val resourcesDir = File(targetDir, "src/jvmMain/resources")
        resourcesDir.mkdirs()

        when (type) {
            "quarkus" -> {
                val propsFile = File(resourcesDir, "application.properties")
                propsFile.writeText(
                    """
quarkus.http.port=8080
quarkus.http.host=0.0.0.0
quarkus.qute.dev-mode.no-restart-templates=**/*.html
                """.trimIndent()
                )
            }

            "spring-boot" -> {
                val propsFile = File(resourcesDir, "application.properties")
                propsFile.writeText(
                    """
server.port=8080
spring.application.name=${variables["PROJECT_NAME"]}
                """.trimIndent()
                )
            }

            "ktor" -> {
                val confFile = File(resourcesDir, "application.conf")
                confFile.writeText(
                    """
ktor {
    application {
        modules = [ ${variables["PACKAGE_NAME"]}.ApplicationKt.module ]
    }
    deployment {
        port = 8080
        host = "0.0.0.0"
    }
}
                """.trimIndent()
                )
            }
        }
    }

    private fun generateLibraryStructure(targetDir: File, variables: Map<String, String>) {
        // Create common source structure
        val commonMainDir = File(targetDir, "src/commonMain/kotlin/${variables["PACKAGE_PATH"]}")
        commonMainDir.mkdirs()

        val isMinimal = variables["MINIMAL"] == "true"

        if (!isMinimal) {
            val exampleComponent = File(commonMainDir, "ExampleComponent.kt")
            exampleComponent.writeText(
                """
package ${variables["PACKAGE_NAME"]}

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.core.BasicText
import code.yousef.summon.modifier.Modifier

/**
 * Example component for ${variables["PROJECT_NAME"]} library
 */
@Composable
fun ExampleComponent(
    text: String,
    modifier: Modifier = Modifier
) {
    BasicText(
        text = text,
        modifier = modifier
    )
}
            """.trimIndent()
            )
        } else {
            // Create a minimal README file instead
            val readmeFile = File(commonMainDir, "README.md")
            readmeFile.writeText(
                """
# ${variables["PROJECT_NAME"]}

A Summon component library.

## Getting Started

Add your components in this directory.

## Usage

```kotlin
// TODO: Add usage examples
```
            """.trimIndent()
            )
        }

        // Create test structure
        val commonTestDir = File(targetDir, "src/commonTest/kotlin/${variables["PACKAGE_PATH"]}")
        commonTestDir.mkdirs()

        if (!isMinimal) {
            val testFile = File(commonTestDir, "ExampleComponentTest.kt")
            testFile.writeText(
                """
package ${variables["PACKAGE_NAME"]}

class ExampleComponentTest {
    
    fun testExampleComponent() {
        // TODO: Add component tests
        // Test implementation needed
    }
}
            """.trimIndent()
            )
        } else {
            // Create minimal test placeholder
            val testFile = File(commonTestDir, "LibraryTest.kt")
            testFile.writeText(
                """
package ${variables["PACKAGE_NAME"]}

class LibraryTest {
    
    fun placeholder() {
        // TODO: Add your component tests here
        // Test implementation needed
    }
}
            """.trimIndent()
            )
        }
    }

    private fun generateMultiplatformStructure(targetDir: File, variables: Map<String, String>) {
        generateLibraryStructure(targetDir, variables)
        generateJsMain(targetDir, variables)
    }

    private fun generateExampleComponents(targetDir: File, variables: Map<String, String>) {
        val examplesDir = File(targetDir, "src/jsMain/kotlin/${variables["PACKAGE_PATH"]}/examples")
        examplesDir.mkdirs()

        // Generate button examples
        val buttonExamples = File(examplesDir, "ButtonExamples.kt")
        buttonExamples.writeText(
            """
package ${variables["PACKAGE_NAME"]}.examples

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.core.BasicText
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.padding

@Composable
fun ButtonExamples() {
    Column(modifier = Modifier.padding(16)) {
        BasicText(
            text = "Button Examples",
            modifier = Modifier.padding(bottom = 16)
        )
        
        Button(
            text = "Primary Button",
            onClick = { println("Primary clicked") },
            modifier = Modifier.padding(bottom = 8)
        )
        
        Button(
            text = "Secondary Button", 
            onClick = { println("Secondary clicked") },
            modifier = Modifier.padding(bottom = 8)
        )
    }
}
        """.trimIndent()
        )
    }

    private fun generateGradleWrapper(targetDir: File) {
        // Create gradle wrapper files - in a real implementation, 
        // these would be copied from the Summon project or downloaded
        val gradleDir = File(targetDir, "gradle/wrapper")
        gradleDir.mkdirs()

        val gradlewFile = File(targetDir, "gradlew")
        gradlewFile.writeText(
            """#!/bin/bash
# Gradle wrapper script
exec gradle "$@"
"""
        )
        gradlewFile.setExecutable(true)

        val gradlewBat = File(targetDir, "gradlew.bat")
        gradlewBat.writeText(
            """@echo off
gradle %*
"""
        )
    }

    private fun getBuiltinTemplate(type: String): File {
        // In a real implementation, this would return built-in template directories
        // For now, we'll generate everything programmatically
        return File("/tmp/template-$type")
    }
}