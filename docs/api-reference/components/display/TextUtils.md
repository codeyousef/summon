# TextUtils Components

TextUtils provides a comprehensive collection of pre-styled text components for common typography patterns in Summon
applications. These components offer semantic meaning, consistent styling, and accessibility features out of the box.

## Overview

Typography is fundamental to good user experience. TextUtils components provide:

- **Semantic HTML Equivalents**: H1-H3 headings, paragraphs, captions, labels
- **Specialized Text Types**: Code, errors, success messages, badges
- **Accessibility Features**: Screen reader support and proper semantic roles
- **Consistent Styling**: System fonts and responsive typography
- **Cross-Platform**: Uniform appearance across browser and JVM environments

## Heading Components

### H1, H2, H3

Semantic heading components that correspond to HTML heading elements.

```kotlin
import codes.yousef.summon.components.display.*

@Composable
fun HeadingExample() {
    Column(verticalSpacing = "16px") {
        H1("Main Page Title")
        H2("Section Heading")
        H3("Subsection Heading")
    }
}
```

#### API Reference

```kotlin
@Composable
fun H1(
    text: String,
    modifier: Modifier = Modifier(),
    ariaLevel: Int? = null
)

@Composable
fun H2(
    text: String,
    modifier: Modifier = Modifier(),
    ariaLevel: Int? = null
)

@Composable
fun H3(
    text: String,
    modifier: Modifier = Modifier(),
    ariaLevel: Int? = null
)
```

| Component | Font Size        | Font Weight | Margin            | Use Case                   |
|-----------|------------------|-------------|-------------------|----------------------------|
| H1        | 2rem (32px)      | Bold        | 0.67em top/bottom | Page titles, main headings |
| H2        | 1.5rem (24px)    | Bold        | 0.83em top/bottom | Section headings           |
| H3        | 1.17rem (18.7px) | Bold        | 1em top/bottom    | Subsection headings        |

## Body Text Components

### Paragraph

Standard paragraph text with appropriate line height and spacing.

```kotlin
@Composable
fun ParagraphExample() {
    Paragraph(
        text = "This is a standard paragraph with proper line height and spacing for optimal readability."
    )
}
```

### Caption

Smaller secondary text for descriptions, metadata, or fine print.

```kotlin
@Composable
fun CaptionExample() {
    Column(verticalSpacing = "8px") {
        Text("John Doe", fontSize = "16px", fontWeight = "medium")
        Caption("Software Engineer at Tech Corp")
    }
}
```

### Label

Text for form labels or descriptive text above inputs.

```kotlin
@Composable
fun LabelExample() {
    Column(verticalSpacing = "4px") {
        Label("Email Address")
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Enter your email"
        )
    }
}
```

## Specialized Text Components

### Monospace

Styled text for code snippets, file paths, or technical content.

```kotlin
@Composable
fun MonospaceExample() {
    Column(verticalSpacing = "12px") {
        Paragraph("To install the package, run:")
        Monospace("npm install @summon/ui")
        Paragraph("Then import it in your file:")
        Monospace("import { Button } from '@summon/ui'")
    }
}
```

### ErrorText and SuccessText

Colored text for status messages and feedback.

```kotlin
@Composable
fun StatusTextExample() {
    Column(verticalSpacing = "8px") {
        SuccessText("File uploaded successfully!")
        ErrorText("Invalid email format")
    }
}
```

### EmphasizedText and StrongText

Text with emphasis using italics or bold styling.

```kotlin
@Composable
fun EmphasisExample() {
    Paragraph(
        text = buildString {
            append("This is ")
            append("important")
            append(" and this is ")
            append("emphasized")
            append(" text.")
        }
    )

    // Using individual components
    Row(horizontalSpacing = "4px") {
        Text("This is ")
        StrongText("important")
        Text(" and this is ")
        EmphasizedText("emphasized")
        Text(" text.")
    }
}
```

## Advanced Text Components

### QuoteText

Styled blockquote text with left border and italic styling.

```kotlin
@Composable
fun QuoteExample() {
    Column(verticalSpacing = "16px") {
        Paragraph("As Steve Jobs once said:")
        QuoteText(
            "Innovation distinguishes between a leader and a follower."
        )
    }
}
```

### TruncatedText

Text that truncates after a specified number of lines with ellipsis.

```kotlin
@Composable
fun TruncatedTextExample() {
    TruncatedText(
        text = "This is a very long text that would normally wrap to many lines, but we want to limit it to just two lines for better layout control.",
        maxLines = 2,
        modifier = Modifier().width("300px")
    )
}
```

### KeyboardText

Styled text for displaying keyboard shortcuts.

```kotlin
@Composable
fun KeyboardShortcutExample() {
    Row(
        horizontalSpacing = "8px",
        verticalAlignment = "center"
    ) {
        Text("Copy:")
        KeyboardText("Ctrl+C")
        Text("Paste:")
        KeyboardText("Ctrl+V")
    }
}
```

### BadgeText

Pill-shaped badge text with customizable colors.

```kotlin
@Composable
fun BadgeExample() {
    Row(horizontalSpacing = "8px") {
        BadgeText("New", color = "#E3F2FD", textColor = "#1976D2")
        BadgeText("Hot", color = "#FFEBEE", textColor = "#D32F2F")
        BadgeText("Sale", color = "#E8F5E8", textColor = "#2E7D32")
    }
}
```

### ScreenReaderText

Visually hidden text that's accessible to screen readers.

```kotlin
@Composable
fun AccessibleButtonExample() {
    Button(
        onClick = { deleteItem() },
        modifier = Modifier().ariaLabel("Delete item")
    ) {
        Icon("delete")
        ScreenReaderText("Delete") // Hidden but announced by screen readers
    }
}
```

## Complete API Reference

### Basic Components

```kotlin
// Headings
@Composable fun H1(text: String, modifier: Modifier = Modifier(), ariaLevel: Int? = null)
@Composable fun H2(text: String, modifier: Modifier = Modifier(), ariaLevel: Int? = null)
@Composable fun H3(text: String, modifier: Modifier = Modifier(), ariaLevel: Int? = null)

// Body text
@Composable fun Paragraph(text: String, modifier: Modifier = Modifier())
@Composable fun Caption(text: String, modifier: Modifier = Modifier())
@Composable fun Label(text: String, modifier: Modifier = Modifier())
```

### Specialized Components

```kotlin
// Technical text
@Composable fun Monospace(text: String, modifier: Modifier = Modifier())
@Composable fun KeyboardText(text: String, modifier: Modifier = Modifier())

// Status text
@Composable fun ErrorText(text: String, modifier: Modifier = Modifier())
@Composable fun SuccessText(text: String, modifier: Modifier = Modifier())

// Emphasis
@Composable fun EmphasizedText(text: String, modifier: Modifier = Modifier())
@Composable fun StrongText(text: String, modifier: Modifier = Modifier())

// Special formatting
@Composable fun QuoteText(text: String, modifier: Modifier = Modifier())
@Composable fun TruncatedText(text: String, maxLines: Int, modifier: Modifier = Modifier())
@Composable fun BadgeText(text: String, color: String = "#e0e0e0", textColor: String = "#333333", modifier: Modifier = Modifier())

// Accessibility
@Composable fun ScreenReaderText(text: String)
```

## Practical Examples

### Article Layout

```kotlin
@Composable
fun ArticleLayout() {
    Column(
        modifier = Modifier()
            .maxWidth("800px")
            .margin("0 auto")
            .padding("24px"),
        verticalSpacing = "16px"
    ) {
        // Article header
        H1("Understanding Kotlin Multiplatform")
        Caption("Published on March 15, 2024 by Jane Developer")

        // Article content
        Paragraph(
            "Kotlin Multiplatform is a powerful technology that allows developers to share code between different platforms while maintaining the flexibility to write platform-specific code when needed."
        )

        H2("Key Benefits")
        Paragraph(
            "The main advantages of using Kotlin Multiplatform include code reuse, consistent business logic, and reduced maintenance overhead."
        )

        H3("Code Sharing")
        Paragraph(
            "Share your business logic, networking, and data models across platforms:"
        )

        Monospace("expect class Platform() { fun name(): String }")

        H3("Platform-Specific Implementation")
        Paragraph(
            "Implement platform-specific features when needed:"
        )

        Monospace("actual class Platform actual constructor() { actual fun name(): String = \"Android\" }")

        QuoteText(
            "Kotlin Multiplatform doesn't try to make everything common. It makes the parts that can be common, common, and leaves the rest platform-specific."
        )
    }
}
```

### Form with Status Messages

```kotlin
@Composable
fun RegistrationForm() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier().padding("24px"),
        verticalSpacing = "16px"
    ) {
        H2("Create Account")

        // Email field
        Column(verticalSpacing = "4px") {
            Label("Email Address")
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Enter your email"
            )
        }

        // Password field
        Column(verticalSpacing = "4px") {
            Label("Password")
            TextField(
                value = password,
                onValueChange = { password = it },
                type = "password",
                placeholder = "Enter your password"
            )
            Caption("Must be at least 8 characters long")
        }

        // Status messages
        error?.let { ErrorText(it) }
        if (success) {
            SuccessText("Account created successfully!")
        }

        Button(
            text = "Create Account",
            onClick = { submitForm(email, password) }
        )

        Caption(
            buildString {
                append("By creating an account, you agree to our ")
                append("Terms of Service")
                append(" and ")
                append("Privacy Policy")
                append(".")
            }
        )
    }
}
```

### Help Documentation

```kotlin
@Composable
fun HelpSection() {
    Column(verticalSpacing = "20px") {
        H2("Keyboard Shortcuts")

        Row(
            modifier = Modifier().justifyContent("space-between"),
            horizontalSpacing = "16px"
        ) {
            Column(verticalSpacing = "8px") {
                Row(horizontalSpacing = "12px") {
                    KeyboardText("Ctrl+S")
                    Text("Save document")
                }
                Row(horizontalSpacing = "12px") {
                    KeyboardText("Ctrl+Z")
                    Text("Undo")
                }
                Row(horizontalSpacing = "12px") {
                    KeyboardText("Ctrl+Y")
                    Text("Redo")
                }
            }

            Column(verticalSpacing = "8px") {
                Row(horizontalSpacing = "12px") {
                    KeyboardText("Ctrl+F")
                    Text("Find")
                }
                Row(horizontalSpacing = "12px") {
                    KeyboardText("Ctrl+H")
                    Text("Replace")
                }
                Row(horizontalSpacing = "12px") {
                    KeyboardText("F11")
                    Text("Fullscreen")
                }
            }
        }

        H3("Pro Tips")

        Row(horizontalSpacing = "8px") {
            BadgeText("Tip", color = "#FFF3E0", textColor = "#F57C00")
            Text("Use")
            KeyboardText("Ctrl+Shift+P")
            Text("to open the command palette")
        }
    }
}
```

### Product Card with Badges

```kotlin
@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier()
            .width("300px")
            .padding("16px")
    ) {
        Column(verticalSpacing = "12px") {
            // Product badges
            Row(horizontalSpacing = "8px") {
                if (product.isNew) {
                    BadgeText("New", color = "#E3F2FD", textColor = "#1976D2")
                }
                if (product.onSale) {
                    BadgeText("Sale", color = "#FFEBEE", textColor = "#D32F2F")
                }
                if (product.featured) {
                    BadgeText("Featured", color = "#E8F5E8", textColor = "#2E7D32")
                }
            }

            // Product name
            StrongText(product.name)

            // Product description
            TruncatedText(
                text = product.description,
                maxLines = 2
            )

            // Price
            Row(
                horizontalSpacing = "8px",
                verticalAlignment = "center"
            ) {
                if (product.originalPrice != product.currentPrice) {
                    Text(
                        text = "$${product.originalPrice}",
                        modifier = Modifier()
                            .textDecoration("line-through")
                            .color("#999999")
                    )
                }
                StrongText("$${product.currentPrice}")
            }

            // Technical specs
            if (product.specs.isNotEmpty()) {
                Column(verticalSpacing = "4px") {
                    Caption("Specifications:")
                    product.specs.forEach { spec ->
                        Row(horizontalSpacing = "8px") {
                            Text("•")
                            Caption(spec)
                        }
                    }
                }
            }
        }
    }
}
```

## Accessibility Guidelines

### Semantic Hierarchy

Use heading components in logical order:

```kotlin
// Good - Proper heading hierarchy
H1("Page Title")
H2("Main Section")
H3("Subsection")
H3("Another Subsection")
H2("Another Main Section")

// Bad - Skipping heading levels
H1("Page Title")
H3("Subsection") // Skips H2
```

### Screen Reader Support

Use ScreenReaderText for additional context:

```kotlin
@Composable
fun DataTable() {
    Column {
        H2("Sales Data")
        ScreenReaderText("Table showing monthly sales figures for 2024")

        // Table content here
    }
}
```

### Color and Contrast

Ensure sufficient contrast for text:

```kotlin
// Good - High contrast error text
ErrorText("Password is required") // #d32f2f on light background

// Consider dark mode alternatives
val errorColor = if (isDarkMode) "#ff6b6b" else "#d32f2f"
ErrorText(
    text = "Password is required",
    modifier = Modifier().color(errorColor)
)
```

## Typography System Integration

### Using with Theme

```kotlin
@Composable
fun ThemedContent() {
    val theme = LocalTheme.current

    H1(
        text = "Welcome",
        modifier = Modifier().color(theme.colors.primary)
    )

    Paragraph(
        text = "Content text",
        modifier = Modifier().color(theme.colors.onSurface)
    )
}
```

### Custom Typography Scale

```kotlin
object AppTypography {
    @Composable
    fun DisplayLarge(text: String, modifier: Modifier = Modifier()) {
        Text(
            text = text,
            modifier = modifier
                .fontSize("3.5rem")
                .fontWeight("300")
                .lineHeight("1.2"),
            fontFamily = "system-ui"
        )
    }

    @Composable
    fun BodyLarge(text: String, modifier: Modifier = Modifier()) {
        Text(
            text = text,
            modifier = modifier
                .fontSize("1.125rem")
                .lineHeight("1.6"),
            fontFamily = "system-ui"
        )
    }
}
```

## Performance Considerations

### Font Loading

The TextUtils components use system fonts for optimal performance:

- **System UI**: Primary system font stack
- **Monospace**: Consistent code font across platforms
- **No Web Fonts**: Eliminates loading delays and layout shifts

### Responsive Typography

Consider responsive font sizes for better mobile experience:

```kotlin
@Composable
fun ResponsiveHeading(text: String) {
    val breakpoint = LocalBreakpoint.current
    val fontSize = when (breakpoint) {
        Breakpoint.MOBILE -> "1.5rem"
        Breakpoint.TABLET -> "2rem"
        Breakpoint.DESKTOP -> "2.5rem"
    }

    H1(
        text = text,
        modifier = Modifier().fontSize(fontSize)
    )
}
```

## Testing

### Unit Testing

```kotlin
@Test
fun testHeadingComponents() {
    val mockRenderer = MockPlatformRenderer()

    CompositionLocal.provideComposer(MockComposer()) {
        LocalPlatformRenderer.provides(mockRenderer) {
            H1("Test Heading")

            assertTrue(mockRenderer.renderTextCalled)
            assertEquals("Test Heading", mockRenderer.lastTextContentRendered)

            val styles = mockRenderer.lastTextModifierRendered?.styles
            assertEquals("2rem", styles?.get("font-size"))
            assertEquals("bold", styles?.get("font-weight"))
        }
    }
}
```

### Accessibility Testing

```kotlin
@Test
fun testScreenReaderText() {
    val mockRenderer = MockPlatformRenderer()

    CompositionLocal.provideComposer(MockComposer()) {
        LocalPlatformRenderer.provides(mockRenderer) {
            ScreenReaderText("Hidden content")

            val styles = mockRenderer.lastTextModifierRendered?.styles
            assertEquals("absolute", styles?.get("position"))
            assertEquals("1px", styles?.get("width"))
            assertEquals("1px", styles?.get("height"))
        }
    }
}
```

## Migration Guide

### From HTML

```html
<!-- HTML -->
<h1>Page Title</h1>
<p>Paragraph content</p>
<small>Caption text</small>
<code>inline code</code>
<strong>bold text</strong>
<em>italic text</em>
```

```kotlin
// Summon equivalent
H1("Page Title")
Paragraph("Paragraph content")
Caption("Caption text")
Monospace("inline code")
StrongText("bold text")
EmphasizedText("italic text")
```

### From CSS Classes

```css
/* CSS classes */
.error-text { color: #d32f2f; }
.success-text { color: #2e7d32; }
.badge { background: #e0e0e0; padding: 0.25em 0.6em; border-radius: 1em; }
```

```kotlin
// Summon components
ErrorText("Error message")
SuccessText("Success message")
BadgeText("Badge text")
```

## Best Practices

1. **Use Semantic Components**: Choose components based on meaning, not appearance
2. **Maintain Hierarchy**: Use proper heading order (H1 → H2 → H3)
3. **Consistent Spacing**: Leverage built-in margins and spacing
4. **Accessibility First**: Always consider screen reader users
5. **Performance**: Prefer system fonts over custom web fonts
6. **Responsive Design**: Consider different screen sizes
7. **Color Contrast**: Ensure sufficient contrast ratios
8. **Testing**: Test with actual assistive technologies

TextUtils components provide a solid foundation for typography in your Summon application, ensuring consistency,
accessibility, and performance across all platforms.