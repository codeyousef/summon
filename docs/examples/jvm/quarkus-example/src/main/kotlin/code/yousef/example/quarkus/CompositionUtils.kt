package code.yousef.example.quarkus

import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.html
import kotlinx.html.stream.createHTML
import java.util.concurrent.ConcurrentHashMap
import org.jboss.logging.Logger

/**
 * A simplified HTML generation utility to replace Summon functionality.
 * This class provides methods to generate HTML without depending on the Summon library.
 */
class HTMLComposer {
    private val logger = Logger.getLogger(HTMLComposer::class.java)
    private val properties = ConcurrentHashMap<String, Any>()
    
    /**
     * Set a property in the composer
     * @param name The name of the property to set
     * @param value The value to set
     */
    fun setProperty(name: String, value: Any) {
        properties[name] = value
    }
    
    /**
     * Get a property from the composer
     * @param name The name of the property to get
     * @return The property value or null if not found
     */
    fun getProperty(name: String): Any? {
        return properties[name]
    }
    
    /**
     * Create HTML with the given content
     * @param contentBuilder The content builder function
     * @return The generated HTML as a string
     */
    fun createHTML(contentBuilder: HTML.() -> Unit): String {
        return createHTML().html {
            contentBuilder()
        }
    }
    
    /**
     * Log a debug message
     * @param message The message to log
     */
    fun debug(message: String) {
        logger.debug(message)
    }
}

/**
 * Singleton instance of the HTMLComposer
 */
object HTMLComposition {
    val currentComposer = HTMLComposer()
}

/**
 * Gets the current composer from the HTMLComposition.
 * This is a convenience function to make the code more readable.
 * @return The current composer
 */
fun currentComposer(): HTMLComposer {
    return HTMLComposition.currentComposer
} 