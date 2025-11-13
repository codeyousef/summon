package codes.yousef.summon.components.input

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.IconType
import codes.yousef.summon.components.feedback.AlertVariant
import codes.yousef.summon.components.feedback.ProgressType
import codes.yousef.summon.components.navigation.Tab
import codes.yousef.summon.core.FlowContentCompat
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.Composer
import codes.yousef.summon.runtime.CompositionLocal
import codes.yousef.summon.runtime.FormContent
import codes.yousef.summon.runtime.LocalPlatformRenderer
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.*

// Extension functions for testing
private fun Modifier.hasAttribute(name: String, value: String): Boolean =
    attributes[name] == value

/**
 * Tests for the TextField component
 */
class TextFieldTest {

    /**
     * A mock implementation of PlatformRenderer for testing
     */
    private class MockPlatformRenderer : code.yousef.summon.runtime.MockPlatformRenderer() {
        override var renderTextFieldCalled = false
        var lastValue: String? = null
        var lastOnValueChange: ((String) -> Unit)? = null
        var lastModifier: Modifier? = null
        var lastType: String? = null

        override fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, type: String) {
            renderTextFieldCalled = true
            lastValue = value
            lastOnValueChange = onValueChange
            lastModifier = modifier
            lastType = type
        }

        // Minimal implementations for other required methods
        override fun renderText(text: String, modifier: Modifier) {}
        override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {}
        override fun renderButton(
            onClick: () -> Unit,
            modifier: Modifier,
            content: @Composable FlowContentCompat.() -> Unit
        ) {
        }

        override fun <T> renderSelect(
            selectedValue: T?,
            onSelectedChange: (T?) -> Unit,
            options: List<code.yousef.summon.runtime.SelectOption<T>>,
            modifier: Modifier
        ) {
        }

        override fun renderDatePicker(
            value: LocalDate?,
            onValueChange: (LocalDate?) -> Unit,
            enabled: Boolean,
            min: LocalDate?,
            max: LocalDate?,
            modifier: Modifier
        ) {
        }

        override fun renderTextArea(
            value: String,
            onValueChange: (String) -> Unit,
            enabled: Boolean,
            readOnly: Boolean,
            rows: Int?,
            maxLength: Int?,
            placeholder: String?,
            modifier: Modifier
        ) {
        }

        override fun addHeadElement(content: String) {}
        override fun getHeadElements(): List<String> = emptyList()
        override fun renderComposableRoot(composable: @Composable () -> Unit): String = ""
        override fun renderComposable(composable: @Composable () -> Unit) {}
        override fun renderRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderBox(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderImage(src: String, alt: String?, modifier: Modifier) {}
        override fun renderIcon(
            name: String,
            modifier: Modifier,
            onClick: (() -> Unit)?,
            svgContent: String?,
            type: IconType
        ) {
        }

        override fun renderAlertContainer(
            variant: AlertVariant?,
            modifier: Modifier,
            content: @Composable FlowContentCompat.() -> Unit
        ) {
        }

        override fun renderBadge(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderCheckbox(
            checked: Boolean,
            onCheckedChange: (Boolean) -> Unit,
            enabled: Boolean,
            modifier: Modifier
        ) {
        }

        override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {}
        override fun renderFileUpload(
            onFilesSelected: (List<FileInfo>) -> Unit,
            accept: String?,
            multiple: Boolean,
            enabled: Boolean,
            capture: String?,
            modifier: Modifier
        ): () -> Unit = {}

        override fun renderForm(
            onSubmit: (() -> Unit)?,
            modifier: Modifier,
            content: @Composable FormContent.() -> Unit
        ) {
        }

        override fun renderFormField(
            modifier: Modifier,
            labelId: String?,
            isRequired: Boolean,
            isError: Boolean,
            errorMessageId: String?,
            content: @Composable FlowContentCompat.() -> Unit
        ) {
        }

        override fun renderRadioButton(selected: Boolean, onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {}
        override fun renderSpacer(modifier: Modifier) {}
        override fun renderRangeSlider(
            value: ClosedFloatingPointRange<Float>,
            onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
            valueRange: ClosedFloatingPointRange<Float>,
            steps: Int,
            enabled: Boolean,
            modifier: Modifier
        ) {
        }

        override fun renderSlider(
            value: Float,
            onValueChange: (Float) -> Unit,
            valueRange: ClosedFloatingPointRange<Float>,
            steps: Int,
            enabled: Boolean,
            modifier: Modifier
        ) {
        }

        override fun renderSwitch(
            checked: Boolean,
            onCheckedChange: (Boolean) -> Unit,
            enabled: Boolean,
            modifier: Modifier
        ) {
        }

        override fun renderTimePicker(
            value: LocalTime?,
            onValueChange: (LocalTime?) -> Unit,
            enabled: Boolean,
            is24Hour: Boolean,
            modifier: Modifier
        ) {
        }

        override fun renderAspectRatio(
            ratio: Float,
            modifier: Modifier,
            content: @Composable FlowContentCompat.() -> Unit
        ) {
        }

        override fun renderCard(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderLink(href: String, modifier: Modifier) {}
        override fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit) {}
        override fun renderEnhancedLink(
            href: String,
            target: String?,
            title: String?,
            ariaLabel: String?,
            ariaDescribedBy: String?,
            modifier: Modifier,
            fallbackText: String?
        ) {
        }

        override fun renderTabLayout(
            tabs: List<Tab>,
            selectedTabIndex: Int,
            onTabSelected: (Int) -> Unit,
            modifier: Modifier
        ) {
        }

        override fun renderTabLayout(modifier: Modifier, content: @Composable () -> Unit) {}
        override fun renderTabLayout(
            tabs: List<String>,
            selectedTab: String,
            onTabSelected: (String) -> Unit,
            modifier: Modifier,
            content: () -> Unit
        ) {
        }

        override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {}
        override fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit) {}
        override fun renderAnimatedContent(modifier: Modifier) {}
        override fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit) {}
        override fun renderBlock(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderInline(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderDiv(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderSpan(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderDivider(modifier: Modifier) {}
        override fun renderExpansionPanel(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderGrid(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderLazyColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderLazyRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderHtmlTag(
            tagName: String,
            modifier: Modifier,
            content: @Composable FlowContentCompat.() -> Unit
        ) {
        }
    }

    // Mock implementation of Composer for testing
    private class MockComposer : Composer {
        override val inserting: Boolean = false

        override fun startNode() {}
        override fun startGroup(key: Any?) {}
        override fun endNode() {}
        override fun endGroup() {}
        override fun changed(value: Any?): Boolean = true
        override fun updateValue(value: Any?) {}
        override fun nextSlot() {}
        override fun getSlot(): Any? = null
        override fun setSlot(value: Any?) {}
        override fun recordRead(state: Any) {}
        override fun recordWrite(state: Any) {}
        override fun reportChanged() {}
        override fun registerDisposable(disposable: () -> Unit) {}
        override fun dispose() {}
        override fun startCompose() {}
        override fun endCompose() {}
        override fun <T> compose(composable: @Composable () -> T): T {
            @Suppress("UNCHECKED_CAST")
            return null as T
        }

        override fun recompose() {
            // Mock implementation
        }

        override fun rememberedValue(key: Any): Any? {
            return null
        }

        override fun updateRememberedValue(key: Any, value: Any?) {
            // Mock implementation
        }
    }

    @Test
    fun testTextFieldWithDefaultParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()
        var textChanged = false
        var newValue = ""

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the TextField component with default parameters
            TextField(
                value = "Initial text",
                onValueChange = {
                    textChanged = true
                    newValue = it
                }
            )

            // Verify that renderTextField was called
            assertTrue(mockRenderer.renderTextFieldCalled, "renderTextField should have been called")

            // Verify the value
            assertEquals("Initial text", mockRenderer.lastValue, "Value should be 'Initial text'")

            // Verify the onValueChange handler
            assertNotNull(mockRenderer.lastOnValueChange, "onValueChange handler should not be null")
            mockRenderer.lastOnValueChange?.invoke("New text")
            assertTrue(textChanged, "onValueChange handler should have been called")
            assertEquals("New text", newValue, "New value should be 'New text'")

            // Verify the type
            assertEquals("text", mockRenderer.lastType, "Type should be 'text'")

            // Verify the modifier
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
        }
    }

    @Test
    fun testTextFieldWithPlaceholder() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the TextField component with a placeholder
            TextField(
                value = "",
                onValueChange = { },
                placeholder = "Enter text here"
            )

            // Verify that renderTextField was called
            assertTrue(mockRenderer.renderTextFieldCalled, "renderTextField should have been called")

            // Verify the placeholder attribute
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            assertTrue(
                mockRenderer.lastModifier!!.hasAttribute("placeholder", "Enter text here"),
                "Modifier should have placeholder attribute"
            )
        }
    }

    @Test
    fun testTextFieldWithDifferentTypes() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Test each type
            TextFieldType.values().forEach { type ->
                TextField(
                    value = "",
                    onValueChange = { },
                    type = type
                )

                // Verify the type
                assertEquals(
                    type.name.lowercase(), mockRenderer.lastType,
                    "Type should be '${type.name.lowercase()}'"
                )
            }
        }
    }

    @Test
    fun testDisabledTextField() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()
        var textChanged = false

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the TextField component with isEnabled=false
            TextField(
                value = "Disabled text",
                onValueChange = { textChanged = true },
                isEnabled = false
            )

            // Verify that renderTextField was called
            assertTrue(mockRenderer.renderTextFieldCalled, "renderTextField should have been called")

            // Verify the disabled attribute
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            assertTrue(
                mockRenderer.lastModifier!!.hasAttribute("disabled", "true"),
                "Modifier should have disabled attribute"
            )

            // Verify the onValueChange handler doesn't update when disabled
            mockRenderer.lastOnValueChange?.invoke("New text")
            assertFalse(textChanged, "onValueChange handler should not be called when disabled")
        }
    }

    @Test
    fun testReadOnlyTextField() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the TextField component with isReadOnly=true
            TextField(
                value = "Read-only text",
                onValueChange = { },
                isReadOnly = true
            )

            // Verify that renderTextField was called
            assertTrue(mockRenderer.renderTextFieldCalled, "renderTextField should have been called")

            // Verify the readonly attribute
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            assertTrue(
                mockRenderer.lastModifier!!.hasAttribute("readonly", "true"),
                "Modifier should have readonly attribute"
            )
        }
    }

    @Test
    fun testTextFieldWithValidation() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the TextField component with isError=true
            TextField(
                value = "Test text",
                onValueChange = { },
                isError = true
            )

            // Verify that renderTextField was called
            assertTrue(mockRenderer.renderTextFieldCalled, "renderTextField should have been called")

            // Verify the aria-invalid attribute is set when isError is true
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            assertTrue(
                mockRenderer.lastModifier!!.hasAttribute("aria-invalid", "true"),
                "Modifier should have aria-invalid attribute when isError is true"
            )

            // Call the TextField again with isError=false
            TextField(
                value = "Test text",
                onValueChange = { },
                isError = false
            )

            // Verify the aria-invalid attribute is not set when isError is false
            assertFalse(
                mockRenderer.lastModifier!!.hasAttribute("aria-invalid", "true"),
                "Modifier should not have aria-invalid attribute when isError is false"
            )
        }
    }

    @Test
    fun testStatefulTextField() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()
        var externalValueChanged = false
        var externalNewValue = ""

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the StatefulTextField component
            StatefulTextField(
                initialValue = "Initial text",
                onValueChange = {
                    externalValueChanged = true
                    externalNewValue = it
                }
            )

            // Verify that renderTextField was called
            assertTrue(mockRenderer.renderTextFieldCalled, "renderTextField should have been called")

            // Verify the initial value
            assertEquals("Initial text", mockRenderer.lastValue, "Initial value should be 'Initial text'")

            // Simulate a value change
            mockRenderer.lastOnValueChange?.invoke("New text")

            // Verify the external onValueChange was called
            assertTrue(externalValueChanged, "External onValueChange should have been called")
            assertEquals("New text", externalNewValue, "External new value should be 'New text'")

            // Note: We can't test state persistence between calls in this test environment
            // because our mock Composer doesn't properly implement the remember function.
            // In a real environment, the state would be preserved.
        }
    }
}
