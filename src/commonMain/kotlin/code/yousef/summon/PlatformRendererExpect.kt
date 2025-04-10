package code.yousef.summon

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.input.SelectOption
import code.yousef.summon.components.input.TextFieldType
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.core.style.Color
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.MigratedPlatformRenderer
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Platform-specific implementation of the renderer.
 * This class must be implemented by each platform.
 */
expect class JsPlatformRenderer() : MigratedPlatformRenderer {
    override fun <T> renderComposable(
        composable: @Composable (() -> Unit),
        consumer: T
    )

    // Core components with signatures from both PlatformRenderer and MigratedPlatformRenderer
    override fun renderText(value: String, modifier: Modifier)
    override fun renderText(modifier: Modifier, content: @Composable () -> Unit)

    override fun renderButton(
        onClick: () -> Unit,
        enabled: Boolean,
        modifier: Modifier
    )

    override fun renderButton(modifier: Modifier, content: @Composable () -> Unit)

    override fun renderImage(src: String, alt: String, modifier: Modifier)
    override fun renderImage(modifier: Modifier, content: @Composable () -> Unit)

    override fun renderIcon(name: String, modifier: Modifier)
    override fun renderIcon(modifier: Modifier, content: @Composable () -> Unit)
    override fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: code.yousef.summon.components.display.IconType
    )

    override fun renderRow(modifier: Modifier)
    override fun renderRow(modifier: Modifier, content: @Composable () -> Unit)

    override fun renderColumn(modifier: Modifier)
    override fun renderColumn(modifier: Modifier, content: @Composable () -> Unit)

    override fun renderSpacer(modifier: Modifier)

    override fun renderBox(modifier: Modifier)
    override fun renderBox(modifier: Modifier, content: @Composable () -> Unit)

    override fun renderCard(modifier: Modifier)
    override fun renderCard(modifier: Modifier, content: @Composable () -> Unit)

    override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier)
    override fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit)

    override fun renderAnimatedContent(modifier: Modifier)
    override fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit)

    override fun renderLink(href: String, modifier: Modifier)
    override fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit)

    override fun addHeadElement(content: String)

    override fun renderEnhancedLink(
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?,
        modifier: Modifier
    )

    override fun renderDivider(modifier: Modifier)
    override fun renderDivider(modifier: Modifier, color: Color, thickness: Float, vertical: Boolean)

    override fun renderLazyColumn(modifier: Modifier)
    override fun renderLazyColumn(modifier: Modifier, content: @Composable () -> Unit)

    override fun renderLazyRow(modifier: Modifier)
    override fun renderLazyRow(modifier: Modifier, content: @Composable () -> Unit)

    override fun renderGrid(modifier: Modifier)
    override fun renderGrid(modifier: Modifier, columns: Int, content: @Composable () -> Unit)

    override fun renderAspectRatio(modifier: Modifier)
    override fun renderAspectRatio(modifier: Modifier, ratio: Float, content: @Composable () -> Unit)

    override fun renderResponsiveLayout(modifier: Modifier)
    override fun renderResponsiveLayout(modifier: Modifier, content: @Composable () -> Unit)

    override fun renderExpansionPanel(modifier: Modifier)
    override fun renderExpansionPanel(modifier: Modifier, content: @Composable () -> Unit)

    override fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    )

    override fun renderTabLayout(modifier: Modifier, content: @Composable () -> Unit)
    override fun renderTabLayout(
        tabs: List<String>,
        selectedTab: String,
        onTabSelected: (String) -> Unit,
        modifier: Modifier,
        content: () -> Unit
    )

    // Input components
    override fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier,
        placeholder: String,
        isError: Boolean,
        type: String
    )

    override fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit,
        enabled: Boolean,
        readOnly: Boolean,
        type: TextFieldType,
        placeholder: String?,
        modifier: Modifier
    )

    override fun renderTextArea(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier,
        placeholder: String,
        maxLines: Int
    )

    override fun renderTextArea(
        value: String,
        onValueChange: (String) -> Unit,
        enabled: Boolean,
        readOnly: Boolean,
        rows: Int?,
        maxLength: Int?,
        placeholder: String?,
        modifier: Modifier
    )

    override fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier,
        enabled: Boolean
    )

    override fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    )

    override fun renderRadioButton(selected: Boolean, onClick: () -> Unit, modifier: Modifier, enabled: Boolean)
    override fun renderRadioButton(selected: Boolean, onClick: () -> Unit, enabled: Boolean, modifier: Modifier)

    override fun renderSelect(
        value: String,
        onValueChange: (String) -> Unit,
        options: List<String>,
        modifier: Modifier,
        placeholder: String
    )

    override fun <T> renderSelect(
        value: T?,
        onValueChange: (T?) -> Unit,
        options: List<SelectOption<T>>,
        enabled: Boolean,
        modifier: Modifier
    )

    override fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier,
        enabled: Boolean
    )

    override fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    )

    override fun renderDatePicker(date: LocalDate?, onDateChange: (LocalDate) -> Unit, modifier: Modifier)
    override fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    )

    override fun renderTimePicker(
        time: code.yousef.summon.core.LocalTime?,
        onTimeChange: (code.yousef.summon.core.LocalTime) -> Unit,
        modifier: Modifier,
        is24Hour: Boolean
    )

    override fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    )

    override fun renderFileUpload(
        onFileSelected: (List<Any>) -> Unit,
        modifier: Modifier,
        multiple: Boolean,
        acceptedFileTypes: List<String>
    )

    override fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit

    override fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        modifier: Modifier,
        valueRange: ClosedFloatingPointRange<Float>
    )

    override fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    )

    override fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    )

    // Feedback components
    override fun renderAlertContainer(variant: AlertVariant?, modifier: Modifier)
    override fun renderBadge(modifier: Modifier)
    override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier)
    override fun renderTooltipContainer(modifier: Modifier)

    // Other components
    override fun renderHtmlTag(tag: String, attrs: Map<String, String>, content: () -> Unit)
    override fun renderDiv(modifier: Modifier)
    override fun renderSpan(modifier: Modifier)
    override fun renderForm(onSubmit: () -> Unit, modifier: Modifier)

    // Form field component
    override fun renderFormField(
        modifier: Modifier,
        labelId: String?,
        isRequired: Boolean,
        isError: Boolean,
        errorMessageId: String?,
        content: @Composable () -> Unit
    )

    // Accessibility and animation support
    override fun applyFocus(elementId: String): Boolean
    
    override fun registerAnimation(
        animationId: String, 
        animationProps: Map<String, Any>, 
        targetElementId: String?
    )
    
    override fun startAnimation(
        animationId: String,
        options: Map<String, Any>
    ): code.yousef.summon.core.AnimationController
}
