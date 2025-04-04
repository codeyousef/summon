package code.yousef.summon.examples

import code.yousef.summon.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Example demonstrating how to use the environment-agnostic lifecycle system
 */
class LifecycleAwarenessExample {
    /**
     * Demonstrates the basic usage of lifecycle-aware components
     */
    fun basicUsageExample() {
        println("=== Basic Lifecycle Usage Example ===")
        
        // Get the lifecycle owner
        val lifecycleOwner = LifecycleOwner()
        
        // Create a lifecycle-aware component
        val component = LifecycleAwareComponent(
            lifecycleOwner = lifecycleOwner,
            onResume = {
                println("Component is now active and visible!")
            },
            onPause = {
                println("Component is now paused (hidden but still in memory)")
            },
            onDestroy = {
                println("Component is being destroyed, cleaning up resources")
            }
        )
        
        // In real usage, you would return the component here
        // Then when no longer needed:
        component.dispose()
    }
    
    /**
     * Demonstrates using coroutines with lifecycle
     */
    fun coroutineExample() {
        println("=== Lifecycle with Coroutines Example ===")
        
        // Get the lifecycle owner
        val lifecycleOwner = LifecycleOwner()
        
        // Get a coroutine scope tied to the lifecycle
        val scope = lifecycleCoroutineScope(lifecycleOwner)
        
        // Launch a coroutine that will be cancelled if the lifecycle owner is destroyed
        scope.launch {
            println("Starting long-running task...")
            repeat(100) { iteration ->
                println("Task iteration $iteration")
                delay(1000)
            }
            println("Task completed")
        }
        
        // When using whenActive, the block is only executed when the lifecycle is
        // in STARTED or RESUMED state, and automatically cancelled when inactive
        whenActive(lifecycleOwner, "example-task") {
            println("Starting active-only task...")
            repeat(100) { iteration ->
                println("Active task iteration $iteration")
                delay(1000)
            }
            println("Active task completed")
        }
    }
    
    /**
     * Demonstrates custom lifecycle hooks 
     * (these APIs depend on the platform - JVM or JS)
     */
    fun customLifecycleHooksExample() {
        println("=== Custom Lifecycle Hooks Example ===")
        
        // Platform-specific code would go here
        println("This example requires platform-specific code (JVM or JS)")
        println("See the documentation for platform-specific lifecycle hooks")
    }
} 