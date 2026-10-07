@file:Suppress("UNCHECKED_CAST")

package codes.yousef.summon.routing

import codes.yousef.summon.core.splitCompat

// Removed androidx import

import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Provides support for deep linking, allowing direct navigation to specific application states.
 * This includes generating appropriate meta tags for better SEO and sharing experiences.
 */
class DeepLinking private constructor() {

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
        renderer.addHeadElement("<meta name=\"title\" content=\"${escapeDeepLinkMeta(title)}\">")
        renderer.addHeadElement("<meta name=\"description\" content=\"${escapeDeepLinkMeta(description)}\">")

        // Open Graph meta tags for social media sharing
        renderer.addHeadElement("<meta property=\"og:title\" content=\"${escapeDeepLinkMeta(title)}\">")
        renderer.addHeadElement("<meta property=\"og:description\" content=\"${escapeDeepLinkMeta(description)}\">")
        renderer.addHeadElement("<meta property=\"og:type\" content=\"${escapeDeepLinkMeta(type)}\">")
        renderer.addHeadElement("<meta property=\"og:url\" content=\"${escapeDeepLinkMeta(path)}\">")

        if (imageUrl != null) {
            renderer.addHeadElement("<meta property=\"og:image\" content=\"${escapeDeepLinkMeta(imageUrl)}\">")
        }

        // Twitter Card meta tags
        renderer.addHeadElement("<meta name=\"twitter:card\" content=\"${if (imageUrl != null) "summary_large_image" else "summary"}\">")
        renderer.addHeadElement("<meta name=\"twitter:title\" content=\"${escapeDeepLinkMeta(title)}\">")
        renderer.addHeadElement("<meta name=\"twitter:description\" content=\"${escapeDeepLinkMeta(description)}\">")

        if (imageUrl != null) {
            renderer.addHeadElement("<meta name=\"twitter:image\" content=\"${escapeDeepLinkMeta(imageUrl)}\">")
        }

        // Canonical link
        renderer.addHeadElement("<link rel=\"canonical\" href=\"${escapeDeepLinkMeta(path)}\">")
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
        val fragmentSplit = url.splitCompat("#", limit = 2)
        val fragment = if (fragmentSplit.size > 1) fragmentSplit[1] else null

        val queryStringSplit = fragmentSplit[0].splitCompat("?", limit = 2)
        val path = queryStringSplit[0]

        val queryParams = if (queryStringSplit.size > 1) {
            queryStringSplit[1].splitCompat("&")
                .filter { it.isNotEmpty() }
                .associate { param ->
                    val keyValue = param.splitCompat("=", limit = 2)
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
    fun encodeURIComponent(value: String): String = buildString(value.length) {
        value.encodeToByteArray().forEach { byte ->
            val unsigned = byte.toInt() and 0xff
            if (unsigned in 'A'.code..'Z'.code || unsigned in 'a'.code..'z'.code ||
                unsigned in '0'.code..'9'.code || unsigned == '-'.code || unsigned == '_'.code ||
                unsigned == '.'.code || unsigned == '~'.code
            ) {
                append(unsigned.toChar())
            } else {
                append('%')
                append(HEX_DIGITS[unsigned ushr 4])
                append(HEX_DIGITS[unsigned and 0x0f])
            }
        }
    }

    /**
     * Simple URL decoding function (platform-specific implementations will be more robust).
     */
    fun decodeURIComponent(value: String): String = buildString(value.length) {
        var index = 0
        while (index < value.length) {
            if (value[index] != '%' || index + 2 >= value.length) {
                append(value[index++])
                continue
            }
            val bytes = ByteArray((value.length - index) / 3 + 1)
            var count = 0
            while (index + 2 < value.length && value[index] == '%') {
                val high = value[index + 1].digitToIntOrNull(16) ?: break
                val low = value[index + 2].digitToIntOrNull(16) ?: break
                bytes[count++] = ((high shl 4) or low).toByte()
                index += 3
            }
            if (count == 0) {
                append(value[index++])
            } else {
                append(bytes.decodeToString(0, count, throwOnInvalidSequence = true))
            }
        }
    }

    /**
     * Data class representing the components of a deep link URL.

     * @property path Target path.
     * @property queryParams The query params value.
     * @property fragment The fragment value.
     */
    data class DeepLinkInfo(
        val path: String,
        val queryParams: Map<String, String>,
        val fragment: String?
    )

    /** Provides deep linking factory and constant members. */
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
private const val HEX_DIGITS = "0123456789ABCDEF"

private fun escapeDeepLinkMeta(value: String): String = buildString(value.length) {
    value.forEach { character ->
        when (character) {
            '&' -> append("&amp;")
            '<' -> append("&lt;")
            '>' -> append("&gt;")
            '"' -> append("&quot;")
            '\'' -> append("&#39;")
            else -> append(character)
        }
    }
}

/**
 * Handles deep linking and parameter extraction from URLs.
 */
object DeepLinkManager {
    /**
     * Executes the handle deep link operation.
     *
     * @param url Target URL.
     * @return The resulting value.
     */
    fun handleDeepLink(url: String): RouteMatchResult? {
        val currentRouter = RouterContext.current ?: return null
        val deepLinkInfo = DeepLinking.parseUrl(url)
        val safePath = InternalRoutePath.parse(deepLinkInfo.path) ?: return null
        currentRouter.navigate(safePath.encodedPath)
        return null
    }
}

/**
 * A composable function that handles displaying content based on a specific route
 * and its extracted parameters.
 */
@Composable
fun RouteContentHandler(matchResult: RouteMatchResult) {
    codes.yousef.summon.runtime.key("Summon.RouteContent", matchResult.route.path, matchResult.params.toMap()) {
        matchResult.route.content(RouteParams(matchResult.params))
    }
}

// --- Additional Deep Linking Utilities (Keep/Adapt) ---

/**
 * Generates a URL for a given route and parameters.
 */
fun generateUrl(routePath: String, params: Map<String, String>, queryParams: Map<String, String> = emptyMap()): String {
    var path = routePath
    params.forEach { (key, value) ->
        val encoded = DeepLinking.getInstance().encodeURIComponent(value)
        path = path.replace("{$key}", encoded).replace(":$key", encoded)
    }
    require(InternalRoutePath.parse(path) != null) { "Generated route is not a safe internal path" }
    if (queryParams.isEmpty()) return path

    val query = queryParams.entries.joinToString("&") { (key, value) ->
        val encodedKey = DeepLinking.getInstance().encodeURIComponent(key)
        val encodedValue = DeepLinking.getInstance().encodeURIComponent(value)
        "$encodedKey=$encodedValue"
    }
    return "$path?$query"
}

/**
 * Extracts query parameters from a URL string.
 */
fun extractQueryParams(url: String): Map<String, String> {
    val queryParams = mutableMapOf<String, String>()
    val queryPart = url.substringAfter('?', "")
    if (queryPart.isNotEmpty()) {
        queryPart.splitCompat('&').forEach {
            val parts = it.splitCompat('=', limit = 2)
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
