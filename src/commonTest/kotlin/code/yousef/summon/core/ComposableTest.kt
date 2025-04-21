package code.yousef.summon.core

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.CommonComposer
import code.yousef.summon.runtime.ComposeManagerContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Tests for the @Composable annotation functionality
 */
class ComposableTest {

    /**
     * A simple data class for testing compose with custom objects
     */
    data class TestObject(val id: Int, val name: String)

    @Test
    fun testBasicComposableAnnotation() {
        val composer = CommonComposer()

        // Define a composable function
        @Composable
        fun simpleComposable(): String {
            return "Composable Function Result"
        }

        // Test compose with the annotated function
        val result = composer.compose {
            simpleComposable()
        }

        assertEquals("Composable Function Result", result, "compose should work with @Composable functions")
    }

    @Test
    fun testComposableWithParameters() {
        val composer = CommonComposer()

        // Define a composable function with parameters
        @Composable
        fun parameterizedComposable(value: String, number: Int): String {
            return "Value: $value, Number: $number"
        }

        // Test compose with the annotated function that takes parameters
        val result = composer.compose {
            parameterizedComposable("Test", 42)
        }

        assertEquals("Value: Test, Number: 42", result, "compose should work with @Composable functions that take parameters")
    }

    @Test
    fun testNestedComposableFunctions() {
        val composer = CommonComposer()

        // Define nested composable functions
        @Composable
        fun innerComposable(value: String): String {
            return "Inner: $value"
        }

        @Composable
        fun outerComposable(value: String): String {
            return "Outer: ${innerComposable(value)}"
        }

        // Test compose with nested annotated functions
        val result = composer.compose {
            outerComposable("Test")
        }

        assertEquals("Outer: Inner: Test", result, "compose should work with nested @Composable functions")
    }

    @Test
    fun testComposableWithCustomObjects() {
        val composer = CommonComposer()

        // Define a composable function that works with custom objects
        @Composable
        fun objectComposable(obj: TestObject): TestObject {
            return TestObject(obj.id + 1, "Modified: ${obj.name}")
        }

        // Test compose with the annotated function that works with custom objects
        val testObj = TestObject(1, "Test")
        val result = composer.compose {
            objectComposable(testObj)
        }

        assertEquals(TestObject(2, "Modified: Test"), result, "compose should work with @Composable functions that handle custom objects")
    }

    @Test
    fun testComposableWithComposeManagerContext() {
        // Get the current composer from the context
        val currentComposer = ComposeManagerContext.current
        assertNotNull(currentComposer, "Default composer should not be null")

        // Define a composable function
        @Composable
        fun contextComposable(): String {
            return "Using context composer"
        }

        // Test compose using the context composer
        val result = currentComposer.compose {
            contextComposable()
        }

        assertEquals("Using context composer", result, "compose should work with the context composer")
    }
}
