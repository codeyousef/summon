package code.yousef.summon.security

import kotlin.test.*

class PrincipalTest {
    
    @Test
    fun testPrincipalImplementation() {
        // Create a principal
        val principal = object : Principal {
            override val id: String = "user-123"
            override val roles: Set<Role> = setOf(Role("user"), Role("editor"))
            override val permissions: Set<Permission> = setOf(
                Permission("read:own"), 
                Permission("write:own")
            )
            override val attributes: Map<String, Any> = mapOf(
                "name" to "Test User",
                "email" to "test@example.com",
                "createdAt" to "2023-01-01T00:00:00Z"
            )
        }
        
        // Verify the principal properties
        assertEquals("user-123", principal.id)
        assertEquals(2, principal.roles.size)
        assertTrue(principal.roles.contains(Role("user")))
        assertTrue(principal.roles.contains(Role("editor")))
        assertEquals(2, principal.permissions.size)
        assertTrue(principal.permissions.contains(Permission("read:own")))
        assertTrue(principal.permissions.contains(Permission("write:own")))
        assertEquals(3, principal.attributes.size)
        assertEquals("Test User", principal.attributes["name"])
        assertEquals("test@example.com", principal.attributes["email"])
        assertEquals("2023-01-01T00:00:00Z", principal.attributes["createdAt"])
    }
    
    @Test
    fun testRoleValueClass() {
        val role = Role("admin")
        
        assertEquals("admin", role.name)
        
        // Test equality
        val sameRole = Role("admin")
        val differentRole = Role("user")
        
        assertEquals(role, sameRole)
        assertNotEquals(role, differentRole)
        
        // Test hashCode
        assertEquals(role.hashCode(), sameRole.hashCode())
        assertNotEquals(role.hashCode(), differentRole.hashCode())
        
        // Test toString
        assertTrue(role.toString().contains("admin"))
    }
    
    @Test
    fun testPermissionValueClass() {
        val permission = Permission("read:any")
        
        assertEquals("read:any", permission.name)
        
        // Test equality
        val samePermission = Permission("read:any")
        val differentPermission = Permission("write:any")
        
        assertEquals(permission, samePermission)
        assertNotEquals(permission, differentPermission)
        
        // Test hashCode
        assertEquals(permission.hashCode(), samePermission.hashCode())
        assertNotEquals(permission.hashCode(), differentPermission.hashCode())
        
        // Test toString
        assertTrue(permission.toString().contains("read:any"))
    }
    
    @Test
    fun testRoleSetOperations() {
        val roles = setOf(Role("user"), Role("editor"), Role("admin"))
        
        // Test contains
        assertTrue(roles.contains(Role("user")))
        assertTrue(roles.contains(Role("editor")))
        assertTrue(roles.contains(Role("admin")))
        assertFalse(roles.contains(Role("guest")))
        
        // Test size
        assertEquals(3, roles.size)
        
        // Test iteration
        val roleNames = roles.map { it.name }.toSet()
        assertEquals(setOf("user", "editor", "admin"), roleNames)
    }
    
    @Test
    fun testPermissionSetOperations() {
        val permissions = setOf(
            Permission("read:own"), 
            Permission("write:own"), 
            Permission("read:any")
        )
        
        // Test contains
        assertTrue(permissions.contains(Permission("read:own")))
        assertTrue(permissions.contains(Permission("write:own")))
        assertTrue(permissions.contains(Permission("read:any")))
        assertFalse(permissions.contains(Permission("write:any")))
        
        // Test size
        assertEquals(3, permissions.size)
        
        // Test iteration
        val permissionNames = permissions.map { it.name }.toSet()
        assertEquals(setOf("read:own", "write:own", "read:any"), permissionNames)
    }
}