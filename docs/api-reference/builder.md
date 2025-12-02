# Visual Builder API Reference

This document provides detailed API reference for Summon's Visual Builder infrastructure. These APIs enable building drag-and-drop UI editors, visual component builders, and live preview systems.

## Table of Contents

- [ComponentRegistry](#componentregistry)
- [JsonBlock](#jsonblock)
- [RenderLoop](#renderloop)
- [SelectionManager](#selectionmanager)
- [HistoryManager](#historymanager)
- [PropertyBridge](#propertybridge)
- [EditModeManager](#editmodemanager)
- [CollisionDetector](#collisiondetector)

---

## ComponentRegistry

Thread-safe singleton registry for mapping component type keys to their factory functions.

### Object Definition

```kotlin
object ComponentRegistry {
    fun register(key: String, factory: ComponentFactory)
    fun get(key: String): ComponentFactory
    fun isRegistered(key: String): Boolean
    fun unregister(key: String): Boolean
    fun clear()
    fun size(): Int
    fun keys(): Set<String>
}
```

### Type Definitions

```kotlin
typealias ComponentFactory = (props: Map<String, Any>) -> @Composable () -> Unit
```

### Methods

#### `register(key: String, factory: ComponentFactory)`
Registers a component factory with the given key. If a factory already exists for the key, it will be overwritten (useful for hot reload).

**Parameters:**
- `key` - The unique identifier for the component type
- `factory` - The factory function that creates composables from props

**Example:**
```kotlin
ComponentRegistry.register("text") { props ->
    { Text(text = props["text"] as? String ?: "") }
}

ComponentRegistry.register("button") { props ->
    { Button(
        onClick = props["onClick"] as? (() -> Unit) ?: {},
        label = props["label"] as? String ?: "Button"
    ) }
}
```

#### `get(key: String): ComponentFactory`
Retrieves a component factory by key. Returns a `FallbackComponent` factory if not found.

**Parameters:**
- `key` - The component type key to look up

**Returns:** The registered factory, or a FallbackComponent factory if not found

#### `isRegistered(key: String): Boolean`
Checks if a component is registered under the given key.

#### `unregister(key: String): Boolean`
Removes a component registration. Returns `true` if removed.

#### `clear()`
Clears all component registrations.

---

## JsonBlock

Data class for JSON-based component tree representation.

### Class Definition

```kotlin
data class JsonBlock(
    val type: String,
    val props: Map<String, Any> = emptyMap(),
    val children: List<JsonBlock> = emptyList()
)
```

### Properties

- `type` - The component type key registered in the ComponentRegistry
- `props` - The properties map to pass to the component factory
- `children` - Optional list of child blocks for container components

### Example

```kotlin
// Create a 3-level deep JSON tree
val tree = JsonBlock(
    type = "Column",
    props = mapOf("spacing" to 8),
    children = listOf(
        JsonBlock(
            type = "Text",
            props = mapOf("text" to "Hello"),
            children = emptyList()
        ),
        JsonBlock(
            type = "Button",
            props = mapOf("label" to "Click me"),
            children = emptyList()
        )
    )
)
```

---

## RenderLoop

Reactive render loop with batched updates and frame-rate limiting for efficient visual builder rendering.

### Object Definition

```kotlin
object RenderLoop {
    val isRunning: SummonMutableState<Boolean>
    var targetFps: Int
    
    fun start()
    fun stop()
    fun requestRender()
    fun onFrame(callback: (deltaTime: Long) -> Unit)
    fun removeFrameCallback(callback: (deltaTime: Long) -> Unit)
}
```

### Methods

#### `start()`
Starts the render loop. Frame callbacks will be invoked at the target FPS.

#### `stop()`
Stops the render loop.

#### `requestRender()`
Requests a single render frame (batched with other requests).

#### `onFrame(callback: (deltaTime: Long) -> Unit)`
Registers a callback to be invoked on each frame. `deltaTime` is milliseconds since last frame.

### Example

```kotlin
// Start render loop at 60 FPS
RenderLoop.targetFps = 60
RenderLoop.start()

// Register frame callback
RenderLoop.onFrame { deltaTime ->
    // Update animations, render preview, etc.
    updateAnimations(deltaTime)
}

// Request render when something changes
fun onPropertyChange() {
    RenderLoop.requestRender()
}
```

---

## SelectionManager

Singleton for managing component selection state in visual editors.

### Object Definition

```kotlin
object SelectionManager {
    val selectedId: SummonMutableState<String?>
    var onSelectionChange: ((String?) -> Unit)?
    
    fun select(componentId: String)
    fun deselect()
    fun toggle(componentId: String)
    fun hasSelection(): Boolean
    fun reset()
}
```

### Properties

- `selectedId` - Reactive state holding the currently selected component ID (null if none)
- `onSelectionChange` - Optional callback invoked when selection changes

### Methods

#### `select(componentId: String)`
Selects a component by ID.

#### `deselect()`
Clears the current selection.

#### `toggle(componentId: String)`
Toggles selection for a component (selects if not selected, deselects if already selected).

#### `hasSelection(): Boolean`
Returns `true` if any component is currently selected.

#### `reset()`
Resets the selection manager to initial state.

### Example

```kotlin
// Select a component
SelectionManager.select("component-123")

// Listen for selection changes
SelectionManager.onSelectionChange = { id ->
    if (id != null) {
        showPropertyPanel(id)
    } else {
        hidePropertyPanel()
    }
}

// Check selection state
if (SelectionManager.hasSelection()) {
    val selected = SelectionManager.selectedId.value
    // ...
}
```

---

## HistoryManager

Generic undo/redo history manager with state snapshots.

### Class Definition

```kotlin
class HistoryManager<T>(
    var maxHistorySize: Int = 50
) {
    fun push(state: T)
    fun current(): T
    fun canUndo(): Boolean
    fun canRedo(): Boolean
    fun undo(): Boolean
    fun redo(): Boolean
    fun size(): Int
    fun clear()
}
```

### Methods

#### `push(state: T)`
Pushes a new state onto the history stack. Discards any future states if not at the end.

#### `current(): T`
Returns the current state. Throws `NoSuchElementException` if history is empty.

#### `canUndo(): Boolean` / `canRedo(): Boolean`
Checks if undo/redo is available.

#### `undo(): Boolean` / `redo(): Boolean`
Navigates through history. Returns `true` if the operation was performed.

### JsonTreeHistoryManager

Specialized singleton for JSON component tree history:

```kotlin
object JsonTreeHistoryManager {
    val currentState: SummonMutableState<List<JsonBlock>>
    val canUndo: SummonMutableState<Boolean>
    val canRedo: SummonMutableState<Boolean>
    var maxHistorySize: Int
    
    fun initialize(initialState: List<JsonBlock>)
    fun push(newState: List<JsonBlock>)
    fun undo(): Boolean
    fun redo(): Boolean
    fun getCurrentState(): List<JsonBlock>
    fun clear()
}
```

### Example

```kotlin
// Generic history for any state type
val history = HistoryManager<MyState>()
history.push(initialState)
history.push(newState)
history.undo() // Back to initialState
history.redo() // Forward to newState

// JSON tree history (singleton)
JsonTreeHistoryManager.initialize(initialTree)
JsonTreeHistoryManager.push(updatedTree)
if (JsonTreeHistoryManager.canUndo.value) {
    JsonTreeHistoryManager.undo()
}
```

---

## PropertyBridge

Bridge for live property updates between visual editor and renderer.

### Object Definition

```kotlin
object PropertyBridge {
    val currentTree: SummonMutableState<List<JsonBlock>>
    var onPropertyChange: ((componentId: String, propName: String, value: Any?) -> Unit)?
    
    fun registerComponent(componentId: String, initialProps: Map<String, Any?>)
    fun updateProperty(componentId: String, propName: String, value: Any?): Boolean
    fun updateProperties(componentId: String, properties: Map<String, Any?>): Boolean
    fun getProperty(componentId: String, propName: String): Any?
    fun getAllProperties(componentId: String): Map<String, Any?>
    fun removeProperty(componentId: String, propName: String)
    fun addChild(parentId: String, child: JsonBlock, index: Int = -1): Boolean
    fun removeComponent(componentId: String): Boolean
    fun moveComponent(componentId: String, newParentId: String, index: Int = -1): Boolean
    fun clear()
    fun syncFromHistory()
}
```

### Methods

#### `registerComponent(componentId: String, initialProps: Map<String, Any?>)`
Registers a component with its initial properties for tracking.

#### `updateProperty(componentId: String, propName: String, value: Any?): Boolean`
Updates a single property on a component. Returns `true` if successful.

#### `addChild(parentId: String, child: JsonBlock, index: Int = -1): Boolean`
Adds a new child to a container component. Use `index = -1` to append at end.

#### `removeComponent(componentId: String): Boolean`
Removes a component from the tree.

#### `moveComponent(componentId: String, newParentId: String, index: Int = -1): Boolean`
Moves a component to a new parent or position.

### Example

```kotlin
// Register component
PropertyBridge.registerComponent("text-1", mapOf("text" to "Hello"))

// Update property (triggers onPropertyChange)
PropertyBridge.updateProperty("text-1", "text", "World")

// Listen for changes
PropertyBridge.onPropertyChange = { id, prop, value ->
    println("Component $id property '$prop' changed to: $value")
}

// Add child to container
val newButton = JsonBlock("Button", mapOf("label" to "New"))
PropertyBridge.addChild("container-1", newButton)
```

---

## EditModeManager

Singleton for toggling between edit and preview modes.

### Object Definition

```kotlin
object EditModeManager {
    val isEditMode: SummonMutableState<Boolean>
    var onModeChange: ((Boolean) -> Unit)?
    
    fun enterEditMode()
    fun exitEditMode()
    fun toggleEditMode()
    fun reset()
}
```

### Properties

- `isEditMode` - Reactive state indicating current mode (`true` = edit, `false` = preview)
- `onModeChange` - Optional callback invoked when mode changes

### Example

```kotlin
// Toggle edit mode
Button(onClick = { EditModeManager.toggleEditMode() }) {
    Text(if (EditModeManager.isEditMode.value) "Preview" else "Edit")
}

// Listen for mode changes
EditModeManager.onModeChange = { isEdit ->
    if (isEdit) {
        showEditToolbar()
    } else {
        hideEditToolbar()
    }
}
```

---

## CollisionDetector

Singleton for hit testing and drop zone management in drag-and-drop interfaces.

### Object Definition

```kotlin
object CollisionDetector {
    fun registerDropZone(id: String, bounds: Bounds, priority: Int = 0)
    fun unregisterDropZone(id: String)
    fun updateDropZone(id: String, bounds: Bounds)
    fun findDropTarget(x: Double, y: Double): DropZone?
    fun clear()
}
```

### Data Classes

```kotlin
data class Bounds(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double
)

data class DropZone(
    val id: String,
    val bounds: Bounds,
    val priority: Int = 0
)
```

### Methods

#### `registerDropZone(id: String, bounds: Bounds, priority: Int = 0)`
Registers a drop zone with its bounding rectangle. Higher priority zones are matched first when overlapping.

#### `findDropTarget(x: Double, y: Double): DropZone?`
Finds the highest priority drop zone containing the given point.

### Example

```kotlin
// Register drop zones
CollisionDetector.registerDropZone(
    id = "container-1",
    bounds = Bounds(0.0, 0.0, 400.0, 300.0),
    priority = 1
)

// During drag
fun onDrag(x: Double, y: Double) {
    val target = CollisionDetector.findDropTarget(x, y)
    if (target != null) {
        highlightDropZone(target.id)
    }
}

// On drop
fun onDrop(x: Double, y: Double, draggedId: String) {
    val target = CollisionDetector.findDropTarget(x, y)
    if (target != null) {
        PropertyBridge.moveComponent(draggedId, target.id)
    }
}
```

---

## FallbackComponent

Visual error component displayed when a component type is not found in the registry.

### Function Definition

```kotlin
@Composable
fun FallbackComponent(missingKey: String, props: Map<String, Any>)
```

Renders a container with a red dashed border showing the missing component key and its props, making missing components immediately visible during development.

---

## Best Practices

1. **Register components early**: Register all component types before building UI trees
2. **Use unique IDs**: Ensure all component IDs are unique within the tree
3. **Batch property updates**: Use `updateProperties()` for multiple changes
4. **Save history checkpoints**: Push to history after logical edit operations, not every keystroke
5. **Clear on unmount**: Call `clear()` methods when unmounting the builder
6. **Handle missing components**: The FallbackComponent makes debugging easier - check the console for warnings

## See Also

- [Core API](core.md) - Core interfaces and classes
- [State API](state.md) - State management utilities
- [Components API](components.md) - UI components reference
