package code.yousef.example.springboot

import code.yousef.example.springboot.pages.TodoFilter

/**
 * Registry for mapping callback IDs to actions that should be performed
 * when the callback is executed.
 */
object ActionRegistry {
    // Thread-safe map to handle concurrent access during server-side rendering
    private val actions = mutableMapOf<String, ActionType>()
    
    // Store a snapshot of registered actions for hydration
    private val hydrationSnapshot = mutableMapOf<String, ActionType>()
    
    /**
     * Associates a callback ID with an action type
     */
    fun registerAction(callbackId: String, action: ActionType) {
        // println("DEBUG: ActionRegistry.registerAction($callbackId, $action)")
        actions[callbackId] = action
        hydrationSnapshot[callbackId] = action // Keep for hydration
        // println("DEBUG: ActionRegistry now has ${actions.size} actions")
    }
    
    /**
     * Gets the action associated with a callback ID
     */
    fun getAction(callbackId: String): ActionType? {
        val action = actions[callbackId]
        // println("DEBUG: ActionRegistry.getAction($callbackId) = $action (registry has ${actions.size} actions)")
        return action
    }
    
    /**
     * Gets actions for hydration (snapshot)
     */
    fun getHydrationActions(): Map<String, ActionType> {
        return hydrationSnapshot.toMap()
    }
    
    /**
     * Clears all registered actions
     */
    fun clear() {
        actions.clear()
        // Don't clear hydration snapshot - it's needed for client callbacks
    }
}

/**
 * Types of actions that can be performed by callbacks
 */
sealed class ActionType {
    object ToggleTheme : ActionType()
    object ToggleLanguage : ActionType()
    object Logout : ActionType()
    data class AddTodo(val text: String) : ActionType()
    data class DeleteTodo(val todoId: Long) : ActionType()
    object ClearCompleted : ActionType()
    data class SetFilter(val filter: TodoFilter) : ActionType()
    
    // Authentication actions
    data class Login(val username: String, val password: String, val rememberMe: Boolean) : ActionType()
    data class Register(val email: String, val username: String, val password: String) : ActionType()
    data class ToggleAuthMode(val currentMode: String) : ActionType()
}