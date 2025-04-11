# State Management

Summon provides flexible state management options that work across both JavaScript and JVM platforms, allowing you to build reactive UIs with ease.

## Local Component State

### Basic State

Use the `MutableState` class with `remember` for component-local state:

```kotlin
import code.yousef.summon.annotation.Composable
import code.yousef.summon.state.*
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.extensions.px

@Composable
fun Counter() {
    // Create a state variable
    var count by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier.padding(16.px)
    ) {
        Text("Count: $count")
        
        Button(
            text = "Increment",
            onClick = { count++ }
        )
    }
}
```

The `remember` function preserves state across recompositions, and changes to the state value trigger recomposition of components that use the state.

### Derived State

Create derived state that updates automatically when its dependencies change:

```kotlin
import code.yousef.summon.annotation.Composable
import code.yousef.summon.state.*
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.extensions.px

@Composable
fun TemperatureConverter() {
    var celsius by remember { mutableStateOf(0) }
    
    // Derived state - updates when celsius changes
    val fahrenheit by remember(celsius) { 
        derivedStateOf { (celsius * 9/5) + 32 }
    }
    
    Column(
        modifier = Modifier.padding(16.px).gap(8.px)
    ) {
        Text("Celsius: $celsius°C")
        Text("Fahrenheit: $fahrenheit°F")
        
        Button(
            text = "Increase Temperature",
            onClick = { celsius++ }
        )
    }
}
```

The `derivedStateOf` function creates a state that is computed from other state values and automatically updates when those dependencies change.

## Shared State

### State Hoisting

For sharing state between multiple components, "lift" the state to a common parent:

```kotlin
import code.yousef.summon.annotation.Composable

@Composable
fun ParentComponent() {
    // State is declared in the parent
    var sharedValue by remember { mutableStateOf("") }
    
    Column {
        // Pass state down to children
        ChildInput(
            value = sharedValue,
            onValueChange = { sharedValue = it }
        )
        
        ChildDisplay(value = sharedValue)
    }
}

@Composable
fun ChildInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = "Enter a value"
    )
}

@Composable
fun ChildDisplay(value: String) {
    Text("Current value: $value")
}
```

### StateFlow

For more complex scenarios, use `StateFlow` for observable state:

```kotlin
import code.yousef.summon.annotation.Composable
import code.yousef.summon.state.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Create a shared state holder class
class AppState {
    private val _counter = MutableStateFlow(0)
    val counter = _counter.asStateFlow()
    
    fun increment() {
        _counter.value++
    }
    
    fun decrement() {
        _counter.value--
    }
}

// Create a singleton instance
val appState = AppState()

// Use in components
@Composable
fun CounterDisplay() {
    // Collect the StateFlow as state
    val count by appState.counter.collectAsState()
    
    Text("Count: $count")
}

@Composable
fun CounterControls() {
    Row(
        modifier = Modifier.gap(8.px)
    ) {
        Button(
            text = "Increment",
            onClick = { appState.increment() }
        )
        
        Button(
            text = "Decrement",
            onClick = { appState.decrement() }
        )
    }
}
```

The `collectAsState` function converts a `StateFlow` into a state object that can be used in components and causes recomposition when the flow's value changes.

## State Containers

For more structured state management, use a state container:

```kotlin
import code.yousef.summon.annotation.Composable
import code.yousef.summon.state.*

// Define state and actions
data class TodoState(
    val items: List<TodoItem> = emptyList(),
    val newItemText: String = "",
    val filter: TodoFilter = TodoFilter.ALL
)

enum class TodoFilter { ALL, ACTIVE, COMPLETED }

data class TodoItem(
    val id: String,
    val text: String,
    val completed: Boolean
)

// Define actions
sealed class TodoAction {
    data class SetNewItemText(val text: String) : TodoAction()
    object AddItem : TodoAction()
    data class ToggleItem(val id: String) : TodoAction()
    data class RemoveItem(val id: String) : TodoAction()
    data class SetFilter(val filter: TodoFilter) : TodoAction()
}

// Create a reducer function
fun todoReducer(state: TodoState, action: TodoAction): TodoState {
    return when (action) {
        is TodoAction.SetNewItemText -> state.copy(newItemText = action.text)
        is TodoAction.AddItem -> {
            if (state.newItemText.isBlank()) return state
            val newItem = TodoItem(
                id = UUID.randomUUID().toString(),
                text = state.newItemText,
                completed = false
            )
            state.copy(
                items = state.items + newItem,
                newItemText = ""
            )
        }
        is TodoAction.ToggleItem -> {
            val updatedItems = state.items.map { item ->
                if (item.id == action.id) {
                    item.copy(completed = !item.completed)
                } else {
                    item
                }
            }
            state.copy(items = updatedItems)
        }
        is TodoAction.RemoveItem -> {
            val updatedItems = state.items.filter { it.id != action.id }
            state.copy(items = updatedItems)
        }
        is TodoAction.SetFilter -> state.copy(filter = action.filter)
    }
}

// Create a store
val todoStore = createStore(TodoState(), ::todoReducer)

// Use in components
@Composable
fun TodoApp() {
    val state by todoStore.state.collectAsState()
    
    Column(
        modifier = Modifier.padding(16.px).gap(16.px)
    ) {
        // Add new todo
        Row(
            modifier = Modifier.gap(8.px)
        ) {
            TextField(
                value = state.newItemText,
                onValueChange = { todoStore.dispatch(TodoAction.SetNewItemText(it)) },
                placeholder = "Add a new todo"
            )
            
            Button(
                text = "Add",
                onClick = { todoStore.dispatch(TodoAction.AddItem) }
            )
        }
        
        // Filter controls
        Row(
            modifier = Modifier.gap(8.px)
        ) {
            TodoFilter.values().forEach { filter ->
                Button(
                    text = filter.name,
                    onClick = { todoStore.dispatch(TodoAction.SetFilter(filter)) },
                    modifier = Modifier.applyIf(state.filter == filter) {
                        backgroundColor("#0077cc").color("#ffffff")
                    }
                )
            }
        }
        
        // Todo list
        val filteredItems = when (state.filter) {
            TodoFilter.ALL -> state.items
            TodoFilter.ACTIVE -> state.items.filter { !it.completed }
            TodoFilter.COMPLETED -> state.items.filter { it.completed }
        }
        
        Column(
            modifier = Modifier.gap(8.px)
        ) {
            filteredItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .padding(8.px)
                        .gap(8.px)
                        .alignItems(AlignItems.Center)
                ) {
                    Checkbox(
                        checked = item.completed,
                        onCheckedChange = { todoStore.dispatch(TodoAction.ToggleItem(item.id)) }
                    )
                    
                    Text(
                        text = item.text,
                        modifier = Modifier.applyIf(item.completed) {
                            textDecoration(TextDecoration.LineThrough)
                        }
                    )
                    
                    Button(
                        text = "Delete",
                        onClick = { todoStore.dispatch(TodoAction.RemoveItem(item.id)) }
                    )
                }
            }
        }
    }
}
```

This pattern provides a predictable state container with unidirectional data flow, similar to Redux in the React ecosystem.

## Persistence

Summon provides cross-platform persistence for state:

```kotlin
import code.yousef.summon.state.*

// Define persisted state
data class UserPreferences(
    val theme: String = "light",
    val fontSize: Int = 16
)

// Create a persisted state container
val userPreferences = persistentStateOf(
    "user_preferences", // Storage key
    UserPreferences(), // Default value
    UserPreferences::class // Class reference for serialization
)

// Use in components
class SettingsComponent : Composable {
    override fun render() {
        var preferences by userPreferences
        
        Column(
            modifier = Modifier.padding(16.px).gap(16.px)
        ) {
            // Theme selector
            Row(
                modifier = Modifier.gap(8.px)
            ) {
                Text("Theme:")
                
                Button(
                    text = "Light",
                    onClick = { 
                        preferences = preferences.copy(theme = "light")
                    },
                    modifier = Modifier.opacity(
                        if (preferences.theme == "light") 1.0 else 0.5
                    )
                )
                
                Button(
                    text = "Dark",
                    onClick = { 
                        preferences = preferences.copy(theme = "dark")
                    },
                    modifier = Modifier.opacity(
                        if (preferences.theme == "dark") 1.0 else 0.5
                    )
                )
            }
            
            // Font size selector
            Row(
                modifier = Modifier.gap(8.px).alignItems(AlignItems.Center)
            ) {
                Text("Font Size:")
                
                Button(
                    text = "-",
                    onClick = { 
                        preferences = preferences.copy(
                            fontSize = (preferences.fontSize - 1).coerceAtLeast(12)
                        )
                    }
                )
                
                Text("${preferences.fontSize}px")
                
                Button(
                    text = "+",
                    onClick = { 
                        preferences = preferences.copy(
                            fontSize = (preferences.fontSize + 1).coerceAtMost(24)
                        )
                    }
                )
            }
        }
    }
}
```

The `persistentStateOf` function creates a state that is automatically saved to local storage on the JS platform and preferences/properties on the JVM platform.

## Platform-Specific State

Summon provides platform-specific state extensions:

### JS Platform

```kotlin
import code.yousef.summon.state.*

// Use browser-specific state
val windowSize by windowSizeState()

Column {
    Text("Window width: ${windowSize.width}px")
    Text("Window height: ${windowSize.height}px")
}
```

### JVM Platform

```kotlin
import code.yousef.summon.state.*

// Use JVM-specific state
val systemProperties by systemPropertiesState()

Column {
    Text("Java version: ${systemProperties["java.version"]}")
    Text("OS name: ${systemProperties["os.name"]}")
}
``` 