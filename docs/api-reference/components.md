# Components API Reference

This document provides detailed information about all built-in components available in the Summon library.

## Table of Contents

- [Layout Components](#layout-components)
  - [Box](#box)
  - [Column](#column)
  - [Row](#row)
  - [Grid](#grid)
  - [Spacer](#spacer)
  - [Card](#card)
  - [Divider](#divider)
  - [AspectRatio](#aspectratio)
  - [ExpansionPanel](#expansionpanel)
  - [LazyColumn](#lazycolumn)
  - [LazyRow](#lazyrow)
  - [ResponsiveLayout](#responsivelayout)
- [Display Components](#display-components)
  - [Text](#text)
  - [Image](#image)
  - [Icon](#icon)
  - [Badge](#badge)
- [Input Components](#input-components)
  - [Button](#button)
  - [TextField](#textfield)
  - [TextArea](#textarea)
  - [Checkbox](#checkbox)
  - [RadioButton](#radiobutton)
  - [Switch](#switch)
  - [Select](#select)
  - [Slider](#slider)
  - [RangeSlider](#rangeslider)
  - [DatePicker](#datepicker)
  - [TimePicker](#timepicker)
  - [FileUpload](#fileupload)
  - [Form](#form)
  - [FormField](#formfield)
- [Navigation Components](#navigation-components)
  - [Link](#link)
  - [TabLayout](#tablayout)
- [Feedback Components](#feedback-components)
  - [Alert](#alert)
  - [Snackbar](#snackbar)
  - [SnackbarHost](#snackbarhost)
  - [Progress](#progress)
  - [ProgressBar](#progressbar)
  - [Tooltip](#tooltip)
- [Accessibility Components](#accessibility-components)
  - [AccessibleElement](#accessibleelement)
  - [Semantic HTML Components](#semantic-html-components)
- [Utility Components](#utility-components)
  - [Div](#div)

---

## Layout Components

Layout components help you structure your UI by organizing other components in a specific layout.

### Box

A container component with flexible positioning capabilities for its children.

#### Definition

```kotlin
@Composable
fun Box(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers |
| content | @Composable () -> Unit | The content to be rendered inside the box |

#### Example

```kotlin
Box(
    modifier = Modifier
        .size(200.px)
        .backgroundColor("#f0f0f0")
        .position(Position.Relative)
) {
    // Positioned children can use absolute positioning
    Text(
        "Top Right",
        modifier = Modifier
            .position(Position.Absolute)
            .top(8.px)
            .right(8.px)
    )
}
```

### Column

A layout component that arranges its children vertically. By default, it applies `fillMaxSize()` modifier.

#### Definition

```kotlin
@Composable
fun Column(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers (fillMaxSize is applied by default) |
| content | @Composable () -> Unit | The content to be arranged vertically |

#### Example

```kotlin
Column(
    modifier = Modifier
        .padding(16.px)
        .gap(8.px)
) {
    Text("First item")
    Text("Second item")
    Text("Third item")
}
```

### Row

A layout component that arranges its children horizontally.

#### Definition

```kotlin
@Composable
fun Row(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers |
| content | @Composable () -> Unit | The content to be arranged horizontally |

#### Example

```kotlin
Row(
    modifier = Modifier
        .padding(16.px)
        .gap(8.px)
        .alignItems(AlignItems.Center)
) {
    Icon("✓")
    Text("Completed")
}
```

### Grid

A layout component that arranges its children in a CSS grid.

#### Definition

```kotlin
@Composable
fun Grid(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers (use grid modifiers) |
| content | @Composable () -> Unit | The content to be arranged in a grid |

#### Example

```kotlin
Grid(
    modifier = Modifier
        .gridTemplateColumns("1fr 1fr 1fr")
        .gridGap(16.px)
        .padding(16.px)
) {
    repeat(9) { index ->
        Card {
            Text("Item ${index + 1}")
        }
    }
}
```

### Spacer

A component that creates flexible space between other components.

#### Definition

```kotlin
@Composable
fun Spacer(
    modifier: Modifier = Modifier
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Modifiers to control the spacer size |

#### Example

```kotlin
Row {
    Text("Start")
    Spacer(modifier = Modifier.width(16.px))
    Text("Middle")
    Spacer(modifier = Modifier.weight(1f)) // Flexible spacer
    Text("End")
}
```

### Card

A container component with elevation and default styling.

#### Definition

```kotlin
@Composable
fun Card(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers |
| content | @Composable () -> Unit | The content to be rendered inside the card |

#### Example

```kotlin
Card(
    modifier = Modifier
        .padding(16.px)
        .width(300.px)
        .boxShadow("0 2px 4px rgba(0,0,0,0.1)")
) {
    Column(modifier = Modifier.padding(16.px)) {
        Text("Card Title", modifier = Modifier.fontSize(20.px))
        Text("Card content goes here")
    }
}
```

### Divider

A component for creating visual separators.

#### Definition

```kotlin
@Composable
fun Divider(
    modifier: Modifier = Modifier
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Modifiers to style the divider |

#### Example

```kotlin
Column {
    Text("Section 1")
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.px)
            .backgroundColor("#e0e0e0")
    )
    Text("Section 2")
}
```

### AspectRatio

A component that maintains a specific aspect ratio for its content.

#### Definition

```kotlin
@Composable
fun AspectRatio(
    ratio: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| ratio | Float | The width/height ratio to maintain |
| modifier | Modifier | Optional styling and layout modifiers |
| content | @Composable () -> Unit | The content to be rendered with aspect ratio |

#### Example

```kotlin
AspectRatio(
    ratio = 16f / 9f,
    modifier = Modifier.fillMaxWidth()
) {
    Image(src = "/video-thumbnail.jpg", alt = "Video")
}
```

### ExpansionPanel

A collapsible panel component.

#### Definition

```kotlin
@Composable
fun ExpansionPanel(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| expanded | Boolean | Current expansion state |
| onExpandedChange | (Boolean) -> Unit | Callback when expansion state changes |
| header | @Composable () -> Unit | The always-visible header content |
| modifier | Modifier | Optional styling and layout modifiers |
| content | @Composable () -> Unit | The collapsible content |

#### Example

```kotlin
var expanded by remember { mutableStateOf(false) }

ExpansionPanel(
    expanded = expanded,
    onExpandedChange = { expanded = it },
    header = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.px)
        ) {
            Text("Click to expand")
            Spacer(modifier = Modifier.weight(1f))
            Icon(if (expanded) "▼" else "▶")
        }
    }
) {
    Text("Expanded content", modifier = Modifier.padding(16.px))
}
```

### LazyColumn

A vertically scrolling list that only renders visible items.

#### Definition

```kotlin
@Composable
fun LazyColumn(
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers |
| content | LazyListScope.() -> Unit | DSL for defining list items |

#### Example

```kotlin
LazyColumn(
    modifier = Modifier
        .fillMaxWidth()
        .height(400.px)
) {
    item {
        Text("Header", modifier = Modifier.padding(16.px))
    }
    
    items(100) { index ->
        Card(modifier = Modifier.padding(8.px)) {
            Text("Item $index")
        }
    }
    
    item {
        Text("Footer", modifier = Modifier.padding(16.px))
    }
}
```

### LazyRow

A horizontally scrolling list that only renders visible items.

#### Definition

```kotlin
@Composable
fun LazyRow(
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers |
| content | LazyListScope.() -> Unit | DSL for defining list items |

#### Example

```kotlin
LazyRow(
    modifier = Modifier
        .fillMaxWidth()
        .height(200.px)
) {
    items(20) { index ->
        Card(
            modifier = Modifier
                .width(150.px)
                .height(150.px)
                .padding(8.px)
        ) {
            Text("Card $index")
        }
    }
}
```

### ResponsiveLayout

A component that adapts its layout based on screen size.

#### Definition

```kotlin
@Composable
fun ResponsiveLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)
```

---

## Display Components

### Text

A component for displaying text content.

#### Definition

```kotlin
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| text | String | The text to display |
| modifier | Modifier | Optional styling and layout modifiers |

#### Example

```kotlin
Text(
    text = "Hello, World!",
    modifier = Modifier
        .fontSize(24.px)
        .fontWeight(700)
        .color("#333333")
)
```

### Image

A component for displaying images.

#### Definition

```kotlin
@Composable
fun Image(
    src: String,
    alt: String,
    modifier: Modifier = Modifier
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| src | String | The URL or path to the image |
| alt | String | Alternative text for accessibility |
| modifier | Modifier | Optional styling and layout modifiers |

#### Example

```kotlin
Image(
    src = "/logo.png",
    alt = "Company logo",
    modifier = Modifier
        .width(200.px)
        .height(100.px)
        .objectFit(ObjectFit.Cover)
)
```

### Icon

A component for displaying icons from text or images.

#### Definition

```kotlin
@Composable
fun Icon(
    content: String? = null,
    src: String? = null,
    alt: String = "",
    modifier: Modifier = Modifier
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| content | String? | Text content for the icon (e.g., emoji) |
| src | String? | Image source for the icon |
| alt | String | Alternative text for image icons |
| modifier | Modifier | Optional styling and layout modifiers |

#### Example

```kotlin
// Text icon
Icon(
    content = "✓",
    modifier = Modifier
        .size(24.px)
        .color("#00cc00")
)

// Image icon
Icon(
    src = "/icons/settings.svg",
    alt = "Settings",
    modifier = Modifier.size(24.px)
)
```

### Badge

A component for displaying small status indicators.

#### Definition

```kotlin
@Composable
fun Badge(
    text: String,
    modifier: Modifier = Modifier
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| text | String | The text to display in the badge |
| modifier | Modifier | Optional styling and layout modifiers |

#### Example

```kotlin
Badge(
    text = "New",
    modifier = Modifier
        .backgroundColor("#ff4444")
        .color("#ffffff")
        .padding(4.px, 8.px)
        .borderRadius(12.px)
)
```

---

## Input Components

### Button

A clickable button component.

#### Definition

```kotlin
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
)
```

#### Alternative constructor for text-only buttons:

```kotlin
@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| onClick | () -> Unit | Callback when button is clicked |
| modifier | Modifier | Optional styling and layout modifiers |
| enabled | Boolean | Whether the button is enabled |
| content | @Composable () -> Unit | The button content |
| text | String | Text to display (alternative constructor) |

#### Example

```kotlin
Button(
    onClick = { println("Clicked!") },
    modifier = Modifier
        .padding(8.px, 16.px)
        .backgroundColor("#0077cc")
        .color("#ffffff")
        .borderRadius(4.px)
) {
    Row(modifier = Modifier.gap(8.px)) {
        Icon("✓")
        Text("Submit")
    }
}
```

### TextField

A single-line text input component.

#### Definition

```kotlin
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    type: String = "text",
    enabled: Boolean = true,
    error: String? = null
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| value | String | Current text value |
| onValueChange | (String) -> Unit | Callback when text changes |
| modifier | Modifier | Optional styling and layout modifiers |
| placeholder | String | Placeholder text |
| type | String | Input type (text, email, password, etc.) |
| enabled | Boolean | Whether the field is enabled |
| error | String? | Error message to display |

#### Example

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    placeholder = "Enter your name",
    modifier = Modifier
        .width(300.px)
        .padding(8.px)
)
```

### TextArea

A multi-line text input component.

#### Definition

```kotlin
@Composable
fun TextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    rows: Int = 4,
    enabled: Boolean = true
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| value | String | Current text value |
| onValueChange | (String) -> Unit | Callback when text changes |
| modifier | Modifier | Optional styling and layout modifiers |
| placeholder | String | Placeholder text |
| rows | Int | Number of visible text rows |
| enabled | Boolean | Whether the field is enabled |

### Checkbox

A checkbox input component.

#### Definition

```kotlin
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| checked | Boolean | Current checked state |
| onCheckedChange | (Boolean) -> Unit | Callback when state changes |
| modifier | Modifier | Optional styling and layout modifiers |
| label | String? | Optional label text |
| enabled | Boolean | Whether the checkbox is enabled |

### RadioButton

A radio button for single selection from multiple options.

#### Definition

```kotlin
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| selected | Boolean | Current selection state |
| onClick | () -> Unit | Callback when clicked |
| modifier | Modifier | Optional styling and layout modifiers |
| label | String? | Optional label text |
| enabled | Boolean | Whether the radio button is enabled |

### Switch

A toggle switch component.

#### Definition

```kotlin
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| checked | Boolean | Current toggle state |
| onCheckedChange | (Boolean) -> Unit | Callback when state changes |
| modifier | Modifier | Optional styling and layout modifiers |
| label | String? | Optional label text |
| enabled | Boolean | Whether the switch is enabled |

### Select

A dropdown selection component.

#### Definition

```kotlin
@Composable
fun Select(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<SelectOption>,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true
)

data class SelectOption(
    val value: String,
    val label: String
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| value | String | Currently selected value |
| onValueChange | (String) -> Unit | Callback when selection changes |
| options | List<SelectOption> | Available options |
| modifier | Modifier | Optional styling and layout modifiers |
| placeholder | String | Placeholder text |
| enabled | Boolean | Whether the select is enabled |

### Slider

A single-value slider component.

#### Definition

```kotlin
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    min: Float = 0f,
    max: Float = 100f,
    step: Float = 1f,
    enabled: Boolean = true
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| value | Float | Current slider value |
| onValueChange | (Float) -> Unit | Callback when value changes |
| modifier | Modifier | Optional styling and layout modifiers |
| min | Float | Minimum value |
| max | Float | Maximum value |
| step | Float | Step increment |
| enabled | Boolean | Whether the slider is enabled |

### RangeSlider

A dual-handle range selection slider.

#### Definition

```kotlin
@Composable
fun RangeSlider(
    value: Pair<Float, Float>,
    onValueChange: (Pair<Float, Float>) -> Unit,
    modifier: Modifier = Modifier,
    min: Float = 0f,
    max: Float = 100f,
    step: Float = 1f,
    enabled: Boolean = true
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| value | Pair<Float, Float> | Current range (start, end) |
| onValueChange | (Pair<Float, Float>) -> Unit | Callback when range changes |
| modifier | Modifier | Optional styling and layout modifiers |
| min | Float | Minimum value |
| max | Float | Maximum value |
| step | Float | Step increment |
| enabled | Boolean | Whether the slider is enabled |

### DatePicker

A date selection component.

#### Definition

```kotlin
@Composable
fun DatePicker(
    value: LocalDate?,
    onValueChange: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| value | LocalDate? | Currently selected date |
| onValueChange | (LocalDate?) -> Unit | Callback when date changes |
| modifier | Modifier | Optional styling and layout modifiers |
| placeholder | String | Placeholder text |
| enabled | Boolean | Whether the picker is enabled |

### TimePicker

A time selection component.

#### Definition

```kotlin
@Composable
fun TimePicker(
    value: LocalTime?,
    onValueChange: (LocalTime?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| value | LocalTime? | Currently selected time |
| onValueChange | (LocalTime?) -> Unit | Callback when time changes |
| modifier | Modifier | Optional styling and layout modifiers |
| placeholder | String | Placeholder text |
| enabled | Boolean | Whether the picker is enabled |

### FileUpload

A file upload component with drag & drop support.

#### Definition

```kotlin
@Composable
fun FileUpload(
    onFilesSelected: (List<FileInfo>) -> Unit,
    modifier: Modifier = Modifier,
    accept: String? = null,
    multiple: Boolean = false,
    content: @Composable () -> Unit
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| onFilesSelected | (List<FileInfo>) -> Unit | Callback when files are selected |
| modifier | Modifier | Optional styling and layout modifiers |
| accept | String? | Accepted file types (e.g., "image/*") |
| multiple | Boolean | Whether multiple files can be selected |
| content | @Composable () -> Unit | Custom content for the upload area |

### Form

A form container that manages form state and validation.

#### Definition

```kotlin
@Composable
fun Form(
    state: FormState,
    onSubmit: (Map<String, String>) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable FormScope.() -> Unit
)
```

### FormField

A form field wrapper that integrates with Form component.

#### Definition

```kotlin
@Composable
fun FormScope.FormField(
    name: String,
    validator: Validator? = null,
    content: @Composable (FormFieldState) -> Unit
)
```

---

## Navigation Components

### Link

A navigation link component.

#### Definition

```kotlin
@Composable
fun Link(
    text: String,
    href: String,
    modifier: Modifier = Modifier,
    target: String = "_self"
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| text | String | Link text |
| href | String | Target URL |
| modifier | Modifier | Optional styling and layout modifiers |
| target | String | Link target (_self, _blank, etc.) |

### TabLayout

A tab-based navigation component.

#### Definition

```kotlin
@Composable
fun TabLayout(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>,
    modifier: Modifier = Modifier
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| selectedIndex | Int | Currently selected tab index |
| onTabSelected | (Int) -> Unit | Callback when tab is selected |
| tabs | List<String> | Tab labels |
| modifier | Modifier | Optional styling and layout modifiers |

---

## Feedback Components

### Alert

A component for displaying alert messages.

#### Definition

```kotlin
@Composable
fun Alert(
    message: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    type: AlertType = AlertType.Info,
    onClose: (() -> Unit)? = null
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| message | String | Alert message |
| modifier | Modifier | Optional styling and layout modifiers |
| title | String? | Optional alert title |
| type | AlertType | Alert type (Info, Success, Warning, Error) |
| onClose | (() -> Unit)? | Optional close callback |

### Snackbar

A temporary notification component.

#### Definition

```kotlin
@Composable
fun Snackbar(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: () -> Unit
)
```

### SnackbarHost

A host component for managing snackbar display.

#### Definition

```kotlin
@Composable
fun SnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
)
```

### Progress

An indeterminate progress indicator.

#### Definition

```kotlin
@Composable
fun Progress(
    modifier: Modifier = Modifier
)
```

### ProgressBar

A determinate progress bar.

#### Definition

```kotlin
@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| progress | Float | Progress value (0.0 to 1.0) |
| modifier | Modifier | Optional styling and layout modifiers |

### Tooltip

A hover tooltip component.

#### Definition

```kotlin
@Composable
fun Tooltip(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    tooltip: @Composable () -> Unit
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| content | @Composable () -> Unit | The content that triggers the tooltip |
| modifier | Modifier | Optional styling and layout modifiers |
| tooltip | @Composable () -> Unit | The tooltip content |

---

## Accessibility Components

### AccessibleElement

A wrapper that adds accessibility attributes to content.

#### Definition

```kotlin
@Composable
fun AccessibleElement(
    content: @Composable () -> Unit,
    role: AccessibilityUtils.NodeRole? = null,
    customRole: String? = null,
    label: String? = null,
    relations: Map<String, String> = emptyMap(),
    modifier: Modifier = Modifier
)
```

### Semantic HTML Components

Components that render semantic HTML elements.

```kotlin
@Composable fun Header(...)
@Composable fun Main(...)
@Composable fun Nav(...)
@Composable fun Article(...)
@Composable fun Section(...)
@Composable fun Aside(...)
@Composable fun Footer(...)
@Composable fun Heading(level: Int, ...)
```

---

## Utility Components

### Div

A basic container element.

#### Definition

```kotlin
@Composable
fun Div(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers |
| content | @Composable () -> Unit | The content to be rendered |