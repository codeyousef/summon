import java.security.MessageDigest
import java.util.*

// Apply version management
apply(from = "version.gradle.kts")

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

// Signing credentials (support inline string or file-based to avoid long command lines)
val rawSigningKeyProp = localProperties.getProperty("signingKey")
    ?: project.findProperty("signingKey") as String?
    ?: System.getenv("SIGNING_KEY")

val signingKeyFileProp = localProperties.getProperty("signingKeyFile")
    ?: project.findProperty("signingKeyFile") as String?
    ?: System.getenv("SIGNING_KEY_FILE")

val rawSigningPassword = localProperties.getProperty("signingPassword")
    ?: project.findProperty("signingPassword") as String?
    ?: System.getenv("SIGNING_PASSWORD")

fun decodeIfBase64OrReturn(input: String): String {
    return try {
        val compact = input.replace("\n", "").replace("\r", "").trim()
        val decoded = Base64.getDecoder().decode(compact)
        // Detect BOM to choose proper charset
        val text = when {
            decoded.size >= 2 && decoded[0] == 0xFF.toByte() && decoded[1] == 0xFE.toByte() -> {
                String(decoded, Charsets.UTF_16LE)
            }

            decoded.size >= 2 && decoded[0] == 0xFE.toByte() && decoded[1] == 0xFF.toByte() -> {
                String(decoded, Charsets.UTF_16BE)
            }

            decoded.size >= 3 && decoded[0] == 0xEF.toByte() && decoded[1] == 0xBB.toByte() && decoded[2] == 0xBF.toByte() -> {
                String(decoded, Charsets.UTF_8)
            }

            else -> {
                // Heuristic: if many NUL bytes, assume UTF-16LE
                val zeroCount = decoded.count { it == 0.toByte() }
                if (zeroCount > decoded.size / 3) String(decoded, Charsets.UTF_16LE) else String(
                    decoded,
                    Charsets.UTF_8
                )
            }
        }
        // Sanitize: remove any BOM char and normalize newlines
        text.replace("\uFEFF", "").replace("\r\n", "\n").replace("\r", "\n").trim()
    } catch (_: Throwable) {
        input
    }
}

fun loadSigningKey(): String? {
    // Prefer inline value if provided
    val inline = rawSigningKeyProp?.takeIf { it.isNotBlank() }?.let { decodeIfBase64OrReturn(it) }
    if (inline != null) return inline
    // Fallback to explicit file path property or default private-key.asc in project root
    val candidatePath = signingKeyFileProp?.takeIf { it.isNotBlank() }
        ?: run { val def = file("private-key.asc"); if (def.exists()) def.absolutePath else null }
        ?: return null
    val f = file(candidatePath)
    if (!f.exists()) return null
    val content = f.readText(Charsets.UTF_8)
    return decodeIfBase64OrReturn(content)
}

// Normalize blank values to null so empty -P flags don't trigger signing
val signingKey = loadSigningKey()
val signingPassword = rawSigningPassword?.takeIf { it.isNotBlank() }
// Basic validation: ensure we have an ASCII-armored private key block markers
val isValidPgpKey =
    signingKey?.contains("BEGIN PGP PRIVATE KEY BLOCK") == true && signingKey.contains("END PGP PRIVATE KEY BLOCK")

// Central Portal credentials (optionally via local.properties). The Vanniktech plugin reads Gradle properties
// mavenCentralUsername & mavenCentralPassword. We bridge local.properties and environment variables into
// Gradle properties for convenience during local publishing, and sanitize values to avoid stray quotes/whitespace.
fun sanitizeCred(input: String?): String? {
    if (input == null) return null
    // Remove zero-width & BOM characters that can sneak in from copy/paste
    val cleaned = input
        .replace("\u200B", "") // ZWSP
        .replace("\u200C", "") // ZWNJ
        .replace("\u200D", "") // ZWJ
        .replace("\uFEFF", "") // BOM
        .trim()
    if (cleaned.isEmpty()) return null
    val unquoted = if (cleaned.length >= 2 && (
                (cleaned.startsWith('"') && cleaned.endsWith('"')) ||
                        (cleaned.startsWith('\'') && cleaned.endsWith('\''))
                )
    ) cleaned.substring(1, cleaned.length - 1).trim() else cleaned
    return if (unquoted.isEmpty()) null else unquoted
}

var mavenCentralUsernameSource = ""
var mavenCentralPasswordSource = ""

val mavenCentralUsername = run {
    val vLocal = sanitizeCred(localProperties.getProperty("mavenCentralUsername"))
    if (vLocal != null) {
        mavenCentralUsernameSource = "local.properties"; vLocal
    } else {
        val vProp = sanitizeCred(project.findProperty("mavenCentralUsername") as String?)
        if (vProp != null) {
            mavenCentralUsernameSource = "-P/gradle.properties"; vProp
        } else {
            val vEnv = sanitizeCred(System.getenv("MAVEN_CENTRAL_USERNAME"))
            if (vEnv != null) {
                mavenCentralUsernameSource = "env"; vEnv
            } else null
        }
    }
}
val mavenCentralPassword = run {
    val vLocal = sanitizeCred(localProperties.getProperty("mavenCentralPassword"))
    if (vLocal != null) {
        mavenCentralPasswordSource = "local.properties"; vLocal
    } else {
        val vProp = sanitizeCred(project.findProperty("mavenCentralPassword") as String?)
        if (vProp != null) {
            mavenCentralPasswordSource = "-P/gradle.properties"; vProp
        } else {
            val vEnv = sanitizeCred(System.getenv("MAVEN_CENTRAL_PASSWORD"))
            if (vEnv != null) {
                mavenCentralPasswordSource = "env"; vEnv
            } else null
        }
    }
}

// Ensure Gradle properties exist for the Vanniktech plugin Providers API (even if blank)
// This prevents 'no value available' when the Central build service shuts down.
System.setProperty("org.gradle.project.mavenCentralUsername", (mavenCentralUsername ?: ""))
System.setProperty("org.gradle.project.mavenCentralPassword", (mavenCentralPassword ?: ""))

// Also expose as project properties for findProperty consumers
if (mavenCentralUsername != null) {
    extensions.extraProperties["mavenCentralUsername"] = mavenCentralUsername
}
if (mavenCentralPassword != null) {
    extensions.extraProperties["mavenCentralPassword"] = mavenCentralPassword
}

// Gate Central Portal tasks on presence of credentials to avoid plugin service errors locally
val hasMavenCentralCreds = !((mavenCentralUsername ?: "").isBlank()) && !((mavenCentralPassword ?: "").isBlank())
if (!hasMavenCentralCreds) {
    println("Maven Central credentials not configured. Central publishing tasks will be skipped. Set mavenCentralUsername/mavenCentralPassword in local.properties or via env.")
}

// Version gating for Central publishing (from version.gradle.kts extras)
val isSnapshotVersionFlag = (project.extra["isSnapshotVersion"] as? Boolean) ?: false
val versionValidFlag = (project.extra["versionValid"] as? Boolean) ?: true
val effectiveVersion = (project.extra["versionName"] as? String) ?: project.version.toString()

if (!versionValidFlag) {
    println("Central publish will be skipped: VERSION is invalid/blank as read from version.properties.")
}
if (isSnapshotVersionFlag) {
    println("Central publish will be skipped: VERSION is a SNAPSHOT ($effectiveVersion). Use a non-SNAPSHOT for release.")
}


// Javadoc JAR task for Maven Central
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    // Use docs folder if present or generate empty jar
    val docsDir = file("$projectDir/docs")
    if (docsDir.exists()) {
        from(docsDir)
    }
}

// Configure publishing for Maven Central only
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
        // Maven Central publishing is handled via the publishToCentralPortalManually task
        // No repositories needed here as we use direct Central Portal API
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
    delete(file("${rootProject.buildDir}/js/node_modules"))
    delete(file("${rootProject.buildDir}/js/packages"))
}

// Make clean task depend on cleanNodeModules
tasks.named("clean") {
    dependsOn("cleanNodeModules")
}

// Convenience task to publish to local Maven repository
tasks.register("publishLocal") {
    group = "publishing"
    description = "Publish all publications to the local Maven repository"
    dependsOn("publishToMavenLocal")
    dependsOn(":cli-tool:publishToMavenLocal")
}

// Task to build CLI tool native executables
tasks.register("buildCliExecutables") {
    group = "build"
    description = "Build CLI tool native executables and Shadow JAR"
    dependsOn(":cli-tool:buildNativeExecutable", ":cli-tool:shadowJar")

    doLast {
        println("✅ CLI tool executables built successfully!")
        println("📁 Shadow JAR: cli-tool/build/libs/")
        println("🔧 Native executable: cli-tool/build/native/nativeCompile/")
        println("💡 Use these artifacts for GitHub Releases")
    }
}

// Signing configuration (only when keys are available)
if (isValidPgpKey && signingPassword != null) {
    signing {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
} else {
    println("Signing disabled: SIGNING_KEY missing/invalid or SIGNING_PASSWORD not provided")
}


// Make publish depend on tests (optional)
// tasks.withType<PublishToMavenRepository> {
//     dependsOn("testAll")
// } 


// Global Kotlin compiler flags to reduce warnings
// -Xexpect-actual-classes: suppress Beta warnings for expect/actual
// -Xuse-fir-lt=false: disable K2 LightTree for scripts warning
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes",
            "-Xuse-fir-lt=false",
        )
    }
}


// Helper: print detected Central credentials and signing status (for local verification)
tasks.register("printCentralCredentials") {
    group = "publishing"
    description = "Prints the Maven Central credentials source and signing status (masked)."
    doLast {
        val u = mavenCentralUsername ?: ""
        val masked =
            if (u.isBlank()) "<not set>" else if (u.length <= 2) "*".repeat(u.length) else u.take(2) + "*".repeat(u.length - 2)
        val userSrc =
            if (mavenCentralUsernameSource.isBlank()) "local.properties/-P/env" else mavenCentralUsernameSource
        val passSrc =
            if (mavenCentralPasswordSource.isBlank()) "local.properties/-P/env" else mavenCentralPasswordSource
        println("[Central Publish] mavenCentralUsername = ${masked} (source: ${userSrc})")
        println(
            "[Central Publish] mavenCentralPassword = " + (if ((mavenCentralPassword
                    ?: "").isBlank()
            ) "<not set>" else "<provided>") + " (source: ${passSrc})"
        )
        println("[Central Publish] Central publish tasks will " + if (hasMavenCentralCreds) "RUN" else "BE SKIPPED")
        val signingState = if (isValidPgpKey && signingPassword != null) "enabled" else "disabled"
        println("[Central Publish] Signing is ${signingState}")
    }
}

// Helper: print effective version and gating decisions for Central
tasks.register("printPublishingInfo") {
    group = "publishing"
    description = "Prints effective version and Central publishing gating status."
    doLast {
        println("[Central Publish] Effective version = ${effectiveVersion}")
        println("[Central Publish] isSnapshotVersion = ${isSnapshotVersionFlag}")
        println("[Central Publish] versionValid = ${versionValidFlag}")
        println("[Central Publish] hasMavenCentralCreds = ${hasMavenCentralCreds}")
        val willRun = hasMavenCentralCreds && versionValidFlag && !isSnapshotVersionFlag
        println("[Central Publish] will " + if (willRun) "RUN" else "BE SKIPPED")
    }
}

// Custom task to publish only working publications to local
tasks.register("publishWorkingToMavenLocal") {
    group = "publishing"
    description = "Publish JS and JVM publications to Maven Local (skip broken common metadata)"
    dependsOn("publishJsPublicationToMavenLocal", "publishJvmPublicationToMavenLocal")
}

// Custom Central Portal publishing task - builds and publishes automatically
tasks.register("publishToCentralPortalManually") {
    group = "publishing"
    description = "Build artifacts and publish to Central Portal using their API"
    dependsOn("publishWorkingToMavenLocal")

    doLast {
        val username = mavenCentralUsername ?: throw GradleException("mavenCentralUsername not configured")
        val password = mavenCentralPassword ?: throw GradleException("mavenCentralPassword not configured")

        // Create base64 encoded auth header
        val credentials = "$username:$password"
        val encodedCredentials = Base64.getEncoder().encodeToString(credentials.toByteArray())
        val authHeader = "Bearer $encodedCredentials"

        println("[Central Portal] Using username: ${username.take(2)}****")
        println("[Central Portal] Creating deployment bundle...")

        // Helper function to generate checksum
        fun generateChecksum(file: File, algorithm: String): String {
            val digest = MessageDigest.getInstance(algorithm)
            val bytes = file.readBytes()
            val hash = digest.digest(bytes)
            return hash.joinToString("") { byte -> "%02x".format(byte) }
        }

        // Helper function to create checksum file
        fun createChecksumFile(originalFile: File, targetDir: File, algorithm: String) {
            val checksumValue = generateChecksum(originalFile, algorithm)
            val extension = when (algorithm) {
                "MD5" -> ".md5"
                "SHA-1" -> ".sha1"
                else -> ".${algorithm.lowercase()}"
            }
            val checksumFile = File(targetDir, originalFile.name + extension)
            checksumFile.writeText(checksumValue)
        }

        // Helper function to sign file with GPG (if signing is configured)
        fun signFile(originalFile: File, targetDir: File) {
            if (isValidPgpKey && signingPassword != null) {
                try {
                    println("[Central Portal] Signing ${originalFile.name}")
                    val signatureFile = File(targetDir, originalFile.name + ".asc")

                    // Use Gradle's signing task to sign the file
                    project.exec {
                        commandLine(
                            "gpg", "--batch", "--yes", "--armor", "--detach-sign",
                            "--pinentry-mode", "loopback",
                            "--passphrase", signingPassword,
                            "--output", signatureFile.absolutePath,
                            originalFile.absolutePath
                        )
                    }
                } catch (e: Exception) {
                    println("[Central Portal] Warning: Failed to sign ${originalFile.name}: ${e.message}")
                }
            }
        }

        // Look for existing artifacts in local repo
        val userHome = System.getProperty("user.home")
        val localRepoPath = "$userHome/.m2/repository"
        val groupPath = project.group.toString().replace(".", "/")
        val artifactPath = "$localRepoPath/$groupPath/${project.name}/$effectiveVersion"

        println("[Central Portal] Looking for artifacts in: $artifactPath")

        val bundleFile = layout.buildDirectory.file("central-bundle.zip").get().asFile
        bundleFile.parentFile.mkdirs()

        // Create temporary directory for bundle staging
        val stagingDir = File(layout.buildDirectory.get().asFile, "central-bundle-staging")
        if (stagingDir.exists()) {
            stagingDir.deleteRecursively()
        }
        stagingDir.mkdirs()

        // Create proper Maven directory structure for unified artifacts
        val unifiedTargetDir = File(stagingDir, "$groupPath/${project.name}/$effectiveVersion")
        unifiedTargetDir.mkdirs()

        // Create separate directories for platform-specific artifacts
        val jsTargetDir = File(stagingDir, "$groupPath/${project.name}-js/$effectiveVersion")
        val jvmTargetDir = File(stagingDir, "$groupPath/${project.name}-jvm/$effectiveVersion")
        jsTargetDir.mkdirs()
        jvmTargetDir.mkdirs()

        val artifactDir = file(artifactPath)
        if (!artifactDir.exists()) {
            println("[Central Portal] Artifacts not found at $artifactPath")
            println("[Central Portal] This might be a WSL compilation issue. Creating unified artifacts from platform-specific ones...")

            // Check for platform-specific artifacts
            val jsPath = "$localRepoPath/$groupPath/${project.name}-js/$effectiveVersion"
            val jvmPath = "$localRepoPath/$groupPath/${project.name}-jvm/$effectiveVersion"

            if (!file(jsPath).exists() || !file(jvmPath).exists()) {
                throw GradleException("Platform-specific artifacts not found. Please run the build successfully first.")
            }

            println("[Central Portal] Found JS artifacts at: $jsPath")
            println("[Central Portal] Found JVM artifacts at: $jvmPath")

            // Copy JS artifacts to JS directory
            val jsSourceDir = File(jsPath)
            jsSourceDir.listFiles()?.forEach { sourceFile ->
                if (sourceFile.isFile && !sourceFile.name.endsWith(".md5") && !sourceFile.name.endsWith(".sha1")) {
                    val targetFile = File(jsTargetDir, sourceFile.name)
                    sourceFile.copyTo(targetFile, overwrite = true)

                    // Generate checksums and signatures for JS artifacts
                    if (!sourceFile.name.endsWith(".asc")) {
                        createChecksumFile(targetFile, jsTargetDir, "MD5")
                        createChecksumFile(targetFile, jsTargetDir, "SHA-1")
                        signFile(targetFile, jsTargetDir)
                    }
                }
            }

            // Copy JVM artifacts to JVM directory
            val jvmSourceDir = File(jvmPath)
            jvmSourceDir.listFiles()?.forEach { sourceFile ->
                if (sourceFile.isFile && !sourceFile.name.endsWith(".md5") && !sourceFile.name.endsWith(".sha1")) {
                    val targetFile = File(jvmTargetDir, sourceFile.name)
                    sourceFile.copyTo(targetFile, overwrite = true)

                    // Generate checksums and signatures for JVM artifacts
                    if (!sourceFile.name.endsWith(".asc")) {
                        createChecksumFile(targetFile, jvmTargetDir, "MD5")
                        createChecksumFile(targetFile, jvmTargetDir, "SHA-1")
                        signFile(targetFile, jvmTargetDir)
                    }
                }
            }

            // Create synthetic unified artifacts
            println("[Central Portal] Creating synthetic unified artifacts...")

            // 1. Create unified POM
            val unifiedPomContent = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <!-- This module was also published with a richer model, Gradle metadata,  -->
  <!-- which should be used instead. Do not delete the following line which  -->
  <!-- is to indicate to Gradle or any Gradle module metadata file consumer  -->
  <!-- that they should prefer consuming it instead. -->
  <!-- do_not_remove: published-with-gradle-metadata -->
  <modelVersion>4.0.0</modelVersion>
  <groupId>${project.group.toString()}</groupId>
  <artifactId>${project.name}</artifactId>
  <version>$effectiveVersion</version>
  <packaging>pom</packaging>
  <name>Summon</name>
  <description>A Kotlin Multiplatform UI framework for building web applications</description>
  <url>https://github.com/codeyousef/summon</url>
  <licenses>
    <license>
      <name>MIT License</name>
      <url>https://opensource.org/licenses/MIT</url>
    </license>
  </licenses>
  <developers>
    <developer>
      <id>codeyousef</id>
      <name>codeyousef</name>
      <url>https://github.com/codeyousef/</url>
    </developer>
  </developers>
  <scm>
    <connection>scm:git:git://github.com/codeyousef/summon.git</connection>
    <developerConnection>scm:git:ssh://git@github.com/codeyousef/summon.git</developerConnection>
    <url>https://github.com/codeyousef/summon/</url>
  </scm>
</project>
"""
            val unifiedPomFile = File(unifiedTargetDir, "${project.name}-$effectiveVersion.pom")
            unifiedPomFile.writeText(unifiedPomContent)
            createChecksumFile(unifiedPomFile, unifiedTargetDir, "MD5")
            createChecksumFile(unifiedPomFile, unifiedTargetDir, "SHA-1")
            signFile(unifiedPomFile, unifiedTargetDir)

            // 2. Create unified Gradle module metadata
            val unifiedModuleContent = """{
  "formatVersion": "1.1",
  "component": {
    "group": "${project.group.toString()}",
    "module": "${project.name}",
    "version": "$effectiveVersion",
    "attributes": {
      "org.gradle.status": "release"
    }
  },
  "createdBy": {
    "gradle": {
      "version": "8.5"
    }
  },
  "variants": [
    {
      "name": "jsApiElements",
      "attributes": {
        "org.gradle.category": "library",
        "org.gradle.usage": "kotlin-api",
        "org.jetbrains.kotlin.platform.type": "js"
      },
      "dependencies": [],
      "files": [
        {
          "name": "${project.name}-js-$effectiveVersion.klib",
          "url": "../${project.name}-js/$effectiveVersion/${project.name}-js-$effectiveVersion.klib",
          "size": ${file("$jsPath/${project.name}-js-$effectiveVersion.klib").length()},
          "sha512": "",
          "sha256": "",
          "sha1": "${generateChecksum(file("$jsPath/${project.name}-js-$effectiveVersion.klib"), "SHA-1")}",
          "md5": "${generateChecksum(file("$jsPath/${project.name}-js-$effectiveVersion.klib"), "MD5")}"
        }
      ]
    },
    {
      "name": "jvmApiElements",
      "attributes": {
        "org.gradle.category": "library",
        "org.gradle.usage": "java-api",
        "org.jetbrains.kotlin.platform.type": "jvm"
      },
      "dependencies": [],
      "files": [
        {
          "name": "${project.name}-jvm-$effectiveVersion.jar",
          "url": "../${project.name}-jvm/$effectiveVersion/${project.name}-jvm-$effectiveVersion.jar",
          "size": ${file("$jvmPath/${project.name}-jvm-$effectiveVersion.jar").length()},
          "sha512": "",
          "sha256": "",
          "sha1": "${generateChecksum(file("$jvmPath/${project.name}-jvm-$effectiveVersion.jar"), "SHA-1")}",
          "md5": "${generateChecksum(file("$jvmPath/${project.name}-jvm-$effectiveVersion.jar"), "MD5")}"
        }
      ]
    }
  ]
}
"""
            val unifiedModuleFile = File(unifiedTargetDir, "${project.name}-$effectiveVersion.module")
            unifiedModuleFile.writeText(unifiedModuleContent)
            createChecksumFile(unifiedModuleFile, unifiedTargetDir, "MD5")
            createChecksumFile(unifiedModuleFile, unifiedTargetDir, "SHA-1")
            signFile(unifiedModuleFile, unifiedTargetDir)

            // 3. Create placeholder JAR files
            listOf("", "-sources", "-javadoc").forEach { classifier ->
                val jarName = "${project.name}-$effectiveVersion$classifier.jar"
                val jarFile = File(unifiedTargetDir, jarName)

                // Create empty JAR with just a manifest
                ant.withGroovyBuilder {
                    "jar"("destfile" to jarFile.absolutePath) {
                        "manifest" {
                            "attribute"("name" to "Implementation-Title", "value" to "Summon")
                            "attribute"("name" to "Implementation-Version", "value" to effectiveVersion)
                            "attribute"("name" to "Implementation-Vendor", "value" to "io.github.codeyousef")
                        }
                    }
                }
                createChecksumFile(jarFile, unifiedTargetDir, "MD5")
                createChecksumFile(jarFile, unifiedTargetDir, "SHA-1")
                signFile(jarFile, unifiedTargetDir)
            }

            // 4. Create kotlin-tooling-metadata.json
            val kotlinToolingMetadata = """{
  "schemaVersion": "1.0.0",
  "buildSystem": "gradle",
  "buildSystemVersion": "8.5",
  "buildPlugin": "org.jetbrains.kotlin.multiplatform",
  "buildPluginVersion": "2.2.0",
  "projectTargets": [
    {
      "target": "js",
      "platformType": "js"
    },
    {
      "target": "jvm", 
      "platformType": "jvm"
    }
  ]
}
"""
            val toolingMetadataFile =
                File(unifiedTargetDir, "${project.name}-$effectiveVersion-kotlin-tooling-metadata.json")
            toolingMetadataFile.writeText(kotlinToolingMetadata)
            createChecksumFile(toolingMetadataFile, unifiedTargetDir, "MD5")
            createChecksumFile(toolingMetadataFile, unifiedTargetDir, "SHA-1")
            signFile(toolingMetadataFile, unifiedTargetDir)

        } else {
            // Normal path: create bundle from unified multiplatform artifacts
            println("[Central Portal] Using unified multiplatform artifacts")
            artifactDir.listFiles()?.forEach { sourceFile ->
                if (sourceFile.isFile && !sourceFile.name.endsWith(".md5") && !sourceFile.name.endsWith(".sha1")) {
                    // Copy the file to target directory
                    val targetFile = File(unifiedTargetDir, sourceFile.name)
                    sourceFile.copyTo(targetFile, overwrite = true)

                    // Generate checksums for all files (except signatures)
                    if (!sourceFile.name.endsWith(".asc")) {
                        createChecksumFile(targetFile, unifiedTargetDir, "MD5")
                        createChecksumFile(targetFile, unifiedTargetDir, "SHA-1")
                        signFile(targetFile, unifiedTargetDir)
                    }
                }
            }
        }

        println("[Central Portal] Staged ${unifiedTargetDir.listFiles()?.size ?: 0} unified files, ${jsTargetDir.listFiles()?.size ?: 0} JS files, ${jvmTargetDir.listFiles()?.size ?: 0} JVM files")

        // Create the bundle ZIP with proper directory structure
        ant.withGroovyBuilder {
            "zip"("destfile" to bundleFile.absolutePath) {
                "fileset"("dir" to stagingDir) {
                    "include"("name" to "**/*")
                }
            }
        }

        // Clean up staging directory
        stagingDir.deleteRecursively()

        if (!bundleFile.exists() || bundleFile.length() == 0L) {
            throw GradleException("Failed to create bundle or bundle is empty. Check that artifacts exist.")
        }

        println("[Central Portal] Bundle created: ${bundleFile.absolutePath} (${bundleFile.length()} bytes)")

        // Upload bundle
        val uploadUrl = "https://central.sonatype.com/api/v1/publisher/upload"

        try {
            val process = ProcessBuilder(
                "curl", "-X", "POST", "-v",
                "--header", "Authorization: $authHeader",
                "--form", "bundle=@${bundleFile.absolutePath}",
                "--form", "publishingType=AUTOMATIC",
                uploadUrl
            ).start()

            val response = process.inputStream.bufferedReader().readText()
            val errorOutput = process.errorStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            println("[Central Portal] Curl output: $errorOutput")
            println("[Central Portal] Upload response: $response")

            if (exitCode == 0) {
                // Parse deployment ID from response if needed
                if (response.contains("deploymentId") || response.contains("\"error\"") == false) {
                    println("[Central Portal] Upload successful!")
                } else {
                    throw GradleException("Upload failed: $response")
                }
            } else {
                throw GradleException("Upload failed with exit code $exitCode: $errorOutput")
            }

        } catch (e: Exception) {
            throw GradleException("Failed to upload to Central Portal: ${e.message}", e)
        }
    }
}

