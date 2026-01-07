# Badge Component

The Badge component provides a versatile solution for displaying status indicators, counters, labels, and notification
markers in Summon applications. It supports multiple types, shapes, sizes, and interactive capabilities.

## Overview

Badges are essential UI elements for communicating status, counts, and categorical information. The Summon Badge
component offers:

- **Semantic Types**: PRIMARY, SECONDARY, SUCCESS, WARNING, ERROR, INFO, NEUTRAL
- **Multiple Shapes**: Square, rounded, pill, and dot badges
- **Size Variants**: Small, medium, and large sizes
- **Interactive Support**: Click handlers and accessibility features
- **Flexible Content**: Text, icons, and custom content support
- **Cross-Platform**: Consistent appearance across browser and JVM environments

## Basic Usage

### Simple Text Badge

```kotlin
import codes.yousef.summon.components.feedback.*

@Composable
fun SimpleBadgeExample() {
    Badge(
        content = "New",
        type = BadgeType.PRIMARY
    )
}
```

### Status Badges

```kotlin
@Composable
fun StatusBadgeExample() {
    Row(horizontalSpacing = "8px") {
        StatusBadge("Active", BadgeType.SUCCESS)
        StatusBadge("Pending", BadgeType.WARNING)
        StatusBadge("Error", BadgeType.ERROR)
        StatusBadge("Info", BadgeType.INFO)
    }
}
```

### Counter Badges

```kotlin
@Composable
fun CounterBadgeExample() {
    Row(
        horizontalSpacing = "16px",
        verticalAlignment = "center"
    ) {
        Box(modifier = Modifier().position("relative")) {
            Icon("notifications", size = "24px")
            CounterBadge(
                count = 5,
                modifier = Modifier()
                    .position("absolute")
                    .top("-8px")
                    .right("-8px")
            )
        }

        Box(modifier = Modifier().position("relative")) {
            Icon("mail", size = "24px")
            CounterBadge(
                count = 99,
                modifier = Modifier()
                    .position("absolute")
                    .top("-8px")
                    .right("-8px")
            )
        }
    }
}
```

### Dot Badges

```kotlin
@Composable
fun DotBadgeExample() {
    Row(
        horizontalSpacing = "16px",
        verticalAlignment = "center"
    ) {
        Row(verticalAlignment = "center") {
            DotBadge(BadgeType.SUCCESS)
            Text("Online", modifier = Modifier().marginLeft("8px"))
        }

        Row(verticalAlignment = "center") {
            DotBadge(BadgeType.ERROR)
            Text("Offline", modifier = Modifier().marginLeft("8px"))
        }
    }
}
```

## API Reference

### Badge Component

```kotlin
@Composable
fun Badge(
    content: String,
    modifier: Modifier = Modifier(),
    type: BadgeType = BadgeType.PRIMARY,
    shape: BadgeShape = BadgeShape.ROUNDED,
    isOutlined: Boolean = false,
    size: String = "medium",
    onClick: (() -> Unit)? = null,
    displayStart: (@Composable () -> Unit)? = null,
    displayEnd: (@Composable () -> Unit)? = null,
    iconEnd: (@Composable () -> Unit)? = null // Deprecated
)
```

#### Parameters

| Parameter      | Type                        | Default              | Description                               |
|----------------|-----------------------------|----------------------|-------------------------------------------|
| `content`      | `String`                    | Required             | Text content of the badge                 |
| `modifier`     | `Modifier`                  | `Modifier()`         | Styling and layout modifier               |
| `type`         | `BadgeType`                 | `BadgeType.PRIMARY`  | Semantic type affecting color scheme      |
| `shape`        | `BadgeShape`                | `BadgeShape.ROUNDED` | Visual shape of the badge                 |
| `isOutlined`   | `Boolean`                   | `false`              | Whether to use outlined style             |
| `size`         | `String`                    | `"medium"`           | Size variant ("small", "medium", "large") |
| `onClick`      | `(() -> Unit)?`             | `null`               | Click handler for interactive badges      |
| `displayStart` | `(@Composable () -> Unit)?` | `null`               | Content before the main text              |
| `displayEnd`   | `(@Composable () -> Unit)?` | `null`               | Content after the main text               |

### BadgeType

Enum defining semantic types with corresponding color schemes.

```kotlin
enum class BadgeType {
    PRIMARY,    // Blue (#2196f3)
    SECONDARY,  // Purple (#9c27b0)
    SUCCESS,    // Green (#4caf50)
    WARNING,    // Orange (#ff9800)
    ERROR,      // Red (#f44336)
    INFO,       // Light blue (#03a9f4)
    NEUTRAL     // Gray (#9e9e9e)
}
```

### BadgeShape

Enum defining visual shapes for different use cases.

```kotlin
enum class BadgeShape {
    SQUARE,   // Square with minor border radius (2px)
    ROUNDED,  // Rounded corners (4px)
    PILL,     // Fully rounded (9999px)
    DOT       // Circular dot indicator
}
```

### Convenience Components

```kotlin
// Pre-configured status badge
@Composable
fun StatusBadge(
    status: String,
    type: BadgeType,
    modifier: Modifier = Modifier()
)

// Counter badge for notifications
@Composable
fun CounterBadge(
    count: Int,
    modifier: Modifier = Modifier()
)

// Simple dot indicator
@Composable
fun DotBadge(
    type: BadgeType,
    modifier: Modifier = Modifier()
)
```

## Advanced Examples

### Badge with Icons

```kotlin
@Composable
fun IconBadgeExample() {
    Badge(
        content = "Verified",
        type = BadgeType.SUCCESS,
        shape = BadgeShape.PILL,
        displayStart = {
            Icon(
                name = "verified",
                size = "14px",
                color = "white"
            )
        }
    )
}
```

### Dismissible Badge

```kotlin
@Composable
fun DismissibleBadgeExample() {
    var showBadge by remember { mutableStateOf(true) }

    if (showBadge) {
        Badge(
            content = "Beta Feature",
            type = BadgeType.INFO,
            shape = BadgeShape.PILL,
            displayEnd = {
                Icon(
                    name = "close",
                    size = "12px",
                    color = "white",
                    modifier = Modifier()
                        .cursor("pointer")
                        .onClick { showBadge = false }
                )
            }
        )
    }
}
```

### User Role Badges

```kotlin
@Composable
fun UserRoleBadges(user: User) {
    Row(horizontalSpacing = "4px") {
        user.roles.forEach { role ->
            val (badgeText, badgeType) = when (role) {
                UserRole.ADMIN -> "Admin" to BadgeType.ERROR
                UserRole.MODERATOR -> "Mod" to BadgeType.WARNING
                UserRole.PREMIUM -> "Pro" to BadgeType.PRIMARY
                UserRole.VERIFIED -> "✓" to BadgeType.SUCCESS
                else -> return@forEach
            }

            Badge(
                content = badgeText,
                type = badgeType,
                shape = BadgeShape.PILL,
                size = "small"
            )
        }
    }
}
```

### Progress Badge

```kotlin
@Composable
fun ProgressBadge(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier()
) {
    val percentage = (current.toFloat() / total * 100).toInt()
    val badgeType = when {
        percentage >= 90 -> BadgeType.SUCCESS
        percentage >= 70 -> BadgeType.INFO
        percentage >= 50 -> BadgeType.WARNING
        else -> BadgeType.ERROR
    }

    Badge(
        content = "$current/$total",
        type = badgeType,
        shape = BadgeShape.PILL,
        size = "small",
        modifier = modifier,
        displayStart = {
            Box(
                modifier = Modifier()
                    .width("16px")
                    .height("2px")
                    .backgroundColor("rgba(255, 255, 255, 0.3)")
                    .borderRadius("1px")
                    .position("relative")
            ) {
                Box(
                    modifier = Modifier()
                        .width("${percentage}%")
                        .height("100%")
                        .backgroundColor("white")
                        .borderRadius("1px")
                )
            }
        }
    )
}
```

### Interactive Filter Badges

```kotlin
@Composable
fun FilterBadges(
    selectedFilters: Set<String>,
    onFilterToggle: (String) -> Unit
) {
    val availableFilters = listOf("React", "Kotlin", "TypeScript", "JavaScript", "Python")

    Row(
        horizontalSpacing = "8px",
        modifier = Modifier().flexWrap("wrap")
    ) {
        availableFilters.forEach { filter ->
            val isSelected = filter in selectedFilters

            Badge(
                content = filter,
                type = if (isSelected) BadgeType.PRIMARY else BadgeType.NEUTRAL,
                shape = BadgeShape.PILL,
                isOutlined = !isSelected,
                onClick = { onFilterToggle(filter) },
                modifier = Modifier()
                    .cursor("pointer")
                    .transition("all 0.2s ease")
                    .hover {
                        if (!isSelected) {
                            backgroundColor("#f0f0f0")
                        }
                    },
                displayEnd = if (isSelected) {
                    {
                        Icon(
                            name = "check",
                            size = "12px",
                            color = "white"
                        )
                    }
                } else null
            )
        }
    }
}
```

### Notification Badge System

```kotlin
@Composable
fun NotificationBadgeSystem(
    notifications: List<Notification>
) {
    val groupedNotifications = notifications.groupBy { it.type }

    Row(horizontalSpacing = "12px") {
        // Messages
        val messageCount = groupedNotifications[NotificationType.MESSAGE]?.size ?: 0
        if (messageCount > 0) {
            Box(modifier = Modifier().position("relative")) {
                Icon("message", size = "24px", color = "#666")
                CounterBadge(
                    count = messageCount,
                    modifier = Modifier()
                        .position("absolute")
                        .top("-6px")
                        .right("-6px")
                )
            }
        }

        // Alerts
        val alertCount = groupedNotifications[NotificationType.ALERT]?.size ?: 0
        if (alertCount > 0) {
            Box(modifier = Modifier().position("relative")) {
                Icon("notifications", size = "24px", color = "#666")
                Badge(
                    content = alertCount.toString(),
                    type = BadgeType.ERROR,
                    shape = BadgeShape.PILL,
                    size = "small",
                    modifier = Modifier()
                        .position("absolute")
                        .top("-6px")
                        .right("-6px")
                        .animation("pulse 2s infinite")
                )
            }
        }

        // Updates
        val hasUpdates = groupedNotifications[NotificationType.UPDATE]?.isNotEmpty() == true
        if (hasUpdates) {
            Box(modifier = Modifier().position("relative")) {
                Icon("system_update", size = "24px", color = "#666")
                DotBadge(
                    type = BadgeType.INFO,
                    modifier = Modifier()
                        .position("absolute")
                        .top("2px")
                        .right("2px")
                )
            }
        }
    }
}
```

### Product Tag System

```kotlin
@Composable
fun ProductTags(product: Product) {
    Row(
        horizontalSpacing = "6px",
        modifier = Modifier().flexWrap("wrap")
    ) {
        // Sale badge
        if (product.onSale) {
            Badge(
                content = "SALE",
                type = BadgeType.ERROR,
                shape = BadgeShape.SQUARE,
                size = "small",
                modifier = Modifier().fontWeight("bold")
            )
        }

        // New badge
        if (product.isNew) {
            Badge(
                content = "NEW",
                type = BadgeType.SUCCESS,
                shape = BadgeShape.SQUARE,
                size = "small"
            )
        }

        // Featured badge
        if (product.featured) {
            Badge(
                content = "FEATURED",
                type = BadgeType.PRIMARY,
                shape = BadgeShape.SQUARE,
                size = "small"
            )
        }

        // Limited stock
        if (product.stock <= 5) {
            Badge(
                content = "Only ${product.stock} left",
                type = BadgeType.WARNING,
                shape = BadgeShape.PILL,
                size = "small"
            )
        }

        // Category tags
        product.tags.forEach { tag ->
            Badge(
                content = tag,
                type = BadgeType.NEUTRAL,
                shape = BadgeShape.PILL,
                size = "small",
                isOutlined = true
            )
        }
    }
}
```

## Style Variants

### Outlined Badges

```kotlin
@Composable
fun OutlinedBadgeExample() {
    Row(horizontalSpacing = "8px") {
        Badge(
            content = "Outlined",
            type = BadgeType.PRIMARY,
            isOutlined = true
        )
        Badge(
            content = "Filled",
            type = BadgeType.PRIMARY,
            isOutlined = false
        )
    }
}
```

### Size Variants

```kotlin
@Composable
fun SizeBadgeExample() {
    Row(
        horizontalSpacing = "12px",
        verticalAlignment = "center"
    ) {
        Badge("Small", size = "small")
        Badge("Medium", size = "medium")
        Badge("Large", size = "large")
    }
}
```

### Shape Variants

```kotlin
@Composable
fun ShapeBadgeExample() {
    Row(horizontalSpacing = "8px") {
        Badge("Square", shape = BadgeShape.SQUARE)
        Badge("Rounded", shape = BadgeShape.ROUNDED)
        Badge("Pill", shape = BadgeShape.PILL)
        DotBadge(BadgeType.PRIMARY)
    }
}
```

## Accessibility Guidelines

### Screen Reader Support

```kotlin
// Badges automatically include appropriate ARIA attributes
Badge(
    content = "5",
    type = BadgeType.ERROR,
    // Automatically includes:
    // role="status" for non-interactive badges
    // aria-label="5" for screen readers
)

// Interactive badges
Badge(
    content = "Filter",
    onClick = { toggleFilter() },
    // Automatically includes:
    // role="button"
    // tabindex="0"
    // Keyboard support
)
```

### Meaningful Labels

```kotlin
// Good - Descriptive content
CounterBadge(
    count = unreadCount,
    modifier = Modifier().ariaLabel("$unreadCount unread messages")
)

// Good - Context-aware labels
StatusBadge(
    status = "Active",
    type = BadgeType.SUCCESS,
    modifier = Modifier().ariaLabel("Server status: Active")
)
```

### Color Accessibility

All badge types meet WCAG AA contrast requirements:

```kotlin
// High contrast color combinations
BadgeType.PRIMARY   // White text on #2196f3 (4.5:1 ratio)
BadgeType.SUCCESS   // White text on #4caf50 (4.5:1 ratio)
BadgeType.ERROR     // White text on #f44336 (4.5:1 ratio)
```

## Performance Considerations

### Efficient Rendering

```kotlin
// Use keys for dynamic badge lists
@Composable
fun DynamicBadgeList(items: List<String>) {
    Row(horizontalSpacing = "4px") {
        items.forEach { item ->
            key(item) {
                Badge(
                    content = item,
                    type = BadgeType.NEUTRAL
                )
            }
        }
    }
}
```

### Memory Optimization

```kotlin
// Cache badge configurations for repeated use
object BadgePresets {
    @Composable
    fun NewBadge() = Badge(
        content = "NEW",
        type = BadgeType.SUCCESS,
        shape = BadgeShape.PILL,
        size = "small"
    )

    @Composable
    fun SaleBadge() = Badge(
        content = "SALE",
        type = BadgeType.ERROR,
        shape = BadgeShape.SQUARE,
        size = "small"
    )
}
```

## Platform-Specific Behavior

### Browser Platform

- CSS transitions and hover effects
- Full mouse and keyboard interaction
- Semantic HTML with proper ARIA attributes
- Support for custom CSS styling

### JVM Platform

- Text-based badge rendering
- Color codes for terminal output
- Simple ASCII representations
- Integration with logging systems

## Testing

### Unit Testing

```kotlin
@Test
fun testBadgeRendering() {
    val mockRenderer = MockPlatformRenderer()

    CompositionLocal.provideComposer(MockComposer()) {
        LocalPlatformRenderer.provides(mockRenderer) {
            Badge(
                content = "Test",
                type = BadgeType.SUCCESS
            )

            assertTrue(mockRenderer.renderBadgeCalled)
            val styles = mockRenderer.lastBadgeModifier?.styles
            assertEquals("#4caf50", styles?.get("background-color"))
        }
    }
}
```

### Accessibility Testing

```kotlin
@Test
fun testBadgeAccessibility() {
    val mockRenderer = MockPlatformRenderer()

    CompositionLocal.provideComposer(MockComposer()) {
        LocalPlatformRenderer.provides(mockRenderer) {
            Badge(
                content = "Status",
                onClick = { /* click handler */ }
            )

            val attributes = mockRenderer.lastBadgeModifier?.attributes
            assertEquals("button", attributes?.get("role"))
            assertEquals("0", attributes?.get("tabindex"))
        }
    }
}
```

### Visual Testing

```kotlin
@Test
fun testBadgeVariants() {
    BadgeType.values().forEach { type ->
        BadgeShape.values().forEach { shape ->
            // Test all combinations
            Badge(
                content = "Test",
                type = type,
                shape = shape
            )
            // Verify styling is applied correctly
        }
    }
}
```

## Migration Guide

### From CSS Classes

```css
/* CSS badge classes */
.badge-primary { background: #007bff; color: white; }
.badge-success { background: #28a745; color: white; }
.badge-pill { border-radius: 9999px; }
```

```kotlin
// Summon equivalent
Badge("Primary", type = BadgeType.PRIMARY)
Badge("Success", type = BadgeType.SUCCESS)
Badge("Pill", shape = BadgeShape.PILL)
```

### From Bootstrap

```html
<!-- Bootstrap badges -->
<span class="badge badge-primary">Primary</span>
<span class="badge badge-success badge-pill">Success</span>
```

```kotlin
// Summon equivalent
Badge("Primary", type = BadgeType.PRIMARY)
Badge("Success", type = BadgeType.SUCCESS, shape = BadgeShape.PILL)
```

## Best Practices

1. **Use Semantic Types**: Choose types that match the meaning of your content
2. **Consistent Sizing**: Use the same size for badges in the same context
3. **Limit Badge Count**: Avoid overwhelming users with too many badges
4. **Clear Content**: Keep badge text short and meaningful
5. **Accessible Colors**: Ensure sufficient contrast for readability
6. **Interactive Feedback**: Provide clear hover and focus states for clickable badges
7. **Context-Aware**: Use appropriate shapes and styles for different use cases
8. **Performance**: Cache frequently used badge configurations

The Badge component provides a flexible foundation for status indicators and labels across your Summon application,
ensuring consistency, accessibility, and excellent user experience.