# Effects API Reference

This document provides detailed information about the effects APIs in the Summon library.

## Table of Contents

- [Effect System Overview](#effect-system-overview)
- [Core Effects API](#core-effects-api)
- [Side Effect Management](#side-effect-management)
- [Common Effects](#common-effects)
- [Effect Composition](#effect-composition)
- [Platform-Specific Effects](#platform-specific-effects)

---

## Effect System Overview

The Summon effects system allows you to manage side effects and lifecycle operations within your composable components. Effects are executed after composition and are useful for operations like initialization, cleanup, and integrating with non-composable code.

### Design Principles

1. **Lifecycle Management**: Effects have clear lifecycle boundaries with initialization and cleanup phases
2. **Conditional Execution**: Effects can be conditionally executed based on dependencies
3. **Separation of Concerns**: Side effects are kept separate from the rendering logic
4. **Platform Abstraction**: Effects work consistently across all platforms

---

## Core Effects API

The base APIs for defining and managing effects.

### Function Definitions

```kotlin
package code.yousef.summon.effects

// Execute an effect after each successful composition
@Composable
fun CompositionScope.effect(
    effect: () -> Unit
)

// Execute an effect when composition is first created
@Composable
fun CompositionScope.onMount(
    effect: () -> Unit
)

// Execute an effect when composition is disposed
@Composable
fun CompositionScope.onDispose(
    effect: () -> Unit
)

// Execute an effect after composition when dependencies change
@Composable
fun CompositionScope.effectWithDeps(
    vararg dependencies: Any?,
    effect: () -> Unit
)

// Execute an effect once after composition
@Composable
fun CompositionScope.onMountWithCleanup(
    effect: () -> (() -> Unit)?
)

// Execute an effect with dependencies and cleanup
@Composable
fun CompositionScope.effectWithDepsAndCleanup(
    vararg dependencies: Any?,
    effect: () -> (() -> Unit)?
)
```

### Description

These functions provide the core effect system in Summon, allowing you to perform operations at specific points in the component lifecycle.

### Example

```kotlin
@Composable
fun UserProfile(userId: String) {
    // State for user data
    val userData = remember { mutableStateOf<UserData?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val error = remember { mutableStateOf<String?>(null) }
    
    // Basic effect: runs after every composition
    effect {
        console.log("UserProfile recomposed")
    }
    
    // Mount effect: runs once when component is first rendered
    onMount {
        console.log("UserProfile mounted")
    }
    
    // Cleanup effect: runs when component is removed
    onDispose {
        console.log("UserProfile disposed")
    }
    
    // Effect with dependencies: runs when userId changes
    effectWithDeps(userId) {
        isLoading.value = true
        error.value = null
        
        fetchUserData(userId)
            .then { user ->
                userData.value = user
                isLoading.value = false
            }
            .catch { err ->
                error.value = err.message
                isLoading.value = false
            }
    }
    
    // Effect with cleanup: sets up and tears down a subscription
    onMountWithCleanup {
        // Setup: subscribe to user status updates
        val subscription = userStatusService.subscribe(userId) { status ->
            // Update UI when status changes
            userData.value = userData.value?.copy(status = status)
        }
        
        // Return cleanup function
        return@onMountWithCleanup {
            // Cleanup: unsubscribe when component is removed
            subscription.unsubscribe()
        }
    }
    
    // Render UI based on state
    when {
        isLoading.value -> LoadingSpinner()
        error.value != null -> ErrorDisplay(message = error.value!!)
        userData.value != null -> UserInfo(user = userData.value!!)
        else -> Text("No user data")
    }
}
```

---

## Side Effect Management

APIs for managing asynchronous side effects safely within the component lifecycle.

### Function Definitions

```kotlin
package code.yousef.summon.effects

// Launch a coroutine scoped to the composition
fun CompositionScope.launchEffect(
    block: suspend CoroutineScope.() -> Unit
): Job

// Launch a coroutine with dependencies
fun CompositionScope.launchEffectWithDeps(
    vararg dependencies: Any?,
    block: suspend CoroutineScope.() -> Unit
): Job

// Execute an async effect with cleanup
fun CompositionScope.asyncEffect(
    effect: () -> Promise<() -> Unit>
)

// Execute an async effect when dependencies change
fun CompositionScope.asyncEffectWithDeps(
    vararg dependencies: Any?,
    effect: () -> Promise<() -> Unit>
)

// Safe state updates with cancellation handling
fun <T> CompositionScope.updateStateAsync(
    state: MutableState<T>,
    block: suspend () -> T
): Job
```

### Description

These functions help manage asynchronous operations within the component lifecycle, ensuring they're properly cleaned up when the component is disposed.

### Example

```kotlin
class NewsComponent : Composable {
    private val articles = remember { mutableStateOf<List<Article>>(emptyList()) }
    private val isRefreshing = remember { mutableStateOf(false) }
    
    override fun render() {
        // Launch a coroutine scoped to the component
        launchEffect {
            // Initial data load
            try {
                val data = newsService.getLatestArticles()
                articles.value = data
            } catch (e: Exception) {
                console.error("Failed to load articles: ${e.message}")
            }
        }
        
        // Launch a periodic refresh with cleanup
        launchEffectWithDeps(articles.value.size) {
            // Only start auto-refresh if we have articles
            if (articles.value.isNotEmpty()) {
                while (isActive) {
                    delay(60_000) // Refresh every minute
                    isRefreshing.value = true
                    try {
                        val freshData = newsService.getLatestArticles()
                        articles.value = freshData
                    } catch (e: Exception) {
                        console.error("Refresh failed: ${e.message}")
                    } finally {
                        isRefreshing.value = false
                    }
                }
            }
        }
        
        // Alternative with Promise API
        asyncEffect {
            return@asyncEffect newsService.subscribeToUpdates()
                .then { subscription ->
                    // Return cleanup function
                    return@then {
                        subscription.cancel()
                    }
                }
        }
        
        // Safe state updates with cancellation
        Button(
            text = "Refresh",
            onClick = {
                updateStateAsync(articles) {
                    newsService.getLatestArticles()
                }
            }
        )
        
        // Render articles
        Column {
            if (isRefreshing.value) {
                Text("Refreshing...")
            }
            
            for (article in articles.value) {
                ArticleCard(article)
            }
        }
    }
}
```

---

## Common Effects

Pre-built effects for common scenarios.

### Function Definitions

```kotlin
package code.yousef.summon.effects

// Effect for document title
fun CompositionScope.useDocumentTitle(title: String)

// Effect for handling keyboard shortcuts
fun CompositionScope.useKeyboardShortcut(
    key: String,
    modifiers: Set<KeyModifier> = emptySet(),
    handler: (KeyboardEvent) -> Unit
)

// Effect for interval timer
fun CompositionScope.useInterval(
    delayMs: Int,
    callback: () -> Unit
): IntervalControl

// Effect for timeout
fun CompositionScope.useTimeout(
    delayMs: Int,
    callback: () -> Unit
): TimeoutControl

// Effect for handling clicks outside a component
fun CompositionScope.useClickOutside(
    elementRef: ElementRef,
    handler: (MouseEvent) -> Unit
)

// Effect for window size
fun CompositionScope.useWindowSize(): WindowSize

// Effect for browser location/URL
fun CompositionScope.useLocation(): Location

// Effect for local storage
fun <T> CompositionScope.useLocalStorage(
    key: String,
    initialValue: T,
    serializer: (T) -> String = { it.toString() },
    deserializer: (String) -> T
): MutableState<T>

// Effect for media queries
fun CompositionScope.useMediaQuery(
    query: String
): MutableState<Boolean>
```

### Description

These pre-built effects handle common UI and browser interactions, abstracting away the complexity of managing their lifecycle.

### Example

```kotlin
class ProfilePageComponent : Composable {
    private val isMenuOpen = remember { mutableStateOf(false) }
    private val menuRef = remember { ElementRef() }
    private val userName = remember { mutableStateOf("") }
    
    override fun render() {
        // Update document title
        useDocumentTitle("User Profile - ${userName.value}")
        
        // Handle Escape key to close menu
        useKeyboardShortcut(
            key = "Escape",
            handler = {
                if (isMenuOpen.value) {
                    isMenuOpen.value = false
                }
            }
        )
        
        // Close menu when clicking outside
        useClickOutside(menuRef) { 
            isMenuOpen.value = false 
        }
        
        // Store user preferences in localStorage
        val theme = useLocalStorage(
            key = "user-theme",
            initialValue = "light",
            deserializer = { it }
        )
        
        // Respond to dark mode preference
        val prefersDarkMode = useMediaQuery("(prefers-color-scheme: dark)")
        
        // Interval for session check
        val sessionChecker = useInterval(60_000) {
            checkUserSession()
        }
        
        // Get window size for responsive design
        val windowSize = useWindowSize()
        val isMobile = windowSize.width < 768
        
        // Auto-save form with timeout
        val autoSave = useTimeout(3000) {
            saveUserProfile()
        }
        
        // Restart timeout on input
        TextField(
            value = userName.value,
            onValueChange = { 
                userName.value = it
                autoSave.reset() // Restart the timeout
            }
        )
        
        // Menu with click outside
        if (isMenuOpen.value) {
            Box(
                modifier = Modifier.ref(menuRef)
            ) {
                // Menu content
                Column {
                    Button(
                        text = "Toggle Theme",
                        onClick = {
                            theme.value = if (theme.value == "light") "dark" else "light"
                        }
                    )
                    // Other menu items
                }
            }
        }
        
        // Responsive layout based on window size
        if (isMobile) {
            MobileLayout()
        } else {
            DesktopLayout()
        }
    }
    
    private fun checkUserSession() {
        // Check if user session is still valid
    }
    
    private fun saveUserProfile() {
        // Save profile data
    }
}

// Control interfaces
interface IntervalControl {
    fun pause()
    fun resume()
    fun reset()
    fun setDelay(delayMs: Int)
}

interface TimeoutControl {
    fun cancel()
    fun reset()
    fun setDelay(delayMs: Int)
}

data class WindowSize(
    val width: Int,
    val height: Int
)
```

---

## Effect Composition

APIs for composing and combining effects.

### Function Definitions

```kotlin
package code.yousef.summon.effects

// Create a custom composable effect
fun <T> createEffect(
    setup: CompositionScope.() -> T,
    callback: (T) -> (() -> Unit)?
): (CompositionScope.() -> T)

// Combine multiple effects into one
fun combineEffects(
    vararg effects: CompositionScope.() -> Unit
): CompositionScope.() -> Unit

// Create a conditional effect that only runs when condition is true
fun conditionalEffect(
    condition: () -> Boolean,
    effect: CompositionScope.() -> Unit
): CompositionScope.() -> Unit

// Create a debounced effect
fun <T> debouncedEffect(
    delayMs: Int,
    producer: () -> T,
    effect: (T) -> Unit
): CompositionScope.() -> Unit

// Create a throttled effect
fun <T> throttledEffect(
    delayMs: Int,
    producer: () -> T,
    effect: (T) -> Unit
): CompositionScope.() -> Unit
```

### Description

These functions allow you to create reusable, composed effects that encapsulate complex behaviors.

### Example

```kotlin
// Custom form validation effect
fun validateFormEffect(
    emailState: MutableState<String>,
    passwordState: MutableState<String>,
    errorsState: MutableState<Map<String, String>>
): CompositionScope.() -> Unit = createEffect(
    setup = {
        // Return the current values to watch
        Triple(emailState.value, passwordState.value, errorsState)
    },
    callback = { (email, password, errorsState) ->
        // Perform validation
        val errors = mutableMapOf<String, String>()
        
        if (!isValidEmail(email)) {
            errors["email"] = "Invalid email format"
        }
        
        if (password.length < 8) {
            errors["password"] = "Password must be at least 8 characters"
        }
        
        // Update errors state
        errorsState.value = errors
        
        // No cleanup needed
        null
    }
)

// Debounced search effect
fun searchEffect(
    query: MutableState<String>,
    results: MutableState<List<SearchResult>>
): CompositionScope.() -> Unit = debouncedEffect(
    delayMs = 300,
    producer = { query.value },
    effect = { searchQuery ->
        if (searchQuery.isNotEmpty()) {
            performSearch(searchQuery)
                .then { searchResults ->
                    results.value = searchResults
                }
        } else {
            results.value = emptyList()
        }
    }
)

// Usage in a component
class SearchComponent : Composable {
    private val query = remember { mutableStateOf("") }
    private val results = remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    private val isLoading = remember { mutableStateOf(false) }
    
    override fun render() {
        // Use debounced search effect
        searchEffect(query, results)()
        
        // Loading indicator effect
        effectWithDeps(query.value) {
            if (query.value.isNotEmpty()) {
                isLoading.value = true
                
                // Set loading to false after search completes
                // (assuming search takes less than 500ms)
                setTimeout(500) {
                    isLoading.value = false
                }
            }
        }
        
        // Combine multiple effects
        val combinedEffect = combineEffects(
            // Log searches
            {
                effectWithDeps(query.value) {
                    if (query.value.isNotEmpty()) {
                        logSearchQuery(query.value)
                    }
                }
            },
            // Update recent searches
            {
                effectWithDeps(results.value) {
                    if (results.value.isNotEmpty() && query.value.isNotEmpty()) {
                        updateRecentSearches(query.value)
                    }
                }
            }
        )
        
        // Apply combined effect
        combinedEffect()
        
        // UI rendering
        Column {
            TextField(
                value = query.value,
                onValueChange = { query.value = it },
                placeholder = "Search..."
            )
            
            if (isLoading.value) {
                LoadingIndicator()
            } else {
                SearchResults(results = results.value)
            }
        }
    }
}
```

---

## Platform-Specific Effects

Effects that utilize platform-specific APIs.

### JavaScript Platform

```kotlin
package code.yousef.summon.effects.js

// Effect for browser history
fun CompositionScope.useHistory(): History

// Effect for browser navigator
fun CompositionScope.useNavigator(): Navigator

// Effect for IntersectionObserver
fun CompositionScope.useIntersectionObserver(
    elementRef: ElementRef,
    options: IntersectionObserverOptions = IntersectionObserverOptions()
): IntersectionState

// Effect for ResizeObserver
fun CompositionScope.useResizeObserver(
    elementRef: ElementRef,
    callback: (ResizeObserverEntry) -> Unit
): ResizeObserverCleanup

// Effect for online/offline status
fun CompositionScope.useOnlineStatus(): MutableState<Boolean>

// Effect for clipboard API
fun CompositionScope.useClipboard(): ClipboardAPI

// Effect for geolocation
fun CompositionScope.useGeolocation(
    options: GeolocationOptions = GeolocationOptions()
): GeolocationState

// Web animation API
fun CompositionScope.useWebAnimation(
    elementRef: ElementRef,
    keyframes: Array<Keyframe>,
    options: AnimationOptions
): WebAnimationAPI
```

### JVM Platform

```kotlin
package code.yousef.summon.effects.jvm

// Effect for file system watcher
fun CompositionScope.useFileWatcher(
    path: String,
    callback: (FileEvent) -> Unit
): FileWatcherControl

// Effect for system tray
fun CompositionScope.useSystemTray(
    icon: Image,
    tooltip: String
): SystemTrayControl

// Effect for clipboard API
fun CompositionScope.useClipboard(): ClipboardAPI

// Effect for screen information
fun CompositionScope.useScreenInfo(): ScreenInfo
```

### Description

Platform-specific effects provide access to capabilities unique to each platform while maintaining a consistent API pattern.

### Example

```kotlin
// JavaScript example
class LazyLoadComponent : Composable {
    private val elementRef = remember { ElementRef() }
    private val isVisible = remember { mutableStateOf(false) }
    private val imageUrl = remember { mutableStateOf<String?>(null) }
    
    override fun render() {
        // Track element visibility using IntersectionObserver
        runJsOnly {
            val intersection = useIntersectionObserver(
                elementRef = elementRef,
                options = IntersectionObserverOptions(
                    threshold = 0.1f,
                    rootMargin = "20px"
                )
            )
            
            // Update visibility state
            isVisible.value = intersection.isIntersecting
            
            // Load image when element becomes visible
            effectWithDeps(isVisible.value) {
                if (isVisible.value && imageUrl.value == null) {
                    imageUrl.value = "https://example.com/image.jpg"
                }
            }
            
            // Clipboard API
            val clipboard = useClipboard()
            
            Button(
                text = "Copy URL",
                onClick = {
                    imageUrl.value?.let { clipboard.writeText(it) }
                }
            )
            
            // Online status
            val isOnline = useOnlineStatus()
            
            if (!isOnline.value) {
                Text("You are offline. Some features may not work.")
            }
        }
        
        // Element to observe
        Box(
            modifier = Modifier
                .height(300.dp)
                .width(100.pct)
                .ref(elementRef)
        ) {
            if (imageUrl.value != null) {
                Image(src = imageUrl.value!!)
            } else {
                LoadingPlaceholder()
            }
        }
    }
}

// JVM example
class DesktopComponent : Composable {
    private val notificationCount = remember { mutableStateOf(0) }
    
    override fun render() {
        // Only run on JVM platform
        runJvmOnly {
            // System tray integration
            val systemTray = useSystemTray(
                icon = loadImage("app_icon.png"),
                tooltip = "My Desktop App"
            )
            
            // Update tray icon when notification count changes
            effectWithDeps(notificationCount.value) {
                if (notificationCount.value > 0) {
                    systemTray.displayNotification(
                        title = "New Notifications",
                        message = "You have ${notificationCount.value} new notifications"
                    )
                    
                    systemTray.setIcon(loadImage("app_icon_notification.png"))
                } else {
                    systemTray.setIcon(loadImage("app_icon.png"))
                }
            }
            
            // Watch config file for changes
            val fileWatcher = useFileWatcher("config.json") { event ->
                when (event.type) {
                    FileEventType.MODIFIED -> reloadConfig()
                    FileEventType.DELETED -> createDefaultConfig()
                    else -> { /* ignore */ }
                }
            }
            
            // Screen information for responsive layouts
            val screenInfo = useScreenInfo()
            
            if (screenInfo.dpi > 200) {
                HighResolutionLayout()
            } else {
                StandardLayout()
            }
        }
        
        // Regular UI rendering
        Column {
            Text("Notification Count: ${notificationCount.value}")
            Button(
                text = "Add Notification",
                onClick = { notificationCount.value++ }
            )
        }
    }
    
    private fun reloadConfig() {
        // Reload application configuration
    }
    
    private fun createDefaultConfig() {
        // Create default configuration file
    }
}
```

---

The Summon effects system allows you to manage side effects in a platform-independent way, while still providing access to platform-specific capabilities when needed. Effects make it easy to integrate with external systems, manage component lifecycle, and keep your UI code clean and focused on presentation. 