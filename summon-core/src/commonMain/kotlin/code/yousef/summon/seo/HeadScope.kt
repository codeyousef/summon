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
        onElement("<title>$text</title>")
    }

    override fun meta(
        name: String?,
        property: String?,
        content: String?,
        charset: String?,
        httpEquiv: String?
    ) {
        val attributes = buildString {
            charset?.let { append(" charset=\"$it\"") }
            name?.let { append(" name=\"$it\"") }
            property?.let { append(" property=\"$it\"") }
            content?.let { append(" content=\"$it\"") }
            httpEquiv?.let { append(" http-equiv=\"$it\"") }
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
            append(" rel=\"$rel\"")
            append(" href=\"$href\"")
            type?.let { append(" type=\"$it\"") }
            sizes?.let { append(" sizes=\"$it\"") }
            crossorigin?.let { append(" crossorigin=\"$it\"") }
            media?.let { append(" media=\"$it\"") }
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
                append(" type=\"$type\"")
            }
            src?.let { append(" src=\"$it\"") }
            if (async) append(" async")
            if (defer) append(" defer")
            crossorigin?.let { append(" crossorigin=\"$it\"") }
        }

        if (content != null) {
            onElement("<script$attributes>$content</script>")
        } else {
            onElement("<script$attributes></script>")
        }
    }

    override fun style(content: String, media: String?) {
        val attributes = buildString {
            media?.let { append(" media=\"$it\"") }
        }
        onElement("<style$attributes>$content</style>")
    }

    override fun base(href: String?, target: String?) {
        val attributes = buildString {
            href?.let { append(" href=\"$it\"") }
            target?.let { append(" target=\"$it\"") }
        }
        if (attributes.isNotEmpty()) {
            onElement("<base$attributes>")
        }
    }
}