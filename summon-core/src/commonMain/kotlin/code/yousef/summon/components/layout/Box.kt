package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import kotlinx.html.FlowContent

/**
 * A basic layout composable that arranges its children in a block.
 * Corresponds typically to a `<div>` element in HTML.
 *
 * @param modifier The modifier to apply to this layout.
 * @param content The children composables to place inside the box.
 */
@Composable
fun Box(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit) {
    val renderer = LocalPlatformRenderer.current
    // Call the renderBox method
    renderer.renderBox(modifier, content)
}
