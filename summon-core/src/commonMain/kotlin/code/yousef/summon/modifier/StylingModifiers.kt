/**
 * # Styling Modifiers
 *
 * This file provides comprehensive styling modifier functions for the Summon UI framework.
 * These modifiers enable complete control over visual appearance including backgrounds,
 * typography, colors, borders, shadows, and other aesthetic properties.
 *
 * ## Overview
 *
 * Styling modifiers transform the visual presentation of UI components while maintaining
 * type safety and cross-platform compatibility. This module provides:
 *
 * - **Background Control**: Images, gradients, positioning, and clipping
 * - **Typography**: Font families, styles, sizes, and text formatting
 * - **Visual Effects**: Shadows, borders, filters, and transforms
 * - **Color Management**: Theme-aware color application with semantic values
 * - **Layout Aesthetics**: Visual spacing, alignment, and decoration
 * - **Interactive States**: Hover, focus, and transition effects
 *
 * ## Key Features
 *
 * ### Background Styling
 * - Complete background image control with positioning and sizing
 * - Gradient support with linear and radial patterns
 * - Background clipping for advanced masking effects
 * - Responsive background behavior for different screen sizes
 *
 * ### Typography System
 * - Font family management with web-safe fallbacks
 * - Responsive font sizing with em, rem, px, and viewport units
 * - Text alignment with RTL/LTR support
 * - Line height control for optimal readability
 * - Text decoration and styling effects
 *
 * ### Visual Enhancement
 * - Box shadow creation with multiple shadow support
 * - Border styling with individual side control
 * - Filter effects for modern visual treatment
 * - Transform functions for positioning and effects
 *
 * ## Usage Examples
 *
 * ### Background Styling
 * ```kotlin
 * // Hero section with background image
 * val heroSection = Modifier()
 *     .backgroundImage("url('/images/hero-bg.jpg')")
 *     .backgroundSize("cover")
 *     .backgroundPosition("center")
 *     .backgroundRepeat("no-repeat")
 *     .height("100vh")
 *
 * // Card with gradient background
 * val gradientCard = Modifier()
 *     .background("linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
 *     .borderRadius("12px")
 *     .padding("24px")
 * ```
 *
 * ### Typography Control
 * ```kotlin
 * // Heading with custom typography
 * val heading = Modifier()
 *     .fontFamily("'Inter', sans-serif")
 *     .fontSize("2.5rem")
 *     .fontWeight("700")
 *     .lineHeight("1.2")
 *     .textAlign(TextAlign.Center)
 *
 * // Body text with optimal readability
 * val bodyText = Modifier()
 *     .fontFamily("'Georgia', serif")
 *     .fontSize("1.125rem")
 *     .lineHeight("1.6")
 *     .color("#374151")
 * ```
 *
 * ### Visual Effects
 * ```kotlin
 * // Elevated card with shadow
 * val elevatedCard = Modifier()
 *     .backgroundColor("white")
 *     .borderRadius("8px")
 *     .shadow("0px", "4px", "6px", "rgba(0, 0, 0, 0.07)")
 *     .shadow("0px", "1px", "3px", "rgba(0, 0, 0, 0.06)")
 *
 * // Interactive button with transitions
 * val interactiveButton = Modifier()
 *     .backgroundColor("#3b82f6")
 *     .color("white")
 *     .border("2px", "solid", "transparent")
 *     .transition("all", "0.2s", "ease")
 *     .hover(mapOfCompat(
 *         "background-color" to "#2563eb",
 *         "transform" to "translateY(-1px)"
 *     ))
 * ```
 *
 * ## Performance Considerations
 *
 * - **CSS Generation**: Properties map directly to CSS for optimal rendering
 * - **Hardware Acceleration**: Transform and filter properties use GPU when available
 * - **Batch Updates**: Multiple styling changes are batched for efficiency
 * - **Selector Optimization**: Minimal CSS selector specificity for fast matching
 *
 * ## Cross-Platform Support
 *
 * - **Browser Compatibility**: Modern CSS features with fallback support
 * - **Mobile Optimization**: Touch-friendly sizing and responsive behavior
 * - **Server-Side Rendering**: Full SSR support with critical CSS extraction
 * - **Print Styles**: Automatic print-friendly styling adjustments
 *
 * ## Accessibility Integration
 *
 * - **Color Contrast**: Automatic contrast checking for WCAG compliance
 * - **Focus Management**: Visible focus indicators for keyboard navigation
 * - **Reduced Motion**: Respects user motion preferences for animations
 * - **Screen Reader Support**: Semantic styling that enhances accessibility
 *
 * @see code.yousef.summon.modifier.LayoutModifiers for layout-related modifiers
 * @see code.yousef.summon.modifier.AccessibilityModifiers for accessibility enhancements
 * @see code.yousef.summon.theme for theme integration and color management
 * @since 1.0.0
 */
package code.yousef.summon.modifier

import code.yousef.summon.extensions.px
import kotlin.jvm.JvmName

// ========================
// BACKGROUND STYLING
// ========================

/**
 * Sets the background image for the element.
 *
 * Supports all CSS background-image values including URLs, gradients, and multiple backgrounds.
 * This property enables rich visual backgrounds that can enhance the aesthetic appeal and
 * user experience of UI components.
 *
 * ## Supported Values
 * - **Image URLs**: `url('/path/to/image.jpg')`, `url('https://example.com/image.png')`
 * - **Linear Gradients**: `linear-gradient(45deg, #ff0000, #0000ff)`
 * - **Radial Gradients**: `radial-gradient(circle, #ff0000, #0000ff)`
 * - **Multiple Backgrounds**: Comma-separated list for layered effects
 * - **CSS Functions**: `repeating-linear-gradient()`, `conic-gradient()`
 *
 * ## Examples
 * ```kotlin
 * // Simple background image
 * val heroSection = Modifier()
 *     .backgroundImage("url('/images/hero-background.jpg')")
 *     .backgroundSize("cover")
 *     .height("60vh")
 *
 * // Linear gradient background
 * val gradientButton = Modifier()
 *     .backgroundImage("linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
 *     .color("white")
 *     .padding("12px 24px")
 *
 * // Multiple background layers
 * val complexBackground = Modifier()
 *     .backgroundImage("url('/overlay.png'), url('/main-bg.jpg')")
 *     .backgroundSize("auto, cover")
 *     .backgroundPosition("center, center")
 *
 * // CSS pattern background
 * val patternBackground = Modifier()
 *     .backgroundImage("repeating-linear-gradient(45deg, #f0f0f0 0px, #f0f0f0 10px, #e0e0e0 10px, #e0e0e0 20px)")
 * ```
 *
 * ## Performance Considerations
 * - Use optimized image formats (WebP, AVIF) for better loading times
 * - Consider responsive images with different sizes for different viewports
 * - CSS gradients render faster than image-based gradients
 * - Multiple backgrounds can impact rendering performance
 *
 * @param value The background image value (URL, gradient, or CSS function)
 * @return A new Modifier with background-image applied
 * @see backgroundSize for controlling image scaling
 * @see backgroundPosition for image positioning
 * @see backgroundRepeat for repeat behavior
 * @since 1.0.0
 */
fun Modifier.backgroundImage(value: String): Modifier =
    style("background-image", value)

/**
 * Controls how background images are sized within their container.
 *
 * This property determines how background images scale and fit within the element's
 * content area, enabling responsive and visually appropriate background presentation.
 *
 * ## Common Values
 * - **cover**: Scales image to cover entire container (may crop)
 * - **contain**: Scales image to fit entirely within container
 * - **auto**: Uses image's natural size
 * - **100% 100%**: Stretches to fill container exactly
 * - **50px 30px**: Explicit width and height values
 *
 * ## Examples
 * ```kotlin
 * // Hero image that covers entire area
 * val heroImage = Modifier()
 *     .backgroundImage("url('/hero.jpg')")
 *     .backgroundSize("cover")
 *     .backgroundPosition("center")
 *     .height("100vh")
 *
 * // Logo that maintains aspect ratio
 * val logoBackground = Modifier()
 *     .backgroundImage("url('/logo.svg')")
 *     .backgroundSize("contain")
 *     .backgroundRepeat("no-repeat")
 *     .width("200px")
 *     .height("100px")
 *
 * // Pattern with specific size
 * val patternBackground = Modifier()
 *     .backgroundImage("url('/pattern.png')")
 *     .backgroundSize("50px 50px")
 *     .backgroundRepeat("repeat")
 *
 * // Responsive background sizing
 * val responsiveBackground = Modifier()
 *     .backgroundImage("url('/bg.jpg')")
 *     .backgroundSize("100% auto") // Full width, auto height
 * ```
 *
 * ## Responsive Design
 * - Use `cover` for full-bleed hero sections
 * - Use `contain` for logos and icons
 * - Use percentage values for responsive scaling
 * - Consider different sizes for different screen densities
 *
 * @param value The background size value (keyword or explicit dimensions)
 * @return A new Modifier with background-size applied
 * @see backgroundImage for setting the background image
 * @see backgroundPosition for image positioning
 * @since 1.0.0
 */
fun Modifier.backgroundSize(value: String): Modifier =
    style("background-size", value)

/**
 * Sets the positioning of background images within their container.
 *
 * Controls where background images are positioned, enabling precise control over
 * which part of the image is visible and how it aligns within the element.
 *
 * ## Position Values
 * - **Keywords**: `center`, `top`, `bottom`, `left`, `right`
 * - **Percentages**: `50% 25%` (horizontal vertical)
 * - **Absolute Values**: `10px 20px`, `1rem 0.5rem`
 * - **Mixed Units**: `center 20px`, `left 10%`
 *
 * ## Examples
 * ```kotlin
 * // Centered background image
 * val centeredBackground = Modifier()
 *     .backgroundImage("url('/image.jpg')")
 *     .backgroundPosition("center")
 *     .backgroundSize("cover")
 *
 * // Top-aligned banner
 * val bannerBackground = Modifier()
 *     .backgroundImage("url('/banner.jpg')")
 *     .backgroundPosition("center top")
 *     .backgroundSize("cover")
 *     .height("300px")
 *
 * // Custom positioning with percentages
 * val customPosition = Modifier()
 *     .backgroundImage("url('/artwork.jpg')")
 *     .backgroundPosition("75% 25%") // Focus on upper-right area
 *     .backgroundSize("120%") // Slight zoom
 *
 * // Multiple background positioning
 * val multipleBackgrounds = Modifier()
 *     .backgroundImage("url('/overlay.png'), url('/main.jpg')")
 *     .backgroundPosition("center, bottom")
 *     .backgroundSize("auto, cover")
 * ```
 *
 * ## Use Cases
 * - **Hero Sections**: Position focal point of images
 * - **Product Images**: Highlight specific product features
 * - **Artistic Elements**: Create visual interest and composition
 * - **Responsive Design**: Adjust focus point for different screen sizes
 *
 * @param value The background position value (keywords, percentages, or absolute values)
 * @return A new Modifier with background-position applied
 * @see backgroundImage for setting the background image
 * @see backgroundSize for controlling image scaling
 * @since 1.0.0
 */
fun Modifier.backgroundPosition(value: String): Modifier =
    style("background-position", value)

/**
 * Controls how background images repeat within their container.
 *
 * Determines the repetition behavior of background images, enabling creation of
 * patterns, textures, and decorative effects through controlled image tiling.
 *
 * ## Repeat Values
 * - **no-repeat**: Image appears once only
 * - **repeat**: Tiles in both directions (default)
 * - **repeat-x**: Repeats horizontally only
 * - **repeat-y**: Repeats vertically only
 * - **space**: Repeats with spacing to fit container
 * - **round**: Repeats and scales to fit container
 *
 * ## Examples
 * ```kotlin
 * // Hero image without repetition
 * val heroSection = Modifier()
 *     .backgroundImage("url('/hero.jpg')")
 *     .backgroundRepeat("no-repeat")
 *     .backgroundSize("cover")
 *     .backgroundPosition("center")
 *
 * // Decorative pattern background
 * val patternBackground = Modifier()
 *     .backgroundImage("url('/pattern.png')")
 *     .backgroundRepeat("repeat")
 *     .backgroundSize("100px 100px")
 *
 * // Horizontal stripe pattern
 * val stripePattern = Modifier()
 *     .backgroundImage("url('/stripe.png')")
 *     .backgroundRepeat("repeat-x")
 *     .backgroundPosition("top")
 *
 * // Responsive pattern with spacing
 * val spacedPattern = Modifier()
 *     .backgroundImage("url('/icon.svg')")
 *     .backgroundRepeat("space")
 *     .backgroundSize("50px 50px")
 * ```
 *
 * ## Design Applications
 * - **Textures**: Create rich surface textures with subtle patterns
 * - **Borders**: Use repeat-x/repeat-y for decorative borders
 * - **Watermarks**: Repeat logos or branding elements
 * - **Data Visualization**: Create chart backgrounds and grid patterns
 *
 * @param value The background repeat value (keyword controlling repetition behavior)
 * @return A new Modifier with background-repeat applied
 * @see backgroundImage for setting the background image
 * @see backgroundSize for controlling pattern scale
 * @since 1.0.0
 */
fun Modifier.backgroundRepeat(value: String): Modifier =
    style("background-repeat", value)

/**
 * Controls the background painting area using string values.
 *
 * Determines how far background colors and images extend within the element,
 * enabling advanced masking and clipping effects for sophisticated visual designs.
 *
 * ## Clip Values
 * - **border-box**: Background extends to border edge (default)
 * - **padding-box**: Background extends to padding edge only
 * - **content-box**: Background limited to content area
 * - **text**: Background clips to text content (WebKit prefix may be needed)
 *
 * ## Examples
 * ```kotlin
 * // Standard background clipping
 * val borderClipped = Modifier()
 *     .backgroundColor("#e3f2fd")
 *     .backgroundClip("border-box")
 *     .border("5px", "solid", "transparent")
 *     .padding("20px")
 *
 * // Background only in content area
 * val contentClipped = Modifier()
 *     .backgroundColor("#f3e5f5")
 *     .backgroundClip("content-box")
 *     .padding("30px")
 *     .border("10px", "solid", "#9c27b0")
 *
 * // Gradient text effect (requires -webkit-background-clip)
 * val gradientText = Modifier()
 *     .backgroundImage("linear-gradient(45deg, #ff6b6b, #4ecdc4)")
 *     .backgroundClip("text")
 *     .style("-webkit-background-clip", "text")
 *     .style("-webkit-text-fill-color", "transparent")
 * ```
 *
 * @param value The background clip value as string
 * @return A new Modifier with background-clip applied
 * @see backgroundClip for type-safe enum version
 * @since 1.0.0
 */
fun Modifier.backgroundClip(value: String): Modifier =
    style("background-clip", value)

/**
 * Controls the background painting area using type-safe enum values.
 *
 * Provides compile-time safety for background clipping values while offering
 * the same functionality as the string version with additional IDE support.
 *
 * ## Examples
 * ```kotlin
 * // Type-safe background clipping
 * val paddingClipped = Modifier()
 *     .backgroundColor("#ffecb3")
 *     .backgroundClip(BackgroundClip.PaddingBox)
 *     .padding("25px")
 *     .border("8px", "solid", "#ff9800")
 *
 * // Content area only background
 * val contentOnly = Modifier()
 *     .backgroundImage("url('/texture.png')")
 *     .backgroundClip(BackgroundClip.ContentBox)
 *     .padding("40px")
 * ```
 *
 * @param value BackgroundClip enum value for type-safe clipping behavior
 * @return A new Modifier with background-clip applied
 * @see backgroundClip for string version
 * @since 1.0.0
 */
fun Modifier.backgroundClip(value: BackgroundClip): Modifier =
    backgroundClip(value.toString())

/**
 * Applies comprehensive background styling with multiple properties in a single call.
 *
 * This convenience function allows setting background color, image, positioning, sizing,
 * and repeat behavior simultaneously, providing a streamlined approach for complex
 * background configurations.
 *
 * ## Examples
 * ```kotlin
 * // Complete hero section background
 * val heroBackground = Modifier()
 *     .background(
 *         color = "rgba(0, 0, 0, 0.3)",  // Semi-transparent overlay
 *         image = "url('/hero-image.jpg')",
 *         position = "center",
 *         size = "cover",
 *         repeat = "no-repeat"
 *     )
 *     .height("100vh")
 *
 * // Textured card background
 * val texturedCard = Modifier()
 *     .background(
 *         color = "#f8f9fa",           // Base color
 *         image = "url('/subtle-texture.png')",
 *         position = "top left",
 *         size = "200px 200px",
 *         repeat = "repeat"
 *     )
 *     .borderRadius("8px")
 *     .padding("24px")
 *
 * // Gradient with overlay pattern
 * val complexBackground = Modifier()
 *     .background(
 *         color = "transparent",
 *         image = "linear-gradient(135deg, rgba(255,107,107,0.8), rgba(78,205,196,0.8)), url('/pattern.svg')",
 *         position = "center, top left",
 *         size = "cover, 50px 50px",
 *         repeat = "no-repeat, repeat"
 *     )
 * ```
 *
 * ## Parameter Order
 * The parameters follow the logical order of CSS background properties:
 * 1. **color**: Base background color or gradient
 * 2. **image**: Background image or additional gradients
 * 3. **position**: How the image is positioned
 * 4. **size**: How the image is sized
 * 5. **repeat**: How the image repeats
 *
 * @param color Background color value (can include gradients)
 * @param image Background image URL or CSS image function
 * @param position Background position value
 * @param size Background size value
 * @param repeat Background repeat behavior
 * @return A new Modifier with all background properties applied
 * @see backgroundImage for image-specific control
 * @see backgroundColor for color-only backgrounds
 * @since 1.0.0
 */
fun Modifier.background(color: String, image: String, position: String, size: String, repeat: String): Modifier =
    this.background(color)
        .backgroundImage(image)
        .backgroundPosition(position)
        .backgroundSize(size)
        .backgroundRepeat(repeat)

/**
 * Sets the font family.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("fontFamily(value, null)"))
fun Modifier.fontFamily(value: String): Modifier =
    fontFamily(value, null)

/**
 * Sets the font style (normal, italic, etc).
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("fontStyle(value, null)"))
fun Modifier.fontStyle(value: String): Modifier =
    fontStyle(value, null)

/**
 * Sets the text alignment.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("textAlign(value, null)"))
fun Modifier.textAlign(value: String): Modifier =
    textAlign(value, null)

/**
 * Sets the text alignment using the TextAlign enum.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("textAlign(value, null)"))
fun Modifier.textAlign(value: TextAlign): Modifier =
    textAlign(value, null)

/**
 * Sets the text decoration (underline, line-through, etc).
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("textDecoration(value, null)"))
fun Modifier.textDecoration(value: String): Modifier =
    textDecoration(value, null)

/**
 * Sets the line height with a string value.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("lineHeight(value, null)"))
fun Modifier.lineHeight(value: String): Modifier =
    lineHeight(value, null)

/**
 * Sets the line height with a numeric value (e.g., 1.5).
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("lineHeight(value, null)"))
fun Modifier.lineHeight(value: Number): Modifier =
    lineHeight(value, null)

/**
 * Sets the letter spacing.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("letterSpacing(value, null)"))
fun Modifier.letterSpacing(value: String): Modifier =
    letterSpacing(value, null)

/**
 * Sets the letter spacing with a numeric value in pixels.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("letterSpacing(value, null)"))
fun Modifier.letterSpacing(value: Number): Modifier =
    letterSpacing(value, null)

/**
 * Sets the text transformation (uppercase, lowercase, etc).
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("textTransform(value, null)"))
fun Modifier.textTransform(value: String): Modifier =
    textTransform(value, null)

/**
 * Sets the text transformation using the TextTransform enum.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("textTransform(value, null)"))
fun Modifier.textTransform(value: TextTransform): Modifier =
    textTransform(value, null)

/**
 * Sets the border width.
 * @deprecated Use the version with number parameter for type safety
 */
@Deprecated("Use the version with number parameter for type safety", ReplaceWith("borderWidth(value.toDouble())"))
fun Modifier.borderWidth(value: String): Modifier =
    style("border-width", value)

/**
 * Sets the border width with a numeric value in pixels.
 *
 * @param value The width in pixels
 * @return A new Modifier with the border width style added
 */
fun Modifier.borderWidth(value: Number): Modifier =
    style("border-width", "${value}px")

/**
 * Sets the border width for a specific side with a numeric value in pixels.
 *
 * @param value The width in pixels
 * @param side The side to apply the border width to (default is BorderSide.All)
 * @return A new Modifier with the border width style added
 */
fun Modifier.borderWidth(value: Number, side: BorderSide = BorderSide.All): Modifier =
    when (side) {
        BorderSide.All -> style("border-width", "${value}px")
        BorderSide.Top -> style("border-top-width", "${value}px")
        BorderSide.Right -> style("border-right-width", "${value}px")
        BorderSide.Bottom -> style("border-bottom-width", "${value}px")
        BorderSide.Left -> style("border-left-width", "${value}px")
    }

/**
 * Sets the top border width with a numeric value in pixels.
 *
 * @param value The width in pixels
 * @return A new Modifier with the top border width style added
 */
fun Modifier.borderTopWidth(value: Number): Modifier =
    style("border-top-width", "${value}px")

/**
 * Sets the right border width with a numeric value in pixels.
 *
 * @param value The width in pixels
 * @return A new Modifier with the right border width style added
 */
fun Modifier.borderRightWidth(value: Number): Modifier =
    style("border-right-width", "${value}px")

/**
 * Sets the bottom border width with a numeric value in pixels.
 *
 * @param value The width in pixels
 * @return A new Modifier with the bottom border width style added
 */
fun Modifier.borderBottomWidth(value: Number): Modifier =
    style("border-bottom-width", "${value}px")

/**
 * Sets the left border width with a numeric value in pixels.
 *
 * @param value The width in pixels
 * @return A new Modifier with the left border width style added
 */
fun Modifier.borderLeftWidth(value: Number): Modifier =
    style("border-left-width", "${value}px")

/**
 * Sets the border style (solid, dashed, etc).
 */
fun Modifier.borderStyle(value: String): Modifier =
    style("border-style", value)

/**
 * Sets the border style using the BorderStyle enum.
 */
fun Modifier.borderStyle(value: BorderStyle): Modifier =
    style("border-style", value.toString())

/**
 * Sets the border color.
 */
fun Modifier.borderColor(value: String): Modifier =
    style("border-color", value)


/**
 * Sets the border with all properties in one function.
 *
 * @param width The border width (can be a Number or a String)
 * @param style The border style (can be a String or BorderStyle)
 * @param color The border color
 * @param radius The border radius (can be a Number or a String)
 * @return A new Modifier with all border properties applied
 */
fun Modifier.border(
    width: Number? = null,
    style: String? = null,
    color: String? = null,
    radius: Number? = null
): Modifier {
    var modifier = this
    if (width != null) {
        modifier = modifier.borderWidth(width)
    }
    if (style != null) {
        modifier = modifier.borderStyle(style)
    }
    if (color != null) {
        modifier = modifier.borderColor(color)
    }
    if (radius != null) {
        modifier = modifier.borderRadius("${radius}px")
    }
    return modifier
}

/**
 * Sets the border with all properties in one function.
 *
 * @param width The border width (can be a Number or a String)
 * @param style The border style as a BorderStyle enum
 * @param color The border color
 * @param radius The border radius (can be a Number or a String)
 * @return A new Modifier with all border properties applied
 */
fun Modifier.border(
    width: Number? = null,
    style: BorderStyle? = null,
    color: String? = null,
    radius: Number? = null
): Modifier {
    var modifier = this
    if (width != null) {
        modifier = modifier.borderWidth(width)
    }
    if (style != null) {
        modifier = modifier.borderStyle(style)
    }
    if (color != null) {
        modifier = modifier.borderColor(color)
    }
    if (radius != null) {
        modifier = modifier.borderRadius("${radius}px")
    }
    return modifier
}

/**
 * Sets the border with all properties in one function.
 *
 * @param width The border width as a String
 * @param style The border style (can be a String or BorderStyle)
 * @param color The border color
 * @param radius The border radius as a String
 * @return A new Modifier with all border properties applied
 */
fun Modifier.border(
    width: String? = null,
    style: String? = null,
    color: String? = null,
    radius: String? = null
): Modifier {
    var modifier = this
    if (width != null) {
        modifier = modifier.style("border-width", width)
    }
    if (style != null) {
        modifier = modifier.borderStyle(style)
    }
    if (color != null) {
        modifier = modifier.borderColor(color)
    }
    if (radius != null) {
        modifier = modifier.borderRadius(radius)
    }
    return modifier
}

/**
 * Sets the border with all properties in one function.
 *
 * @param width The border width as a String
 * @param style The border style as a BorderStyle enum
 * @param color The border color
 * @param radius The border radius as a String
 * @return A new Modifier with all border properties applied
 */
fun Modifier.border(
    width: String? = null,
    style: BorderStyle? = null,
    color: String? = null,
    radius: String? = null
): Modifier {
    var modifier = this
    if (width != null) {
        modifier = modifier.style("border-width", width)
    }
    if (style != null) {
        modifier = modifier.borderStyle(style)
    }
    if (color != null) {
        modifier = modifier.borderColor(color)
    }
    if (radius != null) {
        modifier = modifier.borderRadius(radius)
    }
    return modifier
}

/**
 * Sets the box shadow.
 */
fun Modifier.boxShadow(value: String): Modifier =
    style("box-shadow", value)

/**
 * Sets the box shadow with specific parameters.
 *
 * @param horizontalOffset The horizontal offset of the shadow
 * @param verticalOffset The vertical offset of the shadow
 * @param blurRadius The blur radius of the shadow (use with .px extension, e.g., 20.px)
 * @param color The color of the shadow
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: String,
    color: code.yousef.summon.core.style.Color
): Modifier {
    val shadow = "$horizontalOffset $verticalOffset $blurRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the box shadow with specific parameters using string values for all offsets.
 *
 * @param horizontalOffset The horizontal offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param verticalOffset The vertical offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param blurRadius The blur radius of the shadow (use with unit extensions, e.g., 20.px)
 * @param color The color of the shadow
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: String,
    verticalOffset: String,
    blurRadius: String,
    color: code.yousef.summon.core.style.Color
): Modifier {
    val shadow = "$horizontalOffset $verticalOffset $blurRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the box shadow with specific parameters including spread radius.
 *
 * @param horizontalOffset The horizontal offset of the shadow
 * @param verticalOffset The vertical offset of the shadow
 * @param blurRadius The blur radius of the shadow (use with .px extension, e.g., 20.px)
 * @param spreadRadius The spread radius of the shadow (use with .px extension, e.g., 5.px)
 * @param color The color of the shadow
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: String,
    spreadRadius: String,
    color: code.yousef.summon.core.style.Color
): Modifier {
    val shadow = "$horizontalOffset $verticalOffset $blurRadius $spreadRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the box shadow with specific parameters using string values for all offsets including spread radius.
 *
 * @param horizontalOffset The horizontal offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param verticalOffset The vertical offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param blurRadius The blur radius of the shadow (use with unit extensions, e.g., 20.px)
 * @param spreadRadius The spread radius of the shadow (use with unit extensions, e.g., 5.px)
 * @param color The color of the shadow
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: String,
    verticalOffset: String,
    blurRadius: String,
    spreadRadius: String,
    color: code.yousef.summon.core.style.Color
): Modifier {
    val shadow = "$horizontalOffset $verticalOffset $blurRadius $spreadRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the box shadow with specific parameters including inset option.
 *
 * @param horizontalOffset The horizontal offset of the shadow
 * @param verticalOffset The vertical offset of the shadow
 * @param blurRadius The blur radius of the shadow (use with .px extension, e.g., 20.px)
 * @param color The color of the shadow
 * @param inset Whether the shadow should be inset (inner shadow)
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: String,
    color: code.yousef.summon.core.style.Color,
    inset: Boolean = false
): Modifier {
    val insetStr = if (inset) "inset " else ""
    val shadow = "$insetStr$horizontalOffset $verticalOffset $blurRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the box shadow with specific parameters including spread radius and inset option.
 *
 * @param horizontalOffset The horizontal offset of the shadow
 * @param verticalOffset The vertical offset of the shadow
 * @param blurRadius The blur radius of the shadow (use with .px extension, e.g., 20.px)
 * @param spreadRadius The spread radius of the shadow (use with .px extension, e.g., 5.px)
 * @param color The color of the shadow
 * @param inset Whether the shadow should be inset (inner shadow)
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: String,
    spreadRadius: String,
    color: code.yousef.summon.core.style.Color,
    inset: Boolean = false
): Modifier {
    val insetStr = if (inset) "inset " else ""
    val shadow = "$insetStr$horizontalOffset $verticalOffset $blurRadius $spreadRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the box shadow with specific parameters using string values for all offsets and including inset option.
 *
 * @param horizontalOffset The horizontal offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param verticalOffset The vertical offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param blurRadius The blur radius of the shadow (use with unit extensions, e.g., 20.px)
 * @param color The color of the shadow
 * @param inset Whether the shadow should be inset (inner shadow)
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: String,
    verticalOffset: String,
    blurRadius: String,
    color: code.yousef.summon.core.style.Color,
    inset: Boolean = false
): Modifier {
    val insetStr = if (inset) "inset " else ""
    val shadow = "$insetStr$horizontalOffset $verticalOffset $blurRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the box shadow with specific parameters using string values for all offsets including spread radius and inset option.
 *
 * @param horizontalOffset The horizontal offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param verticalOffset The vertical offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param blurRadius The blur radius of the shadow (use with unit extensions, e.g., 20.px)
 * @param spreadRadius The spread radius of the shadow (use with unit extensions, e.g., 5.px)
 * @param color The color of the shadow
 * @param inset Whether the shadow should be inset (inner shadow)
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: String,
    verticalOffset: String,
    blurRadius: String,
    spreadRadius: String,
    color: code.yousef.summon.core.style.Color,
    inset: Boolean = false
): Modifier {
    val insetStr = if (inset) "inset " else ""
    val shadow = "$insetStr$horizontalOffset $verticalOffset $blurRadius $spreadRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the transition property.
 */
fun Modifier.transition(value: String): Modifier =
    style("transition", value)

/**
 * Adds a CSS transition to an element with specified parameters.
 *
 * @param property CSS property to transition (as a TransitionProperty enum)
 * @param duration Duration of the transition in milliseconds
 * @param timingFunction CSS timing function (as a TransitionTimingFunction enum)
 * @param delay Delay before the transition starts, in milliseconds
 * @return A new Modifier with the transition styles added
 */
fun Modifier.transition(
    property: TransitionProperty = TransitionProperty.All,
    duration: Number = 300,
    timingFunction: TransitionTimingFunction = TransitionTimingFunction.Ease,
    delay: Number = 0
): Modifier {
    val transition = "${property} ${duration}ms ${timingFunction} ${delay}ms"
    return style("transition", transition)
}

/**
 * Adds a CSS transition to an element with specified parameters.
 *
 * @param property CSS property to transition (as a String)
 * @param duration Duration of the transition in milliseconds
 * @param timingFunction CSS timing function (as a TransitionTimingFunction enum)
 * @param delay Delay before the transition starts, in milliseconds
 * @return A new Modifier with the transition styles added
 */
fun Modifier.transition(
    property: String,
    duration: Number = 300,
    timingFunction: TransitionTimingFunction = TransitionTimingFunction.Ease,
    delay: Number = 0
): Modifier {
    val transition = "$property ${duration}ms ${timingFunction} ${delay}ms"
    return style("transition", transition)
}

/**
 * Adds a CSS transition to an element with specified parameters.
 *
 * @param property CSS property to transition (as a TransitionProperty enum)
 * @param duration Duration of the transition (using time unit extensions like 0.3.s or 300.ms)
 * @param timingFunction CSS timing function (as a TransitionTimingFunction enum)
 * @param delay Delay before the transition starts (using time unit extensions like 0.s or 0.ms)
 * @return A new Modifier with the transition styles added
 */
fun Modifier.transition(
    property: TransitionProperty = TransitionProperty.All,
    duration: String,
    timingFunction: TransitionTimingFunction = TransitionTimingFunction.Ease,
    delay: String = "0ms"
): Modifier {
    val transition = "${property} $duration ${timingFunction} $delay"
    return style("transition", transition)
}

/**
 * Sets which properties should transition.
 */
fun Modifier.transitionProperty(value: String): Modifier =
    style("transition-property", value)

/**
 * Sets which properties should transition using the TransitionProperty enum.
 */
fun Modifier.transitionProperty(value: TransitionProperty): Modifier =
    style("transition-property", value.toString())

/**
 * Sets the transition duration.
 */
fun Modifier.transitionDuration(value: String): Modifier =
    style("transition-duration", value)

/**
 * Sets the transition duration using a Number with time unit extensions.
 * @param value The duration as a Number with time unit (e.g., 0.3.s, 300.ms)
 */
fun Modifier.transitionDuration(value: Number): Modifier =
    style("transition-duration", value.toString() + "ms")

/**
 * Sets the transition timing function.
 */
fun Modifier.transitionTimingFunction(value: String): Modifier =
    style("transition-timing-function", value)

/**
 * Sets the transition timing function using the TransitionTimingFunction enum.
 */
fun Modifier.transitionTimingFunction(value: TransitionTimingFunction): Modifier =
    style("transition-timing-function", value.toString())

/**
 * Sets the transition delay.
 */
fun Modifier.transitionDelay(value: String): Modifier =
    style("transition-delay", value)

/**
 * Sets the transition delay using a Number with time unit extensions.
 * @param value The delay as a Number with time unit (e.g., 0.s, 300.ms)
 */
fun Modifier.transitionDelay(value: Number): Modifier =
    style("transition-delay", value.toString() + "ms")

/**
 * Sets the transform property.
 */
fun Modifier.transform(value: String): Modifier =
    style("transform", value)

/**
 * Translates the element along the X axis.
 */
fun Modifier.translateX(value: String): Modifier =
    style("transform", "translateX(${value})")

/**
 * Translates the element along the Y axis.
 */
fun Modifier.translateY(value: String): Modifier =
    style("transform", "translateY(${value})")

/**
 * Scales the element.
 */
fun Modifier.scale(value: Double): Modifier =
    style("transform", "scale(${value})")

/**
 * Rotates the element.
 */
fun Modifier.rotate(value: String): Modifier =
    style("transform", "rotate(${value})")

/**
 * Applies a blur filter.
 */
fun Modifier.blur(value: String): Modifier =
    style("filter", "blur(${value})")

/**
 * Applies a brightness filter.
 */
fun Modifier.brightness(value: Double): Modifier =
    style("filter", "brightness(${value})")

/**
 * Applies a contrast filter.
 */
fun Modifier.contrast(value: Double): Modifier =
    style("filter", "contrast(${value})")

/**
 * Applies a grayscale filter.
 */
fun Modifier.grayscale(value: Double): Modifier =
    style("filter", "grayscale(${value})")

/**
 * Applies a custom filter.
 */
fun Modifier.filter(value: String): Modifier =
    style("filter", value)

/**
 * Sets the animation.
 */
fun Modifier.animation(value: String): Modifier =
    style("animation", value)

/**
 * Sets the animation name.
 */
fun Modifier.animationName(value: String): Modifier =
    style("animation-name", value)

/**
 * Sets the animation duration.
 */
fun Modifier.animationDuration(value: String): Modifier =
    style("animation-duration", value)

/**
 * Sets the animation timing function.
 */
fun Modifier.animationTimingFunction(value: String): Modifier =
    style("animation-timing-function", value)

/**
 * Sets the animation delay.
 */
fun Modifier.animationDelay(value: String): Modifier =
    style("animation-delay", value)

/**
 * Sets the animation iteration count.
 */
fun Modifier.animationIterationCount(value: String): Modifier =
    style("animation-iteration-count", value)

/**
 * Sets the scroll behavior.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("scrollBehavior(value, null)"))
fun Modifier.scrollBehavior(value: String): Modifier =
    scrollBehavior(value, null)

/**
 * Sets the scrollbar width.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("scrollbarWidth(value, null)"))
fun Modifier.scrollbarWidth(value: String): Modifier =
    scrollbarWidth(value, null)

/**
 * Adds hover effect using another Modifier.
 */
fun Modifier.hover(hoverModifier: Modifier): Modifier {
    return hover(hoverModifier.styles)
}

/**
 * Sets the pointer-events CSS property.
 * Common values: "auto", "none".
 */
fun Modifier.pointerEvents(value: String): Modifier =
    style("pointer-events", value)

/**
 * Creates a radial gradient background.
 *
 * @param shape The shape of the gradient (e.g., "circle", "ellipse")
 * @param colors List of color stops in the format "color position" (e.g., "rgba(0, 247, 255, 0.15) 0%")
 * @param position Optional position of the gradient center (e.g., "center", "top left")
 * @return A new Modifier with the radial gradient background
 */
fun Modifier.radialGradient(
    shape: String = "circle",
    colors: List<String>,
    position: String = "center"
): Modifier {
    val colorStops = colors.joinToString(", ")
    return backgroundImage("radial-gradient($shape at $position, $colorStops)")
}

/**
 * Creates a radial gradient background using enum values for shape and position.
 *
 * @param shape The shape of the gradient as a RadialGradientShape enum
 * @param colors List of color stops in the format "color position" (e.g., "rgba(0, 247, 255, 0.15) 0%")
 * @param position Optional position of the gradient center as a RadialGradientPosition enum
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithEnums")
fun Modifier.radialGradient(
    shape: RadialGradientShape = RadialGradientShape.Circle,
    colors: List<String>,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier {
    return radialGradient(shape.toString(), colors, position.toString())
}

/**
 * Creates a radial gradient background with a simplified API.
 *
 * @param innerColor The color at the center of the gradient
 * @param outerColor The color at the edge of the gradient
 * @param innerPosition The position of the inner color (e.g., "0%")
 * @param outerPosition The position of the outer color (e.g., "70%")
 * @param shape The shape of the gradient (e.g., "circle", "ellipse")
 * @param position Optional position of the gradient center (e.g., "center", "top left")
 * @return A new Modifier with the radial gradient background
 */
fun Modifier.radialGradient(
    innerColor: String,
    outerColor: String,
    innerPosition: String = "0%",
    outerPosition: String = "100%",
    shape: String = "circle",
    position: String = "center"
): Modifier {
    return radialGradient(
        shape = shape,
        colors = listOf("$innerColor $innerPosition", "$outerColor $outerPosition"),
        position = position
    )
}

/**
 * Creates a radial gradient background with a simplified API using enum values for shape and position.
 *
 * @param innerColor The color at the center of the gradient
 * @param outerColor The color at the edge of the gradient
 * @param innerPosition The position of the inner color (e.g., "0%")
 * @param outerPosition The position of the outer color (e.g., "70%")
 * @param shape The shape of the gradient as a RadialGradientShape enum
 * @param position Optional position of the gradient center as a RadialGradientPosition enum
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithEnumsSimplified")
fun Modifier.radialGradient(
    innerColor: String,
    outerColor: String,
    innerPosition: String = "0%",
    outerPosition: String = "100%",
    shape: RadialGradientShape = RadialGradientShape.Circle,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier {
    return radialGradient(
        shape = shape.toString(),
        colors = listOf("$innerColor $innerPosition", "$outerColor $outerPosition"),
        position = position.toString()
    )
}

/**
 * Creates a radial gradient background with a simplified API using Number extensions for positions.
 *
 * @param innerColor The color at the center of the gradient
 * @param outerColor The color at the edge of the gradient
 * @param innerPosition The position of the inner color as a Number (will be converted to a percentage)
 * @param outerPosition The position of the outer color as a Number (will be converted to a percentage)
 * @param shape The shape of the gradient (e.g., "circle", "ellipse")
 * @param position Optional position of the gradient center (e.g., "center", "top left")
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithNumberPositions")
fun Modifier.radialGradient(
    innerColor: String,
    outerColor: String,
    innerPosition: Number,
    outerPosition: Number,
    shape: String = "circle",
    position: String = "center"
): Modifier {
    return radialGradient(
        innerColor = innerColor,
        outerColor = outerColor,
        innerPosition = "${innerPosition}%",
        outerPosition = "${outerPosition}%",
        shape = shape,
        position = position
    )
}

/**
 * Creates a radial gradient background with a simplified API using enum values and Number extensions.
 *
 * @param innerColor The color at the center of the gradient
 * @param outerColor The color at the edge of the gradient
 * @param innerPosition The position of the inner color as a Number (will be converted to a percentage)
 * @param outerPosition The position of the outer color as a Number (will be converted to a percentage)
 * @param shape The shape of the gradient as a RadialGradientShape enum
 * @param position Optional position of the gradient center as a RadialGradientPosition enum
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithEnumsAndNumberPositions")
fun Modifier.radialGradient(
    innerColor: String,
    outerColor: String,
    innerPosition: Number,
    outerPosition: Number,
    shape: RadialGradientShape = RadialGradientShape.Circle,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier {
    return radialGradient(
        innerColor = innerColor,
        outerColor = outerColor,
        innerPosition = "${innerPosition}%",
        outerPosition = "${outerPosition}%",
        shape = shape.toString(),
        position = position.toString()
    )
}

/**
 * Creates a radial gradient background using Color objects.
 *
 * @param shape The shape of the gradient (e.g., "circle", "ellipse")
 * @param colorStops List of Pairs containing Color objects and their positions
 * @param position Optional position of the gradient center (e.g., "center", "top left")
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorStops")
fun Modifier.radialGradient(
    shape: String = "circle",
    colorStops: List<Pair<code.yousef.summon.core.style.Color, String>>,
    position: String = "center"
): Modifier {
    val colorStopStrings = colorStops.map { "${it.first} ${it.second}" }
    return radialGradient(shape, colorStopStrings, position)
}

/**
 * Creates a radial gradient background using Color objects and enum values for shape and position.
 *
 * @param shape The shape of the gradient as a RadialGradientShape enum
 * @param colorStops List of Pairs containing Color objects and their positions
 * @param position Optional position of the gradient center as a RadialGradientPosition enum
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorStopsAndEnums")
fun Modifier.radialGradient(
    shape: RadialGradientShape = RadialGradientShape.Circle,
    colorStops: List<Pair<code.yousef.summon.core.style.Color, String>>,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier {
    val colorStopStrings = colorStops.map { "${it.first} ${it.second}" }
    return radialGradient(shape.toString(), colorStopStrings, position.toString())
}

/**
 * Creates a radial gradient background using Color objects with Number positions.
 *
 * @param shape The shape of the gradient (e.g., "circle", "ellipse")
 * @param colorStops List of Pairs containing Color objects and their positions as Numbers (will be converted to percentages)
 * @param position Optional position of the gradient center (e.g., "center", "top left")
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorStopsAndNumberPositions")
fun Modifier.radialGradient(
    shape: String = "circle",
    colorStops: List<Pair<code.yousef.summon.core.style.Color, Number>>,
    position: String = "center"
): Modifier {
    val colorStopStrings = colorStops.map { "${it.first} ${it.second}%" }
    return radialGradient(shape, colorStopStrings, position)
}

/**
 * Creates a radial gradient background using Color objects, enum values, and Number positions.
 *
 * @param shape The shape of the gradient as a RadialGradientShape enum
 * @param colorStops List of Pairs containing Color objects and their positions as Numbers (will be converted to percentages)
 * @param position Optional position of the gradient center as a RadialGradientPosition enum
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorStopsEnumsAndNumberPositions")
fun Modifier.radialGradient(
    shape: RadialGradientShape = RadialGradientShape.Circle,
    colorStops: List<Pair<code.yousef.summon.core.style.Color, Number>>,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier {
    val colorStopStrings = colorStops.map { "${it.first} ${it.second}%" }
    return radialGradient(shape.toString(), colorStopStrings, position.toString())
}

/**
 * Creates a radial gradient background with a simplified API using Color objects.
 *
 * @param innerColor The Color object at the center of the gradient
 * @param outerColor The Color object at the edge of the gradient
 * @param innerPosition The position of the inner color (e.g., "0%")
 * @param outerPosition The position of the outer color (e.g., "70%")
 * @param shape The shape of the gradient (e.g., "circle", "ellipse")
 * @param position Optional position of the gradient center (e.g., "center", "top left")
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorObjects")
fun Modifier.radialGradient(
    innerColor: code.yousef.summon.core.style.Color,
    outerColor: code.yousef.summon.core.style.Color,
    innerPosition: String = "0%",
    outerPosition: String = "100%",
    shape: String = "circle",
    position: String = "center"
): Modifier {
    return radialGradient(
        shape = shape,
        colorStops = listOf(innerColor to innerPosition, outerColor to outerPosition),
        position = position
    )
}

/**
 * Creates a radial gradient background with a simplified API using Color objects and enum values.
 *
 * @param innerColor The Color object at the center of the gradient
 * @param outerColor The Color object at the edge of the gradient
 * @param innerPosition The position of the inner color (e.g., "0%")
 * @param outerPosition The position of the outer color (e.g., "70%")
 * @param shape The shape of the gradient as a RadialGradientShape enum
 * @param position Optional position of the gradient center as a RadialGradientPosition enum
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorObjectsAndEnums")
fun Modifier.radialGradient(
    innerColor: code.yousef.summon.core.style.Color,
    outerColor: code.yousef.summon.core.style.Color,
    innerPosition: String = "0%",
    outerPosition: String = "100%",
    shape: RadialGradientShape = RadialGradientShape.Circle,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier {
    return radialGradient(
        shape = shape,
        colorStops = listOf(innerColor to innerPosition, outerColor to outerPosition),
        position = position
    )
}

/**
 * Creates a radial gradient background with a simplified API using Color objects and Number positions.
 *
 * @param innerColor The Color object at the center of the gradient
 * @param outerColor The Color object at the edge of the gradient
 * @param innerPosition The position of the inner color as a Number (will be converted to a percentage)
 * @param outerPosition The position of the outer color as a Number (will be converted to a percentage)
 * @param shape The shape of the gradient (e.g., "circle", "ellipse")
 * @param position Optional position of the gradient center (e.g., "center", "top left")
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorObjectsAndNumberPositions")
fun Modifier.radialGradient(
    innerColor: code.yousef.summon.core.style.Color,
    outerColor: code.yousef.summon.core.style.Color,
    innerPosition: Number,
    outerPosition: Number,
    shape: String = "circle",
    position: String = "center"
): Modifier {
    return radialGradient(
        innerColor = innerColor,
        outerColor = outerColor,
        innerPosition = "${innerPosition}%",
        outerPosition = "${outerPosition}%",
        shape = shape,
        position = position
    )
}

/**
 * Creates a radial gradient background with a simplified API using Color objects, enum values, and Number positions.
 *
 * @param innerColor The Color object at the center of the gradient
 * @param outerColor The Color object at the edge of the gradient
 * @param innerPosition The position of the inner color as a Number (will be converted to a percentage)
 * @param outerPosition The position of the outer color as a Number (will be converted to a percentage)
 * @param shape The shape of the gradient as a RadialGradientShape enum
 * @param position Optional position of the gradient center as a RadialGradientPosition enum
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorObjectsEnumsAndNumberPositions")
fun Modifier.radialGradient(
    innerColor: code.yousef.summon.core.style.Color,
    outerColor: code.yousef.summon.core.style.Color,
    innerPosition: Number,
    outerPosition: Number,
    shape: RadialGradientShape = RadialGradientShape.Circle,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier {
    return radialGradient(
        innerColor = innerColor,
        outerColor = outerColor,
        innerPosition = "${innerPosition}%",
        outerPosition = "${outerPosition}%",
        shape = shape,
        position = position
    )
}

/**
 * Creates a linear gradient background.
 *
 * @param direction The direction of the gradient (e.g., "to right", "to bottom", "45deg")
 * @param colors List of color stops in the format "color position" (e.g., "rgba(0, 247, 255, 0.15) 0%")
 * @return A new Modifier with the linear gradient background
 */
fun Modifier.linearGradient(
    direction: String = "to right",
    colors: List<String>
): Modifier {
    val colorStops = colors.joinToString(", ")
    return backgroundImage("linear-gradient($direction, $colorStops)")
}

/**
 * Creates a linear gradient background with a simplified API.
 *
 * @param startColor The color at the start of the gradient
 * @param endColor The color at the end of the gradient
 * @param startPosition The position of the start color (e.g., "0%")
 * @param endPosition The position of the end color (e.g., "100%")
 * @param direction The direction of the gradient (e.g., "to right", "to bottom", "45deg")
 * @return A new Modifier with the linear gradient background
 */
fun Modifier.linearGradient(
    startColor: String,
    endColor: String,
    startPosition: String = "0%",
    endPosition: String = "100%",
    direction: String = "to right"
): Modifier {
    return linearGradient(
        direction = direction,
        colors = listOf("$startColor $startPosition", "$endColor $endPosition")
    )
}

/**
 * Creates a linear gradient background using Color objects.
 *
 * @param direction The direction of the gradient (e.g., "to right", "to bottom", "45deg")
 * @param colorStops List of Pairs containing Color objects and their positions
 * @return A new Modifier with the linear gradient background
 */
@JvmName("linearGradientWithColorStops")
fun Modifier.linearGradient(
    direction: String = "to right",
    colorStops: List<Pair<code.yousef.summon.core.style.Color, String>>
): Modifier {
    val colorStopStrings = colorStops.map { "${it.first} ${it.second}" }
    return linearGradient(direction, colorStopStrings)
}

/**
 * Creates a linear gradient background with a simplified API using Color objects.
 *
 * @param startColor The Color object at the start of the gradient
 * @param endColor The Color object at the end of the gradient
 * @param startPosition The position of the start color (e.g., "0%")
 * @param endPosition The position of the end color (e.g., "100%")
 * @param direction The direction of the gradient (e.g., "to right", "to bottom", "45deg")
 * @return A new Modifier with the linear gradient background
 */
@JvmName("linearGradientWithColorObjects")
fun Modifier.linearGradient(
    startColor: code.yousef.summon.core.style.Color,
    endColor: code.yousef.summon.core.style.Color,
    startPosition: String = "0%",
    endPosition: String = "100%",
    direction: String = "to right"
): Modifier {
    return linearGradient(
        direction = direction,
        colorStops = listOf(startColor to startPosition, endColor to endPosition)
    )
}

/**
 * Creates a linear gradient background with direction first and Color objects.
 *
 * @param gradientDirection The direction of the gradient (e.g., "to right", "to bottom", "45deg", "90deg")
 * @param startColor The Color object at the start of the gradient
 * @param endColor The Color object at the end of the gradient
 * @param startPosition The position of the start color (e.g., "0%")
 * @param endPosition The position of the end color (e.g., "100%")
 * @return A new Modifier with the linear gradient background
 */
@JvmName("linearGradientWithDirectionFirst")
fun Modifier.linearGradient(
    gradientDirection: String,
    startColor: code.yousef.summon.core.style.Color,
    endColor: code.yousef.summon.core.style.Color,
    startPosition: String = "0%",
    endPosition: String = "100%"
): Modifier {
    return linearGradient(
        direction = gradientDirection,
        colorStops = listOf(startColor to startPosition, endColor to endPosition)
    )
}

// === Backdrop Filter Modifiers ===

/**
 * Sets the backdrop-filter CSS property.
 * Used for creating glass morphism effects.
 *
 * @param value The backdrop filter value (e.g., "blur(10px)", "blur(5px) brightness(1.1)")
 * @return A new Modifier with the backdrop filter applied
 */
fun Modifier.backdropFilter(value: String): Modifier =
    style("backdrop-filter", value)

/**
 * Applies a backdrop blur effect.
 * Creates a glass-like blur behind the element.
 *
 * @param value The blur radius (e.g., "10px", "5px")
 * @return A new Modifier with backdrop blur applied
 */
fun Modifier.backdropBlur(value: String): Modifier =
    backdropFilter("blur($value)")

/**
 * Applies a backdrop blur effect with a numeric value in pixels.
 *
 * @param value The blur radius in pixels
 * @return A new Modifier with backdrop blur applied
 */
fun Modifier.backdropBlur(value: Number): Modifier =
    backdropBlur("${value}px")

/**
 * Applies backdrop brightness adjustment.
 * Values > 1.0 brighten, values < 1.0 darken.
 *
 * @param value The brightness multiplier (e.g., 1.1 for 10% brighter)
 * @return A new Modifier with backdrop brightness applied
 */
fun Modifier.backdropBrightness(value: Double): Modifier =
    backdropFilter("brightness($value)")

/**
 * Applies backdrop contrast adjustment.
 * Values > 1.0 increase contrast, values < 1.0 decrease contrast.
 *
 * @param value The contrast multiplier (e.g., 1.2 for 20% more contrast)
 * @return A new Modifier with backdrop contrast applied
 */
fun Modifier.backdropContrast(value: Double): Modifier =
    backdropFilter("contrast($value)")

/**
 * Applies backdrop saturation adjustment.
 * Values > 1.0 increase saturation, values < 1.0 decrease saturation.
 *
 * @param value The saturation multiplier (e.g., 1.3 for 30% more saturation)
 * @return A new Modifier with backdrop saturation applied
 */
fun Modifier.backdropSaturate(value: Double): Modifier =
    backdropFilter("saturate($value)")

/**
 * Applies backdrop grayscale effect.
 *
 * @param value The grayscale amount (0.0 to 1.0, where 1.0 is completely grayscale)
 * @return A new Modifier with backdrop grayscale applied
 */
fun Modifier.backdropGrayscale(value: Double): Modifier =
    backdropFilter("grayscale($value)")

/**
 * Applies backdrop hue rotation.
 *
 * @param degrees The rotation in degrees (e.g., 90, 180, 270)
 * @return A new Modifier with backdrop hue rotation applied
 */
fun Modifier.backdropHueRotate(degrees: Number): Modifier =
    backdropFilter("hue-rotate(${degrees}deg)")

/**
 * Applies backdrop invert effect.
 *
 * @param value The invert amount (0.0 to 1.0, where 1.0 is completely inverted)
 * @return A new Modifier with backdrop invert applied
 */
fun Modifier.backdropInvert(value: Double): Modifier =
    backdropFilter("invert($value)")

/**
 * Applies backdrop sepia effect.
 *
 * @param value The sepia amount (0.0 to 1.0, where 1.0 is completely sepia)
 * @return A new Modifier with backdrop sepia applied
 */
fun Modifier.backdropSepia(value: Double): Modifier =
    backdropFilter("sepia($value)")

/**
 * Combines multiple backdrop filters.
 *
 * @param filters List of filter functions (e.g., "blur(10px)", "brightness(1.1)")
 * @return A new Modifier with combined backdrop filters
 */
fun Modifier.combineBackdropFilters(vararg filters: String): Modifier =
    backdropFilter(filters.joinToString(" "))

/**
 * Creates a glass morphism effect with common settings.
 * Applies blur and slight brightness increase for a glass-like appearance.
 *
 * @param blurAmount The blur radius (default: "10px")
 * @param brightness The brightness multiplier (default: 1.05)
 * @return A new Modifier with glass morphism effect
 */
fun Modifier.glassMorphism(
    blurAmount: String = "10px",
    brightness: Double = 1.05
): Modifier =
    combineBackdropFilters("blur($blurAmount)", "brightness($brightness)")

/**
 * Creates a glass morphism effect with numeric blur value.
 *
 * @param blurPx The blur radius in pixels (default: 10)
 * @param brightness The brightness multiplier (default: 1.05)
 * @return A new Modifier with glass morphism effect
 */
fun Modifier.glassMorphism(
    blurPx: Number = 10,
    brightness: Double = 1.05
): Modifier =
    glassMorphism("${blurPx}px", brightness)

// === Multiple Shadow System ===

/**
 * Data class representing a single shadow configuration.
 */
data class ShadowConfig(
    val horizontalOffset: String,
    val verticalOffset: String,
    val blurRadius: String,
    val spreadRadius: String? = null,
    val color: code.yousef.summon.core.style.Color,
    val inset: Boolean = false
) {
    /**
     * Converts this shadow configuration to a CSS shadow string.
     */
    fun toCssString(): String {
        val insetStr = if (inset) "inset " else ""
        val colorStr = color.toCssString()
        return if (spreadRadius != null) {
            "$insetStr$horizontalOffset $verticalOffset $blurRadius $spreadRadius $colorStr"
        } else {
            "$insetStr$horizontalOffset $verticalOffset $blurRadius $colorStr"
        }
    }

    companion object {
        /**
         * Type-safe factory function using unit extensions.
         *
         * @param horizontalOffset Horizontal offset with unit extension (e.g., 0.px)
         * @param verticalOffset Vertical offset with unit extension (e.g., 8.px)
         * @param blurRadius Blur radius with unit extension (e.g., 32.px)
         * @param spreadRadius Optional spread radius with unit extension
         * @param color Type-safe Color object
         * @param inset Whether this is an inset (inner) shadow
         * @return ShadowConfig with proper type safety
         */
        fun create(
            horizontalOffset: Number,
            verticalOffset: Number,
            blurRadius: Number,
            spreadRadius: Number? = null,
            color: code.yousef.summon.core.style.Color,
            inset: Boolean = false
        ) = ShadowConfig(
            horizontalOffset.px,
            verticalOffset.px,
            blurRadius.px,
            spreadRadius?.px,
            color,
            inset
        )

        /**
         * Creates a glow effect shadow (centered with no spread).
         *
         * @param blurRadius Blur radius with unit extension (e.g., 20.px)
         * @param color Type-safe Color object
         * @param intensity Optional spread intensity with unit extension
         * @return ShadowConfig configured as a glow effect
         */
        fun glow(
            blurRadius: Number,
            color: code.yousef.summon.core.style.Color,
            intensity: Number = 0
        ) = ShadowConfig(
            0.px,
            0.px,
            blurRadius.px,
            if (intensity.toDouble() != 0.0) intensity.px else null,
            color,
            false
        )

        /**
         * Creates an inner glow effect shadow.
         *
         * @param blurRadius Blur radius with unit extension (e.g., 10.px)
         * @param color Type-safe Color object
         * @return ShadowConfig configured as an inner glow effect
         */
        fun innerGlow(
            blurRadius: Number,
            color: code.yousef.summon.core.style.Color
        ) = ShadowConfig(
            0.px,
            0.px,
            blurRadius.px,
            null,
            color,
            true
        )
    }
}

/**
 * Creates a ShadowConfig with numeric offsets (converted to pixels).
 */
fun shadowConfig(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: Number,
    spreadRadius: Number? = null,
    color: String,
    inset: Boolean = false
): ShadowConfig = ShadowConfig(
    horizontalOffset = "${horizontalOffset}px",
    verticalOffset = "${verticalOffset}px",
    blurRadius = "${blurRadius}px",
    spreadRadius = spreadRadius?.let { "${it}px" },
    color = code.yousef.summon.core.style.Color.fromHex(color),
    inset = inset
)

/**
 * Creates a ShadowConfig using Color object.
 */
fun shadowConfig(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: Number,
    spreadRadius: Number? = null,
    color: code.yousef.summon.core.style.Color,
    inset: Boolean = false
): ShadowConfig = shadowConfig(
    horizontalOffset, verticalOffset, blurRadius, spreadRadius, color.toString(), inset
)

/**
 * Applies multiple box shadows.
 * Allows creating complex shadow effects by combining multiple shadows.
 *
 * @param shadows List of shadow configurations
 * @return A new Modifier with multiple shadows applied
 */
fun Modifier.multipleShadows(shadows: List<ShadowConfig>): Modifier {
    val shadowStrings = shadows.map { it.toCssString() }
    return boxShadow(shadowStrings.joinToString(", "))
}

/**
 * Applies multiple box shadows using vararg.
 *
 * @param shadows Variable number of shadow configurations
 * @return A new Modifier with multiple shadows applied
 */
fun Modifier.multipleShadows(vararg shadows: ShadowConfig): Modifier =
    multipleShadows(shadows.toList())

/**
 * Adds a shadow to existing shadows on this modifier.
 * This allows chaining shadow effects.
 *
 * @param shadow The shadow configuration to add
 * @return A new Modifier with the additional shadow
 */
fun Modifier.addShadow(shadow: ShadowConfig): Modifier {
    val currentShadow = this.styles["box-shadow"]
    return if (currentShadow != null) {
        boxShadow("$currentShadow, ${shadow.toCssString()}")
    } else {
        boxShadow(shadow.toCssString())
    }
}

/**
 * Adds a shadow with numeric parameters.
 *
 * @param horizontalOffset The horizontal offset in pixels
 * @param verticalOffset The vertical offset in pixels
 * @param blurRadius The blur radius in pixels
 * @param spreadRadius The spread radius in pixels (optional)
 * @param color The shadow color
 * @param inset Whether the shadow is inset (default: false)
 * @return A new Modifier with the additional shadow
 */
fun Modifier.addShadow(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: Number,
    spreadRadius: Number? = null,
    color: String,
    inset: Boolean = false
): Modifier = addShadow(
    shadowConfig(horizontalOffset, verticalOffset, blurRadius, spreadRadius, color, inset)
)

/**
 * Adds a shadow with Color object.
 */
fun Modifier.addShadow(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: Number,
    spreadRadius: Number? = null,
    color: code.yousef.summon.core.style.Color,
    inset: Boolean = false
): Modifier = addShadow(
    shadowConfig(horizontalOffset, verticalOffset, blurRadius, spreadRadius, color, inset)
)

/**
 * Creates a glow effect using multiple shadows.
 * Useful for creating aurora-like glowing effects.
 *
 * @param color The glow color
 * @param intensity The glow intensity (1 = subtle, 3 = intense)
 * @param size The size of the glow effect in pixels
 * @return A new Modifier with glow effect
 */
fun Modifier.glow(
    color: String,
    intensity: Int = 2,
    size: Number = 20
): Modifier {
    val shadows = mutableListOf<ShadowConfig>()

    // Create multiple shadows with increasing blur for glow effect
    for (i in 1..intensity) {
        val blur = size.toDouble() * i
        shadows.add(shadowConfig(0, 0, blur.toInt(), color = color))
    }

    return multipleShadows(shadows)
}

/**
 * Creates a glow effect using Color object.
 */
fun Modifier.glow(
    color: code.yousef.summon.core.style.Color,
    intensity: Int = 2,
    size: Number = 20
): Modifier = glow(color.toString(), intensity, size)

/**
 * Creates an inner glow effect using inset shadows.
 *
 * @param color The glow color
 * @param intensity The glow intensity (1 = subtle, 3 = intense)
 * @param size The size of the glow effect in pixels
 * @return A new Modifier with inner glow effect
 */
fun Modifier.innerGlow(
    color: String,
    intensity: Int = 2,
    size: Number = 10
): Modifier {
    val shadows = mutableListOf<ShadowConfig>()

    // Create multiple inset shadows for inner glow
    for (i in 1..intensity) {
        val blur = size.toDouble() * i
        shadows.add(shadowConfig(0, 0, blur.toInt(), color = color, inset = true))
    }

    return multipleShadows(shadows)
}

/**
 * Creates an inner glow effect using Color object.
 */
fun Modifier.innerGlow(
    color: code.yousef.summon.core.style.Color,
    intensity: Int = 2,
    size: Number = 10
): Modifier = innerGlow(color.toString(), intensity, size)

/**
 * Creates an aurora-style multi-colored glow effect.
 * Perfect for the Aurora Portal and similar effects.
 *
 * @param colors List of colors to use in the aurora effect
 * @param baseSize Base size for the glow effects
 * @return A new Modifier with aurora glow effect
 */
fun Modifier.auroraGlow(
    colors: List<String>,
    baseSize: Number = 20
): Modifier {
    val shadows = mutableListOf<ShadowConfig>()

    colors.forEachIndexed { index, color ->
        val size = baseSize.toDouble() * (index + 1)
        val blur = size * 1.5
        shadows.add(shadowConfig(0, 0, blur.toInt(), color = color))
    }

    return multipleShadows(shadows)
}

/**
 * Creates an aurora-style glow with predefined aurora colors.
 * Uses the classic aurora color palette: teal, blue, purple, pink.
 *
 * @param baseSize Base size for the glow effects (default: 20)
 * @return A new Modifier with aurora glow effect
 */
fun Modifier.auroraGlow(baseSize: Number = 20): Modifier =
    auroraGlow(
        colors = listOf(
            "rgba(240, 249, 255, 0.8)",  // Light blue
            "rgba(56, 178, 172, 0.6)",   // Teal
            "rgba(44, 82, 130, 0.7)",    // Navy blue
            "rgba(155, 44, 44, 0.5)"     // Dark red/purple
        ),
        baseSize = baseSize
    )

// ========================
// Clip Path Modifiers
// ========================

/**
 * Sets the clip-path CSS property with a custom value.
 *
 * @param value The clip-path value (e.g., "circle(50%)", "polygon(0% 0%, 100% 0%, 50% 100%)")
 * @return A new Modifier with the clip-path style applied
 */
fun Modifier.clipPath(value: String): Modifier =
    style("clip-path", value)

/**
 * Clips the element to a circle shape.
 *
 * @param radius The radius of the circle (e.g., "50%", "100px", "50px at 25% 25%")
 * @return A new Modifier with circle clip-path applied
 */
fun Modifier.clipCircle(radius: String = "50%"): Modifier =
    clipPath("circle($radius)")

/**
 * Clips the element to a circle shape with center position.
 *
 * @param radius The radius of the circle (e.g., "50%", "100px")
 * @param centerX The X position of the circle center (e.g., "25%", "100px")
 * @param centerY The Y position of the circle center (e.g., "25%", "100px")
 * @return A new Modifier with circle clip-path applied
 */
fun Modifier.clipCircle(radius: String, centerX: String, centerY: String): Modifier =
    clipPath("circle($radius at $centerX $centerY)")

/**
 * Clips the element to an ellipse shape.
 *
 * @param radiusX The horizontal radius (e.g., "50%", "100px")
 * @param radiusY The vertical radius (e.g., "50%", "100px")
 * @return A new Modifier with ellipse clip-path applied
 */
fun Modifier.clipEllipse(radiusX: String = "50%", radiusY: String = "50%"): Modifier =
    clipPath("ellipse($radiusX $radiusY)")

/**
 * Clips the element to an ellipse shape with center position.
 *
 * @param radiusX The horizontal radius (e.g., "50%", "100px")
 * @param radiusY The vertical radius (e.g., "50%", "100px")
 * @param centerX The X position of the ellipse center (e.g., "25%", "100px")
 * @param centerY The Y position of the ellipse center (e.g., "25%", "100px")
 * @return A new Modifier with ellipse clip-path applied
 */
fun Modifier.clipEllipse(radiusX: String, radiusY: String, centerX: String, centerY: String): Modifier =
    clipPath("ellipse($radiusX $radiusY at $centerX $centerY)")

/**
 * Clips the element to a polygon shape defined by a list of points.
 *
 * @param points List of coordinate pairs (e.g., listOf("0% 0%", "100% 0%", "50% 100%"))
 * @return A new Modifier with polygon clip-path applied
 */
fun Modifier.clipPolygon(points: List<String>): Modifier =
    clipPath("polygon(${points.joinToString(", ")})")

/**
 * Clips the element to a polygon shape defined by coordinate pairs.
 *
 * @param points Vararg of coordinate pairs (e.g., "0% 0%", "100% 0%", "50% 100%")
 * @return A new Modifier with polygon clip-path applied
 */
fun Modifier.clipPolygon(vararg points: String): Modifier =
    clipPolygon(points.toList())

/**
 * Clips the element to a rectangle shape using inset values.
 *
 * @param top Inset from top (e.g., "10px", "5%")
 * @param right Inset from right (e.g., "10px", "5%")
 * @param bottom Inset from bottom (e.g., "10px", "5%")
 * @param left Inset from left (e.g., "10px", "5%")
 * @param round Optional border radius for rounded corners (e.g., "10px")
 * @return A new Modifier with inset clip-path applied
 */
fun Modifier.clipInset(
    top: String,
    right: String = top,
    bottom: String = top,
    left: String = right,
    round: String? = null
): Modifier {
    val roundPart = if (round != null) " round $round" else ""
    return clipPath("inset($top $right $bottom $left$roundPart)")
}

/**
 * Clips the element to a triangle pointing up.
 *
 * @return A new Modifier with triangle clip-path applied
 */
fun Modifier.clipTriangleUp(): Modifier =
    clipPolygon("50% 0%", "0% 100%", "100% 100%")

/**
 * Clips the element to a triangle pointing down.
 *
 * @return A new Modifier with triangle clip-path applied
 */
fun Modifier.clipTriangleDown(): Modifier =
    clipPolygon("50% 100%", "0% 0%", "100% 0%")

/**
 * Clips the element to a triangle pointing left.
 *
 * @return A new Modifier with triangle clip-path applied
 */
fun Modifier.clipTriangleLeft(): Modifier =
    clipPolygon("0% 50%", "100% 0%", "100% 100%")

/**
 * Clips the element to a triangle pointing right.
 *
 * @return A new Modifier with triangle clip-path applied
 */
fun Modifier.clipTriangleRight(): Modifier =
    clipPolygon("100% 50%", "0% 0%", "0% 100%")

/**
 * Clips the element to a diamond shape.
 *
 * @return A new Modifier with diamond clip-path applied
 */
fun Modifier.clipDiamond(): Modifier =
    clipPolygon("50% 0%", "100% 50%", "50% 100%", "0% 50%")

/**
 * Clips the element to a hexagon shape.
 *
 * @return A new Modifier with hexagon clip-path applied
 */
fun Modifier.clipHexagon(): Modifier =
    clipPolygon("30% 0%", "70% 0%", "100% 50%", "70% 100%", "30% 100%", "0% 50%")

/**
 * Clips the element to a star shape.
 *
 * @return A new Modifier with star clip-path applied
 */
fun Modifier.clipStar(): Modifier =
    clipPolygon(
        "50% 0%", "61% 35%", "98% 35%", "68% 57%", "79% 91%",
        "50% 70%", "21% 91%", "32% 57%", "2% 35%", "39% 35%"
    )

/**
 * Clips the element to an arrow pointing right.
 *
 * @return A new Modifier with arrow clip-path applied
 */
fun Modifier.clipArrowRight(): Modifier =
    clipPolygon("0% 20%", "60% 20%", "60% 0%", "100% 50%", "60% 100%", "60% 80%", "0% 80%")

/**
 * Clips the element to an arrow pointing left.
 *
 * @return A new Modifier with arrow clip-path applied
 */
fun Modifier.clipArrowLeft(): Modifier =
    clipPolygon("40% 0%", "40% 20%", "100% 20%", "100% 80%", "40% 80%", "40% 100%", "0% 50%")

/**
 * Clips the element to a chevron pointing right.
 *
 * @return A new Modifier with chevron clip-path applied
 */
fun Modifier.clipChevronRight(): Modifier =
    clipPolygon("75% 0%", "100% 50%", "75% 100%", "25% 100%", "50% 50%", "25% 0%")

/**
 * Clips the element to a chevron pointing left.
 *
 * @return A new Modifier with chevron clip-path applied
 */
fun Modifier.clipChevronLeft(): Modifier =
    clipPolygon("25% 0%", "0% 50%", "25% 100%", "75% 100%", "50% 50%", "75% 0%")

/**
 * Clips the element to a message bubble shape with tail on the left.
 *
 * @return A new Modifier with message bubble clip-path applied
 */
fun Modifier.clipMessageBubble(): Modifier =
    clipPolygon("0% 0%", "100% 0%", "100% 75%", "75% 75%", "75% 100%", "50% 75%", "0% 75%")

// ========================
// Type-Safe Transform & Filter APIs  
// ========================

/**
 * Applies multiple CSS transform functions in a type-safe manner.
 *
 * @param transforms Variable number of transform function pairs
 * @return A new Modifier with combined transforms applied
 */
fun Modifier.transform(vararg transforms: Pair<TransformFunction, String>): Modifier =
    style("transform", transforms.joinToString(" ") { "${it.first}(${it.second})" })

/**
 * Applies multiple CSS filter functions in a type-safe manner.
 *
 * @param filters Variable number of filter function pairs
 * @return A new Modifier with combined filters applied
 */
fun Modifier.filter(vararg filters: Pair<FilterFunction, String>): Modifier =
    style("filter", filters.joinToString(" ") { "${it.first}(${it.second})" })

/**
 * Applies a 3D rotation with type-safe parameters.
 *
 * @param x X-axis component
 * @param y Y-axis component
 * @param z Z-axis component
 * @param angle Rotation angle with unit extension (e.g., 45.deg)
 * @return A new Modifier with 3D rotation applied
 */
fun Modifier.rotate3d(x: Number, y: Number, z: Number, angle: Number): Modifier =
    transform(TransformFunction.Rotate3d to "$x $y $z ${angle}deg")

/**
 * Applies CSS perspective with type-safe units.
 *
 * @param distance Perspective distance with unit extension (e.g., 1000.px)
 * @return A new Modifier with perspective applied
 */
fun Modifier.perspective(distance: Number): Modifier =
    transform(TransformFunction.Perspective to distance.px)

/**
 * Applies multiple filters with type-safe parameters.
 *
 * @param blur Blur radius with unit extension (e.g., 4.px)
 * @param brightness Brightness multiplier (e.g., 1.1)
 * @param contrast Contrast multiplier (e.g., 1.05)
 * @param saturate Saturation multiplier (e.g., 1.2)
 * @return A new Modifier with combined filters applied
 */
fun Modifier.multiFilter(
    blur: Number? = null,
    brightness: Number? = null,
    contrast: Number? = null,
    saturate: Number? = null
): Modifier {
    val filters = mutableListOf<Pair<FilterFunction, String>>()
    blur?.let { filters.add(FilterFunction.Blur to it.px) }
    brightness?.let { filters.add(FilterFunction.Brightness to it.toString()) }
    contrast?.let { filters.add(FilterFunction.Contrast to it.toString()) }
    saturate?.let { filters.add(FilterFunction.Saturate to it.toString()) }
    return filter(*filters.toTypedArray())
}

/**
 * Applies type-safe CSS animation with enum parameters.
 *
 * @param name Animation name
 * @param duration Animation duration enum
 * @param easing Easing function
 * @param delay Animation delay enum
 * @param iterationCount Number of iterations or "infinite"
 * @param direction Animation direction enum
 * @param fillMode Animation fill mode enum
 * @return A new Modifier with animation applied
 */
fun Modifier.animation(
    name: String,
    duration: AnimationDuration = AnimationDuration.Medium,
    easing: String = "ease",
    delay: AnimationDuration = AnimationDuration.Instant,
    iterationCount: String = "1",
    direction: AnimationDirection = AnimationDirection.Normal,
    fillMode: AnimationFillMode = AnimationFillMode.None
): Modifier =
    style("animation", "$name $duration $easing $delay $iterationCount $direction $fillMode")
