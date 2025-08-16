package code.yousef.summon.integration.ktor

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.runtime.Composable
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        configureKtorIntegrationTest()
    }.start(wait = true)
}

fun Application.configureKtorIntegrationTest() {
    val renderer = KtorRenderer()

    routing {
        get("/") {
            renderer.renderHtml(call) {
                KtorTestComponent()
            }
        }

        get("/stream") {
            renderer.renderStream(call) {
                KtorTestComponent()
            }
        }
    }
}

@Composable
fun KtorTestComponent() {
    Column {
        Text("Hello from Ktor Integration")
        Button(
            onClick = { /* Handle click */ },
            label = "Click me"
        )
    }
} 