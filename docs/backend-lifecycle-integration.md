# Backend Lifecycle Integration

Summon provides a streamlined lifecycle management system specifically designed for backend JVM applications. This guide explains how to integrate it with popular backend frameworks.

## Overview

The backend lifecycle system consists of:

1. A common `LifecycleOwner` class that manages lifecycle states
2. A JVM-specific implementation with startup and shutdown hooks 
3. Automatic integration with popular backend frameworks
4. Lifecycle-aware components that respond to lifecycle events

## Using the Backend Lifecycle System

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
JvmLifecycleOwner.addStartupHook {
    // Initialize your service
    println("My service is starting up")
}

// Add custom shutdown hook
JvmLifecycleOwner.addShutdownHook {
    // Clean up your service
    println("My service is shutting down")
}
```

## Integrated Backend Frameworks

The JVM implementation automatically detects and integrates with popular backend frameworks:

### Spring Boot

```kotlin
// Example of Spring Boot integration
@Component
class MySpringComponent {
    @PostConstruct
    fun init() {
        // Get lifecycle owner
        val lifecycleOwner = currentLifecycleOwner()
        
        // Register with lifecycle
        lifecycleCoroutineScope(lifecycleOwner).launch {
            // This coroutine will be cancelled when the application shuts down
        }
    }
}
```

### Ktor

```kotlin
// Example of Ktor integration
fun Application.module() {
    // Get lifecycle owner
    val lifecycleOwner = currentLifecycleOwner()
    
    // Register shutdown hook for cleanup
    environment.monitor.subscribe(ApplicationStopping) {
        // This will be called when Ktor is stopping
        println("Ktor application shutting down")
    }
    
    // Start background work that respects lifecycle
    whenActive(lifecycleOwner) {
        // This block is only executed when the lifecycle is active
        // and is automatically cancelled when it becomes inactive
    }
}
```

### Quarkus

```kotlin
// Example of Quarkus integration
@ApplicationScoped
class MyQuarkusService {
    @PostConstruct
    fun init() {
        // Get lifecycle owner
        val lifecycleOwner = currentLifecycleOwner()
        
        // Setup lifecycle-aware component
        val component = LifecycleAwareComponent(lifecycleOwner)
    }
}
```

### Micronaut

```kotlin
// Example of Micronaut integration
@Singleton
class MyMicronautService {
    @PostConstruct
    fun init() {
        // Get lifecycle owner
        val lifecycleOwner = currentLifecycleOwner() 
        
        // Use lifecycleCoroutineScope for background work
        lifecycleCoroutineScope(lifecycleOwner).launch {
            // This coroutine is tied to the application lifecycle
        }
    }
}
```

## Custom Backend Framework Integration

You can create custom integrations for other backend frameworks:

```kotlin
// Add to BackendIntegrations object
fun setupCustomFrameworkIntegration() {
    try {
        // Check if your framework is in classpath
        Class.forName("com.example.framework.MainClass")
        
        // Add lifecycle hooks
        JvmLifecycleOwner.addStartupHook {
            println("Custom framework started")
        }
        
        JvmLifecycleOwner.addShutdownHook {
            println("Custom framework shutting down")
        }
        
        // Add deeper integration specific to your framework
    } catch (e: ClassNotFoundException) {
        // Framework not in classpath, skip integration
    }
}

// Call your integration setup
BackendIntegrations.setupCustomFrameworkIntegration()
```

## Lifecycle States in Backend Context

For backend applications, lifecycle states have these meanings:

- `CREATED`: The component has been created but not started
- `STARTED`: The component is starting but not yet fully operational
- `RESUMED`: The component is fully operational and handling requests
- `PAUSED`: The component is still running but in a reduced capacity (e.g., during maintenance)
- `STOPPED`: The component has stopped handling requests but hasn't released resources
- `DESTROYED`: The component has been destroyed and all resources released

## How the Automatic Detection Works

When your application starts:

1. The `JvmLifecycleOwner` is initialized by the first call to `currentLifecycleOwner()`
2. The `BackendIntegrations.setupAll()` method is called
3. Each framework integration is attempted by checking for framework-specific classes
4. When a framework is detected, appropriate hooks are registered
5. Runtime shutdown hooks ensure cleanup when the application terminates

This allows your code to respond to lifecycle events regardless of which backend framework you're using, with no manual configuration required. 