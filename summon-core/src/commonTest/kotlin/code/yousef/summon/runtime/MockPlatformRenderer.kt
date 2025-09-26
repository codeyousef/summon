package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.core.FlowContentCompat
import code.yousef.summon.modifier.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * A mock implementation of [PlatformRenderer] for testing purposes.
 * This class provides default, no-op implementations for all render functions.
 */
open class MockPlatformRenderer : PlatformRenderer() {

    // Tracking properties
    open var renderBoxCalled = false
    open var renderTextCalled = false
    open var renderButtonCalled = false
    open var renderBlockCalled = false
    open var renderColumnCalled = false
    open var renderRowCalled = false
    var renderIconCalled = false
    var renderImageCalled = false
    var renderLabelCalled = false
    var renderAlertContainerCalled = false
    var renderBadgeCalled = false
    var renderProgressCalled = false
    var renderFormFieldCalled = false
    var renderRadioButtonCalled = false

    var lastTextRendered: String? = null
    var lastModifierRendered: Modifier? = null
    var boxModifierUsed: Modifier? = null
    var lastBlockContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null
    var lastBlockModifierRendered: Modifier? = null
    var lastColumnContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null
    var lastColumnModifierRendered: Modifier? = null
    var lastRowContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null
    var lastRowModifierRendered: Modifier? = null
    var lastIconNameRendered: String? = null
    var lastIconTypeRendered: IconType? = null
    var lastIconModifierRendered: Modifier? = null
    var lastIconOnClickRendered: (() -> Unit)? = null
    var lastIconSvgContentRendered: String? = null
    var lastImageSrcRendered: String? = null
    var lastImageAltRendered: String? = null
    var lastImageModifierRendered: Modifier? = null
    var lastLabelTextRendered: String? = null
    var lastLabelForElementRendered: String? = null
    var lastLabelModifierRendered: Modifier? = null
    var lastAlertVariantRendered: AlertVariant? = null
    var lastAlertModifierRendered: Modifier? = null
    var lastAlertContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null
    var lastBadgeModifierRendered: Modifier? = null
    var lastBadgeContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null
    var lastProgressValueRendered: Float? = null
    var lastProgressTypeRendered: ProgressType? = null
    var lastProgressModifierRendered: Modifier? = null
    var lastBoxModifierRendered: Modifier? = null
    var lastButtonModifierRendered: Modifier? = null
    var lastButtonOnClickRendered: (() -> Unit)? = null
    var lastButtonContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null

    // Checkbox tracking properties
    var renderCheckboxCalled = false
    var lastCheckboxCheckedRendered: Boolean? = null
    var lastCheckboxEnabledRendered: Boolean? = null
    var lastCheckboxModifierRendered: Modifier? = null
    var lastCheckboxOnCheckedChangeRendered: ((Boolean) -> Unit)? = null

    // DatePicker tracking properties
    var renderDatePickerCalled = false
    var lastDatePickerValueRendered: LocalDate? = null
    var lastDatePickerMinDateRendered: LocalDate? = null
    var lastDatePickerMaxDateRendered: LocalDate? = null
    var lastDatePickerEnabledRendered: Boolean? = null
    var lastDatePickerModifierRendered: Modifier? = null
    var lastDatePickerOnValueChangeRendered: ((LocalDate?) -> Unit)? = null

    // FileUpload tracking properties
    var renderFileUploadCalled = false
    var lastFileUploadAcceptRendered: String? = null
    var lastFileUploadMultipleRendered: Boolean? = null
    var lastFileUploadEnabledRendered: Boolean? = null
    var lastFileUploadModifierRendered: Modifier? = null
    var lastFileUploadOnFilesSelectedRendered: ((List<FileInfo>) -> Unit)? = null
    var lastFileUploadCaptureRendered: String? = null
    var lastFileUploadReturnedTriggerFunctionRendered: (() -> Unit)? = null

    // FormField tracking properties
    var lastFormFieldModifierRendered: Modifier? = null
    var lastFormFieldLabelIdRendered: String? = null
    var lastFormFieldIsRequiredRendered: Boolean? = null
    var lastFormFieldIsErrorRendered: Boolean? = null
    var lastFormFieldErrorMessageIdRendered: String? = null
    var lastFormFieldContentLambdaRendered: (@Composable FlowContentCompat.() -> Unit)? = null

    // RadioButton tracking properties
    var lastRadioButtonSelectedRendered: Boolean? = null
    var lastRadioButtonEnabledRendered: Boolean? = null
    var lastRadioButtonModifierRendered: Modifier? = null
    var lastRadioButtonOnClickRendered: (() -> Unit)? = null

    // RangeSlider tracking properties
    var renderRangeSliderCalled = false
    var lastRangeSliderValueRendered: ClosedFloatingPointRange<Float>? = null
    var lastRangeSliderValueRangeRendered: ClosedFloatingPointRange<Float>? = null
    var lastRangeSliderStepsRendered: Int? = null
    var lastRangeSliderEnabledRendered: Boolean? = null
    var lastRangeSliderModifierRendered: Modifier? = null
    var lastRangeSliderOnValueChangeRendered: ((ClosedFloatingPointRange<Float>) -> Unit)? = null

    // Slider tracking properties
    var renderSliderCalled = false
    var lastSliderValueRendered: Float? = null
    var lastSliderValueRangeRendered: ClosedFloatingPointRange<Float>? = null
    var lastSliderStepsRendered: Int? = null
    var lastSliderEnabledRendered: Boolean? = null
    var lastSliderModifierRendered: Modifier? = null
    var lastSliderOnValueChangeRendered: ((Float) -> Unit)? = null

    // Select tracking properties
    var renderSelectCalled = false
    var lastSelectSelectedValueRendered: Any? = null
    var lastSelectOptionsRendered: List<SelectOption<*>>? = null
    var lastSelectModifierRendered: Modifier? = null
    var lastSelectOnSelectedChangeRendered: ((Any?) -> Unit)? = null

    // TextField tracking properties
    open var renderTextFieldCalled = false
    var lastTextFieldValueRendered: String? = null
    var lastTextFieldTypeRendered: String? = null
    var lastTextFieldModifierRendered: Modifier? = null
    var lastTextFieldOnValueChangeRendered: ((String) -> Unit)? = null

    // TextArea tracking properties
    var renderTextAreaCalled = false
    var lastTextAreaValueRendered: String? = null
    var lastTextAreaEnabledRendered: Boolean? = null
    var lastTextAreaReadOnlyRendered: Boolean? = null
    var lastTextAreaRowsRendered: Int? = null
    var lastTextAreaMaxLengthRendered: Int? = null
    var lastTextAreaPlaceholderRendered: String? = null
    var lastTextAreaModifierRendered: Modifier? = null
    var lastTextAreaOnValueChangeRendered: ((String) -> Unit)? = null

    // TimePicker tracking properties
    open var renderTimePickerCalled = false
    var lastTimePickerValueRendered: LocalTime? = null
    var lastTimePickerEnabledRendered: Boolean? = null
    var lastTimePickerIs24HourRendered: Boolean? = null
    var lastTimePickerModifierRendered: Modifier? = null
    var lastTimePickerOnValueChangeRendered: ((LocalTime?) -> Unit)? = null

    // Switch tracking properties
    var renderSwitchCalled = false
    var lastSwitchCheckedRendered: Boolean? = null
    var lastSwitchEnabledRendered: Boolean? = null
    var lastSwitchModifierRendered: Modifier? = null
    var lastSwitchOnCheckedChangeRendered: ((Boolean) -> Unit)? = null

    // Spacer tracking properties
    open var renderSpacerCalled = false
    var lastSpacerModifierRendered: Modifier? = null

    // Link tracking properties
    var renderLinkCalled = false
    var lastLinkHrefRendered: String? = null
    var lastLinkModifierRendered: Modifier? = null

    // Divider tracking properties
    var renderDividerCalled = false
    var lastDividerModifierRendered: Modifier? = null

    // Grid tracking properties
    var renderGridCalled = false
    var lastGridModifierRendered: Modifier? = null
    var lastGridContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null

    // Card tracking properties
    var renderCardCalled = false
    var lastCardModifierRendered: Modifier? = null
    var lastCardContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null

    // TabLayout tracking properties
    open var renderTabLayoutCalled = false
    var lastTabLayoutTabsRendered: List<Tab>? = null
    var lastTabLayoutSelectedTabIndexRendered: Int? = null
    var lastTabLayoutOnTabSelectedRendered: ((Int) -> Unit)? = null
    var lastTabLayoutModifierRendered: Modifier? = null

    // AspectRatio tracking properties
    var renderAspectRatioCalled = false
    var lastAspectRatioRatioRendered: Float? = null
    var lastAspectRatioModifierRendered: Modifier? = null
    var lastAspectRatioContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null

    // ExpansionPanel tracking properties
    var renderExpansionPanelCalled = false
    var lastExpansionPanelModifierRendered: Modifier? = null
    var lastExpansionPanelContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null

    // LazyColumn tracking properties
    var renderLazyColumnCalled = false
    var lastLazyColumnModifierRendered: Modifier? = null
    var lastLazyColumnContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null

    // LazyRow tracking properties
    var renderLazyRowCalled = false
    var lastLazyRowModifierRendered: Modifier? = null
    var lastLazyRowContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null

    // ResponsiveLayout tracking properties
    var renderResponsiveLayoutCalled = false
    var lastResponsiveLayoutModifierRendered: Modifier? = null
    var lastResponsiveLayoutContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null

    // Snackbar tracking properties
    var renderSnackbarCalled = false
    var lastSnackbarMessageRendered: String? = null
    var lastSnackbarActionLabelRendered: String? = null
    var lastSnackbarOnActionRendered: (() -> Unit)? = null

    // Tooltip tracking properties
    var renderTooltipCalled = false
    var lastTooltipTextRendered: String? = null
    var lastTooltipModifierRendered: Modifier? = null
    var lastTooltipContentRendered: (@Composable () -> Unit)? = null

    // Additional tracking properties
    var lastBoxContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null
    var renderDivCalled = false
    var lastDivModifierRendered: Modifier? = null
    var lastDivContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null
    var renderSpanCalled = false
    var lastSpanModifierRendered: Modifier? = null
    var lastSpanContentRendered: (@Composable FlowContentCompat.() -> Unit)? = null

    // GlobalStyle tracking properties
    var renderGlobalStyleCalled = false
    var lastGlobalStyleCssRendered: String? = null
    var globalStyleCallCount = 0

    // RichText/HTML tracking properties
    var renderHtmlCalled = false
    var lastHtmlContentRendered: String? = null
    var lastHtmlModifierRendered: Modifier? = null
    var lastHtmlSanitizeEnabledRendered: Boolean? = null

    /**
     * Resets all tracking properties to their default state.
     * Call this before each test or assertion if needed.
     */
    fun reset() {
        renderBoxCalled = false
        renderTextCalled = false
        renderButtonCalled = false
        renderBlockCalled = false
        renderColumnCalled = false
        renderRowCalled = false
        renderIconCalled = false
        renderImageCalled = false
        renderLabelCalled = false
        renderAlertContainerCalled = false
        renderBadgeCalled = false
        renderProgressCalled = false
        renderFormFieldCalled = false
        renderRadioButtonCalled = false
        lastTextRendered = null
        lastModifierRendered = null
        boxModifierUsed = null
        lastBlockContentRendered = null
        lastBlockModifierRendered = null
        lastColumnContentRendered = null
        lastColumnModifierRendered = null
        lastRowContentRendered = null
        lastRowModifierRendered = null
        lastIconNameRendered = null
        lastIconTypeRendered = null
        lastIconModifierRendered = null
        lastIconOnClickRendered = null
        lastIconSvgContentRendered = null
        lastImageSrcRendered = null
        lastImageAltRendered = null
        lastImageModifierRendered = null
        lastLabelTextRendered = null
        lastLabelForElementRendered = null
        lastLabelModifierRendered = null
        lastAlertVariantRendered = null
        lastAlertModifierRendered = null
        lastAlertContentRendered = null
        lastBadgeModifierRendered = null
        lastBadgeContentRendered = null
        lastProgressValueRendered = null
        lastProgressTypeRendered = null
        lastProgressModifierRendered = null
        lastBoxModifierRendered = null
        lastButtonModifierRendered = null
        lastButtonOnClickRendered = null
        lastButtonContentRendered = null

        // Reset checkbox tracking properties
        renderCheckboxCalled = false
        lastCheckboxCheckedRendered = null
        lastCheckboxEnabledRendered = null
        lastCheckboxModifierRendered = null
        lastCheckboxOnCheckedChangeRendered = null

        // Reset datePicker tracking properties
        renderDatePickerCalled = false
        lastDatePickerValueRendered = null
        lastDatePickerMinDateRendered = null
        lastDatePickerMaxDateRendered = null
        lastDatePickerEnabledRendered = null
        lastDatePickerModifierRendered = null
        lastDatePickerOnValueChangeRendered = null

        // Reset fileUpload tracking properties
        renderFileUploadCalled = false
        lastFileUploadAcceptRendered = null
        lastFileUploadMultipleRendered = null
        lastFileUploadEnabledRendered = null
        lastFileUploadModifierRendered = null
        lastFileUploadOnFilesSelectedRendered = null
        lastFileUploadCaptureRendered = null
        lastFileUploadReturnedTriggerFunctionRendered = null

        // Reset formField tracking properties
        lastFormFieldModifierRendered = null
        lastFormFieldLabelIdRendered = null
        lastFormFieldIsRequiredRendered = null
        lastFormFieldIsErrorRendered = null
        lastFormFieldErrorMessageIdRendered = null
        lastFormFieldContentLambdaRendered = null

        // Reset radioButton tracking properties
        lastRadioButtonSelectedRendered = null
        lastRadioButtonEnabledRendered = null
        lastRadioButtonModifierRendered = null
        lastRadioButtonOnClickRendered = null

        // Reset rangeSlider tracking properties
        renderRangeSliderCalled = false
        lastRangeSliderValueRendered = null
        lastRangeSliderValueRangeRendered = null
        lastRangeSliderStepsRendered = null
        lastRangeSliderEnabledRendered = null
        lastRangeSliderModifierRendered = null
        lastRangeSliderOnValueChangeRendered = null

        // Reset slider tracking properties
        renderSliderCalled = false
        lastSliderValueRendered = null
        lastSliderValueRangeRendered = null
        lastSliderStepsRendered = null
        lastSliderEnabledRendered = null
        lastSliderModifierRendered = null
        lastSliderOnValueChangeRendered = null

        // Reset select tracking properties
        renderSelectCalled = false
        lastSelectSelectedValueRendered = null
        lastSelectOptionsRendered = null
        lastSelectModifierRendered = null
        lastSelectOnSelectedChangeRendered = null

        // Reset textField tracking properties
        renderTextFieldCalled = false
        lastTextFieldValueRendered = null
        lastTextFieldTypeRendered = null
        lastTextFieldModifierRendered = null
        lastTextFieldOnValueChangeRendered = null

        // Reset textArea tracking properties
        renderTextAreaCalled = false
        lastTextAreaValueRendered = null
        lastTextAreaEnabledRendered = null
        lastTextAreaReadOnlyRendered = null
        lastTextAreaRowsRendered = null
        lastTextAreaMaxLengthRendered = null
        lastTextAreaPlaceholderRendered = null
        lastTextAreaModifierRendered = null
        lastTextAreaOnValueChangeRendered = null

        // Reset timePicker tracking properties
        renderTimePickerCalled = false
        lastTimePickerValueRendered = null
        lastTimePickerEnabledRendered = null
        lastTimePickerIs24HourRendered = null
        lastTimePickerModifierRendered = null
        lastTimePickerOnValueChangeRendered = null

        // Reset switch tracking properties
        renderSwitchCalled = false
        lastSwitchCheckedRendered = null
        lastSwitchEnabledRendered = null
        lastSwitchModifierRendered = null
        lastSwitchOnCheckedChangeRendered = null

        // Reset spacer tracking properties
        renderSpacerCalled = false
        lastSpacerModifierRendered = null

        // Reset link tracking properties
        renderLinkCalled = false
        lastLinkHrefRendered = null
        lastLinkModifierRendered = null

        // Reset divider tracking properties
        renderDividerCalled = false
        lastDividerModifierRendered = null

        // Reset grid tracking properties
        renderGridCalled = false
        lastGridModifierRendered = null
        lastGridContentRendered = null

        // Reset card tracking properties
        renderCardCalled = false
        lastCardModifierRendered = null
        lastCardContentRendered = null

        // Reset tabLayout tracking properties
        renderTabLayoutCalled = false
        lastTabLayoutTabsRendered = null
        lastTabLayoutSelectedTabIndexRendered = null
        lastTabLayoutOnTabSelectedRendered = null
        lastTabLayoutModifierRendered = null

        // Reset aspectRatio tracking properties
        renderAspectRatioCalled = false
        lastAspectRatioRatioRendered = null
        lastAspectRatioModifierRendered = null
        lastAspectRatioContentRendered = null

        // Reset expansionPanel tracking properties
        renderExpansionPanelCalled = false
        lastExpansionPanelModifierRendered = null
        lastExpansionPanelContentRendered = null

        // Reset lazyColumn tracking properties
        renderLazyColumnCalled = false
        lastLazyColumnModifierRendered = null
        lastLazyColumnContentRendered = null

        // Reset lazyRow tracking properties
        renderLazyRowCalled = false
        lastLazyRowModifierRendered = null
        lastLazyRowContentRendered = null

        // Reset responsiveLayout tracking properties
        renderResponsiveLayoutCalled = false
        lastResponsiveLayoutModifierRendered = null
        lastResponsiveLayoutContentRendered = null

        // Reset snackbar tracking properties
        renderSnackbarCalled = false
        lastSnackbarMessageRendered = null
        lastSnackbarActionLabelRendered = null
        lastSnackbarOnActionRendered = null

        // Reset tooltip tracking properties
        renderTooltipCalled = false
        lastTooltipTextRendered = null
        lastTooltipModifierRendered = null
        lastTooltipContentRendered = null

        // Reset additional tracking properties
        lastBoxContentRendered = null
        renderDivCalled = false
        lastDivModifierRendered = null
        lastDivContentRendered = null
        renderSpanCalled = false
        lastSpanModifierRendered = null
        lastSpanContentRendered = null

        // Reset GlobalStyle tracking properties
        renderGlobalStyleCalled = false
        lastGlobalStyleCssRendered = null
        globalStyleCallCount = 0

        // Reset RichText/HTML tracking properties
        renderHtmlCalled = false
        lastHtmlContentRendered = null
        lastHtmlModifierRendered = null
        lastHtmlSanitizeEnabledRendered = null
    }

    override fun renderText(text: String, modifier: Modifier) {
        renderTextCalled = true
        lastTextRendered = text
        lastModifierRendered = modifier
    }

    override fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        renderButtonCalled = true
        lastButtonOnClickRendered = onClick
        lastButtonModifierRendered = modifier
        lastButtonContentRendered = content
    }

    override fun renderBox(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderBoxCalled = true
        boxModifierUsed = modifier
        lastBoxModifierRendered = modifier
        lastBoxContentRendered = content
    }

    override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {
        renderLabelCalled = true
        lastLabelTextRendered = text
        lastLabelModifierRendered = modifier
        lastLabelForElementRendered = forElement
    }

    override fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier,
        type: String
    ) {
        renderTextFieldCalled = true
        lastTextFieldValueRendered = value
        lastTextFieldOnValueChangeRendered = onValueChange
        lastTextFieldModifierRendered = modifier
        lastTextFieldTypeRendered = type
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> renderSelect(
        selectedValue: T?,
        onSelectedChange: (T?) -> Unit,
        options: List<SelectOption<T>>,
        modifier: Modifier
    ) {
        renderSelectCalled = true
        lastSelectSelectedValueRendered = selectedValue
        lastSelectOnSelectedChangeRendered = onSelectedChange as ((Any?) -> Unit)
        lastSelectOptionsRendered = options as List<SelectOption<*>>
        lastSelectModifierRendered = modifier
    }

    override fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        min: LocalDate?,
        max: LocalDate?,
        modifier: Modifier
    ) {
        renderDatePickerCalled = true
        lastDatePickerValueRendered = value
        lastDatePickerOnValueChangeRendered = onValueChange
        lastDatePickerEnabledRendered = enabled
        lastDatePickerMinDateRendered = min
        lastDatePickerMaxDateRendered = max
        lastDatePickerModifierRendered = modifier
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
        renderTextAreaCalled = true
        lastTextAreaValueRendered = value
        lastTextAreaOnValueChangeRendered = onValueChange
        lastTextAreaEnabledRendered = enabled
        lastTextAreaReadOnlyRendered = readOnly
        lastTextAreaRowsRendered = rows
        lastTextAreaMaxLengthRendered = maxLength
        lastTextAreaPlaceholderRendered = placeholder
        lastTextAreaModifierRendered = modifier
    }

    override fun addHeadElement(content: String) {}

    override fun getHeadElements(): List<String> = emptyList()

    override fun renderComposableRoot(composable: @Composable () -> Unit): String = ""

    override fun renderComposable(composable: @Composable () -> Unit) {}

    override fun renderRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderRowCalled = true
        lastRowModifierRendered = modifier
        lastRowContentRendered = content
    }

    override fun renderColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderColumnCalled = true
        lastColumnModifierRendered = modifier
        lastColumnContentRendered = content
    }

    override fun renderImage(src: String, alt: String?, modifier: Modifier) {
        renderImageCalled = true
        lastImageSrcRendered = src
        lastImageAltRendered = alt
        lastImageModifierRendered = modifier
    }

    override fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: IconType
    ) {
        renderIconCalled = true
        lastIconNameRendered = name
        lastIconModifierRendered = modifier
        lastIconOnClickRendered = onClick
        lastIconSvgContentRendered = svgContent
        lastIconTypeRendered = type
    }

    override fun renderAlertContainer(
        variant: AlertVariant?,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        renderAlertContainerCalled = true
        lastAlertVariantRendered = variant
        lastAlertModifierRendered = modifier
        lastAlertContentRendered = content
    }

    override fun renderBadge(
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        renderBadgeCalled = true
        lastBadgeModifierRendered = modifier
        lastBadgeContentRendered = content
    }

    override fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        renderCheckboxCalled = true
        lastCheckboxCheckedRendered = checked
        lastCheckboxOnCheckedChangeRendered = onCheckedChange
        lastCheckboxEnabledRendered = enabled
        lastCheckboxModifierRendered = modifier
    }

    override fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        label: String?,
        modifier: Modifier
    ) {
        renderCheckboxCalled = true
        lastCheckboxCheckedRendered = checked
        lastCheckboxOnCheckedChangeRendered = onCheckedChange
        lastCheckboxEnabledRendered = enabled
        lastCheckboxModifierRendered = modifier
    }

    override fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        is24Hour: Boolean,
        modifier: Modifier
    ) {
        renderTimePickerCalled = true
        lastTimePickerValueRendered = value
        lastTimePickerOnValueChangeRendered = onValueChange
        lastTimePickerEnabledRendered = enabled
        lastTimePickerIs24HourRendered = is24Hour
        lastTimePickerModifierRendered = modifier
    }


    override fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit {
        renderFileUploadCalled = true
        lastFileUploadOnFilesSelectedRendered = onFilesSelected
        lastFileUploadAcceptRendered = accept
        lastFileUploadMultipleRendered = multiple
        lastFileUploadEnabledRendered = enabled
        lastFileUploadCaptureRendered = capture
        lastFileUploadModifierRendered = modifier
        val triggerFunction = { /* no-op */ }
        lastFileUploadReturnedTriggerFunctionRendered = triggerFunction
        return triggerFunction
    }

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
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        renderFormFieldCalled = true
        lastFormFieldModifierRendered = modifier
        lastFormFieldLabelIdRendered = labelId
        lastFormFieldIsRequiredRendered = isRequired
        lastFormFieldIsErrorRendered = isError
        lastFormFieldErrorMessageIdRendered = errorMessageId
        lastFormFieldContentLambdaRendered = content
    }

    override fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        renderRangeSliderCalled = true
        lastRangeSliderValueRendered = value
        lastRangeSliderOnValueChangeRendered = onValueChange
        lastRangeSliderValueRangeRendered = valueRange
        lastRangeSliderStepsRendered = steps
        lastRangeSliderEnabledRendered = enabled
        lastRangeSliderModifierRendered = modifier
    }

    override fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        renderSliderCalled = true
        lastSliderValueRendered = value
        lastSliderOnValueChangeRendered = onValueChange
        lastSliderValueRangeRendered = valueRange
        lastSliderStepsRendered = steps
        lastSliderEnabledRendered = enabled
        lastSliderModifierRendered = modifier
    }

    override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {
        renderProgressCalled = true
        lastProgressValueRendered = value
        lastProgressTypeRendered = type
        lastProgressModifierRendered = modifier
    }

    override fun renderSpacer(modifier: Modifier) {
        renderSpacerCalled = true
        lastSpacerModifierRendered = modifier
    }

    override fun renderLink(href: String, modifier: Modifier) {
        renderLinkCalled = true
        lastLinkHrefRendered = href
        lastLinkModifierRendered = modifier
    }

    override fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit) {}

    override fun renderEnhancedLink(
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?,
        modifier: Modifier
    ) {}

    override fun renderDivider(modifier: Modifier) {
        renderDividerCalled = true
        lastDividerModifierRendered = modifier
    }

    override fun renderGrid(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderGridCalled = true
        lastGridModifierRendered = modifier
        lastGridContentRendered = content
    }

    override fun renderBlock(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderBlockCalled = true
        lastBlockModifierRendered = modifier
        lastBlockContentRendered = content
    }

    override fun renderInline(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}

    override fun renderDiv(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderDivCalled = true
        lastDivModifierRendered = modifier
        lastDivContentRendered = content
    }

    override fun renderSpan(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderSpanCalled = true
        lastSpanModifierRendered = modifier
        lastSpanContentRendered = content
    }

    override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {}

    override fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit) {}

    override fun renderAnimatedContent(modifier: Modifier) {}

    override fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit) {}

    override fun renderAspectRatio(
        ratio: Float,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        renderAspectRatioCalled = true
        lastAspectRatioRatioRendered = ratio
        lastAspectRatioModifierRendered = modifier
        lastAspectRatioContentRendered = content
    }

    override fun renderCard(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderCardCalled = true
        lastCardModifierRendered = modifier
        lastCardContentRendered = content
    }

    override fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        renderSwitchCalled = true
        lastSwitchCheckedRendered = checked
        lastSwitchOnCheckedChangeRendered = onCheckedChange
        lastSwitchEnabledRendered = enabled
        lastSwitchModifierRendered = modifier
    }

    override fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    ) {
        renderTabLayoutCalled = true
        lastTabLayoutTabsRendered = tabs
        lastTabLayoutSelectedTabIndexRendered = selectedTabIndex
        lastTabLayoutOnTabSelectedRendered = onTabSelected
        lastTabLayoutModifierRendered = modifier
    }

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

    override fun renderExpansionPanel(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderExpansionPanelCalled = true
        lastExpansionPanelModifierRendered = modifier
        lastExpansionPanelContentRendered = content
    }

    override fun renderLazyColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderLazyColumnCalled = true
        lastLazyColumnModifierRendered = modifier
        lastLazyColumnContentRendered = content
    }

    override fun renderLazyRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderLazyRowCalled = true
        lastLazyRowModifierRendered = modifier
        lastLazyRowContentRendered = content
    }

    override fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderResponsiveLayoutCalled = true
        lastResponsiveLayoutModifierRendered = modifier
        lastResponsiveLayoutContentRendered = content
    }

    override fun renderHtmlTag(
        tagName: String,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
    }

    override fun renderSnackbar(message: String, actionLabel: String?, onAction: (() -> Unit)?) {
        renderSnackbarCalled = true
        lastSnackbarMessageRendered = message
        lastSnackbarActionLabelRendered = actionLabel
        lastSnackbarOnActionRendered = onAction
    }

    override fun renderDropdownMenu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {}

    override fun renderTooltip(text: String, modifier: Modifier, content: @Composable () -> Unit) {
        renderTooltipCalled = true
        lastTooltipTextRendered = text
        lastTooltipModifierRendered = modifier
        lastTooltipContentRendered = content
    }

    override fun renderModal(
        visible: Boolean,
        onDismissRequest: () -> Unit,
        title: String?,
        content: @Composable () -> Unit,
        actions: @Composable (() -> Unit)?
    ) {}

    override fun renderScreen(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}

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
    ) {
        renderRadioButtonCalled = true
        lastRadioButtonSelectedRendered = selected
        lastRadioButtonOnClickRendered = onClick
        lastRadioButtonEnabledRendered = enabled
        lastRadioButtonModifierRendered = modifier
    }

    override fun renderBoxContainer(modifier: Modifier, content: @Composable () -> Unit) {}

    // Mock implementations for new render methods
    override fun renderGlobalStyle(css: String) {
        renderGlobalStyleCalled = true
        lastGlobalStyleCssRendered = css
        globalStyleCallCount++
    }

    override fun renderHtml(htmlContent: String, modifier: Modifier, sanitize: Boolean) {
        renderHtmlCalled = true
        lastHtmlContentRendered = htmlContent
        lastHtmlModifierRendered = modifier
        lastHtmlSanitizeEnabledRendered = sanitize
    }
}
