package codes.yousef.summon.security.service

import codes.yousef.summon.security.*
import codes.yousef.summon.security.annotations.RequiresAccess
import codes.yousef.summon.security.annotations.RequiresAuthentication
import codes.yousef.summon.security.annotations.RequiresPermissions
import codes.yousef.summon.security.annotations.RequiresRoles
import codes.yousef.summon.security.config.SecurityConfig
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SecurityServiceContractTest {
    private val admin = Role("admin")
    private val editor = Role("editor")
    private val read = Permission("read")
    private val write = Permission("write")
    private val credentials = UsernamePasswordCredentials("user", "secret")

    private fun authentication(id: String): Authentication = SimpleAuthentication(
        credentials,
        object : Principal {
            override val id = id
            override val roles = setOf(admin, editor)
            override val permissions = setOf(read, write)
            override val attributes = emptyMap<String, Any>()
        },
        true
    )

    @AfterTest
    fun clear() = SecurityContext.clearAuthentication()

    @Test
    fun successfulLoginRefreshAndLogoutPersistContext() = runTest {
        val first = authentication("first")
        val refreshed = authentication("refreshed")
        var invalidated: Authentication? = null
        val provider = object : AuthenticationProvider {
            override suspend fun authenticate(credentials: Credentials) = AuthenticationResult.Success(first)
            override suspend fun refresh(authentication: Authentication) = AuthenticationResult.Success(refreshed)
            override suspend fun invalidate(authentication: Authentication) { invalidated = authentication }
        }
        val service = SecurityService(SecurityConfig(provider))
        assertSame(first, (service.login(credentials) as AuthenticationResult.Success).authentication)
        assertSame(first, service.getCurrentAuthentication())
        assertEquals("first", service.getCurrentPrincipal()?.id)
        assertTrue(service.isAuthenticated())
        assertTrue(service.hasRole(admin))
        assertTrue(service.hasPermission(read))

        assertSame(refreshed, (service.refreshAuthentication() as AuthenticationResult.Success).authentication)
        assertSame(refreshed, service.getCurrentAuthentication())
        service.logout()
        assertSame(refreshed, invalidated)
        assertNull(service.getCurrentAuthentication())
        service.logout()
        assertNull(service.refreshAuthentication())
    }

    @Test
    fun failedProviderResultsDoNotReplaceCurrentAuthentication() = runTest {
        val existing = authentication("existing")
        val failure = AuthenticationResult.Failure(IllegalStateException("denied"))
        var loginResult: AuthenticationResult = AuthenticationResult.Success(existing)
        val provider = object : AuthenticationProvider {
            override suspend fun authenticate(credentials: Credentials) = loginResult
            override suspend fun refresh(authentication: Authentication) = failure
            override suspend fun invalidate(authentication: Authentication) = Unit
        }
        val service = SecurityService(SecurityConfig(provider))
        service.login(credentials)
        loginResult = failure
        assertSame(failure, service.login(credentials))
        assertSame(existing, service.getCurrentAuthentication())
        assertSame(failure, service.refreshAuthentication())
        assertSame(existing, service.getCurrentAuthentication())
    }

    @Test
    fun requirementChecksUseAllDeclaredCapabilitiesAndAnnotationAuthenticationFlag() = runTest {
        val current = authentication("user")
        val provider = object : AuthenticationProvider {
            override suspend fun authenticate(credentials: Credentials) = AuthenticationResult.Success(current)
            override suspend fun refresh(authentication: Authentication) = error("unused")
            override suspend fun invalidate(authentication: Authentication) = Unit
        }
        val service = SecurityService(SecurityConfig(provider))
        service.login(credentials)
        assertTrue(service.checkSecurityRequirements(true, setOf(admin, editor), setOf(read, write)))
        assertFalse(service.checkSecurityRequirements(true, setOf(admin, Role("missing")), setOf(read)))
        assertFalse(service.checkSecurityRequirements(true, setOf(admin), setOf(read, Permission("missing"))))
        assertTrue(service.checkSecurityRequirements(RequiresRoles("admin", "editor")))
        assertTrue(service.checkSecurityRequirements(RequiresPermissions("read", "write")))
        assertTrue(service.checkSecurityRequirements(RequiresAuthentication()))
        assertTrue(service.checkSecurityRequirements(RequiresAccess(true, arrayOf("admin"), arrayOf("read"))))

        SecurityContext.clearAuthentication()
        assertFalse(service.checkSecurityRequirements(RequiresAuthentication()))
        assertTrue(service.checkSecurityRequirements(RequiresAccess(false, emptyArray(), emptyArray())))
        assertEquals("value", service.withAuthentication(authentication("temporary")) { "value" })
        assertNull(service.getCurrentAuthentication())
    }
}
