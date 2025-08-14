package code.yousef.summon.annotation

/**
 * Marks a function as composable, which means it can participate in the Summon composition system.
 *
 * Composable functions can only be called from within other @Composable functions.
 * They form the building blocks of the Summon UI hierarchy.
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