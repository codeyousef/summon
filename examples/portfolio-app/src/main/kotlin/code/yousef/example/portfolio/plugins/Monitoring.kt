package code.yousef.example.portfolio.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.http.*
import io.ktor.server.response.*

fun Application.configureMonitoring() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception", cause)
            call.respondText(
                text = "500: Internal Server Error - ${cause.message}",
                status = HttpStatusCode.InternalServerError
            )
        }
        
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respondText(
                text = "404: Page Not Found",
                status = HttpStatusCode.NotFound
            )
        }
    }
}