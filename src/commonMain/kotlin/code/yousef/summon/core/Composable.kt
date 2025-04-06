package code.yousef.summon.core

import kotlinx.html.TagConsumer

/**
 * Base interface for all UI elements in the Summon library.
 * Represents a piece of UI that can be composed.
 */
interface UIElement {
    /**
     * Composes this element into a receiver.
     * Each implementation defines how the element is rendered.
     */
    fun <T> compose(receiver: T): T
} 