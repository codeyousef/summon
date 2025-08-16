package code.yousef.example.springboot

/**
 * Represents a user in the system.
 *
 * @property id The unique identifier for the user
 * @property name The full name of the user
 * @property email The email address of the user
 * @property role The role assigned to the user (e.g., admin, user)
 * @property active Whether the user account is active
 */
data class User(
    val id: Long = 0,
    val name: String,
    val email: String,
    val role: String,
    val active: Boolean = true
)