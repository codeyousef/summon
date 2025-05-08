package code.yousef.summon.examples.js.state

/**
 * Enum representing the different filter options for tasks.
 */
enum class TaskFilter {
    /**
     * Show all tasks regardless of completion status.
     */
    ALL,
    
    /**
     * Show only active (incomplete) tasks.
     */
    ACTIVE,
    
    /**
     * Show only completed tasks.
     */
    COMPLETED
}