package codes.yousef.summon.runtime

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event

/**
 * JavaScript implementation of WebDOMUtils platform-specific functions.
 */

@JsName("scrollIntoView")
actual fun scrollIntoViewPlatform(nativeElementId: String, behavior: String) {
    js(
        """
        var element = document.getElementById(nativeElementId);
        if (element) {
            element.scrollIntoView({ behavior: behavior });
        }
    """
    )
}

@JsName("getComputedStyle")
actual fun getComputedStylePlatform(nativeElementId: String, property: String): String? {
    return js(
        """
        var element = document.getElementById(nativeElementId);
        if (element) {
            var computed = window.getComputedStyle(element);
            return computed.getPropertyValue(property);
        }
        return null;
    """
    ) as? String
}

/**
 * JavaScript implementation of DOMProvider.
 */
actual object DOMProvider {
    actual val window: DOMWindow = JSWindowWrapper()
    actual val document: DOMDocument = JSDocumentWrapper()

    actual fun createElementFromNative(nativeElement: Any): DOMElement {
        return JSDOMElementWrapper(nativeElement as Element)
    }

    actual fun getNativeElement(element: DOMElement): Any {
        return when (element) {
            is JSDOMElementWrapper -> element.element
            else -> throw IllegalArgumentException("Unknown element type")
        }
    }

    actual fun getNativeElementId(element: DOMElement): String {
        return when (element) {
            is JSDOMElementWrapper -> element.element.id
            else -> throw IllegalArgumentException("Unknown element type")
        }
    }
}

/**
 * JavaScript wrapper for DOM Element.
 */
private class JSDOMElementWrapper(val element: Element) : DOMElement {
    override val id: String get() = element.id
    override val tagName: String get() = element.tagName
    override val className: String get() = element.className
    override val textContent: String? get() = element.textContent

    override fun setAttribute(name: String, value: String) {
        element.setAttribute(name, value)
    }

    override fun getAttribute(name: String): String? {
        return element.getAttribute(name)
    }

    override fun removeAttribute(name: String) {
        element.removeAttribute(name)
    }

    override fun hasAttribute(name: String): Boolean {
        return element.hasAttribute(name)
    }

    override fun appendChild(child: DOMElement) {
        val childElement = (child as JSDOMElementWrapper).element
        element.appendChild(childElement)
    }

    override fun removeChild(child: DOMElement) {
        val childElement = (child as JSDOMElementWrapper).element
        element.removeChild(childElement)
    }

    override fun insertBefore(newChild: DOMElement, referenceChild: DOMElement?) {
        val newElement = (newChild as JSDOMElementWrapper).element
        val refElement = (referenceChild as? JSDOMElementWrapper)?.element
        element.insertBefore(newElement, refElement)
    }

    override fun addEventListener(type: String, listener: (event: Any) -> Unit) {
        element.addEventListener(type, { event -> listener(event) })
    }

    override fun removeEventListener(type: String, listener: (event: Any) -> Unit) {
        element.removeEventListener(type, listener as ((Event) -> Unit))
    }

    override fun querySelector(selector: String): DOMElement? {
        return element.querySelector(selector)?.let { JSDOMElementWrapper(it) }
    }

    override fun querySelectorAll(selector: String): List<DOMElement> {
        val nodeList = element.querySelectorAll(selector)
        return (0 until nodeList.length).mapNotNull {
            val node = nodeList.item(it)
            if (node is org.w3c.dom.Element) JSDOMElementWrapper(node) else null
        }
    }

    override fun getBoundingClientRect(): DOMRect {
        val rect = element.getBoundingClientRect()
        return DOMRect(
            left = rect.left,
            top = rect.top,
            right = rect.right,
            bottom = rect.bottom,
            width = rect.width,
            height = rect.height
        )
    }

    override fun focus() {
        (element as? HTMLElement)?.focus()
    }

    override fun blur() {
        (element as? HTMLElement)?.blur()
    }

    override fun click() {
        (element as? HTMLElement)?.click()
    }
}

/**
 * JavaScript wrapper for Document.
 */
private class JSDocumentWrapper : DOMDocument {
    override val body: DOMElement? get() = document.body?.let { JSDOMElementWrapper(it) }
    override val head: DOMElement? get() = document.head?.let { JSDOMElementWrapper(it) }
    override val documentElement: DOMElement? get() = document.documentElement?.let { JSDOMElementWrapper(it) }

    override fun createElement(tagName: String): DOMElement {
        return JSDOMElementWrapper(document.createElement(tagName))
    }

    override fun createTextNode(text: String): DOMElement {
        // Wrap text node in a special span for consistency
        val span = document.createElement("span")
        span.textContent = text
        return JSDOMElementWrapper(span)
    }

    override fun getElementById(id: String): DOMElement? {
        return document.getElementById(id)?.let { JSDOMElementWrapper(it) }
    }

    override fun querySelector(selector: String): DOMElement? {
        return document.querySelector(selector)?.let { JSDOMElementWrapper(it) }
    }

    override fun querySelectorAll(selector: String): List<DOMElement> {
        val nodeList = document.querySelectorAll(selector)
        return (0 until nodeList.length).mapNotNull {
            val node = nodeList.item(it)
            if (node is org.w3c.dom.Element) JSDOMElementWrapper(node) else null
        }
    }

    override fun addEventListener(type: String, listener: (event: Any) -> Unit) {
        document.addEventListener(type, { event -> listener(event) })
    }

    override fun removeEventListener(type: String, listener: (event: Any) -> Unit) {
        document.removeEventListener(type, listener as ((Event) -> Unit))
    }
}

/**
 * JavaScript wrapper for Window.
 */
private class JSWindowWrapper : DOMWindow {
    override val document: DOMDocument = JSDocumentWrapper()
    override val location: DOMLocation = JSLocationWrapper()
    override val history: DOMHistory = JSHistoryWrapper()

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
        return window.setTimeout(callback, delay)
    }

    override fun clearTimeout(timeoutId: Int) {
        window.clearTimeout(timeoutId)
    }

    override fun setInterval(callback: () -> Unit, delay: Int): Int {
        return window.setInterval(callback, delay)
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
        window.addEventListener(type, { event -> listener(event) })
    }

    override fun removeEventListener(type: String, listener: (event: Any) -> Unit) {
        window.removeEventListener(type, listener as ((Event) -> Unit))
    }
}

/**
 * JavaScript wrapper for Location.
 */
private class JSLocationWrapper : DOMLocation {
    override var href: String
        get() = window.location.href
        set(value) {
            window.location.href = value
        }

    override var protocol: String
        get() = window.location.protocol
        set(value) {
            window.location.protocol = value
        }

    override var host: String
        get() = window.location.host
        set(value) {
            window.location.host = value
        }

    override var hostname: String
        get() = window.location.hostname
        set(value) {
            window.location.hostname = value
        }

    override var port: String
        get() = window.location.port
        set(value) {
            window.location.port = value
        }

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
 * JavaScript wrapper for History.
 */
private class JSHistoryWrapper : DOMHistory {
    override val length: Int get() = window.history.length
    override val state: Any? get() = window.history.state

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
        window.history.pushState(state, title ?: "", url)
    }

    override fun replaceState(state: Any?, title: String?, url: String?) {
        window.history.replaceState(state, title ?: "", url)
    }
}

/**
 * Console implementation for JS.
 */
actual object console {
    actual fun log(message: Any?) {
        js("console.log(message)")
    }

    actual fun warn(message: Any?) {
        js("console.warn(message)")
    }

    actual fun error(message: Any?) {
        js("console.error(message)")
    }

    actual fun info(message: Any?) {
        js("console.info(message)")
    }

    actual fun debug(message: Any?) {
        js("console.debug(message)")
    }
}