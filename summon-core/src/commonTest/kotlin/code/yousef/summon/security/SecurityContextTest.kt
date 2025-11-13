package codes.yousef.summon.security

import kotlin.test.*

class SecurityContextTest {
    
    @BeforeTest
    fun setup() {
        // Clear the security context before each test
        SecurityContext.clearAuthentication()
    }
    
    @AfterTest
    fun teardown() {
        // Clear the security context after each test
        SecurityContext.clearAuthentication()
    }
    
    @Test
    fun testGetAuthenticationWhenEmpty() {
        // Initially, there should be no authentication
        assertNull(SecurityContext.getAuthentication())
    }
    
    @Test
    fun testIsAuthenticatedWhenEmpty() {
        // Initially, isAuthenticated should be false
        assertFalse(SecurityContext.isAuthenticated())
    }
    
    @Test
    fun testGetPrincipalWhenEmpty() {
        // Initially, there should be no principal
        assertNull(SecurityContext.getPrincipal())
    }
    
    @Test
    fun testWithAuthentication() {
        // Create credentials and authentication
        val credentials = UsernamePasswordCredentials("testuser", "password123")
        val principal = object : Principal {
            override val id: String = "user-123"
            override val roles: Set<Role> = setOf(Role("user"))
            override val permissions: Set<Permission> = setOf(Permission("read:own"))
            override val attributes: Map<String, Any> = mapOf("name" to "Test User")
        }
        val authentication = SimpleAuthentication(
            credentials = credentials,
            principal = principal,
            isAuthenticated = true
        )
        
        // Execute a block with the authentication
        val result = SecurityContext.withAuthentication(authentication) {
            // Inside the block, the authentication should be set
            val currentAuth = SecurityContext.getAuthentication()
            assertEquals(authentication, currentAuth)
            assertTrue(SecurityContext.isAuthenticated())
            assertEquals(principal, SecurityContext.getPrincipal())
            
            // Return a value from the block
            "success"
        }
        
        // Verify the result
        assertEquals("success", result)
        
        // After the block, the authentication should be cleared
        assertNull(SecurityContext.getAuthentication())
        assertFalse(SecurityContext.isAuthenticated())
        assertNull(SecurityContext.getPrincipal())
    }
    
    @Test
    fun testWithAuthenticationNested() {
        // Create two different authentications
        val credentials1 = UsernamePasswordCredentials("user1", "pass1")
        val principal1 = object : Principal {
            override val id: String = "user-1"
            override val roles: Set<Role> = setOf(Role("user"))
            override val permissions: Set<Permission> = setOf(Permission("read:own"))
            override val attributes: Map<String, Any> = emptyMap()
        }
        val auth1 = SimpleAuthentication(
            credentials = credentials1,
            principal = principal1,
            isAuthenticated = true
        )
        
        val credentials2 = UsernamePasswordCredentials("user2", "pass2")
        val principal2 = object : Principal {
            override val id: String = "user-2"
            override val roles: Set<Role> = setOf(Role("admin"))
            override val permissions: Set<Permission> = setOf(Permission("read:any"))
            override val attributes: Map<String, Any> = emptyMap()
        }
        val auth2 = SimpleAuthentication(
            credentials = credentials2,
            principal = principal2,
            isAuthenticated = true
        )
        
        // Execute nested blocks with different authentications
        SecurityContext.withAuthentication(auth1) {
            // In the outer block, auth1 should be set
            assertEquals(auth1, SecurityContext.getAuthentication())
            assertEquals(principal1, SecurityContext.getPrincipal())
            
            SecurityContext.withAuthentication(auth2) {
                // In the inner block, auth2 should be set
                assertEquals(auth2, SecurityContext.getAuthentication())
                assertEquals(principal2, SecurityContext.getPrincipal())
            }
            
            // After the inner block, auth1 should be restored
            assertEquals(auth1, SecurityContext.getAuthentication())
            assertEquals(principal1, SecurityContext.getPrincipal())
        }
        
        // After the outer block, no authentication should be set
        assertNull(SecurityContext.getAuthentication())
        assertNull(SecurityContext.getPrincipal())
    }
    
    @Test
    fun testHasRole() {
        // Create authentication with roles
        val credentials = UsernamePasswordCredentials("testuser", "password123")
        val principal = object : Principal {
            override val id: String = "user-123"
            override val roles: Set<Role> = setOf(Role("user"), Role("editor"))
            override val permissions: Set<Permission> = emptySet()
            override val attributes: Map<String, Any> = emptyMap()
        }
        val authentication = SimpleAuthentication(
            credentials = credentials,
            principal = principal,
            isAuthenticated = true
        )
        
        SecurityContext.withAuthentication(authentication) {
            // Test has role
            assertTrue(SecurityContext.hasRole(Role("user")))
            assertTrue(SecurityContext.hasRole(Role("editor")))
            assertFalse(SecurityContext.hasRole(Role("admin")))
        }
    }
    
    @Test
    fun testHasPermission() {
        // Create authentication with permissions
        val credentials = UsernamePasswordCredentials("testuser", "password123")
        val principal = object : Principal {
            override val id: String = "user-123"
            override val roles: Set<Role> = emptySet()
            override val permissions: Set<Permission> = setOf(
                Permission("read:own"), 
                Permission("write:own")
            )
            override val attributes: Map<String, Any> = emptyMap()
        }
        val authentication = SimpleAuthentication(
            credentials = credentials,
            principal = principal,
            isAuthenticated = true
        )
        
        SecurityContext.withAuthentication(authentication) {
            // Test has permission
            assertTrue(SecurityContext.hasPermission(Permission("read:own")))
            assertTrue(SecurityContext.hasPermission(Permission("write:own")))
            assertFalse(SecurityContext.hasPermission(Permission("read:any")))
        }
    }
}
