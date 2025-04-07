package integrations.quarkus

import core.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import integrations.quarkus.NativeImageSupport.ReflectiveComponent
import io.quarkus.qute.Engine
import io.quarkus.qute.Template
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

/**
 * Demo of Summon integration with Quarkus.
 * This file shows example usage of the various integration components.
 */
object QuarkusDemo {

    /**
     * Example of a Summon component that will be used in a Quarkus application
     */
    @ReflectiveComponent
    class GreetingComponent(private val name: String = "World") : Composable {
        override fun <T> compose(receiver: T): T {
            // Note: This implementation would depend on how the Composable interface is defined
            // This is just a placeholder to demonstrate the concept
            return receiver
        }
    }

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
        fun greeting(): jakarta.ws.rs.core.Response {
            val component = GreetingComponent("Quarkus User")

            // Method 1: Using ResteasyIntegration utility
            return ResteasyIntegration.createResponse(component)
        }

        @GET
        @Path("/greeting-cdi")
        @Produces(MediaType.TEXT_HTML)
        fun greetingWithCDI(): String {
            val component = GreetingComponent("CDI User")

            // Method 2: Using CDI-aware renderer
            return cdiAwareRenderer.render(component)
        }

        @GET
        @Path("/not-found")
        @Produces(MediaType.TEXT_HTML)
        fun notFound(): jakarta.ws.rs.core.Response {
            val errorComponent = Column(
                content = listOf(
                    Text("Page Not Found"),
                    Button("Go Home", onClick = { /* would navigate home */ })
                )
            )

            // Using helper for common responses
            return ResteasyIntegration.Responses.notFound(errorComponent)
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

        fun renderPage(component: Composable): String {
            val template = createTemplate()
            return template.data("component", component).render()
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
    @ReflectiveComponent
    class UserProfileComponent : Composable {
        @Inject
        lateinit var userService: UserService

        override fun <T> compose(receiver: T): T {
            // This would use the injected userService
            // Just a placeholder implementation
            return receiver
        }
    }

    /**
     * Example of creating a Summon+Quarkus application programmatically
     */
    fun createApplication() {
        // Register components for native image support
        val registry = NativeImageSupport.ModuleRegistry()
        registry.register(GreetingComponent::class.java)
        registry.register(UserProfileComponent::class.java)

        // Generate reflection configuration
        val reflectionConfig = registry.generateReflectionConfig()
        println("Generated reflection config for native image: $reflectionConfig")

        // Example of creating components with CDI support
        val componentFactory = CDISupport.ComponentFactory()
        val userProfile = componentFactory.create(UserProfileComponent::class.java)

        // Example of working with CDI directly
        try {
            val cdi = jakarta.enterprise.inject.spi.CDI.current()
            val summonRenderer = cdi.select(QuarkusExtension.SummonRenderer::class.java).get()

            // Use the renderer
            val html = summonRenderer.render(userProfile)
            println("Rendered HTML: $html")
        } catch (e: Exception) {
            // Handle case where CDI is not available
            println("CDI not available: ${e.message}")
        }
    }
} 
