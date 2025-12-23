package codes.yousef.summon.focus

import codes.yousef.summon.accessibility.KeyboardNavigation
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.LocalPlatformRenderer

@Composable
fun Focusable(
    modifier: Modifier = Modifier(),
    isFocused: Boolean = false,
    onFocusChanged: (Boolean) -> Unit = {},
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current

    // Create focus modifier based on focus state
    val focusModifier = when {
        isFocused -> with(KeyboardNavigation) { modifier.focusable(0).autoFocus() }
        else -> with(KeyboardNavigation) { modifier.focusable(0) }
    }

    // Render the content with the focus modifier
    renderer.renderBox(
        modifier = focusModifier,
        content = content
    )
}

/**
 * Wraps content to make it focusable and manage its focus state.
 */
@Composable
fun FocusableContainer(
    modifier: Modifier = Modifier(),
    isFocused: Boolean = false,
    onFocusChanged: (Boolean) -> Unit = {},
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current

    // Create focus modifier based on focus state
    val focusModifier = when {
        isFocused -> with(KeyboardNavigation) { modifier.focusable(0).autoFocus() }
        else -> with(KeyboardNavigation) { modifier.focusable(0) }
    }

    // Render the content with the focus modifier
    renderer.renderBox(
        modifier = focusModifier,
        content = content
    )
} 