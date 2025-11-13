package codes.yousef.summon.runtime

import kotlin.test.Test
import kotlin.test.assertTrue
import codes.yousef.summon.annotation.Composable as AnnotationComposable

/**
 * Tests for the Composable typealias.
 */
class ComposableTypealiasTest {
    
    /**
     * Test that the Composable typealias can be used to annotate a function.
     */
    @Test
    fun testComposableTypealiasCanBeApplied() {
        // Define a function with the Composable typealias
        @Composable
        fun TestComposableFunction(): String {
            return "test"
        }
        
        // Call the function to verify it works
        val result = TestComposableFunction()
        assertTrue(result == "test", "Function with @Composable typealias should work")
    }
    
    /**
     * Test that the Composable typealias is interchangeable with the original annotation.
     */
    @Test
    fun testComposableTypealiasIsInterchangeable() {
        // Define a function with the original annotation
        @AnnotationComposable
        fun TestOriginalFunction(): String {
            return "original"
        }
        
        // Define a function with the typealias
        @Composable
        fun TestTypealiasFunction(): String {
            return "typealias"
        }
        
        // Define a function that takes a composable function parameter
        fun TakeComposable(content: @Composable () -> String): String {
            return content()
        }
        
        // Define a function that takes an original annotation function parameter
        fun TakeOriginal(content: @AnnotationComposable () -> String): String {
            return content()
        }
        
        // Verify that functions with either annotation can be passed to either parameter type
        val result1 = TakeComposable { TestOriginalFunction() }
        val result2 = TakeComposable { TestTypealiasFunction() }
        val result3 = TakeOriginal { TestOriginalFunction() }
        val result4 = TakeOriginal { TestTypealiasFunction() }
        
        assertTrue(result1 == "original", "Original function should work with Composable parameter")
        assertTrue(result2 == "typealias", "Typealias function should work with Composable parameter")
        assertTrue(result3 == "original", "Original function should work with Original parameter")
        assertTrue(result4 == "typealias", "Typealias function should work with Original parameter")
    }
}