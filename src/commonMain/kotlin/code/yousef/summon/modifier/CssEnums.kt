package code.yousef.summon.modifier

import code.yousef.summon.extensions.px

/**
 * CSS position values.
 */
enum class Position(val value: String) {
    Static("static"),
    Relative("relative"),
    Absolute("absolute"),
    Fixed("fixed"),
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
