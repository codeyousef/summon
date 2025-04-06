package code.yousef.summon.examples

import androidx.compose.runtime.Composable
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.*
import code.yousef.summon.theme.*
import code.yousef.summon.ui.Alignment
import code.yousef.summon.ui.unit.dp
import code.yousef.summon.ui.unit.px

// Placeholder Color data class until theme is sorted out
data class Color(val hex: String) { // Simplified placeholder
    companion object {
        val Black = Color("#000000")
        val White = Color("#FFFFFF")
    }
}

// Placeholder Theme objects
object ThemeColors { // TODO: Replace with actual theme access
    val primary = Color("#6200EE")
    val secondary = Color("#03DAC6")
    val surface = Color("#FFFFFF")
    val background = Color("#FFFFFF")
    val error = Color("#B00020")
    val onPrimary = Color("#FFFFFF")
    val onSecondary = Color("#000000")
    val onSurface = Color("#000000")
    val onBackground = Color("#000000")
    val onError = Color("#FFFFFF")
}

object MaterialTheme { // TODO: Replace with actual theme access
    object typography {
        // Placeholder styles - these should likely be Modifier extensions or similar
        val h1: Modifier = Modifier.fontSize("96px").fontWeight("300")
        val h2: Modifier = Modifier.fontSize("60px").fontWeight("300")
        val h3: Modifier = Modifier.fontSize("48px").fontWeight("400")
        val h4: Modifier = Modifier.fontSize("34px").fontWeight("400")
        val h5: Modifier = Modifier.fontSize("24px").fontWeight("400")
        val h6: Modifier = Modifier.fontSize("20px").fontWeight("500")
        val subtitle1: Modifier = Modifier.fontSize("16px").fontWeight("400")
        val subtitle2: Modifier = Modifier.fontSize("14px").fontWeight("500")
        val body1: Modifier = Modifier.fontSize("16px").fontWeight("400")
        val body2: Modifier = Modifier.fontSize("14px").fontWeight("400")
        val button: Modifier = Modifier.fontSize("14px").fontWeight("500") // .textTransform("uppercase") // TODO
        val caption: Modifier = Modifier.fontSize("12px").fontWeight("400")
        val overline: Modifier = Modifier.fontSize("10px").fontWeight("400") // .textTransform("uppercase") // TODO
    }
}

/**
 * Demonstrates the usage of predefined colors and typography styles from the theme.
 */
@Composable
fun ColorAndTypographyExample() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Theme Colors & Typography Showcase", modifier = MaterialTheme.typography.h5)

        // Color Showcase Section
        ColorShowcase()

        // Typography Showcase Section
        TypographyShowcase()
    }
}

@Composable
private fun ColorShowcase() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text("Color Palette", modifier = MaterialTheme.typography.h6.padding(bottom = 8.dp))

        val colors = mapOf(
            "Primary" to ThemeColors.primary,
            "Secondary" to ThemeColors.secondary,
            "Surface" to ThemeColors.surface,
            "Background" to ThemeColors.background,
            "Error" to ThemeColors.error,
            "OnPrimary" to ThemeColors.onPrimary,
            "OnSecondary" to ThemeColors.onSecondary,
            "OnSurface" to ThemeColors.onSurface,
            "OnBackground" to ThemeColors.onBackground,
            "OnError" to ThemeColors.onError
        )

        colors.entries.chunked(2).forEach { rowEntries ->
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                rowEntries.forEach { (name, color) ->
                    ColorSample(name, color, getContrastColor(color))
                }
            }
        }
    }
}

@Composable
private fun TypographyShowcase() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text("Typography Styles", modifier = MaterialTheme.typography.h6.padding(bottom = 8.dp))

        val styles = mapOf(
            "H1" to MaterialTheme.typography.h1,
            "H2" to MaterialTheme.typography.h2,
            "H3" to MaterialTheme.typography.h3,
            "H4" to MaterialTheme.typography.h4,
            "H5" to MaterialTheme.typography.h5,
            "H6" to MaterialTheme.typography.h6,
            "Subtitle1" to MaterialTheme.typography.subtitle1,
            "Subtitle2" to MaterialTheme.typography.subtitle2,
            "Body1" to MaterialTheme.typography.body1,
            "Body2" to MaterialTheme.typography.body2,
            "Button" to MaterialTheme.typography.button,
            "Caption" to MaterialTheme.typography.caption,
            "Overline" to MaterialTheme.typography.overline
        )

        styles.forEach { (name, styleModifier) ->
            Text(
                text = "$name: The quick brown fox jumps over the lazy dog",
                modifier = styleModifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ColorSample(name: String, color: Color, textColor: Color) {
    Card(
        modifier = Modifier.padding(4.dp),
        elevation = "2px"
    ) {
        Box(
            modifier = Modifier.size(120.dp, 80.dp).background(color.hex).padding(8.dp),
        ) {
            Column() {
                Text(name, modifier = MaterialTheme.typography.caption.color(textColor.hex))
                Text(color.toHex(), modifier = MaterialTheme.typography.caption.color(textColor.hex).padding(top = 4.dp))
            }
        }
    }
}

/**
 * Determines a suitable contrast color (black or white) for text on a given background color.
 */
private fun getContrastColor(backgroundColor: Color): Color {
    val brightness = try {
        val r = Integer.parseInt(backgroundColor.hex.substring(1, 3), 16)
        val g = Integer.parseInt(backgroundColor.hex.substring(3, 5), 16)
        val b = Integer.parseInt(backgroundColor.hex.substring(5, 7), 16)
        (0.299 * r + 0.587 * g + 0.114 * b)
    } catch (e: Exception) {
        128.0
    }
    return if (brightness > 128) Color.Black else Color.White
}

/**
 * Placeholder function to convert Color to Hex string (implement actual logic).
 */
private fun Color.toHex(): String {
    return this.hex
} 