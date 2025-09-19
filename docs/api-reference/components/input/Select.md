# Select

Select components provide dropdown selection functionality with support for single and multiple selections, validation,
and accessibility.

## Overview

The Select component allows users to choose one or more options from a dropdown list. It's ideal for presenting many
options in a compact space and supports both simple string values and complex objects.

### Key Features

- **Single/Multiple Selection**: Support for both selection modes
- **Option Objects**: Rich option data with labels, values, and disabled states
- **Validation Support**: Built-in client-side validation
- **Accessibility**: Full ARIA support and keyboard navigation
- **Form Integration**: Works seamlessly with forms
- **Custom Styling**: Type-safe styling with modifiers
- **State Management**: Controlled component with flexible state handling

## API Reference

### Select

```kotlin
@Composable
fun <T> Select(
    selectedValue: SummonMutableState<T?>,
    options: List<SelectOption<T>>,
    onSelectedChange: (T?) -> Unit = {},
    label: String? = null,
    placeholder: String? = null,
    modifier: Modifier = Modifier(),
    multiple: Boolean = false,
    disabled: Boolean = false,
    size: Int = 1,
    validators: List<Validator> = emptyList()
)
```

**Parameters:**

- `selectedValue`: Mutable state holding the current selected value
- `options`: List of options to display in the dropdown
- `onSelectedChange`: Callback invoked when selection changes
- `label`: Optional label to display for the select
- `placeholder`: Placeholder text when no option is selected
- `modifier`: Modifier for styling and layout
- `multiple`: Whether multiple selections are allowed (default: `false`)
- `disabled`: Whether the select is disabled (default: `false`)
- `size`: Number of visible options when dropdown is open (default: `1`)
- `validators`: List of validators for input validation

### SelectOption

```kotlin
data class SelectOption<T>(
    val value: T,
    val label: String,
    val disabled: Boolean = false
)
```

**Properties:**

- `value`: The actual value of the option
- `label`: Display text for the option
- `disabled`: Whether this option can be selected (default: `false`)

### SelectState

```kotlin
class SelectState<T>(
    val selectedValue: SummonMutableState<T?>,
    val validators: List<Validator> = emptyList()
)
```

**Methods:**

- `validate(): Boolean` - Validates the current value
- `getValidationErrors(): List<String>` - Gets current validation errors
- `isValid(): Boolean` - Checks if the field is currently valid

## Usage Examples

### Basic Select

```kotlin
@Composable
fun BasicSelectExample() {
    val countries = listOf("USA", "Canada", "UK", "Germany", "France")
    var selectedCountry by remember { mutableStateOf<String?>(null) }

    Select(
        selectedValue = remember { mutableStateOf(selectedCountry) }.apply { value = selectedCountry },
        options = countries.map { SelectOption(it, it) },
        onSelectedChange = { selectedCountry = it },
        label = "Country",
        placeholder = "Select a country",
        modifier = Modifier()
            .width(300.px)
            .padding(Spacing.MD)
    )
}
```

### Select with Objects

```kotlin
data class User(val id: Int, val name: String, val email: String)

@Composable
fun UserSelectExample() {
    val users = listOf(
        User(1, "John Doe", "john@example.com"),
        User(2, "Jane Smith", "jane@example.com"),
        User(3, "Bob Johnson", "bob@example.com")
    )
    var selectedUser by remember { mutableStateOf<User?>(null) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Select(
            selectedValue = remember { mutableStateOf(selectedUser) }.apply { value = selectedUser },
            options = users.map { user ->
                SelectOption(
                    value = user,
                    label = "${user.name} (${user.email})"
                )
            },
            onSelectedChange = { selectedUser = it },
            label = "Assign to User",
            placeholder = "Choose a user",
            modifier = Modifier().width(400.px)
        )

        selectedUser?.let { user ->
            Text(
                "Selected: ${user.name}",
                style = Typography.BODY2,
                modifier = Modifier()
                    .padding(Spacing.SM)
                    .backgroundColor(Colors.Info.LIGHT)
                    .borderRadius(BorderRadius.SM)
                    .padding(Spacing.SM)
            )
        }
    }
}
```

### Select with Validation

```kotlin
@Composable
fun ValidatedSelectExample() {
    val priorities = listOf("Low", "Medium", "High", "Critical")
    var selectedPriority by remember { mutableStateOf<String?>(null) }

    val requiredValidator = Validator { value ->
        if (value.isNullOrBlank()) {
            ValidationResult.invalid("Priority is required")
        } else {
            ValidationResult.valid()
        }
    }

    Select(
        selectedValue = remember { mutableStateOf(selectedPriority) }.apply { value = selectedPriority },
        options = priorities.map { SelectOption(it, it) },
        onSelectedChange = { selectedPriority = it },
        label = "Priority *",
        placeholder = "Select priority level",
        validators = listOf(requiredValidator),
        modifier = Modifier()
            .width(250.px)
            .padding(Spacing.MD)
    )
}
```

### Multiple Selection

```kotlin
@Composable
fun MultipleSelectExample() {
    val skills = listOf(
        "JavaScript", "Kotlin", "Python", "Java", "TypeScript",
        "React", "Vue", "Angular", "Node.js", "Spring"
    )
    var selectedSkills by remember { mutableStateOf(setOf<String>()) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        // Note: Multiple selection requires different state management
        Text("Skills (Multiple Selection):", style = Typography.H6)

        // Custom multi-select implementation
        Column(
            modifier = Modifier()
                .border(Border.solid(1.px, Colors.Gray.MAIN))
                .borderRadius(BorderRadius.SM)
                .padding(Spacing.SM)
                .maxHeight(200.px)
                .overflowY("scroll")
        ) {
            skills.forEach { skill ->
                Row(
                    modifier = Modifier()
                        .width(Width.FULL)
                        .padding(Spacing.XS)
                        .cursor("pointer")
                        .onClick {
                            selectedSkills = if (skill in selectedSkills) {
                                selectedSkills - skill
                            } else {
                                selectedSkills + skill
                            }
                        }
                        .backgroundColor(
                            if (skill in selectedSkills) Colors.Primary.LIGHT else Colors.Transparent
                        )
                        .borderRadius(BorderRadius.XS)
                        .alignItems(AlignItems.Center)
                        .gap(Spacing.SM)
                ) {
                    Checkbox(
                        checked = skill in selectedSkills,
                        onCheckedChange = { /* Handled by row click */ }
                    )
                    Text(skill)
                }
            }
        }

        if (selectedSkills.isNotEmpty()) {
            Text(
                "Selected: ${selectedSkills.joinToString(", ")}",
                style = Typography.BODY2,
                modifier = Modifier()
                    .padding(Spacing.SM)
                    .backgroundColor(Colors.Success.LIGHT)
                    .borderRadius(BorderRadius.SM)
                    .padding(Spacing.SM)
            )
        }
    }
}
```

### Grouped Options

```kotlin
data class OptionGroup<T>(
    val groupName: String,
    val options: List<SelectOption<T>>
)

@Composable
fun GroupedSelectExample() {
    val fontGroups = listOf(
        OptionGroup(
            "Serif",
            listOf(
                SelectOption("Times New Roman", "Times New Roman"),
                SelectOption("Georgia", "Georgia"),
                SelectOption("serif", "serif")
            )
        ),
        OptionGroup(
            "Sans-serif",
            listOf(
                SelectOption("Arial", "Arial"),
                SelectOption("Helvetica", "Helvetica"),
                SelectOption("sans-serif", "sans-serif")
            )
        ),
        OptionGroup(
            "Monospace",
            listOf(
                SelectOption("Courier New", "Courier New"),
                SelectOption("Monaco", "Monaco"),
                SelectOption("monospace", "monospace")
            )
        )
    )

    var selectedFont by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Font Family:", style = Typography.H6)

        // Custom grouped select (since native Select doesn't support groups)
        Column(
            modifier = Modifier()
                .border(Border.solid(1.px, Colors.Gray.MAIN))
                .borderRadius(BorderRadius.SM)
                .width(300.px)
        ) {
            fontGroups.forEachIndexed { groupIndex, group ->
                if (groupIndex > 0) {
                    Divider()
                }

                Text(
                    group.groupName,
                    style = Typography.CAPTION.copy(
                        fontWeight = FontWeight.BOLD,
                        color = Colors.Gray.DARK
                    ),
                    modifier = Modifier()
                        .padding(Spacing.SM)
                        .backgroundColor(Colors.Gray.LIGHT)
                )

                group.options.forEach { option ->
                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .padding(Spacing.SM)
                            .cursor("pointer")
                            .onClick { selectedFont = option.value }
                            .backgroundColor(
                                if (selectedFont == option.value) Colors.Primary.LIGHT else Colors.Transparent
                            )
                            .hoverBackgroundColor(Colors.Gray.LIGHT)
                            .alignItems(AlignItems.Center)
                    ) {
                        Text(
                            option.label,
                            style = Typography.BODY2.copy(fontFamily = option.value)
                        )
                    }
                }
            }
        }

        selectedFont?.let { font ->
            Text(
                "Selected: $font",
                style = Typography.BODY2.copy(fontFamily = font),
                modifier = Modifier()
                    .padding(Spacing.SM)
                    .backgroundColor(Colors.Info.LIGHT)
                    .borderRadius(BorderRadius.SM)
                    .padding(Spacing.SM)
            )
        }
    }
}
```

### Searchable Select

```kotlin
@Composable
fun SearchableSelectExample() {
    val allOptions = listOf(
        "Apple", "Banana", "Cherry", "Date", "Elderberry",
        "Fig", "Grape", "Honeydew", "Kiwi", "Lemon",
        "Mango", "Orange", "Papaya", "Quince", "Raspberry"
    )

    var searchQuery by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isOpen by remember { mutableStateOf(false) }

    val filteredOptions = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allOptions
        } else {
            allOptions.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    Column(modifier = Modifier().gap(Spacing.SM)) {
        Text("Searchable Fruit Selection:", style = Typography.H6)

        Box(modifier = Modifier().position("relative")) {
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    isOpen = true
                },
                placeholder = selectedOption ?: "Search fruits...",
                label = "Fruit",
                modifier = Modifier()
                    .width(300.px)
                    .onFocus { isOpen = true }
            )

            if (isOpen && filteredOptions.isNotEmpty()) {
                Column(
                    modifier = Modifier()
                        .position("absolute")
                        .top("100%")
                        .left("0")
                        .width(Width.FULL)
                        .maxHeight(200.px)
                        .overflowY("scroll")
                        .backgroundColor(Colors.White)
                        .border(Border.solid(1.px, Colors.Gray.MAIN))
                        .borderRadius(BorderRadius.SM)
                        .boxShadow("0 2px 8px rgba(0,0,0,0.1)")
                        .zIndex(1000)
                ) {
                    filteredOptions.forEach { option ->
                        Row(
                            modifier = Modifier()
                                .width(Width.FULL)
                                .padding(Spacing.SM)
                                .cursor("pointer")
                                .onClick {
                                    selectedOption = option
                                    searchQuery = ""
                                    isOpen = false
                                }
                                .hoverBackgroundColor(Colors.Primary.LIGHT)
                                .alignItems(AlignItems.Center)
                        ) {
                            Text(option, style = Typography.BODY2)
                        }
                    }
                }
            }
        }

        selectedOption?.let { option ->
            Text(
                "Selected: $option",
                style = Typography.BODY2,
                modifier = Modifier()
                    .padding(Spacing.SM)
                    .backgroundColor(Colors.Success.LIGHT)
                    .borderRadius(BorderRadius.SM)
                    .padding(Spacing.SM)
            )
        }

        // Click outside to close
        if (isOpen) {
            Box(
                modifier = Modifier()
                    .position("fixed")
                    .top("0")
                    .left("0")
                    .width("100vw")
                    .height("100vh")
                    .zIndex(999)
                    .onClick { isOpen = false }
            ) {}
        }
    }
}
```

### Form Integration

```kotlin
@Composable
fun SelectFormExample() {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedPriority by remember { mutableStateOf<String?>(null) }
    var selectedAssignee by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Bug", "Feature", "Enhancement", "Documentation")
    val priorities = listOf("Low", "Medium", "High", "Critical")
    val assignees = listOf("John Doe", "Jane Smith", "Bob Johnson")

    Form(
        onSubmit = {
            println("Submitted:")
            println("Category: $selectedCategory")
            println("Priority: $selectedPriority")
            println("Assignee: $selectedAssignee")
        }
    ) {
        FormField(label = "Issue Details") {
            Column(modifier = Modifier().gap(Spacing.MD)) {
                Select(
                    selectedValue = remember { mutableStateOf(selectedCategory) }.apply { value = selectedCategory },
                    options = categories.map { SelectOption(it, it) },
                    onSelectedChange = { selectedCategory = it },
                    label = "Category *",
                    placeholder = "Select category",
                    validators = listOf(
                        Validator { value ->
                            if (value.isNullOrBlank()) {
                                ValidationResult.invalid("Category is required")
                            } else {
                                ValidationResult.valid()
                            }
                        }
                    ),
                    modifier = Modifier().width(Width.FULL)
                )

                Select(
                    selectedValue = remember { mutableStateOf(selectedPriority) }.apply { value = selectedPriority },
                    options = priorities.map { SelectOption(it, it) },
                    onSelectedChange = { selectedPriority = it },
                    label = "Priority *",
                    placeholder = "Select priority",
                    validators = listOf(
                        Validator { value ->
                            if (value.isNullOrBlank()) {
                                ValidationResult.invalid("Priority is required")
                            } else {
                                ValidationResult.valid()
                            }
                        }
                    ),
                    modifier = Modifier().width(Width.FULL)
                )

                Select(
                    selectedValue = remember { mutableStateOf(selectedAssignee) }.apply { value = selectedAssignee },
                    options = assignees.map { SelectOption(it, it) },
                    onSelectedChange = { selectedAssignee = it },
                    label = "Assignee",
                    placeholder = "Select assignee (optional)",
                    modifier = Modifier().width(Width.FULL)
                )
            }
        }

        Button(
            text = "Create Issue",
            type = ButtonType.SUBMIT,
            enabled = selectedCategory != null && selectedPriority != null
        )
    }
}
```

### Custom Styling

```kotlin
@Composable
fun StyledSelectExample() {
    val themes = listOf(
        SelectOption("light", "Light Theme"),
        SelectOption("dark", "Dark Theme"),
        SelectOption("auto", "Auto (System)")
    )
    var selectedTheme by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier().gap(Spacing.LG)) {
        Text("Custom Styled Select:", style = Typography.H6)

        // Custom styled select
        Select(
            selectedValue = remember { mutableStateOf(selectedTheme) }.apply { value = selectedTheme },
            options = themes,
            onSelectedChange = { selectedTheme = it },
            label = "Theme",
            placeholder = "Choose theme",
            modifier = Modifier()
                .width(300.px)
                .backgroundColor(Colors.Gray.LIGHT)
                .borderRadius(BorderRadius.LG)
                .border(Border.solid(2.px, Colors.Primary.MAIN))
                .padding(Spacing.MD)
                .fontSize(16.px)
                .fontWeight(FontWeight.MEDIUM)
        )

        // Compact select
        Select(
            selectedValue = remember { mutableStateOf(selectedTheme) }.apply { value = selectedTheme },
            options = themes,
            onSelectedChange = { selectedTheme = it },
            placeholder = "Compact",
            modifier = Modifier()
                .width(150.px)
                .height(32.px)
                .fontSize(14.px)
                .borderRadius(BorderRadius.SM)
        )
    }
}
```

## Accessibility Features

### ARIA Support

The Select component automatically includes:

- `role="combobox"` for the select element
- `aria-expanded` for dropdown state
- `aria-labelledby` for associated labels
- `aria-describedby` for validation messages
- `aria-invalid` for validation state

### Keyboard Navigation

- **Arrow Down/Up**: Navigate through options
- **Enter/Space**: Select focused option
- **Escape**: Close dropdown
- **Tab**: Move focus to next element
- **Type**: Jump to options starting with typed character

### Screen Reader Support

```kotlin
@Composable
fun AccessibleSelectExample() {
    val languages = listOf("English", "Spanish", "French", "German")
    var selectedLanguage by remember { mutableStateOf<String?>(null) }

    Select(
        selectedValue = remember { mutableStateOf(selectedLanguage) }.apply { value = selectedLanguage },
        options = languages.map { SelectOption(it, it) },
        onSelectedChange = { selectedLanguage = it },
        label = "Preferred Language",
        placeholder = "Select your language",
        modifier = Modifier()
            .accessibilityLabel("Language selection dropdown")
            .accessibilityHint("Choose your preferred language from the list")
    )
}
```

## Validation Patterns

### Required Selection

```kotlin
fun requiredSelectValidator() = Validator { value ->
    if (value.isNullOrBlank()) {
        ValidationResult.invalid("This field is required")
    } else {
        ValidationResult.valid()
    }
}

@Composable
fun RequiredSelectExample() {
    var selectedValue by remember { mutableStateOf<String?>(null) }

    Select(
        selectedValue = remember { mutableStateOf(selectedValue) }.apply { value = selectedValue },
        options = listOf("Option 1", "Option 2").map { SelectOption(it, it) },
        onSelectedChange = { selectedValue = it },
        label = "Required Field *",
        validators = listOf(requiredSelectValidator())
    )
}
```

### Custom Validation

```kotlin
fun minimumValueValidator(minimum: Int) = Validator { value ->
    val numValue = value?.toIntOrNull()
    if (numValue != null && numValue >= minimum) {
        ValidationResult.valid()
    } else {
        ValidationResult.invalid("Value must be at least $minimum")
    }
}

@Composable
fun CustomValidationExample() {
    val quantities = (1..10).map { SelectOption(it.toString(), "$it items") }
    var selectedQuantity by remember { mutableStateOf<String?>(null) }

    Select(
        selectedValue = remember { mutableStateOf(selectedQuantity) }.apply { value = selectedQuantity },
        options = quantities,
        onSelectedChange = { selectedQuantity = it },
        label = "Quantity",
        placeholder = "Select quantity",
        validators = listOf(
            requiredSelectValidator(),
            minimumValueValidator(3)
        )
    )
}
```

## State Management

### Controlled Component

```kotlin
@Composable
fun ControlledSelectExample() {
    var selectedValue by remember { mutableStateOf<String?>(null) }
    var selectionHistory by remember { mutableStateOf(listOf<String>()) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Select(
            selectedValue = remember { mutableStateOf(selectedValue) }.apply { value = selectedValue },
            options = listOf("A", "B", "C").map { SelectOption(it, "Option $it") },
            onSelectedChange = { newValue ->
                selectedValue = newValue
                if (newValue != null) {
                    selectionHistory = selectionHistory + newValue
                }
            },
            label = "Controlled Select"
        )

        Button(
            text = "Clear Selection",
            onClick = { selectedValue = null }
        )

        if (selectionHistory.isNotEmpty()) {
            Text(
                "History: ${selectionHistory.joinToString(" → ")}",
                style = Typography.CAPTION
            )
        }
    }
}
```

## Platform Differences

### Browser (JS)

- Renders as HTML `<select>` element
- Native browser dropdown behavior
- CSS styling through modifiers
- Full keyboard and mouse support

### JVM

- Server-side rendering support
- Generates appropriate HTML for SSR
- Maintains state during form submissions
- Server-side validation support

## Performance Considerations

### Optimization Tips

1. **Large Option Lists**: Use virtualization for many options
2. **Search/Filter**: Implement client-side filtering for better performance
3. **Memoization**: Cache option transformations
4. **Lazy Loading**: Load options on demand

```kotlin
@Composable
fun OptimizedSelectExample() {
    // Memoize options to prevent recreation
    val options = remember {
        generateLargeOptionList().map { SelectOption(it.id, it.name) }
    }

    var selectedValue by remember { mutableStateOf<String?>(null) }

    // For very large lists, consider custom implementation with virtualization
    if (options.size > 100) {
        VirtualizedSelect(
            options = options,
            selectedValue = selectedValue,
            onSelectedChange = { selectedValue = it }
        )
    } else {
        Select(
            selectedValue = remember { mutableStateOf(selectedValue) }.apply { value = selectedValue },
            options = options,
            onSelectedChange = { selectedValue = it }
        )
    }
}
```

## Testing Strategies

### Unit Testing

```kotlin
class SelectTest {
    @Test
    fun `select updates value correctly`() {
        val options = listOf("A", "B", "C").map { SelectOption(it, it) }
        var selectedValue: String? = null

        composeTestRule.setContent {
            Select(
                selectedValue = remember { mutableStateOf(selectedValue) }.apply { value = selectedValue },
                options = options,
                onSelectedChange = { selectedValue = it }
            )
        }

        // Open dropdown and select option
        composeTestRule.onNode(hasContentDescription("select")).performClick()
        composeTestRule.onNodeWithText("B").performClick()

        assertEquals("B", selectedValue)
    }

    @Test
    fun `validation works correctly`() {
        val options = listOf("A", "B").map { SelectOption(it, it) }

        composeTestRule.setContent {
            Select(
                selectedValue = remember { mutableStateOf<String?>(null) },
                options = options,
                validators = listOf(requiredSelectValidator())
            )
        }

        // Check that validation error appears
        composeTestRule.onNodeWithText("This field is required").assertExists()
    }
}
```

## Migration Guide

### From HTML Forms

```html
<!-- Before: HTML -->
<select name="country">
    <option value="">Select country</option>
    <option value="us">United States</option>
    <option value="ca">Canada</option>
</select>
```

```kotlin
// After: Summon
@Composable
fun CountrySelect() {
    val countries = listOf(
        SelectOption("us", "United States"),
        SelectOption("ca", "Canada")
    )
    var selected by remember { mutableStateOf<String?>(null) }

    Select(
        selectedValue = remember { mutableStateOf(selected) }.apply { value = selected },
        options = countries,
        onSelectedChange = { selected = it },
        placeholder = "Select country"
    )
}
```

## Best Practices

### Do

- Provide clear, descriptive option labels
- Use placeholder text to guide users
- Group related options logically
- Implement search for large option lists
- Validate required selections

### Don't

- Create extremely long option lists without search
- Use select for binary choices (use Switch instead)
- Forget to handle empty/null states
- Mix different types of options in one select

## Related Components

- [TextField](TextField.md) - For text input
- [Checkbox](Checkbox.md) - For multiple selections
- [RadioButton](RadioButton.md) - For single selections
- [Form](Form.md) - For form integration