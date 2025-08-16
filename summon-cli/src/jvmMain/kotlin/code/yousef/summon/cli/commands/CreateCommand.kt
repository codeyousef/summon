package code.yousef.summon.cli.commands

import code.yousef.summon.cli.generators.ProjectGenerator
import code.yousef.summon.cli.templates.ProjectTemplate
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import java.io.File

/**
 * Command to create a new Summon project from predefined templates.
 * Similar to init but with more template-specific options.
 */
class CreateCommand : CliktCommand(
    name = "create"
) {

    private val template by argument(
        name = "template",
        help = "Template type to create"
    ).choice("js-app", "quarkus-app", "spring-boot-app", "ktor-app", "library", "example", "blank")

    private val projectName by option(
        "--name", "-n",
        help = "Project name"
    ).default("my-summon-app")

    private val packageName by option(
        "--package", "-p",
        help = "Base package name"
    ).default("com.example.app")

    private val directory by option(
        "--output", "-o",
        help = "Output directory (defaults to project name, use '.' for current directory)"
    )

    private val here by option(
        "--here",
        help = "Generate project in current directory"
    ).flag()

    private val withExamples by option(
        "--examples",
        help = "Include example components"
    ).flag()

    private val withAuth by option(
        "--auth",
        help = "Include authentication setup (for backend templates)"
    ).flag()

    private val withDocker by option(
        "--docker",
        help = "Include Docker configuration"
    ).flag()

    private val minimal by option(
        "--minimal", "-m",
        help = "Generate minimal/blank project without examples"
    ).flag()

    override fun run() {
        val targetDir = determineTargetDirectory()

        echo("🎨 Creating Summon project from template: $template")
        echo("📁 Output directory: ${targetDir.absolutePath}")
        echo("🏷️  Package: $packageName")

        // Map template argument to internal template type
        val templateType = when (template) {
            "js-app" -> "js"
            "quarkus-app" -> "quarkus"
            "spring-boot-app" -> "spring-boot"
            "ktor-app" -> "ktor"
            "library" -> "library"
            "example" -> "example"
            "blank" -> "basic"
            else -> "basic"
        }

        // For "blank" template, force minimal mode
        val forceMinimal = template == "blank"

        // Validate target directory
        val validationError = validateTargetDirectory(targetDir)
        if (validationError != null) {
            echo("❌ $validationError")
            return
        }

        try {
            val projectTemplate = ProjectTemplate.fromType(templateType)
            val generator = ProjectGenerator(projectTemplate)

            val config = ProjectGenerator.Config(
                projectName = projectName,
                packageName = packageName,
                targetDirectory = targetDir,
                templateType = templateType,
                includeExamples = withExamples,
                includeAuth = withAuth,
                includeDocker = withDocker,
                minimal = minimal || forceMinimal,
                overwrite = false
            )

            echo("📋 Generating project from template...")
            generator.generate(config)

            echo("✅ Project created successfully!")
            echo("")

            printNextSteps(templateType, targetDir, template == "blank")

        } catch (e: Exception) {
            echo("❌ Failed to create project: ${e.message}")
            if (currentContext.findObject<Boolean>() == true) { // verbose mode
                e.printStackTrace()
            }
        }
    }

    private fun printNextSteps(templateType: String, targetDir: File, isBlank: Boolean = false) {
        echo("📚 Next steps:")
        if (!here && targetDir.name != ".") {
            echo("  cd ${targetDir.name}")
        }

        val stepOffset = if (!here && targetDir.name != ".") 1 else 0

        when (templateType) {
            "js" -> {
                echo("  ${stepOffset + 1}. ./gradlew jsBrowserDevelopmentRun")
                echo("")
                if (isBlank) {
                    echo("🌐 Your minimal app will be available at http://localhost:8080")
                    echo("📝 Start building by editing src/jsMain/kotlin/Main.kt")
                    echo("💡 Add components, state, and styling as needed")
                } else {
                    echo("🌐 Your app will be available at http://localhost:8080")
                    echo("📝 Edit src/jsMain/kotlin/ to modify your components")
                }
            }

            "quarkus" -> {
                echo("  ${stepOffset + 1}. ./gradlew quarkusDev")
                echo("")
                echo("🌐 Your app will be available at http://localhost:8080")
                echo("📝 Backend: src/main/kotlin/")
                echo("📝 Frontend: src/jsMain/kotlin/")
                echo("🔥 Hot reload enabled for both frontend and backend")
            }

            "spring-boot" -> {
                echo("  ${stepOffset + 1}. ./gradlew bootRun")
                echo("")
                echo("🌐 Your app will be available at http://localhost:8080")
                echo("📝 Backend: src/main/kotlin/")
                echo("📝 Frontend: src/jsMain/kotlin/")
            }

            "ktor" -> {
                echo("  ${stepOffset + 1}. ./gradlew run")
                echo("")
                echo("🌐 Your app will be available at http://localhost:8080")
                echo("📝 Backend: src/main/kotlin/")
                echo("📝 Frontend: src/jsMain/kotlin/")
            }

            "library" -> {
                echo("  ${stepOffset + 1}. ./gradlew build")
                echo("  ${stepOffset + 2}. ./gradlew publishToMavenLocal")
                echo("")
                echo("📦 Your library components are in src/commonMain/kotlin/")
                echo("🧪 Tests are in src/commonTest/kotlin/")
            }

            "example" -> {
                echo("  ${stepOffset + 1}. ./gradlew jsBrowserDevelopmentRun")
                echo("")
                echo("🎯 Example components included:")
                echo("   - Button variations")
                echo("   - Form components")
                echo("   - Layout examples")
                echo("   - Theme customization")
            }
        }

        echo("")
        echo("📖 Documentation: https://github.com/codeyousef/summon")
        echo("🐛 Issues: https://github.com/codeyousef/summon/issues")
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

            targetDir.exists() && targetDir.listFiles()?.isNotEmpty() == true -> {
                "Directory is not empty. Use a different location or remove existing files"
            }

            else -> null
        }
    }
}