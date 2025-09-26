package code.yousef.summon.runtime

import code.yousef.summon.core.getCurrentTimeMillis

/**
 * Shared DOM utilities for web platforms (JavaScript and WebAssembly).
 *
 * This module provides common DOM manipulation functions that work across
 * both JS and WASM targets, abstracting away platform-specific differences.
 *
 * @since 0.3.3.0
 */

/**
 * Platform-specific implementations for DOM operations that use js() calls.
 * For JS target: nativeElementId can be the actual element
 * For WASM target: nativeElementId should be the element's string ID
 */
expect fun scrollIntoViewPlatform(nativeElementId: String, behavior: String)
expect fun getComputedStylePlatform(nativeElementId: String, property: String): String?

/**
 * Represents a DOM element abstraction for web platforms.
 */
interface DOMElement {
    val id: String
    val tagName: String
    val className: String
    val textContent: String?

    fun setAttribute(name: String, value: String)
    fun getAttribute(name: String): String?
    fun removeAttribute(name: String)
    fun hasAttribute(name: String): Boolean

    fun appendChild(child: DOMElement)
    fun removeChild(child: DOMElement)
    fun insertBefore(newChild: DOMElement, referenceChild: DOMElement?)

    fun addEventListener(type: String, listener: (event: Any) -> Unit)
    fun removeEventListener(type: String, listener: (event: Any) -> Unit)

    fun querySelector(selector: String): DOMElement?
    fun querySelectorAll(selector: String): List<DOMElement>

    fun getBoundingClientRect(): DOMRect
    fun focus()
    fun blur()
    fun click()
}

/**
 * Represents element bounds and positioning.
 */
data class DOMRect(
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double,
    val width: Double,
    val height: Double
)

/**
 * Common DOM element properties for form inputs.
 */
interface DOMInput : DOMElement {
    var value: String
    var disabled: Boolean
    var checked: Boolean
    var selected: Boolean
    var placeholder: String
}

/**
 * Document abstraction for DOM operations.
 */
interface DOMDocument {
    val body: DOMElement?
    val head: DOMElement?
    val documentElement: DOMElement?

    fun createElement(tagName: String): DOMElement
    fun createTextNode(text: String): DOMElement
    fun getElementById(id: String): DOMElement?
    fun querySelector(selector: String): DOMElement?
    fun querySelectorAll(selector: String): List<DOMElement>

    fun addEventListener(type: String, listener: (event: Any) -> Unit)
    fun removeEventListener(type: String, listener: (event: Any) -> Unit)
}

/**
 * Window abstraction for browser APIs.
 */
interface DOMWindow {
    val document: DOMDocument
    val location: DOMLocation
    val history: DOMHistory

    var innerWidth: Int
    var innerHeight: Int
    var outerWidth: Int
    var outerHeight: Int

    fun alert(message: String)
    fun confirm(message: String): Boolean
    fun prompt(message: String, defaultValue: String = ""): String?

    fun setTimeout(callback: () -> Unit, delay: Int): Int
    fun clearTimeout(timeoutId: Int)
    fun setInterval(callback: () -> Unit, delay: Int): Int
    fun clearInterval(intervalId: Int)

    fun requestAnimationFrame(callback: () -> Unit): Int
    fun cancelAnimationFrame(requestId: Int)

    fun addEventListener(type: String, listener: (event: Any) -> Unit)
    fun removeEventListener(type: String, listener: (event: Any) -> Unit)
}

/**
 * Location API abstraction.
 */
interface DOMLocation {
    var href: String
    var protocol: String
    var host: String
    var hostname: String
    var port: String
    var pathname: String
    var search: String
    var hash: String

    fun assign(url: String)
    fun replace(url: String)
    fun reload()
}

/**
 * History API abstraction.
 */
interface DOMHistory {
    val length: Int
    val state: Any?

    fun back()
    fun forward()
    fun go(delta: Int)
    fun pushState(state: Any?, title: String?, url: String?)
    fun replaceState(state: Any?, title: String?, url: String?)
}

/**
 * Platform-specific DOM API provider.
 * Implementations provide access to the actual DOM APIs.
 */
expect object DOMProvider {
    val window: DOMWindow
    val document: DOMDocument

    fun createElementFromNative(nativeElement: Any): DOMElement
    fun getNativeElement(element: DOMElement): Any
    fun getNativeElementId(element: DOMElement): String
}

/**
 * Utility functions for common DOM operations.
 */
object WebDOMUtils {

    /**
     * Creates a new DOM element with the specified tag name.
     */
    fun createElement(tagName: String): DOMElement {
        return DOMProvider.document.createElement(tagName)
    }

    /**
     * Creates a text node with the specified content.
     */
    fun createTextNode(text: String): DOMElement {
        return DOMProvider.document.createTextNode(text)
    }

    /**
     * Finds an element by ID.
     */
    fun getElementById(id: String): DOMElement? {
        return DOMProvider.document.getElementById(id)
    }

    /**
     * Finds the first element matching the selector.
     */
    fun querySelector(selector: String): DOMElement? {
        return DOMProvider.document.querySelector(selector)
    }

    /**
     * Finds all elements matching the selector.
     */
    fun querySelectorAll(selector: String): List<DOMElement> {
        return DOMProvider.document.querySelectorAll(selector)
    }

    /**
     * Safely executes a DOM operation with error handling.
     */
    inline fun <T> safelyExecute(operation: () -> T): T? {
        return try {
            operation()
        } catch (e: Exception) {
            console.error("DOM operation failed: ${e.message}")
            null
        }
    }

    /**
     * Sets multiple attributes on an element.
     */
    fun setAttributes(element: DOMElement, attributes: Map<String, String>) {
        attributes.forEach { (name, value) ->
            element.setAttribute(name, value)
        }
    }

    /**
     * Sets CSS styles on an element.
     */
    fun setStyles(element: DOMElement, styles: Map<String, String>) {
        styles.forEach { (property, value) ->
            setStyle(element, property, value)
        }
    }

    /**
     * Sets a single CSS style property.
     */
    fun setStyle(element: DOMElement, property: String, value: String) {
        // This will be implemented platform-specifically
        element.setAttribute("style", "${element.getAttribute("style") ?: ""};$property:$value")
    }

    /**
     * Checks if an element is visible in the viewport.
     */
    fun isElementVisible(element: DOMElement): Boolean {
        val rect = element.getBoundingClientRect()
        val window = DOMProvider.window

        return rect.bottom > 0 &&
                rect.right > 0 &&
                rect.left < window.innerWidth &&
                rect.top < window.innerHeight
    }

    /**
     * Scrolls an element into view.
     */
    fun scrollIntoView(element: DOMElement, behavior: String = "smooth") {
        // Platform-specific implementation will handle this
        safelyExecute {
            val nativeId = DOMProvider.getNativeElementId(element)
            scrollIntoViewPlatform(nativeId, behavior)
        }
    }

    /**
     * Gets the computed style of an element.
     */
    fun getComputedStyle(element: DOMElement, property: String): String? {
        return safelyExecute {
            val nativeId = DOMProvider.getNativeElementId(element)
            getComputedStylePlatform(nativeId, property)
        }
    }

    /**
     * Adds a CSS class to an element.
     */
    fun addClass(element: DOMElement, className: String) {
        val currentClasses = element.className.split(" ").filter { it.isNotBlank() }.toMutableSet()
        currentClasses.add(className)
        element.setAttribute("class", currentClasses.joinToString(" "))
    }

    /**
     * Removes a CSS class from an element.
     */
    fun removeClass(element: DOMElement, className: String) {
        val currentClasses = element.className.split(" ").filter { it.isNotBlank() }.toMutableSet()
        currentClasses.remove(className)
        element.setAttribute("class", currentClasses.joinToString(" "))
    }

    /**
     * Toggles a CSS class on an element.
     */
    fun toggleClass(element: DOMElement, className: String) {
        val currentClasses = element.className.split(" ").filter { it.isNotBlank() }.toMutableSet()
        if (currentClasses.contains(className)) {
            currentClasses.remove(className)
        } else {
            currentClasses.add(className)
        }
        element.setAttribute("class", currentClasses.joinToString(" "))
    }

    /**
     * Checks if an element has a specific CSS class.
     */
    fun hasClass(element: DOMElement, className: String): Boolean {
        return element.className.split(" ").contains(className)
    }

    /**
     * Creates a debounced function that delays execution.
     */
    fun debounce(delay: Int, action: () -> Unit): () -> Unit {
        var timeoutId: Int? = null

        return {
            timeoutId?.let { DOMProvider.window.clearTimeout(it) }
            timeoutId = DOMProvider.window.setTimeout(action, delay)
        }
    }

    /**
     * Creates a throttled function that limits execution frequency.
     */
    fun throttle(delay: Int, action: () -> Unit): () -> Unit {
        var lastExecution = 0L

        return {
            val now = getCurrentTimeMillis()
            if (now - lastExecution >= delay) {
                action()
                lastExecution = now
            }
        }
    }
}

/**
 * Console abstraction for logging across platforms.
 */
expect object console {
    fun log(message: Any?)
    fun warn(message: Any?)
    fun error(message: Any?)
    fun info(message: Any?)
    fun debug(message: Any?)
}