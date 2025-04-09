package code.yousef.summon

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.PlatformRenderer

/**
 * Platform-specific implementation of the renderer.
 * This class must be implemented by each platform.
 */
expect class JsPlatformRenderer() : PlatformRenderer {
    override fun <T> renderComposable(
        composable: @Composable (() -> Unit),
        consumer: T
    )

    override fun renderText(value: String, modifier: Modifier)
    override fun renderButton(
        onClick: () -> Unit,
        enabled: Boolean,
        modifier: Modifier
    )

    override fun renderImage(src: String, alt: String, modifier: Modifier)
    override fun renderIcon(name: String, modifier: Modifier)
    override fun renderRow(modifier: Modifier)
    override fun renderColumn(modifier: Modifier)
    override fun renderSpacer(modifier: Modifier)
    override fun renderBox(modifier: Modifier)
    override fun renderCard(modifier: Modifier)
    override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier)
    override fun renderLink(href: String, modifier: Modifier)
}
