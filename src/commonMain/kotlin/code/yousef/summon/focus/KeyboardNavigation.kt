package code.yousef.summon.focus

import code.yousef.summon.runtime.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.remember
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.accessibility.KeyboardNavigation

/**
 * State holder for keyboard navigation
 */
class NavigationState(
    initialFocusIndex: Int = 0
) {
    val currentFocusIndex = mutableStateOf(initialFocusIndex)
}

/**
 * Remember a NavigationState instance
 */
@Composable
fun rememberNavigationState(initialFocusIndex: Int = 0): NavigationState {
    return remember { NavigationState(initialFocusIndex) }
}

/**
 * A composable that provides keyboard navigation capabilities to its content
 */
@Composable
fun KeyboardNavigable(
    modifier: Modifier = Modifier(),
    navigationState: NavigationState = rememberNavigationState(),
    content: @Composable () -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    
    // Apply keyboard navigation modifier
    val navigationModifier = with(KeyboardNavigation) {
        modifier.focusable(navigationState.currentFocusIndex.value)
            .keyboardHandlers(
                mapOf(
                    "ArrowUp" to { navigationState.currentFocusIndex.value-- },
                    "ArrowDown" to { navigationState.currentFocusIndex.value++ }
                )
            )
    }
    
    // Render the content with keyboard navigation
    renderer.renderBox(
        modifier = navigationModifier,
        content = content
    )
} 