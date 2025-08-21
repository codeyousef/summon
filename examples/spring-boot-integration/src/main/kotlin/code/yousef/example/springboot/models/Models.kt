package code.yousef.example.springboot.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

// JPA Entities
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(unique = true, nullable = false)
    @NotBlank
    @Size(min = 3, max = 50)
    val username: String = "",
    
    @Column(unique = true, nullable = false)
    @NotBlank
    @Email
    val email: String = "",
    
    @Column(nullable = false)
    @JsonIgnore
    var passwordHash: String = "",
    
    @Column(nullable = false)
    var language: String = "en",
    
    @Column(nullable = false)
    var theme: String = "light",
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonIgnore
    val todos: MutableList<Todo> = mutableListOf()
)

@Entity
@Table(name = "todos")
@EntityListeners(AuditingEntityListener::class)
data class Todo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false, length = 500)
    @NotBlank
    @Size(max = 500)
    var text: String = "",
    
    @Column(nullable = false)
    var completed: Boolean = false,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    val user: User? = null,
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

// DTOs for API responses
data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val language: String,
    val theme: String,
    val createdAt: String
)

data class TodoDto(
    val id: Long,
    val text: String,
    val completed: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Request DTOs
data class LoginRequest(
    @NotBlank
    val username: String,
    
    @NotBlank
    val password: String
)

data class RegisterRequest(
    @NotBlank
    @Size(min = 3, max = 50)
    val username: String,
    
    @NotBlank
    @Email
    val email: String,
    
    @NotBlank
    @Size(min = 6, max = 100)
    val password: String
)

data class CreateTodoRequest(
    @NotBlank
    @Size(max = 500)
    val text: String
)

data class UpdateTodoRequest(
    @Size(max = 500)
    val text: String? = null,
    val completed: Boolean? = null
)

data class UpdateUserSettingsRequest(
    val language: String? = null,
    val theme: String? = null
)

// Response DTOs
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val user: UserDto? = null
)

data class TodoResponse(
    val success: Boolean,
    val message: String,
    val todo: TodoDto? = null,
    val todos: List<TodoDto>? = null
)

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: Any? = null
)

// WebSocket message types
sealed class WebSocketMessage {
    data class TodoAdded(val todo: TodoDto) : WebSocketMessage()
    data class TodoUpdated(val todo: TodoDto) : WebSocketMessage()
    data class TodoDeleted(val todoId: Long) : WebSocketMessage()
    data class UserConnected(val username: String) : WebSocketMessage()
    data class UserDisconnected(val username: String) : WebSocketMessage()
    data class ThemeChanged(val theme: String) : WebSocketMessage()
}

// Enums
enum class TodoFilter {
    ALL,
    ACTIVE,
    COMPLETED
}

enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    SPANISH("es", "Español"),
    FRENCH("fr", "Français")
}

enum class Theme(val value: String) {
    LIGHT("light"),
    DARK("dark")
}

// Extension functions for entity to DTO conversion
fun User.toDto(): UserDto = UserDto(
    id = id,
    username = username,
    email = email,
    language = language,
    theme = theme,
    createdAt = createdAt.toString()
)

fun Todo.toDto(): TodoDto = TodoDto(
    id = id,
    text = text,
    completed = completed,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString()
)