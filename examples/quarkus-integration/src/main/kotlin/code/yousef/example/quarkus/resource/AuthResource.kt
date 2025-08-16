package code.yousef.example.quarkus.resource

import code.yousef.example.quarkus.model.*
import code.yousef.example.quarkus.service.AuthService
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.time.LocalDateTime

/**
 * REST resource for authentication operations
 */
@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class AuthResource {
    
    @Inject
    lateinit var authService: AuthService
    
    /**
     * Register a new user
     */
    @POST
    @Path("/register")
    fun register(request: RegisterRequest): Response {
        return authService.register(request).fold(
            onSuccess = { user ->
                Response.status(Response.Status.CREATED).entity(user).build()
            },
            onFailure = { error ->
                Response.status(Response.Status.BAD_REQUEST)
                    .entity(ErrorResponse("REGISTRATION_FAILED", error.message ?: "Registration failed"))
                    .build()
            }
        )
    }
    
    /**
     * Login user
     */
    @POST
    @Path("/login")
    fun login(request: LoginRequest): Response {
        return authService.login(request).fold(
            onSuccess = { loginResponse ->
                Response.ok(loginResponse)
                    .header("Set-Cookie", "sessionToken=${loginResponse.sessionToken}; HttpOnly; Path=/; Max-Age=86400")
                    .build()
            },
            onFailure = { error ->
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ErrorResponse("LOGIN_FAILED", error.message ?: "Login failed"))
                    .build()
            }
        )
    }
    
    /**
     * Logout user
     */
    @POST
    @Path("/logout")
    fun logout(@CookieParam("sessionToken") sessionToken: String?): Response {
        return if (sessionToken != null) {
            authService.logout(sessionToken)
            Response.ok(SuccessResponse("Logged out successfully"))
                .header("Set-Cookie", "sessionToken=; HttpOnly; Path=/; Max-Age=0")
                .build()
        } else {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(ErrorResponse("NO_SESSION", "No active session found"))
                .build()
        }
    }
    
    /**
     * Get current session info
     */
    @GET
    @Path("/session")
    fun getSession(@CookieParam("sessionToken") sessionToken: String?): Response {
        return if (sessionToken != null) {
            authService.validateSession(sessionToken).fold(
                onSuccess = { user ->
                    Response.ok(user).build()
                },
                onFailure = { error ->
                    Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ErrorResponse("INVALID_SESSION", error.message ?: "Invalid session"))
                        .build()
                }
            )
        } else {
            Response.status(Response.Status.UNAUTHORIZED)
                .entity(ErrorResponse("NO_SESSION", "No session token provided"))
                .build()
        }
    }
    
    /**
     * Update user settings
     */
    @PUT
    @Path("/settings")
    fun updateSettings(
        @CookieParam("sessionToken") sessionToken: String?,
        request: UpdateSettingsRequest
    ): Response {
        return if (sessionToken != null) {
            authService.validateSession(sessionToken).fold(
                onSuccess = { user ->
                    authService.updateUserSettings(user.id, request).fold(
                        onSuccess = { updatedUser ->
                            Response.ok(updatedUser).build()
                        },
                        onFailure = { error ->
                            Response.status(Response.Status.BAD_REQUEST)
                                .entity(ErrorResponse("UPDATE_FAILED", error.message ?: "Update failed"))
                                .build()
                        }
                    )
                },
                onFailure = { error ->
                    Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ErrorResponse("INVALID_SESSION", error.message ?: "Invalid session"))
                        .build()
                }
            )
        } else {
            Response.status(Response.Status.UNAUTHORIZED)
                .entity(ErrorResponse("NO_SESSION", "No session token provided"))
                .build()
        }
    }
}