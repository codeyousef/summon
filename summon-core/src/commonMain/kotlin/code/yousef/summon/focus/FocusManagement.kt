package code.yousef.summon.focus

import code.yousef.summon.accessibility.KeyboardNavigation
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer
import kotlinx.html.FlowContent

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