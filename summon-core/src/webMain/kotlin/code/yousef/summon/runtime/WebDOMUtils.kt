package codes.yousef.summon.runtime

import codes.yousef.summon.core.getCurrentTimeMillis

/**
 * Shared DOM utilities for web platforms (JavaScript and WebAssembly).
 *
 * This module provides common DOM manipulation functions that work across
 * both JS and WASM targets, abstracting away platform-specific differences.
 *
 * @since 0.3.3.0
 */

/**
 * Platform-specific implementations for DOM operations that use JS() calls.
 * For JS target: nativeElementId can be the actual element
 * For WASM target: nativeElementId should be the element's string ID
 */
expect fun scrollIntoViewPlatform(nativeElementId: String, behavior: String)
/**
 * Returns computed style platform.
 *
 * @param nativeElementId The native element id value.
 * @param property The property value.
 * @return The resulting value.
 */
expect fun getComputedStylePlatform(nativeElementId: String, property: String): String?

/**
 * Represents a DOM element abstraction for web platforms.
 */
interface DOMElement {
    /** The property declaration value. */
    val id: String
    /** The property declaration value. */
    val tagName: String
    /** The property declaration value. */
    val className: String
    /** The property declaration value. */
    val textContent: String?

    /**
     * Sets attribute.
     *
     * @param name Human-readable name.
     * @param value Value to process.
     */
    fun setAttribute(name: String, value: String)
    /**
     * Returns attribute.
     *
     * @param name Human-readable name.
     * @return The resulting value.
     */
    fun getAttribute(name: String): String?
    /**
     * Removes attribute.
     *
     * @param name Human-readable name.
     */
    fun removeAttribute(name: String)
    /**
     * Returns whether this value has attribute.
     *
     * @param name Human-readable name.
     * @return The resulting value.
     */
    fun hasAttribute(name: String): Boolean

    /**
     * Executes the append child operation.
     *
     * @param child The child value.
     */
    fun appendChild(child: DOMElement)
    /**
     * Removes child.
     *
     * @param child The child value.
     */
    fun removeChild(child: DOMElement)
    /**
     * Executes the insert before operation.
     *
     * @param newChild The new child value.
     * @param referenceChild The reference child value.
     */
    fun insertBefore(newChild: DOMElement, referenceChild: DOMElement?)

    /**
     * Adds event listener.
     *
     * @param type The type value.
     * @param listener The listener value.
     */
    fun addEventListener(type: String, listener: (event: Any) -> Unit)
    /**
     * Removes event listener.
     *
     * @param type The type value.
     * @param listener The listener value.
     */
    fun removeEventListener(type: String, listener: (event: Any) -> Unit)

    /**
     * Executes the query selector operation.
     *
     * @param selector The selector value.
     * @return The resulting value.
     */
    fun querySelector(selector: String): DOMElement?
    /**
     * Executes the query selector all operation.
     *
     * @param selector The selector value.
     * @return The resulting value.
     */
    fun querySelectorAll(selector: String): List<DOMElement>

    /**
     * Returns bounding client rect.
     *
     * @return The resulting value.
     */
    fun getBoundingClientRect(): DOMRect
    /** Moves focus to this element. */
    fun focus()
    /** Removes focus from this element. */
    fun blur()
    /** Activates this element. */
    fun click()
}

/**
 * Represents element bounds and positioning.

 * @property left The left value.
 * @property top The top value.
 * @property right The right value.
 * @property bottom The bottom value.
 * @property width The width value.
 * @property height The height value.
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
    /** The property declaration value. */
    var value: String
    /** The property declaration value. */
    var disabled: Boolean
    /** The property declaration value. */
    var checked: Boolean
    /** The property declaration value. */
    var selected: Boolean
    /** The property declaration value. */
    var placeholder: String
}

/**
 * Document abstraction for DOM operations.
 */
interface DOMDocument {
    /** The property declaration value. */
    val body: DOMElement?
    /** The property declaration value. */
    val head: DOMElement?
    /** The property declaration value. */
    val documentElement: DOMElement?

    /**
     * Creates element.
     *
     * @param tagName The tag name value.
     * @return The resulting value.
     */
    fun createElement(tagName: String): DOMElement
    /**
     * Creates text node.
     *
     * @param text The text value.
     * @return The resulting value.
     */
    fun createTextNode(text: String): DOMElement
    /**
     * Returns element by ID.
     *
     * @param id Stable identifier.
     * @return The resulting value.
     */
    fun getElementById(id: String): DOMElement?
    /**
     * Executes the query selector operation.
     *
     * @param selector The selector value.
     * @return The resulting value.
     */
    fun querySelector(selector: String): DOMElement?
    /**
     * Executes the query selector all operation.
     *
     * @param selector The selector value.
     * @return The resulting value.
     */
    fun querySelectorAll(selector: String): List<DOMElement>

    /**
     * Adds event listener.
     *
     * @param type The type value.
     * @param listener The listener value.
     */
    fun addEventListener(type: String, listener: (event: Any) -> Unit)
    /**
     * Removes event listener.
     *
     * @param type The type value.
     * @param listener The listener value.
     */
    fun removeEventListener(type: String, listener: (event: Any) -> Unit)
}

/**
 * Window abstraction for browser APIs.
 */
interface DOMWindow {
    /** The property declaration value. */
    val document: DOMDocument
    /** The property declaration value. */
    val location: DOMLocation
    /** The property declaration value. */
    val history: DOMHistory

    /** The property declaration value. */
    var innerWidth: Int
    /** The property declaration value. */
    var innerHeight: Int
    /** The property declaration value. */
    var outerWidth: Int
    /** The property declaration value. */
    var outerHeight: Int

    /**
     * Executes the alert operation.
     *
     * @param message Message content.
     */
    fun alert(message: String)
    /**
     * Executes the confirm operation.
     *
     * @param message Message content.
     * @return The resulting value.
     */
    fun confirm(message: String): Boolean
    /**
     * Executes the prompt operation.
     *
     * @param message Message content.
     * @param defaultValue The default value value.
     * @return The resulting value.
     */
    fun prompt(message: String, defaultValue: String = ""): String?

    /**
     * Sets timeout.
     *
     * @param callback The callback value.
     * @param delay Delay in milliseconds.
     * @return The resulting value.
     */
    fun setTimeout(callback: () -> Unit, delay: Int): Int
    /**
     * Clears timeout.
     *
     * @param timeoutId The timeout id value.
     */
    fun clearTimeout(timeoutId: Int)
    /**
     * Sets interval.
     *
     * @param callback The callback value.
     * @param delay Delay in milliseconds.
     * @return The resulting value.
     */
    fun setInterval(callback: () -> Unit, delay: Int): Int
    /**
     * Clears interval.
     *
     * @param intervalId The interval id value.
     */
    fun clearInterval(intervalId: Int)

    /**
     * Executes the request animation frame operation.
     *
     * @param callback The callback value.
     * @return The resulting value.
     */
    fun requestAnimationFrame(callback: () -> Unit): Int
    /**
     * Cancels animation frame.
     *
     * @param requestId The request id value.
     */
    fun cancelAnimationFrame(requestId: Int)

    /**
     * Adds event listener.
     *
     * @param type The type value.
     * @param listener The listener value.
     */
    fun addEventListener(type: String, listener: (event: Any) -> Unit)
    /**
     * Removes event listener.
     *
     * @param type The type value.
     * @param listener The listener value.
     */
    fun removeEventListener(type: String, listener: (event: Any) -> Unit)
}

/**
 * Location API abstraction.
 */
interface DOMLocation {
    /** The property declaration value. */
    var href: String
    /** The property declaration value. */
    var protocol: String
    /** The property declaration value. */
    var host: String
    /** The property declaration value. */
    var hostname: String
    /** The property declaration value. */
    var port: String
    /** The property declaration value. */
    var pathname: String
    /** The property declaration value. */
    var search: String
    /** The property declaration value. */
    var hash: String

    /**
     * Executes the assign operation.
     *
     * @param url Target URL.
     */
    fun assign(url: String)
    /**
     * Executes the replace operation.
     *
     * @param url Target URL.
     */
    fun replace(url: String)
    /** Executes the reload operation. */
    fun reload()
}

/**
 * History API abstraction.
 */
interface DOMHistory {
    /** The property declaration value. */
    val length: Int
    /** The property declaration value. */
    val state: Any?

    /** Executes the back operation. */
    fun back()
    /** Executes the forward operation. */
    fun forward()
    /**
     * Executes the go operation.
     *
     * @param delta The delta value.
     */
    fun go(delta: Int)
    /**
     * Executes the push state operation.
     *
     * @param state The state value.
     * @param title The title value.
     * @param url Target URL.
     */
    fun pushState(state: Any?, title: String?, url: String?)
    /**
     * Executes the replace state operation.
     *
     * @param state The state value.
     * @param title The title value.
     * @param url Target URL.
     */
    fun replaceState(state: Any?, title: String?, url: String?)
}

/**
 * Platform-specific DOM API provider.
 * Implementations provide access to the actual DOM APIs.
 */
expect object DOMProvider {
    /** The property declaration value. */
    val window: DOMWindow
    /** The property declaration value. */
    val document: DOMDocument

    /**
     * Creates element from native.
     *
     * @param nativeElement The native element value.
     * @return The resulting value.
     */
    fun createElementFromNative(nativeElement: Any): DOMElement
    /**
     * Returns native element.
     *
     * @param element The element value.
     * @return The resulting value.
     */
    fun getNativeElement(element: DOMElement): Any
    /**
     * Returns native element ID.
     *
     * @param element The element value.
     * @return The resulting value.
     */
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
    /**
     * Executes the log operation.
     *
     * @param message Message content.
     */
    fun log(message: Any?)
    /**
     * Executes the warn operation.
     *
     * @param message Message content.
     */
    fun warn(message: Any?)
    /**
     * Executes the error operation.
     *
     * @param message Message content.
     */
    fun error(message: Any?)
    /**
     * Executes the info operation.
     *
     * @param message Message content.
     */
    fun info(message: Any?)
    /**
     * Executes the debug operation.
     *
     * @param message Message content.
     */
    fun debug(message: Any?)
}