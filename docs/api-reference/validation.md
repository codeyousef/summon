# Validation API Reference

This document provides detailed information about the validation system in the Summon library. The validation system
provides comprehensive form and input validation with built-in validators and support for custom validation logic.

## Table of Contents

- [Overview](#overview)
- [Core Types](#core-types)
- [Built-in Validators](#built-in-validators)
- [Validation Messages](#validation-messages)
- [Custom Validators](#custom-validators)
- [Usage Examples](#usage-examples)

---

## Overview

The Summon validation system provides:

- **Type-safe validation** with clear validation results
- **Built-in validators** for common validation scenarios
- **Custom validators** for specific business logic
- **Centralized error messages** for consistency and localization
- **Form integration** with automatic validation and error display

### Key Features

- Required field validation
- Email format validation
- Length constraints (min/max)
- Pattern matching with regex
- Custom validation functions
- Boolean value validation
- Internationalization-ready error messages

---

## Core Types

### ValidationResult

The result of a validation operation.

```kotlin
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
```

**Properties:**

- `isValid`: Whether the validation passed
- `errorMessage`: Error message if validation failed, null if valid

### Validator Interface

Base interface for all validators.

```kotlin
interface Validator {
    fun validate(value: String): ValidationResult
    val errorMessage: String
}

// Extension for boolean validation
fun Validator.validateBoolean(value: Boolean): ValidationResult
```

**Methods:**

- `validate(value: String)`: Validates a string value
- `validateBoolean(value: Boolean)`: Validates a boolean value (extension)

**Properties:**

- `errorMessage`: Default error message for this validator

---

## Built-in Validators

### RequiredValidator

Ensures a field is not empty.

```kotlin
class RequiredValidator(
    override val errorMessage: String = ValidationMessages.REQUIRED_FIELD
) : Validator
```

**Usage:**
```kotlin
val required = RequiredValidator()
val result = required.validate("") // ValidationResult(false, "This field is required")
val result2 = required.validate("text") // ValidationResult(true, null)
```

### EmailValidator

Validates email format using regex pattern.

```kotlin
class EmailValidator(
    override val errorMessage: String = ValidationMessages.INVALID_EMAIL
) : Validator
```

**Pattern:** `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$`

**Usage:**

```kotlin
val emailValidator = EmailValidator()
val result = emailValidator.validate("invalid-email") // ValidationResult(false, "Please enter a valid email address")
val result2 = emailValidator.validate("user@example.com") // ValidationResult(true, null)
val result3 = emailValidator.validate("") // ValidationResult(true, null) - empty is valid for optional fields
```

### MinLengthValidator

Ensures minimum character length.

```kotlin
class MinLengthValidator(
    private val minLength: Int,
    override val errorMessage: String = ValidationMessages.minLength(minLength)
) : Validator
```

**Usage:**

```kotlin
val minLength = MinLengthValidator(8)
val result = minLength.validate("short") // ValidationResult(false, "Must be at least 8 characters")
val result2 = minLength.validate("long enough") // ValidationResult(true, null)
```

### MaxLengthValidator

Ensures maximum character length.

```kotlin
class MaxLengthValidator(
    private val maxLength: Int,
    override val errorMessage: String = ValidationMessages.maxLength(maxLength)
) : Validator
```

**Usage:**

```kotlin
val maxLength = MaxLengthValidator(50)
val result = maxLength.validate("This is a very long text that exceeds the maximum length limit") // ValidationResult(false, "Must be no more than 50 characters")
val result2 = maxLength.validate("Short text") // ValidationResult(true, null)
```

### PatternValidator

Validates against a regex pattern.

```kotlin
class PatternValidator(
    private val pattern: Regex,
    override val errorMessage: String = ValidationMessages.INVALID_FORMAT
) : Validator
```

**Usage:**
```kotlin
// Phone number pattern
val phonePattern = Regex("^\\+?[1-9]\\d{1,14}$")
val phoneValidator = PatternValidator(phonePattern, "Please enter a valid phone number")

val result = phoneValidator.validate("123") // ValidationResult(false, "Please enter a valid phone number")
val result2 = phoneValidator.validate("+1234567890") // ValidationResult(true, null)

// Custom patterns
val alphanumericPattern = Regex("^[a-zA-Z0-9]+$")
val alphanumericValidator = PatternValidator(alphanumericPattern, "Only letters and numbers allowed")
```

### CustomValidator

Creates validators with custom validation logic.

```kotlin
class CustomValidator(
    private val validateFn: (String) -> Boolean,
    override val errorMessage: String
) : Validator
```

**Usage:**

```kotlin
// Password strength validator
val passwordValidator = CustomValidator(
    validateFn = { password ->
        password.length >= 8 &&
        password.any { it.isUpperCase() } &&
        password.any { it.isLowerCase() } &&
        password.any { it.isDigit() }
    },
    errorMessage = "Password must be at least 8 characters with uppercase, lowercase, and number"
)

// Age validator
val ageValidator = CustomValidator(
    validateFn = { age ->
        age.toIntOrNull()?.let { it in 18..120 } ?: false
    },
    errorMessage = "Age must be between 18 and 120"
)
```

---

## Validation Messages

Centralized validation error messages for consistency and localization.

### ValidationMessages Object

```kotlin
object ValidationMessages {
    // Pre-defined messages
    const val REQUIRED_FIELD = "This field is required"
    const val INVALID_EMAIL = "Please enter a valid email address"
    const val INVALID_FORMAT = "Input format is incorrect"
    const val MUST_BE_NUMBER = "Must be a number"
    const val MUST_BE_POSITIVE = "Must be a positive number"
    const val VALIDATION_FAILED = "Validation failed"

    // Helper functions
    fun minLength(length: Int): String = "Must be at least $length characters"
    fun maxLength(length: Int): String = "Must be no more than $length characters"
}
```

### Custom Error Messages

```kotlin
// Using custom messages
val requiredValidator = RequiredValidator("Please fill in this field")
val emailValidator = EmailValidator("Enter a valid email like user@domain.com")
val minLengthValidator = MinLengthValidator(6, "Password must be at least 6 characters")

// Localized messages (integrate with I18n system)
val localizedRequired = RequiredValidator(i18n.getString("validation.required"))
val localizedEmail = EmailValidator(i18n.getString("validation.email"))
```

---

## Custom Validators

### Advanced Custom Validators

```kotlin
// Confirm password validator
class ConfirmPasswordValidator(
    private val originalPassword: String
) : Validator {
    override val errorMessage = "Passwords do not match"

    override fun validate(value: String): ValidationResult {
        val isValid = value == originalPassword
        return ValidationResult(isValid, if (!isValid) errorMessage else null)
    }
}

// URL validator
class UrlValidator : Validator {
    override val errorMessage = "Please enter a valid URL"
    private val urlPattern = Regex("^https?://[\\w\\-.]+(\\.[a-zA-Z]{2,})(/.*)?$")

    override fun validate(value: String): ValidationResult {
        val isValid = value.isEmpty() || urlPattern.matches(value)
        return ValidationResult(isValid, if (!isValid) errorMessage else null)
    }
}
```

### Composite Validators

```kotlin
// Validator that combines multiple validators
class CompositeValidator(
    private val validators: List<Validator>
) : Validator {
    override val errorMessage = "Validation failed"

    override fun validate(value: String): ValidationResult {
        for (validator in validators) {
            val result = validator.validate(value)
            if (!result.isValid) {
                return result
            }
        }
        return ValidationResult(true, null)
    }
}

// Usage
val passwordValidator = CompositeValidator(listOf(
    RequiredValidator(),
    MinLengthValidator(8),
    PatternValidator(Regex(".*[A-Z].*"), "Must contain uppercase letter"),
    PatternValidator(Regex(".*[0-9].*"), "Must contain number")
))
```

---

## Usage Examples

### Simple Form Validation

```kotlin
@Composable
fun LoginForm() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val emailValidators = listOf(
        RequiredValidator(),
        EmailValidator()
    )

    val passwordValidators = listOf(
        RequiredValidator(),
        MinLengthValidator(8)
    )

    Column(
        modifier = Modifier()
            .maxWidth("400px")
            .padding("20px")
    ) {
        // Email field
        FormField(
            label = "Email",
            error = emailError
        ) {
            TextField(
                value = email,
                onValueChange = {
                    email = it
                    // Validate on change
                    emailError = null
                    for (validator in emailValidators) {
                        val result = validator.validate(it)
                        if (!result.isValid) {
                            emailError = result.errorMessage
                            break
                        }
                    }
                },
                modifier = Modifier().fillMaxWidth()
            )
        }

        Spacer(Modifier().height("16px"))

        // Password field
        FormField(
            label = "Password",
            error = passwordError
        ) {
            TextField(
                value = password,
                onValueChange = {
                    password = it
                    // Validate on change
                    passwordError = null
                    for (validator in passwordValidators) {
                        val result = validator.validate(it)
                        if (!result.isValid) {
                            passwordError = result.errorMessage
                            break
                        }
                    }
                },
                type = "password",
                modifier = Modifier().fillMaxWidth()
            )
        }

        Spacer(Modifier().height("20px"))

        Button(
            onClick = { /* handle login */ },
            label = "Login",
            disabled = emailError != null || passwordError != null || email.isEmpty() || password.isEmpty(),
            modifier = Modifier().fillMaxWidth()
        )
    }
}
```

### Registration Form with Complex Validation

```kotlin
@Composable
fun RegistrationForm() {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Define validators
    val usernameValidators = listOf(
        RequiredValidator(),
        MinLengthValidator(3),
        MaxLengthValidator(20),
        PatternValidator(Regex("^[a-zA-Z0-9_]+$"), "Only letters, numbers, and underscores allowed")
    )

    val passwordValidators = listOf(
        RequiredValidator(),
        MinLengthValidator(8),
        CustomValidator(
            validateFn = { pwd ->
                pwd.any { it.isUpperCase() } &&
                pwd.any { it.isLowerCase() } &&
                pwd.any { it.isDigit() }
            },
            errorMessage = "Password must contain uppercase, lowercase, and number"
        )
    )

    Column(
        modifier = Modifier()
            .maxWidth("400px")
            .padding("20px")
    ) {
        Text(
            text = "Create Account",
            modifier = Modifier()
                .themeTextStyle("h3")
                .marginBottom("20px")
        )

        ValidatedField(
            value = username,
            onValueChange = { username = it },
            validators = usernameValidators,
            label = "Username",
            placeholder = "Enter username"
        )

        ValidatedField(
            value = email,
            onValueChange = { email = it },
            validators = listOf(RequiredValidator(), EmailValidator()),
            label = "Email",
            placeholder = "Enter email address"
        )

        ValidatedField(
            value = password,
            onValueChange = { password = it },
            validators = passwordValidators,
            label = "Password",
            type = "password",
            placeholder = "Enter password"
        )

        ValidatedField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            validators = listOf(
                RequiredValidator(),
                CustomValidator(
                    validateFn = { it == password },
                    errorMessage = "Passwords do not match"
                )
            ),
            label = "Confirm Password",
            type = "password",
            placeholder = "Confirm password"
        )

        Button(
            onClick = { /* handle registration */ },
            label = "Create Account",
            modifier = Modifier()
                .fillMaxWidth()
                .marginTop("20px")
        )
    }
}

@Composable
fun ValidatedField(
    value: String,
    onValueChange: (String) -> Unit,
    validators: List<Validator>,
    label: String,
    placeholder: String = "",
    type: String = "text"
) {
    var error by remember { mutableStateOf<String?>(null) }

    FormField(
        label = label,
        error = error,
        modifier = Modifier().marginBottom("16px")
    ) {
        TextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                // Validate on change
                error = null
                for (validator in validators) {
                    val result = validator.validate(newValue)
                    if (!result.isValid) {
                        error = result.errorMessage
                        break
                    }
                }
            },
            placeholder = placeholder,
            type = type,
            modifier = Modifier().fillMaxWidth()
        )
    }
}
```

## Best Practices

1. **Validate early and often**: Provide immediate feedback to users
2. **Use appropriate validators**: Choose the right validator for each field type
3. **Provide clear error messages**: Make errors actionable and specific
4. **Consider accessibility**: Ensure error messages are accessible to screen readers
5. **Debounce async validation**: Avoid excessive API calls during typing
6. **Validate on blur**: Final validation when user leaves the field
7. **Group related validators**: Use composite validators for complex requirements
8. **Localize error messages**: Support multiple languages with I18n integration

## Migration Notes

When adding validation to existing forms:

- Start with basic required field validation
- Gradually add more specific validators
- Update form submission to check overall validity
- Consider user experience during validation state changes
- Test validation with edge cases and invalid input

## See Also

- [Components API](components.md) - Form components that integrate with validation
- [I18n API](i18n.md) - Localizing validation error messages
- [State API](state.md) - Managing form and validation state
- [Accessibility API](accessibility.md) - Making validation accessible