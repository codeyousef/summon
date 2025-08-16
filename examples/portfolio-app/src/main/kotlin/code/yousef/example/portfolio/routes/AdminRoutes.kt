package code.yousef.example.portfolio.routes

import code.yousef.example.portfolio.auth.AuthService
import code.yousef.example.portfolio.models.*
import code.yousef.example.portfolio.plugins.UserSession
import code.yousef.summon.runtime.PlatformRenderer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.adminRoutes() {
    val renderer by inject<PlatformRenderer>()
    val authService by inject<AuthService>()
    val userRepository by inject<UserRepository>()
    val categoryRepository by inject<CategoryRepository>()
    val portfolioRepository by inject<PortfolioRepository>()
    
    route("/admin") {
        // Login page
        get("/login") {
            val error = call.request.queryParameters["error"]
            val html = renderer.renderComposableRoot {
                code.yousef.example.portfolio.admin.AdminLoginPage(error)
            }
            call.respondText(html, ContentType.Text.Html)
        }
        
        // Login POST
        post("/login") {
            val formParameters = call.receiveParameters()
            val username = formParameters["username"] ?: ""
            val password = formParameters["password"] ?: ""
            
            val user = authService.authenticate(username, password)
            
            if (user != null && authService.isAdmin(user)) {
                call.sessions.set(UserSession(
                    userId = user.id,
                    username = user.username,
                    role = user.role.value
                ))
                call.respondRedirect("/admin/dashboard")
            } else {
                call.respondRedirect("/admin/login?error=Invalid+credentials")
            }
        }
        
        // Protected admin routes
        authenticate("auth-session") {
            // Dashboard
            get("/dashboard") {
                val html = renderer.renderComposableRoot {
                    code.yousef.example.portfolio.admin.AdminDashboardPage()
                }
                call.respondText(html, ContentType.Text.Html)
            }
            
            // Logout
            get("/logout") {
                call.sessions.clear<UserSession>()
                call.respondRedirect("/admin/login")
            }
            
            // User management routes
            route("/api/users") {
                get {
                    val users = userRepository.getAllUsers()
                    call.respond(users)
                }
                
                get("/{username}") {
                    val username = call.parameters["username"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val user = userRepository.getUserByUsername(username)
                    if (user != null) {
                        call.respond(user.toSafeUser())
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
                
                post {
                    val request = call.receive<CreateUserRequest>()
                    try {
                        val user = User.create(
                            username = request.username,
                            password = request.password,
                            email = request.email,
                            fullName = request.fullName,
                            role = UserRole.fromValue(request.role ?: "user")
                        )
                        val createdUser = userRepository.createUser(user)
                        call.respond(HttpStatusCode.Created, createdUser.toSafeUser())
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                    }
                }
                
                put("/{id}") {
                    val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                    val request = call.receive<UpdateUserRequest>()
                    
                    try {
                        val existingUser = userRepository.getAllUsers().find { it.id == id }
                            ?: return@put call.respond(HttpStatusCode.NotFound)
                        
                        val fullUser = userRepository.getUserByUsername(existingUser.username)
                            ?: return@put call.respond(HttpStatusCode.NotFound)
                        
                        var updatedUser = fullUser.copy(
                            email = request.email ?: fullUser.email,
                            fullName = request.fullName ?: fullUser.fullName,
                            role = request.role?.let { UserRole.fromValue(it) } ?: fullUser.role
                        )
                        
                        if (!request.password.isNullOrBlank()) {
                            updatedUser = updatedUser.withNewPassword(request.password)
                        }
                        
                        val savedUser = userRepository.updateUser(updatedUser)
                        call.respond(savedUser.toSafeUser())
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                    }
                }
                
                delete("/{id}") {
                    val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    val success = userRepository.deleteUser(id)
                    if (success) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
}

@Serializable
data class CreateUserRequest(
    val username: String,
    val password: String,
    val email: String? = null,
    val fullName: String? = null,
    val role: String? = null
)

@Serializable
data class UpdateUserRequest(
    val password: String? = null,
    val email: String? = null,
    val fullName: String? = null,
    val role: String? = null
)