package code.yousef.summon.routing.pages


import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.navigation.ButtonLink
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.margin
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal


/**
 * A simple composable function representing the 404 Not Found page.
 */
@Composable
fun NotFoundPage() {
    val composer = CompositionLocal.currentComposer
    
    // Use refactored layout and text components
    Column(
        modifier = Modifier()
            .padding("20px")
            .textAlign("center")
    ) {
        Text("404 - Page Not Found")
        Text("Sorry, the page you were looking for could not be found.")
        ButtonLink(
            text = "Return to Home Page", 
            href = "/",
            modifier = Modifier().margin("20px 0 0 0")
        )
    }
}

// Removed old NotFoundPage class implementing Composable
// Removed old NotFoundComponent helper (if it existed) 
