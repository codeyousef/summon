package code.yousef.summon.examples.ktor.routing

import code.yousef.summon.examples.ktor.database.UserRepository
import code.yousef.summon.examples.ktor.i18n.Translations
import code.yousef.summon.examples.ktor.models.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.authRoutes() {
    route("/auth") {
        post("/login") {
            try {
                val request = call.receive<LoginRequest>()
                val user = UserRepository.findUserByUsername(request.username)
                
                if (user != null && UserRepository.verifyPassword(user, request.password)) {
                    // Get language from session or default to English
                    val currentSession = call.sessions.get<UserSession>()
                    val language = currentSession?.language ?: "en"
                    val theme = currentSession?.theme ?: "light"
                    
                    // Create session
                    val userSession = UserSession(
                        userId = user.id.value,
                        username = user.username,
                        language = language,
                        theme = theme
                    )
                    call.sessions.set(userSession)
                    
                    call.respond(AuthResponse(
                        success = true,
                        message = Translations.get("auth.login_success", language),
                        user = user.toDto()
                    ))
                } else {
                    val language = call.sessions.get<UserSession>()?.language ?: "en"
                    call.respond(
                        status = HttpStatusCode.Unauthorized,
                        message = AuthResponse(
                            success = false,
                            message = Translations.get("auth.login_failed", language)
                        )
                    )
                }
            } catch (e: Exception) {
                val language = call.sessions.get<UserSession>()?.language ?: "en"
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = AuthResponse(
                        success = false,
                        message = Translations.get("error.server_error", language)
                    )
                )
            }
        }
        
        post("/register") {
            try {
                val request = call.receive<RegisterRequest>()
                val user = UserRepository.createUser(request.username, request.email, request.password)
                
                if (user != null) {
                    val language = call.sessions.get<UserSession>()?.language ?: "en"
                    call.respond(AuthResponse(
                        success = true,
                        message = Translations.get("auth.register_success", language),
                        user = user
                    ))
                } else {
                    val language = call.sessions.get<UserSession>()?.language ?: "en"
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = AuthResponse(
                            success = false,
                            message = Translations.get("auth.register_failed", language)
                        )
                    )
                }
            } catch (e: Exception) {
                val language = call.sessions.get<UserSession>()?.language ?: "en"
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = AuthResponse(
                        success = false,
                        message = Translations.get("error.server_error", language)
                    )
                )
            }
        }
        
        post("/logout") {
            call.sessions.clear<UserSession>()
            val language = call.request.queryParameters["lang"] ?: "en"
            call.respond(AuthResponse(
                success = true,
                message = Translations.get("auth.logout", language)
            ))
        }
        
        get("/session") {
            val session = call.sessions.get<UserSession>()
            if (session != null) {
                val user = UserRepository.findUserById(session.userId)
                if (user != null) {
                    call.respond(AuthResponse(
                        success = true,
                        message = "Session valid",
                        user = user.toDto()
                    ))
                } else {
                    call.sessions.clear<UserSession>()
                    call.respond(
                        status = HttpStatusCode.Unauthorized,
                        message = AuthResponse(success = false, message = "Invalid session")
                    )
                }
            } else {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = AuthResponse(success = false, message = "No session")
                )
            }
        }
        
        post("/settings") {
            val session = call.sessions.get<UserSession>()
            if (session != null) {
                try {
                    val body = call.receiveText()
                    val parts = body.split("&").associate { 
                        val (key, value) = it.split("=", limit = 2)
                        key to value
                    }
                    
                    val newLanguage = parts["language"] ?: session.language
                    val newTheme = parts["theme"] ?: session.theme
                    
                    val updatedSession = session.copy(
                        language = newLanguage,
                        theme = newTheme
                    )
                    call.sessions.set(updatedSession)
                    
                    call.respond(AuthResponse(
                        success = true,
                        message = "Settings updated"
                    ))
                } catch (e: Exception) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = AuthResponse(success = false, message = "Invalid settings data")
                    )
                }
            } else {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = AuthResponse(success = false, message = "No session")
                )
            }
        }
    }
}