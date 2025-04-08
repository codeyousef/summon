package code.yousef.summon.runtime

import code.yousef.summon.runtime.remember
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.Composable

/**
 * A simple Hello World example using the Summon composition system.
 * This demonstrates the basic usage of the @Composable annotation and state.
 */
object SimpleHelloWorld {
    /**
     * Main entry point for the Hello World example.
     * Creates a composition and renders a simple UI with a counter.
     */
    fun main() {
        // Create a composition context
        val context = CompositionContext()
        
        // Compose the UI
        context.compose {
            HelloWorld()
        }
        
        // In a real application, this would be rendered to the DOM or UI
        println("Hello World example composed successfully!")
    }
    
    /**
     * A simple Hello World composable that displays a greeting and a counter.
     */
    @Composable
    fun HelloWorld() {
        // Create some state
        val count = remember { mutableStateOf(0) }
        
        // Display a greeting
        Text("Hello, Summon World!")
        
        // Display the counter
        Text("You've clicked ${count.value} times")
        
        // Add a button to increment the counter
        Button(onClick = { count.value++ }) {
            Text("Click me")
        }
    }
    
    /**
     * A text component that displays a string.
     */
    @Composable
    fun Text(text: String) {
        println("Text: $text")
    }
    
    /**
     * A button component that performs an action when clicked.
     */
    @Composable
    fun Button(onClick: () -> Unit, content: @Composable () -> Unit) {
        println("Button: ")
        content()
        println("(Click would trigger the action)")
    }
} 
