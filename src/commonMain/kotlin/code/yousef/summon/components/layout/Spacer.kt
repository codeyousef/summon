package code.yousef.summon.components.layout

import code.yousef.summon.core.UIElement
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import code.yousef.summon.annotation.Composable

/**
 * A composable that adds space between components.
 * Can be used to create fixed gaps in layouts like Row or Column.
 * @param modifier The modifier to apply to this composable. Often used to set size.
 * @param size The size of the spacer (e.g., "16px"). Note: Size can also be controlled via modifier.
 * @param isVertical Whether the spacer primarily affects vertical (true) or horizontal (false) space.
 */
@Composable
fun Spacer(
    modifier: Modifier = Modifier(),
    size: String? = null,
    isVertical: Boolean = true
) {
    val spacerData = SpacerData(
        modifier = modifier,
        size = size,
        isVertical = isVertical
    )
    
    println("Composable Spacer function called.")
}

/**
 * Internal data holder for Spacer properties.
 */
internal data class SpacerData(
    val modifier: Modifier = Modifier(),
    val size: String?,
    val isVertical: Boolean
) 