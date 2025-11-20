package codes.yousef.summon.hydration

import codes.yousef.summon.action.UiAction
import kotlinx.serialization.json.Json
import kotlinx.browser.window

object ClientDispatcher {
    private val json = Json { ignoreUnknownKeys = true }

    fun dispatch(actionJson: String) {
        try {
            val action = json.decodeFromString<UiAction>(actionJson)
            dispatch(action)
        } catch (e: Exception) {
            console.error("Failed to dispatch action", e)
        }
    }

    fun dispatch(action: UiAction) {
        when (action) {
            is UiAction.Navigate -> {
                window.location.href = action.url
            }
            is UiAction.ServerRpc -> {
                console.log("RPC: ${action.endpoint}", action.payload)
                // TODO: Implement actual fetch to /summon/dispatch
            }
        }
    }
}
