package com.summon

/**
 * Base interface for all UI components in the Summon library.
 * Similar to Jetpack Compose's @Composable functions.
 */
interface Composable {
    /**
     * Applies this component to a TagConsumer.
     * Each implementation will define how exactly the component is rendered.
     */
    fun <T> compose(receiver: T): T
} 