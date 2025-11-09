# TextArea

TextArea components provide multi-line text input with support for auto-resize, validation, character limits, and rich
text editing features.

## Overview

The TextArea component allows users to input and edit multi-line text. It's perfect for comments, descriptions,
messages, code snippets, and any scenario requiring substantial text input.

### Key Features

- **Multi-line Input**: Support for unlimited text lines
- **Auto-resize**: Automatically adjusts height based on content
- **Character Limits**: Built-in character counting and limits
- **Validation Support**: Client-side validation integration
- **Accessibility**: Full ARIA support and keyboard navigation
- **Form Integration**: Works seamlessly with forms
- **Read-only Mode**: Display-only text areas
- **Custom Styling**: Type-safe styling with modifiers

## API Reference

### TextArea

```kotlin
@Composable
fun TextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    rows: Int? = null,
    maxLength: Int? = null,
    placeholder: String? = null
)
```

**Parameters:**

- `value`: The current text value
- `onValueChange`: Callback invoked when the text changes
- `modifier`: Modifier for styling and layout
- `enabled`: Whether the text area can be interacted with (default: `true`)
- `readOnly`: Whether the text area is read-only (default: `false`)
- `rows`: Number of visible text lines (default: `null` for auto-sizing)
- `maxLength`: Maximum number of characters allowed (default: `null`)
- `placeholder`: Placeholder text when empty (default: `null`)

### StatefulTextArea

```kotlin
@Composable
fun StatefulTextArea(
    initialValue: String = "",
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    rows: Int? = null,
    maxLength: Int? = null,
    placeholder: String? = null
)
```

**Parameters:**

- `initialValue`: Initial text value (default: `""`)
- `onValueChange`: Callback invoked when text changes
- Other parameters same as `TextArea`

## Usage Examples

### Basic Text Area

```kotlin
@Composable
fun BasicTextAreaExample() {
    var comment by remember { mutableStateOf("") }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Leave a comment:", style = Typography.H6)

        TextArea(
            value = comment,
            onValueChange = { comment = it },
            placeholder = "Enter your comment here...",
            rows = 4,
            modifier = Modifier()
                .width(400.px)
                .padding(Spacing.MD)
                .border(Border.solid(1.px, Colors.Gray.LIGHT))
                .borderRadius(BorderRadius.MD)
                .padding(Spacing.SM)
        )

        Text(
            "Characters: ${comment.length}",
            style = Typography.CAPTION.copy(color = Colors.Gray.MAIN)
        )
    }
}
```

### Text Area with Character Limit

```kotlin
@Composable
fun CharacterLimitTextAreaExample() {
    var message by remember { mutableStateOf("") }
    val maxLength = 280

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Share your thoughts:", style = Typography.H6)

        TextArea(
            value = message,
            onValueChange = { newValue ->
                if (newValue.length <= maxLength) {
                    message = newValue
                }
            },
            placeholder = "What's on your mind?",
            maxLength = maxLength,
            rows = 3,
            modifier = Modifier()
                .width(500.px)
                .border(
                    Border.solid(
                        1.px,
                        if (message.length > maxLength * 0.9) Colors.Warning.MAIN else Colors.Gray.LIGHT
                    )
                )
                .borderRadius(BorderRadius.MD)
                .padding(Spacing.SM)
        )

        Row(
            modifier = Modifier()
                .width(500.px)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Text(
                when {
                    message.length > maxLength * 0.9 -> "Almost at limit!"
                    message.length > maxLength * 0.7 -> "Getting close to limit"
                    else -> ""
                },
                style = Typography.CAPTION.copy(
                    color = when {
                        message.length > maxLength * 0.9 -> Colors.Warning.MAIN
                        message.length > maxLength * 0.7 -> Colors.Info.MAIN
                        else -> Colors.Transparent
                    }
                )
            )

            Text(
                "${message.length}/$maxLength",
                style = Typography.CAPTION.copy(
                    color = when {
                        message.length >= maxLength -> Colors.Error.MAIN
                        message.length > maxLength * 0.9 -> Colors.Warning.MAIN
                        else -> Colors.Gray.MAIN
                    }
                )
            )
        }
    }
}
```

### Auto-resize Text Area

```kotlin
@Composable
fun AutoResizeTextAreaExample() {
    var content by remember { mutableStateOf("") }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Auto-resizing text area:", style = Typography.H6)

        TextArea(
            value = content,
            onValueChange = { content = it },
            placeholder = "Type here and watch the text area grow...",
            modifier = Modifier()
                .width(400.px)
                .minHeight(60.px)
                .maxHeight(300.px)
                .border(Border.solid(1.px, Colors.Gray.LIGHT))
                .borderRadius(BorderRadius.MD)
                .padding(Spacing.SM)
                .resize("vertical") // Allow manual resize
        )

        Text(
            "Lines: ${content.count { it == '\n' } + if (content.isNotEmpty()) 1 else 0}",
            style = Typography.CAPTION.copy(color = Colors.Info.MAIN)
        )
    }
}
```

### Markdown & Code Editors

For authoring experiences that need Markdown previews or syntax highlighting, build on top of `TextArea` rather than
reimplementing it. Two recommended wrappers are:

- [`MarkdownEditor`](MarkdownEditor.md) for live Markdown editing.
- Custom code editors that compose `TextArea` with domain-specific toolbars or syntax highlighting.

```kotlin
@Composable
fun CodeEditorExample() {

```kotlin
@Composable
fun CodeEditorExample() {
    var code by remember {
        mutableStateOf("""
fun greetUser(name: String): String {
    return "Hello, ${'$'}name!"
}

fun main() {
    val greeting = greetUser("World")
    println(greeting)
}
        """.trimIndent())
    }

    var selectedLanguage by remember { mutableStateOf("kotlin") }

    val languages = mapOf(
        "kotlin" to "Kotlin",
        "javascript" to "JavaScript",
        "python" to "Python",
        "java" to "Java"
    )

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Row(
            modifier = Modifier()
                .alignItems(AlignItems.Center)
                .gap(Spacing.MD)
        ) {
            Text("Code Editor", style = Typography.H6)

            Select(
                selectedValue = remember { mutableStateOf(selectedLanguage) }.apply { value = selectedLanguage },
                options = languages.map { (key, value) -> SelectOption(key, value) },
                onSelectedChange = { selectedLanguage = it ?: "kotlin" },
                modifier = Modifier().width(150.px)
            )
        }

        TextArea(
            value = code,
            onValueChange = { code = it },
            rows = 12,
            modifier = Modifier()
                .width(600.px)
                .fontFamily("'Courier New', 'Monaco', monospace")
                .fontSize(14.px)
                .backgroundColor(Colors.Gray.DARK)
                .color(Colors.White)
                .border(Border.solid(1.px, Colors.Gray.MAIN))
                .borderRadius(BorderRadius.MD)
                .padding(Spacing.MD)
        )

        Row(
            modifier = Modifier()
                .width(600.px)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Text(
                "Language: ${languages[selectedLanguage]}",
                style = Typography.CAPTION
            )

            Text(
                "Lines: ${code.count { it == '\n' } + 1} | Characters: ${code.length}",
                style = Typography.CAPTION
            )
        }

        Row(modifier = Modifier().gap(Spacing.SM)) {
            Button(
                text = "Run Code",
                type = ButtonType.PRIMARY,
                onClick = { println("Running $selectedLanguage code") }
            )

            Button(
                text = "Format",
                type = ButtonType.SECONDARY,
                onClick = { /* Format code logic */ }
            )

            Button(
                text = "Clear",
                type = ButtonType.OUTLINE,
                onClick = { code = "" }
            )
        }
    }
}
```

### Form with Text Areas

```kotlin
@Composable
fun TextAreaFormExample() {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Form(
        onSubmit = {
            println("Article submitted:")
            println("Title: $title")
            println("Description: $description")
            println("Tags: $tags")
            println("Notes: $notes")
        }
    ) {
        FormField(label = "Article Details") {
            Column(modifier = Modifier().gap(Spacing.MD)) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Title *",
                    validators = listOf(
                        Validator { value ->
                            if (value.isBlank()) {
                                ValidationResult.invalid("Title is required")
                            } else {
                                ValidationResult.valid()
                            }
                        }
                    ),
                    modifier = Modifier().width(Width.FULL)
                )

                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Text("Description *", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    TextArea(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = "Provide a detailed description of your article...",
                        rows = 4,
                        maxLength = 500,
                        modifier = Modifier()
                            .width(Width.FULL)
                            .border(Border.solid(1.px, Colors.Gray.LIGHT))
                            .borderRadius(BorderRadius.MD)
                            .padding(Spacing.SM)
                    )
                    Text(
                        "${description.length}/500 characters",
                        style = Typography.CAPTION.copy(
                            color = if (description.length > 450) Colors.Warning.MAIN else Colors.Gray.MAIN
                        )
                    )
                }

                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Text("Tags", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    TextArea(
                        value = tags,
                        onValueChange = { tags = it },
                        placeholder = "Enter tags separated by commas (e.g., kotlin, programming, tutorial)",
                        rows = 2,
                        modifier = Modifier()
                            .width(Width.FULL)
                            .border(Border.solid(1.px, Colors.Gray.LIGHT))
                            .borderRadius(BorderRadius.MD)
                            .padding(Spacing.SM)
                    )
                }

                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Text("Additional Notes", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    TextArea(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = "Any additional notes or comments...",
                        rows = 3,
                        modifier = Modifier()
                            .width(Width.FULL)
                            .border(Border.solid(1.px, Colors.Gray.LIGHT))
                            .borderRadius(BorderRadius.MD)
                            .padding(Spacing.SM)
                    )
                }
            }
        }

        Button(
            text = "Publish Article",
            type = ButtonType.SUBMIT,
            enabled = title.isNotBlank() && description.isNotBlank()
        )
    }
}
```

### Read-only Text Area

```kotlin
@Composable
fun ReadOnlyTextAreaExample() {
    val logContent = remember {
        """
2024-03-15 10:30:15 [INFO] Application started
2024-03-15 10:30:16 [INFO] Database connection established
2024-03-15 10:30:17 [INFO] User authentication module loaded
2024-03-15 10:30:18 [WARN] High memory usage detected (85%)
2024-03-15 10:30:20 [INFO] Background tasks initialized
2024-03-15 10:30:22 [ERROR] Failed to load user preferences for user_id: 12345
2024-03-15 10:30:25 [INFO] API server listening on port 8080
        """.trimIndent()
    }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Row(
            modifier = Modifier()
                .alignItems(AlignItems.Center)
                .gap(Spacing.MD)
        ) {
            Text("Application Logs", style = Typography.H6)
            Text("(Read-only)", style = Typography.CAPTION.copy(color = Colors.Gray.MAIN))
        }

        TextArea(
            value = logContent,
            onValueChange = { /* Read-only */ },
            readOnly = true,
            rows = 8,
            modifier = Modifier()
                .width(600.px)
                .fontFamily("monospace")
                .fontSize(13.px)
                .backgroundColor(Colors.Gray.DARK)
                .color(Colors.Gray.LIGHT)
                .border(Border.solid(1.px, Colors.Gray.MAIN))
                .borderRadius(BorderRadius.MD)
                .padding(Spacing.SM)
        )

        Row(modifier = Modifier().gap(Spacing.SM)) {
            Button(
                text = "Copy Logs",
                type = ButtonType.SECONDARY,
                onClick = {
                    // Copy to clipboard logic
                    println("Logs copied to clipboard")
                }
            )

            Button(
                text = "Download",
                type = ButtonType.OUTLINE,
                onClick = {
                    // Download logs logic
                    println("Downloading logs...")
                }
            )

            Button(
                text = "Clear Logs",
                type = ButtonType.OUTLINE,
                onClick = {
                    // Clear logs logic
                    println("Logs cleared")
                }
            )
        }
    }
}
```

### Collaborative Text Area

```kotlin
@Composable
fun CollaborativeTextAreaExample() {
    var content by remember { mutableStateOf("") }
    var isOnline by remember { mutableStateOf(true) }
    var collaborators by remember { mutableStateOf(listOf("Alice", "Bob")) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Text("Collaborative Document", style = Typography.H6)

            Row(
                modifier = Modifier()
                    .alignItems(AlignItems.Center)
                    .gap(Spacing.SM)
            ) {
                Box(
                    modifier = Modifier()
                        .width(8.px)
                        .height(8.px)
                        .backgroundColor(if (isOnline) Colors.Success.MAIN else Colors.Error.MAIN)
                        .borderRadius(BorderRadius.FULL)
                ) {}
                Text(
                    if (isOnline) "Online" else "Offline",
                    style = Typography.CAPTION.copy(
                        color = if (isOnline) Colors.Success.MAIN else Colors.Error.MAIN
                    )
                )
            }
        }

        // Collaborators indicator
        if (collaborators.isNotEmpty()) {
            Row(
                modifier = Modifier()
                    .alignItems(AlignItems.Center)
                    .gap(Spacing.SM)
            ) {
                Text("Collaborators:", style = Typography.CAPTION)
                collaborators.forEach { collaborator ->
                    Box(
                        modifier = Modifier()
                            .padding(horizontal = 6.px, vertical = 2.px)
                            .backgroundColor(Colors.Primary.LIGHT)
                            .borderRadius(BorderRadius.SM)
                    ) {
                        Text(
                            collaborator,
                            style = Typography.CAPTION.copy(color = Colors.Primary.DARK)
                        )
                    }
                }
            }
        }

        TextArea(
            value = content,
            onValueChange = { content = it },
            placeholder = "Start typing to collaborate with others...",
            rows = 8,
            enabled = isOnline,
            modifier = Modifier()
                .width(500.px)
                .border(
                    Border.solid(
                        2.px,
                        if (isOnline) Colors.Success.LIGHT else Colors.Gray.LIGHT
                    )
                )
                .borderRadius(BorderRadius.MD)
                .padding(Spacing.SM)
                .opacity(if (isOnline) 1.0f else 0.7f)
        )

        if (!isOnline) {
            Alert(
                type = AlertType.WARNING,
                title = "Connection Lost",
                message = "You're currently offline. Changes will be saved when connection is restored."
            )
        }

        Row(modifier = Modifier().gap(Spacing.SM)) {
            Button(
                text = "Save",
                type = ButtonType.PRIMARY,
                enabled = isOnline,
                onClick = { println("Document saved") }
            )

            Button(
                text = "Share",
                type = ButtonType.SECONDARY,
                onClick = { println("Sharing document") }
            )

            Button(
                text = if (isOnline) "Go Offline" else "Go Online",
                type = ButtonType.OUTLINE,
                onClick = { isOnline = !isOnline }
            )
        }
    }
}
```

### Stateful Text Area

```kotlin
@Composable
fun StatefulTextAreaExample() {
    StatefulTextArea(
        initialValue = "This is a stateful text area with initial content.",
        onValueChange = { value ->
            println("Text changed: ${value.length} characters")
        },
        placeholder = "Type something here...",
        rows = 4,
        maxLength = 200,
        modifier = Modifier()
            .width(400.px)
            .border(Border.solid(1.px, Colors.Gray.LIGHT))
            .borderRadius(BorderRadius.MD)
            .padding(Spacing.SM)
    )
}
```

## Accessibility Features

### ARIA Support

The TextArea component automatically includes:

- `role="textbox"` with `aria-multiline="true"`
- `aria-label` or `aria-labelledby` for descriptions
- `aria-describedby` for help text and character counts
- `aria-invalid` for validation state
- `aria-readonly` for read-only state

### Keyboard Navigation

- **Tab**: Move focus to/from text area
- **Enter**: New line (not form submission)
- **Ctrl+A**: Select all text
- **Ctrl+C/V/X**: Copy, paste, cut operations
- **Arrow Keys**: Navigate within text

### Screen Reader Support

```kotlin
@Composable
fun AccessibleTextAreaExample() {
    var content by remember { mutableStateOf("") }

    TextArea(
        value = content,
        onValueChange = { content = it },
        placeholder = "Enter your message",
        modifier = Modifier()
            .accessibilityLabel("Message content")
            .accessibilityHint("Enter your message here. Character limit is 500.")
            .accessibilityValue("${content.length} characters entered")
    )
}
```

## Validation Patterns

### Required Text Area

```kotlin
fun requiredTextAreaValidator() = Validator { value ->
    if (value.isBlank()) {
        ValidationResult.invalid("This field is required")
    } else {
        ValidationResult.valid()
    }
}
```

### Minimum Length Validation

```kotlin
fun minimumLengthValidator(minLength: Int) = Validator { value ->
    if (value.length >= minLength) {
        ValidationResult.valid()
    } else {
        ValidationResult.invalid("Must be at least $minLength characters")
    }
}
```

### Content Validation

```kotlin
fun profanityFilterValidator() = Validator { value ->
    val prohibitedWords = listOf("spam", "bad", "inappropriate")
    val containsProhibited = prohibitedWords.any { value.contains(it, ignoreCase = true) }

    if (containsProhibited) {
        ValidationResult.invalid("Content contains prohibited words")
    } else {
        ValidationResult.valid()
    }
}
```

## Platform Differences

### Browser (JS)

- Uses HTML `<textarea>` element
- CSS styling through modifiers
- Native text selection and clipboard operations
- Auto-resize with CSS

### JVM

- Server-side rendering support
- Generates appropriate HTML for SSR
- Text processing on server side
- Server-side validation support

## Performance Considerations

### Optimization Tips

1. **Debounce Updates**: Prevent excessive callbacks during typing
2. **Lazy Validation**: Only validate on blur or submit
3. **Memoization**: Cache expensive text processing
4. **Virtual Scrolling**: For very large text content

```kotlin
@Composable
fun OptimizedTextAreaExample() {
    var content by remember { mutableStateOf("") }
    var debouncedContent by remember { mutableStateOf("") }

    // Debounce content updates
    LaunchedEffect(content) {
        delay(300) // Debounce delay
        debouncedContent = content
    }

    // Expensive validation only on debounced content
    val validationResult = remember(debouncedContent) {
        validateContent(debouncedContent)
    }

    TextArea(
        value = content,
        onValueChange = { content = it },
        modifier = Modifier().width(400.px)
    )
}

private fun validateContent(content: String): ValidationResult {
    // Expensive validation logic
    return ValidationResult.valid()
}
```

## Testing Strategies

### Unit Testing

```kotlin
class TextAreaTest {
    @Test
    fun `text area updates value correctly`() {
        var textValue = ""

        composeTestRule.setContent {
            TextArea(
                value = textValue,
                onValueChange = { textValue = it }
            )
        }

        composeTestRule.onNode(hasContentDescription("text area"))
            .performTextInput("Hello, World!")

        assertEquals("Hello, World!", textValue)
    }

    @Test
    fun `text area respects character limit`() {
        var textValue = ""
        val maxLength = 10

        composeTestRule.setContent {
            TextArea(
                value = textValue,
                onValueChange = { if (it.length <= maxLength) textValue = it },
                maxLength = maxLength
            )
        }

        composeTestRule.onNode(hasContentDescription("text area"))
            .performTextInput("This is a very long text that exceeds the limit")

        assertTrue(textValue.length <= maxLength)
    }
}
```

## Migration Guide

### From HTML Forms

```html
<!-- Before: HTML -->
<textarea name="comment" rows="4" cols="50" maxlength="500" placeholder="Enter comment"></textarea>
```

```kotlin
// After: Summon
@Composable
fun CommentTextArea() {
    var comment by remember { mutableStateOf("") }

    TextArea(
        value = comment,
        onValueChange = { comment = it },
        rows = 4,
        maxLength = 500,
        placeholder = "Enter comment"
    )
}
```

## Best Practices

### Do

- Provide clear placeholder text
- Show character counts for limited text areas
- Use appropriate sizing (rows) for expected content
- Implement auto-resize for dynamic content
- Validate input appropriately

### Don't

- Use text areas for single-line input (use TextField instead)
- Make text areas too small for expected content
- Forget to handle long text gracefully
- Skip accessibility attributes

## Related Components

- [TextField](TextField.md) - For single-line text input
- [Form](Form.md) - For form integration
- [FormField](FormField.md) - For field wrapping
- [Button](Button.md) - For form actions
