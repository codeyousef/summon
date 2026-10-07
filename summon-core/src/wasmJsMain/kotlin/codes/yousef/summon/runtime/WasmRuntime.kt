package codes.yousef.summon.runtime

import kotlinx.coroutines.CancellationException
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener


private val domDiagnostics = RendererDiagnostics { console.error(it) }

// Element storage - maps element IDs to actual DOM elements
private val elementStore = mutableMapOf<String, Node>()
private var elementIdCounter = 0
private val eventHandlers = mutableMapOf<String, EventHandlerEntry>()
private val eventCallbacks = mutableMapOf<String, () -> Unit>()
private val animationFrameCallbacks = mutableMapOf<Int, () -> Unit>()

// Batch operations state
private val batchUpdates = mutableListOf<() -> Unit>()
private var isBatching = false

/**
 * Represents last event.
 *
 * @property type The type value.
 * @property targetId The target id value.
 * @property value Value to process.
 * @property checked The checked value.
 * @property event The event value.
 */
class LastEvent(
    val type: String,
    val targetId: String,
    val value: String,
    val checked: Boolean,
    val event: Event
)

/**
 * Represents event handler entry.
 *
 * @property elementId The element id value.
 * @property eventType The event type value.
 * @property listener The listener value.
 * @property lastEvent The last event value.
 */
class EventHandlerEntry(
    val elementId: String,
    val eventType: String,
    var listener: ((Event) -> Unit)? = null,
    var lastEvent: LastEvent? = null
)

// Helper function to get or create element store entry
private fun getElement(elementId: String): Node? {
    if (elementStore.containsKey(elementId)) {
        return elementStore[elementId]
    }
    // Try the public DOM id, then the renderer's stable composition id.
    val element = document.getElementById(elementId) ?: run {
        val candidates = document.querySelectorAll("[data-sid]")
        var match: Element? = null
        for (index in 0 until candidates.length) {
            val candidate = candidates.item(index) as? Element ?: continue
            if (candidate.getAttribute("data-sid") == elementId) {
                match = candidate
                break
            }
        }
        match
    }
    if (element == null) return null
    elementStore[elementId] = element
    return element
}

/** Executes the WASM clear element store operation. */
fun wasmClearElementStore() {
    elementStore.clear()
}

// Helper to store element and return its ID
private fun storeElement(node: Node): String {
    var nodeId: String? = null

    if (node is Element) {
        nodeId = node.id
    }

    if (nodeId.isNullOrEmpty()) {
        nodeId = "wasm-elem-${++elementIdCounter}"
        if (node is Element) {
            node.id = nodeId
        }
    }

    elementStore[nodeId] = node
    return nodeId
}


// Element creation and basic manipulation
/**
 * Executes the WASM create element by ID operation.
 *
 * @param tagName The tag name value.
 * @return The resulting value.
 */
fun wasmCreateElementById(tagName: String): String {
    return try {
        val element = document.createElement(tagName)
        storeElement(element)
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        ""
    }
}

/**
 * Executes the WASM get element by ID operation.
 *
 * @param id Stable identifier.
 * @return The resulting value.
 */
fun wasmGetElementById(id: String): String? {
    val element = document.getElementById(id)
    return if (element != null) storeElement(element) else null
}

/**
 * Executes the WASM set element attribute operation.
 *
 * @param elementId The element id value.
 * @param name Human-readable name.
 * @param value Value to process.
 * @return The resulting value.
 */
fun wasmSetElementAttribute(elementId: String, name: String, value: String): Boolean {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.setAttribute(name, value)
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM get element attribute operation.
 *
 * @param elementId The element id value.
 * @param name Human-readable name.
 * @return The resulting value.
 */
fun wasmGetElementAttribute(elementId: String, name: String): String? {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.getAttribute(name)
        } else {
            null
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        null
    }
}

/**
 * Executes the WASM remove element attribute operation.
 *
 * @param elementId The element id value.
 * @param name Human-readable name.
 * @return The resulting value.
 */
fun wasmRemoveElementAttribute(elementId: String, name: String): Boolean {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.removeAttribute(name)
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

// Text content manipulation
/**
 * Executes the WASM set element text content operation.
 *
 * @param elementId The element id value.
 * @param text The text value.
 * @return The resulting value.
 */
fun wasmSetElementTextContent(elementId: String, text: String): Boolean {
    return try {
        val node = getElement(elementId)
        if (node != null) {
            node.textContent = text
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM get element text content operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementTextContent(elementId: String): String? {
    return try {
        val node = getElement(elementId)
        node?.textContent
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        null
    }
}

/**
 * Executes the WASM set element inner HTML operation.
 *
 * @param elementId The element id value.
 * @param html The html value.
 * @return The resulting value.
 */
fun wasmSetElementInnerHTML(elementId: String, html: String): Boolean {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.innerHTML = html
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM get element inner HTML operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementInnerHTML(elementId: String): String? {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.innerHTML
        } else {
            domDiagnostics.failure()
            null
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        null
    }
}

// DOM tree manipulation
/**
 * Executes the WASM append child by ID operation.
 *
 * @param parentId The parent id value.
 * @param childId The child id value.
 * @return The resulting value.
 */
fun wasmAppendChildById(parentId: String, childId: String): Boolean {
    return try {
        val parent = getElement(parentId)
        val child = getElement(childId)
        if (parent != null && child != null) {
            parent.appendChild(child)
            true
        } else {
            domDiagnostics.failure()
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM remove child by ID operation.
 *
 * @param parentId The parent id value.
 * @param childId The child id value.
 * @return The resulting value.
 */
fun wasmRemoveChildById(parentId: String, childId: String): Boolean {
    return try {
        val parent = getElement(parentId)
        val child = getElement(childId)
        if (parent != null && child != null && parent.contains(child)) {
            parent.removeChild(child)
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM remove element by ID operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmRemoveElementById(elementId: String): Boolean {
    return try {
        val element = getElement(elementId)
        if (element != null) {
            element.parentNode?.removeChild(element)
            elementStore.entries.filter { it.value === element }.map { it.key }.forEach { elementStore.remove(it) }
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

private fun jsClick(element: JsAny): Unit = js("""{
    var event = new MouseEvent('click', {
        view: window,
        bubbles: true,
        cancelable: true
    });
    element.dispatchEvent(event);
}""")

/**
 * Executes the WASM click element operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmClickElement(elementId: String): Boolean {
    return try {
        val element = getElement(elementId)
        if (element == null) {
            domDiagnostics.failure()
            return false
        }

        // Try native click first for HTMLElement as it's more reliable in happy-dom
        if (element is HTMLElement) {
            try {
                element.click()
                return true
            } catch (e: Throwable) {
                if (e is CancellationException) throw e
                domDiagnostics.failure()
            }
        }

        // Fallback to jsClick
        try {
            jsClick(element)
            return true
        } catch (e: Throwable) {
            if (e is CancellationException) throw e
            domDiagnostics.failure()
            // Fallback to native click if jsClick fails (unlikely)
            if (element is HTMLElement) {
                element.click()
                true
            } else {
                false
            }
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

// CSS class manipulation
/**
 * Executes the WASM add class to element operation.
 *
 * @param elementId The element id value.
 * @param className The class name value.
 * @return The resulting value.
 */
fun wasmAddClassToElement(elementId: String, className: String): Boolean {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            val classes = className.split(" ")
            for (cls in classes) {
                if (cls.isNotEmpty()) {
                    node.classList.add(cls)
                }
            }
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM remove class from element operation.
 *
 * @param elementId The element id value.
 * @param className The class name value.
 * @return The resulting value.
 */
fun wasmRemoveClassFromElement(elementId: String, className: String): Boolean {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            val classes = className.split(" ")
            for (cls in classes) {
                if (cls.isNotEmpty()) {
                    node.classList.remove(cls)
                }
            }
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM element has class operation.
 *
 * @param elementId The element id value.
 * @param className The class name value.
 * @return The resulting value.
 */
fun wasmElementHasClass(elementId: String, className: String): Boolean {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.classList.contains(className)
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM get element class name operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementClassName(elementId: String): String? {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.className
        } else {
            null
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        null
    }
}

// Element properties
/**
 * Executes the WASM get element tag name operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementTagName(elementId: String): String? {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.tagName.lowercase()
        } else {
            null
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        null
    }
}

/**
 * Executes the WASM get element parent operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementParent(elementId: String): String? {
    return try {
        val node = getElement(elementId)
        if (node != null && node.parentNode != null) {
            storeElement(node.parentNode!!)
        } else {
            null
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        null
    }
}

/**
 * Executes the WASM get element ID operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementId(elementId: String): String? {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.id
        } else {
            // Try dynamic id for non-Element nodes
            getElementId(node)

        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        null
    }
}

/**
 * Executes the WASM set element ID operation.
 *
 * @param elementId The element id value.
 * @param newId The new id value.
 * @return The resulting value.
 */
fun wasmSetElementId(elementId: String, newId: String): Boolean {
    return try {
        val node = getElement(elementId)
        if (node != null) {
            if (node is Element) {
                node.id = newId
            } else {
                setElementId(node, newId)
            }

            // Update the store mapping
            elementStore.remove(elementId)
            elementStore[newId] = node
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

// Element hierarchy
/**
 * Executes the WASM get element parent ID operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementParentId(elementId: String): String? {
    return wasmGetElementParent(elementId)
}

/**
 * Executes the WASM get element children operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementChildren(elementId: String): String {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            val childIds = mutableListOf<String>()
            val children = node.children
            for (i in 0 until children.length) {
                children.item(i)?.let { child ->
                    childIds.add(child.getAttribute("data-sid")?.takeIf { it.isNotEmpty() } ?: storeElement(child))
                }
            }
            childIds.joinToString(",")
        } else {
            ""
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        ""
    }
}

/**
 * Executes the WASM get element subtree IDs operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementSubtreeIds(elementId: String): String {
    return try {
        val root = getElement(elementId) as? Element ?: return ""
        val ids = mutableListOf<String>()
        fun collectAliases(node: Node) {
            var found = false
            for ((storedId, storedNode) in elementStore) {
                if (storedNode === node) {
                    ids += storedId
                    found = true
                }
            }
            if (!found) ids += storeElement(node)
        }
        collectAliases(root)
        val descendants = root.querySelectorAll("*")
        for (index in 0 until descendants.length) {
            descendants.item(index)?.let(::collectAliases)
        }
        ids.distinct().joinToString(",")
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        ""
    }
}

// Event handling
/**
 * Executes the WASM add event listener by ID operation.
 *
 * @param elementId The element id value.
 * @param eventType The event type value.
 * @param handlerId The handler id value.
 * @return The resulting value.
 */
fun wasmAddEventListenerById(elementId: String, eventType: String, handlerId: String): Boolean {
    try {
        val element = getElement(elementId) ?: return false

        var entry = eventHandlers[handlerId]
        if (entry == null) {
            entry = EventHandlerEntry(elementId, eventType)
            eventHandlers[handlerId] = entry
        } else if (entry.listener != null) {
            element.removeEventListener(eventType, entry.listener)
        }

        val listener: (Event) -> Unit = { event ->
            val target = event.target
            val targetNode = target as? Node
            val value = getInputValue(targetNode) ?: ""
            val checked = getInputChecked(targetNode)


            entry.lastEvent = LastEvent(
                type = event.type,
                targetId = elementId,
                value = value,
                checked = checked,
                event = event
            )

            val callback = eventCallbacks[handlerId]
            if (callback != null) {
                try {
                    callback()
                } catch (err: Throwable) {
                    if (err is CancellationException) throw err
                    domDiagnostics.failure()

                }
            } else {
                try {
                    CallbackRegistry.executeCallback(handlerId)
                } catch (err: Throwable) {
                    if (err is CancellationException) throw err
                    domDiagnostics.failure()

                }
            }
        }

        entry.listener = listener
        element.addEventListener(eventType, listener)
        return true
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        return false
    }
}

/**
 * Executes the WASM remove event listener by ID operation.
 *
 * @param elementId The element id value.
 * @param eventType The event type value.
 * @param handlerId The handler id value.
 * @return The resulting value.
 */
fun wasmRemoveEventListenerById(elementId: String, eventType: String, handlerId: String): Boolean {
    return try {
        val element = getElement(elementId)
        val entry = eventHandlers[handlerId]
        if (entry != null && entry.listener != null && element != null) {
            element.removeEventListener(eventType, entry.listener)
        }
        eventHandlers.remove(handlerId)
        eventCallbacks.remove(handlerId)
        true
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        eventHandlers.remove(handlerId)
        eventCallbacks.remove(handlerId)
        false
    }
}

// Event properties
/**
 * Executes the WASM get event type operation.
 *
 * @param handlerId The handler id value.
 * @return The resulting value.
 */
fun wasmGetEventType(handlerId: String): String? {
    val entry = eventHandlers[handlerId]
    return entry?.lastEvent?.type
}

/**
 * Executes the WASM get event target ID operation.
 *
 * @param handlerId The handler id value.
 * @return The resulting value.
 */
fun wasmGetEventTargetId(handlerId: String): String? {
    val entry = eventHandlers[handlerId]
    return entry?.lastEvent?.targetId
}

/**
 * Executes the WASM get event value operation.
 *
 * @param handlerId The handler id value.
 * @return The resulting value.
 */
fun wasmGetEventValue(handlerId: String): String? {
    val entry = eventHandlers[handlerId]
    return entry?.lastEvent?.value
}

/**
 * Executes the WASM get event target value operation.
 *
 * @param handlerId The handler id value.
 * @return The resulting value.
 */
fun wasmGetEventTargetValue(handlerId: String): String? {
    return wasmGetEventValue(handlerId)
}

/**
 * Executes the WASM prevent event default operation.
 *
 * @param handlerId The handler id value.
 * @return The resulting value.
 */
fun wasmPreventEventDefault(handlerId: String): Boolean {
    val entry = eventHandlers[handlerId]
    val event = entry?.lastEvent?.event
    if (event != null) {
        event.preventDefault()
        return true
    }
    return false
}

/**
 * Executes the WASM stop event propagation operation.
 *
 * @param handlerId The handler id value.
 * @return The resulting value.
 */
fun wasmStopEventPropagation(handlerId: String): Boolean {
    val entry = eventHandlers[handlerId]
    val event = entry?.lastEvent?.event
    if (event != null) {
        event.stopPropagation()
        return true
    }
    return false
}

// Query selectors
/**
 * Executes the WASM query selector get ID operation.
 *
 * @param selector The selector value.
 * @return The resulting value.
 */
fun wasmQuerySelectorGetId(selector: String): String? {
    return try {
        val element = document.querySelector(selector)
        if (element != null) storeElement(element) else null
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        null
    }
}

/**
 * Executes the WASM query selector all get IDs operation.
 *
 * @param selector The selector value.
 * @return The resulting value.
 */
fun wasmQuerySelectorAllGetIds(selector: String): String {
    return try {
        val elements = document.querySelectorAll(selector)
        val ids = mutableListOf<String>()
        for (i in 0 until elements.length) {
            elements.item(i)?.let { ids.add(storeElement(it)) }
        }
        ids.joinToString(",")
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        ""
    }
}

// Form element specifics
/**
 * Executes the WASM get element value operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementValue(elementId: String): String? {
    return try {
        val element = getElement(elementId)
        getInputValue(element)
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        null
    }
}

/**
 * Executes the WASM set element value operation.
 *
 * @param elementId The element id value.
 * @param value Value to process.
 * @return The resulting value.
 */
fun wasmSetElementValue(elementId: String, value: String): Boolean {
    return try {
        val element = getElement(elementId)
        if (element != null) {
            setInputValue(element, value)
            true
        } else {

            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM get element checked operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementChecked(elementId: String): Boolean {
    return try {
        val element = getElement(elementId)
        getInputChecked(element)
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM set element checked operation.
 *
 * @param elementId The element id value.
 * @param checked The checked value.
 * @return The resulting value.
 */
fun wasmSetElementChecked(elementId: String, checked: Boolean): Boolean {
    return try {
        val element = getElement(elementId)
        if (element != null) {
            setInputChecked(element, checked)
            true

        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM get element disabled operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementDisabled(elementId: String): Boolean {
    return try {
        val element = getElement(elementId)
        getInputDisabled(element)
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM set element disabled operation.
 *
 * @param elementId The element id value.
 * @param disabled The disabled value.
 * @return The resulting value.
 */
fun wasmSetElementDisabled(elementId: String, disabled: Boolean): Boolean {
    return try {
        val element = getElement(elementId)
        if (element != null) {
            setInputDisabled(element, disabled)

            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

// Select element specifics
/**
 * Executes the WASM get selected index operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetSelectedIndex(elementId: String): Int {
    return try {
        val element = getElement(elementId)
        if (element is HTMLSelectElement) {
            element.selectedIndex
        } else {
            -1
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        -1
    }
}

/**
 * Executes the WASM set selected index operation.
 *
 * @param elementId The element id value.
 * @param index Zero-based item index.
 * @return The resulting value.
 */
fun wasmSetSelectedIndex(elementId: String, index: Int): Boolean {
    return try {
        val element = getElement(elementId)
        if (element is HTMLSelectElement) {
            element.selectedIndex = index
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

// Document operations
/**
 * Executes the WASM get document body ID operation.
 *
 * @return The resulting value.
 */
fun wasmGetDocumentBodyId(): String? {
    return document.body?.let { storeElement(it) }
}

/**
 * Executes the WASM get document body operation.
 *
 * @return The resulting value.
 */
fun wasmGetDocumentBody(): String? {
    return wasmGetDocumentBodyId()
}

/**
 * Executes the WASM get document head ID operation.
 *
 * @return The resulting value.
 */
fun wasmGetDocumentHeadId(): String? {
    return document.head?.let { storeElement(it) }
}

/**
 * Executes the WASM get document head operation.
 *
 * @return The resulting value.
 */
fun wasmGetDocumentHead(): String? {
    return wasmGetDocumentHeadId()
}

/**
 * Executes the WASM insert HTML into head operation.
 *
 * @param html The html value.
 * @return The resulting value.
 */
fun wasmInsertHTMLIntoHead(html: String): Boolean {
    return try {
        document.head?.insertAdjacentHTML("beforeend", html)
        true
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM insert adjacent HTML operation.
 *
 * @param elementId The element id value.
 * @param position The position value.
 * @param html The html value.
 * @return The resulting value.
 */
fun wasmInsertAdjacentHTML(elementId: String, position: String, html: String): Boolean {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.insertAdjacentHTML(position, html)
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM get outer HTML operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetOuterHTML(elementId: String): String? {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.outerHTML
        } else {
            null
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        null
    }
}

/**
 * Executes the WASM set inner HTML operation.
 *
 * @param elementId The element id value.
 * @param html The html value.
 * @return The resulting value.
 */
fun wasmSetInnerHTML(elementId: String, html: String): Boolean {
    return wasmSetElementInnerHTML(elementId, html)
}

// Browser capabilities
/**
 * Executes the WASM get user agent operation.
 *
 * @return The resulting value.
 */
fun wasmGetUserAgent(): String {
    return window.navigator.userAgent
}

/**
 * Executes the WASM extract browser version operation.
 *
 * @param userAgent The user agent value.
 * @param browserName The browser name value.
 * @return The resulting value.
 */
fun wasmExtractBrowserVersion(userAgent: String, browserName: String): Int {
    val regex = Regex("$browserName\\/([0-9]+)")
    val match = regex.find(userAgent)
    return match?.groupValues?.get(1)?.toIntOrNull() ?: 0
}

/**
 * Executes the WASM get timestamp operation.
 *
 * @return The resulting value.
 */
fun wasmGetTimestamp(): Long = getTimestampJS().toDouble().toLong()
private fun getTimestampJS(): JsNumber = js("Date.now()")





/**
 * Executes the WASM get location href operation.
 *
 * @return The resulting value.
 */
fun wasmGetLocationHref(): String {
    return window.location.href
}

/**
 * Executes the WASM has async await operation.
 *
 * @return The resulting value.
 */
fun wasmHasAsyncAwait(): Boolean {
    return true
}

/**
 * Executes the WASM has fetch API operation.
 *
 * @return The resulting value.
 */
fun wasmHasFetchAPI(): Boolean = js("typeof fetch !== 'undefined'")

/**
 * Executes the WASM has web sockets operation.
 *
 * @return The resulting value.
 */
fun wasmHasWebSockets(): Boolean = js("typeof WebSocket !== 'undefined'")

/**
 * Executes the WASM has indexed db operation.
 *
 * @return The resulting value.
 */
fun wasmHasIndexedDB(): Boolean = js("typeof indexedDB !== 'undefined'")

/**
 * Executes the WASM has web workers operation.
 *
 * @return The resulting value.
 */
fun wasmHasWebWorkers(): Boolean = js("typeof Worker !== 'undefined'")

/**
 * Executes the WASM has web gl operation.
 *
 * @return The resulting value.
 */
fun wasmHasWebGL(): Boolean = true

/**
 * Executes the WASM has web gl2 operation.
 *
 * @return The resulting value.
 */
fun wasmHasWebGL2(): Boolean = true


/**
 * Executes the WASM has push notifications operation.
 *
 * @return The resulting value.
 */
fun wasmHasPushNotifications(): Boolean = js("typeof PushManager !== 'undefined'")

/**
 * Executes the WASM has web rtc operation.
 *
 * @return The resulting value.
 */
fun wasmHasWebRTC(): Boolean = js("typeof RTCPeerConnection !== 'undefined'")

/**
 * Executes the WASM has touch events operation.
 *
 * @return The resulting value.
 */
fun wasmHasTouchEvents(): Boolean = js("'ontouchstart' in window")

/**
 * Executes the WASM has pointer events operation.
 *
 * @return The resulting value.
 */
fun wasmHasPointerEvents(): Boolean = js("typeof PointerEvent !== 'undefined'")

/**
 * Executes the WASM has resize observer operation.
 *
 * @return The resulting value.
 */
fun wasmHasResizeObserver(): Boolean = js("typeof ResizeObserver !== 'undefined'")

/**
 * Executes the WASM has intersection observer operation.
 *
 * @return The resulting value.
 */
fun wasmHasIntersectionObserver(): Boolean = js("typeof IntersectionObserver !== 'undefined'")

/**
 * Executes the WASM has mutation observer operation.
 *
 * @return The resulting value.
 */
fun wasmHasMutationObserver(): Boolean = js("typeof MutationObserver !== 'undefined'")

/**
 * Executes the WASM has promises operation.
 *
 * @return The resulting value.
 */
fun wasmHasPromises(): Boolean = js("typeof Promise !== 'undefined'")


// Feature detection
/**
 * Executes the WASM has touch support operation.
 *
 * @return The resulting value.
 */
fun wasmHasTouchSupport(): Boolean = js("'ontouchstart' in window || navigator.maxTouchPoints > 0")



/**
 * Executes the WASM has module support operation.
 *
 * @return The resulting value.
 */
fun wasmHasModuleSupport(): Boolean = true
/**
 * Executes the WASM has arrow functions operation.
 *
 * @return The resulting value.
 */
fun wasmHasArrowFunctions(): Boolean = true
/**
 * Executes the WASM test basic WASM support operation.
 *
 * @return The resulting value.
 */
fun wasmTestBasicWasmSupport(): Boolean = true
/**
 * Executes the WASM test advanced WASM support operation.
 *
 * @return The resulting value.
 */
fun wasmTestAdvancedWasmSupport(): Boolean = true

// User agent testing
/**
 * Executes the WASM test mobile user agent operation.
 *
 * @param userAgent The user agent value.
 * @return The resulting value.
 */
fun wasmTestMobileUserAgent(userAgent: String): Boolean = js("/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(userAgent)")

/**
 * Executes the WASM test tablet user agent operation.
 *
 * @param userAgent The user agent value.
 * @return The resulting value.
 */
fun wasmTestTabletUserAgent(userAgent: String): Boolean = js("/iPad|Android(?!.*Mobile)|Tablet/i.test(userAgent)")



// Style operations
/**
 * Executes the WASM scroll element into view operation.
 *
 * @param elementId The element id value.
 * @param behavior The behavior value.
 * @return The resulting value.
 */
fun wasmScrollElementIntoView(elementId: String, behavior: String): Boolean {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            val b = behavior.ifEmpty { "smooth" }
            val options = JSON.parse("{\"behavior\": \"$b\", \"block\": \"start\"}")


            scrollIntoView(node, options)
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}


private fun scrollIntoView(element: JsAny, options: JsAny): Unit = js("element.scrollIntoView(options)")







/**
 * Executes the WASM get computed style property operation.
 *
 * @param elementId The element id value.
 * @param property The property value.
 * @return The resulting value.
 */
fun wasmGetComputedStyleProperty(elementId: String, property: String): String? {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            val styles = window.getComputedStyle(node)
            styles.getPropertyValue(property)
        } else {
            null
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        null
    }
}

/**
 * Executes the WASM apply style property operation.
 *
 * @param elementId The element id value.
 * @param property The property value.
 * @param value Value to process.
 * @return The resulting value.
 */
fun wasmApplyStyleProperty(elementId: String, property: String, value: String): Boolean {
    return try {
        val node = getElement(elementId)
        if (node is HTMLElement) {
            node.style.setProperty(property, value)
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM get element style operation.
 *
 * @param elementId The element id value.
 * @param property The property value.
 * @return The resulting value.
 */
fun wasmGetElementStyle(elementId: String, property: String): String? {
    return try {
        val node = getElement(elementId)
        if (node is HTMLElement) {
            node.style.getPropertyValue(property)
        } else {
            null
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        null
    }
}

/**
 * Executes the WASM set element style operation.
 *
 * @param elementId The element id value.
 * @param cssText The css text value.
 * @return The resulting value.
 */
fun wasmSetElementStyle(elementId: String, cssText: String): Boolean {
    return try {
        val node = getElement(elementId)
        if (node is HTMLElement) {
            node.style.cssText = cssText
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

// Performance
/**
 * Executes the WASM performance now operation.
 *
 * @return The resulting value.
 */
fun wasmPerformanceNow(): Double {
    return window.performance.now()
}

// Memory and batch operations
/**
 * Executes the WASM start batch operation.
 *
 * @return The resulting value.
 */
fun wasmStartBatch(): Boolean {
    isBatching = true
    batchUpdates.clear()
    return true
}

/**
 * Executes the WASM end batch operation.
 *
 * @return The resulting value.
 */
fun wasmEndBatch(): Boolean {
    isBatching = false
    // Apply all batched updates
    window.requestAnimationFrame {
        batchUpdates.forEach { it() }
        batchUpdates.clear()
    }
    return true
}

// Console logging
private fun jsConsoleLog(message: String): Unit = js("console.log(message)")
private fun jsConsoleWarn(message: String): Unit = js("console.warn(message)")
private fun jsConsoleError(message: String): Unit = js("console.error(message)")

/**
 * Executes the WASM console log operation.
 *
 * @param message Message content.
 */
fun wasmConsoleLog(message: String) {
    jsConsoleLog("[Summon WASM] $message")
}

/**
 * Executes the WASM console warn operation.
 *
 * @param message Message content.
 */
fun wasmConsoleWarn(message: String) {
    jsConsoleWarn("[Summon WASM] $message")
}

/**
 * Executes the WASM console error operation.
 *
 * @param message Message content.
 */
fun wasmConsoleError(message: String) {
    jsConsoleError("[Summon WASM] $message")
}


/**
 * Executes the WASM console debug operation.
 *
 * @param message Message content.
 */
fun wasmConsoleDebug(message: String) {
    console.log("[Summon WASM DEBUG] $message")

}


// Helper functions
/**
 * Executes the WASM is not null operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun wasmIsNotNull(value: String?): Boolean {
    return value != null
}

/**
 * Executes the WASM add event handler operation.
 *
 * @param elementId The element id value.
 * @param eventType The event type value.
 * @param handlerId The handler id value.
 * @return The resulting value.
 */
fun wasmAddEventHandler(elementId: String, eventType: String, handlerId: String): Boolean {
    return wasmAddEventListenerById(elementId, eventType, handlerId)
}

/**
 * Executes the WASM remove event handler operation.
 *
 * @param elementId The element id value.
 * @param eventType The event type value.
 * @param handlerId The handler id value.
 * @return The resulting value.
 */
fun wasmRemoveEventHandler(elementId: String, eventType: String, handlerId: String): Boolean {
    return wasmRemoveEventListenerById(elementId, eventType, handlerId)
}

/**
 * Executes the WASM create element with options operation.
 *
 * @param tagName The tag name value.
 * @param options The options value.
 * @return The resulting value.
 */
fun wasmCreateElementWithOptions(tagName: String, options: String): String {
    return try {
        val element = document.createElement(tagName)
        if (options.isNotEmpty()) {
            try {
                applyOptions(element, options)
            } catch (e: Throwable) {
                if (e is CancellationException) throw e
                // Ignore parse errors
            }
        }
        storeElement(element)
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        ""
    }
}

private fun applyOptions(element: JsAny, options: String) {
    try {
        val opts = JSON.parse(options)
        val keys = getKeys(opts)
        val length = keys.length
        for (i in 0 until length) {
            val key = keys[i]
            if (key != null) {
                setDynamic(element, key, getDynamic(opts, key))
            }
        }

    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        // Ignore
    }
}




/**
 * Executes the WASM clone element operation.
 *
 * @param sourceElementId The source element id value.
 * @param deep The deep value.
 * @return The resulting value.
 */
fun wasmCloneElement(sourceElementId: String, deep: Boolean): String? {
    return try {
        val element = getElement(sourceElementId)
        if (element != null) {
            val clone = element.cloneNode(deep) as Element
            storeElement(clone)
        } else {
            null
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        null
    }
}

// Register WASM event callback handler
/**
 * Registers WASM event callback.
 *
 * @param handlerId The handler id value.
 * @param callback The callback value.
 */
fun registerWasmEventCallback(handlerId: String, callback: () -> Unit) {
    eventCallbacks[handlerId] = callback
}

// Animation frame management
/**
 * Executes the WASM request animation frame operation.
 *
 * @return The resulting value.
 */
fun wasmRequestAnimationFrame(): Int {
    val frameId = window.requestAnimationFrame {
        // The callback will be executed via registerWasmAnimationFrameCallback
    }
    return frameId
}

/**
 * Executes the WASM cancel animation frame operation.
 *
 * @param frameId The frame id value.
 * @return The resulting value.
 */
fun wasmCancelAnimationFrame(frameId: Int): Boolean {
    window.cancelAnimationFrame(frameId)
    animationFrameCallbacks.remove(frameId)
    return true
}

/**
 * Registers WASM animation frame callback.
 *
 * @param frameId The frame id value.
 * @param callback The callback value.
 */
fun registerWasmAnimationFrameCallback(frameId: Int, callback: () -> Unit) {
    animationFrameCallbacks[frameId] = callback

    // Override the frame to execute our callback
    window.cancelAnimationFrame(frameId)
    val newFrameId = window.requestAnimationFrame {
        try {
            callback()
        } catch (e: Throwable) {
            if (e is CancellationException) throw e
            domDiagnostics.failure()
        } finally {
            animationFrameCallbacks.remove(frameId)
        }
    }

    // Update the mapping if frameId changed
    if (newFrameId != frameId) {
        animationFrameCallbacks.remove(frameId)
        animationFrameCallbacks[newFrameId] = callback
    }
}

// Missing functions from WasmNativeInterfaces.kt
/**
 * Executes the WASM find elements by selector operation.
 *
 * @param selector The selector value.
 * @return The resulting value.
 */
fun wasmFindElementsBySelector(selector: String): String {
    return wasmQuerySelectorAllGetIds(selector)
}

/**
 * Executes the WASM get elements by tag name operation.
 *
 * @param tagName The tag name value.
 * @return The resulting value.
 */
fun wasmGetElementsByTagName(tagName: String): String {
    return try {
        val elements = document.getElementsByTagName(tagName)
        val ids = mutableListOf<String>()
        for (i in 0 until elements.length) {
            elements.item(i)?.let { ids.add(storeElement(it)) }
        }
        ids.joinToString(",")
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        ""
    }
}

/**
 * Executes the WASM get elements by class name operation.
 *
 * @param className The class name value.
 * @return The resulting value.
 */
fun wasmGetElementsByClassName(className: String): String {
    return try {
        val elements = document.getElementsByClassName(className)
        val ids = mutableListOf<String>()
        for (i in 0 until elements.length) {
            elements.item(i)?.let { ids.add(storeElement(it)) }
        }
        ids.joinToString(",")
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        ""
    }
}

/**
 * Executes the WASM is element visible operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmIsElementVisible(elementId: String): Boolean {
    return try {
        val element = getElement(elementId)
        if (element is HTMLElement) {
            // Simple check: offsetParent is null if display: none
            element.offsetParent != null
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM get element position operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementPosition(elementId: String): String {
    return try {
        val element = getElement(elementId)
        if (element is Element) {
            val rect = getBoundingClientRectJS(element).unsafeCast<WasmDOMRect>()
            "${rect.x},${rect.y},${rect.width},${rect.height}"
        } else {
            "0,0,0,0"
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        "0,0,0,0"
    }
}



/**
 * Executes the WASM set element position operation.
 *
 * @param elementId The element id value.
 * @param x The x value.
 * @param y The y value.
 * @param width The width value.
 * @param height The height value.
 * @return The resulting value.
 */
fun wasmSetElementPosition(elementId: String, x: Double, y: Double, width: Double, height: Double): Boolean {
    return try {
        val element = getElement(elementId)
        if (element is HTMLElement) {
            element.style.left = "${x}px"
            element.style.top = "${y}px"
            element.style.width = "${width}px"
            element.style.height = "${height}px"
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM replace element operation.
 *
 * @param oldElementId The old element id value.
 * @param newElementId The new element id value.
 * @return The resulting value.
 */
fun wasmReplaceElement(oldElementId: String, newElementId: String): Boolean {
    return try {
        val oldEl = getElement(oldElementId)
        val newEl = getElement(newElementId)
        if (oldEl != null && newEl != null && oldEl.parentNode != null) {
            oldEl.parentNode?.replaceChild(newEl, oldEl)
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM move element operation.
 *
 * @param elementId The element id value.
 * @param newParentId The new parent id value.
 * @param beforeElementId The before element id value.
 * @return The resulting value.
 */
fun wasmMoveElement(elementId: String, newParentId: String, beforeElementId: String?): Boolean {
    return try {
        val element = getElement(elementId)
        val newParent = getElement(newParentId)
        if (element != null && newParent != null) {
            if (beforeElementId != null) {
                val beforeEl = getElement(beforeElementId)
                newParent.insertBefore(element, beforeEl)
            } else {
                newParent.appendChild(element)
            }
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM start performance measure operation.
 *
 * @param measureName The measure name value.
 * @return The resulting value.
 */
fun wasmStartPerformanceMeasure(measureName: String): Boolean {
    mark(measureName)
    return true
}

/**
 * Executes the WASM end performance measure operation.
 *
 * @param measureName The measure name value.
 * @return The resulting value.
 */
fun wasmEndPerformanceMeasure(measureName: String): Double {
    return endMeasure(measureName)
}

private fun endMeasure(measureName: String): Double = js("""{
    try {
        window.performance.measure(measureName, measureName);
        var entries = window.performance.getEntriesByName(measureName);
        if (entries.length > 0) {
            return entries[entries.length - 1].duration;
        }
        return 0.0;
    } catch (e) { return 0.0; }
}""")


/**
 * Executes the WASM log element tree operation.
 *
 * @param rootElementId The root element id value.
 * @return The resulting value.
 */
fun wasmLogElementTree(rootElementId: String): Boolean {
    return try {
        val element = getElement(rootElementId)
        if (element != null) {
            console.log(element)
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        false
    }
}

/**
 * Executes the WASM validate element ID operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmValidateElementId(elementId: String): Boolean {
    return getElement(elementId) != null
}

// Error boundary utils
/** Executes the WASM enable static form fallbacks operation. */
fun wasmEnableStaticFormFallbacks() {
    // No-op
}

/**
 * Executes the WASM clear WASM cache operation.
 *
 * @return The resulting value.
 */
fun wasmClearWasmCache(): Boolean {
    return true
}

/**
 * Executes the WASM verify JS fallback operation.
 *
 * @return The resulting value.
 */
fun wasmVerifyJSFallback(): Boolean {
    return true
}

/**
 * Executes the WASM clear module cache operation.
 *
 * @return The resulting value.
 */
fun wasmClearModuleCache(): Boolean {
    return true
}

/**
 * Executes the WASM load compatibility shims operation.
 *
 * @return The resulting value.
 */
fun wasmLoadCompatibilityShims(): Boolean {
    return true
}

/**
 * Executes the WASM check network connectivity operation.
 *
 * @return The resulting value.
 */
fun wasmCheckNetworkConnectivity(): Boolean {
    return window.navigator.onLine
}

/**
 * Executes the WASM retry network operation operation.
 *
 * @return The resulting value.
 */
fun wasmRetryNetworkOperation(): Boolean {
    return window.navigator.onLine
}

/**
 * Executes the WASM enable offline mode operation.
 *
 * @return The resulting value.
 */
fun wasmEnableOfflineMode(): Boolean {
    return true
}

/**
 * Executes the WASM clear all caches operation.
 *
 * @return The resulting value.
 */
fun wasmClearAllCaches(): Boolean {
    return true
}

/**
 * Executes the WASM reset to known state operation.
 *
 * @return The resulting value.
 */
fun wasmResetToKnownState(): Boolean {
    window.location.reload()
    return true
}

/**
 * Executes the WASM verify basic functionality operation.
 *
 * @return The resulting value.
 */
fun wasmVerifyBasicFunctionality(): Boolean {
    return true
}

/**
 * Executes the WASM get current time operation.
 *
 * @return The resulting value.
 */
fun wasmGetCurrentTime(): Long = getTimestampJS().toDouble().toLong()





/**
 * Executes the WASM log error operation.
 *
 * @param message Message content.
 */
fun wasmLogError(message: String) {
    domDiagnostics.failure()
}

/**
 * Executes the WASM log warning operation.
 *
 * @param message Message content.
 */
fun wasmLogWarning(message: String) {
    console.warn(message)
}

/**
 * Executes the WASM report error operation.
 *
 * @param message Message content.
 * @param stackTrace The stack trace value.
 * @param metadata The metadata value.
 */
fun wasmReportError(message: String, stackTrace: String, metadata: String) {
    domDiagnostics.failure()
}

/**
 * Executes the WASM report error operation.
 *
 * @param reportData The report data value.
 */
fun wasmReportError(reportData: String) {
    domDiagnostics.failure()
}

/**
 * Executes the WASM delay operation.
 *
 * @param ms The ms value.
 */
fun wasmDelay(ms: Int) {
    // No-op
}

/** Executes the WASM setup global error handling operation. */
fun wasmSetupGlobalErrorHandling() {
    window.addEventListener("error") { event ->
        domDiagnostics.failure()

    }
    window.addEventListener("unhandledrejection") { event ->
        domDiagnostics.failure()

    }
}

/**
 * Executes the WASM get used memory operation.
 *
 * @return The resulting value.
 */
fun wasmGetUsedMemory(): Long {
    return 0L
}

/**
 * Executes the WASM get total memory operation.
 *
 * @return The resulting value.
 */
fun wasmGetTotalMemory(): Long {
    return 0L
}

/**
 * Executes the WASM get memory limit operation.
 *
 * @return The resulting value.
 */
fun wasmGetMemoryLimit(): Long {
    return 0L
}

/**
 * Executes the WASM force garbage collection operation.
 *
 * @return The resulting value.
 */
fun wasmForceGarbageCollection(): Boolean {
    return false
}

/**
 * Executes the WASM reduce memory usage operation.
 *
 * @return The resulting value.
 */
fun wasmReduceMemoryUsage(): Boolean {
    return false
}

/**
 * Executes the WASM get current frame rate operation.
 *
 * @return The resulting value.
 */
fun wasmGetCurrentFrameRate(): Double {
    return 60.0
}

/**
 * Executes the WASM get cpu usage operation.
 *
 * @return The resulting value.
 */
fun wasmGetCPUUsage(): Double {
    return 0.0
}

/**
 * Executes the WASM enable emergency optimizations operation.
 *
 * @return The resulting value.
 */
fun wasmEnableEmergencyOptimizations(): Boolean {
    return true
}

// Additional missing functions
/**
 * Executes the WASM scroll into view operation.
 *
 * @param elementId The element id value.
 * @param behavior The behavior value.
 */
fun wasmScrollIntoView(elementId: String, behavior: String) {
    wasmScrollElementIntoView(elementId, behavior)
}

/**
 * Executes the WASM get computed style operation.
 *
 * @param elementId The element id value.
 * @param property The property value.
 * @return The resulting value.
 */
fun wasmGetComputedStyle(elementId: String, property: String): String? {
    return wasmGetComputedStyleProperty(elementId, property)
}

/**
 * Executes the WASM insert before by ID operation.
 *
 * @param parentId The parent id value.
 * @param newChildId The new child id value.
 * @param refChildId The ref child id value.
 * @return The resulting value.
 */
fun wasmInsertBeforeById(parentId: String, newChildId: String, refChildId: String): Boolean {
    return wasmMoveElement(newChildId, parentId, refChildId)
}

/**
 * Executes the WASM get element bounding left operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementBoundingLeft(elementId: String): Double {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            (getBoundingClientRectJS(node).unsafeCast<WasmDOMRect>()).left
        } else {
            0.0
        }
    } catch (e: Throwable) { 0.0 }
}

/**
 * Executes the WASM get element bounding top operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementBoundingTop(elementId: String): Double {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            (getBoundingClientRectJS(node).unsafeCast<WasmDOMRect>()).top
        } else {
            0.0
        }
    } catch (e: Throwable) { 0.0 }
}

/**
 * Executes the WASM get element bounding right operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementBoundingRight(elementId: String): Double {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            (getBoundingClientRectJS(node).unsafeCast<WasmDOMRect>()).right
        } else {
            0.0
        }
    } catch (e: Throwable) { 0.0 }
}

/**
 * Executes the WASM get element bounding bottom operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementBoundingBottom(elementId: String): Double {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            (getBoundingClientRectJS(node).unsafeCast<WasmDOMRect>()).bottom
        } else {
            0.0
        }
    } catch (e: Throwable) { 0.0 }
}

/**
 * Executes the WASM get element bounding width operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementBoundingWidth(elementId: String): Double {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            (getBoundingClientRectJS(node).unsafeCast<WasmDOMRect>()).width
        } else {
            0.0
        }
    } catch (e: Throwable) { 0.0 }
}

/**
 * Executes the WASM get element bounding height operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementBoundingHeight(elementId: String): Double {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            (getBoundingClientRectJS(node).unsafeCast<WasmDOMRect>()).height
        } else {
            0.0
        }
    } catch (e: Throwable) { 0.0 }
}

/**
 * Executes the WASM get element scroll top operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementScrollTop(elementId: String): Double {
    return try {
        (getElement(elementId) as? HTMLElement)?.scrollTop ?: 0.0
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        0.0
    }
}

/**
 * Executes the WASM get element scroll left operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetElementScrollLeft(elementId: String): Double {
    return try {
        (getElement(elementId) as? HTMLElement)?.scrollLeft ?: 0.0
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        0.0
    }
}



/**
 * Executes the WASM get active element ID operation.
 *
 * @return The resulting value.
 */
fun wasmGetActiveElementId(): String? {
    return try {
        document.activeElement?.let { storeElement(it) }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        null
    }
}

/**
 * Executes the WASM get input selection start operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetInputSelectionStart(elementId: String): Int? {
    return try {
        when (val element = getElement(elementId)) {
            is HTMLInputElement -> element.selectionStart
            is HTMLTextAreaElement -> element.selectionStart
            else -> null
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        null
    }
}

/**
 * Executes the WASM get input selection end operation.
 *
 * @param elementId The element id value.
 * @return The resulting value.
 */
fun wasmGetInputSelectionEnd(elementId: String): Int? {
    return try {
        when (val element = getElement(elementId)) {
            is HTMLInputElement -> element.selectionEnd
            is HTMLTextAreaElement -> element.selectionEnd
            else -> null
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        null
    }
}

/**
 * Executes the WASM has meaningful focus operation.
 *
 * @return The resulting value.
 */
fun wasmHasMeaningfulFocus(): Boolean {
    val active = document.activeElement
    return active != null && active !== document.body
}

/**
 * Executes the WASM restore element focus operation.
 *
 * @param elementId The element id value.
 * @param selectionStart The selection start value.
 * @param selectionEnd The selection end value.
 */
fun wasmRestoreElementFocus(elementId: String, selectionStart: Int?, selectionEnd: Int?) {
    try {
        val node = getElement(elementId)
        if (node is HTMLElement && document.contains(node)) {
            if (wasmGetActiveElementId() != elementId) node.focus()
            when (node) {
                is HTMLInputElement -> if (selectionStart != null && selectionEnd != null) {
                    node.setSelectionRange(selectionStart, selectionEnd)
                }
                is HTMLTextAreaElement -> if (selectionStart != null && selectionEnd != null) {
                    node.setSelectionRange(selectionStart, selectionEnd)
                }
            }
        }
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
    }
}

/**
 * Executes the WASM focus element operation.
 *
 * @param elementId The element id value.
 */
fun wasmFocusElement(elementId: String) {
    try {
        val node = getElement(elementId)
        if (node is HTMLElement) node.focus()
    } catch (e: Throwable) {}
}

/**
 * Executes the WASM blur element operation.
 *
 * @param elementId The element id value.
 */
fun wasmBlurElement(elementId: String) {
    try {
        val node = getElement(elementId)
        if (node is HTMLElement) node.blur()
    } catch (e: Throwable) {}
}

/**
 * Executes the WASM create text node operation.
 *
 * @param text The text value.
 * @return The resulting value.
 */
fun wasmCreateTextNode(text: String): String {
    return try {
        val node = document.createTextNode(text)
        storeElement(node)
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        ""
    }
}

/**
 * Executes the WASM get document element ID operation.
 *
 * @return The resulting value.
 */
fun wasmGetDocumentElementId(): String? {
    return document.documentElement?.let { storeElement(it) }
}

/**
 * Executes the WASM add document event listener operation.
 *
 * @param type The type value.
 * @param handlerId The handler id value.
 */
fun wasmAddDocumentEventListener(type: String, handlerId: String) {
    // Similar to wasmAddEventListenerById but for document
    // We can reuse the logic if we treat document as an element with a special ID
    // or just implement it separately.
    try {
        val listener: (Event) -> Unit = { event ->
             // ... logic ...
             // For now, simple implementation
             CallbackRegistry.executeCallback(handlerId)
        }
        document.addEventListener(type, listener)
    } catch (e: Throwable) {}
}

/**
 * Executes the WASM remove document event listener operation.
 *
 * @param type The type value.
 * @param handlerId The handler id value.
 */
fun wasmRemoveDocumentEventListener(type: String, handlerId: String) {
    val entry = eventHandlers[handlerId]
    if (entry != null && entry.listener != null) {
        document.removeEventListener(type, entry.listener!!)
        eventHandlers.remove(handlerId)
    }
}

/**
 * Executes the WASM set location href operation.
 *
 * @param href The href value.
 */
fun wasmSetLocationHref(href: String) { window.location.href = href }
/**
 * Executes the WASM get location hostname operation.
 *
 * @return The resulting value.
 */
fun wasmGetLocationHostname(): String? = window.location.hostname
/**
 * Executes the WASM get location port operation.
 *
 * @return The resulting value.
 */
fun wasmGetLocationPort(): String? = window.location.port
/**
 * Executes the WASM get location pathname operation.
 *
 * @return The resulting value.
 */
fun wasmGetLocationPathname(): String? = window.location.pathname
/**
 * Executes the WASM set location pathname operation.
 *
 * @param pathname The pathname value.
 */
fun wasmSetLocationPathname(pathname: String) { window.location.pathname = pathname }
/**
 * Executes the WASM get location search operation.
 *
 * @return The resulting value.
 */
fun wasmGetLocationSearch(): String? = window.location.search
/**
 * Executes the WASM set location search operation.
 *
 * @param search The search value.
 */
fun wasmSetLocationSearch(search: String) { window.location.search = search }
/**
 * Executes the WASM get location hash operation.
 *
 * @return The resulting value.
 */
fun wasmGetLocationHash(): String? = window.location.hash
/**
 * Executes the WASM set location hash operation.
 *
 * @param hash The hash value.
 */
fun wasmSetLocationHash(hash: String) { window.location.hash = hash }
/**
 * Executes the WASM location assign operation.
 *
 * @param url Target URL.
 */
fun wasmLocationAssign(url: String) { window.location.assign(url) }
/**
 * Executes the WASM location replace operation.
 *
 * @param url Target URL.
 */
fun wasmLocationReplace(url: String) { window.location.replace(url) }
/** Executes the WASM location reload operation. */
fun wasmLocationReload() { window.location.reload() }

/**
 * Executes the WASM get history length operation.
 *
 * @return The resulting value.
 */
fun wasmGetHistoryLength(): Int = window.history.length
/**
 * Executes the WASM get history state operation.
 *
 * @return The resulting value.
 */
fun wasmGetHistoryState(): String? = window.history.state?.toString()
/** Executes the WASM history back operation. */
fun wasmHistoryBack() { window.history.back() }
/** Executes the WASM history forward operation. */
fun wasmHistoryForward() { window.history.forward() }
/**
 * Executes the WASM history go operation.
 *
 * @param delta The delta value.
 */
fun wasmHistoryGo(delta: Int) { window.history.go(delta) }
/**
 * Executes the WASM history push state operation.
 *
 * @param state The state value.
 * @param title The title value.
 * @param url Target URL.
 */
fun wasmHistoryPushState(state: String, title: String, url: String) {
    window.history.pushState(state.toJsString(), title, url)
}
/**
 * Executes the WASM history replace state operation.
 *
 * @param state The state value.
 * @param title The title value.
 * @param url Target URL.
 */
fun wasmHistoryReplaceState(state: String, title: String, url: String) {
    window.history.replaceState(state.toJsString(), title, url)
}


/**
 * Executes the WASM performance mark operation.
 *
 * @param name Human-readable name.
 */
fun wasmPerformanceMark(name: String) {
    mark(name)
}
private fun mark(name: String): Unit = js("window.performance.mark(name)")

/**
 * Executes the WASM performance measure operation.
 *
 * @param name Human-readable name.
 * @param startMark The start mark value.
 * @param endMark The end mark value.
 * @return The resulting value.
 */
fun wasmPerformanceMeasure(name: String, startMark: String, endMark: String): Double {
    measure(name, startMark, endMark)
    return 0.0
}
private fun measure(name: String, startMark: String, endMark: String): Unit = js("window.performance.measure(name, startMark, endMark)")

/**
 * Executes the WASM performance measure to now operation.
 *
 * @param name Human-readable name.
 * @param startMark The start mark value.
 * @return The resulting value.
 */
fun wasmPerformanceMeasureToNow(name: String, startMark: String): Double {
    measureToNow(name, startMark)
    return 0.0
}
private fun measureToNow(name: String, startMark: String): Unit = js("window.performance.measure(name, startMark)")


/**
 * Executes the WASM get used heap size operation.
 *
 * @return The resulting value.
 */
fun wasmGetUsedHeapSize(): Long = 0L
/**
 * Executes the WASM get total heap size operation.
 *
 * @return The resulting value.
 */
fun wasmGetTotalHeapSize(): Long = 0L
/**
 * Executes the WASM get heap size limit operation.
 *
 * @return The resulting value.
 */
fun wasmGetHeapSizeLimit(): Long = 0L
/**
 * Executes the WASM get frame rate operation.
 *
 * @return The resulting value.
 */
fun wasmGetFrameRate(): Double = 60.0
/**
 * Executes the WASM get render time operation.
 *
 * @return The resulting value.
 */
fun wasmGetRenderTime(): Double = 0.0
/**
 * Executes the WASM get hydration time operation.
 *
 * @return The resulting value.
 */
fun wasmGetHydrationTime(): Double = 0.0
/**
 * Executes the WASM get script load time operation.
 *
 * @return The resulting value.
 */
fun wasmGetScriptLoadTime(): Double = 0.0
/**
 * Executes the WASM get DOM content loaded time operation.
 *
 * @return The resulting value.
 */
fun wasmGetDOMContentLoadedTime(): Double = 0.0
/**
 * Executes the WASM get first contentful paint operation.
 *
 * @return The resulting value.
 */
fun wasmGetFirstContentfulPaint(): Double = 0.0
/**
 * Executes the WASM get largest contentful paint operation.
 *
 * @return The resulting value.
 */
fun wasmGetLargestContentfulPaint(): Double = 0.0
/**
 * Executes the WASM report metrics operation.
 *
 * @param metricsData The metrics data value.
 */
fun wasmReportMetrics(metricsData: String) { console.log("Metrics: $metricsData") }


// Feature detection for additional APIs
/**
 * Executes the WASM has local storage operation.
 *
 * @return The resulting value.
 */
fun wasmHasLocalStorage(): Boolean {
    return try {
        window.localStorage
        true
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        false
    }
}

/**
 * Executes the WASM has session storage operation.
 *
 * @return The resulting value.
 */
fun wasmHasSessionStorage(): Boolean {
    return try {
        window.sessionStorage
        true
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        false
    }
}

/**
 * Executes the WASM has service workers operation.
 *
 * @return The resulting value.
 */
fun wasmHasServiceWorkers(): Boolean = js("('serviceWorker' in navigator)")

/**
 * Executes the WASM has WASM simd operation.
 *
 * @return The resulting value.
 */
fun wasmHasWasmSIMD(): Boolean = js("WebAssembly.validate(new Uint8Array([0, 97, 115, 109, 1, 0, 0, 0, 1, 5, 1, 96, 0, 1, 123, 3, 2, 1, 0, 10, 10, 1, 8, 0, 65, 0, 253, 15, 253, 98, 11]))")

/**
 * Executes the WASM has WASM threads operation.
 *
 * @return The resulting value.
 */
fun wasmHasWasmThreads(): Boolean = js("typeof SharedArrayBuffer !== 'undefined'")

/**
 * Executes the WASM has dynamic import operation.
 *
 * @return The resulting value.
 */
fun wasmHasDynamicImport(): Boolean =
    js("'noModule' in document.createElement('script')")

/**
 * Executes the WASM get screen width operation.
 *
 * @return The resulting value.
 */
fun wasmGetScreenWidth(): Int = window.screen.width
/**
 * Executes the WASM get screen height operation.
 *
 * @return The resulting value.
 */
fun wasmGetScreenHeight(): Int = window.screen.height
/**
 * Executes the WASM get device pixel ratio operation.
 *
 * @return The resulting value.
 */
fun wasmGetDevicePixelRatio(): Double = window.devicePixelRatio
/**
 * Executes the WASM get color depth operation.
 *
 * @return The resulting value.
 */
fun wasmGetColorDepth(): Int = window.screen.colorDepth

private fun getElementId(node: Node?): String? {
    return (node as? Element)?.id
}

private fun setElementId(node: Node?, id: String) {
    (node as? Element)?.id = id
}

private fun getInputValue(node: Node?): String? {
    return (node as? HTMLInputElement)?.value ?: (node as? HTMLTextAreaElement)?.value
}

private fun setInputValue(node: Node?, value: String) {
    (node as? HTMLInputElement)?.value = value
    (node as? HTMLTextAreaElement)?.value = value
}

private fun getInputChecked(node: Node?): Boolean {
    return (node as? HTMLInputElement)?.checked ?: false
}

private fun setInputChecked(node: Node?, checked: Boolean) {
    (node as? HTMLInputElement)?.checked = checked
}

private fun getInputDisabled(node: Node?): Boolean {
    return (node as? HTMLInputElement)?.disabled
        ?: (node as? HTMLButtonElement)?.disabled
        ?: (node as? HTMLSelectElement)?.disabled
        ?: (node as? HTMLTextAreaElement)?.disabled
        ?: false
}

private fun setInputDisabled(node: Node?, disabled: Boolean) {
    (node as? HTMLInputElement)?.disabled = disabled
    (node as? HTMLButtonElement)?.disabled = disabled
    (node as? HTMLSelectElement)?.disabled = disabled
    (node as? HTMLTextAreaElement)?.disabled = disabled
}

private fun getBoundingClientRectJS(element: JsAny): JsAny = js("element.getBoundingClientRect()")

/** Contract for WASM DOM rect. */
external interface WasmDOMRect : JsAny {
    /** The property declaration value. */
    val x: Double
    /** The property declaration value. */
    val y: Double
    /** The property declaration value. */
    val width: Double
    /** The property declaration value. */
    val height: Double
    /** The property declaration value. */
    val top: Double
    /** The property declaration value. */
    val right: Double
    /** The property declaration value. */
    val bottom: Double
    /** The property declaration value. */
    val left: Double
}



/** The property declaration value. */
external val JSON: JSONClass

/** Contract for JSON class. */
external interface JSONClass {
    /**
     * Parses the operation.
     *
     * @param text The text value.
     * @return The resulting value.
     */
    fun parse(text: String): JsAny
}

private fun getKeys(o: JsAny): JsArray<JsString> = Object.keys(o)


private fun getDynamic(o: JsAny, key: JsString): JsAny? = Reflect.get(o, key)

private fun setDynamic(o: JsAny, key: JsString, value: JsAny?) {
    Reflect.set(o, key, value)
}


/** Provides reflect operations. */
external object Reflect {
    /**
     * Returns the operation.
     *
     * @param target The target value.
     * @param propertyKey The property key value.
     * @return The resulting value.
     */
    fun get(target: JsAny, propertyKey: JsString): JsAny?
    /**
     * Sets the operation.
     *
     * @param target The target value.
     * @param propertyKey The property key value.
     * @param value Value to process.
     * @return The resulting value.
     */
    fun set(target: JsAny, propertyKey: JsString, value: JsAny?): Boolean
}

/** Provides object operations. */
external object Object {
    /**
     * Executes the keys operation.
     *
     * @param o The o value.
     * @return The resulting value.
     */
    fun keys(o: JsAny): JsArray<JsString>
}

// Restoring wasmExecuteCallback function which is used by the example project.
/**
 * Executes the WASM execute callback operation.
 *
 * @param callbackId The callback id value.
 * @return The resulting value.
 */
fun wasmExecuteCallback(callbackId: String): Boolean {
    return try {
        CallbackRegistry.executeCallback(callbackId)
        true
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        domDiagnostics.failure()
        false
    }
}

/**
 * Executes the WASM get summon state operation.
 *
 * @return The resulting value.
 */
fun wasmGetSummonState(): String? = js("window.__SUMMON_STATE__ ? JSON.stringify(window.__SUMMON_STATE__) : null")








