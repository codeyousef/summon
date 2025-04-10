package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable

/**
 * Manages the lifecycle of a composition.
 * This class is responsible for creating, updating, and disposing compositions.
 *
 * Renamed to avoid conflicts with ComposeManagerContext.
 */
class CompositionContextImpl private constructor() {
    private val composer: Composer = RecomposerHolder.recomposer.createComposer()
    
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
        composer.dispose()
        CompositionLocal.setCurrentComposer(null)
    }
    
    companion object {
        /**
         * Creates a new composition and composes the content.
         *
         * @param content The composable content to compose.
         * @return A new CompositionContextImpl.
         */
        fun createComposition(content: @Composable () -> Unit): CompositionContextImpl {
            val context = CompositionContextImpl()
            context.compose(content)
            return context
        }
    }
} 
