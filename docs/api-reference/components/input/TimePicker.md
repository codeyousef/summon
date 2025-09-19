# TimePicker

TimePicker components provide time selection functionality with support for 12/24-hour formats, time ranges, and
validation.

## Overview

The TimePicker component allows users to select time values using a visual time picker interface or text input. It
supports various time formats, validation, and integrates seamlessly with forms and date/time systems.

### Key Features

- **12/24-Hour Formats**: Support for both time display modes
- **Time Validation**: Built-in time validation and constraints
- **Accessibility**: Full ARIA support and keyboard navigation
- **Form Integration**: Works seamlessly with forms and validation
- **Platform Native**: Uses native time pickers when available
- **Custom Formatting**: Configurable time display formats
- **State Management**: Controlled and uncontrolled components

## API Reference

### TimePicker

```kotlin
@Composable
fun TimePicker(
    value: LocalTime?,
    onValueChange: (LocalTime?) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    is24Hour: Boolean = false,
    label: String? = null
)
```

**Parameters:**

- `value`: The currently selected time using kotlinx.datetime.LocalTime
- `onValueChange`: Callback invoked when the time changes
- `modifier`: Modifier for styling and layout
- `enabled`: Whether the time picker can be interacted with (default: `true`)
- `is24Hour`: Whether to use 24-hour format (default: `false`)
- `label`: Optional label for the time picker

### StatefulTimePicker

```kotlin
@Composable
fun StatefulTimePicker(
    initialValue: LocalTime? = null,
    onValueChange: (LocalTime?) -> Unit = {},
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    is24Hour: Boolean = false,
    label: String? = null
)
```

**Parameters:**

- `initialValue`: Initial time value (default: `null`)
- `onValueChange`: Callback invoked when time changes
- Other parameters same as `TimePicker`

## Usage Examples

### Basic Time Picker

```kotlin
@Composable
fun BasicTimePickerExample() {
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        TimePicker(
            value = selectedTime,
            onValueChange = { selectedTime = it },
            label = "Select Time",
            modifier = Modifier()
                .width(250.px)
                .padding(Spacing.MD)
        )

        selectedTime?.let { time ->
            Text(
                "Selected: ${formatTime(time)}",
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

private fun formatTime(time: LocalTime): String {
    val hour = if (time.hour == 0) 12 else if (time.hour > 12) time.hour - 12 else time.hour
    val minute = time.minute.toString().padStart(2, '0')
    val amPm = if (time.hour < 12) "AM" else "PM"
    return "$hour:$minute $amPm"
}
```

### 12-Hour vs 24-Hour Format

```kotlin
@Composable
fun TimeFormatExample() {
    var selectedTime by remember { mutableStateOf(LocalTime(14, 30)) } // 2:30 PM
    var is24Hour by remember { mutableStateOf(false) }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Time Format Comparison", style = Typography.H6)

        Row(
            modifier = Modifier()
                .alignItems(AlignItems.Center)
                .gap(Spacing.MD)
        ) {
            Switch(
                checked = is24Hour,
                onCheckedChange = { is24Hour = it }
            )
            Text("Use 24-hour format")
        }

        Row(modifier = Modifier().gap(Spacing.LG)) {
            Column(modifier = Modifier().gap(Spacing.SM)) {
                Text("12-Hour Format", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                TimePicker(
                    value = selectedTime,
                    onValueChange = { selectedTime = it ?: selectedTime },
                    is24Hour = false,
                    label = "12-Hour Time",
                    modifier = Modifier().width(200.px)
                )
                Text(
                    formatTime12Hour(selectedTime),
                    style = Typography.BODY2.copy(color = Colors.Primary.MAIN)
                )
            }

            Column(modifier = Modifier().gap(Spacing.SM)) {
                Text("24-Hour Format", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                TimePicker(
                    value = selectedTime,
                    onValueChange = { selectedTime = it ?: selectedTime },
                    is24Hour = true,
                    label = "24-Hour Time",
                    modifier = Modifier().width(200.px)
                )
                Text(
                    formatTime24Hour(selectedTime),
                    style = Typography.BODY2.copy(color = Colors.Primary.MAIN)
                )
            }
        }
    }
}

private fun formatTime12Hour(time: LocalTime): String {
    val hour = if (time.hour == 0) 12 else if (time.hour > 12) time.hour - 12 else time.hour
    val minute = time.minute.toString().padStart(2, '0')
    val amPm = if (time.hour < 12) "AM" else "PM"
    return "$hour:$minute $amPm"
}

private fun formatTime24Hour(time: LocalTime): String {
    val hour = time.hour.toString().padStart(2, '0')
    val minute = time.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}
```

### Meeting Scheduler

```kotlin
@Composable
fun MeetingSchedulerExample() {
    var startTime by remember { mutableStateOf<LocalTime?>(LocalTime(9, 0)) }
    var endTime by remember { mutableStateOf<LocalTime?>(LocalTime(10, 0)) }
    var meetingTitle by remember { mutableStateOf("") }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Schedule Meeting", style = Typography.H6)

        TextField(
            value = meetingTitle,
            onValueChange = { meetingTitle = it },
            label = "Meeting Title",
            modifier = Modifier().width(400.px)
        )

        Row(modifier = Modifier().gap(Spacing.MD)) {
            TimePicker(
                value = startTime,
                onValueChange = {
                    startTime = it
                    // Auto-adjust end time to be at least 30 minutes later
                    if (it != null && (endTime == null || endTime!! <= it)) {
                        endTime = it.plus(30, DateTimeUnit.MINUTE)
                    }
                },
                label = "Start Time",
                is24Hour = true,
                modifier = Modifier().width(180.px)
            )

            TimePicker(
                value = endTime,
                onValueChange = { endTime = it },
                label = "End Time",
                is24Hour = true,
                enabled = startTime != null,
                modifier = Modifier().width(180.px)
            )
        }

        // Duration calculation
        if (startTime != null && endTime != null && endTime!! > startTime!!) {
            val duration = Duration.between(startTime, endTime)
            val hours = duration.toHours()
            val minutes = duration.toMinutes() % 60

            Text(
                "Duration: ${if (hours > 0) "${hours}h " else ""}${minutes}m",
                style = Typography.BODY2.copy(color = Colors.Success.MAIN)
            )
        }

        // Time conflict warning
        if (startTime != null && endTime != null) {
            val conflictMessage = checkTimeConflicts(startTime!!, endTime!!)
            conflictMessage?.let { message ->
                Alert(
                    type = AlertType.WARNING,
                    title = "Schedule Conflict",
                    message = message
                )
            }
        }

        Button(
            text = "Schedule Meeting",
            enabled = meetingTitle.isNotBlank() && startTime != null && endTime != null &&
                     endTime!! > startTime!!,
            onClick = {
                println("Meeting scheduled: $meetingTitle from $startTime to $endTime")
            }
        )
    }
}

private fun checkTimeConflicts(start: LocalTime, end: LocalTime): String? {
    // Example business logic
    return when {
        start.hour < 8 || end.hour > 18 -> "Meeting outside business hours (8 AM - 6 PM)"
        start.hour == 12 && start.minute < 60 && end.hour >= 13 -> "Conflicts with lunch break"
        else -> null
    }
}
```

### Alarm Clock

```kotlin
@Composable
fun AlarmClockExample() {
    var alarmTime by remember { mutableStateOf<LocalTime?>(LocalTime(7, 0)) }
    var isAlarmEnabled by remember { mutableStateOf(false) }
    var selectedDays by remember { mutableStateOf(setOf<DayOfWeek>()) }

    val weekdays = DayOfWeek.values()

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Set Alarm", style = Typography.H6)

        Row(
            modifier = Modifier()
                .alignItems(AlignItems.Center)
                .gap(Spacing.LG)
        ) {
            TimePicker(
                value = alarmTime,
                onValueChange = { alarmTime = it },
                label = "Alarm Time",
                is24Hour = false,
                modifier = Modifier().width(200.px)
            )

            Column(modifier = Modifier().gap(Spacing.SM)) {
                Row(
                    modifier = Modifier()
                        .alignItems(AlignItems.Center)
                        .gap(Spacing.SM)
                ) {
                    Switch(
                        checked = isAlarmEnabled,
                        onCheckedChange = { isAlarmEnabled = it }
                    )
                    Text("Enable Alarm")
                }

                alarmTime?.let { time ->
                    Text(
                        "Alarm set for ${formatTime12Hour(time)}",
                        style = Typography.BODY2.copy(
                            color = if (isAlarmEnabled) Colors.Success.MAIN else Colors.Gray.MAIN
                        )
                    )
                }
            }
        }

        // Day selection
        Text("Repeat on:", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
        Row(
            modifier = Modifier()
                .gap(Spacing.SM)
                .flexWrap("wrap")
        ) {
            weekdays.forEach { day ->
                val dayName = day.name.take(3) // Mon, Tue, etc.
                Button(
                    text = dayName,
                    size = ButtonSize.SMALL,
                    type = if (day in selectedDays) ButtonType.PRIMARY else ButtonType.SECONDARY,
                    onClick = {
                        selectedDays = if (day in selectedDays) {
                            selectedDays - day
                        } else {
                            selectedDays + day
                        }
                    },
                    modifier = Modifier().minWidth(50.px)
                )
            }
        }

        if (selectedDays.isEmpty()) {
            Text(
                "Select at least one day for the alarm to repeat",
                style = Typography.CAPTION.copy(color = Colors.Warning.MAIN)
            )
        }

        // Quick preset buttons
        Text("Quick presets:", style = Typography.BODY2)
        Row(modifier = Modifier().gap(Spacing.SM)) {
            Button(
                text = "6:00 AM",
                size = ButtonSize.SMALL,
                type = ButtonType.OUTLINE,
                onClick = { alarmTime = LocalTime(6, 0) }
            )
            Button(
                text = "7:00 AM",
                size = ButtonSize.SMALL,
                type = ButtonType.OUTLINE,
                onClick = { alarmTime = LocalTime(7, 0) }
            )
            Button(
                text = "8:00 AM",
                size = ButtonSize.SMALL,
                type = ButtonType.OUTLINE,
                onClick = { alarmTime = LocalTime(8, 0) }
            )
        }
    }
}
```

### Time Range Selector

```kotlin
data class TimeSlot(val start: LocalTime, val end: LocalTime, val available: Boolean)

@Composable
fun TimeRangeSelectorExample() {
    var selectedTimeSlot by remember { mutableStateOf<TimeSlot?>(null) }

    val availableSlots = remember {
        listOf(
            TimeSlot(LocalTime(9, 0), LocalTime(10, 0), true),
            TimeSlot(LocalTime(10, 0), LocalTime(11, 0), true),
            TimeSlot(LocalTime(11, 0), LocalTime(12, 0), false), // Unavailable
            TimeSlot(LocalTime(14, 0), LocalTime(15, 0), true),
            TimeSlot(LocalTime(15, 0), LocalTime(16, 0), true),
            TimeSlot(LocalTime(16, 0), LocalTime(17, 0), true)
        )
    }

    Column(modifier = Modifier().gap(Spacing.MD)) {
        Text("Select Available Time Slot", style = Typography.H6)

        Text("Available slots:", style = Typography.BODY2)

        Column(modifier = Modifier().gap(Spacing.SM)) {
            availableSlots.forEach { slot ->
                Row(
                    modifier = Modifier()
                        .width(Width.FULL)
                        .padding(Spacing.SM)
                        .border(
                            Border.solid(
                                1.px,
                                when {
                                    selectedTimeSlot == slot -> Colors.Primary.MAIN
                                    slot.available -> Colors.Gray.LIGHT
                                    else -> Colors.Error.LIGHT
                                }
                            )
                        )
                        .borderRadius(BorderRadius.MD)
                        .backgroundColor(
                            when {
                                selectedTimeSlot == slot -> Colors.Primary.LIGHT
                                slot.available -> Colors.White
                                else -> Colors.Gray.LIGHT
                            }
                        )
                        .cursor(if (slot.available) "pointer" else "not-allowed")
                        .onClick { if (slot.available) selectedTimeSlot = slot }
                        .justifyContent(JustifyContent.SpaceBetween)
                        .alignItems(AlignItems.Center)
                ) {
                    Text(
                        "${formatTime12Hour(slot.start)} - ${formatTime12Hour(slot.end)}",
                        style = Typography.BODY2.copy(
                            color = if (slot.available) Colors.Text.PRIMARY else Colors.Gray.MAIN
                        )
                    )

                    if (!slot.available) {
                        Text(
                            "Unavailable",
                            style = Typography.CAPTION.copy(color = Colors.Error.MAIN)
                        )
                    } else if (selectedTimeSlot == slot) {
                        Icon(name = "check", color = Colors.Primary.MAIN)
                    }
                }
            }
        }

        selectedTimeSlot?.let { slot ->
            Card(
                modifier = Modifier()
                    .width(Width.FULL)
                    .backgroundColor(Colors.Success.LIGHT)
                    .padding(Spacing.MD)
            ) {
                Text(
                    "Selected: ${formatTime12Hour(slot.start)} - ${formatTime12Hour(slot.end)}",
                    style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM)
                )
            }
        }
    }
}
```

### Stateful Time Picker

```kotlin
@Composable
fun StatefulTimePickerExample() {
    StatefulTimePicker(
        initialValue = LocalTime(12, 0),
        onValueChange = { time ->
            println("Time changed to: $time")
        },
        label = "Lunch Break",
        is24Hour = true,
        modifier = Modifier()
            .width(250.px)
            .padding(Spacing.MD)
    )
}
```

### Form Integration

```kotlin
@Composable
fun TimePickerFormExample() {
    var eventTitle by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf<LocalDate?>(null) }
    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }
    var isAllDay by remember { mutableStateOf(false) }

    Form(
        onSubmit = {
            println("Event created:")
            println("Title: $eventTitle")
            println("Date: $eventDate")
            if (!isAllDay) {
                println("Time: $startTime - $endTime")
            } else {
                println("All day event")
            }
        }
    ) {
        FormField(label = "Event Details") {
            Column(modifier = Modifier().gap(Spacing.MD)) {
                TextField(
                    value = eventTitle,
                    onValueChange = { eventTitle = it },
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

                DatePicker(
                    value = eventDate,
                    onValueChange = { eventDate = it },
                    label = "Event Date *",
                    minDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                    modifier = Modifier().width(300.px)
                )

                Row(
                    modifier = Modifier()
                        .alignItems(AlignItems.Center)
                        .gap(Spacing.MD)
                ) {
                    Switch(
                        checked = isAllDay,
                        onCheckedChange = {
                            isAllDay = it
                            if (it) {
                                startTime = null
                                endTime = null
                            }
                        }
                    )
                    Text("All day event")
                }

                if (!isAllDay) {
                    Row(modifier = Modifier().gap(Spacing.MD)) {
                        TimePicker(
                            value = startTime,
                            onValueChange = {
                                startTime = it
                                // Auto-set end time to 1 hour later
                                if (it != null && endTime == null) {
                                    endTime = it.plus(1, DateTimeUnit.HOUR)
                                }
                            },
                            label = "Start Time *",
                            is24Hour = true,
                            modifier = Modifier().width(180.px)
                        )

                        TimePicker(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = "End Time *",
                            is24Hour = true,
                            enabled = startTime != null,
                            modifier = Modifier().width(180.px)
                        )
                    }
                }
            }
        }

        Button(
            text = "Create Event",
            type = ButtonType.SUBMIT,
            enabled = eventTitle.isNotBlank() && eventDate != null &&
                     (isAllDay || (startTime != null && endTime != null))
        )
    }
}
```

### Custom Styling

```kotlin
@Composable
fun StyledTimePickerExample() {
    var selectedTime by remember { mutableStateOf<LocalTime?>(LocalTime(15, 30)) }

    Column(modifier = Modifier().gap(Spacing.LG)) {
        Text("Custom Styled Time Pickers:", style = Typography.H6)

        // Large time picker
        TimePicker(
            value = selectedTime,
            onValueChange = { selectedTime = it },
            label = "Large Time Picker",
            is24Hour = true,
            modifier = Modifier()
                .width(300.px)
                .fontSize(18.px)
                .padding(Spacing.LG)
                .backgroundColor(Colors.Primary.LIGHT)
                .borderRadius(BorderRadius.LG)
                .border(Border.solid(2.px, Colors.Primary.MAIN))
        )

        // Compact time picker
        TimePicker(
            value = selectedTime,
            onValueChange = { selectedTime = it },
            label = "Compact",
            is24Hour = false,
            modifier = Modifier()
                .width(150.px)
                .height(32.px)
                .fontSize(14.px)
                .borderRadius(BorderRadius.SM)
        )

        // Dark theme time picker
        TimePicker(
            value = selectedTime,
            onValueChange = { selectedTime = it },
            label = "Dark Theme",
            is24Hour = true,
            modifier = Modifier()
                .width(250.px)
                .backgroundColor(Colors.Gray.DARK)
                .color(Colors.White)
                .borderRadius(BorderRadius.MD)
                .padding(Spacing.MD)
        )
    }
}
```

## Accessibility Features

### ARIA Support

The TimePicker component automatically includes:

- `role="textbox"` for the input element
- `aria-label` for screen reader description
- `aria-expanded` for time picker popup state
- `aria-describedby` for help text and errors
- `aria-invalid` for validation state

### Keyboard Navigation

- **Tab**: Move focus to the time picker
- **Enter/Space**: Open time picker popup
- **Arrow Keys**: Navigate time values (hours/minutes)
- **Escape**: Close time picker popup
- **Page Up/Down**: Increment/decrement by larger amounts

### Screen Reader Support

```kotlin
@Composable
fun AccessibleTimePickerExample() {
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    TimePicker(
        value = selectedTime,
        onValueChange = { selectedTime = it },
        label = "Appointment Time",
        modifier = Modifier()
            .accessibilityLabel("Select appointment time")
            .accessibilityHint("Use arrow keys to adjust hours and minutes")
            .accessibilityRole("spinbutton")
    )
}
```

## Validation Patterns

### Required Time

```kotlin
fun requiredTimeValidator() = Validator { value ->
    if (value.isNullOrBlank()) {
        ValidationResult.invalid("Time is required")
    } else {
        ValidationResult.valid()
    }
}
```

### Business Hours Validation

```kotlin
fun businessHoursValidator() = Validator { value ->
    if (value.isBlank()) return@Validator ValidationResult.valid()

    try {
        val time = LocalTime.parse(value)
        if (time.hour in 9..17) { // 9 AM to 5 PM
            ValidationResult.valid()
        } else {
            ValidationResult.invalid("Time must be during business hours (9 AM - 5 PM)")
        }
    } catch (e: Exception) {
        ValidationResult.invalid("Invalid time format")
    }
}
```

### Time Range Validation

```kotlin
fun timeRangeValidator(minTime: LocalTime, maxTime: LocalTime) = Validator { value ->
    if (value.isBlank()) return@Validator ValidationResult.valid()

    try {
        val time = LocalTime.parse(value)
        if (time in minTime..maxTime) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid("Time must be between ${formatTime12Hour(minTime)} and ${formatTime12Hour(maxTime)}")
        }
    } catch (e: Exception) {
        ValidationResult.invalid("Invalid time format")
    }
}
```

## Platform Differences

### Browser (JS)

- Uses HTML `<input type="time">` for native support
- Browser-specific time formatting
- CSS styling through modifiers
- Full mouse and touch support

### JVM

- Server-side rendering support
- Custom time picker implementation
- Generates appropriate HTML for SSR
- Server-side validation support

## Performance Considerations

### Optimization Tips

1. **Memoization**: Cache time formatting functions
2. **Debouncing**: Debounce rapid time changes
3. **Lazy Rendering**: Only render visible time options
4. **State Optimization**: Minimize re-renders

```kotlin
@Composable
fun OptimizedTimePickerExample() {
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    // Memoize formatters to prevent recreation
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val displayFormatter = remember { DateTimeFormatter.ofPattern("h:mm a") }

    // Debounced time change handler
    var pendingTime by remember { mutableStateOf<LocalTime?>(null) }

    LaunchedEffect(pendingTime) {
        pendingTime?.let { time ->
            delay(300) // Debounce
            selectedTime = time
        }
    }

    TimePicker(
        value = selectedTime,
        onValueChange = { pendingTime = it },
        is24Hour = true,
        modifier = Modifier().width(250.px)
    )
}
```

## Testing Strategies

### Unit Testing

```kotlin
class TimePickerTest {
    @Test
    fun `time picker updates value correctly`() {
        var selectedTime: LocalTime? = null
        val testTime = LocalTime(14, 30)

        composeTestRule.setContent {
            TimePicker(
                value = selectedTime,
                onValueChange = { selectedTime = it }
            )
        }

        // Simulate time selection
        composeTestRule.onNodeWithContentDescription("time picker").performClick()
        // Additional test interactions...

        assertEquals(testTime, selectedTime)
    }

    @Test
    fun `time picker respects 12/24 hour format`() {
        composeTestRule.setContent {
            TimePicker(
                value = LocalTime(14, 30),
                onValueChange = {},
                is24Hour = true
            )
        }

        // Verify 24-hour format display
        composeTestRule.onNodeWithText("14:30").assertExists()
    }
}
```

## Migration Guide

### From HTML Forms

```html
<!-- Before: HTML -->
<input type="time" name="meeting_time" min="09:00" max="17:00">
```

```kotlin
// After: Summon
@Composable
fun MeetingTimeSelection() {
    var meetingTime by remember { mutableStateOf<LocalTime?>(null) }

    TimePicker(
        value = meetingTime,
        onValueChange = { meetingTime = it },
        // Note: Time constraints would be handled through validation
        is24Hour = true
    )
}
```

## Best Practices

### Do

- Use appropriate time formats for your locale
- Provide clear time range constraints
- Validate times on both client and server
- Consider time zones for global applications
- Support both keyboard and mouse interaction

### Don't

- Force specific time formats without good reason
- Forget to handle invalid times gracefully
- Ignore accessibility requirements
- Mix 12/24-hour formats inconsistently

## Related Components

- [DatePicker](DatePicker.md) - For date selection
- [TextField](TextField.md) - For text-based time input
- [Form](Form.md) - For form integration
- [FormField](FormField.md) - For field wrapping