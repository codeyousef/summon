package codes.yousef.summon.modifier

/**
 * A structured CSS rule that cannot be represented by an element's inline style attribute.
 *
 * Platform renderers consume these definitions directly. The legacy serialized data attributes
 * remain available for compatibility, but are not the source of truth for new renderers.
 */
sealed interface ConditionalStyleDefinition {
    val styles: Map<String, String>
}

/** Pseudo-class states supported by Summon's conditional modifier APIs. */
enum class ConditionalStyleState {
    HOVER,
    FOCUS,
    FOCUS_VISIBLE,
    ACTIVE,
    FOCUS_WITHIN,
    FIRST_CHILD,
    LAST_CHILD,
    NTH_CHILD,
    ONLY_CHILD,
    VISITED,
    DISABLED,
    CHECKED
}

/**
 * Styles applied while an element matches [state].
 *
 * [argument] is required for [ConditionalStyleState.NTH_CHILD] and omitted for all other states.
 */
data class StateStyleDefinition(
    val state: ConditionalStyleState,
    override val styles: Map<String, String>,
    val argument: String? = null
) : ConditionalStyleDefinition {
    init {
        require(state == ConditionalStyleState.NTH_CHILD || argument == null) {
            "Only NTH_CHILD accepts a state argument"
        }
        require(state != ConditionalStyleState.NTH_CHILD || !argument.isNullOrBlank()) {
            "NTH_CHILD requires a non-blank argument"
        }
    }
}

/** Styles applied while [query] matches. */
data class MediaStyleDefinition(
    val query: MediaQuery,
    override val styles: Map<String, String>
) : ConditionalStyleDefinition

/** Appends a structured conditional style while preserving all existing modifier state. */
internal fun Modifier.withConditionalStyle(definition: ConditionalStyleDefinition): Modifier =
    when (this) {
        is ModifierImpl -> copy(conditionalStyles = conditionalStyles + definition)
        else -> ModifierImpl(conditionalStyles = listOf(definition))
    }
