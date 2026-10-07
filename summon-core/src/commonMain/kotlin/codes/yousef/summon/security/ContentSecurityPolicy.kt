package codes.yousef.summon.security

/**
 * Explicitly audited public state that may be embedded in SSR output.
 * Private vault/session state must never be represented by this type.
 */
data class PublicHydrationState(val json: String) {
    init {
        require(json.length <= 65_536) { "Public hydration state exceeds 64 KiB" }
        require(json.all { it.code <= 0x7f }) { "Public hydration state must be ASCII JSON" }
    }
}

/**
 * A rendered HTML document and the exact CSP header that must accompany it.
 * Keeping both values together prevents nonce/header drift at integration boundaries.
 */
data class CspDocument(
    val html: String,
    val contentSecurityPolicy: String
)

/** Strict first-party policy for locked/private application shells. */
object PrivateShellContentSecurityPolicy {
    fun headerValue(styleNonce: String): String {
        require(isValidNonce(styleNonce)) { "CSP nonce must be 32 bytes encoded as base64url" }
        return listOf(
            "default-src 'none'",
            "script-src 'self' 'wasm-unsafe-eval'",
            "script-src-elem 'self'",
            "script-src-attr 'none'",
            "style-src 'self' 'nonce-$styleNonce'",
            "style-src-elem 'self' 'nonce-$styleNonce'",
            "style-src-attr 'unsafe-inline'",
            "img-src 'self' blob:",
            "media-src 'self' blob:",
            "font-src 'self'",
            "connect-src 'self'",
            "worker-src 'self'",
            "manifest-src 'self'",
            "object-src 'none'",
            "base-uri 'none'",
            "frame-ancestors 'none'",
            "form-action 'self'"
        ).joinToString("; ")
    }

    fun isValidNonce(value: String): Boolean =
        value.length == 43 && value.all { it.isLetterOrDigit() || it == '-' || it == '_' }
}
