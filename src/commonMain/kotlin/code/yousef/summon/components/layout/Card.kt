package code.yousef.summon.components.layout

// Remove old imports
// import code.yousef.summon.ClickableComponent
// import code.yousef.summon.core.Composable
// import code.yousef.summon.LayoutComponent
// import code.yousef.summon.core.getPlatformRenderer // Use Provider for now
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
// import kotlinx.html.TagConsumer

// Add new runtime imports
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.core.getPlatformRenderer

/**
 * A composable that displays content in a card with elevation, rounded corners, and optional border.
 *
 * @param modifier Modifier to be applied to the card
 * @param content The content to be displayed inside the card
 */
@Composable
fun Card(
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    val renderer = getPlatformRenderer()
    renderer.renderCard(modifier)
    content()
}

// The old Card class and its constructors/methods are removed. 