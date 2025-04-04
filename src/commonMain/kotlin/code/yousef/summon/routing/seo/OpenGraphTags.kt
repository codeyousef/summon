package code.yousef.summon.routing.seo

import code.yousef.summon.Composable
import kotlinx.html.HEAD
import kotlinx.html.meta

/**
 * OpenGraphTags component for adding social media metadata
 * These tags help social media platforms display rich previews when users share your content
 */
class OpenGraphTags(
    private val title: String,
    private val type: String = "website",
    private val url: String,
    private val image: String? = null,
    private val description: String? = null,
    private val siteName: String? = null,
    private val locale: String? = null,
    private val extraTags: Map<String, String> = emptyMap()
) : Composable {

    @Suppress("UNCHECKED_CAST")
    override fun <T> compose(receiver: T): T {
        if (receiver is HEAD) {
            receiver.apply {
                // Basic OG tags
                meta {
                    attributes["property"] = "og:title"
                    attributes["content"] = title
                }
                meta {
                    attributes["property"] = "og:type"
                    attributes["content"] = type
                }
                meta {
                    attributes["property"] = "og:url"
                    attributes["content"] = url
                }

                // Optional OG tags
                image?.let {
                    meta {
                        attributes["property"] = "og:image"
                        attributes["content"] = it
                    }
                }
                description?.let {
                    meta {
                        attributes["property"] = "og:description"
                        attributes["content"] = it
                    }
                }
                siteName?.let {
                    meta {
                        attributes["property"] = "og:site_name"
                        attributes["content"] = it
                    }
                }
                locale?.let {
                    meta {
                        attributes["property"] = "og:locale"
                        attributes["content"] = it
                    }
                }

                // Add any additional OG tags
                extraTags.forEach { (name, content) ->
                    meta {
                        attributes["property"] = "og:$name"
                        attributes["content"] = content
                    }
                }
            }
        }

        return receiver
    }

    companion object {
        /**
         * Create OpenGraph tags for an article
         */
        fun article(
            title: String,
            url: String,
            image: String,
            description: String,
            publishedTime: String? = null,
            author: String? = null,
            siteName: String? = null
        ): OpenGraphTags {
            val extraTags = mutableMapOf<String, String>()
            publishedTime?.let { extraTags["article:published_time"] = it }
            author?.let { extraTags["article:author"] = it }

            return OpenGraphTags(
                title = title,
                type = "article",
                url = url,
                image = image,
                description = description,
                siteName = siteName,
                extraTags = extraTags
            )
        }

        /**
         * Create OpenGraph tags for a product
         */
        fun product(
            title: String,
            url: String,
            image: String,
            description: String,
            price: String? = null,
            currency: String? = null,
            siteName: String? = null
        ): OpenGraphTags {
            val extraTags = mutableMapOf<String, String>()
            price?.let { extraTags["product:price:amount"] = it }
            currency?.let { extraTags["product:price:currency"] = it }

            return OpenGraphTags(
                title = title,
                type = "product",
                url = url,
                image = image,
                description = description,
                siteName = siteName,
                extraTags = extraTags
            )
        }
    }
} 