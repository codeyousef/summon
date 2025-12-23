package codes.yousef.summon.modifier

/**
 * Sets the position property of the element using a Position enum value.
 * @param value The CSS position value as a Position enum
 */
fun Modifier.position(value: Position): Modifier = style("position", value.toString())

/**
 * Sets the overflow property of the element using an Overflow enum value.
 * @param value The CSS overflow value as an Overflow enum
 */
fun Modifier.overflow(value: Overflow): Modifier = style("overflow", value.toString())

/**
 * Sets the overflow-x property of the element using an Overflow enum value.
 * @param value The CSS overflow-x value as an Overflow enum
 */
fun Modifier.overflowX(value: Overflow): Modifier = style("overflow-x", value.toString())

/**
 * Sets the overflow-y property of the element using an Overflow enum value.
 * @param value The CSS overflow-y value as an Overflow enum
 */
fun Modifier.overflowY(value: Overflow): Modifier = style("overflow-y", value.toString())

/**
 * Sets the display property of the element using a Display enum value.
 * @param value The CSS display value as a Display enum
 */
fun Modifier.display(value: Display): Modifier = style("display", value.toString())

/**
 * Sets the justify-content property of the element using a JustifyContent enum value.
 * @param value The CSS justify-content value as a JustifyContent enum
 */
fun Modifier.justifyContent(value: JustifyContent): Modifier = style("justify-content", value.toString())

/**
 * Sets the justify-items property of the element using a JustifyItems enum value.
 * @param value The CSS justify-items value as a JustifyItems enum
 */
fun Modifier.justifyItems(value: JustifyItems): Modifier = style("justify-items", value.toString())

/**
 * Sets the justify-self property of the element using a JustifySelf enum value.
 * @param value The CSS justify-self value as a JustifySelf enum
 */
fun Modifier.justifySelf(value: JustifySelf): Modifier = style("justify-self", value.toString())

/**
 * Sets the align-items property of the element using an AlignItems enum value.
 * @param value The CSS align-items value as an AlignItems enum
 */
fun Modifier.alignItems(value: AlignItems): Modifier = style("align-items", value.toString())

/**
 * Sets the align-self property of the element using an AlignSelf enum value.
 * @param value The CSS align-self value as an AlignSelf enum
 */
fun Modifier.alignSelf(value: AlignSelf): Modifier = style("align-self", value.toString())

/**
 * Sets the align-content property of the element using an AlignContent enum value.
 * @param value The CSS align-content value as an AlignContent enum
 */
fun Modifier.alignContent(value: AlignContent): Modifier = style("align-content", value.toString())

/**
 * Sets the flex-direction property of the element using a FlexDirection enum value.
 * @param value The CSS flex-direction value as a FlexDirection enum
 */
fun Modifier.flexDirection(value: FlexDirection): Modifier = style("flex-direction", value.toString())


