package code.yousef.example.quarkus

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

/**
 * A simple REST endpoint for the Quarkus example.
 */
@Path("/hello")
class HelloResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello() = "Hello from Quarkus!"

    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    fun helloJson() = mapOf(
        "message" to "Hello from Quarkus!",
        "framework" to "Quarkus",
        "version" to "3.6.5"
    )

    @GET
    @Path("/html")
    @Produces(MediaType.TEXT_HTML)
    fun helloHtml() = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Hello from Quarkus</title>
            <style>
                body { 
                    font-family: system-ui, sans-serif; 
                    line-height: 1.5;
                    padding: 2rem;
                    max-width: 800px;
                    margin: 0 auto;
                    color: #333;
                }
                h1 { color: #4695EB; }
                .card {
                    border: 1px solid #ddd;
                    border-radius: 8px;
                    padding: 1rem;
                    margin: 1rem 0;
                    background-color: #f9f9f9;
                }
            </style>
        </head>
        <body>
            <h1>Hello from Quarkus!</h1>
            <div class="card">
                <p>This is a simple example showing Quarkus in action.</p>
                <p>When ready to use Summon, uncomment the dependencies in build.gradle.kts</p>
            </div>
        </body>
        </html>
    """.trimIndent()

    /*
     * The following code demonstrates how to use Summon with Quarkus:
     *
     * @GET
     * @Path("/ui")
     * @Produces(MediaType.TEXT_HTML)
     * fun renderUI(): String {
     *     return summonRenderer.render {
     *         WelcomeComponent("Quarkus User")
     *     }
     * }
     */
} 