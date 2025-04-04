package code.yousef.summon.routing.seo

import code.yousef.summon.Composable
import kotlinx.html.HEAD
import kotlinx.html.link

/**
 * CanonicalLinks component for managing canonical URLs
 * This helps search engines understand the primary version of a page when
 * multiple URLs might serve the same or similar content
 */
class CanonicalLinks(
    private val url: String,
    private val alternateLanguages: Map<String, String> = emptyMap(),
    private val ampUrl: String? = null
) : Composable {

    @Suppress("UNCHECKED_CAST")
    override fun <T> compose(receiver: T): T {
        if (receiver is HEAD) {
            receiver.apply {
                // Canonical URL - the definitive/preferred version of the page
                link {
                    attributes["rel"] = "canonical"
                    attributes["href"] = url
                }

                // Alternate language versions
                alternateLanguages.forEach { (lang, href) ->
                    link {
                        attributes["rel"] = "alternate"
                        attributes["hreflang"] = lang
                        attributes["href"] = href
                    }
                }

                // AMP version if available
                ampUrl?.let {
                    link {
                        attributes["rel"] = "amphtml"
                        attributes["href"] = it
                    }
                }
            }
        }

        return receiver
    }

    companion object {
        /**
         * Create a simple canonical link
         */
        fun simple(url: String): CanonicalLinks {
            return CanonicalLinks(url)
        }

        /**
         * Create a canonical link with language alternates
         */
        fun withLanguages(
            defaultUrl: String,
            defaultLanguage: String,
            alternates: Map<String, String>
        ): CanonicalLinks {
            val allLanguages = mutableMapOf<String, String>()
            allLanguages[defaultLanguage] = defaultUrl
            allLanguages.putAll(alternates)

            return CanonicalLinks(
                url = defaultUrl,
                alternateLanguages = allLanguages
            )
        }

        /**
         * Create a canonical link with AMP version
         */
        fun withAmp(
            url: String,
            ampUrl: String
        ): CanonicalLinks {
            return CanonicalLinks(
                url = url,
                ampUrl = ampUrl
            )
        }
    }
} 