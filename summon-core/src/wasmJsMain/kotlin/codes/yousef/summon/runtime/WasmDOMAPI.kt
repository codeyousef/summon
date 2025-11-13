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
 */
class WasmDOMElement(
    override val tagName: String,
    override val id: String,
    val nativeElementId: String
) : DOMElement {

    override val className: String
        get() = wasmGetElementClassName(nativeElementId) ?: ""

    override val textContent: String?
        get() = wasmGetElementTextContent(nativeElementId)

    override fun getAttribute(name: String): String? {
        return wasmGetElementAttribute(nativeElementId, name)
    }

    override fun setAttribute(name: String, value: String) {
        wasmSetElementAttribute(nativeElementId, name, value)
    }

    override fun removeAttribute(name: String) {
        wasmRemoveElementAttribute(nativeElementId, name)
    }

    override fun appendChild(child: DOMElement) {
        val wasmChild = child as? WasmDOMElement
            ?: throw WasmDOMException("Invalid child element type")
        wasmAppendChildById(nativeElementId, wasmChild.nativeElementId)
    }

    override fun removeChild(child: DOMElement) {
        val wasmChild = child as? WasmDOMElement
            ?: throw WasmDOMException("Invalid child element type")
        wasmRemoveChildById(nativeElementId, wasmChild.nativeElementId)
    }

    override fun addEventListener(type: String, listener: (event: Any) -> Unit) {
        val handlerId = "inline-handler-${wasmPerformanceNow().toLong()}"
        wasmAddEventListenerById(nativeElementId, type, handlerId)
        // Note: In real implementation, would need proper callback registration
    }

    override fun removeEventListener(type: String, listener: (event: Any) -> Unit) {
        // Note: This is a limitation - we can't easily track and remove specific listeners
        // without a more complex event handler management system
    }

    override fun querySelector(selector: String): DOMElement? {
        val elementId = wasmQuerySelectorGetId(selector)
        return if (elementId != null) {
            val tagName = wasmGetElementTagName(elementId) ?: "div"
            WasmDOMElement(tagName, elementId, elementId)
        } else null
    }

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

    override fun hasAttribute(name: String): Boolean {
        return wasmGetElementAttribute(nativeElementId, name) != null
    }

    // Additional methods with override - these ARE in the DOMElement interface apparently
    override fun insertBefore(newChild: DOMElement, referenceChild: DOMElement?) {
        // Simplified implementation - would need proper insertBefore support
        appendChild(newChild)
    }

    override fun getBoundingClientRect(): DOMRect {
        // Simplified implementation - would need proper getBoundingClientRect support
        return DOMRect(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    }

    override fun focus() {
        // Would need wasmFocusElement implementation
    }

    override fun blur() {
        // Would need wasmBlurElement implementation
    }

    override fun click() {
        // Would need wasmClickElement implementation
    }

    override fun toString(): String = "WasmDOMElement(tagName=$tagName, id=$id)"
}

/**
 * WASM-specific simple event implementation using Any for compatibility.
 * Since runtime.DOMElement uses (event: Any) -> Unit, we use a simple data class.
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
    fun createElement(tagName: String): DOMElement
    fun setTextContent(element: DOMElement, text: String)
    fun setAttribute(element: DOMElement, name: String, value: String)
    fun addClass(element: DOMElement, className: String)
    fun removeClass(element: DOMElement, className: String)
    fun appendChild(parent: DOMElement, child: DOMElement)
    fun removeElement(element: DOMElement)
    fun addEventListener(element: DOMElement, eventType: String, handler: (WasmDOMEvent) -> Unit)
    fun removeEventListener(element: DOMElement, eventType: String, handler: (WasmDOMEvent) -> Unit)
    fun findElementByHydrationId(markerId: String): DOMElement?
    fun getHydrationId(element: DOMElement): String?
}