# Summon Documentation

Welcome to the Summon documentation! Summon is a Kotlin Multiplatform library for building user interfaces that work
seamlessly across JavaScript, WebAssembly (WASM), and JVM platforms.

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

### WebAssembly (WASM)

- [WASM Guide](wasm-guide.md) - **NEW**: Complete guide to building high-performance WASM applications

### API Reference
- [Core API](api-reference/core.md) - Core interfaces and classes
- [Components API](api-reference/components.md) - All UI components reference
- [HTML DSL API](api-reference/html-dsl.md) - **NEW (v0.7.0)**: HTML5 semantic elements DSL
- [Desktop Features API](api-reference/desktop.md) - **NEW (v0.7.0)**: Multi-window and desktop features
- [Modifier API](api-reference/modifier.md) - Styling and layout modifiers (includes new breakpoint shortcuts & scoped
  selectors)
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
- [Visual Builder API](api-reference/builder.md) - Visual builder infrastructure

### Lifecycle & Environment Integration
- [Framework-Agnostic Lifecycle Integration](lifecycle-integration.md) - Lifecycle management across different UI frameworks
- [JS Environment Integration](js-environment-integration.md) - Integration with browser, Node.js, and Web Worker environments
- [Backend Lifecycle Integration](backend-lifecycle-integration.md) - Integration with JVM backend frameworks

### Integration Guides
- [Integration Guides](integration-guides.md) - Integrate with existing frameworks and platforms

### Roadmap
- [Roadmap Overview](roadmap/README.md) - Future plans and version history

## Key Features

- **Cross-Platform**: Write once, run anywhere - browser, server, and native platforms
- **Component-Based**: Build UIs using composable components with rich feedback components
  - **Modal System**: Dialog components with variants (Alert, Confirmation, Fullscreen)
  - **Loading Indicators**: Multiple animation types (Spinner, Dots, Linear, Circular)
  - **Toast Notifications**: Positioned notifications with action support
  - **Video/Audio**: Type-safe media components with browser policy enforcement
- **Network & Communication**: Built-in networking capabilities
  - **WebSocket**: Cross-platform WebSocket with auto-reconnection
  - **HTTP Client**: Full-featured HTTP client with JSON and form support
  - **Storage**: Local, session, and memory storage abstraction
- **State Management**: Built-in state management with reactivity
- **Next.js-Style Routing**: Automatic file-based routing with code generation
- **Security**: Built-in authentication and authorization with JWT support
- **Accessibility**: Built-in accessibility features
- **Animation**: Smooth animations and transitions
- **SSR Support**: Server-side rendering capabilities
- **Theme System**: Flexible theming with dark mode support and CSS variable injection
- **Internationalization**: Full i18n support with RTL layouts for languages like Arabic and Hebrew
- **Visual Builder Support**: Infrastructure for building drag-and-drop UI editors

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

Here's a simple example of a Summon component using the standalone implementation:

```kotlin
// Standalone Summon implementation - no external dependencies required
@Target(AnnotationTarget.FUNCTION)
annotation class Composable

data class Modifier(
    val styles: Map<String, String> = emptyMap(),
    val attributes: Map<String, String> = emptyMap()
) {
    fun style(propertyName: String, value: String): Modifier =
        copy(styles = this.styles + (propertyName to value))
    fun padding(value: String): Modifier = style("padding", value)
    fun gap(value: String): Modifier = style("gap", value)
    fun backgroundColor(color: String): Modifier = style("background-color", color)
    fun color(value: String): Modifier = style("color", value)
    fun fontSize(value: String): Modifier = style("font-size", value)
    fun fontWeight(value: String): Modifier = style("font-weight", value)
    fun borderRadius(value: String): Modifier = style("border-radius", value)
    fun display(value: String): Modifier = style("display", value)
    fun flexDirection(value: String): Modifier = style("flex-direction", value)
    fun cursor(value: String): Modifier = style("cursor", value)
    fun onClick(handler: String): Modifier = copy(attributes = attributes + ("onclick" to handler))
}

class State<T>(var value: T)
fun <T> mutableStateOf(initial: T) = State(initial)
fun <T> remember(calculation: () -> State<T>) = calculation()
fun Modifier(): Modifier = Modifier(emptyMap(), emptyMap())

@Composable
fun Text(text: String, modifier: Modifier = Modifier()): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.styles.entries.joinToString("; ") { "${it.key}: ${it.value}" }}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }}" else ""
    return "<span$attrsStr$styleStr>$text</span>"
}

@Composable
fun Button(text: String, modifier: Modifier = Modifier()): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.styles.entries.joinToString("; ") { "${it.key}: ${it.value}" }}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }}" else ""
    return "<button$attrsStr$styleStr>$text</button>"
}

@Composable
fun Column(modifier: Modifier = Modifier(), content: () -> String): String {
    val columnModifier = modifier.display("flex").flexDirection("column")
    val styleStr = if (columnModifier.styles.isNotEmpty()) " style=\"${columnModifier.styles.entries.joinToString("; ") { "${it.key}: ${it.value}" }}\"" else ""
    return "<div$styleStr>${content()}</div>"
}

@Composable
fun Row(modifier: Modifier = Modifier(), content: () -> String): String {
    val rowModifier = modifier.display("flex").flexDirection("row")
    val styleStr = if (rowModifier.styles.isNotEmpty()) " style=\"${rowModifier.styles.entries.joinToString("; ") { "${it.key}: ${it.value}" }}\"" else ""
    return "<div$styleStr>${content()}</div>"
}

@Composable
fun Counter(): String {
    val count = remember { mutableStateOf(0) }
    
    return Column(
        modifier = Modifier().padding("16px").gap("8px")
    ) {
        Text(
            text = "Count: ${count.value}",
            modifier = Modifier().fontSize("24px").fontWeight("700")
        ) + Row(
            modifier = Modifier().gap("8px")
        ) {
            Button(
                text = "Increment",
                modifier = Modifier()
                    .backgroundColor("#0077cc")
                    .color("#ffffff")
                    .padding("8px 16px")
                    .borderRadius("4px")
                    .cursor(Cursor.Pointer)
                    .onClick("incrementCounter()")
            ) + Button(
                text = "Decrement", 
                modifier = Modifier()
                    .backgroundColor("#6c757d")
                    .color("#ffffff")
                    .padding("8px 16px")
                    .borderRadius("4px")
                    .cursor(Cursor.Pointer)
                    .onClick("decrementCounter()")
            ) + Button(
                text = "Reset",
                modifier = Modifier()
                    .backgroundColor("#dc3545")
                    .color("#ffffff")
                    .padding("8px 16px")
                    .borderRadius("4px")
                    .cursor(Cursor.Pointer)
                    .onClick("resetCounter()")
            )
        }
    }
}
```

## Security

Summon provides security patterns that can be implemented with the standalone components:

```kotlin
// Security-aware component pattern
@Composable
fun SecurityAwareUI(isAuthenticated: Boolean, userRole: String): String {
    return when {
        !isAuthenticated -> LoginForm()
        userRole == "admin" -> AdminDashboard()
        else -> UserDashboard()
    }
}

@Composable
fun LoginForm(): String {
    return Column(
        modifier = Modifier().padding("20px").gap("10px")
    ) {
        Text("Please log in", modifier = Modifier().fontSize("24px").fontWeight(FontWeight.Bold)) +
        Text("Enter your credentials to continue") +
        Button(
            text = "Login",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding("10px 20px")
                .borderRadius("5px")
                .cursor(Cursor.Pointer)
                .onClick("handleLogin()")
        )
    }
}

@Composable
fun AdminDashboard(): String {
    return Column(
        modifier = Modifier().padding("20px").gap("15px")
    ) {
        Text("Admin Dashboard", modifier = Modifier().fontSize("28px").fontWeight(FontWeight.Bold)) +
        Text("Welcome, Administrator!", modifier = Modifier().fontSize("18px")) +
        Button(
            text = "Manage Users",
            modifier = Modifier()
                .backgroundColor("#28a745")
                .color("white")
                .padding("10px 20px")
                .borderRadius("5px")
                .cursor(Cursor.Pointer)
        )
    }
}

@Composable
fun UserDashboard(): String {
    return Column(
        modifier = Modifier().padding("20px").gap("15px")
    ) {
        Text("User Dashboard", modifier = Modifier().fontSize("28px").fontWeight(FontWeight.Bold)) +
        Text("Welcome back!", modifier = Modifier().fontSize("18px")) +
        Button(
            text = "View Profile",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding("10px 20px")
                .borderRadius("5px")
                .cursor(Cursor.Pointer)
        )
    }
}
```

See [Security Documentation](security.md) for more details. 

## Internationalization

Summon supports internationalization patterns with the standalone implementation:

```kotlin
// Simple i18n implementation
data class I18nConfig(
    val currentLanguage: String = "en",
    val isRTL: Boolean = false,
    val translations: Map<String, Map<String, String>> = emptyMap()
)

@Composable
fun createI18nConfig(language: String = "en"): I18nConfig {
    val translations = mapOf(
        "en" to mapOf(
            "welcome" to "Welcome",
            "greeting" to "Hello, World!",
            "language_switcher" to "Language"
        ),
        "fr" to mapOf(
            "welcome" to "Bienvenue",
            "greeting" to "Bonjour le monde!",
            "language_switcher" to "Langue"
        ),
        "ar" to mapOf(
            "welcome" to "مرحبا",
            "greeting" to "مرحبا بالعالم!",
            "language_switcher" to "اللغة"
        )
    )
    
    return I18nConfig(
        currentLanguage = language,
        isRTL = language in listOf("ar", "he"),
        translations = translations
    )
}

@Composable
fun HelloWorld(i18n: I18nConfig): String {
    val welcomeText = i18n.translations[i18n.currentLanguage]?.get("welcome") ?: "Welcome"
    val greetingText = i18n.translations[i18n.currentLanguage]?.get("greeting") ?: "Hello, World!"
    
    return Column(
        modifier = Modifier()
            .padding("20px")
            .style("direction", if (i18n.isRTL) "rtl" else "ltr")
    ) {
        Text(welcomeText, modifier = Modifier().fontSize("24px").fontWeight(FontWeight.Bold)) +
        Text(greetingText, modifier = Modifier().fontSize("18px"))
    }
}

@Composable
fun LanguageSwitcher(currentLanguage: String): String {
    val languages = listOf(
        "en" to "English",
        "fr" to "Français", 
        "ar" to "العربية"
    )
    
    return Row(
        modifier = Modifier().gap("10px")
    ) {
        languages.joinToString("") { (code, name) ->
            Button(
                text = name,
                modifier = Modifier()
                    .backgroundColor(if (code == currentLanguage) "#0077cc" else "#f0f0f0")
                    .color(if (code == currentLanguage) "white" else "black")
                    .padding("8px 16px")
                    .borderRadius("4px")
                    .cursor(Cursor.Pointer)
                    .onClick("changeLanguage('$code')")
            )
        }
    }
}

// Usage example
@Composable
fun MultilingualApp(language: String = "en"): String {
    val i18n = createI18nConfig(language)
    
    return Column(
        modifier = Modifier().padding("20px").gap("20px")
    ) {
        LanguageSwitcher(language) +
        HelloWorld(i18n)
    }
}
```

See [Internationalization Documentation](i18n.md) for more details. 