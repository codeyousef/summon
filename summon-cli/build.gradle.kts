// Apply version management
apply(from = "../version-helper.gradle.kts")

// Set project version and group from the loaded properties
project.version = project.extra["summonVersion"] as String
project.group = project.extra["summonGroup"] as String

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow.jar)
    alias(libs.plugins.graalvm.native.image)
    `maven-publish`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
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

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(libs.clikt)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)

                // File operations
                implementation("com.charleskorn.kaml:kaml:0.61.0")
                implementation("org.apache.commons:commons-compress:1.27.1")
                implementation("commons-io:commons-io:2.18.0")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}


// Create a custom shadowJar task since we're using KMP
val shadowJar = tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveClassifier.set("")
    archiveBaseName.set("summon-cli")
    from(kotlin.jvm().compilations.getByName("main").output)
    configurations = listOf(
        project.configurations.getByName("jvmRuntimeClasspath")
    )

    manifest {
        attributes(
            "Main-Class" to "code.yousef.summon.cli.SummonCliKt",
            "Implementation-Title" to "Summon CLI",
            "Implementation-Version" to project.version
        )
    }

    // Relocate dependencies to avoid conflicts
    relocate("com.github.ajalt.clikt", "summon.shaded.clikt")

    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")

    mergeServiceFiles()

    minimize {
        exclude(dependency("com.github.ajalt.clikt:.*:.*"))
        exclude(dependency("org.jetbrains.kotlinx:.*:.*"))
    }
}

tasks.named("build") {
    dependsOn(shadowJar)
}

// Task to create executable scripts for different platforms
tasks.register("createExecutableScripts") {
    dependsOn(shadowJar)
    group = "distribution"
    description = "Create executable scripts for different platforms"

    doLast {
        val shadowJarTask = shadowJar.get()
        val jarFile = shadowJarTask.archiveFile.get().asFile
        val binDir = File(layout.buildDirectory.get().asFile, "bin")
        binDir.mkdirs()

        // Use relative path for scripts to be more portable
        val jarName = jarFile.name
        val relativeJarPath = "../libs/$jarName"

        // Unix/Linux script
        val unixScript = File(binDir, "summon")
        unixScript.writeText(
            """#!/bin/bash
SCRIPT_DIR="${'$'}( cd "${'$'}( dirname "${'$'}{BASH_SOURCE[0]}" )" && pwd )"
java -jar "${'$'}SCRIPT_DIR/$relativeJarPath" "${'$'}@"
"""
        )
        unixScript.setExecutable(true)

        // Windows script  
        val windowsScript = File(binDir, "summon.bat")
        windowsScript.writeText(
            """@echo off
set SCRIPT_DIR=%~dp0
java -jar "%SCRIPT_DIR%$relativeJarPath" %*
"""
        )

        println("Created executable scripts in ${binDir.absolutePath}")
        println("Unix/Linux: ${unixScript.absolutePath}")
        println("Windows: ${windowsScript.absolutePath}")
    }
}

// Publishing configuration for CLI tool
publishing {
    publications {
        create<MavenPublication>("cli") {
            groupId = project.extra["summonGroup"] as String
            artifactId = "summon-cli"
            version = project.extra["summonVersion"] as String

            // Add the shadow JAR as the main artifact
            artifact(shadowJar.get())

            pom {
                name.set("Summon CLI")
                description.set("Command-line interface for the Summon Kotlin Multiplatform UI framework")
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
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

// Make the publication depend on the shadowJar task
tasks.withType<PublishToMavenRepository> {
    dependsOn(shadowJar)
}

// GraalVM Native Image configuration
graalvmNative {
    toolchainDetection.set(true)

    binaries {
        create("main") {
            imageName.set("summon")
            mainClass.set("code.yousef.summon.cli.SummonCliKt")

            // Native image build arguments - minimal configuration to prevent hanging
            buildArgs.addAll(
                listOf(
                    "--no-fallback",
                    "--initialize-at-build-time=kotlin.DeprecationLevel",
                    "--static",  // Force static linking to avoid linker issues
                    "-J-Xmx6g",
                    "-J-Xms1g",
                    "-O0"  // No optimization to prevent hanging
                )
            )

            // Disable configuration directory to prevent hanging issues
            // configurationFileDirectories.from(file("src/nativeConfig"))
            
            // JVM args for the native-image process
            jvmArgs("-Xmx4g")
        }
    }

    // Enable native tests (optional)
    testSupport.set(false)
}

// Create directories for native image configuration if they don't exist
tasks.register("createNativeConfigDirs") {
    doLast {
        file("src/nativeConfig").mkdirs()
    }
}

// Manual native compilation task (since GraalVM plugin doesn't work well with KMP)
tasks.register("buildNativeExecutable") {
    group = "build"
    description = "Build native executable using GraalVM"
    dependsOn("shadowJar")
    
    doLast {
        val shadowJarFile = file("build/libs").listFiles()
            ?.filter { it.name.startsWith("summon-cli") && it.name.endsWith(".jar") && !it.name.contains("sources") }
            ?.maxByOrNull { it.lastModified() }
        
        if (shadowJarFile == null || !shadowJarFile.exists()) {
            throw GradleException("Shadow JAR not found. Run './gradlew shadowJar' first.")
        }
        
        val outputDir = file("build/native")
        outputDir.mkdirs()
        
        val executableName = "summon"  // native-image adds .exe automatically on Windows
        
        val outputFile = File(outputDir, executableName)
        
        println("🔨 Building native executable...")
        println("📦 Input JAR: ${shadowJarFile.absolutePath}")
        println("🎯 Output: ${outputFile.absolutePath}")
        
        // Build native-image command (handle Windows execution properly)
        val command = mutableListOf<String>().apply {
            val osName = System.getProperty("os.name").lowercase()
            val javaHome = System.getenv("JAVA_HOME")
            
            println("🔍 OS detected: $osName")
            println("🔍 JAVA_HOME: $javaHome")
            
            // Windows detection - be more specific about WSL vs Linux
            val gradleUserHome = System.getenv("GRADLE_USER_HOME")
            val userDir = System.getProperty("user.dir")
            println("🔍 GRADLE_USER_HOME: $gradleUserHome")
            println("🔍 user.dir: $userDir")
            
            val isWindowsEnvironment = osName.contains("windows") || 
                                     gradleUserHome?.contains("\\") == true ||
                                     (userDir?.contains("/mnt/c/") == true) || // WSL detection - must be /mnt/c/ specifically
                                     File("C:\\graalvm-community-openjdk-17.0.8\\bin\\native-image.cmd").exists() // Direct path check
            
            println("🔍 Windows environment check result: $isWindowsEnvironment")
            
            val nativeImageCmd = if (isWindowsEnvironment) {
                println("🪟 Windows environment detected - using Windows paths")
                
                // Try multiple Windows paths - convert backslashes to forward slashes for WSL compatibility
                val possiblePaths = listOf(
                    javaHome?.let { "$it\\bin\\native-image.cmd" },
                    "C:\\graalvm-community-openjdk-17.0.8\\bin\\native-image.cmd",
                    "/mnt/c/graalvm-community-openjdk-17.0.8/bin/native-image.cmd", // WSL path
                    "C:\\graalvm\\bin\\native-image.cmd"
                ).filterNotNull()
                
                val foundPath = possiblePaths.firstOrNull { path ->
                    val exists = File(path).exists()
                    println("🔍 Checking path: $path -> $exists")
                    exists
                }
                
                when {
                    foundPath != null -> {
                        println("🎯 Using found path: $foundPath")
                        foundPath
                    }
                    else -> {
                        println("🎯 Falling back to PATH: native-image.cmd")
                        "native-image.cmd"
                    }
                }
            } else {
                println("🐧 Linux environment detected - using Unix paths")
                "native-image"
            }
            
            println("🔧 Final native-image command: $nativeImageCmd")
            
            // Handle execution method - always use cmd.exe for .cmd files
            if (isWindowsEnvironment && nativeImageCmd.endsWith(".cmd")) {
                // Convert WSL path back to Windows path for cmd.exe
                val windowsPath = if (nativeImageCmd.startsWith("/mnt/c/")) {
                    "C:" + nativeImageCmd.substring(6).replace("/", "\\")
                } else {
                    nativeImageCmd
                }
                println("🔄 Converting to Windows path: $windowsPath")
                add("cmd.exe")
                add("/c")
                add(windowsPath)
            } else {
                add(nativeImageCmd)
            }
            add("--no-fallback")
            add("--initialize-at-build-time=kotlin.DeprecationLevel")
            add("--static")
            add("-J-Xmx6g")
            add("-J-Xms1g")
            add("-O0")
            
            // Disable config directory to prevent hanging
            // val reflectConfigDir = file("src/nativeConfig")
            // if (reflectConfigDir.exists()) {
            //     val configPath = if (isWindowsEnvironment && reflectConfigDir.absolutePath.startsWith("/mnt/d/")) {
            //         "D:" + reflectConfigDir.absolutePath.substring(6).replace("/", "\\")
            //     } else {
            //         reflectConfigDir.absolutePath
            //     }
            //     println("🔄 Config path: $configPath")
            //     add("-H:ConfigurationFileDirectories=$configPath")
            // }
            
            // Convert paths for Windows execution if needed
            val jarPath = if (isWindowsEnvironment && shadowJarFile.absolutePath.startsWith("/mnt/d/")) {
                "D:" + shadowJarFile.absolutePath.substring(6).replace("/", "\\")
            } else {
                shadowJarFile.absolutePath
            }
            
            val outputPath = if (isWindowsEnvironment && outputFile.absolutePath.startsWith("/mnt/d/")) {
                "D:" + outputFile.absolutePath.substring(6).replace("/", "\\")
            } else {
                outputFile.absolutePath
            }
            
            println("🔄 JAR path: $jarPath")
            println("🔄 Output path: $outputPath")
            
            add("-jar")
            add(jarPath)
            add(outputPath)
        }
        
        println("🏃 Executing command:")
        println("   ${command.joinToString(" ")}")
        println()
        
        val process = ProcessBuilder(command)
            .directory(projectDir)
            .redirectErrorStream(true)
            .start()
            
        // Consume output stream to prevent blocking
        val outputReader = Thread {
            process.inputStream.bufferedReader().use { reader ->
                reader.lines().forEach { line ->
                    println("   $line")
                }
            }
        }
        outputReader.start()
        
        // Wait with timeout and file existence check
        val actualOutputFile = if (System.getProperty("os.name").lowercase().contains("windows")) {
            File(outputDir, "summon.exe")
        } else {
            outputFile
        }
        
        var exitCode = -1
        val startTime = System.currentTimeMillis()
        val timeoutMs = 5 * 60 * 1000L // 5 minutes
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (!process.isAlive) {
                exitCode = process.exitValue()
                break
            }
            
            // If executable exists and is substantial size, consider it successful
            if (actualOutputFile.exists() && actualOutputFile.length() > 1000000) { // > 1MB
                println("   ✅ Executable detected, terminating hanging process...")
                process.destroyForcibly()
                exitCode = 0
                break
            }
            
            Thread.sleep(1000) // Check every second
        }
        
        if (System.currentTimeMillis() - startTime >= timeoutMs) {
            println("   ⚠️ Process timed out, checking if executable was created...")
            process.destroyForcibly()
            exitCode = if (actualOutputFile.exists() && actualOutputFile.length() > 1000000) 0 else -1
        }
        
        outputReader.join(5000) // Wait up to 5 seconds for output thread
        
        if (exitCode == 0 && actualOutputFile.exists()) {
            val sizeMB = actualOutputFile.length() / 1024 / 1024
            println("✅ Native executable built successfully!")
            println("📊 Size: ${sizeMB} MB")
            println("📍 Location: ${actualOutputFile.absolutePath}")
        } else {
            val errorMsg = if (actualOutputFile.exists()) {
                "Native compilation completed but process handling failed"
            } else {
                "Native image compilation failed with exit code: $exitCode"
            }
            throw GradleException(errorMsg)
        }
    }
}