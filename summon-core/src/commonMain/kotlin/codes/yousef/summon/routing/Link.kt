package codes.yousef.summon.routing

import codes.yousef.summon.modifier.onClick
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.components.navigation.Link as InternalLink

/**
 * Creates a navigation link in the application.
 * Unlike regular anchor elements, Link integrates with the router to provide client-side navigation.
 *
 * @param text Text to display in the link
 * @param href Path to navigate to
 * @param target Target window for the link (e.g., "_self", "_blank")
 * @param modifier Modifier for styling
 * @param onClick Optional click handler that can intercept navigation
 */
@Composable
fun Link(
    text: String,
    href: String,
    target: String = "_self",
    modifier: Modifier = Modifier(),
    onClick: (() -> Unit)? = null
) {
    val router = LocalRouter ?: RouterContext.current

    InternalLink(
        href = href,
        target = target,
        modifier = modifier.onClick {
            // Call user's click handler if provided
            onClick?.invoke()

            // If we have a router and target is current window, use router navigation
            if (router != null && target == "_self") {
                router.navigate(href)
            }
        }
    ) {
        codes.yousef.summon.components.display.Text(text)
    }
} 
