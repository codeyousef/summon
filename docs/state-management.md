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

### Advanced Derived State (v0.2.7+)

The new `simpleDerivedStateOf` function provides a more straightforward way to create derived state:

```kotlin
@Composable
fun ShoppingCart() {
    val items = remember { mutableStateListOf<Item>() }
    
    // Automatically recalculates when items change
    val totalPrice = simpleDerivedStateOf {
        items.sumOf { it.price }
    }
    
    val itemCount = simpleDerivedStateOf {
        items.size
    }
    
    Column {
        Text("Items: ${itemCount.value}")
        Text("Total: $${totalPrice.value}")
        
        Button(
            text = "Add Item",
            onClick = { 
                items.add(Item("Product", 9.99))
            }
        )
    }
}
```

### Producing State from Suspend Functions (v0.2.7+)

Use `produceState` to create state from asynchronous operations:

```kotlin
@Composable
fun UserProfile(userId: String) {
    val userProfile = produceState<UserProfile?>(
        initialValue = null,
        key1 = userId
    ) {
        value = fetchUserProfile(userId)
    }
    
    when (val profile = userProfile.value) {
        null -> Text("Loading...")
        else -> Text("Welcome, ${profile.name}")
    }
}
```

### Collecting Flow as State (v0.2.7+)

Convert Kotlin Flow to Summon State with `collectAsState`:

```kotlin
@Composable
fun LiveDataDisplay(dataFlow: Flow<String>) {
    val currentData = dataFlow.collectAsState()
    
    Text("Current value: ${currentData.value}")
}
```

### Observable Lists (v0.2.7+)

Use `mutableStateListOf` for lists that trigger recomposition on modification:

```kotlin
@Composable
fun TodoList() {
    val todos = remember { mutableStateListOf<String>() }
    
    Column {
        todos.forEach { todo ->
            Row {
                Text(todo)
                Button(
                    text = "Remove",
                    onClick = { todos.remove(todo) }
                )
            }
        }
        
        Button(
            text = "Add Todo",
            onClick = { todos.add("New Todo ${todos.size + 1}") }
        )
    }
}
```

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

## ViewModel Pattern

Summon supports the ViewModel pattern for managing component state:

```kotlin
import code.yousef.summon.state.ViewModel
import code.yousef.summon.state.*
import kotlinx.coroutines.flow.StateFlow

class CounterViewModel : ViewModel() {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun increment() {
        _count.value++
    }
    
    fun decrement() {
        _count.value--
    }
    
    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Simulate async operation
                delay(1000)
                _count.value = 42
            } finally {
                _isLoading.value = false
            }
        }
    }
}

@Composable
fun CounterScreen() {
    val viewModel = rememberViewModel { CounterViewModel() }
    val count by viewModel.count.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Column(modifier = Modifier.padding(16.px)) {
        if (isLoading) {
            Progress()
        } else {
            Text("Count: $count")
            Row(modifier = Modifier.gap(8.px)) {
                Button("Increment") { viewModel.increment() }
                Button("Decrement") { viewModel.decrement() }
                Button("Load Data") { viewModel.loadData() }
            }
        }
    }
}
```

## RememberSaveable

For state that should survive configuration changes and process death:

```kotlin
@Composable
fun FormScreen() {
    // State that survives configuration changes
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var agreed by rememberSaveable { mutableStateOf(false) }
    
    // Complex state with custom saver
    var formData by rememberSaveable(
        saver = FormDataSaver
    ) { mutableStateOf(FormData()) }
    
    Column {
        TextField(
            value = name,
            onValueChange = { name = it },
            placeholder = "Name"
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Email"
        )
        Checkbox(
            checked = agreed,
            onCheckedChange = { agreed = it },
            label = "I agree to terms"
        )
    }
}

// Custom saver for complex objects
object FormDataSaver : Saver<FormData, Bundle> {
    override fun save(value: FormData): Bundle = Bundle().apply {
        putString("name", value.name)
        putString("email", value.email)
        putBoolean("agreed", value.agreed)
    }
    
    override fun restore(value: Bundle): FormData = FormData(
        name = value.getString("name", ""),
        email = value.getString("email", ""),
        agreed = value.getBoolean("agreed", false)
    )
}
```

## Flow Integration

Seamless integration with Kotlin Flow for reactive programming:

```kotlin
@Composable
fun SearchScreen() {
    var query by remember { mutableStateOf("") }
    
    // Convert state to flow
    val queryFlow = snapshotFlow { query }
    
    // Debounced search
    val searchResults by queryFlow
        .debounce(300)
        .flatMapLatest { searchQuery ->
            if (searchQuery.isBlank()) {
                flowOf(emptyList())
            } else {
                searchRepository.search(searchQuery)
            }
        }
        .collectAsState(initial = emptyList())
    
    Column {
        TextField(
            value = query,
            onValueChange = { query = it },
            placeholder = "Search..."
        )
        
        LazyColumn {
            items(searchResults) { result ->
                SearchResultItem(result)
            }
        }
    }
}

// Combine multiple flows
@Composable
fun DashboardScreen() {
    val userFlow = userRepository.currentUser
    val notificationsFlow = notificationRepository.unreadCount
    
    val dashboardState by combine(
        userFlow,
        notificationsFlow
    ) { user, notificationCount ->
        DashboardState(user, notificationCount)
    }.collectAsState(initial = DashboardState.Loading)
    
    when (dashboardState) {
        is DashboardState.Loading -> Progress()
        is DashboardState.Success -> {
            Column {
                Text("Welcome, ${dashboardState.user.name}")
                Badge("${dashboardState.notificationCount}")
            }
        }
    }
}
```

## State Hoisting Patterns

Advanced patterns for managing state across component hierarchies:

```kotlin
// State holder class
class FormState(
    initialValues: FormValues = FormValues()
) {
    var values by mutableStateOf(initialValues)
        private set
    
    var errors by mutableStateOf<Map<String, String>>(emptyMap())
        private set
    
    val isValid: Boolean
        get() = errors.isEmpty() && values.isComplete()
    
    fun updateField(field: String, value: String) {
        values = values.copy(field to value)
        validateField(field, value)
    }
    
    private fun validateField(field: String, value: String) {
        val error = when (field) {
            "email" -> if (!value.contains("@")) "Invalid email" else null
            "password" -> if (value.length < 8) "Too short" else null
            else -> null
        }
        
        errors = if (error != null) {
            errors + (field to error)
        } else {
            errors - field
        }
    }
}

@Composable
fun rememberFormState(
    initialValues: FormValues = FormValues()
): FormState = remember {
    FormState(initialValues)
}

// Usage
@Composable
fun RegistrationForm() {
    val formState = rememberFormState()
    
    Column {
        FormField(
            value = formState.values.email,
            onValueChange = { formState.updateField("email", it) },
            error = formState.errors["email"],
            label = "Email"
        )
        
        FormField(
            value = formState.values.password,
            onValueChange = { formState.updateField("password", it) },
            error = formState.errors["password"],
            label = "Password",
            type = "password"
        )
        
        Button(
            text = "Register",
            enabled = formState.isValid,
            onClick = { submitForm(formState.values) }
        )
    }
}
```

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

// Media query state
val isMobile by mediaQueryState("(max-width: 768px)")

if (isMobile) {
    MobileLayout()
} else {
    DesktopLayout()
}

// Online status
val isOnline by onlineState()

if (!isOnline) {
    Banner("You are offline")
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

// File system state
val diskSpace by diskSpaceState("/")

ProgressBar(
    progress = diskSpace.used / diskSpace.total,
    label = "Disk Usage"
)
``` 