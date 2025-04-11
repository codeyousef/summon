package code.yousef.summon.routing.pages.blog

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.padding
import code.yousef.summon.runtime.Composable
import code.yousef.summon.routing.Link
import code.yousef.summon.routing.RouteParams

/**
 * Blog post page component that demonstrates dynamic routing.
 * This file uses the [id] pattern in its name to create a dynamic route.
 * The actual URL would be /blog/:id where :id is a parameter.
 */
@Composable
fun BlogPostPage(postId: String? = null) {
    // If postId wasn't passed directly, try to get it from the current route params
    val id = postId ?: RouteParams.current["id"] ?: "unknown"
    
    Column(
        modifier = Modifier().padding("16px")
    ) {
        Text("Blog Post")
        Text("Post ID: $id")
        
        // This would normally fetch post data based on the ID
        val title = if (id == "latest-post") "What's New in Summon" else "Blog Post $id"
        val content = "This is the content of the blog post with ID: $id."
        
        Text("Title: $title")
        Text("Content: $content")
        
        // Navigation link back to home
        Link(
            text = "Back to Home",
            href = "/"
        )
    }
} 