package codes.yousef.summon.integration.quarkus.renderer

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.IconType
import codes.yousef.summon.components.foundation.TrustedSvg
import codes.yousef.summon.components.feedback.AlertVariant
import codes.yousef.summon.components.input.FileInfo
import codes.yousef.summon.core.FlowContentCompat
import codes.yousef.summon.integration.quarkus.htmx.HtmxAttributeHandler
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.FormContent
import codes.yousef.summon.runtime.NativeSelectOption
import codes.yousef.summon.runtime.PlatformRenderer
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * A wrapper around PlatformRenderer that adds support for HTMX attributes and raw HTML content.
 * This class extends PlatformRenderer and delegates to a base PlatformRenderer for most operations,
 * but adds custom handling for HTMX attributes and raw HTML content.
 */
class EnhancedJvmPlatformRenderer : PlatformRenderer() {

    /**
     * Moves explicit and HTMX attributes out of the style map while preserving every ordinary
     * modifier channel. Trusted raw markup is rendered only through `renderRawHtml`.
     */
    private fun processModifier(modifier: Modifier): Modifier {
        val hasHtmxAttributes = modifier.styles.keys.any { key ->
            key.startsWith(HtmxAttributeHandler.HTML_ATTRIBUTE_PREFIX) ||
                HtmxAttributeHandler.isHtmxAttribute(key)
        }
        if (!hasHtmxAttributes) return modifier

        val htmlAttributes = mutableMapOf<String, String>()
        val regularStyles = mutableMapOf<String, String>()
        modifier.styles.forEach { (key, value) ->
            when {
                key.startsWith(HtmxAttributeHandler.HTML_ATTRIBUTE_PREFIX) ->
                    htmlAttributes[key.removePrefix(HtmxAttributeHandler.HTML_ATTRIBUTE_PREFIX)] = value

                HtmxAttributeHandler.isHtmxAttribute(key) -> htmlAttributes[key] = value
                else -> regularStyles[key] = value
            }
        }
        return ModifierImpl(
            styles = regularStyles,
            attributes = modifier.attributes + htmlAttributes,
            eventHandlers = modifier.eventHandlers,
            complexEventHandlers = modifier.complexEventHandlers,
            pseudoElements = modifier.pseudoElements
        )
    }

    // Override only the methods that exist in PlatformRenderer

    /**
     * Renders text.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    override fun renderText(text: String, modifier: Modifier) {
        super.renderText(text, processModifier(modifier))
    }

    /**
     * Renders label.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     * @param forElement The for element value.
     */
    override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {
        super.renderLabel(text, processModifier(modifier), forElement)
    }

    /**
     * Renders button.
     *
     * @param onClick Callback invoked when click.
     * @param modifier Styles and attributes applied to the rendered element.
     * @param content Composable content emitted by this API.
     */
    override fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        super.renderButton(onClick, processModifier(modifier), content)
    }

    /**
     * Renders text field.
     *
     * @param value Value to process.
     * @param onValueChange Callback invoked when value change.
     * @param modifier Styles and attributes applied to the rendered element.
     * @param type The type value.
     */
    override fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, type: String) {
        super.renderTextField(value, onValueChange, processModifier(modifier), type)
    }

    /**
     * Renders select.
     *
     * @param selectedValue The selected value value.
     * @param onSelectedChange Callback invoked when selected change.
     * @param options The options value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    override fun <T> renderSelect(
        selectedValue: T?,
        onSelectedChange: (T?) -> Unit,
        options: List<codes.yousef.summon.runtime.SelectOption<T>>,
        modifier: Modifier
    ) {
        super.renderSelect(selectedValue, onSelectedChange, options, processModifier(modifier))
    }

    /**
     * Renders date picker.
     *
     * @param value Value to process.
     * @param onValueChange Callback invoked when value change.
     * @param enabled Whether the behavior is enabled.
     * @param min The min value.
     * @param max The max value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    override fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        min: LocalDate?,
        max: LocalDate?,
        modifier: Modifier
    ) {
        super.renderDatePicker(value, onValueChange, enabled, min, max, processModifier(modifier))
    }

    /**
     * Renders text area.
     *
     * @param value Value to process.
     * @param onValueChange Callback invoked when value change.
     * @param enabled Whether the behavior is enabled.
     * @param readOnly The read only value.
     * @param rows The rows value.
     * @param maxLength The max length value.
     * @param placeholder The placeholder value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
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
        super.renderTextArea(
            value,
            onValueChange,
            enabled,
            readOnly,
            rows,
            maxLength,
            placeholder,
            processModifier(modifier)
        )
    }

    /**
     * Renders row.
     *
     * @param modifier Styles and attributes applied to the rendered element.
     * @param content Composable content emitted by this API.
     */
    override fun renderRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        super.renderRow(processModifier(modifier), content)
    }

    /**
     * Renders column.
     *
     * @param modifier Styles and attributes applied to the rendered element.
     * @param content Composable content emitted by this API.
     */
    override fun renderColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        super.renderColumn(processModifier(modifier), content)
    }

    /**
     * Renders box.
     *
     * @param modifier Styles and attributes applied to the rendered element.
     * @param content Composable content emitted by this API.
     */
    override fun renderBox(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        super.renderBox(processModifier(modifier), content)
    }

    /**
     * Renders image.
     *
     * @param src The src value.
     * @param alt The alt value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    override fun renderImage(src: String, alt: String?, modifier: Modifier) {
        super.renderImage(src, alt, processModifier(modifier))
    }

    /**
     * Renders icon.
     *
     * @param name Human-readable name.
     * @param modifier Styles and attributes applied to the rendered element.
     * @param onClick Callback invoked when click.
     * @param svgContent The svg content value.
     * @param type The type value.
     */
    override fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: TrustedSvg?,
        type: IconType
    ) {
        super.renderIcon(name, processModifier(modifier), onClick, svgContent, type)
    }

    /**
     * Renders alert container.
     *
     * @param variant The variant value.
     * @param modifier Styles and attributes applied to the rendered element.
     * @param content Composable content emitted by this API.
     */
    override fun renderAlertContainer(
        variant: AlertVariant?,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        super.renderAlertContainer(variant, processModifier(modifier), content)
    }

    /**
     * Renders badge.
     *
     * @param modifier Styles and attributes applied to the rendered element.
     * @param content Composable content emitted by this API.
     */
    override fun renderBadge(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        super.renderBadge(processModifier(modifier), content)
    }

    /**
     * Renders checkbox.
     *
     * @param checked The checked value.
     * @param onCheckedChange Callback invoked when checked change.
     * @param enabled Whether the behavior is enabled.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    override fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        super.renderCheckbox(checked, onCheckedChange, enabled, processModifier(modifier))
    }

    // renderProgress method doesn't exist in PlatformRenderer

    /**
     * Renders file upload.
     *
     * @param onFilesSelected Callback invoked when files selected.
     * @param accept The accept value.
     * @param multiple The multiple value.
     * @param enabled Whether the behavior is enabled.
     * @param capture The capture value.
     * @param modifier Styles and attributes applied to the rendered element.
     * @return The resulting value.
     */
    override fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit {
        return super.renderFileUpload(onFilesSelected, accept, multiple, enabled, capture, processModifier(modifier))
    }

    /**
     * Renders form.
     *
     * @param onSubmit Callback invoked when submit.
     * @param modifier Styles and attributes applied to the rendered element.
     * @param content Composable content emitted by this API.
     */
    override fun renderForm(onSubmit: (() -> Unit)?, modifier: Modifier, content: @Composable FormContent.() -> Unit) {
        super.renderForm(onSubmit, processModifier(modifier), content)
    }

    /**
     * Renders form field.
     *
     * @param modifier Styles and attributes applied to the rendered element.
     * @param labelId The label id value.
     * @param isRequired The is required value.
     * @param isError The is error value.
     * @param errorMessageId The error message id value.
     * @param content Composable content emitted by this API.
     */
    override fun renderFormField(
        modifier: Modifier,
        labelId: String?,
        isRequired: Boolean,
        isError: Boolean,
        errorMessageId: String?,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        super.renderFormField(
            processModifier(modifier),
            labelId,
            isRequired,
            isError,
            errorMessageId,
            content
        )
    }

    /**
     * Renders native input.
     *
     * @param type The type value.
     * @param modifier Styles and attributes applied to the rendered element.
     * @param value Value to process.
     * @param isChecked The is checked value.
     */
    override fun renderNativeInput(
        type: String,
        modifier: Modifier,
        value: String?,
        isChecked: Boolean?
    ) {
        super.renderNativeInput(type, processModifier(modifier), value, isChecked)
    }

    /**
     * Renders native textarea.
     *
     * @param modifier Styles and attributes applied to the rendered element.
     * @param value Value to process.
     */
    override fun renderNativeTextarea(modifier: Modifier, value: String?) {
        super.renderNativeTextarea(processModifier(modifier), value)
    }

    /**
     * Renders native select.
     *
     * @param modifier Styles and attributes applied to the rendered element.
     * @param options The options value.
     */
    override fun renderNativeSelect(modifier: Modifier, options: List<NativeSelectOption>) {
        super.renderNativeSelect(processModifier(modifier), options)
    }

    /**
     * Renders native button.
     *
     * @param type The type value.
     * @param modifier Styles and attributes applied to the rendered element.
     * @param content Composable content emitted by this API.
     */
    override fun renderNativeButton(
        type: String,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        super.renderNativeButton(type, processModifier(modifier), content)
    }

    /**
     * Renders radio button.
     *
     * @param checked The checked value.
     * @param onCheckedChange Callback invoked when checked change.
     * @param label The label value.
     * @param enabled Whether the behavior is enabled.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    override fun renderRadioButton(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        label: String?,
        enabled: Boolean,
        modifier: Modifier
    ) {
        super.renderRadioButton(checked, onCheckedChange, label, enabled, processModifier(modifier))
    }

    // renderSpacer method doesn't exist in PlatformRenderer

    /**
     * Renders range slider.
     *
     * @param value Value to process.
     * @param onValueChange Callback invoked when value change.
     * @param valueRange The value range value.
     * @param steps The steps value.
     * @param enabled Whether the behavior is enabled.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    override fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        super.renderRangeSlider(value, onValueChange, valueRange, steps, enabled, processModifier(modifier))
    }

    /**
     * Renders slider.
     *
     * @param value Value to process.
     * @param onValueChange Callback invoked when value change.
     * @param valueRange The value range value.
     * @param steps The steps value.
     * @param enabled Whether the behavior is enabled.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    override fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        super.renderSlider(value, onValueChange, valueRange, steps, enabled, processModifier(modifier))
    }

    /**
     * Renders switch.
     *
     * @param checked The checked value.
     * @param onCheckedChange Callback invoked when checked change.
     * @param enabled Whether the behavior is enabled.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    override fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        super.renderSwitch(checked, onCheckedChange, enabled, processModifier(modifier))
    }

    /**
     * Renders time picker.
     *
     * @param value Value to process.
     * @param onValueChange Callback invoked when value change.
     * @param enabled Whether the behavior is enabled.
     * @param is24Hour The is24 hour value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    override fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        is24Hour: Boolean,
        modifier: Modifier
    ) {
        super.renderTimePicker(value, onValueChange, enabled, is24Hour, processModifier(modifier))
    }

    /**
     * Renders card.
     *
     * @param modifier Styles and attributes applied to the rendered element.
     * @param elevation The elevation value.
     * @param content Composable content emitted by this API.
     */
    override fun renderCard(modifier: Modifier, elevation: Int, content: @Composable () -> Unit) {
        super.renderCard(processModifier(modifier), elevation, content)
    }

    /**
     * Renders link.
     *
     * @param modifier Styles and attributes applied to the rendered element.
     * @param href The href value.
     * @param content Composable content emitted by this API.
     */
    override fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit) {
        super.renderLink(processModifier(modifier), href, content)
    }

    // renderDiv and renderSpan methods don't exist in PlatformRenderer

    /**
     * Renders divider.
     *
     * @param modifier Styles and attributes applied to the rendered element.
     */
    override fun renderDivider(modifier: Modifier) {
        super.renderDivider(processModifier(modifier))
    }

    /**
     * Renders responsive layout.
     *
     * @param modifier Styles and attributes applied to the rendered element.
     * @param content Composable content emitted by this API.
     */
    override fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        super.renderResponsiveLayout(processModifier(modifier), content)
    }

    /**
     * Renders HTML tag.
     *
     * @param tagName The tag name value.
     * @param modifier Styles and attributes applied to the rendered element.
     * @param content Composable content emitted by this API.
     */
    override fun renderHtmlTag(tagName: String, modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        super.renderHtmlTag(tagName, processModifier(modifier), content)
    }

    /**
     * Renders snackbar.
     *
     * @param message Message content.
     * @param actionLabel The action label value.
     * @param onAction Callback invoked when action.
     */
    override fun renderSnackbar(message: String, actionLabel: String?, onAction: (() -> Unit)?) {
        super.renderSnackbar(message, actionLabel, onAction)
    }
}
