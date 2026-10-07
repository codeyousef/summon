package codes.yousef.summon.integration.springboot

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.runtime.Composable
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/** Represents web flux integration test app. */
@SpringBootApplication
class WebFluxIntegrationTestApp

/**
 * Executes the main operation.
 *
 * @param args The args value.
 */
fun main(args: Array<String>) {
    runApplication<WebFluxIntegrationTestApp>(*args)
}

/** Represents web flux test controller. */
@RestController
class WebFluxTestController {
    private val renderer = WebFluxRenderer()

    /**
     * Executes the home operation.
     *
     * @return The resulting value.
     */
    @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
    fun home(): Mono<String> {
        return renderer.renderHtml {
            WebFluxTestComponent()
        }
    }

    /**
     * Executes the stream operation.
     *
     * @return The resulting value.
     */
    @GetMapping("/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun stream(): Mono<String> {
        return renderer.renderStream {
            WebFluxTestComponent()
        }
    }
}

/** Renders web flux test component. */
@Composable
fun WebFluxTestComponent() {
    Column {
        Text("Hello from Spring WebFlux!")
        Button(onClick = {}, label = "Click me")
    }
} 