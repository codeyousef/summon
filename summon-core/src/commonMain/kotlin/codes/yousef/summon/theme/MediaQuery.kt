package codes.yousef.summon.theme

import codes.yousef.summon.core.mapOfCompat

import codes.yousef.summon.modifier.*


/**
 * MediaQuery provides responsive style adjustments based on screen size.
 * This class helps create media query-based styles using a Compose-like API.
 */
object MediaQuery {
    /**
     * Standard breakpoints for responsive design
     */
    object Breakpoints {
        /**
         * Mobile phones (0px - 599px)
         */
        const val xs = 0

        /**
         * Large phones, small tablets (600px - 959px)
         */
        const val sm = 600

        /**
         * Tablets (960px - 1279px)
         */
        const val md = 960

        /**
         * Desktops (1280px - 1919px)
         */
        const val lg = 1280

        /**
         * Large desktops (1920px and above)
         */
        const val xl = 1920
    }

    /**
     * Creates a min-width media query.
     * @param breakpoint Width in pixels
     * @param styleModifier The modifier to apply if the condition is met
     * @return A new MediaQueryModifier
     */
    fun minWidth(breakpoint: Int, styleModifier: Modifier): MediaQueryModifier {
        val query = "@media (min-width: ${breakpoint}px)"
        return MediaQueryModifier(query, styleModifier)
    }

    /**
     * Creates a max-width media query.
     * @param breakpoint Width in pixels
     * @param styleModifier The modifier to apply if the condition is met
     * @return A new MediaQueryModifier
     */
    fun maxWidth(breakpoint: Int, styleModifier: Modifier): MediaQueryModifier {
        val query = "@media (max-width: ${breakpoint}px)"
        return MediaQueryModifier(query, styleModifier)
    }

    /**
     * Creates a min-height media query.
     * @param breakpoint Height in pixels
     * @param styleModifier The modifier to apply if the condition is met
     * @return A new MediaQueryModifier
     */
    fun minHeight(breakpoint: Int, styleModifier: Modifier): MediaQueryModifier {
        val query = "@media (min-height: ${breakpoint}px)"
        return MediaQueryModifier(query, styleModifier)
    }

    /**
     * Creates a max-height media query.
     * @param breakpoint Height in pixels
     * @param styleModifier The modifier to apply if the condition is met
     * @return A new MediaQueryModifier
     */
    fun maxHeight(breakpoint: Int, styleModifier: Modifier): MediaQueryModifier {
        val query = "@media (max-height: ${breakpoint}px)"
        return MediaQueryModifier(query, styleModifier)
    }

    /**
     * Creates a range-based media query for width.
     * @param minWidth Minimum width in pixels
     * @param maxWidth Maximum width in pixels
     * @param styleModifier The modifier to apply if the condition is met
     * @return A new MediaQueryModifier
     */
    fun betweenWidth(minWidth: Int, maxWidth: Int, styleModifier: Modifier): MediaQueryModifier {
        val query = "@media (min-width: ${minWidth}px) and (max-width: ${maxWidth}px)"
        return MediaQueryModifier(query, styleModifier)
    }

    /**
     * Creates a media query for mobile devices.
     * @param styleModifier The modifier to apply if on mobile
     * @return A new MediaQueryModifier
     */
    fun mobile(styleModifier: Modifier): MediaQueryModifier {
        return maxWidth(Breakpoints.sm - 1, styleModifier)
    }

    /**
     * Creates a media query for tablet devices.
     * @param styleModifier The modifier to apply if on tablet
     * @return A new MediaQueryModifier
     */
    fun tablet(styleModifier: Modifier): MediaQueryModifier {
        return betweenWidth(Breakpoints.sm, Breakpoints.lg - 1, styleModifier)
    }

    /**
     * Creates a media query for desktop devices.
     * @param styleModifier The modifier to apply if on desktop
     * @return A new MediaQueryModifier
     */
    fun desktop(styleModifier: Modifier): MediaQueryModifier {
        return minWidth(Breakpoints.lg, styleModifier)
    }

    /**
     * Creates a media query for orientation.
     * @param isPortrait True for portrait, false for landscape
     * @param styleModifier The modifier to apply if the orientation matches
     * @return A new MediaQueryModifier
     */
    fun orientation(isPortrait: Boolean, styleModifier: Modifier): MediaQueryModifier {
        val orientation = if (isPortrait) "portrait" else "landscape"
        val query = "@media (orientation: $orientation)"
        return MediaQueryModifier(query, styleModifier)
    }

    /**
     * Creates a media query for dark mode preference.
     * @param styleModifier The modifier to apply if dark mode is preferred
     * @return A new MediaQueryModifier
     */
    fun darkMode(styleModifier: Modifier): MediaQueryModifier {
        val query = "@media (prefers-color-scheme: dark)"
        return MediaQueryModifier(query, styleModifier)
    }

    /**
     * Creates a media query for light mode preference.
     * @param styleModifier The modifier to apply if light mode is preferred
     * @return A new MediaQueryModifier
     */
    fun lightMode(styleModifier: Modifier): MediaQueryModifier {
        val query = "@media (prefers-color-scheme: light)"
        return MediaQueryModifier(query, styleModifier)
    }

    /**
     * Creates a media query for reduced motion preference.
     * @param styleModifier The modifier to apply if reduced motion is preferred
     * @return A new MediaQueryModifier
     */
    fun reducedMotion(styleModifier: Modifier): MediaQueryModifier {
        val query = "@media (prefers-reduced-motion: reduce)"
        return MediaQueryModifier(query, styleModifier)
    }
}

/**
 * MediaQueryModifier contains a media query condition and the styles to apply when the condition is met.
 * This is used to create responsive styles in a declarative way.
 */
class MediaQueryModifier(
    private val query: String,
    private val styleModifier: Modifier
) {
    /**
     * Combines this media query with a base modifier.
     * @param baseModifier The base modifier to apply (regardless of media query)
     * @return A combined modifier that includes both the base styles and the media query
     */
    fun applyTo(baseModifier: Modifier): Modifier {
        // Store media query in a special key that platform implementations will handle
        val mediaQueryEntries = styleModifier.styles.entries
            .joinToString(";") { (key, value) -> "$key:$value" }

        return baseModifier.then(
            ModifierImpl(mapOfCompat("__media" to "$query{$mediaQueryEntries}"))
        )
    }

    /**
     * Combines multiple media query modifiers into a single modifier.
     * @param others Additional media query modifiers to combine
     * @return A combined modifier containing all media queries
     */
    fun and(vararg others: MediaQueryModifier): MediaQueryModifier {
        val combinedQueries = listOf(this, *others)
            .joinToString("") {
                "${it.query}{${it.styleModifier.styles.entries.joinToString(";") { (k, v) -> "$k:$v" }}}"
            }

        return MediaQueryModifier(
            "__multiple",
            ModifierImpl(mapOfCompat("__media_multiple" to combinedQueries))
        )
    }
}

/**
 * Extension function to apply a MediaQueryModifier to a Modifier.
 * @param mediaQueryModifier The media query modifier to apply
 * @return A combined modifier that includes both base styles and the media query
 */
fun Modifier.responsive(mediaQueryModifier: MediaQueryModifier): Modifier {
    return mediaQueryModifier.applyTo(this)
}

/**
 * Extension function to apply multiple MediaQueryModifiers to a Modifier.
 * @param mediaQueryModifiers The media query modifiers to apply
 * @return A combined modifier that includes both base styles and all media queries
 */
fun Modifier.responsive(vararg mediaQueryModifiers: MediaQueryModifier): Modifier {
    var result = this
    for (mqModifier in mediaQueryModifiers) {
        result = mqModifier.applyTo(result)
    }
    return result
} 
