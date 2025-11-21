package codes.yousef.summon.test

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.runtime.PlatformRenderer
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GoldenMasterTest {

    @Test
    fun `test structural validity with Jsoup`() {
        val renderer = PlatformRenderer()
        
        val html = renderer.renderComposableRoot {
            Column {
                Text("Hello World")
                Button(label = "Click Me", onClick = {})
            }
        }
        
        val doc = Jsoup.parse(html)
        
        // Verify structure
        // Note: Selectors might need adjustment based on actual rendering output
        assertNotNull(doc.select("div").first(), "Should contain a div (Column)")
        
        // Text might be rendered in a span or directly. 
        // Based on TextExample in Main.kt, it seems to use Text component which likely renders a span or similar.
        // Let's check for the text content directly if tag is uncertain, but Jsoup needs tags.
        // Assuming Text renders a span or div.
        
        val textElement = doc.getElementsContainingOwnText("Hello World").first()
        assertNotNull(textElement, "Should contain 'Hello World'")
        
        val buttonElement = doc.select("button").first()
        assertNotNull(buttonElement, "Should contain a button")
        assertEquals("Click Me", buttonElement?.text())
    }
}
