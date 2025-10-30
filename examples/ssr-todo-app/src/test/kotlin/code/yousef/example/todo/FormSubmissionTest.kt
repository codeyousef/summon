package code.yousef.example.todo

import code.yousef.example.todo.components.CreateTodoForm
import code.yousef.example.todo.models.FormState
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.clearPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains

class FormSubmissionTest {
    @Test
    fun `form generates correct HTML with action and method attributes`() {
        // Arrange
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)
        val formState = FormState.success()

        // Act
        val html = try {
            renderer.renderComposableRoot {
                CreateTodoForm(formState = formState)
            }
        } finally {
            clearPlatformRenderer()
        }

        // Assert - Check that form has correct attributes for SSR submission
        // This test will initially FAIL because the form doesn't generate proper HTML yet
        assertContains(
            html, """<form""", ignoreCase = true,
            message = "HTML should contain a form element"
        )
        assertContains(
            html, """action="/todos"""", ignoreCase = true,
            message = "Form should have action='/todos' attribute"
        )
        assertContains(
            html, """method="post"""", ignoreCase = true,
            message = "Form should have method='post' attribute"
        )
    }

    @Test
    fun `form contains input field with name attribute`() {
        // Arrange
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)
        val formState = FormState.success()

        // Act
        val html = try {
            renderer.renderComposableRoot {
                CreateTodoForm(formState = formState)
            }
        } finally {
            clearPlatformRenderer()
        }

        // Assert - Check that input field has name attribute for form data
        // This test will initially FAIL
        assertContains(
            html, """name="text"""", ignoreCase = true,
            message = "Input field should have name='text' attribute for form submission"
        )
    }

    @Test
    fun `form contains submit button`() {
        // Arrange
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)
        val formState = FormState.success()

        // Act
        val html = try {
            renderer.renderComposableRoot {
                CreateTodoForm(formState = formState)
            }
        } finally {
            clearPlatformRenderer()
        }

        // Assert - Check that button is a submit type
        // This test will initially FAIL
        assertContains(
            html, """<button""", ignoreCase = true,
            message = "HTML should contain a button element"
        )
        assertContains(
            html, """type="submit"""", ignoreCase = true,
            message = "Button should have type='submit' attribute"
        )
    }
}
