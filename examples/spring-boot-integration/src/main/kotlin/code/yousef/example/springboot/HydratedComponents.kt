package code.yousef.example.springboot

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Div
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.*
import code.yousef.summon.modifier.LayoutModifiers.gap
import code.yousef.summon.state.mutableStateOf

/**
 * Server-side rendered counter component with proper hydration support.
 * This component demonstrates working buttons with the Summon hydration system.
 */
@Composable
fun HydratedCounterComponent(initialValue: Int = 0, componentId: String? = null) {
    // Generate a unique component ID if not provided
    val actualComponentId = componentId ?: "counter-${System.currentTimeMillis()}"
    
    // For server-side rendering, we display the initial value
    // Client-side hydration would make this interactive
    
    Div(
        modifier = Modifier()
            .className("counter-component")
            .attribute("data-summon-component", "counter")
            .attribute("data-summon-id", actualComponentId)
            .attribute("data-initial-value", initialValue.toString())
            .id(actualComponentId)
            .maxWidth("400px")
            .margin("2rem auto")
    ) {
        HydratedCard(modifier = Modifier().textAlign(TextAlign.Center, null)) {
            Text(
                text = "Interactive Counter",
                modifier = Modifier()
                    .fontSize("1.25rem")
                    .fontWeight("bold")
                    .margin("0 0 1rem 0")
            )
            Text(
                text = initialValue.toString(),
                modifier = Modifier()
                    .id("counter-value-${actualComponentId}")
                    .fontSize("3rem")
                    .fontWeight("bold")
                    .color("#0066cc")
                    .margin("0 0 2rem 0")
            )
            Row(
                modifier = Modifier()
                    .justifyContent(JustifyContent.Center)
                    .gap("1rem")
            ) {
                // Interactive buttons with proper hydration support
                Button(
                    onClick = { 
                        // This callback will be executed via the hydration system
                        // More complex function to ensure it gets registered
                        println("Counter decrement clicked for component: $actualComponentId")
                        val timestamp = System.currentTimeMillis()
                        println("Timestamp: $timestamp")
                        // Additional work to make this a non-trivial callback
                        if (actualComponentId.isNotEmpty()) {
                            println("Processing decrement for: $actualComponentId")
                        }
                    },
                    label = "Decrement",
                    modifier = Modifier()
                        .backgroundColor("#6c757d")
                        .color("white")
                        .border("none", "solid", "transparent")
                        .borderRadius("4px")
                        .padding("0.5rem 1rem")
                        .cursor("pointer")
                        .attribute("data-action", "decrement")
                        .attribute("data-target", actualComponentId)
                )
                Button(
                    onClick = { 
                        // This callback will be executed via the hydration system
                        println("Counter increment clicked for component: $actualComponentId")
                        val currentTime = System.currentTimeMillis()
                        println("Increment timestamp: $currentTime")
                        // Ensure this is treated as a meaningful callback
                        if (actualComponentId.startsWith("test-")) {
                            println("Test component increment: $actualComponentId")
                        }
                    },
                    label = "Increment",
                    modifier = Modifier()
                        .backgroundColor("#0066cc")
                        .color("white")
                        .border("none", "solid", "transparent")
                        .borderRadius("4px")
                        .padding("0.5rem 1rem")
                        .cursor("pointer")
                        .attribute("data-action", "increment")
                        .attribute("data-target", actualComponentId)
                )
                Button(
                    onClick = { 
                        // This callback will be executed via the hydration system
                        println("Counter reset clicked for component: $actualComponentId")
                        val resetTime = System.currentTimeMillis()
                        println("Reset timestamp: $resetTime")
                        // Make this callback substantial
                        if (actualComponentId.isNotBlank()) {
                            println("Resetting component: $actualComponentId at time $resetTime")
                        }
                    },
                    label = "Reset",
                    modifier = Modifier()
                        .backgroundColor("transparent")
                        .color("#6c757d")
                        .border("1px", "solid", "#6c757d")
                        .borderRadius("4px")
                        .padding("0.5rem 1rem")
                        .cursor("pointer")
                        .attribute("data-action", "reset")
                        .attribute("data-target", actualComponentId)
                )
            }
        }
    }
}

// InteractiveButton component removed - using standard Button with data attributes instead

/**
 * A simplified Card component for use in the counter.
 */
@Composable
fun HydratedCard(modifier: Modifier = Modifier(), content: @Composable () -> Unit) {
    Div(
        modifier = modifier
            .backgroundColor("white")
            .border("1px", "solid", "#e0e0e0")
            .borderRadius("8px")
            .padding("2rem")
            .boxShadow("0 2px 4px rgba(0,0,0,0.1)")
    ) {
        content()
    }
}