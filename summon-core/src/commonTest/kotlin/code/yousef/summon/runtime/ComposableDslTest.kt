package codes.yousef.summon.runtime

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Tests for the ComposableDsl annotation.
 */
class ComposableDslTest {
    
    /**
     * Test that the ComposableDsl annotation can be applied to a class.
     * This is a basic test to verify the annotation exists and can be used.
     */
    @Test
    fun testComposableDslCanBeAppliedToClass() {
        // Define a class with the ComposableDsl annotation
        @ComposableDsl
        class TestDslClass {
            fun testMethod() = "test"
        }
        
        // Create an instance of the class and verify it works
        val instance = TestDslClass()
        assertTrue(instance.testMethod() == "test", "TestDslClass should work with @ComposableDsl annotation")
    }
}