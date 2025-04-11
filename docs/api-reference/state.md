# State Management API Reference

This document provides detailed information about the state management APIs in the Summon library.

## Table of Contents

- [MutableState](#mutablestate)
- [Remember](#remember)
- [DerivedState](#derivedstate)
- [StateFlow Integration](#stateflow-integration)
- [StateContainer](#statecontainer)
- [Persistent State](#persistent-state)
- [Platform-Specific State](#platform-specific-state)

---

## MutableState

The `MutableState` class is the fundamental building block for state management in Summon.

### Class Definition

```kotlin
package code.yousef.summon.state

class MutableState<T>(initialValue: T) {
    var value: T
    
    fun component1(): T
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}

fun <T> mutableStateOf(initialValue: T): MutableState<T>
```

### Description

`MutableState` is a container for a value that can change over time. When the value changes, any components that use this state will be automatically recomposed.

### Example

```kotlin
@Composable
fun CounterExample() {
    // Direct usage
    val state = mutableStateOf(0)
    Text("Count: ${state.value}")
    Button(
        text = "Increment",
        onClick = { state.value++ }
    )

    // Using property delegate
    var count by mutableStateOf(0)
    Text("Count: $count")
    Button(
        text = "Increment",
        onClick = { count++ }
    )

    // Destructuring
    val (count2, setCount) = mutableStateOf(0)
    Text("Count: $count2")
    Button(
        text = "Increment",
        onClick = { setCount(count2 + 1) }
    )
}
```

---

## Remember

The `remember` function is used to preserve state across recompositions.

### Function Definition

```kotlin
package code.yousef.summon.state

@Composable
fun <T> remember(calculation: () -> T): T
@Composable
fun <T, A> remember(a: A, calculation: (A) -> T): T
@Composable
fun <T, A, B> remember(a: A, b: B, calculation: (A, B) -> T): T
@Composable
fun <T, A, B, C> remember(a: A, b: B, c: C, calculation: (A, B, C) -> T): T
```

### Description

`remember` memoizes the result of a calculation and returns the same value across recompositions, unless one of the keys (parameters) changes.

### Example

```kotlin
@Composable
fun RememberExample(items: List<String>, searchQuery: String) {
    // Simple remember
    val count = remember { mutableStateOf(0) }

    // With keys
    val filteredItems = remember(items, searchQuery) {
        items.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    // Combining with property delegate
    var count2 by remember { mutableStateOf(0) }
    
    Column {
        Text("Count: ${count.value}")
        Button(
            text = "Increment",
            onClick = { count.value++ }
        )
        
        Text("Filtered Items: ${filteredItems.size}")
        
        Text("Count 2: $count2")
        Button(
            text = "Increment",
            onClick = { count2++ }
        )
    }
}
```

---

## DerivedState

`DerivedState` represents a state that is computed from other state values.

### Class Definition

```kotlin
package code.yousef.summon.state

class DerivedState<T>(private val calculation: () -> T) {
    val value: T
    
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}

fun <T> derivedStateOf(calculation: () -> T): DerivedState<T>
```

### Description

`DerivedState` calculates a value based on other state values. The value is recomputed when any of the state values it depends on changes.

### Example

```kotlin
@Composable
fun DerivedStateExample() {
    val firstName = remember { mutableStateOf("John") }
    val lastName = remember { mutableStateOf("Doe") }

    val fullName by derivedStateOf { "${firstName.value} ${lastName.value}" }

    Column {
        Text("Full Name: $fullName")
        
        TextField(
            value = firstName.value,
            onValueChange = { firstName.value = it },
            label = "First Name"
        )
        
        TextField(
            value = lastName.value,
            onValueChange = { lastName.value = it },
            label = "Last Name"
        )
    }
}
```

---

## StateFlow Integration

Summon provides integration with Kotlin's `StateFlow` for more complex state management.

### Functions

```kotlin
package code.yousef.summon.state

@Composable
fun <T> StateFlow<T>.collectAsState(): State<T>
```

### Description

The `collectAsState` extension function converts a `StateFlow` into a state object that can be used in Summon components.

### Example

```kotlin
// Create a StateFlow in a ViewModel or service
class CounterViewModel {
    private val _count = MutableStateFlow(0)
    val count = _count.asStateFlow()
    
    fun increment() {
        _count.value++
    }
    
    fun decrement() {
        _count.value--
    }
}

@Composable
fun StateFlowExample() {
    // Use the StateFlow in a component
    val viewModel = remember { CounterViewModel() }
    val count by viewModel.count.collectAsState()

    Column {
        Text("Count: $count")
        Button(
            text = "Increment",
            onClick = { viewModel.increment() }
        )
        Button(
            text = "Decrement",
            onClick = { viewModel.decrement() }
        )
    }
}
```

---

## StateContainer

The `StateContainer` class provides a structured approach to state management, similar to Redux.

### Class Definition

```kotlin
package code.yousef.summon.state

abstract class StateContainer<S, A>(initialState: S) {
    val state: StateFlow<S>
    
    fun dispatch(action: A)
    
    protected abstract fun reduce(state: S, action: A): S
}
```

### Description

`StateContainer` implements a unidirectional data flow pattern where state can only be modified by dispatching actions, which are processed by a reducer function to produce a new state.

### Example

```kotlin
// Define state
data class TodoState(
    val items: List<String> = emptyList(),
    val newItemText: String = ""
)

// Define actions
sealed class TodoAction {
    data class SetNewItemText(val text: String) : TodoAction()
    object AddItem : TodoAction()
    data class RemoveItem(val index: Int) : TodoAction()
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
                    items = state.items + state.newItemText,
                    newItemText = ""
                )
            }
            is TodoAction.RemoveItem -> state.copy(
                items = state.items.filterIndexed { index, _ -> index != action.index }
            )
        }
    }
}

@Composable
fun TodoApp() {
    // Use in components
    val todoContainer = remember { TodoContainer() }
    val state by todoContainer.state.collectAsState()

    Column {
        // Input for new items
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
        
        // List of items
        for ((index, item) in state.items.withIndex()) {
            Row {
                Text(item)
                Button(
                    text = "Remove",
                    onClick = { 
                        todoContainer.dispatch(TodoAction.RemoveItem(index))
                    }
                )
            }
        }
    }
}
```

---

## Persistent State

Summon provides cross-platform APIs for persistent state that survives application restarts.

### Functions

```kotlin
package code.yousef.summon.state

@Composable
fun <T> persistentStateOf(
    key: String,
    defaultValue: T,
    serializer: KSerializer<T>
): MutableState<T>

@Composable
fun <T> persistentStateOf(
    key: String,
    defaultValue: T,
    type: KClass<T>
): MutableState<T>
```

### Description

`persistentStateOf` creates a state that is automatically saved to local storage on the JS platform and preferences/properties on the JVM platform.

### Example

```kotlin
@Serializable
data class UserPreferences(
    val theme: String = "light",
    val fontSize: Int = 16
)

@Composable
fun PreferencesSettings() {
    // Create a persistent state
    val preferences = persistentStateOf(
        "user_preferences",
        UserPreferences(),
        UserPreferences::class
    )

    // Use in components
    var userPrefs by preferences

    Column {
        // Theme selector
        Text("Theme:")
        Row {
            Button(
                text = "Light",
                onClick = { 
                    userPrefs = userPrefs.copy(theme = "light")
                },
                modifier = Modifier.opacity(
                    if (userPrefs.theme == "light") 1.0 else 0.5
                )
            )
            
            Button(
                text = "Dark",
                onClick = { 
                    userPrefs = userPrefs.copy(theme = "dark")
                },
                modifier = Modifier.opacity(
                    if (userPrefs.theme == "dark") 1.0 else 0.5
                )
            )
        }
        
        // Font size selector
        Text("Font Size: ${userPrefs.fontSize}px")
        Row {
            Button(
                text = "-",
                onClick = { 
                    userPrefs = userPrefs.copy(
                        fontSize = (userPrefs.fontSize - 1).coerceAtLeast(12)
                    )
                }
            )
            
            Button(
                text = "+",
                onClick = { 
                    userPrefs = userPrefs.copy(
                        fontSize = (userPrefs.fontSize + 1).coerceAtMost(24)
                    )
                }
            )
        }
    }
}
```

---

## Platform-Specific State

Summon provides platform-specific state utilities.

### JS Platform

```kotlin
package code.yousef.summon.state.js

data class WindowSize(val width: Int, val height: Int)

@Composable
fun windowSizeState(): State<WindowSize>
@Composable
fun mediaQueryState(query: String): State<Boolean>
@Composable
fun localStorageState(key: String, defaultValue: String): MutableState<String>
@Composable
fun sessionStorageState(key: String, defaultValue: String): MutableState<String>
```

### JVM Platform

```kotlin
package code.yousef.summon.state.jvm

@Composable
fun systemPropertiesState(): State<Map<String, String>>
@Composable
fun preferencesState(path: String): MutableState<Map<String, String>>
@Composable
fun fileBasedState(file: File, serializer: KSerializer<T>): MutableState<T>
```

### Example

```kotlin
@Composable
fun JsPlatformStateExample() {
    // JS Platform
    val windowSize by windowSizeState()
    val isDarkMode by mediaQueryState("(prefers-color-scheme: dark)")
    var token by localStorageState("auth_token", "")

    Column {
        Text("Window width: ${windowSize.width}px")
        Text("Window height: ${windowSize.height}px")
        Text("Dark mode: $isDarkMode")
        
        if (token.isNotEmpty()) {
            Text("Logged in with token: $token")
            Button(
                text = "Logout",
                onClick = { token = "" }
            )
        } else {
            Button(
                text = "Login",
                onClick = { token = "example-token" }
            )
        }
    }
}

@Composable
fun JvmPlatformStateExample() {
    // JVM Platform
    val systemProps by systemPropertiesState()
    var appPrefs by preferencesState("app_preferences")

    Column {
        Text("Java version: ${systemProps["java.version"]}")
        Text("OS name: ${systemProps["os.name"]}")
        
        TextField(
            value = appPrefs["username"] ?: "",
            onValueChange = { 
                appPrefs = appPrefs + ("username" to it)
            },
            placeholder = "Username"
        )
        
        Button(
            text = "Save Preferences",
            onClick = { /* Automatically saved */ }
        )
    }
}
``` 