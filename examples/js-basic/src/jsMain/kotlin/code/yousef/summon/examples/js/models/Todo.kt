package code.yousef.summon.examples.js.models

import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    val id: String,
    val text: String,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String
)

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