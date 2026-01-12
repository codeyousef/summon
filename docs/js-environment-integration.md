# JavaScript Environment Integration

Summon provides a flexible lifecycle management system designed to work across different JavaScript environments. This guide explains how to use it with browsers, Node.js, and Web Workers.

## Overview

The JS lifecycle system consists of:

1. A common `LifecycleOwner` class that manages lifecycle states
2. A JS-specific implementation that automatically detects the environment
3. Hooks for browser, Node.js, and Web Worker environments
4. Environment-agnostic API for responding to lifecycle events

## Using the JS Lifecycle System

### Basic Usage

```kotlin
// Get the current lifecycle owner
val lifecycleOwner = currentLifecycleOwner()

// Create a lifecycle-aware component
val component = LifecycleAwareComponent(
    lifecycleOwner = lifecycleOwner,
    onResume = {
        // Start services, open connections
        println("Component is active")
    },
    onPause = {
        // Pause operations, reduce resource usage
        println("Component is paused")
    },
    onDestroy = {
        // Cleanup resources, close connections
        println("Component is destroyed")
    }
)

// Don't forget to dispose when no longer needed
component.dispose()
```

### Registering Custom Hooks

```kotlin
// Add custom startup hook
JsLifecycleOwner.addStartupHook {
    // Initialize your service
    println("My service is starting up")
}

// Add custom shutdown hook
JsLifecycleOwner.addShutdownHook {
    // Clean up your service
    println("My service is shutting down")
}

// Add custom visibility change hook (useful in browser environments)
JsLifecycleOwner.addVisibilityChangeHook { isVisible ->
    if (isVisible) {
        // App is now visible to the user
        println("Application became visible")
    } else {
        // App is hidden from the user
        println("Application is hidden") 
    }
}
```

## Supported JavaScript Environments

### Browser

In browser environments, the lifecycle is connected to document visibility and page lifecycle events:

- Document `visibilitychange` event: Controls transitions between `RESUMED` and `PAUSED` states
- Window `beforeunload` event: Triggers `STOPPED` state and shutdown hooks

```kotlin
// Example of browser-specific code
class MyBrowserComponent {
    init {
        // This will work in browser environments
        val lifecycleOwner = currentLifecycleOwner()
        
        // Respond to visibility changes
        JsLifecycleOwner.addVisibilityChangeHook { isVisible ->
            if (isVisible) {
                refreshData()
            } else {
                pauseBackgroundTasks()
            }
        }
    }
    
    private fun refreshData() {
        // Refresh data when tab becomes visible
    }
    
    private fun pauseBackgroundTasks() {
        // Pause tasks when tab is hidden
    }
}
```

### Node.js

In Node.js environments, the lifecycle connects to process events:

- Process `exit` event: Triggers `DESTROYED` state
- Process `SIGINT` and `SIGTERM` signals: Trigger `STOPPED` state and shutdown hooks

```kotlin
// Example of Node.js integration
class MyNodeService {
    init {
        // This will work in Node.js environments
        val lifecycleOwner = currentLifecycleOwner()
        
        // Use lifecycleCoroutineScope for background work
        lifecycleCoroutineScope(lifecycleOwner).launch {
            // This coroutine will be cancelled when the process exits
        }
        
        // Register cleanup on shutdown
        JsLifecycleOwner.addShutdownHook {
            closeConnections()
        }
    }
    
    private fun closeConnections() {
        // Close database connections, etc.
    }
}
```

### Web Workers

In Web Worker environments, the lifecycle can be controlled via custom messages:

```kotlin
// Example of Web Worker integration
class MyWorkerComponent {
    init {
        // This will work in Web Worker environments
        val lifecycleOwner = currentLifecycleOwner()
        
        // Web Workers can receive lifecycle events via messages
        // Example message to control lifecycle:
        // { type: 'lifecycle', action: 'pause' }
        // { type: 'lifecycle', action: 'resume' }
        // { type: 'lifecycle', action: 'terminate' }
    }
}
```

## Custom Environment Integration

You can add support for other JS environments by extending the `JsEnvironmentIntegrations` object:

```kotlin
// Add to JsEnvironmentIntegrations object
fun setupCustomEnvironmentIntegration() {
    try {
        // Check if your environment is available
        if (isCustomEnvironment()) {
            println("Custom environment detected")
            
            // Add lifecycle hooks
            JsLifecycleOwner.addStartupHook {
                println("Custom environment started")
            }
            
            JsLifecycleOwner.addShutdownHook {
                println("Custom environment shutting down")
            }
            
            // Set up custom event listeners
            setupCustomEventListeners()
        }
    } catch (e: Throwable) {
        // Error handling
    }
}

private fun isCustomEnvironment(): Boolean {
    return js("typeof customGlobal !== 'undefined'").unsafeCast<Boolean>()
}

private fun setupCustomEventListeners() {
    js("""
        // Your custom JS environment integration code
    """.trimIndent()).unsafeCast<Unit>()
}

// Call your integration setup
JsEnvironmentIntegrations.setupCustomEnvironmentIntegration()
```

## Lifecycle States in JS Context

For JavaScript applications, lifecycle states have these meanings:

- `CREATED`: The component has been created but not started
- `STARTED`: The component is starting but not yet fully operational
- `RESUMED`: The component is fully operational and visible (for browser environments)
- `PAUSED`: The component is still running but not visible (e.g., tab in background)
- `STOPPED`: The component is being shut down
- `DESTROYED`: The component has been destroyed and all resources released

## How the Automatic Detection Works

When your application starts:

1. The `JsLifecycleOwner` is initialized by the first call to `currentLifecycleOwner()`
2. The `JsEnvironmentIntegrations.setupAll()` method is called
3. Each environment integration attempts to detect its environment
4. When an environment is detected, appropriate hooks are registered
5. Environment-specific events are connected to lifecycle state changes

This allows your code to respond to lifecycle events regardless of which JavaScript environment you're using.

## Production Build Considerations

When building Kotlin/JS for production using `jsBrowserProductionWebpack`, the minification process can mangle function
names. Summon components are designed to work correctly in minified builds, but keep these guidelines in mind:

### @JsName Annotations

Key functions and components are annotated with `@JsName` to preserve consistent naming:

```kotlin
// These functions maintain stable names in minified builds
getPlatformRenderer()    // @JsName("getPlatformRenderer")
setPlatformRenderer()    // @JsName("setPlatformRenderer")
clearPlatformRenderer()  // @JsName("clearPlatformRenderer")
```

### Callback Handling Best Practices

When creating callbacks that modify state, use named local functions instead of inline lambdas:

```kotlin
// Recommended approach
@Composable
fun MyComponent() {
    val state = remember { mutableStateOf("") }

    // Named function avoids potential minification issues
    fun handleChange(newValue: String) {
        state.value = newValue
    }

    BasicTextField(
        value = state.value,
        onValueChange = ::handleChange
    )
}
```

### BasicTextField for Simple Cases

For simple text inputs without validation, use `BasicTextField` which has no internal state:

```kotlin
BasicTextField(
    value = text,
    onValueChange = { text = it },
    placeholder = "Enter text"
)
```

### Testing Production Builds

Always test your application with the production build before deploying:

```bash
# Build production bundle
./gradlew :app:jsBrowserProductionWebpack

# Serve and test the production output
```

The e2e test suite includes specific tests for minified builds in `minified-build.spec.ts`.
 
