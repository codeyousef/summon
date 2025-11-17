# Async Form Validation API Reference

## Overview

Enhanced form validation system with asynchronous validation support for server-side checks. Provides type-safe
validators, state management, and seamless integration with existing synchronous validators.

**Package**: `code.yousef.summon.components.forms`  
**Since**: 1.0.0 (async support added in 0.4.8.3)

---

## AsyncValidator Interface

Interface for validators that perform asynchronous validation (e.g., server-side checks).

```kotlin
interface AsyncValidator {
    suspend fun validate(value: String): String?
}
```

**Method**:

- `validate(value: String): String?` - Validates a value asynchronously
    - Returns error message if invalid
    - Returns `null` if valid
    - Uses `suspend` for coroutine support

**Example**:

```kotlin
class UsernameAvailabilityValidator(
    private val apiClient: ApiClient
) : AsyncValidator {
    override suspend fun validate(value: String): String? {
        if (value.isBlank()) return null
        
        return withContext(Dispatchers.IO) {
            val isAvailable = apiClient.checkUsernameAvailability(value)
            if (!isAvailable) {
                "Username '$value' is already taken"
            } else null
        }
    }
}
```

---

## FormValidationState Class

Manages validation state for entire forms with support for both synchronous and asynchronous validators.

```kotlin
class FormValidationState {
    fun registerField(fieldName: String, validators: List<Validator>)
    fun registerAsyncValidator(fieldName: String, validator: AsyncValidator)
    fun validateField(fieldName: String, value: String): String?
    suspend fun validateFieldAsync(fieldName: String, value: String): String?
    fun validateAll(values: Map<String, String>): Map<String, String?>
    fun getError(fieldName: String): String?
    fun hasError(fieldName: String): Boolean
    fun isValid(): Boolean
    fun clearFieldError(fieldName: String)
    fun clearAllErrors()
}
```

### Methods

#### registerField

Registers synchronous validators for a field.

```kotlin
fun registerField(fieldName: String, validators: List<Validator>)
```

**Parameters**:

- `fieldName: String` - The field identifier
- `validators: List<Validator>` - List of synchronous validators

**Example**:

```kotlin
validationState.registerField("email", listOf(
    Validator.required("Email is required"),
    Validator.email("Must be a valid email")
))
```

---

#### registerAsyncValidator

Registers an asynchronous validator for a field.

```kotlin
fun registerAsyncValidator(fieldName: String, validator: AsyncValidator)
```

**Parameters**:

- `fieldName: String` - The field identifier
- `validator: AsyncValidator` - The async validator instance

**Example**:

```kotlin
validationState.registerAsyncValidator(
    "username",
    UsernameAvailabilityValidator(apiClient)
)
```

---

#### validateField

Validates a field synchronously (runs sync validators only).

```kotlin
fun validateField(fieldName: String, value: String): String?
```

**Parameters**:

- `fieldName: String` - The field to validate
- `value: String` - The field value

**Returns**: First error message, or `null` if valid

**Example**:

```kotlin
val error = validationState.validateField("email", emailValue)
if (error != null) {
    showError(error)
}
```

---

#### validateFieldAsync

Validates a field asynchronously (runs sync validators first, then async).

```kotlin
suspend fun validateFieldAsync(fieldName: String, value: String): String?
```

**Parameters**:

- `fieldName: String` - The field to validate
- `value: String` - The field value

**Returns**: Error message if invalid, `null` if valid

**Behavior**:

1. Runs all synchronous validators first
2. If any sync validator fails, returns error immediately
3. If sync validators pass, runs async validator (if registered)
4. Returns async validator result

**Example**:

```kotlin
launch {
    val error = validationState.validateFieldAsync("username", usernameValue)
    if (error != null) {
        usernameError = error
    }
}
```

---

#### validateAll

Validates all registered fields synchronously.

```kotlin
fun validateAll(values: Map<String, String>): Map<String, String?>
```

**Parameters**:

- `values: Map<String, String>` - Map of field names to values

**Returns**: Map of field names to error messages (`null` if valid)

**Example**:

```kotlin
val errors = validationState.validateAll(mapOf(
    "email" to emailValue,
    "password" to passwordValue
))

if (validationState.isValid()) {
    submitForm()
} else {
    displayErrors(errors)
}
```

---

#### getError

Gets the current error message for a field.

```kotlin
fun getError(fieldName: String): String?
```

**Parameters**:

- `fieldName: String` - The field identifier

**Returns**: Error message, or `null` if no error

**Example**:

```kotlin
val emailError = validationState.getError("email")
if (emailError != null) {
    Text(emailError, modifier = Modifier().color("red"))
}
```

---

#### hasError

Checks if a field has an error.

```kotlin
fun hasError(fieldName: String): Boolean
```

**Parameters**:

- `fieldName: String` - The field identifier

**Returns**: `true` if field has error, `false` otherwise

**Example**:

```kotlin
TextField(
    modifier = Modifier()
        .borderColor(
            if (validationState.hasError("email")) "red" else "#ccc"
        )
)
```

---

#### isValid

Checks if all fields are valid.

```kotlin
fun isValid(): Boolean
```

**Returns**: `true` if all fields are valid, `false` if any field has error

**Example**:

```kotlin
Button(
    onClick = { submitForm() },
    enabled = validationState.isValid()
)
```

---

#### clearFieldError

Clears the error for a specific field.

```kotlin
fun clearFieldError(fieldName: String)
```

**Parameters**:

- `fieldName: String` - The field identifier

**Example**:

```kotlin
// Clear error when user starts typing
TextField(
    onValueChange = { 
        value = it
        validationState.clearFieldError("email")
    }
)
```

---

#### clearAllErrors

Clears all field errors.

```kotlin
fun clearAllErrors()
```

**Example**:

```kotlin
// Clear all errors when closing form
fun closeForm() {
    validationState.clearAllErrors()
    isFormOpen = false
}
```

---

## ValidationResult Sealed Class

Represents the result of validation (sync or async).

```kotlin
sealed class ValidationResult {
    data class Sync(val error: String?) : ValidationResult()
    data class Async(val pending: Boolean, val error: String?) : ValidationResult()
}
```

**Variants**:

- `Sync(error)` - Synchronous validation result
- `Async(pending, error)` - Asynchronous validation with pending state

**Example**:

```kotlin
val result = when (validationResult) {
    is ValidationResult.Sync -> {
        if (result.error != null) "Error: ${result.error}" else "Valid"
    }
    is ValidationResult.Async -> {
        if (result.pending) "Validating..."
        else if (result.error != null) "Error: ${result.error}"
        else "Valid"
    }
}
```

---

## Data Classes

### ServerValidationRequest

Request object for server-side validation.

```kotlin
data class ServerValidationRequest(
    val fieldName: String,
    val value: String,
    val formData: Map<String, String> = emptyMap()
)
```

**Fields**:

- `fieldName` - The field being validated
- `value` - The field value
- `formData` - Optional additional form context

**Example**:

```kotlin
val request = ServerValidationRequest(
    fieldName = "username",
    value = usernameValue,
    formData = mapOf("email" to emailValue)
)
```

---

### ServerValidationResponse

Response object from server-side validation.

```kotlin
data class ServerValidationResponse(
    val valid: Boolean,
    val error: String? = null
)
```

**Fields**:

- `valid` - Whether the value is valid
- `error` - Error message if invalid

**Example**:

```kotlin
val response = ServerValidationResponse(
    valid = false,
    error = "Username is already taken"
)
```

---

## Usage Examples

### Basic Async Validation

```kotlin
@Composable
fun RegistrationForm() {
    val usernameState = remember { mutableStateOf("") }
    val validationState = remember { FormValidationState() }
    
    // Setup validators
    LaunchedEffect(Unit) {
        validationState.registerField("username", listOf(
            Validator.required("Username is required"),
            Validator.minLength(3, "Must be at least 3 characters")
        ))
        
        validationState.registerAsyncValidator(
            "username",
            UsernameAvailabilityValidator(apiClient)
        )
    }
    
    Column {
        TextField(
            value = usernameState.value,
            onValueChange = { 
                usernameState.value = it
                // Validate on change
                scope.launch {
                    validationState.validateFieldAsync("username", it)
                }
            }
        )
        
        validationState.getError("username")?.let { error ->
            Text(error, modifier = Modifier().color("red"))
        }
    }
}
```

---

### Form-Level Validation

```kotlin
@Composable
fun CompleteForm() {
    val validationState = remember { FormValidationState() }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        // Register all fields
        validationState.registerField("email", listOf(
            Validator.required(),
            Validator.email()
        ))
        
        validationState.registerField("password", listOf(
            Validator.required(),
            Validator.minLength(8)
        ))
        
        // Async validators
        validationState.registerAsyncValidator(
            "email",
            EmailAvailabilityValidator(apiClient)
        )
    }
    
    fun handleSubmit() {
        scope.launch {
            // Validate all fields
            val errors = validationState.validateAll(mapOf(
                "email" to emailValue,
                "password" to passwordValue
            ))
            
            if (validationState.isValid()) {
                // All sync validation passed
                // Now check async
                val emailError = validationState.validateFieldAsync(
                    "email", 
                    emailValue
                )
                
                if (emailError == null) {
                    submitForm()
                }
            }
        }
    }
}
```

---

### Debounced Async Validation

```kotlin
@Composable
fun UsernameField() {
    val username = remember { mutableStateOf("") }
    val validationState = remember { FormValidationState() }
    val scope = rememberCoroutineScope()
    var validationJob: Job? = null
    
    TextField(
        value = username.value,
        onValueChange = { newValue ->
            username.value = newValue
            
            // Cancel previous validation
            validationJob?.cancel()
            
            // Debounce validation
            validationJob = scope.launch {
                delay(500) // Wait 500ms
                validationState.validateFieldAsync("username", newValue)
            }
        }
    )
    
    validationState.getError("username")?.let { error ->
        Text(error, modifier = Modifier().color("red"))
    }
}
```

---

### Custom Async Validator

```kotlin
class PasswordStrengthValidator(
    private val apiClient: ApiClient
) : AsyncValidator {
    override suspend fun validate(value: String): String? {
        if (value.isEmpty()) return null
        
        return withContext(Dispatchers.IO) {
            try {
                val strength = apiClient.checkPasswordStrength(value)
                when {
                    strength < 0.3 -> "Password is too weak"
                    strength < 0.6 -> "Password strength is fair"
                    else -> null // Strong enough
                }
            } catch (e: Exception) {
                // Handle error - don't block submission on API failure
                null
            }
        }
    }
}
```

---

## Best Practices

### 1. Run Sync Validators First

```kotlin
// Always validate synchronously before async
val syncError = validationState.validateField("email", value)
if (syncError == null) {
    // Only then run async validation
    validationState.validateFieldAsync("email", value)
}
```

### 2. Debounce Async Validation

```kotlin
// Don't validate on every keystroke
var job: Job? = null
onValueChange = { value ->
    job?.cancel()
    job = scope.launch {
        delay(500)
        validationState.validateFieldAsync("field", value)
    }
}
```

### 3. Handle Network Errors Gracefully

```kotlin
class MyAsyncValidator : AsyncValidator {
    override suspend fun validate(value: String): String? {
        return try {
            // API call
            performValidation(value)
        } catch (e: Exception) {
            // Don't block user on network errors
            null // or show a warning
        }
    }
}
```

### 4. Show Loading State

```kotlin
val isValidating = remember { mutableStateOf(false) }

TextField(
    onValueChange = { value ->
        isValidating.value = true
        scope.launch {
            validationState.validateFieldAsync("field", value)
            isValidating.value = false
        }
    },
    trailingIcon = {
        if (isValidating.value) {
            Spinner()
        }
    }
)
```

---

## Performance Considerations

### Optimization Tips

1. Use debouncing to reduce API calls
2. Cache validation results when possible
3. Cancel in-flight requests on new input
4. Validate only when necessary (blur, submit)
5. Use background dispatchers for API calls

### Memory Management

- FormValidationState doesn't hold references to validators after use
- Coroutines are properly cancelled
- No memory leaks with proper cleanup

---

## Related APIs

- [Validator](./form-validation.md#validator) - Synchronous validators
- [FormValidation Guide](../../guides/form-validation.md) - Complete guide
- [Quick Reference](../../../QUICK_REFERENCE.md#form-validation)

---

## See Also

- [Form Validation Guide](../../guides/form-validation.md)
- [Server Integration Guide](../../guides/server-integration.md)
- [Coroutines Guide](../../guides/coroutines.md)

