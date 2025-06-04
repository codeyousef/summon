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

```

## Additional Input Components

### TextArea

The `TextArea` component creates multi-line text input fields.

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

### RadioButton

The `RadioButton` component creates radio buttons for single selection.

```kotlin
var selectedOption by remember { mutableStateOf("option1") }

Column(modifier = Modifier.gap(8.px)) {
    RadioButton(
        selected = selectedOption == "option1",
        onClick = { selectedOption = "option1" },
        label = "Option 1"
    )
    RadioButton(
        selected = selectedOption == "option2",
        onClick = { selectedOption = "option2" },
        label = "Option 2"
    )
}
```

### Switch

The `Switch` component creates toggle switches.

```kotlin
var enabled by remember { mutableStateOf(false) }

Switch(
    checked = enabled,
    onCheckedChange = { enabled = it },
    label = "Enable notifications",
    modifier = Modifier.padding(8.px)
)
```

### Select

The `Select` component creates dropdown selection fields.

```kotlin
var selectedValue by remember { mutableStateOf("") }

Select(
    value = selectedValue,
    onValueChange = { selectedValue = it },
    options = listOf(
        SelectOption("small", "Small"),
        SelectOption("medium", "Medium"),
        SelectOption("large", "Large")
    ),
    placeholder = "Choose size",
    modifier = Modifier.width(200.px)
)
```

### Slider

The `Slider` component creates value selection sliders.

```kotlin
var value by remember { mutableStateOf(50f) }

Slider(
    value = value,
    onValueChange = { value = it },
    min = 0f,
    max = 100f,
    step = 1f,
    modifier = Modifier.width(300.px)
)
```

### RangeSlider

The `RangeSlider` component creates dual-handle range selection sliders.

```kotlin
var range by remember { mutableStateOf(20f to 80f) }

RangeSlider(
    value = range,
    onValueChange = { range = it },
    min = 0f,
    max = 100f,
    step = 1f,
    modifier = Modifier.width(300.px)
)
```

### DatePicker

The `DatePicker` component creates date selection fields.

```kotlin
var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

DatePicker(
    value = selectedDate,
    onValueChange = { selectedDate = it },
    placeholder = "Select date",
    modifier = Modifier.width(200.px)
)
```

### TimePicker

The `TimePicker` component creates time selection fields.

```kotlin
var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

TimePicker(
    value = selectedTime,
    onValueChange = { selectedTime = it },
    placeholder = "Select time",
    modifier = Modifier.width(200.px)
)
```

### FileUpload

The `FileUpload` component creates file upload areas with drag & drop support.

```kotlin
var selectedFiles by remember { mutableStateOf<List<FileInfo>>(emptyList()) }

FileUpload(
    onFilesSelected = { files ->
        selectedFiles = files
    },
    accept = "image/*",
    multiple = true,
    modifier = Modifier
        .width(400.px)
        .height(200.px)
        .border(2.px, "#cccccc", BorderStyle.Dashed)
        .borderRadius(8.px)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .justifyContent(JustifyContent.Center)
            .alignItems(AlignItems.Center)
    ) {
        Text("Drag & drop files here or click to browse")
        if (selectedFiles.isNotEmpty()) {
            Text("${selectedFiles.size} files selected")
        }
    }
}
```

## Additional Layout Components

### Box

The `Box` component creates a container with positioning capabilities.

```kotlin
Box(
    modifier = Modifier
        .size(200.px)
        .backgroundColor("#f0f0f0")
        .position(Position.Relative)
) {
    // Positioned child
    Text(
        "Top Right",
        modifier = Modifier
            .position(Position.Absolute)
            .top(8.px)
            .right(8.px)
    )
    
    // Centered child
    Text(
        "Center",
        modifier = Modifier
            .position(Position.Absolute)
            .top(50.percent)
            .left(50.percent)
            .transform("translate(-50%, -50%)")
    )
}
```

### Spacer

The `Spacer` component creates flexible spacing.

```kotlin
Row {
    Text("Start")
    Spacer(modifier = Modifier.width(16.px))
    Text("Middle")
    Spacer(modifier = Modifier.weight(1f)) // Flexible spacer
    Text("End")
}
```

### Divider

The `Divider` component creates visual separators.

```kotlin
Column(modifier = Modifier.gap(16.px)) {
    Text("Section 1")
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.px)
            .backgroundColor("#cccccc")
    )
    Text("Section 2")
}
```

### AspectRatio

The `AspectRatio` component maintains aspect ratio for its content.

```kotlin
AspectRatio(
    ratio = 16f / 9f,
    modifier = Modifier
        .fillMaxWidth()
        .backgroundColor("#000000")
) {
    // Content will maintain 16:9 aspect ratio
    Image(
        src = "/video-thumbnail.jpg",
        alt = "Video thumbnail"
    )
}
```

### ExpansionPanel

The `ExpansionPanel` component creates collapsible panels.

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
                .justifyContent(JustifyContent.SpaceBetween)
        ) {
            Text("Click to expand", modifier = Modifier.fontWeight(600))
            Icon(if (expanded) "▼" else "▶")
        }
    }
) {
    Column(modifier = Modifier.padding(16.px)) {
        Text("Expanded content goes here")
        Text("This content is only visible when expanded")
    }
}
```

### LazyColumn and LazyRow

The `LazyColumn` and `LazyRow` components create virtualized lists for better performance with large datasets.

```kotlin
// LazyColumn for vertical scrolling
LazyColumn(
    modifier = Modifier
        .fillMaxWidth()
        .height(400.px)
) {
    items(1000) { index ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.px)
        ) {
            Text("Item $index", modifier = Modifier.padding(16.px))
        }
    }
    
    // You can also use different item types
    item {
        Text(
            "Header",
            modifier = Modifier
                .padding(16.px)
                .fontSize(20.px)
                .fontWeight(700)
        )
    }
    
    items(users) { user ->
        UserRow(user)
    }
}

// LazyRow for horizontal scrolling
LazyRow(
    modifier = Modifier
        .fillMaxWidth()
        .height(200.px)
) {
    items(images) { image ->
        Image(
            src = image.url,
            alt = image.description,
            modifier = Modifier
                .width(150.px)
                .height(150.px)
                .padding(8.px)
        )
    }
}
```

## Additional Display Components

### Image

The `Image` component displays images with various options.

```kotlin
Image(
    src = "/logo.png",
    alt = "Company logo",
    modifier = Modifier
        .width(200.px)
        .height(100.px)
        .objectFit(ObjectFit.Cover)
        .borderRadius(8.px)
)
```

### Icon

The `Icon` component displays icons from various sources.

```kotlin
// Text-based icon
Icon(
    content = "✓",
    modifier = Modifier
        .size(24.px)
        .color("#00cc00")
)

// Image-based icon
Icon(
    src = "/icons/settings.svg",
    alt = "Settings",
    modifier = Modifier
        .size(24.px)
        .color("#666666")
)
```

## Additional Feedback Components

### Progress and ProgressBar

The `Progress` and `ProgressBar` components show progress indicators.

```kotlin
// Indeterminate progress
Progress(
    modifier = Modifier.size(32.px)
)

// Determinate progress bar
var progress by remember { mutableStateOf(0.7f) }

ProgressBar(
    progress = progress,
    modifier = Modifier
        .fillMaxWidth()
        .height(8.px)
        .backgroundColor("#e0e0e0")
        .borderRadius(4.px)
)
```

### Snackbar and SnackbarHost

The `Snackbar` components show temporary notifications.

```kotlin
@Composable
fun MyApp() {
    val snackbarHostState = remember { SnackbarHostState() }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Your app content
        Column {
            Button(
                onClick = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Action completed successfully",
                            actionLabel = "Undo"
                        )
                    }
                }
            ) {
                Text("Show Snackbar")
            }
        }
        
        // Snackbar host positioned at the bottom
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.px)
        )
    }
}
```

### Tooltip

The `Tooltip` component shows hover tooltips.

```kotlin
Tooltip(
    content = {
        Text(
            "This is a helpful tooltip",
            modifier = Modifier
                .padding(8.px)
                .backgroundColor("#333333")
                .color("#ffffff")
                .borderRadius(4.px)
        )
    }
) {
    Button(onClick = { /* Handle click */ }) {
        Text("Hover me")
    }
}
```

## Additional Navigation Components

### TabLayout

The `TabLayout` component creates tab-based navigation.

```kotlin
var selectedTab by remember { mutableStateOf(0) }

Column {
    TabLayout(
        selectedIndex = selectedTab,
        onTabSelected = { selectedTab = it },
        tabs = listOf("Overview", "Details", "Reviews"),
        modifier = Modifier
            .fillMaxWidth()
            .borderBottom(1.px, "#cccccc")
    )
    
    // Tab content
    when (selectedTab) {
        0 -> OverviewContent()
        1 -> DetailsContent()
        2 -> ReviewsContent()
    }
}
```

## Form Management

### Form and FormField

The `Form` and `FormField` components help manage form state and validation.

```kotlin
@Composable
fun RegistrationForm() {
    val formState = rememberFormState()
    
    Form(
        state = formState,
        onSubmit = { values ->
            // Handle form submission
            println("Form submitted with: $values")
        }
    ) {
        FormField(
            name = "username",
            validator = RequiredValidator("Username is required")
        ) { field ->
            TextField(
                value = field.value,
                onValueChange = field.onChange,
                placeholder = "Username",
                error = field.error,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        FormField(
            name = "email",
            validator = EmailValidator("Invalid email address")
        ) { field ->
            TextField(
                value = field.value,
                onValueChange = field.onChange,
                placeholder = "Email",
                type = "email",
                error = field.error,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Button(
            onClick = { formState.submit() },
            enabled = formState.isValid,
            modifier = Modifier.marginTop(16.px)
        ) {
            Text("Register")
        }
    }
}
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
