package code.yousef.summon.runtime

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
        ComposerContext.withComposer(composer) {
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
