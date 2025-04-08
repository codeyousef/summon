package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.PlatformRenderer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.MigratedPlatformRenderer
import code.yousef.summon.runtime.getPlatformRenderer

/**
 * A layout composable that places its children in a horizontal sequence.
 * 
 * @param modifier Modifier to be applied to the Row layout
 * @param horizontalArrangement Arrangement of children along the main axis (horizontal)
 * @param verticalAlignment Alignment of children along the cross axis (vertical)
 * @param content The content lambda defining the children
 */
@Composable
fun Row(
    modifier: Modifier = Modifier.create(),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Horizontal.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Vertical.Top,
    content: @Composable () -> Unit
) {
    val renderer = getPlatformRenderer() as MigratedPlatformRenderer
    renderer.renderRow(modifier, content)
}

object Alignment {
    enum class Vertical { Top, CenterVertically, Bottom }
    enum class Horizontal { Start, CenterHorizontally, End }
}

object Arrangement {
    enum class Horizontal { Start, End, Center, SpaceBetween, SpaceAround, SpaceEvenly }
    enum class Vertical { Top, Bottom, Center, SpaceBetween, SpaceAround, SpaceEvenly }
} 
