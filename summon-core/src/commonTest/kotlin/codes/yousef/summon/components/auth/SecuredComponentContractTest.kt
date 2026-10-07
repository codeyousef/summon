package codes.yousef.summon.components.auth

import codes.yousef.summon.security.Authentication
import codes.yousef.summon.security.Credentials
import codes.yousef.summon.security.Permission
import codes.yousef.summon.security.Principal
import codes.yousef.summon.security.Role
import codes.yousef.summon.security.SecurityContext
import codes.yousef.summon.security.UsernamePasswordCredentials
import codes.yousef.summon.security.annotations.RequiresAccess
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SecuredComponentContractTest {
    private val admin = Role("admin")
    private val editor = Role("editor")
    private val read = Permission("read")
    private val write = Permission("write")
    private val component = SecuredComponent()

    private fun auth(authenticated: Boolean = true): Authentication = object : Authentication {
        override val credentials: Credentials = UsernamePasswordCredentials("user", "secret")
        override val principal: Principal = object : Principal {
            override val id = "user"
            override val roles = setOf(admin, editor)
            override val permissions = setOf(read, write)
            override val attributes = emptyMap<String, Any>()
        }
        override val isAuthenticated = authenticated
        override val details = emptyMap<String, Any>()
    }

    private fun count(authentication: Authentication?, action: SecuredComponent.(() -> Unit) -> Unit): Int {
        var calls = 0
        SecurityContext.withAuthentication(authentication) { action.invoke(component) { calls++ } }
        return calls
    }

    @AfterTest
    fun clear() = SecurityContext.clearAuthentication()

    @Test
    fun individualSecurityConditionsRenderOnlyWhenSatisfied() {
        assertEquals(1, count(auth()) { authenticated(it) })
        assertEquals(0, count(null) { authenticated(it) })
        assertEquals(1, count(null) { unauthenticated(it) })
        assertEquals(0, count(auth()) { unauthenticated(it) })
        assertEquals(1, count(auth()) { withRole(admin, it) })
        assertEquals(0, count(auth()) { withRole(Role("missing"), it) })
        assertEquals(1, count(auth()) { withPermission(read, it) })
        assertEquals(0, count(auth()) { withPermission(Permission("missing"), it) })
    }

    @Test
    fun combinedRequirementsRequireEveryDeclaredRoleAndPermission() {
        fun evaluate(authentication: Authentication?, roles: Set<Role>, permissions: Set<Permission>, requireAuth: Boolean): Int {
            var calls = 0
            SecurityContext.withAuthentication(authentication) {
                component.withSecurityRequirements(requireAuth, roles, permissions) { calls++ }
            }
            return calls
        }
        assertEquals(1, evaluate(auth(), setOf(admin, editor), setOf(read, write), true))
        assertEquals(0, evaluate(auth(), setOf(admin, Role("missing")), setOf(read), true))
        assertEquals(0, evaluate(auth(), setOf(admin), setOf(read, Permission("missing")), true))
        assertEquals(0, evaluate(null, emptySet(), emptySet(), true))
        assertEquals(1, evaluate(null, emptySet(), emptySet(), false))

        var calls = 0
        SecurityContext.withAuthentication(auth()) {
            component.withSecurityRequirements(RequiresAccess(true, arrayOf("admin"), arrayOf("read"))) { calls++ }
        }
        SecurityContext.withAuthentication(null) {
            component.withSecurityRequirements(RequiresAccess(false, emptyArray(), emptyArray())) { calls++ }
        }
        assertEquals(2, calls)
    }
}
