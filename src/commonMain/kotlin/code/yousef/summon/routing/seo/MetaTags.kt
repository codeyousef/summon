package code.yousef.summon.routing.seo

import code.yousef.summon.Composable
import kotlinx.html.HEAD
import kotlinx.html.meta
import kotlinx.html.title

/**
 * MetaTags component for easily configuring page metadata in HTML head
 *
 * @property title The page title
 * @property description Meta description for search engines
 * @property keywords Keywords for search engines (comma separated)
 * @property author Content author
 * @property viewport Viewport settings (defaults to responsive configuration)
 * @property charset Character set (defaults to UTF-8)
 * @property extraTags Map of additional meta tags (name/content pairs)
 */
class MetaTags(
    private val title: String? = null,
    private val description: String? = null,
    private val keywords: String? = null,
    private val author: String? = null,
    private val viewport: String = "width=device-width, initial-scale=1.0",
    private val charset: String = "UTF-8",
    private val extraTags: Map<String, String> = emptyMap()
) : Composable {

    @Suppress("UNCHECKED_CAST")
    override fun <T> compose(receiver: T): T {
        if (receiver is HEAD) {
            receiver.apply {
                // Set page title if provided
                title?.let { title(it) }

                // Add charset tag
                meta(charset = charset)

                // Add viewport
                meta(name = "viewport", content = viewport)

                // Add basic SEO meta tags if provided
                description?.let { meta(name = "description", content = it) }
                keywords?.let { meta(name = "keywords", content = it) }
                author?.let { meta(name = "author", content = it) }

                // Add any additional meta tags
                extraTags.forEach { (name, content) ->
                    meta(name = name, content = content)
                }
            }
        }

        return receiver
    }

    companion object {
        /**
         * Create a MetaTags instance with common metadata
         */
        fun standard(
            title: String,
            description: String,
            keywords: String? = null,
            author: String? = null
        ): MetaTags = MetaTags(
            title = title,
            description = description,
            keywords = keywords,
            author = author
        )
    }
} 