package code.yousef.summon.integrations.quarkus.renderer

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.integrations.quarkus.htmx.HtmxAttributeHandler
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.FormContent
import code.yousef.summon.runtime.PlatformRenderer
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent

/**
 * A wrapper around PlatformRenderer that adds support for HTMX attributes and raw HTML content.
 * This class extends PlatformRenderer and delegates to a base PlatformRenderer for most operations,
 * but adds custom handling for HTMX attributes and raw HTML content.
 */
class EnhancedJvmPlatformRenderer : PlatformRenderer() {

    /**
     * Process a modifier to handle HTMX attributes and raw HTML content.
     * This method extracts HTMX attributes and raw HTML content from the modifier
     * and creates a new modifier with the appropriate attributes.
     *
     * @param modifier The modifier to process
     * @return A new modifier with processed attributes
     */
    private fun processModifier(modifier: Modifier): Modifier {
        // Check if the modifier contains HTMX attributes or raw HTML content
        val hasHtmxAttributes = modifier.styles.any { (key, _) ->
            key.startsWith(HtmxAttributeHandler.HTML_ATTRIBUTE_PREFIX) ||
                    HtmxAttributeHandler.isHtmxAttribute(key)
        }
        val hasRawHtml = modifier.styles.containsKey("__raw_html")

        // If the modifier doesn't contain HTMX attributes or raw HTML content, return it as is
        if (!hasHtmxAttributes && !hasRawHtml) {
            return modifier
        }

        // Create a new modifier with processed attributes
        var newModifier = Modifier()

        // Process HTMX attributes
        if (hasHtmxAttributes) {
            // Extract HTML attributes
            val htmlAttributes = mutableMapOf<String, String>()
            val regularStyles = mutableMapOf<String, String>()

            modifier.styles.forEach { (key, value) ->
                when {
                    key.startsWith(HtmxAttributeHandler.HTML_ATTRIBUTE_PREFIX) -> {
                        val attributeName = key.removePrefix(HtmxAttributeHandler.HTML_ATTRIBUTE_PREFIX)
                        htmlAttributes[attributeName] = value
                    }

                    HtmxAttributeHandler.isHtmxAttribute(key) -> {
                        htmlAttributes[key] = value
                    }

                    else -> {
                        regularStyles[key] = value
                    }
                }
            }

            // Create a new modifier with the regular styles
            newModifier = Modifier(regularStyles, htmlAttributes)
        }

        // Process raw HTML content
        if (hasRawHtml) {
            val rawHtml = modifier.styles["__raw_html"] ?: ""

            // Add a custom attribute to indicate raw HTML content
            newModifier = newModifier.copy(
                attributes = newModifier.attributes + ("data-raw-html" to rawHtml)
            )
        }

        return newModifier
    }

    // Override only the methods that exist in PlatformRenderer

    override fun renderText(text: String, modifier: Modifier) {
        super.renderText(text, processModifier(modifier))
    }

    override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {
        super.renderLabel(text, processModifier(modifier), forElement)
    }

    override fun renderButton(onClick: () -> Unit, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        super.renderButton(onClick, processModifier(modifier), content)
    }

    override fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, type: String) {
        super.renderTextField(value, onValueChange, processModifier(modifier), type)
    }

    override fun <T> renderSelect(
        selectedValue: T?,
        onSelectedChange: (T?) -> Unit,
        options: List<code.yousef.summon.runtime.SelectOption<T>>,
        modifier: Modifier
    ) {
        super.renderSelect(selectedValue, onSelectedChange, options, processModifier(modifier))
    }

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

    override fun renderRow(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        super.renderRow(processModifier(modifier), content)
    }

    override fun renderColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        super.renderColumn(processModifier(modifier), content)
    }

    override fun renderBox(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        super.renderBox(processModifier(modifier), content)
    }

    override fun renderImage(src: String, alt: String?, modifier: Modifier) {
        super.renderImage(src, alt, processModifier(modifier))
    }

    override fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: IconType
    ) {
        super.renderIcon(name, processModifier(modifier), onClick, svgContent, type)
    }

    override fun renderAlertContainer(
        variant: AlertVariant?,
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    ) {
        super.renderAlertContainer(variant, processModifier(modifier), content)
    }

    override fun renderBadge(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        super.renderBadge(processModifier(modifier), content)
    }

    override fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        super.renderCheckbox(checked, onCheckedChange, enabled, processModifier(modifier))
    }

    // renderProgress method doesn't exist in PlatformRenderer

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

    override fun renderForm(onSubmit: (() -> Unit)?, modifier: Modifier, content: @Composable FormContent.() -> Unit) {
        super.renderForm(onSubmit, processModifier(modifier), content)
    }

    // renderFormField method doesn't exist in PlatformRenderer

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

    override fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        super.renderSwitch(checked, onCheckedChange, enabled, processModifier(modifier))
    }

    override fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        is24Hour: Boolean,
        modifier: Modifier
    ) {
        super.renderTimePicker(value, onValueChange, enabled, is24Hour, processModifier(modifier))
    }

    override fun renderCard(modifier: Modifier, elevation: Int, content: @Composable () -> Unit) {
        super.renderCard(processModifier(modifier), elevation, content)
    }

    override fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit) {
        super.renderLink(processModifier(modifier), href, content)
    }

    // renderDiv and renderSpan methods don't exist in PlatformRenderer

    override fun renderDivider(modifier: Modifier) {
        super.renderDivider(processModifier(modifier))
    }

    override fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        super.renderResponsiveLayout(processModifier(modifier), content)
    }

    override fun renderHtmlTag(tagName: String, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        super.renderHtmlTag(tagName, processModifier(modifier), content)
    }

    override fun renderSnackbar(message: String, actionLabel: String?, onAction: (() -> Unit)?) {
        super.renderSnackbar(message, actionLabel, onAction)
    }
}