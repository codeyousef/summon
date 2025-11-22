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
     * Navigates to a new URL.
     */
    @Serializable
    @SerialName("nav")
    data class Navigate(val url: String) : UiAction()

    /**
     * Executes a Remote Procedure Call (RPC) on the server.
     */
    @Serializable
    @SerialName("rpc")
    data class ServerRpc(
        val endpoint: String,
        val payload: JsonElement,
        val optimisticUpdate: JsonElement? = null
    ) : UiAction()

    /**
     * Toggles the visibility of a DOM element on the client side.
     * This avoids a server round-trip for simple UI interactions like menus.
     */
    @Serializable
    @SerialName("toggle")
    data class ToggleVisibility(val targetId: String) : UiAction()
}
