package code.yousef.summon

import code.yousef.summon.runtime.PlatformRenderer

import modifier.Modifier
import kotlin.ranges.ClosedFloatingPointRange
import code.yousef.summon.components.input.TextFieldType
import code.yousef.summon.components.input.SelectOption
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import components.feedback.AlertVariant

/**
 * Dummy actual implementation for JsPlatformRenderer on JVM target.
 * This satisfies the compiler but should ideally not be used.
 */
actual class JsPlatformRenderer actual constructor() : PlatformRenderer {
    // Implement necessary methods from PlatformRenderer, likely throwing errors or no-oping.
    override fun renderText(value: String, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderButton(onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    // Add dummy implementations for ALL methods defined in the PlatformRenderer interface
    override fun renderImage(src: String, alt: String, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderIcon(name: String, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderBox(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderRow(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderColumn(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    override fun renderCard(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderDivider(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderLazyColumn(modifier: Modifier) {
         throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    } 

    override fun renderLazyRow(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    override fun renderGrid(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    override fun renderAspectRatio(modifier: Modifier) {
         throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    override fun renderSpacer(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    override fun renderResponsiveLayout(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderExpansionPanel(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderTextField(
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
    
    override fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderRadioButton(
        selected: Boolean,
        onClick: () -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    override fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun <T> renderSelect(
        value: T?,
        onValueChange: (T?) -> Unit,
        options: List<SelectOption<T>>,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    override fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
         throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    override fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderAlertContainer(variant: AlertVariant?, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    override fun renderBadge(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    override fun renderTooltipContainer(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    override fun renderLink(href: String, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderTabLayout(tabs: List<Tab>, selectedTabIndex: Int, onTabSelected: (Int) -> Unit, modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
    
    override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {
         throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

     override fun renderAnimatedContent(modifier: Modifier) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
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
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>, 
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int, 
        enabled: Boolean,
        modifier: Modifier
    ) {
        throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }

    override fun renderForm(onSubmit: () -> Unit, modifier: Modifier) {
         throw NotImplementedError("JsPlatformRenderer should not be used on JVM")
    }
} 
