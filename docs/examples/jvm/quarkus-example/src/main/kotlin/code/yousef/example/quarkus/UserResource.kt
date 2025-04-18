package code.yousef.example.quarkus

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status
import jakarta.inject.Inject
import code.yousef.example.quarkus.components.RegistrationPage
import code.yousef.example.quarkus.components.UserEditPage
import code.yousef.summon.platform.PageMetadata
import code.yousef.summon.platform.RenderToString
import code.yousef.summon.runtime.JvmPlatformRenderer

/**
 * REST resource for managing users in the system.
 */
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class UserResource {

    @Inject
    lateinit var userService: UserService
    
    // Use a single renderer instance for all requests
    private val renderer = JvmPlatformRenderer()

    /**
     * Get all users.
     * 
     * @return List of all users
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getUsers(): List<User> {
        return userService.getAllUsers()
    }

    /**
     * Get active users.
     * 
     * @return List of active users
     */
    @GET
    @Path("/active")
    @Produces(MediaType.APPLICATION_JSON)
    fun getActiveUsers(): List<User> {
        return userService.getActiveUsers()
    }

    /**
     * Get a user by ID.
     * 
     * @param id The user ID
     * @return The user if found
     */
    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long): Response {
        val user = userService.getUserById(id)
        return if (user != null) {
            Response.ok(user).build()
        } else {
            Response.status(Status.NOT_FOUND).build()
        }
    }

    /**
     * Create a new user.
     * 
     * @param user The user to create
     * @return The created user
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun createUser(user: User): Response {
        val newUser = userService.addUser(user)
        return Response.status(Status.CREATED).entity(newUser).build()
    }

    /**
     * Update an existing user.
     * 
     * @param id The ID of the user to update
     * @param user The updated user data
     * @return The updated user
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun updateUser(@PathParam("id") id: Long, user: User): Response {
        val updatedUser = userService.updateUser(id, user)
        return if (updatedUser != null) {
            Response.ok(updatedUser).build()
        } else {
            Response.status(Status.NOT_FOUND).build()
        }
    }

    /**
     * Delete a user by ID.
     * 
     * @param id The ID of the user to delete
     * @return Success response or not found
     */
    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long): Response {
        val deleted = userService.deleteUser(id)
        return if (deleted) {
            Response.noContent().build()
        } else {
            Response.status(Status.NOT_FOUND).build()
        }
    }

    /**
     * Search for users by name or email.
     * 
     * @param query The search term
     * @return List of matching users
     */
    @GET
    @Path("/search")
    fun searchUsers(@QueryParam("q") query: String): List<User> {
        return userService.searchUsers(query)
    }

    /**
     * Get users by role.
     * 
     * @param role The role to filter by
     * @return List of users with the specified role
     */
    @GET
    @Path("/role/{role}")
    fun getUsersByRole(@PathParam("role") role: String): List<User> {
        return userService.getUsersByRole(role)
    }

    /**
     * Activate a user.
     * 
     * @param id The ID of the user to activate
     * @return The activated user
     */
    @PATCH
    @Path("/{id}/activate")
    @Produces(MediaType.APPLICATION_JSON)
    fun activateUser(@PathParam("id") id: Long): Response {
        val updatedUser = userService.activateUser(id)
        return if (updatedUser != null) {
            Response.ok(updatedUser).build()
        } else {
            Response.status(Status.NOT_FOUND).build()
        }
    }

    /**
     * Deactivate a user.
     * 
     * @param id The ID of the user to deactivate
     * @return The deactivated user
     */
    @PATCH
    @Path("/{id}/deactivate")
    @Produces(MediaType.APPLICATION_JSON)
    fun deactivateUser(@PathParam("id") id: Long): Response {
        val updatedUser = userService.deactivateUser(id)
        return if (updatedUser != null) {
            Response.ok(updatedUser).build()
        } else {
            Response.status(Status.NOT_FOUND).build()
        }
    }

    /**
     * Renders the edit user page.
     */
    @GET
    @Path("/users/edit/{id}")
    @Produces(MediaType.TEXT_HTML)
    fun renderEditPage(@PathParam("id") id: Long): Response {
        val user = userService.getUserById(id)
        return if (user != null) {
            val metadata = PageMetadata(
                title = "Edit User: ${user.name}",
                description = "Edit user profile for ${user.name}"
            )
            Response.ok(RenderToString.withMetadata(renderer, metadata) { 
                UserEditPage(user) 
            }).build()
        } else {
            Response.status(Status.NOT_FOUND).build()
        }
    }
    
    /**
     * Renders the registration page.
     */
    @GET
    @Path("/users/register")
    @Produces(MediaType.TEXT_HTML)
    fun renderRegistrationPage(): String {
        val metadata = PageMetadata(
            title = "Register New User",
            description = "Create a new user account"
        )
        return RenderToString.withMetadata(renderer, metadata) { 
            RegistrationPage() 
        }
    }
} 