package code.yousef.summon.runtime

/**
 * Global access point for the Recomposer instance.
 * This allows components to access the recomposer from anywhere in the codebase.
 */
object RecomposerHolder {
    /**
     * The global Recomposer instance responsible for managing recomposition.
     */
    val recomposer: Recomposer = Recomposer()
} 