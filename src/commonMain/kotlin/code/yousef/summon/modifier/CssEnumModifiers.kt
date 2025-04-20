package code.yousef.summon.modifier

/**
 * Sets the position property of the element using a Position enum value.
 * @param value The CSS position value as a Position enum
 */
fun Modifier.position(value: Position): Modifier = position(value.toString())

/**
 * Sets the overflow property of the element using an Overflow enum value.
 * @param value The CSS overflow value as an Overflow enum
 */
fun Modifier.overflow(value: Overflow): Modifier = overflow(value.toString())

/**
 * Sets the overflow-x property of the element using an Overflow enum value.
 * @param value The CSS overflow-x value as an Overflow enum
 */
fun Modifier.overflowX(value: Overflow): Modifier = overflowX(value.toString())

/**
 * Sets the overflow-y property of the element using an Overflow enum value.
 * @param value The CSS overflow-y value as an Overflow enum
 */
fun Modifier.overflowY(value: Overflow): Modifier = overflowY(value.toString())

/**
 * Sets the display property of the element using a Display enum value.
 * @param value The CSS display value as a Display enum
 */
fun Modifier.display(value: Display): Modifier = display(value.toString())

/**
 * Sets the justify-content property of the element using a JustifyContent enum value.
 * @param value The CSS justify-content value as a JustifyContent enum
 */
fun Modifier.justifyContent(value: JustifyContent): Modifier = justifyContent(value.toString())

/**
 * Sets the justify-items property of the element using a JustifyItems enum value.
 * @param value The CSS justify-items value as a JustifyItems enum
 */
fun Modifier.justifyItems(value: JustifyItems): Modifier = justifyItems(value.toString())

/**
 * Sets the justify-self property of the element using a JustifySelf enum value.
 * @param value The CSS justify-self value as a JustifySelf enum
 */
fun Modifier.justifySelf(value: JustifySelf): Modifier = justifySelf(value.toString())
