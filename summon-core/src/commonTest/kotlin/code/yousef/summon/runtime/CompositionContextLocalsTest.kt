package code.yousef.summon.runtime

import kotlin.test.Test
import kotlin.test.assertSame

class CompositionContextLocalsTest {
    
    @Test
    fun testGetCompositionLocal() {
        // The function should return the CompositionLocal singleton
        val compositionLocal = getCompositionLocal()
        
        // Verify that the returned object is the same as the CompositionLocal singleton
        assertSame(CompositionLocal, compositionLocal, 
            "getCompositionLocal() should return the CompositionLocal singleton")
    }
}