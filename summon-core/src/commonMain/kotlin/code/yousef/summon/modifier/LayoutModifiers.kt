/**
 * # Layout Modifiers
 *
 * This file provides comprehensive layout modifier functions for the Summon UI framework.
 * These modifiers enable precise control over element sizing, positioning, spacing, and layout behavior
 * with type-safe CSS property mapping and cross-platform compatibility.
 *
 * ## Overview
 *
 * Layout modifiers are essential for creating responsive, well-structured user interfaces. This module provides:
 *
 * - **Sizing Control**: Width, height, min/max dimensions with type-safe values
 * - **Spacing Management**: Padding and margin with flexible value specification
 * - **Flexbox Properties**: Complete CSS Flexbox property support with enums
 * - **Grid Layout**: CSS Grid properties for advanced layouts
 * - **Positioning**: Absolute, relative, and fixed positioning with utilities
 * - **Advanced Layout**: Radial positioning, circular layouts, and animation effects
 *
 * ## Key Features
 *
 * ### Type-Safe Sizing
 * - Dimensional constraints with `minWidth`, `maxWidth`, `minHeight`, `maxHeight`
 * - Flexible sizing with `fillMaxWidth`, `fillMaxHeight` for container-based layouts
 * - Precise control with string-based values supporting all CSS units
 *
 * ### Flexible Spacing
 * - Individual side control with `paddingTop`, `marginLeft`, etc.
 * - Multi-value shortcuts for `padding(vertical, horizontal)`
 * - Named parameter functions `paddingOf`, `marginOf` for selective application
 *
 * ### Advanced Positioning
 * - Mathematical positioning with `radialPosition` using angles and radii
 * - Circular layout arrangement with `circularLayout` for orbital patterns
 * - Center alignment utilities with `centerAbsolute`, `fixedCenter`
 * - Relative offsets with `relativeOffset` for fine positioning adjustments
 *
 * ### Animation Integration
 * - Built-in floating effects with `floatingAnimation`
 * - Continuous rotation with `rotatingAnimation`
 * - Type-safe animation parameters using enum-based configuration
 *
 * ## Usage Examples
 *
 * ### Basic Layout Sizing
 * ```kotlin
 * // Responsive container with constraints
 * val containerModifier = Modifier()
 *     .fillMaxWidth()
 *     .minHeight("400px")
 *     .maxWidth("1200px")
 *     .padding("20px")
 *
 * // Fixed aspect ratio element
 * val cardModifier = Modifier()
 *     .width("300px")
 *     .height("200px")
 *     .margin("16px")
 * ```
 *
 * ### Flexible Spacing
 * ```kotlin
 * // Different padding for different sides
 * val asymmetricPadding = Modifier()
 *     .paddingTop("24px")
 *     .paddingBottom("16px")
 *     .paddingOf(left = "12px", right = "12px")
 *
 * // Margin with horizontal and vertical values
 * val centeredMargin = Modifier()
 *     .margin("0", "auto") // vertical 0, horizontal auto for centering
 * ```
 *
 * ### Flexbox Layout
 * ```kotlin
 * // Flexible container settings
 * val flexContainer = Modifier()
 *     .style("display", "flex")
 *     .flexWrap(FlexWrap.Wrap)
 *     .alignContent("space-between")
 *     .justifyItems("center")
 *
 * // Flexible item configuration
 * val flexItem = Modifier()
 *     .flexGrow(1)
 *     .flexShrink(0)
 *     .flexBasis("250px")
 *     .alignSelf("stretch")
 * ```
 *
 * ### Grid Layout
 * ```kotlin
 * // Grid item positioning
 * val gridItem = Modifier()
 *     .gridColumn("1 / 3")  // Span columns 1-2
 *     .gridRow("2")         // Place in row 2
 *     .gridArea("header")   // Named grid area
 * ```
 *
 * ### Advanced Positioning
 * ```kotlin
 * // Radial positioning for circular menus
 * val menuItem = Modifier()
 *     .radialPosition(RadialAngle.Deg45, RadialRadius.Medium)
 *
 * // Circular layout for avatars
 * val avatar = Modifier()
 *     .circularLayout(
 *         radius = RadialRadius.Large,
 *         totalItems = 6,
 *         currentIndex = itemIndex
 *     )
 *
 * // Centered modal overlay
 * val modalOverlay = Modifier()
 *     .fixedCenter()
 *     .size("400px", "300px")
 * ```
 *
 * ### Animation Effects
 * ```kotlin
 * // Floating button effect
 * val floatingButton = Modifier()
 *     .floatingAnimation(
 *         duration = AnimationDuration.Medium,
 *         intensity = FloatIntensity.Gentle
 *     )
 *
 * // Loading spinner rotation
 * val spinner = Modifier()
 *     .rotatingAnimation(
 *         speed = RotationSpeed.Fast,
 *         clockwise = true
 *     )
 * ```
 *
 * ## Performance Considerations
 *
 * - **Layout Calculations**: Complex positioning calculations are performed once during modifier creation
 * - **CSS Generation**: Properties map directly to CSS for efficient rendering
 * - **Animation Efficiency**: Animations use CSS transforms for hardware acceleration
 * - **Memory Usage**: Extension functions don't create additional object overhead
 *
 * ## Cross-Platform Compatibility
 *
 * - **Browser**: All properties map to standard CSS specifications
 * - **JVM**: Properties serialize to inline CSS styles for server-side rendering
 * - **JavaScript Interop**: Functions work seamlessly with JS objects and values
 *
 * ## Type Safety
 *
 * - **Enum Support**: Where applicable, both string and enum overloads are provided
 * - **Unit Extensions**: Integration with unit extension functions (`.px`, `.percent`)
 * - **Validation**: Runtime checks prevent invalid positioning and sizing values
 *
 * @see code.yousef.summon.modifier.Modifier for base modifier functionality
 * @see code.yousef.summon.modifier.StylingModifiers for appearance-related modifiers
 * @see code.yousef.summon.animation for animation system integration
 * @since 1.0.0
 */
package codes.yousef.summon.modifier

import codes.yousef.summon.extensions.percent
import codes.yousef.summon.extensions.px

/**
 * Sets the minimum width constraint for the element.
 *
 * The element will never be smaller than this width, even if its content would naturally
 * take up less space. This is essential for maintaining layout consistency and preventing
 * elements from becoming too narrow.
 *
 * ## Examples
 * ```kotlin
 * // Button with minimum width
 * val button = Modifier()
 *     .minWidth("120px")
 *     .padding("8px 16px")
 *
 * // Responsive card with flexible minimum
 * val card = Modifier()
 *     .minWidth("300px")
 *     .maxWidth("600px")
 *     .width("50%")
 * ```
 *
 * @param value The minimum width value (e.g., "200px", "10rem", "30%")
 * @return A new Modifier with the min-width constraint applied
 * @see maxWidth for setting maximum width constraints
 * @see width for setting exact width
 * @since 1.0.0
 */
fun Modifier.minWidth(value: String): Modifier =
    style("min-width", value)

/**
 * Sets the minimum height constraint for the element.
 *
 * The element will never be smaller than this height, ensuring that elements
 * maintain a minimum vertical presence even with limited content.
 *
 * ## Examples
 * ```kotlin
 * // Input field with minimum height
 * val textArea = Modifier()
 *     .minHeight("100px")
 *     .fillMaxWidth()
 *
 * // Container with minimum viewport height
 * val section = Modifier()
 *     .minHeight("100vh")
 *     .padding("20px")
 * ```
 *
 * @param value The minimum height value (e.g., "150px", "5rem", "20vh")
 * @return A new Modifier with the min-height constraint applied
 * @see maxHeight for setting maximum height constraints
 * @see height for setting exact height
 * @since 1.0.0
 */
fun Modifier.minHeight(value: String): Modifier =
    style("min-height", value)

/**
 * Sets the maximum height constraint for the element.
 *
 * The element will never grow larger than this height, with overflow behavior
 * determined by CSS overflow properties. Useful for preventing elements from
 * becoming too tall and maintaining layout boundaries.
 *
 * ## Examples
 * ```kotlin
 * // Scrollable content area
 * val scrollableArea = Modifier()
 *     .maxHeight("400px")
 *     .overflowY("auto")
 *     .fillMaxWidth()
 *
 * // Limited height image
 * val heroImage = Modifier()
 *     .maxHeight("50vh")
 *     .width("100%")
 *     .objectFit("cover")
 * ```
 *
 * @param value The maximum height value (e.g., "500px", "80vh", "30rem")
 * @return A new Modifier with the max-height constraint applied
 * @see minHeight for setting minimum height constraints
 * @see height for setting exact height
 * @see overflowY for controlling vertical overflow behavior
 * @since 1.0.0
 */
fun Modifier.maxHeight(value: String): Modifier =
    style("max-height", value)

/**
 * Constrains element proportions using CSS aspect-ratio.
 *
 * @param ratio Numeric ratio such as 1.5 for 3:2 layouts
 */
fun Modifier.aspectRatio(ratio: Number): Modifier =
    style("aspect-ratio", ratio.toString())

private fun Number.toCssRatioPart(): String {
    val doubleValue = this.toDouble()
    return if (doubleValue % 1.0 == 0.0) {
        doubleValue.toLong().toString()
    } else {
        this.toString()
    }
}

/**
 * Constrains element proportions using width/height inputs.
 *
 * @param width Ratio numerator
 * @param height Ratio denominator
 */
fun Modifier.aspectRatio(width: Number, height: Number): Modifier =
    style("aspect-ratio", "${width.toCssRatioPart()} / ${height.toCssRatioPart()}")

/**
 * Applies CSS inset shorthand.
 */
fun Modifier.inset(value: String): Modifier = style("inset", value)

/**
 * Applies CSS inset shorthand with vertical/horizontal values.
 */
fun Modifier.inset(vertical: String, horizontal: String): Modifier =
    style("inset", "$vertical $horizontal")

/**
 * Applies CSS inset shorthand with explicit sides.
 */
fun Modifier.inset(top: String, right: String, bottom: String, left: String): Modifier =
    style("inset", "$top $right $bottom $left")

/**
 * Fine-grained inset helper mapping to top/right/bottom/left individually.
 */
fun Modifier.positionInset(
    top: String? = null,
    right: String? = null,
    bottom: String? = null,
    left: String? = null
): Modifier {
    var result = this
    top?.let { result = result.style("top", it) }
    right?.let { result = result.style("right", it) }
    bottom?.let { result = result.style("bottom", it) }
    left?.let { result = result.style("left", it) }
    return result
}

// fillMaxWidth() removed - exists as member function in Modifier class

// padding(String) removed - exists as member function in Modifier class

/**
 * Sets different padding values for vertical and horizontal directions.
 *
 * This is a convenient shorthand for applying symmetric padding where the top and bottom
 * have the same value, and left and right have the same value.
 *
 * ## Examples
 * ```kotlin
 * // Card with more vertical padding than horizontal
 * val card = Modifier()
 *     .padding("24px", "16px") // 24px top/bottom, 16px left/right
 *
 * // Button with minimal vertical, generous horizontal padding
 * val button = Modifier()
 *     .padding("8px", "20px")
 *     .backgroundColor("#007bff")
 * ```
 *
 * @param vertical Padding for top and bottom sides
 * @param horizontal Padding for left and right sides
 * @return A new Modifier with symmetric padding applied
 * @see padding for uniform padding on all sides
 * @see paddingOf for selective side padding
 * @since 1.0.0
 */
fun Modifier.padding(vertical: String, horizontal: String): Modifier =
    style("padding", "$vertical $horizontal")

/**
 * Sets padding for the top side of the element only.
 *
 * Useful for fine-tuning spacing without affecting other sides,
 * particularly when building complex layouts with precise spacing requirements.
 *
 * ## Examples
 * ```kotlin
 * // Header with extra top spacing
 * val header = Modifier()
 *     .paddingTop("40px")
 *     .padding("0", "20px") // 0 vertical, 20px horizontal
 *
 * // List item with separating top padding
 * val listItem = Modifier()
 *     .paddingTop("12px")
 *     .borderTop("1px", "solid", "#e0e0e0")
 * ```
 *
 * @param value The top padding value (e.g., "16px", "1rem", "2%")
 * @return A new Modifier with top padding applied
 * @see paddingBottom for bottom padding
 * @see paddingOf for multiple side padding
 * @since 1.0.0
 */
fun Modifier.paddingTop(value: String): Modifier =
    style("padding-top", value)

/**
 * Sets padding for the right side of the element only.
 *
 * Particularly useful for RTL/LTR layouts and creating asymmetric spacing
 * that responds to directional text flow.
 *
 * ## Examples
 * ```kotlin
 * // Form field with extra right spacing for icon
 * val inputField = Modifier()
 *     .paddingRight("40px") // Space for search icon
 *     .padding("12px")
 *
 * // Navigation item with directional padding
 * val navItem = Modifier()
 *     .paddingRight("24px")
 *     .borderRight("2px", "solid", "#007bff")
 * ```
 *
 * @param value The right padding value (e.g., "16px", "1rem", "2%")
 * @return A new Modifier with right padding applied
 * @see paddingLeft for left padding
 * @see paddingOf for multiple side padding
 * @since 1.0.0
 */
fun Modifier.paddingRight(value: String): Modifier =
    style("padding-right", value)

/**
 * Sets padding for the bottom side of the element only.
 *
 * Essential for creating vertical rhythm and proper spacing between sections,
 * particularly useful in typography and content layout.
 *
 * ## Examples
 * ```kotlin
 * // Section with bottom spacing
 * val section = Modifier()
 *     .paddingBottom("32px")
 *     .borderBottom("1px", "solid", "#ddd")
 *
 * // Footer with extra bottom padding
 * val footer = Modifier()
 *     .paddingBottom("20px")
 *     .backgroundColor("#f8f9fa")
 * ```
 *
 * @param value The bottom padding value (e.g., "16px", "1rem", "2%")
 * @return A new Modifier with bottom padding applied
 * @see paddingTop for top padding
 * @see paddingOf for multiple side padding
 * @since 1.0.0
 */
fun Modifier.paddingBottom(value: String): Modifier =
    style("padding-bottom", value)

/**
 * Sets padding for the left side of the element only.
 *
 * Important for creating indentation, nesting visual hierarchy,
 * and directional layout adjustments.
 *
 * ## Examples
 * ```kotlin
 * // Nested list item with indentation
 * val nestedItem = Modifier()
 *     .paddingLeft("32px")
 *     .borderLeft("3px", "solid", "#007bff")
 *
 * // Form label with left alignment
 * val label = Modifier()
 *     .paddingLeft("4px")
 *     .fontWeight("600")
 * ```
 *
 * @param value The left padding value (e.g., "16px", "1rem", "2%")
 * @return A new Modifier with left padding applied
 * @see paddingRight for right padding
 * @see paddingOf for multiple side padding
 * @since 1.0.0
 */
fun Modifier.paddingLeft(value: String): Modifier =
    style("padding-left", value)

// margin(String) removed - exists as member function in Modifier class

/**
 * Sets different margin values for vertical and horizontal directions.
 *
 * Similar to the padding variant, this creates symmetric margins where top and bottom
 * receive the same value, and left and right receive the same value.
 *
 * ## Examples
 * ```kotlin
 * // Card with vertical spacing but centered horizontally
 * val card = Modifier()
 *     .margin("20px", "auto") // 20px top/bottom, auto left/right (centers)
 *     .maxWidth("600px")
 *
 * // Button group with minimal vertical, generous horizontal spacing
 * val buttonGroup = Modifier()
 *     .margin("8px", "16px")
 *     .style("display", "flex")
 * ```
 *
 * @param vertical Margin for top and bottom sides
 * @param horizontal Margin for left and right sides
 * @return A new Modifier with symmetric margin applied
 * @see margin for uniform margin on all sides
 * @see marginOf for selective side margin
 * @since 1.0.0
 */
fun Modifier.margin(vertical: String, horizontal: String): Modifier =
    style("margin", "$vertical $horizontal")

/**
 * Sets margin for all four sides individually with explicit control.
 * @deprecated This extension is shadowed by member function. Use member function instead.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
fun Modifier.margin(top: String, right: String, bottom: String, left: String): Modifier =
    style("margin", "$top $right $bottom $left")

/**
 * Centers the element horizontally by applying `margin: {vertical} auto`.
 *
 * @param vertical Vertical margin (default "0")
 */
fun Modifier.centerHorizontally(vertical: String = "0"): Modifier =
    margin(vertical, "auto")

/**
 * Sets margin for specific sides using named parameters for enhanced readability.
 *
 * This function provides a more declarative approach to setting margins,
 * allowing you to specify only the sides you need while keeping the code readable.
 * Unspecified sides will use "0" when multiple sides are provided, or individual
 * CSS properties when only one side is specified.
 *
 * ## Examples
 * ```kotlin
 * // Only bottom margin for list items
 * val listItem = Modifier()
 *     .marginOf(bottom = "8px")
 *
 * // Top and bottom margins for sections
 * val section = Modifier()
 *     .marginOf(top = "32px", bottom = "24px")
 *
 * // Centering with horizontal auto margins
 * val centeredCard = Modifier()
 *     .marginOf(left = "auto", right = "auto")
 *     .maxWidth("500px")
 *
 * // Complex asymmetric margin
 * val complexLayout = Modifier()
 *     .marginOf(top = "20px", right = "16px", left = "24px")
 * ```
 *
 * ## Behavior
 * - **Single parameter**: Uses individual CSS property (e.g., `margin-top`)
 * - **Multiple parameters**: Uses CSS shorthand with "0" for unspecified sides
 * - **Auto margins**: Supports "auto" for centering and flexible layouts
 *
 * @param top Optional margin for the top side
 * @param right Optional margin for the right side
 * @param bottom Optional margin for the bottom side
 * @param left Optional margin for the left side
 * @return A new Modifier with the specified margins applied
 * @see margin for positional parameter versions
 * @see paddingOf for equivalent padding function
 * @since 1.0.0
 */
fun Modifier.marginOf(
    top: String? = null,
    right: String? = null,
    bottom: String? = null,
    left: String? = null
): Modifier {
    // If only one parameter is provided, set that specific margin direction
    if (top != null && right == null && bottom == null && left == null) {
        return style("margin-top", top)
    }
    if (right != null && top == null && bottom == null && left == null) {
        return style("margin-right", right)
    }
    if (bottom != null && top == null && right == null && left == null) {
        return style("margin-bottom", bottom)
    }
    if (left != null && top == null && right == null && bottom == null) {
        return style("margin-left", left)
    }

    // If multiple parameters are provided, construct the margin string
    val marginParts = arrayOf(
        top ?: "0",
        right ?: "0",
        bottom ?: "0",
        left ?: "0"
    )
    return style("margin", marginParts.joinToString(" "))
}

/**
 * Sets padding for specific sides using named parameters for enhanced readability.
 *
 * This function provides a declarative approach to setting padding, allowing precise
 * control over spacing while maintaining code readability. It's particularly useful
 * for complex layouts where you need padding on only certain sides.
 *
 * ## Examples
 * ```kotlin
 * // Icon with only right padding for spacing from text
 * val iconWithSpacing = Modifier()
 *     .paddingOf(right = "8px")
 *
 * // Header with top and bottom padding
 * val header = Modifier()
 *     .paddingOf(top = "24px", bottom = "16px")
 *     .backgroundColor("#f8f9fa")
 *
 * // Form field with asymmetric padding for better visual balance
 * val formField = Modifier()
 *     .paddingOf(top = "12px", bottom = "10px", left = "16px", right = "40px")
 *
 * // Content area with selective padding
 * val contentArea = Modifier()
 *     .paddingOf(left = "20px", right = "20px", bottom = "32px")
 * ```
 *
 * ## Behavior
 * - **Single parameter**: Uses individual CSS property (e.g., `padding-left`)
 * - **Multiple parameters**: Uses CSS shorthand with "0" for unspecified sides
 * - **Flexible spacing**: Perfect for responsive and adaptive layouts
 *
 * @param top Optional padding for the top side
 * @param right Optional padding for the right side
 * @param bottom Optional padding for the bottom side
 * @param left Optional padding for the left side
 * @return A new Modifier with the specified padding applied
 * @see padding for positional parameter versions
 * @see marginOf for equivalent margin function
 * @since 1.0.0
 */
fun Modifier.paddingOf(
    top: String? = null,
    right: String? = null,
    bottom: String? = null,
    left: String? = null
): Modifier {
    // If only one parameter is provided, set that specific padding direction
    if (top != null && right == null && bottom == null && left == null) {
        return style("padding-top", top)
    }
    if (right != null && top == null && bottom == null && left == null) {
        return style("padding-right", right)
    }
    if (bottom != null && top == null && right == null && left == null) {
        return style("padding-bottom", bottom)
    }
    if (left != null && top == null && right == null && bottom == null) {
        return style("padding-left", left)
    }

    // If multiple parameters are provided, construct the padding string
    val paddingParts = arrayOf(
        top ?: "0",
        right ?: "0",
        bottom ?: "0",
        left ?: "0"
    )
    return style("padding", paddingParts.joinToString(" "))
}

// marginTop(String) removed - exists as member function in Modifier class

// marginRight(String) removed - exists as member function in Modifier class

// marginBottom(String) removed - exists as member function in Modifier class

// marginLeft(String) removed - exists as member function in Modifier class

// ========================
// FLEXBOX LAYOUT PROPERTIES
// ========================

/**
 * Controls how flex items wrap within a flex container.
 *
 * This property determines whether flex items are forced into a single line
 * or allowed to wrap onto multiple lines, and controls the direction of
 * the cross axis when wrapping occurs.
 *
 * ## Examples
 * ```kotlin
 * // Allow items to wrap to new lines
 * val flexContainer = Modifier()
 *     .style("display", "flex")
 *     .flexWrap("wrap")
 *     .gap("16px")
 *
 * // Prevent wrapping (default behavior)
 * val inlineContainer = Modifier()
 *     .style("display", "flex")
 *     .flexWrap("nowrap")
 * ```
 *
 * @param value CSS flex-wrap value ("nowrap", "wrap", "wrap-reverse")
 * @return A new Modifier with flex-wrap applied
 * @see flexWrap for type-safe enum version
 * @since 1.0.0
 */
fun Modifier.flexWrap(value: String): Modifier =
    style("flex-wrap", value)

/**
 * Controls how flex items wrap within a flex container using type-safe values.
 *
 * Provides compile-time safety and IDE autocompletion for flex-wrap values,
 * preventing common typos and invalid CSS values.
 *
 * ## Examples
 * ```kotlin
 * // Responsive card grid that wraps
 * val cardGrid = Modifier()
 *     .style("display", "flex")
 *     .flexWrap(FlexWrap.Wrap)
 *     .justifyContent("space-between")
 *
 * // Navigation bar that doesn't wrap
 * val navbar = Modifier()
 *     .style("display", "flex")
 *     .flexWrap(FlexWrap.NoWrap)
 *     .alignItems("center")
 * ```
 *
 * @param value FlexWrap enum value for type-safe wrapping behavior
 * @return A new Modifier with flex-wrap applied
 * @see flexWrap for string version
 * @since 1.0.0
 */
fun Modifier.flexWrap(value: FlexWrap): Modifier =
    style("flex-wrap", value.toString())

/**
 * Defines the flex grow factor for a flex item.
 *
 * Determines how much the item should grow relative to other flex items
 * when distributing available space in the flex container. Higher values
 * mean the item will take up proportionally more space.
 *
 * ## Examples
 * ```kotlin
 * // Main content area that takes most space
 * val mainContent = Modifier()
 *     .flexGrow(1)
 *     .padding("20px")
 *
 * // Sidebar that grows but takes less space
 * val sidebar = Modifier()
 *     .flexGrow(0)
 *     .width("250px")
 *
 * // Multiple items with proportional growth
 * val primarySection = Modifier().flexGrow(3)  // Takes 3/5 of space
 * val secondarySection = Modifier().flexGrow(2) // Takes 2/5 of space
 * ```
 *
 * @param value Grow factor (0 = no growth, positive integers for proportional growth)
 * @return A new Modifier with flex-grow applied
 * @see flexShrink for shrinking behavior
 * @see flexBasis for initial size before growing/shrinking
 * @since 1.0.0
 */
fun Modifier.flexGrow(value: Int): Modifier =
    style("flex-grow", value.toString())

/**
 * Defines the flex shrink factor for a flex item.
 *
 * Determines how much the item should shrink relative to other flex items
 * when there isn't enough space in the flex container. Higher values
 * mean the item will shrink more aggressively.
 *
 * ## Examples
 * ```kotlin
 * // Text content that can shrink significantly
 * val flexibleText = Modifier()
 *     .flexShrink(1)
 *     .minWidth("0") // Allow text to shrink below intrinsic width
 *
 * // Fixed-size button that shouldn't shrink
 * val fixedButton = Modifier()
 *     .flexShrink(0)
 *     .width("120px")
 *
 * // Image that can shrink moderately
 * val responsiveImage = Modifier()
 *     .flexShrink(2)
 *     .maxWidth("100%")
 * ```
 *
 * @param value Shrink factor (0 = no shrinking, positive integers for proportional shrinking)
 * @return A new Modifier with flex-shrink applied
 * @see flexGrow for growing behavior
 * @see flexBasis for initial size before growing/shrinking
 * @since 1.0.0
 */
fun Modifier.flexShrink(value: Int): Modifier =
    style("flex-shrink", value.toString())

/**
 * Sets the initial main size of a flex item before free space is distributed.
 *
 * Defines the default size of an element before the remaining space is distributed
 * according to the flex factors. This can be a length (px, em, %) or keywords
 * like "auto" or "content".
 *
 * ## Examples
 * ```kotlin
 * // Card with minimum basis that can grow
 * val expandingCard = Modifier()
 *     .flexBasis("300px")
 *     .flexGrow(1)
 *     .maxWidth("600px")
 *
 * // Item that uses content size as basis
 * val contentSizedItem = Modifier()
 *     .flexBasis("auto")
 *     .flexShrink(0)
 *
 * // Equal-width columns in percentage
 * val column = Modifier()
 *     .flexBasis("33.333%")
 *     .padding("16px")
 * ```
 *
 * @param value Initial size ("auto", "content", or any CSS length value)
 * @return A new Modifier with flex-basis applied
 * @see flexGrow for growth behavior
 * @see flexShrink for shrinking behavior
 * @since 1.0.0
 */
fun Modifier.flexBasis(value: String): Modifier =
    style("flex-basis", value)

/**
 * Applies the flex shorthand (`flex: grow shrink basis`) in a type-safe way.
 *
 * @param grow Flex-grow factor (e.g., 1)
 * @param shrink Flex-shrink factor (e.g., 1)
 * @param basis Flex-basis value (e.g., "480px", "30%")
 */
fun Modifier.flex(grow: Number, shrink: Number, basis: String): Modifier =
    style("flex", "${grow.toString()} ${shrink.toString()} $basis")

/**
 * Overrides the align-items property for individual flex items.
 *
 * Allows individual flex items to have a different alignment than what's
 * specified by the container's align-items property, providing fine-grained
 * control over cross-axis alignment.
 *
 * ## Examples
 * ```kotlin
 * // Item that aligns to bottom while others center
 * val bottomAlignedItem = Modifier()
 *     .alignSelf("flex-end")
 *     .padding("8px")
 *
 * // Item that stretches to full height
 * val stretchedItem = Modifier()
 *     .alignSelf("stretch")
 *     .backgroundColor("#f0f0f0")
 *
 * // Item with baseline alignment for text
 * val baselineItem = Modifier()
 *     .alignSelf("baseline")
 *     .fontSize("18px")
 * ```
 *
 * @param value CSS align-self value ("auto", "flex-start", "flex-end", "center", "baseline", "stretch")
 * @return A new Modifier with align-self applied
 * @see alignContent for multi-line alignment
 * @since 1.0.0
 */
fun Modifier.alignSelf(value: String): Modifier =
    style("align-self", value)

/**
 * Aligns flex lines when there's extra space in the cross-axis.
 *
 * This property only has an effect when the flex container has multiple lines
 * (flex-wrap is set to wrap) and there's extra space in the cross-axis direction.
 *
 * ## Examples
 * ```kotlin
 * // Center multiple wrapped lines
 * val wrappedContainer = Modifier()
 *     .style("display", "flex")
 *     .flexWrap("wrap")
 *     .alignContent("center")
 *     .height("400px")
 *
 * // Distribute lines with space between
 * val distributedContainer = Modifier()
 *     .style("display", "flex")
 *     .flexWrap("wrap")
 *     .alignContent("space-between")
 * ```
 *
 * @param value CSS align-content value ("stretch", "flex-start", "flex-end", "center", "space-between", "space-around", "space-evenly")
 * @return A new Modifier with align-content applied
 * @see alignSelf for individual item alignment
 * @since 1.0.0
 */
fun Modifier.alignContent(value: String): Modifier =
    style("align-content", value)

// ========================
// CSS GRID LAYOUT PROPERTIES
// ========================

/**
 * Sets the default justification for grid items within their grid areas.
 *
 * Defines the default justify-self value for all grid items, controlling
 * how they align within their grid cells along the inline (row) axis.
 *
 * ## Examples
 * ```kotlin
 * // Grid container with centered items
 * val centeredGrid = Modifier()
 *     .style("display", "grid")
 *     .style("grid-template-columns", "repeat(3, 1fr)")
 *     .justifyItems("center")
 *     .gap("16px")
 *
 * // Grid with items aligned to end
 * val endAlignedGrid = Modifier()
 *     .style("display", "grid")
 *     .justifyItems("end")
 *     .padding("20px")
 * ```
 *
 * @param value CSS justify-items value ("stretch", "start", "end", "center", "baseline")
 * @return A new Modifier with justify-items applied
 * @see justifySelf for individual item justification
 * @since 1.0.0
 */
fun Modifier.justifyItems(value: String): Modifier =
    style("justify-items", value)

/**
 * Justifies a grid item within its grid area along the inline (row) axis.
 *
 * Overrides the grid container's justify-items value for this specific item,
 * allowing fine-grained control over individual grid item positioning.
 *
 * ## Examples
 * ```kotlin
 * // Grid item aligned to the right of its cell
 * val rightAlignedItem = Modifier()
 *     .justifySelf("end")
 *     .padding("8px")
 *
 * // Grid item that stretches to fill its cell
 * val stretchedGridItem = Modifier()
 *     .justifySelf("stretch")
 *     .backgroundColor("#e0e0e0")
 *
 * // Grid item centered in its cell
 * val centeredGridItem = Modifier()
 *     .justifySelf("center")
 *     .alignSelf("center")
 * ```
 *
 * @param value CSS justify-self value ("auto", "start", "end", "center", "stretch", "baseline")
 * @return A new Modifier with justify-self applied
 * @see justifyItems for container-level justification
 * @since 1.0.0
 */
fun Modifier.justifySelf(value: String): Modifier =
    style("justify-self", value)

/**
 * Sets the spacing between grid tracks (both rows and columns).
 *
 * Shorthand for setting both row-gap and column-gap in a grid container.
 * Controls the size of the gutters between grid tracks, making it easier
 * to create consistently spaced grid layouts.
 *
 * ## Examples
 * ```kotlin
 * // Photo grid with uniform spacing
 * val photoGrid = Modifier()
 *     .style("display", "grid")
 *     .style("grid-template-columns", "repeat(auto-fit, minmax(200px, 1fr))")
 *     .gridGap("16px")
 *
 * // Card layout with larger spacing
 * val cardLayout = Modifier()
 *     .style("display", "grid")
 *     .style("grid-template-columns", "repeat(3, 1fr)")
 *     .gridGap("24px")
 *     .padding("20px")
 * ```
 *
 * @param value Gap size (e.g., "16px", "1rem", "2em") or "row-gap column-gap" for different values
 * @return A new Modifier with grid-gap applied
 * @see style for setting row-gap and column-gap individually
 * @since 1.0.0
 */
fun Modifier.gridGap(value: String): Modifier =
    style("grid-gap", value)

/**
 * Assigns a grid item to a named grid area or specific grid coordinates.
 *
 * Provides a convenient way to place grid items in named areas defined by
 * grid-template-areas, or specify exact row and column positions using
 * the shorthand syntax.
 *
 * ## Examples
 * ```kotlin
 * // Using named grid areas
 * val header = Modifier()
 *     .gridArea("header")
 *     .backgroundColor("#f0f0f0")
 *
 * val sidebar = Modifier()
 *     .gridArea("sidebar")
 *     .width("250px")
 *
 * // Using coordinate syntax (row-start / column-start / row-end / column-end)
 * val featuredItem = Modifier()
 *     .gridArea("1 / 1 / 3 / 3") // Spans from row 1-3, column 1-3
 *     .backgroundColor("#007bff")
 *
 * // Spanning specific tracks
 * val wideItem = Modifier()
 *     .gridArea("2 / 1 / 3 / -1") // Row 2-3, all columns
 * ```
 *
 * @param value Named area or grid coordinates (e.g., "header", "1 / 2 / 3 / 4")
 * @return A new Modifier with grid-area applied
 * @see gridColumn for column-specific placement
 * @see gridRow for row-specific placement
 * @since 1.0.0
 */
fun Modifier.gridArea(value: String): Modifier =
    style("grid-area", value)

/**
 * Specifies which grid columns a grid item occupies.
 *
 * Controls the horizontal placement and spanning of grid items using
 * line numbers, names, or the span keyword. Provides precise control
 * over column positioning in grid layouts.
 *
 * ## Examples
 * ```kotlin
 * // Span across multiple columns
 * val wideHeader = Modifier()
 *     .gridColumn("1 / -1") // From first to last column
 *     .backgroundColor("#e9ecef")
 *
 * // Specific column range
 * val content = Modifier()
 *     .gridColumn("2 / 4") // From column 2 to 4
 *     .padding("20px")
 *
 * // Span a number of columns
 * val spanningItem = Modifier()
 *     .gridColumn("span 2") // Span 2 columns from current position
 *
 * // Using named lines
 * val namedPlacement = Modifier()
 *     .gridColumn("content-start / content-end")
 * ```
 *
 * @param value Column specification (e.g., "1", "1 / 3", "span 2", "content-start / content-end")
 * @return A new Modifier with grid-column applied
 * @see gridRow for row placement
 * @see gridArea for combined row/column placement
 * @since 1.0.0
 */
fun Modifier.gridColumn(value: String): Modifier =
    style("grid-column", value)

/**
 * Specifies which grid rows a grid item occupies.
 *
 * Controls the vertical placement and spanning of grid items using
 * line numbers, names, or the span keyword. Essential for creating
 * complex grid layouts with varying item heights.
 *
 * ## Examples
 * ```kotlin
 * // Tall sidebar spanning multiple rows
 * val sidebar = Modifier()
 *     .gridRow("1 / 4") // From row 1 to 4
 *     .backgroundColor("#f8f9fa")
 *
 * // Item spanning specific number of rows
 * val tallCard = Modifier()
 *     .gridRow("span 3") // Span 3 rows
 *     .borderRadius("8px")
 *
 * // Specific row placement
 * val footer = Modifier()
 *     .gridRow("5") // Place in row 5
 *     .gridColumn("1 / -1") // Span all columns
 * ```
 *
 * @param value Row specification (e.g., "1", "2 / 4", "span 2", "footer-start / footer-end")
 * @return A new Modifier with grid-row applied
 * @see gridColumn for column placement
 * @see gridArea for combined row/column placement
 * @since 1.0.0
 */
fun Modifier.gridRow(value: String): Modifier =
    style("grid-row", value)

// ========================
// LAYOUT UTILITY PROPERTIES
// ========================

/**
 * Sets the stacking order of the element using string values.
 *
 * Alternative to the integer version that accepts string values,
 * useful for CSS custom properties or calculated values.
 *
 * ## Examples
 * ```kotlin
 * // Using CSS custom property
 * val dynamicLayer = Modifier()
 *     .zIndex("var(--modal-z-index)")
 *
 * // Calculated z-index
 * val contextualLayer = Modifier()
 *     .zIndex("calc(var(--base-z) + 10)")
 * ```
 *
 * @param value Z-index value as string (supports CSS functions and variables)
 * @return A new Modifier with z-index applied
 * @see zIndex for integer version
 * @since 1.0.0
 */
fun Modifier.zIndex(value: String): Modifier =
    style("z-index", value)

/**
 * Controls horizontal overflow behavior when content exceeds container width.
 *
 * Determines how content that overflows the element's content area horizontally
 * should be handled. Essential for controlling scrolling and clipping behavior.
 *
 * ## Examples
 * ```kotlin
 * // Horizontal scrolling for wide content
 * val horizontalScroll = Modifier()
 *     .overflowX("auto")
 *     .whiteSpace("nowrap")
 *
 * // Hide horizontal overflow
 * val clippedContent = Modifier()
 *     .overflowX("hidden")
 *     .maxWidth("300px")
 *
 * // Force scrollbar
 * val alwaysScroll = Modifier()
 *     .overflowX("scroll")
 *     .height("200px")
 * ```
 *
 * @param value CSS overflow-x value ("visible", "hidden", "scroll", "auto")
 * @return A new Modifier with overflow-x applied
 * @see overflowY for vertical overflow control
 * @since 1.0.0
 */
fun Modifier.overflowX(value: String): Modifier =
    style("overflow-x", value)

/**
 * Controls vertical overflow behavior when content exceeds container height.
 *
 * Determines how content that overflows the element's content area vertically
 * should be handled. Critical for scrollable containers and content clipping.
 *
 * ## Examples
 * ```kotlin
 * // Scrollable content area
 * val scrollableArea = Modifier()
 *     .overflowY("auto")
 *     .maxHeight("400px")
 *     .padding("16px")
 *
 * // Hidden overflow for truncated content
 * val truncatedContent = Modifier()
 *     .overflowY("hidden")
 *     .height("100px")
 *
 * // Chat messages with forced scrollbar
 * val chatArea = Modifier()
 *     .overflowY("scroll")
 *     .height("300px")
 * ```
 *
 * @param value CSS overflow-y value ("visible", "hidden", "scroll", "auto")
 * @return A new Modifier with overflow-y applied
 * @see overflowX for horizontal overflow control
 * @see maxHeight for height constraints
 * @since 1.0.0
 */
fun Modifier.overflowY(value: String): Modifier =
    style("overflow-y", value)

/**
 * Controls the visibility of the element without affecting layout.
 *
 * Unlike display:none, visibility:hidden preserves the element's space
 * in the layout while making it invisible. Useful for conditional
 * visibility where layout stability is important.
 *
 * ## Examples
 * ```kotlin
 * // Conditionally hidden element that maintains space
 * val conditionalElement = Modifier()
 *     .visibility(if (showElement) "visible" else "hidden")
 *     .padding("16px")
 *
 * // Loading placeholder
 * val loadingPlaceholder = Modifier()
 *     .visibility("hidden")
 *     .backgroundColor("#f0f0f0")
 *     .height("200px")
 *
 * // Accessible but invisible content
 * val screenReaderOnly = Modifier()
 *     .visibility("hidden")
 *     .position("absolute")
 * ```
 *
 * @param value CSS visibility value ("visible", "hidden", "collapse")
 * @return A new Modifier with visibility applied
 * @see opacity for transparency-based hiding
 * @since 1.0.0
 */
fun Modifier.visibility(value: String): Modifier =
    style("visibility", value)

/**
 * Type-safe overload for CSS visibility values.
 *
 * @param value Visibility enum (Visible, Hidden, Collapse)
 * @return A new Modifier with visibility applied
 */
fun Modifier.visibility(value: Visibility): Modifier =
    style("visibility", value.toString())

// fontSize(String) removed - exists as member function in Modifier class

// cursor(String) removed - exists as member function in Modifier class

/**
 * Sets the cursor appearance using type-safe Cursor enum values.
 *
 * Provides compile-time safety for cursor values and prevents typos
 * while offering IDE autocompletion for all available cursor types.
 *
 * ## Examples
 * ```kotlin
 * // Interactive button cursor
 * val button = Modifier()
 *     .cursor(Cursor.Pointer)
 *     .padding("8px 16px")
 *
 * // Text selection cursor
 * val textArea = Modifier()
 *     .cursor(Cursor.Text)
 *     .border("1px", "solid", "#ccc")
 *
 * // Loading or busy state
 * val loading = Modifier()
 *     .cursor(Cursor.Wait)
 *     .opacity(0.7f)
 * ```
 *
 * @param value Cursor enum value providing type-safe cursor appearance
 * @return A new Modifier with cursor style applied
 * @see cursor for string version
 * @since 1.0.0
 */
fun Modifier.cursor(value: Cursor): Modifier =
    style("cursor", value.toString())

// ========================
// ADVANCED LAYOUT POSITIONING (TYPE-SAFE)
// ========================

/**
 * Positions an element at a specific angle and radius from the center using radial coordinates.
 *
 * This advanced positioning function enables orbital layouts, circular menus, and complex
 * geometric arrangements by calculating precise coordinates based on mathematical principles.
 * Perfect for creating innovative UI patterns like radial navigation, satellite layouts,
 * or decorative element positioning.
 *
 * ## Mathematical Foundation
 * - Uses trigonometric functions (sin/cos) to calculate x,y coordinates
 * - Angles are measured clockwise from the positive x-axis (standard CSS convention)
 * - Coordinates are relative to the center point (50%, 50%) of the container
 * - Transform is applied to center the element on its calculated position
 *
 * ## Examples
 * ```kotlin
 * // Circular menu items
 * val menuItems = listOf("Home", "About", "Contact", "Blog")
 * menuItems.forEachIndexed { index, item ->
 *     val angle = RadialAngle.fromDegrees(index * 90) ?: RadialAngle.Deg0
 *     MenuItem(
 *         text = item,
 *         modifier = Modifier()
 *             .radialPosition(angle, RadialRadius.Large)
 *             .padding("8px 12px")
 *     )
 * }
 *
 * // Orbital navigation buttons
 * val navigationButton = Modifier()
 *     .radialPosition(RadialAngle.Deg45, RadialRadius.Medium)
 *     .size("40px")
 *     .borderRadius("50%")
 *
 * // Decorative elements positioned around a central focal point
 * val decoration = Modifier()
 *     .radialPosition(RadialAngle.Deg120, RadialRadius.Small)
 *     .opacity(0.6f)
 * ```
 *
 * ## Performance Considerations
 * - Calculations are performed once during modifier creation
 * - Uses CSS calc() for responsive positioning
 * - Transform property uses hardware acceleration when available
 *
 * @param angle The angular position using type-safe RadialAngle enum
 * @param radius The distance from center using type-safe RadialRadius enum
 * @return A new Modifier with precise radial positioning applied
 * @see circularLayout for automatic multi-item circular arrangements
 * @see centerAbsolute for simple center positioning
 * @since 1.0.0
 */
fun Modifier.radialPosition(angle: RadialAngle, radius: RadialRadius): Modifier {
    val radians = angle.degrees.toDouble() * kotlin.math.PI / 180.0
    val radiusValue = radius.value.toDouble()
    val x = radiusValue * kotlin.math.cos(radians)
    val y = radiusValue * kotlin.math.sin(radians)

    return position(Position.Absolute)
        .style("left", "calc(50% + ${x.px})")
        .style("top", "calc(50% + ${y.px})")
        .transform(TransformFunction.Translate to "-50% -50%")
}

/**
 * Arranges elements in a perfect circular layout with automatic angle calculation.
 *
 * This function simplifies the creation of circular layouts by automatically calculating
 * the optimal angle for each item based on the total number of items and the current
 * item's index. Perfect for creating user avatars, navigation wheels, or any UI pattern
 * requiring equidistant circular arrangement.
 *
 * ## Automatic Calculations
 * - Divides 360° by total items for equal spacing
 * - Starts from 0° (rightmost position) and proceeds clockwise
 * - Handles edge cases where angles don't map to predefined RadialAngle values
 * - Falls back to RadialAngle.Deg0 for invalid calculations
 *
 * ## Examples
 * ```kotlin
 * // User avatar circle (6 people)
 * val avatars = users.take(6)
 * avatars.forEachIndexed { index, user ->
 *     Avatar(
 *         user = user,
 *         modifier = Modifier()
 *             .circularLayout(
 *                 radius = RadialRadius.Large,
 *                 totalItems = avatars.size,
 *                 currentIndex = index
 *             )
 *             .size("60px")
 *             .borderRadius("50%")
 *     )
 * }
 *
 * // Navigation wheel with 8 options
 * val navOptions = listOf("Home", "Search", "Profile", "Settings", "Help", "About", "Contact", "Logout")
 * navOptions.forEachIndexed { index, option ->
 *     NavButton(
 *         label = option,
 *         modifier = Modifier()
 *             .circularLayout(
 *                 radius = RadialRadius.Medium,
 *                 totalItems = navOptions.size,
 *                 currentIndex = index
 *             )
 *     )
 * }
 *
 * // Decorative elements around a logo (12 items)
 * repeat(12) { index ->
 *     DecorativeElement(
 *         modifier = Modifier()
 *             .circularLayout(
 *                 radius = RadialRadius.Small,
 *                 totalItems = 12,
 *                 currentIndex = index
 *             )
 *             .size("8px")
 *             .borderRadius("50%")
 *             .backgroundColor("#ff6b6b")
 *     )
 * }
 * ```
 *
 * ## Use Cases
 * - **Social Features**: Friend/follower avatars around profile pictures
 * - **Navigation**: Radial menus and action wheels
 * - **Data Visualization**: Circular charts and diagrams
 * - **Gaming UI**: Ability wheels and item selectors
 * - **Decorative Design**: Orbital animations and visual effects
 *
 * @param radius The circle radius using type-safe RadialRadius enum
 * @param totalItems Total number of items to arrange in the circle (must be > 0)
 * @param currentIndex Zero-based index of this item (0 to totalItems-1)
 * @return A new Modifier with circular layout positioning applied
 * @throws IllegalArgumentException if totalItems <= 0 or currentIndex is out of bounds
 * @see radialPosition for manual angle-based positioning
 * @see RadialRadius for available radius options
 * @since 1.0.0
 */
fun Modifier.circularLayout(radius: RadialRadius, totalItems: Int, currentIndex: Int): Modifier {
    require(totalItems > 0) { "Total items must be greater than 0, got: $totalItems" }
    require(currentIndex in 0 until totalItems) { "Current index $currentIndex must be between 0 and ${totalItems - 1}" }

    val angleStep = 360.0 / totalItems
    val angleDegrees = (angleStep * currentIndex).toInt()
    val angle = RadialAngle.fromDegrees(angleDegrees) ?: RadialAngle.Deg0
    return radialPosition(angle, radius)
}

/**
 * Centers an element absolutely within its container using mathematical precision.
 *
 * This function provides perfect center alignment using absolute positioning with
 * CSS transforms, ensuring the element is centered regardless of its dimensions.
 * The technique uses the transform property for hardware-accelerated positioning.
 *
 * ## Positioning Technique
 * - Sets position to absolute
 * - Places top-left corner at container center (50%, 50%)
 * - Transforms element back by 50% of its own dimensions
 * - Results in perfect center alignment
 *
 * ## Examples
 * ```kotlin
 * // Modal dialog centered in viewport
 * val modal = Modifier()
 *     .centerAbsolute()
 *     .width("400px")
 *     .height("300px")
 *     .backgroundColor("white")
 *     .borderRadius("8px")
 *     .shadow("0px", "4px", "12px", "rgba(0,0,0,0.15)")
 *
 * // Loading spinner in center of container
 * val loadingSpinner = Modifier()
 *     .centerAbsolute()
 *     .size("40px")
 *     .rotatingAnimation(RotationSpeed.Medium)
 *
 * // Overlay content centered on image
 * val imageOverlay = Modifier()
 *     .centerAbsolute()
 *     .padding("16px")
 *     .backgroundColor("rgba(0,0,0,0.7)")
 *     .color("white")
 *     .borderRadius("4px")
 * ```
 *
 * ## Use Cases
 * - **Modals and Dialogs**: Perfect center alignment in viewports
 * - **Loading States**: Spinners and progress indicators
 * - **Image Overlays**: Text or controls over images
 * - **Tooltips**: Positioned content over trigger elements
 * - **Floating Elements**: Absolutely positioned UI components
 *
 * @return A new Modifier with absolute center positioning applied
 * @see fixedCenter for viewport-relative centering
 * @see radialPosition for angle-based positioning
 * @since 1.0.0
 */
fun Modifier.centerAbsolute(): Modifier =
    position(Position.Absolute)
        .style("left", 50.percent)
        .style("top", 50.percent)
        .transform(TransformFunction.Translate to "-50% -50%")

/**
 * Centers an element with fixed positioning relative to the viewport.
 *
 * Similar to centerAbsolute but uses fixed positioning, meaning the element
 * will remain centered in the viewport even when the page is scrolled.
 * Perfect for modal overlays, floating panels, and persistent UI elements.
 *
 * ## Fixed vs Absolute Positioning
 * - **Fixed**: Positioned relative to viewport, ignores scrolling
 * - **Absolute**: Positioned relative to nearest positioned ancestor
 * - **Use Fixed For**: Modals, notifications, floating toolbars
 * - **Use Absolute For**: Content overlays, tooltips, dropdowns
 *
 * ## Examples
 * ```kotlin
 * // Full-screen modal overlay
 * val modalOverlay = Modifier()
 *     .fixedCenter()
 *     .width("90vw")
 *     .height("80vh")
 *     .maxWidth("600px")
 *     .maxHeight("500px")
 *     .backgroundColor("white")
 *     .borderRadius("12px")
 *     .shadow("0px", "8px", "24px", "rgba(0,0,0,0.2)")
 *
 * // Floating action button
 * val fab = Modifier()
 *     .fixedCenter()
 *     .size("56px")
 *     .borderRadius("50%")
 *     .backgroundColor("#007bff")
 *     .shadow("0px", "4px", "8px", "rgba(0,0,0,0.2)")
 *     .cursor(Cursor.Pointer)
 *
 * // Persistent notification
 * val notification = Modifier()
 *     .fixedCenter()
 *     .padding("16px 24px")
 *     .backgroundColor("#28a745")
 *     .color("white")
 *     .borderRadius("6px")
 * ```
 *
 * ## Accessibility Considerations
 * - Ensure fixed elements don't obstruct important content
 * - Provide escape mechanisms (close buttons, ESC key handling)
 * - Consider z-index stacking for proper layering
 * - Test with screen readers and keyboard navigation
 *
 * @return A new Modifier with fixed center positioning applied
 * @see centerAbsolute for container-relative centering
 * @see zIndex for controlling stacking order
 * @since 1.0.0
 */
fun Modifier.fixedCenter(): Modifier =
    position(Position.Fixed)
        .style("left", 50.percent)
        .style("top", 50.percent)
        .transform(TransformFunction.Translate to "-50% -50%")

/**
 * Applies a relative offset from the element's normal position in the document flow.
 *
 * Unlike absolute positioning, relative positioning maintains the element's space
 * in the normal document flow while visually offsetting it by the specified amounts.
 * This is useful for fine-tuning positioning without affecting other elements.
 *
 * ## Relative Positioning Behavior
 * - Element retains its original space in the layout
 * - Other elements behave as if this element is in its original position
 * - Visual offset is applied on top of the normal position
 * - Useful for subtle adjustments and micro-interactions
 *
 * ## Examples
 * ```kotlin
 * // Subtle hover effect with position shift
 * val hoverableCard = Modifier()
 *     .relativeOffset(0, 0)
 *     .transition("all", "0.2s", "ease")
 *     .hover(mapOfCompat(
 *         "transform" to "translate(0px, -2px)",
 *         "box-shadow" to "0px 6px 12px rgba(0,0,0,0.15)"
 *     ))
 *
 * // Fine-tuning text alignment
 * val adjustedIcon = Modifier()
 *     .relativeOffset(0, -1) // Nudge up by 1px for better text alignment
 *     .size("16px")
 *
 * // Creating layered effects
 * val layeredElement = Modifier()
 *     .relativeOffset(4, 4)
 *     .backgroundColor("#e0e0e0")
 *     .zIndex(-1) // Behind the main element
 *
 * // Responsive offset adjustments
 * val responsiveOffset = Modifier()
 *     .relativeOffset(
 *         x = if (isMobile) 0 else 8,
 *         y = if (isMobile) 0 else -4
 *     )
 * ```
 *
 * ## Performance Notes
 * - Relative positioning triggers layout recalculation
 * - Use transforms for animations instead of changing left/top
 * - Consider absolute positioning for complex layouts
 * - Test performance with many relatively positioned elements
 *
 * @param x Horizontal offset in pixels (positive = right, negative = left)
 * @param y Vertical offset in pixels (positive = down, negative = up)
 * @return A new Modifier with relative offset positioning applied
 * @see centerAbsolute for absolute center positioning
 * @see transform for transform-based offsets
 * @since 1.0.0
 */
fun Modifier.relativeOffset(x: Number, y: Number): Modifier =
    position(Position.Relative)
        .style("left", x.px)
        .style("top", y.px)

// ========================
// ANIMATION INTEGRATION
// ========================

/**
 * Applies a smooth floating animation effect with type-safe parameters.
 *
 * Creates a gentle up-and-down floating motion that's perfect for buttons, cards,
 * or any interactive elements that benefit from subtle motion to indicate interactivity.
 * The animation uses hardware-accelerated transforms for optimal performance.
 *
 * ## Animation Characteristics
 * - **Motion**: Vertical floating movement (up and down)
 * - **Easing**: Ease-in-out for natural, organic motion
 * - **Loop**: Infinite alternating animation
 * - **Performance**: Uses CSS transforms for hardware acceleration
 *
 * ## Examples
 * ```kotlin
 * // Floating call-to-action button
 * val ctaButton = Modifier()
 *     .floatingAnimation(
 *         duration = AnimationDuration.Medium,
 *         intensity = FloatIntensity.Moderate
 *     )
 *     .padding("12px 24px")
 *     .backgroundColor("#007bff")
 *     .borderRadius("8px")
 *
 * // Subtle floating for cards
 * val floatingCard = Modifier()
 *     .floatingAnimation(
 *         duration = AnimationDuration.Slow,
 *         intensity = FloatIntensity.Gentle
 *     )
 *     .shadow("0px", "4px", "8px", "rgba(0,0,0,0.1)")
 *
 * // Attention-grabbing notification
 * val notification = Modifier()
 *     .floatingAnimation(
 *         duration = AnimationDuration.Fast,
 *         intensity = FloatIntensity.Strong
 *     )
 *     .backgroundColor("#28a745")
 *     .color("white")
 * ```
 *
 * ## Accessibility Considerations
 * - Respects user's motion preferences (prefers-reduced-motion)
 * - Intensity can be set to Gentle for subtle motion
 * - Consider providing option to disable animations
 *
 * ## Performance Notes
 * - Uses CSS transform property for hardware acceleration
 * - Minimal impact on layout and paint operations
 * - Suitable for multiple elements without performance degradation
 *
 * @param duration Animation duration using type-safe AnimationDuration enum (default: Slow)
 * @param intensity Float distance using type-safe FloatIntensity enum (default: Gentle)
 * @return A new Modifier with floating animation applied
 * @see rotatingAnimation for rotation-based animations
 * @see AnimationDuration for available duration options
 * @see FloatIntensity for available intensity levels
 * @since 1.0.0
 */
fun Modifier.floatingAnimation(
    duration: AnimationDuration = AnimationDuration.Slow,
    intensity: FloatIntensity = FloatIntensity.Gentle
): Modifier =
    animation(
        name = "float-${intensity.value}",
        duration = duration,
        easing = "ease-in-out",
        iterationCount = "infinite",
        direction = AnimationDirection.Alternate
    )

/**
 * Applies a continuous rotation animation with precise control over speed and direction.
 *
 * Creates smooth, continuous rotation perfect for loading spinners, icons, or decorative
 * elements. The animation uses linear easing for consistent rotation speed and can be
 * configured for clockwise or counter-clockwise motion.
 *
 * ## Animation Characteristics
 * - **Motion**: 360-degree continuous rotation
 * - **Easing**: Linear for consistent speed
 * - **Loop**: Infinite continuous animation
 * - **Direction**: Configurable clockwise or counter-clockwise
 * - **Performance**: Hardware-accelerated CSS transforms
 *
 * ## Examples
 * ```kotlin
 * // Loading spinner
 * val loadingSpinner = Modifier()
 *     .rotatingAnimation(
 *         speed = RotationSpeed.Fast,
 *         clockwise = true
 *     )
 *     .size("24px")
 *     .border("2px", "solid", "#007bff")
 *     .borderTop("2px", "solid", "transparent")
 *     .borderRadius("50%")
 *
 * // Decorative rotating icon
 * val decorativeIcon = Modifier()
 *     .rotatingAnimation(
 *         speed = RotationSpeed.Slow,
 *         clockwise = false
 *     )
 *     .opacity(0.3f)
 *
 * // Gear icon with mechanical rotation
 * val gearIcon = Modifier()
 *     .rotatingAnimation(
 *         speed = RotationSpeed.Medium,
 *         clockwise = true
 *     )
 *     .color("#6c757d")
 * ```
 *
 * ## Use Cases
 * - **Loading States**: Spinners and progress indicators
 * - **Icons**: Refresh, settings, or sync indicators
 * - **Decorative Elements**: Background animations and visual flair
 * - **Interactive Feedback**: Hover effects and state changes
 * - **Branding**: Logo animations and brand elements
 *
 * ## Performance Optimization
 * - Uses `transform: rotate()` for optimal performance
 * - GPU-accelerated on supported devices
 * - Minimal CPU usage with CSS animations
 * - Can be paused/resumed with CSS animation-play-state
 *
 * @param speed Rotation speed using type-safe RotationSpeed enum (default: Slow)
 * @param clockwise Direction of rotation - true for clockwise, false for counter-clockwise (default: true)
 * @return A new Modifier with continuous rotation animation applied
 * @see floatingAnimation for floating motion effects
 * @see RotationSpeed for available speed options
 * @see AnimationDirection for animation direction control
 * @since 1.0.0
 */
fun Modifier.rotatingAnimation(
    speed: RotationSpeed = RotationSpeed.Slow,
    clockwise: Boolean = true
): Modifier {
    val direction = if (clockwise) AnimationDirection.Normal else AnimationDirection.Reverse
    return animation(
        name = "rotate-360",
        duration = fromSpeed(speed),
        easing = "linear",
        iterationCount = "infinite",
        direction = direction
    )
}

/**
 * Converts RotationSpeed enum values to corresponding AnimationDuration enum values.
 *
 * This internal helper function provides the mapping between rotation speed presets
 * and animation durations, ensuring consistent timing across different animation types
 * while maintaining type safety throughout the animation system.
 *
 * ## Speed to Duration Mapping
 * - **RotationSpeed.VerySlow** (30s) → AnimationDuration.VerySlow
 * - **RotationSpeed.Slow** (10s) → AnimationDuration.Slow
 * - **RotationSpeed.Medium** (5s) → AnimationDuration.Medium
 * - **RotationSpeed.Fast** (2s) → AnimationDuration.Fast
 * - **Default fallback** → AnimationDuration.Medium
 *
 * @param speed The RotationSpeed enum value to convert
 * @return Corresponding AnimationDuration enum value
 * @see RotationSpeed for available speed options
 * @see AnimationDuration for duration specifications
 * @since 1.0.0
 */
private fun fromSpeed(speed: RotationSpeed): AnimationDuration =
    when (speed.value.toInt()) {
        30 -> AnimationDuration.VerySlow
        20 -> AnimationDuration.VerySlow
        10 -> AnimationDuration.Slow
        5 -> AnimationDuration.Medium
        2 -> AnimationDuration.Fast
        else -> AnimationDuration.Medium
    }
