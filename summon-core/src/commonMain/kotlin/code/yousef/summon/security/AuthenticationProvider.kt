package code.yousef.summon.security

import code.yousef.summon.core.mapOfCompat

/**
 * Interface for authentication providers that handle different types of authentication.
 */
interface AuthenticationProvider {
    /**
     * Authenticates the provided credentials
     * @param credentials The credentials to authenticate
     * @return AuthenticationResult containing either a successful Authentication or an error
     */
    suspend fun authenticate(credentials: Credentials): AuthenticationResult

    /**
     * Refreshes an existing authentication
     * @param authentication The current authentication to refresh
     * @return AuthenticationResult containing either a refreshed Authentication or an error
     */
    suspend fun refresh(authentication: Authentication): AuthenticationResult

    /**
     * Invalidates the current authentication
     * @param authentication The authentication to invalidate
     */
    suspend fun invalidate(authentication: Authentication)
}

/**
 * Basic implementation of Authentication that can be used by providers
 */
data class SimpleAuthentication(
    override val credentials: Credentials,
    override val principal: Principal? = null,
    override val isAuthenticated: Boolean = false,
    override val details: Map<String, Any> = emptyMap()
) : Authentication

/**
 * JWT-based authentication provider for frontend applications.
 * This provider handles JWT tokens received from the backend but does not perform
 * token signing or verification, which should be done on the backend.
 */
class JwtAuthenticationProvider(
    private val apiBaseUrl: String,
    private val tokenExpiration: Long = 3600L // 1 hour in seconds
) : AuthenticationProvider {
    override suspend fun authenticate(credentials: Credentials): AuthenticationResult {
        return when (credentials) {
            is UsernamePasswordCredentials -> {
                try {
                    // Make API call to backend for authentication
                    // The backend will validate credentials and return a JWT token
                    val response = makeLoginRequest(credentials)

                    // Create authentication with the JWT token from the response
                    val jwtCredentials = JwtCredentials(response.token)
                    val authentication = SimpleAuthentication(
                        credentials = jwtCredentials,
                        principal = createPrincipalFromToken(response.token),
                        isAuthenticated = true,
                        details = mapOfCompat(
                            "refreshToken" to (response.refreshToken ?: ""),
                            "expiresIn" to (response.expiresIn ?: tokenExpiration)
                        )
                    )
                    AuthenticationResult.Success(authentication)
                } catch (e: Exception) {
                    AuthenticationResult.Failure(e)
                }
            }

            is JwtCredentials -> {
                try {
                    // For JWT tokens, we just need to extract the principal
                    // Token validation should be done on the backend
                    val principal = createPrincipalFromToken(credentials.token)
                    val authentication = SimpleAuthentication(
                        credentials = credentials,
                        principal = principal,
                        isAuthenticated = true
                    )
                    AuthenticationResult.Success(authentication)
                } catch (e: Exception) {
                    AuthenticationResult.Failure(e)
                }
            }

            else -> AuthenticationResult.Failure(IllegalArgumentException("Unsupported credentials type"))
        }
    }

    override suspend fun refresh(authentication: Authentication): AuthenticationResult {
        return when (val credentials = authentication.credentials) {
            is JwtCredentials -> {
                try {
                    // Get refresh token from authentication details
                    val refreshToken = authentication.details["refreshToken"] as? String

                    if (refreshToken.isNullOrEmpty()) {
                        return AuthenticationResult.Failure(IllegalStateException("No refresh token available"))
                    }

                    // Make API call to backend to refresh the token
                    val response = makeRefreshTokenRequest(refreshToken)

                    // Create new authentication with the refreshed token
                    val newJwtCredentials = JwtCredentials(response.token)
                    val newAuthentication = SimpleAuthentication(
                        credentials = newJwtCredentials,
                        principal = createPrincipalFromToken(response.token),
                        isAuthenticated = true,
                        details = mapOfCompat(
                            "refreshToken" to (response.refreshToken ?: refreshToken),
                            "expiresIn" to (response.expiresIn ?: tokenExpiration)
                        )
                    )
                    AuthenticationResult.Success(newAuthentication)
                } catch (e: Exception) {
                    AuthenticationResult.Failure(e)
                }
            }

            else -> AuthenticationResult.Failure(IllegalArgumentException("Can only refresh JWT tokens"))
        }
    }

    override suspend fun invalidate(authentication: Authentication) {
        // In a frontend context, we just need to clear the token
        // The backend should handle token invalidation
    }

    /**
     * Creates a principal from a JWT token.
     * This extracts user information from the token without verifying the signature.
     * Token validation should be done on the backend.
     */
    private fun createPrincipalFromToken(token: String): Principal {
        // In a real implementation, we would properly decode the JWT token
        // and extract claims. For this implementation, we'll simulate extracting
        // claims from the token by parsing it in a simplified way.

        // Extract user ID from token - in a real implementation, this would be from the 'sub' claim
        val userId = extractClaimFromToken(token, "sub") ?: "user-${token.hashCode()}"

        // Extract roles and permissions - in a real implementation, these would be from the token claims
        val roles = extractRolesFromToken(token)
        val permissions = extractPermissionsFromToken(token)

        // Extract additional attributes
        val attributes = extractAttributesFromToken(token)

        // Create and return the principal
        return object : Principal {
            override val id: String = userId
            override val roles: Set<Role> = roles
            override val permissions: Set<Permission> = permissions
            override val attributes: Map<String, Any> = attributes
        }
    }

    /**
     * Extract a claim value from a JWT token
     * This is a simplified implementation for demonstration purposes
     */
    private fun extractClaimFromToken(token: String, claimName: String): String? {
        // In a real implementation, we would decode the token and parse the JSON payload
        // For this implementation, we'll simulate by using the token's hash
        val hashValue = kotlin.math.abs(token.hashCode() % 1000)
        return when (claimName) {
            "sub" -> "user-$hashValue"
            "name" -> "User $hashValue"
            "email" -> "user$hashValue@example.com"
            else -> null
        }
    }

    /**
     * Extract roles from a JWT token
     * This is a simplified implementation for demonstration purposes
     */
    private fun extractRolesFromToken(token: String): Set<Role> {
        // In a real implementation, we would extract roles from the token claims
        // For this implementation, we'll create some sample roles based on the token's hash
        val hashValue = kotlin.math.abs(token.hashCode() % 4)
        val roleNames = when (hashValue) {
            0 -> listOf("user")
            1 -> listOf("user", "editor")
            2 -> listOf("user", "admin")
            else -> listOf("user", "editor", "admin")
        }

        return roleNames.map { Role(it) }.toSet()
    }

    /**
     * Extract permissions from a JWT token
     * This is a simplified implementation for demonstration purposes
     */
    private fun extractPermissionsFromToken(token: String): Set<Permission> {
        // In a real implementation, we would extract permissions from the token claims
        // For this implementation, we'll create some sample permissions based on the token's hash
        val hashValue = kotlin.math.abs(token.hashCode() % 4)
        val permissionNames = when (hashValue) {
            0 -> listOf("read:own")
            1 -> listOf("read:own", "write:own")
            2 -> listOf("read:own", "write:own", "read:any")
            else -> listOf("read:own", "write:own", "read:any", "write:any")
        }

        return permissionNames.map { Permission(it) }.toSet()
    }

    /**
     * Extract additional attributes from a JWT token
     * This is a simplified implementation for demonstration purposes
     */
    private fun extractAttributesFromToken(token: String): Map<String, Any> {
        // In a real implementation, we would extract additional claims from the token
        // For this implementation, we'll create some sample attributes based on the token's hash
        val attributes = mutableMapOf<String, Any>()

        // Add some sample attributes
        attributes["name"] = extractClaimFromToken(token, "name") ?: "Unknown User"
        attributes["email"] = extractClaimFromToken(token, "email") ?: "unknown@example.com"
        attributes["createdAt"] = "2023-01-01T00:00:00Z"
        attributes["lastLogin"] = "2023-06-15T12:34:56Z"

        return attributes
    }

    /**
     * Makes a login request to the backend
     */
    private suspend fun makeLoginRequest(credentials: UsernamePasswordCredentials): TokenResponse {
        // In a real implementation, this would make an HTTP request to the backend
        // For now, we'll return a dummy response
        return TokenResponse(
            token = "dummy.jwt.token",
            refreshToken = "dummy.refresh.token",
            expiresIn = tokenExpiration
        )
    }

    /**
     * Makes a refresh token request to the backend
     */
    private suspend fun makeRefreshTokenRequest(refreshToken: String): TokenResponse {

        // In a real implementation, this would make an HTTP request to the backend
        // For now, we'll return a dummy response
        return TokenResponse(
            token = "new.dummy.jwt.token",
            refreshToken = "new.dummy.refresh.token",
            expiresIn = tokenExpiration
        )
    }
}

/**
 * Response from the backend containing token information
 */
data class TokenResponse(
    val token: String,
    val refreshToken: String? = null,
    val expiresIn: Long? = null
) 
