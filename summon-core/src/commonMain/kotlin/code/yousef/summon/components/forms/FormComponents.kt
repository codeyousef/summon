package codes.yousef.summon.components.forms

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.getPlatformRenderer

/**
 * Describes how a form submission should be performed.
 *
 * @property value Serialized HTTP method value rendered on the `<form>` element.
 */
enum class FormMethod(val value: String) {
    Get("get"),
    Post("post")
}

/**
 * Supported form encoding types.
 *
 * @property value Value assigned to the `enctype` attribute when rendering the `<form>`.
 */
enum class FormEncType(val value: String) {
    UrlEncoded("application/x-www-form-urlencoded"),
    Multipart("multipart/form-data"),
    TextPlain("text/plain")
}

/**
 * Represents a hidden input that should be emitted automatically for a form.
 *
+ * @property name Field name submitted with the request.
 * @property value Field value submitted with the request.
 */
data class FormHiddenField(
    val name: String,
    val value: String
)

/**
 * Emits a semantic `<form>` element with server-friendly attributes and slot content.
 *
 * The composable only relies on native browser submission, so no JavaScript hooks or hydration
 * assumptions are made. Hidden fields can be passed via [hiddenFields] to avoid re-declaring them in
 * every call site.
 *
 * @param action Target URL for the submission (required).
 * @param method HTTP method, defaults to POST.
 * @param encType Optional encoding type for file uploads or custom parsing.
 * @param hiddenFields Convenience collection of `<input type="hidden">` values to render first.
 * @param modifier Styling and accessibility modifiers applied to the `<form>`.
 * @param content Declarative body of the form (inputs, buttons, etc.).
 */
@Composable
fun Form(
    action: String,
    method: FormMethod = FormMethod.Post,
    encType: FormEncType? = null,
    hiddenFields: List<FormHiddenField> = emptyList(),
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    var resolvedModifier = modifier
        .attribute("action", action)
        .attribute("method", method.value)

    if (encType != null) {
        resolvedModifier = resolvedModifier.attribute("enctype", encType.value)
    }

    getPlatformRenderer().renderForm(onSubmit = null, modifier = resolvedModifier) {
        hiddenFields.forEach { hidden ->
            HiddenField(name = hidden.name, value = hidden.value)
        }
        content()
    }
}

@Composable
private fun HiddenField(name: String, value: String) {
    val renderer = LocalPlatformRenderer.current
    val hiddenModifier = Modifier().attribute("name", name)
    renderer.renderNativeInput(
        type = "hidden",
        modifier = hiddenModifier,
        value = value
    )
}
