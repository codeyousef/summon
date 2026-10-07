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
 * @see codes.yousef.summon.modifier.CssEnumModifiers for enum-based modifier functions
 * @see codes.yousef.summon.modifier.Modifier for core modifier functionality
 * @since 1.0.0
 */
package codes.yousef.summon.modifier

import codes.yousef.summon.extensions.px

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
 * Use `absolutePosition` or `centerAbsolute` for common absolute layouts.
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

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS overflow values.

 * @property value Value to process.
 */
enum class Overflow(val value: String) {
    /** The visible overflow option. */
    Visible("visible"),
    /** The hidden overflow option. */
    Hidden("hidden"),
    /** The scroll overflow option. */
    Scroll("scroll"),
    /** The auto overflow option. */
    Auto("auto");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS display values.

 * @property value Value to process.
 */
enum class Display(val value: String) {
    /** The none display option. */
    None("none"),
    /** The block display option. */
    Block("block"),
    /** The inline display option. */
    Inline("inline"),
    /** The inline block display option. */
    InlineBlock("inline-block"),
    /** The flex display option. */
    Flex("flex"),
    /** The grid display option. */
    Grid("grid"),
    /** The inline flex display option. */
    InlineFlex("inline-flex"),
    /** The inline grid display option. */
    InlineGrid("inline-grid");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS visibility values.

 * @property value Value to process.
 */
enum class Visibility(val value: String) {
    /** The visible visibility option. */
    Visible("visible"),
    /** The hidden visibility option. */
    Hidden("hidden"),
    /** The collapse visibility option. */
    Collapse("collapse");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS justify-content values.

 * @property value Value to process.
 */
enum class JustifyContent(val value: String) {
    /** The flex start justify content option. */
    FlexStart("flex-start"),
    /** The flex end justify content option. */
    FlexEnd("flex-end"),
    /** The center justify content option. */
    Center("center"),
    /** The space between justify content option. */
    SpaceBetween("space-between"),
    /** The space around justify content option. */
    SpaceAround("space-around"),
    /** The space evenly justify content option. */
    SpaceEvenly("space-evenly");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS justify-items values.

 * @property value Value to process.
 */
enum class JustifyItems(val value: String) {
    /** The start justify items option. */
    Start("start"),
    /** The end justify items option. */
    End("end"),
    /** The center justify items option. */
    Center("center"),
    /** The stretch justify items option. */
    Stretch("stretch"),
    /** The baseline justify items option. */
    Baseline("baseline");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS justify-self values.

 * @property value Value to process.
 */
enum class JustifySelf(val value: String) {
    /** The auto justify self option. */
    Auto("auto"),
    /** The start justify self option. */
    Start("start"),
    /** The end justify self option. */
    End("end"),
    /** The center justify self option. */
    Center("center"),
    /** The stretch justify self option. */
    Stretch("stretch"),
    /** The baseline justify self option. */
    Baseline("baseline");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS text-align values.

 * @property value Value to process.
 */
enum class TextAlign(val value: String) {
    /** The left text align option. */
    Left("left"),
    /** The right text align option. */
    Right("right"),
    /** The center text align option. */
    Center("center"),
    /** The justify text align option. */
    Justify("justify"),
    /** The start text align option. */
    Start("start"),
    /** The end text align option. */
    End("end");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS background-clip values.

 * @property value Value to process.
 */
enum class BackgroundClip(val value: String) {
    /** The border box background clip option. */
    BorderBox("border-box"),
    /** The padding box background clip option. */
    PaddingBox("padding-box"),
    /** The content box background clip option. */
    ContentBox("content-box"),
    /** The text background clip option. */
    Text("text");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS font-weight values.

 * @property value Value to process.
 */
enum class FontWeight(val value: String) {
    /** The thin font weight option. */
    Thin("100"),
    /** The extra light font weight option. */
    ExtraLight("200"),
    /** The light font weight option. */
    Light("300"),
    /** The normal font weight option. */
    Normal("400"),
    /** The medium font weight option. */
    Medium("500"),
    /** The semi bold font weight option. */
    SemiBold("600"),
    /** The bold font weight option. */
    Bold("700"),
    /** The extra bold font weight option. */
    ExtraBold("800"),
    /** The black font weight option. */
    Black("900");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value

    /** Provides font weight factory and constant members. */
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
 * CSS font-style values.

 * @property value Value to process.
 */
enum class FontStyle(val value: String) {
    /** The normal font style option. */
    Normal("normal"),
    /** The italic font style option. */
    Italic("italic"),
    /** The oblique font style option. */
    Oblique("oblique");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS text-decoration values.

 * @property value Value to process.
 */
enum class TextDecoration(val value: String) {
    /** The none text decoration option. */
    None("none"),
    /** The underline text decoration option. */
    Underline("underline"),
    /** The line through text decoration option. */
    LineThrough("line-through"),
    /** The overline text decoration option. */
    Overline("overline");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS white-space values.

 * @property value Value to process.
 */
enum class WhiteSpace(val value: String) {
    /** The normal white space option. */
    Normal("normal"),
    /** The no wrap white space option. */
    NoWrap("nowrap"),
    /** The pre white space option. */
    Pre("pre"),
    /** The pre line white space option. */
    PreLine("pre-line"),
    /** The pre wrap white space option. */
    PreWrap("pre-wrap"),
    /** The break spaces white space option. */
    BreakSpaces("break-spaces");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * HTML button types.

 * @property value Value to process.
 */
enum class ButtonType(val value: String) {
    /** The button button type option. */
    Button("button"),
    /** The submit button type option. */
    Submit("submit"),
    /** The reset button type option. */
    Reset("reset");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS radial-gradient shape values.

 * @property value Value to process.
 */
enum class RadialGradientShape(val value: String) {
    /** The circle radial gradient shape option. */
    Circle("circle"),
    /** The ellipse radial gradient shape option. */
    Ellipse("ellipse");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS radial-gradient position values.

 * @property value Value to process.
 */
enum class RadialGradientPosition(val value: String) {
    /** The center radial gradient position option. */
    Center("center"),
    /** The top radial gradient position option. */
    Top("top"),
    /** The right radial gradient position option. */
    Right("right"),
    /** The bottom radial gradient position option. */
    Bottom("bottom"),
    /** The left radial gradient position option. */
    Left("left"),
    /** The top left radial gradient position option. */
    TopLeft("top left"),
    /** The top right radial gradient position option. */
    TopRight("top right"),
    /** The bottom left radial gradient position option. */
    BottomLeft("bottom left"),
    /** The bottom right radial gradient position option. */
    BottomRight("bottom right");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS flex-wrap values.

 * @property value Value to process.
 */
enum class FlexWrap(val value: String) {
    /** The no wrap flex wrap option. */
    NoWrap("nowrap"),
    /** The wrap flex wrap option. */
    Wrap("wrap"),
    /** The wrap reverse flex wrap option. */
    WrapReverse("wrap-reverse");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS flex-direction values.

 * @property value Value to process.
 */
enum class FlexDirection(val value: String) {
    /** The row flex direction option. */
    Row("row"),
    /** The row reverse flex direction option. */
    RowReverse("row-reverse"),
    /** The column flex direction option. */
    Column("column"),
    /** The column reverse flex direction option. */
    ColumnReverse("column-reverse");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS border-style values.

 * @property value Value to process.
 */
enum class BorderStyle(val value: String) {
    /** The none border style option. */
    None("none"),
    /** The hidden border style option. */
    Hidden("hidden"),
    /** The dotted border style option. */
    Dotted("dotted"),
    /** The dashed border style option. */
    Dashed("dashed"),
    /** The solid border style option. */
    Solid("solid"),
    /** The double border style option. */
    Double("double"),
    /** The groove border style option. */
    Groove("groove"),
    /** The ridge border style option. */
    Ridge("ridge"),
    /** The inset border style option. */
    Inset("inset"),
    /** The outset border style option. */
    Outset("outset");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS cursor values.

 * @property value Value to process.
 */
enum class Cursor(val value: String) {
    /** The auto cursor option. */
    Auto("auto"),
    /** The default cursor option. */
    Default("default"),
    /** The none cursor option. */
    None("none"),
    /** The context menu cursor option. */
    ContextMenu("context-menu"),
    /** The help cursor option. */
    Help("help"),
    /** The pointer cursor option. */
    Pointer("pointer"),
    /** The progress cursor option. */
    Progress("progress"),
    /** The wait cursor option. */
    Wait("wait"),
    /** The cell cursor option. */
    Cell("cell"),
    /** The crosshair cursor option. */
    Crosshair("crosshair"),
    /** The text cursor option. */
    Text("text"),
    /** The vertical text cursor option. */
    VerticalText("vertical-text"),
    /** The alias cursor option. */
    Alias("alias"),
    /** The copy cursor option. */
    Copy("copy"),
    /** The move cursor option. */
    Move("move"),
    /** The no drop cursor option. */
    NoDrop("no-drop"),
    /** The not allowed cursor option. */
    NotAllowed("not-allowed"),
    /** The grab cursor option. */
    Grab("grab"),
    /** The grabbing cursor option. */
    Grabbing("grabbing"),
    /** The all scroll cursor option. */
    AllScroll("all-scroll"),
    /** The col resize cursor option. */
    ColResize("col-resize"),
    /** The row resize cursor option. */
    RowResize("row-resize"),
    /** The n resize cursor option. */
    NResize("n-resize"),
    /** The e resize cursor option. */
    EResize("e-resize"),
    /** The s resize cursor option. */
    SResize("s-resize"),
    /** The w resize cursor option. */
    WResize("w-resize"),
    /** The ne resize cursor option. */
    NeResize("ne-resize"),
    /** The nw resize cursor option. */
    NwResize("nw-resize"),
    /** The se resize cursor option. */
    SeResize("se-resize"),
    /** The sw resize cursor option. */
    SwResize("sw-resize"),
    /** The ew resize cursor option. */
    EwResize("ew-resize"),
    /** The ns resize cursor option. */
    NsResize("ns-resize"),
    /** The nesw resize cursor option. */
    NeswResize("nesw-resize"),
    /** The nwse resize cursor option. */
    NwseResize("nwse-resize"),
    /** The zoom in cursor option. */
    ZoomIn("zoom-in"),
    /** The zoom out cursor option. */
    ZoomOut("zoom-out");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS pointer-events values.

 * @property value Value to process.
 */
enum class PointerEvents(val value: String) {
    /** The auto pointer events option. */
    Auto("auto"),
    /** The none pointer events option. */
    None("none"),
    /** The visible painted pointer events option. */
    VisiblePainted("visiblePainted"),
    /** The visible fill pointer events option. */
    VisibleFill("visibleFill"),
    /** The visible stroke pointer events option. */
    VisibleStroke("visibleStroke"),
    /** The visible pointer events option. */
    Visible("visible"),
    /** The painted pointer events option. */
    Painted("painted"),
    /** The fill pointer events option. */
    Fill("fill"),
    /** The stroke pointer events option. */
    Stroke("stroke"),
    /** The all pointer events option. */
    All("all");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS transition-property values.

 * @property value Value to process.
 */
enum class TransitionProperty(val value: String) {
    /** The all transition property option. */
    All("all"),
    /** The none transition property option. */
    None("none"),
    /** The transform transition property option. */
    Transform("transform"),
    /** The opacity transition property option. */
    Opacity("opacity"),
    /** The background transition property option. */
    Background("background"),
    /** The background color transition property option. */
    BackgroundColor("background-color"),
    /** The color transition property option. */
    Color("color"),
    /** The height transition property option. */
    Height("height"),
    /** The width transition property option. */
    Width("width"),
    /** The margin transition property option. */
    Margin("margin"),
    /** The padding transition property option. */
    Padding("padding"),
    /** The border transition property option. */
    Border("border"),
    /** The border color transition property option. */
    BorderColor("border-color"),
    /** The border radius transition property option. */
    BorderRadius("border-radius"),
    /** The box shadow transition property option. */
    BoxShadow("box-shadow"),
    /** The text shadow transition property option. */
    TextShadow("text-shadow"),
    /** The font size transition property option. */
    FontSize("font-size"),
    /** The font weight transition property option. */
    FontWeight("font-weight"),
    /** The line height transition property option. */
    LineHeight("line-height"),
    /** The letter spacing transition property option. */
    LetterSpacing("letter-spacing"),
    /** The visibility transition property option. */
    Visibility("visibility"),
    /** The z index transition property option. */
    ZIndex("z-index");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS transition-timing-function values.

 * @property value Value to process.
 */
enum class TransitionTimingFunction(val value: String) {
    /** The ease transition timing function option. */
    Ease("ease"),
    /** The linear transition timing function option. */
    Linear("linear"),
    /** The ease in transition timing function option. */
    EaseIn("ease-in"),
    /** The ease out transition timing function option. */
    EaseOut("ease-out"),
    /** The ease in out transition timing function option. */
    EaseInOut("ease-in-out"),
    /** The step start transition timing function option. */
    StepStart("step-start"),
    /** The step end transition timing function option. */
    StepEnd("step-end"),
    /** The cubic bezier transition timing function option. */
    CubicBezier("cubic-bezier(0.4, 0, 0.2, 1)"); // Material Design standard easing

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS text-transform values.

 * @property value Value to process.
 */
enum class TextTransform(val value: String) {
    /** The none text transform option. */
    None("none"),
    /** The capitalize text transform option. */
    Capitalize("capitalize"),
    /** The uppercase text transform option. */
    Uppercase("uppercase"),
    /** The lowercase text transform option. */
    Lowercase("lowercase"),
    /** The full width text transform option. */
    FullWidth("full-width"),
    /** The full size kana text transform option. */
    FullSizeKana("full-size-kana");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS border side values.

 * @property value Value to process.
 */
enum class BorderSide(val value: String) {
    /** The top border side option. */
    Top("top"),
    /** The right border side option. */
    Right("right"),
    /** The bottom border side option. */
    Bottom("bottom"),
    /** The left border side option. */
    Left("left"),
    /** The all border side option. */
    All("all");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS align-items values.

 * @property value Value to process.
 */
enum class AlignItems(val value: String) {
    /** The flex start align items option. */
    FlexStart("flex-start"),
    /** The flex end align items option. */
    FlexEnd("flex-end"),
    /** The center align items option. */
    Center("center"),
    /** The baseline align items option. */
    Baseline("baseline"),
    /** The stretch align items option. */
    Stretch("stretch");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS align-content values.

 * @property value Value to process.
 */
enum class AlignContent(val value: String) {
    /** The flex start align content option. */
    FlexStart("flex-start"),
    /** The flex end align content option. */
    FlexEnd("flex-end"),
    /** The center align content option. */
    Center("center"),
    /** The space between align content option. */
    SpaceBetween("space-between"),
    /** The space around align content option. */
    SpaceAround("space-around"),
    /** The stretch align content option. */
    Stretch("stretch");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS align-self values.

 * @property value Value to process.
 */
enum class AlignSelf(val value: String) {
    /** The auto align self option. */
    Auto("auto"),
    /** The flex start align self option. */
    FlexStart("flex-start"),
    /** The flex end align self option. */
    FlexEnd("flex-end"),
    /** The center align self option. */
    Center("center"),
    /** The baseline align self option. */
    Baseline("baseline"),
    /** The stretch align self option. */
    Stretch("stretch");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS transform function values.

 * @property value Value to process.
 */
enum class TransformFunction(val value: String) {
    /** The translate transform function option. */
    Translate("translate"),
    /** The translate x transform function option. */
    TranslateX("translateX"),
    /** The translate y transform function option. */
    TranslateY("translateY"),
    /** The translate z transform function option. */
    TranslateZ("translateZ"),
    /** The translate3d transform function option. */
    Translate3d("translate3d"),
    /** The scale transform function option. */
    Scale("scale"),
    /** The scale x transform function option. */
    ScaleX("scaleX"),
    /** The scale y transform function option. */
    ScaleY("scaleY"),
    /** The rotate transform function option. */
    Rotate("rotate"),
    /** The rotate x transform function option. */
    RotateX("rotateX"),
    /** The rotate y transform function option. */
    RotateY("rotateY"),
    /** The rotate z transform function option. */
    RotateZ("rotateZ"),
    /** The rotate3d transform function option. */
    Rotate3d("rotate3d"),
    /** The skew transform function option. */
    Skew("skew"),
    /** The skew x transform function option. */
    SkewX("skewX"),
    /** The skew y transform function option. */
    SkewY("skewY"),
    /** The perspective transform function option. */
    Perspective("perspective");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS filter function values.

 * @property value Value to process.
 */
enum class FilterFunction(val value: String) {
    /** The blur filter function option. */
    Blur("blur"),
    /** The brightness filter function option. */
    Brightness("brightness"),
    /** The contrast filter function option. */
    Contrast("contrast"),
    /** The grayscale filter function option. */
    Grayscale("grayscale"),
    /** The hue rotate filter function option. */
    HueRotate("hue-rotate"),
    /** The invert filter function option. */
    Invert("invert"),
    /** The saturate filter function option. */
    Saturate("saturate"),
    /** The sepia filter function option. */
    Sepia("sepia"),
    /** The drop shadow filter function option. */
    DropShadow("drop-shadow");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS mix-blend-mode values.

 * @property value Value to process.
 */
enum class BlendMode(val value: String) {
    /** The normal blend mode option. */
    Normal("normal"),
    /** The multiply blend mode option. */
    Multiply("multiply"),
    /** The screen blend mode option. */
    Screen("screen"),
    /** The overlay blend mode option. */
    Overlay("overlay"),
    /** The darken blend mode option. */
    Darken("darken"),
    /** The lighten blend mode option. */
    Lighten("lighten"),
    /** The color dodge blend mode option. */
    ColorDodge("color-dodge"),
    /** The color burn blend mode option. */
    ColorBurn("color-burn"),
    /** The hard light blend mode option. */
    HardLight("hard-light"),
    /** The soft light blend mode option. */
    SoftLight("soft-light"),
    /** The difference blend mode option. */
    Difference("difference"),
    /** The exclusion blend mode option. */
    Exclusion("exclusion");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS animation duration presets with proper units.

 * @property value Value to process.
 * @property unit The unit value.
 */
enum class AnimationDuration(val value: Number, val unit: String) {
    /** The instant animation duration option. */
    Instant(0, "s"),
    /** The fast animation duration option. */
    Fast(200, "ms"),
    /** The medium animation duration option. */
    Medium(500, "ms"),
    /** The slow animation duration option. */
    Slow(1, "s"),
    /** The very slow animation duration option. */
    VerySlow(2, "s");

    /** The property declaration value. */
    val css: String get() = "$value$unit"
    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = css
}

/**
 * CSS animation direction values.

 * @property value Value to process.
 */
enum class AnimationDirection(val value: String) {
    /** The normal animation direction option. */
    Normal("normal"),
    /** The reverse animation direction option. */
    Reverse("reverse"),
    /** The alternate animation direction option. */
    Alternate("alternate"),
    /** The alternate reverse animation direction option. */
    AlternateReverse("alternate-reverse");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * CSS animation fill mode values.

 * @property value Value to process.
 */
enum class AnimationFillMode(val value: String) {
    /** The none animation fill mode option. */
    None("none"),
    /** The forwards animation fill mode option. */
    Forwards("forwards"),
    /** The backwards animation fill mode option. */
    Backwards("backwards"),
    /** The both animation fill mode option. */
    Both("both");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}

/**
 * Radial positioning radius presets with unit extensions.

 * @property value Value to process.
 */
enum class RadialRadius(val value: Number) {
    /** The small radial radius option. */
    Small(100),
    /** The medium radial radius option. */
    Medium(200),
    /** The large radial radius option. */
    Large(300),
    /** The extra large radial radius option. */
    ExtraLarge(450);

    /** The property declaration value. */
    val px: String get() = value.px
    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = px
}

/**
 * Radial positioning angles with proper degree units.

 * @property degrees The degrees value.
 */
enum class RadialAngle(val degrees: Number) {
    /** The deg0 radial angle option. */
    Deg0(0),
    /** The deg45 radial angle option. */
    Deg45(45),
    /** The deg90 radial angle option. */
    Deg90(90),
    /** The deg135 radial angle option. */
    Deg135(135),
    /** The deg180 radial angle option. */
    Deg180(180),
    /** The deg225 radial angle option. */
    Deg225(225),
    /** The deg270 radial angle option. */
    Deg270(270),
    /** The deg315 radial angle option. */
    Deg315(315);

    /** The property declaration value. */
    val deg: String get() = "${degrees}deg"
    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = deg

    /** Provides radial angle factory and constant members. */
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

 * @property value Value to process.
 */
enum class FloatIntensity(val value: Number) {
    /** The subtle float intensity option. */
    Subtle(5),
    /** The gentle float intensity option. */
    Gentle(10),
    /** The moderate float intensity option. */
    Moderate(20),
    /** The strong float intensity option. */
    Strong(30);

    /** The property declaration value. */
    val px: String get() = value.px
    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = px
}

/**
 * Animation rotation speed presets.

 * @property value Value to process.
 * @property unit The unit value.
 */
enum class RotationSpeed(val value: Number, val unit: String) {
    /** The very slow rotation speed option. */
    VerySlow(30, "s"),
    /** The slow rotation speed option. */
    Slow(20, "s"),
    /** The medium rotation speed option. */
    Medium(10, "s"),
    /** The fast rotation speed option. */
    Fast(5, "s"),
    /** The very fast rotation speed option. */
    VeryFast(2, "s");

    /** The property declaration value. */
    val css: String get() = "$value$unit"
    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = css
}

/**
 * CSS object-fit values for controlling how content fits within its container.

 * @property value Value to process.
 */
enum class ObjectFit(val value: String) {
    /** The fill object fit option. */
    Fill("fill"),
    /** The contain object fit option. */
    Contain("contain"),
    /** The cover object fit option. */
    Cover("cover"),
    /** The none object fit option. */
    None("none"),
    /** The scale down object fit option. */
    ScaleDown("scale-down");

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = value
}
