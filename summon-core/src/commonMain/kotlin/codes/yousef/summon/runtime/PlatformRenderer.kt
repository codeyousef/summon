/**
 * # Summon Runtime Package
 *
 * This package contains the core runtime components that power the Summon UI framework's
 * composition system and cross-platform rendering capabilities.
 *
 * ## Overview
 *
 * The runtime package provides the foundational infrastructure for:
 *
 * - **Composition Management**: [Composer] and [Recomposer] for tracking and updating UI state
 * - **Platform Abstraction**: [PlatformRenderer] for cross-platform rendering
 * - **State Management**: Reactive state primitives and memory management
 * - **Effects System**: Side effect management through [LaunchedEffect] and [DisposableEffect]
 * - **Composition Locals**: Context propagation through the composition tree
 *
 * ## Key Components
 *
 * ### Core Runtime
 * - [PlatformRenderer] - Abstract renderer for platform-specific output
 * - [Composer] - Manages composition state and recomposition tracking
 * - [Recomposer] - Schedules and executes recomposition when state changes
 *
 * ### State and Memory
 * - [remember] - Remembers values across recompositions
 * - [CompositionLocal] - Provides implicit context down the composition tree
 * - [mutableStateOf] - Creates reactive state holders
 *
 * ### Effects
 * - [LaunchedEffect] - Launches coroutines tied to composition lifecycle
 * - [DisposableEffect] - Manages resources with cleanup
 * - [SideEffect] - Executes side effects after composition
 *
 * @since 1.0.0
 */
package codes.yousef.summon.runtime

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.IconType
import codes.yousef.summon.components.feedback.AlertVariant
import codes.yousef.summon.components.feedback.ProgressType
import codes.yousef.summon.components.input.FileInfo
import codes.yousef.summon.components.navigation.Tab
import codes.yousef.summon.core.FlowContentCompat
import codes.yousef.summon.modifier.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Represents an option in a select dropdown component.
 *
 * @param T The type of the option value
 * @property value The underlying value of the option
 * @property label The display text shown to users
 * @property disabled Whether this option can be selected
 * @since 1.0.0
 */
data class SelectOption<T>(
    val value: T,
    val label: String,
    val disabled: Boolean = false
)

/**
 * Represents a static option used by native `<select>` elements that do not rely on
 * Summon's JavaScript event system. These options are rendered exactly as provided,
 * making them ideal for server-managed forms.
 *
 * @property value Submitted value for the option
 * @property label Visible label shown to users
 * @property isSelected Whether the option should be pre-selected
 * @property isDisabled Whether the option is available for selection
 * @property isPlaceholder Marks the option as a non-selectable placeholder (hidden on open menus)
 * @since 1.0.0
 */
data class NativeSelectOption(
    val value: String,
    val label: String,
    val isSelected: Boolean = false,
    val isDisabled: Boolean = false,
    val isPlaceholder: Boolean = false
)

/**
 * Abstract platform renderer that bridges Summon's declarative UI components to platform-specific output.
 *
 * The PlatformRenderer is the core abstraction that enables Summon to work across different platforms
 * (Browser/JavaScript, JVM/Server-side) by providing a unified interface for rendering UI components.
 * Each platform provides its own implementation that generates appropriate output:
 *
 * - **JavaScript/Browser**: Generates DOM elements and manipulates the browser DOM
 * - **JVM/Server**: Generates HTML strings for server-side rendering
 *
 * ## Architecture
 *
 * The renderer operates on a component-by-component basis, receiving:
 * - Component-specific parameters (text, click handlers, etc.)
 * - [Modifier] for styling and attributes
 * - Content lambdas for composable children
 *
 * ## Rendering Context
 *
 * Most rendering functions operate within a platform-specific context:
 * - **JS Platform**: Direct DOM manipulation context
 * - **JVM Platform**: HTML generation context (typically kotlinx.html.FlowContent)
 *
 * ## State Management Integration
 *
 * The renderer integrates with Summon's reactive state system:
 * - Event handlers (onClick, onValueChange) trigger state updates
 * - State changes trigger recomposition through the [Recomposer]
 * - Only changed components are re-rendered during recomposition
 *
 * ## Platform-Specific Features
 *
 * ### Hydration Support
 * Server-rendered HTML can be "hydrated" on the client:
 * - [renderComposableRootWithHydration] generates hydration-ready HTML
 * - [hydrateComposableRoot] attaches event handlers to existing DOM
 *
 * ### Head Management
 * Control document head elements:
 * - [addHeadElement] adds `<meta>`, `<title>`, `<link>` tags
 * - [getHeadElements] retrieves all added head elements
 *
 * ## Usage Example
 *
 * ```kotlin
 * // Platform-specific renderer instance
 * val renderer = PlatformRenderer()
 * setPlatformRenderer(renderer)
 *
 * // Render a complete page
 * val html = renderer.renderComposableRoot {
 *     ThemeProvider {
 *         Column(modifier = Modifier().padding("16px")) {
 *             Text("Welcome to Summon!")
 *             Button(
 *                 onClick = { println("Clicked!") },
 *                 label = "Click Me"
 *             )
 *         }
 *     }
 * }
 * ```
 *
 * ## Implementation Guidelines
 *
 * When implementing platform-specific renderers:
 *
 * 1. **Respect Modifiers**: Apply all styles and attributes from the modifier
 * 2. **Handle Events**: Wire up event handlers to update reactive state
 * 3. **Manage Context**: Maintain proper rendering context throughout the tree
 * 4. **Performance**: Optimize for minimal re-rendering during recomposition
 * 5. **Accessibility**: Ensure rendered output is accessible
 *
 * @see codes.yousef.summon.modifier.Modifier
 * @see codes.yousef.summon.runtime.Composer
 * @see codes.yousef.summon.runtime.Recomposer
 * @see codes.yousef.summon.annotation.Composable
 * @since 1.0.0
 */
expect open class PlatformRenderer() {

    // --- Core Rendering Primitives ---

    /**
     * Renders text content with the specified styling.
     *
     * This is the fundamental text rendering method used by [Text] components
     * and other text-based UI elements. The implementation varies by platform:
     *
     * - **Browser**: Creates a text node or span element
     * - **Server**: Generates appropriate HTML text content
     *
     * @param text The text content to render
     * @param modifier Styling and attributes to apply to the text element
     * @see codes.yousef.summon.components.display.Text
     * @since 1.0.0
     */
    open fun renderText(text: String, modifier: Modifier)

    /**
     * Renders a semantic label element.
     *
     * Labels provide accessibility context and are typically associated
     * with form inputs. When [forElement] is specified, the label is
     * programmatically associated with that form control.
     *
     * @param text The label text to display
     * @param modifier Styling and attributes for the label
     * @param forElement Optional ID of the form element this label describes
     * @see codes.yousef.summon.components.input.FormField
     * @since 1.0.0
     */
    open fun renderLabel(text: String, modifier: Modifier, forElement: String? = null)

    // --- Interactive Component Renderers ---

    /**
     * Renders an interactive button element.
     *
     * Buttons are fundamental interactive elements that respond to user clicks.
     * The [onClick] handler integrates with Summon's reactive state system,
     * allowing button clicks to trigger state updates and recomposition.
     *
     * ## Platform Behavior
     *
     * - **Browser**: Creates `<button>` element with click event listener
     * - **Server**: Generates `<button>` HTML with form submission handling
     *
     * ## Example
     *
     * ```kotlin
     * renderer.renderButton(
     *     onClick = { count++ },
     *     modifier = Modifier().padding("8px").backgroundColor("blue")
     * ) {
     *     Text("Click me!")
     * }
     * ```
     *
     * @param onClick Callback invoked when the button is clicked
     * @param modifier Styling and attributes for the button element
     * @param content Composable content to render inside the button
     * @see codes.yousef.summon.components.input.Button
     * @since 1.0.0
     */
    open fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    )

    /**
     * Renders a text input field with reactive value binding.
     *
     * Text fields provide user text input with real-time value synchronization.
     * The [onValueChange] callback enables two-way data binding with Summon's
     * state management system.
     *
     * ## Platform Behavior
     *
     * - **Browser**: Creates `<input>` with input event listeners
     * - **Server**: Generates `<input>` with form submission handling
     *
     * ## Input Types
     *
     * The [type] parameter supports all HTML input types:
     * - `"text"` - Standard text input
     * - `"password"` - Password input with masked characters
     * - `"email"` - Email input with validation
     * - `"number"` - Numeric input
     * - `"color"` - Color picker
     * - `"date"` - Date picker
     *
     * ## Example
     *
     * ```kotlin
     * var text by remember { mutableStateOf("") }
     *
     * renderer.renderTextField(
     *     value = text,
     *     onValueChange = { text = it },
     *     modifier = Modifier().padding("8px"),
     *     type = "text"
     * )
     * ```
     *
     * @param value Current text value displayed in the field
     * @param onValueChange Callback invoked when the user changes the text
     * @param modifier Styling and attributes for the input element
     * @param type HTML input type (e.g., "text", "password", "email")
     * @see codes.yousef.summon.components.input.TextField
     * @since 1.0.0
     */
    open fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier,
        type: String
    )

    /** Renders a select dropdown */
    open fun <T> renderSelect(
        selectedValue: T?, // The value that should be marked selected
        onSelectedChange: (T?) -> Unit, // JS hook needed for JVM/JS
        options: List<codes.yousef.summon.runtime.SelectOption<T>>, // Data for <option> tags
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

    /**
     * Renders head elements using a type-safe DSL.
     *
     * This method provides a Kotlin-idiomatic way to add SEO and metadata
     * elements to the document head. The HeadScope provides methods for
     * creating meta tags, links, scripts, and other head elements.
     *
     * @param builder Lambda with HeadScope receiver for defining head elements
     * @see codes.yousef.summon.seo.HeadScope
     * @since 1.0.0
     */
    open fun renderHeadElements(builder: codes.yousef.summon.seo.HeadScope.() -> Unit)

    // --- Composition Root ---
    open fun renderComposableRoot(composable: @Composable () -> Unit): String

    // --- Hydration Support ---
    open fun renderComposableRootWithHydration(composable: @Composable () -> Unit): String

    open fun renderComposableRootWithHydration(state: Any?, composable: @Composable () -> Unit): String

    open fun hydrateComposableRoot(rootElementId: String, composable: @Composable () -> Unit)

    /**
     * Renders a composable component into the current context
     * This is a convenience method for rendering a composable without directly accessing FlowContent
     */
    open fun renderComposable(composable: @Composable () -> Unit)

    // --- Layout Components ---
    open fun renderRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit)
    open fun renderColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit)
    open fun renderBox(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit)

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
        content: @Composable FlowContentCompat.() -> Unit
    )

    /** Renders a badge */
    open fun renderBadge(
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
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
        content: @Composable FlowContentCompat.() -> Unit
    )

    /** Renders a native HTML input that does not require JavaScript interop */
    open fun renderNativeInput(
        type: String,
        modifier: Modifier,
        value: String? = null,
        isChecked: Boolean? = null
    )

    /** Renders a native HTML textarea element */
    open fun renderNativeTextarea(
        modifier: Modifier,
        value: String? = null
    )

    /** Renders a native HTML select element with the provided static options */
    open fun renderNativeSelect(
        modifier: Modifier,
        options: List<NativeSelectOption>
    )

    /** Renders a native HTML button element */
    open fun renderNativeButton(
        type: String,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
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
    open fun renderAspectRatio(ratio: Float, modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit)

    /** Renders a card container */
    open fun renderCard(
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
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
        modifier: Modifier = Modifier(),
        fallbackText: String? = null
    )

    /** Renders a canvas element */
    open fun renderCanvas(
        modifier: Modifier,
        width: Int? = null,
        height: Int? = null,
        content: @Composable FlowContentCompat.() -> Unit
    )

    /** Renders a script tag with optional inline content */
    open fun renderScriptTag(
        src: String?,
        async: Boolean,
        defer: Boolean,
        type: String?,
        modifier: Modifier,
        inlineContent: String?
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
    open fun renderBlock(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit)
    open fun renderInline(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit)
    open fun renderDiv(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit)
    open fun renderSpan(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit)

    /** Renders a divider element (e.g., <hr>) */
    open fun renderDivider(modifier: Modifier)

    /** Renders an expansion panel container */
    open fun renderExpansionPanel(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit)

    /** Renders a grid layout container */
    open fun renderGrid(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit)

    /** Renders a lazy column container (e.g., a div for scrolling) */
    open fun renderLazyColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit)

    /** Renders a lazy row container (e.g., a div for horizontal scrolling) */
    open fun renderLazyRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit)

    /** Renders a responsive layout container (typically a div) */
    open fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit)

    /** Renders an arbitrary HTML tag with the given name, modifier, and content */
    open fun renderHtmlTag(tagName: String, modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit)
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

    open fun renderScreen(modifier: Modifier, content: @Composable (FlowContentCompat.() -> Unit))
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

    /** Renders a modal dialog overlay */
    open fun renderModal(
        onDismiss: () -> Unit,
        modifier: Modifier,
        variant: codes.yousef.summon.components.feedback.ModalVariant,
        size: codes.yousef.summon.components.feedback.ModalSize,
        dismissOnBackdropClick: Boolean,
        showCloseButton: Boolean,
        header: (@Composable () -> Unit)?,
        footer: (@Composable () -> Unit)?,
        content: @Composable () -> Unit
    )

    /** Renders a loading indicator */
    open fun renderLoading(
        modifier: Modifier,
        variant: codes.yousef.summon.components.feedback.LoadingVariant,
        size: codes.yousef.summon.components.feedback.LoadingSize,
        text: String?,
        textModifier: Modifier
    )

    /** Renders a toast notification */
    open fun renderToast(
        toast: codes.yousef.summon.components.feedback.ToastData,
        onDismiss: () -> Unit,
        modifier: Modifier
    )

    /**
     * Called at the start of a recomposition cycle.
     * Platforms can use this to reset counters or prepare state.
     */
    open fun startRecomposition()

    /**
     * Called at the end of a recomposition cycle.
     * Platforms can use this to cleanup or finalize state.
     */
    open fun endRecomposition()
}
