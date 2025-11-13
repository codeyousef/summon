package codes.yousef.summon.integration.quarkus

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.Produces
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.MessageBodyWriter
import jakarta.ws.rs.ext.Provider
import java.io.OutputStream
import java.lang.reflect.Type

/**
 * Integration with RESTEasy for rendering Summon components directly in REST endpoints.
 *
 * This provider allows returning Summon component objects directly from JAX-RS endpoints
 * and automatically renders them as HTML.
 *
 * Usage:
 *
 * ```kotlin
 * @Path("/hello")
 * class HelloResource {
 *     @GET
 *     @Produces(MediaType.TEXT_HTML)
 *     fun hello(): Any {
 *         // Return a component...
 *     }
 * }
 * ```
 */
@Provider
@Produces(MediaType.TEXT_HTML)
@ApplicationScoped
class SummonMessageBodyWriter : MessageBodyWriter<Any> {

    override fun isWriteable(
        type: Class<*>?,
        genericType: Type?,
        annotations: Array<Annotation>?,
        mediaType: MediaType?
    ): Boolean {
        return type != null && type.annotations.any {
            it.annotationClass.java.simpleName == "Composable"
        }
    }

    override fun writeTo(
        component: Any,
        type: Class<*>?,
        genericType: Type?,
        annotations: Array<Annotation>?,
        mediaType: MediaType?,
        httpHeaders: MultivaluedMap<String, Any>?,
        entityStream: OutputStream?
    ) {
        try {
            // Simple HTML rendering as a placeholder
            val html = "<div class=\"summon-component\">Component: ${component::class.simpleName}</div>"
            entityStream?.write(html.toByteArray(Charsets.UTF_8))
        } catch (e: Exception) {
            throw WebApplicationException("Error rendering Summon component", e)
        }
    }

    override fun getSize(
        component: Any?,
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
    fun createResponse(component: Any): Response {
        // Simple HTML rendering as a placeholder
        val html = "<div class=\"summon-component\">Component: ${component::class.simpleName}</div>"

        return Response
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
        fun notFound(component: Any): Response {
            // Simple HTML rendering as a placeholder
            val html = "<div class=\"summon-component\">Component: ${component::class.simpleName}</div>"

            return Response
                .status(Response.Status.NOT_FOUND)
                .entity(html)
                .type(MediaType.TEXT_HTML)
                .build()
        }

        /**
         * Creates a redirect response.
         */
        fun redirect(location: String): Response {
            return Response
                .status(Response.Status.FOUND)
                .header("Location", location)
                .build()
        }
    }
} 
