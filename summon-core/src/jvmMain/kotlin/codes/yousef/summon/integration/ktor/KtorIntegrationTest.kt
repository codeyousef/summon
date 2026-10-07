package codes.yousef.summon.integration.ktor

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.integration.ktor.KtorRenderer.Companion.respondSummonHydrated
import codes.yousef.summon.runtime.Composable
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

/** Executes the main operation. */
fun main() {
    embeddedServer(Netty, port = 8080) {
        configureKtorIntegrationTest()
    }.start(wait = true)
}

/** Executes the configure ktor integration test operation. */
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

        get("/hydrated") {
            call.respondSummonHydrated {
                KtorTestComponent()
            }
        }

        summonRouter(basePath = "/pages")
    }
}

/** Renders ktor test component. */
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