package code.yousef.summon.integrations.quarkus.renderer

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.input.SelectOption
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.integrations.quarkus.htmx.HtmxAttributeHandler
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.FormContent
import code.yousef.summon.runtime.JvmPlatformRenderer
import code.yousef.summon.runtime.PlatformRenderer
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * A wrapper around JvmPlatformRenderer that adds support for HTMX attributes and raw HTML content.
 * This class implements the PlatformRenderer interface and delegates to JvmPlatformRenderer for most operations,
 * but adds custom handling for HTMX attributes and raw HTML content.
 */
class EnhancedJvmPlatformRenderer : PlatformRenderer {
    private val delegate = JvmPlatformRenderer()
    
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
            val htmlAttributes = modifier.styles.filter { (key, _) ->
                key.startsWith(HtmxAttributeHandler.HTML_ATTRIBUTE_PREFIX) || 
                HtmxAttributeHandler.isHtmxAttribute(key)
            }
            
            // Add HTML attributes to the new modifier with a special prefix
            htmlAttributes.forEach { (key, value) ->
                val attributeName = if (key.startsWith(HtmxAttributeHandler.HTML_ATTRIBUTE_PREFIX)) {
                    key.removePrefix(HtmxAttributeHandler.HTML_ATTRIBUTE_PREFIX)
                } else {
                    key
                }
                newModifier = newModifier.style("__html_attr_$attributeName", value)
            }
        }
        
        // Process raw HTML content
        if (hasRawHtml) {
            newModifier = newModifier.style("__raw_html", modifier.styles["__raw_html"]!!)
        }
        
        // Add remaining styles
        modifier.styles.forEach { (key, value) ->
            if (!key.startsWith(HtmxAttributeHandler.HTML_ATTRIBUTE_PREFIX) && 
                !HtmxAttributeHandler.isHtmxAttribute(key) &&
                key != "__raw_html") {
                newModifier = newModifier.style(key, value)
            }
        }
        
        return newModifier
    }
    
    // Delegate all PlatformRenderer methods to JvmPlatformRenderer, but process modifiers first
    
    override fun renderText(text: String, modifier: Modifier) {
        delegate.renderText(text, processModifier(modifier))
    }
    
    override fun renderButton(onClick: () -> Unit, modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderButton(onClick, processModifier(modifier), content)
    }
    
    override fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, type: String) {
        delegate.renderTextField(value, onValueChange, processModifier(modifier), type)
    }
    
    override fun <T> renderSelect(selectedValue: T?, onSelectedChange: (T?) -> Unit, options: List<SelectOption<T>>, modifier: Modifier) {
        delegate.renderSelect(selectedValue, onSelectedChange, options, processModifier(modifier))
    }
    
    override fun renderDatePicker(value: LocalDate?, onValueChange: (LocalDate?) -> Unit, enabled: Boolean, min: LocalDate?, max: LocalDate?, modifier: Modifier) {
        delegate.renderDatePicker(value, onValueChange, enabled, min, max, processModifier(modifier))
    }
    
    override fun renderTextArea(value: String, onValueChange: (String) -> Unit, enabled: Boolean, readOnly: Boolean, rows: Int?, maxLength: Int?, placeholder: String?, modifier: Modifier) {
        delegate.renderTextArea(value, onValueChange, enabled, readOnly, rows, maxLength, placeholder, processModifier(modifier))
    }
    
    override fun addHeadElement(content: String) {
        delegate.addHeadElement(content)
    }
    
    override fun getHeadElements(): List<String> {
        return delegate.getHeadElements()
    }
    
    override fun renderComposable(composable: @Composable () -> Unit) {
        delegate.renderComposable(composable)
    }
    
    override fun renderComposableRoot(composable: @Composable () -> Unit): String {
        return delegate.renderComposableRoot(composable)
    }
    
    override fun renderRow(modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderRow(processModifier(modifier), content)
    }
    
    override fun renderColumn(modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderColumn(processModifier(modifier), content)
    }
    
    override fun renderBox(modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderBox(processModifier(modifier), content)
    }
    
    override fun renderImage(src: String, alt: String, modifier: Modifier) {
        delegate.renderImage(src, alt, processModifier(modifier))
    }
    
    override fun renderIcon(name: String, modifier: Modifier, onClick: (() -> Unit)?, svgContent: String?, type: IconType) {
        delegate.renderIcon(name, processModifier(modifier), onClick, svgContent, type)
    }
    
    override fun renderAlertContainer(variant: AlertVariant?, modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderAlertContainer(variant, processModifier(modifier), content)
    }
    
    override fun renderBadge(modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderBadge(processModifier(modifier), content)
    }
    
    override fun renderCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean, modifier: Modifier) {
        delegate.renderCheckbox(checked, onCheckedChange, enabled, processModifier(modifier))
    }
    
    override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {
        delegate.renderProgress(value, type, processModifier(modifier))
    }
    
    override fun renderFileUpload(onFilesSelected: (List<FileInfo>) -> Unit, accept: String?, multiple: Boolean, enabled: Boolean, capture: String?, modifier: Modifier): () -> Unit {
        return delegate.renderFileUpload(onFilesSelected, accept, multiple, enabled, capture, processModifier(modifier))
    }
    
    override fun renderForm(onSubmit: (() -> Unit)?, modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderForm(onSubmit, processModifier(modifier), content)
    }
    
    override fun renderFormField(modifier: Modifier, labelId: String?, isRequired: Boolean, isError: Boolean, errorMessageId: String?, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderFormField(processModifier(modifier), labelId, isRequired, isError, errorMessageId, content)
    }
    
    override fun renderRadioButton(selected: Boolean, onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {
        delegate.renderRadioButton(selected, onClick, enabled, processModifier(modifier))
    }
    
    override fun renderSpacer(modifier: Modifier) {
        delegate.renderSpacer(processModifier(modifier))
    }
    
    override fun renderRangeSlider(value: ClosedFloatingPointRange<Float>, onValueChange: (ClosedFloatingPointRange<Float>) -> Unit, valueRange: ClosedFloatingPointRange<Float>, steps: Int, enabled: Boolean, modifier: Modifier) {
        delegate.renderRangeSlider(value, onValueChange, valueRange, steps, enabled, processModifier(modifier))
    }
    
    override fun renderSlider(value: Float, onValueChange: (Float) -> Unit, valueRange: ClosedFloatingPointRange<Float>, steps: Int, enabled: Boolean, modifier: Modifier) {
        delegate.renderSlider(value, onValueChange, valueRange, steps, enabled, processModifier(modifier))
    }
    
    override fun renderSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean, modifier: Modifier) {
        delegate.renderSwitch(checked, onCheckedChange, enabled, processModifier(modifier))
    }
    
    override fun renderTimePicker(value: LocalTime?, onValueChange: (LocalTime?) -> Unit, enabled: Boolean, is24Hour: Boolean, modifier: Modifier) {
        delegate.renderTimePicker(value, onValueChange, enabled, is24Hour, processModifier(modifier))
    }
    
    override fun renderAspectRatio(ratio: Float, modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderAspectRatio(ratio, processModifier(modifier), content)
    }
    
    override fun renderCard(modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderCard(processModifier(modifier), content)
    }
    
    override fun renderLink(href: String, modifier: Modifier) {
        delegate.renderLink(href, processModifier(modifier))
    }
    
    override fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit) {
        delegate.renderLink(processModifier(modifier), href, content)
    }
    
    override fun renderEnhancedLink(href: String, target: String?, title: String?, ariaLabel: String?, ariaDescribedBy: String?, modifier: Modifier) {
        delegate.renderEnhancedLink(href, target, title, ariaLabel, ariaDescribedBy, processModifier(modifier))
    }
    
    override fun renderTabLayout(tabs: List<Tab>, selectedTabIndex: Int, onTabSelected: (Int) -> Unit, modifier: Modifier) {
        delegate.renderTabLayout(tabs, selectedTabIndex, onTabSelected, processModifier(modifier))
    }
    
    override fun renderTabLayout(modifier: Modifier, content: @Composable () -> Unit) {
        delegate.renderTabLayout(processModifier(modifier), content)
    }
    
    override fun renderTabLayout(tabs: List<String>, selectedTab: String, onTabSelected: (String) -> Unit, modifier: Modifier, content: () -> Unit) {
        delegate.renderTabLayout(tabs, selectedTab, onTabSelected, processModifier(modifier), content)
    }
    
    override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {
        delegate.renderAnimatedVisibility(visible, processModifier(modifier))
    }
    
    override fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit) {
        delegate.renderAnimatedVisibility(processModifier(modifier), content)
    }
    
    override fun renderAnimatedContent(modifier: Modifier) {
        delegate.renderAnimatedContent(processModifier(modifier))
    }
    
    override fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit) {
        delegate.renderAnimatedContent(processModifier(modifier), content)
    }
    
    override fun renderBlock(modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderBlock(processModifier(modifier), content)
    }
    
    override fun renderInline(modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderInline(processModifier(modifier), content)
    }
    
    override fun renderDiv(modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderDiv(processModifier(modifier), content)
    }
    
    override fun renderSpan(modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderSpan(processModifier(modifier), content)
    }
    
    override fun renderDivider(modifier: Modifier) {
        delegate.renderDivider(processModifier(modifier))
    }
    
    override fun renderExpansionPanel(modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderExpansionPanel(processModifier(modifier), content)
    }
    
    override fun renderGrid(modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderGrid(processModifier(modifier), content)
    }
    
    override fun renderLazyColumn(modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderLazyColumn(processModifier(modifier), content)
    }
    
    override fun renderLazyRow(modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderLazyRow(processModifier(modifier), content)
    }
    
    override fun renderResponsiveLayout(modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderResponsiveLayout(processModifier(modifier), content)
    }
    
    override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {
        delegate.renderLabel(text, processModifier(modifier), forElement)
    }
    
    override fun renderHtmlTag(tagName: String, modifier: Modifier, content: @Composable (FormContent.() -> Unit)) {
        delegate.renderHtmlTag(tagName, processModifier(modifier), content)
    }
}