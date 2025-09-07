package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import code.yousef.summon.js.console
import code.yousef.summon.state.MutableState
import code.yousef.summon.state.mutableStateOf
import kotlinx.browser.document
import org.w3c.dom.Element

/**
 * JavaScript implementation of HydrationManager.
 * This handles the actual hydration of server-rendered components on the client side.
 */
actual class HydrationManager {
    private val registeredComponents = mutableMapOf<String, HydrationInfo>()
    private val hydratedComponents = mutableSetOf<String>()
    private val componentStates = mutableMapOf<String, MutableMap<String, MutableState<*>>>()
    private var componentCounter = 0

    actual fun registerComponent(
        elementId: String,
        componentType: String,
        initialState: Map<String, Any?>,
        composable: @Composable () -> Unit
    ) {
        registeredComponents[elementId] = HydrationInfo(elementId, componentType, initialState, composable)
    }

    actual fun hydrateAll() {
        registeredComponents.keys.forEach { elementId ->
            hydrateComponent(elementId)
        }
    }

    actual fun hydrateComponent(elementId: String): Boolean {
        if (hydratedComponents.contains(elementId)) {
            return true // Already hydrated
        }

        val component = registeredComponents[elementId] ?: return false
        val element = document.getElementById(elementId) ?: return false

        try {
            // Create a recomposer for this component
            val recomposer = RecomposerHolder.current()

            // Set up the platform renderer to target this specific element
            val renderer = PlatformRenderer()

            // Store the current parent and set it to our target element
            val originalBody = document.body
            js("window.currentParent = element")

            // Create a composer for this component
            val composer = recomposer.createComposer()

            try {
                // Clear the element's content first
                element.innerHTML = ""

                // Compose the component
                composer.compose(component.composable)

                // Mark as hydrated
                hydratedComponents.add(elementId)

                // Set up recomposition for this component
                recomposer.setCompositionRoot(component.composable)

                return true
            } finally {
                // Restore the original parent
                js("window.currentParent = originalBody")
            }
        } catch (e: Exception) {
            console.error("Failed to hydrate component $elementId: ${e.message}")
            return false
        }
    }

    actual fun <T> restoreState(componentId: String, stateKey: String, initialValue: T): MutableState<T> {
        // Get or create the state map for this component
        val componentStateMap = componentStates.getOrPut(componentId) { mutableMapOf() }

        // Check if we already have state for this key
        @Suppress("UNCHECKED_CAST")
        val existingState = componentStateMap[stateKey] as? MutableState<T>
        if (existingState != null) {
            return existingState
        }

        // Create new state with initial value
        val newState = mutableStateOf(initialValue)
        componentStateMap[stateKey] = newState

        return newState
    }

    actual fun generateComponentId(componentType: String): String {
        componentCounter++
        return "${componentType}-${componentCounter}"
    }

    actual fun clear() {
        registeredComponents.clear()
        hydratedComponents.clear()
        componentStates.clear()
        componentCounter = 0
    }

    /**
     * Hydrates components based on data attributes in the DOM.
     * This scans the document for elements with data-summon-component attributes.
     */
    fun hydrateFromDOM() {
        val elements = document.querySelectorAll("[data-summon-component]")
        for (i in 0 until elements.length) {
            val element = elements.item(i) as? Element ?: continue
            val componentType = element.getAttribute("data-summon-component") ?: continue
            val componentId = element.getAttribute("data-summon-id") ?: element.id

            if (componentId.isNotEmpty()) {
                // Try to find a registered component for this element
                val component = registeredComponents[componentId]
                if (component != null) {
                    hydrateComponent(componentId)
                } else {
                    // Create a basic component registration based on the DOM data
                    val initialStateJson = element.getAttribute("data-summon-state")
                    val initialState = if (initialStateJson != null) {
                        parseInitialState(initialStateJson)
                    } else {
                        emptyMap()
                    }

                    // For now, we'll need to handle specific component types
                    when (componentType) {
                        "counter" -> {
                            registerCounterComponent(componentId, initialState)
                            hydrateComponent(componentId)
                        }
                        // Add other component types as needed
                    }
                }
            }
        }
    }

    private fun parseInitialState(stateJson: String): Map<String, Any?> {
        // Simple JSON parsing - in a real implementation you'd use kotlinx.serialization
        return try {
            val parsed = js("JSON.parse(stateJson)")
            val result = mutableMapOf<String, Any?>()

            // Convert JS object to Kotlin map
            js(
                """
                for (var key in parsed) {
                    if (parsed.hasOwnProperty(key)) {
                        result.set(key, parsed[key]);
                    }
                }
            """
            )

            result.toMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun registerCounterComponent(componentId: String, initialState: Map<String, Any?>) {
        val initialValue = (initialState["value"] as? Number)?.toInt() ?: 0

        registerComponent(
            elementId = componentId,
            componentType = "counter",
            initialState = initialState
        ) {
            // This will be replaced with the actual CounterComponent implementation
            // that uses hydration-aware state management
        }
    }
}

/**
 * Global hydration manager instance for JavaScript runtime.
 */
val globalHydrationManager = HydrationManager()

/**
 * Initializes hydration when the DOM is ready.
 * This should be called automatically when the Summon library loads.
 */
fun initializeHydration() {
    if (js("document.readyState === 'loading'") as Boolean) {
        js("document.addEventListener('DOMContentLoaded', function() { initializeHydration(); })")
    } else {
        globalHydrationManager.hydrateFromDOM()
    }
}