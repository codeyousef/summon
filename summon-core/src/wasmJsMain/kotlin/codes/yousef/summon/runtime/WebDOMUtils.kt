package codes.yousef.summon.runtime

import kotlinx.browser.window
import kotlinx.browser.document
import org.w3c.dom.*

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
            is WasmDOMElementWrapper -> {
                /*
                try {
                    wasmConsoleLog("getNativeElementId: WasmDOMElementWrapper with ID: ${element.elementId}")
                } catch (e: Throwable) {
                    // Ignore logging errors in test environment
                }
                */
                element.elementId
            }

            else -> {
                val elementType = element::class.simpleName ?: "Unknown"
                val elementString = element.toString()
                try {
                    wasmConsoleError("getNativeElementId: Unsupported element type: $elementType")
                    wasmConsoleError("Element details: $elementString")
                    wasmConsoleError("Expected: WasmDOMElementWrapper, Got: $elementType")
                } catch (e: Throwable) {
                    // Ignore logging errors in test environment
                }
                throw IllegalArgumentException("Unknown element type: $elementType (Expected WasmDOMElementWrapper)")
            }
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
        val childId = DOMProvider.getNativeElementId(child)
        wasmAppendChildById(elementId, childId)
    }

    override fun removeChild(child: DOMElement) {
        val childId = DOMProvider.getNativeElementId(child)
        wasmRemoveChildById(elementId, childId)
    }

    override fun insertBefore(newChild: DOMElement, referenceChild: DOMElement?) {
        val newChildId = DOMProvider.getNativeElementId(newChild)
        val refChildId = referenceChild?.let { DOMProvider.getNativeElementId(it) }
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
        get() = window.innerWidth
        set(value) { /* Read-only */ }

    override var innerHeight: Int
        get() = window.innerHeight
        set(value) { /* Read-only */ }

    override var outerWidth: Int
        get() = window.outerWidth
        set(value) { /* Read-only */ }

    override var outerHeight: Int
        get() = window.outerHeight
        set(value) { /* Read-only */ }

    override fun alert(message: String) {
        window.alert(message)
    }

    override fun confirm(message: String): Boolean {
        return window.confirm(message)
    }

    override fun prompt(message: String, defaultValue: String): String? {
        return window.prompt(message, defaultValue)
    }

    override fun setTimeout(callback: () -> Unit, delay: Int): Int {
        return window.setTimeout({ callback(); null }, delay)
    }

    override fun clearTimeout(timeoutId: Int) {
        window.clearTimeout(timeoutId)
    }

    override fun setInterval(callback: () -> Unit, delay: Int): Int {
        return window.setInterval({ callback(); null }, delay)
    }


    override fun clearInterval(intervalId: Int) {
        window.clearInterval(intervalId)
    }

    override fun requestAnimationFrame(callback: () -> Unit): Int {
        return window.requestAnimationFrame { callback() }
    }

    override fun cancelAnimationFrame(requestId: Int) {
        window.cancelAnimationFrame(requestId)
    }

    override fun addEventListener(type: String, listener: (event: Any) -> Unit) {
        // We need to wrap the listener to convert Event to Any
        // But DOMWindow.addEventListener expects (Any) -> Unit
        // window.addEventListener expects EventListener or (Event) -> Unit
        // We can cast or wrap.
        window.addEventListener(type, { event -> listener(event) })
    }

    override fun removeEventListener(type: String, listener: (event: Any) -> Unit) {
        // Removing is tricky with lambdas unless we stored the wrapper.
        // For now, we might not support removal correctly if we wrap.
        // But if listener IS the wrapper...
        // Let's assume for now we just add.
        // Or we can use the same logic as WasmRuntime.kt if we want.
        // But window.removeEventListener requires the exact same function instance.
    }
}


/**
 * WASM wrapper for Location.
 */
private class WasmLocationWrapper : DOMLocation {
    override var href: String
        get() = window.location.href
        set(value) {
            window.location.href = value
        }

    override var protocol: String
        get() = window.location.protocol
        set(value) { /* Read-only in most cases */ }

    override var host: String
        get() = window.location.host
        set(value) { /* Read-only in most cases */ }

    override var hostname: String
        get() = window.location.hostname
        set(value) { /* Read-only in most cases */ }

    override var port: String
        get() = window.location.port
        set(value) { /* Read-only in most cases */ }

    override var pathname: String
        get() = window.location.pathname
        set(value) {
            window.location.pathname = value
        }

    override var search: String
        get() = window.location.search
        set(value) {
            window.location.search = value
        }

    override var hash: String
        get() = window.location.hash
        set(value) {
            window.location.hash = value
        }

    override fun assign(url: String) {
        window.location.assign(url)
    }

    override fun replace(url: String) {
        window.location.replace(url)
    }

    override fun reload() {
        window.location.reload()
    }
}


/**
 * WASM wrapper for History.
 */
private class WasmHistoryWrapper : DOMHistory {
    override val length: Int
        get() = window.history.length

    override val state: Any?
        get() = window.history.state

    override fun back() {
        window.history.back()
    }

    override fun forward() {
        window.history.forward()
    }

    override fun go(delta: Int) {
        window.history.go(delta)
    }

    override fun pushState(state: Any?, title: String?, url: String?) {
        window.history.pushState(state?.toString()?.toJsString(), title ?: "", url)
    }

    override fun replaceState(state: Any?, title: String?, url: String?) {
        window.history.replaceState(state?.toString()?.toJsString(), title ?: "", url)
    }


}

