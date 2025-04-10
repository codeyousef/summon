package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.PlatformRenderer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.MigratedPlatformRenderer

/**
 * Card component for grouped content with styling.
 *
 * @param modifier Modifier for applying styling and layout properties to the card
 * @param elevation Optional elevation for shadow effect (CSS box-shadow)
 * @param borderRadius Optional border radius for rounded corners
 * @param onClick Optional click handler for interactive cards
 * @param content Composable content to be contained within the card
 */
@Composable
fun Card(
    modifier: Modifier = Modifier.create(),
    elevation: String = "2px",
    borderRadius: String = "4px",
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    // Apply styling properties to the modifier
    val finalModifier = modifier
        .shadow("0", elevation, "8px", "rgba(0,0,0,0.1)")
        .borderRadius(borderRadius)
    
    val renderer = LocalPlatformRenderer.current as MigratedPlatformRenderer
    renderer.renderCard(finalModifier, content)
} 