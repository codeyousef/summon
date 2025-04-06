package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.UIElement
import code.yousef.summon.modifier.Modifier

/**
 * A layout composable that arranges its children in a grid using CSS Grid.
 * Grid provides a powerful way to create responsive two-dimensional layouts.
 *
 * @param columns The number of columns or a template string (e.g. "1fr 2fr 1fr" or "repeat(3, 1fr)")
 * @param rows The number of rows or a template string (e.g. "auto 1fr auto" or "repeat(2, minmax(100px, auto))")
 * @param gap The gap between grid items (e.g. "10px" or "10px 20px" for row/column gaps)
 * @param areas Optional grid template areas definition
 * @param modifier The modifier to apply to this composable
 * @param content The composables to display inside the grid
 */
@Composable
fun Grid(
    columns: String,
    rows: String = "auto",
    gap: String = "0",
    areas: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    val gridData = GridData(
        columns = columns,
        rows = rows,
        gap = gap,
        areas = areas,
        modifier = modifier
    )
    // Assuming UIElement handles node creation, applying modifiers, and content execution
    UIElement(
        factory = { gridData },
        update = { /* Update logic if GridData properties can change */ },
        content = content
    )
}

/**
 * Internal data class holding non-content parameters for Grid.
 */
internal data class GridData(
    val columns: String,
    val rows: String,
    val gap: String,
    val areas: String?,
    val modifier: Modifier
)