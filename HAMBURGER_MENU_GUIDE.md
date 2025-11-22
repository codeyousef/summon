# Implementing a Responsive Hamburger Menu with Summon in Ktor

This guide demonstrates the exact correct way to implement a responsive hamburger menu using the Summon framework in a JVM Ktor project.

## Prerequisites

Ensure you have the Summon dependencies in your `build.gradle.kts`:

```kotlin
implementation("codes.yousef.summon:summon-core:0.5.0.3")
// Add other necessary dependencies
```

## Implementation

We will create a `ResponsiveNavBar` composable that switches between a desktop navigation row and a mobile hamburger menu using `ResponsiveLayout`.

### 1. Define the Navigation Component

Create a file `ResponsiveNavBar.kt`:

```kotlin
package com.example.components

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.IconDefaults
import codes.yousef.summon.components.display.MaterialIcon
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.ResponsiveLayout
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.components.layout.ScreenSize
import codes.yousef.summon.modifier.*
import codes.yousef.summon.modifier.ModifierExtras.onClick
import codes.yousef.summon.runtime.mutableStateOf
import codes.yousef.summon.runtime.remember

@Composable
fun ResponsiveNavBar() {
    // Define the navigation links
    val links = listOf("Home", "About", "Services", "Contact")

    // Define the Desktop Layout (Horizontal Row)
    val desktopLayout = @Composable {
        Row(
            modifier = Modifier()
                .fillMaxWidth()
                .padding("16px")
                .style("display", "flex")
                .style("justify-content", "space-between")
                .style("align-items", "center")
                .backgroundColor("#ffffff")
                .shadow("0px", "2px", "4px", "rgba(0,0,0,0.1)")
        ) {
            Logo()
            
            // Desktop Menu Items
            Row(modifier = Modifier().style("gap", "24px")) {
                links.forEach { link ->
                    NavLink(text = link)
                }
            }
        }
    }

    // Define the Mobile Layout (Hamburger Menu)
    val mobileLayout = @Composable {
        // State to track if the menu is open
        // This works with Summon's hydration system
        val isOpen = remember { mutableStateOf(false) }

        Column(
            modifier = Modifier()
                .fillMaxWidth()
                .backgroundColor("#ffffff")
                .shadow("0px", "2px", "4px", "rgba(0,0,0,0.1)")
        ) {
            // Header Row with Logo and Hamburger Button
            Row(
                modifier = Modifier()
                    .fillMaxWidth()
                    .padding("16px")
                    .style("display", "flex")
                    .style("justify-content", "space-between")
                    .style("align-items", "center")
            ) {
                Logo()

                // Hamburger Icon
                // Toggles the isOpen state when clicked
                MaterialIcon(
                    name = if (isOpen.value) "close" else "menu",
                    modifier = Modifier().cursor("pointer"),
                    onClick = { isOpen.value = !isOpen.value }
                )
            }

            // Collapsible Menu Content
            // Only visible when isOpen.value is true
            if (isOpen.value) {
                Column(
                    modifier = Modifier()
                        .fillMaxWidth()
                        .padding("0", "16px", "16px", "16px")
                        .style("gap", "12px")
                        .style("border-top", "1px solid #eee")
                        .paddingTop("16px")
                ) {
                    links.forEach { link ->
                        NavLink(
                            text = link, 
                            modifier = Modifier().fillMaxWidth().padding("8px")
                        )
                    }
                }
            }
        }
    }

    // Use ResponsiveLayout to switch between layouts based on screen size
    ResponsiveLayout(
        content = mapOf(
            ScreenSize.SMALL to mobileLayout,
            ScreenSize.MEDIUM to mobileLayout, // Use mobile layout for tablets too if desired
            ScreenSize.LARGE to desktopLayout,
            ScreenSize.XLARGE to desktopLayout
        ),
        defaultContent = desktopLayout
    )
}

@Composable
fun Logo() {
    Text(
        text = "MyBrand",
        modifier = Modifier()
            .fontSize("24px")
            .fontWeight("bold")
            .color("#333")
    )
}

@Composable
fun NavLink(text: String, modifier: Modifier = Modifier()) {
    Text(
        text = text,
        modifier = modifier
            .cursor("pointer")
            .color("#555")
            .hover(mapOf("color" to "#007bff"))
            .onClick { 
                // Handle navigation here
                println("Navigating to $text") 
            }
    )
}
```

### 2. Integration in Ktor (SSR)

In your Ktor route handler, use `respondSummonHydrated` to ensure the client-side state (hydration) works correctly.

```kotlin
import codes.yousef.summon.integration.ktor.KtorRenderer.Companion.respondSummonHydrated
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.homeRoute() {
    get("/") {
        call.respondSummonHydrated {
            Column {
                ResponsiveNavBar()
                
                // Rest of your page content
                Text("Welcome to the home page!", modifier = Modifier().padding("32px"))
            }
        }
    }
}
```

## Key Concepts

1.  **`ResponsiveLayout`**: This component renders all layout variants to the DOM but uses CSS/JS to toggle their visibility based on the client's screen size. This ensures no layout shift during hydration.
2.  **`remember { mutableStateOf(...) }`**: This creates a state variable that survives recompositions. In the context of Summon SSR + Hydration, this state is managed by the client-side runtime after the initial HTML load.
3.  **`onClick` Lambda**: The lambda passed to `onClick` is executed in the client's browser context. Summon handles the binding of this event.
4.  **`MaterialIcon`**: A convenient wrapper for rendering Material Design icons. Ensure you have the Material Icons font loaded in your HTML head (usually handled by Summon's default template or added manually).

## Styling Notes

-   **Mobile First**: The `mobileLayout` uses a `Column` to stack the header and the menu items vertically.
-   **Desktop**: The `desktopLayout` uses a `Row` to place the logo and links horizontally.
-   **State Visibility**: The `if (isOpen.value)` block dynamically adds/removes the menu items from the DOM based on the state.
