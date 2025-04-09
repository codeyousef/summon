package code.yousef.summon.focus

import code.yousef.summon.runtime.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.accessibility.KeyboardNavigation

@Composable
fun Focusable(
    modifier: Modifier = Modifier(),
    isFocused: Boolean = false,
    onFocusChanged: (Boolean) -> Unit = {},
    content: @Composable () -> Unit
) {
    val renderer = getPlatformRenderer()
    
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