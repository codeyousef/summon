package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
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

/** Data class for Select options */
data class SelectOption<T>(
    val value: T, // The value attribute of the <option>
    val label: String, // The text content of the <option>
    val disabled: Boolean = false
)

// Ensure MigratedPlatformRenderer interface definition is completely removed from this file.
/**
 * EXPECTED Interface responsible for rendering Summon composables into a target format (e.g., HTML).
 * Implementations bridge the gap between the abstract component model and the platform's rendering mechanism.
 * Functions typically operate within a target context (e.g., a kotlinx.html.FlowContent receiver).
 */
expect interface PlatformRenderer {

    // --- Core Rendering Primitives ---
    /** Renders text content */
    fun renderText(text: String, modifier: Modifier)

    /** Renders a label element with semantic meaning */
    fun renderLabel(text: String, modifier: Modifier, forElement: String? = null)

    // --- Specific Component Renderers ---
    /** Renders a button */
    fun renderButton(
        onClick: () -> Unit, // JS hook needed for JVM/JS
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    )

    /** Renders a text input field */
    fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit, // JS hook needed for JVM/JS
        modifier: Modifier,
        type: String // HTML input type attribute value (e.g., "text", "password", "color")
    )

    /** Renders a select dropdown */
    fun <T> renderSelect(
        selectedValue: T?, // The value that should be marked selected
        onSelectedChange: (T?) -> Unit, // JS hook needed for JVM/JS
        options: List<SelectOption<T>>, // Data for <option> tags
        modifier: Modifier
    )

    /** Renders a date picker input */
    fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit, // JS hook needed
        enabled: Boolean,
        min: LocalDate? = null,
        max: LocalDate? = null,
        modifier: Modifier
    )

    /** Renders a multi-line text area input */
    fun renderTextArea(
        value: String,
        onValueChange: (String) -> Unit, // JS hook needed
        enabled: Boolean = true,
        readOnly: Boolean = false,
        rows: Int? = null,
        maxLength: Int? = null,
        placeholder: String? = null,
        modifier: Modifier
    )

    // --- Head Management ---
    fun addHeadElement(content: String)
    fun getHeadElements(): List<String>

    // --- Composition Root ---
    fun renderComposableRoot(composable: @Composable () -> Unit): String

    /** 
     * Renders a composable component into the current context 
     * This is a convenience method for rendering a composable without directly accessing FlowContent
     */
    fun renderComposable(composable: @Composable () -> Unit)

    // --- Layout Components ---
    fun renderRow(modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    fun renderColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    fun renderBox(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    /** Renders an image */
    fun renderImage(src: String, alt: String, modifier: Modifier)

    /** Renders an icon */
    fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: IconType
    )

    /** Renders a container for alert messages */
    fun renderAlertContainer(
        variant: AlertVariant? = null,
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    )

    /** Renders a badge */
    fun renderBadge(
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    )

    /** Renders a checkbox */
    fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit, // JS hook needed
        enabled: Boolean,
        modifier: Modifier
    )

    /** Renders a progress indicator */
    fun renderProgress(
        value: Float? = null, // Null for indeterminate
        type: ProgressType,
        modifier: Modifier
    )

    /** Renders a file upload input. */
    fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit, // JS hook needed
        accept: String? = null,
        multiple: Boolean = false,
        enabled: Boolean = true,
        capture: String? = null,
        modifier: Modifier
    ): () -> Unit // Returns a trigger function

    /** Renders a form element */
    fun renderForm(
        onSubmit: (() -> Unit)? = null,
        modifier: Modifier,
        content: @Composable FormContent.() -> Unit
    )

    /** Renders a container for form fields */
    fun renderFormField(
        modifier: Modifier,
        labelId: String? = null,
        isRequired: Boolean = false,
        isError: Boolean = false,
        errorMessageId: String? = null,
        content: @Composable FlowContent.() -> Unit
    )

    /** Renders a radio button input */
    fun renderRadioButton(
        selected: Boolean,
        onClick: () -> Unit, // JS hook needed
        enabled: Boolean,
        modifier: Modifier
    )

    /** Renders a simple spacer element */
    fun renderSpacer(modifier: Modifier)

    /** Renders a range slider */
    fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit, // JS hook needed
        valueRange: ClosedFloatingPointRange<Float> = 0.0f..1.0f,
        steps: Int = 0, // 0 means continuous
        enabled: Boolean = true,
        modifier: Modifier
    )

    /** Renders a single-value slider */
    fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit, // JS hook needed
        valueRange: ClosedFloatingPointRange<Float> = 0.0f..1.0f,
        steps: Int = 0, // 0 means continuous
        enabled: Boolean = true,
        modifier: Modifier
    )

    /** Renders a switch toggle */
    fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit, // JS hook needed
        enabled: Boolean = true,
        modifier: Modifier
    )

    /** Renders a time picker input */
    fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean = true,
        is24Hour: Boolean = false,
        modifier: Modifier
    )

    /** Renders a container that maintains a specific aspect ratio */
    fun renderAspectRatio(
        ratio: Float,
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    )

    /** Renders a card container */
    fun renderCard(
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    )

    /** Renders a hyperlink */
    fun renderLink(href: String, modifier: Modifier)

    /** Renders a hyperlink with content */
    fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit)

    /** Renders an enhanced hyperlink */
    fun renderEnhancedLink(
        href: String,
        target: String? = null,
        title: String? = null,
        ariaLabel: String? = null,
        ariaDescribedBy: String? = null,
        modifier: Modifier = Modifier()
    )

    /** Renders a Tab layout */
    fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    )

    /** Renders a Tab layout with content */
    fun renderTabLayout(modifier: Modifier, content: @Composable () -> Unit)

    /** Renders a Tab layout with simple tabs and content */
    fun renderTabLayout(
        tabs: List<String>,
        selectedTab: String,
        onTabSelected: (String) -> Unit,
        modifier: Modifier,
        content: () -> Unit
    )

    /** Renders conditionally visible content */
    fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier)

    /** Renders conditionally visible content with composable */
    fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit)

    /** Renders content with animation */
    fun renderAnimatedContent(modifier: Modifier)

    /** Renders content with animation and composable */
    fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit)

    // --- Basic HTML Element Renderers ---
    fun renderBlock(modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    fun renderInline(modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    fun renderDiv(modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    fun renderSpan(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    /** Renders a divider element (e.g., <hr>) */
    fun renderDivider(modifier: Modifier)

    /** Renders an expansion panel container */
    fun renderExpansionPanel(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    /** Renders a grid layout container */
    fun renderGrid(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    /** Renders a lazy column container (e.g., a div for scrolling) */
    fun renderLazyColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    /** Renders a lazy row container (e.g., a div for horizontal scrolling) */
    fun renderLazyRow(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    /** Renders a responsive layout container (typically a div) */
    fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    /** Renders an arbitrary HTML tag with the given name, modifier, and content */
    fun renderHtmlTag(tagName: String, modifier: Modifier, content: @Composable FlowContent.() -> Unit)

}
