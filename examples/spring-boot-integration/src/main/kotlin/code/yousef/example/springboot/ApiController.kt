package code.yousef.example.springboot

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ApiController {

    @GetMapping("/health")
    fun health(): Map<String, Any> {
        return mapOf(
            "status" to "ok",
            "timestamp" to System.currentTimeMillis(),
            "framework" to "Summon + Spring Boot",
            "version" to "1.0.0",
            "features" to listOf(
                "JWT Authentication",
                "H2 Database with JPA",
                "Multi-language support",
                "Theme switching",
                "Reactive UI with Summon components"
            )
        )
    }

    @GetMapping("/info")
    fun info(): Map<String, String> {
        return mapOf(
            "name" to "Spring Boot Todo API",
            "description" to "Todo application with JWT authentication and H2 database",
            "framework" to "Summon",
            "platform" to "Spring Boot",
            "database" to "H2",
            "authentication" to "JWT"
        )
    }
}