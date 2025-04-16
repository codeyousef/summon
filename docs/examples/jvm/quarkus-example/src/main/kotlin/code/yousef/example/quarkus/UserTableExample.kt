package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

/**
 * Example that demonstrates the usage of the UserTableComponent.
 */
@Composable
fun UserTableExample() {
    Box(
        modifier = Modifier()
            .style("style", "padding: 2rem; max-width: 1200px; margin: 0 auto;")
    ) {
        Column(
            modifier = Modifier()
                .style("style", "gap: 1.5rem;")
        ) {
            Text(
                text = "User Management",
                modifier = Modifier()
                    .style("style", "font-size: 2rem; font-weight: bold; color: #333;")
            )
            
            // Create sample user data
            val users = listOf(
                User(1, "John Doe", "john.doe@example.com", "admin", true),
                User(2, "Jane Smith", "jane.smith@example.com", "editor", true),
                User(3, "Robert Johnson", "robert.j@example.com", "user", true),
                User(4, "Maria Garcia", "maria.g@example.com", "moderator", true),
                User(5, "Alex Chen", "alex.chen@example.com", "user", false)
            )
            
            // Use the UserTableComponent to display the users
            UserTableComponent(users)
        }
    }
} 