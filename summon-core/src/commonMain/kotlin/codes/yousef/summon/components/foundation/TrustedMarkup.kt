package codes.yousef.summon.components.foundation

import kotlin.jvm.JvmInline

/**
 * HTML supplied by application source code, never by users, messages, network responses, or
 * storage.
 *
 * @property value trusted markup
 */
@JvmInline
value class TrustedHtml private constructor(val value: String) {
    /** Explicit author-code trust boundary. */
    companion object {
        /** Marks [value] as trusted application-authored HTML. */
        fun fromAuthorCode(value: String): TrustedHtml = TrustedHtml(value)
    }
}

/**
 * Inline SVG supplied by application source code. Untrusted SVG is not supported.
 *
 * @property value trusted SVG markup
 */
@JvmInline
value class TrustedSvg private constructor(val value: String) {
    /** Explicit author-code trust boundary. */
    companion object {
        /** Marks [value] as trusted application-authored SVG. */
        fun fromAuthorCode(value: String): TrustedSvg = TrustedSvg(value)
    }
}

/**
 * Stylesheet supplied by application source code. Untrusted CSS is not supported.
 *
 * @property value trusted stylesheet source
 */
@JvmInline
value class TrustedCss private constructor(val value: String) {
    /** Explicit author-code trust boundary. */
    companion object {
        /** Marks [value] as trusted application-authored CSS. */
        fun fromAuthorCode(value: String): TrustedCss = TrustedCss(value)
    }
}
