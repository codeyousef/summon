package code.yousef.example.quarkus

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier

/**
 * Test Web Resource to isolate PlatformRenderer issues
 */
@Path("/test")
class TestWebResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    fun testRenderer(): String {
        return try {
            val renderer = PlatformRenderer()
            renderer.renderComposableRoot {
                Column(
                    modifier = Modifier()
                        .style("padding", "20px")
                ) {
                    Text("PlatformRenderer Test - Success!")
                }
            }
        } catch (e: Exception) {
            """
            <html>
            <body>
                <h1>PlatformRenderer Error</h1>
                <p>Error: ${e.message}</p>
                <p>Stack trace:</p>
                <pre>${e.stackTraceToString()}</pre>
            </body>
            </html>
            """.trimIndent()
        }
    }
}