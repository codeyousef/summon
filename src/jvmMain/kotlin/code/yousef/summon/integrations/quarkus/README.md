# Summon Quarkus Integration

This module provides integration between Summon and Quarkus, making it easy to use Summon's composable components in Quarkus applications.

## Features

- **HTMX Support**: Properly handles HTMX attributes as separate HTML attributes
- **Qute Template Integration**: Seamlessly integrates with Quarkus Qute templates
- **Component Loading**: Ensures components load properly after navigation
- **Layout Stability**: Prevents layout shifts when indicators appear

## Getting Started

### 1. Add Dependencies

Add the following dependencies to your `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("code.yousef:summon-core:$summonVersion")
    implementation("code.yousef:summon-quarkus-integration:$summonVersion")

    // Quarkus dependencies
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-qute")
    // Add other Quarkus dependencies as needed
}
```

### 2. Configure Quarkus

Create a `SummonConfig.kt` file in your application:

```kotlin
package com.example.application

import code.yousef.summon.integrations.quarkus.EnhancedQuarkusExtension
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton

@ApplicationScoped
class SummonConfig {

    @Produces
    @Singleton
    fun produceSummonRenderer(): EnhancedQuarkusExtension.EnhancedSummonRenderer {
        return EnhancedQuarkusExtension.EnhancedSummonRenderer()
    }
}
```

### 3. Create a Resource Class

Create a resource class to handle HTTP requests:

```kotlin
package com.example.application

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.integrations.quarkus.EnhancedQuarkusExtension
import code.yousef.summon.modifier.Modifier
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/")
class WebResource {

    @Inject
    lateinit var renderer: EnhancedQuarkusExtension.EnhancedSummonRenderer

    @GET
    @Produces(MediaType.TEXT_HTML)
    fun home(): String {
        return renderer.renderTemplate("Home Page") {
            AppRoot {
                Text("Hello from Summon with Quarkus!")
            }
        }
    }

    @Composable
    fun AppRoot(content: @Composable () -> Unit) {
        Box(modifier = Modifier().style("class", "container")) {
            content()
        }
    }
}
```

## Using HTMX Components

The library provides several composable components for HTMX integration:

### HtmxButton

```kotlin
@Composable
fun ButtonExample() {
    HtmxButton(
        label = "Click Me",
        endpoint = "/api/data",
        method = "get",
        target = "#result",
        swap = "innerHTML"
    )
}
```

### HtmxContainer

```kotlin
@Composable
fun ContainerExample() {
    HtmxContainer(
        id = "data-container",
        endpoint = "/api/data",
        trigger = "load",
        minHeight = "200px",
        loadingText = "Loading data..."
    )
}
```

### HtmxForm

```kotlin
@Composable
fun FormExample() {
    HtmxForm(
        action = "/api/submit",
        method = "post",
        target = "#result",
        swap = "outerHTML"
    ) {
        // Form content
    }
}
```

## Integrating with Qute Templates

The library provides components for integrating Qute templates with HTMX:

### HtmxQuteTemplate

```kotlin
@Inject
lateinit var template: Template

@Composable
fun QuteExample() {
    HtmxQuteTemplate(
        template = template,
        hxGet = "/api/refresh",
        hxTarget = "#result",
        "name" to "John Doe",
        "items" to listOf("Item 1", "Item 2", "Item 3")
    )
}
```

### HtmxQuteContainer

```kotlin
@Inject
lateinit var template: Template

@Composable
fun QuteContainerExample() {
    HtmxQuteContainer(
        id = "qute-container",
        template = template,
        endpoint = "/api/data",
        trigger = "load",
        "name" to "John Doe"
    )
}
```

## Advanced Usage

### Navigation Support

The library automatically handles HTMX navigation events, ensuring components load properly after navigation:

```html
<a href="/dashboard" hx-get="/dashboard" hx-target="body" hx-swap="innerHTML">Dashboard</a>
```

### Custom HTMX Attributes

You can add custom HTMX attributes using the `htmlAttribute` extension function:

```kotlin
@Composable
fun CustomExample() {
    Box(
        modifier = Modifier()
            .htmlAttribute("hx-get", "/api/data")
            .htmlAttribute("hx-trigger", "mouseenter")
            .htmlAttribute("hx-target", "#result")
            .htmlAttribute("hx-swap", "innerHTML")
    ) {
        Text("Hover me")
    }
}
```

## Troubleshooting

### HTMX Attributes Not Working

If HTMX attributes are not working, make sure you're using the `EnhancedQuarkusExtension.EnhancedSummonRenderer` class and not the basic `QuarkusExtension.SummonRenderer`.

### Components Not Loading After Navigation

If components don't load after navigation, check that the HTMX navigation script is included in your template. The `EnhancedQuarkusExtension.EnhancedSummonRenderer` includes this script by default.

### Layout Shifts During Loading

Use the `HtmxIndicator` component to show loading indicators without causing layout shifts:

```kotlin
@Composable
fun IndicatorExample() {
    HtmxIndicator(
        id = "loading-indicator",
        text = "Loading..."
    )

    HtmxButton(
        label = "Load Data",
        endpoint = "/api/data",
        target = "#result",
        indicator = "loading-indicator"
    )
}
```
