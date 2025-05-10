package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent

/**
 * A mock implementation of [PlatformRenderer] for testing purposes.
 * This class provides default, no-op implementations for all render functions.
 */
open class MockPlatformRenderer : PlatformRenderer() {

    // Tracking properties
    var renderBoxCalled = false
    var renderTextCalled = false
    var renderButtonCalled = false

    var lastTextRendered: String? = null
    var boxModifierUsed: Modifier? = null

    /**
     * Resets all tracking properties to their default state.
     * Call this before each test or assertion if needed.
     */
    fun reset() {
        renderBoxCalled = false
        renderTextCalled = false
        renderButtonCalled = false
        lastTextRendered = null
        boxModifierUsed = null
    }

    override fun renderText(text: String, modifier: Modifier) {
        renderTextCalled = true
        lastTextRendered = text
    }

    override fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    ) {
        renderButtonCalled = true
    }

    override fun renderBox(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        renderBoxCalled = true
        boxModifierUsed = modifier
    }

    override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {}

    override fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier,
        type: String
    ) {}

    override fun <T> renderSelect(
        selectedValue: T?,
        onSelectedChange: (T?) -> Unit,
        options: List<SelectOption<T>>,
        modifier: Modifier
    ) {}

    override fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        min: LocalDate?,
        max: LocalDate?,
        modifier: Modifier
    ) {}

    override fun renderTextArea(
        value: String,
        onValueChange: (String) -> Unit,
        enabled: Boolean,
        readOnly: Boolean,
        rows: Int?,
        maxLength: Int?,
        placeholder: String?,
        modifier: Modifier
    ) {}

    override fun addHeadElement(content: String) {}

    override fun getHeadElements(): List<String> = emptyList()

    override fun renderComposableRoot(composable: @Composable () -> Unit): String = ""

    override fun renderComposable(composable: @Composable () -> Unit) {}

    override fun renderRow(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderImage(src: String, alt: String?, modifier: Modifier) {}

    override fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: IconType
    ) {}

    override fun renderAlertContainer(
        variant: AlertVariant?,
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    ) {}

    override fun renderBadge(
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    ) {}

    override fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {}

    override fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        label: String?,
        modifier: Modifier
    ) {}

    override fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        is24Hour: Boolean,
        modifier: Modifier
    ) {}

    override fun renderDateTimePicker(
        valueDate: LocalDate?,
        valueTime: LocalTime?,
        onValueDateChange: (LocalDate?) -> Unit,
        onValueTimeChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {}

    override fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit = { /* no-op */ }

    override fun renderForm(
        onSubmit: (() -> Unit)?,
        modifier: Modifier,
        content: @Composable FormContent.() -> Unit
    ) {}

    override fun renderFormField(
        modifier: Modifier,
        labelId: String?,
        isRequired: Boolean,
        isError: Boolean,
        errorMessageId: String?,
        content: @Composable FlowContent.() -> Unit
    ) {}

    override fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {}

    override fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {}

    override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {}

    override fun renderSpacer(modifier: Modifier) {}

    override fun renderLink(href: String, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderLink(href: String, modifier: Modifier) {}

    override fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit) {}

    override fun renderEnhancedLink(
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?,
        modifier: Modifier
    ) {}

    override fun renderDivider(modifier: Modifier) {}

    override fun renderGrid(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderBlock(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderInline(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderDiv(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderSpan(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {}

    override fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit) {}

    override fun renderAnimatedContent(modifier: Modifier) {}

    override fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit) {}

    override fun renderAspectRatio(ratio: Float, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderCard(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {}

    override fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    ) {}

    override fun renderTabLayout(
        modifier: Modifier, 
        content: @Composable () -> Unit
    ) {}

    override fun renderTabLayout(
        tabs: List<String>,
        selectedTab: String,
        onTabSelected: (String) -> Unit,
        modifier: Modifier,
        content: () -> Unit
    ) {}

    override fun renderExpansionPanel(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderLazyColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderLazyRow(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderHtmlTag(tagName: String, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderSnackbar(message: String, actionLabel: String?, onAction: (() -> Unit)?) {}

    override fun renderDropdownMenu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {}

    override fun renderTooltip(text: String, modifier: Modifier, content: @Composable () -> Unit) {}

    override fun renderModal(
        visible: Boolean,
        onDismissRequest: () -> Unit,
        title: String?,
        content: @Composable () -> Unit,
        actions: @Composable (() -> Unit)?
    ) {}

    override fun renderScreen(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}

    override fun renderHtml(htmlContent: String, modifier: Modifier) {}

    override fun renderSurface(modifier: Modifier, elevation: Int, content: @Composable () -> Unit) {}

    override fun renderSwipeToDismiss(
        state: Any,
        background: @Composable () -> Unit,
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {}

    override fun renderVerticalPager(count: Int, state: Any, modifier: Modifier, content: @Composable (Int) -> Unit) {}

    override fun renderHorizontalPager(count: Int, state: Any, modifier: Modifier, content: @Composable (Int) -> Unit) {}

    override fun renderAspectRatioContainer(ratio: Float, modifier: Modifier, content: @Composable () -> Unit) {}

    override fun renderFilePicker(
        onFilesSelected: (List<FileInfo>) -> Unit,
        enabled: Boolean,
        multiple: Boolean,
        accept: String?,
        modifier: Modifier,
        actions: @Composable (() -> Unit)?
    ) {}

    override fun renderAlert(
        message: String,
        variant: AlertVariant,
        modifier: Modifier,
        title: String?,
        icon: @Composable (() -> Unit)?,
        actions: @Composable (() -> Unit)?
    ) {}

    override fun renderCard(modifier: Modifier, elevation: Int, content: @Composable () -> Unit) {}

    override fun renderLinearProgressIndicator(progress: Float?, modifier: Modifier, type: ProgressType) {}

    override fun renderCircularProgressIndicator(progress: Float?, modifier: Modifier, type: ProgressType) {}

    override fun renderModalBottomSheet(onDismissRequest: () -> Unit, modifier: Modifier, content: @Composable () -> Unit) {}

    override fun renderAlertDialog(
        onDismissRequest: () -> Unit,
        confirmButton: @Composable () -> Unit,
        modifier: Modifier,
        dismissButton: @Composable (() -> Unit)?,
        icon: @Composable (() -> Unit)?,
        title: @Composable (() -> Unit)?,
        text: @Composable (() -> Unit)?
    ) {}

    override fun renderRadioButton(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        label: String?,
        enabled: Boolean,
        modifier: Modifier
    ) {}

    override fun renderRadioButton(
        selected: Boolean, 
        onClick: () -> Unit, 
        enabled: Boolean, 
        modifier: Modifier
    ) {}

    override fun renderBoxContainer(modifier: Modifier, content: @Composable () -> Unit) {}
}
