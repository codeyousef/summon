package code.yousef.summon.examples.js

import kotlinx.browser.document
import kotlinx.browser.window

// Standalone Summon implementation - no external dependencies required
@Target(AnnotationTarget.FUNCTION)
annotation class Composable

// Summon Modifier system for type-safe styling
data class Modifier(
    val styles: Map<String, String> = emptyMap(),
    val attributes: Map<String, String> = emptyMap()
) {
    fun style(propertyName: String, value: String): Modifier =
        copy(styles = this.styles + (propertyName to value))
        
    fun padding(value: String): Modifier = style("padding", value)
    fun padding(vertical: String, horizontal: String): Modifier = style("padding", "$vertical $horizontal")
    fun padding(top: String, right: String, bottom: String, left: String): Modifier = 
        style("padding", "$top $right $bottom $left")
    fun margin(value: String): Modifier = style("margin", value)
    fun marginTop(value: String): Modifier = style("margin-top", value)
    fun marginBottom(value: String): Modifier = style("margin-bottom", value)
    fun backgroundColor(color: String): Modifier = style("background-color", color)
    fun color(value: String): Modifier = style("color", value)
    fun fontSize(value: String): Modifier = style("font-size", value)
    fun fontWeight(value: String): Modifier = style("font-weight", value)
    fun gap(value: String): Modifier = style("gap", value)
    fun display(value: String): Modifier = style("display", value)
    fun flexDirection(value: String): Modifier = style("flex-direction", value)
    fun alignItems(value: String): Modifier = style("align-items", value)
    fun justifyContent(value: String): Modifier = style("justify-content", value)
    fun borderRadius(value: String): Modifier = style("border-radius", value)
    fun border(value: String): Modifier = style("border", value)
    fun cursor(value: String): Modifier = style("cursor", value)
    fun textAlign(value: String): Modifier = style("text-align", value)
    fun minHeight(value: String): Modifier = style("min-height", value)
    fun width(value: String): Modifier = style("width", value)
    fun height(value: String): Modifier = style("height", value)
    
    fun attribute(name: String, value: String): Modifier =
        copy(attributes = this.attributes + (name to value))
    
    fun onClick(handler: String): Modifier = attribute("onclick", handler)
    fun id(value: String): Modifier = attribute("id", value)
    fun className(value: String): Modifier = attribute("class", value)
    
    fun toStyleString(): String =
        if (styles.isEmpty()) "" else styles.entries.joinToString(
            separator = "; ",
            postfix = ";"
        ) { "${it.key}: ${it.value}" }
        
    fun toAttributesString(): String =
        attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }
}

// Type-safe CSS enums
enum class Display { 
    None, Block, Inline, InlineBlock, Flex, Grid, InlineFlex, InlineGrid 
}

enum class Position { 
    Static, Relative, Absolute, Fixed, Sticky 
}

enum class FlexDirection { 
    Row, Column, RowReverse, ColumnReverse 
}

enum class JustifyContent { 
    FlexStart, FlexEnd, Center, SpaceBetween, SpaceAround, SpaceEvenly 
}

enum class AlignItems { 
    FlexStart, FlexEnd, Center, Baseline, Stretch 
}

enum class TextAlign { 
    Left, Right, Center, Justify, Start, End 
}

enum class FontWeight { 
    Thin, ExtraLight, Light, Normal, Medium, SemiBold, Bold, ExtraBold, Black 
}

enum class BorderStyle { 
    None, Solid, Dashed, Dotted, Double, Groove, Ridge, Inset, Outset 
}

enum class Cursor { 
    Auto, Default, Pointer, Wait, Text, Move, NotAllowed, Crosshair 
}

// Extension functions to convert enums to CSS values
fun String.toKebabCase(): String = this.fold(StringBuilder()) { acc, char ->
    when {
        char.isUpperCase() && acc.isNotEmpty() -> acc.append("-").append(char.lowercase())
        else -> acc.append(char.lowercase())
    }
}.toString()

fun FontWeight.toCssValue(): String = when (this) {
    FontWeight.Thin -> "100"
    FontWeight.ExtraLight -> "200"
    FontWeight.Light -> "300"
    FontWeight.Normal -> "400"
    FontWeight.Medium -> "500"
    FontWeight.SemiBold -> "600"
    FontWeight.Bold -> "700"
    FontWeight.ExtraBold -> "800"
    FontWeight.Black -> "900"
}

// Type-safe modifier extensions
// Note: In a real application using the Summon framework, these would be imported.
// They are defined here only for this standalone example to work without dependencies.
fun Modifier.display(value: Display): Modifier = style("display", value.name.lowercase())
fun Modifier.position(value: Position): Modifier = style("position", value.name.lowercase())
fun Modifier.flexDirection(value: FlexDirection): Modifier = style("flex-direction", value.name.toKebabCase())
fun Modifier.justifyContent(value: JustifyContent): Modifier = style("justify-content", value.name.toKebabCase())
fun Modifier.alignItems(value: AlignItems): Modifier = style("align-items", value.name.toKebabCase())
fun Modifier.textAlign(value: TextAlign): Modifier = style("text-align", value.name.lowercase())
fun Modifier.fontWeight(value: FontWeight): Modifier = style("font-weight", value.toCssValue())
fun Modifier.cursor(value: Cursor): Modifier = style("cursor", value.name.toKebabCase())
fun Modifier.border(width: String, style: BorderStyle, color: String): Modifier = 
    style("border", "$width ${style.name.lowercase()} $color")

// CSS unit extensions
val Number.px: String get() = "${this}px"
val Number.rem: String get() = "${this}rem"
val Number.em: String get() = "${this}em"
val Number.percent: String get() = "${this}%"
val Number.vw: String get() = "${this}vw"
val Number.vh: String get() = "${this}vh"

// State management
class State<T>(var value: T)
fun <T> mutableStateOf(initial: T) = State(initial)
fun <T> remember(calculation: () -> State<T>) = calculation()

// Utility to create empty modifier
fun EmptyModifier(): Modifier = Modifier(emptyMap(), emptyMap())

// Pure Summon UI Components
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier()
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.toStyleString()}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.toAttributesString()}" else ""
    return "<span$attrsStr$styleStr>$text</span>"
}

@Composable
fun Button(
    text: String,
    modifier: Modifier = Modifier()
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.toStyleString()}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.toAttributesString()}" else ""
    return "<button$attrsStr$styleStr>$text</button>"
}

@Composable
fun Column(
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val columnModifier = modifier.display(Display.Flex).flexDirection(FlexDirection.Column)
    val styleStr = if (columnModifier.styles.isNotEmpty()) " style=\"${columnModifier.toStyleString()}\"" else ""
    val attrsStr = if (columnModifier.attributes.isNotEmpty()) " ${columnModifier.toAttributesString()}" else ""
    return "<div$attrsStr$styleStr>${content()}</div>"
}

@Composable
fun Row(
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val rowModifier = modifier.display(Display.Flex).flexDirection(FlexDirection.Row).alignItems(AlignItems.Center)
    val styleStr = if (rowModifier.styles.isNotEmpty()) " style=\"${rowModifier.toStyleString()}\"" else ""
    val attrsStr = if (rowModifier.attributes.isNotEmpty()) " ${rowModifier.toAttributesString()}" else ""
    return "<div$attrsStr$styleStr>${content()}</div>"
}

@Composable
fun Box(
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.toStyleString()}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.toAttributesString()}" else ""
    return "<div$attrsStr$styleStr>${content()}</div>"
}

@Composable
fun Card(
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val cardModifier = modifier
        .backgroundColor("white")
        .borderRadius(8.px)
        .style("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.1)")
        .padding(16.px)
    return Box(cardModifier) { content() }
}

// Simple Color type
data class Color(val hex: String) {
    companion object {
        val GREEN = Color("#4CAF50")
        val BLACK = Color("#000000")
        val BLUE = Color("#2196F3")
        val RED = Color("#F44336")
        val WHITE = Color("#FFFFFF")
        val GRAY = Color("#9E9E9E")
    }
    
    fun toHexString(): String = hex
}

/**
 * Main application content using standalone implementation
 */
@Composable
fun MainApp(): String {
    val count = remember { mutableStateOf(0) }
    
    return Column(
        modifier = EmptyModifier()
            .padding(16.px)
    ) {
        Text(
            text = "Summon JS Example (Standalone)",
            modifier = EmptyModifier()
                .fontSize(28.px)
                .fontWeight(FontWeight.Bold)
                .color("#333333")
                .margin("0 0 16px 0")
        ) +
        
        Text(
            text = "This example demonstrates the standalone Summon implementation for Kotlin/JS applications.",
            modifier = EmptyModifier()
                .fontSize(16.px)
                .color("#666666")
                .margin("0 0 24px 0")
        ) +
        
        Box(
            modifier = EmptyModifier()
                .backgroundColor(Color.GREEN.toHexString())
                .padding(16.px)
                .margin("16px 0")
                .borderRadius(8.px)
        ) {
            Text(
                text = "This box has a GREEN background",
                modifier = EmptyModifier()
                    .color(Color.BLACK.toHexString())
                    .fontWeight(FontWeight.Medium)
            )
        } +
        
        Card(
            modifier = EmptyModifier()
                .margin("16px 0")
        ) {
            Column(
                modifier = EmptyModifier()
                    .gap(12.px)
            ) {
                Text(
                    text = "Interactive Counter Demo",
                    modifier = EmptyModifier()
                        .fontSize(20.px)
                        .fontWeight(FontWeight.SemiBold)
                        .marginBottom(8.px)
                ) +
                
                Text(
                    text = "Count: 0", // Fixed: removed ${count.value} reference
                    modifier = EmptyModifier()
                        .fontSize(18.px)
                        .id("counter-display")
                ) +
                
                Row(
                    modifier = EmptyModifier()
                        .gap(8.px)
                        .marginTop(12.px)
                ) {
                    Button(
                        text = "Increment",
                        modifier = EmptyModifier()
                            .backgroundColor(Color.BLUE.toHexString())
                            .color(Color.WHITE.toHexString())
                            .padding(8.px, 16.px)
                            .borderRadius(4.px)
                            .cursor(Cursor.Pointer)
                            .border("0", BorderStyle.None, "transparent")
                            .onClick("incrementCounter()")
                    ) +
                    
                    Button(
                        text = "Decrement",
                        modifier = EmptyModifier()
                            .backgroundColor(Color.RED.toHexString())
                            .color(Color.WHITE.toHexString())
                            .padding(8.px, 16.px)
                            .borderRadius(4.px)
                            .cursor(Cursor.Pointer)
                            .border("0", BorderStyle.None, "transparent")
                            .onClick("decrementCounter()")
                    ) +
                    
                    Button(
                        text = "Reset",
                        modifier = EmptyModifier()
                            .backgroundColor(Color.GRAY.toHexString())
                            .color(Color.WHITE.toHexString())
                            .padding(8.px, 16.px)
                            .borderRadius(4.px)
                            .cursor(Cursor.Pointer)
                            .border("0", BorderStyle.None, "transparent")
                            .onClick("resetCounter()")
                    )
                }
            }
        } +
        
        Box(
            modifier = EmptyModifier()
                .backgroundColor("#f0f0f0")
                .padding(16.px)
                .borderRadius(8.px)
                .marginTop(16.px)
        ) {
            Column(
                modifier = EmptyModifier()
                    .gap(8.px)
            ) {
                Text(
                    text = "Features Demonstrated:",
                    modifier = EmptyModifier()
                        .fontWeight(FontWeight.SemiBold)
                        .marginBottom(4.px)
                ) +
                Text("✓ Type-safe styling with modifiers") +
                Text("✓ Composable functions for UI") +
                Text("✓ State management with mutableStateOf") +
                Text("✓ Event handling with JavaScript interop") +
                Text("✓ Type-safe CSS enums and unit extensions")
            }
        }
    }
}

/**
 * Generate the HTML document structure
 */
fun generateHtmlDocument() {
    // Create a style element for basic styling
    val styleElement = document.createElement("style")
    styleElement.textContent = """
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
            line-height: 1.6;
            color: #333;
            background-color: #f8fafc;
            padding: 20px;
        }
        
        #root {
            min-height: 100vh;
            width: 100%;
        }
    """.trimIndent()
    document.head?.appendChild(styleElement)
    
    // Create a title element
    val titleElement = document.createElement("title")
    titleElement.textContent = "Summon JS Example (Standalone)"
    document.head?.appendChild(titleElement)
    
    // Create a meta element for viewport
    val metaElement = document.createElement("meta")
    metaElement.setAttribute("name", "viewport")
    metaElement.setAttribute("content", "width=device-width, initial-scale=1.0")
    document.head?.appendChild(metaElement)
    
    // Create a root element if it doesn't exist
    if (document.getElementById("root") == null) {
        val rootElement = document.createElement("div")
        rootElement.id = "root"
        document.body?.appendChild(rootElement)
    }
}

/**
 * Add JavaScript for interactivity
 */
fun addInteractivityJS() {
    val scriptElement = document.createElement("script")
    scriptElement.textContent = """
        // Counter state
        let counter = 0;
        
        // Update the counter display
        function updateCounterDisplay() {
            const counterElement = document.getElementById('counter-display');
            if (counterElement) {
                counterElement.innerHTML = 'Count: ' + counter;
            }
        }
        
        // Counter functions
        function incrementCounter() {
            counter++;
            updateCounterDisplay();
        }
        
        function decrementCounter() {
            counter--;
            updateCounterDisplay();
        }
        
        function resetCounter() {
            counter = 0;
            updateCounterDisplay();
        }
        
        console.log('Interactive functions added');
    """.trimIndent()
    document.body?.appendChild(scriptElement)
}

fun main() {
    console.log("Standalone Summon JS Example starting...")
    
    // Wait for DOM to be ready
    if (document.readyState.toString() == "loading") {
        document.addEventListener("DOMContentLoaded", {
            initializeApp()
        })
    } else {
        initializeApp()
    }
}

fun initializeApp() {
    try {
        console.log("Initializing Summon standalone app...")
        
        // Generate the HTML document structure
        generateHtmlDocument()
        
        // Get the root element
        val rootElement = document.getElementById("root")
            ?: throw IllegalStateException("Root element not found")
        
        // Render the main application
        rootElement.innerHTML = MainApp()
        
        // Add JavaScript for interactivity
        addInteractivityJS()
        
        console.log("Summon standalone app initialized successfully!")
    } catch (e: Exception) {
        console.error("Error during initialization: ${e.message}")
        document.body?.innerHTML = """
            <div style="padding: 20px; color: red;">
                <h1>Error</h1>
                <p>Failed to initialize application: ${e.message}</p>
            </div>
        """.trimIndent()
    }
}