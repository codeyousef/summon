package code.yousef.summon.routing.pages

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.textAlign
import code.yousef.summon.routing.Link
import code.yousef.summon.runtime.Composable

/**
 * Not Found (404) page component for the application.
 * This is a special page that gets shown when no routes match.
 */
@Composable
fun NotFoundPage() {
    Column(
        modifier = Modifier()
            .padding("16px")
            .textAlign("center")
    ) {
        Text("404 - Page Not Found")
        Text("Sorry, the page you are looking for does not exist.")

        // Navigation link back to home
        Link(
            text = "Back to Home",
            href = "/"
        )
    }
}

// Removed old NotFoundPage class implementing Composable
// Removed old NotFoundComponent helper (if it existed) 
