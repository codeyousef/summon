package code.yousef.summon.runtime

/**
 * Marks a function as composable, which means it can participate in the composition.
 * Composable functions can only be called from other composable functions or a composition root.
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER
)
annotation class Composable 