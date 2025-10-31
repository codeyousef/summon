package code.yousef.summon.security

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class TokenResponseTest {
    
    @Test
    fun testTokenResponseCreation() {
        // Create a token response with all fields
        val token = "jwt.token.string"
        val refreshToken = "refresh.token.string"
        val expiresIn = 3600L
        
        val response = TokenResponse(
            token = token,
            refreshToken = refreshToken,
            expiresIn = expiresIn
        )
        
        // Verify the properties
        assertEquals(token, response.token)
        assertEquals(refreshToken, response.refreshToken)
        assertEquals(expiresIn, response.expiresIn)
    }
    
    @Test
    fun testTokenResponseWithDefaultValues() {
        // Create a token response with only the required token
        val token = "jwt.token.string"
        
        val response = TokenResponse(token)
        
        // Verify the properties
        assertEquals(token, response.token)
        assertNull(response.refreshToken)
        assertNull(response.expiresIn)
    }
    
    @Test
    fun testTokenResponseEquality() {
        // Create two identical token responses
        val token = "jwt.token.string"
        val refreshToken = "refresh.token.string"
        val expiresIn = 3600L
        
        val response1 = TokenResponse(token, refreshToken, expiresIn)
        val response2 = TokenResponse(token, refreshToken, expiresIn)
        
        // Verify they are equal
        assertEquals(response1, response2)
        assertEquals(response1.hashCode(), response2.hashCode())
    }
    
    @Test
    fun testTokenResponseInequality() {
        // Create two different token responses
        val response1 = TokenResponse("token1", "refresh1", 3600L)
        val response2 = TokenResponse("token2", "refresh2", 7200L)
        
        // Verify they are not equal
        assertNotEquals(response1, response2)
        assertNotEquals(response1.hashCode(), response2.hashCode())
    }
    
    @Test
    fun testTokenResponseCopy() {
        // Create a token response
        val original = TokenResponse("token", "refresh", 3600L)
        
        // Create a copy with a different token
        val copy = original.copy(token = "new.token")
        
        // Verify the copy has the new token but the same refresh token and expiration
        assertEquals("new.token", copy.token)
        assertEquals(original.refreshToken, copy.refreshToken)
        assertEquals(original.expiresIn, copy.expiresIn)
    }
}
