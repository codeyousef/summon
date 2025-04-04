package com.summon

import kotlinx.html.TagConsumer

/**
 * Platform-specific renderer interface that serves as a common abstraction
 * for rendering UI components across platforms.
 * 
 * Following section 8 of the guide: "Exploring Common Intermediate Representations or Abstraction Layers"
 */
interface PlatformRenderer {
    /**
     * Renders a Text component to the appropriate platform output.
     */
    fun <T> renderText(text: Text, consumer: TagConsumer<T>): T

    /**
     * Renders a Button component to the appropriate platform output.
     */
    fun <T> renderButton(button: Button, consumer: TagConsumer<T>): T
    
    /**
     * Renders a Row component to the appropriate platform output.
     */
    fun <T> renderRow(row: Row, consumer: TagConsumer<T>): T
    
    /**
     * Renders a Column component to the appropriate platform output.
     */
    fun <T> renderColumn(column: Column, consumer: TagConsumer<T>): T
    
    /**
     * Renders a Spacer component to the appropriate platform output.
     */
    fun <T> renderSpacer(spacer: Spacer, consumer: TagConsumer<T>): T
}

/**
 * Gets the platform-specific renderer.
 * This will be implemented differently on each platform.
 */
expect fun getPlatformRenderer(): PlatformRenderer 