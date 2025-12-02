package codes.yousef.summon.components.layout

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Renders a split pane layout with resizable divider.
 *
 * @param orientation The orientation of the split ("horizontal" or "vertical")
 * @param modifier The modifier to apply to this component
 * @param first The content of the first pane
 * @param second The content of the second pane
 */
@Composable
fun SplitPane(
    orientation: String = "horizontal",
    modifier: Modifier = Modifier(),
    first: @Composable () -> Unit,
    second: @Composable () -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderSplitPane(orientation, modifier, first, second)
}
