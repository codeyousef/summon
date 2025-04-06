package code.yousef.summon.animation

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.transform

/**
 * Keys commonly used for keyboard navigation in web applications.
 */
object KeyboardKeys {
    const val TAB = "Tab"
    const val ENTER = "Enter"
    const val SPACE = " "
    const val ESCAPE = "Escape"
    const val ARROW_UP = "ArrowUp"
    const val ARROW_DOWN = "ArrowDown"
    const val ARROW_LEFT = "ArrowLeft"
    const val ARROW_RIGHT = "ArrowRight"
    const val HOME = "Home"
    const val END = "End"
    const val PAGE_UP = "PageUp"
    const val PAGE_DOWN = "PageDown"
}

/**
 * Creates a button with animation effects when clicked.
 * TODO: Implement actual animation via Modifier or state.
 *
 * @param onClick Callback to invoke when the button is clicked
 * @param modifier Base modifier to apply
 * @param enabled Controls the enabled state of the button
 * @param variant The visual style variant of the button
 * @param content The composable content to display inside the button
 */
@Composable
fun AnimatedButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = variant,
        content = content
    )
}

/**
 * Creates a text component that animates in when first displayed.
 *
 * @param text The text content to display
 * @param modifier Base modifier to apply
 * @param enterTransition The entrance transition type
 * @param duration Animation duration in milliseconds
 */
@Composable
fun AnimatedText(
    text: String,
    modifier: Modifier = Modifier(),
    enterTransition: EnterTransition = EnterTransition.FADE_IN,
    duration: Int = 500
) {
    // Direct implementation without remember or animatedVisibility
    val isVisible = true // Default to visible
    
    // Apply animation styles to the text modifier
    val animationModifier = when (enterTransition) {
        EnterTransition.FADE_IN -> 
            modifier.style("transition", "opacity ${duration}ms ease-in")
                   .opacity(if (isVisible) 1.0f else 0.0f)
        EnterTransition.SLIDE_IN -> 
            modifier.style("transition", "transform ${duration}ms ease-in")
                   .transform(if (isVisible) "translateX(0)" else "translateX(-100%)")
        EnterTransition.EXPAND_IN -> 
            modifier.style("transition", "transform ${duration}ms ease-in")
                   .transform(if (isVisible) "scale(1)" else "scale(0.8)")
        EnterTransition.ZOOM_IN -> 
            modifier.style("transition", "transform ${duration}ms ease-in")
                   .transform(if (isVisible) "scale(1)" else "scale(0.5)")
    }
    
    Text(text = text, modifier = animationModifier)
}

/**
 * Creates a pulse animation effect for any component.
 * TODO: Needs refactoring based on updated animation system (InfiniteTransitionComponent?).
 *
 * @param content The content to animate
 * @param pulseInterval The interval between pulses in milliseconds
 * @param minScale The minimum scale during the pulse
 * @param maxScale The maximum scale during the pulse
 * @param modifier Base modifier to apply
 * @return A composable with pulse animation
 */
/*
@Composable
fun PulseAnimation(
    pulseInterval: Int = 1000,
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    content()
}
*/

/**
 * Creates a staggered animation where each child appears with a delay.
 *
 * @param items List of items to display with staggered animation
 * @param itemContent Function to create content for each item
 * @param staggerDelay Delay between each item's animation in milliseconds
 * @param enterTransition The animation to use for each item
 * @param modifier Base modifier to apply
 */
@Composable
fun <T> StaggeredColumn(
    items: List<T>,
    modifier: Modifier = Modifier(),
    staggerDelay: Int = 100,
    enterTransition: EnterTransition = EnterTransition.FADE_IN,
    itemContent: @Composable (T) -> Unit
) {
    Column(modifier = modifier) {
        items.forEachIndexed { index, item ->
            // Calculate index-based delay
            val delay = index * staggerDelay
            
            // Apply animation styles based on the transition type
            val itemModifier = when (enterTransition) {
                EnterTransition.FADE_IN -> 
                    Modifier().style("transition", "opacity 300ms ease-in ${delay}ms")
                              .opacity(1.0f) // Start visible since we can't track state
                
                EnterTransition.SLIDE_IN -> 
                    Modifier().style("transition", "transform 300ms ease-in ${delay}ms")
                              .transform("translateX(0)") // Start at final position
                
                EnterTransition.EXPAND_IN -> 
                    Modifier().style("transition", "transform 300ms ease-in ${delay}ms")
                              .transform("scale(1)") // Start at final scale
                
                EnterTransition.ZOOM_IN -> 
                    Modifier().style("transition", "transform 300ms ease-in ${delay}ms")
                              .transform("scale(1)") // Start at final scale
            }
            
            // Wrap the content with the modified item
            // Since we can't track state anymore, items will always be visible with applied transitions
            itemContent(item)
        }
    }
} 