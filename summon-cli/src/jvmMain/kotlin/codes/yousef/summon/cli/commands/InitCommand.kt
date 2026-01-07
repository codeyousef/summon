package codes.yousef.summon.cli.commands

import codes.yousef.summon.cli.generators.ProjectGenerator
import codes.yousef.summon.cli.templates.ProjectTemplate
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import java.io.File

/**
 * Scaffold a new Summon project (standalone site or full-stack backend + Summon UI).
 */
class InitCommand(
    private val templateResolver: (String) -> ProjectTemplate = { ProjectTemplate.fromType(it) },
    private val generatorFactory: (ProjectTemplate) -> ProjectExecutor = { template ->
        val generator = ProjectGenerator(template)
        ProjectExecutor { config -> generator.generate(config) }
    },
    private val inputProvider: () -> String? = { readLine() }
) : CliktCommand(name = "init") {

    fun interface ProjectExecutor {
        fun generate(config: ProjectGenerator.Config)
    }

    private val projectName by argument(
        name = "name",
        help = "Name of the project"
    ).default("summon-app")

    private val packageName by option(
        "--package", "-p",
        help = "Base package name (e.g., com.example.app)"
    ).default("com.example.app")

    private val directory by option(
        "--dir", "-d",
        help = "Target directory (defaults to project name, use '.' for current directory)"
    )

    private val here by option(
        "--here",
        help = "Generate the project in the current directory"
    ).flag()

    private val force by option(
        "--force", "-f",
        help = "Overwrite existing files if the directory is not empty"
    ).flag()

    private val mode by option(
        "--mode",
        help = "Skip the prompt by specifying standalone or fullstack"
    ).choice("standalone", "fullstack")

    private val backend by option(
        "--backend",
        help = "Backend to use for fullstack projects (spring, ktor, quarkus)"
    ).choice("spring", "spring-boot", "ktor", "quarkus")

    override fun run() {
        val targetDir = determineTargetDirectory()
        val selection = resolveTemplateSelection()

        echo("🚀 Initializing Summon project: $projectName")
        echo("📁 Target directory: ${targetDir.absolutePath}")
        echo("📦 Template: ${selection.displayName}")
        echo("🏷️  Package: $packageName")

        val validationError = validateTargetDirectory(targetDir)
        if (validationError != null) {
            echo("❌ $validationError")
            return
        }

        val projectTemplate = templateResolver(selection.templateType)
        val projectExecutor = generatorFactory(projectTemplate)

        val config = ProjectGenerator.Config(
            projectName = projectName,
            packageName = packageName,
            targetDirectory = targetDir,
            templateType = selection.templateType,
            minimal = false,
            overwrite = force
        )

        echo("📋 Generating project structure...")
        projectExecutor.generate(config)
        echo("✅ Project created successfully!\n")

        printNextSteps(targetDir, selection)
    }

    private fun printNextSteps(targetDir: File, selection: TemplateResolution) {
        val startIndex = if (!here && targetDir.name != ".") {
            echo("Next steps:")
            echo("  1. cd ${targetDir.name}")
            2
        } else {
            echo("Next steps:")
            1
        }

        echo("  $startIndex. ./gradlew build")

        when (selection.templateType) {
            "js" -> {
                echo("  ${startIndex + 1}. ./gradlew jsBrowserDevelopmentRun")
                echo("     Open http://localhost:8080 to see your app")
            }

            "ktor" -> {
                echo("  ${startIndex + 1}. ./gradlew run")
                echo("     Backend + Summon UI available at http://localhost:8080")
            }

            "spring-boot" -> {
                echo("  ${startIndex + 1}. ./gradlew bootRun")
                echo("     Backend + Summon UI available at http://localhost:8080")
            }

            "quarkus" -> {
                echo("  ${startIndex + 1}. ./gradlew unitTest   # lightweight backend checks")
                echo("  ${startIndex + 2}. ./gradlew quarkusDev  # hot-reload backend + Summon UI")
            }
        }
    }

    private fun resolveTemplateSelection(): TemplateResolution {
        val selectedMode = mode?.let { parseMode(it) } ?: promptForMode()

        return when (selectedMode) {
            Mode.STANDALONE -> TemplateResolution(
                templateType = "js",
                displayName = "Standalone site (browser)",
                mode = selectedMode,
                backend = null
            )

            Mode.FULLSTACK -> {
                val selectedBackend = backend?.let { parseBackend(it) } ?: promptForBackend()
                TemplateResolution(
                    templateType = selectedBackend.templateType,
                    displayName = "Full stack (${selectedBackend.displayName})",
                    mode = selectedMode,
                    backend = selectedBackend
                )
            }
        }
    }

    private fun promptForMode(): Mode {
        while (true) {
            echo("Select project type:")
            echo("  1) Standalone site")
            echo("  2) Full stack (Summon UI + backend)")
            echo("> ", trailingNewline = false)
            val input = inputProvider() ?: throw CliktError("Exiting: No input provided (EOF).")
            when (input.trim().lowercase()) {
                "1", "standalone", "s" -> return Mode.STANDALONE
                "2", "fullstack", "f" -> return Mode.FULLSTACK
                else -> echo("Please enter 1 or 2.")
            }
        }
    }

    private fun promptForBackend(): Backend {
        while (true) {
            echo("Select backend:")
            echo("  1) Spring Boot")
            echo("  2) Ktor")
            echo("  3) Quarkus")
            echo("> ", trailingNewline = false)
            val input = inputProvider() ?: throw CliktError("Exiting: No input provided (EOF).")
            when (input.trim().lowercase()) {
                "1", "spring", "spring-boot", "s" -> return Backend.SPRING
                "2", "ktor", "k" -> return Backend.KTOR
                "3", "quarkus", "q" -> return Backend.QUARKUS
                else -> echo("Please enter 1, 2, or 3.")
            }
        }
    }

    private fun parseMode(value: String): Mode = when (value.lowercase()) {
        "standalone" -> Mode.STANDALONE
        "fullstack" -> Mode.FULLSTACK
        else -> error("❌ Unsupported mode '$value'. Use standalone or fullstack.")
    }

    private fun parseBackend(value: String): Backend = when (value.lowercase()) {
        "spring", "spring-boot" -> Backend.SPRING
        "ktor" -> Backend.KTOR
        "quarkus" -> Backend.QUARKUS
        else -> error("❌ Unsupported backend '$value'. Use spring, ktor, or quarkus.")
    }

    private fun determineTargetDirectory(): File = when {
        here -> File(".").absoluteFile
        directory != null -> {
            val dir = File(directory!!)
            if (dir.isAbsolute) dir else File(".", directory!!).absoluteFile
        }

        else -> File(".", projectName).absoluteFile
    }

    private fun validateTargetDirectory(targetDir: File): String? = when {
        targetDir.exists() && !targetDir.isDirectory ->
            "Target path exists but is not a directory"

        targetDir.exists() && targetDir.listFiles()?.isNotEmpty() == true && !force ->
            "Directory is not empty. Use --force to overwrite or choose a different location"

        else -> null
    }

    private data class TemplateResolution(
        val templateType: String,
        val displayName: String,
        val mode: Mode,
        val backend: Backend?
    )

    private enum class Mode { STANDALONE, FULLSTACK }

    private enum class Backend(val templateType: String, val displayName: String) {
        SPRING("spring-boot", "Spring Boot"),
        KTOR("ktor", "Ktor"),
        QUARKUS("quarkus", "Quarkus")
    }
}
