package codes.yousef.summon.core.registry

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.remember
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock

/**
 * JSON block representation for dynamic rendering.
 *
 * @property type The component type key registered in the [ComponentRegistry]
 * @property props The properties map to pass to the component factory
 * @property children Optional list of child blocks for container components
 */
data class JsonBlock(
    val type: String,
    val props: Map<String, Any> = emptyMap(),
    val children: List<JsonBlock> = emptyList()
)

/**
 * Component factory type that takes props and returns a Composable.
 */
typealias ComponentFactory = (props: Map<String, Any>) -> @Composable () -> Unit

/**
 * A thread-safe singleton registry for mapping component type keys to their factories.
 *
 * The ComponentRegistry enables dynamic component creation from serialized JSON trees,
 * making it possible to create visual builders and dynamic UI generation.
 *
 * ## Features
 *
 * - **Thread-Safe**: Uses a reentrant lock for safe concurrent access
 * - **Hot Reload Support**: Allows overwriting existing registrations with a warning
 * - **Fallback Component**: Returns a visual error component for missing registrations
 *
 * ## Usage
 *
 * ```kotlin
 * // Register components
 * ComponentRegistry.register("text") { props ->
 *     { Text(text = props["text"] as? String ?: "") }
 * }
 *
 * ComponentRegistry.register("button") { props ->
 *     { Button(
 *         onClick = props["onClick"] as? (() -> Unit) ?: {},
 *         label = props["label"] as? String ?: "Button"
 *     ) }
 * }
 *
 * // Retrieve and use
 * val factory = ComponentRegistry.get("text")
 * val composable = factory(mapOf("text" to "Hello"))
 * ```
 *
 * @since 1.0.0
 */
object ComponentRegistry {
    private val lock = ReentrantLock()
    private val registry = mutableMapOf<String, ComponentFactory>()
    
    /**
     * Registers a component factory with the given key.
     *
     * If a factory already exists for the key, it will be overwritten
     * and a warning will be logged (useful for hot reload scenarios).
     *
     * @param key The unique identifier for the component type
     * @param factory The factory function that creates composables from props
     */
    fun register(key: String, factory: ComponentFactory) {
        lock.withLock {
            if (registry.containsKey(key)) {
                println("[ComponentRegistry] Warning: Overwriting existing registration for key '$key'")
            }
            registry[key] = factory
        }
    }
    
    /**
     * Retrieves a component factory by key.
     *
     * If the key is not found, returns a [FallbackComponent] factory that renders
     * a visible error indicator (red dashed border) in the DOM.
     *
     * @param key The component type key to look up
     * @return The registered factory, or a FallbackComponent factory if not found
     */
    fun get(key: String): ComponentFactory {
        return lock.withLock {
            registry[key] ?: { props -> 
                { FallbackComponent(key, props) }
            }
        }
    }
    
    /**
     * Checks if a component is registered under the given key.
     *
     * @param key The component type key to check
     * @return true if a factory is registered, false otherwise
     */
    fun isRegistered(key: String): Boolean {
        return lock.withLock {
            registry.containsKey(key)
        }
    }
    
    /**
     * Removes a component registration.
     *
     * @param key The component type key to remove
     * @return true if a factory was removed, false if the key wasn't registered
     */
    fun unregister(key: String): Boolean {
        return lock.withLock {
            registry.remove(key) != null
        }
    }
    
    /**
     * Clears all component registrations.
     * Useful for testing or complete reinitialization.
     */
    fun clear() {
        lock.withLock {
            registry.clear()
        }
    }
    
    /**
     * Returns the number of registered components.
     */
    fun size(): Int {
        return lock.withLock {
            registry.size
        }
    }
    
    /**
     * Returns all registered component keys.
     */
    fun keys(): Set<String> {
        return lock.withLock {
            registry.keys.toSet()
        }
    }
}

/**
 * A visual error component that displays when a component type is not found in the registry.
 *
 * Renders a container with a red dashed border to make missing components immediately visible
 * during development.
 *
 * @param missingKey The component key that was not found
 * @param props The props that were passed to the missing component
 */
@Composable
fun FallbackComponent(missingKey: String, props: Map<String, Any>) {
    val renderer = LocalPlatformRenderer.current
    
    val errorModifier = Modifier()
        .style("border", "2px dashed #ff0000")
        .style("padding", "8px")
        .style("margin", "4px")
        .style("background-color", "rgba(255, 0, 0, 0.1)")
        .style("color", "#cc0000")
        .style("font-family", "monospace")
        .style("font-size", "12px")
        .dataAttribute("fallback-component", "true")
        .dataAttribute("missing-key", missingKey)
    
    renderer.renderDiv(modifier = errorModifier) {
        renderer.renderText(
            text = "⚠️ Missing component: '$missingKey'",
            modifier = Modifier().style("font-weight", "bold")
        )
        if (props.isNotEmpty()) {
            renderer.renderDiv(modifier = Modifier().style("margin-top", "4px")) {
                renderer.renderText(
                    text = "Props: ${props.keys.joinToString(", ")}",
                    modifier = Modifier().style("font-size", "10px")
                )
            }
        }
    }
}

/**
 * Marker interface for identifying FallbackComponent instances.
 * Used for testing to verify fallback behavior.
 */
interface FallbackComponentMarker
