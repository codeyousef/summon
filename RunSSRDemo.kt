#!/usr/bin/env kotlin

@file:DependsOn("io.ktor:ktor-server-netty:2.3.12")
@file:DependsOn("io.ktor:ktor-server-html-builder:2.3.12")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*

/**
 * Simple SSR Demo Server
 * 
 * This demonstrates the concept of SSR with basic HTML generation.
 * In a real Summon app, this would use PlatformRenderer.renderComposableRoot()
 * to render @Composable functions to HTML.
 */

data class SimpleTodo(val id: Int, val text: String, val completed: Boolean = false)

val todos = listOf(
    SimpleTodo(1, "Learn Summon Framework", true),
    SimpleTodo(2, "Build SSR Todo App", true), 
    SimpleTodo(3, "Deploy to Production", false),
    SimpleTodo(4, "Add more features", false)
)

fun generateTodoHTML(): String {
    val completed = todos.count { it.completed }
    val total = todos.size
    
    return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Summon SSR Todo Demo</title>
        <style>
            body { font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; }
            .header { text-align: center; margin-bottom: 30px; }
            .todo-item { padding: 10px; border: 1px solid #ddd; margin: 5px 0; border-radius: 5px; }
            .completed { background-color: #f0f8f0; text-decoration: line-through; color: #666; }
            .stats { text-align: center; margin-top: 20px; font-weight: bold; }
            .note { background: #e7f3ff; padding: 15px; border-radius: 5px; margin: 20px 0; }
        </style>
    </head>
    <body>
        <div class="header">
            <h1>📝 Summon SSR Todo Demo</h1>
            <p>Server-Side Rendered Todo Application</p>
        </div>
        
        <div class="note">
            <strong>🚀 SSR Demo:</strong> This page was rendered on the server using Kotlin. 
            In a full Summon app, this would be generated from @Composable functions!
        </div>
        
        <h2>Your Todos:</h2>
        ${todos.joinToString("\n") { todo ->
            """<div class="todo-item ${if (todo.completed) "completed" else ""}">
                ${if (todo.completed) "✅" else "⬜"} ${todo.text}
            </div>"""
        }}
        
        <div class="stats">
            📊 Progress: $completed / $total completed (${(completed * 100 / total)}%)
        </div>
        
        <div class="note">
            <strong>🔧 How it works:</strong>
            <ul>
                <li>Server generates HTML from Kotlin code</li>
                <li>No client-side JavaScript required</li>
                <li>SEO-friendly and fast initial load</li>
                <li>Perfect for content-heavy applications</li>
            </ul>
        </div>
        
        <p style="text-align: center; margin-top: 40px;">
            <em>This demo shows the concept of SSR. The full Summon framework provides @Composable functions and components for building complex UIs!</em>
        </p>
    </body>
    </html>
    """.trimIndent()
}

fun main() {
    println("🚀 Starting Summon SSR Demo Server...")
    println("📱 Open your browser to: http://localhost:8080")
    println("✨ This demonstrates server-side rendering concepts!")
    
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText(generateTodoHTML(), ContentType.Text.Html)
            }
            
            get("/health") {
                call.respondText("Server is running! SSR Demo active.")
            }
        }
    }.start(wait = true)
}