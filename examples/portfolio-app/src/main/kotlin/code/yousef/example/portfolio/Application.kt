package code.yousef.example.portfolio

import code.yousef.example.portfolio.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory

fun main() {
    embeddedServer(
        Netty,
        port = System.getenv("PORT")?.toInt() ?: 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    // Install Koin for dependency injection
    install(Koin) {
        slf4jLogger()
        modules(appModule, databaseModule, repositoryModule)
    }
    
    // Configure plugins
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureDatabase()
    configureRouting()
    configureMonitoring()
}