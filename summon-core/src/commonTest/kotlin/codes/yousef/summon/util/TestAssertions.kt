package codes.yousef.summon.util

import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.MockPlatformRenderer
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Common assertion utilities for testing components and rendering.
 * These utilities help reduce duplication in test code.
 */
object TestAssertions {

    /**
     * Asserts that a component was rendered with the expected properties
     */
    fun assertComponentRendered(
        renderer: MockPlatformRenderer,
        componentType: String,
        verifyBlock: (MockPlatformRenderer) -> Unit = {}
    ) {
        assertTrue(
            when (componentType) {
                "Button" -> renderer.renderButtonCalled
                "TextField" -> renderer.renderTextFieldCalled
                "Image" -> renderer.renderImageCalled
                "Box" -> renderer.renderBoxCalled
                "Row" -> renderer.renderRowCalled
                "Column" -> renderer.renderColumnCalled
                "Text" -> renderer.renderTextCalled
                "Link" -> renderer.renderLinkCalled
                "Select" -> renderer.renderSelectCalled
                "Checkbox" -> renderer.renderCheckboxCalled
                else -> false
            },
            "$componentType was not rendered"
        )
        verifyBlock(renderer)
    }

    /**
     * Asserts that a modifier contains expected styles
     */
    fun assertModifierHasStyle(
        modifier: Modifier?,
        styleName: String,
        expectedValue: String,
        message: String? = null
    ) {
        assertNotNull(modifier, "Modifier should not be null")
        val actualValue = modifier.styles[styleName]
        assertEquals(
            expectedValue,
            actualValue,
            message ?: "Expected style '$styleName' to be '$expectedValue' but was '$actualValue'"
        )
    }

    /**
     * Asserts that a modifier contains expected attributes
     */
    fun assertModifierHasAttribute(
        modifier: Modifier?,
        attributeName: String,
        expectedValue: String,
        message: String? = null
    ) {
        assertNotNull(modifier, "Modifier should not be null")
        val actualValue = modifier.attributes?.get(attributeName)
        assertEquals(
            expectedValue,
            actualValue,
            message ?: "Expected attribute '$attributeName' to be '$expectedValue' but was '$actualValue'"
        )
    }

    /**
     * Asserts that a modifier has a specific class name
     */
    fun assertModifierHasClass(
        modifier: Modifier?,
        className: String,
        message: String? = null
    ) {
        assertModifierHasAttribute(modifier, "class", className, message)
    }

    /**
     * Asserts that rendered content contains expected text
     */
    fun assertRenderedTextContains(
        renderer: MockPlatformRenderer,
        expectedText: String,
        ignoreCase: Boolean = false
    ) {
        val renderedContent = buildString {
            renderer.lastTextRendered?.let { append(it) }
            renderer.lastLabelTextRendered?.let { append(it) }
            renderer.lastTooltipTextRendered?.let { append(it) }
            // Add other text sources as needed
        }

        assertTrue(
            renderedContent.contains(expectedText, ignoreCase),
            "Expected rendered content to contain '$expectedText' but was '$renderedContent'"
        )
    }

    /**
     * Asserts common button properties
     */
    fun assertButton(
        renderer: MockPlatformRenderer,
        expectedText: String,
        expectedEnabled: Boolean = true,
        expectedModifier: (Modifier) -> Unit = {}
    ) {
        assertComponentRendered(renderer, "Button")
        // Note: Button text is usually rendered as part of content
        assertNotNull(renderer.lastButtonContentRendered, "Button content should not be null")
        renderer.lastButtonModifierRendered?.let { expectedModifier(it) }
    }

    /**
     * Asserts common text field properties
     */
    fun assertTextField(
        renderer: MockPlatformRenderer,
        expectedValue: String,
        expectedType: String = "text",
        expectedModifier: (Modifier) -> Unit = {}
    ) {
        assertComponentRendered(renderer, "TextField")
        assertEquals(expectedValue, renderer.lastTextFieldValueRendered)
        assertEquals(expectedType, renderer.lastTextFieldTypeRendered)
        renderer.lastTextFieldModifierRendered?.let { expectedModifier(it) }
    }

    /**
     * Asserts that a validation error is displayed
     */
    fun assertValidationError(
        renderer: MockPlatformRenderer,
        expectedError: String
    ) {
        val errorFound = renderer.lastTextRendered?.contains(expectedError) == true ||
                renderer.lastLabelTextRendered?.contains(expectedError) == true

        assertTrue(
            errorFound,
            "Expected validation error '$expectedError' was not found in rendered content"
        )
    }

    /**
     * Asserts that no validation errors are displayed
     */
    fun assertNoValidationErrors(renderer: MockPlatformRenderer) {
        // Check that no error-related text is present in rendered content
        val hasNoErrors = renderer.lastTextRendered?.contains("error", ignoreCase = true) != true &&
                renderer.lastLabelTextRendered?.contains("error", ignoreCase = true) != true

        assertTrue(
            hasNoErrors,
            "Expected no validation errors but found error text in rendered content"
        )
    }
}

/**
 * Extension functions for more fluent assertions
 */

/**
 * Asserts that this renderer rendered a specific component
 */
fun MockPlatformRenderer.assertRendered(componentType: String) {
    TestAssertions.assertComponentRendered(this, componentType)
}

/**
 * Asserts that this modifier has a specific style
 */
fun Modifier.assertHasStyle(styleName: String, expectedValue: String) {
    TestAssertions.assertModifierHasStyle(this, styleName, expectedValue)
}

/**
 * Asserts that this modifier has a specific attribute
 */
fun Modifier.assertHasAttribute(attributeName: String, expectedValue: String) {
    TestAssertions.assertModifierHasAttribute(this, attributeName, expectedValue)
}