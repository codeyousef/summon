package code.yousef.summon.components.input

import code.yousef.summon.runtime.*
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.input.FormField // Import the component
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.util.TestComposer
import code.yousef.summon.util.runTestComposable
import code.yousef.summon.util.TestFileInfo // Needed for mock renderer boilerplate
import code.yousef.summon.components.display.Text // Import Text
import kotlinx.datetime.LocalTime
import kotlinx.datetime.LocalDate
import kotlinx.html.FlowContent
import kotlinx.html.id // Import for id attribute
import kotlinx.html.Tag // Import Tag
import kotlinx.html.Entities // Import Entities
import kotlinx.html.Unsafe // Import Unsafe

// Import other types used in PlatformRenderer methods for the mock
import code.yousef.summon.runtime.SelectOption as RendererSelectOption
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import kotlinx.html.TagConsumer
import kotlinx.html.org.w3c.dom.events.Event

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertSame
import kotlin.test.assertNull // Add assertNull import

// Add import for shared MockPlatformRenderer
import code.yousef.summon.runtime.MockPlatformRenderer

class FormFieldTest {

    @Test
    fun testBasicFormFieldRendering() {
        val mockRenderer = MockPlatformRenderer() // Changed to shared MockPlatformRenderer
        var contentRendered = false
        val testModifier = Modifier().padding("10px")

        runTestComposable(mockRenderer) {
            FormField(
                modifier = testModifier,
                fieldContent = { contentRendered = true /* fieldContentCalled = true */ }
            )
        }

        // Check parameters passed to the RENDERER
        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        // Assertions using updated property names
        assertNotNull(mockRenderer.lastFormFieldModifierRendered, "Modifier should not be null")
        assertNull(mockRenderer.lastFormFieldLabelIdRendered, "Label ID should be null by default")
        assertEquals(false, mockRenderer.lastFormFieldIsRequiredRendered, "isOptional should be false by default")
        assertEquals(false, mockRenderer.lastFormFieldIsErrorRendered, "isError should be false by default")
        assertNull(mockRenderer.lastFormFieldErrorMessageIdRendered, "Error Message ID should be null by default")
        assertNotNull(mockRenderer.lastFormFieldContentLambdaRendered, "Content lambda should be captured")

        // Invoke the captured content lambda to ensure it runs
        val flowContent = TestComposer.TestFlowContent() // Assuming a test/mock FlowContent
        mockRenderer.lastFormFieldContentLambdaRendered?.invoke(flowContent)
        assertTrue(contentRendered, "Content lambda was not executed")
    }

    @Test
    fun testFormFieldWithLabel() {
        val mockRenderer = MockPlatformRenderer() // Changed to shared MockPlatformRenderer
        val testLabelText = "Test Label"

        runTestComposable(mockRenderer) {
            FormField(
                label = { Text(testLabelText) }, // Wrap in composable lambda
                fieldContent = {}
            )
        }

        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        // We don't know the exact ID, but check it's passed.
        assertNotNull(mockRenderer.lastFormFieldLabelIdRendered, "Label ID should be set when labelText is provided")
    }

    @Test
    fun testFormFieldWithRequiredState() {
        val mockRenderer = MockPlatformRenderer() // Changed to shared MockPlatformRenderer

        runTestComposable(mockRenderer) {
            FormField(
                isRequired = true,
                fieldContent = {}
            )
        }

        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        assertEquals(true, mockRenderer.lastFormFieldIsRequiredRendered, "isRequired should be true")
    }

    @Test
    fun testFormFieldWithErrorStateAndText() {
        val mockRenderer = MockPlatformRenderer() // Changed to shared MockPlatformRenderer
        val testErrorText = "Test Error"

        runTestComposable(mockRenderer) {
            FormField(
                isError = true,
                errorText = { Text(testErrorText) }, // Wrap in composable lambda
                fieldContent = {}
            )
        }

        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        assertEquals(true, mockRenderer.lastFormFieldIsErrorRendered, "isError should be true")
        assertNull(mockRenderer.lastFormFieldErrorMessageIdRendered, "Error Message ID should be null")
        // Similar to label, we don't know the exact ID, but check it's passed.
    }

    @Test
    fun testFormFieldWithErrorStateNoErrorText() {
        val mockRenderer = MockPlatformRenderer() // Changed to shared MockPlatformRenderer

        runTestComposable(mockRenderer) {
            FormField(
                isError = true,
                errorText = null, // No error text provided
                fieldContent = {}
            )
        }

        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        assertEquals(true, mockRenderer.lastFormFieldIsErrorRendered, "isError should be true")
        assertNull(mockRenderer.lastFormFieldErrorMessageIdRendered, "Error Message ID should be null when errorText is null")
    }

    @Test
    fun testFormFieldWithHelperText() {
        val mockRenderer = MockPlatformRenderer() // Changed to shared MockPlatformRenderer
        val testHelperText = "Test Helper"

        runTestComposable(mockRenderer) {
            FormField(
                helperText = { Text(testHelperText) }, // Wrap in composable lambda
                isError = false, // Explicitly not an error
                fieldContent = {}
            )
        }

        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        assertEquals(false, mockRenderer.lastFormFieldIsErrorRendered, "isError should be false")
        assertNull(mockRenderer.lastFormFieldErrorMessageIdRendered, "errorMessageId should be null when helperText is shown (not error)")
    }

    @Test
    fun testFormFieldHelperTextTakesPrecedenceWhenNoError() {
        val mockRenderer = MockPlatformRenderer() // Changed to shared MockPlatformRenderer
        val testHelperText = "Helper Text"
        val testErrorText = "Error Text" // Provide both

        runTestComposable(mockRenderer) {
            FormField(
                helperText = { Text(testHelperText) }, // Wrap in composable lambda
                errorText = { Text(testErrorText) }, // Wrap in composable lambda
                isError = false, // Not in error state
                fieldContent = {}
            )
        }

        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        assertEquals(false, mockRenderer.lastFormFieldIsErrorRendered, "isError should be false")
        assertNull(mockRenderer.lastFormFieldErrorMessageIdRendered, "errorMessageId should be null when not isError, even if errorText is provided")
        // The FormField composable should internally render helperText, not errorText,
        // and thus not pass an errorMessageId to the renderer.
    }

    @Test
    fun testFormFieldErrorTextTakesPrecedenceWhenError() {
        val mockRenderer = MockPlatformRenderer() // Changed to shared MockPlatformRenderer
        val testHelperText = "Helper Text"
        val testErrorText = "Error Text" // Provide both

        runTestComposable(mockRenderer) {
            FormField(
                helperText = { Text(testHelperText) }, // Wrap in composable lambda
                errorText = { Text(testErrorText) }, // Wrap in composable lambda
                isError = true, // In error state
                fieldContent = {}
            )
        }

        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        assertEquals(true, mockRenderer.lastFormFieldIsErrorRendered, "isError should be true")
        assertNull(mockRenderer.lastFormFieldErrorMessageIdRendered, "errorMessageId should be null even when isError is true")
         // The FormField composable should internally render errorText, not helperText,
         // and thus pass an errorMessageId to the renderer.
    }
} 