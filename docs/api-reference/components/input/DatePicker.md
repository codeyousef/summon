# DatePicker

DatePicker components provide calendar-based date selection with support for date ranges, validation, and
internationalization.

## Overview

The DatePicker component allows users to select dates using a visual calendar interface or text input. It supports date
ranges, custom formatting, and integrates seamlessly with forms and validation systems.

### Key Features

- **Calendar Interface**: Visual date selection with calendar widget
- **Date Ranges**: Minimum and maximum date constraints
- **Custom Formatting**: Configurable date display formats
- **Accessibility**: Full ARIA support and keyboard navigation
- **Form Integration**: Works seamlessly with forms and validation
- **Internationalization**: Supports different locales and date formats
- **Platform Native**: Uses native date pickers when available

## API Reference

### DatePicker

```kotlin
@Composable
fun DatePicker(
    value: LocalDate?,
    onValueChange: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    dateFormat: String = "yyyy-MM-dd",
    initialDisplayMonth: LocalDate? = null
)
```

**Parameters:**

- `value`: The currently selected date, or null if none selected
- `onValueChange`: Callback invoked when the user selects a new date
- `modifier`: Modifier for styling and layout
- `enabled`: Whether the date picker can be interacted with (default: `true`)
- `label`: Optional label displayed for the date picker
- `minDate`: Minimum selectable date (inclusive), null means no lower bound
- `maxDate`: Maximum selectable date (inclusive), null means no upper bound
- `dateFormat`: Format string for date display (default: "yyyy-MM-dd")
- `initialDisplayMonth`: Initial month to display in calendar

## Usage Examples

### Basic Date Picker

```kotlin
@Composable
fun BasicDatePickerExample() {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        DatePicker(
            value = selectedDate,
            onValueChange = { selectedDate = it },
            label = "Select Date",
            modifier = Modifier()
                .width(300.px)
                .padding(Spacing.MD)
        )

        selectedDate?.let { date ->
            Text(
                "Selected: ${date.toString()}",
                style = Typography.BODY2,
                modifier = Modifier()
                    .padding(Spacing.SM)
                    .backgroundColor(Colors.Info.LIGHT)
                    .borderRadius(BorderRadius.SM)
                    .padding(Spacing.SM)
            )
        }
    }
}
```

### Date Range Constraints

```kotlin
@Composable
fun DateRangeExample() {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val maxDate = today.plus(DatePeriod(months = 6))

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Book Appointment", style = Typography.H6)
        Text(
            "Select a date within the next 6 months",
            style = Typography.BODY2.copy(color = Colors.Gray.MAIN)
        )

        DatePicker(
            value = selectedDate,
            onValueChange = { selectedDate = it },
            label = "Appointment Date",
            minDate = today,
            maxDate = maxDate,
            modifier = Modifier().width(300.px)
        )

        selectedDate?.let { date ->
            val daysFromNow = date.toEpochDays() - today.toEpochDays()
            Text(
                "Appointment in $daysFromNow days",
                style = Typography.BODY2.copy(color = Colors.Success.MAIN)
            )
        }
    }
}
```

### Birthday Date Picker

```kotlin
@Composable
fun BirthdayPickerExample() {
    var birthDate by remember { mutableStateOf<LocalDate?>(null) }
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val minDate = LocalDate(1900, 1, 1)
    val maxDate = today.minus(DatePeriod(years = 13)) // Must be at least 13 years old

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Enter Your Birthday", style = Typography.H6)

        DatePicker(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = "Date of Birth",
            minDate = minDate,
            maxDate = maxDate,
            dateFormat = "MMM dd, yyyy",
            initialDisplayMonth = LocalDate(1995, 6, 1), // Start at a reasonable default
            modifier = Modifier().width(300.px)
        )

        birthDate?.let { date ->
            val age = Period.between(date, today).years
            Text(
                "Age: $age years old",
                style = Typography.BODY2.copy(color = Colors.Info.MAIN)
            )
        }
    }
}
```

### Custom Date Formats

```kotlin
@Composable
fun DateFormatExample() {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedFormat by remember { mutableStateOf("yyyy-MM-dd") }

    val formats = listOf(
        "yyyy-MM-dd" to "2024-03-15",
        "MM/dd/yyyy" to "03/15/2024",
        "dd.MM.yyyy" to "15.03.2024",
        "MMM dd, yyyy" to "Mar 15, 2024",
        "EEEE, MMMM dd, yyyy" to "Friday, March 15, 2024"
    )

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Date Format Options", style = Typography.H6)

        // Format selection
        Column(modifier = Modifier().gap(Spacing.SM)) {
            Text("Select Format:", style = Typography.BODY2)
            formats.forEach { (format, example) ->
                Row(
                    modifier = Modifier()
                        .alignItems(AlignItems.Center)
                        .gap(Spacing.SM)
                ) {
                    RadioButton(
                        selected = selectedFormat == format,
                        onClick = { selectedFormat = format },
                        label = "$format (e.g., $example)"
                    )
                }
            }
        }

        Divider()

        DatePicker(
            value = selectedDate,
            onValueChange = { selectedDate = it },
            label = "Date with Custom Format",
            dateFormat = selectedFormat,
            modifier = Modifier().width(350.px)
        )

        selectedDate?.let { date ->
            Text(
                "Raw value: $date",
                style = Typography.CAPTION.copy(color = Colors.Gray.MAIN)
            )
        }
    }
}
```

### Event Date Selection

```kotlin
data class Event(
    val id: String,
    val name: String,
    val date: LocalDate,
    val type: EventType
)

enum class EventType { MEETING, DEADLINE, HOLIDAY, PERSONAL }

@Composable
fun EventDatePickerExample() {
    var eventDate by remember { mutableStateOf<LocalDate?>(null) }
    var eventName by remember { mutableStateOf("") }
    var eventType by remember { mutableStateOf(EventType.MEETING) }

    val existingEvents = remember {
        listOf(
            Event("1", "Team Meeting", LocalDate(2024, 3, 15), EventType.MEETING),
            Event("2", "Project Deadline", LocalDate(2024, 3, 20), EventType.DEADLINE),
            Event("3", "Holiday", LocalDate(2024, 3, 25), EventType.HOLIDAY)
        )
    }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Schedule New Event", style = Typography.H6)

        TextField(
            value = eventName,
            onValueChange = { eventName = it },
            label = "Event Name",
            modifier = Modifier().width(300.px)
        )

        DatePicker(
            value = eventDate,
            onValueChange = { eventDate = it },
            label = "Event Date",
            minDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            modifier = Modifier().width(300.px)
        )

        // Show conflicts
        eventDate?.let { date ->
            val conflictingEvents = existingEvents.filter { it.date == date }
            if (conflictingEvents.isNotEmpty()) {
                Alert(
                    type = AlertType.WARNING,
                    title = "Schedule Conflict",
                    message = "You have ${conflictingEvents.size} existing event(s) on this date:",
                    modifier = Modifier().width(300.px)
                ) {
                    Column(modifier = Modifier().gap(Spacing.XS)) {
                        conflictingEvents.forEach { event ->
                            Text(
                                "• ${event.name} (${event.type.name.lowercase()})",
                                style = Typography.BODY2
                            )
                        }
                    }
                }
            }
        }

        Button(
            text = "Schedule Event",
            enabled = eventName.isNotBlank() && eventDate != null,
            onClick = {
                println("Scheduling: $eventName on $eventDate")
            }
        )
    }
}
```

### Form Integration

```kotlin
@Composable
fun DatePickerFormExample() {
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var title by remember { mutableStateOf("") }

    Form(
        onSubmit = {
            println("Submitted:")
            println("Title: $title")
            println("Start: $startDate")
            println("End: $endDate")
        }
    ) {
        FormField(label = "Event Details") {
            Column(modifier = Modifier().gap(Spacing.MD)) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Event Title *",
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

                Row(modifier = Modifier().gap(Spacing.MD)) {
                    DatePicker(
                        value = startDate,
                        onValueChange = {
                            startDate = it
                            // Auto-adjust end date if it's before start date
                            if (endDate != null && it != null && endDate!! < it) {
                                endDate = it
                            }
                        },
                        label = "Start Date *",
                        minDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                        modifier = Modifier().width(200.px)
                    )

                    DatePicker(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = "End Date *",
                        minDate = startDate ?: Clock.System.todayIn(TimeZone.currentSystemDefault()),
                        modifier = Modifier().width(200.px)
                    )
                }

                // Show duration
                if (startDate != null && endDate != null) {
                    val duration = endDate!!.toEpochDays() - startDate!!.toEpochDays() + 1
                    Text(
                        "Duration: $duration day${if (duration != 1L) "s" else ""}",
                        style = Typography.BODY2.copy(color = Colors.Info.MAIN)
                    )
                }
            }
        }

        Button(
            text = "Create Event",
            type = ButtonType.SUBMIT,
            enabled = title.isNotBlank() && startDate != null && endDate != null
        )
    }
}
```

### Vacation Planner

```kotlin
@Composable
fun VacationPlannerExample() {
    var departureDate by remember { mutableStateOf<LocalDate?>(null) }
    var returnDate by remember { mutableStateOf<LocalDate?>(null) }

    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val maxAdvanceBooking = today.plus(DatePeriod(years = 1))

    // Blocked dates (weekends in this example)
    fun isWeekend(date: LocalDate): Boolean {
        return date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
    }

    Column(modifier = Modifier().gap(Spacing.LG)) {
        Text("Plan Your Vacation", style = Typography.H5)

        Row(modifier = Modifier().gap(Spacing.LG)) {
            Column(modifier = Modifier().gap(Spacing.MD)) {
                DatePicker(
                    value = departureDate,
                    onValueChange = {
                        departureDate = it
                        // Auto-set minimum return date
                        if (returnDate != null && it != null && returnDate!! <= it) {
                            returnDate = it.plus(DatePeriod(days = 1))
                        }
                    },
                    label = "Departure Date",
                    minDate = today.plus(DatePeriod(days = 1)), // Must book at least 1 day ahead
                    maxDate = maxAdvanceBooking,
                    modifier = Modifier().width(250.px)
                )

                departureDate?.let { depDate ->
                    if (isWeekend(depDate)) {
                        Text(
                            "⚠️ Weekend departure may have higher prices",
                            style = Typography.CAPTION.copy(color = Colors.Warning.MAIN)
                        )
                    }
                }
            }

            Column(modifier = Modifier().gap(Spacing.MD)) {
                DatePicker(
                    value = returnDate,
                    onValueChange = { returnDate = it },
                    label = "Return Date",
                    minDate = departureDate?.plus(DatePeriod(days = 1)) ?: today,
                    maxDate = maxAdvanceBooking,
                    enabled = departureDate != null,
                    modifier = Modifier().width(250.px)
                )

                returnDate?.let { retDate ->
                    if (isWeekend(retDate)) {
                        Text(
                            "⚠️ Weekend return may have higher prices",
                            style = Typography.CAPTION.copy(color = Colors.Warning.MAIN)
                        )
                    }
                }
            }
        }

        if (departureDate != null && returnDate != null) {
            val duration = returnDate!!.toEpochDays() - departureDate!!.toEpochDays()
            val weekends = (0 until duration).count { dayOffset ->
                isWeekend(departureDate!!.plus(DatePeriod(days = dayOffset.toInt())))
            }

            Card(
                modifier = Modifier()
                    .width(Width.FULL)
                    .backgroundColor(Colors.Primary.LIGHT)
                    .padding(Spacing.MD)
            ) {
                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Text(
                        "Vacation Summary",
                        style = Typography.H6.copy(color = Colors.Primary.DARK)
                    )
                    Text("Duration: $duration days")
                    Text("Weekend days: $weekends")
                    Text("Weekdays: ${duration - weekends}")

                    val estimatedCost = (duration * 150) + (weekends * 50) // Weekend premium
                    Text(
                        "Estimated cost: $${estimatedCost}",
                        style = Typography.BODY1.copy(fontWeight = FontWeight.BOLD)
                    )
                }
            }
        }
    }
}
```

### Disabled State

```kotlin
@Composable
fun DisabledDatePickerExample() {
    var isFormLocked by remember { mutableStateOf(true) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Row(
            modifier = Modifier()
                .alignItems(AlignItems.Center)
                .gap(Spacing.MD)
        ) {
            Switch(
                checked = !isFormLocked,
                onCheckedChange = { isFormLocked = !it }
            )
            Text("Enable date selection")
        }

        DatePicker(
            value = selectedDate,
            onValueChange = { selectedDate = it },
            label = "Event Date",
            enabled = !isFormLocked,
            modifier = Modifier()
                .width(300.px)
                .opacity(if (isFormLocked) 0.6f else 1.0f)
        )

        if (isFormLocked) {
            Text(
                "Enable the switch above to select a date",
                style = Typography.CAPTION.copy(color = Colors.Gray.MAIN)
            )
        }
    }
}
```

## Accessibility Features

### ARIA Support

The DatePicker component automatically includes:

- `role="textbox"` for the input element
- `aria-label` for screen reader description
- `aria-expanded` for calendar popup state
- `aria-describedby` for help text and errors
- `aria-invalid` for validation state

### Keyboard Navigation

- **Tab**: Move focus to the date picker
- **Enter/Space**: Open calendar popup
- **Arrow Keys**: Navigate calendar dates
- **Escape**: Close calendar popup
- **Home/End**: Go to start/end of week
- **Page Up/Down**: Navigate months

### Screen Reader Support

```kotlin
@Composable
fun AccessibleDatePickerExample() {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    DatePicker(
        value = selectedDate,
        onValueChange = { selectedDate = it },
        label = "Appointment Date",
        modifier = Modifier()
            .accessibilityLabel("Select appointment date")
            .accessibilityHint("Use arrow keys to navigate calendar, Enter to select date")
            .accessibilityRole("button") // For the calendar trigger
    )
}
```

## Validation Patterns

### Required Date

```kotlin
fun requiredDateValidator() = Validator { value ->
    if (value.isNullOrBlank()) {
        ValidationResult.invalid("Date is required")
    } else {
        ValidationResult.valid()
    }
}

@Composable
fun RequiredDateExample() {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    DatePicker(
        value = selectedDate,
        onValueChange = { selectedDate = it },
        label = "Required Date *",
        // Note: Validation would be handled in a form context
        modifier = Modifier().width(300.px)
    )
}
```

### Future Date Validation

```kotlin
fun futureDateValidator() = Validator { value ->
    if (value.isBlank()) return@Validator ValidationResult.valid() // Allow empty for optional fields

    try {
        val date = LocalDate.parse(value)
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        if (date > today) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid("Date must be in the future")
        }
    } catch (e: Exception) {
        ValidationResult.invalid("Invalid date format")
    }
}
```

### Business Day Validation

```kotlin
fun businessDayValidator() = Validator { value ->
    if (value.isBlank()) return@Validator ValidationResult.valid()

    try {
        val date = LocalDate.parse(value)
        if (date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY) {
            ValidationResult.invalid("Please select a business day (Monday-Friday)")
        } else {
            ValidationResult.valid()
        }
    } catch (e: Exception) {
        ValidationResult.invalid("Invalid date format")
    }
}
```

## Platform Differences

### Browser (JS)

- Uses HTML `<input type="date">` for native support
- Falls back to custom calendar widget when needed
- CSS styling through modifiers
- Browser-specific date formatting

### JVM

- Server-side rendering support
- Generates appropriate HTML for SSR
- Custom calendar implementation for rich interactions
- Server-side validation support

## Performance Considerations

### Optimization Tips

1. **Lazy Calendar Rendering**: Only render visible month
2. **Memoization**: Cache expensive date calculations
3. **Debouncing**: Debounce rapid date changes
4. **Virtual Scrolling**: For large date ranges

```kotlin
@Composable
fun OptimizedDatePickerExample() {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // Memoize expensive calculations
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val dateRange = remember { today..today.plus(DatePeriod(years = 1)) }

    // Debounced date change handler
    var pendingDate by remember { mutableStateOf<LocalDate?>(null) }

    LaunchedEffect(pendingDate) {
        pendingDate?.let { date ->
            delay(300) // Debounce
            selectedDate = date
        }
    }

    DatePicker(
        value = selectedDate,
        onValueChange = { pendingDate = it },
        minDate = dateRange.start,
        maxDate = dateRange.endInclusive,
        modifier = Modifier().width(300.px)
    )
}
```

## Testing Strategies

### Unit Testing

```kotlin
class DatePickerTest {
    @Test
    fun `date picker updates value correctly`() {
        var selectedDate: LocalDate? = null
        val testDate = LocalDate(2024, 3, 15)

        composeTestRule.setContent {
            DatePicker(
                value = selectedDate,
                onValueChange = { selectedDate = it }
            )
        }

        // Simulate date selection
        composeTestRule.onNodeWithContentDescription("date picker").performClick()
        composeTestRule.onNodeWithText("15").performClick()

        assertEquals(testDate, selectedDate)
    }

    @Test
    fun `date picker respects min/max constraints`() {
        val minDate = LocalDate(2024, 1, 1)
        val maxDate = LocalDate(2024, 12, 31)
        var selectedDate: LocalDate? = null

        composeTestRule.setContent {
            DatePicker(
                value = selectedDate,
                onValueChange = { selectedDate = it },
                minDate = minDate,
                maxDate = maxDate
            )
        }

        // Test that dates outside range are disabled
        composeTestRule.onNodeWithText("31")
            .assertIsNotEnabled() // Assuming this is outside the valid range
    }
}
```

### Integration Testing

```kotlin
@Test
fun `date picker integrates with form validation`() {
    var submittedDate: LocalDate? = null

    composeTestRule.setContent {
        var dateValue by remember { mutableStateOf<LocalDate?>(null) }

        Form(onSubmit = { submittedDate = dateValue }) {
            DatePicker(
                value = dateValue,
                onValueChange = { dateValue = it }
            )
            Button(text = "Submit", type = ButtonType.SUBMIT)
        }
    }

    // Select date and submit
    val testDate = LocalDate(2024, 3, 15)
    composeTestRule.onNodeWithContentDescription("date picker").performClick()
    composeTestRule.onNodeWithText("15").performClick()
    composeTestRule.onNodeWithText("Submit").performClick()

    assertEquals(testDate, submittedDate)
}
```

## Migration Guide

### From HTML Forms

```html
<!-- Before: HTML -->
<input type="date" name="event_date" min="2024-01-01" max="2024-12-31">
```

```kotlin
// After: Summon
@Composable
fun EventDateSelection() {
    var eventDate by remember { mutableStateOf<LocalDate?>(null) }

    DatePicker(
        value = eventDate,
        onValueChange = { eventDate = it },
        minDate = LocalDate(2024, 1, 1),
        maxDate = LocalDate(2024, 12, 31)
    )
}
```

### From Other Frameworks

```jsx
// React example
const [date, setDate] = useState(null);
<input
    type="date"
    value={date}
    onChange={(e) => setDate(e.target.value)}
    min="2024-01-01"
/>
```

```kotlin
// Summon equivalent
var date by remember { mutableStateOf<LocalDate?>(null) }
DatePicker(
    value = date,
    onValueChange = { date = it },
    minDate = LocalDate(2024, 1, 1)
)
```

## Best Practices

### Do

- Provide clear date format expectations
- Use appropriate min/max constraints
- Validate dates on both client and server
- Consider time zones for global applications
- Provide keyboard navigation support

### Don't

- Force specific date formats without good reason
- Forget to handle invalid dates gracefully
- Use date pickers for distant past/future dates
- Ignore accessibility requirements

## Related Components

- [TimePicker](TimePicker.md) - For time selection
- [TextField](TextField.md) - For text-based date input
- [Form](Form.md) - For form integration
- [FormField](FormField.md) - For field wrapping