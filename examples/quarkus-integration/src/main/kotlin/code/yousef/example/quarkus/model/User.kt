package code.yousef.example.quarkus.model

import java.time.LocalDateTime
import java.util.*

/**
 * User model
 */
data class User(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val passwordHash: String,
    val email: String? = null,
    val themePreference: ThemePreference = ThemePreference.LIGHT,
    val languagePreference: String = "en",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastLoginAt: LocalDateTime? = null
) {
    /**
     * Creates a public version of this user without sensitive data
     */
    fun toPublic(): PublicUser = PublicUser(
        id = id,
        username = username,
        email = email,
        themePreference = themePreference,
        languagePreference = languagePreference,
        createdAt = createdAt,
        lastLoginAt = lastLoginAt
    )
}

/**
 * Public user data (without password hash)
 */
data class PublicUser(
    val id: String,
    val username: String,
    val email: String? = null,
    val themePreference: ThemePreference,
    val languagePreference: String,
    val createdAt: LocalDateTime,
    val lastLoginAt: LocalDateTime? = null
)

/**
 * Theme preferences
 */
enum class ThemePreference(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    AUTO("Auto");
    
    companion object {
        fun fromString(value: String): ThemePreference = 
            values().find { it.name.equals(value, ignoreCase = true) } ?: LIGHT
    }
}

/**
 * Login request model
 */
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * Registration request model
 */
data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String? = null
)

/**
 * Login response model
 */
data class LoginResponse(
    val user: PublicUser,
    val sessionToken: String
)

/**
 * Settings update request
 */
data class UpdateSettingsRequest(
    val themePreference: String? = null,
    val languagePreference: String? = null
)