package code.yousef.summon.routing.pages.blog

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.routing.NavLink
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal


/**
 * A simple composable function representing a Blog Post page.
 */
@Composable
fun BlogPostPage(id: String?) {
    val composer = CompositionLocal.currentComposer

    Column(
        modifier = Modifier().padding("20px")
    ) {
        Text("Blog Post")
        if (id != null) {
            Text("Displaying post with ID: $id")
            // Placeholder: Actual post content would be fetched and displayed here
        } else {
            Text("Error: Post ID not provided.")
        }
        
        // Example Navigation
        NavLink(to = "/") {
            Text("Go Home")
        }
    }
} 