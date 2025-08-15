package code.yousef.summon.integration.ktor

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.integration.ktor.KtorRenderer.Companion.respondSummon
import code.yousef.summon.integration.ktor.KtorRenderer.Companion.summon
import code.yousef.summon.integration.ktor.KtorStreamingSupport.respondStreamingSummon
import code.yousef.summon.runtime.Composable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Demo of Summon integration with Ktor.
 * This file shows example usage of the various integration components.
 */
object KtorDemo {

    /**
     * Example of a Summon component that will be used in a Ktor application
     */
    @Composable
    fun GreetingComponent(name: String = "World") {
        Column {
            Text(text = "Hello, $name!")
            Button(
                label = "Click me",
                onClick = { /* handle click */ }
            )
        }
    }

    /**
     * Example Ktor application setup
     */
    fun createKtorApp(): Application.() -> Unit = {
        // Setup routing
        routing {
            // Method 1: Using the summon extension function
            summon("/hello", title = "Summon Hello") {
                GreetingComponent("Ktor User")
            }

            // Method 2: Using the respondSummon extension function
            get("/greeting") {
                call.respondSummon("Greeting Page") {
                    GreetingComponent("Greeting User")
                }
            }

            // Method 3: Using the streaming support for large pages
            get("/stream") {
                call.respondStreamingSummon("Streaming Demo") {
                    Column {
                        // Generate a large amount of content
                        repeat(1000) { index ->
                            Text("Line $index: Streamed content")
                        }
                    }
                }
            }

            // Method 4: Using traditional Ktor HTML DSL with Summon components
            get("/hybrid") {
                val renderer = KtorRenderer()

                // Render a component to a string
                val summonContent = renderer.renderToString {
                    GreetingComponent("Hybrid User")
                }

                // Use it in a regular Ktor HTML response
                call.respondText(summonContent, ContentType.Text.Html)
            }
        }
    }

    /**
     * Example main function to start the Ktor server
     */
    fun main() {
        embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = createKtorApp())
            .start(wait = true)
    }
} 