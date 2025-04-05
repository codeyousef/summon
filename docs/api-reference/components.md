# Components API Reference

This document provides detailed information about the built-in components available in the Summon library.

## Table of Contents

- [Layout Components](#layout-components)
  - [Box](#box)
  - [Column](#column)
  - [Row](#row)
  - [Grid](#grid)
  - [Spacer](#spacer)
  - [Card](#card)
- [Display Components](#display-components)
  - [Text](#text)
  - [Image](#image)
  - [Icon](#icon)
  - [Badge](#badge)
  - [Divider](#divider)
- [Input Components](#input-components)
  - [Button](#button)
  - [TextField](#textfield)
  - [TextArea](#textarea)
  - [Checkbox](#checkbox)
  - [RadioButton](#radiobutton)
  - [Select](#select)
  - [Slider](#slider)
- [Navigation Components](#navigation-components)
  - [Link](#link)
  - [NavBar](#navbar)
  - [TabBar](#tabbar)
  - [Pagination](#pagination)
- [Feedback Components](#feedback-components)
  - [Alert](#alert)
  - [ProgressBar](#progressbar)
  - [Spinner](#spinner)
  - [Toast](#toast)
  - [Tooltip](#tooltip)

---

## Layout Components

Layout components help you structure your UI by organizing other components in a specific layout.

### Box

A basic container component that can hold other components.

#### Definition

```kotlin
fun Box(
    modifier: Modifier = Modifier,
    content: @Composable CompositionScope.() -> Unit
): Composable
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers |
| content | @Composable CompositionScope.() -> Unit | The content to be rendered inside the box |

#### Example

```kotlin
Box(
    modifier = Modifier
        .padding(16.px)
        .backgroundColor("#f0f0f0")
        .borderRadius(8.px)
) {
    Text("Content inside a box")
}
```

### Column

A layout component that arranges its children vertically.

#### Definition

```kotlin
fun Column(
    modifier: Modifier = Modifier,
    content: @Composable CompositionScope.() -> Unit
): Composable
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers |
| content | @Composable CompositionScope.() -> Unit | The content to be arranged vertically |

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
fun Row(
    modifier: Modifier = Modifier,
    content: @Composable CompositionScope.() -> Unit
): Composable
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers |
| content | @Composable CompositionScope.() -> Unit | The content to be arranged horizontally |

#### Example

```kotlin
Row(
    modifier = Modifier
        .padding(16.px)
        .gap(8.px)
) {
    Button(text = "Button 1")
    Button(text = "Button 2")
    Button(text = "Button 3")
}
```

### Grid

A layout component that arranges its children in a grid.

#### Definition

```kotlin
fun Grid(
    modifier: Modifier = Modifier,
    content: @Composable CompositionScope.() -> Unit
): Composable
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers |
| content | @Composable CompositionScope.() -> Unit | The content to be arranged in a grid |

#### Example

```kotlin
Grid(
    modifier = Modifier
        .gridTemplateColumns("1fr 1fr 1fr")
        .gap(16.px)
        .padding(16.px)
) {
    repeat(9) { index ->
        Box(
            modifier = Modifier
                .backgroundColor("#f0f0f0")
                .padding(16.px)
                .textAlign(TextAlign.Center)
        ) {
            Text("Item ${index + 1}")
        }
    }
}
```

### Spacer

A component that adds space between other components.

#### Definition

```kotlin
fun Spacer(
    width: CSSSize? = null,
    height: CSSSize? = null
): Composable
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| width | CSSSize? | Optional width of the spacer (default: null) |
| height | CSSSize? | Optional height of the spacer (default: null) |

#### Example

```kotlin
Column {
    Text("Text above spacer")
    Spacer(height = 16.px)
    Text("Text below spacer")
}

Row {
    Text("Text before spacer")
    Spacer(width = 16.px)
    Text("Text after spacer")
}
```

### Card

A container component with predefined styling for a card-like appearance.

#### Definition

```kotlin
fun Card(
    modifier: Modifier = Modifier,
    content: @Composable CompositionScope.() -> Unit
): Composable
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers |
| content | @Composable CompositionScope.() -> Unit | The content to be rendered inside the card |

#### Example

```kotlin
Card(
    modifier = Modifier
        .padding(16.px)
        .width(300.px)
) {
    Column(
        modifier = Modifier
            .padding(16.px)
            .gap(8.px)
    ) {
        Text(
            text = "Card Title",
            modifier = Modifier
                .fontSize(20.px)
                .fontWeight(700)
        )
        Text("This is the content of the card.")
        Button(text = "Action")
    }
}
```

---

## Display Components

Display components are used to present different types of content.

### Text

A component for displaying text.

#### Definition

```kotlin
fun Text(
    text: String,
    modifier: Modifier = Modifier
): Composable
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
fun Image(
    src: String,
    alt: String = "",
    modifier: Modifier = Modifier
): Composable
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| src | String | The URL of the image |
| alt | String | Alternative text for the image (default: "") |
| modifier | Modifier | Optional styling and layout modifiers |

#### Example

```kotlin
Image(
    src = "https://example.com/image.jpg",
    alt = "Example image",
    modifier = Modifier
        .width(300.px)
        .height(200.px)
        .objectFit(ObjectFit.Cover)
        .borderRadius(8.px)
)
```

### Icon

A component for displaying icons.

#### Definition

```kotlin
fun Icon(
    name: String,
    size: CSSSize = 24.px,
    modifier: Modifier = Modifier
): Composable
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| name | String | The name of the icon |
| size | CSSSize | The size of the icon (default: 24.px) |
| modifier | Modifier | Optional styling and layout modifiers |

#### Example

```kotlin
Icon(
    name = "star",
    size = 32.px,
    modifier = Modifier.color("#FFD700")
)
```

### Badge

A component for displaying small status indicators.

#### Definition

```kotlin
fun Badge(
    text: String,
    modifier: Modifier = Modifier
): Composable
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
        .backgroundColor("#f44336")
        .color("#ffffff")
        .padding(4.px, 8.px)
        .borderRadius(12.px)
        .fontSize(12.px)
)
```

### Divider

A component for creating a horizontal or vertical dividing line.

#### Definition

```kotlin
fun Divider(
    vertical: Boolean = false,
    modifier: Modifier = Modifier
): Composable
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| vertical | Boolean | Whether the divider should be vertical (default: false) |
| modifier | Modifier | Optional styling and layout modifiers |

#### Example

```kotlin
// Horizontal divider
Divider(
    modifier = Modifier
        .margin(16.px, 0.px)
        .width(100.percent)
        .backgroundColor("#e0e0e0")
)

// Vertical divider
Divider(
    vertical = true,
    modifier = Modifier
        .margin(0.px, 16.px)
        .height(100.percent)
        .backgroundColor("#e0e0e0")
)
```

---

## Input Components

Input components allow users to interact with your application.

### Button

A clickable button component.

#### Definition

```kotlin
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    disabled: Boolean = false
): Composable
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| text | String | The text to display on the button |
| onClick | () -> Unit | The action to perform when the button is clicked |
| modifier | Modifier | Optional styling and layout modifiers |
| disabled | Boolean | Whether the button should be disabled (default: false) |

#### Example

```kotlin
Button(
    text = "Click me",
    onClick = { println("Button clicked!") },
    modifier = Modifier
        .padding(8.px, 16.px)
        .backgroundColor("#0077cc")
        .color("#ffffff")
        .borderRadius(4.px)
        .hover {
            backgroundColor("#005599")
        }
)
```

### TextField

A component for text input.

#### Definition

```kotlin
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    type: InputType = InputType.Text,
    disabled: Boolean = false
): Composable
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| value | String | The current value of the text field |
| onValueChange | (String) -> Unit | Callback that is called when the value changes |
| placeholder | String | The placeholder text to display when the field is empty (default: "") |
| modifier | Modifier | Optional styling and layout modifiers |
| type | InputType | The type of input (default: InputType.Text) |
| disabled | Boolean | Whether the text field should be disabled (default: false) |

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
        .border(1.px, "#cccccc")
        .borderRadius(4.px)
        .focus {
            borderColor("#0077cc")
        }
)
```

### TextArea

A component for multi-line text input.

#### Definition

```kotlin
fun TextArea(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    rows: Int = 4,
    modifier: Modifier = Modifier,
    disabled: Boolean = false
): Composable
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| value | String | The current value of the text area |
| onValueChange | (String) -> Unit | Callback that is called when the value changes |
| placeholder | String | The placeholder text to display when the field is empty (default: "") |
| rows | Int | The number of visible rows (default: 4) |
| modifier | Modifier | Optional styling and layout modifiers |
| disabled | Boolean | Whether the text area should be disabled (default: false) |

#### Example

```kotlin
var text by remember { mutableStateOf("") }

TextArea(
    value = text,
    onValueChange = { text = it },
    placeholder = "Enter your message",
    rows = 5,
    modifier = Modifier
        .width(300.px)
        .padding(8.px)
        .border(1.px, "#cccccc")
        .borderRadius(4.px)
)
```

### Checkbox

A component for boolean input.

#### Definition

```kotlin
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String = "",
    modifier: Modifier = Modifier,
    disabled: Boolean = false
): Composable
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| checked | Boolean | The current state of the checkbox |
| onCheckedChange | (Boolean) -> Unit | Callback that is called when the state changes |
| label | String | The label text to display next to the checkbox (default: "") |
| modifier | Modifier | Optional styling and layout modifiers |
| disabled | Boolean | Whether the checkbox should be disabled (default: false) |

#### Example

```kotlin
var checked by remember { mutableStateOf(false) }

Checkbox(
    checked = checked,
    onCheckedChange = { checked = it },
    label = "Accept terms and conditions",
    modifier = Modifier.padding(8.px)
)
```

## Navigation Components

### Link

A component for creating hyperlinks.

#### Definition

```kotlin
fun Link(
    text: String,
    href: String,
    target: String = "_self",
    modifier: Modifier = Modifier
): Composable
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| text | String | The text to display |
| href | String | The URL to link to |
| target | String | The target attribute for the link (default: "_self") |
| modifier | Modifier | Optional styling and layout modifiers |

#### Example

```kotlin
Link(
    text = "Visit our website",
    href = "https://example.com",
    target = "_blank",
    modifier = Modifier
        .color("#0077cc")
        .textDecoration(TextDecoration.None)
        .hover {
            textDecoration(TextDecoration.Underline)
        }
)
``` 