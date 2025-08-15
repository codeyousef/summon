package code.yousef.summon.cli.commands

import code.yousef.summon.cli.generators.ComponentGenerator
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import java.io.File

/**
 * Command to generate individual components, pages, and other files.
 */
class GenerateCommand : CliktCommand(
    name = "generate"
) {

    init {
        subcommands(
            ComponentSubcommand(),
            PageSubcommand(),
            ThemeSubcommand()
        )
    }

    override fun run() {
        if (currentContext.invokedSubcommand == null) {
            echo(
                """
                Generate command requires a subcommand:
                
                  component   Generate a new component
                  page       Generate a new page/route
                  theme      Generate theme configuration
                
                Use 'summon generate <subcommand> --help' for specific help.
            """.trimIndent()
            )
        }
    }
}

/**
 * Subcommand to generate components
 */
class ComponentSubcommand : CliktCommand(
    name = "component"
) {

    private val componentName by argument(
        name = "name",
        help = "Name of the component (e.g., UserCard, LoginForm)"
    )

    private val componentType by option(
        "--type", "-t",
        help = "Type of component to generate"
    ).choice("basic", "stateful", "form", "layout", "input").default("basic")

    private val withTest by option(
        "--test",
        help = "Generate test file"
    ).flag(default = true)

    private val withDoc by option(
        "--doc",
        help = "Generate documentation file"
    ).flag()

    private val packagePath by option(
        "--package", "-p",
        help = "Package path relative to components (e.g., ui/cards)"
    )

    override fun run() {
        echo("🧩 Generating component: $componentName")
        echo("📦 Type: $componentType")

        try {
            val generator = ComponentGenerator()
            val config = ComponentGenerator.Config(
                name = componentName,
                type = componentType,
                packagePath = packagePath,
                generateTest = withTest,
                generateDoc = withDoc
            )

            generator.generate(config)

            echo("✅ Component generated successfully!")
            echo("📁 Files created:")

            val basePath = "src/commonMain/kotlin"
            val packageDir = if (packagePath != null) "components/$packagePath" else "components"
            echo("   - $basePath/$packageDir/$componentName.kt")

            if (withTest) {
                echo("   - src/commonTest/kotlin/$packageDir/${componentName}Test.kt")
            }

            if (withDoc) {
                echo("   - docs/components/$packageDir/$componentName.md")
            }

        } catch (e: Exception) {
            echo("❌ Failed to generate component: ${e.message}")
            if (currentContext.findObject<Boolean>() == true) {
                e.printStackTrace()
            }
        }
    }
}

/**
 * Subcommand to generate pages
 */
class PageSubcommand : CliktCommand(
    name = "page"
) {

    private val pageName by argument(
        name = "name",
        help = "Name of the page (e.g., About, ContactUs, UserProfile)"
    )

    private val route by option(
        "--route", "-r",
        help = "Route path (defaults to lowercase page name)"
    )

    private val withLayout by option(
        "--layout",
        help = "Use layout wrapper"
    ).flag(default = true)

    override fun run() {
        val routePath = route ?: pageName.lowercase()

        echo("📄 Generating page: $pageName")
        echo("🛣️  Route: /$routePath")

        try {
            // TODO: Implement page generation logic
            val pagesDir = File("src/commonMain/kotlin/routing/pages")
            if (!pagesDir.exists()) {
                echo("❌ Not in a Summon project directory (pages directory not found)")
                return
            }

            val pageFile = File(pagesDir, "$pageName.kt")

            val pageContent = generatePageContent(pageName, routePath, withLayout)
            pageFile.writeText(pageContent)

            echo("✅ Page generated successfully!")
            echo("📁 Created: ${pageFile.relativeTo(File("."))}")
            echo("🌐 Available at: http://localhost:8080/$routePath")

        } catch (e: Exception) {
            echo("❌ Failed to generate page: ${e.message}")
        }
    }

    private fun generatePageContent(name: String, route: String, withLayout: Boolean): String {
        return """
@file:Suppress("FunctionName")

package routing.pages

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.core.BasicText
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.padding

/**
 * $name page component
 * Route: /$route
 */
@Composable
fun $name() {
    ${if (withLayout) "PageLayout {" else ""}
        Column(
            modifier = Modifier.padding(16)
        ) {
            BasicText(
                text = "$name",
                modifier = Modifier.padding(bottom = 16)
            )
            
            BasicText(
                text = "Welcome to the $name page!"
            )
            
            // TODO: Add your page content here
        }
    ${if (withLayout) "}" else ""}
}

${
            if (withLayout) """
@Composable
private fun PageLayout(content: @Composable () -> Unit) {
    // TODO: Add common layout elements (header, footer, navigation)
    content()
}
""" else ""
        }
        """.trimIndent()
    }
}

/**
 * Subcommand to generate theme configuration
 */
class ThemeSubcommand : CliktCommand(
    name = "theme"
) {

    private val themeName by argument(
        name = "name",
        help = "Name of the theme (e.g., Dark, Corporate, Gaming)"
    )

    private val baseTheme by option(
        "--base",
        help = "Base theme to extend"
    ).choice("material", "minimal", "custom").default("material")

    override fun run() {
        echo("🎨 Generating theme: $themeName")
        echo("📋 Base: $baseTheme")

        try {
            // TODO: Implement theme generation
            echo("⚠️  Theme generation not yet implemented")

        } catch (e: Exception) {
            echo("❌ Failed to generate theme: ${e.message}")
        }
    }
}