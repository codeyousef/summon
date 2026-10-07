package codes.yousef.summon.security.guards

import codes.yousef.summon.routing.GuardResult
import codes.yousef.summon.routing.Route
import codes.yousef.summon.routing.RouteGuard
import codes.yousef.summon.routing.RouteParams
import codes.yousef.summon.security.Authentication
import codes.yousef.summon.security.Credentials
import codes.yousef.summon.security.Permission
import codes.yousef.summon.security.Principal
import codes.yousef.summon.security.Role
import codes.yousef.summon.security.SecurityContext
import codes.yousef.summon.security.UsernamePasswordCredentials
import codes.yousef.summon.security.annotations.RequiresAccess
import codes.yousef.summon.security.annotations.RequiresAuthentication
import codes.yousef.summon.security.annotations.RequiresPermissions
import codes.yousef.summon.security.annotations.RequiresRoles
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SecurityGuardsContractTest {
    private val route = Route("/private") { { } }
    private val params = RouteParams(emptyMap())
    private val admin = Role("admin")
    private val editor = Role("editor")
    private val read = Permission("read")
    private val write = Permission("write")

    private fun authentication(
        authenticated: Boolean = true,
        roles: Set<Role> = setOf(admin),
        permissions: Set<Permission> = setOf(read)
    ): Authentication = object : Authentication {
        override val credentials: Credentials = UsernamePasswordCredentials("user", "secret")
        override val principal: Principal = object : Principal {
            override val id = "user"
            override val roles = roles
            override val permissions = permissions
            override val attributes = emptyMap<String, Any>()
        }
        override val isAuthenticated = authenticated
        override val details = emptyMap<String, Any>()
    }

    private fun evaluate(guard: RouteGuard, authentication: Authentication?): GuardResult =
        SecurityContext.withAuthentication(authentication) { guard.canActivate(route, params) }

    @AfterTest
    fun clear() = SecurityContext.clearAuthentication()

    @Test
    fun directGuardsCoverAuthenticationAnyAndAllSemantics() {
        val auth = authentication()
        assertEquals(GuardResult.Redirect("/login"), evaluate(AuthenticationGuard(), null))
        assertEquals(GuardResult.Redirect("/login"), evaluate(AuthenticationGuard(), authentication(authenticated = false)))
        assertEquals(GuardResult.Allow, evaluate(AuthenticationGuard(), auth))
        assertEquals(GuardResult.Allow, evaluate(RoleGuard(admin), auth))
        assertEquals(GuardResult.Deny, evaluate(RoleGuard(editor), auth))
        assertEquals(GuardResult.Allow, evaluate(RolesGuard(setOf(admin, editor)), auth))
        assertEquals(GuardResult.Deny, evaluate(RolesGuard(setOf(admin, editor), requireAll = true), auth))
        assertEquals(GuardResult.Allow, evaluate(PermissionGuard(read), auth))
        assertEquals(GuardResult.Deny, evaluate(PermissionGuard(write), auth))
        assertEquals(GuardResult.Allow, evaluate(PermissionsGuard(setOf(read, write)), auth))
        assertEquals(GuardResult.Deny, evaluate(PermissionsGuard(setOf(read, write), requireAll = true), auth))
    }

    @Test
    fun annotationAndCompositeGuardsStopAtFirstFailure() {
        val auth = authentication()
        val guarded = AnnotationBasedGuard(true, setOf(admin), setOf(read))
        assertEquals(GuardResult.Redirect("/login"), evaluate(guarded, null))
        assertEquals(GuardResult.Deny, evaluate(guarded, authentication(roles = emptySet())))
        assertEquals(GuardResult.Deny, evaluate(guarded, authentication(permissions = emptySet())))
        assertEquals(GuardResult.Allow, evaluate(guarded, auth))
        assertEquals(GuardResult.Allow, evaluate(AnnotationBasedGuard(), null))

        assertEquals(GuardResult.Allow, evaluate(CompositeGuard.all(RoleGuard(admin), PermissionGuard(read)), auth))
        assertEquals(GuardResult.Deny, evaluate(CompositeGuard.all(RoleGuard(editor), PermissionGuard(read)), auth))
        assertEquals(GuardResult.Allow, evaluate(CompositeGuard.any(RoleGuard(editor), PermissionGuard(read)), auth))
        assertEquals(GuardResult.Deny, evaluate(CompositeGuard.any(RoleGuard(editor), PermissionGuard(write)), auth))
        assertEquals(GuardResult.Allow, evaluate(CompositeGuard.all(), auth))
        assertEquals(GuardResult.Deny, evaluate(CompositeGuard.any(), auth))
    }

    @Test
    fun factoryPreservesAnnotationRequirements() {
        val auth = authentication()
        assertEquals(GuardResult.Allow, evaluate(SecurityGuardFactory.createGuard(RequiresAuthentication()), auth))
        assertEquals(GuardResult.Allow, evaluate(SecurityGuardFactory.createGuard(RequiresRoles("admin")), auth))
        assertEquals(GuardResult.Deny, evaluate(SecurityGuardFactory.createGuard(RequiresRoles("editor")), auth))
        assertEquals(GuardResult.Allow, evaluate(SecurityGuardFactory.createGuard(RequiresPermissions("read")), auth))
        assertEquals(
            GuardResult.Allow,
            evaluate(SecurityGuardFactory.createGuard(RequiresAccess(true, arrayOf("admin"), arrayOf("read"))), auth)
        )
    }
}
