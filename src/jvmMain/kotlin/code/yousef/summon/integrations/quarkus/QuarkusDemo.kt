package code.yousef.summon.integrations.quarkus

import jakarta.enterprise.inject.Instance
import jakarta.enterprise.inject.spi.CDI

/**
 * Demo utility for Summon-Quarkus integration
 */
object QuarkusDemo {

    /**
     * Demonstrates how to render a component using the SummonRenderer in a CDI context
     */
    fun demonstrateSummonRendering() {
        try {
            val cdi = CDI.current()
            val summonRenderer: Instance<QuarkusExtension.SummonRenderer> =
                cdi.select(QuarkusExtension.SummonRenderer::class.java)

            // Get the renderer
            val renderer = summonRenderer.get()

            // Build some content using the renderer helper methods
            val heading = renderer.renderHeading(1, "User Profile")
            val paragraph = renderer.renderParagraph("This is a sample user profile")
            val button = renderer.renderButton("Edit Profile", "alert('Edit button clicked')")

            // Combine the content
            val content = "$heading\n$paragraph\n$button"

            // Render a full template
            val html = renderer.renderTemplate("User Profile", content)

            println("Rendered HTML: $html")
        } catch (e: Exception) {
            println("CDI not available or other error: ${e.message}")
        }
    }
} 