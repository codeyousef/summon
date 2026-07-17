package codes.yousef.summon.seo

/**
 * Scope for defining HTML head elements in a type-safe, composable way.
 * Provides methods for creating meta tags, links, scripts, and other head elements.
 */
interface HeadScope {
    /**
     * Renders a title element in the document head.
     * @param text The title text
     */
    fun title(text: String)

    /**
     * Renders a meta tag in the document head.
     * @param name The name attribute for the meta tag
     * @param property The property attribute for OpenGraph tags
     * @param content The content attribute value
     * @param charset The charset attribute for character encoding
     * @param httpEquiv The http-equiv attribute for HTTP headers
     */
    fun meta(
        name: String? = null,
        property: String? = null,
        content: String? = null,
        charset: String? = null,
        httpEquiv: String? = null
    )

    /**
     * Renders a link element in the document head.
     * @param rel The relationship type
     * @param href The URL of the linked resource
     * @param type The MIME type of the linked resource
     * @param sizes Icon sizes (for rel="icon")
     * @param crossorigin CORS settings
     * @param media Media query for conditional loading
     */
    fun link(
        rel: String,
        href: String,
        type: String? = null,
        sizes: String? = null,
        crossorigin: String? = null,
        media: String? = null
    )

    /**
     * Renders a script element in the document head.
     * @param src External script URL
     * @param content Inline script content
     * @param type Script type (default: "text/javascript")
     * @param async Load script asynchronously
     * @param defer Defer script execution
     * @param crossorigin CORS settings
     */
    fun script(
        src: String? = null,
        content: String? = null,
        type: String = "text/javascript",
        async: Boolean = false,
        defer: Boolean = false,
        crossorigin: String? = null
    )

    /**
     * Renders a style element in the document head.
     * @param content CSS content
     * @param media Media query for conditional styles
     */
    fun style(
        content: String,
        media: String? = null
    )

    /**
     * Renders a base element in the document head.
     * @param href Base URL for relative URLs
     * @param target Default target for links
     */
    fun base(
        href: String? = null,
        target: String? = null
    )
}

/**
 * Default implementation of HeadScope that generates HTML strings.
 * Platform-specific renderers can override this for optimized rendering.
 */
class DefaultHeadScope(private val onElement: (String) -> Unit) : HeadScope {
    override fun title(text: String) {
        onElement("<title>${escapeText(text)}</title>")
    }

    override fun meta(
        name: String?,
        property: String?,
        content: String?,
        charset: String?,
        httpEquiv: String?
    ) {
        val attributes = buildString {
            charset?.let { appendAttribute("charset", it) }
            name?.let { appendAttribute("name", it) }
            property?.let { appendAttribute("property", it) }
            content?.let { appendAttribute("content", it) }
            httpEquiv?.let { appendAttribute("http-equiv", it) }
        }
        if (attributes.isNotEmpty()) {
            onElement("<meta$attributes>")
        }
    }

    override fun link(
        rel: String,
        href: String,
        type: String?,
        sizes: String?,
        crossorigin: String?,
        media: String?
    ) {
        val attributes = buildString {
            appendAttribute("rel", rel)
            appendAttribute("href", href)
            type?.let { appendAttribute("type", it) }
            sizes?.let { appendAttribute("sizes", it) }
            crossorigin?.let { appendAttribute("crossorigin", it) }
            media?.let { appendAttribute("media", it) }
        }
        onElement("<link$attributes>")
    }

    override fun script(
        src: String?,
        content: String?,
        type: String,
        async: Boolean,
        defer: Boolean,
        crossorigin: String?
    ) {
        val attributes = buildString {
            if (type != "text/javascript") {
                appendAttribute("type", type)
            }
            src?.let { appendAttribute("src", it) }
            if (async) append(" async")
            if (defer) append(" defer")
            crossorigin?.let { appendAttribute("crossorigin", it) }
        }

        if (content != null) {
            onElement("<script$attributes>${escapeRawTextEndTag(content, "script")}</script>")
        } else {
            onElement("<script$attributes></script>")
        }
    }

    override fun style(content: String, media: String?) {
        val attributes = buildString {
            media?.let { appendAttribute("media", it) }
        }
        onElement("<style$attributes>${escapeRawTextEndTag(content, "style")}</style>")
    }

    override fun base(href: String?, target: String?) {
        val attributes = buildString {
            href?.let { appendAttribute("href", it) }
            target?.let { appendAttribute("target", it) }
        }
        if (attributes.isNotEmpty()) {
            onElement("<base$attributes>")
        }
    }

    private fun StringBuilder.appendAttribute(name: String, value: String) {
        append(' ')
        append(name)
        append("=\"")
        append(escapeAttribute(value))
        append('"')
    }

    private fun escapeText(value: String): String = buildString(value.length) {
        value.forEach { character ->
            append(
                when (character) {
                    '&' -> "&amp;"
                    '<' -> "&lt;"
                    '>' -> "&gt;"
                    else -> character
                }
            )
        }
    }

    private fun escapeAttribute(value: String): String = buildString(value.length) {
        value.forEach { character ->
            append(
                when (character) {
                    '&' -> "&amp;"
                    '<' -> "&lt;"
                    '>' -> "&gt;"
                    '"' -> "&quot;"
                    '\'' -> "&#39;"
                    else -> character
                }
            )
        }
    }

    private fun escapeRawTextEndTag(value: String, tagName: String): String =
        value.replace("</$tagName", "<\\/$tagName", ignoreCase = true)
}
