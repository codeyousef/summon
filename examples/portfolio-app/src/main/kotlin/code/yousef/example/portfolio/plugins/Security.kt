package code.yousef.example.portfolio.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class UserSession(val userId: String, val username: String, val role: String)

fun Application.configureSecurity() {
    val secretKey = hex(System.getenv("SESSION_SECRET_KEY") ?: "00112233445566778899aabbccddeeff")
    
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.extensions["SameSite"] = "lax"
            cookie.httpOnly = true
            cookie.secure = System.getenv("ENVIRONMENT") == "production"
            transform(SessionTransportTransformerMessageAuthentication(secretKey))
        }
    }
    
    install(Authentication) {
        basic("auth-basic") {
            realm = "Access to the admin area"
            validate { credentials ->
                val user = transaction {
                    Users.select { Users.username eq credentials.name }
                        .singleOrNull()
                }
                
                if (user != null && BCrypt.checkpw(credentials.password, user[Users.passwordHash])) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
        
        session<UserSession>("auth-session") {
            validate { session ->
                if (session.role == "admin") {
                    UserIdPrincipal(session.username)
                } else {
                    null
                }
            }
            
            challenge {
                call.respondRedirect("/admin/login")
            }
        }
    }
}