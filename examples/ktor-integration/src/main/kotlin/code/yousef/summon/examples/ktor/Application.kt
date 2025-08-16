package code.yousef.summon.examples.ktor

import code.yousef.summon.examples.ktor.database.DatabaseConfig
import code.yousef.summon.examples.ktor.models.UserSession
import code.yousef.summon.examples.ktor.routing.authRoutes
import code.yousef.summon.examples.ktor.routing.pageRoutes
import code.yousef.summon.examples.ktor.routing.todoRoutes
import code.yousef.summon.examples.ktor.routing.webSocketRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.http.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import java.time.Duration

fun main() {
    // Initialize database
    DatabaseConfig.init()
    
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureSessions()
    configureWebSockets()
    configureCORS()
    configureStatusPages()
    configureCallLogging()
    configureDefaultHeaders()
    configureRouting()
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

fun Application.configureSessions() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 86400 // 24 hours
            cookie.httpOnly = true
            cookie.secure = false // Set to true in production with HTTPS
            cookie.extensions["SameSite"] = "lax"
            serializer = KotlinxSessionSerializer()
        }
    }
}

fun Application.configureWebSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}

fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowCredentials = true
        anyHost() // Allow any host for development - restrict in production
    }
}

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf(
                    "success" to false,
                    "message" to "Internal server error",
                    "error" to (cause.message ?: "Unknown error")
                )
            )
        }
        
        status(HttpStatusCode.NotFound) { call, _ ->
            // Redirect 404s to home page (for SPA routing)
            call.respondRedirect("/")
        }
    }
}

fun Application.configureCallLogging() {
    install(CallLogging) {
        level = org.slf4j.event.Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
}

fun Application.configureDefaultHeaders() {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
        header("X-Framework", "Summon")
    }
}

fun Application.configureRouting() {
    routing {
        // Page routes (HTML)
        pageRoutes()
        
        // API routes
        authRoutes()
        todoRoutes()
        
        // WebSocket routes
        webSocketRoutes()
        
        // Health check
        get("/health") {
            call.respond(mapOf(
                "status" to "ok",
                "timestamp" to System.currentTimeMillis(),
                "framework" to "Summon + Ktor"
            ))
        }
        
        // API info
        get("/api") {
            call.respond(mapOf(
                "name" to "Ktor Todo API",
                "version" to "1.0.0",
                "framework" to "Summon",
                "features" to listOf(
                    "Real-time WebSocket updates",
                    "Session-based authentication", 
                    "Multi-language support",
                    "Theme switching",
                    "SQLite database with Exposed ORM"
                )
            ))
        }
    }
}