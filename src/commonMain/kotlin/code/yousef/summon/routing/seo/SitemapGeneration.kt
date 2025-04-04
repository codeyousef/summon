package code.yousef.summon.routing.seo

import code.yousef.summon.core.Composable
import code.yousef.summon.routing.Route
import kotlinx.html.FlowContent
import kotlinx.html.TagConsumer
import kotlinx.html.pre

/**
 * SitemapGeneration provides utilities for creating XML sitemaps
 * that help search engines discover and understand the structure of your site
 */
class SitemapGeneration {

    /**
     * A URL entry in the sitemap with its metadata
     */
    data class SitemapUrl(
        val loc: String,
        val lastmod: String? = null,
        val changefreq: ChangeFrequency? = null,
        val priority: Double? = null,
        val alternates: Map<String, String> = emptyMap()
    )

    /**
     * Change frequency options for sitemap entries
     */
    enum class ChangeFrequency(val value: String) {
        ALWAYS("always"),
        HOURLY("hourly"),
        DAILY("daily"),
        WEEKLY("weekly"),
        MONTHLY("monthly"),
        YEARLY("yearly"),
        NEVER("never")
    }

    /**
     * Component for rendering the sitemap XML
     */
    class SitemapXml(
        private val urls: List<SitemapUrl>,
        private val baseUrl: String
    ) : Composable {

        @Suppress("UNCHECKED_CAST")
        override fun <T> compose(receiver: T): T {
            if (receiver is FlowContent) {
                val xml = buildSitemapXml()
                receiver.pre {
                    +xml
                }
            } else if (receiver is TagConsumer<*>) {
                @Suppress("UNCHECKED_CAST")
                val consumer = receiver as TagConsumer<Any?>
                val xml = buildSitemapXml()
                consumer.onTagContent(xml)
                return receiver
            }

            return receiver
        }

        /**
         * Get the raw XML string for the sitemap
         */
        fun getXmlString(): String {
            return buildSitemapXml()
        }

        private fun buildSitemapXml(): String {
            val xmlBuilder = StringBuilder()
            xmlBuilder.append(
                """<?xml version="1.0" encoding="UTF-8"?>
                |<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
                |        xmlns:xhtml="http://www.w3.org/1999/xhtml">
            """.trimMargin()
            )

            urls.forEach { url ->
                xmlBuilder.append("  <url>\n")
                xmlBuilder.append("    <loc>${baseUrl.trimEnd('/')}/${url.loc.trimStart('/')}</loc>\n")

                url.lastmod?.let {
                    xmlBuilder.append("    <lastmod>$it</lastmod>\n")
                }

                url.changefreq?.let {
                    xmlBuilder.append("    <changefreq>${it.value}</changefreq>\n")
                }

                url.priority?.let {
                    xmlBuilder.append("    <priority>$it</priority>\n")
                }

                url.alternates.forEach { (lang, href) ->
                    xmlBuilder.append(
                        """    <xhtml:link rel="alternate" hreflang="$lang" href="$href" />
                    """.trimIndent()
                    )
                    xmlBuilder.append("\n")
                }

                xmlBuilder.append("  </url>\n")
            }

            xmlBuilder.append("</urlset>")
            return xmlBuilder.toString()
        }
    }

    companion object {
        /**
         * Generate sitemap entries from a list of routes
         */
        fun fromRoutes(
            routes: List<Route>,
            baseUrl: String,
            defaultChangeFreq: ChangeFrequency = ChangeFrequency.WEEKLY,
            defaultPriority: Double = 0.7
        ): SitemapXml {
            val sitemapUrls = routes.map { route ->
                val path = route.pattern
                    .replace(Regex(":[^/]+"), "1") // Replace :id with 1, :slug with 1, etc.
                    .replace(Regex("\\*.*"), "")   // Remove wildcard parts

                SitemapUrl(
                    loc = path,
                    changefreq = defaultChangeFreq,
                    priority = if (path == "/" || path.isEmpty()) 1.0 else defaultPriority
                )
            }

            return SitemapXml(sitemapUrls, baseUrl)
        }

        /**
         * Format date for sitemap (YYYY-MM-DD)
         */
        fun formatDate(year: Int, month: Int, day: Int): String {
            return "${year.toString().padStart(4, '0')}-${month.toString().padStart(2, '0')}-${
                day.toString().padStart(2, '0')
            }"
        }

        /**
         * Generate a robots.txt content with sitemap reference
         */
        fun robotsTxt(sitemapUrl: String): String {
            return """
                User-agent: *
                Allow: /
                
                Sitemap: $sitemapUrl
            """.trimIndent()
        }
    }
} 