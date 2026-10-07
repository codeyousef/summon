package codes.yousef.summon.runtime

/**
 * WebAssembly implementation of DOM API for high-performance DOM manipulation.
 *
 * This implementation uses string-based element IDs instead of dynamic types
 * to comply with WASM's type restrictions while maintaining type safety.
 *
 * @since 0.3.3.0
 */
class WasmDOMAPI : DOMAPIContract {

    private val elementCache = mutableMapOf<String, WasmDOMElement>()
    private val eventHandlers = mutableMapOf<String, MutableList<(WasmDOMEvent) -> Unit>>()
    private var elementIdCounter = 0
    private var handlerIdCounter = 0

    /**
     * Creates element.
     *
     * @param tagName The tag name value.
     * @return The resulting value.
     */
    override fun createElement(tagName: String): DOMElement {
        val elementId = "wasm-element-${elementIdCounter++}"

        return try {
            val nativeElementId = wasmCreateElementById(tagName)
            wasmSetElementId(nativeElementId, elementId)

            val wasmElement = WasmDOMElement(
                tagName = tagName,
                id = elementId,
                nativeElementId = nativeElementId
            )

            elementCache[elementId] = wasmElement
            wasmElement
        } catch (e: Exception) {
            wasmConsoleError("Failed to create element: $tagName")
            throw WasmDOMException("Failed to create element: $tagName", e)
        }
    }

    /**
     * Sets text content.
     *
     * @param element The element value.
     * @param text The text value.
     */
    override fun setTextContent(element: DOMElement, text: String) {
        val wasmElement = element as? WasmDOMElement
            ?: throw WasmDOMException("Invalid element type")

        try {
            wasmSetElementTextContent(wasmElement.nativeElementId, text)
        } catch (e: Exception) {
            wasmConsoleError("Failed to set text content: $text")
            throw WasmDOMException("Failed to set text content", e)
        }
    }

    /**
     * Sets attribute.
     *
     * @param element The element value.
     * @param name Human-readable name.
     * @param value Value to process.
     */
    override fun setAttribute(element: DOMElement, name: String, value: String) {
        val wasmElement = element as? WasmDOMElement
            ?: throw WasmDOMException("Invalid element type")

        try {
            wasmSetElementAttribute(wasmElement.nativeElementId, name, value)
        } catch (e: Exception) {
            wasmConsoleError("Failed to set attribute: $name=$value")
            throw WasmDOMException("Failed to set attribute: $name", e)
        }
    }

    /**
     * Adds class.
     *
     * @param element The element value.
     * @param className The class name value.
     */
    override fun addClass(element: DOMElement, className: String) {
        val wasmElement = element as? WasmDOMElement
            ?: throw WasmDOMException("Invalid element type")

        try {
            wasmAddClassToElement(wasmElement.nativeElementId, className)
        } catch (e: Exception) {
            wasmConsoleError("Failed to add class: $className")
            throw WasmDOMException("Failed to add class: $className", e)
        }
    }

    /**
     * Removes class.
     *
     * @param element The element value.
     * @param className The class name value.
     */
    override fun removeClass(element: DOMElement, className: String) {
        val wasmElement = element as? WasmDOMElement
            ?: throw WasmDOMException("Invalid element type")

        try {
            wasmRemoveClassFromElement(wasmElement.nativeElementId, className)
        } catch (e: Exception) {
            wasmConsoleError("Failed to remove class: $className")
            throw WasmDOMException("Failed to remove class: $className", e)
        }
    }

    /**
     * Executes the append child operation.
     *
     * @param parent The parent value.
     * @param child The child value.
     */
    override fun appendChild(parent: DOMElement, child: DOMElement) {
        val wasmParent = parent as? WasmDOMElement
            ?: throw WasmDOMException("Invalid parent element type")
        val wasmChild = child as? WasmDOMElement
            ?: throw WasmDOMException("Invalid child element type")

        try {
            wasmAppendChildById(wasmParent.nativeElementId, wasmChild.nativeElementId)
        } catch (e: Exception) {
            wasmConsoleError("Failed to append child")
            throw WasmDOMException("Failed to append child", e)
        }
    }

    /**
     * Removes element.
     *
     * @param element The element value.
     */
    override fun removeElement(element: DOMElement) {
        val wasmElement = element as? WasmDOMElement
            ?: throw WasmDOMException("Invalid element type")

        try {
            wasmRemoveElementById(wasmElement.nativeElementId)
            elementCache.remove(wasmElement.id)

            // Clean up event handlers
            eventHandlers.remove("${wasmElement.id}:*")
        } catch (e: Exception) {
            wasmConsoleError("Failed to remove element")
            throw WasmDOMException("Failed to remove element", e)
        }
    }

    /**
     * Adds event listener.
     *
     * @param element The element value.
     * @param eventType The event type value.
     * @param handler The handler value.
     */
    override fun addEventListener(element: DOMElement, eventType: String, handler: (WasmDOMEvent) -> Unit) {
        val wasmElement = element as? WasmDOMElement
            ?: throw WasmDOMException("Invalid element type")

        try {
            val handlerKey = "${wasmElement.id}:$eventType"
            val handlerList = eventHandlers.getOrPut(handlerKey) { mutableListOf() }
            handlerList.add(handler)

            val handlerId = "handler-${handlerIdCounter++}"

            wasmAddEventListenerById(wasmElement.nativeElementId, eventType, handlerId)

            // Register callback for when this handler is triggered
            registerEventCallback(handlerId, eventType, wasmElement, handler)
        } catch (e: Exception) {
            wasmConsoleError("Failed to add event listener: $eventType")
            throw WasmDOMException("Failed to add event listener: $eventType", e)
        }
    }

    /**
     * Removes event listener.
     *
     * @param element The element value.
     * @param eventType The event type value.
     * @param handler The handler value.
     */
    override fun removeEventListener(element: DOMElement, eventType: String, handler: (WasmDOMEvent) -> Unit) {
        val wasmElement = element as? WasmDOMElement
            ?: throw WasmDOMException("Invalid element type")

        try {
            val handlerKey = "${wasmElement.id}:$eventType"
            val handlerList = eventHandlers[handlerKey]
            handlerList?.remove(handler)

            if (handlerList?.isEmpty() == true) {
                eventHandlers.remove(handlerKey)
                // Note: In a real implementation, we'd need to track handler IDs to remove them properly
            }
        } catch (e: Exception) {
            wasmConsoleError("Failed to remove event listener: $eventType")
            throw WasmDOMException("Failed to remove event listener: $eventType", e)
        }
    }

    /**
     * Finds element by hydration ID.
     *
     * @param markerId The marker id value.
     * @return The resulting value.
     */
    override fun findElementByHydrationId(markerId: String): DOMElement? {
        return try {
            val elementId = wasmQuerySelectorGetId("[data-summon-hydration=\"$markerId\"]")
            if (elementId == null) {
                return null
            }

            // Check if we already have this element cached
            elementCache.values.find { it.nativeElementId == elementId }?.let { return it }

            // Create wrapper for existing element
            val wasmElementId = "wasm-hydration-${elementIdCounter++}"
            wasmSetElementId(elementId, wasmElementId)

            val tagName = wasmGetElementTagName(elementId)?.lowercase() ?: "div"
            val wasmElement = WasmDOMElement(
                tagName = tagName,
                id = wasmElementId,
                nativeElementId = elementId
            )

            elementCache[wasmElementId] = wasmElement
            wasmElement
        } catch (e: Exception) {
            wasmConsoleError("Failed to find element by hydration ID: $markerId")
            null
        }
    }

    /**
     * Returns hydration ID.
     *
     * @param element The element value.
     * @return The resulting value.
     */
    override fun getHydrationId(element: DOMElement): String? {
        val wasmElement = element as? WasmDOMElement
            ?: return null

        return try {
            wasmGetElementAttribute(wasmElement.nativeElementId, "data-summon-hydration")
        } catch (e: Exception) {
            wasmConsoleError("Failed to get hydration ID")
            null
        }
    }

    /**
     * WASM-specific optimizations
     */

    fun batchDOMOperations(operations: List<() -> Unit>) {
        try {
            wasmStartBatch()
            operations.forEach { it() }
        } catch (e: Exception) {
            wasmConsoleError("Batch DOM operations failed")
            throw WasmDOMException("Batch DOM operations failed", e)
        } finally {
            wasmEndBatch()
        }
    }

    /**
     * Executes the measure performance operation.
     *
     * @param operation The operation value.
     * @param block Operation to execute.
     * @return The resulting value.
     */
    fun measurePerformance(operation: String, block: () -> Unit): Long {
        val startTime = wasmPerformanceNow().toLong()
        try {
            block()
        } finally {
            val endTime = wasmPerformanceNow().toLong()
            val duration = endTime - startTime
            wasmConsoleDebug("WASM DOM operation '$operation' took ${duration}ms")
            return duration
        }
    }

    /**
     * Returns memory usage.
     *
     * @return The resulting value.
     */
    fun getMemoryUsage(): WasmMemoryInfo {
        return try {
            val totalElements = elementCache.size
            val totalHandlers = eventHandlers.values.sumOf { it.size }

            WasmMemoryInfo(
                totalElements = totalElements,
                totalEventHandlers = totalHandlers,
                cacheSize = elementCache.size,
                timestamp = wasmPerformanceNow().toLong()
            )
        } catch (e: Exception) {
            wasmConsoleError("Failed to get memory usage")
            WasmMemoryInfo(0, 0, 0, wasmPerformanceNow().toLong())
        }
    }

    /** Clears cache. */
    fun clearCache() {
        try {
            elementCache.clear()
            eventHandlers.clear()
            elementIdCounter = 0
            handlerIdCounter = 0
        } catch (e: Exception) {
            wasmConsoleError("Failed to clear cache")
        }
    }

    /**
     * Creates element from native.
     *
     * @param nativeElementId The native element id value.
     * @return The resulting value.
     */
    fun createElementFromNative(nativeElementId: String): DOMElement {
        // Check if element is already cached
        elementCache.values.find { it.nativeElementId == nativeElementId }?.let { return it }

        // Create new wrapper
        val wasmElementId = "wasm-existing-${elementIdCounter++}"
        val tagName = wasmGetElementTagName(nativeElementId)?.lowercase() ?: "div"

        val wasmElement = WasmDOMElement(
            tagName = tagName,
            id = wasmElementId,
            nativeElementId = nativeElementId
        )

        elementCache[wasmElementId] = wasmElement
        return wasmElement
    }

    /**
     * Register a callback for an event handler. This would typically be implemented
     * with a global callback registry that JavaScript can invoke.
     */
    private fun registerEventCallback(
        handlerId: String,
        eventType: String,
        element: WasmDOMElement,
        handler: (WasmDOMEvent) -> Unit
    ) {
        // In a real implementation, this would register the callback with a global registry
        // that JavaScript can invoke when events occur. For now, we'll store it locally.
        // The actual event dispatch would need to be handled by the JavaScript bridge.
    }
}

/**
 * WASM-specific DOM element implementation using string IDs.

 * @property tagName The tag name value.
 * @property id Stable identifier.
 * @property nativeElementId The native element id value.
 */
class WasmDOMElement(
    override val tagName: String,
    override val id: String,
    val nativeElementId: String
) : DOMElement {

    /** The property declaration value. */
    override val className: String
        get() = wasmGetElementClassName(nativeElementId) ?: ""

    /** The property declaration value. */
    override val textContent: String?
        get() = wasmGetElementTextContent(nativeElementId)

    /**
     * Returns attribute.
     *
     * @param name Human-readable name.
     * @return The resulting value.
     */
    override fun getAttribute(name: String): String? {
        return wasmGetElementAttribute(nativeElementId, name)
    }

    /**
     * Sets attribute.
     *
     * @param name Human-readable name.
     * @param value Value to process.
     */
    override fun setAttribute(name: String, value: String) {
        check(wasmSetElementAttribute(nativeElementId, name, value)) { "Cannot update an element attribute" }
    }

    /**
     * Removes attribute.
     *
     * @param name Human-readable name.
     */
    override fun removeAttribute(name: String) {
        check(wasmRemoveElementAttribute(nativeElementId, name)) { "Cannot remove an element attribute" }
    }

    /**
     * Executes the append child operation.
     *
     * @param child The child value.
     */
    override fun appendChild(child: DOMElement) {
        val wasmChild = child as? WasmDOMElement
            ?: throw WasmDOMException("Invalid child element type")
        check(wasmAppendChildById(nativeElementId, wasmChild.nativeElementId)) { "Cannot append an element" }
    }

    /**
     * Removes child.
     *
     * @param child The child value.
     */
    override fun removeChild(child: DOMElement) {
        val wasmChild = child as? WasmDOMElement
            ?: throw WasmDOMException("Invalid child element type")
        check(wasmRemoveChildById(nativeElementId, wasmChild.nativeElementId)) { "Cannot remove an element" }
    }

    /**
     * Adds event listener.
     *
     * @param type The type value.
     * @param listener The listener value.
     */
    override fun addEventListener(type: String, listener: (event: Any) -> Unit) {
        val handlerId = "inline-handler-${wasmPerformanceNow().toLong()}"
        wasmAddEventListenerById(nativeElementId, type, handlerId)
        // Note: In real implementation, would need proper callback registration
    }

    /**
     * Removes event listener.
     *
     * @param type The type value.
     * @param listener The listener value.
     */
    override fun removeEventListener(type: String, listener: (event: Any) -> Unit) {
        // Note: This is a limitation - we can't easily track and remove specific listeners
        // without a more complex event handler management system
    }

    /**
     * Executes the query selector operation.
     *
     * @param selector The selector value.
     * @return The resulting value.
     */
    override fun querySelector(selector: String): DOMElement? {
        val elementId = wasmQuerySelectorGetId(selector)
        return if (elementId != null) {
            val tagName = wasmGetElementTagName(elementId) ?: "div"
            WasmDOMElement(tagName, elementId, elementId)
        } else null
    }

    /**
     * Executes the query selector all operation.
     *
     * @param selector The selector value.
     * @return The resulting value.
     */
    override fun querySelectorAll(selector: String): List<DOMElement> {
        val elementIds = wasmQuerySelectorAllGetIds(selector)
        return if (elementIds.isNotEmpty()) {
            elementIds.split(",").mapNotNull { elementId ->
                if (elementId.isNotBlank()) {
                    val tagName = wasmGetElementTagName(elementId) ?: "div"
                    WasmDOMElement(tagName, elementId, elementId)
                } else null
            }
        } else emptyList()
    }

    /**
     * Returns whether this value has attribute.
     *
     * @param name Human-readable name.
     * @return The resulting value.
     */
    override fun hasAttribute(name: String): Boolean {
        return wasmGetElementAttribute(nativeElementId, name) != null
    }

    // Additional methods with override - these ARE in the DOMElement interface apparently
    /**
     * Executes the insert before operation.
     *
     * @param newChild The new child value.
     * @param referenceChild The reference child value.
     */
    override fun insertBefore(newChild: DOMElement, referenceChild: DOMElement?) {
        // Simplified implementation - would need proper insertBefore support
        appendChild(newChild)
    }

    /**
     * Returns bounding client rect.
     *
     * @return The resulting value.
     */
    override fun getBoundingClientRect(): DOMRect {
        // Simplified implementation - would need proper getBoundingClientRect support
        return DOMRect(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    }

    /** Moves focus to this element. */
    override fun focus() {
        // Would need wasmFocusElement implementation
    }

    /** Removes focus from this element. */
    override fun blur() {
        // Would need wasmBlurElement implementation
    }

    /** Activates this element. */
    override fun click() {
        // Would need wasmClickElement implementation
    }

    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = "WasmDOMElement(tagName=$tagName, id=$id)"
}

/**
 * WASM-specific simple event implementation using Any for compatibility.
 * Since runtime.DOMElement uses (event: Any) -> Unit, we use a simple data class.

 * @property type The type value.
 * @property handlerId The handler id value.
 * @property targetId The target id value.
 */
data class WasmDOMEvent(
    val type: String,
    val handlerId: String,
    val targetId: String
)

/**
 * WASM DOM exception for error handling.
 */
class WasmDOMException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Memory usage information for WASM DOM operations.

 * @property totalElements The total elements value.
 * @property totalEventHandlers The total event handlers value.
 * @property cacheSize The cache size value.
 * @property timestamp The timestamp value.
 */
data class WasmMemoryInfo(
    val totalElements: Int,
    val totalEventHandlers: Int,
    val cacheSize: Int,
    val timestamp: Long
)

/**
 * Contract interface for DOM API implementations.
 */
interface DOMAPIContract {
    /**
     * Creates element.
     *
     * @param tagName The tag name value.
     * @return The resulting value.
     */
    fun createElement(tagName: String): DOMElement
    /**
     * Sets text content.
     *
     * @param element The element value.
     * @param text The text value.
     */
    fun setTextContent(element: DOMElement, text: String)
    /**
     * Sets attribute.
     *
     * @param element The element value.
     * @param name Human-readable name.
     * @param value Value to process.
     */
    fun setAttribute(element: DOMElement, name: String, value: String)
    /**
     * Adds class.
     *
     * @param element The element value.
     * @param className The class name value.
     */
    fun addClass(element: DOMElement, className: String)
    /**
     * Removes class.
     *
     * @param element The element value.
     * @param className The class name value.
     */
    fun removeClass(element: DOMElement, className: String)
    /**
     * Executes the append child operation.
     *
     * @param parent The parent value.
     * @param child The child value.
     */
    fun appendChild(parent: DOMElement, child: DOMElement)
    /**
     * Removes element.
     *
     * @param element The element value.
     */
    fun removeElement(element: DOMElement)
    /**
     * Adds event listener.
     *
     * @param element The element value.
     * @param eventType The event type value.
     * @param handler The handler value.
     */
    fun addEventListener(element: DOMElement, eventType: String, handler: (WasmDOMEvent) -> Unit)
    /**
     * Removes event listener.
     *
     * @param element The element value.
     * @param eventType The event type value.
     * @param handler The handler value.
     */
    fun removeEventListener(element: DOMElement, eventType: String, handler: (WasmDOMEvent) -> Unit)
    /**
     * Finds element by hydration ID.
     *
     * @param markerId The marker id value.
     * @return The resulting value.
     */
    fun findElementByHydrationId(markerId: String): DOMElement?
    /**
     * Returns hydration ID.
     *
     * @param element The element value.
     * @return The resulting value.
     */
    fun getHydrationId(element: DOMElement): String?
}