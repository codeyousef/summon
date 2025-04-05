# Summon Documentation

Welcome to the Summon documentation! Summon is a Kotlin Multiplatform library for building user interfaces that work seamlessly across JavaScript and JVM platforms.

## Table of Contents

### Getting Started
- [Getting Started Guide](getting-started.md) - Introduction and basic setup
- [Components](components.md) - Learn about Summon's built-in UI components
- [Routing](routing.md) - Set up navigation in your application
- [State Management](state-management.md) - Manage application state effectively
- [Styling](styling.md) - Apply styles to your components

### Lifecycle & Environment Integration
- [Framework-Agnostic Lifecycle Integration](lifecycle-integration.md) - Lifecycle management across different UI frameworks
- [JS Environment Integration](js-environment-integration.md) - Integration with browser, Node.js, and Web Worker environments
- [Backend Lifecycle Integration](backend-lifecycle-integration.md) - Integration with JVM backend frameworks

### Integration Guides
- [Integration Guides](integration-guides.md) - Integrate with existing frameworks and platforms

### Publishing
- [Publishing to Maven Central](publishing.md) - Publish your Summon-based library

## Key Features

- **Cross-Platform**: Build once, run on both JavaScript and JVM platforms
- **Component-Based**: Create reusable UI components
- **Type-Safe**: Leverage Kotlin's type system
- **Styling System**: Flexible and powerful styling using a modifier API
- **State Management**: Simple yet powerful state management solutions
- **Routing**: Cross-platform routing system
- **Lifecycle Aware**: Built-in lifecycle management
- **Framework Interoperability**: Integrate with existing frameworks

## Getting Help

If you need help with Summon, you can:

- Check the documentation in this repository
- [Open an issue](https://github.com/yebaital/summon/issues) for bugs or feature requests
- [Start a discussion](https://github.com/yebaital/summon/discussions) for questions and ideas

## Example

Here's a simple example of a Summon component:

```kotlin
import code.yousef.summon.core.Composable
import code.yousef.summon.components.*
import code.yousef.summon.modifier.*
import code.yousef.summon.state.*

class Counter : Composable {
    override fun render() {
        var count by remember { mutableStateOf(0) }
        
        Column(
            modifier = Modifier
                .padding(16.px)
                .gap(8.px)
        ) {
            Text(
                text = "Count: $count",
                modifier = Modifier
                    .fontSize(24.px)
                    .fontWeight(700)
            )
            
            Row(
                modifier = Modifier.gap(8.px)
            ) {
                Button(
                    text = "Increment",
                    onClick = { count++ },
                    modifier = Modifier
                        .backgroundColor("#0077cc")
                        .color("#ffffff")
                        .padding(8.px, 16.px)
                        .borderRadius(4.px)
                )
                
                Button(
                    text = "Decrement",
                    onClick = { count-- },
                    modifier = Modifier
                        .backgroundColor("#6c757d")
                        .color("#ffffff")
                        .padding(8.px, 16.px)
                        .borderRadius(4.px)
                )
                
                Button(
                    text = "Reset",
                    onClick = { count = 0 },
                    modifier = Modifier
                        .backgroundColor("#dc3545")
                        .color("#ffffff")
                        .padding(8.px, 16.px)
                        .borderRadius(4.px)
                )
            }
        }
    }
}
``` 