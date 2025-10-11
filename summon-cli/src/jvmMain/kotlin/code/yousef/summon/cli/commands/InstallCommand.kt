package code.yousef.summon.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Command to install Summon CLI globally and add it to PATH.
 * 
 * This command will:
 * 1. Copy the current executable to a permanent location
 * 2. Add that location to the user's PATH environment variable
 * 3. Verify the installation was successful
 */
class InstallCommand : CliktCommand(
    name = "install"
) {
    
    private val global by option("--global", help = "Install system-wide (requires admin privileges)")
        .flag(default = false)
    
    private val force by option("--force", help = "Force reinstall if already installed")
        .flag(default = false)

    override fun run() {
        try {
            echo("🔧 Installing Summon CLI...")
            
            val currentExecutable = getCurrentExecutablePath()
            if (currentExecutable == null) {
                echo("❌ Error: Could not determine current executable location", err = true)
                return
            }
            
            echo("📍 Current location: ${currentExecutable.absolutePath}")
            
            val installDir = getInstallationDirectory()
            
            // Determine target filename based on source file type
            val targetFileName = if (currentExecutable.name.endsWith(".jar")) {
                "summon-cli.jar"
            } else if (isWindows()) {
                "summon.exe"
            } else {
                "summon"
            }
            val targetExecutable = File(installDir, targetFileName)
            
            // Check if already installed
            if (targetExecutable.exists() && !force) {
                echo("✅ Summon CLI is already installed at: ${targetExecutable.absolutePath}")
                echo("💡 Use --force to reinstall")
                return
            }
            
            // Create installation directory
            if (!installDir.exists()) {
                echo("📁 Creating installation directory: ${installDir.absolutePath}")
                installDir.mkdirs()
            }
            
            // Copy executable
            echo("📋 Copying executable to: ${targetExecutable.absolutePath}")
            Files.copy(
                currentExecutable.toPath(),
                targetExecutable.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
            
            // If we installed a JAR, create a wrapper script
            if (targetFileName.endsWith(".jar")) {
                createJarWrapper(installDir, targetExecutable)
            }
            
            // Add to PATH
            if (addToPath(installDir)) {
                echo("✅ Successfully installed Summon CLI!")
                echo("📍 Installation location: ${targetExecutable.absolutePath}")
                if (targetFileName.endsWith(".jar")) {
                    echo("📝 Wrapper script: ${File(installDir, if (isWindows()) "summon.bat" else "summon").absolutePath}")
                }
                echo("🔄 Please restart your terminal or run 'refreshenv' to use 'summon' commands")
                echo("")
                echo("🧪 Test your installation:")
                echo("   summon --version")
                echo("   summon --help")
            } else {
                echo("⚠️ Installation completed but failed to add to PATH", err = true)
                echo("📝 Manual PATH setup required:")
                echo("   Add ${installDir.absolutePath} to your PATH environment variable")
            }
            
        } catch (e: Exception) {
            echo("❌ Installation failed: ${e.message}", err = true)
        }
    }
    
    private fun getCurrentExecutablePath(): File? {
        return try {
            // Try to get the current JAR/executable location
            val jarFile = File(this::class.java.protectionDomain.codeSource.location.toURI())
            
            // If it's a JAR file, use it directly
            if (jarFile.exists() && jarFile.name.endsWith(".jar")) {
                jarFile
            } else if (jarFile.name.endsWith(".exe") || jarFile.name == "summon") {
                // Native executable
                jarFile
            } else {
                // Fallback: look for summon.exe or JAR in common locations
                val currentDir = File(System.getProperty("user.dir"))
                listOf(
                    File(currentDir, "summon.exe"),
                    File(currentDir, "summon-cli.jar"),
                    File(currentDir, "cli-tool/build/native/summon.exe"),
                    File(currentDir, "build/native/summon.exe")
                ).find { it.exists() }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getInstallationDirectory(): File {
        return if (global && isWindows()) {
            File("C:\\Program Files\\Summon\\bin")
        } else {
            File(System.getProperty("user.home"), ".summon/bin")
        }
    }
    
    private fun addToPath(directory: File): Boolean {
        return if (isWindows()) {
            addToWindowsPath(directory)
        } else {
            addToUnixPath(directory)
        }
    }
    
    private fun addToWindowsPath(directory: File): Boolean {
        return try {
            val pathKey = if (global) "HKLM\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment" 
                         else "HKCU\\Environment"
            
            // Get current PATH
            val getCurrentPath = ProcessBuilder(
                "reg", "query", pathKey, "/v", "PATH"
            ).start()
            getCurrentPath.waitFor()
            
            val currentPath = getCurrentPath.inputStream.bufferedReader().use { reader ->
                reader.readText()
            }
            
            val pathValue = currentPath.lines()
                .find { it.contains("PATH") && it.contains("REG_") }
                ?.substringAfter("PATH")
                ?.substringAfter("REG_EXPAND_SZ")
                ?.trim() ?: ""
            
            // Check if already in PATH
            if (pathValue.contains(directory.absolutePath)) {
                echo("✅ Directory already in PATH")
                return true
            }
            
            // Add to PATH
            val newPath = if (pathValue.isEmpty()) {
                directory.absolutePath
            } else {
                "${pathValue};${directory.absolutePath}"
            }
            
            val setPath = ProcessBuilder(
                "reg", "add", pathKey, "/v", "PATH", "/t", "REG_EXPAND_SZ", "/d", newPath, "/f"
            ).start()
            
            val exitCode = setPath.waitFor()
            if (exitCode == 0) {
                echo("✅ Successfully added to PATH")
                true
            } else {
                echo("❌ Failed to update PATH (exit code: $exitCode)", err = true)
                false
            }
            
        } catch (e: Exception) {
            echo("❌ Error updating PATH: ${e.message}", err = true)
            false
        }
    }
    
    private fun addToUnixPath(directory: File): Boolean {
        // For Unix systems, add to shell profile
        return try {
            val shellProfile = File(System.getProperty("user.home"), ".profile")
            val pathEntry = "export PATH=\"\$PATH:${directory.absolutePath}\""
            
            if (!shellProfile.exists()) {
                shellProfile.createNewFile()
            }
            
            val content = shellProfile.readText()
            if (!content.contains(directory.absolutePath)) {
                shellProfile.appendText("\n# Added by Summon CLI installer\n$pathEntry\n")
                echo("✅ Added to PATH via ~/.profile")
            } else {
                echo("✅ Directory already in PATH")
            }
            true
        } catch (e: Exception) {
            echo("❌ Error updating PATH: ${e.message}", err = true)
            false
        }
    }
    
    private fun createJarWrapper(installDir: File, jarFile: File) {
        try {
            if (isWindows()) {
                // Create Windows batch file
                val batchFile = File(installDir, "summon.bat")
                batchFile.writeText("""
                    @echo off
                    java -jar "%~dp0summon-cli.jar" %*
                """.trimIndent())
                echo("📝 Created wrapper script: ${batchFile.absolutePath}")
            } else {
                // Create Unix shell script
                val shellFile = File(installDir, "summon")
                shellFile.writeText("""
                    #!/bin/bash
                    SCRIPT_DIR="${'$'}( cd "${'$'}( dirname "${'$'}{BASH_SOURCE[0]}" )" && pwd )"
                    exec java -jar "${'$'}SCRIPT_DIR/summon-cli.jar" "${'$'}@"
                """.trimIndent())
                shellFile.setExecutable(true)
                echo("📝 Created wrapper script: ${shellFile.absolutePath}")
            }
        } catch (e: Exception) {
            echo("⚠️ Warning: Failed to create wrapper script: ${e.message}", err = true)
        }
    }
    
    private fun isWindows(): Boolean {
        return System.getProperty("os.name").lowercase().contains("windows")
    }
}