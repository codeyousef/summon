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
expect open class PlatformRenderer() {

    // --- Core Rendering Primitives ---
    /** Renders text content */
    open fun renderText(text: String, modifier: Modifier)

    /** Renders a label element with semantic meaning */
    open fun renderLabel(text: String, modifier: Modifier, forElement: String? = null)

    // --- Specific Component Renderers ---
    /** Renders a button */
    open fun renderButton(
        onClick: () -> Unit, // JS hook needed for JVM/JS
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    )

    /** Renders a text input field */
    open fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit, // JS hook needed for JVM/JS
        modifier: Modifier,
        type: String // HTML input type attribute value (e.g., "text", "password", "color")
    )

    /** Renders a select dropdown */
    open fun <T> renderSelect(
        selectedValue: T?, // The value that should be marked selected
        onSelectedChange: (T?) -> Unit, // JS hook needed for JVM/JS
        options: List<code.yousef.summon.runtime.SelectOption<T>>, // Data for <option> tags
        modifier: Modifier
    )

    /** Renders a date picker input */
    open fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit, // JS hook needed
        enabled: Boolean,
        min: LocalDate? = null,
        max: LocalDate? = null,
        modifier: Modifier
    )

    /** Renders a multi-line text area input */
    open fun renderTextArea(
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
    open fun addHeadElement(content: String)
    open fun getHeadElements(): List<String>

    // --- Composition Root ---
    open fun renderComposableRoot(composable: @Composable () -> Unit): String
    
    // --- Hydration Support ---
    open fun renderComposableRootWithHydration(composable: @Composable () -> Unit): String
    open fun hydrateComposableRoot(rootElementId: String, composable: @Composable () -> Unit)

    /**
     * Renders a composable component into the current context
     * This is a convenience method for rendering a composable without directly accessing FlowContent
     */
    open fun renderComposable(composable: @Composable () -> Unit)

    // --- Layout Components ---
    open fun renderRow(modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    open fun renderColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    open fun renderBox(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    /** Renders an image */
    open fun renderImage(src: String, alt: String?, modifier: Modifier)

    /** Renders an icon */
    open fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: IconType
    )

    /** Renders a container for alert messages */
    open fun renderAlertContainer(
        variant: AlertVariant? = null,
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    )

    /** Renders a badge */
    open fun renderBadge(
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    )

    /** Renders a checkbox */
    open fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit, // JS hook needed
        enabled: Boolean,
        modifier: Modifier
    )

    /** Renders a progress indicator */
    open fun renderProgress(
        value: Float? = null, // Null for indeterminate
        type: ProgressType,
        modifier: Modifier
    )

    /** Renders a file upload input. */
    open fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit, // JS hook needed
        accept: String? = null,
        multiple: Boolean = false,
        enabled: Boolean = true,
        capture: String? = null,
        modifier: Modifier
    ): () -> Unit // Returns a trigger function

    /** Renders a form element */
    open fun renderForm(
        onSubmit: (() -> Unit)? = null,
        modifier: Modifier,
        content: @Composable FormContent.() -> Unit
    )

    /** Renders a container for form fields */
    open fun renderFormField(
        modifier: Modifier,
        labelId: String? = null,
        isRequired: Boolean = false,
        isError: Boolean = false,
        errorMessageId: String? = null,
        content: @Composable FlowContent.() -> Unit
    )

    /** Renders a radio button input */
    open fun renderRadioButton(
        selected: Boolean,
        onClick: () -> Unit, // JS hook needed
        enabled: Boolean,
        modifier: Modifier
    )

    /** Renders a simple spacer element */
    open fun renderSpacer(modifier: Modifier)

    /** Renders a range slider */
    open fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit, // JS hook needed
        valueRange: ClosedFloatingPointRange<Float> = 0.0f..1.0f,
        steps: Int = 0, // 0 means continuous
        enabled: Boolean = true,
        modifier: Modifier
    )

    /** Renders a single-value slider */
    open fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit, // JS hook needed
        valueRange: ClosedFloatingPointRange<Float> = 0.0f..1.0f,
        steps: Int = 0, // 0 means continuous
        enabled: Boolean = true,
        modifier: Modifier
    )

    /** Renders a switch toggle */
    open fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit, // JS hook needed
        enabled: Boolean = true,
        modifier: Modifier
    )

    /** Renders a time picker input */
    open fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean = true,
        is24Hour: Boolean = false,
        modifier: Modifier
    )

    /** Renders a container that maintains a specific aspect ratio */
    open fun renderAspectRatio(ratio: Float, modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    /** Renders a card container */
    open fun renderCard(
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    )

    /** Renders a hyperlink */
    open fun renderLink(href: String, modifier: Modifier)

    /** Renders a hyperlink with content */
    open fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit)

    /** Renders an enhanced hyperlink */
    open fun renderEnhancedLink(
        href: String,
        target: String? = null,
        title: String? = null,
        ariaLabel: String? = null,
        ariaDescribedBy: String? = null,
        modifier: Modifier = Modifier()
    )

    /** Renders a Tab layout */
    open fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    )

    /** Renders a Tab layout with content */
    open fun renderTabLayout(modifier: Modifier, content: @Composable () -> Unit)

    /** Renders a Tab layout with simple tabs and content */
    open fun renderTabLayout(
        tabs: List<String>,
        selectedTab: String,
        onTabSelected: (String) -> Unit,
        modifier: Modifier,
        content: () -> Unit
    )

    /** Renders conditionally visible content */
    open fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier)

    /** Renders conditionally visible content with composable */
    open fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit)

    /** Renders content with animation */
    open fun renderAnimatedContent(modifier: Modifier)

    /** Renders content with animation and composable */
    open fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit)

    // --- Basic HTML Element Renderers ---
    open fun renderBlock(modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    open fun renderInline(modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    open fun renderDiv(modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    open fun renderSpan(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    /** Renders a divider element (e.g., <hr>) */
    open fun renderDivider(modifier: Modifier)

    /** Renders an expansion panel container */
    open fun renderExpansionPanel(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    /** Renders a grid layout container */
    open fun renderGrid(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    /** Renders a lazy column container (e.g., a div for scrolling) */
    open fun renderLazyColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    /** Renders a lazy row container (e.g., a div for horizontal scrolling) */
    open fun renderLazyRow(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    /** Renders a responsive layout container (typically a div) */
    open fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContent.() -> Unit)

    /** Renders an arbitrary HTML tag with the given name, modifier, and content */
    open fun renderHtmlTag(tagName: String, modifier: Modifier, content: @Composable FlowContent.() -> Unit)
    open fun renderSnackbar(message: String, actionLabel: String?, onAction: (() -> Unit)?)
    open fun renderDropdownMenu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        content: @Composable (() -> Unit)
    )

    open fun renderTooltip(text: String, modifier: Modifier, content: @Composable (() -> Unit))
    open fun renderModal(
        visible: Boolean,
        onDismissRequest: () -> Unit,
        title: String?,
        content: @Composable (() -> Unit),
        actions: @Composable (() -> Unit)?
    )

    open fun renderScreen(modifier: Modifier, content: @Composable (FlowContent.() -> Unit))
    open fun renderHtml(htmlContent: String, modifier: Modifier)
    
    /** Renders HTML content with optional sanitization */
    open fun renderHtml(htmlContent: String, modifier: Modifier, sanitize: Boolean)
    
    /** Injects global CSS styles into the document head */
    open fun renderGlobalStyle(css: String)
    
    open fun renderSurface(modifier: Modifier, elevation: Int, content: @Composable (() -> Unit))
    open fun renderSwipeToDismiss(
        state: Any,
        background: @Composable (() -> Unit),
        modifier: Modifier,
        content: @Composable (() -> Unit)
    )

    open fun renderVerticalPager(count: Int, state: Any, modifier: Modifier, content: @Composable ((Int) -> Unit))
    open fun renderHorizontalPager(count: Int, state: Any, modifier: Modifier, content: @Composable ((Int) -> Unit))
    open fun renderAspectRatioContainer(ratio: Float, modifier: Modifier, content: @Composable (() -> Unit))
    open fun renderFilePicker(
        onFilesSelected: (List<FileInfo>) -> Unit,
        enabled: Boolean,
        multiple: Boolean,
        accept: String?,
        modifier: Modifier,
        actions: @Composable (() -> Unit)?
    )

    open fun renderAlert(
        message: String,
        variant: AlertVariant,
        modifier: Modifier,
        title: String?,
        icon: @Composable (() -> Unit)?,
        actions: @Composable (() -> Unit)?
    )

    open fun renderCard(modifier: Modifier, elevation: Int, content: @Composable (() -> Unit))
    open fun renderLinearProgressIndicator(progress: Float?, modifier: Modifier, type: ProgressType)
    open fun renderCircularProgressIndicator(progress: Float?, modifier: Modifier, type: ProgressType)
    open fun renderModalBottomSheet(onDismissRequest: () -> Unit, modifier: Modifier, content: @Composable (() -> Unit))
    open fun renderAlertDialog(
        onDismissRequest: () -> Unit,
        confirmButton: @Composable (() -> Unit),
        modifier: Modifier,
        dismissButton: @Composable (() -> Unit)?,
        icon: @Composable (() -> Unit)?,
        title: @Composable (() -> Unit)?,
        text: @Composable (() -> Unit)?
    )

    open fun renderRadioButton(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        label: String?,
        enabled: Boolean,
        modifier: Modifier
    )

    open fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        label: String?,
        modifier: Modifier
    )

    open fun renderBoxContainer(modifier: Modifier, content: @Composable (() -> Unit))

}
