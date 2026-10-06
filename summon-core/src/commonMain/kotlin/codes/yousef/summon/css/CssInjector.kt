package codes.yousef.summon.css

import codes.yousef.summon.components.foundation.TrustedCss

/**
 * Manages replaceable style blocks containing CSS explicitly trusted by application source code.
 *
 * This API does not sanitize CSS. User, message, storage, and network data must never be converted
 * to [TrustedCss]; use typed styling APIs for data-derived presentation.
 */
expect object CssInjector {
    fun injectTrustedCss(id: String, css: TrustedCss): Boolean
    fun removeTrustedCss(id: String): Boolean
    fun getTrustedCss(id: String): String?
    fun hasTrustedCss(id: String): Boolean
}

internal fun isValidCssBlockId(id: String): Boolean =
    id.length in 1..128 && id.all {
        it.code < 128 && (it.isLetterOrDigit() || it == '-' || it == '_')
    }
