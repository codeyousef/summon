package code.yousef.summon.routing.seo

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * The DeepLinking class provides utilities for generating metadata for deep links
 * and improving SEO for your application routes. It helps with social media sharing
 * and search engine indexing of specific pages.
 */
class DeepLinking private constructor() {
    
    /**
     * Generates meta tags for deep linking and better SEO.
     *
     * @param path The URL path for canonical URLs and social sharing
     * @param title The page title for sharing and search results
     * @param description Page description for search engines and previews
     * @param imageUrl Optional image URL for rich social sharing cards
     * @param type Content type (default: "website")
     * @return A Composable that adds the meta tags to the document head
     */
    @Composable
    fun generateMetaTags(
        path: String,
        title: String,
        description: String,
        imageUrl: String? = null,
        type: String = "website"
    ) {
        val renderer = LocalPlatformRenderer.current
        
        // Basic meta tags
        renderer.addHeadElement("<meta name=\"title\" content=\"$title\">")
        renderer.addHeadElement("<meta name=\"description\" content=\"$description\">")
        
        // Open Graph tags for social sharing
        renderer.addHeadElement("<meta property=\"og:title\" content=\"$title\">")
        renderer.addHeadElement("<meta property=\"og:description\" content=\"$description\">")
        renderer.addHeadElement("<meta property=\"og:type\" content=\"$type\">")
        renderer.addHeadElement("<meta property=\"og:url\" content=\"$path\">")
        
        if (imageUrl != null) {
            renderer.addHeadElement("<meta property=\"og:image\" content=\"$imageUrl\">")
        }
        
        // Twitter Card tags
        renderer.addHeadElement("<meta name=\"twitter:card\" content=\"${if (imageUrl != null) "summary_large_image" else "summary"}\">")
        renderer.addHeadElement("<meta name=\"twitter:title\" content=\"$title\">")
        renderer.addHeadElement("<meta name=\"twitter:description\" content=\"$description\">")
        
        if (imageUrl != null) {
            renderer.addHeadElement("<meta name=\"twitter:image\" content=\"$imageUrl\">")
        }
        
        // Canonical URL
        renderer.addHeadElement("<link rel=\"canonical\" href=\"$path\">")
    }
    
    /**
     * Creates a deep link URL with optional query parameters and fragment.
     *
     * @param path Base URL path
     * @param queryParams Optional query parameters
     * @param fragment Optional URL fragment (hash)
     * @return The formatted deep link URL
     */
    fun createDeepLink(
        path: String,
        queryParams: Map<String, String> = emptyMap(),
        fragment: String? = null
    ): String {
        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        
        val queryString = if (queryParams.isEmpty()) {
            ""
        } else {
            "?" + queryParams.entries.joinToString("&") { (key, value) ->
                "$key=${encodeURIComponent(value)}"
            }
        }
        
        val fragmentString = fragment?.let { "#$it" } ?: ""
        
        return normalizedPath + queryString + fragmentString
    }
    
    /**
     * Parses a deep link URL into its components.
     *
     * @param url The URL to parse
     * @return A DeepLinkInfo object containing path, query parameters, and fragment
     */
    fun parseDeepLink(url: String): DeepLinkInfo {
        // Split URL into path, query, and fragment
        val fragmentSplit = url.split("#", limit = 2)
        val fragment = if (fragmentSplit.size > 1) fragmentSplit[1] else null
        
        val queryStringSplit = fragmentSplit[0].split("?", limit = 2)
        val path = queryStringSplit[0]
        
        val queryParams = if (queryStringSplit.size > 1) {
            queryStringSplit[1].split("&")
                .filter { it.isNotEmpty() }
                .associate { param ->
                    val keyValue = param.split("=", limit = 2)
                    val key = keyValue[0]
                    val value = if (keyValue.size > 1) decodeURIComponent(keyValue[1]) else ""
                    key to value
                }
        } else {
            emptyMap()
        }
        
        return DeepLinkInfo(path, queryParams, fragment)
    }
    
    /**
     * Data class representing components of a deep link URL.
     */
    data class DeepLinkInfo(
        val path: String,
        val queryParams: Map<String, String>,
        val fragment: String?
    )
    
    companion object {
        // Singleton instance
        private val instance by lazy { DeepLinking() }
        
        // Helper methods for URL encoding/decoding
        fun encodeURIComponent(value: String): String {
            // A basic implementation that works for common cases
            return value.replace(" ", "%20")
                .replace("!", "%21")
                .replace("\"", "%22")
                .replace("#", "%23")
                .replace("$", "%24")
                .replace("%", "%25")
                .replace("&", "%26")
                .replace("'", "%27")
                .replace("(", "%28")
                .replace(")", "%29")
                .replace("*", "%2A")
                .replace("+", "%2B")
                .replace(",", "%2C")
                .replace("/", "%2F")
                .replace(":", "%3A")
                .replace(";", "%3B")
                .replace("=", "%3D")
                .replace("?", "%3F")
                .replace("@", "%40")
                .replace("[", "%5B")
                .replace("\\", "%5C")
                .replace("]", "%5D")
                .replace("{", "%7B")
                .replace("|", "%7C")
                .replace("}", "%7D")
        }
        
        fun decodeURIComponent(value: String): String {
            // A basic implementation for common cases
            return value.replace("%20", " ")
                .replace("%21", "!")
                .replace("%22", "\"")
                .replace("%23", "#")
                .replace("%24", "$")
                .replace("%25", "%")
                .replace("%26", "&")
                .replace("%27", "'")
                .replace("%28", "(")
                .replace("%29", ")")
                .replace("%2A", "*")
                .replace("%2B", "+")
                .replace("%2C", ",")
                .replace("%2F", "/")
                .replace("%3A", ":")
                .replace("%3B", ";")
                .replace("%3D", "=")
                .replace("%3F", "?")
                .replace("%40", "@")
                .replace("%5B", "[")
                .replace("%5C", "\\")
                .replace("%5D", "]")
                .replace("%7B", "{")
                .replace("%7C", "|")
                .replace("%7D", "}")
        }
        
        /**
         * Access the singleton instance of DeepLinking.
         */
        val current: DeepLinking get() = instance
    }
} 