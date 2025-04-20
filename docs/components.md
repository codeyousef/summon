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

The `Column` component arranges its children vertically. By default, it applies the `fillMaxSize` modifier to fill the maximum available width and height.

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

Note that the `fillMaxSize` modifier is applied automatically, so the Column will fill its parent container by default. If you want to override this behavior, you can provide your own size modifiers.

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

## Accessibility Components

### AccessibleElement

The `AccessibleElement` component wraps content with accessibility attributes to improve screen reader support and overall accessibility.

```kotlin
AccessibleElement(
    role = AccessibilityUtils.NodeRole.BUTTON,
    label = "Close dialog",
    relations = mapOf("controls" to "main-dialog"),
    modifier = Modifier
        .padding(8.px)
        .backgroundColor("#0077cc")
        .color("#ffffff")
) {
    Text("Close")
}
```

### Semantic HTML Components

For better document structure and accessibility, Summon provides semantic HTML wrapper components:

```kotlin
Header(id = "site-header") {
    Heading(level = 1) {
        Text("Website Title")
    }
    Nav {
        Row {
            Link("Home", "/")
            Link("About", "/about")
            Link("Contact", "/contact")
        }
    }
}

Main {
    Section(id = "intro") {
        Heading(level = 2) {
            Text("Introduction")
        }
        Text("Welcome to our website...")
    }

    Article {
        Heading(level = 2) {
            Text("Latest News")
        }
        Text("News content goes here...")
    }
}

Footer {
    Text("© 2023 My Company")
}
```

## Custom Components

Creating custom components in Summon is straightforward using the `@Composable` annotation:

```kotlin
import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.extensions.px

@Composable
fun CustomCard(
    title: String,
    content: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
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
                text = buttonText,
                onClick = onButtonClick
            )
        }
    }
}

// Usage
@Composable
fun MyPage() {
    CustomCard(
        title = "Welcome",
        content = "This is a custom component example.",
        buttonText = "Learn More",
        onButtonClick = { /* Handle click */ }
    )
}
```

## Component Composition

Components can be composed together to build more complex UIs:

```kotlin
@Composable
fun UserProfile(user: User) {
    Column(
        modifier = Modifier
            .padding(16.px)
            .gap(16.px)
    ) {
        Card {
            Column(modifier = Modifier.padding(16.px).gap(8.px)) {
                Text(
                    text = user.name,
                    modifier = Modifier.fontSize(24.px).fontWeight(700)
                )
                Text(user.email)
                Text("Member since: ${user.joinDate}")
            }
        }

        Card {
            Column(modifier = Modifier.padding(16.px).gap(8.px)) {
                Text(
                    text = "Recent Activity",
                    modifier = Modifier.fontSize(18.px).fontWeight(700)
                )

                user.activities.forEach { activity ->
                    Row(
                        modifier = Modifier
                            .padding(8.px)
                            .borderBottom(1.px, "#eeeeee")
                    ) {
                        Text(activity.date)
                        Spacer(width = 16.px)
                        Text(activity.description)
                    }
                }
            }
        }
    }
}

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
