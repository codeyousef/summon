@file:Suppress("UNCHECKED_CAST")

package code.yousef.summon.routing

import kotlinx.html.TagConsumer
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.meta

/**
 * Provides support for deep linking, allowing direct navigation to specific application states.
 * This includes generating appropriate meta tags for better SEO and sharing experiences.
 */
class DeepLinking private constructor() {

    /**
     * Generates meta tags for a specific route to improve SEO and sharing.
     *
     * @param path The URL path
     * @param title The page title
     * @param description The page description
     * @param imageUrl Optional image URL for social media sharing
     * @param type Optional content type (default: "website")
     */
    fun generateMetaTags(
        path: String,
        title: String,
        description: String,
        imageUrl: String? = null,
        type: String = "website"
    ) {
        // TODO: Refactor for Compose HTML - This likely needs to be a @Composable function
        //       that uses Compose HTML's APIs (e.g., Head) instead of returning an object.
        //       For now, we'll just keep the logic but it won't be directly usable.
        fun <T> applyMetaTags(receiver: T): T {
            if (receiver is TagConsumer<*>) {
                @Suppress("UNCHECKED_CAST")
                val consumer = receiver as TagConsumer<Any?>

                consumer.head {
                    // Basic meta tags
                    meta(name = "title", content = title)
                    meta(name = "description", content = description)

                    // Open Graph meta tags for social media sharing
                    meta(name = "og:title", content = title)
                    meta(name = "og:description", content = description)
                    meta(name = "og:type", content = type)
                    meta(name = "og:url", content = path)

                    if (imageUrl != null) {
                        meta(name = "og:image", content = imageUrl)
                    }

                    // Twitter Card meta tags
                    meta(
                        name = "twitter:card",
                        content = if (imageUrl != null) "summary_large_image" else "summary"
                    )
                    meta(name = "twitter:title", content = title)
                    meta(name = "twitter:description", content = description)

                    if (imageUrl != null) {
                        meta(name = "twitter:image", content = imageUrl)
                    }

                    // Canonical link
                    link(rel = "canonical", href = path)
                }

                return consumer as T
            }

            return receiver
        }
    }

    /**
     * Creates a canonicalized deep link URL with optional query parameters.
     *
     * @param path The base URL path
     * @param queryParams Optional map of query parameters
     * @param fragment Optional URL fragment (hash)
     * @return The complete deep link URL
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
     * @return A DeepLinkInfo object containing the parsed components
     */
    fun parseDeepLink(url: String): DeepLinkInfo {
        // Split the URL into path, query, and fragment
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
     * Simple URL encoding function (platform-specific implementations will be more robust).
     */
    private fun encodeURIComponent(value: String): String {
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

    /**
     * Simple URL decoding function (platform-specific implementations will be more robust).
     */
    private fun decodeURIComponent(value: String): String {
        // A very basic implementation for common cases
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
     * Data class representing the components of a deep link URL.
     */
    data class DeepLinkInfo(
        val path: String,
        val queryParams: Map<String, String>,
        val fragment: String?
    )

    companion object {
        private var instance: DeepLinking? = null

        /**
         * Gets the singleton instance of DeepLinking.
         *
         * @return The DeepLinking singleton instance
         */
        fun getInstance(): DeepLinking {
            if (instance == null) {
                instance = DeepLinking()
            }
            return instance!!
        }

        /**
         * Generates meta tags for a specific route to improve SEO and sharing.
         * Convenience method that delegates to the instance.
         */
        fun metaTags(
            path: String,
            title: String,
            description: String,
            imageUrl: String? = null,
            type: String = "website"
        ) {
            getInstance().generateMetaTags(path, title, description, imageUrl, type)
        }

        /**
         * Creates a canonicalized deep link URL with optional query parameters.
         * Convenience method that delegates to the instance.
         */
        fun createUrl(
            path: String,
            queryParams: Map<String, String> = emptyMap(),
            fragment: String? = null
        ): String {
            return getInstance().createDeepLink(path, queryParams, fragment)
        }

        /**
         * Parses a deep link URL into its components.
         * Convenience method that delegates to the instance.
         */
        fun parseUrl(url: String): DeepLinkInfo {
            return getInstance().parseDeepLink(url)
        }
    }
}

/**
 * Handles deep linking and parameter extraction from URLs.
 */
object DeepLinkManager {
    fun handleDeepLink(url: String): RouteMatchResult? {
        val routes = RouterBuilder.build().routes
        // Parse URL, match against registered routes, extract params
        println("DeepLinkManager: Handling URL '$url' (not implemented)")
        return null // Placeholder
    }
}

/**
 * A composable function that handles displaying content based on a specific route
 * and its extracted parameters.
 */
fun RouteContentHandler(matchResult: RouteMatchResult) {
    println("RouteContentHandler called for path: ${matchResult.route.path}. Needs Compose HTML implementation.")
    // This call needs Compose HTML context:
    // matchResult.route.content(matchResult.params)
}

// --- Additional Deep Linking Utilities (Keep/Adapt) ---

/**
 * Generates a URL for a given route and parameters.
 */
fun generateUrl(routePath: String, params: Map<String, String>): String {
    var url = routePath
    params.forEach { (key, value) ->
        url = url.replace("{$key}", value)
    }
    // TODO: Handle query parameters?
    return url
}

/**
 * Extracts query parameters from a URL string.
 */
fun extractQueryParams(url: String): Map<String, String> {
    val queryParams = mutableMapOf<String, String>()
    val queryPart = url.substringAfter('?', "")
    if (queryPart.isNotEmpty()) {
        queryPart.split('&').forEach {
            val parts = it.split('=', limit = 2)
            if (parts.size == 2) {
                queryParams[parts[0]] = parts[1] // TODO: URL Decode?
            }
        }
    }
    return queryParams
}

// Removed old `RouteHandler` class that implemented `Composable`
// ... other potential old structures related to deep linking ... 
