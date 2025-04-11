# Summon Documentation

Welcome to the Summon documentation! Summon is a Kotlin Multiplatform library for building user interfaces that work seamlessly across JavaScript and JVM platforms.

## Table of Contents

### Getting Started
- [Components](components.md) - Learn about Summon's built-in UI components
- [Routing](routing.md) - Set up navigation in your application with Next.js-style file-based routing
- [State Management](state-management.md) - Manage application state effectively
- [Styling](styling.md) - Apply styles to your components
- [Accessibility and SEO](accessibility-and-seo.md) - Build accessible and SEO-friendly applications

### Lifecycle & Environment Integration
- [Framework-Agnostic Lifecycle Integration](lifecycle-integration.md) - Lifecycle management across different UI frameworks
- [JS Environment Integration](js-environment-integration.md) - Integration with browser, Node.js, and Web Worker environments
- [Backend Lifecycle Integration](backend-lifecycle-integration.md) - Integration with JVM backend frameworks

### Integration Guides
- [Integration Guides](integration-guides.md) - Integrate with existing frameworks and platforms


## Key Features

- **Cross-Platform**: Write once, run anywhere - browser, server, and native platforms
- **Component-Based**: Build UIs using composable components
- **State Management**: Built-in state management with reactivity
- **Next.js-Style Routing**: Automatic file-based routing with code generation
- **Security**: Built-in authentication and authorization with JWT support
- **Accessibility**: Built-in accessibility features
- **Animation**: Smooth animations and transitions
- **SSR Support**: Server-side rendering capabilities
- **Theme System**: Flexible theming with dark mode support

## Getting Help

If you need help with Summon, you can:

- Check the documentation in this repository
- [Open an issue](https://github.com/yebaital/summon/issues) for bugs or feature requests
- [Start a discussion](https://github.com/yebaital/summon/discussions) for questions and ideas

## Example

Here's a simple example of a Summon component:

```kotlin
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.*
import code.yousef.summon.modifier.*
import code.yousef.summon.state.*

@Composable
fun Counter() {
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
```

## Security

Summon provides a comprehensive security system for handling authentication and authorization:

```kotlin
// Configure security
val securityConfig = securityConfig {
    authenticationProvider = createJwtAuthenticationProvider(
        apiBaseUrl = "https://api.example.com"
    )
    loginUrl = "/login"
    defaultSuccessUrl = "/"
}

// Protect routes
@RequiresAuthentication
@Composable
fun ProfilePage() {
    // Component implementation
}

@RequiresRoles(["admin"])
@Composable
fun AdminDashboard() {
    // Component implementation
}

// Use security-aware components
@Composable
fun SecurityAwareUI() {
    SecuredComponent {
        authenticated {
            // Show authenticated content
        }
        unauthenticated {
            // Show login form
        }
        withRole("admin") {
            // Show admin content
        }
    }
}
```

See [Security Documentation](security.md) for more details. 