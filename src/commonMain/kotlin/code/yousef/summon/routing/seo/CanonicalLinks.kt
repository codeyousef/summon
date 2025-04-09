package code.yousef.summon.routing.seo

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.SideEffect
import code.yousef.summon.runtime.getPlatformRenderer

/**
 * CanonicalLinks component for managing canonical URLs
 * This helps search engines understand the primary version of a page when
 * multiple URLs might serve the same or similar content
 */
@Composable
fun CanonicalLinks(
    url: String,
    alternateLanguages: Map<String, String> = emptyMap(),
    ampUrl: String? = null
) {
    val composer = CompositionLocal.currentComposer
    
    // This is a head-only component, so we need to use a SideEffect to manipulate the head
    SideEffect {
        // Get the platform renderer to add head elements
        val renderer = getPlatformRenderer()
        
        // Add canonical URL link
        renderer.addHeadElement("<link rel=\"canonical\" href=\"$url\">")
        
        // Add alternate language links
        alternateLanguages.forEach { (lang, href) ->
            renderer.addHeadElement("<link rel=\"alternate\" hreflang=\"$lang\" href=\"$href\">")
        }
        
        // Add AMP link if available
        ampUrl?.let {
            renderer.addHeadElement("<link rel=\"amphtml\" href=\"$it\">")
        }
    }
}

/**
 * Create a simple canonical link
 */
@Composable
fun SimpleCanonicalLink(url: String) {
    CanonicalLinks(url)
}

/**
 * Create a canonical link with language alternates
 */
@Composable
fun CanonicalLinksWithLanguages(
    defaultUrl: String,
    defaultLanguage: String,
    alternates: Map<String, String>
) {
    val allLanguages = mutableMapOf<String, String>()
    allLanguages[defaultLanguage] = defaultUrl
    allLanguages.putAll(alternates)

    CanonicalLinks(
        url = defaultUrl,
        alternateLanguages = allLanguages
    )
}

/**
 * Create a canonical link with AMP version
 */
@Composable
fun CanonicalLinksWithAmp(
    url: String,
    ampUrl: String
) {
    CanonicalLinks(
        url = url,
        ampUrl = ampUrl
    )
}

/**
 * Composable function to add a canonical link tag to the document head.
 * Helps prevent duplicate content issues for SEO.
 *
 * @param href The canonical URL for the current page content.
 */
@Composable
fun CanonicalLink(href: String) {
    val renderer = getPlatformRenderer()
    
    SideEffect {
        renderer.addHeadElement("<link rel=\"canonical\" href=\"$href\">")
    }

    // Renders no UI.
} 
