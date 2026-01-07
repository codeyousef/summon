# Alert Component System

The Alert component system provides a comprehensive solution for displaying contextual feedback messages to users in
Summon applications. It includes the main Alert component, AlertVariant enum for different message types, and
AlertBuilder for fluent construction.

## Overview

Alerts are crucial for communicating important information, status updates, and feedback to users. The Summon Alert
system provides:

- **Semantic Variants**: INFO, SUCCESS, WARNING, ERROR, and NEUTRAL alerts
- **Flexible Content**: Support for icons, titles, messages, and action buttons
- **Accessibility**: Built-in ARIA attributes and screen reader support
- **Dismissible**: Optional close functionality with callbacks
- **Builder Pattern**: Fluent API for complex alert configurations
- **Cross-Platform**: Consistent behavior across browser and JVM environments

## Basic Usage

### Simple Text Alert

```kotlin
import codes.yousef.summon.components.feedback.*

@Composable
fun SimpleAlertExample() {
    Alert(
        message = "Your changes have been saved successfully!",
        variant = AlertVariant.SUCCESS
    )
}
```

### Alert with Title and Dismiss

```kotlin
@Composable
fun DismissibleAlertExample() {
    var showAlert by remember { mutableStateOf(true) }

    if (showAlert) {
        Alert(
            title = "Important Update",
            message = "Please review the new terms of service before continuing.",
            variant = AlertVariant.WARNING,
            onDismiss = { showAlert = false }
        )
    }
}
```

### Alert with Custom Actions

```kotlin
@Composable
fun ActionAlertExample() {
    Alert(
        title = "Confirm Deletion",
        message = "Are you sure you want to delete this item? This action cannot be undone.",
        variant = AlertVariant.ERROR,
        actions = {
            Row(horizontalSpacing = "8px") {
                Button(
                    text = "Cancel",
                    variant = ButtonVariant.GHOST,
                    onClick = { /* handle cancel */ }
                )
                Button(
                    text = "Delete",
                    variant = ButtonVariant.DANGER,
                    onClick = { /* handle delete */ }
                )
            }
        }
    )
}
```

## API Reference

### Alert Component

```kotlin
@Composable
fun Alert(
    modifier: Modifier = Modifier(),
    variant: AlertVariant = AlertVariant.INFO,
    onDismiss: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
)

// Convenience overload for simple text alerts
@Composable
fun Alert(
    message: String,
    modifier: Modifier = Modifier(),
    variant: AlertVariant = AlertVariant.INFO,
    onDismiss: (() -> Unit)? = null,
    title: String? = null,
    icon: (@Composable () -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null
)
```

#### Parameters

| Parameter             | Type                        | Default             | Description                                 |
|-----------------------|-----------------------------|---------------------|---------------------------------------------|
| `message` / `content` | `String` / `@Composable`    | Required            | Main alert content                          |
| `modifier`            | `Modifier`                  | `Modifier()`        | Styling and layout modifier                 |
| `variant`             | `AlertVariant`              | `AlertVariant.INFO` | Alert type affecting styling and icon       |
| `onDismiss`           | `(() -> Unit)?`             | `null`              | Callback when alert is dismissed            |
| `title`               | `String?` / `@Composable?`  | `null`              | Optional title for the alert                |
| `icon`                | `(@Composable () -> Unit)?` | `null`              | Custom icon (uses default based on variant) |
| `actions`             | `(@Composable () -> Unit)?` | `null`              | Action buttons or controls                  |

### AlertVariant

Enum defining the semantic types of alerts with corresponding visual styles.

```kotlin
enum class AlertVariant {
    SUCCESS,  // Green styling for success messages
    WARNING,  // Amber styling for warnings
    ERROR,    // Red styling for errors
    INFO,     // Blue styling for information
    NEUTRAL   // Gray styling for neutral messages
}
```

#### Variant Styling

| Variant   | Background            | Border          | Text Color            | Default Icon |
|-----------|-----------------------|-----------------|-----------------------|--------------|
| `SUCCESS` | Light green (#e8f5e9) | Green (#4caf50) | Dark green (#1b5e20)  | CheckCircle  |
| `WARNING` | Light amber (#fff8e1) | Amber (#ffc107) | Dark orange (#ff6f00) | Warning      |
| `ERROR`   | Light red (#ffebee)   | Red (#f44336)   | Dark red (#b71c1c)    | Error        |
| `INFO`    | Light blue (#e3f2fd)  | Blue (#2196f3)  | Dark blue (#0d47a1)   | Info         |
| `NEUTRAL` | Light gray (#f5f5f5)  | Gray (#9e9e9e)  | Dark gray (#212121)   | Info         |

### AlertBuilder

Fluent builder pattern for complex alert configurations.

```kotlin
class AlertBuilder {
    var message: String
    var title: String?
    var variant: AlertVariant
    var onDismiss: (() -> Unit)?
    var icon: (@Composable () -> Unit)?
    var actions: (@Composable () -> Unit)?
    var modifier: Modifier
}

fun alert(block: AlertBuilder.() -> Unit)
```

## Advanced Examples

### Multi-Step Alert with Progress

```kotlin
@Composable
fun ProgressAlert() {
    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 3

    Alert(
        variant = AlertVariant.INFO,
        icon = {
            CircularProgress(
                progress = currentStep.toFloat() / totalSteps,
                size = "24px"
            )
        },
        title = {
            Text("Setup Progress ($currentStep/$totalSteps)")
        },
        content = {
            Column(verticalSpacing = "8px") {
                Text("Setting up your account...")

                when (currentStep) {
                    1 -> Text("Creating user profile...")
                    2 -> Text("Configuring preferences...")
                    3 -> Text("Finalizing setup...")
                }

                ProgressBar(
                    progress = currentStep.toFloat() / totalSteps,
                    modifier = Modifier().width("100%").marginTop("8px")
                )
            }
        },
        actions = {
            if (currentStep < totalSteps) {
                Button(
                    text = "Continue",
                    onClick = { currentStep++ }
                )
            } else {
                Button(
                    text = "Finish",
                    variant = ButtonVariant.PRIMARY,
                    onClick = { /* complete setup */ }
                )
            }
        }
    )
}
```

### Form Validation Alert

```kotlin
@Composable
fun FormValidationAlert(
    errors: List<String>,
    onClose: () -> Unit
) {
    if (errors.isNotEmpty()) {
        Alert(
            variant = AlertVariant.ERROR,
            title = "Please correct the following errors:",
            onDismiss = onClose,
            content = {
                Column(verticalSpacing = "4px") {
                    errors.forEach { error ->
                        Row(
                            horizontalSpacing = "8px",
                            verticalAlignment = "flex-start"
                        ) {
                            Text("•", color = "#f44336")
                            Text(error)
                        }
                    }
                }
            },
            actions = {
                Button(
                    text = "Fix Issues",
                    variant = ButtonVariant.PRIMARY,
                    onClick = onClose
                )
            }
        )
    }
}
```

### Notification Center Alert

```kotlin
@Composable
fun NotificationAlert(
    notification: Notification,
    onMarkAsRead: () -> Unit,
    onAction: () -> Unit
) {
    Alert(
        variant = when (notification.type) {
            NotificationType.INFO -> AlertVariant.INFO
            NotificationType.SUCCESS -> AlertVariant.SUCCESS
            NotificationType.WARNING -> AlertVariant.WARNING
            NotificationType.ERROR -> AlertVariant.ERROR
        },
        icon = {
            Box(
                modifier = Modifier()
                    .width("40px")
                    .height("40px")
                    .borderRadius("50%")
                    .backgroundColor("#E3F2FD")
                    .display("flex")
                    .alignItems("center")
                    .justifyContent("center")
            ) {
                Avatar(
                    imageUrl = notification.senderAvatar,
                    name = notification.senderName,
                    size = AvatarSize.SMALL
                )
            }
        },
        title = {
            Row(
                horizontalSpacing = "8px",
                verticalAlignment = "center"
            ) {
                Text(
                    text = notification.title,
                    fontWeight = "semibold"
                )
                if (!notification.isRead) {
                    Badge(
                        text = "New",
                        color = "#1976D2",
                        size = BadgeSize.SMALL
                    )
                }
            }
        },
        content = {
            Column(verticalSpacing = "8px") {
                Text(notification.message)
                Text(
                    text = formatTimeAgo(notification.timestamp),
                    fontSize = "12px",
                    color = "#666666"
                )
            }
        },
        actions = {
            Row(horizontalSpacing = "8px") {
                if (!notification.isRead) {
                    Button(
                        text = "Mark as Read",
                        variant = ButtonVariant.GHOST,
                        size = ButtonSize.SMALL,
                        onClick = onMarkAsRead
                    )
                }
                if (notification.actionLabel != null) {
                    Button(
                        text = notification.actionLabel,
                        variant = ButtonVariant.PRIMARY,
                        size = ButtonSize.SMALL,
                        onClick = onAction
                    )
                }
            }
        }
    )
}
```

### Toast-Style Alert

```kotlin
@Composable
fun ToastAlert(
    message: String,
    variant: AlertVariant = AlertVariant.SUCCESS,
    duration: Long = 3000L,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(duration)
        onDismiss()
    }

    Alert(
        message = message,
        variant = variant,
        onDismiss = onDismiss,
        modifier = Modifier()
            .position("fixed")
            .top("20px")
            .right("20px")
            .minWidth("300px")
            .maxWidth("500px")
            .borderRadius("8px")
            .boxShadow("0 4px 12px rgba(0, 0, 0, 0.15)")
            .zIndex("1000")
            .animation("slideInFromRight 0.3s ease-out")
    )
}
```

## Builder Pattern Usage

### Simple Builder Usage

```kotlin
@Composable
fun BuilderAlertExample() {
    alert {
        message = "File uploaded successfully!"
        variant = AlertVariant.SUCCESS
        onDismiss = { /* handle dismiss */ }
    }
}
```

### Complex Builder Configuration

```kotlin
@Composable
fun ComplexBuilderExample() {
    alert {
        title = "System Maintenance"
        message = "The system will be under maintenance from 2:00 AM to 4:00 AM UTC."
        variant = AlertVariant.WARNING

        icon = {
            Icon(
                name = "maintenance",
                size = "24px",
                color = "#ff6f00"
            )
        }

        actions = {
            Row(horizontalSpacing = "8px") {
                Button(
                    text = "Schedule Reminder",
                    variant = ButtonVariant.GHOST,
                    onClick = { /* schedule reminder */ }
                )
                Button(
                    text = "Learn More",
                    onClick = { /* open details */ }
                )
            }
        }

        onDismiss = { /* handle dismiss */ }

        modifier = Modifier()
            .marginBottom("16px")
            .borderRadius("8px")
    }
}
```

## Alert Queue Management

### Alert Manager

```kotlin
class AlertManager {
    private val _alerts = mutableStateListOf<AlertData>()
    val alerts: List<AlertData> = _alerts

    fun showAlert(
        message: String,
        variant: AlertVariant = AlertVariant.INFO,
        duration: Long? = null,
        actions: (@Composable () -> Unit)? = null
    ) {
        val alert = AlertData(
            id = generateId(),
            message = message,
            variant = variant,
            timestamp = Clock.System.now(),
            duration = duration,
            actions = actions
        )
        _alerts.add(alert)

        // Auto-dismiss if duration is specified
        duration?.let {
            CoroutineScope(Dispatchers.Default).launch {
                delay(it)
                dismissAlert(alert.id)
            }
        }
    }

    fun dismissAlert(id: String) {
        _alerts.removeAll { it.id == id }
    }

    fun clearAll() {
        _alerts.clear()
    }
}

@Composable
fun AlertContainer(alertManager: AlertManager) {
    Column(
        modifier = Modifier()
            .position("fixed")
            .top("20px")
            .right("20px")
            .zIndex("1000"),
        verticalSpacing = "12px"
    ) {
        alertManager.alerts.forEach { alert ->
            ToastAlert(
                message = alert.message,
                variant = alert.variant,
                duration = alert.duration ?: 5000L,
                onDismiss = { alertManager.dismissAlert(alert.id) }
            )
        }
    }
}
```

## Accessibility Guidelines

### Screen Reader Support

Alerts automatically include appropriate ARIA attributes:

```kotlin
// Alert automatically includes:
// role="alert" for immediate announcements
// aria-live="polite" or "assertive" based on variant
// proper heading structure

Alert(
    title = "Error",
    message = "Please check your internet connection",
    variant = AlertVariant.ERROR
    // Automatically announced to screen readers
)
```

### Keyboard Navigation

```kotlin
@Composable
fun KeyboardAccessibleAlert() {
    Alert(
        message = "Action required",
        variant = AlertVariant.WARNING,
        actions = {
            Row(horizontalSpacing = "8px") {
                Button(
                    text = "Cancel",
                    onClick = { /* handle cancel */ }
                    // Automatically keyboard accessible
                )
                Button(
                    text = "Confirm",
                    onClick = { /* handle confirm */ },
                    modifier = Modifier().focus() // Auto-focus primary action
                )
            }
        }
    )
}
```

### Color and Contrast

All alert variants meet WCAG AA contrast requirements:

```kotlin
// Built-in high contrast colors
AlertVariant.ERROR   // #b71c1c on #ffebee (4.5:1 ratio)
AlertVariant.SUCCESS // #1b5e20 on #e8f5e9 (4.5:1 ratio)
AlertVariant.WARNING // #ff6f00 on #fff8e1 (4.5:1 ratio)
AlertVariant.INFO    // #0d47a1 on #e3f2fd (4.5:1 ratio)
```

## Platform-Specific Behavior

### Browser Platform

- Automatic ARIA live region announcements
- CSS transitions and animations
- Focus management for keyboard users
- Support for native browser notifications API integration

### JVM Platform

- Text-based alert rendering
- Console output formatting
- Integration with logging frameworks
- Email/SMS notification support

## Performance Considerations

### Memory Management

```kotlin
// Efficient alert cleanup
@Composable
fun ManagedAlerts() {
    val alerts = remember { mutableStateListOf<Alert>() }

    // Cleanup expired alerts
    LaunchedEffect(alerts.size) {
        alerts.removeAll { it.isExpired() }
    }
}
```

### Animation Performance

```kotlin
// Optimize animations for large alert lists
Alert(
    modifier = Modifier()
        .transition("transform 0.2s ease-out") // Light transitions
        .will-change("transform") // Browser optimization hint
)
```

## Testing

### Unit Testing

```kotlin
@Test
fun testAlertRendering() {
    val mockRenderer = MockPlatformRenderer()

    CompositionLocal.provideComposer(MockComposer()) {
        LocalPlatformRenderer.provides(mockRenderer) {
            Alert(
                message = "Test alert",
                variant = AlertVariant.SUCCESS
            )

            assertTrue(mockRenderer.renderAlertContainerCalled)
            assertEquals(AlertVariant.SUCCESS, mockRenderer.lastAlertVariant)
        }
    }
}
```

### Accessibility Testing

```kotlin
@Test
fun testAlertAccessibility() {
    val mockRenderer = MockPlatformRenderer()

    CompositionLocal.provideComposer(MockComposer()) {
        LocalPlatformRenderer.provides(mockRenderer) {
            Alert(
                title = "Error Alert",
                message = "Something went wrong",
                variant = AlertVariant.ERROR
            )

            val attributes = mockRenderer.lastAlertModifier?.attributes
            assertEquals("alert", attributes?.get("role"))
            assertEquals("assertive", attributes?.get("aria-live"))
        }
    }
}
```

### Builder Pattern Testing

```kotlin
@Test
fun testAlertBuilder() {
    val builder = AlertBuilder().apply {
        message = "Test message"
        variant = AlertVariant.WARNING
        title = "Test Title"
    }

    // Verify builder state
    assertEquals("Test message", builder.message)
    assertEquals(AlertVariant.WARNING, builder.variant)
    assertEquals("Test Title", builder.title)
}
```

## Migration Guide

### From HTML/CSS

```html
<!-- HTML -->
<div class="alert alert-success">
  <h4>Success!</h4>
  <p>Your changes have been saved.</p>
  <button class="close">&times;</button>
</div>
```

```kotlin
// Summon equivalent
Alert(
    title = "Success!",
    message = "Your changes have been saved.",
    variant = AlertVariant.SUCCESS,
    onDismiss = { /* handle close */ }
)
```

### From Bootstrap/Material-UI

```jsx
// React Bootstrap
<Alert variant="success" dismissible onClose={handleClose}>
  <Alert.Heading>Well done!</Alert.Heading>
  <p>You successfully completed the task.</p>
</Alert>
```

```kotlin
// Summon equivalent
Alert(
    title = "Well done!",
    message = "You successfully completed the task.",
    variant = AlertVariant.SUCCESS,
    onDismiss = handleClose
)
```

## Best Practices

1. **Choose Appropriate Variants**: Use semantic variants that match the message context
2. **Clear Messaging**: Write concise, actionable alert text
3. **Limit Alert Frequency**: Avoid overwhelming users with too many alerts
4. **Provide Actions**: Include relevant action buttons when appropriate
5. **Auto-Dismiss Appropriately**: Use timeouts for non-critical alerts
6. **Maintain Focus**: Manage keyboard focus for accessibility
7. **Test with Screen Readers**: Verify announcements work correctly
8. **Consider Mobile**: Ensure alerts work well on small screens

The Alert component system provides a robust foundation for user feedback across your Summon application, ensuring
accessibility, consistency, and excellent user experience.