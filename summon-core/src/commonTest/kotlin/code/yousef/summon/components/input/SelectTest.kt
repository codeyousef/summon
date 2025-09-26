package code.yousef.summon.components.input

// Import the PlatformRenderer's SelectOption, this is the one the interface method uses
// Import the component's SelectOption for creating the options list passed TO the component
// Import other types used in PlatformRenderer methods for the mock
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.MockPlatformRenderer
import code.yousef.summon.runtime.SelectOption
import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.util.runComposableTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SelectTest {

    @Test
    fun testBasicSelectRendering() {
        val renderer = MockPlatformRenderer()
        var selectedState: SummonMutableState<String?>? = null
        val componentOptions = listOf(
            SelectOption("val1", "Label 1"),
            SelectOption("val2", "Label 2", disabled = true)
        )
        val onSelectChange: (String?) -> Unit = { selectedState?.value = it }

        runComposableTest(renderer) {
            selectedState = mutableStateOf<String?>(null)
            Select<String>(
                selectedValue = selectedState!!,
                options = componentOptions
            )
        }

        assertEquals(true, renderer.renderSelectCalled)
        assertEquals(null, renderer.lastSelectSelectedValueRendered)
        assertNotNull(renderer.lastSelectOnSelectedChangeRendered)
        assertNotNull(renderer.lastSelectOptionsRendered)
        assertEquals(2, renderer.lastSelectOptionsRendered!!.size)

        assertEquals("val1", renderer.lastSelectOptionsRendered!![0].value)
        assertEquals("Label 1", renderer.lastSelectOptionsRendered!![0].label)
        assertEquals(false, renderer.lastSelectOptionsRendered!![0].disabled)
        assertEquals("val2", renderer.lastSelectOptionsRendered!![1].value)
        assertEquals("Label 2", renderer.lastSelectOptionsRendered!![1].label)
        assertEquals(true, renderer.lastSelectOptionsRendered!![1].disabled)

        assertNotNull(renderer.lastSelectModifierRendered)
        assertEquals(null, renderer.lastSelectModifierRendered?.attributes?.get("disabled"))
    }

    @Test
    fun testSelectWithInitialValue() {
        val renderer = MockPlatformRenderer()
        var selectedState: SummonMutableState<String?>? = null
        val componentOptions = listOf(SelectOption("v1", "L1"), SelectOption("v2", "L2"))

        runComposableTest(renderer) {
            selectedState = mutableStateOf<String?>("v2")
            Select(selectedState!!, componentOptions)
        }

        assertEquals(true, renderer.renderSelectCalled)
        assertEquals("v2", renderer.lastSelectSelectedValueRendered)
        assertEquals(componentOptions.size, renderer.lastSelectOptionsRendered?.size)
    }

    @Test
    fun testDisabledSelect() {
        val renderer = MockPlatformRenderer()
        var selectedState: SummonMutableState<Int?>? = null
        val componentOptions = listOf(SelectOption(1, "One"))

        runComposableTest(renderer) {
            selectedState = mutableStateOf<Int?>(1)
            Select(selectedState!!, componentOptions, disabled = true)
        }

        assertEquals(true, renderer.renderSelectCalled)
        assertNotNull(renderer.lastSelectModifierRendered)
        assertEquals("disabled", renderer.lastSelectModifierRendered?.attributes?.get("disabled"))
    }

    @Test
    fun testSelectWithLabel() {
        val renderer = MockPlatformRenderer()
        var selectedState: SummonMutableState<String?>? = null
        val componentOptions = listOf(SelectOption("opt1", "Option 1"))

        runComposableTest(renderer) {
            selectedState = mutableStateOf<String?>(null)
            Select(
                selectedValue = selectedState!!,
                options = componentOptions,
                label = "Choose an option"
            )
        }

        // Verify label was rendered
        assertEquals(true, renderer.renderLabelCalled)
        assertEquals("Choose an option", renderer.lastLabelTextRendered)
        assertNotNull(renderer.lastLabelModifierRendered)
        // Label should have a 'for' attribute linking to the select element
        assertNotNull(renderer.lastLabelForElementRendered)
    }

    @Test
    fun testSelectWithPlaceholder() {
        val renderer = MockPlatformRenderer()
        var selectedState: SummonMutableState<String?>? = null
        val componentOptions = listOf(SelectOption("a", "A"), SelectOption("b", "B"))

        runComposableTest(renderer) {
            selectedState = mutableStateOf<String?>(null)
            Select(
                selectedValue = selectedState!!,
                options = componentOptions,
                placeholder = "Select an item..."
            )
        }

        // The placeholder is typically added as the first option
        assertNotNull(renderer.lastSelectOptionsRendered)
        // The actual options plus the placeholder option
        assertEquals(3, renderer.lastSelectOptionsRendered!!.size)

        // First option should be the placeholder
        val placeholderOption = renderer.lastSelectOptionsRendered!![0]
        assertEquals(null, placeholderOption.value) // Null value for placeholder
        assertEquals("Select an item...", placeholderOption.label)
        assertEquals(true, placeholderOption.disabled) // Placeholder is typically disabled
    }

    @Test
    fun testSelectOnChangeCallback() {
        val renderer = MockPlatformRenderer()
        var selectedState: SummonMutableState<String?>? = null
        val componentOptions = listOf(SelectOption("x", "X"), SelectOption("y", "Y"))
        var changeCallbackValue: String? = null

        runComposableTest(renderer) {
            selectedState = mutableStateOf<String?>(null)
            Select(
                selectedValue = selectedState!!,
                options = componentOptions,
                onSelectedChange = { value ->
                    changeCallbackValue = value
                }
            )
        }

        // Simulate selecting an option
        assertNotNull(renderer.lastSelectOnSelectedChangeRendered)
        renderer.lastSelectOnSelectedChangeRendered!!("y")

        // Verify the callback was called with the correct value
        assertEquals("y", changeCallbackValue)
        // State should also be updated
        assertEquals("y", selectedState?.value)
    }

    @Test
    fun testSelectWithMultiple() {
        val renderer = MockPlatformRenderer()
        var selectedState: SummonMutableState<String?>? = null
        val componentOptions = listOf(SelectOption("1", "One"), SelectOption("2", "Two"))

        runComposableTest(renderer) {
            selectedState = mutableStateOf<String?>(null)
            Select(
                selectedValue = selectedState!!,
                options = componentOptions,
                multiple = true
            )
        }

        // Verify multiple attribute is set
        assertNotNull(renderer.lastSelectModifierRendered)
        assertEquals("true", renderer.lastSelectModifierRendered?.attributes?.get("multiple"))
    }

    @Test
    fun testSelectWithSize() {
        val renderer = MockPlatformRenderer()
        var selectedState: SummonMutableState<String?>? = null
        val componentOptions = listOf(
            SelectOption("a", "A"),
            SelectOption("b", "B"),
            SelectOption("c", "C")
        )

        runComposableTest(renderer) {
            selectedState = mutableStateOf<String?>(null)
            Select(
                selectedValue = selectedState!!,
                options = componentOptions,
                size = 3
            )
        }

        // Verify size attribute is set
        assertNotNull(renderer.lastSelectModifierRendered)
        assertEquals("3", renderer.lastSelectModifierRendered?.attributes?.get("size"))
    }

    @Test
    fun testSelectWithCustomModifier() {
        val renderer = MockPlatformRenderer()
        var selectedState: SummonMutableState<String?>? = null
        val componentOptions = listOf(SelectOption("opt", "Option"))

        runComposableTest(renderer) {
            selectedState = mutableStateOf<String?>(null)
            Select(
                selectedValue = selectedState!!,
                options = componentOptions,
                modifier = Modifier().className("custom-select").style("width", "200px")
            )
        }

        // Verify custom modifier is applied
        assertNotNull(renderer.lastSelectModifierRendered)
        assertEquals("custom-select", renderer.lastSelectModifierRendered?.attributes?.get("class"))
        assertEquals("width: 200px;", renderer.lastSelectModifierRendered?.toStyleString())
    }

    @Test
    fun testSelectWithAllOptions() {
        val renderer = MockPlatformRenderer()
        var selectedState: SummonMutableState<String?>? = null
        val componentOptions = listOf(
            SelectOption("opt1", "Option 1"),
            SelectOption("opt2", "Option 2", disabled = true),
            SelectOption("opt3", "Option 3")
        )
        var callbackValue: String? = null

        runComposableTest(renderer) {
            selectedState = mutableStateOf<String?>("opt3")
            Select(
                selectedValue = selectedState!!,
                options = componentOptions,
                disabled = false,
                label = "Select Field",
                placeholder = "Choose...",
                multiple = false,
                size = 5,
                onSelectedChange = { callbackValue = it },
                modifier = Modifier().id("my-select")
            )
        }

        // Verify all parameters were properly handled
        assertEquals(true, renderer.renderSelectCalled)
        assertEquals(true, renderer.renderLabelCalled)
        assertEquals("Select Field", renderer.lastLabelTextRendered)
        assertEquals("opt3", renderer.lastSelectSelectedValueRendered)
        assertEquals(4, renderer.lastSelectOptionsRendered?.size) // 3 options + placeholder
        assertEquals("5", renderer.lastSelectModifierRendered?.attributes?.get("size"))
        assertEquals("my-select", renderer.lastSelectModifierRendered?.attributes?.get("id"))

        // Test callback
        renderer.lastSelectOnSelectedChangeRendered?.invoke("opt1")
        assertEquals("opt1", callbackValue)
    }
}