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
        val fullstackTemplates = setOf("quarkus", "spring-boot", "ktor")
        val isFullstack = config.templateType in fullstackTemplates
        val rootElementId = if (isFullstack) "app" else "root"

        return mapOf(
            "PROJECT_NAME" to config.projectName,
            "PACKAGE_NAME" to config.packageName,
            "PACKAGE_PATH" to packagePath,
            "APP_TITLE" to TemplateHelpers.transformName(config.projectName, "pascalcase"),
            "APP_CLASS" to TemplateHelpers.transformName(config.projectName, "pascalcase") + "App",
            "SUMMON_VERSION" to readVersionFromProperties(),
            "KOTLIN_VERSION" to "2.2.21",
            "INCLUDE_EXAMPLES" to if (config.includeExamples && !config.minimal) "true" else "false",
            "INCLUDE_AUTH" to if (config.includeAuth) "true" else "false",
            "INCLUDE_DOCKER" to if (config.includeDocker) "true" else "false",
            "MINIMAL" to if (config.minimal) "true" else "false",
            "IS_FULLSTACK" to if (isFullstack) "true" else "false",
            "ROOT_ELEMENT_ID" to rootElementId
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
        generateSharedSummonApp(config.targetDirectory, variables)
        generateFullstackJsSupport(config.targetDirectory, variables)
        generateQuarkusServer(config.targetDirectory, variables)
        generateMainKt(config.targetDirectory, variables, "quarkus")
        generateGradleWrapper(config.targetDirectory)
    }

    private fun generateSpringBootProject(config: Config, variables: Map<String, String>) {
        val sourceTemplate = getBuiltinTemplate("spring-boot")
        copyTemplate(sourceTemplate, config.targetDirectory, variables)

        generateBuildGradleKts(config.targetDirectory, variables, "spring-boot")
        generateSettingsGradleKts(config.targetDirectory, variables)
        generateApplicationProperties(config.targetDirectory, variables, "spring-boot")
        generateSharedSummonApp(config.targetDirectory, variables)
        generateFullstackJsSupport(config.targetDirectory, variables)
        generateSpringBootServer(config.targetDirectory, variables)
        generateMainKt(config.targetDirectory, variables, "spring-boot")
        generateGradleWrapper(config.targetDirectory)
    }

    private fun generateKtorProject(config: Config, variables: Map<String, String>) {
        val sourceTemplate = getBuiltinTemplate("ktor")
        copyTemplate(sourceTemplate, config.targetDirectory, variables)

        generateBuildGradleKts(config.targetDirectory, variables, "ktor")
        generateSettingsGradleKts(config.targetDirectory, variables)
        generateApplicationProperties(config.targetDirectory, variables, "ktor")
        generateSharedSummonApp(config.targetDirectory, variables)
        generateFullstackJsSupport(config.targetDirectory, variables)
        generateKtorServer(config.targetDirectory, variables)
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
        generateIndexHtml(config.targetDirectory, variables)
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
    maven {
        url = uri("https://repo1.maven.org/maven2/")
        name = "MavenCentral"
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
                implementation("org.jetbrains.kotlin:kotlin-stdlib-js:${variables["KOTLIN_VERSION"]}")
                implementation("io.github.codeyousef:summon:${variables["SUMMON_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")
                implementation(npm("core-js", "3.46.0"))
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
    id("io.quarkus") version "3.15.7"
}

repositories {
    maven {
        url = uri("https://repo1.maven.org/maven2/")
        name = "MavenCentral"
    }
}

kotlin {
    jvm()
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
                outputFileName = "app.js"
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:${variables["KOTLIN_VERSION"]}")
                implementation("io.github.codeyousef:summon:${variables["SUMMON_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:${variables["KOTLIN_VERSION"]}")
                implementation("io.quarkus:quarkus-kotlin")
                implementation("io.quarkus:quarkus-rest")
                implementation("io.quarkus:quarkus-rest-jackson")
                implementation("io.quarkus:quarkus-qute")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-js:${variables["KOTLIN_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")
                implementation(npm("core-js", "3.46.0"))
            }
        }
    }
}

val webpackTask = tasks.named("jsBrowserProductionWebpack")
tasks.named("quarkusDev") {
    dependsOn(webpackTask)
}
tasks.named("build") {
    dependsOn(webpackTask)
}
        """.trimIndent()
    }

    private fun generateSpringBootBuildGradle(variables: Map<String, String>): String {
        return """
plugins {
    kotlin("multiplatform") version "${variables["KOTLIN_VERSION"]}"
    kotlin("plugin.serialization") version "${variables["KOTLIN_VERSION"]}"
    kotlin("plugin.spring") version "${variables["KOTLIN_VERSION"]}"
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

repositories {
    maven {
        url = uri("https://repo1.maven.org/maven2/")
        name = "MavenCentral"
    }
}

kotlin {
    jvm()
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
                outputFileName = "app.js"
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:${variables["KOTLIN_VERSION"]}")
                implementation("io.github.codeyousef:summon:${variables["SUMMON_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:${variables["KOTLIN_VERSION"]}")
                implementation("org.springframework.boot:spring-boot-starter-web")
                implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
                implementation("org.springframework.boot:spring-boot-starter-webflux")
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
                implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-js:${variables["KOTLIN_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")
                implementation(npm("core-js", "3.46.0"))
            }
        }
    }
}

val webpackTask = tasks.named("jsBrowserProductionWebpack")
tasks.named("bootRun") {
    dependsOn(webpackTask)
}
tasks.named("build") {
    dependsOn(webpackTask)
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
    maven {
        url = uri("https://repo1.maven.org/maven2/")
        name = "MavenCentral"
    }
}

kotlin {
    jvm()
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
                outputFileName = "app.js"
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:${variables["KOTLIN_VERSION"]}")
                implementation("io.github.codeyousef:summon:${variables["SUMMON_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:${variables["KOTLIN_VERSION"]}")
                implementation("io.ktor:ktor-server-core:3.3.1")
                implementation("io.ktor:ktor-server-netty:3.3.1")
                implementation("io.ktor:ktor-server-html-builder:3.3.1")
                implementation("io.ktor:ktor-server-content-negotiation:3.3.1")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.3.1")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-js:${variables["KOTLIN_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")
                implementation(npm("core-js", "3.46.0"))
            }
        }
    }
}

application {
    mainClass.set("${variables["PACKAGE_NAME"]}.ApplicationKt")
}

val webpackTask = tasks.named("jsBrowserProductionWebpack")
tasks.named("run") {
    dependsOn(webpackTask)
}
tasks.named("build") {
    dependsOn(webpackTask)
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
    maven {
        url = uri("https://repo1.maven.org/maven2/")
        name = "MavenCentral"
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
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:${variables["KOTLIN_VERSION"]}")
                api("io.github.codeyousef:summon:${variables["SUMMON_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:${variables["KOTLIN_VERSION"]}")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-js:${variables["KOTLIN_VERSION"]}")
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
    maven {
        url = uri("https://repo1.maven.org/maven2/")
        name = "MavenCentral"
    }
}

kotlin {
    jvm()
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
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:${variables["KOTLIN_VERSION"]}")
                implementation("io.github.codeyousef:summon:${variables["SUMMON_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:${variables["KOTLIN_VERSION"]}")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-js:${variables["KOTLIN_VERSION"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")
                implementation(npm("core-js", "3.46.0"))
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
        val useSharedApp = variables["IS_FULLSTACK"] == "true"

        val content = when {
            useSharedApp -> generateFullstackClientMain(variables)
            isMinimal -> generateMinimalJsMain(variables)
            else -> generateFullJsMain(variables)
        }

        mainFile.writeText(content)
    }

    private fun generateMinimalJsMain(variables: Map<String, String>): String {
        return """
package ${variables["PACKAGE_NAME"]}

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.foundation.BasicText
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.renderComposable
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

@Composable
fun App() {
    BasicText(text = "Hello, ${variables["APP_TITLE"]}!")
}

fun main() {
    window.onload = {
        val rootElement = document.getElementById("${variables["ROOT_ELEMENT_ID"]}") as? HTMLElement
        if (rootElement != null) {
            val renderer = PlatformRenderer()
            renderComposable(renderer, {
                App()
            }, rootElement)
        }
    }
}
        """.trimIndent()
    }

    private fun generateFullJsMain(variables: Map<String, String>): String {
        return """
package ${variables["PACKAGE_NAME"]}

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.foundation.BasicText
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.input.Button
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.padding
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.renderComposable
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

@Composable
fun App() {
    val counter = remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier().padding("16px")
    ) {
        BasicText(
            text = "Welcome to ${variables["APP_TITLE"]}!",
            modifier = Modifier().padding(bottom = "16px", left = "0px", right = "0px", top = "0px")
        )
        
        BasicText(
            text = "Count: ${'$'}{counter.value}",
            modifier = Modifier().padding(bottom = "16px", left = "0px", right = "0px", top = "0px")
        )
        
        Button(
            onClick = { counter.value++ },
            label = "Click me!"
        )
    }
}

fun main() {
    window.onload = {
        val rootElement = document.getElementById("${variables["ROOT_ELEMENT_ID"]}") as? HTMLElement
        if (rootElement != null) {
            val renderer = PlatformRenderer()
            renderComposable(renderer, {
                App()
            }, rootElement)
        }
    }
}
        """.trimIndent()
    }

    private fun generateFullstackClientMain(variables: Map<String, String>): String {
        return """
package ${variables["PACKAGE_NAME"]}

import code.yousef.summon.renderComposable
import code.yousef.summon.runtime.PlatformRenderer
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

fun main() {
    window.onload = {
        val rootElement = document.getElementById("${variables["ROOT_ELEMENT_ID"]}") as? HTMLElement
        if (rootElement != null) {
            val renderer = PlatformRenderer()
            renderComposable(renderer, { App() }, rootElement)
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

    private fun generateSharedSummonApp(targetDir: File, variables: Map<String, String>) {
        val commonDir = File(targetDir, "src/commonMain/kotlin/${variables["PACKAGE_PATH"]}")
        if (!commonDir.exists()) {
            commonDir.mkdirs()
        }

        val appFile = File(commonDir, "App.kt")
        if (appFile.exists()) {
            return
        }

        appFile.writeText(
            """
package ${variables["PACKAGE_NAME"]}

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.foundation.BasicText
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.padding
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.mutableStateOf

@Composable
fun App() {
    val counter = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier().padding("24px")
    ) {
        BasicText(
            text = "Welcome to ${variables["APP_TITLE"]}!",
            modifier = Modifier().padding(bottom = "16px", left = "0px", right = "0px", top = "0px")
        )

        BasicText(
            text = "You've clicked ${'$'}{counter.value} time(s).",
            modifier = Modifier().padding(bottom = "12px", left = "0px", right = "0px", top = "0px")
        )

        Button(
            onClick = { counter.value++ },
            label = "Add one"
        )
    }
}
            """.trimIndent()
        )
    }

    private fun generateFullstackJsSupport(targetDir: File, variables: Map<String, String>) {
        val resourcesDir = File(targetDir, "src/jsMain/resources")
        if (!resourcesDir.exists()) {
            resourcesDir.mkdirs()
        }

        val indexFile = File(resourcesDir, "index.html")
        if (!indexFile.exists()) {
            indexFile.writeText(
                """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${variables["APP_TITLE"]}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <main id="${variables["ROOT_ELEMENT_ID"]}"></main>
</body>
</html>
                """.trimIndent()
            )
        }
    }

    private fun generateKtorServer(targetDir: File, variables: Map<String, String>) {
        val jvmDir = File(targetDir, "src/jvmMain/kotlin/${variables["PACKAGE_PATH"]}")
        jvmDir.mkdirs()
        val serverFile = File(jvmDir, "Application.kt")
        serverFile.writeText(
            """
package ${variables["PACKAGE_NAME"]}

import code.yousef.summon.runtime.PlatformRenderer
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.File

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::summonModule).start(wait = true)
}

fun Application.summonModule() {
    routing {
        get("/") {
            val renderer = PlatformRenderer()
            val body = renderer.renderComposableRoot { App() }
            call.respondText(renderPage(body), ContentType.Text.Html)
        }
        get("/static/app.js") {
            val bundle = File("build/dist/js/productionExecutable/app.js")
            if (bundle.exists()) {
                call.respondBytes(bundle.readBytes(), ContentType.Application.JavaScript)
            } else {
                call.respondBytes(
                    "// Run `./gradlew jsBrowserProductionWebpack` to generate the browser bundle."
                        .toByteArray(),
                    ContentType.Application.JavaScript,
                    HttpStatusCode.OK
                )
            }
        }
        get("/health") {
            call.respondText("OK", ContentType.Text.Plain)
        }
    }
}

private fun renderPage(content: String): String = \"\"\"
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${variables["APP_TITLE"]}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
</head>
<body>
    <main id="${variables["ROOT_ELEMENT_ID"]}">
        ${'$'}content
    </main>
    <script src="/static/app.js"></script>
</body>
</html>
\"\"\".trimIndent()
            """.trimIndent()
        )
    }

    private fun generateSpringBootServer(targetDir: File, variables: Map<String, String>) {
        val jvmDir = File(targetDir, "src/jvmMain/kotlin/${variables["PACKAGE_PATH"]}")
        jvmDir.mkdirs()
        val serverFile = File(jvmDir, "Application.kt")
        serverFile.writeText(
            """
package ${variables["PACKAGE_NAME"]}

import code.yousef.summon.runtime.PlatformRenderer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File

@SpringBootApplication
class ${variables["APP_CLASS"]}Application

fun main(args: Array<String>) = runApplication<${variables["APP_CLASS"]}Application>(*args)

@RestController
class SummonController {

    @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
    fun index(): ResponseEntity<String> {
        val renderer = PlatformRenderer()
        val body = renderer.renderComposableRoot { App() }
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(renderPage(body))
    }

    @GetMapping("/static/app.js", produces = ["application/javascript"])
    fun bundle(): ResponseEntity<String> {
        val bundle = File("build/dist/js/productionExecutable/app.js")
        val script = if (bundle.exists()) {
            bundle.readText()
        } else {
            "// Run `./gradlew jsBrowserProductionWebpack` to build the frontend bundle."
        }
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/javascript"))
            .body(script)
    }

    @GetMapping("/health", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun health(): String = "OK"
}

private fun renderPage(content: String): String = \"\"\"
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${variables["APP_TITLE"]}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
</head>
<body>
    <main id="${variables["ROOT_ELEMENT_ID"]}">
        ${'$'}content
    </main>
    <script src="/static/app.js"></script>
</body>
</html>
\"\"\".trimIndent()
            """.trimIndent()
        )
    }

    private fun generateQuarkusServer(targetDir: File, variables: Map<String, String>) {
        val jvmDir = File(targetDir, "src/jvmMain/kotlin/${variables["PACKAGE_PATH"]}")
        jvmDir.mkdirs()
        val serverFile = File(jvmDir, "SummonResource.kt")
        serverFile.writeText(
            """
package ${variables["PACKAGE_NAME"]}

import code.yousef.summon.runtime.PlatformRenderer
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.io.File

@Path("/")
class SummonResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    fun index(): String {
        val renderer = PlatformRenderer()
        val body = renderer.renderComposableRoot { App() }
        return renderPage(body)
    }

    @GET
    @Path("/static/app.js")
    @Produces("application/javascript")
    fun bundle(): Response {
        val bundle = File("build/dist/js/productionExecutable/app.js")
        val payload = if (bundle.exists()) {
            bundle.readText()
        } else {
            "// Run `./gradlew jsBrowserProductionWebpack` to build the frontend bundle."
        }
        return Response.ok(payload, "application/javascript").build()
    }

    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    fun health(): String = "OK"
}

private fun renderPage(content: String): String = \"\"\"
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${variables["APP_TITLE"]}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
</head>
<body>
    <main id="${variables["ROOT_ELEMENT_ID"]}">
        ${'$'}content
    </main>
    <script src="/static/app.js"></script>
</body>
</html>
\"\"\".trimIndent()
            """.trimIndent()
        )
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
import code.yousef.summon.components.foundation.BasicText
import code.yousef.summon.modifier.Modifier

/**
 * Example component for ${variables["PROJECT_NAME"]} library
 */
@Composable
fun ExampleComponent(
    text: String,
    modifier: Modifier = Modifier()
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

### Basic Components

```kotlin
@Composable
fun MyApp() {
    ThemeProvider(theme = MaterialTheme) {
        Column(
            modifier = Modifier.padding(16)
        ) {
            Text(
                text = "Welcome to Summon!",
                style = MaterialTheme.typography.h1
            )
            
            Button(
                onClick = { /* Handle click */ },
                modifier = Modifier.padding(top = 8)
            ) {
                Text("Get Started")
            }
        }
    }
}
```

### Creating Components

```kotlin
@Composable
fun UserCard(user: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8),
        elevation = 4
    ) {
        Row(
            modifier = Modifier.padding(16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                src = user.avatarUrl,
                alt = "Profile picture",
                modifier = Modifier
                    .size(48)
                    .clip(CircleShape)
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12)
            ) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
```

### State Management

```kotlin
@Composable
fun Counter() {
    var counter by remember { mutableStateOf(0) }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Count: ${'$'}counter")
        
        Row {
            Button(onClick = { ${'$'}counter-- }) {
                Text("-")
            }
            
            Button(
                onClick = { ${'$'}counter++ },
                modifier = Modifier.padding(start = 8)
            ) {
                Text("+")
            }
        }
    }
}
```

### Routing (File-based)

Create pages in `src/commonMain/kotlin/routing/pages/`:

```kotlin
// src/commonMain/kotlin/routing/pages/Index.kt
@Composable
fun Index() {
    Column {
        Text("Home Page")
        Link(href = "/about") {
            Text("Go to About")
        }
    }
}

// src/commonMain/kotlin/routing/pages/About.kt
@Composable
fun About() {
    Column {
        Text("About Page")
        Link(href = "/") {
            Text("Go Home")
        }
    }
}
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

import code.yousef.summon.state.mutableStateOf
import kotlin.test.Test
import kotlin.test.assertEquals

class ExampleComponentTest {

    @Test
    fun `mutable state increments`() {
        val counter = mutableStateOf(0)

        counter.value += 1

        assertEquals(1, counter.value)
    }

    @Test
    fun `mutable state stores latest value`() {
        val message = mutableStateOf("Initial")

        message.value = "Updated"

        assertEquals("Updated", message.value)
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
import code.yousef.summon.components.foundation.BasicText
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.padding

@Composable
fun ButtonExamples() {
    Column(modifier = Modifier().padding("16px")) {
        BasicText(
            text = "Button Examples",
            modifier = Modifier().padding(bottom = "16px", left = "0px", right = "0px", top = "0px")
        )
        
        Button(
            onClick = { println("Primary clicked") },
            label = "Primary Button",
            modifier = Modifier().padding(bottom = "8px", left = "0px", right = "0px", top = "0px")
        )
        
        Button(
            onClick = { println("Secondary clicked") },
            label = "Secondary Button", 
            modifier = Modifier().padding(bottom = "8px", left = "0px", right = "0px", top = "0px")
        )
    }
}
        """.trimIndent()
        )
    }

    private fun generateGradleWrapper(targetDir: File) {
        // Create gradle wrapper directory
        val gradleDir = File(targetDir, "gradle/wrapper")
        gradleDir.mkdirs()

        // Copy gradle wrapper files from resources
        val classLoader = this::class.java.classLoader

        // Copy gradle-wrapper.jar
        val wrapperJarResource = classLoader.getResourceAsStream("gradle-wrapper/gradle-wrapper.jar")
        if (wrapperJarResource != null) {
            File(gradleDir, "gradle-wrapper.jar").outputStream().use { output ->
                wrapperJarResource.use { input ->
                    input.copyTo(output)
                }
            }
        } else {
            println("⚠️  Warning: gradle-wrapper.jar not found in resources")
        }

        // Copy gradle-wrapper.properties
        val wrapperPropsResource = classLoader.getResourceAsStream("gradle-wrapper/gradle-wrapper.properties")
        if (wrapperPropsResource != null) {
            File(gradleDir, "gradle-wrapper.properties").outputStream().use { output ->
                wrapperPropsResource.use { input ->
                    input.copyTo(output)
                }
            }
        }

        // Copy gradlew script
        val gradlewResource = classLoader.getResourceAsStream("gradle-wrapper/gradlew")
        if (gradlewResource != null) {
            val gradlewFile = File(targetDir, "gradlew")
            gradlewFile.outputStream().use { output ->
                gradlewResource.use { input ->
                    input.copyTo(output)
                }
            }
            gradlewFile.setExecutable(true)
        }

        // Copy gradlew.bat script
        val gradlewBatResource = classLoader.getResourceAsStream("gradle-wrapper/gradlew.bat")
        if (gradlewBatResource != null) {
            File(targetDir, "gradlew.bat").outputStream().use { output ->
                gradlewBatResource.use { input ->
                    input.copyTo(output)
                }
            }
        }
    }

    private fun getBuiltinTemplate(type: String): File {
        // In a real implementation, this would return built-in template directories
        // For now, we'll generate everything programmatically
        return File("/tmp/template-$type")
    }

    /**
     * Read version from version.properties file
     */
    private fun readVersionFromProperties(): String {
        return try {
            val versionPropsFile = File("version.properties")
            if (versionPropsFile.exists()) {
                val props = java.util.Properties()
                versionPropsFile.inputStream().use { props.load(it) }
                props.getProperty("VERSION", "0.4.2.0")
            } else {
                // Try relative to project root
                val rootVersionFile = File("../version.properties")
                if (rootVersionFile.exists()) {
                    val props = java.util.Properties()
                    rootVersionFile.inputStream().use { props.load(it) }
                    props.getProperty("VERSION", "0.4.2.0")
                } else {
                    "0.4.2.0" // Fallback to current version
                }
            }
        } catch (e: Exception) {
            println("⚠️  Could not read version from version.properties: ${e.message}")
            "0.4.2.0" // Fallback
        }
    }
}
