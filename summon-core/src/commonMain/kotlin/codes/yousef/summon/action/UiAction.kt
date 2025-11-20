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
}
