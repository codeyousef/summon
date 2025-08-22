package code.yousef.example.springboot

/**
 * Registry for mapping callback IDs to actions that should be performed
 * when the callback is executed.
 */
object ActionRegistry {
    private val actions = mutableMapOf<String, ActionType>()
    
    /**
     * Associates a callback ID with an action type
     */
    fun registerAction(callbackId: String, action: ActionType) {
        actions[callbackId] = action
    }
    
    /**
     * Gets the action associated with a callback ID
     */
    fun getAction(callbackId: String): ActionType? {
        return actions[callbackId]
    }
    
    /**
     * Clears all registered actions
     */
    fun clear() {
        actions.clear()
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
}