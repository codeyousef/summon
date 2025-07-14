package code.yousef.example.quarkus

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Standalone Summon implementation - no external dependencies required
@Target(AnnotationTarget.FUNCTION)
annotation class StandaloneComposable

// Summon Modifier system for type-safe styling
data class StandaloneModifier(
    val styles: Map<String, String> = emptyMap(),
    val attributes: Map<String, String> = emptyMap()
) {
    fun style(propertyName: String, value: String): StandaloneModifier =
        copy(styles = this.styles + (propertyName to value))
        
    fun padding(value: String): StandaloneModifier = style("padding", value)
    fun padding(vertical: String, horizontal: String): StandaloneModifier = style("padding", "$vertical $horizontal")
    fun padding(top: String, right: String, bottom: String, left: String): StandaloneModifier = 
        style("padding", "$top $right $bottom $left")
    fun margin(value: String): StandaloneModifier = style("margin", value)
    fun marginTop(value: String): StandaloneModifier = style("margin-top", value)
    fun marginBottom(value: String): StandaloneModifier = style("margin-bottom", value)
    fun backgroundColor(color: String): StandaloneModifier = style("background-color", color)
    fun color(value: String): StandaloneModifier = style("color", value)
    fun fontSize(value: String): StandaloneModifier = style("font-size", value)
    fun fontWeight(value: String): StandaloneModifier = style("font-weight", value)
    fun gap(value: String): StandaloneModifier = style("gap", value)
    fun display(value: String): StandaloneModifier = style("display", value)
    fun flexDirection(value: String): StandaloneModifier = style("flex-direction", value)
    fun alignItems(value: String): StandaloneModifier = style("align-items", value)
    fun justifyContent(value: String): StandaloneModifier = style("justify-content", value)
    fun borderRadius(value: String): StandaloneModifier = style("border-radius", value)
    fun border(value: String): StandaloneModifier = style("border", value)
    fun borderLeft(value: String): StandaloneModifier = style("border-left", value)
    fun borderTop(value: String): StandaloneModifier = style("border-top", value)
    fun cursor(value: String): StandaloneModifier = style("cursor", value)
    fun textAlign(value: String): StandaloneModifier = style("text-align", value)
    fun minHeight(value: String): StandaloneModifier = style("min-height", value)
    fun width(value: String): StandaloneModifier = style("width", value)
    fun height(value: String): StandaloneModifier = style("height", value)
    fun flexWrap(value: String): StandaloneModifier = style("flex-wrap", value)
    fun boxShadow(value: String): StandaloneModifier = style("box-shadow", value)
    fun opacity(value: String): StandaloneModifier = style("opacity", value)
    fun position(value: String): StandaloneModifier = style("position", value)
    fun left(value: String): StandaloneModifier = style("left", value)
    fun top(value: String): StandaloneModifier = style("top", value)
    fun maxWidth(value: String): StandaloneModifier = style("max-width", value)
    
    fun attribute(name: String, value: String): StandaloneModifier =
        copy(attributes = this.attributes + (name to value))
    
    fun onClick(handler: String): StandaloneModifier = attribute("onclick", handler)
    fun id(value: String): StandaloneModifier = attribute("id", value)
    fun className(value: String): StandaloneModifier = attribute("class", value)
    fun href(value: String): StandaloneModifier = attribute("href", value)
    fun type(value: String): StandaloneModifier = attribute("type", value)
    fun name(value: String): StandaloneModifier = attribute("name", value)
    fun placeholder(value: String): StandaloneModifier = attribute("placeholder", value)
    fun required(value: String = "true"): StandaloneModifier = attribute("required", value)
    fun value(value: String): StandaloneModifier = attribute("value", value)
    fun method(value: String): StandaloneModifier = attribute("method", value)
    fun action(value: String): StandaloneModifier = attribute("action", value)
    
    // HTMX attributes
    fun hxGet(value: String): StandaloneModifier = attribute("hx-get", value)
    fun hxPost(value: String): StandaloneModifier = attribute("hx-post", value)
    fun hxPut(value: String): StandaloneModifier = attribute("hx-put", value)
    fun hxDelete(value: String): StandaloneModifier = attribute("hx-delete", value)
    fun hxTrigger(value: String): StandaloneModifier = attribute("hx-trigger", value)
    fun hxTarget(value: String): StandaloneModifier = attribute("hx-target", value)
    fun hxSwap(value: String): StandaloneModifier = attribute("hx-swap", value)
    
    fun toStyleString(): String =
        if (styles.isEmpty()) "" else styles.entries.joinToString(
            separator = "; ",
            postfix = ";"
        ) { "${it.key}: ${it.value}" }
        
    fun toAttributesString(): String =
        attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }
}

// Type-safe CSS enums
enum class StandaloneDisplay { 
    None, Block, Inline, InlineBlock, Flex, Grid, InlineFlex, InlineGrid 
}

enum class StandalonePosition { 
    Static, Relative, Absolute, Fixed, Sticky 
}

enum class StandaloneFlexDirection { 
    Row, Column, RowReverse, ColumnReverse 
}

enum class StandaloneJustifyContent { 
    FlexStart, FlexEnd, Center, SpaceBetween, SpaceAround, SpaceEvenly 
}

enum class StandaloneAlignItems { 
    FlexStart, FlexEnd, Center, Baseline, Stretch 
}

enum class StandaloneTextAlign { 
    Left, Right, Center, Justify, Start, End 
}

enum class StandaloneFontWeight { 
    Thin, ExtraLight, Light, Normal, Medium, SemiBold, Bold, ExtraBold, Black 
}

enum class StandaloneBorderStyle { 
    None, Solid, Dashed, Dotted, Double, Groove, Ridge, Inset, Outset 
}

enum class StandaloneCursor { 
    Auto, Default, Pointer, Wait, Text, Move, NotAllowed, Crosshair 
}

// Extension functions to convert enums to CSS values
fun String.toKebabCase(): String = this.fold(StringBuilder()) { acc, char ->
    when {
        char.isUpperCase() && acc.isNotEmpty() -> acc.append("-").append(char.lowercase())
        else -> acc.append(char.lowercase())
    }
}.toString()

fun StandaloneFontWeight.toCssValue(): String = when (this) {
    StandaloneFontWeight.Thin -> "100"
    StandaloneFontWeight.ExtraLight -> "200"
    StandaloneFontWeight.Light -> "300"
    StandaloneFontWeight.Normal -> "400"
    StandaloneFontWeight.Medium -> "500"
    StandaloneFontWeight.SemiBold -> "600"
    StandaloneFontWeight.Bold -> "700"
    StandaloneFontWeight.ExtraBold -> "800"
    StandaloneFontWeight.Black -> "900"
}

// Type-safe modifier extensions
fun StandaloneModifier.display(value: StandaloneDisplay): StandaloneModifier = style("display", value.name.lowercase())
fun StandaloneModifier.position(value: StandalonePosition): StandaloneModifier = style("position", value.name.lowercase())
fun StandaloneModifier.flexDirection(value: StandaloneFlexDirection): StandaloneModifier = style("flex-direction", value.name.toKebabCase())
fun StandaloneModifier.justifyContent(value: StandaloneJustifyContent): StandaloneModifier = style("justify-content", value.name.toKebabCase())
fun StandaloneModifier.alignItems(value: StandaloneAlignItems): StandaloneModifier = style("align-items", value.name.toKebabCase())
fun StandaloneModifier.textAlign(value: StandaloneTextAlign): StandaloneModifier = style("text-align", value.name.lowercase())
fun StandaloneModifier.fontWeight(value: StandaloneFontWeight): StandaloneModifier = style("font-weight", value.toCssValue())
fun StandaloneModifier.cursor(value: StandaloneCursor): StandaloneModifier = style("cursor", value.name.toKebabCase())
fun StandaloneModifier.border(width: String, style: StandaloneBorderStyle, color: String): StandaloneModifier = 
    style("border", "$width ${style.name.lowercase()} $color")

// CSS unit extensions
val Number.px: String get() = "${this}px"
val Number.rem: String get() = "${this}rem"
val Number.em: String get() = "${this}em"
val Number.percent: String get() = "${this}%"
val Number.vw: String get() = "${this}vw"
val Number.vh: String get() = "${this}vh"

// Utility to create empty modifier
fun StandaloneModifier(): StandaloneModifier = StandaloneModifier(emptyMap(), emptyMap())

// Pure Summon UI Components
@StandaloneComposable
fun StandaloneText(
    text: String,
    modifier: StandaloneModifier = StandaloneModifier()
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.toStyleString()}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.toAttributesString()}" else ""
    return "<span$attrsStr$styleStr>$text</span>"
}

@StandaloneComposable
fun StandaloneButton(
    label: String,
    modifier: StandaloneModifier = StandaloneModifier()
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.toStyleString()}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.toAttributesString()}" else ""
    return "<button$attrsStr$styleStr>$label</button>"
}

@StandaloneComposable
fun StandaloneColumn(
    modifier: StandaloneModifier = StandaloneModifier(),
    content: () -> String
): String {
    val columnModifier = modifier.display(StandaloneDisplay.Flex).flexDirection(StandaloneFlexDirection.Column)
    val styleStr = if (columnModifier.styles.isNotEmpty()) " style=\"${columnModifier.toStyleString()}\"" else ""
    val attrsStr = if (columnModifier.attributes.isNotEmpty()) " ${columnModifier.toAttributesString()}" else ""
    return "<div$attrsStr$styleStr>${content()}</div>"
}

@StandaloneComposable
fun StandaloneRow(
    modifier: StandaloneModifier = StandaloneModifier(),
    content: () -> String
): String {
    val rowModifier = modifier.display(StandaloneDisplay.Flex).flexDirection(StandaloneFlexDirection.Row).alignItems(StandaloneAlignItems.Center)
    val styleStr = if (rowModifier.styles.isNotEmpty()) " style=\"${rowModifier.toStyleString()}\"" else ""
    val attrsStr = if (rowModifier.attributes.isNotEmpty()) " ${rowModifier.toAttributesString()}" else ""
    return "<div$attrsStr$styleStr>${content()}</div>"
}

@StandaloneComposable
fun StandaloneBox(
    modifier: StandaloneModifier = StandaloneModifier(),
    content: () -> String
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.toStyleString()}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.toAttributesString()}" else ""
    return "<div$attrsStr$styleStr>${content()}</div>"
}

@StandaloneComposable
fun StandaloneCard(
    modifier: StandaloneModifier = StandaloneModifier(),
    content: () -> String
): String {
    val cardModifier = modifier
        .backgroundColor("white")
        .borderRadius(8.px)
        .boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
        .padding(16.px)
    return StandaloneBox(cardModifier) { content() }
}

// Standalone example components for demonstration

/**
 * Standalone demo component for testing
 */
@StandaloneComposable
fun StandaloneDemoComponent(): String {
    return StandaloneColumn(
        modifier = StandaloneModifier()
            .padding(20.px)
            .gap(16.px)
    ) {
        StandaloneText(
            text = "Standalone Summon Demo",
            modifier = StandaloneModifier()
                .fontSize(24.px)
                .fontWeight(StandaloneFontWeight.Bold)
                .color("#333333")
        ) +
        
        StandaloneCard(
            modifier = StandaloneModifier()
                .backgroundColor("#f8f9fa")
                .padding(16.px)
        ) {
            StandaloneColumn(
                modifier = StandaloneModifier().gap(8.px)
            ) {
                StandaloneText(
                    text = "This is a standalone implementation",
                    modifier = StandaloneModifier().fontWeight(StandaloneFontWeight.SemiBold)
                ) +
                StandaloneText("✓ No external dependencies") +
                StandaloneText("✓ Type-safe CSS styling") +
                StandaloneText("✓ Works immediately") +
                
                StandaloneButton(
                    label = "Test Button",
                    modifier = StandaloneModifier()
                        .backgroundColor("#0077cc")
                        .color("white")
                        .padding(8.px, 16.px)
                        .borderRadius(4.px)
                        .cursor(StandaloneCursor.Pointer)
                        .marginTop(12.px)
                )
            }
        }
    }
}

/**
 * Simple navigation demo
 */
@StandaloneComposable
fun StandaloneNavDemo(): String {
    return StandaloneRow(
        modifier = StandaloneModifier()
            .backgroundColor("#ffffff")
            .padding(16.px)
            .gap(12.px)
            .boxShadow("0 2px 4px rgba(0,0,0,0.1)")
    ) {
        StandaloneText(
            text = "Standalone Demo",
            modifier = StandaloneModifier()
                .fontSize(18.px)
                .fontWeight(StandaloneFontWeight.Bold)
        ) +
        
        StandaloneButton(
            label = "Home",
            modifier = StandaloneModifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding(6.px, 12.px)
                .borderRadius(4.px)
        ) +
        
        StandaloneButton(
            label = "About",
            modifier = StandaloneModifier()
                .backgroundColor("#6c757d")
                .color("white")
                .padding(6.px, 12.px)
                .borderRadius(4.px)
        )
    }
}

/**
 * Complete standalone page example
 */
@StandaloneComposable
fun StandaloneExamplePage(): String {
    return StandaloneColumn(
        modifier = StandaloneModifier()
            .minHeight("100vh")
            .backgroundColor("#f8f9fa")
    ) {
        StandaloneNavDemo() +
        
        StandaloneBox(
            modifier = StandaloneModifier()
                .padding(20.px)
        ) {
            StandaloneDemoComponent()
        } +
        
        // Footer
        StandaloneBox(
            modifier = StandaloneModifier()
                .backgroundColor("#ffffff")
                .padding(16.px)
                .textAlign(StandaloneTextAlign.Center)
                .marginTop("auto")
        ) {
            StandaloneText(
                text = "© ${LocalDateTime.now().year} Standalone Summon Example",
                modifier = StandaloneModifier().color("#666666")
            )
        }
    }
}