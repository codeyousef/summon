package codes.yousef.summon.components.styles

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.foundation.TrustedCss
import codes.yousef.summon.core.camelToKebabCase
import codes.yousef.summon.modifier.AnimationDirection
import codes.yousef.summon.modifier.AnimationDuration
import codes.yousef.summon.modifier.AnimationFillMode
import codes.yousef.summon.modifier.MediaQuery
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.style
import kotlin.jvm.JvmInline

/**
 * HTML elements that can be targeted by a [TypedStyleSheet].

 * @property cssName The css name value.
 */
enum class StyleElement(internal val cssName: String) {
    /** The HTML style element option. */
    Html("html"),
    /** The body style element option. */
    Body("body"),
    /** The main style element option. */
    Main("main"),
    /** The header style element option. */
    Header("header"),
    /** The footer style element option. */
    Footer("footer"),
    /** The nav style element option. */
    Nav("nav"),
    /** The section style element option. */
    Section("section"),
    /** The article style element option. */
    Article("article"),
    /** The aside style element option. */
    Aside("aside"),
    /** The div style element option. */
    Div("div"),
    /** The span style element option. */
    Span("span"),
    /** The paragraph style element option. */
    Paragraph("p"),
    /** The heading1 style element option. */
    Heading1("h1"),
    /** The heading2 style element option. */
    Heading2("h2"),
    /** The heading3 style element option. */
    Heading3("h3"),
    /** The heading4 style element option. */
    Heading4("h4"),
    /** The heading5 style element option. */
    Heading5("h5"),
    /** The heading6 style element option. */
    Heading6("h6"),
    /** The anchor style element option. */
    Anchor("a"),
    /** The button style element option. */
    Button("button"),
    /** The form style element option. */
    Form("form"),
    /** The label style element option. */
    Label("label"),
    /** The input style element option. */
    Input("input"),
    /** The text area style element option. */
    TextArea("textarea"),
    /** The select style element option. */
    Select("select"),
    /** The option style element option. */
    Option("option"),
    /** The details style element option. */
    Details("details"),
    /** The summary style element option. */
    Summary("summary"),
    /** The list style element option. */
    List("ul"),
    /** The ordered list style element option. */
    OrderedList("ol"),
    /** The list item style element option. */
    ListItem("li"),
    /** The image style element option. */
    Image("img"),
    /** The video style element option. */
    Video("video"),
    /** The audio style element option. */
    Audio("audio"),
    /** The canvas style element option. */
    Canvas("canvas"),
    /** The iframe style element option. */
    Iframe("iframe"),
    /** The preformatted style element option. */
    Preformatted("pre"),
    /** The code style element option. */
    Code("code"),
    /** The table style element option. */
    Table("table"),
    /** The table head style element option. */
    TableHead("thead"),
    /** The table body style element option. */
    TableBody("tbody"),
    /** The table row style element option. */
    TableRow("tr"),
    /** The table header style element option. */
    TableHeader("th"),
    /** The table cell style element option. */
    TableCell("td")
}

/**
 * Attribute names supported by typed selectors.
 *
 * Use [data] and ARIA for application-specific data and accessibility attributes.

 * @property cssName The css name value.
 */
class StyleAttribute private constructor(internal val cssName: String) {
    /** Standard and validated custom attribute names. */
    companion object {
        /** Native `open` state. */
        val Open = StyleAttribute("open")
        /** Native disabled state. */
        val Disabled = StyleAttribute("disabled")
        /** Native checked state. */
        val Checked = StyleAttribute("checked")
        /** Native selected state. */
        val Selected = StyleAttribute("selected")
        /** Native hidden state. */
        val Hidden = StyleAttribute("hidden")
        /** Native element type. */
        val Type = StyleAttribute("type")
        /** Inline style attribute. */
        val Style = StyleAttribute("style")
        /** ARIA role. */
        val Role = StyleAttribute("role")
        /** Text direction. */
        val Direction = StyleAttribute("dir")
        /** Content language. */
        val Language = StyleAttribute("lang")
        /** ARIA current-item state. */
        val AriaCurrent = aria("current")
        /** ARIA expanded state. */
        val AriaExpanded = aria("expanded")

        /** Creates a validated `data-*` attribute named [name]. */
        fun data(name: String): StyleAttribute = StyleAttribute("data-${validatedName(name)}")

        /** Creates a validated `ARIA-*` attribute named [name]. */
        fun aria(name: String): StyleAttribute = StyleAttribute("aria-${validatedName(name)}")

        private fun validatedName(name: String): String {
            require(name.matches(Regex("[a-zA-Z_][a-zA-Z0-9_-]*"))) {
                "Attribute names must contain only letters, digits, underscores, and hyphens"
            }
            return name
        }
    }
}

/**
 * CSS attribute-selector matching operators.
 *
 * @property operator The operator value.
 */
enum class AttributeMatch(internal val operator: String) {
    /** The equals attribute match option. */
    Equals("="),
    /** The includes word attribute match option. */
    IncludesWord("~="),
    /** The starts with attribute match option. */
    StartsWith("^="),
    /** The ends with attribute match option. */
    EndsWith("$="),
    /** The contains attribute match option. */
    Contains("*=")
}

/**
 * Supported CSS pseudo-classes.
 *
 * @property css The css value.
 */
sealed class StylePseudoClass(internal val css: String) {
    /** Pointer hover. */
    data object Hover : StylePseudoClass("hover")
    /** Element focus. */
    data object Focus : StylePseudoClass("focus")
    /** Keyboard-visible focus. */
    data object FocusVisible : StylePseudoClass("focus-visible")
    /** Focus within the subtree. */
    data object FocusWithin : StylePseudoClass("focus-within")
    /** Active interaction. */
    data object Active : StylePseudoClass("active")
    /** Visited link. */
    data object Visited : StylePseudoClass("visited")
    /** Disabled control. */
    data object Disabled : StylePseudoClass("disabled")
    /** Checked control. */
    data object Checked : StylePseudoClass("checked")
    /** First sibling. */
    data object FirstChild : StylePseudoClass("first-child")
    /** Last sibling. */
    data object LastChild : StylePseudoClass("last-child")
    /** Only sibling. */
    data object OnlyChild : StylePseudoClass("only-child")
    /** Element with no children. */
    data object Empty : StylePseudoClass("empty")

    /**
     * Validated `:nth-child()` expression.
     *
     * @property expression The expression value.
     */
    class NthChild private constructor(private val expression: String) :
        StylePseudoClass("nth-child($expression)") {
        /** Common `odd` sequence. */
        companion object {
            /** Odd children. */
            val Odd = NthChild("odd")
            /** Even children. */
            val Even = NthChild("even")

            /** Selects the one-based child at [index]. */
            fun index(index: Int): NthChild {
                require(index > 0) { "nth-child index must be greater than zero" }
                return NthChild(index.toString())
            }

            /** Selects children matching `step*n + offset`. */
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

/**
 * Supported CSS pseudo-elements, including explicit browser-prefixed variants.
 *
 * @property css The css value.
 */
enum class StylePseudoElement(internal val css: String) {
    /** The before style pseudo element option. */
    Before("before"),
    /** The after style pseudo element option. */
    After("after"),
    /** The placeholder style pseudo element option. */
    Placeholder("placeholder"),
    /** The selection style pseudo element option. */
    Selection("selection"),
    /** The marker style pseudo element option. */
    Marker("marker"),
    /** The file selector button style pseudo element option. */
    FileSelectorButton("file-selector-button"),
    /** The webkit details marker style pseudo element option. */
    WebkitDetailsMarker("-webkit-details-marker"),
    /** The webkit scrollbar style pseudo element option. */
    WebkitScrollbar("-webkit-scrollbar"),
    /** The webkit scrollbar track style pseudo element option. */
    WebkitScrollbarTrack("-webkit-scrollbar-track"),
    /** The webkit scrollbar thumb style pseudo element option. */
    WebkitScrollbarThumb("-webkit-scrollbar-thumb"),
    /** The webkit search cancel button style pseudo element option. */
    WebkitSearchCancelButton("-webkit-search-cancel-button"),
    /** The webkit slider thumb style pseudo element option. */
    WebkitSliderThumb("-webkit-slider-thumb")
}

/**
 * A structured selector for [TypedStyleSheet]. It cannot contain arbitrary selector syntax.
 */
sealed class StyleSelector {
    internal abstract fun render(): String

    /** Universal `*` selector. */
    data object Universal : StyleSelector() {
        /** Renders the operation. */
        override fun render() = "*"
    }

    /** Document `:root` selector. */
    data object Root : StyleSelector() {
        /** Renders the operation. */
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

    /** Requires this selector and validated CSS class [name]. */
    fun withClass(name: String): StyleSelector = WithClass(this, validateIdentifier(name))

    /** Adds a structured attribute selector. */
    fun attribute(
        attribute: StyleAttribute,
        value: String? = null,
        match: AttributeMatch = AttributeMatch.Equals,
        caseInsensitive: Boolean = false
    ): StyleSelector = WithAttribute(this, attribute, value, match, caseInsensitive)

    /** Adds [pseudoClass]. */
    fun pseudoClass(pseudoClass: StylePseudoClass): StyleSelector =
        WithPseudoClass(this, pseudoClass)

    /** Adds [pseudoElement]. */
    fun pseudoElement(pseudoElement: StylePseudoElement): StyleSelector =
        WithPseudoElement(this, pseudoElement)

    /** Excludes [excluded] with `:not()`. */
    fun not(excluded: StyleSelector): StyleSelector = Negated(this, excluded)

    /** Selects a descendant [target]. */
    fun descendant(target: StyleSelector): StyleSelector = Related(this, " ", target)

    /** Selects a direct child [target]. */
    fun child(target: StyleSelector): StyleSelector = Related(this, " > ", target)

    /** Selects the immediately following sibling [target]. */
    fun adjacentSibling(target: StyleSelector): StyleSelector = Related(this, " + ", target)

    /** Selects a following sibling [target]. */
    fun generalSibling(target: StyleSelector): StyleSelector = Related(this, " ~ ", target)

    /** Groups this selector with [other] and [additional]. */
    fun or(other: StyleSelector, vararg additional: StyleSelector): StyleSelector =
        Group(listOf(this, other) + additional)

    /** Validated selector factories. */
    companion object {
        /** Selects [element]. */
        fun element(element: StyleElement): StyleSelector = ElementSelector(element)

        /** Selects a validated class [name]. */
        fun className(name: String): StyleSelector = ClassSelector(validateIdentifier(name))

        /** Selects a validated ID [name]. */
        fun id(name: String): StyleSelector = IdSelector(validateIdentifier(name))

        /** Groups at least two selectors. */
        fun all(first: StyleSelector, second: StyleSelector, vararg additional: StyleSelector): StyleSelector =
            Group(listOf(first, second) + additional)

        /** Groups at least two [selectors]. */
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

/**
 * Validated CSS animation identifier.
 *
 * @property cssName The css name value.
 */
@JvmInline
value class AnimationName private constructor(internal val cssName: String) {
    /** Animation-name factory. */
    companion object {
        /** Creates a validated animation [name]. */
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

/** Collects bounded percentage keyframes for one animation. */
class TypedKeyframesScope internal constructor() {
    private val frames = mutableListOf<Keyframe>()

    /** Adds the zero-percent [modifier]. */
    fun from(modifier: Modifier) {
        frame(0, modifier)
    }

    /** Adds the hundred-percent [modifier]. */
    fun to(modifier: Modifier) {
        frame(100, modifier)
    }

    /** Adds declarations at [percent], which must be in `0..100`. */
    fun frame(percent: Int, modifier: Modifier) {
        require(percent in 0..100) { "Keyframe position must be between 0 and 100" }
        if (modifier.styles.isNotEmpty()) frames += Keyframe("$percent%", modifier)
    }

    internal fun build(): List<Keyframe> = frames.toList()
}

/** Collects structured global rules, media blocks, and keyframes. */
class TypedStyleSheetScope internal constructor() {
    private val entries = mutableListOf<TypedStyleEntry>()

    /** Adds [modifier] declarations under [selector]. */
    fun rule(
        selector: StyleSelector,
        modifier: Modifier,
        priority: StyleRulePriority = StyleRulePriority.Normal
    ) {
        if (modifier.styles.isNotEmpty()) entries += TypedRule(selector, modifier, priority)
    }

    /** Adds a typed [query] containing rules from [block]. */
    fun media(query: MediaQuery, block: TypedStyleSheetScope.() -> Unit) {
        require(query !is MediaQuery.Custom) {
            "TypedStyleSheet does not accept raw custom media-query strings"
        }
        val nested = TypedStyleSheetScope().apply(block).entries
        if (nested.isNotEmpty()) entries += TypedMediaBlock(query, nested.toList())
    }

    /** Adds [name] keyframes from [block]. */
    fun keyframes(name: AnimationName, block: TypedKeyframesScope.() -> Unit) {
        val frames = TypedKeyframesScope().apply(block).build()
        if (frames.isNotEmpty()) entries += TypedKeyframes(name, frames)
    }

    internal fun render(): String = entries.joinToString("\n") { it.render() }
}

/** Controls whether a typed global rule is allowed to override inline component defaults. */
enum class StyleRulePriority {
    /** The normal style rule priority option. */
    Normal,
    /** The important style rule priority option. */
    Important
}

/**
 * Emits global rules from structured selectors and typed [Modifier] declarations.
 * Application code never needs to author CSS syntax.
 */
@Composable
fun TypedStyleSheet(block: TypedStyleSheetScope.() -> Unit) {
    val css = TypedStyleSheetScope().apply(block).render()
    if (css.isNotEmpty()) GlobalStyle(TrustedCss.fromAuthorCode(css))
}

/** Applies a structured CSS animation shorthand. */
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

/**
 * Standard CSS animation timing functions.
 *
 * @property css The css value.
 */
enum class AnimationEasing(internal val css: String) {
    /** The linear animation easing option. */
    Linear("linear"),
    /** The ease animation easing option. */
    Ease("ease"),
    /** The ease in animation easing option. */
    EaseIn("ease-in"),
    /** The ease out animation easing option. */
    EaseOut("ease-out"),
    /** The ease in out animation easing option. */
    EaseInOut("ease-in-out"),
    /** The step start animation easing option. */
    StepStart("step-start"),
    /** The step end animation easing option. */
    StepEnd("step-end")
}

/**
 * Validated CSS animation repetition count.
 *
 * @property css The css value.
 */
sealed class AnimationIterationCount(internal val css: String) {
    /** Run once. */
    data object Once : AnimationIterationCount("1")
    /** Repeat indefinitely. */
    data object Infinite : AnimationIterationCount("infinite")

    /** Repeat a positive number of times. */
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
    if ('-' in this) this else camelToKebabCase()
