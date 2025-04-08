package code.yousef.summon.routing.pages

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.routing.NavLink
import code.yousef.summon.routing.RouteParams
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.p

/**
 * My Simple Page component that maps to the /my-simple-page route in the router.
 */
class MySimplePage : Composable {
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val consumer = receiver as TagConsumer<Any?>

            return consumer.div {
                h1 {
                    +"My Simple Page"
                }
                
                Column(
                    modifier = Modifier()
                        .padding("20px")
                        .maxWidth("800px")
                        .margin("0 auto")
                        .background("#f5f5f5")
                        .borderRadius("8px"),
                    content = listOf(
                        Text(
                            text = "My First Summon Page",
                            modifier = Modifier()
                                .fontSize("24px")
                                .fontWeight("bold")
                                .margin("20px 0")
                                .color("#333")
                        ),
                        Text(
                            text = "This is a simple webpage created using Summon composables.",
                            modifier = Modifier().margin("0 0 20px 0")
                        ),
                        Button(
                            label = "Click Me!",
                            onClick = { println("Button clicked!") },
                            modifier = Modifier()
                                .padding("10px 20px")
                                .background("#2196f3")
                                .color("white")
                                .borderRadius("4px")
                                .cursor("pointer")
                        )
                    )
                ).compose(this)
                
                p {
                    +"Navigation links:"
                }
                
                div {
                    NavLink("/", "Home", "nav-link").compose(this)
                    NavLink("/about", "About", "nav-link").compose(this)
                    NavLink("/my-simple-page", "My Simple Page", "nav-link", "active").compose(this)
                }
            } as T
        }
        return receiver
    }

    companion object {
        /**
         * Factory function to create an instance of this page.
         * This is required by the router system.
         */
        fun create(params: RouteParams): Composable {
            return MySimplePage()
        }
    }
} 
