package codes.yousef.summon.components.display

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.foundation.TrustedHtml
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/** Renders untrusted text as a text node. Markup in [text] is never interpreted. */
@Composable
fun RichText(
    text: String,
    modifier: Modifier = Modifier()
) {
    SafeDocumentContent(SafeDocument.plaintext(text), modifier)
}

/** Renders a validated, bounded document tree for untrusted formatted content. */
@Composable
fun RichText(
    document: SafeDocument,
    modifier: Modifier = Modifier(),
    cidResolver: CidResolver? = null
) {
    SafeDocumentContent(document, modifier, cidResolver)
}

/** Renders HTML that application code has explicitly marked as trusted. */
@Composable
fun Html(
    htmlContent: TrustedHtml,
    modifier: Modifier = Modifier()
) {
    LocalPlatformRenderer.current.renderHtml(htmlContent, modifier)
}

/**
 * Renders Markdown source as plaintext.
 *
 * Summon intentionally does not convert untrusted Markdown through an HTML string. Applications
 * that need formatting should parse it into [SafeDocument] with a reviewed bounded parser.
 */
@Composable
fun Markdown(
    markdownContent: String,
    modifier: Modifier = Modifier()
) {
    RichText(markdownContent, modifier)
}
