package com.summon

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import kotlinx.html.unsafe
import com.summon.examples.TextExample
import com.summon.examples.CardExample
import com.summon.examples.ImageExample

/**
 * JVM example demonstrating the Summon library.
 */
fun main() {
    // Print the UI example HTML
    val uiExample = createUIExample()
    println("UI EXAMPLE:")
    println(uiExample)
    
    // Print the form example HTML
    val formExample = createContactForm()
    println("\nFORM EXAMPLE:")
    println(formExample)
    
    // Print the Text component example HTML
    val textExample = createTextExample()
    println("\nTEXT COMPONENT EXAMPLE:")
    println(textExample)
    
    // Print the Card component example HTML
    val cardExample = createCardExample()
    println("\nCARD COMPONENT EXAMPLE:")
    println(cardExample)
    
    // Print the Image component example HTML
    val imageExample = createImageExample()
    println("\nIMAGE COMPONENT EXAMPLE:")
    println(imageExample)
}

/**
 * Creates a UI example with basic components.
 */
private fun createUIExample(): String {
    // Create a more advanced UI with enhanced styling
    val example = Column(
        modifier = Modifier()
            .background("linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)")
            .padding("20px")
            .border("1px", "solid", "#ddd")
            .borderRadius("8px")
            .shadow(),
        content = listOf(
            Text(
                "Hello from Summon UI!",
                Modifier()
                    .padding("10px")
                    .color("#333")
                    .fontSize("24px")
                    .fontWeight("bold")
                    .hover(mapOf("color" to "#0066cc"))
            ),
            Spacer("20px", true),
            Text(
                "This is a demo of the enhanced styling capabilities.",
                Modifier()
                    .padding("10px")
                    .color("#666")
                    .fontSize("16px")
            ),
            Spacer("20px", true),
            Row(
                modifier = Modifier()
                    .padding("10px")
                    .fillMaxWidth(),
                content = listOf(
                    Button(
                        "Primary Button",
                        { println("Primary button clicked!") },
                        Modifier()
                            .background("#4CAF50")
                            .color("white")
                            .padding("10px 20px")
                            .borderRadius("4px")
                            .hover(mapOf("background-color" to "#45a049"))
                    ),
                    Spacer("10px", false),
                    Button(
                        "Secondary Button",
                        { println("Secondary button clicked!") },
                        Modifier()
                            .background("#f1f1f1")
                            .color("#333")
                            .padding("10px 20px")
                            .border("1px", "solid", "#ccc")
                            .borderRadius("4px")
                            .hover(mapOf("background-color" to "#ddd"))
                    )
                )
            )
        )
    )
    
    // Create a StringBuilder to capture the HTML output
    val output = StringBuilder()
    
    // Render the UI to HTML with stylesheet support
    val consumer = output.appendHTML()
    consumer.html {
        head {
            title("Summon UI Demo")
            // We'll include some CSS reset styles
            style {
                unsafe {
                    raw("""
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        min-height: 100vh;
                        background-color: #f0f2f5;
                        padding: 20px;
                    }
                    """.trimIndent())
                }
            }
            
            // Add the generated hover styles
            style {
                unsafe {
                    raw(CssClassStore.generateCss())
                }
            }
        }
        body {
            div {
                // Render our UI
                example.compose(consumer)
            }
        }
    }
    
    return output.toString()
}

/**
 * Creates a Text component example showcasing the enhanced Text features.
 */
private fun createTextExample(): String {
    // Create a text example using our TextExample class
    val example = TextExample().createTextDemo()
    
    // Create a StringBuilder to capture the HTML output
    val output = StringBuilder()
    
    // Render the UI to HTML
    val consumer = output.appendHTML()
    consumer.html {
        head {
            title("Summon Text Component Demo")
            // Include some CSS reset styles
            style {
                unsafe {
                    raw("""
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background-color: #f9f9f9;
                        padding: 20px;
                    }
                    """.trimIndent())
                }
            }
            
            // Add the generated hover styles
            style {
                unsafe {
                    raw(CssClassStore.generateCss())
                }
            }
        }
        body {
            div {
                // Render our example
                example.compose(consumer)
            }
        }
    }
    
    return output.toString()
}

/**
 * Creates a Card component example showcasing various Card styles and interactions.
 */
private fun createCardExample(): String {
    // Create a card example using our CardExample class
    val example = CardExample.cardLayout()
    
    // Create a StringBuilder to capture the HTML output
    val output = StringBuilder()
    
    // Render the UI to HTML
    val consumer = output.appendHTML()
    consumer.html {
        head {
            title("Summon Card Component Demo")
            // Include some CSS reset styles
            style {
                unsafe {
                    raw("""
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background-color: #f9f9f9;
                        padding: 20px;
                    }
                    """.trimIndent())
                }
            }
            
            // Add the generated hover styles
            style {
                unsafe {
                    raw(CssClassStore.generateCss())
                }
            }
        }
        body {
            div {
                // Render our example
                example.compose(consumer)
            }
        }
    }
    
    return output.toString()
}

/**
 * Creates an Image component example showcasing various Image features.
 */
private fun createImageExample(): String {
    // Create a basic image example
    val basicExample = ImageExample.basicImage()
    val multipleImagesExample = ImageExample.multipleImages()
    
    // Combine both examples in a column
    val example = Column(
        modifier = Modifier()
            .background("#ffffff")
            .padding("20px")
            .borderRadius("8px")
            .shadow(),
        content = listOf(
            Text(
                "Image Component Examples",
                Modifier()
                    .padding("0 0 20px 0")
                    .color("#333")
                    .fontSize("28px")
                    .fontWeight("bold")
            ),
            basicExample,
            Spacer("40px", true),
            multipleImagesExample
        )
    )
    
    // Create a StringBuilder to capture the HTML output
    val output = StringBuilder()
    
    // Render the UI to HTML
    val consumer = output.appendHTML()
    consumer.html {
        head {
            title("Summon Image Component Demo")
            // Include some CSS reset styles
            style {
                unsafe {
                    raw("""
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background-color: #f5f5f5;
                        padding: 20px;
                    }
                    """.trimIndent())
                }
            }
            
            // Add the generated hover styles
            style {
                unsafe {
                    raw(CssClassStore.generateCss())
                }
            }
        }
        body {
            div {
                style = "max-width: 1200px; margin: 0 auto;"
                // Render our example
                example.compose(consumer)
            }
        }
    }
    
    return output.toString()
} 