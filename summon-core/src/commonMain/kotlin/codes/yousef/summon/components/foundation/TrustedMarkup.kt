package codes.yousef.summon.components.foundation

import kotlin.jvm.JvmInline

/**
 * HTML supplied by application source code, never by users, messages, network responses, or storage.
 * Constructing this value is an explicit trust decision; use SafeDocument for untrusted content.
 */
@JvmInline
value class TrustedHtml private constructor(val value: String) {
    companion object {
        fun fromAuthorCode(value: String): TrustedHtml = TrustedHtml(value)
    }
}

/** Inline SVG supplied by application source code. Untrusted SVG is not supported. */
@JvmInline
value class TrustedSvg private constructor(val value: String) {
    companion object {
        fun fromAuthorCode(value: String): TrustedSvg = TrustedSvg(value)
    }
}

/** Stylesheet source supplied by application code. Untrusted CSS is not supported. */
@JvmInline
value class TrustedCss private constructor(val value: String) {
    companion object {
        fun fromAuthorCode(value: String): TrustedCss = TrustedCss(value)
    }
}
