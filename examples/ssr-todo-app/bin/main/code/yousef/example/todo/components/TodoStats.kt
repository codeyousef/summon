package code.yousef.example.todo.components

import codes.yousef.example.todo.design.*
import codes.yousef.example.todo.design.ModifierExtensions.typography
import codes.yousef.example.todo.models.TodoStats
import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.ModifierExtras.withAttribute

/**
 * Component for displaying todo statistics
 */
@Composable
fun TodoStatsDisplay(
    stats: TodoStats,
    modifier: Modifier = Modifier()
) {
    Row(
        modifier = modifier
            .padding(Spacing.MD.value)
            .backgroundColor(SemanticColor.PRIMARY.lightValue + "10")
            .borderRadius(BorderRadius.MD.value)
            .style("gap", Spacing.SM.value)
    ) {
        Text(
            text = "Progress: ${stats.completed} / ${stats.total}",
            modifier = Modifier()
                .typography(TextSize.BASE, FontWeight.MEDIUM)
                .color(SemanticColor.PRIMARY.lightValue)
        )

        if (stats.total > 0) {
            val percentage = (stats.completed * 100) / stats.total
            Text(
                text = "($percentage%)",
                modifier = Modifier()
                    .typography(TextSize.SM, FontWeight.NORMAL)
                    .color(SemanticColor.PRIMARY.lightValue + "CC")
            )
        }
    }
}

/**
 * Detailed stats component with progress information
 */
@Composable
fun DetailedTodoStats(
    stats: TodoStats,
    modifier: Modifier = Modifier()
) {
    Column(
        modifier = modifier
            .padding(Spacing.MD.value)
            .backgroundColor(SemanticColor.SURFACE.lightValue)
            .borderRadius(BorderRadius.MD.value)
            .style("gap", Spacing.SM.value)
    ) {
        Text(
            text = "Todo Summary",
            modifier = Modifier()
                .typography(TextSize.LG, FontWeight.SEMIBOLD)
                .color(SemanticColor.TEXT_PRIMARY.lightValue)
        )

        Row(
            modifier = Modifier()
                .style("gap", Spacing.MD.value)
        ) {
            Text(
                text = "Total: ${stats.total}",
                modifier = Modifier()
                    .typography(TextSize.BASE, FontWeight.NORMAL)
            )

            Text(
                text = "Completed: ${stats.completed}",
                modifier = Modifier()
                    .typography(TextSize.BASE, FontWeight.NORMAL)
                    .color(SemanticColor.SUCCESS.lightValue)
            )

            Text(
                text = "Remaining: ${stats.remaining}",
                modifier = Modifier()
                    .typography(TextSize.BASE, FontWeight.NORMAL)
                    .color(SemanticColor.WARNING.lightValue)
            )
        }

        if (stats.total > 0) {
            val percentage = (stats.completed * 100) / stats.total
            Text(
                text = "Progress: $percentage%",
                modifier = Modifier()
                    .typography(TextSize.BASE, FontWeight.MEDIUM)
            )

            // Simple text-based progress bar
            val progressChars = 20
            val filledChars = (percentage * progressChars) / 100
            val progressBar = "█".repeat(filledChars) + "░".repeat(progressChars - filledChars)

            Text(
                text = "[$progressBar]",
                modifier = Modifier()
                    .withAttribute("style", "font-family: monospace")
                    .typography(TextSize.SM, FontWeight.NORMAL)
            )
        }
    }
}

/**
 * Compact stats component for inline display
 */
@Composable
fun CompactTodoStats(
    stats: TodoStats,
    modifier: Modifier = Modifier()
) {
    Text(
        text = when {
            stats.total == 0 -> "No todos"
            stats.completed == stats.total -> "All done! ✅"
            stats.completed == 0 -> "${stats.total} todos to go"
            else -> "${stats.completed}/${stats.total} done"
        },
        modifier = modifier
            .padding(Spacing.SM.value)
            .backgroundColor(
                when {
                    stats.total == 0 -> SemanticColor.SURFACE.lightValue
                    stats.completed == stats.total -> SemanticColor.SUCCESS.lightValue + "20"
                    else -> SemanticColor.PRIMARY.lightValue + "20"
                }
            )
            .borderRadius(BorderRadius.SM.value)
            .typography(TextSize.SM, FontWeight.MEDIUM)
            .color(
                when {
                    stats.total == 0 -> SemanticColor.TEXT_SECONDARY.lightValue
                    stats.completed == stats.total -> SemanticColor.SUCCESS.lightValue
                    else -> SemanticColor.PRIMARY.lightValue
                }
            )
    )
}