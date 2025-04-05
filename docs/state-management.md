# State Management

Summon provides flexible state management options that work across both JavaScript and JVM platforms, allowing you to build reactive UIs with ease.

## Local Component State

### Basic State

Use the `MutableState` class with `remember` for component-local state:

```kotlin
import code.yousef.summon.state.*

class Counter : Composable {
    override fun render() {
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
}
```

The `remember` function preserves state across recompositions, and changes to the state value trigger recomposition of components that use the state.

### Derived State

Create derived state that updates automatically when its dependencies change:

```kotlin
import code.yousef.summon.state.*

class TemperatureConverter : Composable {
    override fun render() {
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
}
```

The `derivedStateOf` function creates a state that is computed from other state values and automatically updates when those dependencies change.

## Shared State

### State Hoisting

For sharing state between multiple components, "lift" the state to a common parent:

```kotlin
class ParentComponent : Composable {
    override fun render() {
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
}

class ChildInput(
    private val value: String,
    private val onValueChange: (String) -> Unit
) : Composable {
    override fun render() {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = "Enter a value"
        )
    }
}

class ChildDisplay(private val value: String) : Composable {
    override fun render() {
        Text("Current value: $value")
    }
}
```

### StateFlow

For more complex scenarios, use `StateFlow` for observable state:

```kotlin
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
class CounterDisplay : Composable {
    override fun render() {
        // Collect the StateFlow as state
        val count by appState.counter.collectAsState()
        
        Text("Count: $count")
    }
}

class CounterControls : Composable {
    override fun render() {
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
}
```

The `collectAsState` function converts a `StateFlow` into a state object that can be used in components and causes recomposition when the flow's value changes.

## State Containers

For more structured state management, use a state container:

```kotlin
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

// Create state container
class TodoContainer : StateContainer<TodoState, TodoAction>(TodoState()) {
    override fun reduce(state: TodoState, action: TodoAction): TodoState {
        return when (action) {
            is TodoAction.SetNewItemText -> state.copy(
                newItemText = action.text
            )
            
            is TodoAction.AddItem -> {
                if (state.newItemText.isBlank()) return state
                
                state.copy(
                    items = state.items + TodoItem(
                        id = generateId(),
                        text = state.newItemText,
                        completed = false
                    ),
                    newItemText = ""
                )
            }
            
            is TodoAction.ToggleItem -> state.copy(
                items = state.items.map { item ->
                    if (item.id == action.id) {
                        item.copy(completed = !item.completed)
                    } else {
                        item
                    }
                }
            )
            
            is TodoAction.RemoveItem -> state.copy(
                items = state.items.filter { it.id != action.id }
            )
            
            is TodoAction.SetFilter -> state.copy(
                filter = action.filter
            )
        }
    }
    
    private fun generateId(): String = 
        System.currentTimeMillis().toString()
}

// Create a singleton instance
val todoContainer = TodoContainer()

// Use in components
class TodoApp : Composable {
    override fun render() {
        // Get the current state
        val state by todoContainer.state.collectAsState()
        
        // Filtered items
        val filteredItems = remember(state.items, state.filter) {
            when (state.filter) {
                TodoFilter.ALL -> state.items
                TodoFilter.ACTIVE -> state.items.filter { !it.completed }
                TodoFilter.COMPLETED -> state.items.filter { it.completed }
            }
        }
        
        Column(
            modifier = Modifier.padding(16.px).gap(16.px)
        ) {
            // Input for new items
            Row(
                modifier = Modifier.gap(8.px)
            ) {
                TextField(
                    value = state.newItemText,
                    onValueChange = { 
                        todoContainer.dispatch(TodoAction.SetNewItemText(it))
                    },
                    placeholder = "Add new todo"
                )
                
                Button(
                    text = "Add",
                    onClick = { 
                        todoContainer.dispatch(TodoAction.AddItem)
                    }
                )
            }
            
            // Filter controls
            Row(
                modifier = Modifier.gap(8.px)
            ) {
                Button(
                    text = "All",
                    onClick = { 
                        todoContainer.dispatch(TodoAction.SetFilter(TodoFilter.ALL))
                    },
                    modifier = Modifier.opacity(
                        if (state.filter == TodoFilter.ALL) 1.0 else 0.5
                    )
                )
                
                Button(
                    text = "Active",
                    onClick = { 
                        todoContainer.dispatch(TodoAction.SetFilter(TodoFilter.ACTIVE))
                    },
                    modifier = Modifier.opacity(
                        if (state.filter == TodoFilter.ACTIVE) 1.0 else 0.5
                    )
                )
                
                Button(
                    text = "Completed",
                    onClick = { 
                        todoContainer.dispatch(TodoAction.SetFilter(TodoFilter.COMPLETED))
                    },
                    modifier = Modifier.opacity(
                        if (state.filter == TodoFilter.COMPLETED) 1.0 else 0.5
                    )
                )
            }
            
            // Todo list
            Column(
                modifier = Modifier.gap(8.px)
            ) {
                for (item in filteredItems) {
                    Row(
                        modifier = Modifier
                            .padding(8.px)
                            .gap(8.px)
                            .alignItems(AlignItems.Center)
                    ) {
                        Checkbox(
                            checked = item.completed,
                            onCheckedChange = { 
                                todoContainer.dispatch(TodoAction.ToggleItem(item.id))
                            }
                        )
                        
                        Text(
                            text = item.text,
                            modifier = if (item.completed) {
                                Modifier.textDecoration(TextDecoration.LineThrough)
                            } else {
                                Modifier
                            }
                        )
                        
                        Button(
                            text = "Remove",
                            onClick = { 
                                todoContainer.dispatch(TodoAction.RemoveItem(item.id))
                            }
                        )
                    }
                }
            }
        }
    }
}
```

The `StateContainer` class provides a structured way to manage state using a reducer pattern similar to Redux, making it easier to manage complex state and actions.

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