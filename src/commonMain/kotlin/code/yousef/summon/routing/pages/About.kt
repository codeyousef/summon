package code.yousef.summon.routing.pages

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.padding
import code.yousef.summon.runtime.Composable
import code.yousef.summon.routing.Link

/**
 * About page component for the application.
 * This file maps to the route "/about" due to its name and location.
 */
@Composable
fun AboutPage() {
    Column(
        modifier = Modifier().padding("16px")
    ) {
        Text("About Summon")
        Text("Summon is a Kotlin Multiplatform UI framework for building modern applications.")
        Text("This page demonstrates the file-based routing system, similar to Next.js.")
        
        // Navigation link back to home
        Link(
            text = "Back to Home",
            href = "/"
        )
    }
}
