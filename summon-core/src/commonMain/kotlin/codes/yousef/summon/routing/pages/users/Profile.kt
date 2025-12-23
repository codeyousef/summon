package codes.yousef.summon.routing.pages.users

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.*
import codes.yousef.summon.routing.NavLink
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.CompositionLocal

/**
 * A simple composable function representing the User Profile page.
 */
@Composable
fun UserProfilePage() {
    val composer = CompositionLocal.currentComposer

    Column(
        modifier = Modifier().padding("20px")
    ) {
        Text("User Profile")
        Text("This is the user profile page.")
        // Placeholder: User details would be displayed here

        // Example Navigation
        NavLink(to = "/") {
            Text("Go Home")
        }
    }
}
