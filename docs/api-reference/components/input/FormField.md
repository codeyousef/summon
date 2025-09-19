# FormField

FormField components provide structured layout and presentation for form inputs, including labels, helper text, error
messages, and validation state indicators.

## Overview

The FormField component serves as a wrapper for input controls, providing consistent structure and styling for labels,
help text, error messages, and validation states. It ensures proper accessibility and visual hierarchy for form
elements.

### Key Features

- **Structured Layout**: Consistent spacing and arrangement for form elements
- **Label Management**: Automatic label association and styling
- **Error Handling**: Built-in error message display and validation states
- **Helper Text**: Support for additional context and instructions
- **Required Indicators**: Visual markers for required fields
- **Accessibility**: Full ARIA support with proper associations
- **Responsive Design**: Adapts to different screen sizes and form layouts

## API Reference

### FormField

```kotlin
@Composable
fun FormField(
    modifier: Modifier = Modifier(),
    label: @Composable (() -> Unit)? = null,
    helperText: @Composable (() -> Unit)? = null,
    errorText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    isRequired: Boolean = false,
    fieldContent: @Composable () -> Unit
)
```

**Parameters:**

- `modifier`: Modifier applied to the FormField container
- `label`: Optional composable lambda for the field's label
- `helperText`: Optional composable lambda for displaying helper text
- `errorText`: Optional composable lambda for displaying error text
- `isError`: Indicates whether the field is in an error state (default: `false`)
- `isRequired`: Indicates whether the field is required (default: `false`)
- `fieldContent`: The composable lambda defining the actual input control

## Usage Examples

### Basic Form Field

```kotlin
@Composable
fun BasicFormFieldExample() {
    var name by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    FormField(
        label = {
            Text("Full Name", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
        },
        helperText = {
            Text("Enter your first and last name", style = Typography.CAPTION)
        },
        isRequired = true,
        isError = isError,
        errorText = if (isError) {
            { Text("Name is required", style = Typography.CAPTION) }
        } else null
    ) {
        TextField(
            value = name,
            onValueChange = {
                name = it
                isError = it.isBlank()
            },
            placeholder = "Enter your name",
            modifier = Modifier().width(300.px)
        )
    }
}
```

### Form Field with Validation

```kotlin
@Composable
fun ValidatedFormFieldExample() {
    var email by remember { mutableStateOf("") }
    var validationState by remember { mutableStateOf<ValidationResult?>(null) }

    val emailValidator = remember {
        Validator { value ->
            when {
                value.isBlank() -> ValidationResult.invalid("Email is required")
                !value.contains("@") -> ValidationResult.invalid("Please enter a valid email address")
                !value.contains(".") -> ValidationResult.invalid("Please enter a valid email address")
                else -> ValidationResult.valid()
            }
        }
    }

    FormField(
        label = {
            Text("Email Address", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
        },
        helperText = if (validationState?.isValid != false) {
            { Text("We'll use this to contact you", style = Typography.CAPTION) }
        } else null,
        errorText = if (validationState?.isValid == false) {
            { Text(validationState?.errorMessage ?: "", style = Typography.CAPTION) }
        } else null,
        isError = validationState?.isValid == false,
        isRequired = true,
        modifier = Modifier().width(350.px)
    ) {
        TextField(
            value = email,
            onValueChange = { newValue ->
                email = newValue
                validationState = emailValidator.validate(newValue)
            },
            type = "email",
            placeholder = "user@example.com",
            modifier = Modifier().width(Width.FULL)
        )
    }
}
```

### Multi-Input Form Field

```kotlin
@Composable
fun MultiInputFormFieldExample() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    FormField(
        label = {
            Text("Full Name", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
        },
        helperText = {
            Text("Enter your first and last name separately", style = Typography.CAPTION)
        },
        isRequired = true,
        isError = hasError,
        errorText = if (hasError) {
            { Text("Both first and last name are required", style = Typography.CAPTION) }
        } else null,
        modifier = Modifier().width(500.px)
    ) {
        Row(modifier = Modifier().gap(Spacing.MD)) {
            TextField(
                value = firstName,
                onValueChange = {
                    firstName = it
                    hasError = firstName.isBlank() || lastName.isBlank()
                },
                placeholder = "First name",
                modifier = Modifier().weight(1f)
            )

            TextField(
                value = lastName,
                onValueChange = {
                    lastName = it
                    hasError = firstName.isBlank() || lastName.isBlank()
                },
                placeholder = "Last name",
                modifier = Modifier().weight(1f)
            )
        }
    }
}
```

### Form Field with Select

```kotlin
@Composable
fun SelectFormFieldExample() {
    var selectedCountry by remember { mutableStateOf<String?>(null) }
    var selectedState by remember { mutableStateOf<String?>(null) }

    val countries = listOf("United States", "Canada", "United Kingdom", "Australia")
    val states = mapOf(
        "United States" to listOf("California", "New York", "Texas", "Florida"),
        "Canada" to listOf("Ontario", "Quebec", "British Columbia", "Alberta"),
        "United Kingdom" to listOf("England", "Scotland", "Wales", "Northern Ireland"),
        "Australia" to listOf("New South Wales", "Victoria", "Queensland", "Western Australia")
    )

    Column(modifier = Modifier().gap(Spacing.LG)) {
        FormField(
            label = {
                Text("Country", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
            },
            helperText = {
                Text("Select your country of residence", style = Typography.CAPTION)
            },
            isRequired = true,
            modifier = Modifier().width(300.px)
        ) {
            Select(
                selectedValue = remember { mutableStateOf(selectedCountry) }.apply { value = selectedCountry },
                options = countries.map { SelectOption(it, it) },
                onSelectedChange = {
                    selectedCountry = it
                    selectedState = null // Reset state when country changes
                },
                placeholder = "Choose a country",
                modifier = Modifier().width(Width.FULL)
            )
        }

        if (selectedCountry != null) {
            FormField(
                label = {
                    Text("State/Province", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                },
                isRequired = true,
                modifier = Modifier().width(300.px)
            ) {
                Select(
                    selectedValue = remember { mutableStateOf(selectedState) }.apply { value = selectedState },
                    options = (states[selectedCountry] ?: emptyList()).map { SelectOption(it, it) },
                    onSelectedChange = { selectedState = it },
                    placeholder = "Choose a state/province",
                    modifier = Modifier().width(Width.FULL)
                )
            }
        }
    }
}
```

### Form Field with File Upload

```kotlin
@Composable
fun FileUploadFormFieldExample() {
    var selectedFiles by remember { mutableStateOf<List<FileInfo>>(emptyList()) }
    var hasError by remember { mutableStateOf(false) }

    FormField(
        label = {
            Text("Profile Picture", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
        },
        helperText = if (!hasError) {
            { Text("Upload a profile picture (JPG, PNG, max 5MB)", style = Typography.CAPTION) }
        } else null,
        errorText = if (hasError) {
            { Text("Please select a valid image file", style = Typography.CAPTION) }
        } else null,
        isError = hasError,
        isRequired = true,
        modifier = Modifier().width(400.px)
    ) {
        Column(modifier = Modifier().gap(Spacing.SM)) {
            FileUpload(
                onFilesSelected = { files ->
                    val validFiles = files.filter { file ->
                        file.type.startsWith("image/") && file.size <= 5 * 1024 * 1024
                    }
                    selectedFiles = validFiles
                    hasError = files.isNotEmpty() && validFiles.isEmpty()
                },
                accept = "image/*",
                buttonLabel = "Choose Image"
            )

            if (selectedFiles.isNotEmpty()) {
                selectedFiles.forEach { file ->
                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .padding(Spacing.SM)
                            .backgroundColor(Colors.Success.LIGHT)
                            .borderRadius(BorderRadius.SM)
                            .justifyContent(JustifyContent.SpaceBetween)
                            .alignItems(AlignItems.Center)
                    ) {
                        Text(file.name, style = Typography.BODY2)
                        Text(formatFileSize(file.size), style = Typography.CAPTION)
                    }
                }
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}
```

### Form Field with Custom Layout

```kotlin
@Composable
fun CustomLayoutFormFieldExample() {
    var acceptTerms by remember { mutableStateOf(false) }
    var acceptNewsletter by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    FormField(
        label = {
            Text("Agreement", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
        },
        helperText = if (!hasError) {
            { Text("Please review and accept our terms", style = Typography.CAPTION) }
        } else null,
        errorText = if (hasError) {
            { Text("You must accept the terms to continue", style = Typography.CAPTION) }
        } else null,
        isError = hasError,
        modifier = Modifier().width(450.px)
    ) {
        Column(modifier = Modifier().gap(Spacing.MD)) {
            Card(
                modifier = Modifier()
                    .width(Width.FULL)
                    .backgroundColor(Colors.Gray.LIGHT)
                    .padding(Spacing.MD)
                    .borderRadius(BorderRadius.MD)
            ) {
                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Text("Terms of Service", style = Typography.H6)
                    Text(
                        "By using our service, you agree to our terms and conditions. " +
                        "Please read the full terms at example.com/terms",
                        style = Typography.BODY2
                    )
                }
            }

            Column(modifier = Modifier().gap(Spacing.SM)) {
                Checkbox(
                    checked = acceptTerms,
                    onCheckedChange = {
                        acceptTerms = it
                        hasError = !it
                    },
                    label = "I accept the Terms of Service *"
                )

                Checkbox(
                    checked = acceptNewsletter,
                    onCheckedChange = { acceptNewsletter = it },
                    label = "I want to receive newsletter updates"
                )
            }
        }
    }
}
```

### Form Field Groups

```kotlin
@Composable
fun FormFieldGroupExample() {
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }

    FormField(
        label = {
            Text("Shipping Address", style = Typography.H6.copy(color = Colors.Primary.MAIN))
        },
        helperText = {
            Text("Enter the address where you'd like your order delivered", style = Typography.CAPTION)
        },
        modifier = Modifier().width(Width.FULL)
    ) {
        Column(modifier = Modifier().gap(Spacing.MD)) {
            FormField(
                label = {
                    Text("Street Address", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                },
                isRequired = true
            ) {
                TextField(
                    value = street,
                    onValueChange = { street = it },
                    placeholder = "123 Main Street",
                    modifier = Modifier().width(Width.FULL)
                )
            }

            Row(modifier = Modifier().gap(Spacing.MD)) {
                FormField(
                    label = {
                        Text("City", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    },
                    isRequired = true,
                    modifier = Modifier().weight(1f)
                ) {
                    TextField(
                        value = city,
                        onValueChange = { city = it },
                        placeholder = "City",
                        modifier = Modifier().width(Width.FULL)
                    )
                }

                FormField(
                    label = {
                        Text("ZIP Code", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    },
                    isRequired = true,
                    modifier = Modifier().width(120.px)
                ) {
                    TextField(
                        value = zipCode,
                        onValueChange = { zipCode = it },
                        placeholder = "12345",
                        modifier = Modifier().width(Width.FULL)
                    )
                }
            }

            FormField(
                label = {
                    Text("Country", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                },
                isRequired = true
            ) {
                Select(
                    selectedValue = remember { mutableStateOf(country) }.apply { value = country },
                    options = listOf("United States", "Canada", "United Kingdom").map { SelectOption(it, it) },
                    onSelectedChange = { country = it ?: "" },
                    placeholder = "Select country",
                    modifier = Modifier().width(250.px)
                )
            }
        }
    }
}
```

### Conditional Form Fields

```kotlin
@Composable
fun ConditionalFormFieldExample() {
    var accountType by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var taxId by remember { mutableStateOf("") }
    var personalId by remember { mutableStateOf("") }

    Column(modifier = Modifier().gap(Spacing.LG)) {
        FormField(
            label = {
                Text("Account Type", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
            },
            helperText = {
                Text("Choose the type of account you want to create", style = Typography.CAPTION)
            },
            isRequired = true
        ) {
            Column(modifier = Modifier().gap(Spacing.SM)) {
                RadioButton(
                    selected = accountType == "personal",
                    onClick = { accountType = "personal" },
                    label = "Personal Account"
                )
                RadioButton(
                    selected = accountType == "business",
                    onClick = { accountType = "business" },
                    label = "Business Account"
                )
            }
        }

        // Conditional fields based on account type
        when (accountType) {
            "personal" -> {
                FormField(
                    label = {
                        Text("Personal ID", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    },
                    helperText = {
                        Text("Social Security Number or other government ID", style = Typography.CAPTION)
                    },
                    isRequired = true
                ) {
                    TextField(
                        value = personalId,
                        onValueChange = { personalId = it },
                        placeholder = "XXX-XX-XXXX",
                        type = "password",
                        modifier = Modifier().width(200.px)
                    )
                }
            }
            "business" -> {
                Column(modifier = Modifier().gap(Spacing.MD)) {
                    FormField(
                        label = {
                            Text("Business Name", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                        },
                        helperText = {
                            Text("Legal name of your business", style = Typography.CAPTION)
                        },
                        isRequired = true
                    ) {
                        TextField(
                            value = businessName,
                            onValueChange = { businessName = it },
                            placeholder = "Acme Corporation",
                            modifier = Modifier().width(300.px)
                        )
                    }

                    FormField(
                        label = {
                            Text("Tax ID (EIN)", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                        },
                        helperText = {
                            Text("Employer Identification Number", style = Typography.CAPTION)
                        },
                        isRequired = true
                    ) {
                        TextField(
                            value = taxId,
                            onValueChange = { taxId = it },
                            placeholder = "XX-XXXXXXX",
                            modifier = Modifier().width(200.px)
                        )
                    }
                }
            }
        }
    }
}
```

### Form Field with Custom Styling

```kotlin
@Composable
fun StyledFormFieldExample() {
    var value by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(modifier = Modifier().gap(Spacing.LG)) {
        // Standard styling
        FormField(
            label = {
                Text("Standard Field", style = Typography.BODY1)
            },
            helperText = {
                Text("This is a standard form field", style = Typography.CAPTION)
            }
        ) {
            TextField(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier().width(300.px)
            )
        }

        // Custom styled form field
        FormField(
            label = {
                Text(
                    "Premium Field",
                    style = Typography.BODY1.copy(
                        color = Colors.Primary.MAIN,
                        fontWeight = FontWeight.BOLD
                    )
                )
            },
            helperText = {
                Text(
                    "This field has custom styling",
                    style = Typography.CAPTION.copy(color = Colors.Primary.DARK)
                )
            },
            errorText = if (isError) {
                {
                    Text(
                        "This field has an error",
                        style = Typography.CAPTION.copy(
                            color = Colors.Error.MAIN,
                            fontWeight = FontWeight.MEDIUM
                        )
                    )
                }
            } else null,
            isError = isError,
            modifier = Modifier()
                .padding(Spacing.MD)
                .backgroundColor(Colors.Primary.LIGHT)
                .borderRadius(BorderRadius.LG)
                .border(Border.solid(2.px, Colors.Primary.MAIN))
                .padding(Spacing.MD)
        ) {
            TextField(
                value = value,
                onValueChange = {
                    value = it
                    isError = it.length > 10
                },
                modifier = Modifier()
                    .width(300.px)
                    .backgroundColor(Colors.White)
                    .borderRadius(BorderRadius.MD)
            )
        }
    }
}
```

## Accessibility Features

### ARIA Support

The FormField component automatically includes:

- `aria-labelledby` for proper label association
- `aria-describedby` for helper text and error messages
- `aria-required` for required fields
- `aria-invalid` for fields in error state
- Proper heading structure for nested fields

### Keyboard Navigation

- **Tab**: Navigate between form fields
- **Shift+Tab**: Navigate backwards
- **Enter**: Activate focused field
- **Escape**: Clear focus from current field

### Screen Reader Support

```kotlin
@Composable
fun AccessibleFormFieldExample() {
    var email by remember { mutableStateOf("") }

    FormField(
        label = {
            Text("Email Address")
        },
        helperText = {
            Text("We'll never share your email with anyone else")
        },
        isRequired = true,
        modifier = Modifier()
            .accessibilityLabel("Email address input section")
            .accessibilityRole("group")
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            type = "email",
            modifier = Modifier()
                .accessibilityLabel("Email address")
                .accessibilityHint("Enter your email address, required field")
        )
    }
}
```

## Styling and Theming

### Custom Spacing

```kotlin
@Composable
fun CustomSpacingFormField() {
    var value by remember { mutableStateOf("") }

    FormField(
        label = {
            Text("Custom Spaced Field")
        },
        modifier = Modifier()
            .gap(Spacing.LG) // Custom gap between elements
    ) {
        TextField(
            value = value,
            onValueChange = { value = it }
        )
    }
}
```

### Theme Integration

```kotlin
@Composable
fun ThemedFormFieldExample() {
    var value by remember { mutableStateOf("") }

    FormField(
        label = {
            Text(
                "Themed Field",
                style = Typography.BODY1.copy(color = Theme.current.primary)
            )
        },
        helperText = {
            Text(
                "This field uses theme colors",
                style = Typography.CAPTION.copy(color = Theme.current.onSurface.copy(alpha = 0.7f))
            )
        },
        modifier = Modifier()
            .backgroundColor(Theme.current.surface)
            .borderRadius(Theme.current.borderRadius)
            .padding(Theme.current.spacing.medium)
    ) {
        TextField(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier()
                .backgroundColor(Theme.current.background)
                .borderRadius(Theme.current.borderRadius)
        )
    }
}
```

## Platform Differences

### Browser (JS)

- Uses proper HTML structure with `<label>`, `<input>`, etc.
- Native form field associations
- CSS styling for visual presentation
- Browser accessibility features

### JVM

- Server-side rendering support
- Generates appropriate HTML structure
- Maintains proper semantics
- Server-side validation integration

## Testing Strategies

### Unit Testing

```kotlin
class FormFieldTest {
    @Test
    fun `form field displays label correctly`() {
        composeTestRule.setContent {
            FormField(
                label = { Text("Test Label") }
            ) {
                TextField(value = "", onValueChange = {})
            }
        }

        composeTestRule.onNodeWithText("Test Label").assertExists()
    }

    @Test
    fun `form field shows required indicator when isRequired is true`() {
        composeTestRule.setContent {
            FormField(
                label = { Text("Required Field") },
                isRequired = true
            ) {
                TextField(value = "", onValueChange = {})
            }
        }

        composeTestRule.onNodeWithText("*").assertExists()
    }

    @Test
    fun `form field shows error text when isError is true`() {
        composeTestRule.setContent {
            FormField(
                label = { Text("Field") },
                errorText = { Text("Error message") },
                isError = true
            ) {
                TextField(value = "", onValueChange = {})
            }
        }

        composeTestRule.onNodeWithText("Error message").assertExists()
    }
}
```

## Migration Guide

### From HTML Forms

```html
<!-- Before: HTML -->
<div class="form-field">
    <label for="email">Email Address *</label>
    <input type="email" id="email" name="email" required>
    <small>We'll never share your email</small>
</div>
```

```kotlin
// After: Summon
@Composable
fun EmailField() {
    var email by remember { mutableStateOf("") }

    FormField(
        label = { Text("Email Address") },
        helperText = { Text("We'll never share your email") },
        isRequired = true
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            type = "email"
        )
    }
}
```

## Best Practices

### Do

- Use clear, descriptive labels
- Provide helpful context with helper text
- Show required field indicators
- Display validation errors clearly
- Group related fields logically
- Maintain consistent spacing and styling

### Don't

- Use vague or unclear labels
- Overwhelm users with too much helper text
- Hide important validation messages
- Create overly complex field layouts
- Forget accessibility considerations

## Related Components

- [Form](Form.md) - For form containers
- [TextField](TextField.md) - For text input
- [Select](Select.md) - For dropdown selection
- [Checkbox](Checkbox.md) - For boolean choices
- [Button](Button.md) - For form actions