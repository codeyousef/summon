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
