package code.yousef.summon.runtime

import code.yousef.summon.runtime.wasmConsoleLog
import code.yousef.summon.runtime.wasmConsoleWarn
import code.yousef.summon.runtime.wasmConsoleError

/**
 * WebAssembly implementation of DOMProvider.
 */
actual object DOMProvider {
    actual val window: DOMWindow = WasmDOMWindow()
    actual val document: DOMDocument = WasmDOMDocument()

    actual fun createElementFromNative(nativeElement: Any): DOMElement {
        // In WASM, nativeElement should be a string ID
        val elementId = nativeElement.toString()
        val tagName = wasmGetElementTagName(elementId) ?: "div"
        return WasmDOMElement(tagName, elementId, elementId)
    }

    actual fun getNativeElement(element: DOMElement): Any {
        return (element as WasmDOMElement).nativeElementId
    }

    actual fun getNativeElementId(element: DOMElement): String {
        return (element as WasmDOMElement).nativeElementId
    }
}

/**
 * WASM implementation of DOMWindow.
 */
private class WasmDOMWindow : DOMWindow {
    override val document: DOMDocument = WasmDOMDocument()
    override val location: DOMLocation = WasmDOMLocation()
    override val history: DOMHistory = WasmDOMHistory()

    override var innerWidth: Int
        get() = wasmGetScreenWidth()
        set(_) = Unit

    override var innerHeight: Int
        get() = wasmGetScreenHeight()
        set(_) = Unit

    override var outerWidth: Int
        get() = wasmGetScreenWidth()
        set(_) = Unit

    override var outerHeight: Int
        get() = wasmGetScreenHeight()
        set(_) = Unit

    override fun alert(message: String) {
        wasmConsoleLog("ALERT: $message")
    }

    override fun confirm(message: String): Boolean {
        wasmConsoleLog("CONFIRM: $message")
        return true // Simplified
    }

    override fun prompt(message: String, defaultValue: String): String? {
        wasmConsoleLog("PROMPT: $message")
        return defaultValue // Simplified
    }

    override fun setTimeout(callback: () -> Unit, delay: Int): Int {
        // Simplified implementation - would need proper timer support
        callback()
        return 1
    }

    override fun clearTimeout(timeoutId: Int) {
        // No-op in simplified implementation
    }

    override fun setInterval(callback: () -> Unit, delay: Int): Int {
        // Simplified implementation - would need proper timer support
        return 1
    }

    override fun clearInterval(intervalId: Int) {
        // No-op in simplified implementation
    }

    override fun requestAnimationFrame(callback: () -> Unit): Int {
        // Simplified implementation - would need proper RAF support
        callback()
        return 1
    }

    override fun cancelAnimationFrame(requestId: Int) {
        // No-op in simplified implementation
    }

    override fun addEventListener(type: String, listener: (event: Any) -> Unit) {
        // Would need global event handler registration
    }

    override fun removeEventListener(type: String, listener: (event: Any) -> Unit) {
        // Would need global event handler tracking
    }
}

/**
 * WASM implementation of DOMDocument.
 */
private class WasmDOMDocument : DOMDocument {
    override val body: DOMElement?
        get() {
            val bodyId = wasmGetDocumentBodyId()
            return if (bodyId != null) {
                WasmDOMElement("body", bodyId, bodyId)
            } else null
        }

    override val head: DOMElement?
        get() {
            val headId = wasmGetDocumentHeadId()
            return if (headId != null) {
                WasmDOMElement("head", headId, headId)
            } else null
        }

    override val documentElement: DOMElement?
        get() {
            // Assume HTML element exists
            val htmlId = wasmQuerySelectorGetId("html")
            return if (htmlId != null) {
                WasmDOMElement("html", htmlId, htmlId)
            } else null
        }

    override fun createElement(tagName: String): DOMElement {
        val elementId = wasmCreateElementById(tagName)
        return WasmDOMElement(tagName, elementId, elementId)
    }

    override fun createTextNode(text: String): DOMElement {
        // Create a text node as a special element
        val textId = wasmCreateElementById("text")
        wasmSetElementTextContent(textId, text)
        return WasmDOMElement("text", textId, textId)
    }

    override fun getElementById(id: String): DOMElement? {
        val elementId = wasmGetElementById(id)
        return if (elementId != null) {
            val tagName = wasmGetElementTagName(elementId) ?: "div"
            WasmDOMElement(tagName, elementId, elementId)
        } else null
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

    override fun addEventListener(type: String, listener: (event: Any) -> Unit) {
        // Would need global document event handler registration
    }

    override fun removeEventListener(type: String, listener: (event: Any) -> Unit) {
        // Would need global document event handler tracking
    }
}

/**
 * WASM implementation of DOMLocation.
 */
private class WasmDOMLocation : DOMLocation {
    override var href: String
        get() = wasmGetLocationHref() ?: ""
        set(_) = Unit // Read-only in simplified implementation

    override var protocol: String
        get() = href.substringBefore("://") + "://"
        set(_) = Unit

    override var host: String
        get() = href.substringAfter("://").substringBefore("/")
        set(_) = Unit

    override var hostname: String
        get() = host.substringBefore(":")
        set(_) = Unit

    override var port: String
        get() = if (":" in host) host.substringAfter(":") else ""
        set(_) = Unit

    override var pathname: String
        get() = "/" + href.substringAfter("://").substringAfter("/")
        set(_) = Unit

    override var search: String
        get() = if ("?" in href) "?" + href.substringAfter("?").substringBefore("#") else ""
        set(_) = Unit

    override var hash: String
        get() = if ("#" in href) "#" + href.substringAfter("#") else ""
        set(_) = Unit

    override fun assign(url: String) {
        // Would need navigation support
    }

    override fun replace(url: String) {
        // Would need navigation support
    }

    override fun reload() {
        // Would need reload support
    }
}

/**
 * WASM implementation of DOMHistory.
 */
private class WasmDOMHistory : DOMHistory {
    override val length: Int = 1 // Simplified

    override val state: Any? = null // Simplified

    override fun back() {
        // Would need history API support
    }

    override fun forward() {
        // Would need history API support
    }

    override fun go(delta: Int) {
        // Would need history API support
    }

    override fun pushState(state: Any?, title: String?, url: String?) {
        // Would need history API support
    }

    override fun replaceState(state: Any?, title: String?, url: String?) {
        // Would need history API support
    }
}