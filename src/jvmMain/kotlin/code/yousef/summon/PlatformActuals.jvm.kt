package code.yousef.summon

import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.TextFieldType
import code.yousef.summon.components.input.SelectOption
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.ranges.ClosedFloatingPointRange

/**
 * Dummy actual implementation for JsPlatformRenderer on JVM target.
 * This satisfies the compiler but should ideally not be used.
 */
actual class JsPlatformRenderer actual constructor() : PlatformRenderer {
    // Required overrides from PlatformRenderer interface
    actual override fun renderText(value: String, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderButton(onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderImage(src: String, alt: String, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderIcon(name: String, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderBox(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderRow(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderColumn(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    actual override fun renderCard(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderSpacer(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    actual override fun <T> renderComposable(composable: @Composable () -> Unit, consumer: T) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    // Additional methods not in the core interface but needed for compatibility
    fun renderDivider(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    fun renderLazyColumn(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    } 

    fun renderLazyRow(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    fun renderGrid(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    fun renderAspectRatio(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    fun renderResponsiveLayout(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    fun renderExpansionPanel(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    fun renderTextField(
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
    
    fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    fun renderRadioButton(
        selected: Boolean,
        onClick: () -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    fun <T> renderSelect(
        value: T?,
        onValueChange: (T?) -> Unit,
        options: List<SelectOption<T>>,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    fun renderFileUpload(
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

    fun renderAlertContainer(variant: AlertVariant?, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    fun renderBadge(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    fun renderTooltipContainer(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    fun renderLink(href: String, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    fun renderTabLayout(tabs: List<Tab>, selectedTabIndex: Int, onTabSelected: (Int) -> Unit, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    fun renderAnimatedContent(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    fun renderTextArea(
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

    fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>, 
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int, 
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    fun renderForm(onSubmit: () -> Unit, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
} 
