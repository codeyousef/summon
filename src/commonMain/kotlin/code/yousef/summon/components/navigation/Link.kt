package code.yousef.summon.components.navigation

import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.UIElement
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.* // Import necessary modifier extensions

/**
 * Internal data class holding parameters for the Link composable.
 */
internal data class LinkData(
    val text: String,
    val href: String,
    val modifier: Modifier,
    val target: String?,
    val rel: String?,
    val title: String?,
    val onClick: (() -> Unit)?,
    val isExternal: Boolean,
    val isNoFollow: Boolean,
    val ariaLabel: String?,
    val ariaDescribedBy: String?,
    val enabled: Boolean
) {
    /**
     * Gets all additional link-specific attributes that should be applied
     * beyond what is in the modifier.
     */
    internal fun getLinkAttributes(): Map<String, String> {
        val attributes = mutableMapOf<String, String>()
        target?.let { attributes["target"] = it }
        val relValues = mutableListOf<String>()
        rel?.let { relValues.addAll(it.split(" ")) }
        if (target == "_blank" || isExternal) {
            if (!relValues.contains("noopener")) relValues.add("noopener")
            if (!relValues.contains("noreferrer")) relValues.add("noreferrer")
        }
        if (isNoFollow && !relValues.contains("nofollow")) {
            relValues.add("nofollow")
        }
        if (relValues.isNotEmpty()) {
            attributes["rel"] = relValues.joinToString(" ")
        }
        title?.let { attributes["title"] = it }
        ariaLabel?.let { attributes["aria-label"] = it }
        ariaDescribedBy?.let { attributes["aria-describedby"] = it }
        return attributes
    }
}

/**
 * A composable that displays a hyperlink with SEO-friendly attributes.
 *
 * @param text The text content of the link.
 * @param href The URL this link points to.
 * @param modifier The modifier to apply to this composable.
 * @param target Optional target attribute (_blank, _self, etc.).
 * @param rel Optional rel attribute.
 * @param title Optional title attribute.
 * @param onClick Optional click handler.
 * @param isExternal Whether this link points to an external site.
 * @param isNoFollow Whether search engines should not follow this link.
 * @param ariaLabel Optional accessible name.
 * @param ariaDescribedBy Optional ID of element that describes this link.
 * @param enabled Whether the link is enabled.
 */
@Composable
fun Link(
    text: String,
    href: String,
    modifier: Modifier = Modifier(),
    target: String? = null,
    rel: String? = null,
    title: String? = null,
    onClick: (() -> Unit)? = null,
    isExternal: Boolean = false,
    isNoFollow: Boolean = false,
    ariaLabel: String? = null,
    ariaDescribedBy: String? = null,
    enabled: Boolean = true
) {
    val linkData = LinkData(
        text = text,
        href = href,
        modifier = modifier,
        target = target,
        rel = rel,
        title = title,
        onClick = onClick,
        isExternal = isExternal,
        isNoFollow = isNoFollow,
        ariaLabel = ariaLabel,
        ariaDescribedBy = ariaDescribedBy,
        enabled = enabled
    )

    println("Composable Link function called for href: $href")

    // TODO: Renderer needs to handle rendering the <a> tag with text and attributes.
    UIElement(
        factory = { linkData },
        update = { /* Update logic */ },
        content = { /* Link typically doesn't have children, text is part of LinkData */ }
    )
}

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
        text = text,
        href = href,
        modifier = modifier,
        target = "_blank",
        isExternal = true,
        isNoFollow = noFollow
    )
}

/**
 * Creates a button-styled link composable.
 * @param text The text content of the link.
 * @param href The URL this link points to.
 * @param modifier The modifier to apply to this composable.
 */
@Composable
fun ButtonLink(
    text: String,
    href: String,
    modifier: Modifier = Modifier()
        .padding("10px 20px")
        .background("#4CAF50")
        .color("white")
        .borderRadius("4px")
        .display("inline-block")
        .textDecoration("none")
        .hover(mapOf("background-color" to "#45a049"))
) {
    Link(
        text = text,
        href = href,
        modifier = modifier
    )
} 