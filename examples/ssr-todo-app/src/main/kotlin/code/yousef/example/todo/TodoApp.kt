package code.yousef.example.todo

import code.yousef.example.todo.components.CreateTodoForm
import code.yousef.example.todo.components.TodoList
import code.yousef.example.todo.components.TodoStatsDisplay
import code.yousef.example.todo.design.*
import code.yousef.example.todo.design.ModifierExtensions.marginVertical
import code.yousef.example.todo.design.ModifierExtensions.radius
import code.yousef.example.todo.design.ModifierExtensions.spacing
import code.yousef.example.todo.models.*
import code.yousef.example.todo.services.SessionTodoService
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.sessions.serialization.*
import kotlinx.serialization.json.Json

/**
 * Fully Functional SSR Todo Application
 *
 * This file contains:
 * - Session configuration
 * - Route handlers for all CRUD operations
 * - Error handling
 * - Server setup
 */

/**
 * Main application composable
 */
@Composable
fun TodoApp(
    todos: List<Todo>,
    stats: TodoStats,
    formState: FormState = FormState.success(),
    editingTodoId: Int? = null,
    editFormState: FormState = FormState.success()
) {
    // Global container for background
    code.yousef.summon.components.layout.Column(
        modifier = Modifier()
            .backgroundColor(SemanticColor.BACKGROUND.lightValue)
            .style("width", "100%")
            .style("min-height", "100vh")
    ) {
        // Centered container with max width
        code.yousef.summon.components.layout.Column(
            modifier = Modifier()
                .style("width", "100%")
                .style("max-width", MaxWidth.LG.value)
                .style("margin-left", "auto")
                .style("margin-right", "auto")
                .spacing(Spacing.LG)
                .backgroundColor(SemanticColor.SURFACE.lightValue)
                .style("box-shadow", Shadow.MD.value)
                .radius(BorderRadius.LG)
                .marginVertical(Spacing.XL)
        ) {
            // App header with modern typography
            Text(
                text = "📝 Todo App",
                modifier = Modifier()
                    .style("font-size", TextSize.XXXL.value)
                    .style("font-weight", FontWeight.BOLD.value)
                    .color(SemanticColor.TEXT_PRIMARY.lightValue)
                    .style("margin-bottom", Spacing.SM.value)
            )
            Text(
                text = "A modern, responsive todo application",
                modifier = Modifier()
                    .style("font-size", TextSize.LG.value)
                    .style("font-weight", FontWeight.MEDIUM.value)
                    .color(SemanticColor.TEXT_SECONDARY.lightValue)
                    .style("margin-bottom", Spacing.MD.value)
            )

            // Create todo form with modern styling
            CreateTodoForm(formState = formState)

            // Todo statistics with modern styling
            TodoStatsDisplay(stats = stats)

            // Todo list with modern styling
            TodoList(
                todos = todos,
                editingTodoId = editingTodoId,
                editFormState = editFormState
            )
        }
    }
}

fun Application.configureTodoApp() {
    // Configure sessions with kotlinx serialization
    install(Sessions) {
        cookie<TodoSession>("todo_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 3600 // 1 hour
            cookie.httpOnly = true
            cookie.secure = false // Set to true in production with HTTPS
            serializer = KotlinxSessionSerializer(Json)
        }
    }

    routing {
        todoRoutes()
    }
}

/**
 * Route handlers for all CRUD operations
 * T022: GET / route handler with todo list rendering
 * T023: POST /todos route handler with validation
 * T024: POST /todos/{id}/toggle route handler
 * T025: POST /todos/{id}/edit route handler with validation
 * T026: POST /todos/{id}/delete route handler
 * T027: Error handling for 404 and validation errors
 */
fun Route.todoRoutes() {
    val renderer = PlatformRenderer()

    // Helper function to get or create session
    fun ApplicationCall.getTodoSession(): TodoSession {
        return sessions.get<TodoSession>() ?: TodoSession().also {
            sessions.set(it)
        }
    }

    // Helper function to save session back
    fun ApplicationCall.saveTodoSession(session: TodoSession) {
        sessions.set(session)
    }

    // Helper function to render page with error handling
    suspend fun ApplicationCall.renderTodoPage(
        formState: FormState = FormState.success(),
        editingTodoId: Int? = null,
        editFormState: FormState = FormState.success()
    ) {
        try {
            val session = getTodoSession()
            val todoService = SessionTodoService(session)
            val todos = todoService.getAll()
            val stats = todoService.getStats()

            val html = renderer.renderComposableRoot {
                TodoApp(
                    todos = todos,
                    stats = stats,
                    formState = formState,
                    editingTodoId = editingTodoId,
                    editFormState = editFormState
                )
            }
            respondText(html, ContentType.Text.Html)
        } catch (e: Exception) {
            respondText(
                "Error: ${e.message}",
                ContentType.Text.Html,
                HttpStatusCode.InternalServerError
            )
        }
    }

    // T022: GET / route handler
    get("/") {
        val editingTodoId = call.request.queryParameters["edit"]?.toIntOrNull()
        call.renderTodoPage(editingTodoId = editingTodoId)
    }

    // T023: POST /todos route handler with validation
    post("/todos") {
        try {
            val parameters = call.receiveParameters()
            val paramMap = parameters.entries().associate { it.key to (it.value.firstOrNull() ?: "") }
            val form = CreateTodoForm.fromParameters(paramMap)

            val validationError = form.validate()
            if (validationError != null) {
                // Render page with validation error
                call.renderTodoPage(
                    formState = FormState.withError(validationError, paramMap)
                )
            } else {
                // Create todo and redirect
                val session = call.getTodoSession()
                val todoService = SessionTodoService(session)
                todoService.create(form.text)
                // Get the modified session from the service
                call.saveTodoSession(todoService.session)
                // Redirect to prevent duplicate submission
                call.respondRedirect("/")
            }
        } catch (e: ValidationException) {
            val parameters = call.receiveParameters()
            val paramMap = parameters.entries().associate { it.key to (it.value.firstOrNull() ?: "") }
            call.renderTodoPage(
                formState = FormState.withError(e.message ?: "Validation error", paramMap)
            )
        } catch (e: Exception) {
            call.respondText(
                "Error creating todo: ${e.message}",
                ContentType.Text.Html,
                HttpStatusCode.InternalServerError
            )
        }
    }

    // T024: POST /todos/{id}/toggle route handler
    post("/todos/{id}/toggle") {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid todo ID", ContentType.Text.Html, HttpStatusCode.BadRequest)
                return@post
            }

            val session = call.getTodoSession()
            val todoService = SessionTodoService(session)
            val result = todoService.toggle(id)

            if (result != null) {
                // Get the modified session from the service
                call.saveTodoSession(todoService.session)
                call.respondRedirect("/")
            } else {
                call.respondText("Todo not found", ContentType.Text.Html, HttpStatusCode.NotFound)
            }
        } catch (e: Exception) {
            call.respondText(
                "Error toggling todo: ${e.message}",
                ContentType.Text.Html,
                HttpStatusCode.InternalServerError
            )
        }
    }

    // T025: POST /todos/{id}/edit route handler with validation
    post("/todos/{id}/edit") {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid todo ID", ContentType.Text.Html, HttpStatusCode.BadRequest)
                return@post
            }

            val session = call.getTodoSession()
            val todoService = SessionTodoService(session)
            val parameters = call.receiveParameters()
            val paramMap = parameters.entries().associate { it.key to (it.value.firstOrNull() ?: "") }
            val form = UpdateTodoForm.fromParameters(id, paramMap)

            val validationError = form.validate()
            if (validationError != null) {
                // Render page with validation error and editing state
                call.renderTodoPage(
                    editingTodoId = id,
                    editFormState = FormState.withError(validationError, paramMap)
                )
            } else {
                // Update todo
                val result = todoService.update(id, form.text)
                if (result != null) {
                    // Get the modified session from the service
                    call.saveTodoSession(todoService.session)
                    call.respondRedirect("/")
                } else {
                    call.respondText("Todo not found", ContentType.Text.Html, HttpStatusCode.NotFound)
                }
            }
        } catch (e: ValidationException) {
            val id = call.parameters["id"]?.toIntOrNull() ?: 0
            val parameters = call.receiveParameters()
            val paramMap = parameters.entries().associate { it.key to (it.value.firstOrNull() ?: "") }
            call.renderTodoPage(
                editingTodoId = id,
                editFormState = FormState.withError(e.message ?: "Validation error", paramMap)
            )
        } catch (e: Exception) {
            call.respondText(
                "Error updating todo: ${e.message}",
                ContentType.Text.Html,
                HttpStatusCode.InternalServerError
            )
        }
    }

    // T026: POST /todos/{id}/delete route handler
    post("/todos/{id}/delete") {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid todo ID", ContentType.Text.Html, HttpStatusCode.BadRequest)
                return@post
            }

            val session = call.getTodoSession()
            val todoService = SessionTodoService(session)
            val result = todoService.delete(id)

            if (result) {
                // Get the modified session from the service
                call.saveTodoSession(todoService.session)
                call.respondRedirect("/")
            } else {
                call.respondText("Todo not found", ContentType.Text.Html, HttpStatusCode.NotFound)
            }
        } catch (e: Exception) {
            call.respondText(
                "Error deleting todo: ${e.message}",
                ContentType.Text.Html,
                HttpStatusCode.InternalServerError
            )
        }
    }
}

fun main() {
    // Initialize Summon renderer
    val renderer = PlatformRenderer()
    setPlatformRenderer(renderer)

    embeddedServer(Netty, port = 8080) {
        configureTodoApp()
    }.start(wait = true)

    println("🚀 SSR Todo App started at http://localhost:8080")
}