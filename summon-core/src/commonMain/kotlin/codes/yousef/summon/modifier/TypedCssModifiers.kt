package codes.yousef.summon.modifier

import codes.yousef.summon.components.ScrollableComponent
import codes.yousef.summon.core.style.Color
import codes.yousef.summon.extensions.px
import codes.yousef.summon.i18n.LayoutDirection

/** Controls how words may be broken when text would otherwise overflow. */
enum class WordBreak(val value: String) {
    Normal("normal"),
    BreakAll("break-all"),
    KeepAll("keep-all"),
    BreakWord("break-word");

    override fun toString(): String = value
}

/** Controls whether otherwise unbreakable text may wrap. */
enum class OverflowWrap(val value: String) {
    Normal("normal"),
    BreakWord("break-word"),
    Anywhere("anywhere");

    override fun toString(): String = value
}

/** Controls how clipped inline content is represented. */
enum class TextOverflow(val value: String) {
    Clip("clip"),
    Ellipsis("ellipsis");

    override fun toString(): String = value
}

/** Selects the box model used to calculate an element's dimensions. */
enum class BoxSizing(val value: String) {
    ContentBox("content-box"),
    BorderBox("border-box");

    override fun toString(): String = value
}

/** Declares the color schemes in which an element can be comfortably rendered. */
enum class ColorScheme(val value: String) {
    Normal("normal"),
    Light("light"),
    Dark("dark"),
    LightDark("light dark"),
    DarkLight("dark light"),
    OnlyLight("only light"),
    OnlyDark("only dark");

    override fun toString(): String = value
}

/** Common marker styles accepted by the CSS list-style-type property. */
enum class ListStyleType(val value: String) {
    None("none"),
    Disc("disc"),
    Circle("circle"),
    Square("square"),
    Decimal("decimal"),
    DecimalLeadingZero("decimal-leading-zero"),
    LowerAlpha("lower-alpha"),
    UpperAlpha("upper-alpha"),
    LowerRoman("lower-roman"),
    UpperRoman("upper-roman"),
    DisclosureOpen("disclosure-open"),
    DisclosureClosed("disclosure-closed");

    override fun toString(): String = value
}

/** Standard values accepted by the CSS scrollbar-width property. */
enum class ScrollbarWidth(val value: String) {
    Auto("auto"),
    Thin("thin"),
    None("none");

    override fun toString(): String = value
}

/** Standard line styles accepted by CSS outlines. */
enum class OutlineStyle(val value: String) {
    Auto("auto"),
    None("none"),
    Dotted("dotted"),
    Dashed("dashed"),
    Solid("solid"),
    Double("double"),
    Groove("groove"),
    Ridge("ridge"),
    Inset("inset"),
    Outset("outset");

    override fun toString(): String = value
}

/** Applies the inline text/layout direction represented by the framework i18n model. */
fun Modifier.direction(value: LayoutDirection): Modifier =
    style("direction", value.name.lowercase())

/**
 * A structured CSS text shadow declaration.
 *
 * Use [pixels] when all offsets are pixel values, or construct a value directly
 * when CSS units such as `rem`, `em`, or `calc(...)` are required.
 */
data class TextShadow(
    val offsetX: String,
    val offsetY: String,
    val blurRadius: String = "0",
    val color: String? = null,
) {
    init {
        require(offsetX.isNotBlank()) { "TextShadow offsetX must not be blank" }
        require(offsetY.isNotBlank()) { "TextShadow offsetY must not be blank" }
        require(blurRadius.isNotBlank()) { "TextShadow blurRadius must not be blank" }
        require(color == null || color.isNotBlank()) { "TextShadow color must not be blank" }
    }

    internal fun toCssValue(): String = buildList {
        add(offsetX)
        add(offsetY)
        add(blurRadius)
        color?.let(::add)
    }.joinToString(" ")

    companion object {
        /** Creates a text shadow whose dimensions are expressed in pixels. */
        fun pixels(
            offsetX: Number,
            offsetY: Number,
            blurRadius: Number = 0,
            color: Color? = null,
        ): TextShadow = TextShadow(
            offsetX = offsetX.px,
            offsetY = offsetY.px,
            blurRadius = blurRadius.px,
            color = color?.toCssString(),
        )
    }
}

/** Applies a custom text-shadow declaration. */
fun Modifier.textShadow(value: String): Modifier = style("text-shadow", value)

/** Applies one or more structured text shadows. */
fun Modifier.textShadow(vararg shadows: TextShadow): Modifier {
    require(shadows.isNotEmpty()) { "textShadow requires at least one shadow" }
    return style("text-shadow", shadows.joinToString(", ") { it.toCssValue() })
}

/** Sets the distance between text and its decoration line. */
fun Modifier.textUnderlineOffset(value: String): Modifier = style("text-underline-offset", value)

/** Sets the text-decoration offset in pixels. */
fun Modifier.textUnderlineOffset(value: Number): Modifier = textUnderlineOffset(value.px)

/** Sets word-break using a type-safe CSS value. */
fun Modifier.wordBreak(value: WordBreak): Modifier = style("word-break", value.value)

/** Sets a custom word-break value. */
fun Modifier.wordBreak(value: String): Modifier = style("word-break", value)

/** Sets overflow-wrap using a type-safe CSS value. */
fun Modifier.overflowWrap(value: OverflowWrap): Modifier = style("overflow-wrap", value.value)

/** Sets a custom overflow-wrap value. */
fun Modifier.overflowWrap(value: String): Modifier = style("overflow-wrap", value)

/** Sets text-overflow using a type-safe CSS value. */
fun Modifier.textOverflow(value: TextOverflow): Modifier = style("text-overflow", value.value)

/** Sets a custom text-overflow value. */
fun Modifier.textOverflow(value: String): Modifier = style("text-overflow", value)

/**
 * Limits content to [lines] lines.
 *
 * The WebKit declaration is emitted by default for compatibility. This modifier
 * intentionally does not change display or overflow so callers retain layout control.
 */
fun Modifier.lineClamp(lines: Int, includeWebkitFallback: Boolean = true): Modifier {
    require(lines > 0) { "lineClamp lines must be greater than zero" }
    var result = style("line-clamp", lines.toString())
    if (includeWebkitFallback) {
        result = result.style("-webkit-line-clamp", lines.toString())
    }
    return result
}

/** Sets box-sizing using a type-safe CSS value. */
fun Modifier.boxSizing(value: BoxSizing): Modifier = style("box-sizing", value.value)

/** Sets a custom box-sizing value. */
fun Modifier.boxSizing(value: String): Modifier = style("box-sizing", value)

/** Sets color-scheme using a type-safe standard value. */
fun Modifier.colorScheme(value: ColorScheme): Modifier = style("color-scheme", value.value)

/** Sets a custom color-scheme declaration. */
fun Modifier.colorScheme(value: String): Modifier = style("color-scheme", value)

/** Applies an arbitrary CSS list-style shorthand. */
fun Modifier.listStyle(value: String): Modifier = style("list-style", value)

/** Applies a marker type through the CSS list-style shorthand. */
fun Modifier.listStyle(type: ListStyleType): Modifier = listStyle(type.value)

/** Sets list-style-type using a type-safe standard marker. */
fun Modifier.listStyleType(value: ListStyleType): Modifier = style("list-style-type", value.value)

/** Sets a custom list-style-type declaration, including custom counter styles. */
fun Modifier.listStyleType(value: String): Modifier = style("list-style-type", value)

/** Applies a pixel-valued CSS inset shorthand. */
fun Modifier.inset(value: Number): Modifier = inset(value.px)

/** Applies pixel-valued vertical and horizontal insets. */
fun Modifier.inset(vertical: Number, horizontal: Number): Modifier =
    inset(vertical.px, horizontal.px)

/** Applies four explicit pixel-valued insets. */
fun Modifier.inset(top: Number, right: Number, bottom: Number, left: Number): Modifier =
    inset(top.px, right.px, bottom.px, left.px)

/** Sets the logical inline-start inset. */
fun Modifier.insetInlineStart(value: String): Modifier = style("inset-inline-start", value)

/** Sets the logical inline-start inset in pixels. */
fun Modifier.insetInlineStart(value: Number): Modifier = insetInlineStart(value.px)

/** Sets the logical inline-end inset. */
fun Modifier.insetInlineEnd(value: String): Modifier = style("inset-inline-end", value)

/** Sets the logical inline-end inset in pixels. */
fun Modifier.insetInlineEnd(value: Number): Modifier = insetInlineEnd(value.px)

/** Sets the scrollbar thumb and track colors. */
fun Modifier.scrollbarColor(
    thumb: String,
    track: String,
    component: ScrollableComponent? = null,
): Modifier = style("scrollbar-color", "$thumb $track")

/** Sets the scrollbar thumb and track colors using typed colors. */
fun Modifier.scrollbarColor(
    thumb: Color,
    track: Color,
    component: ScrollableComponent? = null,
): Modifier = scrollbarColor(thumb.toCssString(), track.toCssString(), component)

/** Sets scrollbar width using a type-safe CSS value. */
fun Modifier.scrollbarWidth(
    value: ScrollbarWidth,
    component: ScrollableComponent? = null,
): Modifier = scrollbarWidth(value.value, component)

/** Applies an arbitrary CSS outline shorthand. */
fun Modifier.outline(value: String): Modifier = style("outline", value)

/** Applies a structured outline declaration. */
fun Modifier.outline(width: String, style: OutlineStyle, color: String): Modifier =
    outline("$width ${style.value} $color")

/** Applies a structured outline with a pixel width. */
fun Modifier.outline(width: Number, style: OutlineStyle, color: String): Modifier =
    outline(width.px, style, color)

/** Applies a structured outline using a typed color. */
fun Modifier.outline(width: Number, style: OutlineStyle, color: Color): Modifier =
    outline(width.px, style, color.toCssString())

/** Sets the distance between an outline and the element's border edge. */
fun Modifier.outlineOffset(value: String): Modifier = style("outline-offset", value)

/** Sets outline offset in pixels. */
fun Modifier.outlineOffset(value: Number): Modifier = outlineOffset(value.px)

/**
 * A composable CSS Grid track value.
 *
 * Prefer the factory helpers below to raw strings when building repeated or
 * min/max track lists.
 */
data class GridTrack(val value: String) {
    init {
        require(value.isNotBlank()) { "GridTrack value must not be blank" }
    }
}

/** Wraps a CSS length, keyword, or named line as a grid track. */
fun gridTrack(value: String): GridTrack = GridTrack(value)

/** Creates an `fr` grid track. */
fun gridFraction(value: Number = 1): GridTrack {
    val numericValue = value.toDouble()
    require(numericValue.isFinite() && numericValue >= 0.0) {
        "Grid fraction must be a finite, non-negative number"
    }
    return GridTrack("${value.toCssNumber()}fr")
}

/** Creates a `minmax(...)` grid track. */
fun gridMinMax(min: GridTrack, max: GridTrack): GridTrack =
    GridTrack("minmax(${min.value}, ${max.value})")

/** Creates a fixed-count `repeat(...)` grid track group. */
fun gridRepeat(count: Int, vararg tracks: GridTrack): GridTrack {
    require(count > 0) { "Grid repeat count must be greater than zero" }
    return gridRepeat(count.toString(), tracks)
}

/** Creates an auto-fit `repeat(...)` grid track group. */
fun gridAutoFit(vararg tracks: GridTrack): GridTrack = gridRepeat("auto-fit", tracks)

/** Creates an auto-fill `repeat(...)` grid track group. */
fun gridAutoFill(vararg tracks: GridTrack): GridTrack = gridRepeat("auto-fill", tracks)

private fun gridRepeat(repetition: String, tracks: Array<out GridTrack>): GridTrack {
    require(tracks.isNotEmpty()) { "Grid repeat requires at least one track" }
    return GridTrack("repeat($repetition, ${tracks.joinToString(" ") { it.value }})")
}

/** Applies a typed grid-template-columns track list. */
fun Modifier.gridTemplateColumns(vararg tracks: GridTrack): Modifier {
    require(tracks.isNotEmpty()) { "gridTemplateColumns requires at least one track" }
    return gridTemplateColumns(tracks.joinToString(" ") { it.value })
}

/** Applies a custom grid-template-rows declaration. */
fun Modifier.gridTemplateRows(value: String): Modifier = style("grid-template-rows", value)

/** Applies a typed grid-template-rows track list. */
fun Modifier.gridTemplateRows(vararg tracks: GridTrack): Modifier {
    require(tracks.isNotEmpty()) { "gridTemplateRows requires at least one track" }
    return gridTemplateRows(tracks.joinToString(" ") { it.value })
}

private fun Number.toCssNumber(): String {
    val number = toDouble()
    return if (number % 1.0 == 0.0) number.toLong().toString() else toString()
}
