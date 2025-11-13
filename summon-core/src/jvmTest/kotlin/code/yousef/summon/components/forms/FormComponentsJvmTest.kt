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
                FormButton(text = "Save")
            }
        }

        assertTrue(html.contains("""<form"""), "form tag should be rendered")
        assertTrue(html.contains("""action="/admin/projects/upsert""""), "action attribute should match")
        assertTrue(html.contains("""type="hidden""""), "hidden input should exist")
        assertTrue(html.contains("""name="token""""), "hidden input name should be rendered")
        assertTrue(html.contains("""name="title""""), "text field should include name attribute")
        assertTrue(html.contains("""value="Sample Project""""), "default value should be serialized")
        assertTrue(html.contains("""type="submit""""), "submit button should be native")
    }
}
