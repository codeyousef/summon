/**
 * # Pointer Event Modifiers
 *
 * Comprehensive pointer interaction modifiers for the Summon framework.
 * This module provides type-safe event handling for mouse, touch, and
 * drag interactions across all supported platforms.
 *
 * ## Core Features
 *
 * - **Multi-Touch Support**: Complete touch gesture handling
 * - **Mouse Interactions**: Click, hover, and movement events
 * - **Drag and Drop**: Full drag-and-drop functionality
 * - **Pointer Control**: Enable/disable pointer event processing
 * - **Cross-Platform**: Consistent behavior across desktop and mobile
 * - **Performance Optimized**: Efficient event delegation and handling
 *
 * ## Event Categories
 *
 * ### Click Events
 * - `onClick()` - Primary click/tap interactions
 * - Event bubbling and capture support
 * - Automatic touch-to-click translation
 *
 * ### Mouse Events
 * - `onMouseEnter()` - Mouse enter detection
 * - `onMouseLeave()` - Mouse leave detection
 * - Hover state management
 *
 * ### Touch Events
 * - `onTouchStart()` - Touch initiation
 * - `onTouchEnd()` - Touch completion
 * - `onTouchMove()` - Touch movement tracking
 * - Multi-touch gesture support
 *
 * ### Drag Events
 * - `onDragStart()` - Drag initiation
 * - `onDragEnd()` - Drag completion
 * - `onDragOver()` - Drag hover detection
 * - `onDrop()` - Drop target handling
 * - `draggable()` - Make elements draggable
 *
 * ### Pointer Control
 * - `enablePointerEvents()` - Enable event processing
 * - `disablePointerEvents()` - Disable event processing
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Basic click handling
 * Button(
 *     modifier = Modifier()
 *         .onClick("handleButtonClick()")
 * )
 *
 * // Hover effects
 * Card(
 *     modifier = Modifier()
 *         .onMouseEnter("showTooltip()")
 *         .onMouseLeave("hideTooltip()")
 * )
 *
 * // Touch interactions
 * Box(
 *     modifier = Modifier()
 *         .onTouchStart("startGesture(event)")
 *         .onTouchMove("updateGesture(event)")
 *         .onTouchEnd("endGesture(event)")
 * )
 *
 * // Drag and drop
 * Box(
 *     modifier = Modifier()
 *         .draggable(true)
 *         .onDragStart("initiateDrag(event)")
 *         .onDragEnd("completeDrag(event)")
 * )
 *
 * // Drop target
 * Column(
 *     modifier = Modifier()
 *         .onDragOver("allowDrop(event)")
 *         .onDrop("handleDrop(event)")
 * )
 * ```
 *
 * ## Advanced Interaction Patterns
 *
 * ### Custom Gesture Recognition
 * ```kotlin
 * Box(
 *     modifier = Modifier()
 *         .onTouchStart("gestureRecognizer.start(event)")
 *         .onTouchMove("gestureRecognizer.update(event)")
 *         .onTouchEnd("gestureRecognizer.end(event)")
 * )
 * ```
 *
 * ### Interactive Lists
 * ```kotlin
 * LazyColumn {
 *     items(listItems) { item ->
 *         ListItem(
 *             modifier = Modifier()
 *                 .onClick("selectItem('${item.id}')")
 *                 .onMouseEnter("highlightItem('${item.id}')")
 *                 .onMouseLeave("unhighlightItem('${item.id}')")
 *         )
 *     }
 * }
 * ```
 *
 * ### Conditional Event Handling
 * ```kotlin
 * Box(
 *     modifier = if (isInteractive) {
 *         Modifier()
 *             .enablePointerEvents()
 *             .onClick("handleClick()")
 *     } else {
 *         Modifier().disablePointerEvents()
 *     }
 * )
 * ```
 *
 * ## Event Handler Format
 *
 * Event handlers use JavaScript expressions that have access to:
 *
 * - `event` - The DOM event object
 * - Global functions and variables
 * - Component-specific event handlers
 *
 * ```kotlin
 * // Simple function call
 * .onClick("handleClick()")
 *
 * // Event object access
 * .onClick("handleClick(event)")
 *
 * // Inline logic
 * .onClick("if (event.ctrlKey) { handleCtrlClick(); } else { handleClick(); }")
 *
 * // Complex expressions
 * .onDragStart("event.dataTransfer.setData('text/plain', '${item.id}')")
 * ```
 *
 * ## Performance Features
 *
 * - **Event Delegation**: Efficient event handling through delegation
 * - **Passive Listeners**: Non-blocking event listeners where appropriate
 * - **Touch Optimization**: Optimized touch event handling for mobile
 * - **Memory Management**: Automatic cleanup of event listeners
 *
 * ## Accessibility Integration
 *
 * Pointer events work seamlessly with accessibility features:
 *
 * ```kotlin
 * Button(
 *     modifier = Modifier()
 *         .onClick("submit()")
 *         .ariaLabel("Submit form")
 *         .role("button")
 * )
 * ```
 *
 * ## Cross-Platform Considerations
 *
 * - **Touch Devices**: Automatic touch-to-click mapping
 * - **Desktop**: Full mouse event support
 * - **Hybrid Devices**: Seamless touch and mouse coexistence
 * - **Browser Compatibility**: Works across all modern browsers
 *
 * @see Modifier for the core modifier system
 * @see AccessibilityModifiers for accessible interactions
 * @see AnimationModifiers for animated interactions
 * @since 1.0.0
 */
package code.yousef.summon.modifier

/**
 * Makes the element ignore all pointer events (clicks, touches, hovers).
 * Events will pass through to elements behind this one.
 * 
 * @return A new [Modifier] with pointer-events: none
 */
fun Modifier.disablePointerEvents(): Modifier =
    pointerEvents(PointerEvents.None)

/**
 * Enables pointer events on the element (default behavior).
 * 
 * @return A new [Modifier] with pointer-events: auto
 */
fun Modifier.enablePointerEvents(): Modifier =
    pointerEvents(PointerEvents.Auto)

/**
 * Sets the CSS pointer-events property using type-safe enum values.
 *
 * @param value PointerEvents enum describing the desired behavior
 * @return A new [Modifier] with pointer-events applied
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
fun Modifier.pointerEvents(value: PointerEvents): Modifier =
    style("pointer-events", value.toString())

/**
 * Adds a click event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the element is clicked
 * @return A new [Modifier] with the onclick attribute
 */
fun Modifier.onClick(handler: String): Modifier {
    return attribute("onclick", handler)
}

/**
 * Adds a mouse enter event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the mouse enters the element
 * @return A new [Modifier] with the onmouseenter attribute
 */
fun Modifier.onMouseEnter(handler: String): Modifier {
    return attribute("onmouseenter", handler)
}

/**
 * Adds a mouse leave event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the mouse leaves the element
 * @return A new [Modifier] with the onmouseleave attribute
 */
fun Modifier.onMouseLeave(handler: String): Modifier {
    return attribute("onmouseleave", handler)
}

/**
 * Adds a touch start event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the element is touched
 * @return A new [Modifier] with the ontouchstart attribute
 */
fun Modifier.onTouchStart(handler: String): Modifier {
    return attribute("ontouchstart", handler)
}

/**
 * Adds a touch end event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the touch ends
 * @return A new [Modifier] with the ontouchend attribute
 */
fun Modifier.onTouchEnd(handler: String): Modifier {
    return attribute("ontouchend", handler)
}

/**
 * Adds a touch move event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the touch moves
 * @return A new [Modifier] with the ontouchmove attribute
 */
fun Modifier.onTouchMove(handler: String): Modifier {
    return attribute("ontouchmove", handler)
}

/**
 * Adds a drag start event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the drag starts
 * @return A new [Modifier] with the ondragstart attribute
 */
fun Modifier.onDragStart(handler: String): Modifier {
    return attribute("ondragstart", handler)
}

/**
 * Adds a drag end event listener to the element.
 * 
 * @param handler The JavaScript code to execute when the drag ends
 * @return A new [Modifier] with the ondragend attribute
 */
fun Modifier.onDragEnd(handler: String): Modifier {
    return attribute("ondragend", handler)
}

/**
 * Adds a drag over event listener to the element.
 * 
 * @param handler The JavaScript code to execute when dragging over the element
 * @return A new [Modifier] with the ondragover attribute
 */
fun Modifier.onDragOver(handler: String): Modifier {
    return attribute("ondragover", handler)
}

/**
 * Adds a drop event listener to the element.
 * 
 * @param handler The JavaScript code to execute when something is dropped on the element
 * @return A new [Modifier] with the ondrop attribute
 */
fun Modifier.onDrop(handler: String): Modifier {
    return attribute("ondrop", handler)
}

/**
 * Makes the element draggable.
 * 
 * @param value Whether the element is draggable ("true" or "false")
 * @return A new [Modifier] with the draggable attribute
 */
fun Modifier.draggable(value: Boolean): Modifier =
    attribute("draggable", value.toString()) 
