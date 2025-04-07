package code.yousef.summon.theme


import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable


/**
 * Typography provides consistent text styling presets across the application.
 * It defines standard font styles for different text elements like headings,
 * body text, captions, etc.
 */

// --- Generic Text Style Applier ---
@Composable
private fun ThemedText(
    text: String,
    styleKey: (Typography) -> String,
    modifier: Modifier = Modifier()
) {
    // Assumes Typography data class is defined elsewhere (e.g., Theme.kt)
    val typography = SummonTheme.typography 
    val styleString = styleKey(typography)
    Text(text = text, modifier = modifier)
}

// --- Specific Themed Text Composables ---
@Composable fun H1(text: String, modifier: Modifier = Modifier()) = ThemedText(text, { it.h1 }, modifier)
@Composable fun H2(text: String, modifier: Modifier = Modifier()) = ThemedText(text, { "font-size: 1.5em; font-weight: bold;" }, modifier)
@Composable fun H3(text: String, modifier: Modifier = Modifier()) = ThemedText(text, { "font-size: 1.2em; font-weight: bold;" }, modifier)
@Composable fun Paragraph(text: String, modifier: Modifier = Modifier()) = ThemedText(text, { it.body1 }, modifier)
@Composable fun Caption(text: String, modifier: Modifier = Modifier()) = ThemedText(text, { "font-size: 0.9em; color: grey;" }, modifier)
@Composable fun Label(text: String, modifier: Modifier = Modifier()) = ThemedText(text, { "font-size: 0.9em; font-weight: 500;" }, modifier)
@Composable fun Monospace(text: String, modifier: Modifier = Modifier()) = ThemedText(text, { "font-family: monospace;" }, modifier)
// Add others

// Removed old individual text style classes implementing Composable
// Removed old typographyText helper function 
