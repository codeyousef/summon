package com.summon

import kotlinx.browser.document
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.id
import org.w3c.dom.HTMLElement

/**
 * JS example demonstrating the Summon library.
 */
fun main() {
    // Get the body element to append our UI to
    val body = document.body ?: return
    
    // Create a simple UI
    val example = Column(
        modifier = Modifier().background("lightblue").padding("10px"),
        content = listOf(
            Text("Hello from Summon UI!", Modifier().padding("5px")),
            Spacer("10px", true),
            Button(
                "Click Me", 
                { console.log("Button clicked!") }, 
                Modifier().background("white").padding("5px")
            )
        )
    )
    
    // Render the UI to the DOM
    body.append {
        div {
            id = "app"
            example.compose(this)
        }
    }
} 