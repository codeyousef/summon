package codes.yousef.summon.components.display

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Renders untrusted Markdown source as plaintext.
 *
 * Parse reviewed formatted content into [SafeDocument] when formatting is required.
 */
@Composable
fun RichMarkdown(
    markdown: String,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderRichMarkdown(markdown, modifier)
}
