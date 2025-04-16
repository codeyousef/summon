package code.yousef.example.quarkus

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.Logger

/**
 * Minimal test resource to verify routing
 */
@Path("/test")
@ApplicationScoped
class MinimalTestResource {
    private val logger = Logger.getLogger(MinimalTestResource::class.java)

    init {
        logger.info("MinimalTestResource initialized")
        println("MinimalTestResource initialized")
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello(): String {
        logger.info("MinimalTestResource.hello() called")
        println("MinimalTestResource.hello() called")
        return "Hello from minimal test resource"
    }

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    fun ping(): String {
        logger.info("MinimalTestResource.ping() called")
        println("MinimalTestResource.ping() called")
        return "pong"
    }

    @GET
    @Path("/html")
    @Produces(MediaType.TEXT_HTML)
    fun testHtml(): String {
        logger.info("MinimalTestResource.testHtml() called")
        println("MinimalTestResource.testHtml() called")
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Minimal HTML Test</title>
            </head>
            <body>
                <h1>Minimal HTML Test</h1>
                <p>This is a simple HTML page for testing.</p>
            </body>
            </html>
        """.trimIndent()
    }
} 