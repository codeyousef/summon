package code.yousef.example.todo

import code.yousef.summon.components.input.Form
import code.yousef.summon.components.input.TextField
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains

class TextFieldTest {
    @Test
    fun `text field includes name attribute for form submission`() {
        // Arrange
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)

        // Act - Render a text field inside a form
        val html = renderer.renderComposableRoot {
            Form(onSubmit = {}) {
                TextField(
                    value = "",
                    onValueChange = {},
                    placeholder = "Add new todo..."
                )
            }
        }

        // Assert - TextField should have name attribute for form data collection
        // This test will initially FAIL
        assertContains(
            html, """<input""", ignoreCase = true,
            message = "HTML should contain an input element"
        )
        assertContains(
            html, """type="text"""", ignoreCase = true,
            message = "Input should be of type text"
        )
        assertContains(
            html, """name=""", ignoreCase = false,
            message = "Input field should have a name attribute for form submission"
        )
        assertContains(
            html, """placeholder="Add new todo...""", ignoreCase = true,
            message = "Input should have the placeholder text"
        )
    }

    @Test
    fun `text field with value renders correctly`() {
        // Arrange
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)
        val testValue = "Test Todo Item"

        // Act
        val html = renderer.renderComposableRoot {
            TextField(
                value = testValue,
                onValueChange = {},
                placeholder = "Enter todo"
            )
        }

        // Assert - Basic text field rendering
        assertContains(
            html, """<input""", ignoreCase = true,
            message = "HTML should contain an input element"
        )
        assertContains(
            html, """value="$testValue"""", ignoreCase = true,
            message = "Input should contain the provided value"
        )
    }
}