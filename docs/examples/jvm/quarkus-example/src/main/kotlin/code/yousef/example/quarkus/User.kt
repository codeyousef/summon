package code.yousef.example.quarkus

/**
 * Represents a user in the system.
 *
 * @property id The unique identifier of the user
 * @property name The name of the user
 * @property email The email address of the user
 * @property role The role of the user in the system
 */
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String
) 