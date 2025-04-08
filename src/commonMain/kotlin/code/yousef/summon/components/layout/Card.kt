package code.yousef.summon.components.layout

import code.yousef.summon.ClickableComponent
import code.yousef.summon.core.Composable
import code.yousef.summon.LayoutComponent
import code.yousef.summon.core.getPlatformRenderer
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer

/**
 * Card component for grouped content with styling.
 *
 * @param content List of composable elements to be contained within the card
 * @param modifier Modifier for applying styling and layout properties to the card
 * @param elevation Optional elevation for shadow effect (CSS box-shadow)
 * @param borderRadius Optional border radius for rounded corners
 * @param onClick Optional click handler for interactive cards
 */
data class Card(
    val content: List<Composable>,
    val modifier: Modifier = Modifier(),
    val elevation: String = "2px",
    val borderRadius: String = "4px",
    val onClick: (() -> Unit)? = null
) : Composable, LayoutComponent, ClickableComponent {
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return getPlatformRenderer().renderCard(this, receiver as TagConsumer<T>)
        }
        return receiver
    }

    /**
     * Convenience constructor for creating a card with a single child element
     */
    constructor(
        content: Composable,
        modifier: Modifier = Modifier(),
        elevation: String = "2px",
        borderRadius: String = "4px",
        onClick: (() -> Unit)? = null
    ) : this(listOf(content), modifier, elevation, borderRadius, onClick)
} 