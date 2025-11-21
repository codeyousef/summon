package codes.yousef.summon.routing

import codes.yousef.summon.action.UiAction
import codes.yousef.summon.state.UiState
import kotlinx.serialization.json.Json

interface ActionHandler {
    suspend fun handle(action: UiAction, currentState: UiState): UiState
}

object ActionDispatcher {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun dispatch(actionJson: String, currentState: UiState, handler: ActionHandler): UiState {
        val action = json.decodeFromString<UiAction>(actionJson)
        return handler.handle(action, currentState)
    }
}
