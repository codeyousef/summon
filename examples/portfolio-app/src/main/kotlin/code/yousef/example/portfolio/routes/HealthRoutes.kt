package code.yousef.example.portfolio.routes

import code.yousef.example.portfolio.plugins.dbQuery
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class HealthStatus(
    val status: String,
    val checks: Map<String, HealthCheck>
)

@Serializable
data class HealthCheck(
    val status: String,
    val message: String? = null
)

fun Route.healthRoutes() {
    get("/health") {
        val databaseHealth = checkDatabaseHealth()
        
        val overallStatus = if (databaseHealth.status == "UP") "UP" else "DOWN"
        
        val healthStatus = HealthStatus(
            status = overallStatus,
            checks = mapOf(
                "database" to databaseHealth
            )
        )
        
        val statusCode = if (overallStatus == "UP") HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable
        call.respond(statusCode, healthStatus)
    }
    
    get("/health/ready") {
        // Simple readiness check
        call.respond(HttpStatusCode.OK, mapOf("status" to "ready"))
    }
    
    get("/health/live") {
        // Simple liveness check
        call.respond(HttpStatusCode.OK, mapOf("status" to "alive"))
    }
}

private suspend fun checkDatabaseHealth(): HealthCheck {
    return try {
        dbQuery {
            // Try to execute a simple query
            code.yousef.example.portfolio.plugins.Users.selectAll().count()
        }
        HealthCheck(
            status = "UP",
            message = "Database connection successful"
        )
    } catch (e: Exception) {
        HealthCheck(
            status = "DOWN",
            message = "Database connection failed: ${e.message}"
        )
    }
}