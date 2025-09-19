# Modal Component

The Modal component provides a comprehensive solution for displaying overlay dialogs, confirmations, alerts, and complex
forms in Summon applications. It offers flexible configuration, accessibility features, and cross-platform
compatibility.

## Overview

Modals are essential for capturing user attention and displaying critical information or interactions. The Summon Modal
component provides:

- **Multiple Variants**: DEFAULT, ALERT, CONFIRMATION, FULLSCREEN
- **Flexible Sizing**: SMALL, MEDIUM, LARGE, EXTRA_LARGE
- **Accessibility First**: Focus management, keyboard navigation, ARIA attributes
- **User Experience**: Backdrop click handling, ESC key support, smooth animations
- **Customizable Content**: Header, body, and footer slots for complete control
- **Cross-Platform**: Consistent behavior across browser and JVM environments

## Basic Usage

### Simple Modal

```kotlin
import code.yousef.summon.components.feedback.*

@Composable
fun SimpleModalExample() {
    var showModal by remember { mutableStateOf(false) }

    Column(verticalSpacing = "16px") {
        Button(
            text = "Open Modal",
            onClick = { showModal = true }
        )

        Modal(
            isOpen = showModal,
            onDismiss = { showModal = false },
            header = {
                Text(
                    text = "Modal Title",
                    fontSize = "20px",
                    fontWeight = "semibold"
                )
            },
            footer = {
                Row(horizontalSpacing = "8px") {
                    Button(
                        text = "Cancel",
                        variant = ButtonVariant.SECONDARY,
                        onClick = { showModal = false }
                    )
                    Button(
                        text = "Save",
                        onClick = {
                            // Handle save
                            showModal = false
                        }
                    )
                }
            }
        ) {
            Text("This is the modal content area where you can place any composable content.")
        }
    }
}
```

### Confirmation Modal

```kotlin
@Composable
fun ConfirmationModalExample() {
    var showConfirmation by remember { mutableStateOf(false) }

    Column(verticalSpacing = "16px") {
        Button(
            text = "Delete Item",
            variant = ButtonVariant.DANGER,
            onClick = { showConfirmation = true }
        )

        ConfirmationModal(
            isOpen = showConfirmation,
            title = "Confirm Deletion",
            message = "Are you sure you want to delete this item? This action cannot be undone.",
            confirmText = "Delete",
            cancelText = "Cancel",
            onConfirm = {
                // Handle deletion
                showConfirmation = false
            },
            onCancel = { showConfirmation = false }
        )
    }
}
```

### Alert Modal

```kotlin
@Composable
fun AlertModalExample() {
    var showAlert by remember { mutableStateOf(false) }

    Column(verticalSpacing = "16px") {
        Button(
            text = "Show Alert",
            onClick = { showAlert = true }
        )

        AlertModal(
            isOpen = showAlert,
            title = "Important Notice",
            message = "Your session will expire in 5 minutes. Please save your work.",
            buttonText = "I Understand",
            onDismiss = { showAlert = false }
        )
    }
}
```

## API Reference

### Modal Component

```kotlin
@Composable
fun Modal(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier(),
    variant: ModalVariant = ModalVariant.DEFAULT,
    size: ModalSize = ModalSize.MEDIUM,
    dismissOnBackdropClick: Boolean = true,
    showCloseButton: Boolean = true,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
)
```

#### Parameters

| Parameter                | Type                        | Default                | Description                            |
|--------------------------|-----------------------------|------------------------|----------------------------------------|
| `isOpen`                 | `Boolean`                   | Required               | Whether the modal is currently visible |
| `onDismiss`              | `() -> Unit`                | Required               | Callback when modal should be closed   |
| `modifier`               | `Modifier`                  | `Modifier()`           | Styling and layout modifier            |
| `variant`                | `ModalVariant`              | `ModalVariant.DEFAULT` | Visual variant of the modal            |
| `size`                   | `ModalSize`                 | `ModalSize.MEDIUM`     | Size of the modal dialog               |
| `dismissOnBackdropClick` | `Boolean`                   | `true`                 | Whether backdrop click dismisses modal |
| `showCloseButton`        | `Boolean`                   | `true`                 | Whether to show close button in header |
| `header`                 | `(@Composable () -> Unit)?` | `null`                 | Header content slot                    |
| `footer`                 | `(@Composable () -> Unit)?` | `null`                 | Footer content slot                    |
| `content`                | `@Composable () -> Unit`    | Required               | Main modal content                     |

### ModalVariant

Enum defining visual and behavioral variants.

```kotlin
enum class ModalVariant {
    DEFAULT,      // Standard modal with default styling
    ALERT,        // Alert modal for important messages
    CONFIRMATION, // Confirmation modal for user decisions
    FULLSCREEN    // Full-screen modal covering entire viewport
}
```

### ModalSize

Enum defining size options for modal dimensions.

```kotlin
enum class ModalSize {
    SMALL,        // ~400px width - confirmations, alerts
    MEDIUM,       // ~600px width - standard dialogs
    LARGE,        // ~800px width - complex forms
    EXTRA_LARGE   // ~1200px width - extensive content
}
```

### Convenience Components

```kotlin
// Pre-configured confirmation modal
@Composable
fun ConfirmationModal(
    isOpen: Boolean,
    title: String,
    message: String,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier()
)

// Pre-configured alert modal
@Composable
fun AlertModal(
    isOpen: Boolean,
    title: String,
    message: String,
    buttonText: String = "OK",
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier()
)
```

## Advanced Examples

### Form Modal

```kotlin
@Composable
fun FormModalExample() {
    var showForm by remember { mutableStateOf(false) }
    var formData by remember { mutableStateOf(UserFormData()) }
    var isSubmitting by remember { mutableStateOf(false) }

    Column(verticalSpacing = "16px") {
        Button(
            text = "Add User",
            onClick = { showForm = true }
        )

        Modal(
            isOpen = showForm,
            onDismiss = { showForm = false },
            size = ModalSize.LARGE,
            dismissOnBackdropClick = false, // Prevent accidental dismissal
            header = {
                Column(
                    modifier = Modifier().padding("24px 24px 0 24px")
                ) {
                    Text(
                        text = "Add New User",
                        fontSize = "24px",
                        fontWeight = "semibold",
                        marginBottom = "8px"
                    )
                    Text(
                        text = "Fill in the details below to create a new user account.",
                        color = "#666666",
                        fontSize = "14px"
                    )
                }
            },
            footer = {
                Row(
                    modifier = Modifier()
                        .padding("0 24px 24px 24px")
                        .justifyContent("flex-end"),
                    horizontalSpacing = "12px"
                ) {
                    Button(
                        text = "Cancel",
                        variant = ButtonVariant.SECONDARY,
                        onClick = { showForm = false },
                        disabled = isSubmitting
                    )
                    Button(
                        text = if (isSubmitting) "Creating..." else "Create User",
                        onClick = {
                            isSubmitting = true
                            // Handle form submission
                        },
                        disabled = isSubmitting || !formData.isValid(),
                        loading = isSubmitting
                    )
                }
            }
        ) {
            UserForm(
                data = formData,
                onDataChange = { formData = it },
                modifier = Modifier().padding("24px")
            )
        }
    }
}
```

### Multi-Step Modal

```kotlin
@Composable
fun MultiStepModalExample() {
    var showWizard by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 3

    Modal(
        isOpen = showWizard,
        onDismiss = { showWizard = false },
        size = ModalSize.LARGE,
        showCloseButton = false, // Custom close handling
        header = {
            Column(
                modifier = Modifier().padding("24px 24px 0 24px")
            ) {
                Row(
                    modifier = Modifier().justifyContent("space-between"),
                    verticalAlignment = "center"
                ) {
                    Text(
                        text = "Setup Wizard",
                        fontSize = "24px",
                        fontWeight = "semibold"
                    )
                    IconButton(
                        icon = "close",
                        onClick = { showWizard = false },
                        ariaLabel = "Close wizard"
                    )
                }

                // Progress indicator
                Row(
                    modifier = Modifier()
                        .marginTop("16px")
                        .width("100%"),
                    horizontalSpacing = "8px"
                ) {
                    repeat(totalSteps) { index ->
                        Box(
                            modifier = Modifier()
                                .flex("1")
                                .height("4px")
                                .backgroundColor(
                                    if (index < currentStep) "#2196F3" else "#E0E0E0"
                                )
                                .borderRadius("2px")
                        )
                    }
                }

                Text(
                    text = "Step $currentStep of $totalSteps",
                    fontSize = "14px",
                    color = "#666666",
                    marginTop = "8px"
                )
            }
        },
        footer = {
            Row(
                modifier = Modifier()
                    .padding("0 24px 24px 24px")
                    .justifyContent("space-between")
            ) {
                Button(
                    text = "Previous",
                    variant = ButtonVariant.SECONDARY,
                    onClick = { if (currentStep > 1) currentStep-- },
                    disabled = currentStep == 1
                )

                Row(horizontalSpacing = "12px") {
                    Button(
                        text = "Cancel",
                        variant = ButtonVariant.GHOST,
                        onClick = { showWizard = false }
                    )
                    Button(
                        text = if (currentStep == totalSteps) "Finish" else "Next",
                        onClick = {
                            if (currentStep < totalSteps) {
                                currentStep++
                            } else {
                                // Complete wizard
                                showWizard = false
                            }
                        }
                    )
                }
            }
        }
    ) {
        Box(
            modifier = Modifier()
                .padding("24px")
                .minHeight("300px")
        ) {
            when (currentStep) {
                1 -> WizardStep1()
                2 -> WizardStep2()
                3 -> WizardStep3()
            }
        }
    }
}
```

### Image Lightbox Modal

```kotlin
@Composable
fun ImageLightboxModal(
    images: List<String>,
    currentIndex: Int,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onIndexChange: (Int) -> Unit
) {
    Modal(
        isOpen = isOpen,
        onDismiss = onDismiss,
        variant = ModalVariant.FULLSCREEN,
        dismissOnBackdropClick = true,
        showCloseButton = false,
        modifier = Modifier()
            .backgroundColor("rgba(0, 0, 0, 0.9)")
    ) {
        Box(
            modifier = Modifier()
                .fillSize()
                .display("flex")
                .alignItems("center")
                .justifyContent("center")
                .position("relative")
        ) {
            // Close button
            IconButton(
                icon = "close",
                onClick = onDismiss,
                modifier = Modifier()
                    .position("absolute")
                    .top("20px")
                    .right("20px")
                    .backgroundColor("rgba(255, 255, 255, 0.1)")
                    .color("white")
                    .borderRadius("50%")
                    .padding("12px")
                    .zIndex("10"),
                ariaLabel = "Close lightbox"
            )

            // Image
            Image(
                src = images[currentIndex],
                alt = "Image ${currentIndex + 1} of ${images.size}",
                modifier = Modifier()
                    .maxWidth("90%")
                    .maxHeight("90%")
                    .objectFit("contain")
            )

            // Navigation arrows
            if (images.size > 1) {
                // Previous button
                if (currentIndex > 0) {
                    IconButton(
                        icon = "arrow_back",
                        onClick = { onIndexChange(currentIndex - 1) },
                        modifier = Modifier()
                            .position("absolute")
                            .left("20px")
                            .top("50%")
                            .transform("translateY(-50%)")
                            .backgroundColor("rgba(255, 255, 255, 0.1)")
                            .color("white")
                            .borderRadius("50%")
                            .padding("12px"),
                        ariaLabel = "Previous image"
                    )
                }

                // Next button
                if (currentIndex < images.size - 1) {
                    IconButton(
                        icon = "arrow_forward",
                        onClick = { onIndexChange(currentIndex + 1) },
                        modifier = Modifier()
                            .position("absolute")
                            .right("20px")
                            .top("50%")
                            .transform("translateY(-50%)")
                            .backgroundColor("rgba(255, 255, 255, 0.1)")
                            .color("white")
                            .borderRadius("50%")
                            .padding("12px"),
                        ariaLabel = "Next image"
                    )
                }
            }

            // Image counter
            Text(
                text = "${currentIndex + 1} / ${images.size}",
                color = "white",
                fontSize = "14px",
                modifier = Modifier()
                    .position("absolute")
                    .bottom("20px")
                    .left("50%")
                    .transform("translateX(-50%)")
                    .backgroundColor("rgba(0, 0, 0, 0.5)")
                    .padding("8px 12px")
                    .borderRadius("16px")
            )
        }
    }
}
```

### Loading Modal

```kotlin
@Composable
fun LoadingModal(
    isOpen: Boolean,
    message: String = "Loading...",
    progress: Float? = null
) {
    Modal(
        isOpen = isOpen,
        onDismiss = { /* Cannot dismiss loading modal */ },
        size = ModalSize.SMALL,
        dismissOnBackdropClick = false,
        showCloseButton = false,
        modifier = Modifier()
            .backgroundColor("rgba(0, 0, 0, 0.5)")
    ) {
        Column(
            modifier = Modifier()
                .padding("32px")
                .alignItems("center"),
            verticalSpacing = "16px"
        ) {
            if (progress != null) {
                CircularProgress(
                    progress = progress,
                    size = "48px",
                    strokeWidth = "4px"
                )
            } else {
                CircularProgress(
                    indeterminate = true,
                    size = "48px",
                    strokeWidth = "4px"
                )
            }

            Text(
                text = message,
                fontSize = "16px",
                textAlign = "center"
            )

            if (progress != null) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = "14px",
                    color = "#666666"
                )
            }
        }
    }
}
```

## Modal Management

### Modal Manager

```kotlin
class ModalManager {
    private val _modals = mutableStateListOf<ModalConfig>()
    val modals: List<ModalConfig> = _modals

    fun showModal(config: ModalConfig) {
        _modals.add(config)
    }

    fun dismissModal(id: String) {
        _modals.removeAll { it.id == id }
    }

    fun dismissAll() {
        _modals.clear()
    }
}

data class ModalConfig(
    val id: String,
    val variant: ModalVariant,
    val size: ModalSize,
    val content: @Composable () -> Unit
)

@Composable
fun ModalContainer(modalManager: ModalManager) {
    modalManager.modals.forEach { config ->
        Modal(
            isOpen = true,
            onDismiss = { modalManager.dismissModal(config.id) },
            variant = config.variant,
            size = config.size
        ) {
            config.content()
        }
    }
}
```

## Accessibility Guidelines

### Focus Management

```kotlin
@Composable
fun AccessibleModal() {
    Modal(
        isOpen = true,
        onDismiss = { /* handle dismiss */ },
        // Automatically manages focus:
        // - Traps focus within modal
        // - Returns focus to trigger element on close
        // - Sets initial focus to first interactive element
        header = {
            Text(
                text = "Modal Title",
                // Automatically receives focus for screen readers
                modifier = Modifier().role("heading").ariaLevel(2)
            )
        }
    ) {
        // Modal content with proper tab order
    }
}
```

### Keyboard Navigation

```kotlin
// Modal automatically handles:
// - ESC key to dismiss
// - Tab/Shift+Tab for navigation within modal
// - Enter/Space for button activation
// - Arrow keys for custom controls
```

### Screen Reader Support

```kotlin
Modal(
    isOpen = true,
    onDismiss = { /* handle dismiss */ },
    // Automatically includes:
    // role="dialog"
    // aria-modal="true"
    // aria-labelledby="modal-title"
    // aria-describedby="modal-description"
    header = {
        Text(
            text = "Modal Title",
            modifier = Modifier().id("modal-title")
        )
    }
) {
    Text(
        text = "Modal description...",
        modifier = Modifier().id("modal-description")
    )
}
```

## Platform-Specific Behavior

### Browser Platform

- DOM portal rendering for proper stacking
- CSS animations and transitions
- Backdrop blur effects
- Automatic scroll locking
- Focus trap implementation

### JVM Platform

- Console-based modal simulation
- Text-based confirmations
- Logging integration
- Simplified user interactions

## Performance Considerations

### Lazy Loading

```kotlin
@Composable
fun LazyModal() {
    var showModal by remember { mutableStateOf(false) }

    if (showModal) {
        Modal(
            isOpen = true,
            onDismiss = { showModal = false }
        ) {
            // Modal content only rendered when needed
            ExpensiveModalContent()
        }
    }
}
```

### Animation Optimization

```kotlin
Modal(
    modifier = Modifier()
        .transition("transform 0.3s ease-out, opacity 0.3s ease-out")
        .will-change("transform, opacity") // Browser optimization
)
```

## Testing

### Unit Testing

```kotlin
@Test
fun testModalRendering() {
    val mockRenderer = MockPlatformRenderer()
    var dismissed = false

    CompositionLocal.provideComposer(MockComposer()) {
        LocalPlatformRenderer.provides(mockRenderer) {
            Modal(
                isOpen = true,
                onDismiss = { dismissed = true }
            ) {
                Text("Modal content")
            }

            assertTrue(mockRenderer.renderModalCalled)
            assertFalse(dismissed)
        }
    }
}
```

### Accessibility Testing

```kotlin
@Test
fun testModalAccessibility() {
    // Test focus management
    // Test keyboard navigation
    // Test screen reader attributes
    // Test ARIA compliance
}
```

## Best Practices

1. **Focus Management**: Always manage focus properly for accessibility
2. **Escape Route**: Provide clear ways to dismiss modals
3. **Content Hierarchy**: Use proper heading structure in modal content
4. **Mobile Consideration**: Ensure modals work well on small screens
5. **Performance**: Only render modal content when modal is open
6. **User Context**: Don't show too many modals simultaneously
7. **Clear Actions**: Provide obvious action buttons with clear labels
8. **Backdrop Handling**: Consider whether backdrop clicks should dismiss

The Modal component provides a robust foundation for overlay interactions across your Summon application, ensuring
accessibility, usability, and consistent user experience.