package codes.yousef.summon.core

/**
 * Base interface for all UI components in the Summon library.
 * Similar to Jetpack Compose's @Composable functions.
 *
 * @deprecated Use the @Composable annotation from code.yousef.summon.annotation instead.
 * This interface is being phased out in favor of the annotation-based approach.
 */
@Deprecated(
    message = "Use the @Composable annotation from code.yousef.summon.annotation instead",
    replaceWith = ReplaceWith(
        expression = "@Composable fun",
        imports = ["code.yousef.summon.annotation.Composable"]
    )
)
interface Composable {
    /**
     * Applies this component to a receiver.
     * Each implementation will define how exactly the component is rendered.
     */
    fun <T> compose(receiver: T): T
} 
