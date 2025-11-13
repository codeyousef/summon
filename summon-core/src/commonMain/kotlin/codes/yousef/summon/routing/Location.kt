package codes.yousef.summon.routing

import codes.yousef.summon.core.splitCompat

/**
 * Represents a location in the application's navigation history.
 * Contains information about the current URL path and state.
 *
 * @param path The URL path (e.g., "/users/123")
 * @param queryParams Map of query parameters from the URL
 * @param hash The URL hash fragment (without the "#" symbol)
 * @param state Map of state values associated with this location
 */
data class Location(
    val path: String,
    val queryParams: Map<String, String> = emptyMap(),
    val hash: String = "",
    val state: Map<String, Any>? = null
) {
    /**
     * The full path including query parameters and hash.
     */
    val fullPath: String
        get() {
            val queryString = if (queryParams.isNotEmpty()) {
                "?" + queryParams.entries.joinToString("&") { "${it.key}=${it.value}" }
            } else {
                ""
            }

            val hashString = if (hash.isNotEmpty()) {
                "#$hash"
            } else {
                ""
            }

            return path + queryString + hashString
        }

    /**
     * Creates a new Location with the same properties but a different path.
     */
    fun withPath(newPath: String): Location = copy(path = newPath)

    /**
     * Creates a new Location with the same properties but different query parameters.
     */
    fun withQueryParams(newQueryParams: Map<String, String>): Location = copy(queryParams = newQueryParams)

    /**
     * Creates a new Location with the same properties but an additional query parameter.
     */
    fun withQueryParam(key: String, value: String): Location =
        copy(queryParams = queryParams + (key to value))

    /**
     * Creates a new Location with the same properties but a different hash.
     */
    fun withHash(newHash: String): Location = copy(hash = newHash)

    /**
     * Creates a new Location with the same properties but different state.
     */
    fun withState(newState: Map<String, Any>?): Location = copy(state = newState)

    companion object {
        /**
         * Parse a URL string into a Location object.
         */
        fun fromUrl(url: String): Location {
            val hashSplit = url.splitCompat("#", limit = 2)
            val hash = if (hashSplit.size > 1) hashSplit[1] else ""

            val queryParamsSplit = hashSplit[0].splitCompat("?", limit = 2)
            val path = queryParamsSplit[0]

            val queryParams = if (queryParamsSplit.size > 1) {
                queryParamsSplit[1].splitCompat("&")
                    .filter { it.isNotEmpty() }
                    .map {
                        val paramSplit = it.splitCompat("=", limit = 2)
                        val key = paramSplit[0]
                        val value = if (paramSplit.size > 1) paramSplit[1] else ""
                        key to value
                    }
                    .toMap()
            } else {
                emptyMap()
            }

            return Location(path, queryParams, hash)
        }
    }
} 