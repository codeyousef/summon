package code.yousef.summon.components.input

// Import the PlatformRenderer's SelectOption, this is the one the interface method uses
// Import the component's SelectOption for creating the options list passed TO the component
// Import other types used in PlatformRenderer methods for the mock
import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.util.runTestComposable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import code.yousef.summon.components.input.SelectOption as ComponentSelectOption
import code.yousef.summon.runtime.SelectOption as RendererSelectOption

class SelectTest {

    @Test
    fun testBasicSelectRendering() {
        val renderer = MockPlatformRenderer()
        var selectedState: SummonMutableState<String?>? = null
        val componentOptions = listOf(
            ComponentSelectOption("val1", "Label 1"),
            ComponentSelectOption("val2", "Label 2", disabled = true)
        )
        val onSelectChange: (String?) -> Unit = { selectedState?.value = it }

        runTestComposable(renderer) {
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
        assertEquals(null, renderer.lastSelectModifierRendered?.styles?.get("__attr:disabled"))
    }

    @Test
    fun testSelectWithInitialValue() {
        val renderer = MockPlatformRenderer()
        var selectedState: SummonMutableState<String?>? = null
        val componentOptions = listOf(ComponentSelectOption("v1", "L1"), ComponentSelectOption("v2", "L2"))

        runTestComposable(renderer) {
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
        val componentOptions = listOf(ComponentSelectOption(1, "One"))

        runTestComposable(renderer) {
            selectedState = mutableStateOf<Int?>(1)
            Select(selectedState!!, componentOptions, disabled = true)
        }

        assertEquals(true, renderer.renderSelectCalled)
        assertNotNull(renderer.lastSelectModifierRendered)
        assertEquals("disabled", renderer.lastSelectModifierRendered?.styles?.get("__attr:disabled"))
    }

    // TODO: Test other parameters like multiple, size, label, placeholder
    // TODO: Test interaction with onSelectedChange callback
    // TODO: Test validation (might be tricky if SelectState isn't used directly by rendering)
}