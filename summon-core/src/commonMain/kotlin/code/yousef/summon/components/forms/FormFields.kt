package code.yousef.summon.components.forms

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Label
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.NativeSelectOption
import code.yousef.summon.theme.Spacing

/**
 * Server-friendly text input that renders as `<input>` with the provided defaults.
 */
@Composable
fun FormTextField(
    name: String,
    label: String,
    defaultValue: String = "",
    placeholder: String? = null,
    description: String? = null,
    validationMessage: String? = null,
    required: Boolean = false,
    type: FormTextFieldType = FormTextFieldType.Text,
    autoComplete: String? = null,
    modifier: Modifier = Modifier(),
    fieldModifier: Modifier = Modifier(),
    id: String? = null
) {
    val renderer = LocalPlatformRenderer.current
    val fieldId = rememberFieldId(name, id, "input")

    FormFieldGroup(
        label = label,
        fieldId = fieldId,
        modifier = modifier,
        required = required,
        description = description,
        validationMessage = validationMessage
    ) { metadata ->
        var inputModifier = FormDefaults.textInputModifier()
            .then(fieldModifier)
            .id(fieldId)
            .attribute("name", name)

        placeholder?.let { inputModifier = inputModifier.attribute("placeholder", it) }
        if (required) inputModifier = inputModifier.attribute("required", "true")
        autoComplete?.let { inputModifier = inputModifier.attribute("autocomplete", it) }
        metadata.describedById?.let { inputModifier = inputModifier.ariaAttribute("describedby", it) }
        metadata.errorMessageId?.let { inputModifier = inputModifier.ariaAttribute("errormessage", it) }

        renderer.renderNativeInput(
            type = type.htmlType,
            modifier = inputModifier,
            value = defaultValue
        )
    }
}

/**
 * Multi-line text input rendered via `<textarea>`.
 */
@Composable
fun FormTextArea(
    name: String,
    label: String,
    defaultValue: String = "",
    rows: Int? = null,
    maxLength: Int? = null,
    description: String? = null,
    validationMessage: String? = null,
    required: Boolean = false,
    modifier: Modifier = Modifier(),
    fieldModifier: Modifier = Modifier(),
    id: String? = null
) {
    val renderer = LocalPlatformRenderer.current
    val fieldId = rememberFieldId(name, id, "textarea")

    FormFieldGroup(
        label = label,
        fieldId = fieldId,
        modifier = modifier,
        required = required,
        description = description,
        validationMessage = validationMessage
    ) { metadata ->
        var textAreaModifier = FormDefaults.textInputModifier()
            .then(fieldModifier)
            .id(fieldId)
            .attribute("name", name)

        rows?.let { textAreaModifier = textAreaModifier.attribute("rows", it.toString()) }
        maxLength?.let { textAreaModifier = textAreaModifier.attribute("maxlength", it.toString()) }
        if (required) textAreaModifier = textAreaModifier.attribute("required", "true")
        metadata.describedById?.let { textAreaModifier = textAreaModifier.ariaAttribute("describedby", it) }
        metadata.errorMessageId?.let { textAreaModifier = textAreaModifier.ariaAttribute("errormessage", it) }

        renderer.renderNativeTextarea(
            modifier = textAreaModifier,
            value = defaultValue
        )
    }
}

/**
 * Select input that renders semantic `<select>` markup.
 */
@Composable
fun FormSelect(
    name: String,
    label: String,
    options: List<FormSelectOption>,
    selectedValue: String? = null,
    placeholder: String? = null,
    description: String? = null,
    validationMessage: String? = null,
    required: Boolean = false,
    modifier: Modifier = Modifier(),
    fieldModifier: Modifier = Modifier(),
    id: String? = null
) {
    val renderer = LocalPlatformRenderer.current
    val fieldId = rememberFieldId(name, id, "select")

    val nativeOptions = buildList {
        if (!placeholder.isNullOrBlank()) {
            add(
                NativeSelectOption(
                    value = "",
                    label = placeholder,
                    isSelected = selectedValue.isNullOrBlank(),
                    isDisabled = true,
                    isPlaceholder = true
                )
            )
        }
        options.forEach { option ->
            add(
                NativeSelectOption(
                    value = option.value,
                    label = option.label,
                    isDisabled = option.disabled,
                    isSelected = option.value == selectedValue
                )
            )
        }
    }

    FormFieldGroup(
        label = label,
        fieldId = fieldId,
        modifier = modifier,
        required = required,
        description = description,
        validationMessage = validationMessage
    ) { metadata ->
        var selectModifier = FormDefaults.textInputModifier()
            .then(fieldModifier)
            .id(fieldId)
            .attribute("name", name)

        if (required) selectModifier = selectModifier.attribute("required", "true")
        metadata.describedById?.let { selectModifier = selectModifier.ariaAttribute("describedby", it) }
        metadata.errorMessageId?.let { selectModifier = selectModifier.ariaAttribute("errormessage", it) }

        renderer.renderNativeSelect(
            modifier = selectModifier,
            options = nativeOptions
        )
    }
}

/**
 * Accessible checkbox with inline label and optional helper text.
 */
@Composable
fun FormCheckbox(
    name: String,
    label: String,
    value: String = "on",
    checked: Boolean = false,
    description: String? = null,
    validationMessage: String? = null,
    required: Boolean = false,
    modifier: Modifier = Modifier(),
    checkboxModifier: Modifier = Modifier(),
    id: String? = null
) {
    val renderer = LocalPlatformRenderer.current
    val fieldId = rememberFieldId(name, id, "checkbox")
    val descriptionId = description?.takeIf { it.isNotBlank() }?.let { "$fieldId-description" }
    val errorId = validationMessage?.takeIf { it.isNotBlank() }?.let { "$fieldId-error" }

    Column(
        modifier = FormDefaults.fieldContainerModifier()
            .then(modifier)
    ) {
        Row(
            modifier = Modifier()
                .style("align-items", "center")
                .style("gap", Spacing.xs)
        ) {
            var inputModifier = FormDefaults.checkboxInputModifier()
                .then(checkboxModifier)
                .id(fieldId)
                .attribute("name", name)
                .attribute("value", value)

            if (required) inputModifier = inputModifier.attribute("required", "true")
            descriptionId?.let { inputModifier = inputModifier.ariaAttribute("describedby", it) }
            errorId?.let { inputModifier = inputModifier.ariaAttribute("errormessage", it) }

            renderer.renderNativeInput(
                type = "checkbox",
                modifier = inputModifier,
                value = value,
                isChecked = checked
            )

            Label(
                text = label,
                modifier = FormDefaults.checkboxLabelModifier(),
                forElement = fieldId
            )
            if (required) {
                Text(
                    text = "*",
                    modifier = FormDefaults.requiredIndicatorModifier()
                )
            }
        }

        if (!description.isNullOrBlank()) {
            var descModifier = FormDefaults.supportingTextModifier()
            descModifier = descriptionId?.let { descModifier.id(it) } ?: descModifier
            Text(text = description, modifier = descModifier)
        }

        if (!validationMessage.isNullOrBlank()) {
            var validationModifier = FormDefaults.validationTextModifier()
            validationModifier = errorId?.let { validationModifier.id(it) } ?: validationModifier
            validationModifier = validationModifier
                .role("status")
                .ariaAttribute("live", "polite")
            Text(text = validationMessage, modifier = validationModifier)
        }
    }
}

/**
 * Semantic button that triggers native form submission or reset behaviour.
 */
@Composable
fun FormButton(
    text: String,
    type: FormButtonType = FormButtonType.Submit,
    variant: FormButtonVariant = FormButtonVariant.Primary,
    modifier: Modifier = Modifier(),
    fullWidth: Boolean = true,
    ariaLabel: String? = null
) {
    val renderer = LocalPlatformRenderer.current
    var buttonModifier = FormDefaults.buttonModifier(variant)
        .then(modifier)

    if (fullWidth) {
        buttonModifier = buttonModifier.width("100%")
    }
    if (!ariaLabel.isNullOrBlank()) {
        buttonModifier = buttonModifier.ariaAttribute("label", ariaLabel)
    }

    renderer.renderNativeButton(
        type = type.value,
        modifier = buttonModifier
    ) {
        Text(text = text)
    }
}
