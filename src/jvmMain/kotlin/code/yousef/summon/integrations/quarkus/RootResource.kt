package code.yousef.summon.integrations.quarkus

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import java.net.URI

/**
 * Root resource that redirects to the Summon resource
 */
@Path("/")
class RootResource {
    
    /**
     * Redirect root requests to the Summon endpoint
     */
    @GET
    fun redirectToSummon(): Response {
        return Response.temporaryRedirect(URI.create("/summon/")).build()
    }
} 