/**
 * # Keyframes Generator
 *
 * Comprehensive CSS keyframes generation utility for the Summon framework.
 * This module provides a complete toolkit for creating sophisticated CSS
 * animations through programmatic keyframe definition.
 *
 * ## Core Features
 *
 * - **Predefined Animations**: Ready-to-use keyframes for common effects
 * - **Custom Keyframes**: Build complex animations from scratch
 * - **Easing Integration**: Generate keyframes from mathematical easing functions
 * - **Performance Optimized**: Hardware-accelerated properties when possible
 * - **Cross-Browser Compatible**: CSS that works across all modern browsers
 *
 * ## Animation Categories
 *
 * ### Entrance/Exit Effects
 * - Fade in/out animations
 * - Slide animations from all directions
 * - Zoom in/out effects
 * - 3D flip transformations
 *
 * ### Attention Seekers
 * - Pulse effects with configurable intensity
 * - Shake animations for error states
 * - Bounce effects for playful interactions
 * - Floating animations for ambient movement
 *
 * ### Utility Animations
 * - Typing effects for dynamic text
 * - Blinking cursor for text inputs
 * - Color transitions for state changes
 * - Background color animations
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Generate a fade-in animation
 * val fadeInCSS = KeyframesGenerator.fadeIn("my-fade-in")
 *
 * // Create a custom slide effect
 * val slideCSS = KeyframesGenerator.slideInFromLeft(
 *     name = "slide-left",
 *     distance = 50
 * )
 *
 * // Build a complex pulse animation
 * val pulseCSS = KeyframesGenerator.pulse(
 *     name = "attention-pulse",
 *     minScale = 0.9f,
 *     maxScale = 1.1f
 * )
 *
 * // Generate from custom easing function
 * val customCSS = KeyframesGenerator.fromEasing(
 *     name = "elastic-scale",
 *     easing = { t -> elasticOut(t) },
 *     property = "transform",
 *     fromValue = 0.5f,
 *     toValue = 1.0f,
 *     steps = 20
 * )
 * ```
 *
 * ## Advanced Features
 *
 * ### Custom Keyframes Builder
 * ```kotlin
 * val customAnimation = KeyframesGenerator.custom(
 *     name = "complex-animation",
 *     keyframes = mapOfCompat(
 *         "0%" to mapOfCompat(
 *             "transform" to "scale(1) rotate(0deg)",
 *             "opacity" to "1"
 *         ),
 *         "50%" to mapOfCompat(
 *             "transform" to "scale(1.2) rotate(180deg)",
 *             "opacity" to "0.7"
 *         ),
 *         "100%" to mapOfCompat(
 *             "transform" to "scale(1) rotate(360deg)",
 *             "opacity" to "1"
 *         )
 *     )
 * )
 * ```
 *
 * ### Easing-Based Generation
 * ```kotlin
 * // Generate smooth keyframes from mathematical functions
 * val smoothAnimation = KeyframesGenerator.fromEasing(
 *     name = "smooth-scale",
 *     easing = Easing.CUBIC_BEZIER(0.25f, 0.1f, 0.25f, 1.0f),
 *     property = "transform",
 *     fromValue = 0.8f,
 *     toValue = 1.0f,
 *     steps = 15
 * )
 * ```
 *
 * ## Performance Considerations
 *
 * - **Transform Properties**: Uses transform for hardware acceleration
 * - **Opacity Animations**: Leverages compositor for smooth rendering
 * - **Minimal Reflow**: Avoids properties that trigger layout recalculation
 * - **Step Optimization**: Configurable keyframe density for performance tuning
 *
 * ## Integration with Components
 *
 * Generated keyframes work seamlessly with animation modifiers:
 *
 * ```kotlin
 * // Generate keyframes
 * val slideKeyframes = KeyframesGenerator.slideInFromTop()
 *
 * // Apply to component
 * Box(
 *     modifier = Modifier()
 *         .animate(
 *             name = "slide-in-top",
 *             duration = 500,
 *             timingFunction = "ease-out"
 *         )
 * )
 * ```
 *
 * @see AnimationModifiers for applying generated keyframes
 * @see Easing for mathematical easing functions
 * @see Animation for core animation system
 * @since 1.0.0
 */
package code.yousef.summon.animation

/**
 * Comprehensive utility for generating CSS keyframes definitions.
 *
 * This object provides a complete toolkit for creating CSS animations
 * programmatically, from simple fade effects to complex multi-property
 * animations with custom easing curves.
 *
 * All generated keyframes are optimized for performance and cross-browser
 * compatibility, using hardware-accelerated properties where possible.
 *
 * @since 1.0.0
 */
object KeyframesGenerator {

    /**
     * Generates a CSS keyframes definition for fading in an element.
     *
     * @param name The name of the keyframes
     * @return A CSS keyframes string
     */
    fun fadeIn(name: String = "fade-in"): String = """
        @keyframes $name {
            0% { opacity: 0; }
            100% { opacity: 1; }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for fading out an element.
     *
     * @param name The name of the keyframes
     * @return A CSS keyframes string
     */
    fun fadeOut(name: String = "fade-out"): String = """
        @keyframes $name {
            0% { opacity: 1; }
            100% { opacity: 0; }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for a slide-in animation from the top.
     *
     * @param name The name of the keyframes
     * @param distance The distance to slide in pixels
     * @return A CSS keyframes string
     */
    fun slideInFromTop(name: String = "slide-in-top", distance: Int = 30): String = """
        @keyframes $name {
            0% { 
                transform: translateY(-${distance}px);
                opacity: 0;
            }
            100% { 
                transform: translateY(0);
                opacity: 1;
            }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for a slide-in animation from the bottom.
     *
     * @param name The name of the keyframes
     * @param distance The distance to slide in pixels
     * @return A CSS keyframes string
     */
    fun slideInFromBottom(name: String = "slide-in-bottom", distance: Int = 30): String = """
        @keyframes $name {
            0% { 
                transform: translateY(${distance}px);
                opacity: 0;
            }
            100% { 
                transform: translateY(0);
                opacity: 1;
            }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for a slide-in animation from the left.
     *
     * @param name The name of the keyframes
     * @param distance The distance to slide in pixels
     * @return A CSS keyframes string
     */
    fun slideInFromLeft(name: String = "slide-in-left", distance: Int = 30): String = """
        @keyframes $name {
            0% { 
                transform: translateX(-${distance}px);
                opacity: 0;
            }
            100% { 
                transform: translateX(0);
                opacity: 1;
            }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for a slide-in animation from the right.
     *
     * @param name The name of the keyframes
     * @param distance The distance to slide in pixels
     * @return A CSS keyframes string
     */
    fun slideInFromRight(name: String = "slide-in-right", distance: Int = 30): String = """
        @keyframes $name {
            0% { 
                transform: translateX(${distance}px);
                opacity: 0;
            }
            100% { 
                transform: translateX(0);
                opacity: 1;
            }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for zooming in an element.
     *
     * @param name The name of the keyframes
     * @param startScale The starting scale value (e.g., 0.5 for 50%)
     * @return A CSS keyframes string
     */
    fun zoomIn(name: String = "zoom-in", startScale: Float = 0.5f): String = """
        @keyframes $name {
            0% { 
                transform: scale($startScale);
                opacity: 0;
            }
            100% { 
                transform: scale(1);
                opacity: 1;
            }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for zooming out an element.
     *
     * @param name The name of the keyframes
     * @param endScale The ending scale value (e.g., 0.5 for 50%)
     * @return A CSS keyframes string
     */
    fun zoomOut(name: String = "zoom-out", endScale: Float = 0.5f): String = """
        @keyframes $name {
            0% { 
                transform: scale(1);
                opacity: 1;
            }
            100% { 
                transform: scale($endScale);
                opacity: 0;
            }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for a pulse effect.
     *
     * @param name The name of the keyframes
     * @param minScale The minimum scale during the pulse
     * @param maxScale The maximum scale during the pulse
     * @return A CSS keyframes string
     */
    fun pulse(name: String = "pulse", minScale: Float = 0.95f, maxScale: Float = 1.05f): String = """
        @keyframes $name {
            0% { transform: scale(1); }
            50% { transform: scale($maxScale); }
            100% { transform: scale(1); }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for a shake effect.
     *
     * @param name The name of the keyframes
     * @param intensity The intensity of the shake in pixels
     * @return A CSS keyframes string
     */
    fun shake(name: String = "shake", intensity: Int = 10): String = """
        @keyframes $name {
            0%, 100% { transform: translateX(0); }
            10%, 30%, 50%, 70%, 90% { transform: translateX(-${intensity / 2}px); }
            20%, 40%, 60%, 80% { transform: translateX(${intensity}px); }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for a floating effect.
     *
     * @param name The name of the keyframes
     * @param distance The distance to float up and down in pixels
     * @return A CSS keyframes string
     */
    fun float(name: String = "float", distance: Int = 10): String = """
        @keyframes $name {
            0% { transform: translateY(0); }
            50% { transform: translateY(-${distance}px); }
            100% { transform: translateY(0); }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for a blinking cursor effect.
     *
     * @param name The name of the keyframes
     * @return A CSS keyframes string
     */
    fun blinkingCursor(name: String = "blink"): String = """
        @keyframes $name {
            0%, 49% { border-right-color: currentColor; }
            50%, 100% { border-right-color: transparent; }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for a flip effect on the X axis.
     *
     * @param name The name of the keyframes
     * @return A CSS keyframes string
     */
    fun flipX(name: String = "flip-x"): String = """
        @keyframes $name {
            0% { transform: perspective(400px) rotateX(90deg); opacity: 0; }
            40% { transform: perspective(400px) rotateX(-20deg); }
            60% { transform: perspective(400px) rotateX(10deg); }
            80% { transform: perspective(400px) rotateX(-5deg); }
            100% { transform: perspective(400px) rotateX(0deg); opacity: 1; }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for a flip effect on the Y axis.
     *
     * @param name The name of the keyframes
     * @return A CSS keyframes string
     */
    fun flipY(name: String = "flip-y"): String = """
        @keyframes $name {
            0% { transform: perspective(400px) rotateY(90deg); opacity: 0; }
            40% { transform: perspective(400px) rotateY(-20deg); }
            60% { transform: perspective(400px) rotateY(10deg); }
            80% { transform: perspective(400px) rotateY(-5deg); }
            100% { transform: perspective(400px) rotateY(0deg); opacity: 1; }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for a bounce effect.
     *
     * @param name The name of the keyframes
     * @return A CSS keyframes string
     */
    fun bounce(name: String = "bounce"): String = """
        @keyframes $name {
            0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
            40% { transform: translateY(-30px); }
            60% { transform: translateY(-15px); }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for a typing effect.
     *
     * @param name The name of the keyframes
     * @return A CSS keyframes string
     */
    fun typing(name: String = "typing"): String = """
        @keyframes $name {
            from { width: 0; }
            to { width: 100%; }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for a color change effect.
     *
     * @param name The name of the keyframes
     * @param startColor The starting color
     * @param endColor The ending color
     * @return A CSS keyframes string
     */
    fun colorChange(
        name: String = "color-change",
        startColor: String = "#ff0000",
        endColor: String = "#0000ff"
    ): String = """
        @keyframes $name {
            0% { color: $startColor; }
            100% { color: $endColor; }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for a background color change effect.
     *
     * @param name The name of the keyframes
     * @param startColor The starting background color
     * @param endColor The ending background color
     * @return A CSS keyframes string
     */
    fun backgroundColorChange(
        name: String = "bg-color-change",
        startColor: String = "#ff0000",
        endColor: String = "#0000ff"
    ): String = """
        @keyframes $name {
            0% { background-color: $startColor; }
            100% { background-color: $endColor; }
        }
    """.trimIndent()

    /**
     * Generates a CSS keyframes definition for a custom animation with the specified keyframe steps.
     *
     * @param name The name of the keyframes
     * @param keyframes A map of percentage points to CSS property maps
     * @return A CSS keyframes string
     */
    fun custom(name: String, keyframes: Map<String, Map<String, String>>): String {
        val sb = StringBuilder("@keyframes $name {\n")

        keyframes.forEach { (percentage, properties) ->
            sb.append("    $percentage { ")
            properties.forEach { (property, value) ->
                sb.append("$property: $value; ")
            }
            sb.append("}\n")
        }

        sb.append("}")
        return sb.toString()
    }

    /**
     * Creates a keyframes definition using animation values sampled at specific intervals
     * using the provided easing function.
     *
     * @param name The name of the keyframes
     * @param easing The easing function to sample
     * @param property The CSS property to animate
     * @param fromValue The starting value
     * @param toValue The ending value
     * @param steps The number of keyframe steps to generate
     * @return A CSS keyframes string
     */
    fun fromEasing(
        name: String,
        easing: (Float) -> Float,
        property: String,
        fromValue: Float,
        toValue: Float,
        steps: Int = 10
    ): String {
        val sb = StringBuilder("@keyframes $name {\n")
        val delta = toValue - fromValue

        for (i in 0..steps) {
            val progress = i.toFloat() / steps
            val easedProgress = easing(progress)
            val value = fromValue + (delta * easedProgress)

            sb.append("    ${(progress * 100).toInt()}% { $property: $value; }\n")
        }

        sb.append("}")
        return sb.toString()
    }
} 