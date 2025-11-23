# Implementing a Responsive Hamburger Menu with Summon in Ktor

This guide demonstrates the exact correct way to implement a responsive hamburger menu using the Summon framework in a JVM Ktor project.

## Prerequisites

Ensure you have the Summon dependencies in your `build.gradle.kts`:

```kotlin
implementation("codes.yousef.summon:summon-core:0.5.0.5")
// Add other necessary dependencies
```

**Important:** For `MaterialIcon` to work, you must include the Material Icons font in your `index.html` (usually in `src/jsMain/resources/index.html` or similar):

```html
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
```

## Implementation

We will create a `ResponsiveNavBar` composable that switches between a desktop navigation row and a mobile hamburger menu using `ResponsiveLayout`.

### 1. Define the Navigation Component

Create a file `ResponsiveNavBar.kt`:

```kotlin
package com.example.components

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.MaterialIcon
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.ResponsiveLayout
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.components.layout.ScreenSize
import codes.yousef.summon.modifier.*
import codes.yousef.summon.modifier.LayoutModifiers.alignItems
import codes.yousef.summon.modifier.LayoutModifiers.display
import codes.yousef.summon.modifier.LayoutModifiers.gap
import codes.yousef.summon.modifier.LayoutModifiers.justifyContent
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
                .display(Display.Flex)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
                .backgroundColor("#ffffff")
                .style("box-shadow", "0px 2px 4px rgba(0,0,0,0.1)")
        ) {
            Logo()
            
            // Desktop Menu Items
            Row(modifier = Modifier().gap("24px")) {
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
                .style("box-shadow", "0px 2px 4px rgba(0,0,0,0.1)")
        ) {
            // Header Row with Logo and Hamburger Button
            Row(
                modifier = Modifier()
                    .fillMaxWidth()
                    .padding("16px")
                    .display(Display.Flex)
                    .justifyContent(JustifyContent.SpaceBetween)
                    .alignItems(AlignItems.Center)
            ) {
                Logo()

                // Hamburger Icon
                // Wrapped in a Box to ensure proper click target size and z-index
                Box(
                    modifier = Modifier()
                        .cursor(Cursor.Pointer)
                        .style("z-index", "100") // Ensure it's above other elements
                        .padding("8px") // Add padding for touch target
                        .onClick { isOpen.value = !isOpen.value }
                        .display(Display.Flex)
                        .alignItems(AlignItems.Center)
                        .justifyContent(JustifyContent.Center)
                ) {
                    MaterialIcon(
                        name = if (isOpen.value) "close" else "menu",
                        modifier = Modifier()
                            .fontSize("24px")
                            .color("#333")
                            .style("text-transform", "none") // CRITICAL: Prevents "menu" -> "MENU" which breaks ligatures
                            .style("line-height", "1")
                            .display(Display.Block)
                    )
                }
            }

            // Collapsible Menu Content
            // Only visible when isOpen.value is true
            if (isOpen.value) {
                Column(
                    modifier = Modifier()
                        .fillMaxWidth()
                        .padding("0", "16px", "16px", "16px")
                        .gap("12px")
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
            .cursor(Cursor.Pointer)
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

## Important Implementation Notes

### Material Icons Font
The hamburger menu icon requires the Material Icons font to be loaded. Add this to your HTML `<head>`:

```html
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
```

### Imports
Make sure to import the necessary functions from `LayoutModifiers`:
- `gap`, `display`, `justifyContent`, `alignItems` are in `LayoutModifiers` object
- Import them explicitly: `import codes.yousef.summon.modifier.LayoutModifiers.gap`

### MaterialIcon Sizing
The `MaterialIcon` uses the `size` parameter (default is "24px") for sizing. You can also apply `fontSize` via the modifier for consistent sizing with your design system.

## Styling Notes

-   **Mobile First**: The `mobileLayout` uses a `Column` to stack the header and the menu items vertically.
-   **Desktop**: The `desktopLayout` uses a `Row` to place the logo and links horizontally.
-   **State Visibility**: The `if (isOpen.value)` block dynamically adds/removes the menu items from the DOM based on the state.
-   **Icon Visibility**: The icon size is set via the `size` parameter in `MaterialIcon` or via `.fontSize()` modifier to ensure visibility.
