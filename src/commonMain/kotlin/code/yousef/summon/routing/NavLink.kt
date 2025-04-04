@file:Suppress("UNCHECKED_CAST")

package code.yousef.summon.routing

import code.yousef.summon.core.Composable
import kotlinx.html.TagConsumer
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.id
import kotlin.random.Random

/**
 * A navigation link component that renders as an anchor tag.
 * Can automatically detect if it points to the current route.
 *
 * @param to Path to navigate to
 * @param text Text to display in the link
 * @param className Optional CSS class name
 * @param activeClassName CSS class to apply when this link is active
 * @param onClick Optional click handler
 */
class NavLink(
    val to: String,
    val text: String,
    val className: String = "",
    val activeClassName: String = "active",
    val onClick: (() -> Unit)? = null
) : Composable {
    // Counter for generating unique IDs
    companion object {
        private var idCounter = 0
        
        /**
         * Creates a NavLink with the specified options.
         */
        fun create(
            to: String,
            text: String,
            className: String = "",
            activeClassName: String = "active",
            onClick: (() -> Unit)? = null
        ): NavLink {
            return NavLink(to, text, className, activeClassName, onClick)
        }
    }
    
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val consumer = receiver as TagConsumer<Any?>

            // Get the current router instance
            // This will be set by the Router component when rendering
            val router = RouterContext.current
            val isActive = router?.getCurrentPath() == to

            // Generate a unique ID for this link
            val uniqueId = "nav-link-${idCounter++}-${Random.nextInt(10000)}"
            
            return consumer.a(href = to) {
                // Set the unique ID
                id = uniqueId

                // Set up the classes
                val classNames = mutableSetOf<String>()

                // Apply regular class name
                if (className.isNotEmpty()) {
                    classNames.add(className)
                }

                // Apply active class if this link is active
                if (isActive && activeClassName.isNotEmpty()) {
                    classNames.add(activeClassName)
                }

                // Set the classes attribute
                if (classNames.isNotEmpty()) {
                    classes = classNames
                }

                // Mark this as a navigation link with data attributes
                attributes["data-summon-navlink"] = "true"
                attributes["data-summon-to"] = to

                // Add click handling information as data attributes
                // Platform-specific code will need to handle the actual click events
                attributes["onclick"] = "summonHandleNavLinkClick(this); return false;"

                +text
            } as T
        }

        return receiver
    }
} 