package code.yousef.summon.runtime

import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.input.SelectOption
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent

/**
 * JVM implementation of PlatformRenderer interface.
 * This implementation handles rendering Summon composables to HTML on the server side using kotlinx.html.
 */
actual interface PlatformRenderer {
    // --- Core Rendering Primitives ---
    actual fun renderText(text: String, modifier: Modifier)

    // --- Specific Component Renderers ---
    actual fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    )

    actual fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier,
        type: String
    )

    actual fun <T> renderSelect(
        selectedValue: T?,
        onSelectedChange: (T?) -> Unit,
        options: List<SelectOption<T>>,
        modifier: Modifier
    )

    actual fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        min: LocalDate?,
        max: LocalDate?,
        modifier: Modifier
    )

    actual fun renderTextArea(
        value: String,
        onValueChange: (String) -> Unit,
        enabled: Boolean,
        readOnly: Boolean,
        rows: Int?,
        maxLength: Int?,
        placeholder: String?,
        modifier: Modifier
    )

    // --- Head Management ---
    actual fun addHeadElement(content: String)
    actual fun getHeadElements(): List<String>

    // --- Composition Root ---
    actual fun renderComposableRoot(composable: @Composable () -> Unit): String

    /**
     * Renders a composable into the current context.
     * This is a convenience method for rendering a composable without directly accessing FlowContent.
     */
    actual fun renderComposable(composable: @Composable () -> Unit)

    // --- Layout Components ---
    actual fun renderRow(modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    actual fun renderColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    actual fun renderBox(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    actual fun renderImage(src: String, alt: String, modifier: Modifier)

    actual fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: IconType
    )

    actual fun renderAlertContainer(
        variant: AlertVariant?,
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    )

    actual fun renderBadge(
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    )

    actual fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    )

    actual fun renderProgress(
        value: Float?,
        type: ProgressType,
        modifier: Modifier
    )

    actual fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit

    actual fun renderForm(
        onSubmit: (() -> Unit)?,
        modifier: Modifier,
        content: @Composable FormContent.() -> Unit
    )

    actual fun renderFormField(
        modifier: Modifier,
        labelId: String?,
        isRequired: Boolean,
        isError: Boolean,
        errorMessageId: String?,
        content: @Composable FlowContent.() -> Unit
    )

    actual fun renderRadioButton(
        selected: Boolean,
        onClick: () -> Unit,
        enabled: Boolean,
        modifier: Modifier
    )

    actual fun renderSpacer(modifier: Modifier)

    actual fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    )

    actual fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    )

    actual fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    )

    actual fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        is24Hour: Boolean,
        modifier: Modifier
    )

    actual fun renderAspectRatio(
        ratio: Float,
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    )

    actual fun renderCard(
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    )

    actual fun renderLink(href: String, modifier: Modifier)

    actual fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit)

    actual fun renderEnhancedLink(
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?,
        modifier: Modifier
    )

    actual fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    )

    actual fun renderTabLayout(modifier: Modifier, content: @Composable () -> Unit)

    actual fun renderTabLayout(
        tabs: List<String>,
        selectedTab: String,
        onTabSelected: (String) -> Unit,
        modifier: Modifier,
        content: () -> Unit
    )

    actual fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier)

    actual fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit)

    actual fun renderAnimatedContent(modifier: Modifier)

    actual fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit)

    // --- Basic HTML Element Renderers ---
    actual fun renderBlock(modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    actual fun renderInline(modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    actual fun renderDiv(modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    actual fun renderSpan(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    actual fun renderDivider(modifier: Modifier)

    actual fun renderExpansionPanel(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    actual fun renderGrid(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    actual fun renderLazyColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    actual fun renderLazyRow(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    actual fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    actual fun renderHtmlTag(tagName: String, modifier: Modifier, content: @Composable FlowContent.() -> Unit)
}