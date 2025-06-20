package code.yousef.summon.examples.js

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.extensions.minHeight
import code.yousef.summon.extensions.px
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.StylingModifierExtras.transform
import code.yousef.summon.modifier.StylingModifiers.boxShadow
import code.yousef.summon.modifier.StylingModifiers.fontWeight
import code.yousef.summon.modifier.StylingModifiers.lineHeight
import code.yousef.summon.modifier.StylingModifiers.transition
import code.yousef.summon.modifier.alignItems
import code.yousef.summon.modifier.backgroundImage
import code.yousef.summon.modifier.backgroundSize
import code.yousef.summon.modifier.flexWrap
import code.yousef.summon.modifier.fontFamily
import code.yousef.summon.modifier.LayoutModifiers.gap
import code.yousef.summon.modifier.hover
import code.yousef.summon.modifier.justifyContent
import code.yousef.summon.modifier.letterSpacing
import code.yousef.summon.modifier.padding
import code.yousef.summon.modifier.position
import code.yousef.summon.modifier.LayoutModifiers.top
import code.yousef.summon.routing.seo.Header
import code.yousef.summon.routing.seo.Main
import code.yousef.summon.routing.seo.Nav
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import kotlinx.browser.document
import kotlinx.html.org.w3c.dom.events.Event
import web.html.HTMLElement

/**
 * Enum class to represent different showcase sections
 */
enum class ShowcaseSection {
    CORE_COMPONENTS,
    LAYOUT_COMPONENTS,
    FEEDBACK_COMPONENTS,
    CUSTOM_COMPONENTS
}

/**
 * Main application content
 */
@Composable
fun SummonShowcase() {
    // Track the active section (in a real app would use state)
    var activeSection = ShowcaseSection.CORE_COMPONENTS

    // Create the page structure with modernized styling
    Column(
        modifier = Modifier()
            .fontFamily(
                "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif",
                null
            )
    ) {
        // Header with navigation - modern gradient background
        Header(
            modifier = Modifier()
                .padding(20.px)
                .boxShadow("0 4px 12px rgba(0, 0, 0, 0.08)")
                .backgroundColor("#ffffff")
                .border(2.px, "solid", "#f0f0f0")
                .position("sticky")
                .top(0.px)
                .zIndex(100)
        ) {
            Row(
                modifier = Modifier()
                    .padding(8.px)
                    .gap(24.px)
                    .alignItems("center")
                    .justifyContent("space-between")
                    .maxWidth(1200.px)
                    .margin("0 auto")
                    .width("100%")
            ) {
                // App title with gradient text effect
                Text(
                    text = "Summon JS Showcase",
                    modifier = Modifier()
                        .fontSize(28.px)
                        .fontWeight(800)
                        .background("linear-gradient(45deg, #4568dc, #b06ab3)")
                        .letterSpacing("0.5px", null)
                )

                // Navigation with modern styling
                Nav(
                    modifier = Modifier()
                        .padding(8.px)
                ) {
                    Row(
                        modifier = Modifier()
                            .gap(12.px)
                            .flexWrap("wrap")
                            .justifyContent("center")
                    ) {
                        NavButton(
                            text = "Core Components",
                            isActive = activeSection == ShowcaseSection.CORE_COMPONENTS,
                            onClick = {
                                activeSection = ShowcaseSection.CORE_COMPONENTS
                                scrollToSection("core-components")
                            }
                        )

                        NavButton(
                            text = "Layout Components",
                            isActive = activeSection == ShowcaseSection.LAYOUT_COMPONENTS,
                            onClick = {
                                activeSection = ShowcaseSection.LAYOUT_COMPONENTS
                                scrollToSection("layout-components")
                            }
                        )

                        NavButton(
                            text = "Feedback Components",
                            isActive = activeSection == ShowcaseSection.FEEDBACK_COMPONENTS,
                            onClick = {
                                activeSection = ShowcaseSection.FEEDBACK_COMPONENTS
                                scrollToSection("feedback-components")
                            }
                        )

                        NavButton(
                            text = "Custom Components",
                            isActive = activeSection == ShowcaseSection.CUSTOM_COMPONENTS,
                            onClick = {
                                activeSection = ShowcaseSection.CUSTOM_COMPONENTS
                                scrollToSection("custom-components")
                            }
                        )
                    }
                }
            }
        }

        // Main content with subtle background pattern
        Main(
            modifier = Modifier()
                .padding(24.px)
                .backgroundColor("#f8f9fa")
                .backgroundImage("radial-gradient(#e0e0e0 1px, transparent 1px)")
                .backgroundSize("20px 20px")
                .minHeight("calc(100vh - 80px)")
        ) {
            Column(
                modifier = Modifier()
                    .maxWidth(1200.px)
                    .margin("0 auto")
                    .gap(40.px)
                    .padding(8.px)
            ) {
                // Introduction section with modern card styling
                Box(
                    modifier = Modifier()
                        .padding(24.px)
                        .backgroundColor("#ffffff")
                        .borderRadius(16.px)
                        .boxShadow("0 12px 24px rgba(0, 0, 0, 0.06)")
                        .border(1.px, "solid", "rgba(230, 230, 230, 0.7)")
                ) {
                    Column(modifier = Modifier().padding(16.px).gap(20.px)) {
                        Text(
                            text = "Welcome to Summon JS Showcase",
                            modifier = Modifier()
                                .fontSize(32.px)
                                .fontWeight(800)
                                .color("#2d3748")
                                .lineHeight(1.2)
                        )

                        Text(
                            text = "This showcase demonstrates the various components and features available in the Summon library for Kotlin/JS applications. Browse through the examples to see what's possible and how to use each component.",
                            modifier = Modifier()
                                .fontSize(16.px)
                                .lineHeight(1.6)
                                .color("#4a5568")
                        )
                    }
                }

                // Core Components Section
                Box(
                    modifier = Modifier()
                        .id("core-components")
                ) {
                    CoreComponentsShowcase()
                }

                // Layout Components Section
                Box(
                    modifier = Modifier()
                        .id("layout-components")
                ) {
                    LayoutComponentsShowcase()
                }

                // Feedback Components Section
                Box(
                    modifier = Modifier()
                        .id("feedback-components")

                ) {
                    FeedbackComponentsShowcase()
                }

                // Custom Components Section
                Box(
                    modifier = Modifier()
                        .id("custom-components")
                ) {
                    CustomComponentsShowcase()
                }
            }
        }
    }
}

/**
 * Navigation button component with enhanced styling and hover effects
 */
@Composable
fun NavButton(text: String, isActive: Boolean, onClick: () -> Unit) {
    Button(
        label = text,
        onClick = onClick,
        modifier = Modifier()
            .padding(10.px, 14.px)
            .borderRadius(8.px)
            .backgroundColor(if (isActive) "#4568dc" else "transparent")
            .color(if (isActive) "#ffffff" else "#4a5568")
            .fontWeight(600)
            .fontSize(15.px)
            .boxShadow(if (isActive) "0 4px 8px rgba(69, 104, 220, 0.25)" else "none")
            .border(1.px, "solid", if (isActive) "#4568dc" else "transparent")
            .transition("all 0.2s ease")
            .hover(
                Modifier()
                    .backgroundColor(if (isActive) "#4568dc" else "rgba(240, 240, 240, 0.5)")
                    .boxShadow(if (isActive) "0 6px 12px rgba(69, 104, 220, 0.3)" else "none")
                    .transform("translateY(-2px)")
            )
    )
}

/**
 * JavaScript function to scroll to a section by ID
 */
fun scrollToSection(elementId: String) {
    val element = document.getElementById(elementId)
    element?.scrollIntoView(js("{ behavior: 'smooth', block: 'start' }"))
}

fun main() {
    document.addEventListener("DOMContentLoaded", { _: Event ->
        val rootElement = document.getElementById("root") as? HTMLElement
            ?: document.createElement("div").apply {
                id = "root"
                document.body?.appendChild(this)
            } as HTMLElement

        // Clear the root element
        rootElement.innerHTML = ""

        // Configure the renderer
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)

        // Provide the renderer in the composition
        val rendererProvider = LocalPlatformRenderer.provides(renderer)

        // Render the application
        renderer.renderComposable {
            SummonShowcase()
        }
    })
}
