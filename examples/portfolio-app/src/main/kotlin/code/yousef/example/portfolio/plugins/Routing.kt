package code.yousef.example.portfolio.plugins

import code.yousef.example.portfolio.routes.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // Static resources
        staticResources("/static", "static")
        
        // Main portfolio routes
        portfolioRoutes()
        
        // Admin routes
        adminRoutes()
        
        // API routes
        apiRoutes()
        
        // Health check
        healthRoutes()
    }
}