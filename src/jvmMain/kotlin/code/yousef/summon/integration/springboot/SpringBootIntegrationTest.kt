package code.yousef.summon.integration.springboot

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.runtime.Composable
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType

@SpringBootApplication
class SpringBootIntegrationTestApp

fun main(args: Array<String>) {
    runApplication<SpringBootIntegrationTestApp>(*args)
}

@Controller
class SpringBootTestController {
    private val renderer = SpringBootRenderer()
    
    @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
    fun index(response: HttpServletResponse) {
        renderer.renderHtml(response) {
            SpringBootTestComponent()
        }
    }
    
    @GetMapping("/stream", produces = [MediaType.TEXT_HTML_VALUE])
    fun stream(response: HttpServletResponse) {
        renderer.renderStream(response) {
            SpringBootTestComponent()
        }
    }
}

@Composable
fun SpringBootTestComponent() {
    Column {
        Text("Hello from Spring Boot Integration")
        Button(
            onClick = { /* Handle click */ },
            label = "Click me"
        )
    }
} 