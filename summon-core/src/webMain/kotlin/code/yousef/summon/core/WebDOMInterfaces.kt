package codes.yousef.summon.core

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
    /** Uppercase HTML tag name. */
    val tagName: String
    /** DOM element ID, or an empty string. */
    val id: String
    /** Serialized class attribute. */
    val className: String
    /** Serialized descendant markup. */
    val innerHTML: String
    /** Descendant text content. */
    val textContent: String
    /** Child elements in document order. */
    val children: List<DOMElement>
    /** Parent element, or `null` when detached. */
    val parentElement: DOMElement?

    /** Returns attribute [name], or `null` when absent. */
    fun getAttribute(name: String): String?
    /** Sets attribute [name] to [value]. */
    fun setAttribute(name: String, value: String)
    /** Removes attribute [name]. */
    fun removeAttribute(name: String)
    /** Appends [child]. */
    fun appendChild(child: DOMElement)
    /** Removes [child]. */
    fun removeChild(child: DOMElement)
    /** Registers [listener] for [type]. Retain the same callback for removal. */
    fun addEventListener(type: String, listener: (DOMEvent) -> Unit)
    /** Removes the exact [listener] previously registered for [type]. */
    fun removeEventListener(type: String, listener: (DOMEvent) -> Unit)
    /** Returns the first descendant matching [selector]. */
    fun querySelector(selector: String): DOMElement?
    /** Returns all descendants matching [selector] in document order. */
    fun querySelectorAll(selector: String): List<DOMElement>
}

/**
 * Represents a DOM event with common properties.
 */
interface DOMEvent {
    /** Browser event type. */
    val type: String
    /** Original event target. */
    val target: DOMElement?
    /** Target whose listener is currently running. */
    val currentTarget: DOMElement?
    /** Whether this event bubbles. */
    val bubbles: Boolean
    /** Whether this event can be canceled. */
    val cancelable: Boolean
    /** Whether default behavior has been prevented. */
    val defaultPrevented: Boolean

    /** Prevents default browser behavior when cancelable. */
    fun preventDefault()
    /** Stops propagation after current-target listeners. */
    fun stopPropagation()
    /** Stops propagation and remaining current-target listeners. */
    fun stopImmediatePropagation()
}

/**
 * Represents a form element with form-specific properties.
 */
interface FormElement : DOMElement {
    /** Current serialized control value. */
    val value: String
    /** Submitted field name. */
    val name: String
    /** Whether interaction and submission are disabled. */
    val disabled: Boolean
    /** Whether constraint validation requires a value. */
    val required: Boolean

    /** Requests focus. */
    fun focus()
    /** Releases focus. */
    fun blur()
    /** Selects editable text when supported. */
    fun select()
}

/**
 * Represents an input element with input-specific properties.
 */
interface InputElement : FormElement {
    /** HTML input type. */
    val type: String
    /** Placeholder text. */
    val placeholder: String
    /** Checkbox or radio checked state. */
    val checked: Boolean
    /** Maximum accepted text length. */
    val maxLength: Int
    /** Minimum accepted text length. */
    val minLength: Int
    /** HTML constraint-validation pattern. */
    val pattern: String
    /** Whether text mutation is prohibited. */
    val readOnly: Boolean
}

/**
 * Represents a select element with select-specific properties.
 */
interface SelectElement : FormElement {
    /** Selected option index, or platform sentinel when none. */
    val selectedIndex: Int
    /** Options in document order. */
    val options: List<OptionElement>
    /** Whether multiple options may be selected. */
    val multiple: Boolean
    /** Visible row count. */
    val size: Int
}

/**
 * Represents an option element within a select.
 */
interface OptionElement : DOMElement {
    /** Submitted option value. */
    val value: String
    /** Visible option text. */
    val text: String
    /** Current selected state. */
    val selected: Boolean
    /** Whether selection is disabled. */
    val disabled: Boolean
    /** Zero-based option index. */
    val index: Int
}

/**
 * Represents a text area element.
 */
interface TextAreaElement : FormElement {
    /** Visible text rows. */
    val rows: Int
    /** Visible text columns. */
    val cols: Int
    /** Native wrapping mode. */
    val wrap: String
    /** Placeholder text. */
    val placeholder: String
    /** Whether text mutation is prohibited. */
    val readOnly: Boolean
    /** Maximum accepted text length. */
    val maxLength: Int
    /** Minimum accepted text length. */
    val minLength: Int
}

/**
 * Common CSS style properties interface.
 */
interface CSSStyleDeclaration {
    /** Returns [property], or an empty string when unset. */
    fun getPropertyValue(property: String): String
    /** Sets [property] to [value] with optional [priority]. */
    fun setProperty(property: String, value: String, priority: String = "")
    /** Removes [property] and returns its previous value. */
    fun removeProperty(property: String): String
}

/**
 * Represents the document object.
 */
interface DocumentElement : DOMElement {
    /** Document body, when available. */
    val body: DOMElement?
    /** Document head, when available. */
    val head: DOMElement?
    /** Document title. */
    val title: String
    /** Current document location. */
    val location: LocationElement?

    /** Creates a detached [tagName] element. */
    fun createElement(tagName: String): DOMElement
    /** Creates a text node containing [text]. */
    fun createTextNode(text: String): DOMElement
    /** Returns the element with `id`, or `null`. */
    fun getElementById(id: String): DOMElement?
    /** Returns elements with [tagName]. */
    fun getElementsByTagName(tagName: String): List<DOMElement>
    /** Returns elements containing [className]. */
    fun getElementsByClassName(className: String): List<DOMElement>
}

/**
 * Represents the window location object.
 */
interface LocationElement {
    /** Complete location URL. */
    val href: String
    /** URL protocol including its trailing colon. */
    val protocol: String
    /** Host and optional port. */
    val host: String
    /** Host name without port. */
    val hostname: String
    /** Explicit port, or empty. */
    val port: String
    /** URL path. */
    val pathname: String
    /** Query including its leading question mark. */
    val search: String
    /** Fragment including its leading hash. */
    val hash: String

    /** Navigates to `url`. */
    fun assign(url: String)
    /** Reloads the current location. */
    fun reload()
    /** Replaces browser history with `url`. */
    fun replace(url: String)
}

/**
 * Represents the window object.
 */
interface WindowElement {
    /** Current document. */
    val document: DocumentElement
    /** Current location. */
    val location: LocationElement
    /** Browser navigator capabilities. */
    val navigator: NavigatorElement
    /** High-resolution timing API, when available. */
    val performance: PerformanceElement?
    /** Viewport width in pixels. */
    val innerWidth: Int
    /** Viewport height in pixels. */
    val innerHeight: Int
    /** Browser-window width in pixels. */
    val outerWidth: Int
    /** Browser-window height in pixels. */
    val outerHeight: Int

    /** Shows a modal [message]. */
    fun alert(message: String)
    /** Shows a confirmation [message]. */
    fun confirm(message: String): Boolean
    /** Requests text input with [message] and [defaultText]. */
    fun prompt(message: String, defaultText: String = ""): String?
    /** Schedules one [callback] after [delay] milliseconds. */
    fun setTimeout(callback: () -> Unit, delay: Int): Int
    /** Cancels [timeoutId]. */
    fun clearTimeout(timeoutId: Int)
    /** Schedules repeated [callback] calls every [delay] milliseconds. */
    fun setInterval(callback: () -> Unit, delay: Int): Int
    /** Cancels [intervalId]. */
    fun clearInterval(intervalId: Int)
}

/**
 * Represents the navigator object.
 */
interface NavigatorElement {
    /** Browser user-agent string. */
    val userAgent: String
    /** Preferred language. */
    val language: String
    /** Preferred languages in order. */
    val languages: List<String>
    /** Browser-reported platform. */
    val platform: String
    /** Whether cookies are enabled. */
    val cookieEnabled: Boolean
    /** Current network connectivity hint. */
    val onLine: Boolean
    /** Maximum concurrent touch points. */
    val maxTouchPoints: Int
}

/**
 * Represents the performance object.
 */
interface PerformanceElement {
    /** Current high-resolution timestamp in milliseconds. */
    fun now(): Double
    /** Records a named performance mark. */
    fun mark(name: String)
    /** Measures between optional marks. */
    fun measure(name: String, startMark: String? = null, endMark: String? = null)
    /** Returns entries named [name]. */
    fun getEntriesByName(name: String): List<PerformanceEntry>
    /** Returns entries of [type]. */
    fun getEntriesByType(type: String): List<PerformanceEntry>
}

/**
 * Represents a performance entry.
 */
interface PerformanceEntry {
    /** Entry name. */
    val name: String
    /** Entry category. */
    val entryType: String
    /** Start timestamp in milliseconds. */
    val startTime: Double
    /** Duration in milliseconds. */
    val duration: Double
}