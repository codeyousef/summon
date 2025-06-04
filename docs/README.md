# Summon Documentation

Welcome to the Summon documentation! Summon is a Kotlin Multiplatform library for building user interfaces that work seamlessly across JavaScript and JVM platforms.

## Table of Contents

### Getting Started
- [Components](components.md) - Learn about Summon's built-in UI components
- [Routing](routing.md) - Set up navigation in your application with Next.js-style file-based routing
- [File-Based Routing](file-based-routing.md) - Next.js-style automatic page discovery
- [State Management](state-management.md) - Manage application state effectively
- [Styling](styling.md) - Apply styles to your components
- [Security](security.md) - Authentication and authorization features
- [Accessibility Guide](accessibility-guide.md) - Build accessible applications
- [Accessibility and SEO](accessibility-and-seo.md) - Build accessible and SEO-friendly applications
- [Internationalization](i18n.md) - Add multi-language support with RTL layouts

### API Reference
- [Core API](api-reference/core.md) - Core interfaces and classes
- [Components API](api-reference/components.md) - All UI components reference
- [Modifier API](api-reference/modifier.md) - Styling and layout modifiers
- [State API](api-reference/state.md) - State management utilities
- [Routing API](api-reference/routing.md) - Navigation and routing
- [Effects API](api-reference/effects.md) - Side effects and lifecycle
- [Events API](api-reference/events.md) - Event handling
- [Animation API](api-reference/animation.md) - Animations and transitions
- [Theme API](api-reference/theme.md) - Theming system
- [Color API](api-reference/color.md) - Color system
- [Focus API](api-reference/focus.md) - Focus management
- [Validation API](api-reference/validation.md) - Form validation
- [Security API](api-reference/security.md) - Security features
- [Authentication API](api-reference/auth.md) - Authentication providers
- [Accessibility API](api-reference/accessibility.md) - Accessibility features
- [SEO API](api-reference/seo.md) - SEO features
- [I18n API](api-reference/i18n-api.md) - Internationalization

### Lifecycle & Environment Integration
- [Framework-Agnostic Lifecycle Integration](lifecycle-integration.md) - Lifecycle management across different UI frameworks
- [JS Environment Integration](js-environment-integration.md) - Integration with browser, Node.js, and Web Worker environments
- [Backend Lifecycle Integration](backend-lifecycle-integration.md) - Integration with JVM backend frameworks

### Integration Guides
- [Integration Guides](integration-guides.md) - Integrate with existing frameworks and platforms

### Examples
- [JS Example](examples/js/js-example/README.md) - JavaScript platform example
- [Quarkus Example](examples/jvm/quarkus-example/README.md) - Quarkus integration example
- [I18n Example](examples/i18n-example.md) - Internationalization example


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
- **Internationalization**: Full i18n support with RTL layouts for languages like Arabic and Hebrew

## Advanced Features

### State Management
- **ViewModel Support**: Base ViewModel class for MVVM pattern
- **Flow Integration**: Kotlin Flow integration for reactive state
- **Remember Saveable**: State preservation across recompositions
- **State Hoisting**: Pattern for lifting state up

### Performance Optimization
- **LazyColumn/LazyRow**: Virtualized lists for large datasets
- **Streaming SSR**: Progressive server-side rendering
- **Dynamic Rendering**: On-demand content rendering
- **Static Generation**: Pre-render static content

### Accessibility & SEO
- **ARIA Support**: Comprehensive ARIA attributes
- **Keyboard Navigation**: Full keyboard support
- **Focus Management**: Advanced focus control
- **Screen Reader Support**: Optimized for assistive technologies
- **Meta Tags**: SEO meta tag management
- **Structured Data**: JSON-LD support
- **Sitemap Generation**: Automatic sitemap creation

### Styling & Theming
- **CSS-in-Kotlin**: Type-safe CSS properties
- **Media Queries**: Responsive design utilities
- **Dark Mode**: Built-in dark mode support
- **Custom Themes**: Create custom theme systems
- **Gradient Support**: Linear and radial gradients
- **Animation**: Keyframes and transitions

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

## Internationalization

Summon provides a flexible internationalization system that supports multiple languages including right-to-left (RTL) languages:

```kotlin
// Configure supported languages
I18nConfig.configure {
    language("en", "English")
    language("fr", "Français")
    language("ar", "العربية", LayoutDirection.RTL)
    language("he", "עברית", LayoutDirection.RTL)
    
    // Set default language
    setDefault("en")
}

// Initialize i18n (for JS platform)
JsI18nImplementation.init()
JsI18nImplementation.loadLanguageResources("/i18n/")

// Use translated strings in components
@Composable
fun HelloWorld() {
    Text(stringResource("common.welcome"))
    
    // Direction-aware modifiers for RTL support
    Row(Modifier.directionalRow()) {
        // Content will adapt to current language direction
        Text(
            stringResource("common.greeting"),
            modifier = Modifier
                .paddingStart("10px")  // Left in LTR, Right in RTL
                .paddingEnd("20px")    // Right in LTR, Left in RTL
        )
    }
}

// Language switcher
@Composable
fun LanguageSwitcher() {
    val currentLanguage = LocalLanguage.current
    
    Row {
        I18nConfig.supportedLanguages.forEach { language ->
            Button(
                onClick = { changeLanguage(language.code) },
                modifier = Modifier.marginEnd("10px")
            ) {
                Text(language.name)
            }
        }
    }
}
```

See [Internationalization Documentation](i18n.md) for more details. 