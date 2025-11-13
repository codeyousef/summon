package codes.yousef.summon.focus

import codes.yousef.summon.accessibility.KeyboardNavigation
import codes.yousef.summon.core.FlowContentCompat
import codes.yousef.summon.core.mapOfCompat
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.mutableStateOf
import codes.yousef.summon.runtime.remember

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
    content: @Composable FlowContentCompat.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current

    // Apply keyboard navigation modifier
    val navigationModifier = with(KeyboardNavigation) {
        modifier.focusable(navigationState.currentFocusIndex.value)
            .keyboardHandlers(
                mapOfCompat(
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