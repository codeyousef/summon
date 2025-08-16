package code.yousef.summon.examples.ktor.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

// Database Tables
object Users : IntIdTable() {
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 60)
    val createdAt = timestamp("created_at").default(Clock.System.now())
}

object Todos : IntIdTable() {
    val userId = reference("user_id", Users)
    val text = varchar("text", 500)
    val completed = bool("completed").default(false)
    val createdAt = timestamp("created_at").default(Clock.System.now())
    val updatedAt = timestamp("updated_at").default(Clock.System.now())
}

// DAO Entities
class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    
    var username by Users.username
    var email by Users.email
    var passwordHash by Users.passwordHash
    var createdAt by Users.createdAt
    
    fun toDto() = UserDto(
        id = id.value,
        username = username,
        email = email,
        createdAt = createdAt
    )
}

class Todo(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Todo>(Todos)
    
    var userId by Todos.userId
    var text by Todos.text
    var completed by Todos.completed
    var createdAt by Todos.createdAt
    var updatedAt by Todos.updatedAt
    
    fun toDto() = TodoDto(
        id = id.value,
        text = text,
        completed = completed,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// Data Transfer Objects
@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    val createdAt: Instant
)

@Serializable
data class TodoDto(
    val id: Int,
    val text: String,
    val completed: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
data class CreateTodoRequest(
    val text: String
)

@Serializable
data class UpdateTodoRequest(
    val text: String? = null,
    val completed: Boolean? = null
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val user: UserDto? = null
)

@Serializable
data class TodoResponse(
    val success: Boolean,
    val message: String,
    val todo: TodoDto? = null,
    val todos: List<TodoDto>? = null
)

// Session data
@Serializable
data class UserSession(
    val userId: Int,
    val username: String,
    val language: String = "en",
    val theme: String = "light"
)

// WebSocket message types
@Serializable
sealed class WebSocketMessage {
    @Serializable
    data class TodoAdded(val todo: TodoDto) : WebSocketMessage()
    
    @Serializable
    data class TodoUpdated(val todo: TodoDto) : WebSocketMessage()
    
    @Serializable
    data class TodoDeleted(val todoId: Int) : WebSocketMessage()
    
    @Serializable
    data class UserConnected(val username: String) : WebSocketMessage()
    
    @Serializable
    data class UserDisconnected(val username: String) : WebSocketMessage()
    
    @Serializable
    data class ThemeChanged(val theme: String) : WebSocketMessage()
}

// Enums
enum class TodoFilter {
    ALL,
    ACTIVE,
    COMPLETED
}

enum class Language(val code: String, val name: String) {
    ENGLISH("en", "English"),
    SPANISH("es", "Español"),
    FRENCH("fr", "Français")
}

enum class Theme(val value: String) {
    LIGHT("light"),
    DARK("dark")
}