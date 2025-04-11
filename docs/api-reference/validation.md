# Validation API Reference

This document provides detailed information about the validation APIs in the Summon library.

## Table of Contents

- [Validator](#validator)
- [ValidationResult](#validationresult)
- [Built-in Validators](#built-in-validators)
- [Custom Validators](#custom-validators)
- [Form Validation](#form-validation)

---

## Validator

The `Validator` interface is the foundation for all validation logic in Summon.

### Interface Definition

```kotlin
package code.yousef.summon.validation

interface Validator<T> {
    fun validate(value: T): ValidationResult
}
```

### Description

Validators check if a value meets certain criteria and return a `ValidationResult` indicating whether the validation passed or failed.

### Example

```kotlin
class EmailValidator : Validator<String> {
    override fun validate(value: String): ValidationResult {
        val emailRegex = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        val isValid = value.matches(emailRegex)
        
        return if (isValid) {
            ValidationResult.Success
        } else {
            ValidationResult.Error("Please enter a valid email address")
        }
    }
}
```

---

## ValidationResult

The `ValidationResult` sealed class represents the outcome of a validation.

### Class Definition

```kotlin
package code.yousef.summon.validation

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
```

### Description

ValidationResult can be either `Success` indicating the value is valid, or `Error` which includes an error message explaining why validation failed.

### Example

```kotlin
fun validatePassword(password: String): ValidationResult {
    return when {
        password.length < 8 -> ValidationResult.Error("Password must be at least 8 characters")
        !password.any { it.isDigit() } -> ValidationResult.Error("Password must contain at least one digit")
        !password.any { it.isUpperCase() } -> ValidationResult.Error("Password must contain at least one uppercase letter")
        !password.any { it.isLowerCase() } -> ValidationResult.Error("Password must contain at least one lowercase letter")
        else -> ValidationResult.Success
    }
}
```

---

## Built-in Validators

Summon provides several built-in validators for common validation scenarios.

### RequiredValidator

```kotlin
package code.yousef.summon.validation

class RequiredValidator<T>(
    private val errorMessage: String = "This field is required"
) : Validator<T?> {
    override fun validate(value: T?): ValidationResult {
        return if (value == null || (value is String && value.isBlank())) {
            ValidationResult.Error(errorMessage)
        } else {
            ValidationResult.Success
        }
    }
}
```

### Other Built-in Validators

```kotlin
// Email validator
class EmailValidator(
    private val errorMessage: String = "Please enter a valid email address"
) : Validator<String>

// Length validator
class LengthValidator(
    private val minLength: Int = 0,
    private val maxLength: Int = Int.MAX_VALUE,
    private val errorMessage: String? = null
) : Validator<String>

// Number range validator
class NumberRangeValidator<T : Number>(
    private val min: T? = null,
    private val max: T? = null,
    private val errorMessage: String? = null
) : Validator<T>

// Pattern validator
class PatternValidator(
    private val pattern: Regex,
    private val errorMessage: String = "Input doesn't match the required pattern"
) : Validator<String>
```

### Example

```kotlin
val requiredValidator = RequiredValidator<String>("This field cannot be empty")
val emailValidator = EmailValidator()
val passwordValidator = LengthValidator(8, 20, "Password must be between 8 and 20 characters")

// Validate user input
val nameResult = requiredValidator.validate(nameInput)
val emailResult = emailValidator.validate(emailInput)
val passwordResult = passwordValidator.validate(passwordInput)

// Display validation results
if (nameResult is ValidationResult.Error) {
    displayError(nameErrorLabel, nameResult.message)
}
```

---

## Custom Validators

Creating custom validators by implementing the `Validator` interface.

### Composing Validators

```kotlin
// Combine multiple validators into one
class CompositeValidator<T>(
    private val validators: List<Validator<T>>
) : Validator<T> {
    override fun validate(value: T): ValidationResult {
        validators.forEach { validator ->
            val result = validator.validate(value)
            if (result is ValidationResult.Error) {
                return result
            }
        }
        return ValidationResult.Success
    }
}

// Example function to compose validators
fun <T> composeValidators(vararg validators: Validator<T>): Validator<T> {
    return CompositeValidator(validators.toList())
}
```

### Example of Custom Validator

```kotlin
// Create a username validator
class UsernameValidator : Validator<String> {
    override fun validate(value: String): ValidationResult {
        if (value.length < 3) {
            return ValidationResult.Error("Username must be at least 3 characters")
        }
        
        if (value.length > 20) {
            return ValidationResult.Error("Username must be at most 20 characters")
        }
        
        if (!value.matches("[a-zA-Z0-9_]+".toRegex())) {
            return ValidationResult.Error("Username can only contain letters, numbers, and underscores")
        }
        
        return ValidationResult.Success
    }
}

// Create a composite validator for a registration form
val usernameValidator = composeValidators(
    RequiredValidator("Username is required"),
    UsernameValidator()
)

val passwordValidator = composeValidators(
    RequiredValidator("Password is required"),
    LengthValidator(8, 100, "Password must be at least 8 characters"),
    PatternValidator(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$".toRegex(),
        "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
)
```

---

## Form Validation

Helper functions and components for validating forms.

### Form

```kotlin
package code.yousef.summon.components.input

@Composable
fun Form(
    onSubmit: (Map<String, Any>) -> Unit,
    modifier: Modifier = Modifier,
    validate: Boolean = true,
    content: @Composable FormScope.() -> Unit
)

class FormScope {
    fun register(
        name: String,
        initialValue: Any? = null,
        validators: List<Validator<Any>> = emptyList()
    ): FormField<Any>
}

data class FormField<T>(
    val name: String,
    val value: T?,
    val onChange: (T) -> Unit,
    val error: String?
)
```

### Example

```kotlin
@Composable
fun LoginForm(onLogin: (username: String, password: String) -> Unit) {
    Form(
        onSubmit = { formData ->
            onLogin(
                formData["username"] as String,
                formData["password"] as String
            )
        },
        modifier = Modifier.padding(16.px),
        validate = true
    ) {
        val usernameField = register(
            name = "username",
            validators = listOf(RequiredValidator("Please enter your username"))
        )
        
        val passwordField = register(
            name = "password",
            validators = listOf(RequiredValidator("Please enter your password"))
        )
        
        Column(modifier = Modifier.gap(16.px)) {
            TextField(
                value = usernameField.value as? String ?: "",
                onValueChange = { usernameField.onChange(it) },
                label = "Username",
                error = usernameField.error
            )
            
            TextField(
                value = passwordField.value as? String ?: "",
                onValueChange = { passwordField.onChange(it) },
                type = TextFieldType.Password,
                label = "Password",
                error = passwordField.error
            )
            
            Button(
                text = "Login",
                type = ButtonType.Submit,
                modifier = Modifier.marginTop(16.px)
            )
        }
    }
}
``` 