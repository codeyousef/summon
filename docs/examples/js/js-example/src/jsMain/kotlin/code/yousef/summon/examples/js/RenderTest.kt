package code.yousef.summon.examples.js

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.core.style.Color
import code.yousef.summon.extensions.px
import code.yousef.summon.modifier.Modifier
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
 * A simple test to verify that the page renders correctly
 */
@Composable
fun RenderTest() {
    Column(
        modifier = Modifier()
            .padding(16.px)
    ) {
        Text(
            text = "Render Test - If you see this, the test passed!",
            modifier = Modifier()
                .fontSize(24.px)
                .fontWeight("700")
                .color("#333333")
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
 * Run this function to test if the page renders correctly
 */
fun runRenderTest() {
    console.log("Starting render test...")

    // Ensure that the window object is properly initialized
    js("""
    if (typeof window.currentParent === 'undefined') {
        console.log("Initializing window.currentParent in RenderTest");
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
        console.log("window.currentParent already initialized in RenderTest");
    }
    """)

    document.addEventListener("DOMContentLoaded", { _: Event ->
        console.log("DOM loaded, setting up test...")

        val rootElement = document.getElementById("root")
            ?: document.createElement("div").apply {
                id = "root"
                document.body?.appendChild(this)
            }

        rootElement.innerHTML = ""

        try {
            console.log("Creating renderer...")
            val renderer = PlatformRenderer()
            setPlatformRenderer(renderer)

            console.log("Creating composer...")
            val composer = RecomposerHolder.createComposer()

            console.log("Setting up composition...")
            CompositionLocal.provideComposer(composer) {
                console.log("Providing renderer...")
                val rendererProvider = LocalPlatformRenderer.provides(renderer)

                console.log("Rendering test component...")
                // Explicitly set the current parent element to the root element
                js("window.currentParent = document.getElementById('root')")
                console.log("Set window.currentParent to root element for test")
                renderer.renderComposable {
                    RenderTest()
                }

                console.log("Render complete! If you see this message and the UI, the test passed.")
                window.alert("Render test completed successfully!")
            }
        } catch (e: Exception) {
            console.error("Error during render test: ${e.message}")
            console.error("Stack trace: ${e.stackTraceToString()}")
            window.alert("Render test failed: ${e.message}")
        }
    })
}

// No main function here to avoid conflicts with Main.kt
// Call runRenderTest() from Main.kt instead
