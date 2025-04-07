package code.yousef.summon.runtime

/**
 * Manages the lifecycle of a composition.
 * This class is responsible for creating, updating, and disposing compositions.
 */
class CompositionContext {
    private val composer: Composer
    
    /**
     * Creates a new CompositionContext with a new composer.
     */
    constructor() {
        composer = RecomposerHolder.recomposer.createComposer()
    }
    
    /**
     * Creates a new CompositionContext with the given composer.
     * 
     * @param composer The composer to use for this context.
     */
    constructor(composer: Composer) {
        this.composer = composer
    }
    
    /**
     * Composes the content with the current composer.
     * 
     * @param content The composable content to compose.
     */
    fun compose(content: @Composable () -> Unit) {
        try {
            // Set the current composer
            CompositionLocal.setCurrentComposer(composer)
            
            // Start composition
            composer.startNode()
            
            // Execute the content
            content()
            
            // End composition
            composer.endNode()
        } finally {
            // Reset the current composer
            CompositionLocal.setCurrentComposer(null)
        }
    }
    
    /**
     * Disposes this composition context, releasing any resources.
     */
    fun dispose() {
        // In a real implementation, this would clean up resources
        CompositionLocal.setCurrentComposer(null)
    }
    
    companion object {
        /**
         * Creates a new composition and composes the given content.
         * 
         * @param content The composable content to compose.
         * @return A new CompositionContext.
         */
        fun createComposition(content: @Composable () -> Unit): CompositionContext {
            val context = CompositionContext()
            context.compose(content)
            return context
        }
    }
} 
