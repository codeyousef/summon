package code.yousef.summon.integration.springboot

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import reactor.core.publisher.Mono

/**
 * Integration class for rendering Summon components in a Spring WebFlux application.
 * This class provides methods to easily render Summon components to reactive responses.
 *
 * IMPORTANT: To use this class, you must add the following dependencies to your project:
 * - org.springframework.boot:spring-boot-starter-webflux
 * - io.projectreactor.kotlin:reactor-kotlin-extensions
 */
class WebFluxRenderer {
    private val renderer = PlatformRenderer()

    /**
     * Renders a Summon component as HTML and returns it as a Mono<String>.
     * This is useful for returning HTML responses in WebFlux controllers.
     *
     * @param content The composable function that defines the UI to render
     * @return A Mono<String> containing the HTML representation of the component
     */
    fun renderHtml(content: @Composable () -> Unit): Mono<String> {
        return Mono.fromCallable {
            createHTML().html {
                head {
                    meta(charset = "UTF-8")
                    meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                    title("Summon WebFlux Application")
                    style {
                        unsafe {
                            +"""
                        body { 
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            margin: 0;
                            padding: 20px;
                            color: #333;
                        }
                        """
                        }
                    }
                }
                body {
                    setPlatformRenderer(renderer)
                    content()
                }
            }
        }
    }

    /**
     * Renders a Summon component as a stream of HTML and returns it as a Mono<String>.
     * This is useful for server-sent events (SSE) or streaming responses in WebFlux applications.
     *
     * @param content The composable function that defines the UI to render
     * @return A Mono<String> containing the HTML representation of the component
     */
    fun renderStream(content: @Composable () -> Unit): Mono<String> {
        return Mono.fromCallable {
            createHTML().html {
                head {
                    meta(charset = "UTF-8")
                    meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                    title("Summon WebFlux Stream")
                }
                body {
                    setPlatformRenderer(renderer)
                    content()
                }
            }
        }
    }
} 