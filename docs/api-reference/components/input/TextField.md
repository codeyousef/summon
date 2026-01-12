# TextField Component

The TextField component provides a comprehensive solution for text input in Summon applications. It supports various
input types, validation, accessibility features, and both controlled and uncontrolled usage patterns.

## Overview

Text input is fundamental to user interfaces. The Summon TextField component offers:

- **Multiple Input Types**: Text, password, email, number, URL, search, date, time
- **Validation Support**: Built-in validation system with error handling
- **Accessibility First**: ARIA attributes, proper labeling, screen reader support
- **State Management**: Both controlled and uncontrolled variants
- **Error Handling**: Visual and programmatic error states
- **Cross-Platform**: Consistent behavior across browser and JVM environments

## Basic Usage

### Simple Text Field

```kotlin
import codes.yousef.summon.components.input.*

@Composable
fun SimpleTextFieldExample() {
    var name by remember { mutableStateOf("") }

    TextField(
        value = name,
        onValueChange = { name = it },
        placeholder = "Enter your name",
        modifier = Modifier().width("300px")
    )
}
```

### Text Field with Label

```kotlin
@Composable
fun LabeledTextFieldExample() {
    var email by remember { mutableStateOf("") }

    Column(verticalSpacing = "8px") {
        Label("Email Address")
        TextField(
            value = email,
            onValueChange = { email = it },
            type = TextFieldType.Email,
            placeholder = "you@example.com",
            modifier = Modifier().width("100%")
        )
    }
}
```

### Password Field

```kotlin
@Composable
fun PasswordFieldExample() {
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Column(verticalSpacing = "8px") {
        Label("Password")
        Row(
            horizontalSpacing = "8px",
            verticalAlignment = "center"
        ) {
            TextField(
                value = password,
                onValueChange = { password = it },
                type = if (showPassword) TextFieldType.Text else TextFieldType.Password,
                placeholder = "Enter your password",
                modifier = Modifier().flex("1")
            )

            IconButton(
                icon = if (showPassword) "visibility_off" else "visibility",
                onClick = { showPassword = !showPassword },
                ariaLabel = if (showPassword) "Hide password" else "Show password"
            )
        }
    }
}
```

## API Reference

### TextField Component

```kotlin
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    label: String? = null,
    placeholder: String? = null,
    type: TextFieldType = TextFieldType.Text,
    isError: Boolean = false,
    isEnabled: Boolean = true,
    isReadOnly: Boolean = false,
    validators: List<Validator> = emptyList()
)
```

#### Parameters

| Parameter       | Type               | Default              | Description                            |
|-----------------|--------------------|----------------------|----------------------------------------|
| `value`         | `String`           | Required             | Current text value (controlled)        |
| `onValueChange` | `(String) -> Unit` | Required             | Callback when value changes            |
| `modifier`      | `Modifier`         | `Modifier()`         | Styling and layout modifier            |
| `label`         | `String?`          | `null`               | Field label (handled externally)       |
| `placeholder`   | `String?`          | `null`               | Placeholder text when empty            |
| `type`          | `TextFieldType`    | `TextFieldType.Text` | Input type for validation and behavior |
| `isError`       | `Boolean`          | `false`              | Whether field is in error state        |
| `isEnabled`     | `Boolean`          | `true`               | Whether field accepts input            |
| `isReadOnly`    | `Boolean`          | `false`              | Whether field is read-only             |
| `validators`    | `List<Validator>`  | `emptyList()`        | Validation rules to apply              |

### StatefulTextField Component

```kotlin
@Composable
fun StatefulTextField(
    initialValue: String = "",
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier(),
    label: String? = null,
    placeholder: String? = null,
    type: TextFieldType = TextFieldType.Text,
    isError: Boolean = false,
    isEnabled: Boolean = true,
    isReadOnly: Boolean = false,
    validators: List<Validator> = emptyList()
)
```

### TextFieldType

Enum defining different input types with corresponding HTML input types.

```kotlin
enum class TextFieldType {
    Text,     // Standard text input
    Password, // Password input (hidden characters)
    Email,    // Email input with validation
    Number,   // Numeric input
    Tel,      // Telephone number input
    Url,      // URL input with validation
    Search,   // Search input
    Date,     // Date picker input
    Time      // Time picker input
}
```

## Advanced Examples

### Form with Validation

```kotlin
@Composable
fun ValidatedFormExample() {
    var formData by remember { mutableStateOf(UserFormData()) }
    var errors by remember { mutableStateOf(emptyMap<String, String>()) }

    // Email validators
    val emailValidators = listOf(
        Validator.required("Email is required"),
        Validator.email("Please enter a valid email address")
    )

    // Password validators
    val passwordValidators = listOf(
        Validator.required("Password is required"),
        Validator.minLength(8, "Password must be at least 8 characters"),
        Validator.pattern(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$".toRegex(),
            "Password must contain uppercase, lowercase, and numbers"
        )
    )

    Column(
        verticalSpacing = "16px",
        modifier = Modifier().padding("24px")
    ) {
        H2("Create Account")

        // Email field
        FormField(
            label = "Email Address",
            error = errors["email"],
            required = true
        ) {
            TextField(
                value = formData.email,
                onValueChange = {
                    formData = formData.copy(email = it)
                    errors = errors - "email" // Clear error on change
                },
                type = TextFieldType.Email,
                placeholder = "you@example.com",
                validators = emailValidators,
                isError = errors.containsKey("email"),
                modifier = Modifier().width("100%")
            )
        }

        // Password field
        FormField(
            label = "Password",
            error = errors["password"],
            required = true
        ) {
            TextField(
                value = formData.password,
                onValueChange = {
                    formData = formData.copy(password = it)
                    errors = errors - "password"
                },
                type = TextFieldType.Password,
                placeholder = "Enter a secure password",
                validators = passwordValidators,
                isError = errors.containsKey("password"),
                modifier = Modifier().width("100%")
            )
        }

        // Confirm password field
        FormField(
            label = "Confirm Password",
            error = errors["confirmPassword"],
            required = true
        ) {
            TextField(
                value = formData.confirmPassword,
                onValueChange = {
                    formData = formData.copy(confirmPassword = it)
                    errors = errors - "confirmPassword"
                },
                type = TextFieldType.Password,
                placeholder = "Confirm your password",
                validators = listOf(
                    Validator.required("Please confirm your password"),
                    Validator.custom { value ->
                        if (value == formData.password) {
                            ValidationResult.valid()
                        } else {
                            ValidationResult.invalid("Passwords do not match")
                        }
                    }
                ),
                isError = errors.containsKey("confirmPassword"),
                modifier = Modifier().width("100%")
            )
        }

        // Submit button
        Button(
            text = "Create Account",
            onClick = {
                val validationErrors = validateForm(formData, emailValidators, passwordValidators)
                if (validationErrors.isEmpty()) {
                    // Submit form
                    submitForm(formData)
                } else {
                    errors = validationErrors
                }
            },
            disabled = !isFormValid(formData),
            modifier = Modifier().width("100%")
        )
    }
}
```

### Search Field with Debouncing

```kotlin
@Composable
fun SearchFieldExample() {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(emptyList<SearchResult>()) }
    var isSearching by remember { mutableStateOf(false) }

    // Debounce search
    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            isSearching = true
            delay(300) // Debounce delay
            try {
                results = searchService.search(query)
            } finally {
                isSearching = false
            }
        } else {
            results = emptyList()
        }
    }

    Column(verticalSpacing = "8px") {
        Row(
            horizontalSpacing = "8px",
            verticalAlignment = "center",
            modifier = Modifier()
                .backgroundColor("#F5F5F5")
                .borderRadius("8px")
                .padding("12px")
        ) {
            Icon(
                name = "search",
                size = "20px",
                color = "#666666"
            )

            TextField(
                value = query,
                onValueChange = { query = it },
                type = TextFieldType.Search,
                placeholder = "Search products, articles, or help topics...",
                modifier = Modifier()
                    .flex("1")
                    .border("none")
                    .backgroundColor("transparent")
                    .outline("none")
            )

            if (isSearching) {
                CircularProgress(
                    size = "16px",
                    strokeWidth = "2px"
                )
            } else if (query.isNotBlank()) {
                IconButton(
                    icon = "clear",
                    onClick = { query = "" },
                    size = "16px",
                    ariaLabel = "Clear search"
                )
            }
        }

        // Search results
        if (results.isNotEmpty()) {
            Card(
                modifier = Modifier()
                    .width("100%")
                    .maxHeight("300px")
                    .overflow("auto")
                    .border("1px solid #E0E0E0")
                    .borderRadius("8px")
            ) {
                LazyColumn {
                    items(results) { result ->
                        SearchResultItem(
                            result = result,
                            query = query,
                            onClick = {
                                // Handle result selection
                                query = result.title
                            }
                        )
                    }
                }
            }
        }
    }
}
```

### Multi-line Text Area

```kotlin
@Composable
fun TextAreaExample() {
    var description by remember { mutableStateOf("") }
    val maxLength = 500

    Column(verticalSpacing = "8px") {
        Label("Description")

        TextArea(
            value = description,
            onValueChange = {
                if (it.length <= maxLength) {
                    description = it
                }
            },
            placeholder = "Enter a detailed description...",
            modifier = Modifier()
                .width("100%")
                .height("120px")
                .resize("vertical")
        )

        Row(
            modifier = Modifier().justifyContent("space-between"),
            verticalAlignment = "center"
        ) {
            Text(
                text = "${description.length}/$maxLength characters",
                fontSize = "12px",
                color = if (description.length > maxLength * 0.9) "#FF6B6B" else "#666666"
            )

            if (description.length > maxLength * 0.9) {
                Text(
                    text = "Approaching character limit",
                    fontSize = "12px",
                    color = "#FF6B6B"
                )
            }
        }
    }
}
```

### Numeric Input with Formatting

```kotlin
@Composable
fun NumericFieldExample() {
    var amount by remember { mutableStateOf("") }
    var formattedAmount by remember { mutableStateOf("") }

    val numberFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale.US)
    }

    Column(verticalSpacing = "8px") {
        Label("Amount")

        TextField(
            value = amount,
            onValueChange = { newValue ->
                // Only allow numeric input
                val numericValue = newValue.filter { it.isDigit() || it == '.' }
                amount = numericValue

                // Format for display
                val doubleValue = numericValue.toDoubleOrNull()
                formattedAmount = if (doubleValue != null) {
                    numberFormatter.format(doubleValue)
                } else {
                    ""
                }
            },
            type = TextFieldType.Number,
            placeholder = "0.00",
            modifier = Modifier()
                .width("200px")
                .textAlign("right")
        )

        if (formattedAmount.isNotBlank()) {
            Text(
                text = "Formatted: $formattedAmount",
                fontSize = "14px",
                color = "#666666"
            )
        }
    }
}
```

### Auto-Complete Field

```kotlin
@Composable
fun AutoCompleteExample() {
    var input by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf(emptyList<String>()) }
    var showSuggestions by remember { mutableStateOf(false) }

    val allOptions = listOf(
        "JavaScript", "Java", "Kotlin", "Python", "TypeScript",
        "React", "Vue", "Angular", "Svelte", "Flutter"
    )

    LaunchedEffect(input) {
        suggestions = if (input.length >= 2) {
            allOptions.filter {
                it.contains(input, ignoreCase = true)
            }.take(5)
        } else {
            emptyList()
        }
        showSuggestions = suggestions.isNotEmpty()
    }

    Box(modifier = Modifier().position("relative")) {
        TextField(
            value = input,
            onValueChange = { input = it },
            placeholder = "Type to search technologies...",
            modifier = Modifier().width("300px"),
            onFocus = { showSuggestions = suggestions.isNotEmpty() },
            onBlur = {
                // Delay hiding to allow click on suggestions
                delay(150)
                showSuggestions = false
            }
        )

        if (showSuggestions) {
            Card(
                modifier = Modifier()
                    .position("absolute")
                    .top("100%")
                    .left("0")
                    .right("0")
                    .zIndex("1000")
                    .maxHeight("200px")
                    .overflow("auto")
                    .border("1px solid #E0E0E0")
                    .borderRadius("4px")
                    .backgroundColor("white")
                    .boxShadow("0 2px 8px rgba(0,0,0,0.1)")
            ) {
                suggestions.forEach { suggestion ->
                    Box(
                        modifier = Modifier()
                            .width("100%")
                            .padding("12px")
                            .cursor("pointer")
                            .hover { backgroundColor("#F5F5F5") }
                            .onClick {
                                input = suggestion
                                showSuggestions = false
                            }
                    ) {
                        Text(suggestion)
                    }
                }
            }
        }
    }
}
```

## Validation System

### Built-in Validators

```kotlin
// Common validators
val validators = listOf(
    Validator.required("This field is required"),
    Validator.minLength(5, "Must be at least 5 characters"),
    Validator.maxLength(100, "Must be less than 100 characters"),
    Validator.email("Please enter a valid email"),
    Validator.pattern(
        "^[A-Za-z]+$".toRegex(),
        "Only letters are allowed"
    ),
    Validator.custom { value ->
        if (value.contains("forbidden")) {
            ValidationResult.invalid("Contains forbidden word")
        } else {
            ValidationResult.valid()
        }
    }
)
```

### Real-time Validation

```kotlin
@Composable
fun RealTimeValidationExample() {
    var username by remember { mutableStateOf("") }
    var isValidating by remember { mutableStateOf(false) }
    var isAvailable by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(username) {
        if (username.length >= 3) {
            isValidating = true
            delay(500) // Debounce
            try {
                isAvailable = checkUsernameAvailability(username)
            } finally {
                isValidating = false
            }
        } else {
            isAvailable = null
        }
    }

    Column(verticalSpacing = "8px") {
        Label("Username")

        Row(
            horizontalSpacing = "8px",
            verticalAlignment = "center"
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                placeholder = "Choose a username",
                modifier = Modifier().flex("1"),
                validators = listOf(
                    Validator.required("Username is required"),
                    Validator.minLength(3, "Username must be at least 3 characters"),
                    Validator.pattern(
                        "^[a-zA-Z0-9_]+$".toRegex(),
                        "Only letters, numbers, and underscores allowed"
                    )
                )
            )

            when {
                isValidating -> CircularProgress(size = "16px")
                isAvailable == true -> Icon("check_circle", color = "#4CAF50")
                isAvailable == false -> Icon("error", color = "#F44336")
            }
        }

        when {
            isValidating -> Text("Checking availability...", fontSize = "12px", color = "#666")
            isAvailable == true -> Text("Username is available", fontSize = "12px", color = "#4CAF50")
            isAvailable == false -> Text("Username is already taken", fontSize = "12px", color = "#F44336")
        }
    }
}
```

## Accessibility Guidelines

### Screen Reader Support

```kotlin
// TextField automatically includes appropriate ARIA attributes
TextField(
    value = value,
    onValueChange = onValueChange,
    // Automatically includes:
    // role="textbox"
    // aria-invalid="true/false" based on error state
    // aria-required="true" for required fields
    // aria-describedby for associated error messages
)
```

### Keyboard Navigation

```kotlin
// TextField supports standard keyboard interactions:
// - Tab/Shift+Tab for focus navigation
// - Enter for form submission (in forms)
// - Escape to clear focus
// - Standard text editing shortcuts (Ctrl+A, Ctrl+C, etc.)
```

### Error Handling

```kotlin
@Composable
fun AccessibleErrorExample() {
    var email by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(verticalSpacing = "4px") {
        Label(
            text = "Email Address",
            required = true,
            htmlFor = "email-input" // Links label to input
        )

        TextField(
            value = email,
            onValueChange = {
                email = it
                error = validateEmail(it)
            },
            type = TextFieldType.Email,
            isError = error != null,
            modifier = Modifier()
                .id("email-input")
                .ariaDescribedBy(if (error != null) "email-error" else null)
        )

        if (error != null) {
            ErrorText(
                text = error,
                modifier = Modifier()
                    .id("email-error")
                    .role("alert") // Announces to screen readers
            )
        }
    }
}
```

## Platform-Specific Behavior

### Browser Platform

- Native HTML input elements with full browser support
- Automatic form validation and submission
- Mobile device optimization (virtual keyboards)
- Copy/paste support
- Spell checking and autocomplete

### JVM Platform

- Console-based input simulation
- Text-based prompts and validation
- Integration with CLI frameworks
- File-based input/output support

## Performance Considerations

### Debounced Input

```kotlin
@Composable
fun DebouncedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    debounceMs: Long = 300,
    modifier: Modifier = Modifier()
) {
    var localValue by remember { mutableStateOf(value) }

    LaunchedEffect(localValue) {
        delay(debounceMs)
        if (localValue != value) {
            onValueChange(localValue)
        }
    }

    TextField(
        value = localValue,
        onValueChange = { localValue = it },
        modifier = modifier
    )
}
```

### Memory Optimization

```kotlin
// Use keys for dynamic field lists
@Composable
fun DynamicFieldList(fields: List<FieldConfig>) {
    fields.forEach { field ->
        key(field.id) {
            TextField(
                value = field.value,
                onValueChange = { updateField(field.id, it) },
                placeholder = field.placeholder
            )
        }
    }
}
```

## Testing

### Unit Testing

```kotlin
@Test
fun testTextFieldInput() {
    val mockRenderer = MockPlatformRenderer()
    var capturedValue = ""

    CompositionLocal.provideComposer(MockComposer()) {
        LocalPlatformRenderer.provides(mockRenderer) {
            TextField(
                value = "initial",
                onValueChange = { capturedValue = it },
                type = TextFieldType.Email
            )

            // Simulate input change
            mockRenderer.simulateTextInput("test@example.com")
            assertEquals("test@example.com", capturedValue)
        }
    }
}
```

### Validation Testing

```kotlin
@Test
fun testTextFieldValidation() {
    val emailValidator = Validator.email("Invalid email")
    var validationResult: ValidationResult? = null

    val result = emailValidator.validate("invalid-email")
    assertFalse(result.isValid)
    assertEquals("Invalid email", result.errorMessage)

    val validResult = emailValidator.validate("test@example.com")
    assertTrue(validResult.isValid)
}
```

## Best Practices

1. **Use Controlled Components**: Prefer controlled TextField over StatefulTextField for better state management
2. **Validate Early**: Provide real-time feedback for better user experience
3. **Clear Placeholders**: Use descriptive placeholder text that guides users
4. **Appropriate Types**: Use specific input types for better mobile experience
5. **Error Handling**: Always provide clear, actionable error messages
6. **Accessibility**: Include proper labels and ARIA attributes
7. **Performance**: Debounce expensive operations like API calls
8. **Mobile Optimization**: Consider virtual keyboard types and input methods
9. **JS Production Builds**: Use `BasicTextField` if experiencing callback issues in minified builds

## JavaScript Minification Considerations

When building for production with Kotlin/JS, the minification process can mangle function names and property accessors.
The TextField components have been designed to work correctly in minified builds, but if you encounter issues:

### Symptoms of Minification Issues

- `TypeError: ... is not a function` errors in production but not development
- State changes not triggering UI updates
- Callbacks not being invoked when expected

### Solutions

1. **Use BasicTextField**: The `BasicTextField` component avoids internal state that could be affected by minification:

```kotlin
// Instead of
TextField(
    value = text,
    onValueChange = { text = it },
    validators = listOf(...
)
)

// Use
BasicTextField(
    value = text,
    onValueChange = { text = it }
)
// Handle validation externally
```

2. **Use getPlatformRenderer()**: When accessing the renderer directly, use `getPlatformRenderer()` which is annotated
   with `@JsName` for consistent naming:

```kotlin
import codes.yousef.summon.runtime.getPlatformRenderer

val renderer = getPlatformRenderer()
renderer.renderTextField(value, onValueChange, modifier, type)
```

3. **Avoid Inline Lambdas with State**: If creating custom components, wrap state modifications in named local
   functions:

```kotlin
// Avoid
renderer.renderTextField(value, { newValue ->
    myState.value = newValue  // .value access may be mangled
})

// Prefer
fun handleChange(newValue: String) {
    myState.value = newValue
}
renderer.renderTextField(value, ::handleChange)
```

The TextField component provides a robust foundation for text input across your Summon application, ensuring
accessibility, validation, and excellent user experience.