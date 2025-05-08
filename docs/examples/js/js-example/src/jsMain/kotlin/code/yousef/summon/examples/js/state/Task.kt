package code.yousef.summon.examples.js.state

/**
 * Represents a task in the task management application.
 *
 * @property id Unique identifier for the task
 * @property title The title or description of the task
 * @property completed Whether the task has been completed
 */
data class Task(
    val id: String,
    val title: String,
    val completed: Boolean = false
)