package code.yousef.summon.cli.commands

import code.yousef.summon.cli.generators.ProjectGenerator
import code.yousef.summon.cli.templates.ProjectTemplate
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
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
    ).choice("site", "fullstack", "js", "quarkus", "spring-boot", "ktor", "basic").default("basic")

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

    private val examples by option(
        "--examples", "-e",
        help = "Include example components"
    ).flag()

    private val mode by option(
        "--mode",
        help = "Project mode: standalone site or fullstack"
    ).choice("standalone", "fullstack")

    private val backend by option(
        "--backend",
        help = "Backend to use for fullstack projects (ktor, spring, quarkus)"
    ).choice("ktor", "spring", "spring-boot", "quarkus")

    override fun run() {
        val targetDir = determineTargetDirectory()

        val resolved = resolveTemplateSelection(template, mode, backend)

        echo("🚀 Initializing Summon project: $projectName")
        echo("📁 Target directory: ${targetDir.absolutePath}")
        echo("📦 Template: ${resolved.displayName}")
        echo("🏷️  Package: $packageName")

        var finalProjectName = projectName
        var finalPackageName = packageName
        var finalMinimal = minimal
        var finalExamples = examples

        if (interactive) {
            val interactiveResult = runInteractiveMode()
            finalProjectName = interactiveResult.projectName
            finalPackageName = interactiveResult.packageName
            finalMinimal = interactiveResult.minimal
            finalExamples = interactiveResult.examples
        }

        // Validate target directory
        val validationError = validateTargetDirectory(targetDir)
        if (validationError != null) {
            echo("❌ $validationError")
            return
        }

        try {
            val projectTemplate = ProjectTemplate.fromType(resolved.templateType)
            val generator = ProjectGenerator(projectTemplate)

            val config = ProjectGenerator.Config(
                projectName = finalProjectName,
                packageName = finalPackageName,
                targetDirectory = targetDir,
                templateType = resolved.templateType,
                minimal = finalMinimal,
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

            when (resolved.templateType) {
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
                    val msgStep = (nextStep.toInt() + 1).toString()
                    echo("  $msgStep. Summon UI shared in src/commonMain/kotlin/")
                }

                "ktor" -> {
                    echo("  $nextStep. ./gradlew run")
                    echo("     Open http://localhost:8080 to see your app")
                    val msgStep = (nextStep.toInt() + 1).toString()
                    echo("  $msgStep. Summon UI shared in src/commonMain/kotlin/")
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

    private data class InteractiveResult(
        val projectName: String,
        val packageName: String,
        val minimal: Boolean,
        val examples: Boolean
    )

    private fun runInteractiveMode(): InteractiveResult {
        echo("🔧 Interactive project setup")
        echo("Press Enter to use default values shown in [brackets]")
        echo("")

        // Implement interactive prompts using basic input
        val finalProjectName = projectName ?: run {
            echo("Project name [my-summon-app]: ", trailingNewline = false)
            val input = readLine()?.trim()
            if (input.isNullOrBlank()) "my-summon-app" else input
        }

        val finalPackageName = packageName ?: run {
            echo("Package name [com.example.app]: ", trailingNewline = false)
            val input = readLine()?.trim()
            if (input.isNullOrBlank()) "com.example.app" else input
        }

        val isMinimal = minimal || run {
            echo("Create minimal project? [y/N]: ", trailingNewline = false)
            readLine()?.trim()?.lowercase() == "y"
        }

        val includeExamples = !isMinimal && (examples || run {
            echo("Include example components? [Y/n]: ", trailingNewline = false)
            readLine()?.trim()?.lowercase() != "n"
        })

        echo("📝 Project configuration:")
        echo("   Name: $finalProjectName")
        echo("   Package: $finalPackageName")
        echo("   Type: ${if (isMinimal) "Minimal" else "Full"}")
        if (!isMinimal) echo("   Examples: ${if (includeExamples) "Yes" else "No"}")
        echo("")

        echo("Continue with this configuration? [Y/n]: ", trailingNewline = false)
        val confirm = readLine()?.trim()?.lowercase()
        if (confirm == "n") {
            echo("❌ Project creation cancelled")
            throw ProgramResult(1)
        }

        return InteractiveResult(
            projectName = finalProjectName,
            packageName = finalPackageName,
            minimal = isMinimal,
            examples = includeExamples
        )
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

    private data class TemplateResolution(
        val templateType: String,
        val displayName: String
    )

    private fun resolveTemplateSelection(
        templateArg: String,
        modeArg: String?,
        backendArg: String?
    ): TemplateResolution {
        val normalizedBackend = backendArg?.let { normalizeBackend(it) }

        if (modeArg != null) {
            return when (modeArg) {
                "standalone" -> TemplateResolution("js", "standalone site (browser)")
                "fullstack" -> {
                    val backend = normalizedBackend
                        ?: error("❌ --backend is required when --mode=fullstack (ktor, spring, or quarkus)")
                    TemplateResolution(
                        backend,
                        "fullstack (${backendDisplayName(backend)})"
                    )
                }

                else -> TemplateResolution(templateArg, templateArg)
            }
        }

        return when (templateArg) {
            "site", "js" -> TemplateResolution("js", "standalone site (browser)")
            "fullstack" -> {
                val backend = normalizedBackend
                    ?: error("❌ --backend is required when using the 'fullstack' template (ktor, spring, or quarkus)")
                TemplateResolution(
                    backend,
                    "fullstack (${backendDisplayName(backend)})"
                )
            }

            "quarkus" -> TemplateResolution("quarkus", "fullstack (Quarkus)")
            "spring-boot" -> TemplateResolution("spring-boot", "fullstack (Spring Boot)")
            "ktor" -> TemplateResolution("ktor", "fullstack (Ktor)")
            else -> TemplateResolution(templateArg, templateArg)
        }
    }

    private fun normalizeBackend(value: String): String = when (value.lowercase()) {
        "ktor" -> "ktor"
        "spring", "spring-boot" -> "spring-boot"
        "quarkus" -> "quarkus"
        else -> error("❌ Unsupported backend '$value'. Use ktor, spring, or quarkus.")
    }

    private fun backendDisplayName(type: String): String = when (type) {
        "ktor" -> "Ktor"
        "spring-boot" -> "Spring Boot"
        "quarkus" -> "Quarkus"
        else -> type
    }
}
