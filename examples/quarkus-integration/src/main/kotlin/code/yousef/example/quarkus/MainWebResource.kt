package code.yousef.example.quarkus

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import code.yousef.summon.runtime.*
import code.yousef.example.quarkus.ui.TodoApp
import code.yousef.example.quarkus.ui.state.AppState
import code.yousef.example.quarkus.model.*
import code.yousef.example.quarkus.service.TodoService
import java.time.LocalDateTime

/**
 * Main Web Resource for the Todo Application
 * Serves the complete Todo app built with Summon UI components
 */
@Path("/")
class MainWebResource {

    /**
     * Home page - serves the complete todo application
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun home(): String {
        // Initialize demo data
        initializeDemoData()
        
        val renderer = PlatformRenderer()
        return renderer.renderComposableRootWithHydration {
            TodoApp()
        }
    }
    
    /**
     * Initialize demo data for the application
     */
    private fun initializeDemoData() {
        // Create demo user if not logged in
        if (!AppState.isLoggedIn.value) {
            val demoUser = PublicUser(
                id = "demo-user-id",
                username = "demo",
                email = "demo@example.com",
                themePreference = ThemePreference.LIGHT,
                languagePreference = "en",
                createdAt = LocalDateTime.now(),
                lastLoginAt = LocalDateTime.now()
            )
            
            AppState.setUserSession(demoUser, "demo-session-token")
            
            // Create demo todos
            val demoTodos = listOf(
                Todo(
                    title = "Welcome to Summon Todo App!",
                    description = "This is a demo todo created with Summon UI framework and Quarkus backend. Try creating, editing, and completing todos!",
                    priority = Priority.HIGH,
                    userId = demoUser.id
                ),
                Todo(
                    title = "Try the theme switcher",
                    description = "Click the theme toggle in the header to switch between light and dark modes",
                    priority = Priority.MEDIUM,
                    userId = demoUser.id
                ),
                Todo(
                    title = "Test language switching",
                    description = "Use the language selector to see the app in different languages",
                    priority = Priority.LOW,
                    userId = demoUser.id
                ),
                Todo(
                    title = "Completed task example",
                    description = "This task shows how completed todos look",
                    completed = true,
                    priority = Priority.MEDIUM,
                    userId = demoUser.id,
                    updatedAt = LocalDateTime.now().minusHours(2)
                ),
                Todo(
                    title = "Urgent task with due date",
                    description = "This demonstrates urgent priority and due date functionality",
                    priority = Priority.URGENT,
                    dueDate = LocalDateTime.now().plusDays(1),
                    userId = demoUser.id
                )
            )
            
            AppState.setTodos(demoTodos)
            
            // Set stats
            val stats = code.yousef.example.quarkus.service.TodoStats(
                total = demoTodos.size,
                completed = demoTodos.count { it.completed },
                pending = demoTodos.count { !it.completed },
                overdue = demoTodos.count { 
                    !it.completed && it.dueDate != null && it.dueDate.isBefore(LocalDateTime.now()) 
                },
                highPriority = demoTodos.count { it.priority == Priority.HIGH || it.priority == Priority.URGENT }
            )
            AppState.setTodoStats(stats)
        }
    }
}