package codes.yousef.summon.routing.pages

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.*
import codes.yousef.summon.routing.Link
import codes.yousef.summon.runtime.Composable

/**
 * Not Found (404) page component for the application.
 * This is a special page that gets shown when no routes match.
 */
@Composable
fun NotFoundPage() {
    Column(
        modifier = Modifier()
            .padding("16px")
            .style("text-align", "center")
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
