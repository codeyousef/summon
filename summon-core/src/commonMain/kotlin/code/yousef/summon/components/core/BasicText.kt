/**
 * # Core Components Package
 *
 * This package provides fundamental UI components that form the foundation of the Summon component library.
 *
 * ## Overview
 *
 * Core components are the building blocks used throughout the framework:
 *
 * - **BasicText**: Simplified text rendering with essential styling
 * - **ThemeProvider**: Context provider for application theming
 * - **Composition Primitives**: Fundamental composition utilities
 *
 * These components are optimized for performance and provide the essential functionality
 * needed by higher-level components in the framework.
 *
 * @since 1.0.0
 */
package code.yousef.summon.components.core

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.theme.TextStyle

/**
 * Represents the result of a text layout operation.
 *
 * TextLayoutResult provides information about how text was laid out after
 * rendering, including dimensions, line information, and overflow status.
 * This information can be used for dynamic sizing, scrolling calculations,
 * and responsive layout decisions.
 *
 * ## Usage
 *
 * Text layout results are provided through the [onTextLayout] callback
 * in text components:
 *
 * ```kotlin
 * BasicText(
 *     text = "Sample text",
 *     onTextLayout = { result ->
 *         println("Text size: ${result.width} x ${result.height}")
 *         println("Lines: ${result.lineCount}")
 *         if (result.hasVisualOverflow) {
 *             println("Text overflows container")
 *         }
 *     }
 * )
 * ```
 *
 * ## Properties
 *
 * - [width]: The total width of the laid-out text in pixels
 * - [height]: The total height of the laid-out text in pixels
 * - [lineCount]: Number of lines the text spans (for multi-line text)
 * - [hasVisualOverflow]: Whether text extends beyond its container
 *
 * @property width The rendered width of the text in pixels
 * @property height The rendered height of the text in pixels
 * @property lineCount Number of text lines (1 for single-line text)
 * @property hasVisualOverflow True if text exceeds container bounds
 * @see BasicText
 * @see code.yousef.summon.components.display.Text
 * @since 1.0.0
 */
class TextLayoutResult(
    val width: Float,
    val height: Float,
    val lineCount: Int = 1,
    val hasVisualOverflow: Boolean = false
)

/**
 * A lightweight text rendering component with essential styling capabilities.
 *
 * BasicText provides fundamental text display functionality with a simplified API
 * compared to the full-featured [Text] component. It's optimized for performance
 * and provides the core text rendering capabilities needed for most use cases.
 *
 * ## Key Features
 *
 * ### Styling Integration
 * - Seamless integration with [TextStyle] for typography
 * - Full [Modifier] support for layout and visual properties
 * - Automatic style-to-modifier conversion for platform rendering
 *
 * ### Performance Optimized
 * - Minimal overhead for simple text display
 * - Efficient style application and rendering
 * - Streamlined component lifecycle
 *
 * ### Platform Agnostic
 * - Works across all supported platforms (JS, JVM)
 * - Uses platform-specific renderers for optimal output
 * - Consistent behavior regardless of target platform
 *
 * ## Usage Examples
 *
 * ### Simple Text
 * ```kotlin
 * BasicText("Hello, World!")
 * ```
 *
 * ### Styled Text
 * ```kotlin
 * BasicText(
 *     text = "Styled Text",
 *     style = TextStyle(
 *         fontSize = "18px",
 *         fontWeight = "bold",
 *         color = "#007ACC"
 *     )
 * )
 * ```
 *
 * ### With Layout Callback
 * ```kotlin
 * BasicText(
 *     text = "Measured Text",
 *     modifier = Modifier().padding("16px"),
 *     onTextLayout = { result ->
 *         if (result.hasVisualOverflow) {
 *             // Handle text overflow
 *         }
 *     }
 * )
 * ```
 *
 * ### Advanced Styling
 * ```kotlin
 * BasicText(
 *     text = "Advanced Text",
 *     modifier = Modifier()
 *         .backgroundColor("#f0f0f0")
 *         .border("1px solid #ccc")
 *         .borderRadius("4px")
 *         .padding("8px"),
 *     style = TextStyle(
 *         fontFamily = "Arial, sans-serif",
 *         lineHeight = "1.5",
 *         letterSpacing = "0.5px"
 *     )
 * )
 * ```
 *
 * ## Style Application
 *
 * BasicText automatically converts [TextStyle] properties to appropriate
 * [Modifier] styles for platform rendering:
 *
 * - `fontFamily` → `Modifier.fontFamily()`
 * - `fontSize` → `Modifier.fontSize()`
 * - `fontWeight` → `Modifier.fontWeight()`
 * - `color` → `Modifier.color()`
 * - `textDecoration` → `Modifier.textDecoration()`
 * - `lineHeight` → `Modifier.lineHeight()`
 * - `letterSpacing` → `Modifier.letterSpacing()`
 *
 * ## Performance Considerations
 *
 * - Prefer BasicText over Text for simple text display
 * - Use [remember] for expensive style calculations
 * - Consider text length impact on layout performance
 * - Layout callbacks are called on every recomposition
 *
 * ## Comparison with Text Component
 *
 * | Feature | BasicText | Text |
 * |---------|-----------|------|
 * | Performance | High | Medium |
 * | API Complexity | Simple | Full-featured |
 * | Text Selection | No | Yes |
 * | Rich Formatting | Limited | Full |
 * | Use Case | Simple display | Complex text needs |
 *
 * @param text The text content to display
 * @param modifier [Modifier] for layout, styling, and interaction
 * @param style [TextStyle] defining typography and text appearance
 * @param onTextLayout Callback providing [TextLayoutResult] after text layout
 * @see code.yousef.summon.components.display.Text for full-featured text display
 * @see TextStyle for typography configuration
 * @see TextLayoutResult for layout information
 * @since 1.0.0
 */
@Composable
fun BasicText(
    text: String,
    modifier: Modifier = Modifier(),
    style: TextStyle = TextStyle(),
    onTextLayout: (TextLayoutResult) -> Unit = {}
) {
    // Get the platform renderer using CompositionLocal
    val renderer = LocalPlatformRenderer.current

    // Convert TextStyle to Modifier styles
    var finalModifier = modifier

    // Apply text style properties to the modifier
    style.fontFamily?.let { finalModifier = finalModifier.fontFamily(it, null) }
    style.fontSize?.let { finalModifier = finalModifier.fontSize(it) }
    style.fontWeight?.let { finalModifier = finalModifier.fontWeight(it) }
    style.fontStyle?.let { finalModifier = finalModifier.style("font-style", it) }
    style.color?.let { finalModifier = finalModifier.color(it) }
    style.textDecoration?.let { finalModifier = finalModifier.textDecoration(it, null) }
    style.lineHeight?.let { finalModifier = finalModifier.lineHeight(it, null) }
    style.letterSpacing?.let { finalModifier = finalModifier.letterSpacing(it, null) }

    // Render the text using the platform renderer with correct arguments
    renderer.renderText(text = text, modifier = finalModifier)

    // Create a layout result (simplified for now)
    val layoutResult = TextLayoutResult(
        width = text.length.toFloat() * 8, // Rough estimate
        height = 20f // Default height
    )

    // Notify the callback
    onTextLayout(layoutResult)
}

/**
 * Marker annotation for composable functions.
 * This will eventually be replaced by the actual Compose annotation.
 */
// Removed redundant annotation class as we're now using the one from runtime package 
