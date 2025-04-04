package com.summon

import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.span
import kotlinx.html.button
import kotlinx.html.style
import kotlinx.html.id

/**
 * JS implementation of the PlatformRenderer interface.
 * Provides JS-specific rendering for UI components.
 */
class JsPlatformRenderer : PlatformRenderer {
    /**
     * Renders a Text component as a span element with the text content.
     */
    override fun <T> renderText(text: Text, consumer: TagConsumer<T>): T {
        consumer.span {
            // Apply the modifier styles
            style = text.modifier.toStyleString()
            
            // Add the text content
            +text.text
        }
        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a Button component as a button element with appropriate attributes.
     */
    override fun <T> renderButton(button: Button, consumer: TagConsumer<T>): T {
        val buttonId = "btn-${button.hashCode()}"
        consumer.button {
            style = button.modifier.toStyleString()
            id = buttonId
            
            // Add the button text
            +button.label
            
            // Add a data attribute for click handling
            attributes["data-summon-click"] = "true"
        }
        
        // Set up the click handler for this button
        setupJsClickHandler(buttonId, button)
        
        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }
    
    /**
     * Renders a Row component as a div with flex-direction: row.
     */
    override fun <T> renderRow(row: Row, consumer: TagConsumer<T>): T {
        consumer.div {
            val combinedStyles = row.modifier.styles + mapOf(
                "display" to "flex",
                "flex-direction" to "row"
            )
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }
            
            // Render each child
            row.content.forEach { child ->
                child.compose(this)
            }
        }
        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }
    
    /**
     * Renders a Column component as a div with flex-direction: column.
     */
    override fun <T> renderColumn(column: Column, consumer: TagConsumer<T>): T {
        consumer.div {
            val combinedStyles = column.modifier.styles + mapOf(
                "display" to "flex",
                "flex-direction" to "column"
            )
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }
            
            // Render each child
            column.content.forEach { child ->
                child.compose(this)
            }
        }
        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }
    
    /**
     * Renders a Spacer component as a div with appropriate dimension.
     */
    override fun <T> renderSpacer(spacer: Spacer, consumer: TagConsumer<T>): T {
        consumer.div {
            val styleProp = if (spacer.isVertical) "height" else "width"
            style = "$styleProp:${spacer.size};"
        }
        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }
}

/**
 * Helper function to set up the click handler for a button using the existing extension function.
 */
private fun setupJsClickHandler(buttonId: String, button: Button) {
    button.setupJsClickHandler(buttonId)
}

/**
 * JS implementation of the getPlatformRenderer function.
 */
actual fun getPlatformRenderer(): PlatformRenderer = JsPlatformRenderer() 