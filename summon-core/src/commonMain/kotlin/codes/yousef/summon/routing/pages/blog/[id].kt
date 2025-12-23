package codes.yousef.summon.routing.pages.blog

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.*
import codes.yousef.summon.routing.Link
import codes.yousef.summon.routing.RouteParams
import codes.yousef.summon.runtime.Composable

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