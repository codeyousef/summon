package codes.yousef.summon.runtime

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener


// Element storage - maps element IDs to actual DOM elements
private val elementStore = mutableMapOf<String, Node>()
private var elementIdCounter = 0
private val eventHandlers = mutableMapOf<String, EventHandlerEntry>()
private val eventCallbacks = mutableMapOf<String, () -> Unit>()
private val animationFrameCallbacks = mutableMapOf<Int, () -> Unit>()

// Batch operations state
private val batchUpdates = mutableListOf<() -> Unit>()
private var isBatching = false

class LastEvent(
    val type: String,
    val targetId: String,
    val value: String,
    val checked: Boolean,
    val event: Event
)

class EventHandlerEntry(
    val elementId: String,
    val eventType: String,
    var listener: ((Event) -> Unit)? = null,
    var lastEvent: LastEvent? = null
)

// Helper function to get or create element store entry
private fun getElement(elementId: String): Node? {
    if (elementStore.containsKey(elementId)) {
        // console.log("[Summon WASM] getElement($elementId) found in store")
        return elementStore[elementId]
    }
    // Try to find element in DOM
    val element = document.getElementById(elementId)
    if (element != null) {
        console.log("[Summon WASM] getElement($elementId) found in DOM, adding to store")
        elementStore[elementId] = element
        return element
    }
    console.log("[Summon WASM] getElement($elementId) NOT found")
    return null
}

fun wasmClearElementStore() {
    console.log("[Summon WASM] Clearing element store. Size was: " + elementStore.size)
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
fun wasmCreateElementById(tagName: String): String {
    return try {
        val element = document.createElement(tagName)
        storeElement(element)
    } catch (e: Throwable) {
        console.error("[Summon WASM] createElement failed:" + ": " + e.message)
        ""
    }
}

fun wasmGetElementById(id: String): String? {
    val element = document.getElementById(id)
    return if (element != null) storeElement(element) else null
}

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
        console.error("[Summon WASM] setAttribute failed:" + ": " + e.message)
        false
    }
}

fun wasmGetElementAttribute(elementId: String, name: String): String? {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.getAttribute(name)
        } else {
            null
        }
    } catch (e: Throwable) {
        console.error("[Summon WASM] getAttribute failed:" + ": " + e.message)
        null
    }
}

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
        console.error("[Summon WASM] removeAttribute failed:" + ": " + e.message)
        false
    }
}

// Text content manipulation
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
        console.error("[Summon WASM] setTextContent failed:" + ": " + e.message)
        false
    }
}

fun wasmGetElementTextContent(elementId: String): String? {
    return try {
        val node = getElement(elementId)
        node?.textContent
    } catch (e: Throwable) {
        console.error("[Summon WASM] getTextContent failed:" + ": " + e.message)
        null
    }
}

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
        console.error("[Summon WASM] setInnerHTML failed:" + ": " + e.message)
        false
    }
}

fun wasmGetElementInnerHTML(elementId: String): String? {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            console.log("[Summon WASM] getInnerHTML for $elementId. childNodes: " + node.childNodes.length)
            node.innerHTML
        } else {
            console.error("[Summon WASM] getInnerHTML: node $elementId is not an Element")
            null
        }
    } catch (e: Throwable) {
        console.error("[Summon WASM] getInnerHTML failed:" + ": " + e.message)
        null
    }
}

// DOM tree manipulation
fun wasmAppendChildById(parentId: String, childId: String): Boolean {
    return try {
        val parent = getElement(parentId)
        val child = getElement(childId)
        if (parent != null && child != null) {
            parent.appendChild(child)
            console.log("[Summon WASM] Appended child $childId to parent $parentId. Parent childNodes length: " + parent.childNodes.length)
            if (parent is Element) {
                 console.log("[Summon WASM] Parent outerHTML: " + parent.outerHTML)
            }
            true
        } else {
            console.error("[Summon WASM] appendChild failed: parent=$parent (id=$parentId), child=$child (id=$childId)")
            false
        }
    } catch (e: Throwable) {
        console.error("[Summon WASM] appendChild failed:" + ": " + e.message)
        false
    }
}

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
        console.error("[Summon WASM] removeChild failed:" + ": " + e.message)
        false
    }
}

fun wasmRemoveElementById(elementId: String): Boolean {
    return try {
        val element = getElement(elementId)
        if (element != null && element.parentNode != null) {
            element.parentNode?.removeChild(element)
            elementStore.remove(elementId)
            true
        } else {
            false
        }
    } catch (e: Throwable) {
        console.error("[Summon WASM] removeElement failed:" + ": " + e.message)
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

fun wasmClickElement(elementId: String): Boolean {
    return try {
        val element = getElement(elementId)
        if (element == null) {
            console.error("[Summon WASM] clickElement failed: Element with ID $elementId not found in store")
            return false
        }

        // Try native click first for HTMLElement as it's more reliable in happy-dom
        if (element is HTMLElement) {
            try {
                element.click()
                return true
            } catch (e: Throwable) {
                console.warn("[Summon WASM] native click failed, falling back to jsClick: ${e.message}")
            }
        }
        
        // Fallback to jsClick
        try {
            jsClick(element)
            return true
        } catch (e: Throwable) {
            console.error("[Summon WASM] clickElement failed: ${e.message}")
            // Fallback to native click if jsClick fails (unlikely)
            if (element is HTMLElement) {
                element.click()
                true
            } else {
                false
            }
        }
    } catch (e: Throwable) {
        console.error("[Summon WASM] clickElement failed:" + ": " + e.message)
        false
    }
}

// CSS class manipulation
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
        console.error("[Summon WASM] addClass failed:" + ": " + e.message)
        false
    }
}

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
        console.error("[Summon WASM] removeClass failed:" + ": " + e.message)
        false
    }
}

fun wasmElementHasClass(elementId: String, className: String): Boolean {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.classList.contains(className)
        } else {
            false
        }
    } catch (e: Throwable) {
        console.error("[Summon WASM] hasClass failed:" + ": " + e.message)
        false
    }
}

fun wasmGetElementClassName(elementId: String): String? {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.className
        } else {
            null
        }
    } catch (e: Throwable) {
        console.error("[Summon WASM] getClassName failed:" + ": " + e.message)
        null
    }
}

// Element properties
fun wasmGetElementTagName(elementId: String): String? {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.tagName.lowercase()
        } else {
            null
        }
    } catch (e: Throwable) {
        console.error("[Summon WASM] getTagName failed:" + ": " + e.message)
        null
    }
}

fun wasmGetElementParent(elementId: String): String? {
    return try {
        val node = getElement(elementId)
        if (node != null && node.parentNode != null) {
            storeElement(node.parentNode!!)
        } else {
            null
        }
    } catch (e: Throwable) {
        console.error("[Summon WASM] getElementParent failed:" + ": " + e.message)
        null
    }
}

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
        console.error("[Summon WASM] getId failed:" + ": " + e.message)
        null
    }
}

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
        console.error("[Summon WASM] setId failed:" + ": " + e.message)
        false
    }
}

// Element hierarchy
fun wasmGetElementParentId(elementId: String): String? {
    return wasmGetElementParent(elementId)
}

fun wasmGetElementChildren(elementId: String): String {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            val childIds = mutableListOf<String>()
            val children = node.children
            for (i in 0 until children.length) {
                children.item(i)?.let { childIds.add(storeElement(it)) }
            }
            childIds.joinToString(",")
        } else {
            ""
        }
    } catch (e: Throwable) {
        console.error("[Summon WASM] getChildren failed:" + ": " + e.message)
        ""
    }
}

// Event handling
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

            console.log("[Summon WASM] Event triggered: ${event.type} on element: $elementId handler: $handlerId")
            
            val callback = eventCallbacks[handlerId]
            if (callback != null) {
                try {
                    callback()
                } catch (err: Throwable) {
                    console.error("[Summon WASM] Event callback failed: $err")

                }
            } else {
                try {
                    CallbackRegistry.executeCallback(handlerId)
                } catch (err: Throwable) {
                    console.error("[Summon WASM] CallbackRegistry execution failed: $err")

                }
            }
        }

        entry.listener = listener
        element.addEventListener(eventType, listener)
        return true
    } catch (e: Throwable) {
        console.error("[Summon WASM] addEventListener failed:" + ": " + e.message)
        return false
    }
}

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
        console.error("[Summon WASM] removeEventListener failed:" + ": " + e.message)
        eventHandlers.remove(handlerId)
        eventCallbacks.remove(handlerId)
        false
    }
}

// Event properties
fun wasmGetEventType(handlerId: String): String? {
    val entry = eventHandlers[handlerId]
    return entry?.lastEvent?.type
}

fun wasmGetEventTargetId(handlerId: String): String? {
    val entry = eventHandlers[handlerId]
    return entry?.lastEvent?.targetId
}

fun wasmGetEventValue(handlerId: String): String? {
    val entry = eventHandlers[handlerId]
    return entry?.lastEvent?.value
}

fun wasmGetEventTargetValue(handlerId: String): String? {
    return wasmGetEventValue(handlerId)
}

fun wasmPreventEventDefault(handlerId: String): Boolean {
    val entry = eventHandlers[handlerId]
    val event = entry?.lastEvent?.event
    if (event != null) {
        event.preventDefault()
        return true
    }
    return false
}

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
fun wasmQuerySelectorGetId(selector: String): String? {
    return try {
        val element = document.querySelector(selector)
        if (element != null) storeElement(element) else null
    } catch (e: Throwable) {
        console.error("[Summon WASM] querySelector failed:" + ": " + e.message)
        null
    }
}

fun wasmQuerySelectorAllGetIds(selector: String): String {
    return try {
        val elements = document.querySelectorAll(selector)
        val ids = mutableListOf<String>()
        for (i in 0 until elements.length) {
            elements.item(i)?.let { ids.add(storeElement(it)) }
        }
        ids.joinToString(",")
    } catch (e: Throwable) {
        console.error("[Summon WASM] querySelectorAll failed:" + ": " + e.message)
        ""
    }
}

// Form element specifics
fun wasmGetElementValue(elementId: String): String? {
    return try {
        val element = getElement(elementId)
        getInputValue(element)
    } catch (e: Throwable) {
        console.error("[Summon WASM] getValue failed:" + ": " + e.message)
        null
    }
}

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
        console.error("[Summon WASM] setValue failed:" + ": " + e.message)
        false
    }
}

fun wasmGetElementChecked(elementId: String): Boolean {
    return try {
        val element = getElement(elementId)
        getInputChecked(element)
    } catch (e: Throwable) {
        console.error("[Summon WASM] getChecked failed:" + ": " + e.message)
        false
    }
}

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
        console.error("[Summon WASM] setChecked failed:" + ": " + e.message)
        false
    }
}

fun wasmGetElementDisabled(elementId: String): Boolean {
    return try {
        val element = getElement(elementId)
        getInputDisabled(element)
    } catch (e: Throwable) {
        console.error("[Summon WASM] getDisabled failed:" + ": " + e.message)
        false
    }
}

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
        console.error("[Summon WASM] setDisabled failed:" + ": " + e.message)
        false
    }
}

// Select element specifics
fun wasmGetSelectedIndex(elementId: String): Int {
    return try {
        val element = getElement(elementId)
        if (element is HTMLSelectElement) {
            element.selectedIndex
        } else {
            -1
        }
    } catch (e: Throwable) {
        console.error("[Summon WASM] getSelectedIndex failed:" + ": " + e.message)
        -1
    }
}

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
        console.error("[Summon WASM] setSelectedIndex failed:" + ": " + e.message)
        false
    }
}

// Document operations
fun wasmGetDocumentBodyId(): String? {
    return document.body?.let { storeElement(it) }
}

fun wasmGetDocumentBody(): String? {
    return wasmGetDocumentBodyId()
}

fun wasmGetDocumentHeadId(): String? {
    return document.head?.let { storeElement(it) }
}

fun wasmGetDocumentHead(): String? {
    return wasmGetDocumentHeadId()
}

fun wasmInsertHTMLIntoHead(html: String): Boolean {
    return try {
        document.head?.insertAdjacentHTML("beforeend", html)
        true
    } catch (e: Throwable) {
        console.error("[Summon WASM] insertHTMLIntoHead failed:" + ": " + e.message)
        false
    }
}

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
        console.error("[Summon WASM] insertAdjacentHTML failed:" + ": " + e.message)
        false
    }
}

fun wasmGetOuterHTML(elementId: String): String? {
    return try {
        val node = getElement(elementId)
        if (node is Element) {
            node.outerHTML
        } else {
            null
        }
    } catch (e: Throwable) {
        console.error("[Summon WASM] getOuterHTML failed:" + ": " + e.message)
        null
    }
}

fun wasmSetInnerHTML(elementId: String, html: String): Boolean {
    return wasmSetElementInnerHTML(elementId, html)
}

// Browser capabilities
fun wasmGetUserAgent(): String {
    return window.navigator.userAgent
}

fun wasmExtractBrowserVersion(userAgent: String, browserName: String): Int {
    val regex = Regex("$browserName\\/([0-9]+)")
    val match = regex.find(userAgent)
    return match?.groupValues?.get(1)?.toIntOrNull() ?: 0
}

fun wasmGetTimestamp(): Long = getTimestampJS().toDouble().toLong()
private fun getTimestampJS(): JsNumber = js("Date.now()")





fun wasmGetLocationHref(): String {
    return window.location.href
}

fun wasmHasAsyncAwait(): Boolean {
    return true
}

fun wasmHasFetchAPI(): Boolean = js("typeof fetch !== 'undefined'")

fun wasmHasWebSockets(): Boolean = js("typeof WebSocket !== 'undefined'")

fun wasmHasIndexedDB(): Boolean = js("typeof indexedDB !== 'undefined'")

fun wasmHasWebWorkers(): Boolean = js("typeof Worker !== 'undefined'")

fun wasmHasWebGL(): Boolean = true

fun wasmHasWebGL2(): Boolean = true


fun wasmHasPushNotifications(): Boolean = js("typeof PushManager !== 'undefined'")

fun wasmHasWebRTC(): Boolean = js("typeof RTCPeerConnection !== 'undefined'")

fun wasmHasTouchEvents(): Boolean = js("'ontouchstart' in window")

fun wasmHasPointerEvents(): Boolean = js("typeof PointerEvent !== 'undefined'")

fun wasmHasResizeObserver(): Boolean = js("typeof ResizeObserver !== 'undefined'")

fun wasmHasIntersectionObserver(): Boolean = js("typeof IntersectionObserver !== 'undefined'")

fun wasmHasMutationObserver(): Boolean = js("typeof MutationObserver !== 'undefined'")

fun wasmHasPromises(): Boolean = js("typeof Promise !== 'undefined'")


// Feature detection
fun wasmHasTouchSupport(): Boolean = js("'ontouchstart' in window || navigator.maxTouchPoints > 0")



fun wasmHasModuleSupport(): Boolean = true
fun wasmHasArrowFunctions(): Boolean = true
fun wasmTestBasicWasmSupport(): Boolean = true
fun wasmTestAdvancedWasmSupport(): Boolean = true

// User agent testing
fun wasmTestMobileUserAgent(userAgent: String): Boolean = js("/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(userAgent)")

fun wasmTestTabletUserAgent(userAgent: String): Boolean = js("/iPad|Android(?!.*Mobile)|Tablet/i.test(userAgent)")



// Style operations
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
        console.error("[Summon WASM] scrollIntoView failed:" + ": " + e.message)
        false
    }
}


private fun scrollIntoView(element: JsAny, options: JsAny): Unit = js("element.scrollIntoView(options)")







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
        console.error("[Summon WASM] getComputedStyle failed:" + ": " + e.message)
        null
    }
}

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
        console.error("[Summon WASM] applyStyleProperty failed:" + ": " + e.message)
        false
    }
}

fun wasmGetElementStyle(elementId: String, property: String): String? {
    return try {
        val node = getElement(elementId)
        if (node is HTMLElement) {
            node.style.getPropertyValue(property)
        } else {
            null
        }
    } catch (e: Throwable) {
        console.error("[Summon WASM] getElementStyle failed:" + ": " + e.message)
        null
    }
}

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
        console.error("[Summon WASM] setElementStyle failed:" + ": " + e.message)
        false
    }
}

// Performance
fun wasmPerformanceNow(): Double {
    return window.performance.now()
}

// Memory and batch operations
fun wasmStartBatch(): Boolean {
    isBatching = true
    batchUpdates.clear()
    return true
}

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

fun wasmConsoleLog(message: String) {
    jsConsoleLog("[Summon WASM] $message")
}

fun wasmConsoleWarn(message: String) {
    jsConsoleWarn("[Summon WASM] $message")
}

fun wasmConsoleError(message: String) {
    jsConsoleError("[Summon WASM] $message")
}


fun wasmConsoleDebug(message: String) {
    console.log("[Summon WASM DEBUG] $message")

}


// Helper functions
fun wasmIsNotNull(value: String?): Boolean {
    return value != null
}

fun wasmAddEventHandler(elementId: String, eventType: String, handlerId: String): Boolean {
    return wasmAddEventListenerById(elementId, eventType, handlerId)
}

fun wasmRemoveEventHandler(elementId: String, eventType: String, handlerId: String): Boolean {
    return wasmRemoveEventListenerById(elementId, eventType, handlerId)
}

fun wasmCreateElementWithOptions(tagName: String, options: String): String {
    return try {
        val element = document.createElement(tagName)
        if (options.isNotEmpty()) {
            try {
                applyOptions(element, options)
            } catch (e: Throwable) {
                // Ignore parse errors
            }
        }
        storeElement(element)
    } catch (e: Throwable) {
        console.error("[Summon WASM] createElementWithOptions failed:" + ": " + e.message)
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
        // Ignore
    }
}




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
        console.error("[Summon WASM] cloneElement failed:" + ": " + e.message)
        null
    }
}

// Register WASM event callback handler
fun registerWasmEventCallback(handlerId: String, callback: () -> Unit) {
    console.log("[Summon WASM] Registering event callback: $handlerId")
    eventCallbacks[handlerId] = callback
}

// Animation frame management
fun wasmRequestAnimationFrame(): Int {
    val frameId = window.requestAnimationFrame {
        // The callback will be executed via registerWasmAnimationFrameCallback
    }
    console.log("[Summon WASM] Requested animation frame with ID: $frameId")
    return frameId
}

fun wasmCancelAnimationFrame(frameId: Int): Boolean {
    console.log("[Summon WASM] Cancelling animation frame: $frameId")
    window.cancelAnimationFrame(frameId)
    animationFrameCallbacks.remove(frameId)
    return true
}

fun registerWasmAnimationFrameCallback(frameId: Int, callback: () -> Unit) {
    console.log("[Summon WASM] Registering animation frame callback for ID: $frameId")
    animationFrameCallbacks[frameId] = callback

    // Override the frame to execute our callback
    window.cancelAnimationFrame(frameId)
    val newFrameId = window.requestAnimationFrame {
        console.log("[Summon WASM] Executing animation frame callback for ID: $frameId")
        try {
            callback()
        } catch (e: Throwable) {
            console.error("[Summon WASM] Animation frame callback failed:" + ": " + e.message)
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
fun wasmFindElementsBySelector(selector: String): String {
    return wasmQuerySelectorAllGetIds(selector)
}

fun wasmGetElementsByTagName(tagName: String): String {
    return try {
        val elements = document.getElementsByTagName(tagName)
        val ids = mutableListOf<String>()
        for (i in 0 until elements.length) {
            elements.item(i)?.let { ids.add(storeElement(it)) }
        }
        ids.joinToString(",")
    } catch (e: Throwable) {
        console.error("[Summon WASM] getElementsByTagName failed:" + ": " + e.message)
        ""
    }
}

fun wasmGetElementsByClassName(className: String): String {
    return try {
        val elements = document.getElementsByClassName(className)
        val ids = mutableListOf<String>()
        for (i in 0 until elements.length) {
            elements.item(i)?.let { ids.add(storeElement(it)) }
        }
        ids.joinToString(",")
    } catch (e: Throwable) {
        console.error("[Summon WASM] getElementsByClassName failed:" + ": " + e.message)
        ""
    }
}

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
        console.error("[Summon WASM] isElementVisible failed:" + ": " + e.message)
        false
    }
}

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
        console.error("[Summon WASM] getElementPosition failed:" + ": " + e.message)
        "0,0,0,0"
    }
}



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
        console.error("[Summon WASM] setElementPosition failed:" + ": " + e.message)
        false
    }
}

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
        console.error("[Summon WASM] replaceElement failed:" + ": " + e.message)
        false
    }
}

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
        console.error("[Summon WASM] moveElement failed:" + ": " + e.message)
        false
    }
}

fun wasmStartPerformanceMeasure(measureName: String): Boolean {
    mark(measureName)
    return true
}

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
        false
    }
}

fun wasmValidateElementId(elementId: String): Boolean {
    return getElement(elementId) != null
}

// Error boundary utils
fun wasmEnableStaticFormFallbacks() {
    // No-op
}

fun wasmClearWasmCache(): Boolean {
    return true
}

fun wasmVerifyJSFallback(): Boolean {
    return true
}

fun wasmClearModuleCache(): Boolean {
    return true
}

fun wasmLoadCompatibilityShims(): Boolean {
    return true
}

fun wasmCheckNetworkConnectivity(): Boolean {
    return window.navigator.onLine
}

fun wasmRetryNetworkOperation(): Boolean {
    return window.navigator.onLine
}

fun wasmEnableOfflineMode(): Boolean {
    return true
}

fun wasmClearAllCaches(): Boolean {
    return true
}

fun wasmResetToKnownState(): Boolean {
    window.location.reload()
    return true
}

fun wasmVerifyBasicFunctionality(): Boolean {
    return true
}

fun wasmGetCurrentTime(): Long = getTimestampJS().toDouble().toLong()





fun wasmLogError(message: String) {
    console.error(message)
}

fun wasmLogWarning(message: String) {
    console.warn(message)
}

fun wasmReportError(message: String, stackTrace: String, metadata: String) {
    console.error("Report Error: $message\nStack: $stackTrace\nMetadata: $metadata")
}

fun wasmReportError(reportData: String) {
    console.error("Report Error: $reportData")
}

fun wasmDelay(ms: Int) {
    // No-op
}

fun wasmSetupGlobalErrorHandling() {
    window.addEventListener("error") { event ->
        console.error("Global error: $event")

    }
    window.addEventListener("unhandledrejection") { event ->
        console.error("Unhandled rejection: $event")

    }
}

fun wasmGetUsedMemory(): Long {
    return 0L
}

fun wasmGetTotalMemory(): Long {
    return 0L
}

fun wasmGetMemoryLimit(): Long {
    return 0L
}

fun wasmForceGarbageCollection(): Boolean {
    return false
}

fun wasmReduceMemoryUsage(): Boolean {
    return false
}

fun wasmGetCurrentFrameRate(): Double {
    return 60.0
}

fun wasmGetCPUUsage(): Double {
    return 0.0
}

fun wasmEnableEmergencyOptimizations(): Boolean {
    return true
}

// Additional missing functions
fun wasmScrollIntoView(elementId: String, behavior: String) {
    wasmScrollElementIntoView(elementId, behavior)
}

fun wasmGetComputedStyle(elementId: String, property: String): String? {
    return wasmGetComputedStyleProperty(elementId, property)
}

fun wasmInsertBeforeById(parentId: String, newChildId: String, refChildId: String): Boolean {
    return wasmMoveElement(newChildId, parentId, refChildId)
}

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



fun wasmFocusElement(elementId: String) {
    try {
        val node = getElement(elementId)
        if (node is HTMLElement) node.focus()
    } catch (e: Throwable) {}
}

fun wasmBlurElement(elementId: String) {
    try {
        val node = getElement(elementId)
        if (node is HTMLElement) node.blur()
    } catch (e: Throwable) {}
}

fun wasmCreateTextNode(text: String): String {
    return try {
        val node = document.createTextNode(text)
        storeElement(node)
    } catch (e: Throwable) {
        console.error("[Summon WASM] createTextNode failed:" + ": " + e.message)
        ""
    }
}

fun wasmGetDocumentElementId(): String? {
    return document.documentElement?.let { storeElement(it) }
}

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

fun wasmRemoveDocumentEventListener(type: String, handlerId: String) {
    val entry = eventHandlers[handlerId]
    if (entry != null && entry.listener != null) {
        document.removeEventListener(type, entry.listener!!)
        eventHandlers.remove(handlerId)
    }
}

fun wasmSetLocationHref(href: String) { window.location.href = href }
fun wasmGetLocationHostname(): String? = window.location.hostname
fun wasmGetLocationPort(): String? = window.location.port
fun wasmGetLocationPathname(): String? = window.location.pathname
fun wasmSetLocationPathname(pathname: String) { window.location.pathname = pathname }
fun wasmGetLocationSearch(): String? = window.location.search
fun wasmSetLocationSearch(search: String) { window.location.search = search }
fun wasmGetLocationHash(): String? = window.location.hash
fun wasmSetLocationHash(hash: String) { window.location.hash = hash }
fun wasmLocationAssign(url: String) { window.location.assign(url) }
fun wasmLocationReplace(url: String) { window.location.replace(url) }
fun wasmLocationReload() { window.location.reload() }

fun wasmGetHistoryLength(): Int = window.history.length
fun wasmGetHistoryState(): String? = window.history.state?.toString()
fun wasmHistoryBack() { window.history.back() }
fun wasmHistoryForward() { window.history.forward() }
fun wasmHistoryGo(delta: Int) { window.history.go(delta) }
fun wasmHistoryPushState(state: String, title: String, url: String) {
    window.history.pushState(state.toJsString(), title, url)
}
fun wasmHistoryReplaceState(state: String, title: String, url: String) {
    window.history.replaceState(state.toJsString(), title, url)
}


fun wasmPerformanceMark(name: String) {
    mark(name)
}
private fun mark(name: String): Unit = js("window.performance.mark(name)")

fun wasmPerformanceMeasure(name: String, startMark: String, endMark: String): Double {
    measure(name, startMark, endMark)
    return 0.0
}
private fun measure(name: String, startMark: String, endMark: String): Unit = js("window.performance.measure(name, startMark, endMark)")

fun wasmPerformanceMeasureToNow(name: String, startMark: String): Double {
    measureToNow(name, startMark)
    return 0.0
}
private fun measureToNow(name: String, startMark: String): Unit = js("window.performance.measure(name, startMark)")


fun wasmGetUsedHeapSize(): Long = 0L
fun wasmGetTotalHeapSize(): Long = 0L
fun wasmGetHeapSizeLimit(): Long = 0L
fun wasmGetFrameRate(): Double = 60.0
fun wasmGetRenderTime(): Double = 0.0
fun wasmGetHydrationTime(): Double = 0.0
fun wasmGetScriptLoadTime(): Double = 0.0
fun wasmGetDOMContentLoadedTime(): Double = 0.0
fun wasmGetFirstContentfulPaint(): Double = 0.0
fun wasmGetLargestContentfulPaint(): Double = 0.0
fun wasmReportMetrics(metricsData: String) { console.log("Metrics: $metricsData") }


// Feature detection for additional APIs
fun wasmHasLocalStorage(): Boolean {
    return try {
        window.localStorage
        true
    } catch (e: Throwable) {
        false
    }
}

fun wasmHasSessionStorage(): Boolean {
    return try {
        window.sessionStorage
        true
    } catch (e: Throwable) {
        false
    }
}

fun wasmHasServiceWorkers(): Boolean = js("('serviceWorker' in navigator)")

fun wasmHasWasmSIMD(): Boolean = js("WebAssembly.validate(new Uint8Array([0, 97, 115, 109, 1, 0, 0, 0, 1, 5, 1, 96, 0, 1, 123, 3, 2, 1, 0, 10, 10, 1, 8, 0, 65, 0, 253, 15, 253, 98, 11]))")

fun wasmHasWasmThreads(): Boolean = js("typeof SharedArrayBuffer !== 'undefined'")

private fun checkDynamicImportSupport(): Unit = js("new Function('import(\"\")')")

fun wasmHasDynamicImport(): Boolean = try {
    checkDynamicImportSupport()
    true
} catch (e: Throwable) {
    false
}

fun wasmGetScreenWidth(): Int = window.screen.width
fun wasmGetScreenHeight(): Int = window.screen.height
fun wasmGetDevicePixelRatio(): Double = window.devicePixelRatio
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

external interface WasmDOMRect : JsAny {
    val x: Double
    val y: Double
    val width: Double
    val height: Double
    val top: Double
    val right: Double
    val bottom: Double
    val left: Double
}



external val JSON: JSONClass

external interface JSONClass {
    fun parse(text: String): JsAny
}

private fun getKeys(o: JsAny): JsArray<JsString> = Object.keys(o)


private fun getDynamic(o: JsAny, key: JsString): JsAny? = Reflect.get(o, key)

private fun setDynamic(o: JsAny, key: JsString, value: JsAny?) {
    Reflect.set(o, key, value)
}


external object Reflect {
    fun get(target: JsAny, propertyKey: JsString): JsAny?
    fun set(target: JsAny, propertyKey: JsString, value: JsAny?): Boolean
}

external object Object {
    fun keys(o: JsAny): JsArray<JsString>
}

// Restoring wasmExecuteCallback function which is used by the example project.
fun wasmExecuteCallback(callbackId: String): Boolean {
    return try {
        CallbackRegistry.executeCallback(callbackId)
        true
    } catch (e: Throwable) {
        console.error("[Summon WASM] wasmExecuteCallback failed: ${e.message}")
        false
    }
}

fun wasmGetSummonState(): String? = js("window.__SUMMON_STATE__ ? JSON.stringify(window.__SUMMON_STATE__) : null")








