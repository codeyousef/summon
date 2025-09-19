# State Management API Reference

This document provides detailed information about the state management APIs in the Summon library.

## Table of Contents

- [Core State Interfaces](#core-state-interfaces)
- [MutableState](#mutablestate)
- [Remember Functions](#remember-functions)
- [Derived State](#derived-state)
- [Flow Integration](#flow-integration)
- [Saveable State](#saveable-state)
- [Usage Examples](#usage-examples)

---

## Core State Interfaces

Summon provides a hierarchy of state interfaces for different use cases.

### State<T>

The base interface for read-only state holders.

```kotlin
interface State<T> {
    val value: T
}
```

### SummonMutableState<T>

Interface for mutable state holders.

```kotlin
interface SummonMutableState<T> : State<T> {
    override var value: T
}
```

### MutableState<T>

Extended mutable state interface with component functions and listeners.

```kotlin
interface MutableState<T> : SummonMutableState<T> {
    operator fun component1(): T
    operator fun component2(): (T) -> Unit

    fun addListener(listener: (T) -> Unit)
    fun removeListener(listener: (T) -> Unit)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}
```

### Property Delegation Extensions

Enable convenient property delegation syntax with state objects.

```kotlin
// For read-only state
operator fun <T> State<T>.getValue(thisRef: Any?, property: KProperty<*>): T

// For mutable state
operator fun <T> SummonMutableState<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T)
```

---

## MutableState

The primary implementation for mutable state management in Summon.

### Factory Function

```kotlin
fun <T> mutableStateOf(initialValue: T): MutableState<T>
```

Creates a new MutableState instance with the given initial value.

### Usage Examples

```kotlin
@Composable
fun CounterExample() {
    // Direct value access
    val state = mutableStateOf(0)
    Text("Count: ${state.value}")
    Button(
        text = "Increment",
        onClick = { state.value++ }
    )

    // Property delegation
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

### State Listeners

MutableState supports adding listeners for value changes:

```kotlin
val state = mutableStateOf("initial")

// Add listener
state.addListener { newValue ->
    println("State changed to: $newValue")
}

// Remove listener
val listener: (String) -> Unit = { println("Listener: $it") }
state.addListener(listener)
state.removeListener(listener)
```

---

## Remember Functions

The `remember` functions preserve state and values across recompositions.

### Basic Remember

```kotlin
@Composable
fun <T> remember(calculation: () -> T): T
```

Remembers a value across recompositions. The calculation is only executed once.

### Remember with Keys

```kotlin
@Composable
fun <T> remember(vararg keys: Any?, calculation: () -> T): T
```

Remembers a value and recalculates when any of the keys change.

### Remember Mutable State

```kotlin
@Composable
fun <T> rememberMutableStateOf(initial: T): SummonMutableState<T>
```

Creates and remembers a mutable state with the given initial value.

### Usage Examples

```kotlin
@Composable
fun RememberExample(items: List<String>, searchQuery: String) {
    // Simple remember
    val count = remember { mutableStateOf(0) }

    // With keys - recalculates when items or searchQuery change
    val filteredItems = remember(items, searchQuery) {
        items.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    // Remember mutable state
    var localState by rememberMutableStateOf("initial")

    Column {
        Text("Count: ${count.value}")
        Button(
            text = "Increment",
            onClick = { count.value++ }
        )

        Text("Filtered Items: ${filteredItems.size}")

        TextField(
            value = localState,
            onValueChange = { localState = it }
        )
    }
}
```

---

## Derived State

Create state that is computed from other state values.

### Simple Derived State

```kotlin
fun <T> simpleDerivedStateOf(calculation: () -> T): State<T>
```

Creates a simple derived state that recomputes when accessed.

### Composable Derived State

```kotlin
@Composable
fun <T> derivedStateOf(calculation: () -> T): SummonMutableState<T>

@Composable
fun <T> derivedStateOf(vararg dependencies: Any?, calculation: () -> T): SummonMutableState<T>
```

Creates derived state that automatically updates when dependencies change.

### Usage Examples

```kotlin
@Composable
fun DerivedStateExample() {
    val firstName = remember { mutableStateOf("John") }
    val lastName = remember { mutableStateOf("Doe") }

    // Simple derived state - recalculates on every access
    val simpleFullName = simpleDerivedStateOf {
        "${firstName.value} ${lastName.value}"
    }

    // Composable derived state - updates when dependencies change
    val fullName by derivedStateOf {
        "${firstName.value} ${lastName.value}"
    }

    // Derived state with explicit dependencies
    val greeting by derivedStateOf(firstName, lastName) {
        "Hello, ${firstName.value} ${lastName.value}!"
    }

    Column {
        Text("Simple: ${simpleFullName.value}")
        Text("Full Name: $fullName")
        Text(greeting)

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

## Flow Integration

Summon provides integration with Kotlin Flows for reactive state management.

### Collect Flow as State

```kotlin
@Composable
fun <T> Flow<T>.collectAsState(initial: T): State<T>

@Composable
fun <T> StateFlow<T>.collectAsState(): State<T>
```

Converts a Flow or StateFlow into a State that updates when the Flow emits new values.

### Produce State

```kotlin
@Composable
fun <T> produceState(
    initialValue: T,
    vararg keys: Any?,
    producer: suspend ProduceStateScope<T>.() -> Unit
): State<T>

interface ProduceStateScope<T> {
    var value: T
}
```

Creates state from a suspend function, perfect for async operations.

### Usage Examples

```kotlin
@Composable
fun FlowIntegrationExample(userId: String) {
    // Collect from StateFlow
    val user = userRepository.getUserFlow(userId).collectAsState()

    // Collect from Flow with initial value
    val notifications = notificationService
        .getNotificationsFlow()
        .collectAsState(initial = emptyList())

    // Produce state from async operation
    val weather = produceState<Weather?>(
        initialValue = null,
        key1 = userId
    ) {
        value = weatherService.getWeather(userId)

        // Update every 5 minutes
        while (true) {
            delay(5.minutes)
            try {
                value = weatherService.getWeather(userId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    Column {
        user.value?.let { u ->
            Text("User: ${u.name}")
        }

        Text("Notifications: ${notifications.value.size}")

        weather.value?.let { w ->
            Text("Temperature: ${w.temperature}°C")
        } ?: Text("Loading weather...")
    }
}
```

---

## Saveable State

Persist state values across recompositions and app restarts.

### Saveable State Registry

```kotlin
object SaveableStateRegistry {
    fun <T> get(key: String): T?
    fun set(key: String, value: Any?)
    fun clear()
}
```

### Remember Saveable

```kotlin
fun <T> rememberSaveable(key: String, initialValue: T): SummonMutableState<T>

fun <T> rememberWithIdentifier(identifier: String, initialValue: T): SummonMutableState<T>
```

Creates state that persists across recompositions.

### Utility Functions

```kotlin
fun clearSaveableStates()
fun hasSaveableState(key: String): Boolean
```

### Usage Examples

```kotlin
@Composable
fun SaveableStateExample() {
    // State persisted with unique key
    val settings = rememberSaveable("user_settings", UserSettings())

    // State persisted with identifier
    var theme by rememberWithIdentifier("app_theme", "light")

    // Check if state exists
    val hasPersistedData = hasSaveableState("user_data")

    Column {
        Text("Theme: $theme")

        Button(
            text = if (theme == "light") "Dark Mode" else "Light Mode",
            onClick = {
                theme = if (theme == "light") "dark" else "light"
            }
        )

        Text("Has persisted data: $hasPersistedData")

        Button(
            text = "Clear All Saved States",
            onClick = { clearSaveableStates() }
        )
    }
}
```

---

## Usage Examples

### Complete Counter with Persistence

```kotlin
@Composable
fun PersistentCounter() {
    var count by rememberSaveable("counter_value", 0)

    // Derived state for display
    val countDisplay by derivedStateOf {
        "Count: $count"
    }

    // Track if count is even/odd
    val isEven by derivedStateOf(count) {
        count % 2 == 0
    }

    Column {
        Text(countDisplay)
        Text("Is even: $isEven")

        Row {
            Button(
                text = "Increment",
                onClick = { count++ }
            )

            Button(
                text = "Decrement",
                onClick = { count-- }
            )

            Button(
                text = "Reset",
                onClick = { count = 0 }
            )
        }
    }
}
```

### Async Data Loading with State

```kotlin
@Composable
fun AsyncDataExample(query: String) {
    // Loading state
    var isLoading by rememberMutableStateOf(false)

    // Error state
    var error by rememberMutableStateOf<String?>(null)

    // Data state from async operation
    val searchResults = produceState<List<SearchResult>>(
        initialValue = emptyList(),
        key1 = query
    ) {
        if (query.isNotBlank()) {
            isLoading = true
            error = null

            try {
                value = searchService.search(query)
            } catch (e: Exception) {
                error = e.message
                value = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    Column {
        when {
            isLoading -> Text("Loading...")
            error != null -> Text("Error: $error")
            searchResults.value.isEmpty() && query.isNotBlank() -> Text("No results")
            else -> {
                searchResults.value.forEach { result ->
                    Text(result.title)
                }
            }
        }
    }
}
```

## Best Practices

1. **Use appropriate state type**: Choose between `State<T>`, `SummonMutableState<T>`, or `MutableState<T>` based on
   your needs
2. **Minimize state**: Keep state as minimal and normalized as possible
3. **Use derived state**: Compute derived values rather than storing them separately
4. **Handle errors**: Properly handle errors in async state operations
5. **Clean up resources**: Use appropriate cleanup in `produceState` coroutines
6. **Key dependencies**: Use explicit keys in `remember` and `derivedStateOf` for better control
7. **Persist wisely**: Only persist state that truly needs to survive app restarts

## See Also

- [Core API](core.md) - Core composition APIs
- [Effects API](effects.md) - Side effects and lifecycle management
- [Components API](components.md) - UI components that work with state