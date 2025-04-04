package com.summon

import kotlinx.html.TagConsumer

/**
 * A composable that displays a hyperlink with SEO-friendly attributes.
 *
 * @param text The text content of the link
 * @param href The URL this link points to
 * @param modifier The modifier to apply to this composable
 * @param target Optional target attribute to specify where to open the link (_blank, _self, etc.)
 * @param rel Optional rel attribute to define the relationship between current and linked document
 * @param title Optional title attribute that provides additional information
 * @param onClick Optional click handler for the link
 * @param isExternal Whether this link points to an external site (adds appropriate rel attribute)
 * @param isNoFollow Whether search engines should not follow this link (adds nofollow to rel)
 * @param ariaLabel Optional accessible name for screen readers
 * @param ariaDescribedBy Optional ID of element that describes this link for accessibility
 */
data class Link(
    val text: String,
    val href: String,
    val modifier: Modifier = Modifier(),
    val target: String? = null,
    val rel: String? = null,
    val title: String? = null,
    val onClick: (() -> Unit)? = null,
    val isExternal: Boolean = false,
    val isNoFollow: Boolean = false,
    val ariaLabel: String? = null,
    val ariaDescribedBy: String? = null
) : Composable {
    /**
     * Renders this Link composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderLink(this, receiver as TagConsumer<T>)
        }
        return receiver
    }

    /**
     * Gets all additional link-specific attributes that should be applied
     * beyond what is in the modifier.
     */
    internal fun getLinkAttributes(): Map<String, String> {
        val attributes = mutableMapOf<String, String>()

        // Add target attribute if specified
        target?.let { attributes["target"] = it }

        // Build rel attribute
        val relValues = mutableListOf<String>()
        rel?.let { relValues.addAll(it.split(" ")) }

        // Add noopener and noreferrer for security if target is _blank
        if (target == "_blank" || isExternal) {
            if (!relValues.contains("noopener")) relValues.add("noopener")
            if (!relValues.contains("noreferrer")) relValues.add("noreferrer")
        }

        // Add nofollow if specified
        if (isNoFollow && !relValues.contains("nofollow")) {
            relValues.add("nofollow")
        }

        // Set the rel attribute if there are values
        if (relValues.isNotEmpty()) {
            attributes["rel"] = relValues.joinToString(" ")
        }

        // Add title attribute if specified
        title?.let { attributes["title"] = it }

        // Add accessibility attributes
        ariaLabel?.let { attributes["aria-label"] = it }
        ariaDescribedBy?.let { attributes["aria-describedby"] = it }

        return attributes
    }
}

/**
 * Creates an external link with appropriate attributes for SEO and security.
 * @param text The text content of the link
 * @param href The URL this link points to
 * @param modifier The modifier to apply to this composable
 * @param noFollow Whether search engines should not follow this link
 */
fun externalLink(
    text: String,
    href: String,
    modifier: Modifier = Modifier(),
    noFollow: Boolean = false
): Link = Link(
    text = text,
    href = href,
    modifier = modifier,
    target = "_blank",
    isExternal = true,
    isNoFollow = noFollow
)

/**
 * Creates a button-styled link (looks like a button but navigates like a link).
 * @param text The text content of the link
 * @param href The URL this link points to
 * @param modifier The modifier to apply to this composable
 */
fun buttonLink(
    text: String,
    href: String,
    modifier: Modifier = Modifier().padding("10px 20px").background("#4CAF50").color("white")
        .borderRadius("4px").display("inline-block").textDecoration("none")
        .hover(mapOf("background-color" to "#45a049"))
): Link = Link(
    text = text,
    href = href,
    modifier = modifier
) 