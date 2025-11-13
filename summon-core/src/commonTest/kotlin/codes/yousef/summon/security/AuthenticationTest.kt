package codes.yousef.summon.security

import kotlin.test.*

class AuthenticationTest {

    @Test
    fun testSimpleAuthentication() {
        // Create credentials
        val credentials = UsernamePasswordCredentials("testuser", "password123")

        // Create a simple authentication with the credentials
        val authentication = SimpleAuthentication(
            credentials = credentials,
            principal = null,
            isAuthenticated = false,
            details = emptyMap()
        )

        // Verify the authentication properties
        assertEquals(credentials, authentication.credentials)
        assertNull(authentication.principal)
        assertFalse(authentication.isAuthenticated)
        assertTrue(authentication.details.isEmpty())
    }

    @Test
    fun testSimpleAuthenticationWithPrincipal() {
        // Create credentials
        val credentials = UsernamePasswordCredentials("testuser", "password123")

        // Create a principal
        val principal = object : Principal {
            override val id: String = "user-123"
            override val roles: Set<Role> = setOf(Role("user"))
            override val permissions: Set<Permission> = setOf(Permission("read:own"))
            override val attributes: Map<String, Any> = mapOf("name" to "Test User")
        }

        // Create a simple authentication with the credentials and principal
        val authentication = SimpleAuthentication(
            credentials = credentials,
            principal = principal,
            isAuthenticated = true,
            details = mapOf("loginTime" to "2023-01-01T00:00:00Z")
        )

        // Verify the authentication properties
        assertEquals(credentials, authentication.credentials)
        assertEquals(principal, authentication.principal)
        assertTrue(authentication.isAuthenticated)
        assertEquals(1, authentication.details.size)
        assertEquals("2023-01-01T00:00:00Z", authentication.details["loginTime"])
    }

    @Test
    fun testUsernamePasswordCredentials() {
        val credentials = UsernamePasswordCredentials("testuser", "password123")

        assertEquals("testuser", credentials.username)
        assertEquals("password123", credentials.password)
    }

    @Test
    fun testJwtCredentials() {
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val credentials = JwtCredentials(token)

        assertEquals(token, credentials.token)
    }

    @Test
    fun testOAuth2Credentials() {
        val accessToken = "access-token-123"
        val refreshToken = "refresh-token-456"
        val tokenType = "Bearer"
        val expiresIn = 3600L

        val credentials = OAuth2Credentials(
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenType = tokenType,
            expiresIn = expiresIn
        )

        assertEquals(accessToken, credentials.accessToken)
        assertEquals(refreshToken, credentials.refreshToken)
        assertEquals(tokenType, credentials.tokenType)
        assertEquals(expiresIn, credentials.expiresIn)
    }

    @Test
    fun testAuthenticationResultSuccess() {
        // Create credentials
        val credentials = UsernamePasswordCredentials("testuser", "password123")

        // Create a simple authentication
        val authentication = SimpleAuthentication(
            credentials = credentials,
            isAuthenticated = true
        )

        // Create a success result
        val result = AuthenticationResult.Success(authentication)

        // Verify the result
        assertEquals(authentication, result.authentication)
    }

    @Test
    fun testAuthenticationResultFailure() {
        // Create an error
        val error = IllegalArgumentException("Invalid credentials")

        // Create a failure result
        val result = AuthenticationResult.Failure(error)

        // Verify the result
        assertEquals(error, result.error)
    }
}
