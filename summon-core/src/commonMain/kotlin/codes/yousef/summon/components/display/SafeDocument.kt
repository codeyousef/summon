package codes.yousef.summon.components.display

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.layout.Div
import codes.yousef.summon.components.layout.Span
import codes.yousef.summon.components.navigation.Link
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.modifier.style
import codes.yousef.summon.runtime.DisposableEffect
import codes.yousef.summon.runtime.remember

/**
 * Bounded, platform-independent document tree for untrusted formatted content.
 *
 * The tree cannot represent markup, script, forms, frames, event handlers, stylesheets, remote
 * images, or arbitrary URLs. Original source bytes do not belong in this model.
 *
 * @property nodes validated immutable document nodes
 * @property plaintextFallback safe text rendered when the node list is empty
 */
class SafeDocument private constructor(
    val nodes: List<SafeDocumentNode>,
    val plaintextFallback: String
) {
    /** Validated document factories and the policy identifier emitted into rendered output. */
    companion object {
        /** Current safe-document validation policy. */
        const val POLICY_VERSION: String = "summon-safe-document-1"
        private const val MAX_NODES = 10_000
        private const val MAX_DEPTH = 32
        private const val MAX_TEXT_CHARS = 1_048_576

        /** Validates and freezes [nodes] and [plaintextFallback]. */
        fun create(nodes: List<SafeDocumentNode>, plaintextFallback: String): SafeDocument {
            require(plaintextFallback.length <= MAX_TEXT_CHARS) { "Plaintext fallback is too large" }
            var nodeCount = 0
            var textChars = plaintextFallback.length

            fun freeze(items: List<SafeDocumentNode>, depth: Int): List<SafeDocumentNode> {
                require(depth <= MAX_DEPTH) { "Safe document nesting is too deep" }
                return items.map { node ->
                    nodeCount++
                    require(nodeCount <= MAX_NODES) { "Safe document has too many nodes" }
                    val frozen = when (node) {
                        is SafeDocumentNode.Text -> {
                            textChars += node.value.length
                            node
                        }
                        is SafeDocumentNode.Container ->
                            node.copy(children = freeze(node.children, depth + 1))
                        is SafeDocumentNode.Link ->
                            node.copy(label = freeze(node.label, depth + 1))
                        is SafeDocumentNode.CidImage -> {
                            textChars += node.alt.length
                            node
                        }
                        SafeDocumentNode.LineBreak -> node
                    }
                    require(textChars <= MAX_TEXT_CHARS) { "Safe document text is too large" }
                    frozen
                }
            }

            return SafeDocument(freeze(nodes, 1), plaintextFallback)
        }

        /** Creates a text-only document. */
        fun plaintext(text: String): SafeDocument = create(
            nodes = listOf(SafeDocumentNode.Text(text)),
            plaintextFallback = text
        )
    }
}

/** Element kinds permitted by [SafeDocumentNode.Container]. */
enum class SafeContainerKind {
    /** The paragraph safe container kind option. */
    PARAGRAPH,
    /** The heading safe container kind option. */
    HEADING,
    /** The strong safe container kind option. */
    STRONG,
    /** The emphasis safe container kind option. */
    EMPHASIS,
    /** The code safe container kind option. */
    CODE,
    /** The quote safe container kind option. */
    QUOTE,
    /** The unordered list safe container kind option. */
    UNORDERED_LIST,
    /** The ordered list safe container kind option. */
    ORDERED_LIST,
    /** The list item safe container kind option. */
    LIST_ITEM
}

/** Closed node model for safe formatted content. */
sealed interface SafeDocumentNode {
    /**
     * Plain text.
     *
     * @property value visible text
     */
    data class Text(val value: String) : SafeDocumentNode

    /**
     * Semantic container.
     *
     * @property kind allowed semantic kind
     * @property children ordered safe child nodes
     * @property headingLevel heading rank, valid only for heading containers
     */
    data class Container(
        val kind: SafeContainerKind,
        val children: List<SafeDocumentNode>,
        val headingLevel: Int = 1
    ) : SafeDocumentNode {
        init {
            require(kind == SafeContainerKind.HEADING || headingLevel == 1) {
                "Heading level is only valid for heading nodes"
            }
            require(headingLevel in 1..6) { "Heading level must be between 1 and 6" }
        }
    }

    /**
     * Approved outbound link.
     *
     * @property target validated destination
     * @property label safe document label
     */
    data class Link(
        val target: SafeOutboundLink,
        val label: List<SafeDocumentNode>
    ) : SafeDocumentNode

    /**
     * Local content-ID image.
     *
     * @property reference validated content-ID
     * @property alt alternative text
     */
    data class CidImage(
        val reference: CidReference,
        val alt: String
    ) : SafeDocumentNode

    /** Semantic line break. */
    data object LineBreak : SafeDocumentNode
}

/**
 * An HTTPS URL whose decoded normalized host was explicitly approved.
 *
 * @property href normalized HTTPS URL
 * @property displayHost normalized ASCII host suitable for display
 */
class SafeOutboundLink private constructor(
    val href: String,
    val displayHost: String
) {
    /** Strict outbound-link parser. */
    companion object {
        /** Parses [raw] only when its normalized host exactly matches [allowedHosts]. */
        fun parse(raw: String, allowedHosts: Set<String>): SafeOutboundLink? {
            if (raw != raw.trim() || raw.any { it <= ' ' || it == '\\' }) return null
            val schemeEnd = raw.indexOf("://")
            if (schemeEnd <= 0 || !raw.substring(0, schemeEnd).equals("https", ignoreCase = true)) return null
            val authorityStart = schemeEnd + 3
            val authorityEnd = raw.indexOfFirstFrom(authorityStart) { it == '/' || it == '?' || it == '#' }
                .let { if (it < 0) raw.length else it }
            val authority = percentDecodeAscii(raw.substring(authorityStart, authorityEnd)) ?: return null
            if (authority.isEmpty() || '@' in authority || ':' in authority) return null
            val host = authority.lowercase()
            if (!isValidHost(host)) return null
            val approved = allowedHosts.any { approvedHost -> host == approvedHost.lowercase() }
            if (!approved) return null
            val suffix = raw.substring(authorityEnd)
            val decodedSuffix = percentDecodeAscii(suffix) ?: return null
            if (decodedSuffix.any { it <= '\u001f' || it == '\u007f' || it == '\\' }) return null
            return SafeOutboundLink("https://$host$suffix", host)
        }

        private fun isValidHost(host: String): Boolean {
            if (host.length !in 1..253 || host.startsWith('.') || host.endsWith('.') || ".." in host) return false
            return host.split('.').all { label ->
                label.length in 1..63 &&
                    label.first().isLetterOrDigit() &&
                    label.last().isLetterOrDigit() &&
                    label.all { it.isLetterOrDigit() || it == '-' } &&
                    label.all { it.code < 128 }
            }
        }
    }
}

/**
 * Validated content-ID reference.
 *
 * @property contentId normalized identifier without the `cid:` prefix
 */
class CidReference private constructor(val contentId: String) {
    /** Strict content-ID parser. */
    companion object {
        /** Parses a bounded ASCII content-ID, with optional `cid:` and angle brackets. */
        fun parse(raw: String): CidReference? {
            val value = raw.removePrefix("cid:").removeSurrounding("<", ">")
            if (value.length !in 1..255) return null
            if (!value.all { it.isLetterOrDigit() || it in "._@+-" }) return null
            return CidReference(value)
        }
    }
}

/**
 * Locally created object URL with explicit release ownership.
 *
 * @property value validated `blob:` URL

 * @property releaseAction The release action value.
 */
class LocalObjectUrl private constructor(
    val value: String,
    private val releaseAction: () -> Unit
) {
    /** Releases the underlying object URL. */
    fun release() = releaseAction()

    /** Validated object-URL factory. */
    companion object {
        /** Creates an owner for [value], or returns `null` when it is not a safe `blob:` URL. */
        fun create(value: String, release: () -> Unit): LocalObjectUrl? =
            if (value.startsWith("blob:") && value.none { it <= '\u001f' || it == '\u007f' }) {
                LocalObjectUrl(value, release)
            } else {
                null
            }
    }
}

/** Resolves a content-ID to an explicitly releasable local object URL. */
fun interface CidResolver {
    /** Resolves [reference], returning `null` when no local object exists. */
    fun resolve(reference: CidReference): LocalObjectUrl?
}

/**
 * Renders a validated [document], resolving only local CID images through [cidResolver].
 *
 * Every resolved object URL is released when its image leaves composition.
 */
@Composable
fun SafeDocumentContent(
    document: SafeDocument,
    modifier: Modifier = Modifier(),
    cidResolver: CidResolver? = null
) {
    Div(modifier.attribute("data-safe-document-policy", SafeDocument.POLICY_VERSION).attribute("dir", "auto")) {
        val content = document.nodes.ifEmpty { listOf(SafeDocumentNode.Text(document.plaintextFallback)) }
        renderSafeNodes(content, cidResolver)
    }
}

@Composable
private fun renderSafeNodes(nodes: List<SafeDocumentNode>, cidResolver: CidResolver?) {
    for (node in nodes) {
        when (node) {
            is SafeDocumentNode.Text -> Text(node.value)
            is SafeDocumentNode.Container -> renderContainer(node, cidResolver)
            is SafeDocumentNode.Link -> Link(
                href = node.target.href,
                target = "_blank",
                rel = "noopener noreferrer",
                isExternal = true
            ) {
                renderSafeNodes(node.label, cidResolver)
                Text(" (${node.target.displayHost})")
            }
            is SafeDocumentNode.CidImage -> renderCidImage(node, cidResolver)
            SafeDocumentNode.LineBreak -> Div {}
        }
    }
}

@Composable
private fun renderContainer(node: SafeDocumentNode.Container, cidResolver: CidResolver?) {
    val modifier = when (node.kind) {
        SafeContainerKind.PARAGRAPH -> Modifier().style("margin", "1em 0")
        SafeContainerKind.HEADING -> Modifier()
            .attribute("role", "heading")
            .attribute("aria-level", node.headingLevel.toString())
            .style("font-weight", "bold")
        SafeContainerKind.STRONG -> Modifier().style("font-weight", "bold")
        SafeContainerKind.EMPHASIS -> Modifier().style("font-style", "italic")
        SafeContainerKind.CODE -> Modifier().style("font-family", "monospace")
        SafeContainerKind.QUOTE -> Modifier().style("margin-inline-start", "1em")
        SafeContainerKind.UNORDERED_LIST -> Modifier().attribute("role", "list")
        SafeContainerKind.ORDERED_LIST -> Modifier().attribute("role", "list")
        SafeContainerKind.LIST_ITEM -> Modifier().attribute("role", "listitem")
    }
    Span(modifier) { renderSafeNodes(node.children, cidResolver) }
}

@Composable
private fun renderCidImage(node: SafeDocumentNode.CidImage, cidResolver: CidResolver?) {
    val resource = remember(node.reference.contentId, cidResolver) { cidResolver?.resolve(node.reference) }
    if (resource == null) {
        Text(node.alt)
        return
    }
    DisposableEffect(resource) { resource::release }
    Image(src = resource.value, alt = node.alt)
}

private inline fun String.indexOfFirstFrom(startIndex: Int, predicate: (Char) -> Boolean): Int {
    for (index in startIndex until length) if (predicate(this[index])) return index
    return -1
}

private fun percentDecodeAscii(value: String): String? {
    val output = StringBuilder(value.length)
    var index = 0
    while (index < value.length) {
        val char = value[index]
        if (char != '%') {
            if (char.code >= 128) return null
            output.append(char)
            index++
            continue
        }
        if (index + 2 >= value.length) return null
        val high = value[index + 1].digitToIntOrNull(16) ?: return null
        val low = value[index + 2].digitToIntOrNull(16) ?: return null
        val decoded = (high shl 4) or low
        if (decoded >= 128) return null
        output.append(decoded.toChar())
        index += 3
    }
    return output.toString()
}
