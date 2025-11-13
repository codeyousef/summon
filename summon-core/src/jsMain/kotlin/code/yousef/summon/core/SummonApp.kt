package codes.yousef.summon.core

import codes.yousef.summon.annotation.Composable
import kotlinx.browser.document

/**
 * Default application composable for Summon applications.
 * This is used as the default root composable if no custom @App is defined.
 */
@Composable
fun SummonApp(content: @Composable () -> Unit) {
    // Apply default styles
    applyDefaultStyles()

    // Render content
    content()
}

/**
 * Applies the default styles to the document.
 */
private fun applyDefaultStyles() {
    // Create a style element for default styles
    val styleElement = document.createElement("style")
    styleElement.textContent = """
        html, body {
            padding: 0;
            margin: 0;
        }

        * {
            box-sizing: border-box;
        }
    """.trimIndent()

    // Add to document head
    document.head?.appendChild(styleElement)
}

