package code.yousef.example.quarkus.service

import code.yousef.example.quarkus.model.*
import jakarta.enterprise.context.ApplicationScoped
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Authentication service for managing users and sessions
 */
@ApplicationScoped
class AuthService {
    
    private val users = ConcurrentHashMap<String, User>()
    private val sessions = ConcurrentHashMap<String, Session>()
    private val usersByUsername = ConcurrentHashMap<String, String>() // username -> userId
    
    init {
        // Create a demo user for testing
        val demoUser = User(
            username = "demo",
            passwordHash = hashPassword("demo123"),
            email = "demo@example.com",
            themePreference = ThemePreference.LIGHT,
            languagePreference = "en"
        )
        users[demoUser.id] = demoUser
        usersByUsername[demoUser.username] = demoUser.id
    }
    
    /**
     * Registers a new user
     */
    fun register(request: RegisterRequest): Result<PublicUser> {
        if (usersByUsername.containsKey(request.username)) {
            return Result.failure(IllegalArgumentException("Username already exists"))
        }
        
        if (request.username.isBlank() || request.password.length < 6) {
            return Result.failure(IllegalArgumentException("Invalid username or password"))
        }
        
        val user = User(
            username = request.username,
            passwordHash = hashPassword(request.password),
            email = request.email,
            themePreference = ThemePreference.LIGHT,
            languagePreference = "en"
        )
        
        users[user.id] = user
        usersByUsername[user.username] = user.id
        
        return Result.success(user.toPublic())
    }
    
    /**
     * Authenticates a user and creates a session
     */
    fun login(request: LoginRequest): Result<LoginResponse> {
        val userId = usersByUsername[request.username]
            ?: return Result.failure(IllegalArgumentException("Invalid username or password"))
        
        val user = users[userId]
            ?: return Result.failure(IllegalArgumentException("User not found"))
        
        if (!verifyPassword(request.password, user.passwordHash)) {
            return Result.failure(IllegalArgumentException("Invalid username or password"))
        }
        
        // Update last login time
        val updatedUser = user.copy(lastLoginAt = LocalDateTime.now())
        users[userId] = updatedUser
        
        // Create session
        val session = Session(
            userId = user.id,
            token = generateSessionToken()
        )
        sessions[session.token] = session
        
        return Result.success(LoginResponse(
            user = updatedUser.toPublic(),
            sessionToken = session.token
        ))
    }
    
    /**
     * Validates a session token and returns the user
     */
    fun validateSession(token: String): Result<PublicUser> {
        val session = sessions[token]
            ?: return Result.failure(IllegalArgumentException("Invalid session"))
        
        if (session.isExpired()) {
            sessions.remove(token)
            return Result.failure(IllegalArgumentException("Session expired"))
        }
        
        val user = users[session.userId]
            ?: return Result.failure(IllegalArgumentException("User not found"))
        
        // Update session access time
        sessions[token] = session.updateAccess()
        
        return Result.success(user.toPublic())
    }
    
    /**
     * Logs out a user by invalidating their session
     */
    fun logout(token: String): Result<Unit> {
        sessions.remove(token)
        return Result.success(Unit)
    }
    
    /**
     * Gets a user by ID
     */
    fun getUserById(userId: String): PublicUser? {
        return users[userId]?.toPublic()
    }
    
    /**
     * Updates user settings
     */
    fun updateUserSettings(userId: String, request: UpdateSettingsRequest): Result<PublicUser> {
        val user = users[userId]
            ?: return Result.failure(IllegalArgumentException("User not found"))
        
        val updatedUser = user.copy(
            themePreference = request.themePreference?.let { ThemePreference.fromString(it) } ?: user.themePreference,
            languagePreference = request.languagePreference ?: user.languagePreference
        )
        
        users[userId] = updatedUser
        return Result.success(updatedUser.toPublic())
    }
    
    /**
     * Hashes a password using SHA-256 (in production, use bcrypt or similar)
     */
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Verifies a password against a hash
     */
    private fun verifyPassword(password: String, hash: String): Boolean {
        return hashPassword(password) == hash
    }
    
    /**
     * Generates a random session token
     */
    private fun generateSessionToken(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
}