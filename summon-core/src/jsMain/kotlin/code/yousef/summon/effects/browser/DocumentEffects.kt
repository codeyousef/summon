package code.yousef.summon.effects.browser

import code.yousef.summon.effects.CompositionScope
import code.yousef.summon.effects.effectWithDeps
import code.yousef.summon.runtime.Composable
import kotlinx.browser.document
import org.w3c.dom.HTMLLinkElement
import org.w3c.dom.HTMLMetaElement

/**
 * Effect for updating document title
 *
 * @param title The title to set for the document
 */
@Composable
fun CompositionScope.useDocumentTitle(title: String) {
    effectWithDeps(title) {
        // Store the original title for restoration if needed
        val originalTitle = document.title

        // Update the document title
        document.title = title

        // Optional: Return a cleanup function to restore the original title
        // if this component unmounts (uncomment if needed)
        // {
        //     document.title = originalTitle
        // }
    }
}

/**
 * Effect for managing meta tags in the document head
 *
 * @param name The name attribute of the meta tag
 * @param content The content attribute of the meta tag
 */
@Composable
fun CompositionScope.useMetaTag(name: String, content: String) {
    effectWithDeps(name, content) {
        // Check if the meta tag already exists
        var metaTag = document.querySelector("meta[name='$name']") as? HTMLMetaElement

        if (metaTag == null) {
            // Create a new meta tag if it doesn't exist
            metaTag = document.createElement("meta") as HTMLMetaElement
            metaTag.name = name
            document.head?.appendChild(metaTag)
        }

        // Set/update the content
        metaTag.content = content

        // Return cleanup function to remove the meta tag when component unmounts
        {
            // Only remove the tag if we created it
            if (document.querySelector("meta[name='$name']") != null) {
                document.head?.removeChild(metaTag)
            }
        }
    }
}

/**
 * Effect for managing Open Graph meta tags (useful for social media sharing)
 *
 * @param property The og:property name
 * @param content The content value
 */
@Composable
fun CompositionScope.useOpenGraphTag(property: String, content: String) {
    effectWithDeps(property, content) {
        // The full property name with the "og:" prefix
        val fullProperty = "og:$property"

        // Check if the meta tag already exists
        var metaTag = document.querySelector("meta[property='$fullProperty']") as? HTMLMetaElement

        if (metaTag == null) {
            // Create a new meta tag if it doesn't exist
            metaTag = document.createElement("meta") as HTMLMetaElement
            // For properties like og:title, we need to use setAttribute as it's not a standard property
            metaTag.asDynamic().property = fullProperty
            document.head?.appendChild(metaTag)
        }

        // Set/update the content
        metaTag.content = content

        // Return cleanup function
        {
            // Only remove the tag if we created it
            if (document.querySelector("meta[property='$fullProperty']") != null) {
                document.head?.removeChild(metaTag)
            }
        }
    }
}

/**
 * Effect for adding a favicon link to the document
 *
 * @param href The URL of the favicon
 * @param type The MIME type of the favicon, defaults to "image/x-icon"
 */
@Composable
fun CompositionScope.useFavicon(href: String, type: String = "image/x-icon") {
    effectWithDeps(href, type) {
        // Check if a favicon link already exists
        var linkElement = document.querySelector("link[rel='icon']") as? HTMLLinkElement

        if (linkElement == null) {
            // Create a new link element if it doesn't exist
            linkElement = document.createElement("link") as HTMLLinkElement
            linkElement.rel = "icon"
            document.head?.appendChild(linkElement)
        }

        // Set/update the href and type
        linkElement.href = href
        linkElement.type = type

        // Return cleanup function
        {
            // Only remove if we created it
            if (document.querySelector("link[rel='icon'][href='$href']") != null) {
                document.head?.removeChild(linkElement)
            }
        }
    }
} 
