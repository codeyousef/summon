@file:Suppress("UNCHECKED_CAST")

package com.summon.routing.pages.blog

import com.summon.Composable
import com.summon.routing.NavLink
import com.summon.routing.RouteParams
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.p

/**
 * Blog Post page component that maps to the /blog/:id route in our Next.js style router.
 * The [id] in the file name becomes a route parameter.
 */
class BlogPost(private val postId: String?) : Composable {
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val consumer = receiver as TagConsumer<Any?>
            
            return consumer.div {
                h1 {
                    +"Blog Post #${postId ?: "Unknown"}"
                }
                
                p {
                    +"This is a dynamic blog post page located at /pages/blog/[id].kt"
                }
                
                p {
                    +"The post ID is extracted from the URL: ${postId ?: "Unknown"}"
                }
                
                div {
                    // Navigation links to other pages
                    NavLink("/", "Home", "nav-link").compose(this)
                    NavLink("/about", "About", "nav-link").compose(this)
                    NavLink("/blog/123", "Blog Post #123", "nav-link", if (postId == "123") "active" else "").compose(this)
                    NavLink("/blog/456", "Blog Post #456", "nav-link", if (postId == "456") "active" else "").compose(this)
                }
            } as T
        }
        
        return receiver
    }
    
    companion object {
        /**
         * Factory function to create an instance of this page.
         * This will be registered with the router.
         */
        fun create(params: RouteParams): Composable {
            // Extract the post ID from the route parameters
            val postId = params["id"]
            return BlogPost(postId)
        }
    }
} 