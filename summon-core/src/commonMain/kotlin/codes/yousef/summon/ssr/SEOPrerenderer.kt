package codes.yousef.summon.ssr

/**
 * The SEOPrerenderer class provides utilities for server-side rendering with SEO enhancements.
 * It helps detect search engine crawlers and enrich SEO metadata for better indexing.
 */
class SEOPrerenderer {
    /**
     * Determines if a user agent belongs to a search engine crawler.
     *
     * @param userAgent The user agent string to check
     * @return True if the user agent is identified as a search engine crawler
     */
    fun isSearchEngineCrawler(userAgent: String): Boolean {
        val crawlerPatterns = listOf(
            "googlebot",
            "bingbot",
            "yandexbot",
            "duckduckbot",
            "slurp",
            "baiduspider",
            "facebookexternalhit",
            "twitterbot",
            "rogerbot",
            "linkedinbot",
            "embedly"
        )

        val lowerUserAgent = userAgent.lowercase()
        return crawlerPatterns.any { crawler -> lowerUserAgent.contains(crawler) }
    }

    /**
     * Enriches SEO metadata with additional information to improve search engine indexing.
     *
     * @param metadata The base SEO metadata to enhance
     * @return Enhanced SEO metadata with additional recommended fields
     */
    fun enrichSeoMetadata(metadata: SeoMetadata): SeoMetadata {
        val enhanced = metadata.customMetaTags.toMutableMap()

        // Add recommended meta tags if not already present
        if ("viewport" !in enhanced) {
            enhanced["viewport"] = "width=device-width, initial-scale=1.0"
        }

        if ("generator" !in enhanced) {
            enhanced["generator"] = "Summon"
        }

        // Set a longer description if the current one is too short
        val description = if (metadata.description.length < 50 && metadata.description.isNotEmpty()) {
            "${metadata.description} - Enhanced with Summon framework for optimal performance and search engine visibility."
        } else {
            metadata.description
        }

        return SeoMetadata(
            title = metadata.title,
            description = description,
            keywords = metadata.keywords,
            canonical = metadata.canonical,
            openGraph = metadata.openGraph,
            twitterCard = metadata.twitterCard,
            structuredData = metadata.structuredData,
            robots = metadata.robots,
            customMetaTags = enhanced
        )
    }

    /**
     * Creates a standard set of OpenGraph metadata from basic SEO metadata.
     *
     * @param seoMetadata Basic SEO metadata
     * @param url The canonical URL of the page
     * @param imageUrl Optional image URL for rich previews
     * @return OpenGraph metadata for social sharing
     */
    fun createOpenGraphMetadata(
        seoMetadata: SeoMetadata,
        url: String,
        imageUrl: String = "",
        siteName: String = ""
    ): OpenGraphMetadata {
        return OpenGraphMetadata(
            title = seoMetadata.title,
            description = seoMetadata.description,
            url = url,
            image = imageUrl,
            siteName = siteName
        )
    }
}

/**
 * Object for convenient access to SEO prerendering functionality.
 */
object SEOPrerender {
    private val prerenderer = SEOPrerenderer()

    /**
     * Determines if a user agent belongs to a search engine crawler.
     *
     * @param userAgent The user agent string to check
     * @return True if the user agent is identified as a search engine crawler
     */
    fun isSearchEngineCrawler(userAgent: String): Boolean {
        return prerenderer.isSearchEngineCrawler(userAgent)
    }

    /**
     * Enriches SEO metadata with additional information to improve search engine indexing.
     *
     * @param metadata The base SEO metadata to enhance
     * @return Enhanced SEO metadata with additional recommended fields
     */
    fun enrichSeoMetadata(metadata: SeoMetadata): SeoMetadata {
        return prerenderer.enrichSeoMetadata(metadata)
    }

    /**
     * Creates a standard set of OpenGraph metadata from basic SEO metadata.
     *
     * @param seoMetadata Basic SEO metadata
     * @param url The canonical URL of the page
     * @param imageUrl Optional image URL for rich previews
     * @return OpenGraph metadata for social sharing
     */
    fun createOpenGraphMetadata(
        seoMetadata: SeoMetadata,
        url: String,
        imageUrl: String = "",
        siteName: String = ""
    ): OpenGraphMetadata {
        return prerenderer.createOpenGraphMetadata(seoMetadata, url, imageUrl, siteName)
    }
} 