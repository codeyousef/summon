package code.yousef.summon.examples.js.models

/**
 * Represents a user in our application
 */
data class User(
    val username: String,
    val displayName: String,
    val email: String? = null,
    val avatar: String? = null
)