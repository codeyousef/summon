# Framework-Agnostic Lifecycle Integration

Summon provides a lifecycle management system that works across different UI frameworks. This guide explains how to use and extend this system.

## Overview

The lifecycle system consists of:

1. A common `LifecycleOwner` class that manages lifecycle states
2. Platform-specific implementations for JVM and JS
3. A framework-agnostic plugin system for the JVM platform
4. Lifecycle-aware components that respond to lifecycle events

## Using the Lifecycle System

### Basic Usage

```kotlin
// Get the current lifecycle owner
val lifecycleOwner = currentLifecycleOwner()

// Create a lifecycle-aware component
val component = lifecycleAware(lifecycleOwner) {
    onResume {
        println("Component is resumed")
    }
    
    onPause {
        println("Component is paused")
    }
    
    onDestroy {
        println("Component is destroyed")
    }
}

// Don't forget to dispose when no longer needed
component.dispose()
```

### Using with Coroutines

```kotlin
// Get a coroutine scope tied to the lifecycle
val scope = lifecycleCoroutineScope()

// Launch a coroutine that's cancelled when the lifecycle owner is destroyed
scope.launch {
    // Long-running operation
}

// Or use the whenActive helper
whenActive(key = "uniqueKey") {
    // This block is only executed when the lifecycle is in STARTED or RESUMED state
    // and is automatically cancelled when it becomes inactive
}
```

## JVM Framework Integration

The JVM implementation automatically detects the UI framework you're using and sets up the appropriate lifecycle integration.

### Supported Frameworks

- JavaFX
- Swing
- Compose for Desktop
- Headless mode (for non-UI applications)

### Custom Framework Integration

You can create and register your own framework integration:

```kotlin
// Create a custom integration
class MyFrameworkIntegration : LifecycleIntegration {
    override fun setup(lifecycleOwner: JvmLifecycleOwner) {
        // Connect lifecycle events to your framework
        
        // Example:
        myFramework.onShow {
            lifecycleOwner.simulateStart()
            lifecycleOwner.simulateResume()
        }
        
        myFramework.onHide {
            lifecycleOwner.simulatePause()
        }
        
        myFramework.onClose {
            lifecycleOwner.simulateStop()
            lifecycleOwner.simulateDestroy()
        }
    }
}

// Register your integration
JvmLifecycleOwner.registerIntegration(MyFrameworkIntegration())
```

## Manual Control

In some cases, you may want to manually control the lifecycle states:

```kotlin
// Get the JVM lifecycle owner
val jvmLifecycleOwner = JvmLifecycleOwner.instance

// Manually trigger lifecycle events
jvmLifecycleOwner.simulateStart()
jvmLifecycleOwner.simulateResume()
jvmLifecycleOwner.simulatePause()
jvmLifecycleOwner.simulateStop()
jvmLifecycleOwner.simulateDestroy()
```

## Lifecycle States

The system supports the following lifecycle states:

- `CREATED`: The component has been created but is not visible
- `STARTED`: The component is starting but not yet interactive
- `RESUMED`: The component is visible and interactive
- `PAUSED`: The component is visible but not interactive
- `STOPPED`: The component is not visible
- `DESTROYED`: The component has been destroyed

## How the Automatic Detection Works

When your application starts:

1. The `JvmLifecycleFactory` checks which UI frameworks are available in the classpath
2. It registers the appropriate integration based on what it finds
3. If multiple frameworks are available, it follows a priority order: JavaFX > Swing > Compose > Headless
4. The integration connects the lifecycle events to the appropriate framework events

This allows your code to respond to lifecycle events without being directly tied to any specific UI framework. 