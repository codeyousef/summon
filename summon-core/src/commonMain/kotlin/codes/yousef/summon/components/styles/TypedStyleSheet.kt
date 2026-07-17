package codes.yousef.summon.components.styles

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.AnimationDirection
import codes.yousef.summon.modifier.AnimationDuration
import codes.yousef.summon.modifier.AnimationFillMode
import codes.yousef.summon.modifier.MediaQuery
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.style
import kotlin.jvm.JvmInline

/**
 * HTML elements that can be targeted by a [TypedStyleSheet].
 */
enum class StyleElement(internal val cssName: String) {
    Html("html"),
    Body("body"),
    Main("main"),
    Header("header"),
    Footer("footer"),
    Nav("nav"),
    Section("section"),
    Article("article"),
    Aside("aside"),
    Div("div"),
    Span("span"),
    Paragraph("p"),
    Heading1("h1"),
    Heading2("h2"),
    Heading3("h3"),
    Heading4("h4"),
    Heading5("h5"),
    Heading6("h6"),
    Anchor("a"),
    Button("button"),
    Form("form"),
    Label("label"),
    Input("input"),
    TextArea("textarea"),
    Select("select"),
    Option("option"),
    Details("details"),
    Summary("summary"),
    List("ul"),
    OrderedList("ol"),
    ListItem("li"),
    Image("img"),
    Video("video"),
    Audio("audio"),
    Canvas("canvas"),
    Iframe("iframe"),
    Preformatted("pre"),
    Code("code"),
    Table("table"),
    TableHead("thead"),
    TableBody("tbody"),
    TableRow("tr"),
    TableHeader("th"),
    TableCell("td")
}

/**
 * Attribute names supported by typed selectors.
 *
 * Use [data] and [aria] for application-specific data and accessibility attributes.
 */
class StyleAttribute private constructor(internal val cssName: String) {
    companion object {
        val Open = StyleAttribute("open")
        val Disabled = StyleAttribute("disabled")
        val Checked = StyleAttribute("checked")
        val Selected = StyleAttribute("selected")
        val Hidden = StyleAttribute("hidden")
        val Type = StyleAttribute("type")
        val Style = StyleAttribute("style")
        val Role = StyleAttribute("role")
        val Direction = StyleAttribute("dir")
        val Language = StyleAttribute("lang")
        val AriaCurrent = aria("current")
        val AriaExpanded = aria("expanded")

        fun data(name: String): StyleAttribute = StyleAttribute("data-${validatedName(name)}")

        fun aria(name: String): StyleAttribute = StyleAttribute("aria-${validatedName(name)}")

        private fun validatedName(name: String): String {
            require(name.matches(Regex("[a-zA-Z_][a-zA-Z0-9_-]*"))) {
                "Attribute names must contain only letters, digits, underscores, and hyphens"
            }
            return name
        }
    }
}

enum class AttributeMatch(internal val operator: String) {
    Equals("="),
    IncludesWord("~="),
    StartsWith("^="),
    EndsWith("$="),
    Contains("*=")
}

sealed class StylePseudoClass(internal val css: String) {
    data object Hover : StylePseudoClass("hover")
    data object Focus : StylePseudoClass("focus")
    data object FocusVisible : StylePseudoClass("focus-visible")
    data object FocusWithin : StylePseudoClass("focus-within")
    data object Active : StylePseudoClass("active")
    data object Visited : StylePseudoClass("visited")
    data object Disabled : StylePseudoClass("disabled")
    data object Checked : StylePseudoClass("checked")
    data object FirstChild : StylePseudoClass("first-child")
    data object LastChild : StylePseudoClass("last-child")
    data object OnlyChild : StylePseudoClass("only-child")
    data object Empty : StylePseudoClass("empty")

    class NthChild private constructor(private val expression: String) :
        StylePseudoClass("nth-child($expression)") {
        companion object {
            val Odd = NthChild("odd")
            val Even = NthChild("even")

            fun index(index: Int): NthChild {
                require(index > 0) { "nth-child index must be greater than zero" }
                return NthChild(index.toString())
            }

            fun sequence(step: Int, offset: Int = 0): NthChild {
                require(step != 0) { "nth-child sequence step must not be zero" }
                val suffix = when {
                    offset > 0 -> "+$offset"
                    offset < 0 -> offset.toString()
                    else -> ""
                }
                return NthChild("${step}n$suffix")
            }
        }
    }
}

enum class StylePseudoElement(internal val css: String) {
    Before("before"),
    After("after"),
    Placeholder("placeholder"),
    Selection("selection"),
    Marker("marker"),
    FileSelectorButton("file-selector-button"),
    WebkitDetailsMarker("-webkit-details-marker"),
    WebkitScrollbar("-webkit-scrollbar"),
    WebkitScrollbarTrack("-webkit-scrollbar-track"),
    WebkitScrollbarThumb("-webkit-scrollbar-thumb"),
    WebkitSearchCancelButton("-webkit-search-cancel-button"),
    WebkitSliderThumb("-webkit-slider-thumb")
}

/**
 * A structured selector for [TypedStyleSheet]. It cannot contain arbitrary selector syntax.
 */
sealed class StyleSelector {
    internal abstract fun render(): String

    data object Universal : StyleSelector() {
        override fun render() = "*"
    }

    data object Root : StyleSelector() {
        override fun render() = ":root"
    }

    private data class ElementSelector(val element: StyleElement) : StyleSelector() {
        override fun render() = element.cssName
    }

    private data class ClassSelector(val name: String) : StyleSelector() {
        override fun render() = ".$name"
    }

    private data class IdSelector(val name: String) : StyleSelector() {
        override fun render() = "#$name"
    }

    private data class WithClass(val base: StyleSelector, val name: String) : StyleSelector() {
        override fun render() = "${base.render()}.$name"
    }

    private data class WithAttribute(
        val base: StyleSelector,
        val attribute: StyleAttribute,
        val value: String?,
        val match: AttributeMatch,
        val caseInsensitive: Boolean
    ) : StyleSelector() {
        override fun render(): String {
            val attributeSelector = if (value == null) {
                "[${attribute.cssName}]"
            } else {
                val flag = if (caseInsensitive) " i" else ""
                "[${attribute.cssName}${match.operator}\"${escapeCssString(value)}\"$flag]"
            }
            return base.render() + attributeSelector
        }
    }

    private data class WithPseudoClass(
        val base: StyleSelector,
        val pseudoClass: StylePseudoClass
    ) : StyleSelector() {
        override fun render() = "${base.render()}:${pseudoClass.css}"
    }

    private data class WithPseudoElement(
        val base: StyleSelector,
        val pseudoElement: StylePseudoElement
    ) : StyleSelector() {
        override fun render() = "${base.render()}::${pseudoElement.css}"
    }

    private data class Negated(val base: StyleSelector, val excluded: StyleSelector) : StyleSelector() {
        override fun render() = "${base.render()}:not(${excluded.render()})"
    }

    private data class Related(
        val parent: StyleSelector,
        val combinator: String,
        val target: StyleSelector
    ) : StyleSelector() {
        override fun render() = "${parent.render()}$combinator${target.render()}"
    }

    private data class Group(val selectors: List<StyleSelector>) : StyleSelector() {
        override fun render() = selectors.joinToString(", ") { it.render() }
    }

    fun withClass(name: String): StyleSelector = WithClass(this, validateIdentifier(name))

    fun attribute(
        attribute: StyleAttribute,
        value: String? = null,
        match: AttributeMatch = AttributeMatch.Equals,
        caseInsensitive: Boolean = false
    ): StyleSelector = WithAttribute(this, attribute, value, match, caseInsensitive)

    fun pseudoClass(pseudoClass: StylePseudoClass): StyleSelector =
        WithPseudoClass(this, pseudoClass)

    fun pseudoElement(pseudoElement: StylePseudoElement): StyleSelector =
        WithPseudoElement(this, pseudoElement)

    fun not(excluded: StyleSelector): StyleSelector = Negated(this, excluded)

    fun descendant(target: StyleSelector): StyleSelector = Related(this, " ", target)

    fun child(target: StyleSelector): StyleSelector = Related(this, " > ", target)

    fun adjacentSibling(target: StyleSelector): StyleSelector = Related(this, " + ", target)

    fun generalSibling(target: StyleSelector): StyleSelector = Related(this, " ~ ", target)

    fun or(other: StyleSelector, vararg additional: StyleSelector): StyleSelector =
        Group(listOf(this, other) + additional)

    companion object {
        fun element(element: StyleElement): StyleSelector = ElementSelector(element)

        fun className(name: String): StyleSelector = ClassSelector(validateIdentifier(name))

        fun id(name: String): StyleSelector = IdSelector(validateIdentifier(name))

        fun all(first: StyleSelector, second: StyleSelector, vararg additional: StyleSelector): StyleSelector =
            Group(listOf(first, second) + additional)

        fun all(selectors: Iterable<StyleSelector>): StyleSelector {
            val values = selectors.toList()
            require(values.size >= 2) { "A selector group requires at least two selectors" }
            return Group(values)
        }

        private fun validateIdentifier(value: String): String {
            require(value.matches(Regex("-?[_a-zA-Z][_a-zA-Z0-9-]*"))) {
                "Style identifiers must be valid CSS identifiers without selector syntax"
            }
            return value
        }

        private fun escapeCssString(value: String): String = buildString(value.length) {
            value.forEach { character ->
                when (character) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\n' -> append("\\a ")
                    '\r' -> append("\\d ")
                    '\u000c' -> append("\\c ")
                    else -> append(character)
                }
            }
        }
    }
}

@JvmInline
value class AnimationName private constructor(internal val cssName: String) {
    companion object {
        fun named(name: String): AnimationName {
            require(name.matches(Regex("-?[_a-zA-Z][_a-zA-Z0-9-]*"))) {
                "Animation names must be valid identifiers"
            }
            return AnimationName(name)
        }
    }
}

private sealed interface TypedStyleEntry {
    fun render(): String
}

private data class TypedRule(
    val selector: StyleSelector,
    val declarations: Modifier,
    val priority: StyleRulePriority
) : TypedStyleEntry {
    override fun render(): String = "${selector.render()} { ${declarations.renderDeclarations(priority)} }"
}

private data class TypedMediaBlock(
    val query: MediaQuery,
    val entries: List<TypedStyleEntry>
) : TypedStyleEntry {
    override fun render(): String =
        "@media $query { ${entries.joinToString(" ") { it.render() }} }"
}

private data class TypedKeyframes(
    val name: AnimationName,
    val frames: List<Keyframe>
) : TypedStyleEntry {
    override fun render(): String =
        "@keyframes ${name.cssName} { ${frames.joinToString(" ") { it.render() }} }"
}

internal data class Keyframe(val position: String, val declarations: Modifier) {
    fun render(): String = "$position { ${declarations.renderDeclarations()} }"
}

class TypedKeyframesScope internal constructor() {
    private val frames = mutableListOf<Keyframe>()

    fun from(modifier: Modifier) {
        frame(0, modifier)
    }

    fun to(modifier: Modifier) {
        frame(100, modifier)
    }

    fun frame(percent: Int, modifier: Modifier) {
        require(percent in 0..100) { "Keyframe position must be between 0 and 100" }
        if (modifier.styles.isNotEmpty()) frames += Keyframe("$percent%", modifier)
    }

    internal fun build(): List<Keyframe> = frames.toList()
}

class TypedStyleSheetScope internal constructor() {
    private val entries = mutableListOf<TypedStyleEntry>()

    fun rule(
        selector: StyleSelector,
        modifier: Modifier,
        priority: StyleRulePriority = StyleRulePriority.Normal
    ) {
        if (modifier.styles.isNotEmpty()) entries += TypedRule(selector, modifier, priority)
    }

    fun media(query: MediaQuery, block: TypedStyleSheetScope.() -> Unit) {
        require(query !is MediaQuery.Custom) {
            "TypedStyleSheet does not accept raw custom media-query strings"
        }
        val nested = TypedStyleSheetScope().apply(block).entries
        if (nested.isNotEmpty()) entries += TypedMediaBlock(query, nested.toList())
    }

    fun keyframes(name: AnimationName, block: TypedKeyframesScope.() -> Unit) {
        val frames = TypedKeyframesScope().apply(block).build()
        if (frames.isNotEmpty()) entries += TypedKeyframes(name, frames)
    }

    internal fun render(): String = entries.joinToString("\n") { it.render() }
}

/** Controls whether a typed global rule is allowed to override inline component defaults. */
enum class StyleRulePriority {
    Normal,
    Important
}

/**
 * Emits global rules from structured selectors and typed [Modifier] declarations.
 * Application code never needs to author CSS syntax.
 */
@Composable
fun TypedStyleSheet(block: TypedStyleSheetScope.() -> Unit) {
    val css = TypedStyleSheetScope().apply(block).render()
    if (css.isNotEmpty()) GlobalStyle(css)
}

fun Modifier.animation(
    name: AnimationName,
    duration: AnimationDuration = AnimationDuration.Medium,
    easing: AnimationEasing = AnimationEasing.Ease,
    delay: AnimationDuration = AnimationDuration.Instant,
    iterationCount: AnimationIterationCount = AnimationIterationCount.Once,
    direction: AnimationDirection = AnimationDirection.Normal,
    fillMode: AnimationFillMode = AnimationFillMode.None
): Modifier = style(
    "animation",
    "${name.cssName} $duration ${easing.css} $delay ${iterationCount.css} $direction $fillMode"
)

enum class AnimationEasing(internal val css: String) {
    Linear("linear"),
    Ease("ease"),
    EaseIn("ease-in"),
    EaseOut("ease-out"),
    EaseInOut("ease-in-out"),
    StepStart("step-start"),
    StepEnd("step-end")
}

sealed class AnimationIterationCount(internal val css: String) {
    data object Once : AnimationIterationCount("1")
    data object Infinite : AnimationIterationCount("infinite")

    class Times(count: Int) : AnimationIterationCount(count.also {
        require(it > 0) { "Animation iteration count must be greater than zero" }
    }.toString())
}

private fun Modifier.renderDeclarations(
    priority: StyleRulePriority = StyleRulePriority.Normal
): String = styles.entries.joinToString("; ", postfix = ";") { (property, value) ->
    val importance = if (priority == StyleRulePriority.Important) " !important" else ""
    "${property.toKebabCase()}: ${value.normalizedCssNumber()}$importance"
}

private fun String.normalizedCssNumber(): String =
    if (matches(Regex("-?[0-9]+\\.0"))) dropLast(2) else this

private fun String.toKebabCase(): String =
    if ('-' in this) this else replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()
