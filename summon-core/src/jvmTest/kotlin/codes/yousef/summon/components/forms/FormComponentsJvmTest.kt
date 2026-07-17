package codes.yousef.summon.components.forms

import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertTrue

class FormComponentsJvmTest {

    @Test
    fun formRendersNativeInputsForSsr() {
        val renderer = PlatformRenderer()

        val html = renderer.renderComposableRoot {
            Form(
                action = "/admin/projects/upsert",
                hiddenFields = listOf(FormHiddenField("token", "abc123"))
            ) {
                FormTextField(
                    name = "title",
                    label = "Title",
                    defaultValue = "Sample Project",
                    required = true
                )
                FormTextField(
                    name = "dueDate",
                    label = "Due date",
                    defaultValue = "2026-07-17",
                    type = FormTextFieldType.Date
                )
                FormTextField(
                    name = "amount",
                    label = "Amount",
                    defaultValue = "12.50",
                    type = FormTextFieldType.Number,
                    step = 0.01
                )
                FormRadioGroup(
                    name = "mode",
                    label = "Mode",
                    options = listOf(
                        FormRadioOption("set", "Set"),
                        FormRadioOption("shift", "Shift")
                    ),
                    selectedValue = "set"
                )
                FormButton(text = "Save", name = "action", value = "apply", formId = "detached-form")
            }
        }

        assertTrue(html.contains("""<form"""), "form tag should be rendered")
        assertTrue(html.contains("""action="/admin/projects/upsert""""), "action attribute should match")
        assertTrue(html.contains("""type="hidden""""), "hidden input should exist")
        assertTrue(html.contains("""name="token""""), "hidden input name should be rendered")
        assertTrue(html.contains("""name="title""""), "text field should include name attribute")
        assertTrue(html.contains("""value="Sample Project""""), "default value should be serialized")
        assertTrue(html.contains("""type="submit""""), "submit button should be native")
        assertTrue(html.contains("""type="date""""), "date field should use the native input type")
        assertTrue(html.contains("""step="0.01""""), "numeric step should be serialized")
        assertTrue(html.contains("""type="radio""""), "radio group should use native inputs")
        assertTrue(html.contains("""name="action""""), "submit button name should be serialized")
        assertTrue(html.contains("""value="apply""""), "submit button value should be serialized")
        assertTrue(html.contains("""form="detached-form""""), "associated form id should be serialized")
    }
}
