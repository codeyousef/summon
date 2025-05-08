package code.yousef.summon.examples.js.components

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.examples.js.state.Task
import code.yousef.summon.i18n.stringResource
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.StylingModifierExtras.fontStyle
import code.yousef.summon.runtime.Composable

/**
 * Component for displaying a list of tasks.
 *
 * @param tasks The list of tasks to display
 * @param onTaskCompleted Callback for when a task completion status is toggled
 * @param onTaskDeleted Callback for when a task is deleted
 */
@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskCompleted: (String) -> Unit,
    onTaskDeleted: (String) -> Unit,
    modifier: Modifier = Modifier()
) {
    Column(
        modifier = modifier
    ) {
        // Section title
        Text(
            text = stringResource("tasks.yourTasks"),
            modifier = Modifier()
                .fontSize("20px")
                .fontWeight("bold")
                .color("#333333")
                .marginBottom("16px")
        )

        // Empty state
        if (tasks.isEmpty()) {
            Column(
                modifier = Modifier()
                    .padding("20px")
                    .margin("10px 0")
                    .background("#f9f9f9")
                    .borderRadius("8px")
                    .width("100%")
            ) {
                Text(
                    text = stringResource("tasks.empty"),
                    modifier = Modifier()
                        .fontSize("16px")
                        .color("#666666")
                        .textAlign("center")
                        .padding("20px")
                )
            }
        }

        // Task list
        if (tasks.isNotEmpty()) {
            Column(
                modifier = Modifier()
                    .padding("10px")
                    .margin("10px 0")
                    .background("#ffffff")
                    .borderRadius("8px")
                    .border("1px", "solid", "#e0e0e0")
                    .width("100%")
            ) {
                // Render each task
                tasks.forEach { task ->
                    TaskItem(
                        task = task,
                        onTaskCompleted = onTaskCompleted,
                        onTaskDeleted = onTaskDeleted
                    )
                }
            }
        }

        // Task summary
        if (tasks.isNotEmpty()) {
            Text(
                text = stringResource("tasks.summary"),
                modifier = Modifier()
                    .fontSize("14px")
                    .color("#888888")
                    .fontStyle("italic")
                    .marginTop("10px")
                    .textAlign("right")
                    .padding("5px")
            )
        }
    }
}
