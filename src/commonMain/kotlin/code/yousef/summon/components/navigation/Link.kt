package code.yousef.summon.components.navigation


import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.modifier.attribute
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.getPlatformRenderer


/**
 * A composable that displays a hyperlink.
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
        val renderer = getPlatformRenderer()
        
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
    // TODO: Proper hover handling via modifier/platform?
    // .hover(mapOf("background-color" to "#45a049"))
) {
    Link(
        href = href,
        modifier = modifier
    ) {
        Text(text) // Pass text as content
    }
} 
