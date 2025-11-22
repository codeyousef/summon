package codes.yousef.summon.modifier

import codes.yousef.summon.action.UiAction
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Attaches a [UiAction] to the component, which will be serialized
 * into a `data-action` attribute for client-side handling.
 *
 * @param action The action to dispatch when the component is interacted with.
 * @return A new Modifier with the action attribute.
 */
fun Modifier.action(action: UiAction): Modifier {
    val json = Json { encodeDefaults = true }
    val actionJson = json.encodeToString(action)
    return this.attribute("data-action", actionJson)
}
