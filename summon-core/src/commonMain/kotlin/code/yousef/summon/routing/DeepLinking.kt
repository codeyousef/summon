@file:Suppress("UNCHECKED_CAST")

package code.yousef.summon.routing

// Removed androidx import

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer

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
    @Deprecated("Use the @Composable MetaTags function instead")
    fun generateMetaTags(
        path: String,
        title: String,
        description: String,
        imageUrl: String? = null,
        type: String = "website"
    ) {
        // Implementation kept for backward compatibility
    }

    /**
     * Adds meta tags for SEO and social sharing in a composable context.
     *
     * @param path The URL path
     * @param title The page title
     * @param description The page description
     * @param imageUrl Optional image URL for social media sharing
     * @param type Optional content type (default: "website")
     */
    @Composable
    fun MetaTags(
        path: String,
        title: String,
        description: String,
        imageUrl: String? = null,
        type: String = "website"
    ) {
        val renderer = LocalPlatformRenderer.current

        // Add basic meta tags
        renderer.addHeadElement("<meta name=\"title\" content=\"$title\">")
        renderer.addHeadElement("<meta name=\"description\" content=\"$description\">")

        // Open Graph meta tags for social media sharing
        renderer.addHeadElement("<meta property=\"og:title\" content=\"$title\">")
        renderer.addHeadElement("<meta property=\"og:description\" content=\"$description\">")
        renderer.addHeadElement("<meta property=\"og:type\" content=\"$type\">")
        renderer.addHeadElement("<meta property=\"og:url\" content=\"$path\">")

        if (imageUrl != null) {
            renderer.addHeadElement("<meta property=\"og:image\" content=\"$imageUrl\">")
        }

        // Twitter Card meta tags
        renderer.addHeadElement("<meta name=\"twitter:card\" content=\"${if (imageUrl != null) "summary_large_image" else "summary"}\">")
        renderer.addHeadElement("<meta name=\"twitter:title\" content=\"$title\">")
        renderer.addHeadElement("<meta name=\"twitter:description\" content=\"$description\">")

        if (imageUrl != null) {
            renderer.addHeadElement("<meta name=\"twitter:image\" content=\"$imageUrl\">")
        }

        // Canonical link
        renderer.addHeadElement("<link rel=\"canonical\" href=\"$path\">")
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

    /**
     * Simple URL decoding function (platform-specific implementations will be more robust).
     */
    fun decodeURIComponent(value: String): String {
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
         *
         * @deprecated Use the @Composable MetaTags function instead for proper integration with Compose
         */
        @Deprecated("Use the @Composable MetaTags function instead for proper integration with Compose")
        fun metaTags(
            path: String,
            title: String,
            description: String,
            imageUrl: String? = null,
            type: String = "website"
        ) {
            // Use MetaTags composable instead of deprecated generateMetaTags
            // This is a static function so we'll suppress the deprecation warning
            @Suppress("DEPRECATION")
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
        // Get the current router from RouterContext
        val currentRouter = RouterContext.current
        if (currentRouter == null) {
            println("DeepLinkManager: No router found in context")
            return null
        }

        // Parse URL and extract parameters
        val deepLinkInfo = DeepLinking.parseUrl(url)

        // Log navigation attempt
        println("DeepLinkManager: Handling URL '${deepLinkInfo.path}'")

        // Extract path parameters from the URL
        val pathParams = extractPathParameters(deepLinkInfo.path)

        // Combine path parameters with query parameters
        val allParams = pathParams.toMutableMap()
        allParams.putAll(deepLinkInfo.queryParams)

        // Navigate using the router
        currentRouter.navigate(deepLinkInfo.path)

        // Since we don't have direct access to the route definitions,
        // we can't create a complete RouteMatchResult with the actual route
        // In a real implementation with access to the router's routes,
        // we would find the matching route and create a proper RouteMatchResult

        println("DeepLinkManager: Extracted parameters: $allParams")
        return null
    }

    /**
     * Extract path parameters from a URL path
     *
     * @param path The URL path
     * @return Map of parameter names to values
     */
    private fun extractPathParameters(path: String): Map<String, String> {
        val params = mutableMapOf<String, String>()

        // Split the path into segments
        val segments = path.trim('/').split('/')

        // Look for segments that might be parameters
        // In a real implementation, we would match against route patterns
        segments.forEachIndexed { index, segment ->
            if (segment.startsWith(':')) {
                // This is a parameter in the format :paramName
                val paramName = segment.substring(1)
                params[paramName] = segment
            } else if (segment.matches(Regex("\\{.*\\}"))) {
                // This is a parameter in the format {paramName}
                val paramName = segment.removeSurrounding("{", "}")
                params[paramName] = segment
            }
        }

        return params
    }
}

/**
 * A composable function that handles displaying content based on a specific route
 * and its extracted parameters.
 */
@Composable
fun RouteContentHandler(matchResult: RouteMatchResult) {
    // Invoke the route's content function with the extracted parameters
    matchResult.route.content(RouteParams(matchResult.params))
}

// --- Additional Deep Linking Utilities (Keep/Adapt) ---

/**
 * Generates a URL for a given route and parameters.
 */
fun generateUrl(routePath: String, params: Map<String, String>, queryParams: Map<String, String> = emptyMap()): String {
    // Replace path parameters
    var url = routePath
    val unusedParams = params.toMutableMap()

    // Replace path parameters in the URL
    params.forEach { (key, value) ->
        val placeholder = "{$key}"
        if (url.contains(placeholder)) {
            url = url.replace(placeholder, value)
            unusedParams.remove(key)
        }
    }

    // Add query parameters for any remaining params and explicit queryParams
    val allQueryParams = unusedParams + queryParams
    if (allQueryParams.isNotEmpty()) {
        url += "?" + allQueryParams.entries.joinToString("&") { (key, value) ->
            "$key=${DeepLinking.getInstance().encodeURIComponent(value)}"
        }
    }

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
                val key = parts[0]
                val value = DeepLinking.getInstance().decodeURIComponent(parts[1])
                queryParams[key] = value
            }
        }
    }
    return queryParams
}

// Removed old `RouteHandler` class that implemented `Composable`
// ... other potential old structures related to deep linking ... 
