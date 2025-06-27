package code.yousef.summon.test

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.LayoutModifiers.gap
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
 * A test component that displays primary and secondary buttons
 */
@Composable
fun ButtonTest() {
    Column(
        modifier = Modifier()
            .padding("16px")
            .gap("16px")
    ) {
        Text(
            text = "Button Styling Test",
            modifier = Modifier()
                .fontSize("24px")
                .fontWeight("700")
                .color("#333333")
        )

        Text(
            text = "Primary and secondary buttons should have different styles",
            modifier = Modifier()
                .fontSize("16px")
                .color("#666666")
        )

        Row(
            modifier = Modifier()
                .gap("16px")
        ) {
            Button(
                onClick = { window.alert("Primary button clicked!") },
                label = "Primary Button",
                variant = ButtonVariant.PRIMARY
            )

            Button(
                onClick = { window.alert("Secondary button clicked!") },
                label = "Secondary Button",
                variant = ButtonVariant.SECONDARY
            )
        }
    }
}

/**
 * Run this function to test button styling
 */
fun runButtonTest() {
    console.log("Starting button styling test...")

    // Ensure that the window object is properly initialized
    js("""
    if (typeof window.currentParent === 'undefined') {
        console.log("Initializing window.currentParent in ButtonTest");
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
        console.log("window.currentParent already initialized in ButtonTest");
    }
    """)

    document.addEventListener("DOMContentLoaded", { _: Event ->
        console.log("DOM loaded, setting up button test...")

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

                console.log("Rendering button test component...")
                // Explicitly set the current parent element to the root element
                js("window.currentParent = document.getElementById('root')")
                console.log("Set window.currentParent to root element for button test")
                renderer.renderComposable {
                    ButtonTest()
                }

                console.log("Button test render complete!")
                console.log("Check if primary and secondary buttons have different styles.")
            }
        } catch (e: Exception) {
            console.error("Error during button test: ${e.message}")
            console.error("Stack trace: ${e.stackTraceToString()}")
            window.alert("Button test failed: ${e.message}")
        }
    })
}