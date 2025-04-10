package code.yousef.summon

import code.yousef.summon.runtime.MigratedPlatformRenderer
import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.TextFieldType
import code.yousef.summon.components.input.SelectOption
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.core.style.Color
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.ranges.ClosedFloatingPointRange

/**
 * Dummy actual implementation for JsPlatformRenderer on JVM target.
 * This satisfies the compiler but should ideally not be used.
 */
actual class JsPlatformRenderer actual constructor() : MigratedPlatformRenderer {
    // Required overrides from MigratedPlatformRenderer interface
    actual override fun addHeadElement(content: String) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun <T> renderComposable(composable: @Composable () -> Unit, consumer: T) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderText(value: String, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderText(modifier: Modifier, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderButton(onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderButton(modifier: Modifier, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderImage(src: String, alt: String, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderImage(modifier: Modifier, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderIcon(name: String, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderIcon(modifier: Modifier, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: code.yousef.summon.components.display.IconType
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderRow(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderRow(modifier: Modifier, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderColumn(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderColumn(modifier: Modifier, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderBox(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderBox(modifier: Modifier, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderCard(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderCard(modifier: Modifier, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderSpacer(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderAnimatedContent(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderLink(href: String, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderEnhancedLink(
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderDivider(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderDivider(modifier: Modifier, color: Color, thickness: Float, vertical: Boolean) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderLazyColumn(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderLazyColumn(modifier: Modifier, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderLazyRow(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderLazyRow(modifier: Modifier, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderGrid(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderGrid(modifier: Modifier, columns: Int, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderAspectRatio(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderAspectRatio(modifier: Modifier, ratio: Float, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderResponsiveLayout(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderResponsiveLayout(modifier: Modifier, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderExpansionPanel(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderExpansionPanel(modifier: Modifier, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit,
        enabled: Boolean,
        readOnly: Boolean,
        type: TextFieldType,
        placeholder: String?, 
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderTextField(
        value: String, onValueChange: (String) -> Unit, modifier: Modifier,
        placeholder: String, isError: Boolean, type: String
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderCheckbox(
        checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier,
        enabled: Boolean
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderRadioButton(
        selected: Boolean,
        onClick: () -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderRadioButton(
        selected: Boolean, onClick: () -> Unit, modifier: Modifier,
        enabled: Boolean
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderSwitch(
        checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier,
        enabled: Boolean
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun <T> renderSelect(
        value: T?,
        onValueChange: (T?) -> Unit,
        options: List<SelectOption<T>>,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderSelect(
        value: String, onValueChange: (String) -> Unit, options: List<String>,
        modifier: Modifier, placeholder: String
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderDatePicker(date: LocalDate?, onDateChange: (LocalDate) -> Unit, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderTimePicker(
        time: code.yousef.summon.core.LocalTime?, 
        onTimeChange: (code.yousef.summon.core.LocalTime) -> Unit, 
        modifier: Modifier,
        is24Hour: Boolean
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
        return {}
    }
    
    actual override fun renderFileUpload(
        onFileSelected: (List<Any>) -> Unit, modifier: Modifier, multiple: Boolean,
        acceptedFileTypes: List<String>
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderAlertContainer(variant: AlertVariant?, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderBadge(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderTooltipContainer(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderTabLayout(tabs: List<Tab>, selectedTabIndex: Int, onTabSelected: (Int) -> Unit, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderTabLayout(modifier: Modifier, content: @Composable () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderTabLayout(
        tabs: List<String>, 
        selectedTab: String, 
        onTabSelected: (String) -> Unit,
        modifier: Modifier, 
        content: () -> Unit
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderTextArea(
        value: String,
        onValueChange: (String) -> Unit,
        enabled: Boolean,
        readOnly: Boolean,
        rows: Int?, 
        maxLength: Int?, 
        placeholder: String?, 
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderTextArea(
        value: String, onValueChange: (String) -> Unit, modifier: Modifier,
        placeholder: String, maxLines: Int
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>, 
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int, 
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>, onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        modifier: Modifier, valueRange: ClosedFloatingPointRange<Float>
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderForm(onSubmit: () -> Unit, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderDiv(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderSpan(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderHtmlTag(tag: String, attrs: Map<String, String>, content: () -> Unit) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderFormField(
        modifier: Modifier,
        labelId: String?,
        isRequired: Boolean,
        isError: Boolean,
        errorMessageId: String?,
        content: @Composable () -> Unit
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
} 
