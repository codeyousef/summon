package codes.yousef.summon.components.forms

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.id
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FormFieldsRenderingTest {
    @Test
    fun completeServerFormRendersSubmissionValidationAndAccessibilityMetadata() {
        val html = PlatformRenderer().renderComposableRoot {
            Form(
                action = "/submit",
                method = FormMethod.Post,
                encType = FormEncType.Multipart,
                hiddenFields = listOf(FormHiddenField("csrf", "token")),
                modifier = Modifier().id("profile")
            ) {
                FormTextField(
                    name = "email",
                    label = "Email",
                    defaultValue = "person@example.test",
                    placeholder = "name@example.test",
                    description = "Public contact",
                    validationMessage = "Invalid email",
                    required = true,
                    type = FormTextFieldType.Email,
                    autoComplete = "email",
                    maxLength = 120,
                    step = 2,
                    id = "email-id"
                )
                FormTextArea(
                    name = "bio",
                    label = "Biography",
                    defaultValue = "About",
                    rows = 6,
                    maxLength = 500,
                    description = "Short biography",
                    validationMessage = "Too long",
                    required = true,
                    id = "bio-id"
                )
                FormSelect(
                    name = "role",
                    label = "Role",
                    options = listOf(
                        FormSelectOption("reader", "Reader"),
                        FormSelectOption("owner", "Owner", disabled = true)
                    ),
                    selectedValue = "owner",
                    placeholder = "Choose",
                    description = "Account role",
                    validationMessage = "Choose a role",
                    required = true,
                    id = "role-id"
                )
                FormCheckbox(
                    name = "terms",
                    label = "Accept terms",
                    value = "accepted",
                    checked = true,
                    description = "Required agreement",
                    validationMessage = "Required",
                    required = true,
                    id = "terms-id"
                )
                FormRadioGroup(
                    name = "plan",
                    label = "Plan",
                    options = listOf(
                        FormRadioOption("free", "Free"),
                        FormRadioOption("paid", "Paid", disabled = true)
                    ),
                    selectedValue = "paid",
                    required = true,
                    id = "plan-id"
                )
                FormButton(
                    text = "Save",
                    type = FormButtonType.Submit,
                    variant = FormButtonVariant.Primary,
                    fullWidth = true,
                    ariaLabel = "Save profile",
                    name = "intent",
                    value = "save",
                    formId = "profile"
                )
                FormButton("Reset", FormButtonType.Reset, FormButtonVariant.Secondary, fullWidth = false)
                FormButton("Delete", FormButtonType.Button, FormButtonVariant.Danger, fullWidth = false)
            }
        }

        assertContains(html, "action=\"/submit\"")
        assertContains(html, "enctype=\"multipart/form-data\"")
        assertContains(html, "type=\"hidden\"")
        assertContains(html, "maxlength=\"120\"")
        assertContains(html, "step=\"2\"")
        assertContains(html, "aria-invalid=\"true\"")
        assertContains(html, "aria-describedby")
        assertContains(html, "selected=\"selected\"")
        assertTrue(Regex("""<input[^>]*type="checkbox"[^>]*checked""").containsMatchIn(html))
        assertTrue(Regex("""<input(?=[^>]*type="radio")(?=[^>]*disabled)[^>]*>""").containsMatchIn(html))
        assertContains(html, "form=\"profile\"")
        assertContains(html, "Save profile")
    }

    @Test
    fun minimalFieldsOmitOptionalMetadataAndCoverAllTextInputTypes() {
        val html = PlatformRenderer().renderComposableRoot {
            Form("/search", FormMethod.Get) {
                FormTextField("text", "Text")
                FormTextField("url", "URL", type = FormTextFieldType.Url)
                FormTextField("password", "Password", type = FormTextFieldType.Password)
                FormTextField("number", "Number", type = FormTextFieldType.Number)
                FormTextField("tel", "Telephone", type = FormTextFieldType.Tel)
                FormTextArea("notes", "Notes")
                FormSelect("empty", "Empty", emptyList())
                FormSelect("one", "One", listOf(FormSelectOption("1", "One")), selectedValue = "missing")
                FormCheckbox("optional", "Optional")
                FormRadioGroup("none", "None", listOf(FormRadioOption("only", "Only")))
                FormButton("Submit")
            }
        }

        assertContains(html, "method=\"get\"")
        assertContains(html, "type=\"url\"")
        assertContains(html, "type=\"password\"")
        assertContains(html, "type=\"number\"")
        assertContains(html, "type=\"tel\"")
        assertFalse(html.contains("enctype="))
        assertContains(html, "Optional")
    }

    @Test
    fun radioGroupsRejectMissingAndDuplicateSubmissionValues() {
        assertFailsWith<IllegalArgumentException> {
            PlatformRenderer().renderComposableRoot { FormRadioGroup("empty", "Empty", emptyList()) }
        }
        assertFailsWith<IllegalArgumentException> {
            PlatformRenderer().renderComposableRoot {
                FormRadioGroup(
                    "duplicate",
                    "Duplicate",
                    listOf(FormRadioOption("same", "One"), FormRadioOption("same", "Two"))
                )
            }
        }
    }
}
