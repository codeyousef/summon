package codes.yousef.summon.integration.springboot

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.runtime.Composable
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/** Represents spring boot integration test app. */
@SpringBootApplication
class SpringBootIntegrationTestApp

/**
 * Executes the main operation.
 *
 * @param args The args value.
 */
fun main(args: Array<String>) {
    runApplication<SpringBootIntegrationTestApp>(*args)
}

/** Represents spring boot test controller. */
@Controller
class SpringBootTestController {
    private val renderer = SpringBootRenderer()

    /**
     * Executes the index operation.
     *
     * @param response The response value.
     */
    @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
    fun index(response: HttpServletResponse) {
        renderer.renderHtml(response) {
            SpringBootTestComponent()
        }
    }

    /**
     * Executes the stream operation.
     *
     * @param response The response value.
     */
    @GetMapping("/stream", produces = [MediaType.TEXT_HTML_VALUE])
    fun stream(response: HttpServletResponse) {
        renderer.renderStream(response) {
            SpringBootTestComponent()
        }
    }
}

/** Renders spring boot test component. */
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