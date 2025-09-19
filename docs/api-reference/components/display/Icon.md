# Icon Component

The Icon component provides a versatile, cross-platform solution for displaying icons in Summon applications. It
supports multiple icon types including SVG vectors, font-based icons (Material Icons, Font Awesome), and image
fallbacks.

## Overview

Icons are essential UI elements that provide visual cues and enhance user experience. The Summon Icon component offers:

- **Multiple Icon Types**: SVG, font-based, and image icons
- **Accessibility First**: Built-in ARIA labels and semantic roles
- **Interactive Support**: Click handlers with proper accessibility
- **Type-Safe Styling**: Full modifier system integration
- **Cross-Platform**: Consistent rendering across browser and JVM environments

## Basic Usage

### Simple Icon

```kotlin
import code.yousef.summon.components.display.Icon
import code.yousef.summon.components.display.IconType

@Composable
fun SimpleIconExample() {
    Icon(
        name = "home",
        modifier = Modifier()
            .size("24px")
            .color("#333333")
    )
}
```

### Material Design Icon

```kotlin
import code.yousef.summon.components.display.MaterialIcon

@Composable
fun MaterialIconExample() {
    MaterialIcon(
        name = "favorite",
        size = "32px",
        color = "#FF5722",
        modifier = Modifier().padding("8px")
    )
}
```

### SVG Icon

```kotlin
import code.yousef.summon.components.display.SvgIcon

@Composable
fun SvgIconExample() {
    val heartSvg = """
        <svg viewBox="0 0 24 24">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
        </svg>
    """.trimIndent()

    SvgIcon(
        svgContent = heartSvg,
        ariaLabel = "Favorite",
        modifier = Modifier()
            .size("20px")
            .color("#E91E63")
    )
}
```

## API Reference

### Icon

The main icon component that supports all icon types.

```kotlin
@Composable
fun Icon(
    name: String,
    modifier: Modifier = Modifier(),
    size: String? = null,
    color: String? = null,
    type: IconType = IconType.SVG,
    fontFamily: String? = null,
    svgContent: String? = null,
    ariaLabel: String? = null,
    onClick: (() -> Unit)? = null
)
```

#### Parameters

| Parameter    | Type            | Default        | Description                         |
|--------------|-----------------|----------------|-------------------------------------|
| `name`       | `String`        | Required       | Icon identifier or ligature name    |
| `modifier`   | `Modifier`      | `Modifier()`   | Styling and layout modifier         |
| `size`       | `String?`       | `null`         | Icon size (prefer using modifier)   |
| `color`      | `String?`       | `null`         | Icon color (prefer using modifier)  |
| `type`       | `IconType`      | `IconType.SVG` | Rendering strategy for the icon     |
| `fontFamily` | `String?`       | `null`         | Font family for font icons          |
| `svgContent` | `String?`       | `null`         | Raw SVG markup for SVG icons        |
| `ariaLabel`  | `String?`       | `null`         | Accessible label for screen readers |
| `onClick`    | `(() -> Unit)?` | `null`         | Click handler for interactive icons |

### IconType

Enum defining different icon rendering strategies.

```kotlin
enum class IconType {
    SVG,    // Vector SVG icons
    FONT,   // Font-based icons (Material Icons, Font Awesome)
    IMAGE   // Image-based icons (fallback)
}
```

### MaterialIcon

Convenience component for Material Design icons.

```kotlin
@Composable
fun MaterialIcon(
    name: String,
    modifier: Modifier = Modifier(),
    size: String = IconDefaults.Size.MEDIUM,
    color: String? = null,
    onClick: (() -> Unit)? = null
)
```

Requires the Material Icons font to be loaded in your application.

### FontAwesomeIcon

Convenience component for Font Awesome icons.

```kotlin
@Composable
fun FontAwesomeIcon(
    name: String,
    modifier: Modifier = Modifier(),
    size: String = IconDefaults.Size.MEDIUM,
    color: String? = null,
    fontFamily: String = "Font Awesome 5 Free",
    onClick: (() -> Unit)? = null
)
```

### SvgIcon

Component for inline SVG icons.

```kotlin
@Composable
fun SvgIcon(
    svgContent: String,
    modifier: Modifier = Modifier(),
    size: String = IconDefaults.Size.MEDIUM,
    color: String? = null,
    ariaLabel: String? = null,
    onClick: (() -> Unit)? = null
)
```

## IconDefaults

Predefined constants and common icons.

### Size Presets

```kotlin
object Size {
    const val SMALL = "16px"
    const val MEDIUM = "24px"
    const val LARGE = "32px"
}
```

### Common Icons

```kotlin
// Basic actions
IconDefaults.Add()
IconDefaults.Delete()
IconDefaults.Edit()
IconDefaults.Download()
IconDefaults.Upload()

// Status indicators
IconDefaults.Info()
IconDefaults.CheckCircle()
IconDefaults.Warning()
IconDefaults.Error()
IconDefaults.Close()
```

## Advanced Examples

### Interactive Icon Button

```kotlin
@Composable
fun IconButton(
    icon: String,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    MaterialIcon(
        name = icon,
        modifier = Modifier()
            .size("24px")
            .padding("8px")
            .borderRadius("4px")
            .backgroundColor(if (enabled) "#E3F2FD" else "#F5F5F5")
            .color(if (enabled) "#1976D2" else "#9E9E9E")
            .cursor(if (enabled) "pointer" else "not-allowed")
            .hover { backgroundColor("#BBDEFB") },
        onClick = if (enabled) onClick else null
    ).apply {
        if (!enabled) {
            ariaLabel("$label (disabled)")
        } else {
            ariaLabel(label)
        }
    }
}
```

### Status Icon with Color Variants

```kotlin
@Composable
fun StatusIcon(
    status: Status,
    modifier: Modifier = Modifier()
) {
    val (iconName, color) = when (status) {
        Status.SUCCESS -> "check_circle" to "#4CAF50"
        Status.WARNING -> "warning" to "#FF9800"
        Status.ERROR -> "error" to "#F44336"
        Status.INFO -> "info" to "#2196F3"
    }

    MaterialIcon(
        name = iconName,
        color = color,
        modifier = modifier.size("20px"),
        ariaLabel = "Status: ${status.name.lowercase()}"
    )
}
```

### Animated Loading Icon

```kotlin
@Composable
fun LoadingIcon(
    modifier: Modifier = Modifier()
) {
    val rotationAnimation = remember {
        keyframes<Float> {
            durationMillis = 1000
            0f at 0
            360f at 1000
        }
    }

    MaterialIcon(
        name = "refresh",
        modifier = modifier
            .size("24px")
            .color("#1976D2")
            .rotate(rotationAnimation)
            .animation(infinite = true),
        ariaLabel = "Loading..."
    )
}
```

### Icon with Badge

```kotlin
@Composable
fun NotificationIcon(
    count: Int,
    modifier: Modifier = Modifier()
) {
    Box(modifier = modifier.position("relative")) {
        MaterialIcon(
            name = "notifications",
            size = "24px",
            ariaLabel = "Notifications"
        )

        if (count > 0) {
            Badge(
                text = if (count > 99) "99+" else count.toString(),
                modifier = Modifier()
                    .position("absolute")
                    .top("-4px")
                    .right("-4px")
                    .backgroundColor("#F44336")
                    .color("white")
                    .borderRadius("50%")
                    .minWidth("18px")
                    .height("18px")
                    .fontSize("12px")
                    .textAlign("center")
            )
        }
    }
}
```

## Accessibility Guidelines

### Screen Reader Support

Always provide meaningful labels for icons:

```kotlin
// Good - Descriptive label
Icon(
    name = "delete",
    ariaLabel = "Delete item",
    onClick = { deleteItem() }
)

// Bad - No label
Icon(
    name = "delete",
    onClick = { deleteItem() }
)
```

### Interactive Icons

For clickable icons, ensure proper semantic roles:

```kotlin
// Automatically adds role="button" for interactive icons
Icon(
    name = "settings",
    ariaLabel = "Open settings",
    onClick = { openSettings() }
)
```

### Color and Contrast

Ensure sufficient color contrast for icon visibility:

```kotlin
// High contrast for better accessibility
Icon(
    name = "warning",
    color = "#D32F2F", // Strong red for warnings
    ariaLabel = "Warning: Action required"
)
```

## Performance Considerations

### Icon Loading Strategies

1. **Font Icons**: Load font files once, instant rendering
2. **SVG Icons**: Inline for small sets, external files for large sets
3. **Image Icons**: Use appropriate formats (WebP, SVG) and sizes

### Optimization Tips

```kotlin
// Use appropriate sizes
Icon(
    name = "home",
    modifier = Modifier()
        .size("16px") // Small for inline text
        .size("24px") // Standard for buttons
        .size("32px") // Large for primary actions
)

// Prefer modifier styling over parameters
Icon(
    name = "star",
    modifier = Modifier()
        .size("20px")
        .color("#FFD700") // Better than color parameter
)
```

## Platform-Specific Behavior

### Browser Platform

- Font icons require font files to be loaded
- SVG icons support full CSS styling
- Click events handled via DOM event listeners
- Supports CSS animations and transitions

### JVM Platform

- Font icons rendered using system fonts or embedded resources
- SVG icons converted to appropriate formats for output
- Click events may require special handling depending on output format
- Animation support varies by rendering target

## Common Patterns

### Icon Library Setup

```kotlin
// Create your icon library
object AppIcons {
    @Composable
    fun Home(modifier: Modifier = Modifier()) =
        MaterialIcon("home", modifier)

    @Composable
    fun User(modifier: Modifier = Modifier()) =
        MaterialIcon("person", modifier)

    @Composable
    fun Settings(modifier: Modifier = Modifier()) =
        MaterialIcon("settings", modifier)
}

// Usage
AppIcons.Home(
    modifier = Modifier()
        .size("24px")
        .color("#1976D2")
)
```

### Responsive Icon Sizes

```kotlin
@Composable
fun ResponsiveIcon(
    name: String,
    modifier: Modifier = Modifier()
) {
    val screenSize = LocalBreakpoint.current
    val iconSize = when (screenSize) {
        Breakpoint.MOBILE -> "20px"
        Breakpoint.TABLET -> "24px"
        Breakpoint.DESKTOP -> "28px"
    }

    MaterialIcon(
        name = name,
        size = iconSize,
        modifier = modifier
    )
}
```

## Testing

### Unit Testing

```kotlin
@Test
fun testIconRendering() {
    val mockRenderer = MockPlatformRenderer()

    CompositionLocal.provideComposer(MockComposer()) {
        LocalPlatformRenderer.provides(mockRenderer) {
            Icon(
                name = "test-icon",
                ariaLabel = "Test Icon"
            )

            assertTrue(mockRenderer.renderIconCalled)
            assertEquals("test-icon", mockRenderer.lastIconNameRendered)
            assertEquals("Test Icon",
                mockRenderer.lastIconModifierRendered?.attributes?.get("aria-label"))
        }
    }
}
```

### Accessibility Testing

```kotlin
@Test
fun testIconAccessibility() {
    val mockRenderer = MockPlatformRenderer()

    CompositionLocal.provideComposer(MockComposer()) {
        LocalPlatformRenderer.provides(mockRenderer) {
            Icon(
                name = "clickable",
                ariaLabel = "Clickable Icon",
                onClick = { }
            )

            val attributes = mockRenderer.lastIconModifierRendered?.attributes
            assertEquals("Clickable Icon", attributes?.get("aria-label"))
            assertEquals("button", attributes?.get("role"))
            assertEquals("pointer", mockRenderer.lastIconModifierRendered?.styles?.get("cursor"))
        }
    }
}
```

## Migration Guide

### From Raw HTML

```html
<!-- Before: Raw HTML -->
<i class="material-icons" style="font-size: 24px; color: #1976D2;">home</i>

<!-- After: Summon Icon -->
```

```kotlin
MaterialIcon(
    name = "home",
    size = "24px",
    color = "#1976D2"
)
```

### From Other UI Frameworks

```kotlin
// React Material-UI equivalent
// <HomeIcon fontSize="large" color="primary" />

MaterialIcon(
    name = "home",
    size = IconDefaults.Size.LARGE,
    color = theme.colors.primary
)
```

## Best Practices

1. **Consistent Sizing**: Use predefined size constants
2. **Meaningful Labels**: Always provide accessible descriptions
3. **Performance**: Choose appropriate icon type for your use case
4. **Theming**: Use theme colors for consistent appearance
5. **Testing**: Test with screen readers and keyboard navigation

The Icon component provides a robust foundation for icon usage across your Summon application, ensuring accessibility,
performance, and cross-platform compatibility.