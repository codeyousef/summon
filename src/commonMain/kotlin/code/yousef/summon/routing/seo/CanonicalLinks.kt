package code.yousef.summon.routing.seo

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import kotlinx.html.HEAD
import kotlinx.html.link
import code.yousef.summon.runtime.SideEffect

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
        // TODO: Implement platform-specific head manipulation
        // For now, we'll just print what we would do
        println("CanonicalLinks SideEffect: Setting canonical URL to $url")
        
        // In a real implementation, we would use the platform renderer to add these elements to the head
        // val renderer = getPlatformRenderer()
        // renderer.addHeadElement("<link rel=\"canonical\" href=\"$url\">")
        
        // Add alternate language links
        alternateLanguages.forEach { (lang, href) ->
            println("CanonicalLinks SideEffect: Adding alternate language link for $lang: $href")
            // renderer.addHeadElement("<link rel=\"alternate\" hreflang=\"$lang\" href=\"$href\">")
        }
        
        // Add AMP link if available
        ampUrl?.let {
            println("CanonicalLinks SideEffect: Adding AMP link: $it")
            // renderer.addHeadElement("<link rel=\"amphtml\" href=\"$it\">")
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
    val composer = CompositionLocal.currentComposer

    SideEffect {
        println("CanonicalLink SideEffect: Setting canonical URL to $href")
        // TODO: Implement platform-specific head manipulation.
        // PlatformRendererProvider.code.yousef.summon.runtime.PlatformRendererProvider.getPlatformRenderer().addHeadElement("<link rel=\"canonical\" href=\"$href\">")
    }

    // Renders no UI.
} 
