package codes.yousef.summon.runtime

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class RecomposerHolderTest {
    
    @Test
    fun testRecomposerAccess() {
        // Get the recomposer from the holder
        val recomposer = RecomposerHolder.recomposer
        
        // Verify that a recomposer instance is provided
        assertNotNull(recomposer, "RecomposerHolder should provide a Recomposer instance")
    }
    
    @Test
    fun testCurrentRecomposer() {
        // Get the recomposer using the current() method
        val recomposer = RecomposerHolder.current()
        
        // Verify that the current() method returns the same instance as the recomposer property
        assertSame(RecomposerHolder.recomposer, recomposer, 
            "current() should return the same Recomposer instance as the recomposer property")
    }
    
    @Test
    fun testCreateComposer() {
        // Create a composer using the createComposer() method
        val composer = RecomposerHolder.createComposer()
        
        // Verify that a composer instance is created
        assertNotNull(composer, "createComposer() should create a Composer instance")
    }
}