package code.yousef.summon.modifier

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
