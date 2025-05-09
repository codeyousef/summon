package code.yousef.summon.examples.js

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.core.style.Color
import code.yousef.summon.extensions.px
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.StylingModifiers.backgroundColor
import code.yousef.summon.modifier.StylingModifiers.color
import code.yousef.summon.modifier.StylingModifiers.fontWeight
import code.yousef.summon.modifier.StylingModifiers.textAlign
import code.yousef.summon.modifier.gap
import code.yousef.summon.runtime.JsPlatformRenderer
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.events.Event

/**
 * A minimal example to demonstrate the styling capabilities of Summon library
 * This example focuses on ensuring that styles are properly applied to elements
 */
@Composable
fun MinimalExample() {
    Column(
        modifier = Modifier()
            .padding(16.px)
            .gap(24.px)
    ) {
        // Heading with styling
        Text(
            text = "Summon CSS Style Test",
            modifier = Modifier()
                .fontSize(24.px)
                .fontWeight(700)
                .color("#333333")
                .margin("0 0 16px 0")
        )

        // Box with background color and padding
        Box(
            modifier = Modifier()
                .backgroundColor(Color.RED)
                .padding(16.px)
                .borderRadius(8.px)
        ) {
            Text(
                text = "This box should have a RED background",
                modifier = Modifier()
                    .color(Color.WHITE)
                    .textAlign("center")
            )
        }

        // Row with multiple styled boxes
        Row(
            modifier = Modifier()
                .gap(8.px)
                .padding(16.px)
        ) {
            // Blue box
            Box(
                modifier = Modifier()
                    .backgroundColor(Color.BLUE)
                    .padding(16.px)
                    .borderRadius(8.px)
            ) {
                Text(
                    text = "BLUE",
                    modifier = Modifier()
                        .color(Color.WHITE)
                )
            }

            // Green box
            Box(
                modifier = Modifier()
                    .backgroundColor(Color.GREEN)
                    .padding(16.px)
                    .borderRadius(8.px)
            ) {
                Text(
                    text = "GREEN",
                    modifier = Modifier()
                        .color(Color.BLACK)
                )
            }

            // Yellow box
            Box(
                modifier = Modifier()
                    .backgroundColor(Color.YELLOW)
                    .padding(16.px)
                    .borderRadius(8.px)
            ) {
                Text(
                    text = "YELLOW",
                    modifier = Modifier()
                        .color(Color.BLACK)
                )
            }
        }

        // Card with styled content
        Card(
            modifier = Modifier()
                .padding(16.px)
                .borderRadius(8.px)
        ) {
            Column(
                modifier = Modifier()
                    .padding(16.px)
                    .gap(8.px)
            ) {
                Text(
                    text = "Card Title",
                    modifier = Modifier()
                        .fontSize(18.px)
                        .fontWeight(700)
                )
                Text(
                    text = "This card demonstrates basic styling. All styles should be properly converted from camelCase to kebab-case in the HTML.",
                    modifier = Modifier()
                        .fontSize(14.px)
                )
            }
        }
    }
}

/**
 * Run this function to render the minimal example
 */
fun runMinimalExample() {
    document.addEventListener("DOMContentLoaded", { _: Event ->
        val rootElement = document.getElementById("root")
            ?: document.createElement("div").apply {
                id = "root"
                document.body?.appendChild(this)
            }

        rootElement.innerHTML = ""

        val renderer = JsPlatformRenderer()
        setPlatformRenderer(renderer)
        
        // Create a provider for the LocalPlatformRenderer
        val rendererProvider = LocalPlatformRenderer.provides(renderer)

        renderer.renderComposable {
            MinimalExample()
        }
    })
}
