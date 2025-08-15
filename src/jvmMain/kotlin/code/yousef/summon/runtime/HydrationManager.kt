package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import code.yousef.summon.state.MutableState
import code.yousef.summon.state.mutableStateOf
import java.util.concurrent.atomic.AtomicLong

/**
 * JVM implementation of HydrationManager.
 * On the server side, this primarily generates hydration metadata that will be used by the client.
 */
actual class HydrationManager {
    private val componentIdCounter = AtomicLong(0)
    private val registeredComponents = mutableMapOf<String, HydrationInfo>()

    actual fun registerComponent(
        elementId: String,
        componentType: String,
        initialState: Map<String, Any?>,
        composable: @Composable () -> Unit
    ) {
        registeredComponents[elementId] = HydrationInfo(elementId, componentType, initialState, composable)
    }

    actual fun hydrateAll() {
        // No-op on server side - hydration happens client-side
    }

    actual fun hydrateComponent(elementId: String): Boolean {
        // On server side, just check if component is registered
        return registeredComponents.containsKey(elementId)
    }

    actual fun <T> restoreState(componentId: String, stateKey: String, initialValue: T): MutableState<T> {
        // On server side, just return a regular mutableStateOf
        // The client-side implementation will handle actual state restoration
        return mutableStateOf(initialValue)
    }

    actual fun generateComponentId(componentType: String): String {
        val id = componentIdCounter.incrementAndGet()
        return "${componentType}-${id}"
    }

    actual fun clear() {
        registeredComponents.clear()
        componentIdCounter.set(0)
    }

    /**
     * Gets all registered components for serialization to client.
     * This is used by the server-side renderer to generate hydration metadata.
     */
    fun getRegisteredComponents(): Map<String, HydrationInfo> {
        return registeredComponents.toMap()
    }

    /**
     * Generates hydration script data for the client side.
     * This includes component registrations and initial state.
     */
    fun generateHydrationData(): String {
        val components = registeredComponents.values.map { info ->
            mapOf(
                "elementId" to info.elementId,
                "componentType" to info.componentType,
                "initialState" to info.initialState
            )
        }

        // In a real implementation, you'd use a proper JSON serializer
        // For now, we'll create a simple JavaScript object literal
        val componentsJson = components.joinToString(",\n") { component ->
            val stateJson = component["initialState"].let { state ->
                if (state is Map<*, *>) {
                    state.entries.joinToString(",") { "\"${it.key}\":${formatValue(it.value)}" }
                } else ""
            }
            """
            {
                "elementId": "${component["elementId"]}",
                "componentType": "${component["componentType"]}",
                "initialState": {$stateJson}
            }""".trimIndent()
        }

        return "[$componentsJson]"
    }

    private fun formatValue(value: Any?): String {
        return when (value) {
            is String -> "\"$value\""
            is Number -> value.toString()
            is Boolean -> value.toString()
            null -> "null"
            else -> "\"$value\""
        }
    }
}