package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.EventModifiers.onClick
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.onClick
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Card component for grouped content with styling.
 *
 * @param modifier Modifier for applying styling and layout properties to the card
 * @param elevation Optional elevation for shadow effect (CSS box-shadow)
 * @param borderRadius Optional border radius for rounded corners
 * @param onClick Optional click handler for interactive cards (Not directly handled by Card itself, apply via modifier)
 * @param content Composable content to be contained within the card
 */
@Composable
fun Card(
    modifier: Modifier = Modifier(), // Use Modifier() constructor
    elevation: String = "2px", // Consider using type-safe units or number types
    borderRadius: String = "4px",
    onClick: (() -> Unit)? = null, // onClick needs to be applied via modifier
    content: @Composable () -> Unit
) {
    // Apply styling properties to the modifier
    var finalModifier = modifier
        .shadow("0", elevation, "8px", "rgba(0,0,0,0.1)") // Assuming shadow exists with this signature
        .borderRadius(borderRadius)
    
    // Apply onClick via modifier if provided
    onClick?.let { finalModifier = finalModifier.onClick(it) }
    
    // Get the correct PlatformRenderer
    val renderer = LocalPlatformRenderer.current // No cast needed
    
    // Call renderCard, wrapping content lambda
    renderer.renderCard(
        modifier = finalModifier,
        content = { // Wrap content lambda
            content()
        }
    )
} 