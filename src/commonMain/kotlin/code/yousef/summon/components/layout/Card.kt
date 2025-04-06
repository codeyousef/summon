package code.yousef.summon.components.layout

import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier

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
) {
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