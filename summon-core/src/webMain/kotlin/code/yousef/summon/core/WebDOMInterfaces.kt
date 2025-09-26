package code.yousef.summon.core

/**
 * Common DOM interfaces shared between JS and WASM implementations.
 *
 * These interfaces provide a unified API for DOM manipulation across
 * JavaScript and WebAssembly targets while maintaining type safety.
 */

/**
 * Represents a DOM element with basic properties and methods.
 */
interface DOMElement {
    val tagName: String
    val id: String
    val className: String
    val innerHTML: String
    val textContent: String
    val children: List<DOMElement>
    val parentElement: DOMElement?

    fun getAttribute(name: String): String?
    fun setAttribute(name: String, value: String)
    fun removeAttribute(name: String)
    fun appendChild(child: DOMElement)
    fun removeChild(child: DOMElement)
    fun addEventListener(type: String, listener: (DOMEvent) -> Unit)
    fun removeEventListener(type: String, listener: (DOMEvent) -> Unit)
    fun querySelector(selector: String): DOMElement?
    fun querySelectorAll(selector: String): List<DOMElement>
}

/**
 * Represents a DOM event with common properties.
 */
interface DOMEvent {
    val type: String
    val target: DOMElement?
    val currentTarget: DOMElement?
    val bubbles: Boolean
    val cancelable: Boolean
    val defaultPrevented: Boolean

    fun preventDefault()
    fun stopPropagation()
    fun stopImmediatePropagation()
}

/**
 * Represents a form element with form-specific properties.
 */
interface FormElement : DOMElement {
    val value: String
    val name: String
    val disabled: Boolean
    val required: Boolean

    fun focus()
    fun blur()
    fun select()
}

/**
 * Represents an input element with input-specific properties.
 */
interface InputElement : FormElement {
    val type: String
    val placeholder: String
    val checked: Boolean
    val maxLength: Int
    val minLength: Int
    val pattern: String
    val readOnly: Boolean
}

/**
 * Represents a select element with select-specific properties.
 */
interface SelectElement : FormElement {
    val selectedIndex: Int
    val options: List<OptionElement>
    val multiple: Boolean
    val size: Int
}

/**
 * Represents an option element within a select.
 */
interface OptionElement : DOMElement {
    val value: String
    val text: String
    val selected: Boolean
    val disabled: Boolean
    val index: Int
}

/**
 * Represents a text area element.
 */
interface TextAreaElement : FormElement {
    val rows: Int
    val cols: Int
    val wrap: String
    val placeholder: String
    val readOnly: Boolean
    val maxLength: Int
    val minLength: Int
}

/**
 * Common CSS style properties interface.
 */
interface CSSStyleDeclaration {
    fun getPropertyValue(property: String): String
    fun setProperty(property: String, value: String, priority: String = "")
    fun removeProperty(property: String): String
}

/**
 * Represents the document object.
 */
interface DocumentElement : DOMElement {
    val body: DOMElement?
    val head: DOMElement?
    val title: String
    val location: LocationElement?

    fun createElement(tagName: String): DOMElement
    fun createTextNode(text: String): DOMElement
    fun getElementById(id: String): DOMElement?
    fun getElementsByTagName(tagName: String): List<DOMElement>
    fun getElementsByClassName(className: String): List<DOMElement>
}

/**
 * Represents the window location object.
 */
interface LocationElement {
    val href: String
    val protocol: String
    val host: String
    val hostname: String
    val port: String
    val pathname: String
    val search: String
    val hash: String

    fun assign(url: String)
    fun reload()
    fun replace(url: String)
}

/**
 * Represents the window object.
 */
interface WindowElement {
    val document: DocumentElement
    val location: LocationElement
    val navigator: NavigatorElement
    val performance: PerformanceElement?
    val innerWidth: Int
    val innerHeight: Int
    val outerWidth: Int
    val outerHeight: Int

    fun alert(message: String)
    fun confirm(message: String): Boolean
    fun prompt(message: String, defaultText: String = ""): String?
    fun setTimeout(callback: () -> Unit, delay: Int): Int
    fun clearTimeout(timeoutId: Int)
    fun setInterval(callback: () -> Unit, delay: Int): Int
    fun clearInterval(intervalId: Int)
}

/**
 * Represents the navigator object.
 */
interface NavigatorElement {
    val userAgent: String
    val language: String
    val languages: List<String>
    val platform: String
    val cookieEnabled: Boolean
    val onLine: Boolean
    val maxTouchPoints: Int
}

/**
 * Represents the performance object.
 */
interface PerformanceElement {
    fun now(): Double
    fun mark(name: String)
    fun measure(name: String, startMark: String? = null, endMark: String? = null)
    fun getEntriesByName(name: String): List<PerformanceEntry>
    fun getEntriesByType(type: String): List<PerformanceEntry>
}

/**
 * Represents a performance entry.
 */
interface PerformanceEntry {
    val name: String
    val entryType: String
    val startTime: Double
    val duration: Double
}