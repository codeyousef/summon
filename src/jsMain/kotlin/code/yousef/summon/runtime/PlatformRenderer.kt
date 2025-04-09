package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier

/**
 * JS implementation of the PlatformRenderer interface.
 */
actual interface PlatformRenderer {
    /**
     * Render a composable to the specified consumer
     *
     * @param composable The composable to render
     * @param consumer The consumer to render to
     */
    actual fun <T> renderComposable(composable: @Composable () -> Unit, consumer: T)

    /**
     * Render a text component
     */
    actual fun renderText(
        value: String, 
        modifier: Modifier
    )

    /**
     * Render a button component
     */
    actual fun renderButton(
        onClick: () -> Unit, 
        enabled: Boolean,
        modifier: Modifier
    )

    /**
     * Render an image component
     */
    actual fun renderImage(
        src: String,
        alt: String,
        modifier: Modifier
    )

    /**
     * Render an icon component
     */
    actual fun renderIcon(
        name: String,
        modifier: Modifier
    )

    /**
     * Render a row layout
     */
    actual fun renderRow(
        modifier: Modifier
    )

    /**
     * Render a column layout
     */
    actual fun renderColumn(
        modifier: Modifier
    )

    /**
     * Render a spacer
     */
    actual fun renderSpacer(
        modifier: Modifier
    )

    /**
     * Render a box container
     */
    actual fun renderBox(
        modifier: Modifier
    )

    /**
     * Render a card component
     */
    actual fun renderCard(
        modifier: Modifier
    )

    /**
     * Render an animated visibility component
     */
    actual fun renderAnimatedVisibility(
        visible: Boolean,
        modifier: Modifier
    )

    /**
     * Render a hyperlink component
     * 
     * @param href The URL this link points to
     * @param modifier The modifier to apply to this link
     */
    actual fun renderLink(
        href: String,
        modifier: Modifier
    )
} 
