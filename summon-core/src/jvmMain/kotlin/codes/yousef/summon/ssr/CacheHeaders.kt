package codes.yousef.summon.ssr

/**
 * Cache header utilities for optimizing static asset delivery.
 *
 * These headers enable efficient browser caching for Summon's hydration assets,
 * significantly improving performance for returning visitors.
 *
 * Recommended cache strategies:
 * - **Immutable assets** (hashed filenames): Cache for 1 year with immutable directive
 * - **Versioned assets** (stable names): Cache for 1 day with ETag/Last-Modified
 * - **HTML responses**: No caching or short cache with revalidation
 */
object CacheHeaders {

    /**
     * Cache-Control header value for immutable static assets.
     * Use for files with content hashes in their names (e.g., `abc123.wasm`).
     *
     * - `public`: Allow caching by shared caches (CDNs, proxies)
     * - `max-age=31536000`: Cache for 1 year (365 days)
     * - `immutable`: Asset will never change, don't revalidate
     */
    const val IMMUTABLE_ASSET = "public, max-age=31536000, immutable"

    /**
     * Cache-Control header value for versioned static assets.
     * Use for files with stable names but versioned content (e.g., `summon-hydration.js`).
     *
     * - `public`: Allow caching by shared caches
     * - `max-age=86400`: Cache for 1 day (24 hours)
     * - Relies on ETag/Last-Modified for revalidation
     */
    const val VERSIONED_ASSET = "public, max-age=86400"

    /**
     * Cache-Control header value for dynamic HTML responses.
     * HTML pages should not be cached or have very short cache times.
     *
     * - `no-cache`: Must revalidate with server before using cached copy
     * - `must-revalidate`: Don't serve stale content
     */
    const val HTML_RESPONSE = "no-cache, must-revalidate"

    /**
     * Cache-Control header value for API responses.
     *
     * - `no-store`: Never cache (for sensitive data)
     */
    const val NO_CACHE = "no-store"

    /**
     * Determines the appropriate Cache-Control value based on asset name.
     *
     * @param assetName The name of the asset file
     * @return Appropriate Cache-Control header value
     */
    fun forAsset(assetName: String): String {
        return when {
            // Hashed WASM files (e.g., abc123def456.wasm) - immutable
            assetName.matches(Regex("^[a-f0-9]+\\.wasm$")) -> IMMUTABLE_ASSET

            // Hashed JS chunks (e.g., runtime.abc123.bundle.js) - immutable
            assetName.matches(Regex(".*\\.[a-f0-9]+\\.bundle\\.js$")) -> IMMUTABLE_ASSET

            // Stable-named assets (summon-hydration.js, etc.) - versioned
            assetName.endsWith(".js") || assetName.endsWith(".wasm") -> VERSIONED_ASSET

            // Default to no caching for unknown types
            else -> NO_CACHE
        }
    }

    /**
     * Common HTTP header names used for caching.
     */
    object Headers {
        const val CACHE_CONTROL = "Cache-Control"
        const val ETAG = "ETag"
        const val LAST_MODIFIED = "Last-Modified"
        const val VARY = "Vary"
    }
}
