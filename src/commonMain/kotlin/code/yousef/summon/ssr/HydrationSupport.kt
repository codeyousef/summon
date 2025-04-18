package code.yousef.summon.ssr

import code.yousef.summon.annotation.Composable

// The HydrationStrategy enum and HydrationSupport interface have been moved to ServerSideRendering.kt
// to avoid duplication.

/**
 * Utilities to support client-side hydration of server-rendered content.
 */
object HydrationUtils {

    /**
     * Generates a hydration script for client-side reactivation.
     * This serializes initial state and component structure.
     *
     * @param rootComposable The root composable to generate hydration data for
     * @param initialState Optional initial state for hydration
     * @param strategy The hydration strategy to use
     * @return A script tag with hydration data
     */
    fun generateHydrationScript(
        rootComposable: @Composable () -> Unit,
        initialState: Map<String, Any?> = emptyMap(),
        strategy: HydrationStrategy = HydrationStrategy.FULL
    ): String {
        // Create a hydration context to track components
        val context = HydrationContext()

        // Track the composition
        context.trackComposition { rootComposable() }

        // Generate hydration data
        val componentData = context.generateHydrationData(strategy)

        // Serialize initial state
        val stateData = serializeInitialState(initialState)

        // Combine component data and state data
        val hydrationData = """
            {
                "version": 1,
                "strategy": "${strategy.name}",
                "timestamp": ${getCurrentTimestamp()},
                "components": $componentData,
                "initialState": $stateData
            }
        """.trimIndent()

        return """
            <script id="__SUMMON_HYDRATION_DATA__" type="application/json">
                $hydrationData
            </script>
            <script>
                window.__SUMMON_HYDRATION_READY = true;
                document.dispatchEvent(new CustomEvent('summon:hydration-ready'));
            </script>
        """.trimIndent()
    }

    /**
     * Get current timestamp in a platform-independent way
     */
    private fun getCurrentTimestamp(): Long {
        return kotlin.time.TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds
    }

    /**
     * Serialize initial state to JSON
     */
    private fun serializeInitialState(state: Map<String, Any?>): String {
        if (state.isEmpty()) return "{}"

        return buildString {
            append("{")
            state.entries.forEachIndexed { index, (key, value) ->
                if (index > 0) append(",")
                append("\"$key\":")
                append(serializeValue(value))
            }
            append("}")
        }
    }

    /**
     * Serialize a value to JSON
     */
    private fun serializeValue(value: Any?): String {
        return when (value) {
            null -> "null"
            is String -> "\"${escapeJsonString(value)}\""
            is Number, is Boolean -> value.toString()
            is Map<*, *> -> {
                val map = value.entries.associate { 
                    (it.key as? String ?: it.key.toString()) to it.value 
                }
                buildString {
                    append("{")
                    map.entries.forEachIndexed { index, (key, mapValue) ->
                        if (index > 0) append(",")
                        append("\"$key\":")
                        append(serializeValue(mapValue))
                    }
                    append("}")
                }
            }
            is List<*> -> {
                buildString {
                    append("[")
                    value.forEachIndexed { index, item ->
                        if (index > 0) append(",")
                        append(serializeValue(item))
                    }
                    append("]")
                }
            }
            is Array<*> -> serializeValue(value.toList())
            else -> "\"${escapeJsonString(value.toString())}\""
        }
    }

    /**
     * Escape special characters in JSON strings
     */
    private fun escapeJsonString(str: String): String {
        return str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\b", "\\b")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    /**
     * Placeholder function for initiating client-side hydration.
     * This would typically run on the client, find the server-rendered markup,
     * parse the hydration data, and attach the client-side composition.
     */
    fun hydrateClient(rootElementId: String, rootComposable: @Composable () -> Unit) {
        println("HydrationUtils.hydrateClient called for element '$rootElementId' (not implemented).")
    }
}

// StandardHydrationSupport has been moved to its own file to avoid duplication
// See StandardHydrationSupport.kt

/**
 * Context for tracking component information during composition for hydration
 */
class HydrationContext {
    // Track component instances, state, and event handlers
    private val components = mutableListOf<HydrationComponentInfo>()
    private val currentPath = mutableListOf<String>()

    /**
     * Track a composition for hydration
     * 
     * @param content The composition content to track
     */
    fun trackComposition(content: () -> Unit) {
        try {
            // Start tracking at the root level
            currentPath.add("root")

            // Add a root component
            addComponent("root", "RootComponent")

            // Create a composition tracking context
            val trackingContext = CompositionTrackingContext()

            // Execute the content with tracking
            trackingContext.trackComposition {
                content()
            }

            // Process tracked components
            trackingContext.getTrackedComponents().forEach { componentInfo ->
                // Add each tracked component to our hydration context
                addComponent(
                    id = componentInfo.id,
                    type = componentInfo.type,
                    state = componentInfo.state,
                    props = componentInfo.props,
                    events = componentInfo.events
                )

                // Track parent-child relationships
                if (componentInfo.parent != null) {
                    addRelationship(componentInfo.parent, componentInfo.id)
                }
            }

            // Add standard components that might not be explicitly tracked
            addStandardComponents()

        } finally {
            // Ensure we clean up the path even if an exception occurs
            if (currentPath.isNotEmpty()) {
                currentPath.removeLast()
            }
        }
    }

    /**
     * Add standard components that are part of most compositions
     */
    private fun addStandardComponents() {
        // Add standard layout components if not already present
        if (!hasComponentOfType("Container")) {
            currentPath.add("layout")
            addComponent(
                id = "container-main",
                type = "Container",
                props = mapOf(
                    "width" to "100%",
                    "maxWidth" to "1200px",
                    "margin" to "0 auto"
                )
            )
            currentPath.removeLast()
        }

        // Add standard navigation components if not already present
        if (!hasComponentOfType("Navigation")) {
            currentPath.add("navigation")
            addComponent(
                id = "navigation-main",
                type = "Navigation",
                state = mapOf(
                    "currentPath" to "/"
                )
            )
            currentPath.removeLast()
        }
    }

    /**
     * Check if a component of the given type exists
     */
    private fun hasComponentOfType(type: String): Boolean {
        return components.any { it.type == type }
    }

    /**
     * Add a parent-child relationship between components
     */
    private fun addRelationship(parentId: String, childId: String) {
        // This would be used in a real implementation to track component hierarchy
        // For now, we'll just log it
        println("Adding relationship: $parentId -> $childId")
    }

    /**
     * Context for tracking component creation during composition
     */
    private class CompositionTrackingContext {
        private val trackedComponents = mutableListOf<TrackedComponent>()
        private var currentParent: String? = null

        /**
         * Track a composition, capturing component creation
         */
        fun trackComposition(content: () -> Unit) {
            try {
                // Execute the content, which would create components
                content()
            } catch (e: Exception) {
                println("Error during composition tracking: ${e.message}")
            }

            // In a real implementation, we would hook into the composition system
            // to track component creation. For now, we'll add some sample components.
            addSampleComponents()
        }

        /**
         * Add sample components for demonstration
         */
        private fun addSampleComponents() {
            // Add a header component
            val headerId = "header-main"
            trackedComponents.add(
                TrackedComponent(
                    id = headerId,
                    type = "Header",
                    parent = null,
                    props = mapOf(
                        "title" to "Summon Application",
                        "showNavigation" to true
                    )
                )
            )

            // Add content components
            val contentId = "content-main"
            trackedComponents.add(
                TrackedComponent(
                    id = contentId,
                    type = "Content",
                    parent = null,
                    state = mapOf(
                        "isLoading" to false,
                        "data" to mapOf(
                            "items" to listOf("Item 1", "Item 2", "Item 3")
                        )
                    ),
                    events = listOf("onClick", "onScroll")
                )
            )

            // Add child components
            for (i in 1..3) {
                trackedComponents.add(
                    TrackedComponent(
                        id = "item-$i",
                        type = "Item",
                        parent = contentId,
                        props = mapOf(
                            "text" to "Item $i",
                            "index" to i
                        ),
                        events = listOf("onClick")
                    )
                )
            }

            // Add a footer component
            trackedComponents.add(
                TrackedComponent(
                    id = "footer-main",
                    type = "Footer",
                    parent = null,
                    props = mapOf(
                        "copyright" to "© 2023 Summon",
                        "showSocialLinks" to true
                    )
                )
            )
        }

        /**
         * Get the tracked components
         */
        fun getTrackedComponents(): List<TrackedComponent> {
            return trackedComponents
        }
    }

    /**
     * Information about a tracked component
     */
    private data class TrackedComponent(
        val id: String,
        val type: String,
        val parent: String? = null,
        val state: Map<String, Any?> = emptyMap(),
        val props: Map<String, Any?> = emptyMap(),
        val events: List<String> = emptyList()
    )

    /**
     * Add a component to the tracked components list
     * 
     * @param id The component ID
     * @param type The component type
     * @param state Optional component state
     * @param props Optional component props
     * @param events Optional component events
     */
    private fun addComponent(
        id: String,
        type: String,
        state: Map<String, Any?> = emptyMap(),
        props: Map<String, Any?> = emptyMap(),
        events: List<String> = emptyList()
    ) {
        components.add(
            HydrationComponentInfo(
                id = id,
                type = type,
                path = currentPath.toList(),
                state = state,
                props = props,
                events = events
            )
        )
    }

    /**
     * Add mock components for demonstration purposes
     * In a real implementation, these would be added during composition
     */
    private fun addMockComponents() {
        // Add a container component
        currentPath.add("container")
        addComponent(
            id = "container-1",
            type = "Container",
            props = mapOf(
                "width" to "100%",
                "maxWidth" to "1200px",
                "margin" to "0 auto"
            )
        )

        // Add a header component
        currentPath.add("header")
        addComponent(
            id = "header-1",
            type = "Header",
            props = mapOf(
                "title" to "Summon Application",
                "showNavigation" to true
            )
        )
        currentPath.removeLast() // Remove header from path

        // Add a content component
        currentPath.add("content")
        addComponent(
            id = "content-1",
            type = "Content",
            state = mapOf(
                "isLoading" to false,
                "data" to mapOf(
                    "items" to listOf("Item 1", "Item 2", "Item 3")
                )
            ),
            events = listOf("onClick", "onScroll")
        )

        // Add child components to content
        currentPath.add("items")
        for (i in 1..3) {
            addComponent(
                id = "item-$i",
                type = "Item",
                props = mapOf(
                    "text" to "Item $i",
                    "index" to i
                ),
                events = listOf("onClick")
            )
        }
        currentPath.removeLast() // Remove items from path
        currentPath.removeLast() // Remove content from path

        // Add a footer component
        currentPath.add("footer")
        addComponent(
            id = "footer-1",
            type = "Footer",
            props = mapOf(
                "copyright" to "© 2023 Summon",
                "showSocialLinks" to true
            )
        )
        currentPath.removeLast() // Remove footer from path

        currentPath.removeLast() // Remove container from path
    }

    /**
     * Generate hydration data as JSON
     * 
     * @param strategy The hydration strategy to use
     * @return A JSON string with hydration data
     */
    fun generateHydrationData(strategy: HydrationStrategy): String {
        // Generate a JSON structure with component data
        return buildString {
            append("{")
            append("\"hydrationVersion\": 1,")
            append("\"strategy\": \"${strategy.name}\",")
            append("\"timestamp\": ${getCurrentTimestamp()},")
            append("\"components\": [")

            // Add component info
            components.forEachIndexed { index, component ->
                if (index > 0) append(",")
                append(component.toJson())
            }

            append("]")
            append("}")
        }
    }

    /**
     * Get current timestamp in a platform-independent way
     */
    private fun getCurrentTimestamp(): Long {
        return kotlin.time.TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds
    }

    /**
     * Information about a component for hydration
     */
    private data class HydrationComponentInfo(
        val id: String,
        val type: String,
        val path: List<String>,
        val state: Map<String, Any?> = emptyMap(),
        val props: Map<String, Any?> = emptyMap(),
        val events: List<String> = emptyList()
    ) {
        /**
         * Convert to JSON string
         */
        fun toJson(): String {
            return """
                {
                    "id": "$id",
                    "type": "$type",
                    "path": [${path.joinToString { "\"$it\"" }}],
                    "state": ${serializeMap(state)},
                    "props": ${serializeMap(props)},
                    "events": [${events.joinToString { "\"$it\"" }}]
                }
            """.trimIndent()
        }

        /**
         * Serialize a map to JSON
         */
        private fun serializeMap(map: Map<String, Any?>): String {
            if (map.isEmpty()) return "{}"

            return buildString {
                append("{")
                map.entries.forEachIndexed { index, (key, value) ->
                    if (index > 0) append(",")
                    append("\"$key\":")
                    when (value) {
                        null -> append("null")
                        is String -> append("\"$value\"")
                        is Number, is Boolean -> append(value)
                        else -> append("\"$value\"")
                    }
                }
                append("}")
            }
        }
    }
}

/**
 * Client-side hydration utilities for browser JavaScript environment
 */
object ClientHydration {
    /**
     * Initialize client-side hydration
     */
    fun initialize() {
        // In a JS environment, this would:
        // 1. Find the hydration data script
        // 2. Parse the hydration data
        // 3. Match DOM nodes with component data
        // 4. Reactivate components with their state
        // 5. Attach event handlers

        println("Client-side hydration initialized")
    }

    /**
     * Apply hydration to a specific node
     */
    fun hydrateNode(nodeId: String) {
        // Find the node and its hydration data
        // Reactivate it with the stored state

        println("Hydrated node: $nodeId")
    }

    /**
     * Find hydration data in the page
     */
    fun findHydrationData(): String? {
        // In a browser environment, this would find the script tag and get its content
        return null
    }
} 
