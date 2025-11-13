package codes.yousef.summon.accessibility

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * AccessibleElement is a wrapper component that adds accessibility attributes to its content.
 * It provides a simple way to make UI elements more accessible.
 *
 * @param content The composables to be rendered inside this element
 * @param role The ARIA role for the element from predefined node roles
 * @param customRole A custom ARIA role if not available in the predefined roles
 * @param label The accessible label for screen readers (aria-label)
 * @param relations Map of relationship types to target IDs (e.g. "describedby" to "description-id")
 * @param modifier Additional modifiers to apply to the element
 */
@Composable
fun AccessibleElement(
    content: @Composable () -> Unit,
    role: AccessibilityUtils.NodeRole? = null,
    customRole: String? = null,
    label: String? = null,
    relations: Map<String, String> = emptyMap(),
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current

    // Start with the provided modifier
    var finalModifier = modifier

    // Apply role from either predefined role or custom role
    when {
        role != null -> finalModifier = finalModifier.attribute("role", role.name.lowercase())
        customRole != null -> finalModifier = finalModifier.attribute("role", customRole)
    }

    // Apply label if provided
    if (label != null) {
        finalModifier = finalModifier.attribute("aria-label", label)
    }

    // Apply relationships
    relations.forEach { (relation, targetId) ->
        finalModifier = finalModifier.attribute("aria-$relation", targetId)
    }

    // Render the element with the accessibility attributes
    renderer.renderBox(modifier = finalModifier) {
        content()
    }
}

/**
 * Extension function on Modifier to add an HTML attribute
 */
// private fun Modifier.attribute(name: String, value: String): Modifier {
//    return this.style("__attr:$name", value)
// } 