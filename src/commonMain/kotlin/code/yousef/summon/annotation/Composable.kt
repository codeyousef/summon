package code.yousef.summon.annotation

/**
 * Denotes a composable function. Composable functions are the fundamental building blocks of Summon UI.
 * They describe a piece of UI by emitting nodes into the composition tree.
 *
 * This annotation informs the Summon compiler plugin and runtime that the function:
 * 1. Describes a section of UI.
 * 2. Can only be called from other Composable functions.
 * 3. Executes within the context of a Composition.
 * 4. Can manage state and side effects using Summon's state management APIs (e.g., remember, mutableStateOf).
 */
@Retention(AnnotationRetention.BINARY) // Changed from RUNTIME to BINARY to avoid JS reflection issues
@Target(
    AnnotationTarget.FUNCTION, 
    AnnotationTarget.PROPERTY_GETTER, // Allow on property getters if needed
    AnnotationTarget.TYPE, // Allow on types (interfaces/classes) if needed for certain patterns
    AnnotationTarget.TYPE_PARAMETER // For composable generic functions
)
annotation class Composable 