/**
 * # Summon UI Framework
 *
 * A Kotlin Multiplatform UI framework that brings Jetpack Compose-style declarative UI
 * to browser and JVM environments with type-safe styling and reactive state management.
 *
 * ## Framework Overview
 *
 * Summon enables developers to build modern, reactive user interfaces using a declarative
 * approach that works consistently across JavaScript/Browser and JVM/Server platforms.
 * The framework provides:
 *
 * - **Declarative UI**: Build UIs by describing what they should look like for a given state
 * - **Reactive State**: Automatic UI updates when application state changes
 * - **Cross-Platform**: Single codebase works in browsers and on servers
 * - **Type Safety**: Compile-time checking for UI composition and styling
 * - **Performance**: Efficient rendering with minimal updates
 *
 * ## Architecture
 *
 * ### Composition System
 * The heart of Summon is its composition system, inspired by Jetpack Compose:
 * - Functions marked with [@Composable][code.yousef.summon.annotation.Composable] describe UI structure
 * - [Composer][code.yousef.summon.runtime.Composer] tracks composition state and dependencies
 * - [Recomposer][code.yousef.summon.runtime.Recomposer] schedules updates when state changes
 * - Only changed parts of the UI are re-rendered for optimal performance
 *
 * ### Platform Abstraction
 * [PlatformRenderer][code.yousef.summon.runtime.PlatformRenderer] provides a unified API that adapts to different platforms:
 * - **Browser**: Generates DOM elements with event handlers
 * - **Server**: Produces HTML strings for server-side rendering
 * - **Hybrid**: Supports server rendering with client-side hydration
 *
 * ### State Management
 * Reactive state primitives enable automatic UI updates:
 * - [State][code.yousef.summon.state.State] and [MutableState][code.yousef.summon.state.MutableState] interfaces
 * - [mutableStateOf][code.yousef.summon.state.mutableStateOf] creates reactive state holders
 * - [remember][code.yousef.summon.runtime.remember] persists values across recomposition
 * - Property delegation for ergonomic state access
 *
 * ### Type-Safe Styling
 * [Modifier][code.yousef.summon.modifier.Modifier] system provides compile-time safe styling:
 * - Chainable modifier functions for CSS properties
 * - HTML attribute management
 * - Cross-platform compatibility
 * - JavaScript interoperability
 *
 * ## Quick Start
 *
 * ### Basic Component
 * ```kotlin
 * @Composable
 * fun Greeting(name: String) {
 *     Text(
 *         text = "Hello, $name!",
 *         modifier = Modifier()
 *             .padding("16px")
 *             .color("#333")
 *             .fontSize("18px")
 *     )
 * }
 * ```
 *
 * ### Stateful Component
 * ```kotlin
 * @Composable
 * fun Counter() {
 *     var count by remember { mutableStateOf(0) }
 *
 *     Column(
 *         modifier = Modifier()
 *             .padding("20px")
 *             .backgroundColor("#f5f5f5")
 *             .borderRadius("8px")
 *     ) {
 *         Text("Count: $count")
 *
 *         Row {
 *             Button(
 *                 onClick = { count-- },
 *                 label = "-",
 *                 variant = ButtonVariant.SECONDARY
 *             )
 *             Button(
 *                 onClick = { count++ },
 *                 label = "+",
 *                 variant = ButtonVariant.PRIMARY
 *             )
 *         }
 *     }
 * }
 * ```
 *
 * ### Application Setup
 * ```kotlin
 * @Composable
 * fun App() {
 *     ThemeProvider {
 *         Column(
 *             modifier = Modifier()
 *                 .fillMaxSize()
 *                 .padding("16px")
 *         ) {
 *             Greeting("World")
 *             Counter()
 *         }
 *     }
 * }
 *
 * // Browser
 * fun main() {
 *     val renderer = BrowserPlatformRenderer()
 *     setPlatformRenderer(renderer)
 *
 *     renderer.renderComposableRoot { App() }
 * }
 *
 * // Server (Ktor example)
 * fun Application.module() {
 *     routing {
 *         get("/") {
 *             val renderer = ServerPlatformRenderer()
 *             val html = renderer.renderComposableRoot { App() }
 *             call.respondText(html, ContentType.Text.Html)
 *         }
 *     }
 * }
 * ```
 *
 * ## Package Organization
 *
 * ### Core Packages
 * - [code.yousef.summon.annotation] - Core annotations like [@Composable][code.yousef.summon.annotation.Composable]
 * - [code.yousef.summon.runtime] - Composition system and platform abstraction
 * - [code.yousef.summon.state] - Reactive state management primitives
 * - [code.yousef.summon.modifier] - Type-safe styling and attributes system
 *
 * ### Component Packages
 * - [code.yousef.summon.components.foundation] - Foundational components like [ThemeProvider][code.yousef.summon.components.foundation.ThemeProvider]
 * - [code.yousef.summon.components.input] - Interactive components ([Button][code.yousef.summon.components.input.Button], [TextField], etc.)
 * - [code.yousef.summon.components.layout] - Layout containers ([Column], [Row], [Box])
 * - [code.yousef.summon.components.display] - Display components ([Text], [Image], [Icon])
 * - [code.yousef.summon.components.feedback] - User feedback ([Alert], [Loading], [Modal])
 * - [code.yousef.summon.components.navigation] - Navigation elements ([Link], [NavLink])
 *
 * ### Feature Packages
 * - [code.yousef.summon.routing] - File-based routing system
 * - [code.yousef.summon.theme] - Theme management and design tokens
 * - [code.yousef.summon.animation] - Animation and transition support
 * - [code.yousef.summon.accessibility] - Accessibility features and ARIA support
 * - [code.yousef.summon.validation] - Form validation framework
 * - [code.yousef.summon.i18n] - Internationalization and localization
 * - [code.yousef.summon.security] - Authentication and authorization
 * - [code.yousef.summon.ssr] - Server-side rendering optimizations
 *
 * ## Advanced Features
 *
 * ### Effects System
 * Manage side effects and resource lifecycle:
 * ```kotlin
 * @Composable
 * fun DataComponent(id: String) {
 *     var data by remember { mutableStateOf<Data?>(null) }
 *
 *     LaunchedEffect(id) {
 *         data = apiClient.fetchData(id)
 *     }
 *
 *     DisposableEffect(Unit) {
 *         val subscription = eventBus.subscribe { event ->
 *             // Handle events
 *         }
 *         onDispose { subscription.unsubscribe() }
 *     }
 *
 *     data?.let { DataDisplay(it) }
 * }
 * ```
 *
 * ### File-Based Routing
 * Automatic route discovery from file structure:
 * ```
 * src/commonMain/kotlin/pages/
 * ├── Index.kt          → /
 * ├── About.kt          → /about
 * ├── users/
 * │   ├── Index.kt      → /users
 * │   └── Profile.kt    → /users/profile
 * └── 404.kt           → Not found
 * ```
 *
 * ### Theme System
 * Consistent design with theme providers:
 * ```kotlin
 * @Composable
 * fun App() {
 *     ThemeProvider(
 *         theme = EnhancedThemeConfig(
 *             primaryColor = "#007bff",
 *             designTokens = mapOfCompat(
 *                 "--spacing-sm" to "8px",
 *                 "--spacing-md" to "16px",
 *                 "--border-radius" to "6px"
 *             )
 *         )
 *     ) {
 *         // App content with consistent theming
 *     }
 * }
 * ```
 *
 * ## Platform Integration
 *
 * ### Browser/JavaScript
 * - Direct DOM manipulation with event handling
 * - CSS-in-JS styling with hover and animation support
 * - Browser History API integration for routing
 * - Web API access for advanced features
 *
 * ### JVM/Server
 * - HTML string generation for server-side rendering
 * - Integration with popular frameworks (Ktor, Spring Boot, Quarkus)
 * - Template engine compatibility
 * - Static site generation capabilities
 *
 * ### Hybrid SSR + Client
 * - Server-side rendering with client-side hydration
 * - Automatic code splitting and lazy loading
 * - Progressive enhancement patterns
 * - SEO-friendly output with dynamic interactivity
 *
 * ## Development Experience
 *
 * ### Type Safety
 * - Compile-time checking for component composition
 * - Type-safe state management with property delegation
 * - IDE support with autocompletion and refactoring
 * - Clear error messages and debugging information
 *
 * ### Performance
 * - Efficient recomposition with precise dependency tracking
 * - Minimal DOM updates through smart diffing
 * - Lazy loading and code splitting
 * - Memory-efficient state management
 *
 * ### Testing
 * - MockPlatformRenderer for isolated component testing
 * - State management testing utilities
 * - Integration testing patterns
 * - Snapshot testing for visual regression detection
 *
 * ## Framework Comparison
 *
 * Summon draws inspiration from several UI frameworks while providing unique benefits:
 *
 * - **Like Jetpack Compose**: Declarative composition with @Composable functions
 * - **Like React**: Component-based architecture with reactive state
 * - **Like Next.js**: File-based routing with automatic optimization
 * - **Like Vue**: Template-like syntax with reactive data binding
 * - **Unique**: Native Kotlin multiplatform with type-safe CSS
 *
 * ## Getting Started
 *
 * Add Summon to your Kotlin Multiplatform project:
 *
 * ```kotlin
 * // build.gradle.kts
 * dependencies {
 *     // For multiplatform projects
 *     implementation("io.github.codeyousef:summon:0.3.2.1")
 *
 *     // For JVM-only projects
 *     implementation("io.github.codeyousef:summon-jvm:0.3.2.1")
 *
 *     // For JS-only projects
 *     implementation("io.github.codeyousef:summon-js:0.3.2.1")
 * }
 * ```
 *
 * For comprehensive guides, examples, and API documentation, visit the
 * [Summon Documentation](https://summon-ui.dev) website.
 *
 * @author Yousef
 * @since 1.0.0
 */
package code.yousef.summon

