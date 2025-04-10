package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.getPlatformRenderer

/**
 * A Box component that renders a container element.
 * The Box component is a fundamental layout component that can contain other components.
 */
@Composable
fun Box(
    modifier: Modifier = Modifier.create(),
    content: @Composable () -> Unit
) {
    // Use CompositionLocal for renderer access
    val renderer = LocalPlatformRenderer.current
    renderer.renderBox(modifier)
    content()
}
