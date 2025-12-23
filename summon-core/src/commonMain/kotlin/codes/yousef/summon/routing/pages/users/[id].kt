package codes.yousef.summon.routing.pages.users

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.*
import codes.yousef.summon.routing.Link
import codes.yousef.summon.routing.RouteParams
import codes.yousef.summon.runtime.Composable

/**
 * User profile page component that demonstrates dynamic routing.
 * This file uses the [id] pattern in its name to create a dynamic route.
 * The actual URL would be /users/:id where :id is a parameter.
 */
@Composable
fun UserProfilePage(userId: String? = null) {
    // If userId wasn't passed directly, try to get it from the current route params
    val id = userId ?: RouteParams.current["id"] ?: "unknown"

    Column(
        modifier = Modifier().padding("16px")
    ) {
        Text("User Profile")
        Text("User ID: $id")

        // This would normally fetch user data based on the ID
        Text("Name: User $id")
        Text("Email: user$id@example.com")

        // Navigation link back to home
        Link(
            text = "Back to Home",
            href = "/"
        )
    }
} 