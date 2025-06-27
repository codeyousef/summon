package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.Display
import code.yousef.summon.modifier.FlexDirection
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * A layout component that places its children in a vertical sequence.
 * 
 * @param modifier The modifier to be applied to this layout
 * @param content The content to be displayed inside the column
 */
@Composable
fun Column(
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    // Use platform renderer directly
    val renderer = LocalPlatformRenderer.current

    // Apply default flex styles for Column using type-safe enums
    val columnModifier = Modifier()
        .style("display", Display.Flex.value)
        .style("flex-direction", FlexDirection.Column.value)
        .then(modifier)

    // Call renderColumn and pass the content lambda
    renderer.renderColumn(
        modifier = columnModifier,
        content = { // Wrap the original content lambda
            content() 
        }
    )
}
