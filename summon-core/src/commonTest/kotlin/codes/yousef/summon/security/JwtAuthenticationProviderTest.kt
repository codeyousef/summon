package codes.yousef.summon.security

// Import the standard runTest
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class JwtAuthenticationProviderTest {

    private lateinit var provider: JwtAuthenticationProvider

    @BeforeTest
    fun setup() {
        // Create a provider with a test API base URL
        provider = JwtAuthenticationProvider("https://api.example.com", 3600L)
    }

    // Use runTest directly
    @Test
    fun testAuthenticateWithUsernamePassword() = runTest {
        // Create username/password credentials
        val credentials = UsernamePasswordCredentials("testuser", "password123")

        // Authenticate
        val result = provider.authenticate(credentials)

        // Verify the result
        assertTrue(result is AuthenticationResult.Success)
        val authentication = (result as AuthenticationResult.Success).authentication

        // Verify the authentication
        assertTrue(authentication.isAuthenticated)
        assertNotNull(authentication.principal)
        assertTrue(authentication.credentials is JwtCredentials)

        // Verify the principal
        val principal = authentication.principal!!
        assertNotNull(principal.id)
        assertTrue(principal.roles.isNotEmpty())
        assertTrue(principal.permissions.isNotEmpty())
        assertTrue(principal.attributes.isNotEmpty())

        // Verify the details
        assertTrue(authentication.details.containsKey("refreshToken"))
        assertTrue(authentication.details.containsKey("expiresIn"))
    }

    // Use runTest directly
    @Test
    fun testAuthenticateWithJwtToken() = runTest {
        // Create JWT credentials
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val credentials = JwtCredentials(token)

        // Authenticate
        val result = provider.authenticate(credentials)

        // Verify the result
        assertTrue(result is AuthenticationResult.Success)
        val authentication = (result as AuthenticationResult.Success).authentication

        // Verify the authentication
        assertTrue(authentication.isAuthenticated)
        assertNotNull(authentication.principal)
        assertEquals(credentials, authentication.credentials)

        // Verify the principal
        val principal = authentication.principal!!
        assertNotNull(principal.id)
        assertTrue(principal.roles.isNotEmpty())
        assertTrue(principal.permissions.isNotEmpty())
        assertTrue(principal.attributes.isNotEmpty())
    }

    // Use runTest directly
    @Test
    fun testAuthenticateWithUnsupportedCredentials() = runTest {
        // Create OAuth2 credentials (not directly supported by the provider)
        val credentials = OAuth2Credentials("access-token-123")

        // Authenticate
        val result = provider.authenticate(credentials)

        // Verify the result
        assertTrue(result is AuthenticationResult.Failure)
        val error = (result as AuthenticationResult.Failure).error
        assertTrue(error is IllegalArgumentException)
        assertEquals("Unsupported credentials type", error.message)
    }

    // Use runTest directly
    @Test
    fun testRefreshWithJwtToken() = runTest {
        // Create JWT credentials with refresh token in details
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val credentials = JwtCredentials(token)
        val authentication = SimpleAuthentication(
            credentials = credentials,
            principal = null,
            isAuthenticated = true,
            details = mapOf("refreshToken" to "refresh-token-123")
        )

        // Refresh
        val result = provider.refresh(authentication)

        // Verify the result
        assertTrue(result is AuthenticationResult.Success)
        val newAuthentication = (result as AuthenticationResult.Success).authentication

        // Verify the new authentication
        assertTrue(newAuthentication.isAuthenticated)
        assertNotNull(newAuthentication.principal)
        assertTrue(newAuthentication.credentials is JwtCredentials)
        assertNotEquals(token, (newAuthentication.credentials as JwtCredentials).token)

        // Verify the details
        assertTrue(newAuthentication.details.containsKey("refreshToken"))
        assertTrue(newAuthentication.details.containsKey("expiresIn"))
    }

    // Use runTest directly
    @Test
    fun testRefreshWithoutRefreshToken() = runTest {
        // Create JWT credentials without refresh token in details
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val credentials = JwtCredentials(token)
        val authentication = SimpleAuthentication(
            credentials = credentials,
            principal = null,
            isAuthenticated = true,
            details = emptyMap()
        )

        // Refresh
        val result = provider.refresh(authentication)

        // Verify the result
        assertTrue(result is AuthenticationResult.Failure)
        val error = (result as AuthenticationResult.Failure).error
        assertTrue(error is IllegalStateException)
        assertEquals("No refresh token available", error.message)
    }

    // Use runTest directly
    @Test
    fun testRefreshWithUnsupportedCredentials() = runTest {
        // Create authentication with unsupported credentials
        val credentials = UsernamePasswordCredentials("testuser", "password123")
        val authentication = SimpleAuthentication(
            credentials = credentials,
            principal = null,
            isAuthenticated = true,
            details = mapOf("refreshToken" to "refresh-token-123")
        )

        // Refresh
        val result = provider.refresh(authentication)

        // Verify the result
        assertTrue(result is AuthenticationResult.Failure)
        val error = (result as AuthenticationResult.Failure).error
        assertTrue(error is IllegalArgumentException)
        assertEquals("Can only refresh JWT tokens", error.message)
    }

    // Use runTest directly
    @Test
    fun testInvalidate() = runTest {
        // Create JWT credentials
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val credentials = JwtCredentials(token)
        val authentication = SimpleAuthentication(
            credentials = credentials,
            principal = null,
            isAuthenticated = true
        )

        // Invalidate - this is a no-op in the current implementation
        provider.invalidate(authentication)

        // No assertions needed as the method is a no-op
    }
}

// Remove the custom runTest helper function
// fun runTest(block: suspend () -> Unit) { ... }
