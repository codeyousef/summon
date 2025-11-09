package code.yousef.summon.components.forms

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Label
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier

/**
 * Metadata returned to field content so inputs can wire accessibility relationships.
 */
data class FormFieldMetadata(
    val describedById: String?,
    val errorMessageId: String?
)

/**
 * Helper for composing a label, optional description, validation message, and the actual field.
 */
@Composable
fun FormFieldGroup(
    label: String,
    fieldId: String,
    modifier: Modifier = Modifier(),
    required: Boolean = false,
    description: String? = null,
    validationMessage: String? = null,
    content: @Composable (FormFieldMetadata) -> Unit
) {
    val descriptionId = description?.let { "${fieldId}-description" }
    val errorId = validationMessage?.let { "${fieldId}-error" }

    Column(
        modifier = FormDefaults.fieldContainerModifier()
            .then(modifier)
    ) {
        Row {
            Label(
                text = label,
                modifier = FormDefaults.labelModifier(),
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
            descModifier = descModifier.id(descriptionId!!)
            Text(text = description, modifier = descModifier)
        }

        content(FormFieldMetadata(descriptionId, errorId))

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
