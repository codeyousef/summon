package code.yousef.example.portfolio.auth

import code.yousef.example.portfolio.models.User
import code.yousef.example.portfolio.models.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

/**
 * Authentication service for basic auth
 */
class AuthService : KoinComponent {
    
    private val userRepository: UserRepository by inject()
    
    /**
     * Authenticate a user with username and password
     * @return the authenticated user or null if authentication fails
     */
    suspend fun authenticate(username: String, password: String): User? {
        val user = userRepository.getUserByUsername(username)
        return if (user != null && user.checkPassword(password)) {
            user
        } else {
            null
        }
    }
    
    /**
     * Authenticate a user from basic auth header
     * @return the authenticated user or null if authentication fails
     */
    suspend fun authenticateFromBasicAuth(authHeader: String?): User? {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return null
        }
        
        try {
            val base64Credentials = authHeader.substring("Basic ".length).trim()
            val credentials = String(Base64.getDecoder().decode(base64Credentials)).split(":", limit = 2)
            
            if (credentials.size != 2) {
                return null
            }
            
            val username = credentials[0]
            val password = credentials[1]
            
            return authenticate(username, password)
        } catch (e: Exception) {
            return null
        }
    }
    
    /**
     * Check if a user is an admin
     */
    fun isAdmin(user: User?): Boolean {
        return user?.role?.value == "admin"
    }
}