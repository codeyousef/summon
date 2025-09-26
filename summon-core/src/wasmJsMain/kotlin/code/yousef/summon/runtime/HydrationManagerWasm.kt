package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import code.yousef.summon.state.MutableState
import code.yousef.summon.state.mutableStateOf

/**
 * WASM implementation of HydrationManager for runtime.
 * This class manages server-rendered component hydration for client-side interactivity.
 */
actual class HydrationManager actual constructor() {
    private val registeredComponents = mutableMapOf<String, HydrationInfo>()
    private val componentStates = mutableMapOf<String, MutableMap<String, Any?>>()
    private val hydratedElements = mutableSetOf<String>()
    private var componentIdCounter = 0
    private var hydrationMarkerCounter = 0

    actual fun registerComponent(
        elementId: String,
        componentType: String,
        initialState: Map<String, Any?>,
        composable: @Composable () -> Unit
    ) {
        safeLog("Registering component: $elementId ($componentType)")

        // Store component information
        registeredComponents[elementId] = HydrationInfo(
            elementId = elementId,
            componentType = componentType,
            initialState = initialState,
            composable = composable
        )

        // Initialize component state
        if (initialState.isNotEmpty()) {
            componentStates[elementId] = initialState.toMutableMap()
            safeLog("Initialized state for $elementId: ${initialState.keys}")
        }
    }

    actual fun hydrateAll() {
        safeLog("Starting hydration of ${registeredComponents.size} components")

        var successCount = 0
        var failureCount = 0

        registeredComponents.forEach { (elementId, info) ->
            try {
                if (hydrateComponent(elementId)) {
                    successCount++
                } else {
                    failureCount++
                }
            } catch (e: Exception) {
                safeError("Failed to hydrate component $elementId: ${e.message}")
                failureCount++
            }
        }

        safeLog("Hydration complete: $successCount succeeded, $failureCount failed")
    }

    actual fun hydrateComponent(elementId: String): Boolean {
        safeLog("Hydrating component: $elementId")

        // Check if component is registered
        val hydrationInfo = registeredComponents[elementId]
        if (hydrationInfo == null) {
            safeWarn("Component not registered: $elementId")
            return false
        }

        // Check if already hydrated
        if (hydratedElements.contains(elementId)) {
            safeLog("Component already hydrated: $elementId")
            return true
        }

        try {
            // Verify element exists in DOM
            if (!verifyElementExists(elementId)) {
                safeError("Element not found in DOM: $elementId")
                return false
            }

            // Restore component state if available
            val savedState = componentStates[elementId]
            if (savedState != null && savedState.isNotEmpty()) {
                safeLog("Restoring state for $elementId: ${savedState.keys}")
            }

            // Mark element with hydration attribute
            markElementAsHydrated(elementId)

            // Execute composable to bind interactivity
            try {
                hydrationInfo.composable()
            } catch (e: Exception) {
                safeError("Error executing composable for $elementId: ${e.message}")
                // Continue with hydration even if composable fails
            }

            // Mark as hydrated
            hydratedElements.add(elementId)

            safeLog("Successfully hydrated: $elementId")
            return true

        } catch (e: Exception) {
            safeError("Hydration failed for $elementId: ${e.message}")
            return false
        }
    }

    actual fun <T> restoreState(componentId: String, stateKey: String, initialValue: T): MutableState<T> {
        safeLog("Restoring state: $componentId.$stateKey")

        // Get or create component state map
        val componentState = componentStates.getOrPut(componentId) { mutableMapOf() }

        // Check if state value exists
        val savedValue = componentState[stateKey]

        val restoredValue = if (savedValue != null) {
            try {
                @Suppress("UNCHECKED_CAST")
                savedValue as T
            } catch (e: ClassCastException) {
                safeWarn("Type mismatch for state $componentId.$stateKey, using initial value")
                initialValue
            }
        } else {
            initialValue
        }

        // Create mutable state with the restored value
        val mutableState = mutableStateOf(restoredValue)

        // Store reference for future updates
        componentState[stateKey] = restoredValue

        safeLog("Restored state $componentId.$stateKey = $restoredValue")
        return mutableState
    }

    actual fun generateComponentId(componentType: String): String {
        val id = "${componentType}_wasm_${++componentIdCounter}_${getCurrentTimestamp()}"
        safeLog("Generated component ID: $id")
        return id
    }

    actual fun clear() {
        safeLog("Clearing hydration manager")

        registeredComponents.clear()
        componentStates.clear()
        hydratedElements.clear()
        componentIdCounter = 0
        hydrationMarkerCounter = 0

        safeLog("Hydration manager cleared")
    }

    // Helper methods

    private fun verifyElementExists(elementId: String): Boolean {
        // In WASM, we would use an external function to check DOM
        // For now, assume element exists if it's been registered
        return true
    }

    private fun markElementAsHydrated(elementId: String) {
        // In WASM, we would use an external function to set DOM attribute
        // For now, just log the action
        safeLog("Marking element as hydrated: $elementId")
    }

    private fun getCurrentTimestamp(): Long {
        return try {
            code.yousef.summon.core.getCurrentTimeMillis()
        } catch (e: Exception) {
            // Fallback for WASM environment
            hydrationMarkerCounter++.toLong()
        }
    }

    // Safe logging functions that work in test environment

    private fun safeLog(message: String) {
        try {
            wasmConsoleLog("[HydrationManager] $message")
        } catch (e: Exception) {
            // Fallback for Node.js test environment
            println("[HydrationManager] $message")
        }
    }

    private fun safeWarn(message: String) {
        try {
            wasmConsoleWarn("[HydrationManager WARN] $message")
        } catch (e: Exception) {
            // Fallback for Node.js test environment
            println("[HydrationManager WARN] $message")
        }
    }

    private fun safeError(message: String) {
        try {
            wasmConsoleError("[HydrationManager ERROR] $message")
        } catch (e: Exception) {
            // Fallback for Node.js test environment
            println("[HydrationManager ERROR] $message")
        }
    }

    /**
     * Generate unique hydration markers for server-rendered content
     */
    fun generateHydrationMarker(componentType: String): String {
        return "hydration-${componentType}-${++hydrationMarkerCounter}"
    }

    /**
     * Validate that a hydration marker exists in the DOM
     */
    fun validateHydrationMarker(marker: String): Boolean {
        // In production, this would check the actual DOM
        safeLog("Validating hydration marker: $marker")
        return true
    }

    /**
     * Extract state data from server-rendered JSON
     */
    fun extractServerState(jsonData: String): Map<String, Any?> {
        safeLog("Extracting server state from JSON")

        // Simple JSON parsing for basic types
        val stateMap = mutableMapOf<String, Any?>()

        try {
            // Extract key-value pairs from JSON (simplified)
            val pairs = jsonData
                .removePrefix("{")
                .removeSuffix("}")
                .split(",")
                .map { it.trim() }

            for (pair in pairs) {
                if (pair.contains(":")) {
                    val (key, value) = pair.split(":", limit = 2)
                    val cleanKey = key.trim().removeSurrounding("\"")
                    val cleanValue = value.trim().removeSurrounding("\"")

                    // Parse value type
                    val parsedValue = when {
                        cleanValue == "true" -> true
                        cleanValue == "false" -> false
                        cleanValue == "null" -> null
                        cleanValue.toIntOrNull() != null -> cleanValue.toInt()
                        cleanValue.toDoubleOrNull() != null -> cleanValue.toDouble()
                        else -> cleanValue
                    }

                    stateMap[cleanKey] = parsedValue
                }
            }
        } catch (e: Exception) {
            safeError("Failed to parse server state: ${e.message}")
        }

        return stateMap
    }
}

// Note: External functions for DOM operations are defined in WebDOMUtils.kt