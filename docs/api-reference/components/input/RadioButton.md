# RadioButton

RadioButton components provide single-choice selection within a group, ensuring only one option can be selected at a
time.

## Overview

The RadioButton component allows users to select exactly one option from a set of mutually exclusive choices. It follows
established radio button patterns with full accessibility support and flexible grouping.

### Key Features

- **Mutually Exclusive Selection**: Only one option can be selected per group
- **Flexible Grouping**: External state management for groups
- **Label Positioning**: Labels before or after the radio button
- **Accessibility**: Full ARIA support and keyboard navigation
- **Form Integration**: Works seamlessly with forms
- **Custom Styling**: Type-safe styling with modifiers

## API Reference

### RadioButton

```kotlin
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
)
```

**Parameters:**

- `selected`: Whether this radio button is currently selected within its group
- `onClick`: Callback invoked when this radio button is clicked
- `modifier`: Modifier for styling and layout
- `enabled`: Whether the radio button can be interacted with (default: `true`)

### RadioButton (with Label)

```kotlin
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    labelPosition: LabelPosition = LabelPosition.END,
    radioButtonStyle: Modifier = Modifier()
)
```

**Parameters:**

- `selected`: Whether this radio button is currently selected
- `onClick`: Callback invoked when clicked
- `modifier`: Modifier applied to the entire component
- `enabled`: Whether the radio button is interactive
- `label`: Optional text label
- `labelPosition`: Where to position the label (`START` or `END`)
- `radioButtonStyle`: Modifier applied specifically to the radio input

### RadioButtonWithLabel

```kotlin
@Composable
fun RadioButtonWithLabel(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: @Composable () -> Unit
)
```

**Parameters:**

- `selected`: Whether this radio button is currently selected
- `onClick`: Callback invoked when clicked
- `modifier`: Modifier applied to the container row
- `enabled`: Whether the radio button is interactive
- `label`: Composable lambda for custom label content

### LabelPosition

```kotlin
enum class LabelPosition {
    START, // Label appears before the radio button
    END    // Label appears after the radio button
}
```

## Usage Examples

### Basic Radio Button Group

```kotlin
@Composable
fun BasicRadioGroupExample() {
    val options = listOf("Option 1", "Option 2", "Option 3")
    var selectedOption by remember { mutableStateOf(options[0]) }

    Column(modifier = Modifier().gap(Spacing.SM)) {
        Text("Choose an option:", style = Typography.H6)

        options.forEach { option ->
            RadioButton(
                selected = selectedOption == option,
                onClick = { selectedOption = option },
                label = option,
                modifier = Modifier().padding(vertical = Spacing.XS)
            )
        }

        Text("Selected: $selectedOption")
    }
}
```

### Radio Group with Custom Values

```kotlin
data class ColorOption(val name: String, val value: String)

@Composable
fun ColorRadioGroupExample() {
    val colorOptions = listOf(
        ColorOption("Red", "#FF0000"),
        ColorOption("Green", "#00FF00"),
        ColorOption("Blue", "#0000FF")
    )
    var selectedColor by remember { mutableStateOf(colorOptions[0]) }

    Column(modifier = Modifier().gap(Spacing.SM)) {
        Text("Choose a color:", style = Typography.H6)

        colorOptions.forEach { color ->
            RadioButton(
                selected = selectedColor == color,
                onClick = { selectedColor = color },
                label = color.name,
                modifier = Modifier()
                    .padding(vertical = Spacing.XS)
                    .backgroundColor(Color.parse(color.value).copy(alpha = 0.1f))
                    .borderRadius(BorderRadius.SM)
                    .padding(Spacing.SM)
            )
        }

        Box(
            modifier = Modifier()
                .width(50.px)
                .height(50.px)
                .backgroundColor(Color.parse(selectedColor.value))
                .borderRadius(BorderRadius.MD)
                .marginTop(Spacing.MD)
        ) {}
    }
}
```

### Label Positioning

```kotlin
@Composable
fun LabelPositionExample() {
    var selectedPosition by remember { mutableStateOf("start") }

    Column(modifier = Modifier().gap(Spacing.LG)) {
        Text("Label Position:", style = Typography.H6)

        // Label at start
        RadioButton(
            selected = selectedPosition == "start",
            onClick = { selectedPosition = "start" },
            label = "Label at start",
            labelPosition = LabelPosition.START
        )

        // Label at end (default)
        RadioButton(
            selected = selectedPosition == "end",
            onClick = { selectedPosition = "end" },
            label = "Label at end",
            labelPosition = LabelPosition.END
        )
    }
}
```

### Custom Label Content

```kotlin
@Composable
fun CustomLabelExample() {
    val paymentMethods = listOf("Credit Card", "PayPal", "Bank Transfer")
    var selectedMethod by remember { mutableStateOf(paymentMethods[0]) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Payment Method:", style = Typography.H6)

        paymentMethods.forEach { method ->
            RadioButtonWithLabel(
                selected = selectedMethod == method,
                onClick = { selectedMethod = method },
                modifier = Modifier()
                    .padding(Spacing.SM)
                    .border(
                        Border.solid(
                            1.px,
                            if (selectedMethod == method) Colors.Primary.MAIN else Colors.Gray.LIGHT
                        )
                    )
                    .borderRadius(BorderRadius.MD)
            ) {
                Row(
                    modifier = Modifier()
                        .alignItems(AlignItems.Center)
                        .gap(Spacing.SM)
                ) {
                    Icon(
                        name = when (method) {
                            "Credit Card" -> "credit_card"
                            "PayPal" -> "payment"
                            else -> "account_balance"
                        }
                    )
                    Column {
                        Text(method, style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                        Text(
                            when (method) {
                                "Credit Card" -> "Visa, MasterCard, Amex"
                                "PayPal" -> "Pay with your PayPal account"
                                else -> "Direct bank transfer"
                            },
                            style = Typography.CAPTION
                        )
                    }
                }
            }
        }
    }
}
```

### Form Integration

```kotlin
@Composable
fun RadioButtonFormExample() {
    var selectedSize by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("") }

    Form(
        onSubmit = {
            println("Selected: Size=$selectedSize, Color=$selectedColor")
        }
    ) {
        FormField(label = "Size") {
            Column(modifier = Modifier().gap(Spacing.XS)) {
                listOf("Small", "Medium", "Large").forEach { size ->
                    RadioButton(
                        selected = selectedSize == size,
                        onClick = { selectedSize = size },
                        label = size
                    )
                }
            }
        }

        FormField(label = "Color") {
            Column(modifier = Modifier().gap(Spacing.XS)) {
                listOf("Red", "Blue", "Green").forEach { color ->
                    RadioButton(
                        selected = selectedColor == color,
                        onClick = { selectedColor = color },
                        label = color
                    )
                }
            }
        }

        Button(
            text = "Add to Cart",
            type = ButtonType.SUBMIT,
            enabled = selectedSize.isNotEmpty() && selectedColor.isNotEmpty()
        )
    }
}
```

### Disabled Radio Buttons

```kotlin
@Composable
fun DisabledRadioExample() {
    val options = listOf(
        "Available" to true,
        "Limited Stock" to true,
        "Out of Stock" to false
    )
    var selectedOption by remember { mutableStateOf("Available") }

    Column(modifier = Modifier().gap(Spacing.SM)) {
        Text("Product Options:", style = Typography.H6)

        options.forEach { (option, enabled) ->
            RadioButton(
                selected = selectedOption == option,
                onClick = { if (enabled) selectedOption = option },
                enabled = enabled,
                label = option,
                modifier = Modifier().padding(vertical = Spacing.XS)
            )
        }
    }
}
```

### Radio Group with Validation

```kotlin
@Composable
fun ValidatedRadioGroupExample() {
    val options = listOf("Option A", "Option B", "Option C")
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }

    Column(modifier = Modifier().gap(Spacing.SM)) {
        Text("Required Selection *", style = Typography.H6)

        options.forEach { option ->
            RadioButton(
                selected = selectedOption == option,
                onClick = {
                    selectedOption = option
                    showError = false
                },
                label = option,
                modifier = Modifier().padding(vertical = Spacing.XS)
            )
        }

        if (showError) {
            Text(
                "Please select an option",
                style = Typography.CAPTION.copy(color = Colors.Error.MAIN),
                modifier = Modifier().padding(top = Spacing.XS)
            )
        }

        Button(
            text = "Continue",
            onClick = {
                if (selectedOption == null) {
                    showError = true
                } else {
                    println("Selected: $selectedOption")
                }
            }
        )
    }
}
```

### Custom Styling

```kotlin
@Composable
fun StyledRadioGroupExample() {
    val themes = listOf("Light", "Dark", "Auto")
    var selectedTheme by remember { mutableStateOf("Light") }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Theme Selection:", style = Typography.H6)

        themes.forEach { theme ->
            RadioButton(
                selected = selectedTheme == theme,
                onClick = { selectedTheme = theme },
                label = theme,
                modifier = Modifier()
                    .width(Width.FULL)
                    .padding(Spacing.MD)
                    .backgroundColor(
                        if (selectedTheme == theme) {
                            Colors.Primary.LIGHT
                        } else {
                            Colors.Gray.LIGHT
                        }
                    )
                    .borderRadius(BorderRadius.LG)
                    .border(
                        Border.solid(
                            2.px,
                            if (selectedTheme == theme) {
                                Colors.Primary.MAIN
                            } else {
                                Colors.Transparent
                            }
                        )
                    ),
                radioButtonStyle = Modifier()
                    .accentColor(Colors.Primary.MAIN)
            )
        }
    }
}
```

## Accessibility Features

### ARIA Support

The RadioButton component automatically includes:

- `role="radio"` for screen readers
- `aria-checked` state management
- `radiogroup` role for containers
- `aria-labelledby` for group labels

### Keyboard Navigation

- **Arrow Keys**: Navigate between radio buttons in a group
- **Space/Enter**: Select the focused radio button
- **Tab**: Move focus to the next radio group
- **Shift+Tab**: Move focus to the previous radio group

### Screen Reader Support

```kotlin
@Composable
fun AccessibleRadioGroupExample() {
    val sizes = listOf("Small", "Medium", "Large")
    var selectedSize by remember { mutableStateOf("Medium") }

    // Accessible radio group
    Box(
        modifier = Modifier()
            .accessibilityRole("radiogroup")
            .accessibilityLabel("Size selection")
    ) {
        Column(modifier = Modifier().gap(Spacing.SM)) {
            Text(
                "Select Size:",
                style = Typography.H6,
                modifier = Modifier()
                    .accessibilityRole("heading")
                    .accessibilityLevel(3)
            )

            sizes.forEach { size ->
                RadioButton(
                    selected = selectedSize == size,
                    onClick = { selectedSize = size },
                    label = size,
                    modifier = Modifier()
                        .accessibilityLabel("Size $size")
                        .accessibilityHint(
                            if (selectedSize == size) "Currently selected" else "Double tap to select"
                        )
                )
            }
        }
    }
}
```

## State Management Patterns

### External State Management

```kotlin
@Composable
fun ExternalStateExample() {
    // State managed by parent component
    var selectedValue by remember { mutableStateOf("option1") }

    fun handleSelection(value: String) {
        selectedValue = value
        // Additional logic like analytics, validation, etc.
        println("Option selected: $value")
    }

    Column {
        listOf("option1", "option2", "option3").forEach { option ->
            RadioButton(
                selected = selectedValue == option,
                onClick = { handleSelection(option) },
                label = option.capitalize()
            )
        }

        // External control
        Button(
            text = "Reset to Option 1",
            onClick = { handleSelection("option1") }
        )
    }
}
```

### Complex State with Objects

```kotlin
data class Product(val id: String, val name: String, val price: Double)

@Composable
fun ProductSelectionExample() {
    val products = listOf(
        Product("1", "Basic Plan", 9.99),
        Product("2", "Pro Plan", 19.99),
        Product("3", "Enterprise Plan", 39.99)
    )
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Choose a plan:", style = Typography.H6)

        products.forEach { product ->
            RadioButtonWithLabel(
                selected = selectedProduct == product,
                onClick = { selectedProduct = product },
                modifier = Modifier()
                    .width(Width.FULL)
                    .padding(Spacing.MD)
                    .border(
                        Border.solid(
                            1.px,
                            if (selectedProduct == product) Colors.Primary.MAIN else Colors.Gray.LIGHT
                        )
                    )
                    .borderRadius(BorderRadius.MD)
            ) {
                Row(
                    modifier = Modifier()
                        .width(Width.FULL)
                        .justifyContent(JustifyContent.SpaceBetween)
                        .alignItems(AlignItems.Center)
                ) {
                    Text(product.name, style = Typography.BODY1)
                    Text(
                        "$${product.price}/month",
                        style = Typography.BODY2.copy(fontWeight = FontWeight.BOLD)
                    )
                }
            }
        }

        selectedProduct?.let { product ->
            Text(
                "Selected: ${product.name} - $${product.price}/month",
                style = Typography.BODY2,
                modifier = Modifier()
                    .padding(top = Spacing.MD)
                    .backgroundColor(Colors.Success.LIGHT)
                    .padding(Spacing.SM)
                    .borderRadius(BorderRadius.SM)
            )
        }
    }
}
```

## Platform Differences

### Browser (JS)

- Renders as HTML `<input type="radio">` element
- Native browser grouping by `name` attribute
- CSS styling through modifiers
- Full keyboard and mouse support

### JVM

- Server-side rendering support
- Generates appropriate HTML for SSR
- Maintains state during form submissions
- Server-side validation support

## Performance Considerations

### Optimization Tips

1. **Stable References**: Use `remember` for stable callback references
2. **Minimize Re-renders**: Avoid creating new lambdas in render
3. **Large Groups**: Consider virtualization for many options
4. **Memoization**: Cache expensive computations

```kotlin
@Composable
fun OptimizedRadioGroupExample() {
    val options = remember { generateLargeOptionsList() }
    var selectedOption by remember { mutableStateOf<String?>(null) }

    // Memoize the click handler to prevent recreation
    val handleOptionClick = remember {
        { option: String ->
            selectedOption = option
            // Additional logic
        }
    }

    // For very large lists, consider LazyColumn
    if (options.size > 20) {
        LazyColumn {
            items(options) { option ->
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { handleOptionClick(option) },
                    label = option
                )
            }
        }
    } else {
        Column {
            options.forEach { option ->
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { handleOptionClick(option) },
                    label = option
                )
            }
        }
    }
}
```

## Testing Strategies

### Unit Testing

```kotlin
class RadioButtonTest {
    @Test
    fun `radio button group maintains single selection`() {
        val options = listOf("A", "B", "C")
        var selectedOption = "A"

        composeTestRule.setContent {
            options.forEach { option ->
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { selectedOption = option },
                    label = option
                )
            }
        }

        // Select option B
        composeTestRule.onNodeWithText("B").performClick()
        assertEquals("B", selectedOption)

        // Select option C
        composeTestRule.onNodeWithText("C").performClick()
        assertEquals("C", selectedOption)
    }

    @Test
    fun `disabled radio button does not respond to clicks`() {
        var selectedOption = "A"

        composeTestRule.setContent {
            RadioButton(
                selected = false,
                onClick = { selectedOption = "B" },
                enabled = false,
                label = "Disabled option"
            )
        }

        composeTestRule.onNodeWithText("Disabled option").performClick()
        assertEquals("A", selectedOption) // Should not change
    }
}
```

### Integration Testing

```kotlin
@Test
fun `radio group integrates with form submission`() {
    var submittedValue = ""

    composeTestRule.setContent {
        var selectedValue by remember { mutableStateOf("") }

        Form(onSubmit = { submittedValue = selectedValue }) {
            listOf("Option 1", "Option 2").forEach { option ->
                RadioButton(
                    selected = selectedValue == option,
                    onClick = { selectedValue = option },
                    label = option
                )
            }
            Button(text = "Submit", type = ButtonType.SUBMIT)
        }
    }

    // Select an option and submit
    composeTestRule.onNodeWithText("Option 2").performClick()
    composeTestRule.onNodeWithText("Submit").performClick()

    assertEquals("Option 2", submittedValue)
}
```

## Migration Guide

### From HTML Forms

```html
<!-- Before: HTML -->
<input type="radio" id="small" name="size" value="small">
<label for="small">Small</label>
<input type="radio" id="large" name="size" value="large">
<label for="large">Large</label>
```

```kotlin
// After: Summon
@Composable
fun SizeSelection() {
    var selectedSize by remember { mutableStateOf("small") }

    Column {
        RadioButton(
            selected = selectedSize == "small",
            onClick = { selectedSize = "small" },
            label = "Small"
        )
        RadioButton(
            selected = selectedSize == "large",
            onClick = { selectedSize = "large" },
            label = "Large"
        )
    }
}
```

### From Other Frameworks

```jsx
// React example
const [selected, setSelected] = useState('option1');
{options.map(option => (
    <input
        key={option}
        type="radio"
        checked={selected === option}
        onChange={() => setSelected(option)}
    />
))}
```

```kotlin
// Summon equivalent
var selected by remember { mutableStateOf("option1") }
options.forEach { option ->
    RadioButton(
        selected = selected == option,
        onClick = { selected = option },
        label = option
    )
}
```

## Best Practices

### Do

- Keep radio button groups to 7 or fewer options when possible
- Use clear, descriptive labels
- Provide a default selection when appropriate
- Group related options logically
- Use consistent spacing and alignment

### Don't

- Mix radio buttons with checkboxes in the same group
- Use radio buttons for single yes/no choices (use Switch instead)
- Create very large radio button groups (consider Select instead)
- Forget to handle keyboard navigation

## Related Components

- [Checkbox](Checkbox.md) - For multiple selections
- [Switch](Switch.md) - For on/off toggles
- [Select](Select.md) - For dropdown selections
- [Form](Form.md) - For form integration