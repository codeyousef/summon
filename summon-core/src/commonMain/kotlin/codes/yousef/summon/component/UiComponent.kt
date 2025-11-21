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
    @Serializable
    @SerialName("box")
    data class Box(val children: List<UiComponent>) : UiComponent()

    @Serializable
    @SerialName("txt")
    data class Text(val content: String) : UiComponent()

    @Serializable
    @SerialName("btn")
    data class Button(val label: String, val action: UiAction) : UiComponent()
    
    @Serializable
    @SerialName("ext")
    data class ExternalWidget(val scriptUrl: String, val params: JsonObject) : UiComponent()
}
