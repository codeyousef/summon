package code.yousef.summon.runtime

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CommonComposer
import code.yousef.summon.runtime.ComposeManagerContext

/**
 * A simple "Hello, World!" example.
 */
object SimpleHelloWorld {
    /**
     * Renders "Hello, World!" to the console.
     */
    fun render() {
        // Create a composer
        val composer = CommonComposer()
        
        // Use composition context to render
        ComposeManagerContext.withComposer(composer) {
            hello()
        }
    }
    
    /**
     * A simple composable that prints "Hello, World!".
     */
    @Composable
    private fun hello() {
        println("Hello, World!")
    }
} 
