package codes.yousef.summon.action

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Represents a polymorphic action that can be dispatched from the client to the server.
 */
@Serializable
sealed class UiAction {
/**
 * Navigates to `url`.
 *
 * @property url navigation destination
 */
    @Serializable
    @SerialName("nav")
    data class Navigate(val url: String) : UiAction()

    /**
     * Executes a server RPC.
     *
     * @property endpoint server endpoint identifier
     * @property payload serialized request payload
     * @property optimisticUpdate optional client update applied before the response
     */
    @Serializable
    @SerialName("rpc")
    data class ServerRpc(
        val endpoint: String,
        val payload: JsonElement,
        val optimisticUpdate: JsonElement? = null
    ) : UiAction()

/**
 * Toggles a DOM element without a server round-trip.
 *
 * @property targetId target DOM element identifier
 */
    @Serializable
    @SerialName("toggle")
    data class ToggleVisibility(val targetId: String) : UiAction()
}
