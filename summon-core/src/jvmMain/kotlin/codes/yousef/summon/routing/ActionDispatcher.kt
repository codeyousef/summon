package codes.yousef.summon.routing

import codes.yousef.summon.action.UiAction
import codes.yousef.summon.state.UiState
import kotlinx.serialization.json.Json

/** Contract for action handler. */
interface ActionHandler {
    /**
     * Executes the handle operation.
     *
     * @param action The action value.
     * @param currentState The current state value.
     * @return The resulting value.
     */
    suspend fun handle(action: UiAction, currentState: UiState): UiState
}

/** Provides action dispatcher operations. */
object ActionDispatcher {
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Executes the dispatch operation.
     *
     * @param actionJson The action json value.
     * @param currentState The current state value.
     * @param handler The handler value.
     * @return The resulting value.
     */
    suspend fun dispatch(actionJson: String, currentState: UiState, handler: ActionHandler): UiState {
        val action = json.decodeFromString<UiAction>(actionJson)
        return handler.handle(action, currentState)
    }
}
