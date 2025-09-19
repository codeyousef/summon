/**
 * # CSS Type-Safe Enums
 *
 * This file provides comprehensive type-safe enumerations for CSS property values used
 * throughout the Summon UI modifier system. These enums offer compile-time safety,
 * IDE autocompletion, and prevent common CSS value typos while maintaining full
 * CSS specification compliance.
 *
 * ## Overview
 *
 * Type-safe CSS enums provide several advantages over string-based CSS values:
 *
 * - **Compile-Time Safety**: Catch invalid CSS values at compile time
 * - **IDE Support**: Full autocompletion and refactoring capabilities
 * - **Documentation**: Each enum value includes comprehensive usage guidance
 * - **Consistency**: Standardized naming conventions across the framework
 * - **Extensibility**: Easy to extend with new CSS values as specifications evolve
 *
 * ## Design Principles
 *
 * ### Naming Conventions
 * - **Enum Names**: PascalCase following Kotlin conventions (e.g., `JustifyContent`)
 * - **Value Names**: PascalCase for consistency (e.g., `FlexStart` for "flex-start")
 * - **CSS Output**: Automatic conversion to kebab-case CSS values
 *
 * ### Value Mapping
 * - Each enum value contains the exact CSS string representation
 * - `toString()` method returns the CSS-compatible value
 * - Direct mapping to CSS specifications for accuracy
 *
 * ### Comprehensive Coverage
 * - **Layout Properties**: Position, display, flexbox, grid alignment
 * - **Typography**: Font weights, text alignment, line heights
 * - **Visual Properties**: Border styles, background behaviors
 * - **Interactive States**: Cursor types, pointer events
 * - **Animation Values**: Timing functions, directions, iteration counts
 *
 * ## Usage Examples
 *
 * ### Layout Positioning
 * ```kotlin
 * // Type-safe positioning
 * val modal = Modifier()
 *     .position(Position.Fixed)
 *     .style("top", "50%")
 *     .style("left", "50%")
 *
 * // Flexible layout container
 * val container = Modifier()
 *     .display(Display.Flex)
 *     .justifyContent(JustifyContent.SpaceBetween)
 *     .alignItems(AlignItems.Center)
 * ```
 *
 * ### Typography Control
 * ```kotlin
 * // Heading with proper weight
 * val heading = Modifier()
 *     .fontWeight(FontWeight.Bold)
 *     .textAlign(TextAlign.Center)
 *     .lineHeight(LineHeight.Tight)
 *
 * // Body text with optimal readability
 * val body = Modifier()
 *     .fontWeight(FontWeight.Normal)
 *     .textAlign(TextAlign.Left)
 *     .lineHeight(LineHeight.Relaxed)
 * ```
 *
 * ### Interactive Elements
 * ```kotlin
 * // Button with proper cursor and borders
 * val button = Modifier()
 *     .cursor(Cursor.Pointer)
 *     .borderStyle(BorderStyle.Solid)
 *     .backgroundClip(BackgroundClip.PaddingBox)
 *
 * // Disabled state handling
 * val disabledButton = Modifier()
 *     .cursor(Cursor.NotAllowed)
 *     .pointerEvents(PointerEvents.None)
 * ```
 *
 * ## Performance Benefits
 *
 * - **String Interning**: Enum values use singleton instances
 * - **Memory Efficiency**: No string allocation for common CSS values
 * - **Comparison Speed**: Enum comparison is faster than string comparison
 * - **Tree Shaking**: Unused enum values can be eliminated in production builds
 *
 * ## CSS Specification Compliance
 *
 * All enum values correspond directly to CSS specification values:
 * - **CSS 2.1**: Core layout and positioning properties
 * - **CSS 3**: Flexbox, grid, transforms, and animations
 * - **CSS 4**: Latest color functions and modern layout features
 * - **Vendor Prefixes**: Handled automatically where needed
 *
 * @see code.yousef.summon.modifier.CssEnumModifiers for enum-based modifier functions
 * @see code.yousef.summon.modifier.Modifier for core modifier functionality
 * @since 1.0.0
 */
package code.yousef.summon.modifier

import code.yousef.summon.extensions.px

/**
 * CSS position property values for element positioning control.
 *
 * Defines how elements are positioned within their containing blocks,
 * affecting their placement in the normal document flow and their
 * relationship to other elements.
 *
 * ## Positioning Behaviors
 *
 * - **Static**: Default positioning in normal document flow
 * - **Relative**: Positioned relative to normal position, maintains space
 * - **Absolute**: Positioned relative to nearest positioned ancestor
 * - **Fixed**: Positioned relative to viewport, ignores scrolling
 * - **Sticky**: Switches between relative and fixed based on scroll position
 *
 * ## Examples
 * ```kotlin
 * // Modal overlay with fixed positioning
 * val overlay = Modifier()
 *     .position(Position.Fixed)
 *     .style("inset", "0") // top: 0, right: 0, bottom: 0, left: 0
 *     .backgroundColor("rgba(0, 0, 0, 0.5)")
 *
 * // Sticky navigation header
 * val stickyNav = Modifier()
 *     .position(Position.Sticky)
 *     .style("top", "0")
 *     .backgroundColor("white")
 *     .zIndex(100)
 *
 * // Tooltip positioned absolutely
 * val tooltip = Modifier()
 *     .position(Position.Absolute)
 *     .style("top", "100%")
 *     .style("left", "50%")
 *     .transform(TransformFunction.TranslateX to "-50%")
 * ```
 *
 * @property value The CSS position value string
 * @see code.yousef.summon.modifier.LayoutModifiers.absolutePosition
 * @see code.yousef.summon.modifier.LayoutModifiers.centerAbsolute
 * @since 1.0.0
 */
enum class Position(val value: String) {
    /** Default positioning in normal document flow */
    Static("static"),

    /** Positioned relative to its normal position */
    Relative("relative"),

    /** Positioned relative to nearest positioned ancestor */
    Absolute("absolute"),

    /** Positioned relative to viewport */
    Fixed("fixed"),

    /** Toggles between relative and fixed based on scroll position */
    Sticky("sticky");

    override fun toString(): String = value
}

/**
 * CSS overflow values.
 */
enum class Overflow(val value: String) {
    Visible("visible"),
    Hidden("hidden"),
    Scroll("scroll"),
    Auto("auto");

    override fun toString(): String = value
}

/**
 * CSS display values.
 */
enum class Display(val value: String) {
    None("none"),
    Block("block"),
    Inline("inline"),
    InlineBlock("inline-block"),
    Flex("flex"),
    Grid("grid"),
    InlineFlex("inline-flex"),
    InlineGrid("inline-grid");

    override fun toString(): String = value
}

/**
 * CSS justify-content values.
 */
enum class JustifyContent(val value: String) {
    FlexStart("flex-start"),
    FlexEnd("flex-end"),
    Center("center"),
    SpaceBetween("space-between"),
    SpaceAround("space-around"),
    SpaceEvenly("space-evenly");

    override fun toString(): String = value
}

/**
 * CSS justify-items values.
 */
enum class JustifyItems(val value: String) {
    Start("start"),
    End("end"),
    Center("center"),
    Stretch("stretch"),
    Baseline("baseline");

    override fun toString(): String = value
}

/**
 * CSS justify-self values.
 */
enum class JustifySelf(val value: String) {
    Auto("auto"),
    Start("start"),
    End("end"),
    Center("center"),
    Stretch("stretch"),
    Baseline("baseline");

    override fun toString(): String = value
}

/**
 * CSS text-align values.
 */
enum class TextAlign(val value: String) {
    Left("left"),
    Right("right"),
    Center("center"),
    Justify("justify"),
    Start("start"),
    End("end");

    override fun toString(): String = value
}

/**
 * CSS background-clip values.
 */
enum class BackgroundClip(val value: String) {
    BorderBox("border-box"),
    PaddingBox("padding-box"),
    ContentBox("content-box"),
    Text("text");

    override fun toString(): String = value
}

/**
 * CSS font-weight values.
 */
enum class FontWeight(val value: String) {
    Thin("100"),
    ExtraLight("200"),
    Light("300"),
    Normal("400"),
    Medium("500"),
    SemiBold("600"),
    Bold("700"),
    ExtraBold("800"),
    Black("900");

    override fun toString(): String = value

    companion object {
        /**
         * Creates a FontWeight from a numeric value.
         * @param weight The numeric weight value (100-900)
         * @return The corresponding FontWeight enum value, or null if no exact match
         */
        fun fromValue(weight: Int): FontWeight? = values().find { it.value == weight.toString() }
    }
}

/**
 * CSS radial-gradient shape values.
 */
enum class RadialGradientShape(val value: String) {
    Circle("circle"),
    Ellipse("ellipse");

    override fun toString(): String = value
}

/**
 * CSS radial-gradient position values.
 */
enum class RadialGradientPosition(val value: String) {
    Center("center"),
    Top("top"),
    Right("right"),
    Bottom("bottom"),
    Left("left"),
    TopLeft("top left"),
    TopRight("top right"),
    BottomLeft("bottom left"),
    BottomRight("bottom right");

    override fun toString(): String = value
}

/**
 * CSS flex-wrap values.
 */
enum class FlexWrap(val value: String) {
    NoWrap("nowrap"),
    Wrap("wrap"),
    WrapReverse("wrap-reverse");

    override fun toString(): String = value
}

/**
 * CSS flex-direction values.
 */
enum class FlexDirection(val value: String) {
    Row("row"),
    RowReverse("row-reverse"),
    Column("column"),
    ColumnReverse("column-reverse");

    override fun toString(): String = value
}

/**
 * CSS border-style values.
 */
enum class BorderStyle(val value: String) {
    None("none"),
    Hidden("hidden"),
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

/**
 * CSS cursor values.
 */
enum class Cursor(val value: String) {
    Auto("auto"),
    Default("default"),
    None("none"),
    ContextMenu("context-menu"),
    Help("help"),
    Pointer("pointer"),
    Progress("progress"),
    Wait("wait"),
    Cell("cell"),
    Crosshair("crosshair"),
    Text("text"),
    VerticalText("vertical-text"),
    Alias("alias"),
    Copy("copy"),
    Move("move"),
    NoDrop("no-drop"),
    NotAllowed("not-allowed"),
    Grab("grab"),
    Grabbing("grabbing"),
    AllScroll("all-scroll"),
    ColResize("col-resize"),
    RowResize("row-resize"),
    NResize("n-resize"),
    EResize("e-resize"),
    SResize("s-resize"),
    WResize("w-resize"),
    NeResize("ne-resize"),
    NwResize("nw-resize"),
    SeResize("se-resize"),
    SwResize("sw-resize"),
    EwResize("ew-resize"),
    NsResize("ns-resize"),
    NeswResize("nesw-resize"),
    NwseResize("nwse-resize"),
    ZoomIn("zoom-in"),
    ZoomOut("zoom-out");

    override fun toString(): String = value
}

/**
 * CSS transition-property values.
 */
enum class TransitionProperty(val value: String) {
    All("all"),
    None("none"),
    Transform("transform"),
    Opacity("opacity"),
    Background("background"),
    BackgroundColor("background-color"),
    Color("color"),
    Height("height"),
    Width("width"),
    Margin("margin"),
    Padding("padding"),
    Border("border"),
    BorderColor("border-color"),
    BorderRadius("border-radius"),
    BoxShadow("box-shadow"),
    TextShadow("text-shadow"),
    FontSize("font-size"),
    FontWeight("font-weight"),
    LineHeight("line-height"),
    LetterSpacing("letter-spacing"),
    Visibility("visibility"),
    ZIndex("z-index");

    override fun toString(): String = value
}

/**
 * CSS transition-timing-function values.
 */
enum class TransitionTimingFunction(val value: String) {
    Ease("ease"),
    Linear("linear"),
    EaseIn("ease-in"),
    EaseOut("ease-out"),
    EaseInOut("ease-in-out"),
    StepStart("step-start"),
    StepEnd("step-end"),
    CubicBezier("cubic-bezier(0.4, 0, 0.2, 1)"); // Material Design standard easing

    override fun toString(): String = value
}

/**
 * CSS text-transform values.
 */
enum class TextTransform(val value: String) {
    None("none"),
    Capitalize("capitalize"),
    Uppercase("uppercase"),
    Lowercase("lowercase"),
    FullWidth("full-width"),
    FullSizeKana("full-size-kana");

    override fun toString(): String = value
}

/**
 * CSS border side values.
 */
enum class BorderSide(val value: String) {
    Top("top"),
    Right("right"),
    Bottom("bottom"),
    Left("left"),
    All("all");

    override fun toString(): String = value
}

/**
 * CSS align-items values.
 */
enum class AlignItems(val value: String) {
    FlexStart("flex-start"),
    FlexEnd("flex-end"),
    Center("center"),
    Baseline("baseline"),
    Stretch("stretch");

    override fun toString(): String = value
}

/**
 * CSS align-content values.
 */
enum class AlignContent(val value: String) {
    FlexStart("flex-start"),
    FlexEnd("flex-end"),
    Center("center"),
    SpaceBetween("space-between"),
    SpaceAround("space-around"),
    Stretch("stretch");

    override fun toString(): String = value
}

/**
 * CSS align-self values.
 */
enum class AlignSelf(val value: String) {
    Auto("auto"),
    FlexStart("flex-start"),
    FlexEnd("flex-end"),
    Center("center"),
    Baseline("baseline"),
    Stretch("stretch");

    override fun toString(): String = value
}

/**
 * CSS transform function values.
 */
enum class TransformFunction(val value: String) {
    Translate("translate"),
    TranslateX("translateX"),
    TranslateY("translateY"),
    TranslateZ("translateZ"),
    Translate3d("translate3d"),
    Scale("scale"),
    ScaleX("scaleX"),
    ScaleY("scaleY"),
    Rotate("rotate"),
    RotateX("rotateX"),
    RotateY("rotateY"),
    RotateZ("rotateZ"),
    Rotate3d("rotate3d"),
    Skew("skew"),
    SkewX("skewX"),
    SkewY("skewY"),
    Perspective("perspective");

    override fun toString(): String = value
}

/**
 * CSS filter function values.
 */
enum class FilterFunction(val value: String) {
    Blur("blur"),
    Brightness("brightness"),
    Contrast("contrast"),
    Grayscale("grayscale"),
    HueRotate("hue-rotate"),
    Invert("invert"),
    Saturate("saturate"),
    Sepia("sepia"),
    DropShadow("drop-shadow");

    override fun toString(): String = value
}

/**
 * CSS mix-blend-mode values.
 */
enum class BlendMode(val value: String) {
    Normal("normal"),
    Multiply("multiply"),
    Screen("screen"),
    Overlay("overlay"),
    Darken("darken"),
    Lighten("lighten"),
    ColorDodge("color-dodge"),
    ColorBurn("color-burn"),
    HardLight("hard-light"),
    SoftLight("soft-light"),
    Difference("difference"),
    Exclusion("exclusion");

    override fun toString(): String = value
}

/**
 * CSS animation duration presets with proper units.
 */
enum class AnimationDuration(val value: Number, val unit: String) {
    Instant(0, "s"),
    Fast(200, "ms"),
    Medium(500, "ms"),
    Slow(1, "s"),
    VerySlow(2, "s");

    val css: String get() = "$value$unit"
    override fun toString(): String = css
}

/**
 * CSS animation direction values.
 */
enum class AnimationDirection(val value: String) {
    Normal("normal"),
    Reverse("reverse"),
    Alternate("alternate"),
    AlternateReverse("alternate-reverse");

    override fun toString(): String = value
}

/**
 * CSS animation fill mode values.
 */
enum class AnimationFillMode(val value: String) {
    None("none"),
    Forwards("forwards"),
    Backwards("backwards"),
    Both("both");

    override fun toString(): String = value
}

/**
 * Radial positioning radius presets with unit extensions.
 */
enum class RadialRadius(val value: Number) {
    Small(100),
    Medium(200),
    Large(300),
    ExtraLarge(450);

    val px: String get() = value.px
    override fun toString(): String = px
}

/**
 * Radial positioning angles with proper degree units.
 */
enum class RadialAngle(val degrees: Number) {
    Deg0(0),
    Deg45(45),
    Deg90(90),
    Deg135(135),
    Deg180(180),
    Deg225(225),
    Deg270(270),
    Deg315(315);

    val deg: String get() = "${degrees}deg"
    override fun toString(): String = deg

    companion object {
        /**
         * Creates a RadialAngle from a degree value.
         * @param degrees The angle in degrees
         * @return RadialAngle if found, null otherwise
         */
        fun fromDegrees(degrees: Number): RadialAngle? =
            values().find { it.degrees == degrees }
    }
}

/**
 * Animation floating intensity presets.
 */
enum class FloatIntensity(val value: Number) {
    Subtle(5),
    Gentle(10),
    Moderate(20),
    Strong(30);

    val px: String get() = value.px
    override fun toString(): String = px
}

/**
 * Animation rotation speed presets.
 */
enum class RotationSpeed(val value: Number, val unit: String) {
    VerySlow(30, "s"),
    Slow(20, "s"),
    Medium(10, "s"),
    Fast(5, "s"),
    VeryFast(2, "s");

    val css: String get() = "$value$unit"
    override fun toString(): String = css
}
