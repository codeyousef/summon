# Components

Summon provides a rich set of built-in components for creating consistent user interfaces across platforms. This guide covers the core components and how to use them effectively.

## Core Components

### Text

The `Text` component is used to display text content.

```kotlin
Text(
    text = "Hello, World!",
    modifier = Modifier
        .fontSize(16.px)
        .fontWeight(500)
        .color("#333333")
)
```

### Button

The `Button` component creates clickable buttons with various styles.

```kotlin
Button(
    text = "Submit",
    onClick = { 
        // Handle click
    },
    modifier = Modifier
        .padding(8.px, 16.px)
        .backgroundColor("#0077cc")
        .color("#ffffff")
        .borderRadius(4.px)
)
```

### TextField

The `TextField` component creates input fields for text entry.

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    placeholder = "Enter your name",
    modifier = Modifier
        .width(200.px)
        .padding(8.px)
        .border(1.px, "#cccccc")
        .borderRadius(4.px)
)
```

### Checkbox

The `Checkbox` component creates selectable checkboxes.

```kotlin
var checked by remember { mutableStateOf(false) }

Checkbox(
    checked = checked,
    onCheckedChange = { checked = it },
    label = "Accept terms and conditions",
    modifier = Modifier.padding(8.px)
)
```

### Link

The `Link` component creates hyperlinks.

```kotlin
Link(
    text = "Visit our website",
    href = "https://example.com",
    target = "_blank",
    modifier = Modifier
        .color("#0077cc")
        .textDecoration(TextDecoration.Underline)
)
```

## Layout Components

### Row

The `Row` component arranges its children horizontally.

```kotlin
Row(
    modifier = Modifier
        .padding(16.px)
        .gap(8.px)
) {
    Text("Item 1")
    Text("Item 2")
    Text("Item 3")
}
```

### Column

The `Column` component arranges its children vertically.

```kotlin
Column(
    modifier = Modifier
        .padding(16.px)
        .gap(8.px)
) {
    Text("Item 1")
    Text("Item 2")
    Text("Item 3")
}
```

### Grid

The `Grid` component creates a CSS grid layout.

```kotlin
Grid(
    modifier = Modifier
        .gridTemplateColumns("1fr 1fr 1fr")
        .gap(16.px)
) {
    repeat(9) { index ->
        Div(
            modifier = Modifier
                .padding(16.px)
                .backgroundColor("#f0f0f0")
                .borderRadius(4.px)
        ) {
            Text("Grid Item ${index + 1}")
        }
    }
}
```

### Card

The `Card` component creates a container with a shadow and border.

```kotlin
Card(
    modifier = Modifier
        .padding(16.px)
        .boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
        .borderRadius(8.px)
) {
    Column(
        modifier = Modifier
            .padding(16.px)
            .gap(8.px)
    ) {
        Text(
            text = "Card Title",
            modifier = Modifier
                .fontSize(18.px)
                .fontWeight(700)
        )
        Text("Card content goes here")
        Button(text = "Learn More")
    }
}
```

## Feedback Components

### Alert

The `Alert` component displays important messages to users.

```kotlin
Alert(
    title = "Success",
    message = "Your changes have been saved.",
    type = AlertType.Success,
    onClose = { /* Handle close */ }
)
```

### Badge

The `Badge` component displays a small status indicator.

```kotlin
Badge(
    text = "New",
    modifier = Modifier
        .backgroundColor("#ff0000")
        .color("#ffffff")
        .padding(4.px, 8.px)
        .borderRadius(12.px)
        .fontSize(12.px)
)
```

### Tooltip

The `Tooltip` component displays additional information on hover.

```kotlin
Tooltip(
    text = "This is a tooltip",
    position = TooltipPosition.Top
) {
    Button(text = "Hover me")
}
```

## Form Components

### Form

The `Form` component creates a form container.

```kotlin
Form(
    onSubmit = { event ->
        event.preventDefault()
        // Handle form submission
    }
) {
    Column(
        modifier = Modifier
            .padding(16.px)
            .gap(16.px)
    ) {
        // Form fields
        Button(text = "Submit")
    }
}
```

### TextArea

The `TextArea` component creates a multi-line text input.

```kotlin
var text by remember { mutableStateOf("") }

TextArea(
    value = text,
    onValueChange = { text = it },
    placeholder = "Enter your message",
    rows = 5,
    modifier = Modifier
        .width(100.percent)
        .padding(8.px)
        .border(1.px, "#cccccc")
        .borderRadius(4.px)
)
```

## Creating Custom Components

You can create custom components by implementing the `Composable` interface:

```kotlin
class CustomCard(
    private val title: String,
    private val content: String,
    private val onAction: () -> Unit
) : Composable {
    override fun render() {
        Card(
            modifier = Modifier
                .padding(16.px)
                .boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
                .borderRadius(8.px)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.px)
                    .gap(8.px)
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .fontSize(18.px)
                        .fontWeight(700)
                )
                Text(content)
                Button(
                    text = "Action",
                    onClick = onAction
                )
            }
        }
    }
}

// Usage
CustomCard(
    title = "My Card",
    content = "This is a custom card component",
    onAction = { println("Action clicked!") }
)
```

## Platform-Specific Extensions

Summon provides platform-specific extensions for components to enable platform-specific functionality:

### JVM Extensions

```kotlin
// JVM-specific extension for Button
Button(
    text = "Click me",
    onClick = { /* Handle click */ }
).withJvmAccessKey('C') // Adds access key for desktop applications
```

### JS Extensions

```kotlin
// JS-specific extension for TextField
TextField(
    value = text,
    onValueChange = { text = it }
).withJsAutoFocus() // Automatically focuses the field when rendered
``` 