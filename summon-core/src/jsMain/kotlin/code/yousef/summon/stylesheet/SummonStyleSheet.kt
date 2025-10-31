package code.yousef.summon.stylesheet

import kotlinx.browser.document
import org.w3c.dom.HTMLStyleElement

/**
 * The name of the CSS layer used by Summon components.
 */
const val SUMMON_LAYER = "summon-components"

/**
 * StyleSheet class that defines CSS classes for common components.
 * This follows Kobweb's approach of using CSS classes instead of inline styles.
 */
object SummonStyleSheet {
    private val styleElement: HTMLStyleElement by lazy {
        val element = document.createElement("style") as HTMLStyleElement
        document.head?.appendChild(element) ?: throw IllegalStateException("Document head not found")
        element
    }

    /**
     * Initializes the stylesheet by adding CSS rules to the document.
     * This should be called during application startup.
     */
    fun initialize() {
        val css = buildString {
            append("@layer $SUMMON_LAYER {\n")

            // Base styles
            append(
                """
                /* Base styles */
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
                    margin: 0;
                    padding: 0;
                    line-height: 1.6;
                    background-color: #f5f5f5;
                    color: #333;
                }
                
                /* Root container */
                #root {
                    max-width: 1200px;
                    margin: 0 auto;
                    padding: 20px;
                }
                
                /* App container */
                .summon-app-container {
                    max-width: 1200px;
                    margin: 0 auto;
                    padding: 20px;
                    background-color: #f5f5f5;
                }
                
                /* App header */
                .summon-app-header {
                    padding: 20px;
                    background: #ffffff;
                    border-radius: 8px;
                    margin-bottom: 20px;
                    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                }
                
                /* Text styles */
                .summon-text {
                    margin: 0;
                    padding: 0;
                }
                
                .summon-app-title {
                    font-size: 24px;
                    font-weight: bold;
                    color: #333333;
                    margin-bottom: 16px;
                }
                
                .summon-section-title {
                    font-size: 18px;
                    font-weight: bold;
                    color: #333333;
                    margin-bottom: 16px;
                }
                
                .summon-list-title {
                    font-size: 20px;
                    font-weight: bold;
                    color: #333333;
                    margin-bottom: 16px;
                }
                
                .summon-error-message {
                    color: #ff4d4d;
                    font-size: 14px;
                    margin-bottom: 10px;
                }
                
                /* Button styles */
                .summon-button {
                    cursor: pointer;
                    border: none;
                    border-radius: 4px;
                    padding: 8px 16px;
                    font-size: 14px;
                    transition: background-color 0.2s, transform 0.1s;
                }
                
                .summon-button:hover {
                    transform: translateY(-1px);
                }
                
                .summon-button:active {
                    transform: translateY(1px);
                }
                
                .summon-add-task-button {
                    padding: 10px 20px;
                    background: #4285f4;
                    color: #ffffff;
                    border-radius: 4px;
                    font-weight: bold;
                    font-size: 16px;
                }
                
                .summon-add-task-button:hover {
                    background: #3367d6;
                }
                
                .summon-delete-task-button {
                    padding: 5px 10px;
                    background: #ff4d4d;
                    color: #ffffff;
                    border-radius: 4px;
                    font-size: 14px;
                }
                
                .summon-delete-task-button:hover {
                    background: #e60000;
                }
                
                .summon-filter-button {
                    margin: 0 5px;
                    padding: 8px 12px;
                    background: #e0e0e0;
                    color: #333333;
                    border-radius: 4px;
                }
                
                .summon-filter-button.summon-selected {
                    background: #4285f4;
                    color: #ffffff;
                }
                
                /* Layout components */
                .summon-row {
                    display: flex;
                    flex-direction: row;
                }
                
                .summon-column {
                    display: flex;
                    flex-direction: column;
                }
                
                .summon-justify-between {
                    justify-content: space-between;
                }
                
                .summon-align-center {
                    align-items: center;
                }
                
                .summon-w-100 {
                    width: 100%;
                }
                
                /* Task components */
                .summon-task-form {
                    background: #ffffff;
                    border-radius: 8px;
                    padding: 20px;
                    margin-bottom: 20px;
                    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                }
                
                .summon-filter-controls {
                    background: #ffffff;
                    border-radius: 8px;
                    padding: 15px;
                    margin-bottom: 20px;
                    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                    text-align: center;
                }
                
                .summon-task-list {
                    background: #ffffff;
                    border-radius: 8px;
                    padding: 20px;
                    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                    max-height: 400px;
                    overflow-y: auto;
                }
                
                .summon-task-item {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    padding: 10px;
                    margin: 5px 0;
                    background: #ffffff;
                    border-radius: 4px;
                    border: 1px solid #e0e0e0;
                }
                
                .summon-task-item.summon-completed {
                    background-color: #f0f8ff;
                }
                
                .summon-task-content {
                    display: flex;
                    align-items: center;
                    flex: 1;
                }
                
                /* Checkbox styles */
                .summon-checkbox {
                    margin-right: 10px;
                    cursor: pointer;
                }
                
                /* Empty state */
                .summon-empty-state {
                    padding: 20px;
                    margin: 10px 0;
                    background: #f9f9f9;
                    border-radius: 8px;
                    text-align: center;
                    color: #666666;
                }
                
                /* Utility classes */
                .summon-text-center {
                    text-align: center;
                }
                
                .summon-text-right {
                    text-align: right;
                }
                
                .summon-font-bold {
                    font-weight: bold;
                }
                
                .summon-font-italic {
                    font-style: italic;
                }
                
                .summon-completed-text {
                    text-decoration: line-through;
                    color: #888888;
                }
            """.trimIndent()
            )

            append("\n}\n")
        }

        styleElement.textContent = css
    }
}

/**
 * Extension function to get the appropriate CSS class for a component.
 * This is used to map modifiers to CSS classes.
 */
fun String.toSummonClass(): String {
    return "summon-$this"
}
