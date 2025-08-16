package code.yousef.example.quarkus.model

import java.time.LocalDateTime
import java.util.*

/**
 * User session model
 */
data class Session(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val token: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime = LocalDateTime.now().plusHours(24),
    val lastAccessedAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Checks if this session is expired
     */
    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)
    
    /**
     * Updates the last accessed time
     */
    fun updateAccess(): Session = copy(lastAccessedAt = LocalDateTime.now())
    
    /**
     * Extends the session expiration by the given hours
     */
    fun extendExpiration(hours: Long = 24): Session = copy(
        expiresAt = LocalDateTime.now().plusHours(hours),
        lastAccessedAt = LocalDateTime.now()
    )
}

/**
 * Response model for API errors
 */
data class ErrorResponse(
    val error: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

/**
 * Response model for successful operations
 */
data class SuccessResponse(
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)