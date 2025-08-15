package code.yousef.summon.integration.springboot

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.runtime.Composable
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@SpringBootApplication
class WebFluxIntegrationTestApp

fun main(args: Array<String>) {
    runApplication<WebFluxIntegrationTestApp>(*args)
}

@RestController
class WebFluxTestController {
    private val renderer = WebFluxRenderer()

    @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
    fun home(): Mono<String> {
        return renderer.renderHtml {
            WebFluxTestComponent()
        }
    }

    @GetMapping("/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun stream(): Mono<String> {
        return renderer.renderStream {
            WebFluxTestComponent()
        }
    }
}

@Composable
fun WebFluxTestComponent() {
    Column {
        Text("Hello from Spring WebFlux!")
        Button(onClick = {}, label = "Click me")
    }
} 