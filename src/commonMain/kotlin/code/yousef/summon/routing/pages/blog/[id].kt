package code.yousef.summon.routing.pages.blog

import code.yousef.summon.runtime.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.routing.NavLink


/**
 * A simple composable function representing a blog post page.
 * It receives the post ID as a parameter.
 *
 * @param id The ID of the blog post extracted from the URL.
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
            // TODO: Fetch and display actual post content based on ID
        } else {
            Text("Error: Post ID not provided.")
        }
        
        // Example Navigation
        NavLink(to = "/") {
            Text("Go Home")
        }
    }
} 