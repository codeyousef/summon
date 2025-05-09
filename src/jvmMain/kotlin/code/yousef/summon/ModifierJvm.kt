package code.yousef.summon

import code.yousef.summon.modifier.Modifier
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.style

/**
 * JVM-specific extension functions for Modifier.
 */

/**
 * A counter to generate unique class names
 */
private var classCounter = 0

/**
 * Applies all styles from the modifier to the HTML element, including handling hover effects.
 * For JVM, we use CSS classes for hover effects.
 * @return The generated class name and styles if hover styles are used
 */
fun Modifier.applyStyles(element: CommonAttributeGroupFacade): Pair<String, String>? {
    // Apply regular styles
    element.style = this.toStyleString()

    // Handle hover styles - for JVM we'll return a class name that can be used for styling
    val hoverStyles = this.styles["__hover"]
    if (hoverStyles != null) {
        val className = "summon-hover-${++classCounter}"
        element.attributes["class"] = className
        return className to hoverStyles
    }

    return null
}

/**
 * A class for storing generated CSS classes for hover effects
 */
object CssClassStore {
    private val classes = mutableMapOf<String, String>()

    fun add(className: String, styles: String) {
        classes[className] = styles
    }

    fun generateCss(): String {
        return classes.entries.joinToString("\n") { (className, styles) ->
            ".$className:hover { $styles }"
        }
    }
}

/**
 * Adds hover style information to a CSS class.
 */
fun Pair<String, String>.addToStyleSheet() {
    CssClassStore.add(first, second)
} 
