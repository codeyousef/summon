package code.yousef.example.todo

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.response.*

/**
 * Simple SSR Todo App - simplified version that should definitely work
 */

data class SimpleTodo(val id: Int, val text: String, val completed: Boolean = false)

object SimpleTodoStorage {
    private val todos = mutableListOf<SimpleTodo>()
    private var nextId = 1
    
    init {
        // Add some sample todos
        todos.add(SimpleTodo(nextId++, "Learn Summon SSR"))
        todos.add(SimpleTodo(nextId++, "Build a todo app", true))
        todos.add(SimpleTodo(nextId++, "Deploy to production"))
    }
    
    fun getAllTodos() = todos.toList()
}

@Composable
fun SimpleTodoApp() {
    Column {
        Text("📝 Summon SSR Todo App")
        Text("A simple server-side rendered todo application")
        
        val todos = SimpleTodoStorage.getAllTodos()
        
        Text("Your Todos:")
        
        todos.forEach { todo ->
            Text("${if (todo.completed) "✅" else "⬜"} ${todo.text}")
        }
        
        val completed = todos.count { it.completed }
        val total = todos.size
        Text("Completed: $completed / $total")
        
        Button(
            onClick = { /* Server side action */ },
            label = "Refresh"
        )
    }
}

fun main() {
    // Initialize Summon renderer
    val renderer = PlatformRenderer()
    setPlatformRenderer(renderer)
    
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                val html = renderer.renderComposableRoot {
                    SimpleTodoApp()
                }
                call.respondText(html, ContentType.Text.Html)
            }
        }
    }.start(wait = true)
    
    println("🚀 Simple Summon SSR Todo App started at http://localhost:8080")
}