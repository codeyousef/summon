package codes.yousef.summon.test

import codes.yousef.summon.components.layout.Div
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.runtime.PlatformRenderer
import kotlinx.browser.document
import kotlin.test.Test
import kotlin.test.assertEquals

class HydrationTest {

    @Test
    fun testHydration() {
        // Setup DOM
        val root = document.createElement("div")
        root.id = "app"
        // Simulate server-rendered content
        // Note: The actual structure depends on how Div renders. 
        // Assuming Div renders as <div> and Text as text node.
        // But PlatformRenderer might add IDs or classes.
        // For now, let's just check if it runs without error and content is present.
        root.innerHTML = "<div>Hello Hydration</div>"
        document.body?.appendChild(root)

        val renderer = PlatformRenderer()
        
        try {
            renderer.hydrateComposableRoot("app") {
                Div {
                    Text("Hello Hydration")
                }
            }
            
            // Verify content is preserved. 
            // If hydration fails and it re-renders, it might look the same, 
            // but if it clears it first, we might see a flicker or different attributes.
            // The key for hydration is that it attaches listeners without destroying DOM.
            // Since we can't easily check for "destroyed DOM" in this sync test without mutation observers,
            // we'll just ensure the final state is correct and no errors occurred.
            
            // Also check if the root element has the hydration marker if the implementation adds it.
            // Based on grep, it sets "data-hydrated-by" to "wasm"
            
            val hydratedRoot = document.getElementById("app")
            val attr = hydratedRoot?.getAttribute("data-hydrated-by")
            println("DEBUG: Attribute: $attr")
            
            val innerHTML = hydratedRoot?.innerHTML ?: ""
            println("DEBUG: InnerHTML: $innerHTML")
            
            assertEquals("wasm", attr)
            assertEquals(true, innerHTML.contains("Hello Hydration"), "Content should be preserved")
            
        } finally {
            document.body?.removeChild(root)
        }
    }
}
