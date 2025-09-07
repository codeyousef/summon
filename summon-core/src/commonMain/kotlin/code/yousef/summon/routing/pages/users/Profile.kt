package code.yousef.summon.routing.pages.users

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.routing.NavLink
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal

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
