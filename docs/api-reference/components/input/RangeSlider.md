# RangeSlider

RangeSlider components provide dual-thumb range selection within a defined range, perfect for selecting value ranges
like price ranges, date ranges, or filtering criteria.

## Overview

The RangeSlider component allows users to select a range of values by manipulating two thumbs on a track. It's ideal for
filtering, price ranges, time ranges, and any scenario requiring minimum and maximum value selection.

### Key Features

- **Dual Thumbs**: Independent minimum and maximum value selection
- **Value Ranges**: Customizable overall range constraints
- **Discrete Steps**: Support for stepped or continuous selection
- **Visual Feedback**: Real-time range updates and visual indicators
- **Accessibility**: Full ARIA support and keyboard navigation
- **Form Integration**: Works seamlessly with forms and validation
- **Custom Styling**: Type-safe styling with modifiers
- **State Management**: Controlled and uncontrolled components

## API Reference

### RangeSlider

```kotlin
@Composable
fun RangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier(),
    valueRange: ClosedFloatingPointRange<Float> = 0.0f..1.0f,
    steps: Int = 0,
    enabled: Boolean = true
)
```

**Parameters:**

- `value`: The current selected range
- `onValueChange`: Callback invoked when the selected range changes
- `modifier`: Modifier for styling and layout
- `valueRange`: The total range allowed for selection (default: `0.0f..1.0f`)
- `steps`: Number of discrete steps, 0 for continuous (default: `0`)
- `enabled`: Whether the slider can be interacted with (default: `true`)

### StatefulRangeSlider

```kotlin
@Composable
fun StatefulRangeSlider(
    initialValue: ClosedFloatingPointRange<Float> = 0.25f..0.75f,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit = {},
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    modifier: Modifier = Modifier(),
    isEnabled: Boolean = true,
    label: String? = null,
    showTooltip: Boolean = false,
    valueFormat: (Float) -> String = { it.toString() }
)
```

**Parameters:**

- `initialValue`: Initial range value (default: `0.25f..0.75f`)
- `onValueChange`: Callback invoked when range changes
- `valueRange`: The overall allowed range
- `steps`: Number of discrete steps
- `modifier`: Modifier for styling and layout
- `isEnabled`: Whether the slider is interactive
- `label`: Optional label for the slider
- `showTooltip`: Whether to show value tooltips
- `valueFormat`: Function to format values for display

### FloatRange

```kotlin
data class FloatRange(
    override val start: Float,
    override val endInclusive: Float
) : ClosedFloatingPointRange<Float>
```

**Utility class for managing mutable float ranges.**

## Usage Examples

### Basic Range Slider

```kotlin
@Composable
fun BasicRangeSliderExample() {
    var priceRange by remember { mutableStateOf(25f..75f) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Price Range", style = Typography.H6)

        RangeSlider(
            value = priceRange,
            onValueChange = { priceRange = it },
            valueRange = 0f..100f,
            modifier = Modifier()
                .width(400.px)
                .padding(Spacing.MD)
        )

        Row(
            modifier = Modifier()
                .width(400.px)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Text("$${priceRange.start.toInt()}", style = Typography.BODY2)
            Text(
                "Range: $${priceRange.start.toInt()} - $${priceRange.endInclusive.toInt()}",
                style = Typography.BODY2.copy(fontWeight = FontWeight.MEDIUM)
            )
            Text("$${priceRange.endInclusive.toInt()}", style = Typography.BODY2)
        }
    }
}
```

### Price Filter

```kotlin
@Composable
fun PriceFilterExample() {
    var priceRange by remember { mutableStateOf(50f..200f) }
    val maxPrice = 500f

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Filter by Price", style = Typography.H6)

        Card(
            modifier = Modifier()
                .width(Width.FULL)
                .backgroundColor(Colors.Gray.LIGHT)
                .padding(Spacing.MD)
        ) {
            Column(modifier = Modifier().gap(Spacing.MD)) {
                Row(
                    modifier = Modifier()
                        .width(Width.FULL)
                        .justifyContent(JustifyContent.SpaceBetween)
                        .alignItems(AlignItems.Center)
                ) {
                    Text("Price Range", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    Text(
                        "$${priceRange.start.toInt()} - $${priceRange.endInclusive.toInt()}",
                        style = Typography.BODY2.copy(color = Colors.Primary.MAIN)
                    )
                }

                RangeSlider(
                    value = priceRange,
                    onValueChange = { priceRange = it },
                    valueRange = 0f..maxPrice,
                    modifier = Modifier().width(Width.FULL)
                )

                Row(
                    modifier = Modifier()
                        .width(Width.FULL)
                        .justifyContent(JustifyContent.SpaceBetween)
                ) {
                    Text("$0", style = Typography.CAPTION)
                    Text("$${maxPrice.toInt()}", style = Typography.CAPTION)
                }

                // Quick preset buttons
                Row(modifier = Modifier().gap(Spacing.SM)) {
                    Button(
                        text = "Under $100",
                        size = ButtonSize.SMALL,
                        type = ButtonType.OUTLINE,
                        onClick = { priceRange = 0f..100f }
                    )
                    Button(
                        text = "$100-$250",
                        size = ButtonSize.SMALL,
                        type = ButtonType.OUTLINE,
                        onClick = { priceRange = 100f..250f }
                    )
                    Button(
                        text = "$250+",
                        size = ButtonSize.SMALL,
                        type = ButtonType.OUTLINE,
                        onClick = { priceRange = 250f..maxPrice }
                    )
                }
            }
        }

        // Results summary
        val productCount = calculateProductCount(priceRange)
        Text(
            "$productCount products found in this price range",
            style = Typography.BODY2.copy(color = Colors.Success.MAIN)
        )
    }
}

private fun calculateProductCount(range: ClosedFloatingPointRange<Float>): Int {
    // Mock calculation based on price range
    return when {
        range.endInclusive - range.start < 50 -> 12
        range.endInclusive - range.start < 150 -> 45
        else -> 78
    }
}
```

### Time Range Selector

```kotlin
@Composable
fun TimeRangeSelectorExample() {
    var timeRange by remember { mutableStateOf(9f..17f) } // 9 AM to 5 PM
    val maxHour = 24f

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Available Hours", style = Typography.H6)

        RangeSlider(
            value = timeRange,
            onValueChange = { timeRange = it },
            valueRange = 0f..maxHour,
            steps = 23, // 24 hour steps (0-23)
            modifier = Modifier().width(400.px)
        )

        Row(
            modifier = Modifier()
                .width(400.px)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Text(
                formatHour(timeRange.start),
                style = Typography.BODY2.copy(fontWeight = FontWeight.MEDIUM)
            )
            Text(
                "Available: ${formatHour(timeRange.start)} - ${formatHour(timeRange.endInclusive)}",
                style = Typography.BODY2
            )
            Text(
                formatHour(timeRange.endInclusive),
                style = Typography.BODY2.copy(fontWeight = FontWeight.MEDIUM)
            )
        )

        // Duration calculation
        val duration = timeRange.endInclusive - timeRange.start
        Text(
            "Duration: ${duration.toInt()} hours",
            style = Typography.BODY2.copy(color = Colors.Info.MAIN)
        )

        // Quick presets
        Row(modifier = Modifier().gap(Spacing.SM)) {
            Button(
                text = "Business Hours",
                size = ButtonSize.SMALL,
                type = ButtonType.OUTLINE,
                onClick = { timeRange = 9f..17f }
            )
            Button(
                text = "Extended Hours",
                size = ButtonSize.SMALL,
                type = ButtonType.OUTLINE,
                onClick = { timeRange = 8f..20f }
            )
            Button(
                text = "24/7",
                size = ButtonSize.SMALL,
                type = ButtonType.OUTLINE,
                onClick = { timeRange = 0f..24f }
            )
        }
    }
}

private fun formatHour(hour: Float): String {
    val h = hour.toInt()
    return when {
        h == 0 -> "12:00 AM"
        h < 12 -> "$h:00 AM"
        h == 12 -> "12:00 PM"
        else -> "${h - 12}:00 PM"
    }
}
```

### Age Range Filter

```kotlin
@Composable
fun AgeRangeFilterExample() {
    var ageRange by remember { mutableStateOf(25f..45f) }
    val maxAge = 100f

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Target Age Range", style = Typography.H6)

        RangeSlider(
            value = ageRange,
            onValueChange = { ageRange = it },
            valueRange = 18f..maxAge,
            modifier = Modifier().width(350.px)
        )

        Row(
            modifier = Modifier()
                .width(350.px)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Text("18", style = Typography.CAPTION)
            Text(
                "Ages ${ageRange.start.toInt()} - ${ageRange.endInclusive.toInt()}",
                style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM)
            )
            Text("100+", style = Typography.CAPTION)
        }

        // Age group categorization
        Card(
            modifier = Modifier()
                .width(Width.FULL)
                .backgroundColor(Colors.Info.LIGHT)
                .padding(Spacing.MD)
        ) {
            Column(modifier = Modifier().gap(Spacing.SM)) {
                Text("Target Demographics:", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))

                val demographics = categorizeAgeRange(ageRange)
                demographics.forEach { demographic ->
                    Text("• $demographic", style = Typography.BODY2)
                }
            }
        }

        // Common age ranges
        Text("Quick Selection:", style = Typography.BODY2)
        Column(modifier = Modifier().gap(Spacing.SM)) {
            Row(modifier = Modifier().gap(Spacing.SM)) {
                Button(
                    text = "Young Adults (18-30)",
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    onClick = { ageRange = 18f..30f }
                )
                Button(
                    text = "Millennials (25-40)",
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    onClick = { ageRange = 25f..40f }
                )
            }
            Row(modifier = Modifier().gap(Spacing.SM)) {
                Button(
                    text = "Gen X (40-55)",
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    onClick = { ageRange = 40f..55f }
                )
                Button(
                    text = "Seniors (55+)",
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    onClick = { ageRange = 55f..maxAge }
                )
            }
        }
    }
}

private fun categorizeAgeRange(range: ClosedFloatingPointRange<Float>): List<String> {
    val demographics = mutableListOf<String>()

    if (range.start <= 25 && range.endInclusive >= 18) demographics.add("Young Adults")
    if (range.start <= 35 && range.endInclusive >= 25) demographics.add("Millennials")
    if (range.start <= 55 && range.endInclusive >= 35) demographics.add("Gen X")
    if (range.endInclusive >= 55) demographics.add("Baby Boomers")

    return demographics.ifEmpty { listOf("Custom Range") }
}
```

### Slider with Statistics

```kotlin
@Composable
fun StatisticsRangeSliderExample() {
    var dataRange by remember { mutableStateOf(20f..80f) }
    val dataPoints = remember { generateSampleData() }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Data Analysis Range", style = Typography.H6)

        RangeSlider(
            value = dataRange,
            onValueChange = { dataRange = it },
            valueRange = 0f..100f,
            modifier = Modifier().width(400.px)
        )

        // Statistics
        val filteredData = dataPoints.filter { it in dataRange }
        val average = if (filteredData.isNotEmpty()) filteredData.average() else 0.0
        val median = if (filteredData.isNotEmpty()) filteredData.sorted()[filteredData.size / 2] else 0f

        Card(
            modifier = Modifier()
                .width(Width.FULL)
                .backgroundColor(Colors.Gray.LIGHT)
                .padding(Spacing.MD)
        ) {
            Column(modifier = Modifier().gap(Spacing.SM)) {
                Text(
                    "Statistics for range ${dataRange.start.toInt()}-${dataRange.endInclusive.toInt()}:",
                    style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM)
                )

                Row(modifier = Modifier().gap(Spacing.LG)) {
                    Column {
                        Text("Data Points", style = Typography.CAPTION)
                        Text("${filteredData.size}", style = Typography.BODY2.copy(fontWeight = FontWeight.BOLD))
                    }
                    Column {
                        Text("Average", style = Typography.CAPTION)
                        Text("${String.format("%.1f", average)}", style = Typography.BODY2.copy(fontWeight = FontWeight.BOLD))
                    }
                    Column {
                        Text("Median", style = Typography.CAPTION)
                        Text("${median.toInt()}", style = Typography.BODY2.copy(fontWeight = FontWeight.BOLD))
                    }
                }
            }
        }

        // Data visualization (simplified)
        Box(
            modifier = Modifier()
                .width(400.px)
                .height(60.px)
                .backgroundColor(Colors.Gray.LIGHT)
                .borderRadius(BorderRadius.SM)
                .position("relative")
        ) {
            // Histogram bars (simplified representation)
            Row(
                modifier = Modifier()
                    .width(Width.FULL)
                    .height(Width.FULL)
                    .alignItems(AlignItems.End)
            ) {
                repeat(20) { index ->
                    val binValue = (index + 1) * 5f
                    val isInRange = binValue in dataRange
                    val count = dataPoints.count { it >= binValue - 2.5f && it < binValue + 2.5f }

                    Box(
                        modifier = Modifier()
                            .width(Weight(1f))
                            .height((count * 4).px.coerceAtMost(50.px))
                            .backgroundColor(
                                if (isInRange) Colors.Primary.MAIN else Colors.Gray.MAIN
                            )
                            .margin(horizontal = 1.px)
                    ) {}
                }
            }
        }
    }
}

private fun generateSampleData(): List<Float> {
    return (1..100).map { kotlin.random.Random.nextFloat() * 100f }
}
```

### Date Range Picker

```kotlin
@Composable
fun DateRangeSliderExample() {
    // Using days since epoch for easier calculation
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val startDate = today.minus(DatePeriod(days = 30))
    val endDate = today.plus(DatePeriod(days = 30))

    val totalDays = endDate.toEpochDays() - startDate.toEpochDays()
    var selectedRange by remember { mutableStateOf(7f..(totalDays - 7f)) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Select Date Range", style = Typography.H6)

        RangeSlider(
            value = selectedRange,
            onValueChange = { selectedRange = it },
            valueRange = 0f..totalDays.toFloat(),
            modifier = Modifier().width(400.px)
        )

        // Convert back to dates for display
        val selectedStartDate = startDate.plus(DatePeriod(days = selectedRange.start.toInt()))
        val selectedEndDate = startDate.plus(DatePeriod(days = selectedRange.endInclusive.toInt()))

        Row(
            modifier = Modifier()
                .width(400.px)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            Text(startDate.toString(), style = Typography.CAPTION)
            Column(modifier = Modifier().textAlign("center")) {
                Text(
                    "${selectedStartDate} to ${selectedEndDate}",
                    style = Typography.BODY2.copy(fontWeight = FontWeight.MEDIUM)
                )
                val durationDays = selectedRange.endInclusive - selectedRange.start
                Text(
                    "${durationDays.toInt()} days selected",
                    style = Typography.CAPTION.copy(color = Colors.Primary.MAIN)
                )
            }
            Text(endDate.toString(), style = Typography.CAPTION)
        }

        // Quick date range presets
        Row(modifier = Modifier().gap(Spacing.SM)) {
            Button(
                text = "Last 7 days",
                size = ButtonSize.SMALL,
                type = ButtonType.OUTLINE,
                onClick = {
                    val endDay = totalDays.toFloat()
                    selectedRange = (endDay - 7f)..endDay
                }
            )
            Button(
                text = "Next 7 days",
                size = ButtonSize.SMALL,
                type = ButtonType.OUTLINE,
                onClick = {
                    val startDay = (totalDays / 2f) - 3.5f
                    selectedRange = startDay..(startDay + 7f)
                }
            )
            Button(
                text = "Full Range",
                size = ButtonSize.SMALL,
                type = ButtonType.OUTLINE,
                onClick = { selectedRange = 0f..totalDays.toFloat() }
            )
        }
    }
}
```

### Form Integration

```kotlin
@Composable
fun RangeSliderFormExample() {
    var budgetRange by remember { mutableStateOf(5000f..15000f) }
    var experienceRange by remember { mutableStateOf(2f..8f) }
    var teamSizeRange by remember { mutableStateOf(3f..10f) }

    Form(
        onSubmit = {
            println("Project requirements:")
            println("Budget: $${budgetRange.start.toInt()} - $${budgetRange.endInclusive.toInt()}")
            println("Experience: ${experienceRange.start.toInt()} - ${experienceRange.endInclusive.toInt()} years")
            println("Team size: ${teamSizeRange.start.toInt()} - ${teamSizeRange.endInclusive.toInt()} people")
        }
    ) {
        FormField(label = "Project Requirements") {
            Column(modifier = Modifier().gap(Spacing.LG)) {
                // Budget range
                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .justifyContent(JustifyContent.SpaceBetween)
                            .alignItems(AlignItems.Center)
                    ) {
                        Text("Budget Range", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                        Text(
                            "$${budgetRange.start.toInt()}k - $${budgetRange.endInclusive.toInt()}k",
                            style = Typography.BODY2
                        )
                    }

                    RangeSlider(
                        value = budgetRange,
                        onValueChange = { budgetRange = it },
                        valueRange = 1000f..50000f,
                        modifier = Modifier().width(Width.FULL)
                    )
                }

                // Experience range
                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .justifyContent(JustifyContent.SpaceBetween)
                            .alignItems(AlignItems.Center)
                    ) {
                        Text("Experience Range", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                        Text(
                            "${experienceRange.start.toInt()} - ${experienceRange.endInclusive.toInt()} years",
                            style = Typography.BODY2
                        )
                    }

                    RangeSlider(
                        value = experienceRange,
                        onValueChange = { experienceRange = it },
                        valueRange = 0f..20f,
                        modifier = Modifier().width(Width.FULL)
                    )
                }

                // Team size range
                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .justifyContent(JustifyContent.SpaceBetween)
                            .alignItems(AlignItems.Center)
                    ) {
                        Text("Team Size", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                        Text(
                            "${teamSizeRange.start.toInt()} - ${teamSizeRange.endInclusive.toInt()} people",
                            style = Typography.BODY2
                        )
                    }

                    RangeSlider(
                        value = teamSizeRange,
                        onValueChange = { teamSizeRange = it },
                        valueRange = 1f..50f,
                        modifier = Modifier().width(Width.FULL)
                    )
                }
            }
        }

        Button(
            text = "Find Candidates",
            type = ButtonType.SUBMIT
        )
    }
}
```

### Stateful Range Slider

```kotlin
@Composable
fun StatefulRangeSliderExample() {
    StatefulRangeSlider(
        initialValue = 30f..70f,
        onValueChange = { range ->
            println("Range changed to: ${range.start} - ${range.endInclusive}")
        },
        valueRange = 0f..100f,
        label = "Performance Range",
        showTooltip = true,
        valueFormat = { "${it.toInt()}%" },
        modifier = Modifier()
            .width(350.px)
            .padding(Spacing.MD)
    )
}
```

## Accessibility Features

### ARIA Support

The RangeSlider component automatically includes:

- `role="slider"` for each thumb
- `aria-valuemin`, `aria-valuemax`, `aria-valuenow` for each thumb
- `aria-orientation` for slider direction
- `aria-label` for range description
- `aria-describedby` for additional context

### Keyboard Navigation

- **Arrow Keys**: Move active thumb
- **Tab**: Switch between thumbs
- **Home/End**: Move to min/max values
- **Page Up/Down**: Large increments

### Screen Reader Support

```kotlin
@Composable
fun AccessibleRangeSliderExample() {
    var range by remember { mutableStateOf(25f..75f) }

    RangeSlider(
        value = range,
        onValueChange = { range = it },
        modifier = Modifier()
            .accessibilityLabel("Price range selector")
            .accessibilityValue("From ${range.start.toInt()} to ${range.endInclusive.toInt()} dollars")
            .accessibilityHint("Use arrow keys to adjust range bounds")
    )
}
```

## Testing Strategies

### Unit Testing

```kotlin
class RangeSliderTest {
    @Test
    fun `range slider updates range correctly`() {
        var selectedRange = 25f..75f

        composeTestRule.setContent {
            RangeSlider(
                value = selectedRange,
                onValueChange = { selectedRange = it }
            )
        }

        // Test range update
        assertTrue(selectedRange.start == 25f)
        assertTrue(selectedRange.endInclusive == 75f)
    }

    @Test
    fun `range slider maintains valid range order`() {
        var range = 30f..70f

        composeTestRule.setContent {
            RangeSlider(
                value = range,
                onValueChange = { range = it }
            )
        }

        // Ensure start is always <= end
        assertTrue(range.start <= range.endInclusive)
    }
}
```

## Migration Guide

### From HTML Forms

```html
<!-- Before: HTML (no native range input) -->
<input type="range" id="min" min="0" max="100" value="25">
<input type="range" id="max" min="0" max="100" value="75">
```

```kotlin
// After: Summon
@Composable
fun PriceRangeSelector() {
    var priceRange by remember { mutableStateOf(25f..75f) }

    RangeSlider(
        value = priceRange,
        onValueChange = { priceRange = it },
        valueRange = 0f..100f
    )
}
```

## Best Practices

### Do

- Provide clear range indicators and labels
- Show current values prominently
- Use appropriate step sizes for your data
- Consider touch targets for mobile devices
- Validate that start <= end always

### Don't

- Make ranges too complex or confusing
- Use for single value selection (use Slider instead)
- Forget to handle edge cases (overlapping thumbs)
- Create ranges that are too narrow to interact with

## Related Components

- [Slider](Slider.md) - For single value selection
- [TextField](TextField.md) - For precise numeric input
- [DatePicker](DatePicker.md) - For date range selection
- [Form](Form.md) - For form integration