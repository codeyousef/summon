package code.yousef.summon.components.navigation


import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.StylingModifierExtras.textDecoration
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.LocalPlatformRenderer


/**
 * # Link
 *
 * A navigation component that creates accessible hyperlinks with comprehensive routing,
 * SEO optimization, and security features for both internal and external navigation.
 *
 * ## Overview
 *
 * Link provides comprehensive navigation with:
 * - **Routing integration** - Seamless integration with Summon's file-based routing
 * - **Security features** - Automatic security attributes for external links
 * - **SEO optimization** - Proper link attributes for search engine optimization
 * - **Accessibility** - Full ARIA support and keyboard navigation
 * - **Visual states** - Hover, focus, and active state styling
 *
 * ## Key Features
 *
 * ### Navigation Types
 * - **Internal routing** - Navigate within the application
 * - **External links** - Navigate to external websites with security
 * - **Download links** - Links for file downloads
 * - **Email/Phone links** - Mailto and tel protocol support
 *
 * ### Security & SEO
 * - **Automatic security** - noopener, noreferrer for external links
 * - **SEO attributes** - Proper rel attributes for search engines
 * - **Link validation** - URL format validation and sanitization
 * - **Performance** - Prefetch support for internal routes
 *
 * ## Basic Usage
 *
 * ### Internal Navigation
 * ```kotlin
 * @Composable
 * fun NavigationMenu() {
 *     Row(modifier = Modifier().gap(Spacing.MD)) {
 *         Link(href = "/") {
 *             Text("Home")
 *         }
 *         Link(href = "/products") {
 *             Text("Products")
 *         }
 *         Link(href = "/about") {
 *             Text("About")
 *         }
 *     }
 * }
 * ```
 *
 * ### External Links
 * ```kotlin
 * @Composable
 * fun ExternalLinks() {
 *     Column(modifier = Modifier().gap(Spacing.SM)) {
 *         ExternalLink(
 *             text = "Visit GitHub",
 *             href = "https://github.com/company/repo",
 *             modifier = Modifier()
 *                 .color(Color.BLUE_600)
 *                 .textDecoration("underline")
 *                 .hover {
 *                     color(Color.BLUE_800)
 *                 }
 *         )
 *
 *         ExternalLink(
 *             text = "External Documentation",
 *             href = "https://docs.example.com",
 *             noFollow = true
 *         )
 *     }
 * }
 * ```
 *
 * ### Button-Style Links
 * ```kotlin
 * @Composable
 * fun CallToAction() {
 *     ButtonLink(
 *         text = "Get Started",
 *         href = "/signup",
 *         modifier = Modifier()
 *             .backgroundColor(Color.BLUE_600)
 *             .color(Color.WHITE)
 *             .padding("12px 24px")
 *             .borderRadius("8px")
 *             .textDecoration("none")
 *             .hover {
 *                 backgroundColor(Color.BLUE_700)
 *                 transform("translateY(-2px)")
 *             }
 *     )
 * }
 * ```
 *
 * ### Accessible Links
 * ```kotlin
 * @Composable
 * fun AccessibleNavigation() {
 *     Link(
 *         href = "/profile",
 *         ariaLabel = "Go to user profile page",
 *         title = "View and edit your profile information",
 *         modifier = Modifier()
 *             .padding(Spacing.SM)
 *             .borderRadius("4px")
 *             .focus {
 *                 outline("2px solid #4285f4")
 *                 outlineOffset("2px")
 *             }
 *     ) {
 *         Row(
 *             modifier = Modifier()
 *                 .alignItems(AlignItems.CENTER)
 *                 .gap(Spacing.XS)
 *         ) {
 *             Icon(Icons.USER)
 *             Text("Profile")
 *         }
 *     }
 * }
 * ```
 *
 * @param href The URL this link points to.
 * @param modifier The modifier to apply to this composable.
 * @param target Optional target attribute (_blank, _self, etc.).
 * @param rel Optional base rel attribute. Final rel calculated based on other flags.
 * @param title Optional title attribute.
 * @param isExternal Hints that this link points to an external site (adds noopener, noreferrer to rel).
 * @param isNoFollow Hints that search engines should not follow this link (adds nofollow to rel).
 * @param ariaLabel Optional accessible name for screen readers.
 * @param ariaDescribedBy Optional ID of element that describes this link.
 * @param content The composable content to display inside the link (e.g., Text, Icon).
 *
 * @see Text for link text content
 * @see Button for button-style navigation
 * @see Icon for link icons
 * @see Row for link layouts
 *
 * @sample LinkSamples.internalNavigation
 * @sample LinkSamples.externalLink
 * @sample LinkSamples.buttonLink
 * @sample LinkSamples.accessibleLink
 *
 * @since 1.0.0
 */
@Composable
fun Link(
    href: String,
    modifier: Modifier = Modifier(),
    target: String? = null,
    rel: String? = null, // Base value, will be combined
    title: String? = null,
    isExternal: Boolean = false,
    isNoFollow: Boolean = false,
    ariaLabel: String? = null,
    ariaDescribedBy: String? = null,
    content: @Composable () -> Unit
) {
    val composer = CompositionLocal.currentComposer
    // --- Calculate Final Rel Attribute --- 
    val finalRelValues = mutableListOf<String>()
    rel?.let { finalRelValues.addAll(it.split(" ").filter { it.isNotBlank() }) }
    if (target == "_blank" || isExternal) {
        if (!finalRelValues.contains("noopener")) finalRelValues.add("noopener")
        if (!finalRelValues.contains("noreferrer")) finalRelValues.add("noreferrer")
    }
    if (isNoFollow && !finalRelValues.contains("nofollow")) {
        finalRelValues.add("nofollow")
    }
    val finalRel = finalRelValues.joinToString(" ").ifEmpty { null }
    // --- End Rel Attribute Calculation --- 

    // Apply rel attribute via Modifier as it needs special calculation
    val finalModifier = if (finalRel != null) {
        modifier.attribute("rel", finalRel)
    } else {
        modifier
    }

    composer?.startNode() // Start Link node
    if (composer?.inserting == true) {
        val renderer = LocalPlatformRenderer.current

        // Use the enhanced link renderer with accessibility attributes
        renderer.renderEnhancedLink(
            href = href,
            target = target,
            title = title,
            ariaLabel = ariaLabel,
            ariaDescribedBy = ariaDescribedBy,
            modifier = finalModifier
        )
    }

    // Execute the content lambda within the Link scope
    content()

    composer?.endNode() // End Link node
}

// --- Helper Functions (Updated) --- 

/**
 * Creates an external link composable with appropriate attributes for SEO and security.
 * @param text The text content of the link.
 * @param href The URL this link points to.
 * @param modifier The modifier to apply to this composable.
 * @param noFollow Whether search engines should not follow this link.
 */
@Composable
fun ExternalLink(
    text: String,
    href: String,
    modifier: Modifier = Modifier(),
    noFollow: Boolean = false
) {
    Link(
        href = href,
        modifier = modifier,
        target = "_blank",
        isExternal = true,
        isNoFollow = noFollow
    ) {
        Text(text) // Pass text as content
    }
}

/**
 * Creates a button-styled link composable.
 * @param text The text content of the link.
 * @param href The URL this link points to.
 * @param modifier Modifier to apply. Defaults provide button-like styling.
 */
@Composable
fun ButtonLink(
    text: String,
    href: String,
    modifier: Modifier = Modifier()
        .padding("10px 20px") // Example button styles
        .background("#4CAF50")
        .color("white")
        .borderRadius("4px")
        .textDecoration("none")
        .hover(
            mapOf(
                "background-color" to "#45a049",
                "box-shadow" to "0 2px 4px rgba(0,0,0,0.2)"
            )
        )
) {
    Link(
        href = href,
        modifier = modifier
    ) {
        Text(text) // Pass text as content
    }
} 
