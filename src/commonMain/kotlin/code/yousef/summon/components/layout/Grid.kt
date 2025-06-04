package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.getPlatformRenderer
import kotlinx.html.FlowContent

/**
 * A layout composable that arranges its children in a grid using CSS Grid.
 * Grid provides a powerful way to create responsive two-dimensional layouts.
 *
 * @param columns The number of columns or a template string (e.g. "1fr 2fr 1fr" or "repeat(3, 1fr)")
 * @param rows The number of rows or a template string (e.g. "auto 1fr auto" or "repeat(2, minmax(100px, auto))")
 * @param gap The gap between grid items (e.g. "10px" or "10px 20px" for row/column gaps)
 * @param areas Optional grid template areas definition
 * @param modifier The modifier to apply to this composable
 * @param content The composable content to display inside the grid
 */
@Composable
fun Grid(
    columns: String,
    rows: String = "auto",
    gap: String = "0",
    areas: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    // Prepare the modifier with grid styles
    val gridStyles = mutableMapOf(
        "display" to "grid",
        "grid-template-columns" to columns,
        "grid-template-rows" to rows,
        "gap" to gap
    )
    areas?.let { gridStyles["grid-template-areas"] = it }

    // Create a new modifier that preserves all existing styles and attributes
    val finalModifier = Modifier(modifier.styles + gridStyles, modifier.attributes)

    getPlatformRenderer().renderGrid(
        modifier = finalModifier,
        content = content
    )
}
