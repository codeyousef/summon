package codes.yousef.summon.modifier

/**
 * Type-safe representation of supported CSS pseudo-elements.
 */
enum class PseudoElement(val selector: String) {
    Before("::before"),
    After("::after");
}

/**
 * Internal data object stored on [Modifier] so renderers can emit the corresponding CSS.
 */
data class PseudoElementDefinition(
    val element: PseudoElement,
    val styles: Map<String, String>,
    val content: String = "\"\""
)

private fun buildPseudoModifier(builder: Modifier.() -> Modifier): Modifier =
    builder(Modifier())

private fun Modifier.ensureRelativePosition(): Modifier =
    if (styles.containsKey("position")) {
        this
    } else {
        style("position", "relative")
    }

private fun Modifier.withPseudoElement(
    element: PseudoElement,
    ensurePositionRelative: Boolean,
    content: String?,
    builder: Modifier.() -> Modifier
): Modifier {
    val pseudoModifier = buildPseudoModifier(builder)
    if (pseudoModifier.styles.isEmpty()) {
        return this
    }

    val host = if (ensurePositionRelative) ensureRelativePosition() else this
    val definition = PseudoElementDefinition(
        element = element,
        styles = pseudoModifier.styles,
        content = content ?: pseudoModifier.attributes["content"] ?: "\"\""
    )
    return ModifierImpl(
        host.styles,
        host.attributes,
        host.eventHandlers,
        host.complexEventHandlers,
        host.pseudoElements + definition
    )
}

/**
 * Registers a `::before` pseudo-element with its own modifier styles.
 *
 * @param ensurePositionRelative Automatically apply `position: relative` to the host element so
 * absolute pseudo-layers render correctly.
 * @param content Raw CSS content value. Defaults to `""`.
 * @param builder Modifier DSL used to describe the pseudo-element styles.
 */
fun Modifier.before(
    ensurePositionRelative: Boolean = true,
    content: String? = null,
    builder: Modifier.() -> Modifier
): Modifier = withPseudoElement(PseudoElement.Before, ensurePositionRelative, content, builder)

/**
 * Registers an `::after` pseudo-element with its own modifier styles.
 */
fun Modifier.after(
    ensurePositionRelative: Boolean = true,
    content: String? = null,
    builder: Modifier.() -> Modifier
): Modifier = withPseudoElement(PseudoElement.After, ensurePositionRelative, content, builder)
