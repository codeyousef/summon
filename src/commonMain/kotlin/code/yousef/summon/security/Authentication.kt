package code.yousef.summon.security

/**
 * Represents an authentication request with credentials.
 */
interface Authentication {
    /**
     * The credentials used for authentication
     */
    val credentials: Credentials

    /**
     * The principal after successful authentication
     */
    val principal: Principal?

    /**
     * Whether the authentication is authenticated
     */
    val isAuthenticated: Boolean

    /**
     * Additional authentication details
     */
    val details: Map<String, Any>
}

/**
 * Base interface for all credential types
 */
interface Credentials

/**
 * Username and password credentials
 */
data class UsernamePasswordCredentials(
    val username: String,
    val password: String
) : Credentials

/**
 * JWT token credentials
 */
data class JwtCredentials(
    val token: String
) : Credentials

/**
 * OAuth2 credentials
 */
data class OAuth2Credentials(
    val accessToken: String,
    val refreshToken: String? = null,
    val tokenType: String = "Bearer",
    val expiresIn: Long? = null
) : Credentials

/**
 * Result of an authentication attempt
 */
sealed class AuthenticationResult {
    data class Success(val authentication: Authentication) : AuthenticationResult()
    data class Failure(val error: Throwable) : AuthenticationResult()
} 