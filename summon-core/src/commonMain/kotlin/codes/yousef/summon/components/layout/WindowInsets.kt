package codes.yousef.summon.components.layout

import codes.yousef.summon.runtime.CompositionLocal
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.paddingOf

/**
 * Describes the insets of the window, such as the status bar, navigation bar, etc.
 * Values are in pixels.
 */
data class WindowInsets(
    val left: Int = 0,
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0
) {
    companion object {
        val Zero = WindowInsets(0, 0, 0, 0)
    }
}

/**
 * CompositionLocal for the current [WindowInsets].
 * This allows components deep in the hierarchy to access the safe area insets.
 */
val LocalWindowInsets = CompositionLocal.compositionLocalOf(WindowInsets.Zero)

/**
 * Applies padding to the component matching the provided [WindowInsets].
 */
fun Modifier.windowInsetsPadding(insets: WindowInsets): Modifier {
    return this.paddingOf(
        left = "${insets.left}px",
        top = "${insets.top}px",
        right = "${insets.right}px",
        bottom = "${insets.bottom}px"
    )
}
