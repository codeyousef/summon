package code.yousef.summon.examples.js.components

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.Checkbox
import code.yousef.summon.components.layout.Row
import code.yousef.summon.examples.js.state.Task
import code.yousef.summon.i18n.stringResource
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.mutableStateOf

/**
 * Component for displaying an individual task item.
 *
 * @param task The task to display
 * @param onTaskCompleted Callback for when the task completion status is toggled
 * @param onTaskDeleted Callback for when the task is deleted
 */
@Composable
fun TaskItem(
    task: Task,
    onTaskCompleted: (String) -> Unit,
    onTaskDeleted: (String) -> Unit
) {
    // State for hover effect
    val isHovered = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier()
            .padding("10px")
            .margin("5px 0")
            .background(if (task.completed) "#f0f8ff" else "#ffffff")
            .borderRadius("4px")
            .border("1px", "solid", "#e0e0e0")
            .width("100%")
            .style("justify-content", "space-between")
            .style("align-items", "center")
    ) {
        // Task checkbox and title
        Row(
            modifier = Modifier()
                .style("align-items", "center")
                .style("flex", "1")
        ) {
            // Checkbox for task completion
            Checkbox(
                checked = task.completed,
                onCheckedChange = { onTaskCompleted(task.id) },
                modifier = Modifier()
                    .margin("0 10px 0 0")
            )

            // Task title
            Text(
                text = task.title,
                modifier = Modifier()
                    .fontSize("16px")
                    .color(if (task.completed) "#888888" else "#333333")
                    .textDecoration(if (task.completed) "line-through" else "none")
                    .fontWeight(if (task.completed) "normal" else "500")
            )
        }

        // Delete button
        if (isHovered.value) {
            Button(
                onClick = { onTaskDeleted(task.id) },
                label = stringResource("tasks.delete"),
                modifier = Modifier()
                    .padding("5px 10px")
                    .background("#ff4d4d")
                    .color("#ffffff")
                    .borderRadius("4px")
                    .border("none", "none", "transparent")
                    .cursor("pointer")
                    .fontSize("14px")
            )
        }
    }
}
