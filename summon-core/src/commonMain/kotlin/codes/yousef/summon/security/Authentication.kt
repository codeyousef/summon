package codes.yousef.summon.security

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

 * @property username The username value.
 * @property password The password value.
 */
data class UsernamePasswordCredentials(
    val username: String,
    val password: String
) : Credentials

/**
 * JWT token credentials

 * @property token The token value.
 */
data class JwtCredentials(
    val token: String
) : Credentials

/**
 * OAuth2 credentials

 * @property accessToken The access token value.
 * @property refreshToken The refresh token value.
 * @property tokenType The token type value.
 * @property expiresIn The expires in value.
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
    /**
     * Represents success.
     *
     * @property authentication The authentication value.
     */
    data class Success(val authentication: Authentication) : AuthenticationResult()
    /**
     * Represents failure.
     *
     * @property error The error value.
     */
    data class Failure(val error: Throwable) : AuthenticationResult()
}
