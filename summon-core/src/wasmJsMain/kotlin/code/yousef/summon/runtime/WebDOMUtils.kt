package code.yousef.summon.runtime

/**
 * WebAssembly implementation of WebDOMUtils platform-specific functions.
 * Uses external declarations to interact with JavaScript.
 */

actual fun scrollIntoViewPlatform(nativeElementId: String, behavior: String) {
    wasmScrollIntoView(nativeElementId, behavior)
}

actual fun getComputedStylePlatform(nativeElementId: String, property: String): String? {
    return wasmGetComputedStyle(nativeElementId, property)
}

/**
 * WASM implementation of DOMProvider.
 */
actual object DOMProvider {
    actual val window: DOMWindow = WasmWindowWrapper()
    actual val document: DOMDocument = WasmDocumentWrapper()

    actual fun createElementFromNative(nativeElement: Any): DOMElement {
        // In WASM, we work with element IDs as strings
        return WasmDOMElementWrapper(nativeElement as String)
    }

    actual fun getNativeElement(element: DOMElement): Any {
        return when (element) {
            is WasmDOMElementWrapper -> element.elementId
            else -> throw IllegalArgumentException("Unknown element type")
        }
    }

    actual fun getNativeElementId(element: DOMElement): String {
        return when (element) {
            is WasmDOMElementWrapper -> element.elementId
            else -> throw IllegalArgumentException("Unknown element type")
        }
    }
}

/**
 * WASM wrapper for DOM Element using element IDs.
 */
private class WasmDOMElementWrapper(val elementId: String) : DOMElement {
    override val id: String get() = wasmGetElementId(elementId) ?: ""
    override val tagName: String get() = wasmGetElementTagName(elementId) ?: ""
    override val className: String get() = wasmGetElementClassName(elementId) ?: ""
    override val textContent: String? get() = wasmGetElementTextContent(elementId)

    override fun setAttribute(name: String, value: String) {
        wasmSetElementAttribute(elementId, name, value)
    }

    override fun getAttribute(name: String): String? {
        return wasmGetElementAttribute(elementId, name)
    }

    override fun removeAttribute(name: String) {
        wasmRemoveElementAttribute(elementId, name)
    }

    override fun hasAttribute(name: String): Boolean {
        return wasmGetElementAttribute(elementId, name) != null
    }

    override fun appendChild(child: DOMElement) {
        val childId = (child as WasmDOMElementWrapper).elementId
        wasmAppendChildById(elementId, childId)
    }

    override fun removeChild(child: DOMElement) {
        val childId = (child as WasmDOMElementWrapper).elementId
        wasmRemoveChildById(elementId, childId)
    }

    override fun insertBefore(newChild: DOMElement, referenceChild: DOMElement?) {
        val newChildId = (newChild as WasmDOMElementWrapper).elementId
        val refChildId = (referenceChild as? WasmDOMElementWrapper)?.elementId
        if (refChildId != null) {
            wasmInsertBeforeById(elementId, newChildId, refChildId)
        } else {
            wasmAppendChildById(elementId, newChildId)
        }
    }

    private val eventListeners = mutableMapOf<String, MutableList<(Any) -> Unit>>()

    override fun addEventListener(type: String, listener: (event: Any) -> Unit) {
        val handlerId = "$elementId:$type:${listener.hashCode()}"
        wasmConsoleLog("Adding event listener: $type on element $elementId with handler $handlerId")
        eventListeners.getOrPut(type) { mutableListOf() }.add(listener)
        wasmAddEventListenerById(elementId, type, handlerId)
        // Register the callback so JavaScript can call back to Kotlin
        registerWasmEventCallback(handlerId) {
            try {
                wasmConsoleLog("Kotlin callback executed for handler: $handlerId")
                listener(Unit) // Pass a dummy event object for now
            } catch (e: Exception) {
                wasmConsoleError("Event listener failed: ${e.message}")
            }
        }
        wasmConsoleLog("Event listener registration complete for: $handlerId")
    }

    override fun removeEventListener(type: String, listener: (event: Any) -> Unit) {
        val handlerId = "$elementId:$type:${listener.hashCode()}"
        eventListeners[type]?.remove(listener)
        wasmRemoveEventListenerById(elementId, type, handlerId)
    }

    override fun querySelector(selector: String): DOMElement? {
        val foundId = wasmQuerySelectorGetId(selector)
        return foundId?.let { WasmDOMElementWrapper(it) }
    }

    override fun querySelectorAll(selector: String): List<DOMElement> {
        val idsString = wasmQuerySelectorAllGetIds(selector)
        if (idsString.isEmpty()) return emptyList()
        return idsString.split(",").map { WasmDOMElementWrapper(it) }
    }

    override fun getBoundingClientRect(): DOMRect {
        val left = wasmGetElementBoundingLeft(elementId)
        val top = wasmGetElementBoundingTop(elementId)
        val right = wasmGetElementBoundingRight(elementId)
        val bottom = wasmGetElementBoundingBottom(elementId)
        val width = wasmGetElementBoundingWidth(elementId)
        val height = wasmGetElementBoundingHeight(elementId)

        return DOMRect(
            left = left,
            top = top,
            right = right,
            bottom = bottom,
            width = width,
            height = height
        )
    }

    override fun focus() {
        wasmFocusElement(elementId)
    }

    override fun blur() {
        wasmBlurElement(elementId)
    }

    override fun click() {
        wasmClickElement(elementId)
    }
}

/**
 * WASM wrapper for Document.
 */
private class WasmDocumentWrapper : DOMDocument {
    override val body: DOMElement?
        get() = wasmGetDocumentBodyId()?.let { WasmDOMElementWrapper(it) }

    override val head: DOMElement?
        get() = wasmGetDocumentHeadId()?.let { WasmDOMElementWrapper(it) }

    override val documentElement: DOMElement?
        get() = wasmGetDocumentElementId()?.let { WasmDOMElementWrapper(it) }

    override fun createElement(tagName: String): DOMElement {
        val elementId = wasmCreateElementById(tagName)
        return WasmDOMElementWrapper(elementId)
    }

    override fun createTextNode(text: String): DOMElement {
        val elementId = wasmCreateTextNode(text)
        return WasmDOMElementWrapper(elementId)
    }

    override fun getElementById(id: String): DOMElement? {
        val elementId = wasmGetElementById(id)
        return elementId?.let { WasmDOMElementWrapper(it) }
    }

    override fun querySelector(selector: String): DOMElement? {
        val elementId = wasmQuerySelectorGetId(selector)
        return elementId?.let { WasmDOMElementWrapper(it) }
    }

    override fun querySelectorAll(selector: String): List<DOMElement> {
        val idsString = wasmQuerySelectorAllGetIds(selector)
        if (idsString.isEmpty()) return emptyList()
        return idsString.split(",").map { WasmDOMElementWrapper(it) }
    }

    override fun addEventListener(type: String, listener: (event: Any) -> Unit) {
        val handlerId = "document:$type:${listener.hashCode()}"
        wasmAddDocumentEventListener(type, handlerId)
    }

    override fun removeEventListener(type: String, listener: (event: Any) -> Unit) {
        val handlerId = "document:$type:${listener.hashCode()}"
        wasmRemoveDocumentEventListener(type, handlerId)
    }
}

/**
 * WASM wrapper for Window.
 */
private class WasmWindowWrapper : DOMWindow {
    override val document: DOMDocument = WasmDocumentWrapper()
    override val location: DOMLocation = WasmLocationWrapper()
    override val history: DOMHistory = WasmHistoryWrapper()

    override var innerWidth: Int
        get() = wasmGetWindowInnerWidth()
        set(value) { /* Read-only */ }

    override var innerHeight: Int
        get() = wasmGetWindowInnerHeight()
        set(value) { /* Read-only */ }

    override var outerWidth: Int
        get() = wasmGetWindowOuterWidth()
        set(value) { /* Read-only */ }

    override var outerHeight: Int
        get() = wasmGetWindowOuterHeight()
        set(value) { /* Read-only */ }

    override fun alert(message: String) {
        wasmWindowAlert(message)
    }

    override fun confirm(message: String): Boolean {
        return wasmWindowConfirm(message)
    }

    override fun prompt(message: String, defaultValue: String): String? {
        return wasmWindowPrompt(message, defaultValue)
    }

    override fun setTimeout(callback: () -> Unit, delay: Int): Int {
        val timeoutId = wasmSetTimeout(delay)
        // Store callback somehow - this would need a callback registry
        return timeoutId
    }

    override fun clearTimeout(timeoutId: Int) {
        wasmClearTimeout(timeoutId)
    }

    override fun setInterval(callback: () -> Unit, delay: Int): Int {
        val intervalId = wasmSetInterval(delay)
        // Store callback somehow - this would need a callback registry
        return intervalId
    }

    override fun clearInterval(intervalId: Int) {
        wasmClearInterval(intervalId)
    }

    override fun requestAnimationFrame(callback: () -> Unit): Int {
        val requestId = wasmRequestAnimationFrame()
        // Store callback somehow - this would need a callback registry
        return requestId
    }

    override fun cancelAnimationFrame(requestId: Int) {
        wasmCancelAnimationFrame(requestId)
    }

    override fun addEventListener(type: String, listener: (event: Any) -> Unit) {
        val handlerId = "window:$type:${listener.hashCode()}"
        wasmAddWindowEventListener(type, handlerId)
    }

    override fun removeEventListener(type: String, listener: (event: Any) -> Unit) {
        val handlerId = "window:$type:${listener.hashCode()}"
        wasmRemoveWindowEventListener(type, handlerId)
    }
}

/**
 * WASM wrapper for Location.
 */
private class WasmLocationWrapper : DOMLocation {
    override var href: String
        get() = wasmGetLocationHref() ?: ""
        set(value) {
            wasmSetLocationHref(value)
        }

    override var protocol: String
        get() = wasmGetLocationProtocol() ?: ""
        set(value) { /* Read-only in most cases */ }

    override var host: String
        get() = wasmGetLocationHost() ?: ""
        set(value) { /* Read-only in most cases */ }

    override var hostname: String
        get() = wasmGetLocationHostname() ?: ""
        set(value) { /* Read-only in most cases */ }

    override var port: String
        get() = wasmGetLocationPort() ?: ""
        set(value) { /* Read-only in most cases */ }

    override var pathname: String
        get() = wasmGetLocationPathname() ?: ""
        set(value) {
            wasmSetLocationPathname(value)
        }

    override var search: String
        get() = wasmGetLocationSearch() ?: ""
        set(value) {
            wasmSetLocationSearch(value)
        }

    override var hash: String
        get() = wasmGetLocationHash() ?: ""
        set(value) {
            wasmSetLocationHash(value)
        }

    override fun assign(url: String) {
        wasmLocationAssign(url)
    }

    override fun replace(url: String) {
        wasmLocationReplace(url)
    }

    override fun reload() {
        wasmLocationReload()
    }
}

/**
 * WASM wrapper for History.
 */
private class WasmHistoryWrapper : DOMHistory {
    override val length: Int get() = wasmGetHistoryLength()
    override val state: Any? get() = wasmGetHistoryState()

    override fun back() {
        wasmHistoryBack()
    }

    override fun forward() {
        wasmHistoryForward()
    }

    override fun go(delta: Int) {
        wasmHistoryGo(delta)
    }

    override fun pushState(state: Any?, title: String?, url: String?) {
        wasmHistoryPushState(state?.toString() ?: "", title ?: "", url ?: "")
    }

    override fun replaceState(state: Any?, title: String?, url: String?) {
        wasmHistoryReplaceState(state?.toString() ?: "", title ?: "", url ?: "")
    }
}

// Additional external declarations needed for WebDOMUtils
external fun wasmScrollIntoView(elementId: String, behavior: String)
external fun wasmGetComputedStyle(elementId: String, property: String): String?
external fun wasmInsertBeforeById(parentId: String, newChildId: String, refChildId: String): Boolean
external fun wasmGetElementBoundingLeft(elementId: String): Double
external fun wasmGetElementBoundingTop(elementId: String): Double
external fun wasmGetElementBoundingRight(elementId: String): Double
external fun wasmGetElementBoundingBottom(elementId: String): Double
external fun wasmGetElementBoundingWidth(elementId: String): Double
external fun wasmGetElementBoundingHeight(elementId: String): Double
external fun wasmFocusElement(elementId: String)
external fun wasmBlurElement(elementId: String)
external fun wasmClickElement(elementId: String)
external fun wasmCreateTextNode(text: String): String
external fun wasmGetDocumentElementId(): String?
external fun wasmAddDocumentEventListener(type: String, handlerId: String)
external fun wasmRemoveDocumentEventListener(type: String, handlerId: String)
external fun wasmGetElementParent(elementId: String): String?
external fun wasmGetWindowInnerWidth(): Int
external fun wasmGetWindowInnerHeight(): Int
external fun wasmGetWindowOuterWidth(): Int
external fun wasmGetWindowOuterHeight(): Int
external fun wasmWindowAlert(message: String)
external fun wasmWindowConfirm(message: String): Boolean
external fun wasmWindowPrompt(message: String, defaultValue: String): String?
external fun wasmSetTimeout(delay: Int): Int
external fun wasmClearTimeout(timeoutId: Int)
external fun wasmSetInterval(delay: Int): Int
external fun wasmClearInterval(intervalId: Int)
external fun wasmRequestAnimationFrame(): Int
external fun wasmCancelAnimationFrame(requestId: Int)
external fun wasmAddWindowEventListener(type: String, handlerId: String)
external fun wasmRemoveWindowEventListener(type: String, handlerId: String)
external fun wasmGetLocationProtocol(): String?
external fun wasmGetLocationHost(): String?
external fun wasmGetLocationHostname(): String?
external fun wasmGetLocationPort(): String?
external fun wasmGetLocationPathname(): String?
external fun wasmGetLocationSearch(): String?
external fun wasmGetLocationHash(): String?
external fun wasmSetLocationHref(href: String)
external fun wasmSetLocationPathname(pathname: String)
external fun wasmSetLocationSearch(search: String)
external fun wasmSetLocationHash(hash: String)
external fun wasmLocationAssign(url: String)
external fun wasmLocationReplace(url: String)
external fun wasmLocationReload()
external fun wasmGetHistoryLength(): Int
external fun wasmGetHistoryState(): String?
external fun wasmHistoryBack()
external fun wasmHistoryForward()
external fun wasmHistoryGo(delta: Int)
external fun wasmHistoryPushState(state: String, title: String, url: String)
external fun wasmHistoryReplaceState(state: String, title: String, url: String)