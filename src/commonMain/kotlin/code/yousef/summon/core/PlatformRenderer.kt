package code.yousef.summon.core

import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.input.SelectOption
import code.yousef.summon.components.input.TextFieldType
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Platform-specific renderer interface.
 * Updated signatures to match refactored @Composable functions.
 * Renderers are generally responsible for creating the root element and applying modifiers/attributes.
 * Content composition (children) happens within the @Composable function itself.
 */
interface PlatformRenderer {
    /** Renders text content */
    fun renderText(value: String, modifier: Modifier)

    /** Renders a button container element */
    fun renderButton(onClick: () -> Unit, enabled: Boolean, modifier: Modifier)

    /** Renders the start of a Row layout container */
    fun renderRow(modifier: Modifier)

    /** Renders the start of a Column layout container */
    fun renderColumn(modifier: Modifier)

    /** Renders a spacer element */
    fun renderSpacer(modifier: Modifier)

    /** Renders a div container element */
    fun renderDiv(modifier: Modifier)

    /** Renders a span container element */
    fun renderSpan(modifier: Modifier)

    /** Renders a text input element (<input>) */
    fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit,
        enabled: Boolean,
        readOnly: Boolean,
        type: TextFieldType,
        placeholder: String?,
        modifier: Modifier
    )

    /** Renders a text area element (<textarea>) */
    fun renderTextArea(
        value: String,
        onValueChange: (String) -> Unit,
        enabled: Boolean,
        readOnly: Boolean,
        rows: Int?,
        maxLength: Int?,
        placeholder: String?,
        modifier: Modifier
    )

    /** Renders a checkbox input element */
    fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    )

    /** Renders a radio button input element */
    fun renderRadioButton(
        selected: Boolean,
        onClick: () -> Unit,
        enabled: Boolean,
        // name: String?, // TODO: How to handle name attribute for grouping? Via modifier?
        modifier: Modifier
    )

    /** Renders a select dropdown element */
    fun <T> renderSelect(
        value: T?,
        onValueChange: (T?) -> Unit,
        options: List<SelectOption<T>>,
        enabled: Boolean,
        modifier: Modifier
    )

    // renderFormField removed 

    /** Renders a switch toggle element */
    fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    )

    /** Sets up a file input element and returns a trigger lambda */
    fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit

    /** Renders a range input element */
    fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    )

    /** Renders a slider input element (single-value) */
    fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    )

    /** Renders the start of a form element */
    fun renderForm(onSubmit: () -> Unit, modifier: Modifier)

    /** Renders the start of a card container element */
    fun renderCard(modifier: Modifier)

    /** Renders an image element */
    fun renderImage(src: String, alt: String, modifier: Modifier)

    /** Renders a divider element (e.g., <hr>) */
    fun renderDivider(modifier: Modifier)

    /** Renders a link element (<a>) */
    fun renderLink(href: String, modifier: Modifier)

    /** Renders an icon representation */
    fun renderIcon(name: String, modifier: Modifier)

    /** Renders an alert container element */
    fun renderAlertContainer(variant: AlertVariant?, modifier: Modifier)

    /** Renders a badge container element */
    fun renderBadge(modifier: Modifier)

    /** Renders a tooltip container element */
    fun renderTooltipContainer(modifier: Modifier)

    /** Renders a progress indicator element */
    fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier)

    /** Renders the start of a box layout container */
    fun renderBox(modifier: Modifier)

    /** Renders the start of a grid layout container */
    fun renderGrid(modifier: Modifier)

    /** Renders the start of an aspect ratio container */
    fun renderAspectRatio(modifier: Modifier)

    /** Renders the start of a responsive layout container (basic div) */
    fun renderResponsiveLayout(modifier: Modifier)

    /** Renders the start of a lazy column container */
    fun renderLazyColumn(modifier: Modifier)

    /** Renders the start of a lazy row container */
    fun renderLazyRow(modifier: Modifier)

    /** Renders a tab layout structure (e.g., tab bar) */
    fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    )

    /** Renders the start of an expansion panel container */
    fun renderExpansionPanel(modifier: Modifier)

    /** Renders a date picker input element */
    fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    )

    /** Renders a time picker input element */
    fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    )

    // --- Animation placeholders ---
    // TODO: Define proper animation support integrated with composition lifecycle

    /** Renders the start of an animated visibility container */
    fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier)

    /** Renders the start of an animated content container */
    fun renderAnimatedContent(modifier: Modifier)
} 
