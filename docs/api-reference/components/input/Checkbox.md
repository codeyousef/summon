# Checkbox

Checkbox components provide binary choice inputs with support for indeterminate states, validation, and grouping.

## Overview

The Checkbox component allows users to select one or more options from a set. It supports controlled and uncontrolled
usage patterns, validation, and accessibility features.

### Key Features

- **Binary Selection**: Check/uncheck states
- **Indeterminate State**: For partial selections in groups
- **Validation Support**: Built-in client-side validation
- **Accessibility**: Full ARIA support and keyboard navigation
- **Controlled/Uncontrolled**: Flexible state management
- **Form Integration**: Works seamlessly with forms
- **Custom Styling**: Type-safe styling with modifiers

## API Reference

### Checkbox

```kotlin
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    isIndeterminate: Boolean = false,
    validators: List<Validator> = emptyList()
)
```

**Parameters:**

- `checked`: The current checked state of the checkbox
- `onCheckedChange`: Callback invoked when the checkbox state changes
- `modifier`: Modifier for styling and layout
- `enabled`: Whether the checkbox can be interacted with (default: `true`)
- `label`: Optional text label displayed next to the checkbox
- `isIndeterminate`: Whether to show indeterminate state (default: `false`)
- `validators`: List of validators for input validation

### StatefulCheckbox

```kotlin
@Composable
fun StatefulCheckbox(
    initialChecked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    isIndeterminate: Boolean = false,
    validators: List<Validator> = emptyList()
)
```

**Parameters:**

- `initialChecked`: Initial checked state (default: `false`)
- `onCheckedChange`: Callback invoked when state changes
- Other parameters same as `Checkbox`

## Usage Examples

### Basic Checkbox

```kotlin
@Composable
fun BasicCheckboxExample() {
    var isChecked by remember { mutableStateOf(false) }

    Checkbox(
        checked = isChecked,
        onCheckedChange = { isChecked = it },
        label = "Accept terms and conditions"
    )
}
```

### Checkbox with Validation

```kotlin
@Composable
fun ValidatedCheckboxExample() {
    var isAgreed by remember { mutableStateOf(false) }

    Checkbox(
        checked = isAgreed,
        onCheckedChange = { isAgreed = it },
        label = "I agree to the terms",
        validators = listOf(
            Validator { value ->
                if (value.toBoolean()) {
                    ValidationResult.valid()
                } else {
                    ValidationResult.invalid("You must agree to continue")
                }
            }
        ),
        modifier = Modifier()
            .padding(Spacing.MD)
    )
}
```

### Checkbox Group

```kotlin
@Composable
fun CheckboxGroupExample() {
    val options = listOf("Option 1", "Option 2", "Option 3")
    var selectedOptions by remember { mutableStateOf(setOf<String>()) }

    Column(modifier = Modifier().gap(Spacing.SM)) {
        Text("Select options:", style = Typography.H6)

        options.forEach { option ->
            Checkbox(
                checked = option in selectedOptions,
                onCheckedChange = { isChecked ->
                    selectedOptions = if (isChecked) {
                        selectedOptions + option
                    } else {
                        selectedOptions - option
                    }
                },
                label = option,
                modifier = Modifier().padding(vertical = Spacing.XS)
            )
        }
    }
}
```

### Indeterminate Checkbox

```kotlin
@Composable
fun IndeterminateCheckboxExample() {
    val childOptions = listOf("Child 1", "Child 2", "Child 3")
    var selectedChildren by remember { mutableStateOf(setOf<String>()) }

    val allSelected = selectedChildren.size == childOptions.size
    val noneSelected = selectedChildren.isEmpty()
    val isIndeterminate = !allSelected && !noneSelected

    Column(modifier = Modifier().gap(Spacing.SM)) {
        // Parent checkbox with indeterminate state
        Checkbox(
            checked = allSelected,
            onCheckedChange = { shouldSelectAll ->
                selectedChildren = if (shouldSelectAll) {
                    childOptions.toSet()
                } else {
                    emptySet()
                }
            },
            label = "Select All",
            isIndeterminate = isIndeterminate,
            modifier = Modifier()
                .padding(bottom = Spacing.SM)
                .fontWeight(FontWeight.BOLD)
        )

        // Child checkboxes
        childOptions.forEach { option ->
            Checkbox(
                checked = option in selectedChildren,
                onCheckedChange = { isChecked ->
                    selectedChildren = if (isChecked) {
                        selectedChildren + option
                    } else {
                        selectedChildren - option
                    }
                },
                label = option,
                modifier = Modifier()
                    .padding(left = Spacing.LG)
                    .padding(vertical = Spacing.XS)
            )
        }
    }
}
```

### Stateful Checkbox

```kotlin
@Composable
fun StatefulCheckboxExample() {
    StatefulCheckbox(
        initialChecked = false,
        onCheckedChange = { isChecked ->
            println("Checkbox changed to: $isChecked")
        },
        label = "Enable notifications",
        modifier = Modifier()
            .padding(Spacing.MD)
    )
}
```

### Form Integration

```kotlin
@Composable
fun CheckboxFormExample() {
    var newsletter by remember { mutableStateOf(false) }
    var terms by remember { mutableStateOf(false) }
    var privacy by remember { mutableStateOf(false) }

    Form(
        onSubmit = {
            println("Newsletter: $newsletter, Terms: $terms, Privacy: $privacy")
        }
    ) {
        FormField(label = "Preferences") {
            Column(modifier = Modifier().gap(Spacing.SM)) {
                Checkbox(
                    checked = newsletter,
                    onCheckedChange = { newsletter = it },
                    label = "Subscribe to newsletter"
                )

                Checkbox(
                    checked = terms,
                    onCheckedChange = { terms = it },
                    label = "Accept terms of service",
                    validators = listOf(
                        Validator { value ->
                            if (value.toBoolean()) {
                                ValidationResult.valid()
                            } else {
                                ValidationResult.invalid("Required")
                            }
                        }
                    )
                )

                Checkbox(
                    checked = privacy,
                    onCheckedChange = { privacy = it },
                    label = "Accept privacy policy",
                    validators = listOf(
                        Validator { value ->
                            if (value.toBoolean()) {
                                ValidationResult.valid()
                            } else {
                                ValidationResult.invalid("Required")
                            }
                        }
                    )
                )
            }
        }

        Button(
            text = "Submit",
            enabled = terms && privacy,
            type = ButtonType.SUBMIT
        )
    }
}
```

### Custom Styling

```kotlin
@Composable
fun StyledCheckboxExample() {
    var isChecked by remember { mutableStateOf(false) }

    Checkbox(
        checked = isChecked,
        onCheckedChange = { isChecked = it },
        label = "Custom styled checkbox",
        modifier = Modifier()
            .padding(Spacing.LG)
            .backgroundColor(if (isChecked) Colors.Primary.LIGHT else Colors.Gray.LIGHT)
            .borderRadius(BorderRadius.MD)
            .border(Border.solid(2.px, Colors.Primary.MAIN))
            .padding(Spacing.MD)
    )
}
```

## Accessibility Features

### ARIA Support

The Checkbox component automatically includes:

- `role="checkbox"` for screen readers
- `aria-checked` state management
- `aria-describedby` for validation messages
- `aria-label` or `aria-labelledby` for labels

### Keyboard Navigation

- **Space**: Toggle checkbox state
- **Tab**: Move focus to next interactive element
- **Shift+Tab**: Move focus to previous interactive element

### Screen Reader Support

```kotlin
@Composable
fun AccessibleCheckboxExample() {
    var isChecked by remember { mutableStateOf(false) }

    Checkbox(
        checked = isChecked,
        onCheckedChange = { isChecked = it },
        label = "Receive email notifications",
        modifier = Modifier()
            .accessibilityLabel("Toggle email notifications")
            .accessibilityHint("Double tap to ${if (isChecked) "disable" else "enable"} notifications")
    )
}
```

## Validation Patterns

### Required Checkbox

```kotlin
fun requiredCheckboxValidator() = Validator { value ->
    if (value.toBoolean()) {
        ValidationResult.valid()
    } else {
        ValidationResult.invalid("This field is required")
    }
}

@Composable
fun RequiredCheckboxExample() {
    var agreed by remember { mutableStateOf(false) }

    Checkbox(
        checked = agreed,
        onCheckedChange = { agreed = it },
        label = "I agree to the terms *",
        validators = listOf(requiredCheckboxValidator()),
        modifier = Modifier()
            .padding(Spacing.MD)
    )
}
```

### Custom Validation

```kotlin
fun minimumSelectionValidator(minimum: Int, currentCount: Int) = Validator { _ ->
    if (currentCount >= minimum) {
        ValidationResult.valid()
    } else {
        ValidationResult.invalid("Please select at least $minimum options")
    }
}

@Composable
fun GroupValidationExample() {
    val options = listOf("Red", "Green", "Blue", "Yellow")
    var selectedColors by remember { mutableStateOf(setOf<String>()) }

    Column(modifier = Modifier().gap(Spacing.SM)) {
        Text("Choose at least 2 colors:", style = Typography.H6)

        options.forEach { color ->
            Checkbox(
                checked = color in selectedColors,
                onCheckedChange = { isChecked ->
                    selectedColors = if (isChecked) {
                        selectedColors + color
                    } else {
                        selectedColors - color
                    }
                },
                label = color,
                validators = if (color == options.last()) {
                    listOf(minimumSelectionValidator(2, selectedColors.size))
                } else {
                    emptyList()
                }
            )
        }
    }
}
```

## State Management

### Controlled Component

```kotlin
@Composable
fun ControlledCheckboxExample() {
    // External state management
    var checkboxState by remember { mutableStateOf(false) }

    Column {
        Checkbox(
            checked = checkboxState,
            onCheckedChange = { checkboxState = it },
            label = "Controlled checkbox"
        )

        Button(
            text = "Toggle Programmatically",
            onClick = { checkboxState = !checkboxState }
        )

        Text("Current state: $checkboxState")
    }
}
```

### Uncontrolled Component

```kotlin
@Composable
fun UncontrolledCheckboxExample() {
    StatefulCheckbox(
        initialChecked = true,
        onCheckedChange = { isChecked ->
            println("Checkbox changed to: $isChecked")
        },
        label = "Uncontrolled checkbox"
    )
}
```

## Platform Differences

### Browser (JS)

- Renders as HTML `<input type="checkbox">` element
- Native browser styling and behavior
- Full keyboard and mouse support
- CSS styling through modifiers

### JVM

- Server-side rendering support
- Generates appropriate HTML for SSR
- Maintains state during form submissions
- Validation on server side

## Performance Considerations

### Optimization Tips

1. **Minimize Re-renders**: Use `remember` for stable references
2. **Batch Updates**: Group multiple checkbox changes
3. **Lazy Loading**: Use lazy components for large checkbox groups
4. **Memoization**: Cache expensive validator functions

```kotlin
@Composable
fun OptimizedCheckboxGroup() {
    val options = remember { generateLargeOptionsList() }
    var selections by remember { mutableStateOf(setOf<String>()) }

    // Memoize validator to prevent recreation
    val validator = remember {
        Validator { value ->
            // Expensive validation logic
            validateComplexRules(value)
        }
    }

    LazyColumn {
        items(options) { option ->
            Checkbox(
                checked = option in selections,
                onCheckedChange = { isChecked ->
                    selections = if (isChecked) {
                        selections + option
                    } else {
                        selections - option
                    }
                },
                label = option,
                validators = listOf(validator)
            )
        }
    }
}
```

## Testing Strategies

### Unit Testing

```kotlin
class CheckboxTest {
    @Test
    fun `checkbox toggles state correctly`() {
        var isChecked = false

        composeTestRule.setContent {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                label = "Test checkbox"
            )
        }

        // Click the checkbox
        composeTestRule.onNodeWithText("Test checkbox").performClick()
        assertTrue(isChecked)

        // Click again
        composeTestRule.onNodeWithText("Test checkbox").performClick()
        assertFalse(isChecked)
    }

    @Test
    fun `disabled checkbox does not respond to clicks`() {
        var isChecked = false

        composeTestRule.setContent {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                enabled = false,
                label = "Disabled checkbox"
            )
        }

        composeTestRule.onNodeWithText("Disabled checkbox").performClick()
        assertFalse(isChecked)
    }

    @Test
    fun `validation works correctly`() {
        var hasError = false

        composeTestRule.setContent {
            Checkbox(
                checked = false,
                onCheckedChange = {},
                validators = listOf(
                    Validator { value ->
                        val isValid = value.toBoolean()
                        hasError = !isValid
                        if (isValid) {
                            ValidationResult.valid()
                        } else {
                            ValidationResult.invalid("Required")
                        }
                    }
                )
            )
        }

        assertTrue(hasError)
    }
}
```

### Integration Testing

```kotlin
@Test
fun `checkbox group selects all correctly`() {
    val options = listOf("A", "B", "C")
    var selectedOptions = setOf<String>()

    composeTestRule.setContent {
        CheckboxGroup(
            options = options,
            selectedOptions = selectedOptions,
            onSelectionChange = { selectedOptions = it }
        )
    }

    // Click "Select All"
    composeTestRule.onNodeWithText("Select All").performClick()
    assertEquals(options.toSet(), selectedOptions)
}
```

## Migration Guide

### From HTML Forms

```html
<!-- Before: HTML -->
<input type="checkbox" id="newsletter" name="newsletter" value="yes">
<label for="newsletter">Subscribe to newsletter</label>
```

```kotlin
// After: Summon
@Composable
fun NewsletterCheckbox() {
    var newsletter by remember { mutableStateOf(false) }

    Checkbox(
        checked = newsletter,
        onCheckedChange = { newsletter = it },
        label = "Subscribe to newsletter"
    )
}
```

### From Other Frameworks

```jsx
// React example
const [checked, setChecked] = useState(false);
<input
    type="checkbox"
    checked={checked}
    onChange={(e) => setChecked(e.target.checked)}
/>
```

```kotlin
// Summon equivalent
var checked by remember { mutableStateOf(false) }
Checkbox(
    checked = checked,
    onCheckedChange = { checked = it }
)
```

## Best Practices

### Do

- Use clear, descriptive labels
- Provide validation feedback
- Group related checkboxes logically
- Support keyboard navigation
- Use indeterminate state for partial selections

### Don't

- Mix checkboxes and radio buttons in the same group
- Use very long labels that wrap multiple lines
- Forget to handle the disabled state properly
- Skip validation for required checkboxes

## Related Components

- [RadioButton](RadioButton.md) - For single selections
- [Switch](Switch.md) - For on/off toggles
- [Form](Form.md) - For form integration
- [FormField](FormField.md) - For field wrapping