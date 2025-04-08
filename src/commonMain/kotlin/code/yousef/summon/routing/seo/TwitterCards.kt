package code.yousef.summon.routing.seo

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import kotlinx.html.HEAD
import kotlinx.html.meta

import code.yousef.summon.runtime.SideEffect

/**
 * TwitterCards component for adding Twitter-specific metadata
 * These tags help display rich previews when users share your content on Twitter
 */
class TwitterCards(
    private val card: CardType = CardType.SUMMARY,
    private val title: String? = null,
    private val description: String? = null,
    private val image: String? = null,
    private val imageAlt: String? = null,
    private val site: String? = null,
    private val creator: String? = null,
    private val extraTags: Map<String, String> = emptyMap()
) : Composable {

    enum class CardType(val value: String) {
        SUMMARY("summary"),
        SUMMARY_LARGE_IMAGE("summary_large_image"),
        APP("app"),
        PLAYER("player")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> compose(receiver: T): T {
        if (receiver is HEAD) {
            receiver.apply {
                // Card type
                meta {
                    attributes["name"] = "twitter:card"
                    attributes["content"] = card.value
                }

                // Basic tags
                title?.let {
                    meta {
                        attributes["name"] = "twitter:title"
                        attributes["content"] = it
                    }
                }
                description?.let {
                    meta {
                        attributes["name"] = "twitter:description"
                        attributes["content"] = it
                    }
                }

                // Image
                image?.let {
                    meta {
                        attributes["name"] = "twitter:image"
                        attributes["content"] = it
                    }
                }
                imageAlt?.let {
                    meta {
                        attributes["name"] = "twitter:image:alt"
                        attributes["content"] = it
                    }
                }

                // Account information
                site?.let {
                    meta {
                        attributes["name"] = "twitter:site"
                        attributes["content"] = it
                    }
                }
                creator?.let {
                    meta {
                        attributes["name"] = "twitter:creator"
                        attributes["content"] = it
                    }
                }

                // Add any additional Twitter tags
                extraTags.forEach { (name, content) ->
                    meta {
                        attributes["name"] = "twitter:$name"
                        attributes["content"] = content
                    }
                }
            }
        }

        return receiver
    }

    companion object {
        /**
         * Create Twitter card for an article
         */
        fun article(
            title: String,
            description: String,
            image: String? = null,
            site: String? = null,
            creator: String? = null
        ): TwitterCards {
            return TwitterCards(
                card = CardType.SUMMARY_LARGE_IMAGE,
                title = title,
                description = description,
                image = image,
                site = site,
                creator = creator
            )
        }

        /**
         * Create Twitter card for a product
         */
        fun product(
            title: String,
            description: String,
            image: String,
            price: String? = null,
            site: String? = null
        ): TwitterCards {
            val extraTags = mutableMapOf<String, String>()
            price?.let { extraTags["label1"] = "Price"; extraTags["data1"] = it }

            return TwitterCards(
                card = CardType.SUMMARY,
                title = title,
                description = description,
                image = image,
                site = site,
                extraTags = extraTags
            )
        }
    }
}

/**
 * Composable function to add a Twitter Card meta tag to the document head.
 * Used for Twitter sharing previews.
 *
 * @param name The Twitter card property name (e.g., "twitter:card", "twitter:title").
 * @param content The content value for the property.
 */
@Composable
fun TwitterCardTag(name: String, content: String) {
    val composer = CompositionLocal.currentComposer

    SideEffect {
        println("TwitterCardTag SideEffect: Setting name='$name' content='$content'")
        // TODO: Implement platform-specific head manipulation.
        // PlatformRendererProvider.code.yousef.summon.runtime.PlatformRendererProvider.getPlatformRenderer().addHeadElement("<meta name=\"$name\" content=\"$content\">")
    }
    // Renders no UI.
}

// Convenience functions for common Twitter Card tags
@Composable fun TwitterCard(cardType: String) = TwitterCardTag("twitter:card", cardType) // e.g., "summary", "summary_large_image"
@Composable fun TwitterSite(siteHandle: String) = TwitterCardTag("twitter:site", siteHandle)
@Composable fun TwitterCreator(creatorHandle: String) = TwitterCardTag("twitter:creator", creatorHandle)
@Composable fun TwitterTitle(title: String) = TwitterCardTag("twitter:title", title)
@Composable fun TwitterDescription(description: String) = TwitterCardTag("twitter:description", description)
@Composable fun TwitterImage(imageUrl: String) = TwitterCardTag("twitter:image", imageUrl) 
