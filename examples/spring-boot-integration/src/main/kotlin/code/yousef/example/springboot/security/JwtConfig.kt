package code.yousef.example.springboot.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider {
    
    @Value("\${app.jwt.secret:mySecretKeyForJWTTokenGenerationThatIsLongEnoughForHS256Algorithm}")
    private lateinit var jwtSecret: String
    
    @Value("\${app.jwt.expiration:86400000}") // 24 hours in milliseconds
    private val jwtExpirationInMs: Long = 86400000
    
    private fun getSigningKey(): Key {
        return Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }
    
    fun generateToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserDetails
        val expiryDate = Date(System.currentTimeMillis() + jwtExpirationInMs)
        
        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setIssuedAt(Date())
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact()
    }
    
    fun generateTokenFromUsername(username: String): String {
        val expiryDate = Date(System.currentTimeMillis() + jwtExpirationInMs)
        
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact()
    }
    
    fun getUsernameFromToken(token: String): String {
        val claims = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .build()
            .parseSignedClaims(token)
            .payload
        
        return claims.subject
    }
    
    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
                .build()
                .parseSignedClaims(token)
            return true
        } catch (ex: JwtException) {
            println("JWT validation error: ${ex.message}")
        } catch (ex: IllegalArgumentException) {
            println("JWT token is empty: ${ex.message}")
        }
        return false
    }
}