package code.yousef.summon.integrations.quarkus


import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.integrations.quarkus.NativeImageSupport.ReflectiveComponent
import code.yousef.summon.runtime.Composable
import io.quarkus.qute.Engine
import io.quarkus.qute.Template
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.spi.CDI
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

/**
 * Demo of Summon integration with Quarkus.
 * This file shows example usage of the various integration components.
 */
object QuarkusDemo {

    /**
     * Example of a Summon component that will be used in a Quarkus application
     */
    @Composable
    fun GreetingComponent(name: String = "World") {
        Text(text = "Hello, $name!")
    }

    /**
     * Class for reflection registration - used for native image support
     */
    @ReflectiveComponent
    class GreetingComponentHolder

    /**
     * Example of a RESTEasy resource using Summon components
     */
    @Path("/demo")
    class DemoResource {

        @Inject
        lateinit var cdiAwareRenderer: CDISupport.CDIAwareRenderer

        @GET
        @Path("/greeting")
        @Produces(MediaType.TEXT_HTML)
        fun greeting(): Response {
            // Method 1: Using ResteasyIntegration utility
            return ResteasyIntegration.createResponse {
                GreetingComponent("Quarkus User")
            }
        }

        @GET
        @Path("/greeting-cdi")
        @Produces(MediaType.TEXT_HTML)
        fun greetingWithCDI(): String {
            // Method 2: Using CDI-aware renderer
            return cdiAwareRenderer.render {
                GreetingComponent("CDI User")
            }
        }

        @GET
        @Path("/not-found")
        @Produces(MediaType.TEXT_HTML)
        fun notFound(): Response {
            // Using helper for common responses
            return ResteasyIntegration.Responses.notFound {
                Column {
                    Text(text = "Page Not Found")
                    Button(
                        label = "Go Home",
                        onClick = { /* would navigate home */ }
                    )
                }
            }
        }
    }

    /**
     * Example of Qute template integration with Summon
     */
    @ApplicationScoped
    class TemplateService {

        @Inject
        lateinit var engine: Engine

        fun createTemplate(): Template {
            return engine.parse(
                """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Summon with Qute</title>
                </head>
                <body>
                    <h1>Qute Template with Summon Components</h1>
                    
                    <!-- Using Summon component in a Qute template -->
                    {#let greeting=summon:component(component)}
                        {greeting}
                    {/let}
                    
                    <!-- Using template extension -->
                    <div class="container">
                        {component.render}
                    </div>
                </body>
                </html>
            """.trimIndent()
            )
        }

        fun renderPage(content: @Composable () -> Unit): String {
            val template = createTemplate()
            return template.data("component", content).render()
        }
    }

    /**
     * Example of a CDI-injectable service used by Summon components
     */
    @ApplicationScoped
    class UserService {
        fun getCurrentUser(): String {
            return "Current User"
        }
    }

    /**
     * Example of a component that uses CDI
     */
    @Composable
    fun UserProfileComponent() {
        // This would ideally access the injected UserService
        // For demonstration purposes, we'll just render some text
        Text(text = "User Profile for Current User")
    }

    /**
     * Class for reflection registration - used for native image support
     */
    @ReflectiveComponent
    class UserProfileComponentHolder

    /**
     * Example of creating a Summon+Quarkus application programmatically
     */
    fun createApplication() {
        // Register components for native image support
        val registry = NativeImageSupport.ModuleRegistry()
        registry.register(GreetingComponentHolder::class.java)
        registry.register(UserProfileComponentHolder::class.java)

        // Generate reflection configuration
        val reflectionConfig = registry.generateReflectionConfig()
        println("Generated reflection config for native image: $reflectionConfig")

        // Example of creating components with CDI support
        val componentFactory = CDISupport.ComponentFactory()
        val userProfileFunction = { UserProfileComponent() }

        // Example of working with CDI directly
        try {
            val cdi = CDI.current()
            val summonRenderer = cdi.select(QuarkusExtension.SummonRenderer::class.java).get()

            // Use the renderer
            val html = summonRenderer.render(userProfileFunction)
            println("Rendered HTML: $html")
        } catch (e: Exception) {
            // Handle case where CDI is not available
            println("CDI not available: ${e.message}")
        }
    }
} 
