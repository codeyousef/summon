package code.yousef.example.portfolio.models

import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime
import java.util.*

/**
 * User role enum
 */
enum class UserRole(val value: String, val displayName: String) {
    ADMIN("admin", "Administrator"),
    USER("user", "Regular User");
    
    companion object {
        fun fromValue(value: String): UserRole = 
            values().find { it.value == value } ?: USER
    }
}

/**
 * User model for authentication
 */
data class User(
    override val id: String = UUID.randomUUID().toString(),
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now(),
    override val status: ContentStatus = ContentStatus.PUBLISHED,
    val username: String,
    private val passwordHash: String,
    val email: String? = null,
    val fullName: String? = null,
    val role: UserRole = UserRole.USER
) : BaseModel {
    
    companion object {
        /**
         * Create a new user with a hashed password
         */
        fun create(
            username: String,
            password: String,
            email: String? = null,
            fullName: String? = null,
            role: UserRole = UserRole.USER
        ): User {
            val passwordHash = hashPassword(password)
            return User(
                username = username,
                passwordHash = passwordHash,
                email = email,
                fullName = fullName,
                role = role
            )
        }
        
        /**
         * Hash a password using BCrypt
         */
        fun hashPassword(password: String): String {
            return BCrypt.hashpw(password, BCrypt.gensalt())
        }
    }
    
    /**
     * Check if the provided password matches the stored hash
     */
    fun checkPassword(password: String): Boolean {
        return BCrypt.checkpw(password, passwordHash)
    }
    
    /**
     * Create a copy of this user with a new password
     */
    fun withNewPassword(password: String): User {
        return copy(
            passwordHash = hashPassword(password),
            updatedAt = LocalDateTime.now()
        )
    }
    
    /**
     * Create a safe version of the user without the password hash
     */
    fun toSafeUser(): SafeUser {
        return SafeUser(
            id = id,
            createdAt = createdAt,
            updatedAt = updatedAt,
            status = status,
            username = username,
            email = email,
            fullName = fullName,
            role = role
        )
    }
}

/**
 * Safe version of User without password hash for public APIs
 */
data class SafeUser(
    val id: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val status: ContentStatus,
    val username: String,
    val email: String? = null,
    val fullName: String? = null,
    val role: UserRole
)

/**
 * Repository interface for user operations
 */
interface UserRepository {
    suspend fun getUserByUsername(username: String): User?
    suspend fun createUser(user: User): User
    suspend fun updateUser(user: User): User
    suspend fun deleteUser(id: String): Boolean
    suspend fun getAllUsers(): List<SafeUser>
}