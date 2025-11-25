import java.security.MessageDigest
import java.util.*

// Apply version management
apply(from = "../version.gradle.kts")

// Manual version override for now
version = "0.5.2.4"
group = "codes.yousef"

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.atomicfu)
    alias(libs.plugins.binary.compatibility.validator)
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

// Kotlin/Wasm incremental cache can hold stale fragments after package moves.
// Ensure we clear cached klibs before recompiling the development executables.
listOf(
    "compileDevelopmentExecutableKotlinWasmJs",
    "compileTestDevelopmentExecutableKotlinWasmJs"
).forEach { taskName ->
    tasks.matching { it.name == taskName }.configureEach {
        doFirst {
            val cacheDir = layout.buildDirectory.dir("klib/cache").get().asFile
            if (cacheDir.exists()) {
                cacheDir.deleteRecursively()
            }
        }
    }
}

configurations {
    create("quarkusIntegration")
    create("quarkusDeployment")
    create("ktorIntegration")
    create("springBootIntegration")

    // AtomicFU plugin handles dependencies automatically
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
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
                // Phase 5: Basic optimization configuration
                devtool = "source-map"

                // Production output configuration
                output?.libraryTarget = "umd"
            }
            testTask {
                // Skip browser tests in CI/headless environments where browsers aren't available
                enabled = false
            }
        }
        nodejs {
            testTask {
                useMocha {
                    timeout = "30s"
                }
                val setupScript = project.layout.projectDirectory
                    .file("src/jsTest/resources/setup-happydom.cjs")
                    .asFile
                    .absolutePath
                val existingNodeOptions = environment["NODE_OPTIONS"]?.takeIf { it.isNotBlank() }
                val requireFlag = "--require=$setupScript"
                val combinedOptions = listOfNotNull(existingNodeOptions, requireFlag).joinToString(" ")
                environment("NODE_OPTIONS", combinedOptions)
            }
        }
        binaries.executable()
    }

    // Add WASM target with basic optimization
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
                // WASM-specific configuration
                devtool = "source-map"

                // Ensure stable output filename for hydration WASM JS wrapper
                outputFileName = "summon-hydration.wasm.js"
            }
            testTask {
                // Skip browser tests in CI/headless environments where browsers aren't available
                enabled = false
            }
        }
        nodejs {
            testTask {
                useMocha {
                    timeout = "30s"
                }
                val setupScript = project.layout.projectDirectory
                    .file("src/jsTest/resources/setup-happydom.cjs")
                    .asFile
                    .absolutePath
                val existingNodeOptions = environment["NODE_OPTIONS"]?.takeIf { it.isNotBlank() }
                val requireFlag = "--require=$setupScript"
                val combinedOptions = listOfNotNull(existingNodeOptions, requireFlag).joinToString(" ")
                environment("NODE_OPTIONS", combinedOptions)
            }
        }
        binaries.executable()

        // WASM production build compiler options (Kotlin 2.2.21+)
        compilerOptions {
            freeCompilerArgs.addAll(
                "-Xwasm-debugger-custom-formatters", // Enable debugging support for production
                "-Xir-dce=false",                     // Disable dead code elimination to prevent symbol issues
                "-Xir-minimized-member-names=false"   // Keep member names for proper symbol resolution
            )
        }
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
                // Add atomicfu as compileOnly to avoid conflicts
                compileOnly(libs.atomicfu)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.kotest.property)
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
                implementation(libs.kotlinx.coroutines.reactor)
                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.kotlinx.html.jvm)
                implementation(libs.kotlinx.serialization.json.jvm)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.ktor.server.test.host)
                implementation(libs.spring.test)
                implementation(libs.kotest.runner.junit5)
                implementation(libs.jsoup)
            }
        }
        // Create webMain for shared web code
        val webMain by creating {
            dependsOn(commonMain)
            dependencies {
                // Removed kotlinx.html to prevent W3C DOM type leakage into WASM
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        val jsMain by getting {
            dependsOn(webMain)
            dependencies {
                implementation(libs.kotlinx.html.js)
                implementation(libs.kotlinx.html) // Add generic kotlinx.html for JS target
                implementation(libs.kotlin.js)
                implementation(libs.kotlin.extensions)
                implementation(libs.kotlin.browser)
                implementation(libs.kotlin.react.dom)
                implementation(npm("core-js", libs.versions.coreJs.get()))
                implementation(libs.kotlinx.coroutines.core.js)
                implementation(libs.kotlinx.serialization.json.js)
                implementation(libs.kotlin.stdlib.js)
                implementation(libs.kotlin.stdlib.common)
                // AtomicFU plugin handles atomicfu dependencies automatically
            }
        }

        val wasmJsMain by getting {
            dependsOn(webMain)
            dependencies {
                // kotlinx-browser provides WASM-compatible DOM API types (replaces org.w3c.dom.events from stdlib)
                implementation(libs.kotlinx.browser)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                // Removed atomicfu - causing persistent IC cache issues
            }
        }

        val jsTest by getting {
            dependencies {
                // AtomicFU plugin handles atomicfu dependencies automatically
                implementation(npm("happy-dom", "14.10.3"))
            }
        }
        val wasmJsTest by getting {
            dependencies {
                implementation(npm("happy-dom", "14.10.3"))
            }
        }
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

// Suppress expect/actual Beta warnings globally

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

// Compiler workarounds no longer needed with Kotlin 2.2.21 and stable incremental compilation

// WASM compilation workarounds no longer needed with Kotlin 2.2.21 Beta

// JS tests are now enabled with Kotlin 2.2.21 and proper AtomicFU configuration

// WASM production executables are now enabled with Kotlin 2.2.21 Beta stability

tasks.getByName<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>("jsBrowserProductionWebpack") {
    mainOutputFileName = "summon-hydration.js"
}

// Copy the generated JS + WASM bundles to JVM resources for serving
val summonHydrationOutputDir = layout.buildDirectory.dir("dist/js/productionExecutable")
val wasmOutputDir = layout.buildDirectory.dir("kotlin-webpack/wasmJs/productionExecutable")

tasks.register<Copy>("copyHydrationBundles") {
    dependsOn("jsBrowserDistribution", "wasmJsBrowserProductionWebpack")

    val jsOutputFile = summonHydrationOutputDir.map { it.file("summon-hydration.js") }
    val jsMapFile = summonHydrationOutputDir.map { it.file("summon-hydration.js.map") }
    val wasmJsOutputFile = wasmOutputDir.map { it.file("summon-hydration.wasm.js") }
    val wasmJsMapFile = wasmOutputDir.map { it.file("summon-hydration.wasm.js.map") }
    val wasmHashedOutputFile = wasmOutputDir.map { dir ->
        dir.asFile.listFiles()?.firstOrNull { it.extension == "wasm" } ?: File("/nonexistent")
    }

    from(jsOutputFile)
    from(jsMapFile)
    from(wasmJsOutputFile)
    from(wasmJsMapFile)
    // Rename hashed wasm file to stable summon-hydration.wasm
    from(wasmHashedOutputFile) {
        rename { "summon-hydration.wasm" }
    }
    // Also keep the original hashed WASM file (WASM loader references it by hash)
    from(wasmHashedOutputFile)

    // Copy to source directory so it's included in the JAR
    into(file("src/jvmMain/resources/static"))

    // Only run if at least the JS file exists
    onlyIf {
        jsOutputFile.get().asFile.exists()
    }

    doLast {
        val hashedName = wasmHashedOutputFile.get().name
        println("Copied Summon hydration bundles to src/jvmMain/resources/static/ (will be included in JAR)")
        println(" - JS: summon-hydration.js")
        println(" - WASM JS wrapper: summon-hydration.wasm.js")
        println(" - WASM binary (renamed): summon-hydration.wasm")
        println(" - WASM binary (hashed): $hashedName")
    }
}

// Ensure the bundles are copied before JVM resources are processed
tasks.named<ProcessResources>("jvmProcessResources") {
    dependsOn("copyHydrationBundles")
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
            // Set platform-specific artifact IDs
            when (name) {
                "kotlinMultiplatform" -> artifactId = "summon"
                "jvm" -> artifactId = "summon-jvm" 
                "js" -> artifactId = "summon-js"
                else -> artifactId = "summon-core"
            }
            pom {
                name.set("Summon Framework")
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
    delete(file("${layout.buildDirectory.get()}/js/node_modules"))
    delete(file("${layout.buildDirectory.get()}/js/packages"))
}

// Make clean task depend on cleanNodeModules
tasks.named("clean") {
    dependsOn("cleanNodeModules")
}

// Generate Javadocs for Maven Central (required)
tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    // Create empty javadoc jar as placeholder
    from("src/main/resources") {
        include("**/*.md")
        into(".")
    }
    doFirst {
        // Create a minimal javadoc structure
        val javadocDir = file("${layout.buildDirectory.get()}/tmp/javadoc")
        javadocDir.mkdirs()
        file("$javadocDir/README.md").writeText("# Summon Framework Documentation\n\nDocumentation is available at https://github.com/codeyousef/summon")
        from(javadocDir)
    }
}

// Maven Central Publishing via Central Portal API (New Group ID)
tasks.register("publishToCentralPortalManually") {
    group = "publishing"
    description = "Publish to Maven Central using Central Portal API (codes.yousef)"
    dependsOn("publishToMavenLocal", "javadocJar")
    
    doLast {
        // Load credentials from local.properties
        val localProperties = Properties().apply {
            val localFile = rootProject.file("local.properties")
            if (localFile.exists()) {
                load(localFile.inputStream())
            }
        }
        
        val username = localProperties.getProperty("mavenCentralUsername") 
            ?: throw GradleException("mavenCentralUsername not found in local.properties")

        val signingPassword = localProperties.getProperty("signingPassword")
            ?: throw GradleException("signingPassword not found in local.properties")
            
        println("🚀 Publishing to Maven Central via Central Portal API...")
        println("📦 Username: $username")
        
        // Create bundle directory with proper Maven structure
        val bundleDir = file("${layout.buildDirectory.get()}/central-portal-bundle")
        bundleDir.deleteRecursively()
        
        // Process each publication type with correct artifact IDs
        val artifactMappings = mapOf(
            "summon" to "kotlinMultiplatform",
            "summon-jvm" to "jvm", 
            "summon-js" to "js",
            "summon-core" to "wasmJs"
        )
        
        val allFilesToProcess = mutableListOf<File>()

        artifactMappings.forEach { (artifactId, _) ->
            val mavenPath = "codes/yousef/$artifactId/${project.version}"
            val targetDir = file("$bundleDir/$mavenPath")
            targetDir.mkdirs()
            
            // Copy artifacts from local Maven repository
            val localMavenDir =
                file("${System.getProperty("user.home")}/.m2/repository/codes/yousef/$artifactId/${project.version}")
            if (localMavenDir.exists()) {
                println("📦 Processing $artifactId artifacts...")
                
                localMavenDir.listFiles()?.forEach { file ->
                    if ((file.name.endsWith(".jar") || file.name.endsWith(".pom") || file.name.endsWith(".klib") || file.name.endsWith(
                            ".module"
                        )) &&
                        !file.name.endsWith(".md5") && !file.name.endsWith(".sha1") && !file.name.endsWith(".asc")) {
                        file.copyTo(File(targetDir, file.name), overwrite = true)
                        allFilesToProcess.add(File(targetDir, file.name))
                    }
                }
                
                // Add javadoc jar for each platform
                val javadocJar = file("${layout.buildDirectory.get()}/libs/summon-core-${project.version}-javadoc.jar")
                val needsJavadoc = artifactId in listOf("summon", "summon-jvm")
                if (needsJavadoc && javadocJar.exists()) {
                    val javadocFileName = "$artifactId-${project.version}-javadoc.jar"
                    val targetJavadocJar = File(targetDir, javadocFileName)
                    if (!targetJavadocJar.exists()) {
                        javadocJar.copyTo(targetJavadocJar, overwrite = true)
                        allFilesToProcess.add(targetJavadocJar)
                    }
                }
            } else {
                println("⚠️ No artifacts found for $artifactId at $localMavenDir")
            }
        }
            
        println("📝 Generating checksums and signatures...")
        
        allFilesToProcess.forEach { file ->
                // Generate MD5 checksum
                val md5Hash = MessageDigest.getInstance("MD5")
                    .digest(file.readBytes())
                    .joinToString("") { byte -> "%02x".format(byte) }
                File(file.parent, "${file.name}.md5").writeText(md5Hash)
                
                // Generate SHA1 checksum  
                val sha1Hash = MessageDigest.getInstance("SHA-1")
                    .digest(file.readBytes())
                    .joinToString("") { byte -> "%02x".format(byte) }
                File(file.parent, "${file.name}.sha1").writeText(sha1Hash)
                
                // Generate GPG signature using real key files
                val sigFile = File(file.parent, "${file.name}.asc")
                println("   Creating GPG signature for ${file.name}...")
                
                try {
                    // Import the private key (try clean version first, fallback to original)
                    val privateKeyFile = when {
                        rootProject.file("private-key-clean.asc").exists() -> rootProject.file("private-key-clean.asc")
                        rootProject.file("private-key.asc").exists() -> rootProject.file("private-key.asc")
                        else -> null
                    }
                    if (privateKeyFile == null || !privateKeyFile.exists()) {
                        throw GradleException("private-key.asc not found. Cannot sign artifacts.")
                    }

                    val signScript = rootProject.file("sign-artifact.sh")
                    if (!signScript.exists()) {
                        throw GradleException("sign-artifact.sh not found. Cannot sign artifacts.")
                    }

                    fun String.toWslPath(): String {
                        val windowsDrive = Regex("^([A-Z]):")
                        return if (windowsDrive.containsMatchIn(this)) {
                            this.replace("\\", "/")
                                .replace(windowsDrive) { matchResult ->
                                    "/mnt/${matchResult.groupValues[1].lowercase()}"
                                }
                        } else {
                            this
                        }
                    }

                    providers.exec {
                        commandLine(
                            "bash", signScript.absolutePath.toWslPath(),
                            signingPassword, privateKeyFile.absolutePath.toWslPath(),
                            sigFile.absolutePath.toWslPath(), file.absolutePath.toWslPath()
                        )
                    }.result.get().assertNormalExitValue()

                    if (!sigFile.exists()) {
                        throw GradleException("Failed to create signature for ${file.name}")
                    }
                } catch (e: Exception) {
                    throw GradleException("GPG signing error for ${file.name}: ${e.message}", e)
                }
            }

        println("📦 Creating ZIP bundle for Central Portal API")

            // Create ZIP file for Central Portal API
            val zipFile = file("${bundleDir.parent}/summon-${project.version}-bundle.zip")
            ant.invokeMethod("zip", mapOf(
                "destfile" to zipFile.absolutePath,
                "basedir" to bundleDir.absolutePath
            ))
            
            println("🚀 Uploading to Central Portal via REST API...")
        println("📦 Bundle: ${zipFile.absolutePath}")
        println("👤 Username: ${username}")

            // Upload via Central Portal REST API
            val password = localProperties.getProperty("mavenCentralPassword")
            val authString = Base64.getEncoder().encodeToString("$username:$password".toByteArray())

        val uploadResult = providers.exec {
                commandLine(
                    "curl", "-v", "-X", "POST",
                    "https://central.sonatype.com/api/v1/publisher/upload",
                    "-H", "Authorization: Basic $authString",
                    "-F", "bundle=@${zipFile.absolutePath}",
                    "--fail-with-body"
                )
            isIgnoreExitValue = true
        }.result.get()

            if (uploadResult.exitValue == 0) {
                println("✅ Successfully uploaded to Central Portal!")
                println("🔗 Check status at: https://central.sonatype.com/publishing/deployments")
                println("💡 The deployment will be validated and published automatically")
            } else {
                println("❌ Upload failed with exit code: ${uploadResult.exitValue}")
                println("📂 ZIP bundle location: ${zipFile.absolutePath}")
                println("🔗 Manual upload at: https://central.sonatype.com/publishing/deployments")
                throw GradleException("Failed to upload to Maven Central. Check credentials and try again.")
            }
            
        if (allFilesToProcess.isEmpty()) {
            throw GradleException("No Maven artifacts found. Make sure to run publishToMavenLocal first.")
        }
    }
}

// Legacy publishing to io.github.codeyousef (until 0.5.0.0)
tasks.register("publishToLegacyGroupId") {
    group = "publishing"
    description = "Publish to Maven Central using legacy group ID (io.github.codeyousef) - until 0.5.0.0"
    dependsOn("publishToMavenLocal", "javadocJar")

    doLast {
        val skipLegacyUpload = (project.findProperty("skipLegacyUpload") as String?) == "true"
        // Load credentials (only needed for upload)
        val localProperties = Properties().apply {
            val localFile = rootProject.file("local.properties")
            if (localFile.exists()) {
                load(localFile.inputStream())
            }
        }
        val username = localProperties.getProperty("mavenCentralUsername")
        val password = localProperties.getProperty("mavenCentralPassword")
        val signingPassword = localProperties.getProperty("signingPassword") ?: System.getenv("SIGNING_PASSWORD")
        ?: throw GradleException("signingPassword not found. Provide in local.properties or env SIGNING_PASSWORD")

        println("🚀 Preparing LEGACY bundle (io.github.codeyousef)... skipUpload=$skipLegacyUpload")

        // Create bundle for legacy group
        val bundleDir = file("${layout.buildDirectory.get()}/central-portal-bundle-legacy")
        bundleDir.deleteRecursively()

        val artifactMappings = mapOf(
            "summon" to "kotlinMultiplatform",
            "summon-jvm" to "jvm",
            "summon-js" to "js",
            "summon-core" to "wasmJs"
        )


        artifactMappings.forEach { (artifactId, _) ->
            val legacyMavenPath = "io/github/codeyousef/$artifactId/${project.version}"
            val targetDir = file("$bundleDir/$legacyMavenPath")
            targetDir.mkdirs()

            // Source artifacts from codes.yousef (the new group where they were published)
            val sourceDir =
                file("${System.getProperty("user.home")}/.m2/repository/codes/yousef/$artifactId/${project.version}")

            if (sourceDir.exists()) {
                println("📦 Copying $artifactId from codes.yousef to legacy bundle...")

                // Copy all files 1:1
                sourceDir.listFiles()?.forEach { file ->
                    if (file.isFile) {
                        file.copyTo(File(targetDir, file.name), overwrite = true)
                    }
                }

                // Add/ensure javadoc jar (required by Central) using the generic javadoc we build
                val javadocJar = file("${layout.buildDirectory.get()}/libs/summon-core-${project.version}-javadoc.jar")
                val needsJavadoc = artifactId in listOf("summon", "summon-jvm")
                if (needsJavadoc && javadocJar.exists()) {
                    val javadocFileName = "$artifactId-${project.version}-javadoc.jar"
                    val targetJavadocJar = File(targetDir, javadocFileName)
                    if (!targetJavadocJar.exists()) {
                        javadocJar.copyTo(targetJavadocJar, overwrite = true)
                    }
                }
            } else {
                println("⚠️  Source not found: $sourceDir")
            }
        }

        // Rewrite coordinates in textual metadata to legacy group and fully re-sign/re-checksum everything
        val needArtifactsPatterns = listOf(
            Regex(".*\\.pom$"),
            Regex(".*\\.module$"),
            Regex(".*\\.jar$"),
            Regex(".*\\.klib$"),
            Regex(".*kotlin-tooling-metadata\\.json$")
        )

        // Rewrite group coordinates in textual metadata to the legacy groupId
        bundleDir.walkTopDown()
            .filter { it.isFile && (it.extension in listOf("pom", "module", "json")) }
            .forEach { file ->
                try {
                    val content = file.readText()
                    val updated = content.replace("codes.yousef", "io.github.codeyousef")
                    if (updated != content) file.writeText(updated)
                } catch (e: Exception) {
                    throw GradleException("Failed rewriting groupId in ${file.absolutePath}: ${e.message}", e)
                }
            }

        // Helper to generate checksum files
        fun generateChecksumFiles(target: File) {
            val bytes = target.readBytes()
            val md5 = MessageDigest.getInstance("MD5").digest(bytes).joinToString("") { "%02x".format(it) }
            val sha1 = MessageDigest.getInstance("SHA-1").digest(bytes).joinToString("") { "%02x".format(it) }
            File(target.parentFile, "${target.name}.md5").writeText(md5)
            File(target.parentFile, "${target.name}.sha1").writeText(sha1)
        }

        // Sign a file producing .asc using our script/key
        fun selectPrivateKeyFile(): File {
            val candidates = listOf(
                rootProject.file("private-key-working.asc"),
                rootProject.file("private-key-clean.asc"),
                rootProject.file("private-key-fixed.asc"),
                rootProject.file("private-key-ascii.asc"),
                rootProject.file("private-key.asc")
            )
            val found = candidates.firstOrNull { it.exists() }
            return found
                ?: throw GradleException("No private key file found. Checked: ${candidates.joinToString { it.name }}")
        }

        fun signFile(target: File) {
            if (skipLegacyUpload) return // allow local bundle without signing
            val privateKeyFile = selectPrivateKeyFile()
            val signScript = rootProject.file("sign-artifact.sh")
            if (!signScript.exists()) {
                if (skipLegacyUpload) return else throw GradleException("Signing script missing: sign-artifact.sh")
            }
            val ascOut = File(target.parentFile, "${target.name}.asc")
            providers.exec {
                commandLine(
                    "bash", signScript.absolutePath,
                    signingPassword, privateKeyFile.absolutePath,
                    ascOut.absolutePath, target.absolutePath
                )
            }.result.get().assertNormalExitValue()
            if (!ascOut.exists() && !skipLegacyUpload) {
                throw GradleException("Failed to create signature for ${target.name}")
            }
        }

        // Iterate over every artifact file and generate md5/sha1 and .asc
        bundleDir.walkTopDown()
            .filter { it.isFile }
            .forEach { file ->
                // Only process main artifacts (skip checksum/signature files)
                if (file.name.endsWith(".md5") || file.name.endsWith(".sha1") || file.name.endsWith(".asc")) return@forEach

                // We require checksums and signatures for these artifact kinds
                val isArtifact = needArtifactsPatterns.any { it.matches(file.name) }
                if (isArtifact) {
                    generateChecksumFiles(file)
                    signFile(file)
                    // also checksum the signature itself
                    val asc = File(file.parentFile, "${file.name}.asc")
                    if (asc.exists()) generateChecksumFiles(asc)
                }
            }

        // Validate artifacts: ensure required md5/sha1/asc exist for each primary artifact
        val requiredOk = mutableListOf<String>()
        val requiredMissing = mutableListOf<String>()
        bundleDir.walkTopDown().filter { it.isFile }.forEach { file ->
            if (file.name.endsWith(".md5") || file.name.endsWith(".sha1") || file.name.endsWith(".asc")) return@forEach
            val isArtifact = needArtifactsPatterns.any { it.matches(file.name) }
            if (!isArtifact) return@forEach
            val md5 = File(file.parentFile, "${file.name}.md5")
            val sha1 = File(file.parentFile, "${file.name}.sha1")
            val asc = File(file.parentFile, "${file.name}.asc")
            val ok =
                if (skipLegacyUpload) (md5.exists() && sha1.exists()) else (md5.exists() && sha1.exists() && asc.exists())
            if (ok) requiredOk.add(file.relativeTo(bundleDir).path) else requiredMissing.add(file.relativeTo(bundleDir).path)
        }
        if (requiredMissing.isNotEmpty()) {
            println("❌ Missing artifacts for legacy bundle:")
            requiredMissing.forEach { println("   - $it (missing md5/sha1 and/or asc)") }
            throw GradleException("Legacy bundle validation failed: missing checksums/signatures for ${requiredMissing.size} artifacts")
        } else {
            println("✅ Legacy bundle validation passed for ${requiredOk.size} artifacts")
        }

        println("🧰 Creating LEGACY ZIP bundle for Central Portal upload...")
        val legacyZip = file("${bundleDir.parent}/summon-${project.version}-legacy-bundle.zip")
        ant.invokeMethod(
            "zip", mapOf(
                "destfile" to legacyZip.absolutePath,
                "basedir" to bundleDir.absolutePath
            )
        )

        val authString =
            if (!skipLegacyUpload) Base64.getEncoder().encodeToString("$username:$password".toByteArray()) else null
        if (!skipLegacyUpload) {
            println("🚀 Uploading LEGACY bundle to Central Portal...")
            println("📦 Bundle: ${legacyZip.absolutePath}")
            println("👤 Username: ${username}")
            val legacyUpload = providers.exec {
                commandLine(
                    "curl", "-v", "-X", "POST",
                    "https://central.sonatype.com/api/v1/publisher/upload",
                    "-H", "Authorization: Basic ${authString}",
                    "-F", "bundle=@${legacyZip.absolutePath}",
                    "--fail-with-body"
                )
                isIgnoreExitValue = true
            }.result.get()
            if (legacyUpload.exitValue == 0) {
                println("✅ Legacy upload successful! Validate at Central Portal UI.")
            } else {
                println("❌ Legacy upload failed with exit code: ${legacyUpload.exitValue}")
                println("📂 Bundle location: ${legacyZip.absolutePath}")
                throw GradleException("Failed to upload legacy bundle to Maven Central. Check credentials and try again.")
            }
        } else {
            println("⏭️ Skipped upload. Legacy bundle ready at: ${legacyZip.absolutePath}")
        }

        println("✅ Legacy group ID bundle prepared with rewritten POMs/modules and regenerated checksums/signatures (including .asc checksums)")
    }
}

// Combined task to publish to both group IDs
tasks.register("publishToBothGroupIds") {
    group = "publishing"
    description = "Publish to both codes.yousef and io.github.codeyousef (until 0.5.0.0)"
    dependsOn("publishToMavenLocal", "javadocJar")
    finalizedBy("publishToCentralPortalManually", "publishToLegacyGroupId")
}

// Fix JS test compilation order - ensure main JS compilation completes first
tasks.named("compileTestKotlinJs") {
    dependsOn("compileKotlinJs")
}

tasks.named("compileTestDevelopmentExecutableKotlinJs") {
    dependsOn("compileKotlinJs", "jsMainClasses")
}

// Enable standard output logging for WASM Node tests
tasks.withType<org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest>().configureEach {
    if (name == "wasmJsNodeTest") {
        testLogging {
            events("passed", "skipped", "failed", "standardOut", "standardError")
            showStandardStreams = true
        }
    }
}
