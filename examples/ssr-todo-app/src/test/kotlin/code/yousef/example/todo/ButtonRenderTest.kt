package code.yousef.example.todo

import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.components.input.Form
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.clearPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains

class ButtonRenderTest {
    @Test
    fun `button renders as submit type when inside form`() {
        // Arrange
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)

        // Act - Render a button inside a form context
        val html = try {
            renderer.renderComposableRoot {
                Form(onSubmit = {}) {
                    Button(
                        onClick = {},
                        label = "Add Todo",
                        variant = ButtonVariant.PRIMARY
                    )
                }
            }
        } finally {
            clearPlatformRenderer()
        }

        // Assert - Button should be rendered as submit type for form submission
        // This test will initially FAIL
        assertContains(
            html, """<button""", ignoreCase = true,
            message = "HTML should contain a button element"
        )
        assertContains(
            html, """type="submit"""", ignoreCase = true,
            message = "Button inside form should have type='submit' attribute"
        )
        assertContains(
            html, "Add Todo",
            message = "Button should contain the label text"
        )
    }

    @Test
    fun `button with primary variant renders correctly`() {
        // Arrange
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)

        // Act
        val html = try {
            renderer.renderComposableRoot {
                Button(
                    onClick = {},
                    label = "Test Button",
                    variant = ButtonVariant.PRIMARY
                )
            }
        } finally {
            clearPlatformRenderer()
        }

        // Assert - Basic button rendering
        assertContains(
            html, """<button""", ignoreCase = true,
            message = "HTML should contain a button element"
        )
        assertContains(
            html, "Test Button",
            message = "Button should contain the label text"
        )
    }
}
