package code.yousef.summon.components.display

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Renders rich HTML content with optional sanitization for XSS protection.
 *
 * @param htmlContent The HTML content to render
 * @param modifier The modifier to apply to this component
 * @param sanitize Whether to sanitize the HTML content (default: true)
 */
@Composable
fun RichText(
    htmlContent: String,
    modifier: Modifier = Modifier(),
    sanitize: Boolean = true
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtml(htmlContent, modifier, sanitize)
}

/**
 * Renders trusted HTML content with minimal sanitization.
 * Use this for content you control and trust.
 *
 * @param htmlContent The trusted HTML content to render
 * @param modifier The modifier to apply to this component
 */
@Composable
fun Html(
    htmlContent: String,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtml(htmlContent, modifier, sanitize = false)
}

/**
 * Renders Markdown content by converting it to safe HTML.
 *
 * @param markdownContent The Markdown content to render
 * @param modifier The modifier to apply to this component
 */
@Composable
fun Markdown(
    markdownContent: String,
    modifier: Modifier = Modifier()
) {
    val htmlContent = convertMarkdownToHtml(markdownContent)
    RichText(htmlContent, modifier, sanitize = true)
}

/**
 * Simple markdown to HTML converter.
 * This is a basic implementation that handles common markdown features.
 */
private fun convertMarkdownToHtml(markdown: String): String {
    var html = markdown

    // Headers
    html = html.replace(Regex("^# (.+)$", RegexOption.MULTILINE), "<h1>$1</h1>")
    html = html.replace(Regex("^## (.+)$", RegexOption.MULTILINE), "<h2>$1</h2>")
    html = html.replace(Regex("^### (.+)$", RegexOption.MULTILINE), "<h3>$1</h3>")
    html = html.replace(Regex("^#### (.+)$", RegexOption.MULTILINE), "<h4>$1</h4>")
    html = html.replace(Regex("^##### (.+)$", RegexOption.MULTILINE), "<h5>$1</h5>")
    html = html.replace(Regex("^###### (.+)$", RegexOption.MULTILINE), "<h6>$1</h6>")

    // Bold and italic
    html = html.replace(Regex("\\*\\*(.+?)\\*\\*"), "<strong>$1</strong>")
    html = html.replace(Regex("\\*(.+?)\\*"), "<em>$1</em>")

    // Links
    html = html.replace(Regex("\\[(.+?)\\]\\((.+?)\\)"), "<a href=\"$2\">$1</a>")

    // Lists
    html = html.replace(Regex("^- (.+)$", RegexOption.MULTILINE), "<li>$1</li>")
    // Match list items including newlines - use [\s\S] as a workaround for DOT_MATCHES_ALL
    html = html.replace(Regex("(<li>[\\s\\S]*?</li>)")) { matchResult ->
        "<ul>${matchResult.value}</ul>"
    }

    // Paragraphs
    val lines = html.split("\n")
    val paragraphs = mutableListOf<String>()
    var currentParagraph = ""

    for (line in lines) {
        val trimmedLine = line.trim()
        if (trimmedLine.isEmpty()) {
            if (currentParagraph.isNotEmpty()) {
                paragraphs.add("<p>$currentParagraph</p>")
                currentParagraph = ""
            }
        } else if (!trimmedLine.startsWith("<")) {
            if (currentParagraph.isNotEmpty()) {
                currentParagraph += " "
            }
            currentParagraph += trimmedLine
        } else {
            if (currentParagraph.isNotEmpty()) {
                paragraphs.add("<p>$currentParagraph</p>")
                currentParagraph = ""
            }
            paragraphs.add(trimmedLine)
        }
    }

    if (currentParagraph.isNotEmpty()) {
        paragraphs.add("<p>$currentParagraph</p>")
    }

    return paragraphs.joinToString("\n")
}