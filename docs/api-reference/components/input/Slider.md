# Slider

Slider components provide intuitive value selection within a defined range using a draggable thumb interface.

## Overview

The Slider component allows users to select a single numeric value within a specified range by dragging a thumb along a
track. It's ideal for settings, preferences, and any scenario requiring continuous or discrete value selection.

### Key Features

- **Value Ranges**: Customizable minimum and maximum values
- **Discrete Steps**: Support for stepped or continuous selection
- **Visual Feedback**: Real-time value updates and visual indicators
- **Accessibility**: Full ARIA support and keyboard navigation
- **Form Integration**: Works seamlessly with forms and validation
- **Custom Styling**: Type-safe styling with modifiers
- **State Management**: Controlled and uncontrolled components

## API Reference

### Slider

```kotlin
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier(),
    valueRange: ClosedFloatingPointRange<Float> = 0.0f..1.0f,
    steps: Int = 0,
    enabled: Boolean = true
)
```

**Parameters:**

- `value`: The current selected value
- `onValueChange`: Callback invoked when the value changes
- `modifier`: Modifier for styling and layout
- `valueRange`: The range of allowed values (default: `0.0f..1.0f`)
- `steps`: Number of discrete steps, 0 for continuous (default: `0`)
- `enabled`: Whether the slider can be interacted with (default: `true`)

### StatefulSlider

```kotlin
@Composable
fun StatefulSlider(
    initialValue: Float = 0.5f,
    onValueChange: (Float) -> Unit = {},
    valueRange: ClosedFloatingPointRange<Float> = 0.0f..1.0f,
    steps: Int = 0,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
)
```

**Parameters:**

- `initialValue`: Initial value of the slider (default: `0.5f`)
- `onValueChange`: Callback invoked when value changes
- Other parameters same as `Slider`

## Usage Examples

### Basic Slider

```kotlin
@Composable
fun BasicSliderExample() {
    var value by remember { mutableStateOf(0.5f) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Volume: ${(value * 100).toInt()}%", style = Typography.H6)

        Slider(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier()
                .width(300.px)
                .padding(Spacing.MD)
        )

        Text(
            "Current value: ${String.format("%.2f", value)}",
            style = Typography.BODY2.copy(color = Colors.Gray.MAIN)
        )
    }
}
```

### Custom Range Slider

```kotlin
@Composable
fun CustomRangeSliderExample() {
    var temperature by remember { mutableStateOf(20.0f) }
    val temperatureRange = -10f..50f

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Temperature Setting", style = Typography.H6)

        Row(
            modifier = Modifier()
                .alignItems(AlignItems.Center)
                .gap(Spacing.MD)
        ) {
            Text("${temperatureRange.start.toInt()}°C")

            Slider(
                value = temperature,
                onValueChange = { temperature = it },
                valueRange = temperatureRange,
                modifier = Modifier()
                    .weight(1f)
                    .height(8.px)
            )

            Text("${temperatureRange.endInclusive.toInt()}°C")
        }

        Card(
            modifier = Modifier()
                .width(Width.FULL)
                .backgroundColor(
                    when {
                        temperature < 0 -> Colors.Info.LIGHT
                        temperature < 25 -> Colors.Success.LIGHT
                        else -> Colors.Warning.LIGHT
                    }
                )
                .padding(Spacing.MD)
        ) {
            Text(
                "Current temperature: ${temperature.toInt()}°C",
                style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM)
            )
            Text(
                when {
                    temperature < 0 -> "Freezing conditions"
                    temperature < 10 -> "Cold weather"
                    temperature < 25 -> "Comfortable temperature"
                    temperature < 35 -> "Warm weather"
                    else -> "Hot conditions"
                },
                style = Typography.BODY2
            )
        }
    }
}
```

### Discrete Steps Slider

```kotlin
@Composable
fun DiscreteStepsSliderExample() {
    var rating by remember { mutableStateOf(3f) }
    val maxRating = 5f

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Rate this product", style = Typography.H6)

        Slider(
            value = rating,
            onValueChange = { rating = it },
            valueRange = 1f..maxRating,
            steps = 4, // Creates 5 discrete steps (1, 2, 3, 4, 5)
            modifier = Modifier().width(300.px)
        )

        Row(
            modifier = Modifier()
                .width(300.px)
                .justifyContent(JustifyContent.SpaceBetween)
        ) {
            repeat(5) { index ->
                val starRating = index + 1
                Icon(
                    name = if (starRating <= rating.toInt()) "star" else "star_outline",
                    color = if (starRating <= rating.toInt()) Colors.Warning.MAIN else Colors.Gray.LIGHT,
                    modifier = Modifier().fontSize(24.px)
                )
            }
        }

        Text(
            "Rating: ${rating.toInt()} star${if (rating.toInt() != 1) "s" else ""}",
            style = Typography.BODY2.copy(color = Colors.Gray.MAIN)
        )
    }
}
```

### Audio Controls

```kotlin
@Composable
fun AudioControlsExample() {
    var volume by remember { mutableStateOf(0.7f) }
    var bass by remember { mutableStateOf(0.5f) }
    var treble by remember { mutableStateOf(0.6f) }
    var balance by remember { mutableStateOf(0.5f) } // 0 = left, 1 = right

    Column(modifier = Modifier().gap(Spacing.LG)) {
        Text("Audio Settings", style = Typography.H5)

        // Volume control
        Column(modifier = Modifier().gap(Spacing.SM)) {
            Row(
                modifier = Modifier()
                    .width(Width.FULL)
                    .justifyContent(JustifyContent.SpaceBetween)
                    .alignItems(AlignItems.Center)
            ) {
                Text("Volume", style = Typography.BODY1)
                Text("${(volume * 100).toInt()}%", style = Typography.BODY2)
            }

            Row(
                modifier = Modifier()
                    .alignItems(AlignItems.Center)
                    .gap(Spacing.MD)
            ) {
                Icon(name = "volume_mute", color = Colors.Gray.MAIN)
                Slider(
                    value = volume,
                    onValueChange = { volume = it },
                    modifier = Modifier().weight(1f)
                )
                Icon(name = "volume_up", color = Colors.Gray.MAIN)
            }
        }

        // Bass control
        Column(modifier = Modifier().gap(Spacing.SM)) {
            Row(
                modifier = Modifier()
                    .width(Width.FULL)
                    .justifyContent(JustifyContent.SpaceBetween)
                    .alignItems(AlignItems.Center)
            ) {
                Text("Bass", style = Typography.BODY1)
                Text("${((bass - 0.5f) * 200).toInt()}%", style = Typography.BODY2)
            }

            Slider(
                value = bass,
                onValueChange = { bass = it },
                modifier = Modifier().width(Width.FULL)
            )
        }

        // Treble control
        Column(modifier = Modifier().gap(Spacing.SM)) {
            Row(
                modifier = Modifier()
                    .width(Width.FULL)
                    .justifyContent(JustifyContent.SpaceBetween)
                    .alignItems(AlignItems.Center)
            ) {
                Text("Treble", style = Typography.BODY1)
                Text("${((treble - 0.5f) * 200).toInt()}%", style = Typography.BODY2)
            }

            Slider(
                value = treble,
                onValueChange = { treble = it },
                modifier = Modifier().width(Width.FULL)
            )
        }

        // Balance control
        Column(modifier = Modifier().gap(Spacing.SM)) {
            Row(
                modifier = Modifier()
                    .width(Width.FULL)
                    .justifyContent(JustifyContent.SpaceBetween)
                    .alignItems(AlignItems.Center)
            ) {
                Text("Balance", style = Typography.BODY1)
                Text(
                    when {
                        balance < 0.4f -> "Left ${((0.5f - balance) * 200).toInt()}%"
                        balance > 0.6f -> "Right ${((balance - 0.5f) * 200).toInt()}%"
                        else -> "Center"
                    },
                    style = Typography.BODY2
                )
            }

            Row(
                modifier = Modifier()
                    .alignItems(AlignItems.Center)
                    .gap(Spacing.MD)
            ) {
                Text("L", style = Typography.CAPTION)
                Slider(
                    value = balance,
                    onValueChange = { balance = it },
                    modifier = Modifier().weight(1f)
                )
                Text("R", style = Typography.CAPTION)
            }
        }
    }
}
```

### Progress Indicator

```kotlin
@Composable
fun ProgressSliderExample() {
    var progress by remember { mutableStateOf(0.0f) }
    var isUploading by remember { mutableStateOf(false) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("File Upload Progress", style = Typography.H6)

        // Progress slider (read-only)
        Slider(
            value = progress,
            onValueChange = { /* Read-only */ },
            enabled = false,
            modifier = Modifier()
                .width(400.px)
                .height(6.px)
                .backgroundColor(Colors.Gray.LIGHT)
                .accentColor(Colors.Success.MAIN)
        )

        Row(
            modifier = Modifier()
                .width(400.px)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Text(
                "${(progress * 100).toInt()}% complete",
                style = Typography.BODY2
            )

            if (isUploading) {
                Text(
                    "Uploading...",
                    style = Typography.BODY2.copy(color = Colors.Primary.MAIN)
                )
            }
        }

        Row(modifier = Modifier().gap(Spacing.MD)) {
            Button(
                text = if (isUploading) "Cancel" else "Start Upload",
                onClick = {
                    if (isUploading) {
                        isUploading = false
                        progress = 0f
                    } else {
                        isUploading = true
                    }
                },
                type = if (isUploading) ButtonType.SECONDARY else ButtonType.PRIMARY
            )

            if (!isUploading && progress < 1f) {
                Button(
                    text = "Simulate Progress",
                    onClick = {
                        progress = minOf(1f, progress + 0.1f)
                    },
                    type = ButtonType.OUTLINE
                )
            }
        }

        // Simulate upload progress
        if (isUploading && progress < 1f) {
            LaunchedEffect(isUploading) {
                while (isUploading && progress < 1f) {
                    delay(100)
                    progress = minOf(1f, progress + 0.01f)
                    if (progress >= 1f) {
                        isUploading = false
                    }
                }
            }
        }
    }
}
```

### Image Editor Controls

```kotlin
@Composable
fun ImageEditorSliderExample() {
    var brightness by remember { mutableStateOf(0.5f) }
    var contrast by remember { mutableStateOf(0.5f) }
    var saturation by remember { mutableStateOf(0.5f) }
    var hue by remember { mutableStateOf(0.5f) }

    Column(modifier = Modifier().gap(Spacing.LG)) {
        Text("Image Adjustments", style = Typography.H5)

        // Brightness
        SliderControl(
            label = "Brightness",
            value = brightness,
            onValueChange = { brightness = it },
            formatValue = { "${((it - 0.5f) * 200).toInt()}%" },
            icon = "brightness_6"
        )

        // Contrast
        SliderControl(
            label = "Contrast",
            value = contrast,
            onValueChange = { contrast = it },
            formatValue = { "${((it - 0.5f) * 200).toInt()}%" },
            icon = "contrast"
        )

        // Saturation
        SliderControl(
            label = "Saturation",
            value = saturation,
            onValueChange = { saturation = it },
            formatValue = { "${((it - 0.5f) * 200).toInt()}%" },
            icon = "palette"
        )

        // Hue
        SliderControl(
            label = "Hue",
            value = hue,
            onValueChange = { hue = it },
            formatValue = { "${(it * 360).toInt()}°" },
            icon = "tune"
        )

        Divider()

        Row(modifier = Modifier().gap(Spacing.MD)) {
            Button(
                text = "Reset All",
                onClick = {
                    brightness = 0.5f
                    contrast = 0.5f
                    saturation = 0.5f
                    hue = 0.5f
                },
                type = ButtonType.SECONDARY
            )

            Button(
                text = "Apply Changes",
                onClick = {
                    println("Applied: B:$brightness C:$contrast S:$saturation H:$hue")
                },
                type = ButtonType.PRIMARY
            )
        }
    }
}

@Composable
fun SliderControl(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    formatValue: (Float) -> String,
    icon: String
) {
    Column(modifier = Modifier().gap(Spacing.SM)) {
        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Row(
                modifier = Modifier()
                    .alignItems(AlignItems.Center)
                    .gap(Spacing.SM)
            ) {
                Icon(name = icon, color = Colors.Gray.MAIN)
                Text(label, style = Typography.BODY1)
            }
            Text(formatValue(value), style = Typography.BODY2.copy(fontWeight = FontWeight.MEDIUM))
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier().width(Width.FULL)
        )
    }
}
```

### Gaming Settings

```kotlin
@Composable
fun GamingSettingsExample() {
    var mouseSensitivity by remember { mutableStateOf(0.5f) }
    var sfxVolume by remember { mutableStateOf(0.8f) }
    var musicVolume by remember { mutableStateOf(0.6f) }
    var fovValue by remember { mutableStateOf(0.6f) } // Field of view

    val fovRange = 60f..120f
    val actualFov = fovRange.start + (fovValue * (fovRange.endInclusive - fovRange.start))

    Column(modifier = Modifier().gap(Spacing.LG)) {
        Text("Game Settings", style = Typography.H5)

        // Mouse sensitivity
        Column(modifier = Modifier().gap(Spacing.SM)) {
            Row(
                modifier = Modifier()
                    .width(Width.FULL)
                    .justifyContent(JustifyContent.SpaceBetween)
                    .alignItems(AlignItems.Center)
            ) {
                Text("Mouse Sensitivity", style = Typography.BODY1)
                Text("${(mouseSensitivity * 10).toInt()}/10", style = Typography.BODY2)
            }

            Slider(
                value = mouseSensitivity,
                onValueChange = { mouseSensitivity = it },
                steps = 9, // 10 discrete levels
                modifier = Modifier().width(Width.FULL)
            )
        }

        // Audio settings
        Text("Audio", style = Typography.H6)

        Column(modifier = Modifier().gap(Spacing.MD)) {
            // SFX Volume
            Column(modifier = Modifier().gap(Spacing.SM)) {
                Row(
                    modifier = Modifier()
                        .width(Width.FULL)
                        .justifyContent(JustifyContent.SpaceBetween)
                        .alignItems(AlignItems.Center)
                ) {
                    Text("Sound Effects", style = Typography.BODY1)
                    Text("${(sfxVolume * 100).toInt()}%", style = Typography.BODY2)
                }

                Slider(
                    value = sfxVolume,
                    onValueChange = { sfxVolume = it },
                    modifier = Modifier().width(Width.FULL)
                )
            }

            // Music Volume
            Column(modifier = Modifier().gap(Spacing.SM)) {
                Row(
                    modifier = Modifier()
                        .width(Width.FULL)
                        .justifyContent(JustifyContent.SpaceBetween)
                        .alignItems(AlignItems.Center)
                ) {
                    Text("Music", style = Typography.BODY1)
                    Text("${(musicVolume * 100).toInt()}%", style = Typography.BODY2)
                }

                Slider(
                    value = musicVolume,
                    onValueChange = { musicVolume = it },
                    modifier = Modifier().width(Width.FULL)
                )
            }
        }

        // Field of view
        Column(modifier = Modifier().gap(Spacing.SM)) {
            Row(
                modifier = Modifier()
                    .width(Width.FULL)
                    .justifyContent(JustifyContent.SpaceBetween)
                    .alignItems(AlignItems.Center)
            ) {
                Text("Field of View", style = Typography.BODY1)
                Text("${actualFov.toInt()}°", style = Typography.BODY2)
            }

            Slider(
                value = fovValue,
                onValueChange = { fovValue = it },
                modifier = Modifier().width(Width.FULL)
            )

            Row(
                modifier = Modifier()
                    .width(Width.FULL)
                    .justifyContent(JustifyContent.SpaceBetween)
            ) {
                Text("${fovRange.start.toInt()}°", style = Typography.CAPTION)
                Text("${fovRange.endInclusive.toInt()}°", style = Typography.CAPTION)
            }
        }
    }
}
```

### Stateful Slider

```kotlin
@Composable
fun StatefulSliderExample() {
    StatefulSlider(
        initialValue = 0.75f,
        onValueChange = { value ->
            println("Slider value changed to: $value")
        },
        valueRange = 0f..100f,
        steps = 20, // 21 discrete values (0, 5, 10, ..., 100)
        modifier = Modifier()
            .width(300.px)
            .padding(Spacing.MD)
    )
}
```

### Form Integration

```kotlin
@Composable
fun SliderFormExample() {
    var budget by remember { mutableStateOf(5000f) }
    var duration by remember { mutableStateOf(30f) } // days
    var quality by remember { mutableStateOf(3f) } // 1-5 scale

    val budgetRange = 1000f..20000f
    val durationRange = 7f..365f

    Form(
        onSubmit = {
            println("Project parameters:")
            println("Budget: $${budget.toInt()}")
            println("Duration: ${duration.toInt()} days")
            println("Quality: ${quality.toInt()}/5")
        }
    ) {
        FormField(label = "Project Configuration") {
            Column(modifier = Modifier().gap(Spacing.LG)) {
                // Budget slider
                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .justifyContent(JustifyContent.SpaceBetween)
                            .alignItems(AlignItems.Center)
                    ) {
                        Text("Budget", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                        Text("$${budget.toInt()}", style = Typography.BODY2)
                    }

                    Slider(
                        value = budget,
                        onValueChange = { budget = it },
                        valueRange = budgetRange,
                        modifier = Modifier().width(Width.FULL)
                    )

                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .justifyContent(JustifyContent.SpaceBetween)
                    ) {
                        Text("$${budgetRange.start.toInt()}", style = Typography.CAPTION)
                        Text("$${budgetRange.endInclusive.toInt()}", style = Typography.CAPTION)
                    }
                }

                // Duration slider
                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .justifyContent(JustifyContent.SpaceBetween)
                            .alignItems(AlignItems.Center)
                    ) {
                        Text("Duration", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                        Text("${duration.toInt()} days", style = Typography.BODY2)
                    }

                    Slider(
                        value = duration,
                        onValueChange = { duration = it },
                        valueRange = durationRange,
                        modifier = Modifier().width(Width.FULL)
                    )
                }

                // Quality slider
                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .justifyContent(JustifyContent.SpaceBetween)
                            .alignItems(AlignItems.Center)
                    ) {
                        Text("Quality Level", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                        Text("${quality.toInt()}/5", style = Typography.BODY2)
                    }

                    Slider(
                        value = quality,
                        onValueChange = { quality = it },
                        valueRange = 1f..5f,
                        steps = 4, // 5 discrete levels
                        modifier = Modifier().width(Width.FULL)
                    )

                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .justifyContent(JustifyContent.SpaceBetween)
                    ) {
                        Text("Basic", style = Typography.CAPTION)
                        Text("Premium", style = Typography.CAPTION)
                    }
                }

                // Cost estimate
                val estimatedCost = budget * (quality / 5f) * (duration / 30f)
                Card(
                    modifier = Modifier()
                        .width(Width.FULL)
                        .backgroundColor(Colors.Info.LIGHT)
                        .padding(Spacing.MD)
                ) {
                    Text(
                        "Estimated Total Cost: $${estimatedCost.toInt()}",
                        style = Typography.BODY1.copy(fontWeight = FontWeight.BOLD)
                    )
                }
            }
        }

        Button(
            text = "Start Project",
            type = ButtonType.SUBMIT,
            enabled = budget >= budgetRange.start
        )
    }
}
```

### Disabled State

```kotlin
@Composable
fun DisabledSliderExample() {
    var isLocked by remember { mutableStateOf(true) }
    var value by remember { mutableStateOf(0.3f) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Row(
            modifier = Modifier()
                .alignItems(AlignItems.Center)
                .gap(Spacing.MD)
        ) {
            Switch(
                checked = !isLocked,
                onCheckedChange = { isLocked = !it }
            )
            Text("Enable slider control")
        }

        Text("System Performance", style = Typography.H6)

        Slider(
            value = value,
            onValueChange = { value = it },
            enabled = !isLocked,
            modifier = Modifier()
                .width(300.px)
                .opacity(if (isLocked) 0.6f else 1.0f)
        )

        Text(
            "Performance level: ${(value * 100).toInt()}%",
            style = Typography.BODY2.copy(
                color = if (isLocked) Colors.Gray.MAIN else Colors.Text.PRIMARY
            )
        )

        if (isLocked) {
            Text(
                "Slider is locked. Enable to adjust performance.",
                style = Typography.CAPTION.copy(color = Colors.Warning.MAIN)
            )
        }
    }
}
```

## Accessibility Features

### ARIA Support

The Slider component automatically includes:

- `role="slider"` for screen readers
- `aria-valuemin`, `aria-valuemax`, `aria-valuenow` for value information
- `aria-orientation` for slider direction
- `aria-label` or `aria-labelledby` for descriptions
- `aria-describedby` for additional help text

### Keyboard Navigation

- **Arrow Left/Down**: Decrease value
- **Arrow Right/Up**: Increase value
- **Home**: Go to minimum value
- **End**: Go to maximum value
- **Page Up/Down**: Large increment/decrement

### Screen Reader Support

```kotlin
@Composable
fun AccessibleSliderExample() {
    var volume by remember { mutableStateOf(0.5f) }

    Slider(
        value = volume,
        onValueChange = { volume = it },
        modifier = Modifier()
            .accessibilityLabel("Volume control")
            .accessibilityValue("${(volume * 100).toInt()} percent")
            .accessibilityHint("Use arrow keys to adjust volume level")
    )
}
```

## Validation Patterns

### Range Validation

```kotlin
fun rangeValidator(min: Float, max: Float) = Validator { value ->
    val numValue = value.toFloatOrNull()
    if (numValue != null && numValue in min..max) {
        ValidationResult.valid()
    } else {
        ValidationResult.invalid("Value must be between $min and $max")
    }
}
```

### Custom Validation

```kotlin
fun performanceLevelValidator() = Validator { value ->
    val level = value.toFloatOrNull() ?: 0f
    when {
        level < 0.3f -> ValidationResult.invalid("Performance too low for stable operation")
        level > 0.9f -> ValidationResult.invalid("Performance may cause overheating")
        else -> ValidationResult.valid()
    }
}
```

## Platform Differences

### Browser (JS)

- Uses HTML `<input type="range">` for native support
- CSS styling through modifiers
- Smooth drag interactions
- Touch support on mobile devices

### JVM

- Server-side rendering support
- Custom slider implementation for rich interactions
- Generates appropriate HTML for SSR
- Server-side validation support

## Performance Considerations

### Optimization Tips

1. **Debounce Updates**: Prevent excessive callbacks during dragging
2. **Memoization**: Cache expensive value calculations
3. **Lazy Rendering**: Only update display when necessary
4. **Reduce Re-renders**: Optimize state management

```kotlin
@Composable
fun OptimizedSliderExample() {
    var value by remember { mutableStateOf(0.5f) }
    var displayValue by remember { mutableStateOf(value) }

    // Debounce display updates
    LaunchedEffect(value) {
        delay(100) // Debounce
        displayValue = value
    }

    // Memoize expensive calculations
    val formattedValue = remember(displayValue) {
        String.format("%.1f", displayValue * 100)
    }

    Column {
        Text("Value: $formattedValue%")
        Slider(
            value = value,
            onValueChange = { value = it }
        )
    }
}
```

## Testing Strategies

### Unit Testing

```kotlin
class SliderTest {
    @Test
    fun `slider updates value correctly`() {
        var sliderValue = 0.5f

        composeTestRule.setContent {
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it }
            )
        }

        // Simulate drag interaction
        composeTestRule.onNode(hasContentDescription("slider"))
            .performTouchInput { dragBy(Offset(100f, 0f)) }

        assertTrue(sliderValue > 0.5f)
    }

    @Test
    fun `slider respects value range`() {
        val range = 10f..90f
        var sliderValue = 50f

        composeTestRule.setContent {
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = range
            )
        }

        assertTrue(sliderValue in range)
    }
}
```

## Migration Guide

### From HTML Forms

```html
<!-- Before: HTML -->
<input type="range" min="0" max="100" value="50" step="10">
```

```kotlin
// After: Summon
@Composable
fun VolumeSlider() {
    var volume by remember { mutableStateOf(50f) }

    Slider(
        value = volume,
        onValueChange = { volume = it },
        valueRange = 0f..100f,
        steps = 9 // 10 values: 0, 10, 20, ..., 100
    )
}
```

## Best Practices

### Do

- Provide clear min/max indicators
- Show current value when appropriate
- Use appropriate step sizes for discrete values
- Provide keyboard navigation support
- Consider touch targets for mobile

### Don't

- Use sliders for precise numeric input (use TextField instead)
- Create unnecessarily complex value ranges
- Forget to handle edge cases (min/max values)
- Use sliders for binary choices (use Switch instead)

## Related Components

- [RangeSlider](RangeSlider.md) - For range selection
- [TextField](TextField.md) - For precise numeric input
- [Switch](Switch.md) - For binary choices
- [Form](Form.md) - For form integration