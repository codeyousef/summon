# Components API Reference

This document provides detailed information about the UI components available in the Summon library. All components are
built with type-safe styling, accessibility features, and cross-platform compatibility.

## Table of Contents

- [Foundation Components](#foundation-components)
- [Layout Components](#layout-components)
- [Display Components](#display-components)
- [Input Components](#input-components)
- [Feedback Components](#feedback-components)
- [Navigation Components](#navigation-components)
- [Styling Components](#styling-components)
- [HTML DSL Components](#html-dsl-components) ⭐ NEW (v0.7.0)
- [Desktop Components](#desktop-components) ⭐ NEW (v0.7.0)
- [Component Architecture](#component-architecture)

---

## Foundation Components

Foundation components (package `codes.yousef.summon.components.foundation`) provide theming and other low-level building
blocks shared across the design system.

### ThemeProvider

Provides theme context to child components.

```kotlin
@Composable
fun ThemeProvider(
    theme: Theme,
    content: @Composable () -> Unit
)
```

### BasicText

Low-level text rendering component used internally by other text components.

```kotlin
@Composable
fun BasicText(
    text: String,
    modifier: Modifier = Modifier()
)
```

---

## Layout Components

Layout components help organize and structure your UI.

### Column

Arranges children vertically using CSS flexbox.

```kotlin
@Composable
fun Column(
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
)
```

**Example:**
```kotlin
Column {
    Text("First item")
    Text("Second item")
    Button(onClick = { }, label = "Action")
}
```

### Row

Arranges children horizontally using CSS flexbox.

```kotlin
@Composable
fun Row(
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
)
```

### Box

A container that positions children relative to each other.

```kotlin
@Composable
fun Box(
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
)
```

### Grid

CSS Grid layout for complex arrangements.

```kotlin
@Composable
fun Grid(
    modifier: Modifier = Modifier(),
    columns: String = "1fr",
    rows: String = "auto",
    gap: String = "8px",
    content: @Composable () -> Unit
)
```

### Spacer

Adds space between components.

```kotlin
@Composable
fun Spacer(
    modifier: Modifier = Modifier(),
    width: String? = null,
    height: String? = null
)
```

---

## Display Components

Components for displaying content to users.

### Text

Primary component for displaying text with extensive styling options.

```kotlin
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier(),
    overflow: String? = null,
    lineHeight: String? = null,
    textAlign: String? = null,
    fontFamily: String? = null,
    textDecoration: String? = null,
    textTransform: String? = null,
    letterSpacing: String? = null,
    whiteSpace: String? = null,
    wordBreak: String? = null,
    wordSpacing: String? = null,
    textShadow: String? = null,
    maxLines: Int? = null,
    role: String? = null,
    ariaLabel: String? = null,
    ariaDescribedBy: String? = null,
    semantic: String? = null
)
```

**Example:**
```kotlin
Text(
    text = "Hello World",
    modifier = Modifier().color("#333").fontSize("18px"),
    textAlign = "center",
    maxLines = 2
)
```

### Label

Semantic label component for form controls.

```kotlin
@Composable
fun Label(
    text: String,
    modifier: Modifier = Modifier(),
    forElement: String? = null
)
```

### Icon

Display icons from various icon sets.

```kotlin
@Composable
fun Icon(
    name: String,
    modifier: Modifier = Modifier(),
    size: String = "24px",
    color: String? = null
)
```

### Image

Display images with loading states and accessibility features.

```kotlin
@Composable
fun Image(
    src: String,
    alt: String,
    modifier: Modifier = Modifier(),
    loading: String = "lazy",
    width: String? = null,
    height: String? = null
)
```

### RichText

Display rich formatted text content.

```kotlin
@Composable
fun RichText(
    content: String,
    modifier: Modifier = Modifier(),
    sanitize: Boolean = true
)
```

---

## Input Components

Interactive components for user input.

### Button

Versatile button component with multiple variants.

```kotlin
@Composable
fun Button(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier(),
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    disabled: Boolean = false,
    iconName: String? = null,
    iconPosition: IconPosition = IconPosition.START
)

enum class ButtonVariant {
    PRIMARY, SECONDARY, TERTIARY, DANGER, SUCCESS, WARNING, INFO, LINK, GHOST
}

enum class IconPosition { START, END }
```

**Example:**
```kotlin
Button(
    onClick = { println("Clicked!") },
    label = "Download",
    variant = ButtonVariant.SUCCESS,
    iconName = "download",
    iconPosition = IconPosition.START
)
```

### TextField

Text input field with validation support.

```kotlin
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    placeholder: String? = null,
    label: String? = null,
    type: TextFieldType = TextFieldType.Text,
    isError: Boolean = false,
    isEnabled: Boolean = true,
    isReadOnly: Boolean = false,
    validators: List<Validator> = emptyList()
)
```

#### Parameters

| Parameter       | Type               | Default              | Description                              |
|-----------------|--------------------|----------------------|------------------------------------------|
| `value`         | `String`           | required             | The current text value                   |
| `onValueChange` | `(String) -> Unit` | required             | Callback invoked when input changes      |
| `modifier`      | `Modifier`         | `Modifier()`         | Styling and attributes                   |
| `placeholder`   | `String?`          | `null`               | Placeholder text when empty              |
| `label`         | `String?`          | `null`               | Label text for the field                 |
| `type`          | `TextFieldType`    | `TextFieldType.Text` | Input type (Text, Password, Email, etc.) |
| `isError`       | `Boolean`          | `false`              | Whether to show error state              |
| `isEnabled`     | `Boolean`          | `true`               | Whether the field is enabled             |
| `isReadOnly`    | `Boolean`          | `false`              | Whether the field is read-only           |
| `validators`    | `List<Validator>`  | `emptyList()`        | Validators to apply                      |

### BasicTextField

A minimal text input field without validation state. Safe for JS minification scenarios where state capture in callbacks
can cause issues.

```kotlin
@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    placeholder: String? = null,
    type: String = "text"
)
```

#### When to Use BasicTextField

Use `BasicTextField` when:

- You need a simple input without validation
- You're experiencing issues with minified JS builds
- You want direct pass-through to the renderer without internal state

### StatefulTextField

A stateful version that manages its own state internally.

```kotlin
@Composable
fun StatefulTextField(
    initialValue: String = "",
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier(),
    placeholder: String? = null,
    label: String? = null,
    type: TextFieldType = TextFieldType.Text,
    isError: Boolean = false,
    isEnabled: Boolean = true,
    isReadOnly: Boolean = false,
    validators: List<Validator> = emptyList()
)
```

### Checkbox

Checkbox input with tri-state support.

```kotlin
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier(),
    label: String? = null,
    disabled: Boolean = false,
    indeterminate: Boolean = false
)
```

### RadioButton

Radio button for exclusive selections.

```kotlin
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier(),
    label: String? = null,
    disabled: Boolean = false,
    name: String? = null
)
```

### Select

Dropdown selection component.

```kotlin
@Composable
fun Select(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<SelectOption>,
    modifier: Modifier = Modifier(),
    placeholder: String = "Select an option",
    disabled: Boolean = false,
    searchable: Boolean = false
)

data class SelectOption(
    val value: String,
    val label: String,
    val disabled: Boolean = false
)
```

### RangeSlider

Slider for numeric range selection.

```kotlin
@Composable
fun RangeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier(),
    min: Float = 0f,
    max: Float = 100f,
    step: Float = 1f,
    disabled: Boolean = false
)
```

### Form & FormField

Form containers with validation support.

```kotlin
@Composable
fun Form(
    modifier: Modifier = Modifier(),
    onSubmit: () -> Unit,
    content: @Composable () -> Unit
)

@Composable
fun FormField(
    modifier: Modifier = Modifier(),
    label: String? = null,
    error: String? = null,
    required: Boolean = false,
    content: @Composable () -> Unit
)
```

### FileUpload

File upload component with drag-and-drop support.

```kotlin
@Composable
fun FileUpload(
    onFilesSelected: (List<FileInfo>) -> Unit,
    modifier: Modifier = Modifier(),
    acceptedTypes: List<String> = emptyList(),
    maxFiles: Int = 1,
    maxSizeBytes: Long? = null,
    disabled: Boolean = false
)

data class FileInfo(
    val name: String,
    val size: Long,
    val type: String,
    val lastModified: Long
)
```

### DatePicker

Date selection component.

```kotlin
@Composable
fun DatePicker(
    value: String?,
    onValueChange: (String?) -> Unit,
    modifier: Modifier = Modifier(),
    format: String = "yyyy-MM-dd",
    min: String? = null,
    max: String? = null,
    disabled: Boolean = false
)
```

---

## Feedback Components

Components that provide user feedback and status information.

### Alert

Display important messages to users.

```kotlin
@Composable
fun Alert(
    message: String,
    modifier: Modifier = Modifier(),
    variant: AlertVariant = AlertVariant.INFO,
    dismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    title: String? = null,
    icon: String? = null
)

enum class AlertVariant { SUCCESS, INFO, WARNING, ERROR }
```

### Badge

Small status indicators.

```kotlin
@Composable
fun Badge(
    text: String,
    modifier: Modifier = Modifier(),
    variant: BadgeVariant = BadgeVariant.DEFAULT,
    size: BadgeSize = BadgeSize.MEDIUM
)
```

### Loading

Loading indicators and spinners.

```kotlin
@Composable
fun Loading(
    modifier: Modifier = Modifier(),
    size: String = "24px",
    color: String = "currentColor",
    type: LoadingType = LoadingType.SPINNER
)
```

### Modal

Modal dialogs and overlays.

```kotlin
@Composable
fun Modal(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier(),
    title: String? = null,
    content: @Composable () -> Unit
)
```

### Progress

Progress bars and indicators.

```kotlin
@Composable
fun Progress(
    value: Float,
    modifier: Modifier = Modifier(),
    max: Float = 100f,
    showLabel: Boolean = false,
    variant: ProgressVariant = ProgressVariant.DETERMINATE
)
```

### ProgressBar

Linear progress indicator.

```kotlin
@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier(),
    backgroundColor: String = "#e0e0e0",
    progressColor: String = "#2196f3",
    height: String = "8px"
)
```

### Snackbar & SnackbarHost

Temporary messages and notifications.

```kotlin
@Composable
fun Snackbar(
    message: String,
    modifier: Modifier = Modifier(),
    action: String? = null,
    onActionClick: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    duration: SnackbarDuration = SnackbarDuration.SHORT
)

@Composable
fun SnackbarHost(
    modifier: Modifier = Modifier()
)
```

### Toast

Toast notifications.

```kotlin
@Composable
fun Toast(
    message: String,
    modifier: Modifier = Modifier(),
    type: ToastType = ToastType.INFO,
    duration: Long = 3000L,
    position: ToastPosition = ToastPosition.BOTTOM_CENTER
)
```

### Tooltip

Contextual help information.

```kotlin
@Composable
fun Tooltip(
    text: String,
    modifier: Modifier = Modifier(),
    position: TooltipPosition = TooltipPosition.TOP,
    content: @Composable () -> Unit
)
```

---

## Navigation Components

Components for navigation and routing.

### Link

Navigation links with routing + hydration support.

```kotlin
@Composable
fun Link(
    href: String,
    modifier: Modifier = Modifier(),
    target: String? = null,
    rel: String? = null,
    title: String? = null,
    isExternal: Boolean = false,
    isNoFollow: Boolean = false,
    ariaLabel: String? = null,
    ariaDescribedBy: String? = null,
    id: String? = null,
    dataHref: String? = null,
    dataAttributes: Map<String, String> = emptyMap(),
    navigationMode: LinkNavigationMode = LinkNavigationMode.Native,
    fallbackText: String? = null,
    content: @Composable () -> Unit
)

enum class LinkNavigationMode {
    Native,
    Client
}
```

- `navigationMode = LinkNavigationMode.Client` rewrites the rendered `href` to `#` and mirrors the original path into
  `data-href`, which the hydration runtime (and existing Aurora scripts) already watches for smooth client-side scrolls.
- `fallbackText` lets you intentionally inject server-rendered copy for non-hydrated environments; when `null`, Summon
  no
  longer mirrors the original `href` as fallback text, so custom children render without duplicate labels.

### TabLayout

Tab-based navigation.

```kotlin
@Composable
fun TabLayout(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier(),
    tabs: List<TabItem>
)

data class TabItem(
    val label: String,
    val content: @Composable () -> Unit,
    val disabled: Boolean = false,
    val icon: String? = null
)
```

---

## Styling Components

Components that manage global CSS and animation styling concerns live under `codes.yousef.summon.components.styles`.

### GlobalStyle

Apply global CSS styles to the document.

```kotlin
@Composable
fun GlobalStyle(
    css: String
)
```

---

## HTML DSL Components

Complete HTML5 semantic elements for building accessible, SEO-friendly pages. Added in v0.7.0.

**Package**: `codes.yousef.summon.components.html`

For full documentation, see the [HTML DSL API Reference](html-dsl.md).

### Overview

The HTML DSL provides composable functions that render actual HTML5 semantic elements:

| Category        | Elements                                                                                                                     |
|-----------------|------------------------------------------------------------------------------------------------------------------------------|
| **Structural**  | `Header`, `Nav`, `Main`, `Footer`, `Section`, `Article`, `Aside`, `Address`, `Hgroup`, `Search`                              |
| **Text**        | `H1`-`H6`, `P`, `Blockquote`, `Pre`, `Code`, `Strong`, `Em`, `Small`, `Mark`, `Del`, `Ins`, `Sub`, `Sup`, `S`, `U`, `B`, `I` |
| **Lists**       | `Ul`, `Ol`, `Li`, `Dl`, `Dt`, `Dd`, `Menu`                                                                                   |
| **Tables**      | `Table`, `Thead`, `Tbody`, `Tfoot`, `Tr`, `Th`, `Td`, `Caption`, `Colgroup`, `Col`                                           |
| **Interactive** | `Details`, `Summary`, `Dialog`                                                                                               |
| **Inline**      | `A`, `Span`, `Time`, `Abbr`, `Cite`, `Q`, `Kbd`, `Samp`, `Var`, `Dfn`, `Data`, `Bdi`, `Bdo`, `Ruby`, `Rt`, `Rp`, `Wbr`, `Br` |
| **Media**       | `Figure`, `Figcaption`, `Iframe`, `Embed`, `ObjectTag`, `Param`, `Source`, `Track`, `Audio`, `Meter`                         |

### Quick Example

```kotlin
Article {
    Header {
        H1 { Text("Article Title") }
        Time(datetime = "2026-02-05") { Text("February 5, 2026") }
    }

    Section {
        H2 { Text("Introduction") }
        P { Text("This is a semantic HTML article...") }

        Blockquote(cite = "https://example.com") {
            P { Text("A notable quote from an expert.") }
        }
    }

    Details {
        Summary { Text("Read more") }
        P { Text("Hidden content revealed when expanded.") }
    }

    Footer {
        Small { Text("Copyright 2026") }
    }
}
```

---

## Desktop Components

Desktop-like functionality for web applications. Added in v0.7.0.

**Package**: `codes.yousef.summon.desktop`

For full documentation, see the [Desktop Features API Reference](desktop.md).

### Available Features

| Feature              | Package          | Description                          |
|----------------------|------------------|--------------------------------------|
| **WindowManager**    | `.window`        | Open/manage browser windows and tabs |
| **BroadcastChannel** | `.communication` | Cross-tab real-time messaging        |
| **SyncedStorage**    | `.storage`       | Cross-tab reactive state persistence |
| **FileDialog**       | `.dialog`        | Native file picker dialogs           |
| **MenuBar**          | `.menu`          | Application menu bar component       |
| **SystemTray**       | `.tray`          | Web notifications (tray icon stub)   |
| **DragCoordinator**  | `.communication` | Cross-window drag and drop           |
| **PictureInPicture** | `.pip`           | Floating PiP windows                 |

### Quick Example

```kotlin
// Menu bar with keyboard shortcuts
MenuBar {
    menu("File") {
        item("New", shortcut = KeyboardShortcut("N", ctrl = true)) { createNew() }
        item("Open", shortcut = KeyboardShortcut("O", ctrl = true)) { openFile() }
        separator()
        item("Exit") { exit() }
    }
}

// Cross-tab synced state
@Composable
fun ThemeSwitcher() {
    val theme = rememberSyncedString("theme", "light")
    Button(onClick = { theme.value = if (theme.value == "light") "dark" else "light" }) {
        Text("Theme: ${theme.value}")
    }
}

// Cross-tab messaging
val channel = createBroadcastChannel("my-app")
channel.postMessage("Hello from this tab!")
```

---

## Component Architecture

### Modifier System

All components use the Modifier system for styling and behavior:

```kotlin
// Type-safe styling
Button(
    onClick = { },
    label = "Click Me",
    modifier = Modifier()
        .padding("16px")
        .backgroundColor("#007bff")
        .borderRadius("4px")
        .hover(
            Modifier().backgroundColor("#0056b3")
        )
)
```

### Accessibility Support

Components include built-in accessibility features:

- ARIA attributes and roles
- Keyboard navigation support
- Screen reader compatibility
- Focus management
- Semantic HTML elements

### Platform Renderer Integration

All components work through the platform renderer system:

```kotlin
@Composable
fun MyComponent() {
    val renderer = LocalPlatformRenderer.current
    // Component uses renderer for platform-specific output
}
```

### Event Handling

Components support event handling through lambda callbacks:

```kotlin
Button(
    onClick = { println("Clicked!") },
    label = "Click Me"
)

TextField(
    value = state.value,
    onValueChange = { state.value = it }
)
```

## Best Practices

1. **Use semantic components**: Choose the right component for the content (e.g., `Label` for form labels, `Text` for
   general text)
2. **Apply accessibility**: Use ARIA attributes and semantic HTML when needed
3. **Style with Modifier**: Use the type-safe Modifier system instead of inline styles
4. **Handle errors gracefully**: Provide error states and validation feedback
5. **Keep components focused**: Each component should have a single, clear purpose
6. **Use composition**: Combine simple components to build complex UIs
7. **Test across platforms**: Ensure components work on both JVM and JS targets

## Migration Notes

When upgrading from older versions:

- Replace deprecated `TextComponent` class usage with `Text` composable function
- Update modifier chains to use type-safe enums where available
- Check component signatures for new required or optional parameters

## See Also

- [Modifier API](modifier.md) - Type-safe styling system
- [State API](state.md) - State management for components
- [Theme API](theme.md) - Theming and design system
- [Accessibility API](accessibility.md) - Accessibility features and guidelines
