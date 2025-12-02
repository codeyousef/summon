package codes.yousef.summon.components.display

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Renders Markdown content using a rich text editor/viewer.
 *
 * @param markdown The Markdown content to render
 * @param modifier The modifier to apply to this component
 */
@Composable
fun RichMarkdown(
    markdown: String,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderRichMarkdown(markdown, modifier)
}
