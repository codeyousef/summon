package codes.yousef.summon.test

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.integration.ktor.KtorRenderer.Companion.respondSummonHydrated
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.CallbackRegistry
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

@Composable
fun TestApp() {
    Column(modifier = Modifier()) {
        Button(
            onClick = {
                println("TEST BUTTON CLICKED!")
            },
            label = "Test Button",
            modifier = Modifier()
        )
    }
}

fun main() {
    // Clear any existing callbacks
    CallbackRegistry.clear()
    
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondSummonHydrated {
                    TestApp()
                }
            }
        }
    }.start(wait = true)
}
