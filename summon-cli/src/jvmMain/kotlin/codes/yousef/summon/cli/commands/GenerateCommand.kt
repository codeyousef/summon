package codes.yousef.summon.cli.commands

import codes.yousef.summon.cli.generators.ComponentGenerator
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

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.foundation.BasicText
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Spacer
import codes.yousef.summon.components.navigation.Link
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.padding
import codes.yousef.summon.modifier.fillMaxWidth
import codes.yousef.summon.modifier.fillMaxSize
import codes.yousef.summon.modifier.weight
import codes.yousef.summon.theme.Color
import codes.yousef.summon.theme.TextStyle
import codes.yousef.summon.theme.FontWeight
import codes.yousef.summon.components.layout.Alignment

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
                text = "Welcome to the $name page!",
                modifier = Modifier.padding(bottom = 8)
            )
            
            // Page content examples
            BasicText(
                text = "This page was generated by Summon CLI.",
                modifier = Modifier.padding(bottom = 16)
            )
            
            // Example navigation
            Row(
                modifier = Modifier.padding(top = 16)
            ) {
                Link(href = "/") {
                    BasicText("← Home")
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Link(href = "/about") {
                    BasicText("About →")
                }
            }
        }
    ${if (withLayout) "}" else ""}
}

${
            if (withLayout) """
@Composable
private fun PageLayout(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16)
                .borderBottom(1, Color.Gray),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicText(
                text = "My App",
                style = TextStyle(fontSize = 24, fontWeight = FontWeight.Bold)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Navigation links
            Row {
                Link(href = "/") {
                    BasicText("Home", modifier = Modifier.padding(horizontal = 8))
                }
                Link(href = "/about") {
                    BasicText("About", modifier = Modifier.padding(horizontal = 8))
                }
            }
        }
        
        // Main content area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            content()
        }
        
        // Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16)
                .borderTop(1, Color.Gray),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicText(
                text = "© 2024 My App. Built with Summon.",
                style = TextStyle(fontSize = 12, color = Color.Gray)
            )
        }
    }
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
            // Check if we're in a Summon project
            val themeDir = File("src/commonMain/kotlin/theme")
            if (!themeDir.exists()) {
                echo("❌ Not in a Summon project directory (theme directory not found)")
                return
            }

            val themeFile = File(themeDir, "${themeName}Theme.kt")
            val themeContent = generateThemeContent(themeName, baseTheme)

            themeFile.writeText(themeContent)

            echo("✅ Theme generated successfully!")
            echo("📁 Created: ${themeFile.relativeTo(File("."))}")
            echo("🎨 Usage: Apply this theme in your ThemeProvider")
            echo("")
            echo("Example usage:")
            echo("```kotlin")
            echo("ThemeProvider(theme = ${themeName}Theme) {")
            echo("    // Your app content")
            echo("}")
            echo("```")

        } catch (e: Exception) {
            echo("❌ Failed to generate theme: ${e.message}")
        }
    }

    private fun generateThemeContent(name: String, base: String): String {
        val colorPalette = when (base) {
            "material" -> generateMaterialColors()
            "minimal" -> generateMinimalColors()
            "custom" -> generateCustomColors()
            else -> generateMaterialColors()
        }

        return """
package theme

import codes.yousef.summon.theme.Theme
import codes.yousef.summon.theme.Colors
import codes.yousef.summon.theme.Typography
import codes.yousef.summon.theme.Shapes

/**
 * ${name} theme based on ${base} design system
 * Generated by Summon CLI
 */
val ${name}Theme = Theme(
    colors = Colors(
        ${colorPalette.joinToString(",\n        ")}
    ),
    typography = Typography(
        // Customize typography here
        h1 = TextStyle(
            fontSize = 32,
            fontWeight = FontWeight.Bold,
            lineHeight = 40
        ),
        h2 = TextStyle(
            fontSize = 28,
            fontWeight = FontWeight.Bold,
            lineHeight = 36
        ),
        body1 = TextStyle(
            fontSize = 16,
            fontWeight = FontWeight.Normal,
            lineHeight = 24
        ),
        body2 = TextStyle(
            fontSize = 14,
            fontWeight = FontWeight.Normal,
            lineHeight = 20
        ),
        button = TextStyle(
            fontSize = 14,
            fontWeight = FontWeight.Medium,
            lineHeight = 20
        ),
        caption = TextStyle(
            fontSize = 12,
            fontWeight = FontWeight.Normal,
            lineHeight = 16
        )
    ),
    shapes = Shapes(
        small = 4,
        medium = 8,
        large = 16
    )
)
        """.trimIndent()
    }

    private fun generateMaterialColors(): List<String> = listOf(
        "primary = Color(0xFF6200EE)",
        "primaryVariant = Color(0xFF3700B3)",
        "secondary = Color(0xFF03DAC6)",
        "secondaryVariant = Color(0xFF018786)",
        "background = Color(0xFFFFFFFF)",
        "surface = Color(0xFFFFFFFF)",
        "error = Color(0xFFB00020)",
        "onPrimary = Color(0xFFFFFFFF)",
        "onSecondary = Color(0xFF000000)",
        "onBackground = Color(0xFF000000)",
        "onSurface = Color(0xFF000000)",
        "onError = Color(0xFFFFFFFF)"
    )

    private fun generateMinimalColors(): List<String> = listOf(
        "primary = Color(0xFF000000)",
        "primaryVariant = Color(0xFF424242)",
        "secondary = Color(0xFF757575)",
        "secondaryVariant = Color(0xFF9E9E9E)",
        "background = Color(0xFFFFFFFF)",
        "surface = Color(0xFFFAFAFA)",
        "error = Color(0xFFE57373)",
        "onPrimary = Color(0xFFFFFFFF)",
        "onSecondary = Color(0xFFFFFFFF)",
        "onBackground = Color(0xFF000000)",
        "onSurface = Color(0xFF000000)",
        "onError = Color(0xFFFFFFFF)"
    )

    private fun generateCustomColors(): List<String> = listOf(
        "primary = Color(0xFF2196F3)",
        "primaryVariant = Color(0xFF1976D2)",
        "secondary = Color(0xFFFF9800)",
        "secondaryVariant = Color(0xFFF57C00)",
        "background = Color(0xFFF5F5F5)",
        "surface = Color(0xFFFFFFFF)",
        "error = Color(0xFFF44336)",
        "onPrimary = Color(0xFFFFFFFF)",
        "onSecondary = Color(0xFF000000)",
        "onBackground = Color(0xFF212121)",
        "onSurface = Color(0xFF212121)",
        "onError = Color(0xFFFFFFFF)"
    )
}