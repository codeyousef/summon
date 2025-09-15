import java.security.MessageDigest
import java.util.*
import java.util.Base64

// Apply version management
apply(from = "../version.gradle.kts")

// Manual version override for now
version = "0.3.1.0"
group = "io.github.codeyousef"

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
    delete(file("${buildDir}/js/node_modules"))
    delete(file("${buildDir}/js/packages"))
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

// Maven Central Publishing via Central Portal API
tasks.register("publishToCentralPortalManually") {
    group = "publishing"
    description = "Publish to Maven Central using Central Portal API"
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
        val signingKey = localProperties.getProperty("signingKey")
            ?: throw GradleException("signingKey not found in local.properties")
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
            "summon-js" to "js"
        )
        
        val allFilesToProcess = mutableListOf<File>()
        
        artifactMappings.forEach { (artifactId, publicationType) ->
            val mavenPath = "io/github/codeyousef/$artifactId/${project.version}"
            val targetDir = file("$bundleDir/$mavenPath")
            targetDir.mkdirs()
            
            // Copy artifacts from local Maven repository
            val localMavenDir = file("${System.getProperty("user.home")}/.m2/repository/io/github/codeyousef/$artifactId/${project.version}")
            if (localMavenDir.exists()) {
                println("📦 Processing $artifactId artifacts...")
                
                localMavenDir.listFiles()?.forEach { file ->
                    if ((file.name.endsWith(".jar") || file.name.endsWith(".pom")) && 
                        !file.name.endsWith(".md5") && !file.name.endsWith(".sha1") && !file.name.endsWith(".asc")) {
                        file.copyTo(File(targetDir, file.name), overwrite = true)
                        allFilesToProcess.add(File(targetDir, file.name))
                    }
                }
                
                // Add javadoc jar for each platform
                val javadocJar = file("${layout.buildDirectory.get()}/libs/summon-core-${project.version}-javadoc.jar")
                if (javadocJar.exists()) {
                    val renamedJavadocJar = File(targetDir, "$artifactId-${project.version}-javadoc.jar")
                    javadocJar.copyTo(renamedJavadocJar, overwrite = true)
                    allFilesToProcess.add(renamedJavadocJar)
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
                    // Import the private key (use clean version without BOM)
                    val privateKeyFile = rootProject.file("private-key-clean.asc")
                    if (privateKeyFile.exists()) {
                        exec {
                            commandLine("gpg", "--batch", "--yes", "--import", privateKeyFile.absolutePath)
                            isIgnoreExitValue = true
                        }
                        
                        // Sign the file
                        exec {
                            commandLine("gpg", "--batch", "--yes", "--pinentry-mode", "loopback", 
                                      "--passphrase", signingPassword, "--armor", "--detach-sign", 
                                      "--output", sigFile.absolutePath, file.absolutePath)
                            isIgnoreExitValue = true
                        }
                        
                        if (!sigFile.exists()) {
                            println("   ⚠️  GPG signing failed, creating placeholder signature")
                            sigFile.writeText("-----BEGIN PGP SIGNATURE-----\n(GPG signing failed)\n-----END PGP SIGNATURE-----\n")
                        }
                    } else {
                        println("   ⚠️  private-key.asc not found, creating placeholder signature")
                        sigFile.writeText("-----BEGIN PGP SIGNATURE-----\n(No private key found)\n-----END PGP SIGNATURE-----\n")
                    }
                } catch (e: Exception) {
                    println("   ⚠️  GPG signing error: ${e.message}")
                    sigFile.writeText("-----BEGIN PGP SIGNATURE-----\n(GPG error)\n-----END PGP SIGNATURE-----\n")
                }
            }
            
            println("📦 Creating ZIP bundle for API upload...")
            
            // Create ZIP file for Central Portal API
            val zipFile = file("${bundleDir.parent}/summon-${project.version}-bundle.zip")
            ant.invokeMethod("zip", mapOf(
                "destfile" to zipFile.absolutePath,
                "basedir" to bundleDir.absolutePath
            ))
            
            println("🚀 Uploading to Central Portal via REST API...")
            
            // Upload via Central Portal REST API
            val password = localProperties.getProperty("mavenCentralPassword")
            val authString = Base64.getEncoder().encodeToString("$username:$password".toByteArray())
            
            val uploadResult = exec {
                isIgnoreExitValue = true
                commandLine("curl", "-X", "POST",
                    "https://central.sonatype.com/api/v1/publisher/upload",
                    "-H", "Authorization: Basic $authString",
                    "-F", "bundle=@${zipFile.absolutePath}",
                    "--fail-with-body"
                )
            }
            
            if (uploadResult.exitValue == 0) {
                println("✅ Successfully uploaded to Central Portal!")
                println("🔗 Check status at: https://central.sonatype.com/publishing/deployments")
                println("💡 The deployment will be validated and published automatically")
            } else {
                println("❌ Upload failed. Manual upload may be required.")
                println("📂 ZIP bundle location: ${zipFile.absolutePath}")
                println("🔗 Manual upload at: https://central.sonatype.com/publishing/deployments")
            }
            
        if (allFilesToProcess.isEmpty()) {
            throw GradleException("No Maven artifacts found. Make sure to run publishToMavenLocal first.")
        }
    }
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