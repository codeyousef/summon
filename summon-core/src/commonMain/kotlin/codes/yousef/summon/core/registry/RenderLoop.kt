package codes.yousef.summon.core.registry

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf

/**
 * Recursive render loop for transforming JSON block trees into live DOM trees.
 *
 * This object provides the core logic for the Dynamic Engine, enabling visual builder
 * capabilities and JSON-to-DOM transformation.
 *
 * ## Features
 *
 * - **Recursive Rendering**: Handles nested component trees of any depth
 * - **Granular Error Boundaries**: Wraps each node in try-catch to prevent cascade failures
 * - **Registry Integration**: Uses [ComponentRegistry] for component lookups
 *
 * ## Usage
 *
 * ```kotlin
 * // Define a JSON tree
 * val tree = listOf(
 *     JsonBlock(
 *         type = "column",
 *         props = mapOf("modifier" to Modifier().padding("16px")),
 *         children = listOf(
 *             JsonBlock(type = "text", props = mapOf("text" to "Hello")),
 *             JsonBlock(type = "text", props = mapOf("text" to "World"))
 *         )
 *     )
 * )
 *
 * // Render the tree
 * @Composable
 * fun App() {
 *     RenderLoop.render(tree)
 * }
 * ```
 *
 * @since 1.0.0
 */
object RenderLoop {
    
    /**
     * Renders a list of JSON blocks into the composition.
     *
     * Each block is rendered with its own error boundary, ensuring that
     * a failure in one component doesn't bring down sibling components.
     *
     * @param blocks The list of JSON blocks to render
     * @param parentId Optional parent ID for debugging and selection
     */
    @Composable
    fun render(blocks: List<JsonBlock>, parentId: String? = null) {
        val renderer = LocalPlatformRenderer.current
        
        blocks.forEachIndexed { index, block ->
            val nodeId = "${parentId ?: "root"}-$index"
            renderWithErrorBoundary(block, nodeId)
        }
    }
    
    /**
     * Renders a single JSON block with error boundary protection.
     *
     * If the component throws during instantiation or rendering, an [ErrorComponent]
     * is rendered in its place, showing the error details without crashing the parent.
     *
     * @param block The JSON block to render
     * @param nodeId Unique identifier for this node (used for error tracking)
     */
    @Composable
    private fun renderWithErrorBoundary(block: JsonBlock, nodeId: String) {
        val errorState = remember { mutableStateOf<Throwable?>(null) }
        
        if (errorState.value != null) {
            ErrorComponent(
                nodeId = nodeId,
                error = errorState.value!!,
                componentType = block.type
            )
        } else {
            try {
                renderBlock(block, nodeId)
            } catch (e: Throwable) {
                errorState.value = e
                ErrorComponent(
                    nodeId = nodeId,
                    error = e,
                    componentType = block.type
                )
            }
        }
    }
    
    /**
     * Renders a single JSON block by looking up its factory and executing it.
     *
     * For container components (those with children), the children are recursively
     * rendered within the component's content slot.
     *
     * @param block The JSON block to render
     * @param nodeId Unique identifier for this node
     */
    @Composable
    private fun renderBlock(block: JsonBlock, nodeId: String) {
        val factory = ComponentRegistry.get(block.type)
        
        // Create props with children rendering capability
        val propsWithChildren = if (block.children.isNotEmpty()) {
            block.props + mapOf(
                "__children" to block.children,
                "__nodeId" to nodeId,
                "__renderChildren" to { ->
                    render(block.children, nodeId)
                }
            )
        } else {
            block.props + mapOf("__nodeId" to nodeId)
        }
        
        val composable = factory(propsWithChildren)
        composable()
    }
}

/**
 * Error component displayed when a component fails during rendering.
 *
 * Shows the error information with a distinctive red error style to make
 * failures visible during development.
 *
 * @param nodeId The ID of the failed node
 * @param error The exception that was thrown
 * @param componentType The type of component that failed
 */
@Composable
fun ErrorComponent(nodeId: String, error: Throwable, componentType: String) {
    val renderer = LocalPlatformRenderer.current
    
    val errorModifier = Modifier()
        .style("border", "2px solid #ff0000")
        .style("background-color", "rgba(255, 0, 0, 0.15)")
        .style("padding", "12px")
        .style("margin", "4px")
        .style("border-radius", "4px")
        .style("font-family", "monospace")
        .style("font-size", "12px")
        .dataAttribute("error-boundary", "true")
        .dataAttribute("node-id", nodeId)
        .dataAttribute("component-type", componentType)
    
    renderer.renderDiv(modifier = errorModifier) {
        // Error header
        renderer.renderDiv(
            modifier = Modifier()
                .style("color", "#cc0000")
                .style("font-weight", "bold")
                .style("margin-bottom", "8px")
        ) {
            renderer.renderText(
                text = "❌ Error in component: '$componentType'",
                modifier = Modifier()
            )
        }
        
        // Error message
        renderer.renderDiv(
            modifier = Modifier()
                .style("color", "#660000")
                .style("white-space", "pre-wrap")
                .style("word-break", "break-word")
        ) {
            renderer.renderText(
                text = error.message ?: "Unknown error",
                modifier = Modifier()
            )
        }
        
        // Stack trace summary (first 3 lines)
        val stackSummary = error.stackTraceToString()
            .lines()
            .take(4)
            .joinToString("\n")
        
        if (stackSummary.isNotEmpty()) {
            renderer.renderDiv(
                modifier = Modifier()
                    .style("margin-top", "8px")
                    .style("padding", "8px")
                    .style("background-color", "rgba(0, 0, 0, 0.05)")
                    .style("font-size", "10px")
                    .style("color", "#666")
                    .style("overflow", "auto")
                    .style("max-height", "100px")
            ) {
                renderer.renderText(
                    text = stackSummary,
                    modifier = Modifier().style("white-space", "pre")
                )
            }
        }
    }
}

/**
 * Extension function to render children from within a component factory.
 *
 * Use this in your component factories to render nested children:
 *
 * ```kotlin
 * ComponentRegistry.register("column") { props ->
 *     {
 *         Column(modifier = props.getModifier("modifier")) {
 *             props.renderChildren()
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun Map<String, Any>.renderChildren() {
    @Suppress("UNCHECKED_CAST")
    val renderFn = this["__renderChildren"] as? @Composable () -> Unit
    renderFn?.invoke()
}

/**
 * Extension to safely get a Modifier from props.
 */
fun Map<String, Any>.getModifier(key: String): Modifier {
    return this[key] as? Modifier ?: Modifier()
}

/**
 * Extension to safely get a String from props.
 */
fun Map<String, Any>.getString(key: String, default: String = ""): String {
    return this[key] as? String ?: default
}

/**
 * Extension to safely get an Int from props.
 */
fun Map<String, Any>.getInt(key: String, default: Int = 0): Int {
    return (this[key] as? Number)?.toInt() ?: default
}

/**
 * Extension to safely get a Boolean from props.
 */
fun Map<String, Any>.getBoolean(key: String, default: Boolean = false): Boolean {
    return this[key] as? Boolean ?: default
}
