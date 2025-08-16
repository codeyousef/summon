package code.yousef.example.portfolio.components

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Local wrapper for Divider component that properly uses LocalPlatformRenderer.current
 * This is a temporary workaround until the main Summon library is updated.
 */
@Composable
fun LocalDivider(
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderDivider(modifier)
}