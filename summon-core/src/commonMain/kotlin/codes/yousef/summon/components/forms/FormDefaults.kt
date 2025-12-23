package codes.yousef.summon.components.forms

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.theme.ColorHelpers
import codes.yousef.summon.theme.Spacing

/**
 * Enumerates supported native button types for form submissions.
 *
 * @property value Exact value applied to the `type` attribute.
 */
enum class FormButtonType(val value: String) {
    Submit("submit"),
    Reset("reset"),
    Button("button")
}

/**
 * Variants control the visual tone of [FormButton].
 */
enum class FormButtonVariant {
    Primary, Secondary, Danger
}

/**
 * Supported text field types rendered via `<input type="...">`.
 */
enum class FormTextFieldType(val htmlType: String) {
    Text("text"),
    Email("email"),
    Url("url"),
    Number("number"),
    Password("password"),
    Tel("tel")
}

/**
 * Represents a selectable option for [FormSelect].
 */
data class FormSelectOption(
    val value: String,
    val label: String,
    val disabled: Boolean = false
)

/**
 * Shared styling defaults used by the form components to match the portfolio aesthetic.
 */
object FormDefaults {
    private const val BORDER_RADIUS = "10px"
    private const val FONT_SIZE = "1rem"
    private const val LINE_HEIGHT = "1.5"
    private val focusColor get() = ColorHelpers.primary

    fun fieldContainerModifier(): Modifier =
        Modifier()
            .style("display", "flex")
            .style("flex-direction", "column")
            .style("gap", Spacing.sm)
            .style("margin-bottom", Spacing.md)

    fun labelModifier(): Modifier =
        Modifier()
            .style("font-weight", "600")
            .style("color", ColorHelpers.onSurface)

    fun requiredIndicatorModifier(): Modifier =
        Modifier()
            .style("color", ColorHelpers.error)
            .style("margin-left", "4px")
            .ariaAttribute("hidden", "true")

    fun supportingTextModifier(): Modifier =
        Modifier()
            .style("color", ColorHelpers.onSurfaceVariant)
            .style("font-size", "0.9rem")

    fun validationTextModifier(): Modifier =
        Modifier()
            .style("color", ColorHelpers.error)
            .style("font-size", "0.9rem")

    fun textInputModifier(): Modifier =
        Modifier()
            .width("100%")
            .padding(Spacing.padding(vertical = Spacing.sm, horizontal = Spacing.md))
            .border(width = "1px", style = "solid", color = ColorHelpers.border)
            .borderRadius(BORDER_RADIUS)
            .style("background-color", ColorHelpers.surface)
            .style("color", ColorHelpers.onSurface)
            .style("font-size", FONT_SIZE)
            .style("line-height", LINE_HEIGHT)
            .style("transition", "border-color 150ms ease, box-shadow 150ms ease")
            .style("outline-color", focusColor)
            .style("outline-offset", "3px")

    fun checkboxInputModifier(): Modifier =
        Modifier()
            .style("width", "20px")
            .style("height", "20px")
            .style("margin-right", Spacing.sm)
            .style("border", "1px solid ${ColorHelpers.border}")
            .style("border-radius", "4px")
            .style("background-color", ColorHelpers.surface)
            .style("outline-color", focusColor)
            .style("outline-offset", "3px")

    fun checkboxLabelModifier(): Modifier =
        Modifier()
            .style("font-weight", "500")
            .style("color", ColorHelpers.onSurface)

    fun buttonModifier(variant: FormButtonVariant): Modifier {
        val background = when (variant) {
            FormButtonVariant.Primary -> ColorHelpers.primary
            FormButtonVariant.Secondary -> ColorHelpers.secondary
            FormButtonVariant.Danger -> ColorHelpers.error
        }
        val textColor = when (variant) {
            FormButtonVariant.Secondary -> ColorHelpers.onSecondary
            FormButtonVariant.Danger -> ColorHelpers.onError
            else -> ColorHelpers.onPrimary
        }

        return Modifier()
            .padding(Spacing.padding(vertical = Spacing.sm, horizontal = Spacing.md))
            .style("border", "none")
            .borderRadius(BORDER_RADIUS)
            .style("font-size", "1rem")
            .style("font-weight", "600")
            .style("cursor", "pointer")
            .style("background-color", background)
            .style("color", textColor)
            .style("outline-color", focusColor)
            .style("outline-offset", "3px")
            .style("transition", "opacity 150ms ease")
    }
}

private val nonAlphaNumeric = Regex("[^a-z0-9]+")

internal fun slugify(value: String): String =
    value.lowercase()
        .replace(nonAlphaNumeric, "-")
        .trim('-')
        .ifEmpty { "field" }

@Composable
internal fun rememberFieldId(name: String, providedId: String?, suffix: String): String =
    remember(name, providedId, suffix) {
        (providedId?.takeIf { it.isNotBlank() } ?: "form-${slugify(name)}-$suffix")
    }
