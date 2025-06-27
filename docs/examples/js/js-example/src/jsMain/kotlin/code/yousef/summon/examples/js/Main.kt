package code.yousef.summon.examples.js

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.core.style.Color
import code.yousef.summon.extensions.px
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.StylingModifiers.fontWeight
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.RecomposerHolder
import code.yousef.summon.runtime.setPlatformRenderer
import code.yousef.summon.runtime.Composer
import code.yousef.summon.runtime.CompositionLocal
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.org.w3c.dom.events.Event
import code.yousef.summon.js.console
import kotlin.js.js

/**
 * Main application content
 */
@Composable
fun MainApp() {
    Column(
        modifier = Modifier()
            .padding(16.px)
    ) {
        Text(
            text = "Summon JS Example",
            modifier = Modifier()
                .fontSize(28.px)
                .fontWeight("700")
                .color("#333333")
                .margin("0 0 16px 0")
        )

        Text(
            text = "This example demonstrates the Summon library for Kotlin/JS applications.",
            modifier = Modifier()
                .fontSize(16.px)
                .color("#666666")
                .margin("0 0 24px 0")
        )

        Box(
            modifier = Modifier()
                .backgroundColor(Color.GREEN.toHexString())
                .padding(16.px)
                .margin("16px 0")
                .borderRadius(8.px)
        ) {
            Text(
                text = "This box should have a GREEN background",
                modifier = Modifier()
                    .color(Color.BLACK.toHexString())
            )
        }
    }
}

/**
 * Generate the HTML document structure
 */
fun generateHtmlDocument() {
    // Create a style element for basic styling
    val styleElement = document.createElement("style")
    styleElement.textContent = """
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
            line-height: 1.6;
            color: #333;
            background-color: #f8fafc;
            padding: 20px;
        }
        
        #root {
            min-height: 100vh;
            width: 100%;
        }
    """.trimIndent()
    document.head?.appendChild(styleElement)
    
    // Create a title element
    val titleElement = document.createElement("title")
    titleElement.textContent = "Summon JS Example"
    document.head?.appendChild(titleElement)
    
    // Create a meta element for viewport
    val metaElement = document.createElement("meta")
    metaElement.setAttribute("name", "viewport")
    metaElement.setAttribute("content", "width=device-width, initial-scale=1.0")
    document.head?.appendChild(metaElement)
    
    // Create a root element if it doesn't exist
    if (document.getElementById("root") == null) {
        val rootElement = document.createElement("div")
        rootElement.id = "root"
        document.body?.appendChild(rootElement)
    }
}

fun main() {
    console.log("Main function called - Summon JS Example")

    // Ensure that the window object is properly initialized
    js("""
    if (typeof window.currentParent === 'undefined') {
        console.log("Initializing window.currentParent");
        window.currentParent = document.body;
        window._parentStack = [];

        window.pushParent = function(element) {
            window._parentStack.push(window.currentParent);
            window.currentParent = element;
        };

        window.popParent = function() {
            if (window._parentStack.length > 0) {
                window.currentParent = window._parentStack.pop();
            } else {
                window.currentParent = document.body;
            }
        };
    } else {
        console.log("window.currentParent already initialized");
    }
    """)

    document.addEventListener("DOMContentLoaded", { _: Event ->
        console.log("DOM content loaded event fired")
        
        try {
            // Generate the HTML document structure
            generateHtmlDocument()
            
            // Get or create the root element
            val rootElement = document.getElementById("root")
                ?: document.createElement("div").apply {
                    id = "root"
                    document.body?.appendChild(this)
                }
            
            // Clear the root element
            rootElement.innerHTML = ""
            
            // Configure the renderer
            val renderer = PlatformRenderer()
            setPlatformRenderer(renderer)
            
            // Create a composer for the composition
            val composer = RecomposerHolder.createComposer()
            
            // Provide the composer and renderer in the composition
            CompositionLocal.provideComposer(composer) {
                val rendererProvider = LocalPlatformRenderer.provides(renderer)
                
                // Explicitly set the current parent element to the root element
                js("window.currentParent = document.getElementById('root')")
                
                // Render the main application
                renderer.renderComposable {
                    MainApp()
                }
                
                console.log("Render complete!")
            }
        } catch (e: Exception) {
            console.error("Error during rendering: ${e.message}")
            console.error("Stack trace: ${e.stackTraceToString()}")
            window.alert("Rendering failed: ${e.message}")
        }
    })
}