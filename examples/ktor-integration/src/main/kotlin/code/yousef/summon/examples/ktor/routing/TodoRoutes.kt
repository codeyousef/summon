package code.yousef.summon.examples.ktor.routing

import code.yousef.summon.examples.ktor.database.TodoRepository
import code.yousef.summon.examples.ktor.i18n.Translations
import code.yousef.summon.examples.ktor.models.*
import code.yousef.summon.examples.ktor.websocket.webSocketManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.todoRoutes() {
    route("/api/todos") {
        // Middleware to check authentication
        intercept(ApplicationCallPipeline.Call) {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = TodoResponse(
                        success = false,
                        message = Translations.get("message.login_required", "en")
                    )
                )
                finish()
            }
        }
        
        get {
            val session = call.sessions.get<UserSession>()!!
            val todos = TodoRepository.getTodosByUser(session.userId)
            call.respond(TodoResponse(
                success = true,
                message = "Todos retrieved",
                todos = todos
            ))
        }
        
        post {
            val session = call.sessions.get<UserSession>()!!
            try {
                val request = call.receive<CreateTodoRequest>()
                val todo = TodoRepository.createTodo(session.userId, request.text)
                
                // Broadcast to all connected users via WebSocket
                webSocketManager.broadcastToAll(WebSocketMessage.TodoAdded(todo))
                
                call.respond(TodoResponse(
                    success = true,
                    message = Translations.get("message.todo_added", session.language),
                    todo = todo
                ))
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = TodoResponse(
                        success = false,
                        message = Translations.get("error.server_error", session.language)
                    )
                )
            }
        }
        
        put("/{id}") {
            val session = call.sessions.get<UserSession>()!!
            val todoId = call.parameters["id"]?.toIntOrNull()
            
            if (todoId == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = TodoResponse(
                        success = false,
                        message = "Invalid todo ID"
                    )
                )
                return@put
            }
            
            try {
                val request = call.receive<UpdateTodoRequest>()
                val updatedTodo = TodoRepository.updateTodo(
                    todoId = todoId,
                    userId = session.userId,
                    text = request.text,
                    completed = request.completed
                )
                
                if (updatedTodo != null) {
                    // Broadcast to all connected users via WebSocket
                    webSocketManager.broadcastToAll(WebSocketMessage.TodoUpdated(updatedTodo))
                    
                    call.respond(TodoResponse(
                        success = true,
                        message = Translations.get("message.todo_updated", session.language),
                        todo = updatedTodo
                    ))
                } else {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = TodoResponse(
                            success = false,
                            message = Translations.get("error.todo_not_found", session.language)
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = TodoResponse(
                        success = false,
                        message = Translations.get("error.server_error", session.language)
                    )
                )
            }
        }
        
        delete("/{id}") {
            val session = call.sessions.get<UserSession>()!!
            val todoId = call.parameters["id"]?.toIntOrNull()
            
            if (todoId == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = TodoResponse(
                        success = false,
                        message = "Invalid todo ID"
                    )
                )
                return@delete
            }
            
            val deleted = TodoRepository.deleteTodo(todoId, session.userId)
            if (deleted) {
                // Broadcast to all connected users via WebSocket
                webSocketManager.broadcastToAll(WebSocketMessage.TodoDeleted(todoId))
                
                call.respond(TodoResponse(
                    success = true,
                    message = Translations.get("message.todo_deleted", session.language)
                ))
            } else {
                call.respond(
                    status = HttpStatusCode.NotFound,
                    message = TodoResponse(
                        success = false,
                        message = Translations.get("error.todo_not_found", session.language)
                    )
                )
            }
        }
        
        delete("/completed") {
            val session = call.sessions.get<UserSession>()!!
            val count = TodoRepository.clearCompleted(session.userId)
            
            // Broadcast refresh to all users since multiple todos were deleted
            val todos = TodoRepository.getTodosByUser(session.userId)
            
            call.respond(TodoResponse(
                success = true,
                message = Translations.get("message.todos_cleared", session.language),
                todos = todos
            ))
        }
    }
}