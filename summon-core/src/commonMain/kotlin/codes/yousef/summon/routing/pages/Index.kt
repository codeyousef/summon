package codes.yousef.summon.routing.pages

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.*
import codes.yousef.summon.routing.Link
import codes.yousef.summon.routing.LocalRouter
import codes.yousef.summon.runtime.Composable

/**
 * Home page component for the application.
 * This file maps to the route "/" due to its name and location.
 */
@Composable
fun HomePage() {
    Column(
        modifier = Modifier().padding("16px")
    ) {
        Text("Welcome to Summon")
        Text("This is the home page of the application.")

        // Navigation links
        Link(
            text = "About Us",
            href = "/about"
        )

        // Example of programmatic navigation
        Button(
            label = "View User Profile",
            onClick = {
                // Get router from composition local
                LocalRouter?.navigate("/users/123")
            }
        )

        Button(
            label = "View Blog Post",
            onClick = {
                LocalRouter?.navigate("/blog/latest-post")
            }
        )
    }
} 
