package code.yousef.summon

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.PlatformRenderer

actual class JsPlatformRenderer actual constructor() : PlatformRenderer {
    actual override fun <T> renderComposable(composable: @Composable () -> Unit, consumer: T) {
        // Basic implementation - just a stub for now
    }

    actual override fun renderText(value: String, modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderButton(onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderImage(src: String, alt: String, modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderIcon(name: String, modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderRow(modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderColumn(modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderSpacer(modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderBox(modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderCard(modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }
}
