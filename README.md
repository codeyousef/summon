# Summon

Summon is a Kotlin Multiplatform (JVM/JS) library that enables developers to generate static websites using Compose-like syntax. With Summon, you can write your website content in a declarative style familiar to Compose developers, and the library will generate the corresponding HTML, CSS, and JavaScript.

## Overview

- **Declarative UI**: Use Compose-style syntax to define your website structure
- **Kotlin Multiplatform**: Works on both JVM and JavaScript platforms
- **Static Site Generation**: Outputs clean HTML, CSS, and JavaScript
- **Type Safety**: Leverage Kotlin's type system for safer web development
- **Responsive Design**: Create adaptive layouts with MediaQuery
- **Consistent Styling**: Maintain visual harmony with the Spacing system
- **Theming**: Support for light/dark modes with ColorSystem
- **Typography**: Predefined text styles for consistent text appearance
- **Environment-Agnostic Lifecycle**: Platform-independent lifecycle management that works across different environments

## Key Benefits

- Write once, deploy anywhere with Kotlin Multiplatform
- Familiar syntax for developers already using Compose
- No runtime dependencies required for the generated websites
- Fully static output for optimal performance and hosting flexibility
- Environment-agnostic lifecycle system that works with multiple backend frameworks and JS environments

## Features

### Spacing System

The Spacing system provides a consistent spacing scale throughout your application:

```kotlin
// Use predefined spacing values
Text(
    text = "Hello World",
    modifier = Modifier()
        .spacingPadding(Spacing.md)
        .spacingMargin(
            top = Spacing.lg,
            bottom = Spacing.md
        )
)

// Available spacing constants
Spacing.xs  // 4px
Spacing.sm  // 8px
Spacing.md  // 16px 
Spacing.lg  // 24px
Spacing.xl  // 32px
Spacing.xxl // 48px

// Custom spacing
Spacing.custom(3) // 12px (3 * base unit of 4px)
```

### Media Query System

The MediaQuery system enables responsive design with a simple API:

```kotlin
// Create responsive components
Card(
    modifier = Modifier()
        // Base styles for all screen sizes
        .padding("20px")
        // Apply different styles based on screen size
        .responsive(
            MediaQuery.mobile(
                Modifier().padding("10px")
            ),
            MediaQuery.desktop(
                Modifier().maxWidth("800px")
            )
        ),
    content = listOf(/* your content */)
)

// Using predefined breakpoints
MediaQuery.Breakpoints.xs // 0px (mobile)
MediaQuery.Breakpoints.sm // 600px (tablet)
MediaQuery.Breakpoints.md // 960px (small desktop)
MediaQuery.Breakpoints.lg // 1280px (desktop)
MediaQuery.Breakpoints.xl // 1920px (large desktop)

// Special media query helpers
MediaQuery.darkMode(Modifier().background("#222"))
MediaQuery.lightMode(Modifier().background("#fff"))
MediaQuery.orientation(isPortrait = true, styleModifier)
MediaQuery.reducedMotion(styleModifier)
```

### Color System

The ColorSystem provides predefined color schemes with support for light and dark modes:

```kotlin
// Use semantic color names
Text(
    text = "Primary Text",
    modifier = Modifier()
        .textColor("primary")  // Extension function using ColorSystem
        .backgroundColor("surface")
)

// Set theme mode globally
ColorSystem.setThemeMode(ColorSystem.ThemeMode.DARK)

// Or override for specific components
Button(
    label = "Dark Mode Button",
    modifier = Modifier()
        .backgroundColor("primary", ColorSystem.ThemeMode.DARK)
        .textColor("onPrimary", ColorSystem.ThemeMode.DARK)
)

// Predefined color palettes
val primaryColor = ColorSystem.default.light["primary"] // #1976d2
val primaryDarkColor = ColorSystem.default.dark["primary"] // #90caf9

// Alternative themes
val blueTheme = ColorSystem.blue
val greenTheme = ColorSystem.green
val purpleTheme = ColorSystem.purple

// Utility functions
val transparentColor = ColorSystem.withAlpha("#FF0000", 0.5f) // rgba(255, 0, 0, 0.5)
```

### Typography

Typography provides consistent text styling presets:

```kotlin
// Use the helper functions for common text styles
h1Text("Heading 1")
h2Text("Heading 2")
bodyText("Regular body text")
captionText("Small caption text")
codeText("Code snippet")

// Apply typography presets to your own text components
Text(
    text = "Custom text with preset",
    modifier = Typography.h1.applyTo(Modifier().textColor("primary"))
)

// Use specific typography styles
val bodyStyle = Typography.body
val buttonStyle = Typography.button

// Create text with custom styles
typographyText(
    text = "Custom styled text",
    style = Typography.bodyLarge,
    modifier = Modifier().textColor("secondary")
)

// Standard font families
Typography.FontFamilies.system     // Sans-serif system font stack
Typography.FontFamilies.monospace  // Monospace font stack
Typography.FontFamilies.serif      // Serif font stack

// Font weights
Typography.FontWeights.regular  // 400
Typography.FontWeights.bold     // 700
```

### Theme System

The Theme system provides a centralized styling configuration that combines ColorSystem, Typography, and Spacing:

```kotlin
// Create a custom theme
val customTheme = Theme.createTheme { 
    // Start with base theme and customize it
    copy(
        colorPalette = ColorSystem.purple,
        customValues = mapOf(
            "appHeaderHeight" to "64px",
            "sidebarWidth" to "250px"
        )
    )
}

// Set the active theme
Theme.setTheme(customTheme)

// Get values from the theme
val primaryColor = Theme.getColor("primary")
val spacing = Theme.getSpacing("md")
val borderRadius = Theme.getBorderRadius("sm")
val elevation = Theme.getElevation("md")
val textStyle = Theme.getTextStyle("body")
val customValue = Theme.getCustomValue("appHeaderHeight", "60px")

// Apply theme values to modifiers
val buttonModifier = Modifier()
    .themeBackgroundColor("primary")
    .themeColor("onPrimary")
    .themeBorderRadius("sm")
    .themePadding("md")
    .themeElevation("sm")
```

### StyleSheet

The StyleSheet provides a way to define and reuse styles throughout your application:

```kotlin
// Define reusable styles
createStyleSheet {
    // Define a card style
    style("card") {
        this
            .themeBackgroundColor("surface")
            .themeBorderRadius("md")
            .themeElevation("sm")
            .themePadding("md")
    }
    
    // Define a primary button style
    style("primaryButton") {
        this
            .themeBackgroundColor("primary")
            .themeColor("onPrimary")
            .themeBorderRadius("sm")
            .themePadding("sm")
            .cursor("pointer")
    }
    
    // Extend an existing style
    extendStyle("outlinedButton", "primaryButton") {
        this
            .backgroundColor("transparent")
            .themeColor("primary")
            .themeStyleBorder("1px", "solid", "primary")
    }
}

// Apply styles to components
val card = Card(
    modifier = Modifier().applyStyle("card"),
    content = listOf(
        Text("Card Title"),
        Button(
            label = "Learn More",
            modifier = Modifier().applyStyle("primaryButton")
        )
    )
)

// Combine multiple styles
val specialButton = Button(
    label = "Special Button",
    modifier = Modifier().applyStyles("primaryButton", "card")
)
```

Combine these systems to create beautiful, consistent UIs:

```kotlin
Column(
    modifier = Modifier()
        .backgroundColor("background")
        .spacingPadding(Spacing.lg)
        .responsive(
            MediaQuery.mobile(
                Modifier().spacingPadding(Spacing.md)
            )
        ),
    content = listOf(
        h1Text(
            "Responsive Heading",
            Modifier()
                .textColor("primary")
                .marginBottom(Spacing.md)
        ),
        bodyText(
            "This text uses consistent typography, colors, and spacing.",
            Modifier()
                .textColor("onSurface")
                .responsive(
                    MediaQuery.mobile(
                        Modifier().fontSize("14px")
                    )
                )
        )
    )
)
```

### Navigation System

The navigation system allows for client-side routing and component-based routing:

```kotlin
// Create a router with routes
val routerBuilder = Router.RouterBuilder()
routerBuilder.route("/") { params ->
    HomePage()
}
routerBuilder.route("/about") { params ->
    AboutPage()
}
routerBuilder.route("/user/:userId") { params ->
    UserProfilePage(params)
}
routerBuilder.route("/settings") { params ->
    SettingsPage()
}
// Add redirect for old routes
routerBuilder.route("/old-page") { params ->
    Redirect.to("/", replace = true)
}
// Handle 404 routes
routerBuilder.notFound { params ->
    NotFoundPage()
}
val router = Router(routerBuilder.routes, routerBuilder.notFoundComponent)

// Using NavLink components for navigation with active state
NavLink.create(
    to = "/about",
    text = "About",
    className = "nav-item",
    activeClassName = "nav-item-active"
)

// Access route parameters
class UserProfilePage(private val params: RouteParams) : Composable {
    override fun <T> compose(receiver: T): T {
        val userId = params["userId"] ?: "unknown"
        // ... render user profile with userId
    }
}

// Navigate programmatically with History
Button(
    label = "Back",
    onClick = { router.goBack() }
)
Button(
    label = "Forward",
    onClick = { router.goForward() }
)

// Create deep links with metadata
DeepLinking.metaTags(
    path = "/user/123",
    title = "User Profile",
    description = "View user profile details"
)

// Create shareable URLs
val deepLink = DeepLinking.createUrl(
    path = "/user/123",
    queryParams = mapOf("section" to "profile"),
    fragment = "contact-info"
)
```

### State Management System

Summon provides a robust state management system with powerful features for handling application state:

```kotlin
// Basic state management
val counter = mutableStateOf(0)
Button(
    label = "Count: ${counter.value}",
    onClick = { counter.value++ }
)

// RememberSaveable - State that persists across recompositions
val persistedCounter = rememberSaveable("counter", 0)
Button(
    label = "Persisted Count: ${persistedCounter.value}",
    onClick = { persistedCounter.value++ }
)

// ViewModel - Component for encapsulating and managing UI-related data
class UserViewModel : ViewModel() {
    val username = state("username", "Guest")
    val isLoggedIn = state("isLoggedIn", false)
    
    fun login(name: String) {
        username.value = name
        isLoggedIn.value = true
    }
    
    fun logout() {
        username.value = "Guest"
        isLoggedIn.value = false
    }
}

// Creating and using a ViewModel
val userViewModel = viewModel { UserViewModel() }
Column(
    content = listOf(
        Text("Hello, ${userViewModel.username.value}"),
        Button(
            label = if (userViewModel.isLoggedIn.value) "Logout" else "Login",
            onClick = {
                if (userViewModel.isLoggedIn.value) {
                    userViewModel.logout()
                } else {
                    userViewModel.login("John")
                }
            }
        )
    )
)

// Flow Integration - Connect Kotlin Flows to UI state
val timerFlow = flow {
    var count = 0
    while (true) {
        emit(count++)
        delay(1000)
    }
}

// Convert a Flow to a State
val timerState = timerFlow.toState(initialValue = 0)
Text("Timer: ${timerState.value}")

// StateFlow Integration - Connect to existing StateFlow-based architecture
val temperatureStateFlow = MutableStateFlow(22.5)
val temperature = stateFromStateFlow(temperatureStateFlow)

// Use the state in UI
Text("Temperature: ${temperature.value}°C")
Button(
    label = "Increase",
    onClick = { temperatureStateFlow.value += 0.5 }
)

// Convert a MutableState to a StateFlow for interop with other systems
val counterState = mutableStateOf(0)
val counterFlow = counterState.toStateFlow()

// Platform-specific persistence (JS)
val persistedSettings = mutableStateOf("default")
persistedSettings.persistToLocalStorage(
    key = "user-settings",
    serializer = { it },
    deserializer = { it }
)

// Platform-specific persistence (JVM)
val appSettings = mutableStateOf("default")
appSettings.persistToPreferences(
    key = "app-settings",
    nodeName = "my-app"
)
```

### Environment-Agnostic Lifecycle System

The lifecycle system provides a consistent way to manage component lifecycles across different environments:

```kotlin
// Create a lifecycle-aware component
val lifecycleOwner = currentLifecycleOwner() // Gets the appropriate implementation for the platform
val component = LifecycleAwareComponent(
    lifecycleOwner = lifecycleOwner,
    onResume = {
        // Component is visible and active
        startServices()
    },
    onPause = {
        // Component is hidden but still in memory
        pauseServices()
    },
    onDestroy = {
        // Component is being destroyed
        cleanupResources()
    }
)

// Use with coroutines
val scope = lifecycleCoroutineScope(lifecycleOwner)
scope.launch {
    // This coroutine will be cancelled if the lifecycle owner is destroyed
    while(true) {
        fetchLatestData()
        delay(60000) // Refresh every minute
    }
}

// Automatically run code only when the component is active
whenActive(lifecycleOwner, "background-task") {
    // This block only runs when the component is active (STARTED or RESUMED)
    // and automatically pauses when inactive
}
```

#### JVM Backend Framework Integration

On JVM, the lifecycle system automatically detects and integrates with popular backend frameworks:

```kotlin
// Register custom hooks for JVM applications
JvmLifecycleOwner.addStartupHook {
    // Initialize resources when the application starts
}

JvmLifecycleOwner.addShutdownHook {
    // Release resources when the application shuts down
}

// Works with Spring Boot, Ktor, Quarkus, and Micronaut
// No manual configuration required - automatic detection!
```

#### JavaScript Environment Integration

On JS, the lifecycle system works across different JavaScript environments:

```kotlin
// Register custom hooks for JS applications
JsLifecycleOwner.addStartupHook {
    // Initialize resources when the application starts
}

JsLifecycleOwner.addShutdownHook {
    // Release resources when the application shuts down
}

// For browser environments, respond to visibility changes
JsLifecycleOwner.addVisibilityChangeHook { isVisible ->
    if (isVisible) {
        // Tab is visible - refresh data
        refreshData()
    } else {
        // Tab is hidden - pause background tasks
        pauseBackgroundTasks()
    }
}

// Works with browser, Node.js, and Web Worker environments
// No manual configuration required - automatic detection!
```

### 4.7 Effects and Lifecycle

- **LaunchedEffect** ✅: For side effects on composition
- **DisposableEffect** ✅: For cleanup on decomposition
- **SideEffect** ✅: For non-cancellable side effects
- **DerivedState** ✅: For computed values from other states
- **LifecycleAware Components** ✅: Integration with platform lifecycles
- **Environment-Agnostic Lifecycle** ✅: Consistent lifecycle API across JVM and JS platforms
- **Backend Framework Detection** ✅: Automatic integration with Spring, Ktor, Quarkus, and Micronaut
- **JS Environment Detection** ✅: Support for browser, Node.js, and Web Worker environments

### 4.8 Animations

- **AnimatedVisibility** ✅: For appearance/disappearance animations with configurable enter and exit transitions including fade, slide, expand/shrink, and zoom effects.
- **AnimatedContent** ✅: For content change animations with crossfade, slide, fade, and scale transitions between different states.
- **Transition** ✅: For state-based animations that interpolate between different states.
- **InfiniteTransition** ✅: For continuous animations with configurable animation parameters.
- **Spring Animation** ✅: Physics-based animations with stiffness and damping parameters.
- **Tween Animation** ✅: Time-based animations with easing functions.

```kotlin
// Animate visibility of content
AnimatedVisibility(
    visible = isVisible.value,
    enterTransition = EnterTransition.FADE_IN,
    exitTransition = ExitTransition.FADE_OUT,
    enterParams = TransitionParams(duration = 300),
    content = listOf(Text("I appear and disappear with animation"))
)

// Animate between different content states
AnimatedContent(
    targetState = currentTab,
    transitionType = ContentTransitionType.CROSSFADE,
    duration = 500,
    contentFactory = { tab ->
        when (tab) {
            Tab.HOME -> listOf(HomePage())
            Tab.PROFILE -> listOf(ProfilePage())
            Tab.SETTINGS -> listOf(SettingsPage())
        }
    }
)

// Create state-based animations
transition(
    state = counter,
    animation = SpringAnimation(stiffness = 170f, damping = 26f),
    content = { transition ->
        listOf(
            Text(
                "Count: ${transition.state.value}",
                Modifier()
                    .scale(transition.progress.value)
                    .opacity(transition.progress.value)
            )
        )
    }
)

// Create continuous animations
infiniteTransition(
    running = true,
    content = { infiniteTransition ->
        val scale = infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.2f,
            animation = TweenAnimation(durationMs = 1000, repeating = true)
        )
        
        listOf(
            Text(
                "Pulsing Text", 
                Modifier().scale(scale.value)
            )
        )
    }
)

// Helper functions
val pulsatingButton = pulseAnimation(
    content = listOf(Button("Click Me")),
    pulseInterval = 1500,
    minScale = 0.95f,
    maxScale = 1.05f
)

// Animation modifiers
val animatedModifier = Modifier()
    .fadeIn(duration = 300)
    .transition("transform", 500, "ease-out")
    .slideIn(SlideDirection.LEFT, "100px", 400)
```

### 4.9 Framework Integration

#### 4.9.1 Spring Integration

- **SpringViewResolver**: Custom ViewResolver for Summon components
- **ThymeleafSummonDialect**: Custom dialect for using Summon in Thymeleaf
- **ModelAttributeBinding**: Connection to Spring model attributes
- **SpringSecuritySupport**: Integration with authentication/authorization
- **SpringFormBinding**: Integration with Spring form binding
- **SpringValidation**: Integration with Bean Validation
- **SpringBootStarter**: Auto-configuration for Spring Boot

#### 4.9.2 Quarkus Integration

- **SummonQuteExtension**: Integration with Qute templates
- **QuarkusExtension**: Dedicated Quarkus extension
- **QuteTagExtension**: Custom tags for Qute templates
- **ResteasyIntegration**: Integration with RESTEasy
- **CDISupport**: Injection of beans in components
- **NativeImageSupport**: Support for GraalVM native image

#### 4.9.3 Ktor Integration

- **SummonApplicationCall**: Extension functions for ApplicationCall
- **KtorTemplating**: Integration with Ktor's templating features
- **KtorRouting**: Extensions for routing DSL
- **KtorSessionState**: Integration with Ktor sessions
- **KtorAuthentication**: Integration with authentication

### 4.10 SEO Enhancements

- **MetaTags** ✅: Easy configuration of meta tags
- **StructuredData** ✅: JSON-LD generation helpers
- **SemanticHTML** ✅: Semantic HTML element helpers
- **SitemapGeneration** ✅: Sitemap creation utilities
- **CanonicalLinks** ✅: Management of canonical URLs
- **OpenGraphTags** ✅: Social media metadata support
- **TwitterCards** ✅: Twitter-specific metadata

```kotlin
// Meta tags for search engines
MetaTags(
    title = "My Website",
    description = "A description of my website for search engines",
    keywords = "summon, ui, web framework",
    author = "Your Name"
).compose(head)

// Structured data for rich results
StructuredData.product(
    name = "Amazing Product",
    description = "This product will change your life",
    image = "https://example.com/product1.jpg",
    price = "99.99",
    currency = "USD"
).compose(head)

// Semantic HTML for better content structure
SemanticHTML.Main(className = "content") {
    SemanticHTML.Article(className = "product-description") {
        SemanticHTML.Heading(level = 1) { +"Product Name" }
        // Article content
    }.compose(this)
}.compose(this)

// Generate sitemap from routes
val sitemap = SitemapGeneration.fromRoutes(
    routes = myRoutes,
    baseUrl = "https://example.com"
)

// Use within a route handler
Route("/sitemap.xml") { _ ->
    return@Route object : Composable {
        override fun <T> compose(receiver: T): T {
            if (receiver is TagConsumer<*>) {
                receiver.onTagContent(sitemap.getXmlString())
            }
            return receiver
        }
    }
}

// Canonical links to handle duplicate content
CanonicalLinks.withLanguages(
    defaultUrl = "https://example.com/products/1",
    defaultLanguage = "en",
    alternates = mapOf(
        "es" to "https://example.com/es/products/1",
        "fr" to "https://example.com/fr/products/1"
    )
).compose(head)

// Open Graph tags for social media sharing
OpenGraphTags.article(
    title = "Article Title",
    url = "https://example.com/article/1",
    image = "https://example.com/article-image.jpg",
    description = "Article description",
    publishedTime = "2023-05-15T14:30:00Z",
    author = "Author Name"
).compose(head)

// Twitter Cards for Twitter-specific sharing
TwitterCards.article(
    title = "Article Title",
    description = "Article description for Twitter",
    image = "https://example.com/article-image.jpg",
    site = "@twitterhandle",
    creator = "@authorhandle"
).compose(head)
```

### 4.11 Accessibility

- **ARIA Attributes** ✅: Built-in accessibility attributes for components.
- **KeyboardNavigation**: Support for keyboard interaction
- **FocusManagement**: Utilities for managing focus
- **ScreenReaderText** ✅: Utilities for screen reader content including hidden text.
- **AccessibilityTree**: Inspection of accessibility hierarchy
- **ContrastChecking**: Color contrast validation

### 4.12 Developer Tools

- **Inspector**: Component inspection tool
- **StateVisualizer**: State visualization and debugging
- **PerformanceMonitoring**: Performance measurement utilities
- **LoggingUtilities**: Enhanced logging for development
- **HotReload**: Development time hot reloading
- **TestingUtilities**: Component testing helpers

### 4.13 Server-Side Rendering

- **StaticRendering**: Generation of static HTML
- **DynamicRendering**: Server-side rendering with dynamic data
- **HydrationSupport**: Client-side reactivation of server-rendered HTML
- **PartialHydration**: Selective hydration of interactive elements
- **StreamingSSR**: Streaming server-side rendering for large pages
- **SEOPrerender**: Pre-rendering for search engine crawlers

## 5. Component Usage Examples

```kotlin
// Basic component usage
val greeting = Column(
    modifier = Modifier()
        .padding("20px")
        .background("#f5f5f5"),
    content = listOf(
        Text("Hello, World!", 
            Modifier()
                .fontSize("24px")
                .color("#333333")
        ),
        Spacer("10px", true),
        Button(
            "Click Me",
            onClick = { println("Button clicked!") },
            modifier = Modifier()
                .padding("10px")
                .background("#4CAF50")
                .color("white")
        )
    )
)

// Spring controller example
@Controller
class HomeController {
    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("title", "Home Page")
        model.addAttribute("greeting", "Welcome to Summon!")
        
        return "home" // Uses SummonViewResolver
    }
}

// Ktor route example
fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondSummon(HomeScreen())
        }
    }
}

// Quarkus resource example
@Path("/")
class HomeResource {
    @Inject
    lateinit var summonRenderer: SummonRenderer
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun home(): String {
        return summonRenderer.render(HomeScreen())
    }
}
```

---

# Design & Development Reference

## 1. Executive Summary

Summon is a Kotlin Multiplatform UI library that enables developers to create SEO-friendly web applications using a declarative, Compose-like syntax. The library generates HTML, CSS, and JavaScript for both server-side and client-side rendering, bridging the gap between modern UI development patterns and traditional web technologies. Summon can be used standalone or integrated with popular JVM backend frameworks including Spring Boot, Quarkus, and Ktor.

## 2. Vision and Goals

### 2.1 Vision

To provide a unified, declarative UI development experience across JVM and JavaScript platforms that generates SEO-friendly web content while maintaining the developer experience of modern UI frameworks like Jetpack Compose.

### 2.2 Goals

- Create a consistent API that mirrors Compose's declarative paradigm
- Enable code sharing between JVM and JavaScript platforms
- Generate semantic, accessible HTML that is SEO-friendly
- Provide seamless integration with Spring, Quarkus, and Ktor
- Support both server-side rendering and client-side interactivity
- Maintain a low learning curve for developers familiar with Compose
- Deliver high performance through efficient rendering strategies

## 3. Architecture Overview

Summon follows a multi-layered architecture designed to abstract platform-specific implementation details while providing a consistent API across environments:

1. **API Layer**: Compose-like declarative API for defining UI components
2. **Intermediate Representation Layer**: Platform-agnostic model of UI state
3. **Rendering Layer**: Platform-specific renderers for JVM and JS targets
4. **Framework Integration Layer**: Adapters for backend frameworks

This architecture allows developers to write UI code once and deploy it across different platforms and frameworks.

## 4. Feature Implementation Status

### 4.1 Core Components

- **Text Component** ✅: Enhanced with support for styled text, overflow handling, accessibility features, and utility functions for common text styles.
- **Button Component** ✅: For triggering actions with click event handling.
- **Image Component** ✅: For displaying images with accessibility support including alt text and loading strategies.
- **Link Component** ✅: For navigation with SEO-friendly attributes and proper security features.
- **Icon Component** ✅: For vector icons and font icons
- **Divider Component** ✅: For visual separation with customizable appearance.
- **Card Component** ✅: For grouped content with styling including elevation and border radius.
- **Alert Component** ✅: For notifications and messages
- **Badge Component** ✅: For status indicators
- **Tooltip Component** ✅: For additional information on hover
- **Progress Component** ✅: For loading and progress indication

### 4.2 Form Components

- **TextField** ✅: For text input with validation and various input types.
- **TextArea** ✅: For multi-line text input
- **Checkbox** ✅: For boolean input
- **RadioButton** ✅: For exclusive selection
- **Select/Dropdown** ✅: For option selection
- **DatePicker** ✅: For date selection
- **TimePicker** ✅: For time selection
- **RangeSlider** ✅: For range selection
- **Switch** ✅: For toggle controls
- **FileUpload** ✅: For file selection and uploads
- **Form** ✅: Container with validation and submission handling.
- **FormField** ✅: Wrapper for form controls with labels and validation

### 4.3 Layout Components

- **Row** ✅: Horizontal layout container using Flexbox.
- **Column** ✅: Vertical layout container using Flexbox.
- **Box** ✅: For positioning and stacking elements
- **Grid** ✅: CSS Grid-based responsive layout
- **Spacer** ✅: For creating empty space with customizable dimensions.
- **AspectRatio** ✅: For maintaining width-to-height ratios
- **ResponsiveLayout** ✅: For adaptive layouts based on screen size
- **LazyColumn** ✅: For virtualized vertical lists
- **LazyRow** ✅: For virtualized horizontal lists
- **TabLayout** ✅: For tabbed interfaces
- **ExpansionPanel** ✅: For collapsible sections

### 4.4 Styling System

- **Modifier** ✅: Chainable styling API similar to Compose
- **Theme** ✅: Global styling configuration
- **StyleSheet** ✅: Component for defining reusable styles
- **ColorSystem** ✅: Predefined color schemes with dark/light modes
- **Typography** ✅: Text styling presets
- **Spacing** ✅: Consistent spacing system
- **MediaQuery** ✅: Responsive style adjustments

### 4.5 Navigation

- **Router** ✅: Client-side routing system with path-based navigation.
- **NavLink** ✅: Navigation component with active state detection for creating navigation links.
- **Redirect** ✅: Declarative redirection to different routes.
- **RouteParams** ✅: Access to route parameters extracted from URL paths.
- **DeepLinking** ✅: Support for direct navigation to specific states with SEO enhancements.
- **History** ✅: Navigation history management with back/forward navigation support.

### 4.6 State Management

- **State** ✅: Observable state container for reading values.
- **MutableState** ✅: Modifiable state container for reading and writing values.
- **RememberSaveable** ✅: Persisted state across recompositions
- **ViewModel Integration** ✅: Connection to platform ViewModels
- **FlowBinding** ✅: Integration with Kotlin Flows
- **StateFlow Integration** ✅: Support for StateFlow and SharedFlow

### 4.7 Effects and Lifecycle

- **LaunchedEffect** ✅: For side effects on composition
- **DisposableEffect** ✅: For cleanup on decomposition
- **SideEffect** ✅: For non-cancellable side effects
- **DerivedState** ✅: For computed values from other states
- **LifecycleAware Components** ✅: Integration with platform lifecycles
- **Environment-Agnostic Lifecycle** ✅: Consistent lifecycle API across JVM and JS platforms
- **Backend Framework Detection** ✅: Automatic integration with Spring, Ktor, Quarkus, and Micronaut
- **JS Environment Detection** ✅: Support for browser, Node.js, and Web Worker environments

### 4.8 Animations

- **AnimatedVisibility** ✅: For appearance/disappearance animations with configurable enter and exit transitions including fade, slide, expand/shrink, and zoom effects.
- **AnimatedContent** ✅: For content change animations with crossfade, slide, fade, and scale transitions between different states.
- **Transition** ✅: For state-based animations that interpolate between different states.
- **InfiniteTransition** ✅: For continuous animations with configurable animation parameters.
- **Spring Animation** ✅: Physics-based animations with stiffness and damping parameters.
- **Tween Animation** ✅: Time-based animations with easing functions.

### 4.9 Framework Integration

#### 4.9.1 Spring Integration

- **SpringViewResolver**: Custom ViewResolver for Summon components
- **ThymeleafSummonDialect**: Custom dialect for using Summon in Thymeleaf
- **ModelAttributeBinding**: Connection to Spring model attributes
- **SpringSecuritySupport**: Integration with authentication/authorization
- **SpringFormBinding**: Integration with Spring form binding
- **SpringValidation**: Integration with Bean Validation
- **SpringBootStarter**: Auto-configuration for Spring Boot

#### 4.9.2 Quarkus Integration

- **SummonQuteExtension**: Integration with Qute templates
- **QuarkusExtension**: Dedicated Quarkus extension
- **QuteTagExtension**: Custom tags for Qute templates
- **ResteasyIntegration**: Integration with RESTEasy
- **CDISupport**: Injection of beans in components
- **NativeImageSupport**: Support for GraalVM native image

#### 4.9.3 Ktor Integration

- **SummonApplicationCall**: Extension functions for ApplicationCall
- **KtorTemplating**: Integration with Ktor's templating features
- **KtorRouting**: Extensions for routing DSL
- **KtorSessionState**: Integration with Ktor sessions
- **KtorAuthentication**: Integration with authentication

### 4.10 SEO Enhancements

- **MetaTags** ✅: Easy configuration of meta tags
- **StructuredData** ✅: JSON-LD generation helpers
- **SemanticHTML** ✅: Semantic HTML element helpers
- **SitemapGeneration** ✅: Sitemap creation utilities
- **CanonicalLinks** ✅: Management of canonical URLs
- **OpenGraphTags** ✅: Social media metadata support
- **TwitterCards** ✅: Twitter-specific metadata

### 4.11 Accessibility

- **ARIA Attributes** ✅: Built-in accessibility attributes for components.
- **KeyboardNavigation**: Support for keyboard interaction
- **FocusManagement**: Utilities for managing focus
- **ScreenReaderText** ✅: Utilities for screen reader content including hidden text.
- **AccessibilityTree**: Inspection of accessibility hierarchy
- **ContrastChecking**: Color contrast validation

### 4.12 Developer Tools

- **Inspector**: Component inspection tool
- **StateVisualizer**: State visualization and debugging
- **PerformanceMonitoring**: Performance measurement utilities
- **LoggingUtilities**: Enhanced logging for development
- **HotReload**: Development time hot reloading
- **TestingUtilities**: Component testing helpers

### 4.13 Server-Side Rendering

- **StaticRendering**: Generation of static HTML
- **DynamicRendering**: Server-side rendering with dynamic data
- **HydrationSupport**: Client-side reactivation of server-rendered HTML
- **PartialHydration**: Selective hydration of interactive elements
- **StreamingSSR**: Streaming server-side rendering for large pages
- **SEOPrerender**: Pre-rendering for search engine crawlers

## 5. Component Usage Examples

```kotlin
// Basic component usage
val greeting = Column(
    modifier = Modifier()
        .padding("20px")
        .background("#f5f5f5"),
    content = listOf(
        Text("Hello, World!", 
            Modifier()
                .fontSize("24px")
                .color("#333333")
        ),
        Spacer("10px", true),
        Button(
            "Click Me",
            onClick = { println("Button clicked!") },
            modifier = Modifier()
                .padding("10px")
                .background("#4CAF50")
                .color("white")
        )
    )
)

// Spring controller example
@Controller
class HomeController {
    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("title", "Home Page")
        model.addAttribute("greeting", "Welcome to Summon!")
        
        return "home" // Uses SummonViewResolver
    }
}

// Ktor route example
fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondSummon(HomeScreen())
        }
    }
}

// Quarkus resource example
@Path("/")
class HomeResource {
    @Inject
    lateinit var summonRenderer: SummonRenderer
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun home(): String {
        return summonRenderer.render(HomeScreen())
    }
}