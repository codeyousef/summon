package code.yousef.example.quarkus.resource

import code.yousef.example.quarkus.model.*
import code.yousef.example.quarkus.service.AuthService
import code.yousef.example.quarkus.service.TodoService
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

/**
 * REST resource for todo operations
 */
@Path("/api/todos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class TodoResource {
    
    @Inject
    lateinit var todoService: TodoService
    
    @Inject
    lateinit var authService: AuthService
    
    /**
     * Get all todos for the authenticated user
     */
    @GET
    fun getTodos(@CookieParam("sessionToken") sessionToken: String?): Response {
        return authenticateAndExecute(sessionToken) { user ->
            val todos = todoService.getTodosByUserId(user.id)
            Response.ok(todos).build()
        }
    }
    
    /**
     * Get a specific todo by ID
     */
    @GET
    @Path("/{id}")
    fun getTodo(
        @PathParam("id") todoId: String,
        @CookieParam("sessionToken") sessionToken: String?
    ): Response {
        return authenticateAndExecute(sessionToken) { user ->
            val todo = todoService.getTodoById(todoId, user.id)
            if (todo != null) {
                Response.ok(todo).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(ErrorResponse("TODO_NOT_FOUND", "Todo not found"))
                    .build()
            }
        }
    }
    
    /**
     * Create a new todo
     */
    @POST
    fun createTodo(
        request: CreateTodoRequest,
        @CookieParam("sessionToken") sessionToken: String?
    ): Response {
        return authenticateAndExecute(sessionToken) { user ->
            todoService.createTodo(request, user.id).fold(
                onSuccess = { todo ->
                    Response.status(Response.Status.CREATED).entity(todo).build()
                },
                onFailure = { error ->
                    Response.status(Response.Status.BAD_REQUEST)
                        .entity(ErrorResponse("CREATE_FAILED", error.message ?: "Failed to create todo"))
                        .build()
                }
            )
        }
    }
    
    /**
     * Update an existing todo
     */
    @PUT
    @Path("/{id}")
    fun updateTodo(
        @PathParam("id") todoId: String,
        request: UpdateTodoRequest,
        @CookieParam("sessionToken") sessionToken: String?
    ): Response {
        return authenticateAndExecute(sessionToken) { user ->
            todoService.updateTodo(todoId, request, user.id).fold(
                onSuccess = { todo ->
                    Response.ok(todo).build()
                },
                onFailure = { error ->
                    Response.status(Response.Status.BAD_REQUEST)
                        .entity(ErrorResponse("UPDATE_FAILED", error.message ?: "Failed to update todo"))
                        .build()
                }
            )
        }
    }
    
    /**
     * Toggle completion status of a todo
     */
    @PATCH
    @Path("/{id}/toggle")
    fun toggleTodo(
        @PathParam("id") todoId: String,
        @CookieParam("sessionToken") sessionToken: String?
    ): Response {
        return authenticateAndExecute(sessionToken) { user ->
            todoService.toggleTodo(todoId, user.id).fold(
                onSuccess = { todo ->
                    Response.ok(todo).build()
                },
                onFailure = { error ->
                    Response.status(Response.Status.BAD_REQUEST)
                        .entity(ErrorResponse("TOGGLE_FAILED", error.message ?: "Failed to toggle todo"))
                        .build()
                }
            )
        }
    }
    
    /**
     * Delete a todo
     */
    @DELETE
    @Path("/{id}")
    fun deleteTodo(
        @PathParam("id") todoId: String,
        @CookieParam("sessionToken") sessionToken: String?
    ): Response {
        return authenticateAndExecute(sessionToken) { user ->
            todoService.deleteTodo(todoId, user.id).fold(
                onSuccess = {
                    Response.ok(SuccessResponse("Todo deleted successfully")).build()
                },
                onFailure = { error ->
                    Response.status(Response.Status.BAD_REQUEST)
                        .entity(ErrorResponse("DELETE_FAILED", error.message ?: "Failed to delete todo"))
                        .build()
                }
            )
        }
    }
    
    /**
     * Get todo statistics for the authenticated user
     */
    @GET
    @Path("/stats")
    fun getTodoStats(@CookieParam("sessionToken") sessionToken: String?): Response {
        return authenticateAndExecute(sessionToken) { user ->
            val stats = todoService.getTodoStats(user.id)
            Response.ok(stats).build()
        }
    }
    
    /**
     * Helper function to authenticate user and execute operation
     */
    private fun authenticateAndExecute(
        sessionToken: String?,
        operation: (PublicUser) -> Response
    ): Response {
        return if (sessionToken != null) {
            authService.validateSession(sessionToken).fold(
                onSuccess = { user -> operation(user) },
                onFailure = { error ->
                    Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ErrorResponse("INVALID_SESSION", error.message ?: "Invalid session"))
                        .build()
                }
            )
        } else {
            Response.status(Response.Status.UNAUTHORIZED)
                .entity(ErrorResponse("NO_SESSION", "Authentication required"))
                .build()
        }
    }
}