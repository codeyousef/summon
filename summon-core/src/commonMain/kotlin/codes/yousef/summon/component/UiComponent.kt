package codes.yousef.summon.component

import codes.yousef.summon.action.UiAction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Represents an atomic UI component in the Server-Driven UI tree.
 */
@Serializable
sealed class UiComponent {
/**
 * Ordered container.
 *
 * @property children child components
 */
    @Serializable
    @SerialName("box")
    data class Box(val children: List<UiComponent>) : UiComponent()

/**
 * Plain text.
 *
 * @property content visible content
 */
    @Serializable
    @SerialName("txt")
    data class Text(val content: String) : UiComponent()

/**
 * Action button.
 *
 * @property label visible label
 * @property action serialized activation action
 */
    @Serializable
    @SerialName("btn")
    data class Button(val label: String, val action: UiAction) : UiComponent()

/**
 * Explicit external widget.
 *
 * @property scriptUrl widget script URL
 * @property params serialized widget parameters
 */
    @Serializable
    @SerialName("ext")
    data class ExternalWidget(val scriptUrl: String, val params: JsonObject) : UiComponent()
}
