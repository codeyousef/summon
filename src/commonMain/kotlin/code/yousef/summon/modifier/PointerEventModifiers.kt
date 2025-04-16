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
fun Modifier.onClick(handler: String): Modifier =
    attribute("onclick", handler)

/**
 * Adds a mouse enter event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the mouse enters the element
 * @return A new [Modifier] with the onmouseenter attribute
 */
fun Modifier.onMouseEnter(handler: String): Modifier =
    attribute("onmouseenter", handler)

/**
 * Adds a mouse leave event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the mouse leaves the element
 * @return A new [Modifier] with the onmouseleave attribute
 */
fun Modifier.onMouseLeave(handler: String): Modifier =
    attribute("onmouseleave", handler)

/**
 * Adds a touch start event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the element is touched
 * @return A new [Modifier] with the ontouchstart attribute
 */
fun Modifier.onTouchStart(handler: String): Modifier =
    attribute("ontouchstart", handler)

/**
 * Adds a touch end event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the touch ends
 * @return A new [Modifier] with the ontouchend attribute
 */
fun Modifier.onTouchEnd(handler: String): Modifier =
    attribute("ontouchend", handler)

/**
 * Adds a touch move event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the touch moves
 * @return A new [Modifier] with the ontouchmove attribute
 */
fun Modifier.onTouchMove(handler: String): Modifier =
    attribute("ontouchmove", handler)

/**
 * Adds a drag start event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the drag starts
 * @return A new [Modifier] with the ondragstart attribute
 */
fun Modifier.onDragStart(handler: String): Modifier =
    attribute("ondragstart", handler)

/**
 * Adds a drag end event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the drag ends
 * @return A new [Modifier] with the ondragend attribute
 */
fun Modifier.onDragEnd(handler: String): Modifier =
    attribute("ondragend", handler)

/**
 * Adds a drag over event listener to the element.
 * 
 * @param handler The JavaScript code to execute when dragging over the element
 * @return A new [Modifier] with the ondragover attribute
 */
fun Modifier.onDragOver(handler: String): Modifier =
    attribute("ondragover", handler)

/**
 * Adds a drop event listener to the element.
 * 
 * @param handler The JavaScript code to execute when something is dropped on the element
 * @return A new [Modifier] with the ondrop attribute
 */
fun Modifier.onDrop(handler: String): Modifier =
    attribute("ondrop", handler)

/**
 * Makes the element draggable.
 * 
 * @param value Whether the element is draggable ("true" or "false")
 * @return A new [Modifier] with the draggable attribute
 */
fun Modifier.draggable(value: Boolean): Modifier =
    attribute("draggable", value.toString()) 