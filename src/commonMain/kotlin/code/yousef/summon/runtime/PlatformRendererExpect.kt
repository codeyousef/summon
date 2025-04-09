package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier

/**
 * Platform-specific renderer interface with expect/actual declarations.
 * This is part of the migration to proper multiplatform support.
 */
expect interface PlatformRenderer {
    /**
     * Render a composable to the specified consumer
     *
     * @param composable The composable to render
     * @param consumer The consumer to render to
     */
    fun <T> renderComposable(composable: @Composable () -> Unit, consumer: T)

    /**
     * Render a text component
     */
    fun renderText(value: String, modifier: Modifier)

    /**
     * Render a button component
     */
    fun renderButton(onClick: () -> Unit, enabled: Boolean, modifier: Modifier)

    /**
     * Render an image component
     */
    fun renderImage(src: String, alt: String, modifier: Modifier)

    /**
     * Render an icon component
     */
    fun renderIcon(name: String, modifier: Modifier)

    /**
     * Render a row layout
     */
    fun renderRow(modifier: Modifier)

    /**
     * Render a column layout
     */
    fun renderColumn(modifier: Modifier)

    /**
     * Render a spacer
     */
    fun renderSpacer(modifier: Modifier)

    /**
     * Render a box container
     */
    fun renderBox(modifier: Modifier)

    /**
     * Render a card component
     */
    fun renderCard(modifier: Modifier)

    /**
     * Render an animated visibility component
     */
    fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier)
} 