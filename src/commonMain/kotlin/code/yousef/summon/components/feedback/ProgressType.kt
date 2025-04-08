package code.yousef.summon.components.feedback

/**
 * Types of progress indicators.
 */
enum class ProgressType {
    /**
     * A linear progress bar that fills horizontally.
     * Typically used for processes with known progress percentage.
     */
    LINEAR,
    
    /**
     * A circular progress indicator that fills in a circular motion.
     * Can be used for both determinate and indeterminate progress.
     */
    CIRCULAR,
    
    /**
     * An indeterminate progress indicator that animates continuously.
     * Used when progress percentage is unknown.
     */
    INDETERMINATE
} 