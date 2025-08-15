package code.yousef.summon.cli.commands

import code.yousef.summon.cli.generators.ProjectGenerator
import code.yousef.summon.cli.templates.ProjectTemplate
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import java.io.File

/**
 * Command to initialize a new Summon project in the current directory or a specified directory.
 */
class InitCommand : CliktCommand(
    name = "init"
) {

    private val projectName by argument(
        name = "name",
        help = "Name of the project"
    ).default("summon-app")

    private val template by option(
        "--template", "-t",
        help = "Project template to use"
    ).choice("js", "quarkus", "spring-boot", "ktor", "basic").default("basic")

    private val packageName by option(
        "--package", "-p",
        help = "Base package name (e.g., com.example.myapp)"
    ).default("com.example.app")

    private val directory by option(
        "--dir", "-d",
        help = "Target directory (defaults to project name, use '.' for current directory)"
    )

    private val here by option(
        "--here",
        help = "Generate project in current directory"
    ).flag()

    private val force by option(
        "--force", "-f",
        help = "Overwrite existing files"
    ).flag()

    private val interactive by option(
        "--interactive", "-i",
        help = "Interactive mode for configuration"
    ).flag()

    private val minimal by option(
        "--minimal", "-m",
        help = "Generate minimal/blank project without examples"
    ).flag()

    override fun run() {
        val targetDir = determineTargetDirectory()

        echo("🚀 Initializing Summon project: $projectName")
        echo("📁 Target directory: ${targetDir.absolutePath}")
        echo("📦 Template: $template")
        echo("🏷️  Package: $packageName")

        if (interactive) {
            runInteractiveMode()
            return
        }

        // Validate target directory
        val validationError = validateTargetDirectory(targetDir)
        if (validationError != null) {
            echo("❌ $validationError")
            return
        }

        try {
            val projectTemplate = ProjectTemplate.fromType(template)
            val generator = ProjectGenerator(projectTemplate)

            val config = ProjectGenerator.Config(
                projectName = projectName,
                packageName = packageName,
                targetDirectory = targetDir,
                templateType = template,
                minimal = minimal,
                overwrite = force
            )

            echo("📋 Generating project structure...")
            generator.generate(config)

            echo("✅ Project created successfully!")
            echo("")
            echo("Next steps:")
            if (!here && targetDir.name != ".") {
                echo("  1. cd ${targetDir.name}")
                echo("  2. ./gradlew build")
            } else {
                echo("  1. ./gradlew build")
            }

            val nextStep = if (!here && targetDir.name != ".") "3" else "2"

            when (template) {
                "js" -> {
                    echo("  $nextStep. ./gradlew jsBrowserDevelopmentRun")
                    echo("     Open http://localhost:8080 to see your app")
                }

                "quarkus" -> {
                    echo("  $nextStep. ./gradlew quarkusDev")
                    echo("     Open http://localhost:8080 to see your app")
                }

                "spring-boot" -> {
                    echo("  $nextStep. ./gradlew bootRun")
                    echo("     Open http://localhost:8080 to see your app")
                }

                "ktor" -> {
                    echo("  $nextStep. ./gradlew run")
                    echo("     Open http://localhost:8080 to see your app")
                }

                else -> {
                    echo("  $nextStep. ./gradlew jsBrowserDevelopmentRun  # For browser development")
                    val nextNextStep = if (!here && targetDir.name != ".") "4" else "3"
                    echo("  $nextNextStep. ./gradlew run                      # For JVM development")
                }
            }

        } catch (e: Exception) {
            echo("❌ Failed to create project: ${e.message}")
            if (currentContext.findObject<Boolean>() == true) { // verbose mode
                e.printStackTrace()
            }
        }
    }

    private fun runInteractiveMode() {
        echo("🔧 Interactive project setup")
        echo("Press Enter to use default values shown in [brackets]")
        echo("")

        // TODO: Implement interactive prompts using Clikt's prompt functionality
        echo("⚠️  Interactive mode not yet implemented. Using provided options.")

        // For now, fall back to non-interactive mode
        // In the future, this would use clikt's prompt() function to ask for user input
    }

    private fun determineTargetDirectory(): File {
        return when {
            here -> {
                // Generate in current directory
                File(".").absoluteFile
            }

            directory != null -> {
                // Use specified directory
                val dir = File(directory!!)
                if (dir.isAbsolute) dir else File(".", directory!!).absoluteFile
            }

            else -> {
                // Default: create subdirectory with project name
                File(".", projectName).absoluteFile
            }
        }
    }

    private fun validateTargetDirectory(targetDir: File): String? {
        return when {
            targetDir.exists() && !targetDir.isDirectory() -> {
                "Target path exists but is not a directory"
            }

            targetDir.exists() && targetDir.listFiles()?.isNotEmpty() == true && !force -> {
                "Directory is not empty. Use --force to overwrite or choose a different location"
            }

            else -> null
        }
    }
}