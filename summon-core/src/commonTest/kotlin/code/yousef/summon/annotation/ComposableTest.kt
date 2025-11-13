package codes.yousef.summon.annotation

import kotlin.test.Test
import kotlin.test.assertTrue
import codes.yousef.summon.annotation.StandardComposable as StdComposable

/**
 * Tests for the Composable annotation.
 */
class ComposableTest {

    /**
     * Test that the Composable annotation can be applied to functions.
     */
    @Test
    fun testComposableAnnotationOnFunction() {
        // Define a function with the Composable annotation
        @Composable
        fun TestComposableFunction(): String {
            return "test"
        }

        // Call the function to verify it works
        val result = TestComposableFunction()
        assertTrue(result == "test", "Function with @Composable annotation should work")
    }

    /**
     * Test that the Composable annotation can be applied to property getters.
     * 
     * Note: This test verifies that the code compiles, but doesn't test runtime behavior
     * since annotations are primarily checked at compile time.
     */
    @Test
    fun testComposableAnnotationOnPropertyGetter() {
        // This test simply verifies that the code with a @Composable property getter compiles
        class TestClass {
            val testProperty: String
                @Composable get() = "property"
        }

        // Create an instance and access the property
        val instance = TestClass()
        val result = instance.testProperty
        assertTrue(result == "property", "Property with @Composable getter should work")
    }

    /**
     * Test that the Composable annotation can be used as a type parameter.
     */
    @Test
    fun testComposableAnnotationAsTypeParameter() {
        // Define a function that takes a composable function parameter
        fun takeComposable(content: @Composable () -> String): String {
            return content()
        }

        // Call the function with a lambda
        val result = takeComposable { "type parameter" }
        assertTrue(result == "type parameter", "Function with @Composable type parameter should work")
    }

    /**
     * Test that the StandardComposable typealias can be used instead of Composable.
     */
    @Test
    fun testStandardComposableTypealias() {
        // Define a function with the StandardComposable typealias
        @StdComposable
        fun TestStandardComposableFunction(): String {
            return "standard"
        }

        // Call the function to verify it works
        val result = TestStandardComposableFunction()
        assertTrue(result == "standard", "Function with @StandardComposable typealias should work")
    }

    /**
     * Test that the StandardComposable typealias is interchangeable with Composable.
     */
    @Test
    fun testStandardComposableIsInterchangeable() {
        // Define a function with the Composable annotation
        @Composable
        fun TestComposableFunction(): String {
            return "composable"
        }

        // Define a function with the StandardComposable typealias
        @StdComposable
        fun TestStandardFunction(): String {
            return "standard"
        }

        // Define functions that take different annotation types
        fun TakeComposable(content: @Composable () -> String): String {
            return content()
        }

        fun TakeStandard(content: @StdComposable () -> String): String {
            return content()
        }

        // Verify that functions with either annotation can be passed to either parameter type
        val result1 = TakeComposable { TestComposableFunction() }
        val result2 = TakeComposable { TestStandardFunction() }
        val result3 = TakeStandard { TestComposableFunction() }
        val result4 = TakeStandard { TestStandardFunction() }

        assertTrue(result1 == "composable", "Composable function should work with Composable parameter")
        assertTrue(result2 == "standard", "StandardComposable function should work with Composable parameter")
        assertTrue(result3 == "composable", "Composable function should work with StandardComposable parameter")
        assertTrue(result4 == "standard", "StandardComposable function should work with StandardComposable parameter")
    }
}
