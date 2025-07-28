package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import code.yousef.summon.state.MutableState

/**
 * Manages the hydration of server-rendered components for client-side interactivity.
 * This class bridges the gap between server-side rendered HTML and client-side Summon components.
 */
expect class HydrationManager() {
    
    /**
     * Registers a component for hydration with its DOM element ID and composable function.
     * @param elementId The ID of the DOM element to hydrate
     * @param componentType A unique identifier for the component type
     * @param initialState Any initial state data needed for hydration
     * @param composable The composable function to bind to the element
     */
    fun registerComponent(
        elementId: String, 
        componentType: String,
        initialState: Map<String, Any?> = emptyMap(),
        composable: @Composable () -> Unit
    )
    
    /**
     * Hydrates all registered components, making them interactive.
     * This should be called after the DOM is ready.
     */
    fun hydrateAll()
    
    /**
     * Hydrates a specific component by its element ID.
     * @param elementId The ID of the element to hydrate
     * @return true if the component was successfully hydrated, false if not found
     */
    fun hydrateComponent(elementId: String): Boolean
    
    /**
     * Restores state for a component during hydration.
     * @param componentId The unique ID of the component
     * @param stateKey The key identifying the state property
     * @param initialValue The initial value to set
     * @return A MutableState that will be kept in sync with the UI
     */
    fun <T> restoreState(componentId: String, stateKey: String, initialValue: T): MutableState<T>
    
    /**
     * Generates a unique component ID for hydration purposes.
     * @param componentType The type of component (e.g., "counter", "form")
     * @return A unique ID string
     */
    fun generateComponentId(componentType: String): String
    
    /**
     * Clears all registered components and their state.
     * Useful for testing or when re-initializing the application.
     */
    fun clear()
}

/**
 * Data class representing hydration information for a component.
 */
data class HydrationInfo(
    val elementId: String,
    val componentType: String,
    val initialState: Map<String, Any?>,
    val composable: @Composable () -> Unit
)