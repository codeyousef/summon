package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.PlatformRenderer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.MigratedPlatformRenderer
import code.yousef.summon.runtime.getPlatformRenderer

/**
 * A layout composable that places its children in a vertical sequence.
 * @param modifier The modifier to apply to this composable
 * @param content The composables to display inside the column
 */
@Composable
fun Column(
    modifier: Modifier = Modifier.create(),
    content: @Composable () -> Unit
) {
    // Use CompositionLocal for renderer access
    val renderer = LocalPlatformRenderer.current
    renderer.renderColumn(modifier, content)
} 