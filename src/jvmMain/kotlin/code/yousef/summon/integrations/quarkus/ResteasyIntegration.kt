package integrations.quarkus

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

import core.Composable
import JvmPlatformRenderer
import render
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.Produces
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.ext.MessageBodyWriter
import jakarta.ws.rs.ext.Provider
import java.io.OutputStream
import java.lang.reflect.Type

/**
 * Integration with RESTEasy for rendering Summon components directly in REST endpoints.
 *
 * This provider allows returning Summon Composable objects directly from JAX-RS endpoints
 * and automatically renders them as HTML.
 *
 * Usage:
 *
 * ```kotlin
 * @Path("/hello")
 * class HelloResource {
 *     @GET
 *     @Produces(MediaType.TEXT_HTML)
 *     fun hello(): Composable {
 *         return Column(
 *             content = listOf(
 *                 Text("Hello from Summon + RESTEasy!")
 *             )
 *         )
 *     }
 * }
 * ```
 */
@Provider
@Produces(MediaType.TEXT_HTML)
@ApplicationScoped
class SummonMessageBodyWriter : MessageBodyWriter<Composable> {

    private val renderer = JvmPlatformRenderer()

    override fun isWriteable(
        type: Class<*>?,
        genericType: Type?,
        annotations: Array<Annotation>?,
        mediaType: MediaType?
    ): Boolean {
        return type != null && Composable::class.java.isAssignableFrom(type)
    }

    override fun writeTo(
        component: Composable,
        type: Class<*>?,
        genericType: Type?,
        annotations: Array<Annotation>?,
        mediaType: MediaType?,
        httpHeaders: MultivaluedMap<String, Any>?,
        entityStream: OutputStream?
    ) {
        try {
            val html = renderer.render(component)
            entityStream?.write(html.toByteArray(Charsets.UTF_8))
        } catch (e: Exception) {
            throw WebApplicationException("Error rendering Summon component", e)
        }
    }

    override fun getSize(
        component: Composable?,
        type: Class<*>?,
        genericType: Type?,
        annotations: Array<out Annotation>?,
        mediaType: MediaType?
    ): Long {
        return -1 // Let RESTEasy determine the content length
    }
}

/**
 * Extension functions for Summon components to make RESTEasy integration easier.
 */
object ResteasyIntegration {

    /**
     * Creates a Summon component response suitable for REST endpoints.
     * Adds proper content headers for HTML response.
     *
     * @param component The Summon component to render
     * @return A response object that can be returned from a JAX-RS endpoint
     */
    fun createResponse(component: Composable): jakarta.ws.rs.core.Response {
        val renderer = JvmPlatformRenderer()
        val html = renderer.render(component)

        return jakarta.ws.rs.core.Response
            .ok(html)
            .type(MediaType.TEXT_HTML)
            .build()
    }

    /**
     * Helper functions for common response scenarios.
     */
    object Responses {
        /**
         * Creates a 404 Not Found response with a Summon component.
         */
        fun notFound(component: Composable): jakarta.ws.rs.core.Response {
            val renderer = JvmPlatformRenderer()
            val html = renderer.render(component)

            return jakarta.ws.rs.core.Response
                .status(jakarta.ws.rs.core.Response.Status.NOT_FOUND)
                .entity(html)
                .type(MediaType.TEXT_HTML)
                .build()
        }

        /**
         * Creates a redirect response.
         */
        fun redirect(location: String): jakarta.ws.rs.core.Response {
            return jakarta.ws.rs.core.Response
                .status(jakarta.ws.rs.core.Response.Status.FOUND)
                .header("Location", location)
                .build()
        }
    }
} 
