package code.yousef.summon.examples.js.components

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.i18n.stringResource
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.validation.ValidationResult

/**
 * Form component for adding new tasks.
 *
 * @param onTaskAdded Callback for when a new task is added
 */
@Composable
fun TaskForm(
    onTaskAdded: (String) -> Unit,
    modifier: Modifier = Modifier()
) {
    // State for the task input
    val taskTitle = remember { mutableStateOf("") }
    val validationResult = remember { mutableStateOf<ValidationResult?>(null) }

    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource("tasks.addNew"),
            modifier = Modifier()
                .fontSize("18px")
                .fontWeight("bold")
                .color("#333333")
                .marginBottom("16px")
                .padding("5px 0")
        )

        // Task input field with validation
        TextField(
            value = taskTitle.value,
            onValueChange = { 
                taskTitle.value = it
                // Validate on input change
                validationResult.value = validateTaskTitle(it)
            },
            placeholder = stringResource("tasks.titlePlaceholder"),
            modifier = Modifier()
                .width("100%")
                .padding("10px")
                .margin("0 0 10px 0")
                .fontSize("16px")
                .borderRadius("4px")
                .border("1px", "solid", if (validationResult.value?.isValid == false) "#ff4d4d" else "#cccccc")
        )

        // Show validation error if any
        if (validationResult.value?.isValid == false) {
            Text(
                text = validationResult.value?.errorMessage ?: "",
                modifier = Modifier()
                    .color("#ff4d4d")
                    .fontSize("14px")
                    .margin("0 0 10px 0")
                    .padding("0 10px")
            )
        }

        // Add task button
        Row(
            modifier = Modifier()
                .width("100%")
                .style("justify-content", "flex-end")
                .margin("10px 0 0 0")
        ) {
            Button(
                onClick = {
                    val result = validateTaskTitle(taskTitle.value)
                    validationResult.value = result

                    if (result.isValid) {
                        onTaskAdded(taskTitle.value)
                        taskTitle.value = ""
                        validationResult.value = null
                    }
                },
                label = stringResource("tasks.add"),
                modifier = Modifier()
                    .padding("10px 20px")
                    .background("#4285f4")
                    .color("#ffffff")
                    .borderRadius("4px")
                    .border("none", "none", "transparent")
                    .cursor("pointer")
                    .fontSize("16px")
                    .fontWeight("bold")
                    .hover(mapOf(
                        "background-color" to "#3367d6"
                    ))
            )
        }
    }
}

/**
 * Validates the task title.
 *
 * @param title The task title to validate
 * @return A ValidationResult indicating whether the title is valid
 */
private fun validateTaskTitle(title: String): ValidationResult {
    // Check if the title is empty
    if (title.isBlank()) {
        return ValidationResult(false, stringResource("validation.required"))
    }

    // Check if the title is too short
    if (title.length < 3) {
        return ValidationResult(false, stringResource("validation.minLength"))
    }

    // Check if the title is too long
    if (title.length > 50) {
        return ValidationResult(false, stringResource("validation.maxLength"))
    }

    // All validations passed
    return ValidationResult(true)
}
