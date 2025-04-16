package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
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
    
    // Call renderColumn and pass the content lambda
    renderer.renderColumn(
        modifier = modifier,
        content = { // Wrap the original content lambda
            content() 
        }
    )
} 