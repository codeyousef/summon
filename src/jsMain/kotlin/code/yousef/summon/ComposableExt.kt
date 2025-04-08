package code.yousef.summon


import code.yousef.summon.annotation.Composable
import kotlinx.html.TagConsumer

/**
 * Helper function to simplify composing with TagConsumer.
 * This is used for fluent code in JS platform.
 * 
 * NOTE: This function is a temporary bridge between the old interface-based composition
 * and the new annotation-based composition model. It will be replaced in the future.
 */
fun <T> composeToConsumer(render: @Composable () -> Unit, consumer: TagConsumer<T>): TagConsumer<T> {
    // Temporary implementation
    // In the future, this should integrate with the Composer runtime 
    // and properly render the composable function to the consumer
    return consumer
} 
