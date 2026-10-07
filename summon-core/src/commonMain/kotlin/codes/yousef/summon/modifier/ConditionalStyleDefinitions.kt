package codes.yousef.summon.modifier

/**
 * A structured CSS rule that cannot be represented by an element's inline style attribute.
 *
 * Platform renderers consume these definitions directly. The legacy serialized data attributes
 * remain available for compatibility, but are not the source of truth for new renderers.
 */
sealed interface ConditionalStyleDefinition {
    /** The property declaration value. */
    val styles: Map<String, String>
}

/** Pseudo-class states supported by Summon's conditional modifier APIs. */
enum class ConditionalStyleState {
    /** The hover conditional style state option. */
    HOVER,
    /** The focus conditional style state option. */
    FOCUS,
    /** The focus visible conditional style state option. */
    FOCUS_VISIBLE,
    /** The active conditional style state option. */
    ACTIVE,
    /** The focus within conditional style state option. */
    FOCUS_WITHIN,
    /** The first child conditional style state option. */
    FIRST_CHILD,
    /** The last child conditional style state option. */
    LAST_CHILD,
    /** The nth child conditional style state option. */
    NTH_CHILD,
    /** The only child conditional style state option. */
    ONLY_CHILD,
    /** The visited conditional style state option. */
    VISITED,
    /** The disabled conditional style state option. */
    DISABLED,
    /** The checked conditional style state option. */
    CHECKED
}

/**
 * Styles applied while an element matches [state].
 *
 * [argument] is required for [ConditionalStyleState.NTH_CHILD] and omitted for all other states.

 * @property state The state value.
 * @property styles The styles value.
 * @property argument The argument value.
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

/**
 * Styles applied while [query] matches.
 *
 * @property query The query value.
 * @property styles The styles value.
 */
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
