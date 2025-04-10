package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.core.LocalTime
import code.yousef.summon.core.PlatformRenderer
import code.yousef.summon.core.style.Color
import code.yousef.summon.modifier.Modifier
import kotlinx.datetime.LocalDate

/**
 * Interface for platform-specific renderers (migrated version).
 * This is part of the migration to the new composition system with @Composable annotations.
 * Will eventually replace the core.PlatformRenderer interface.
 */
interface MigratedPlatformRenderer : PlatformRenderer {
    /**
     * Render a composable to the specified consumer
     *
     * @param composable The composable to render
     * @param consumer The consumer to render to
     */
    fun <T> renderComposable(composable: @Composable () -> Unit, consumer: T)

    /**
     * Add an HTML element to the document head.
     * This is used by SEO components like MetaTags, CanonicalLinks, etc.
     * 
     * @param content The HTML content to add to the head
     */
    fun addHeadElement(content: String)

    /**
     * Render a hyperlink with enhanced accessibility attributes
     *
     * @param href The URL this link points to
     * @param target Optional target attribute (_blank, _self, etc.)
     * @param title Optional title attribute for hover description
     * @param ariaLabel Optional accessible name for screen readers
     * @param ariaDescribedBy Optional ID of element that describes this link
     * @param modifier The modifier to apply to this link
     */
    fun renderEnhancedLink(
        href: String,
        target: String? = null,
        title: String? = null,
        ariaLabel: String? = null,
        ariaDescribedBy: String? = null,
        modifier: Modifier
    )

    /**
     * Renders a form field container element with necessary ARIA attributes
     * for proper accessibility.
     *
     * @param modifier The modifier to apply to the form field container
     * @param labelId Optional ID of the associated label element
     * @param isRequired Whether the field is required
     * @param isError Whether the field is in an error state
     * @param errorMessageId Optional ID of the error message element for aria-describedby
     * @param content The content composable function
     */
    fun renderFormField(
        modifier: Modifier,
        labelId: String? = null,
        isRequired: Boolean = false,
        isError: Boolean = false,
        errorMessageId: String? = null,
        content: @Composable () -> Unit
    )

    // Core components
    fun renderText(modifier: Modifier, content: @Composable () -> Unit)
    fun renderBox(modifier: Modifier, content: @Composable () -> Unit)
    fun renderColumn(modifier: Modifier, content: @Composable () -> Unit)
    fun renderRow(modifier: Modifier, content: @Composable () -> Unit)
    fun renderDivider(modifier: Modifier, color: Color, thickness: Float, vertical: Boolean)
    fun renderLazyColumn(modifier: Modifier, content: @Composable () -> Unit)
    fun renderLazyRow(modifier: Modifier, content: @Composable () -> Unit)
    fun renderGrid(modifier: Modifier, columns: Int, content: @Composable () -> Unit)
    fun renderAspectRatio(modifier: Modifier, ratio: Float, content: @Composable () -> Unit)
    fun renderResponsiveLayout(modifier: Modifier, content: @Composable () -> Unit)
    fun renderExpansionPanel(modifier: Modifier, content: @Composable () -> Unit)
    fun renderCard(modifier: Modifier, content: @Composable () -> Unit)
    override fun renderSpacer(modifier: Modifier)
    fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit)
    fun renderTabLayout(modifier: Modifier, content: @Composable () -> Unit)
    fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit)
    fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit)
    fun renderButton(modifier: Modifier, content: @Composable () -> Unit)
    fun renderImage(modifier: Modifier, content: @Composable () -> Unit)
    fun renderIcon(modifier: Modifier, content: @Composable () -> Unit)
    
    /**
     * Renders an icon with enhanced functionality including click handling and SVG support
     *
     * @param name The name or identifier of the icon
     * @param modifier The modifier to apply styling and attributes
     * @param onClick Optional click handler for interactive icons
     * @param svgContent Optional raw SVG content for SVG icons
     * @param type The type of icon (SVG, FONT, or IMAGE)
     */
    fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)? = null,
        svgContent: String? = null,
        type: code.yousef.summon.components.display.IconType = code.yousef.summon.components.display.IconType.SVG
    )

    // Input components
    fun renderTextField(
        value: String, onValueChange: (String) -> Unit, modifier: Modifier,
        placeholder: String, isError: Boolean, type: String
    )

    fun renderTextArea(
        value: String, onValueChange: (String) -> Unit, modifier: Modifier,
        placeholder: String, maxLines: Int
    )

    fun renderCheckbox(
        checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier,
        enabled: Boolean
    )

    fun renderRadioButton(
        selected: Boolean, onClick: () -> Unit, modifier: Modifier,
        enabled: Boolean
    )

    fun renderSelect(
        value: String, onValueChange: (String) -> Unit, options: List<String>,
        modifier: Modifier, placeholder: String
    )

    fun renderSwitch(
        checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier,
        enabled: Boolean
    )

    fun renderDatePicker(date: LocalDate?, onDateChange: (LocalDate) -> Unit, modifier: Modifier)
    fun renderTimePicker(
        time: LocalTime?, onTimeChange: (LocalTime) -> Unit, modifier: Modifier,
        is24Hour: Boolean
    )

    fun renderFileUpload(
        onFileSelected: (List<Any>) -> Unit, modifier: Modifier, multiple: Boolean,
        acceptedFileTypes: List<String>
    )

    fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>, onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        modifier: Modifier, valueRange: ClosedFloatingPointRange<Float>
    )

    // Navigation components
    fun renderTabLayout(
        tabs: List<String>, selectedTab: String, onTabSelected: (String) -> Unit,
        modifier: Modifier, content: () -> Unit
    )

    // Feedback components
    override fun renderAlertContainer(variant: AlertVariant?, modifier: Modifier)
    override fun renderBadge(modifier: Modifier)
    override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier)
    override fun renderTooltipContainer(modifier: Modifier)

    // Animation
    fun renderHtmlTag(tag: String, attrs: Map<String, String>, content: () -> Unit)
} 
