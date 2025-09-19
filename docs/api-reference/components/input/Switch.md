# Switch

Switch components provide toggle functionality for binary on/off states with a sliding visual indicator.

## Overview

The Switch component allows users to toggle between two states (on/off, enabled/disabled, true/false). It provides a
more visual alternative to checkboxes for binary choices and is commonly used for settings and preferences.

### Key Features

- **Binary Toggle**: On/off states with smooth transitions
- **Visual Feedback**: Sliding indicator shows current state
- **Controlled/Uncontrolled**: Flexible state management
- **Accessibility**: Full ARIA support and keyboard navigation
- **Form Integration**: Works seamlessly with forms
- **Custom Styling**: Type-safe styling with modifiers

## API Reference

### Switch

```kotlin
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
)
```

**Parameters:**

- `checked`: The current state of the switch
- `onCheckedChange`: Callback invoked when the switch state changes
- `modifier`: Modifier for styling and layout
- `enabled`: Whether the switch can be interacted with (default: `true`)

### StatefulSwitch

```kotlin
@Composable
fun StatefulSwitch(
    initialChecked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
)
```

**Parameters:**

- `initialChecked`: Initial state of the switch (default: `false`)
- `onCheckedChange`: Callback invoked when state changes
- `modifier`: Modifier for styling and layout
- `enabled`: Whether the switch can be interacted with

## Usage Examples

### Basic Switch

```kotlin
@Composable
fun BasicSwitchExample() {
    var isEnabled by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier()
            .alignItems(AlignItems.Center)
            .gap(Spacing.MD)
    ) {
        Text("Enable notifications")
        Switch(
            checked = isEnabled,
            onCheckedChange = { isEnabled = it }
        )
    }
}
```

### Settings Panel

```kotlin
@Composable
fun SettingsPanelExample() {
    var darkMode by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }
    var autoSave by remember { mutableStateOf(true) }
    var analytics by remember { mutableStateOf(false) }

    Column(modifier = Modifier().gap(Spacing.LG)) {
        Text("Settings", style = Typography.H5)

        // Dark mode setting
        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
                .padding(Spacing.MD)
        ) {
            Column {
                Text("Dark Mode", style = Typography.BODY1)
                Text(
                    "Switch to dark theme",
                    style = Typography.CAPTION.copy(color = Colors.Gray.MAIN)
                )
            }
            Switch(
                checked = darkMode,
                onCheckedChange = { darkMode = it }
            )
        }

        Divider()

        // Notifications setting
        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
                .padding(Spacing.MD)
        ) {
            Column {
                Text("Push Notifications", style = Typography.BODY1)
                Text(
                    "Receive app notifications",
                    style = Typography.CAPTION.copy(color = Colors.Gray.MAIN)
                )
            }
            Switch(
                checked = notifications,
                onCheckedChange = { notifications = it }
            )
        }

        Divider()

        // Auto-save setting
        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
                .padding(Spacing.MD)
        ) {
            Column {
                Text("Auto Save", style = Typography.BODY1)
                Text(
                    "Automatically save changes",
                    style = Typography.CAPTION.copy(color = Colors.Gray.MAIN)
                )
            }
            Switch(
                checked = autoSave,
                onCheckedChange = { autoSave = it }
            )
        }

        Divider()

        // Analytics setting
        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
                .padding(Spacing.MD)
        ) {
            Column {
                Text("Analytics", style = Typography.BODY1)
                Text(
                    "Help improve the app",
                    style = Typography.CAPTION.copy(color = Colors.Gray.MAIN)
                )
            }
            Switch(
                checked = analytics,
                onCheckedChange = { analytics = it }
            )
        }
    }
}
```

### Stateful Switch

```kotlin
@Composable
fun StatefulSwitchExample() {
    StatefulSwitch(
        initialChecked = true,
        onCheckedChange = { isChecked ->
            println("Switch toggled to: $isChecked")
        },
        modifier = Modifier()
            .padding(Spacing.MD)
    )
}
```

### Disabled Switch

```kotlin
@Composable
fun DisabledSwitchExample() {
    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Switch States:", style = Typography.H6)

        // Disabled in off state
        Row(
            modifier = Modifier()
                .alignItems(AlignItems.Center)
                .gap(Spacing.MD)
        ) {
            Text("Disabled (Off)")
            Switch(
                checked = false,
                onCheckedChange = {},
                enabled = false
            )
        }

        // Disabled in on state
        Row(
            modifier = Modifier()
                .alignItems(AlignItems.Center)
                .gap(Spacing.MD)
        ) {
            Text("Disabled (On)")
            Switch(
                checked = true,
                onCheckedChange = {},
                enabled = false
            )
        }

        // Conditional enabling
        var mainFeatureEnabled by remember { mutableStateOf(false) }
        var subFeatureEnabled by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier()
                .alignItems(AlignItems.Center)
                .gap(Spacing.MD)
        ) {
            Text("Main Feature")
            Switch(
                checked = mainFeatureEnabled,
                onCheckedChange = {
                    mainFeatureEnabled = it
                    if (!it) subFeatureEnabled = false // Auto-disable sub-feature
                }
            )
        }

        Row(
            modifier = Modifier()
                .alignItems(AlignItems.Center)
                .gap(Spacing.MD)
        ) {
            Text("Sub Feature")
            Switch(
                checked = subFeatureEnabled,
                onCheckedChange = { subFeatureEnabled = it },
                enabled = mainFeatureEnabled
            )
        }
    }
}
```

### Form Integration

```kotlin
@Composable
fun SwitchFormExample() {
    var termsAccepted by remember { mutableStateOf(false) }
    var marketingOptIn by remember { mutableStateOf(false) }
    var rememberLogin by remember { mutableStateOf(true) }

    Form(
        onSubmit = {
            println("Form submitted:")
            println("Terms: $termsAccepted")
            println("Marketing: $marketingOptIn")
            println("Remember: $rememberLogin")
        }
    ) {
        FormField(label = "Agreement") {
            Column(modifier = Modifier().gap(Spacing.MD)) {
                Row(
                    modifier = Modifier()
                        .width(Width.FULL)
                        .justifyContent(JustifyContent.SpaceBetween)
                        .alignItems(AlignItems.Center)
                ) {
                    Text("Accept Terms & Conditions *")
                    Switch(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it }
                    )
                }

                Row(
                    modifier = Modifier()
                        .width(Width.FULL)
                        .justifyContent(JustifyContent.SpaceBetween)
                        .alignItems(AlignItems.Center)
                ) {
                    Text("Receive Marketing Emails")
                    Switch(
                        checked = marketingOptIn,
                        onCheckedChange = { marketingOptIn = it }
                    )
                }

                Row(
                    modifier = Modifier()
                        .width(Width.FULL)
                        .justifyContent(JustifyContent.SpaceBetween)
                        .alignItems(AlignItems.Center)
                ) {
                    Text("Remember Login")
                    Switch(
                        checked = rememberLogin,
                        onCheckedChange = { rememberLogin = it }
                    )
                }
            }
        }

        Button(
            text = "Create Account",
            type = ButtonType.SUBMIT,
            enabled = termsAccepted
        )
    }
}
```

### Custom Styling

```kotlin
@Composable
fun StyledSwitchExample() {
    var isCustomTheme by remember { mutableStateOf(false) }

    Column(modifier = Modifier().gap(Spacing.LG)) {
        Text("Custom Switch Styling:", style = Typography.H6)

        // Large switch with custom colors
        Row(
            modifier = Modifier()
                .alignItems(AlignItems.Center)
                .gap(Spacing.MD)
        ) {
            Text("Large Switch")
            Switch(
                checked = isCustomTheme,
                onCheckedChange = { isCustomTheme = it },
                modifier = Modifier()
                    .scale(1.5f)
                    .accentColor(Colors.Success.MAIN)
            )
        }

        // Switch with background
        Row(
            modifier = Modifier()
                .alignItems(AlignItems.Center)
                .gap(Spacing.MD)
                .padding(Spacing.MD)
                .backgroundColor(Colors.Gray.LIGHT)
                .borderRadius(BorderRadius.LG)
        ) {
            Text("Highlighted Switch")
            Switch(
                checked = isCustomTheme,
                onCheckedChange = { isCustomTheme = it },
                modifier = Modifier()
                    .accentColor(Colors.Warning.MAIN)
            )
        }
    }
}
```

### Switch with Status Text

```kotlin
@Composable
fun SwitchWithStatusExample() {
    var wifiEnabled by remember { mutableStateOf(true) }
    var bluetoothEnabled by remember { mutableStateOf(false) }
    var locationEnabled by remember { mutableStateOf(true) }

    Column(modifier = Modifier().gap(Spacing.LG)) {
        Text("Device Settings:", style = Typography.H6)

        // Wi-Fi
        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
                .padding(Spacing.SM)
        ) {
            Column {
                Text("Wi-Fi", style = Typography.BODY1)
                Text(
                    if (wifiEnabled) "Connected to MyNetwork" else "Disabled",
                    style = Typography.CAPTION.copy(
                        color = if (wifiEnabled) Colors.Success.MAIN else Colors.Gray.MAIN
                    )
                )
            }
            Switch(
                checked = wifiEnabled,
                onCheckedChange = { wifiEnabled = it }
            )
        }

        Divider()

        // Bluetooth
        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
                .padding(Spacing.SM)
        ) {
            Column {
                Text("Bluetooth", style = Typography.BODY1)
                Text(
                    if (bluetoothEnabled) "Discoverable" else "Off",
                    style = Typography.CAPTION.copy(
                        color = if (bluetoothEnabled) Colors.Info.MAIN else Colors.Gray.MAIN
                    )
                )
            }
            Switch(
                checked = bluetoothEnabled,
                onCheckedChange = { bluetoothEnabled = it }
            )
        }

        Divider()

        // Location
        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
                .padding(Spacing.SM)
        ) {
            Column {
                Text("Location Services", style = Typography.BODY1)
                Text(
                    if (locationEnabled) "High accuracy" else "Disabled",
                    style = Typography.CAPTION.copy(
                        color = if (locationEnabled) Colors.Success.MAIN else Colors.Gray.MAIN
                    )
                )
            }
            Switch(
                checked = locationEnabled,
                onCheckedChange = { locationEnabled = it }
            )
        }
    }
}
```

### Confirmation Dialogs

```kotlin
@Composable
fun SwitchWithConfirmationExample() {
    var destructiveActionEnabled by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
                .padding(Spacing.MD)
                .backgroundColor(Colors.Warning.LIGHT)
                .borderRadius(BorderRadius.MD)
        ) {
            Column {
                Text(
                    "Destructive Actions",
                    style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM)
                )
                Text(
                    "Allow dangerous operations",
                    style = Typography.CAPTION.copy(color = Colors.Warning.DARK)
                )
            }
            Switch(
                checked = destructiveActionEnabled,
                onCheckedChange = { newValue ->
                    if (newValue) {
                        showConfirmationDialog = true
                    } else {
                        destructiveActionEnabled = false
                    }
                }
            )
        }

        if (showConfirmationDialog) {
            AlertDialog(
                title = "Enable Destructive Actions?",
                message = "This will allow operations that cannot be undone. Are you sure?",
                confirmButton = {
                    Button(
                        text = "Enable",
                        onClick = {
                            destructiveActionEnabled = true
                            showConfirmationDialog = false
                        },
                        type = ButtonType.PRIMARY
                    )
                },
                dismissButton = {
                    Button(
                        text = "Cancel",
                        onClick = { showConfirmationDialog = false },
                        type = ButtonType.SECONDARY
                    )
                },
                onDismiss = { showConfirmationDialog = false }
            )
        }
    }
}
```

## Accessibility Features

### ARIA Support

The Switch component automatically includes:

- `role="switch"` for screen readers
- `aria-checked` state management
- `aria-labelledby` for associated labels
- `aria-describedby` for additional descriptions

### Keyboard Navigation

- **Space/Enter**: Toggle the switch state
- **Tab**: Move focus to next interactive element
- **Shift+Tab**: Move focus to previous interactive element

### Screen Reader Support

```kotlin
@Composable
fun AccessibleSwitchExample() {
    var isEnabled by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier()
            .alignItems(AlignItems.Center)
            .gap(Spacing.MD)
    ) {
        Text(
            "Dark Mode",
            style = Typography.BODY1,
            modifier = Modifier()
                .accessibilityRole("label")
        )
        Switch(
            checked = isEnabled,
            onCheckedChange = { isEnabled = it },
            modifier = Modifier()
                .accessibilityLabel("Dark mode toggle")
                .accessibilityHint(
                    if (isEnabled) "Currently enabled. Double tap to disable."
                    else "Currently disabled. Double tap to enable."
                )
        )
    }
}
```

## State Management Patterns

### Controlled Component

```kotlin
@Composable
fun ControlledSwitchExample() {
    var switchState by remember { mutableStateOf(false) }
    var actionLog by remember { mutableStateOf(listOf<String>()) }

    fun toggleSwitch() {
        switchState = !switchState
        actionLog = actionLog + "Switch toggled to $switchState at ${System.currentTimeMillis()}"
    }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Row(
            modifier = Modifier()
                .alignItems(AlignItems.Center)
                .gap(Spacing.MD)
        ) {
            Text("Controlled Switch")
            Switch(
                checked = switchState,
                onCheckedChange = { toggleSwitch() }
            )
        }

        Button(
            text = "Toggle Programmatically",
            onClick = { toggleSwitch() }
        )

        Text("Current state: $switchState")

        if (actionLog.isNotEmpty()) {
            Text(
                "Last action: ${actionLog.last()}",
                style = Typography.CAPTION
            )
        }
    }
}
```

### Uncontrolled Component

```kotlin
@Composable
fun UncontrolledSwitchExample() {
    StatefulSwitch(
        initialChecked = true,
        onCheckedChange = { isChecked ->
            println("Switch changed to: $isChecked")
            // Additional side effects
        }
    )
}
```

### Complex State Management

```kotlin
data class FeatureSettings(
    val notifications: Boolean = true,
    val autoSave: Boolean = true,
    val analytics: Boolean = false,
    val betaFeatures: Boolean = false
)

@Composable
fun ComplexStateExample() {
    var settings by remember { mutableStateOf(FeatureSettings()) }

    fun updateSetting(update: (FeatureSettings) -> FeatureSettings) {
        settings = update(settings)
    }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Feature Settings:", style = Typography.H6)

        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Text("Notifications")
            Switch(
                checked = settings.notifications,
                onCheckedChange = { updateSetting { it.copy(notifications = it) } }
            )
        }

        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Text("Auto Save")
            Switch(
                checked = settings.autoSave,
                onCheckedChange = { updateSetting { it.copy(autoSave = it) } }
            )
        }

        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Text("Analytics")
            Switch(
                checked = settings.analytics,
                onCheckedChange = { updateSetting { it.copy(analytics = it) } }
            )
        }

        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Text("Beta Features")
            Switch(
                checked = settings.betaFeatures,
                onCheckedChange = { updateSetting { it.copy(betaFeatures = it) } }
            )
        }

        // Settings summary
        Text(
            "Active features: ${
                listOfNotNull(
                    if (settings.notifications) "Notifications" else null,
                    if (settings.autoSave) "Auto Save" else null,
                    if (settings.analytics) "Analytics" else null,
                    if (settings.betaFeatures) "Beta Features" else null
                ).joinToString(", ")
            }",
            style = Typography.CAPTION,
            modifier = Modifier().padding(top = Spacing.MD)
        )
    }
}
```

## Platform Differences

### Browser (JS)

- Renders as custom styled checkbox with switch appearance
- Smooth CSS transitions and animations
- Full mouse and touch support
- CSS styling through modifiers

### JVM

- Server-side rendering support
- Generates appropriate HTML for SSR
- Maintains state during form submissions
- Fallback to checkbox input for accessibility

## Performance Considerations

### Optimization Tips

1. **Stable References**: Use `remember` for stable callback references
2. **Avoid Frequent Re-renders**: Minimize state changes
3. **Batch Updates**: Group multiple switch changes
4. **Memoization**: Cache expensive computations

```kotlin
@Composable
fun OptimizedSwitchExample() {
    var settings by remember { mutableStateOf(FeatureSettings()) }

    // Memoize the update function to prevent recreation
    val updateSettings = remember {
        { field: String, value: Boolean ->
            settings = when (field) {
                "notifications" -> settings.copy(notifications = value)
                "autoSave" -> settings.copy(autoSave = value)
                "analytics" -> settings.copy(analytics = value)
                else -> settings
            }
        }
    }

    // Memoize expensive calculations
    val activeFeatureCount = remember(settings) {
        listOf(
            settings.notifications,
            settings.autoSave,
            settings.analytics
        ).count { it }
    }

    Column {
        Text("Active features: $activeFeatureCount")

        Switch(
            checked = settings.notifications,
            onCheckedChange = { updateSettings("notifications", it) }
        )
        // ... other switches
    }
}
```

## Testing Strategies

### Unit Testing

```kotlin
class SwitchTest {
    @Test
    fun `switch toggles state correctly`() {
        var isChecked = false

        composeTestRule.setContent {
            Switch(
                checked = isChecked,
                onCheckedChange = { isChecked = it }
            )
        }

        // Toggle the switch
        composeTestRule.onNode(hasContentDescription("switch")).performClick()
        assertTrue(isChecked)

        // Toggle again
        composeTestRule.onNode(hasContentDescription("switch")).performClick()
        assertFalse(isChecked)
    }

    @Test
    fun `disabled switch does not respond to clicks`() {
        var isChecked = false

        composeTestRule.setContent {
            Switch(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                enabled = false
            )
        }

        composeTestRule.onNode(hasContentDescription("switch")).performClick()
        assertFalse(isChecked)
    }

    @Test
    fun `stateful switch maintains internal state`() {
        composeTestRule.setContent {
            StatefulSwitch(initialChecked = true)
        }

        // Verify initial state
        composeTestRule
            .onNode(hasContentDescription("switch"))
            .assertIsToggleable()
            .assertIsOn()
    }
}
```

### Integration Testing

```kotlin
@Test
fun `switch integrates with form submission`() {
    var submittedValue = false

    composeTestRule.setContent {
        var switchState by remember { mutableStateOf(false) }

        Form(onSubmit = { submittedValue = switchState }) {
            Switch(
                checked = switchState,
                onCheckedChange = { switchState = it }
            )
            Button(text = "Submit", type = ButtonType.SUBMIT)
        }
    }

    // Toggle switch and submit
    composeTestRule.onNode(hasContentDescription("switch")).performClick()
    composeTestRule.onNodeWithText("Submit").performClick()

    assertTrue(submittedValue)
}
```

## Migration Guide

### From HTML Forms

```html
<!-- Before: HTML -->
<input type="checkbox" id="toggle" class="switch">
<label for="toggle">Enable feature</label>
```

```kotlin
// After: Summon
@Composable
fun FeatureToggle() {
    var isEnabled by remember { mutableStateOf(false) }

    Row(modifier = Modifier().alignItems(AlignItems.Center).gap(Spacing.MD)) {
        Text("Enable feature")
        Switch(
            checked = isEnabled,
            onCheckedChange = { isEnabled = it }
        )
    }
}
```

### From Other Frameworks

```jsx
// React example
const [enabled, setEnabled] = useState(false);
<input
    type="checkbox"
    checked={enabled}
    onChange={(e) => setEnabled(e.target.checked)}
    className="switch"
/>
```

```kotlin
// Summon equivalent
var enabled by remember { mutableStateOf(false) }
Switch(
    checked = enabled,
    onCheckedChange = { enabled = it }
)
```

## Best Practices

### Do

- Use switches for immediate binary actions
- Provide clear labels indicating what the switch controls
- Use consistent positioning (labels on left, switches on right)
- Group related switches logically
- Provide feedback for state changes

### Don't

- Use switches for actions that require confirmation
- Mix switches with checkboxes in the same context
- Use switches for mutually exclusive options (use RadioButton instead)
- Create switches without clear state indication

## Related Components

- [Checkbox](Checkbox.md) - For multiple selections
- [RadioButton](RadioButton.md) - For single selections
- [Button](Button.md) - For action triggers
- [Form](Form.md) - For form integration