package code.yousef.summon.runtime

import org.w3c.dom.Element
import org.w3c.dom.events.Event

/**
 * Extension properties and functions for Element to provide access to common DOM properties and methods.
 */

// textContent is already defined in Node, no need to redefine it

/**
 * Sets an attribute on an element.
 */
fun Element.setAttribute(name: String, value: String) {
    this.asDynamic().setAttribute(name, value)
}

/**
 * Adds an event listener to an element.
 */
fun Element.addEventListener(type: String, listener: (Event) -> Unit) {
    // Use direct property assignment with proper lambda conversion for Kotlin/JS
    val jsHandler: (Event) -> dynamic = { event: Event ->
        listener(event)
        Unit
    }
    
    when (type) {
        "click" -> this.asDynamic().onclick = jsHandler
        "change" -> this.asDynamic().onchange = jsHandler
        "input" -> this.asDynamic().oninput = jsHandler
        "submit" -> this.asDynamic().onsubmit = jsHandler
        "keydown" -> this.asDynamic().onkeydown = jsHandler
        "keyup" -> this.asDynamic().onkeyup = jsHandler
        "focus" -> this.asDynamic().onfocus = jsHandler
        "blur" -> this.asDynamic().onblur = jsHandler
        "mouseenter" -> this.asDynamic().onmouseenter = jsHandler
        "mouseleave" -> this.asDynamic().onmouseleave = jsHandler
        else -> {
            // Fallback to addEventListener for other event types
            this.asDynamic().addEventListener(type, jsHandler)
        }
    }
}

/**
 * Appends a child element to this element.
 */
fun Element.appendChild(child: Element) {
    this.asDynamic().appendChild(child)
}

/**
 * Gets or sets the value of an input element.
 */
var Element.value: String
    get() = this.asDynamic().value as? String ?: ""
    set(newValue) {
        this.asDynamic().value = newValue
    }

/**
 * Gets or sets the disabled state of an input element.
 */
var Element.disabled: Boolean
    get() = this.asDynamic().disabled as? Boolean ?: false
    set(newValue) {
        this.asDynamic().disabled = newValue
    }

/**
 * Gets or sets the selected state of an option element.
 */
var Element.selected: Boolean
    get() = this.asDynamic().selected as? Boolean ?: false
    set(newValue) {
        this.asDynamic().selected = newValue
    }

// target is already defined in Event, no need to redefine it