package code.yousef.example.quarkus

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.io.InputStream

/**
 * Static resource handler for serving Summon hydration JavaScript and other static assets
 */
@Path("/")
class StaticResourceHandler {

    /**
     * Serves the Summon button activation script
     */
    @GET
    @Path("/summon-buttons.js")
    @Produces("application/javascript")
    fun summonButtonsJs(): Response {
        val inputStream = getResourceAsStream("/META-INF/resources/summon-buttons.js")
        return if (inputStream != null) {
            Response.ok(inputStream)
                .header("Content-Type", "application/javascript")
                .header("Cache-Control", "public, max-age=3600")
                .build()
        } else {
            Response.status(404).entity("Script not found").build()
        }
    }
    
    /**
     * Serves the Summon hydration JavaScript bundle
     */
    @GET
    @Path("/summon-hydration.js")
    @Produces("application/javascript")
    fun summonHydrationJs(): Response {
        val inputStream = getResourceAsStream("/static/summon-hydration.js")
        return if (inputStream != null) {
            Response.ok(inputStream)
                .header("Content-Type", "application/javascript")
                .header("Cache-Control", "public, max-age=3600")
                .build()
        } else {
            Response.status(404)
                .entity("Summon hydration bundle not found. Please build with: ./gradlew copyJsHydrationBundle")
                .build()
        }
    }
    
    private fun getResourceAsStream(path: String): InputStream? {
        return this::class.java.getResourceAsStream(path)
    }
}