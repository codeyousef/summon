package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Display
import code.yousef.summon.modifier.FlexDirection
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer
import kotlinx.html.FlowContent

/**
 * A layout component that places its children in a horizontal sequence.
 *
 * @param modifier The modifier to be applied to this layout
 * @param content The content to be displayed inside the row
 */
@Composable
fun Row(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    // Use platform renderer directly
    val renderer = LocalPlatformRenderer.current

    // Apply default flex styles for Row using type-safe enums
    val rowModifier = Modifier()
        .style("display", Display.Flex.value)
        .style("flex-direction", FlexDirection.Row.value)
        .then(modifier)

    renderer.renderRow(rowModifier, content)
}

object Alignment {
    enum class Vertical { Top, CenterVertically, Bottom }
    enum class Horizontal { Start, CenterHorizontally, End }
}

object Arrangement {
    enum class Horizontal { Start, End, Center, SpaceBetween, SpaceAround, SpaceEvenly }
    enum class Vertical { Top, Bottom, Center, SpaceBetween, SpaceAround, SpaceEvenly }
} 
