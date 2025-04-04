package code.yousef.summon.examples

import code.yousef.summon.*
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.core.Composable
import kotlinx.coroutines.delay

/**
 * Example demonstrating various effects in Summon
 */
class EffectsExample {
    // State to track the current count
    private val count = mutableStateOf(0)

    // State to track whether data is loading
    private val isLoading = mutableStateOf(true)

    // Derived state that computes a label based on the count value
    private val countLabel = derivedStateOf(count) {
        "Current count: ${count.value}"
    }

    /**
     * Main example function demonstrating different effects
     */
    fun effectsDemo() {
        println("=== Effects Example ===")

        // Use SideEffect to synchronize with non-Summon code
        // This runs on every composition
        sideEffect {
            println("Rendering with count: ${count.value}")
        }

        // LaunchedEffect to perform async operations when keys change
        // This simulates loading data when the component is first created
        val dataFetchingEffect = LaunchedEffect(
            key = "fetchData",
            block = {
                println("Fetching data...")
                isLoading.value = true

                // Simulate network delay
                delay(1000)

                // Update state with "fetched" data
                count.value = 42
                isLoading.value = false
                println("Data fetched!")
            }
        )

        // DisposableEffect to set up and clean up resources
        // This demonstrates setting up event listeners and cleaning them up
        val eventListenerEffect = DisposableEffect(
            key = "eventListener",
            effect = {
                println("Setting up event listener")

                // In a real app, this would be registering DOM event listeners
                val cleanup = {
                    println("Cleaning up event listener")
                }

                // Return the cleanup function
                DisposableEffectResult(cleanup)
            }
        )

        // LifecycleAware component to respond to lifecycle events
        val lifecycleOwner = LifecycleOwner()

        // Setup lifecycle callbacks
        val builder = LifecycleAwareComponentBuilder().apply {
            onResumeCallback = {
                println("Page is now active and visible!")
            }

            onPauseCallback = {
                println("Page is paused (hidden but still in memory)")
            }

            onDestroyCallback = {
                println("Page is being destroyed, cleaning up resources")
            }
        }

        val lifecycleComponent = LifecycleAwareComponent(
            lifecycleOwner = lifecycleOwner,
            onResume = builder.onResumeCallback,
            onPause = builder.onPauseCallback,
            onDestroy = builder.onDestroyCallback
        )

        // Simulate lifecycle events
        println("\n=== Simulating Lifecycle Events ===")
        lifecycleOwner.onResume()  // This will trigger the onResume callback
        lifecycleOwner.onPause()   // This will trigger the onPause callback

        // Clean up effects when done
        println("\n=== Cleaning Up Effects ===")
        dataFetchingEffect.dispose()
        eventListenerEffect.dispose()
        lifecycleComponent.dispose()

        // Print the final state
        println("\n=== Final State ===")
        println("Loading: ${isLoading.value}")
        println("Count: ${count.value}")
        println("Label: ${countLabel.value}")
    }
}

/**
 * Simple mock UI components for the example
 */
class Div(val content: () -> Unit) : Composable {
    override fun <T> compose(receiver: T): T {
        // In a real implementation, this would create HTML div elements
        return receiver
    }
}

class H1(val content: () -> Unit) : Composable {
    override fun <T> compose(receiver: T): T {
        // In a real implementation, this would create HTML h1 elements
        return receiver
    }
}

class Text(val content: String) : Composable {
    override fun <T> compose(receiver: T): T {
        // In a real implementation, this would create text nodes
        return receiver
    }
}

class MockButton(val onClick: () -> Unit, val text: String) : Composable {
    override fun <T> compose(receiver: T): T {
        // In a real implementation, this would create button elements
        return receiver
    }
}

// Helper functions to create components
fun DivComponent(block: () -> Unit) = Div(block)
fun H1Component(block: () -> Unit) = H1(block)
fun TextComponent(content: String) = Text(content)
fun ButtonComponent(onClick: () -> Unit, text: String) = MockButton(onClick, text)

/**
 * Example showing how to use LaunchedEffect to perform side effects during composition.
 */
class LaunchedEffectExample : Composable {
    private val counter = mutableStateOf(0)

    override fun <T> compose(receiver: T): T {
        // Using LaunchedEffect to run a background task when counter changes
        launchedEffect(counter.value) {
            println("Counter changed to: ${counter.value}")

            // For example, send analytics event or fetch data based on counter value
        }

        // Using LaunchedEffect for a one-time initialization
        launchedEffect("init") {
            println("Component initialized")

            // For example, fetch initial data
            delay(1000)
            counter.value = 1
        }

        // Regular component UI
        Column(
            content = listOf(
                TextComponent("Current count: ${counter.value}"),
                ButtonComponent(
                    onClick = { counter.value++ },
                    text = "Increment"
                )
            )
        ).compose(receiver)

        return receiver
    }
}

/**
 * Example showing how to use DisposableEffect to clean up resources.
 */
class DisposableEffectExample : Composable {
    private val isActive = mutableStateOf(true)

    override fun <T> compose(receiver: T): T {
        // Using DisposableEffect to register and clean up event listeners
        if (isActive.value) {
            disposableEffect("eventListener") {
                println("Setting up event listener")

                // For example, register a listener or acquire a resource
                val listener = object {
                    fun handleEvent() {
                        println("Event handled")
                    }
                }

                // Return cleanup function
                onDispose {
                    println("Cleaning up event listener")
                    // For example, remove the listener or release the resource
                }
            }.compose(receiver)
        }

        // Regular component UI
        Column(
            content = listOf(
                TextComponent("Component is ${if (isActive.value) "active" else "inactive"}"),
                ButtonComponent(
                    onClick = { isActive.value = !isActive.value },
                    text = if (isActive.value) "Deactivate" else "Activate"
                )
            )
        ).compose(receiver)

        return receiver
    }
}

/**
 * Example showing how to use SideEffect for non-cancellable effects.
 */
class SideEffectExample : Composable {
    private val title = mutableStateOf("Hello World")

    override fun <T> compose(receiver: T): T {
        // Using SideEffect to synchronize the document title with our state
        sideEffect {
            // This would typically be platform-specific code
            println("Setting document title to: ${title.value}")
        }.compose(receiver)

        // Regular component UI
        Column(
            content = listOf(
                TextComponent("Current title: ${title.value}"),
                TextField(
                    state = title,
                    onValueChange = { title.value = it },
                    placeholder = "Enter new title"
                )
            )
        ).compose(receiver)

        return receiver
    }
}

/**
 * Example showing how to use DerivedState for computed values.
 */
class DerivedStateExample : Composable {
    private val firstName = mutableStateOf("John")
    private val lastName = mutableStateOf("Doe")

    override fun <T> compose(receiver: T): T {
        // Using DerivedState to compute a full name from first and last name
        val fullName = derivedStateOf(firstName, lastName) {
            "${firstName.value} ${lastName.value}"
        }

        // Regular component UI
        Column(
            content = listOf(
                TextComponent("Full name: ${fullName.value}"),
                TextField(
                    state = firstName,
                    onValueChange = { firstName.value = it },
                    placeholder = "First name"
                ),
                TextField(
                    state = lastName,
                    onValueChange = { lastName.value = it },
                    placeholder = "Last name"
                )
            )
        ).compose(receiver)

        return receiver
    }
}

/**
 * Example showing how to use LifecycleAwareComponent to respond to lifecycle events.
 */
class LifecycleAwareExample : Composable {
    private val status = mutableStateOf("Initializing")

    override fun <T> compose(receiver: T): T {
        // Using LifecycleAwareComponent to respond to lifecycle events
        val lifecycleOwner = LifecycleOwner()

        val builder = LifecycleAwareComponentBuilder().apply {
            onResumeCallback = {
                status.value = "Resumed"
                println("Component resumed")
            }

            onPauseCallback = {
                status.value = "Paused"
                println("Component paused")
            }

            onDestroyCallback = {
                status.value = "Destroyed"
                println("Component destroyed")
            }
        }

        // Create the lifecycle-aware component
        val lifecycleComponent = LifecycleAwareComponent(
            lifecycleOwner = lifecycleOwner,
            onResume = builder.onResumeCallback,
            onPause = builder.onPauseCallback,
            onDestroy = builder.onDestroyCallback
        )

        // Show status in the UI
        Column(
            content = listOf(
                TextComponent("Lifecycle Status: ${status.value}"),
                TextComponent("This component responds to lifecycle events")
            )
        ).compose(receiver)

        return receiver
    }
}

/**
 * Combined example that shows all effects working together.
 */
class CombinedEffectsExample : Composable {
    private val tabIndex = mutableStateOf(0)
    private val tabs = listOf(
        "LaunchedEffect",
        "DisposableEffect",
        "SideEffect",
        "DerivedState",
        "LifecycleAware"
    )

    override fun <T> compose(receiver: T): T {
        Column(
            content = listOf(
                // Tab buttons
                Row(
                    content = tabs.mapIndexed { index, title ->
                        ButtonComponent(
                            onClick = { tabIndex.value = index },
                            text = title
                        )
                    }
                ),

                // Tab content
                when (tabIndex.value) {
                    0 -> LaunchedEffectExample()
                    1 -> DisposableEffectExample()
                    2 -> SideEffectExample()
                    3 -> DerivedStateExample()
                    4 -> LifecycleAwareExample()
                    else -> TextComponent("Unknown tab")
                }
            )
        ).compose(receiver)

        return receiver
    }
} 