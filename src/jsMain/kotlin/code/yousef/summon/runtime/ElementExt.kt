package code.yousef.summon.runtime

import org.w3c.dom.Element
import org.w3c.dom.events.Event

/**
 * Extension properties and functions for Element to provide access to common DOM properties and methods.
 */

/**
 * Gets or sets the text content of an element.
 */
var Element.textContent: String
    get() = js("this.textContent || ''") as String
    set(value) {
        js("this.textContent = value")
    }

/**
 * Sets an attribute on an element.
 */
fun Element.setAttribute(name: String, value: String) {
    js("this.setAttribute(name, value)")
}

/**
 * Adds an event listener to an element.
 */
fun Element.addEventListener(type: String, listener: (Event) -> Unit) {
    js("this.addEventListener(type, listener)")
}

/**
 * Appends a child element to this element.
 */
fun Element.appendChild(child: Element) {
    js("this.appendChild(child)")
}

/**
 * Gets or sets the value of an input element.
 */
var Element.value: String
    get() = js("this.value || ''") as String
    set(value) {
        js("this.value = value")
    }

/**
 * Gets or sets the disabled state of an input element.
 */
var Element.disabled: Boolean
    get() = js("this.disabled || false") as Boolean
    set(value) {
        js("this.disabled = value")
    }

/**
 * Gets or sets the selected state of an option element.
 */
var Element.selected: Boolean
    get() = js("this.selected || false") as Boolean
    set(value) {
        js("this.selected = value")
    }

/**
 * Gets the target of an event.
 */
val Event.target: Element?
    get() = js("this.target") as? Element

/**
 * Logs an error message to the console.
 */
fun console.error(message: String) {
    js("console.error(message)")
}