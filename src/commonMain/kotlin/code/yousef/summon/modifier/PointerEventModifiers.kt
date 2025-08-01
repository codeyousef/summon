package code.yousef.summon.modifier

import code.yousef.summon.modifier.ModifierExtras.pointerEvents

/**
 * Modifiers for handling pointer events.
 */

/**
 * Makes the element ignore all pointer events (clicks, touches, hovers).
 * Events will pass through to elements behind this one.
 * 
 * @return A new [Modifier] with pointer-events: none
 */
fun Modifier.disablePointerEvents(): Modifier = 
    pointerEvents("none")

/**
 * Enables pointer events on the element (default behavior).
 * 
 * @return A new [Modifier] with pointer-events: auto
 */
fun Modifier.enablePointerEvents(): Modifier = 
    pointerEvents("auto")

/**
 * Adds a click event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the element is clicked
 * @return A new [Modifier] with the onclick attribute
 */
fun Modifier.onClick(handler: String): Modifier {
    // Directly add the prefixed event key to the map
    val key = "__event:click"
    return Modifier(this.styles + (key to handler), this.attributes)
}

/**
 * Adds a mouse enter event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the mouse enters the element
 * @return A new [Modifier] with the onmouseenter attribute
 */
fun Modifier.onMouseEnter(handler: String): Modifier {
    // Directly add the prefixed event key to the map
    val key = "__event:mouseenter"
    return Modifier(this.styles + (key to handler), this.attributes)
}

/**
 * Adds a mouse leave event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the mouse leaves the element
 * @return A new [Modifier] with the onmouseleave attribute
 */
fun Modifier.onMouseLeave(handler: String): Modifier {
    val key = "__event:mouseleave"
    return Modifier(this.styles + (key to handler), this.attributes)
}

/**
 * Adds a touch start event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the element is touched
 * @return A new [Modifier] with the ontouchstart attribute
 */
fun Modifier.onTouchStart(handler: String): Modifier {
    val key = "__event:touchstart"
    return Modifier(this.styles + (key to handler), this.attributes)
}

/**
 * Adds a touch end event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the touch ends
 * @return A new [Modifier] with the ontouchend attribute
 */
fun Modifier.onTouchEnd(handler: String): Modifier {
    val key = "__event:touchend"
    return Modifier(this.styles + (key to handler), this.attributes)
}

/**
 * Adds a touch move event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the touch moves
 * @return A new [Modifier] with the ontouchmove attribute
 */
fun Modifier.onTouchMove(handler: String): Modifier {
    val key = "__event:touchmove"
    return Modifier(this.styles + (key to handler), this.attributes)
}

/**
 * Adds a drag start event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the drag starts
 * @return A new [Modifier] with the ondragstart attribute
 */
fun Modifier.onDragStart(handler: String): Modifier {
    val key = "__event:dragstart"
    return Modifier(this.styles + (key to handler), this.attributes)
}

/**
 * Adds a drag end event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the drag ends
 * @return A new [Modifier] with the ondragend attribute
 */
fun Modifier.onDragEnd(handler: String): Modifier {
    val key = "__event:dragend"
    return Modifier(this.styles + (key to handler), this.attributes)
}

/**
 * Adds a drag over event listener to the element.
 * 
 * @param handler The JavaScript code to execute when dragging over the element
 * @return A new [Modifier] with the ondragover attribute
 */
fun Modifier.onDragOver(handler: String): Modifier {
    val key = "__event:dragover"
    return Modifier(this.styles + (key to handler), this.attributes)
}

/**
 * Adds a drop event listener to the element.
 * 
 * @param handler The JavaScript code to execute when something is dropped on the element
 * @return A new [Modifier] with the ondrop attribute
 */
fun Modifier.onDrop(handler: String): Modifier {
    val key = "__event:drop"
    return Modifier(this.styles + (key to handler), this.attributes)
}

/**
 * Makes the element draggable.
 * 
 * @param value Whether the element is draggable ("true" or "false")
 * @return A new [Modifier] with the draggable attribute
 */
fun Modifier.draggable(value: Boolean): Modifier =
    attribute("draggable", value.toString()) 