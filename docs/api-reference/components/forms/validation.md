# Form Validation

Comprehensive form validation system for the Summon framework with built-in validators, custom validation logic, and error handling.

## Overview

The form validation system provides type-safe validators that can be composed together to validate form fields. It includes common validation patterns and supports custom validation logic.

## Basic Usage

```kotlin
import codes.yousef.summon.components.forms.Validator
import codes.yousef.summon.components.forms.validateValue

// Validate a single value
val error = validateValue(
    value = "test@example.com",
    validators = listOf(
        Validator.required("Email is required"),
        Validator.email("Must be a valid email")
    )
)
// error is null if valid, or contains error message if invalid
```

## Built-in Validators

### Required

Validates that a field is not empty:

```kotlin
Validator.required("This field is required")
```

### Email

Validates email format:

```kotlin
Validator.email("Must be a valid email address")
```

### Min Length

Validates minimum length:

```kotlin
Validator.minLength(8, "Must be at least 8 characters")
```

### Max Length

Validates maximum length:

```kotlin
Validator.maxLength(100, "Must be at most 100 characters")
```

### Pattern

Validates against a regular expression:

```kotlin
Validator.pattern(
    "^[A-Za-z0-9]+$",
    "Only alphanumeric characters allowed"
)
```

### Min Value

Validates minimum numeric value:

```kotlin
Validator.min(0.0, "Must be at least 0")
```

### Max Value

Validates maximum numeric value:

```kotlin
Validator.max(100.0, "Must be at most 100")
```

### URL

Validates URL format:

```kotlin
Validator.url("Must be a valid URL")
```

### Matches

Validates that value matches another field:

```kotlin
Validator.matches(passwordValue, "Passwords do not match")
```

### Custom

Creates a custom validator with user-defined logic:

```kotlin
Validator.custom { value ->
    if (value.contains(" ")) {
        "Value cannot contain spaces"
    } else {
        null // null means valid
    }
}
```

## API Reference

### Validator

Base sealed class for all validators:

```kotlin
sealed class Validator {
    abstract fun validate(value: String): String?
}
```

Each validator implements the `validate` method which returns:
- `null` if the value is valid
- An error message string if the value is invalid

### Factory Methods

```kotlin
// Create validators using companion object methods
Validator.required(message: String = "This field is required")
Validator.email(message: String = "Must be a valid email address")
Validator.minLength(length: Int, message: String? = null)
Validator.maxLength(length: Int, message: String? = null)
Validator.pattern(pattern: String, message: String = "Invalid format")
Validator.min(min: Double, message: String? = null)
Validator.max(max: Double, message: String? = null)
Validator.url(message: String = "Must be a valid URL")
Validator.custom(validationFn: (String) -> String?)
Validator.matches(otherValue: String, message: String = "Values do not match")
```

### Validation Functions

```kotlin
// Validate and return first error
fun validateValue(value: String, validators: List<Validator>): String?

// Validate and return all errors
fun validateValueAll(value: String, validators: List<Validator>): List<String>
```

## Examples

### Email Field Validation

```kotlin
val emailValidators = listOf(
    Validator.required("Email is required"),
    Validator.email("Must be a valid email")
)

val error = validateValue(userEmail, emailValidators)
if (error != null) {
    // Show error message
}
```

### Password Validation

```kotlin
val passwordValidators = listOf(
    Validator.required("Password is required"),
    Validator.minLength(8, "Must be at least 8 characters"),
    Validator.pattern(
        "^(?=.*[A-Z])(?=.*[0-9]).*$",
        "Must contain uppercase letter and number"
    )
)
```

### Password Confirmation

```kotlin
val password = "MyP@ssw0rd"
val confirmValidators = listOf(
    Validator.required("Please confirm password"),
    Validator.matches(password, "Passwords do not match")
)
```

### Username Validation

```kotlin
val usernameValidators = listOf(
    Validator.required("Username is required"),
    Validator.minLength(3, "Must be at least 3 characters"),
    Validator.maxLength(20, "Must be at most 20 characters"),
    Validator.pattern(
        "^[a-zA-Z0-9_]+$",
        "Only letters, numbers, and underscores allowed"
    ),
    Validator.custom { value ->
        if (value.startsWith("_")) {
            "Cannot start with underscore"
        } else null
    }
)
```

### Age Validation

```kotlin
val ageValidators = listOf(
    Validator.required("Age is required"),
    Validator.min(18.0, "Must be at least 18 years old"),
    Validator.max(120.0, "Please enter a valid age")
)
```

### URL Validation

```kotlin
val urlValidators = listOf(
    Validator.required("URL is required"),
    Validator.url("Please enter a valid URL")
)
```

### Custom Business Logic

```kotlin
val validators = listOf(
    Validator.required("Email is required"),
    Validator.email("Must be a valid email"),
    Validator.custom { value ->
        if (!value.endsWith("@company.com")) {
            "Must use company email address"
        } else null
    }
)
```

### Multiple Errors

Get all validation errors instead of just the first:

```kotlin
val errors = validateValueAll(value, validators)
errors.forEach { error ->
    println("Error: $error")
}
```

## Integration with Form Components

While the framework doesn't yet have a FormTextField component that directly uses validators, you can integrate validation into your own components:

```kotlin
@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    validators: List<Validator>,
    label: String
) {
    val error = remember(value) {
        validateValue(value, validators)
    }
    
    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier()
                .apply {
                    if (error != null) {
                        style("border-color", "red")
                    }
                }
        )
        if (error != null) {
            Text(
                error,
                modifier = Modifier()
                    .style("color", "red")
                    .style("font-size", "12px")
            )
        }
    }
}
```

## Form-level Validation

Validate an entire form:

```kotlin
data class FormData(
    val email: String,
    val password: String,
    val confirmPassword: String
)

fun validateForm(data: FormData): Map<String, String?> {
    return mapOf(
        "email" to validateValue(data.email, listOf(
            Validator.required(),
            Validator.email()
        )),
        "password" to validateValue(data.password, listOf(
            Validator.required(),
            Validator.minLength(8)
        )),
        "confirmPassword" to validateValue(data.confirmPassword, listOf(
            Validator.required(),
            Validator.matches(data.password)
        ))
    )
}

val errors = validateForm(formData)
val hasErrors = errors.values.any { it != null }
```

## Real-time vs Submit Validation

```kotlin
@Composable
fun MyForm() {
    val email = remember { mutableStateOf("") }
    val showErrors = remember { mutableStateOf(false) }
    
    val validators = listOf(
        Validator.required("Email is required"),
        Validator.email("Invalid email")
    )
    
    TextField(
        value = email.value,
        onValueChange = { 
            email.value = it
            // Optionally validate on change
            if (showErrors.value) {
                val error = validateValue(it, validators)
                // Update error display
            }
        }
    )
    
    Button(
        onClick = {
            showErrors.value = true
            val error = validateValue(email.value, validators)
            if (error == null) {
                // Submit form
            }
        },
        label = "Submit"
    )
}
```

## Error Messages

All validators support custom error messages:

```kotlin
// Default message
Validator.required() // "This field is required"

// Custom message
Validator.required("Please enter your name")

// For validators with parameters, message can be customized
Validator.minLength(8, "Password must be at least 8 characters long")
```

## Validation Patterns

### Required Email

```kotlin
listOf(
    Validator.required("Email is required"),
    Validator.email()
)
```

### Optional Email

Only validate format if not empty:

```kotlin
listOf(
    Validator.email() // Email validator only checks if not empty
)
```

### Password with Strength Requirements

```kotlin
listOf(
    Validator.required(),
    Validator.minLength(8),
    Validator.pattern(".*[A-Z].*", "Must contain uppercase"),
    Validator.pattern(".*[a-z].*", "Must contain lowercase"),
    Validator.pattern(".*[0-9].*", "Must contain number"),
    Validator.pattern(".*[@#$%^&+=].*", "Must contain special character")
)
```

### Positive Integer

```kotlin
listOf(
    Validator.pattern("^[0-9]+$", "Must be a positive integer"),
    Validator.min(0.0)
)
```

## Best Practices

1. **Order Validators**: Put `required` first, then format validators
2. **Clear Messages**: Provide specific, actionable error messages
3. **Client-side Only**: These are client-side validations; always validate server-side too
4. **Performance**: Validators run synchronously; keep custom logic fast
5. **Reusability**: Create validator lists as constants for reuse

## See Also

- [TextField](../input/textfield.md) - Text input component
- [Form](../input/form.md) - Form component
- [TextArea](../input/textarea.md) - Multi-line text input
