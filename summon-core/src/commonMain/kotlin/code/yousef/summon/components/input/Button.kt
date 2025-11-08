/**
 * # Summon Input Components Package
 *
 * This package provides interactive input components for user interaction and data entry.
 *
 * ## Overview
 *
 * The input components package includes form controls and interactive elements that enable:
 *
 * - **User Input**: Text fields, checkboxes, radio buttons, sliders
 * - **Actions**: Buttons for triggering operations and navigation
 * - **Selection**: Dropdowns, date pickers, file uploads
 * - **Forms**: Form containers and field management
 * - **Validation**: Input validation and error handling
 *
 * ## Key Components
 *
 * ### Action Components
 * - [Button] - Primary action trigger with multiple variants
 * - **Submit Buttons** - Form submission handling
 * - **Icon Buttons** - Compact action triggers
 *
 * ### Text Input
 * - [TextField] - Single-line text input with validation
 * - [TextArea] - Multi-line text input
 * - **Password Fields** - Secure text input
 *
 * ### Selection Components
 * - [Checkbox] - Boolean selection with tri-state support
 * - [RadioButton] - Single choice from multiple options
 * - [Select] - Dropdown selection with filtering
 * - [DatePicker] - Date selection with calendar
 * - [TimePicker] - Time selection interface
 *
 * ### Advanced Inputs
 * - [Slider] - Numeric range selection
 * - [RangeSlider] - Dual-handle range selection
 * - [FileUpload] - File selection and upload
 * - [Switch] - Toggle switch for boolean values
 *
 * ## Usage Patterns
 *
 * ### Form Composition
 * ```kotlin
 * @Composable
 * fun ContactForm() {
 *     var name by remember { mutableStateOf("") }
 *     var email by remember { mutableStateOf("") }
 *     var message by remember { mutableStateOf("") }
 *
 *     Form(onSubmit = { submitContact(name, email, message) }) {
 *         TextField(
 *             value = name,
 *             onValueChange = { name = it },
 *             label = "Name"
 *         )
 *         TextField(
 *             value = email,
 *             onValueChange = { email = it },
 *             label = "Email",
 *             type = "email"
 *         )
 *         TextArea(
 *             value = message,
 *             onValueChange = { message = it },
 *             label = "Message"
 *         )
 *         Button(
 *             onClick = { /* submit */ },
 *             label = "Send Message",
 *             variant = ButtonVariant.PRIMARY
 *         )
 *     }
 * }
 * ```
 *
 * @since 1.0.0
 */
package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Icon
import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.ModifierExtras.withAttribute
import code.yousef.summon.modifier.hover
import code.yousef.summon.modifier.transition
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Interactive button component for triggering actions.
 *
 * Button is a fundamental UI component that provides users with a way to trigger actions,
 * submit forms, navigate between views, or perform any interactive operation. It supports
 * multiple visual variants, accessibility features, and flexible content composition.
 *
 * ## Visual Variants
 *
 * The button supports several predefined visual styles through [ButtonVariant]:
 *
 * - **PRIMARY**: Main action button with prominent styling
 * - **SECONDARY**: Secondary actions with subdued styling
 * - **TERTIARY**: Minimal styling for less important actions
 * - **DANGER**: Critical or destructive actions (delete, remove)
 * - **SUCCESS**: Positive actions (save, confirm, submit)
 * - **WARNING**: Cautionary actions requiring attention
 * - **INFO**: Informational actions
 * - **LINK**: Text-only styling that looks like a hyperlink
 * - **GHOST**: Transparent background with subtle hover effects
 *
 * ## Icon Support
 *
 * Buttons can include icons alongside text for improved visual communication:
 * - Icons can be positioned at the start or end of the label
 * - Icon-only buttons are supported by omitting the label
 * - Icons inherit the button's color and sizing
 *
 * ## Accessibility
 *
 * The button component implements accessibility best practices:
 * - Proper semantic markup with `<button>` elements
 * - Keyboard navigation support with focus management
 * - Screen reader compatibility with proper labeling
 * - Disabled state handling with appropriate ARIA attributes
 * - Color contrast compliance across all variants
 *
 * ## State Management
 *
 * Button integrates seamlessly with Summon's reactive state system:
 * - Click handlers can update state and trigger recomposition
 * - Disabled state can be dynamically controlled
 * - Visual feedback reflects current application state
 *
 * ## Usage Examples
 *
 * ### Basic Button
 * ```kotlin
 * Button(
 *     onClick = { performAction() },
 *     label = "Click Me"
 * )
 * ```
 *
 * ### Styled Button with Icon
 * ```kotlin
 * Button(
 *     onClick = { saveDocument() },
 *     label = "Save",
 *     variant = ButtonVariant.PRIMARY,
 *     iconName = "save",
 *     iconPosition = IconPosition.START,
 *     modifier = Modifier().padding("16px")
 * )
 * ```
 *
 * ### Form Submit Button
 * ```kotlin
 * Button(
 *     onClick = { submitForm() },
 *     label = "Submit",
 *     variant = ButtonVariant.SUCCESS,
 *     disabled = !isFormValid,
 *     modifier = Modifier().fillMaxWidth()
 * )
 * ```
 *
 * ### Destructive Action
 * ```kotlin
 * Button(
 *     onClick = { deleteItem() },
 *     label = "Delete",
 *     variant = ButtonVariant.DANGER,
 *     iconName = "trash",
 *     modifier = Modifier().margin("8px")
 * )
 * ```
 *
 * ## Platform Rendering
 *
 * The button renders appropriately across platforms:
 * - **Browser**: Native `<button>` element with CSS styling and event handlers
 * - **Server**: HTML `<button>` with proper form integration
 * - **Desktop**: Platform-native button controls (when supported)
 *
 * ## Customization
 *
 * Beyond the built-in variants, buttons can be extensively customized:
 * - Custom colors through modifier styling
 * - Size variations using padding and typography modifiers
 * - Custom hover and focus effects
 * - Border and shadow styling
 * - Animation and transition effects
 *
 * @param onClick Callback function invoked when the button is clicked
 * @param label Text content displayed on the button
 * @param modifier Styling and layout modifiers to apply
 * @param variant Visual style variant determining appearance
 * @param disabled Whether the button is disabled and cannot be interacted with
 * @param iconName Optional icon name to display alongside the label
 * @param iconPosition Position of the icon relative to the label text
 * @sample ButtonSamples.BasicButton
 * @sample ButtonSamples.ButtonWithIcon
 * @sample ButtonSamples.DisabledButton
 * @see ButtonVariant
 * @see IconPosition
 * @see code.yousef.summon.components.display.Icon
 * @since 1.0.0
 */
@Composable
fun Button(
    onClick: (() -> Unit)? = null,
    label: String,
    modifier: Modifier = Modifier(),
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    disabled: Boolean = false,
    iconName: String? = null,
    iconPosition: IconPosition = IconPosition.START,
    dataAttributes: Map<String, String> = emptyMap()
) {
    val renderer = LocalPlatformRenderer.current

    // Generate a stable unique ID based on Button's stable properties
    val stableProps = listOf(
        label,
        variant.name,
        disabled.toString(),
        iconName ?: "no-icon",
        iconPosition.name
    ).joinToString("-")
    val sanitizedLabel = label.lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
    val uniqueHash = stableProps.hashCode().toUInt().toString(16)
    val uniqueId = buildString {
        append("button-")
        if (sanitizedLabel.isNotEmpty()) {
            append(sanitizedLabel.take(24))
            append('-')
        }
        append(uniqueHash)
    }

    // Apply base button styling
    val modifierWithId = (if (modifier.attributes.containsKey("data-summon-id")) {
        modifier
    } else {
        modifier.attribute("data-summon-id", uniqueId)
    }).dataAttributes(dataAttributes)

    val baseModifier = modifierWithId
        .style("display", "inline-flex")
        .style("align-items", "center")
        .style("justify-content", "center")
        .style("padding", "8px 16px")
        .style("border", "none")
        .style("border-radius", "6px")
        .style("cursor", "pointer")
        .style("transition", "all 0.2s ease")
        .style("margin", "0.25rem")
        .hover(
            Modifier()
                .style("transform", "translateY(-1px)")
        )

    // Apply variant-specific styling to the modifier
    val finalModifier = when (variant) {
        ButtonVariant.PRIMARY -> baseModifier
            .style("background-color", "#0d6efd")
            .style("color", "#ffffff")
            .transition("all 0.2s ease")

        ButtonVariant.SECONDARY -> baseModifier
            .style("background-color", "#6c757d")
            .style("color", "#ffffff")
            .transition("all 0.2s ease")

        ButtonVariant.DANGER -> baseModifier
            .style("background-color", "#dc3545 !important")
            .style("color", "#ffffff !important")
            .style("border", "none !important")
            .padding("10px 16px")
            .borderRadius("30px")
            .fontWeight("700")
            .style("text-transform", "capitalize !important")
            .style("box-shadow", "0 2px 4px rgba(220, 53, 69, 0.4) !important")
            .transition("all 0.2s ease")
            .hover(
                Modifier()
                    .style("background-color", "#bb2d3b !important")
                    .style("transform", "translateY(-3px) !important")
                    .style("box-shadow", "0 4px 8px rgba(220, 53, 69, 0.5) !important")
            )

        ButtonVariant.SUCCESS -> baseModifier
            .style("background-color", "#198754 !important")
            .style("color", "#ffffff !important")
            .padding("8px")
            .borderRadius("4px")
            .fontWeight("500")
            .transition("all 0.2s ease")
            .hover(
                Modifier()
                    .style("background-color", "#157347 !important")
            )

        ButtonVariant.WARNING -> baseModifier
            .style("background-color", "#ffc107 !important")
            .style("color", "#000000 !important")
            .padding("8px")
            .borderRadius("4px")
            .fontWeight("500")
            .transition("all 0.2s ease")
            .hover(
                Modifier()
                    .style("background-color", "#ffca2c !important")
            )

        ButtonVariant.INFO -> baseModifier
            .style("background-color", "#0dcaf0 !important")
            .style("color", "#000000 !important")
            .padding("8px")
            .borderRadius("4px")
            .fontWeight("500")
            .transition("all 0.2s ease")
            .hover(
                Modifier()
                    .style("background-color", "#31d2f2 !important")
            )

        ButtonVariant.LINK -> baseModifier
            .style("background-color", "transparent !important")
            .style("color", "#0d6efd !important")
            .padding("8px")
            .fontWeight("500")
            .style("text-decoration", "underline")
            .transition("all 0.2s ease")
            .hover(
                Modifier()
                    .style("color", "#0a58ca !important")
            )

        ButtonVariant.GHOST -> baseModifier
            .style("background-color", "transparent !important")
            .style("color", "inherit !important")
            .padding("8px")
            .borderRadius("4px")
            .border("1px", "solid", "transparent")
            .transition("all 0.2s ease")
            .hover(
                Modifier()
                    .style("background-color", "rgba(0, 0, 0, 0.05) !important")
            )

        ButtonVariant.TERTIARY -> baseModifier
            .style("background-color", "#f8f9fa !important")
            .style("color", "#000000 !important")
            .padding("8px")
            .borderRadius("4px")
            .border("1px", "solid", "#dee2e6")
            .fontWeight("500")
            .transition("all 0.2s ease")
            .hover(
                Modifier()
                    .style("background-color", "#e9ecef !important")
            )
    }
        .style("cursor", if (disabled) "not-allowed" else "pointer")
        .style("pointer-events", if (disabled) "none" else "auto")
        // Add data-variant attribute for CSS targeting
        .withAttribute("data-variant", variant.name.lowercase())
        .let {
            if (disabled) {
                it
                    .attribute("disabled", "true")
                    .attribute("aria-disabled", "true")
            } else {
                it
            }
        }

    // Use the renderButton method, passing onClick, modifier and content
    val safeOnClick = onClick ?: {}

    renderer.renderButton(
        onClick = if (disabled) {
            {}
        } else safeOnClick,
        modifier = finalModifier
    ) {
        // Button content
        if (iconName != null && iconPosition == IconPosition.START) {
            Icon(iconName)
        }

        Text(label)

        if (iconName != null && iconPosition == IconPosition.END) {
            Icon(iconName)
        }
    }
}

/**
 * Button style variant
 */
enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
    TERTIARY,
    DANGER,
    SUCCESS,
    WARNING,
    INFO,
    LINK,
    GHOST
}

/**
 * Position of the icon relative to the label
 */
enum class IconPosition {
    START,
    END
}

/**
 * Contains sample implementations of the Button component for documentation.
 */
object ButtonSamples {
    /**
     * Example of a basic button.
     */
    @Composable
    fun BasicButton() {
        Button(
            onClick = { println("Button clicked!") },
            label = "Click Me"
        )
    }

    /**
     * Example of a button with an icon.
     */
    @Composable
    fun ButtonWithIcon() {
        Button(
            onClick = { /* handle download */ },
            label = "Download",
            iconName = "download",
            variant = ButtonVariant.PRIMARY
        )
    }

    /**
     * Example of a disabled button.
     */
    @Composable
    fun DisabledButton() {
        Button(
            onClick = { /* won't be called when disabled */ },
            label = "Submit",
            disabled = true,
            variant = ButtonVariant.SECONDARY
        )
    }
} 
