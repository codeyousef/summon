@file:Suppress("UNCHECKED_CAST")

package code.yousef.summon.routing.pages

import code.yousef.summon.core.Composable
import code.yousef.summon.routing.NavLink
import code.yousef.summon.routing.RouteParams
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.p
import kotlinx.html.style

/**
 * 404 Not Found page component.
 * Special 404 pages can be named "404.kt" in the pages directory.
 */
class NotFound : Composable {
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val consumer = receiver as TagConsumer<Any?>
            
            return consumer.div {
                style = "text-align: center; padding: 60px 20px;"
                
                h1 {
                    style = "color: #d32f2f; font-size: 3em;"
                    +"404 - Page Not Found"
                }
                
                p {
                    style = "font-size: 1.2em; margin-bottom: 20px;"
                    +"Oops! The page you're looking for doesn't exist."
                }
                
                p {
                    +"This 404 page is located at /pages/404.kt"
                }
                
                div {
                    style = "margin-top: 40px;"
                    // Navigation links to other pages
                    NavLink("/", "Go Home", "nav-link").compose(this)
                    NavLink("/about", "About", "nav-link").compose(this)
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
            return NotFound()
        }
    }
} 