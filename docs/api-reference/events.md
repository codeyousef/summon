# Events API Reference

This document provides detailed information about the event handling APIs in the Summon library.

## Table of Contents

- [Event System Overview](#event-system-overview)
- [Event Interface](#event-interface)
- [Event Listeners](#event-listeners)
- [Common Events](#common-events)
- [Custom Events](#custom-events)
- [Platform-Specific Events](#platform-specific-events)

---

## Event System Overview

Summon provides a cross-platform event system that abstracts browser events on JS and JVM event handling, allowing for consistent event handling across platforms.

### Architecture

```
Event Source (DOM/Platform) → Event Adapter → Summon Event → Event Handler
```

The event system normalizes platform-specific events into a consistent interface, allowing your code to handle events the same way regardless of platform.

---

## Event Interface

The base interface for all events in Summon.

### Interface Definition

```kotlin
package codes.yousef.summon.events

interface Event {
    // Event type (click, change, etc.)
    val type: String
    
    // Target element that triggered the event
    val target: EventTarget
    
    // Current target during event bubbling
    val currentTarget: EventTarget
    
    // Event timestamp
    val timestamp: Long
    
    // Prevent default behavior
    fun preventDefault()
    
    // Stop event propagation
    fun stopPropagation()
    
    // Stop immediate propagation
    fun stopImmediatePropagation()
    
    // Check if default was prevented
    val defaultPrevented: Boolean
    
    // Event phase (capturing, bubbling, etc.)
    val eventPhase: EventPhase
    
    // Check if event bubbles
    val bubbles: Boolean
    
    // Check if event can be canceled
    val cancelable: Boolean
}

// Event phases
enum class EventPhase {
    NONE,
    CAPTURING_PHASE,
    AT_TARGET,
    BUBBLING_PHASE
}

// Event target interface
interface EventTarget {
    // Element ID
    val id: String?
    
    // Element tag name (for HTML elements)
    val tagName: String?
    
    // Access attributes
    fun getAttribute(name: String): String?
    
    // Access data attributes
    fun getDataAttribute(name: String): String?
}
```

### Description

The `Event` interface provides a consistent API for handling events across platforms. It includes methods for controlling event propagation and accessing event information.

### Example

```kotlin
Button(
    text = "Click Me",
    onClick = { event ->
        // Access event details
        console.log("Button clicked at ${event.timestamp}")
        
        // Prevent default behavior
        event.preventDefault()
        
        // Stop propagation
        event.stopPropagation()
        
        // Check target information
        val targetId = event.target.id
        if (targetId != null) {
            console.log("Clicked element ID: $targetId")
        }
    }
)
```

---

## Event Listeners

APIs for attaching and managing event listeners.

### Function Definitions

```kotlin
package codes.yousef.summon.events

// Add an event listener to a component
@Composable
fun addEventListener(
    eventType: String,
    listener: (Event) -> Unit
): EventSubscription

// Add type-specific event listeners
@Composable
fun addClickListener(listener: (MouseEvent) -> Unit): EventSubscription
@Composable
fun addChangeListener(listener: (ChangeEvent) -> Unit): EventSubscription
@Composable
fun addInputListener(listener: (InputEvent) -> Unit): EventSubscription
@Composable
fun addFocusListener(listener: (FocusEvent) -> Unit): EventSubscription
@Composable
fun addBlurListener(listener: (FocusEvent) -> Unit): EventSubscription
@Composable
fun addSubmitListener(listener: (SubmitEvent) -> Unit): EventSubscription
@Composable
fun addKeyListener(
    type: KeyEventType,
    listener: (KeyboardEvent) -> Unit
): EventSubscription

// Event subscription interface
interface EventSubscription {
    // Remove the event listener
    fun unsubscribe()
    
    // Pause listening (temporarily disable)
    fun pause()
    
    // Resume listening
    fun resume()
    
    // Check if subscription is active
    val isActive: Boolean
}

// Key event types
enum class KeyEventType {
    DOWN,
    UP,
    PRESS
}
```

### Description

These functions allow you to attach event listeners to composable components. The `EventSubscription` return value lets you manage the listener lifecycle.

### Example

```kotlin
@Composable
fun EventHandlingComponent() {
    // Generic event listener
    val subscription = addEventListener("mousemove") { event ->
        val mouseEvent = event as MouseEvent
        handleMouseMove(mouseEvent.clientX, mouseEvent.clientY)
    }
    
    // Clean up when component is disposed
    onDispose {
        subscription.unsubscribe()
    }
    
    // Type-specific listeners with Modifier
    TextField(
        value = text,
        onValueChange = { /* handle text changes */ },
        modifier = Modifier.apply {
            // Add a focus listener
            val focusSub = addFocusListener { event ->
                isFieldFocused = true
            }
            
            // Add a blur listener
            val blurSub = addBlurListener { event ->
                isFieldFocused = false
                // Validate on blur
                validateInput(text)
            }
            
            // Clean up subscriptions on disposal
            onDispose {
                focusSub.unsubscribe()
                blurSub.unsubscribe()
            }
        }
    )
    
    // Key events at document level
    DisposableEffect(Unit) {
        val keySub = document.addKeyListener(KeyEventType.DOWN) { event ->
            if (event.key == "Escape") {
                closeModal()
            }
        }
        
        onDispose {
            keySub.unsubscribe()
        }
    }
}
```

---

## Common Events

Specialized event interfaces for common event types.

### Interface Definitions

```kotlin
package codes.yousef.summon.events

// Mouse events (click, mousedown, mouseup, etc.)
interface MouseEvent : Event {
    val clientX: Double
    val clientY: Double
    val screenX: Double
    val screenY: Double
    val button: Int
    val buttons: Int
    val altKey: Boolean
    val ctrlKey: Boolean
    val metaKey: Boolean
    val shiftKey: Boolean
}

// Keyboard events (keydown, keyup, keypress)
interface KeyboardEvent : Event {
    val key: String
    val code: String
    val location: KeyLocation
    val repeat: Boolean
    val altKey: Boolean
    val ctrlKey: Boolean
    val metaKey: Boolean
    val shiftKey: Boolean
    
    // Check if a specific key is pressed
    fun isKey(key: String): Boolean
}

enum class KeyLocation {
    STANDARD,
    LEFT,
    RIGHT,
    NUMPAD
}

// Focus events (focus, blur)
interface FocusEvent : Event {
    val relatedTarget: EventTarget?
}

// Input events
interface InputEvent : Event {
    val data: String?
    val inputType: String?
}

// Change events
interface ChangeEvent : Event {
    val value: String?
    val checked: Boolean?
}

// Form events
interface SubmitEvent : Event {
    // Get form data
    fun getFormData(): Map<String, String>
    
    // Get form element
    val form: EventTarget
}

// Touch events
interface TouchEvent : Event {
    val touches: List<Touch>
    val targetTouches: List<Touch>
    val changedTouches: List<Touch>
    val altKey: Boolean
    val ctrlKey: Boolean
    val metaKey: Boolean
    val shiftKey: Boolean
}

interface Touch {
    val identifier: Int
    val target: EventTarget
    val clientX: Double
    val clientY: Double
    val screenX: Double
    val screenY: Double
    val pageX: Double
    val pageY: Double
    val radiusX: Double?
    val radiusY: Double?
    val rotationAngle: Double?
    val force: Double?
}

// Drag events
interface DragEvent : MouseEvent {
    val dataTransfer: DataTransfer?
}

interface DataTransfer {
    var dropEffect: String
    var effectAllowed: String
    
    // Get/set data
    fun setData(format: String, data: String)
    fun getData(format: String): String
    fun clearData(format: String? = null)
    
    // Files
    val files: List<File>
    
    // Add files to the transfer
    fun addFile(file: File)
}
```

### Description

These specialized event interfaces provide type-safe access to event-specific properties and methods.

### Example

```kotlin
@Composable
fun EventHandlingExample() {
    // Mouse event handling
    Box(
        modifier = Modifier
            .size(200.dp)
            .backgroundColor(Color.LightBlue)
            .onClick { event ->
                // Access mouse event properties
                val x = event.clientX
                val y = event.clientY
                
                // Check modifier keys
                if (event.ctrlKey) {
                    // Handle ctrl+click
                }
            }
    )
    
    // Keyboard handling
    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier.onKeyDown { event ->
            when {
                // Handle Enter key
                event.isKey("Enter") -> {
                    submitForm()
                    true // Mark as handled
                }
                
                // Handle Escape key
                event.isKey("Escape") -> {
                    resetForm()
                    true
                }
                
                else -> false // Not handled
            }
        }
    )
    
    // Form submission
    Form(
        onSubmit = { event ->
            // Prevent default form submission
            event.preventDefault()
            
            // Get form data
            val formData = event.getFormData()
            val username = formData["username"]
            val password = formData["password"]
            
            // Process form
            loginUser(username, password)
        }
    ) {
        // Form fields
        TextField(name = "username")
        TextField(name = "password", type = "password")
        Button(text = "Submit", type = "submit")
    }
    
    // Touch handling
    Box(
        modifier = Modifier
            .size(300.dp)
            .backgroundColor(Color.LightGray)
            .addEventListener("touchstart") { event ->
                val touchEvent = event as TouchEvent
                for (touch in touchEvent.touches) {
                    // Handle each touch point
                    val id = touch.identifier
                    val x = touch.clientX
                    val y = touch.clientY
                    
                    startTouch(id, x, y)
                }
            }
    )
    
    // Drag and drop
    Box(
        modifier = Modifier
            .size(200.dp)
            .backgroundColor(Color.LightGreen)
            .addEventListener("dragover") { event ->
                event.preventDefault() // Allow drop
            }
            .addEventListener("drop") { event ->
                val dragEvent = event as DragEvent
                
                // Prevent default to allow drop
                event.preventDefault()
                
                // Get the dropped data
                val text = dragEvent.dataTransfer?.getData("text/plain")
                if (text != null) {
                    handleDroppedText(text)
                }
                
                // Get dropped files
                val files = dragEvent.dataTransfer?.files ?: emptyList()
                for (file in files) {
                    handleDroppedFile(file)
                }
            }
    )
    
    // Making an element draggable
    Image(
        src = "image.jpg",
        modifier = Modifier
            .draggable {
                // Set the data to transfer
                onDragStart { event ->
                    event.dataTransfer?.setData("text/plain", "Dragged image URL: image.jpg")
                }
            }
    )
}
```

---

## Custom Events

APIs for creating and dispatching custom events.

### Class Definitions

```kotlin
package codes.yousef.summon.events

// Custom event class
class CustomEvent(
    override val type: String,
    val detail: Any? = null,
    override val bubbles: Boolean = false,
    override val cancelable: Boolean = false
) : Event {
    // Event implementation
    // ...
}

// Event dispatcher
object EventDispatcher {
    // Dispatch an event to a target
    fun dispatch(target: EventTarget, event: Event)
    
    // Create and dispatch a custom event
    fun dispatchCustom(
        target: EventTarget,
        type: String,
        detail: Any? = null,
        bubbles: Boolean = false,
        cancelable: Boolean = false
    )
}
```

### Description

These APIs allow you to create and dispatch custom events within your application.

### Example

```kotlin
@Composable
fun CustomEventExample() {
    val productElement = remember { ElementRef() }
    
    DisposableEffect(Unit) {
        // Create a custom event
        val addToCartEvent = CustomEvent(
            type = "add-to-cart",
            detail = mapOf(
                "productId" to "12345",
                "quantity" to 2,
                "price" to 29.99
            ),
            bubbles = true,
            cancelable = true
        )
        
        // Create a listener for the custom event
        val subscription = appContainer.addEventListener("add-to-cart") { event ->
            val customEvent = event as CustomEvent
            val detail = customEvent.detail as? Map<String, Any> ?: return@addEventListener
            
            val productId = detail["productId"] as? String ?: return@addEventListener
            val quantity = detail["quantity"] as? Int ?: 1
            
            updateCart(productId, quantity)
        }
        
        onDispose {
            subscription.unsubscribe()
        }
    }
    
    Box(
        modifier = Modifier.ref(productElement)
    ) {
        Text("Product: Premium Widget")
        Button(
            text = "Add to Cart",
            onClick = {
                // Dispatch the custom event
                EventDispatcher.dispatchCustom(
                    target = productElement.current,
                    type = "add-to-cart",
                    detail = mapOf(
                        "productId" to "12345",
                        "quantity" to 2,
                        "price" to 29.99
                    ),
                    bubbles = true
                )
            }
        )
    }
}
```

---

## Platform-Specific Events

Events specific to particular platforms.

### Browser-Specific Events

```kotlin
package codes.yousef.summon.events.js

// Window events
interface WindowEvent : Event {
    val window: Window
}

// ResizeEvent
interface ResizeEvent : WindowEvent {
    val innerWidth: Int
    val innerHeight: Int
    val outerWidth: Int
    val outerHeight: Int
}

// ScrollEvent
interface ScrollEvent : Event {
    val scrollX: Double
    val scrollY: Double
}

// HashChangeEvent
interface HashChangeEvent : WindowEvent {
    val oldURL: String
    val newURL: String
}

// Access browser-specific events
@Composable
fun window.addResizeListener(listener: (ResizeEvent) -> Unit): EventSubscription
@Composable
fun window.addScrollListener(listener: (ScrollEvent) -> Unit): EventSubscription
@Composable
fun window.addHashChangeListener(listener: (HashChangeEvent) -> Unit): EventSubscription
@Composable
fun window.addPopStateListener(listener: (PopStateEvent) -> Unit): EventSubscription
```

### JVM-Specific Events

```kotlin
package codes.yousef.summon.events.jvm

// Window events for desktop applications
interface WindowEvent : Event {
    val window: Window
}

// WindowResizeEvent
interface WindowResizeEvent : WindowEvent {
    val width: Int
    val height: Int
}

// WindowMoveEvent
interface WindowMoveEvent : WindowEvent {
    val x: Int
    val y: Int
}

// Access JVM-specific events
@Composable
fun Window.addResizeListener(listener: (WindowResizeEvent) -> Unit): EventSubscription
@Composable
fun Window.addMoveListener(listener: (WindowMoveEvent) -> Unit): EventSubscription
@Composable
fun Window.addCloseListener(listener: (WindowEvent) -> Unit): EventSubscription
```

### Description

Platform-specific events provide access to capabilities unique to each platform while maintaining a consistent API structure.

### Example

```kotlin
@Composable
fun PlatformSpecificEventExample() {
    val isHeaderVisible = remember { mutableStateOf(true) }
    
    // JS Browser example
    DisposableEffect(Unit) {
        // Only execute in JS context
        var lastScrollY = 0.0
        
        val scrollSub = window.addScrollListener { event ->
            // Hide header when scrolling down, show when scrolling up
            val currentScrollY = event.scrollY
            isHeaderVisible.value = currentScrollY <= lastScrollY
            lastScrollY = currentScrollY
            
            // Load more content when reaching bottom
            if (currentScrollY + window.innerHeight >= document.body?.scrollHeight ?: 0.0) {
                loadMoreContent()
            }
        }
        
        val resizeSub = window.addResizeListener { event ->
            // Update layout based on new window size
            val width = event.innerWidth
            val height = event.innerHeight
            
            // Update responsive state
            updateLayoutForDimensions(width, height)
        }
        
        onDispose {
            scrollSub.unsubscribe()
            resizeSub.unsubscribe()
        }
    }
    
    // UI with conditional header
    Column {
        if (isHeaderVisible.value) {
            Header()
        }
        MainContent()
    }
    
    // Helper methods
    fun loadMoreContent() {
        // Load additional content
    }
    
    fun updateLayoutForDimensions(width: Int, height: Int) {
        // Update layout based on dimensions
    }
}

// JVM Desktop example
@Composable
fun DesktopWindowExample() {
    // Only execute in JVM context
    runJvmOnly {
        val window = remember { Window("My App", 800, 600) }
        
        DisposableEffect(window) {
            val resizeSub = window.addResizeListener { event ->
                // Update layout based on new window size
                updateLayoutForDimensions(event.width, event.height)
            }
            
            val closeSub = window.addCloseListener { event ->
                // Prompt user before closing
                if (hasUnsavedChanges) {
                    event.preventDefault()
                    showSavePrompt {
                        if (it) {
                            saveChanges()
                            window.close()
                        }
                    }
                }
            }
            
            onDispose {
                resizeSub.unsubscribe()
                closeSub.unsubscribe()
            }
        }
        
        // Render window content
        WindowContent()
    }
    
    // Helper methods
    fun updateLayoutForDimensions(width: Int, height: Int) {
        // Update layout based on dimensions
    }
}
```

---

The Summon event system is designed to provide a consistent API across platforms while still allowing access to platform-specific functionality when needed. By normalizing events, your application code can focus on the business logic rather than platform-specific details. 