package code.yousef.summon.routing.pages.users


import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.routing.NavLink

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
        // TODO: Fetch and display user data
        
        // Example Navigation
        NavLink(to = "/") {
            Text("Go Home")
        }
    }
} 
