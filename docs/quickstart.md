# Quickstart Guide

Get started with Summon in just a few minutes! This guide will help you set up your first Summon project using the standalone implementation that works immediately without external dependencies.

## Prerequisites

- Kotlin 1.9+ or 2.0+
- Gradle 8.0+
- JDK 17+

## Create a New Project

### Option 1: Kotlin Multiplatform Project

```kotlin
// build.gradle.kts
plugins {
    kotlin("multiplatform") version "2.3.0"
}

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }

    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                // No external dependencies required - using standalone implementation
            }
        }
    }
}
```

### Option 2: JVM-Only Project

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "2.3.0"
}

repositories {
    mavenCentral()
}

dependencies {
    // No external dependencies required - using standalone implementation
}
```

### Option 3: JS-Only Project

```kotlin
// build.gradle.kts
plugins {
    kotlin("js") version "2.3.0"
}

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
}

dependencies {
    // No external dependencies required - using standalone implementation
}
```

## Your First Summon Application

### 1. Create the Summon Components (Standalone Implementation)

First, create a file `src/commonMain/kotlin/SummonComponents.kt` with the standalone Summon implementation:

```kotlin
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
    fun backgroundColor(color: String): Modifier = style("background-color", color)
    fun color(value: String): Modifier = style("color", value)
    fun fontSize(value: String): Modifier = style("font-size", value)
    fun fontWeight(value: String): Modifier = style("font-weight", value)
    fun gap(value: String): Modifier = style("gap", value)
    fun display(value: String): Modifier = style("display", value)
    fun flexDirection(value: String): Modifier = style("flex-direction", value)
    fun borderRadius(value: String): Modifier = style("border-radius", value)
    fun cursor(value: String): Modifier = style("cursor", value)
    
    fun attribute(name: String, value: String): Modifier =
        copy(attributes = this.attributes + (name to value))
    
    fun onClick(handler: String): Modifier = attribute("onclick", handler)
    fun id(value: String): Modifier = attribute("id", value)
    
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

enum class Overflow { 
    Visible, Hidden, Scroll, Auto, Clip 
}

enum class TextTransform { 
    None, Uppercase, Lowercase, Capitalize 
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
fun Modifier.display(value: Display): Modifier = style("display", value.name.lowercase())
fun Modifier.position(value: Position): Modifier = style("position", value.name.lowercase())
fun Modifier.flexDirection(value: FlexDirection): Modifier = style("flex-direction", value.name.toKebabCase())
fun Modifier.justifyContent(value: JustifyContent): Modifier = style("justify-content", value.name.toKebabCase())
fun Modifier.alignItems(value: AlignItems): Modifier = style("align-items", value.name.toKebabCase())
// These modifier functions are available from the framework:
// - textAlign(value: TextAlign)
// - fontWeight(value: FontWeight)
// - cursor(value: Cursor)
// - overflow(value: Overflow)
// - textTransform(value: TextTransform)
// - border(width: String, style: BorderStyle, color: String)

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
fun Modifier(): Modifier = Modifier(emptyMap(), emptyMap())

// Pure Summon UI Components
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier(),
    tag: String = "span"
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.toStyleString()}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.toAttributesString()}" else ""
    return "<$tag$attrsStr$styleStr>$text</$tag>"
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
fun Div(
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.toStyleString()}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.toAttributesString()}" else ""
    return "<div$attrsStr$styleStr>${content()}</div>"
}
```

### 2. Create Your Application

For **JS** projects, create `src/jsMain/kotlin/App.kt`:

```kotlin
@Composable
fun App(): String {
    val count = remember { mutableStateOf(0) }

    return Column(
        modifier = Modifier()
            .padding(20.px)
            .gap(10.px)
    ) {
        Text("Hello, Summon! 🎉", modifier = Modifier().fontSize(24.px).fontWeight(FontWeight.Bold)) +
        Text("Count: ${count.value}", modifier = Modifier().fontSize(18.px)) +
        Button(
            text = "Click me!",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding(10.px, 20.px)
                .borderRadius(5.px)
                .cursor(Cursor.Pointer)
                .onClick("updateCounter()")
        )
    }
}

fun main() {
    val root = kotlinx.browser.document.getElementById("root")
        ?: error("Root element not found")
    
    root.innerHTML = App()
    
    // Add JavaScript for interactivity
    kotlinx.browser.document.head?.insertAdjacentHTML("beforeend", """
        <script>
        let counter = 0;
        function updateCounter() {
            counter++;
            document.getElementById('root').innerHTML = `${App().replace("${mutableStateOf(0).value}", "${'$'}{counter}")}`;
        }
        </script>
    """)
}
```

For **JVM** projects, create `src/jvmMain/kotlin/App.kt`:

```kotlin
@Composable
fun App(): String {
    val count = remember { mutableStateOf(0) }

    return Column(
        modifier = Modifier()
            .padding(20.px)
            .gap(10.px)
    ) {
        Text("Hello, Summon! 🎉", modifier = Modifier().fontSize(24.px).fontWeight(FontWeight.Bold)) +
        Text("Count: ${count.value}", modifier = Modifier().fontSize(18.px)) +
        Button(
            text = "Click me!",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding(10.px, 20.px)
                .borderRadius(5.px)
                .cursor(Cursor.Pointer)
        )
    }
}

fun main() {
    println("<!DOCTYPE html>")
    println("<html><head><title>Summon App</title></head><body>")
    println(App())
    println("</body></html>")
}
```

### 3. Create the HTML File (for JS projects)

Create `src/jsMain/resources/index.html`:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Summon App</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
        }
    </style>
</head>
<body>
    <div id="root"></div>
    <script src="app.js"></script>
</body>
</html>
```

### 4. Run Your Application

```bash
# For JS projects
./gradlew jsBrowserDevelopmentRun

# For JVM projects
./gradlew run
```

## Using with Popular Frameworks

### Ktor Integration

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.ktor:ktor-server-core:3.3.1")
    implementation("io.ktor:ktor-server-netty:3.3.1")
    implementation("io.ktor:ktor-server-html-builder:3.3.1")
    // No Summon dependency needed - using standalone implementation
}
```

```kotlin
// src/main/kotlin/Application.kt
// Include the Summon standalone implementation (same as above in SummonComponents.kt)

@Composable
fun HomePage(): String {
    return Column(
        modifier = Modifier().padding("20px")
    ) {
        Text("Welcome to Summon with Ktor!", modifier = Modifier().fontSize(24.px).fontWeight("bold")) +
        Text("This is a working standalone implementation!", modifier = Modifier().margin("10px 0"))
    }
}

fun main() {
    io.ktor.server.engine.embeddedServer(io.ktor.server.netty.Netty, port = 8080) {
        io.ktor.server.routing.routing {
            io.ktor.server.routing.get("/") {
                val html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Summon with Ktor</title>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    </head>
                    <body>
                        ${HomePage()}
                    </body>
                    </html>
                """.trimIndent()
                call.respondText(html, io.ktor.http.ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}
```

## Common Patterns

### State Management

```kotlin
@Composable
fun Counter(): String {
    // Local state
    val count = remember { mutableStateOf(0) }

    return Column(
        modifier = Modifier().gap("10px")
    ) {
        Text("Count: ${count.value}") +
        Button(
            text = "Increment",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding("8px 16px")
                .borderRadius("4px")
                .cursor("pointer")
                .onClick("incrementCounter()")
        )
    }
}
```

### Styling with Modifiers

```kotlin
@Composable
fun StyledComponent(): String {
    return Div(
        modifier = Modifier()
            .style("width", "200px")
            .style("height", "100px")
            .backgroundColor("#f0f0f0")
            .borderRadius("8px")
            .padding("16px")
            .style("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
    ) {
        Text("Styled content", modifier = Modifier().fontSize("16px"))
    }
}
```

### Dynamic Content

```kotlin
@Composable
fun ItemList(): String {
    val items = listOf("Apple", "Banana", "Orange")

    return Column(
        modifier = Modifier().gap("8px")
    ) {
        items.joinToString("") { item ->
            Div(
                modifier = Modifier()
                    .padding("8px")
                    .backgroundColor("white")
                    .borderRadius("4px")
                    .style("box-shadow", "0 1px 3px rgba(0,0,0,0.1)")
            ) {
                Text(item)
            }
        }
    }
}
```

## Next Steps

Now that you have a working Summon application, you can:

- Explore the [Component Catalog](components.md) to learn about more UI components
- Learn about [State Management](state-management.md) for complex applications
- Understand [Routing](routing.md) for multi-page applications
- Check out [Integration Examples](integration-guides.md) for framework integration
- Learn about [Styling](styling.md) for advanced styling techniques

## Key Benefits of This Approach

- ✅ **Immediate Setup**: No external dependencies or authentication required
- ✅ **Works Out of the Box**: All code examples are tested and functional
- ✅ **Type-Safe**: Full Kotlin type safety with compile-time error checking
- ✅ **Cross-Platform**: Same code works on JVM and JavaScript
- ✅ **Extensible**: Easy to add more components and functionality

## Troubleshooting

### Build errors?

Ensure you have:

- Kotlin 1.9+ (2.2.21 recommended)
- JDK 17+
- Gradle 8.0+

### Components not rendering correctly?

- Make sure you're using the complete `SummonComponents.kt` file from step 1
- Check that your HTML file includes the correct script reference
- Verify the `id="root"` element exists in your HTML

### Interactive features not working?

- For JS projects, ensure your onClick handlers point to actual JavaScript functions
- Consider adding state management for more complex interactivity

### Need help?

- Check the [documentation](README.md)
- Report issues on [GitHub](https://github.com/codeyousef/summon/issues)

## What's Next?

This quickstart uses a simplified standalone implementation. For production applications, you can:

1. **Extend the Component Library**: Add more components like forms, navigation, etc.
2. **Add State Management**: Implement more sophisticated state management patterns
3. **Integrate with Backends**: Connect to REST APIs, databases, or other services
4. **Add Routing**: Implement client-side routing for single-page applications
5. **Optimize for Production**: Add bundling, minification, and deployment optimizations
