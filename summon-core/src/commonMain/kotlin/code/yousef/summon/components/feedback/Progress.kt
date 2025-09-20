package code.yousef.summon.components.feedback

import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.mapOfCompat
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Progress animation types that define how progress indicators animate during state changes.
 *
 * Animations enhance user experience by providing visual feedback about progress state
 * changes and system responsiveness. Choose animations based on your use case:
 * - Use SMOOTH for determinate progress that changes frequently
 * - Use PULSE for indeterminate loading states
 * - Use BOUNCE for attention-grabbing indicators
 * - Use NONE when performance is critical or animations are disabled
 *
 * @see Progress for animation implementation details
 * @since 1.0.0
 */
enum class ProgressAnimation {
    /** No animation - instant state changes. Best for performance-critical scenarios. */
    NONE,

    /** Smooth transition animation. Ideal for determinate progress that updates frequently. */
    SMOOTH,

    /** Pulsing opacity animation. Perfect for indeterminate loading states. */
    PULSE,

    /** Bouncing animation. Use sparingly for emphasis or playful interfaces. */
    BOUNCE
}

/**
 * Progress indicator types that define the visual style and behavior of progress components.
 *
 * Different progress types are suitable for different contexts and user interface patterns:
 * - **LINEAR**: Best for file uploads, form submissions, and step-by-step processes
 * - **CIRCULAR**: Ideal for compact spaces, buttons, and when space is limited
 * - **INDETERMINATE**: Perfect for loading states with unknown duration
 *
 * ## Design Guidelines
 * - Use linear progress for operations with known duration or steps
 * - Use circular progress in compact UI elements or mobile interfaces
 * - Use indeterminate progress when you cannot estimate completion time
 *
 * @see Progress for implementation examples
 * @see LinearProgress for linear progress convenience function
 * @see CircularProgress for circular progress convenience function
 * @since 1.0.0
 */
enum class ProgressType {
    /**
     * Horizontal progress bar ideal for showing completion percentage.
     *
     * Linear progress bars work well for:
     * - File upload/download progress
     * - Form completion status
     * - Multi-step process indicators
     * - Loading progress with known duration
     */
    LINEAR,

    /**
     * Circular/radial progress indicator perfect for compact spaces.
     *
     * Circular progress indicators are great for:
     * - Button loading states
     * - Mobile interface elements
     * - Compact dashboard widgets
     * - Inline loading indicators
     */
    CIRCULAR,

    /**
     * Indeterminate progress indicator for unknown duration operations.
     *
     * Indeterminate progress works best for:
     * - Initial data loading
     * - Background processing
     * - Network requests with unknown timing
     * - System operations
     */
    INDETERMINATE
}

/**
 * A comprehensive progress indicator component for displaying loading states and operation progress.
 *
 * The Progress component provides flexible progress indication with support for both determinate
 * and indeterminate states, multiple visual styles, and extensive customization options. It's
 * designed to work seamlessly across different platforms with proper accessibility support.
 *
 * ## Features
 * - **Multiple display types**: Linear, circular, and indeterminate variants
 * - **Determinate and indeterminate modes**: Show specific progress or general loading state
 * - **Rich customization**: Colors, sizes, animations, and styling options
 * - **Accessibility support**: Proper ARIA attributes and screen reader compatibility
 * - **Animation options**: Smooth transitions, pulse effects, and bounce animations
 * - **Platform optimized**: Native rendering through platform-specific implementations
 *
 * ## Basic Usage
 * ```kotlin
 * // Determinate linear progress
 * Progress(
 *     type = ProgressType.LINEAR,
 *     value = 65,
 *     maxValue = 100
 * )()
 *
 * // Indeterminate circular progress
 * Progress(
 *     type = ProgressType.CIRCULAR,
 *     value = null,  // null indicates indeterminate
 *     size = "large"
 * )()
 * ```
 *
 * ## Advanced Usage
 * ```kotlin
 * // Custom styled progress with animation
 * Progress(
 *     type = ProgressType.LINEAR,
 *     value = uploadProgress,
 *     maxValue = 100,
 *     color = "#4caf50",
 *     trackColor = "#e8f5e8",
 *     thickness = "8px",
 *     animation = ProgressAnimation.SMOOTH,
 *     label = "Uploading file...",
 *     modifier = Modifier()
 *         .width(Width.FULL)
 *         .borderRadius(BorderRadius.LG)
 * )()
 * ```
 *
 * ## File Upload Example
 * ```kotlin
 * @Composable
 * fun FileUploadProgress() {
 *     var uploadProgress by remember { mutableStateOf(0) }
 *     var isUploading by remember { mutableStateOf(false) }
 *
 *     Column(modifier = Modifier().style("gap", "16px")) {
 *         Text("Upload Progress")
 *
 *         Progress(
 *             type = ProgressType.LINEAR,
 *             value = if (isUploading) uploadProgress else null,
 *             color = "#2196f3",
 *             animation = ProgressAnimation.SMOOTH,
 *             label = when {
 *                 !isUploading -> "Ready to upload"
 *                 uploadProgress < 100 -> "Uploading... $uploadProgress%"
 *                 else -> "Upload complete!"
 *             },
 *             modifier = Modifier().width(Width.FULL)
 *         )()
 *
 *         Text("$uploadProgress% complete")
 *     }
 * }
 * ```
 *
 * ## Accessibility Features
 * - **ARIA attributes**: Proper `role="progressbar"` and value attributes
 * - **Screen reader support**: Announces progress changes and completion
 * - **Keyboard navigation**: Focusable when interactive
 * - **High contrast**: Respects system high contrast settings
 * - **Descriptive labels**: Optional labels for context
 *
 * ## Performance Considerations
 * - Smooth animations are optimized for 60fps
 * - Platform-specific rendering reduces overhead
 * - Conditional animation based on user preferences
 * - Efficient recomposition for value changes
 *
 * @param modifier The modifier to apply to this composable for styling and layout.
 * @param type The type of progress indicator (LINEAR, CIRCULAR, or INDETERMINATE).
 * @param value The current progress value (0-maxValue). Use null for indeterminate progress.
 * @param maxValue The maximum progress value (default: 100). Defines the scale for progress calculation.
 * @param color The color of the progress indicator (default: Material Blue).
 * @param trackColor The color of the track behind the progress indicator (default: Light Gray).
 * @param size The size of the progress indicator ("small", "medium", "large").
 * @param thickness The thickness of the progress indicator for custom sizing.
 * @param animation The animation style for progress changes and indeterminate states.
 * @param label Optional descriptive label for accessibility and user context.
 *
 * @see ProgressType for available progress indicator types
 * @see ProgressAnimation for animation options
 * @see LinearProgress for simplified linear progress
 * @see CircularProgress for simplified circular progress
 * @see IndeterminateProgress for indeterminate loading states
 * @sample code.yousef.summon.samples.feedback.ProgressSamples.basicProgress
 * @sample code.yousef.summon.samples.feedback.ProgressSamples.fileUploadProgress
 * @since 1.0.0
 */
data class Progress(
    val modifier: Modifier = Modifier(),
    val type: ProgressType = ProgressType.LINEAR,
    val value: Int? = null,
    val maxValue: Int = 100,
    val color: String = "#2196f3",
    val trackColor: String = "#e0e0e0",
    val size: String = "medium",
    val thickness: String = "4px",
    val animation: ProgressAnimation = ProgressAnimation.SMOOTH,
    val label: String? = null
) {
    /**
     * Renders this Progress composable
     */
    @Composable
    operator fun invoke() {
        val composer = CompositionLocal.currentComposer

        // Apply type-specific and animation styles
        val styleModifier = Modifier()
            .then(getTypeStyles().let { Modifier(it) })
            .then(getAnimationStyles().let { Modifier(it) })

        // Apply accessibility attributes
        val accessibilityModifier = getAccessibilityAttributes().entries.fold(Modifier()) { acc, (key, value) ->
            acc.attribute(key, value)
        }

        // Combine with user-provided modifier
        val finalModifier = modifier.then(styleModifier).then(accessibilityModifier)

        composer?.startNode() // Start Progress node
        if (composer?.inserting == true) {
            val renderer = LocalPlatformRenderer.current
            val progressValue = if (value != null) value.toFloat() / maxValue.toFloat() else null
            renderer.renderProgress(progressValue, type, finalModifier)
        }
        composer?.endNode() // End Progress node
    }

    /**
     * Checks if this progress indicator is indeterminate.
     */
    internal fun isIndeterminate(): Boolean {
        return value == null || type == ProgressType.INDETERMINATE
    }

    /**
     * Gets the current progress percentage.
     */
    internal fun getPercentage(): Int {
        if (isIndeterminate()) return 0

        val current = value ?: 0
        return ((current.toFloat() / maxValue.toFloat()) * 100).toInt().coerceIn(0, 100)
    }

    /**
     * Gets type-specific styles for the progress indicator.
     */
    internal fun getTypeStyles(): Map<String, String> {
        val base = mapOfCompat(
            "background-color" to trackColor
        )

        return when (type) {
            ProgressType.LINEAR -> base + mapOfCompat(
                "height" to getSizeValue(),
                "width" to "100%"
            )

            ProgressType.CIRCULAR -> base + mapOfCompat(
                "width" to getCircularSize(),
                "height" to getCircularSize(),
                "border-radius" to "50%"
            )

            ProgressType.INDETERMINATE -> when {
                type == ProgressType.CIRCULAR -> base + mapOfCompat(
                    "width" to getCircularSize(),
                    "height" to getCircularSize(),
                    "border-radius" to "50%"
                )

                else -> base + mapOfCompat(
                    "height" to getSizeValue(),
                    "width" to "100%"
                )
            }
        }
    }

    /**
     * Gets size value based on the size property.
     */
    private fun getSizeValue(): String {
        return when (size) {
            "small" -> "4px"
            "large" -> "8px"
            else -> thickness // Use the thickness value for medium size
        }
    }

    /**
     * Gets circular progress size based on the size property.
     */
    private fun getCircularSize(): String {
        return when (size) {
            "small" -> "24px"
            "large" -> "48px"
            else -> "36px" // medium
        }
    }

    /**
     * Gets animation styles based on the animation property.
     */
    internal fun getAnimationStyles(): Map<String, String> {
        if (animation == ProgressAnimation.NONE) {
            return emptyMap()
        }

        return when (animation) {
            ProgressAnimation.SMOOTH -> mapOfCompat(
                "transition" to "width 0.3s ease-in-out, transform 0.3s ease-in-out"
            )

            ProgressAnimation.PULSE -> mapOfCompat(
                "animation" to "pulse 1.5s infinite"
            )

            ProgressAnimation.BOUNCE -> mapOfCompat(
                "animation" to "bounce 1s infinite"
            )

            else -> emptyMap()
        }
    }

    /**
     * Gets animation keyframes for the specified animation type.
     */
    internal fun getAnimationKeyframes(): String? {
        return when (animation) {
            ProgressAnimation.PULSE -> """
                @keyframes pulse {
                    0% { opacity: 1; }
                    50% { opacity: 0.6; }
                    100% { opacity: 1; }
                }
            """.trimIndent()

            ProgressAnimation.BOUNCE -> """
                @keyframes bounce {
                    0%, 100% { transform: translateY(0); }
                    50% { transform: translateY(-5px); }
                }
            """.trimIndent()

            else -> null
        }
    }

    /**
     * Gets accessibility attributes for the progress indicator.
     */
    internal fun getAccessibilityAttributes(): Map<String, String> {
        val attributes = mutableMapOf<String, String>()

        attributes["role"] = "progressbar"

        if (!isIndeterminate()) {
            attributes["aria-valuenow"] = (value ?: 0).toString()
            attributes["aria-valuemin"] = "0"
            attributes["aria-valuemax"] = maxValue.toString()
            attributes["aria-valuetext"] = "${getPercentage()}%"
        } else {
            attributes["aria-valuetext"] = "Loading"
        }

        // If there's a label, add it as aria-label
        label?.let { attributes["aria-label"] = it }

        return attributes
    }
}

/**
 * Creates a linear progress bar.
 * @param value The current progress value (0-100 or null for indeterminate)
 * @param color The color of the progress indicator
 * @param modifier The modifier to apply to this composable
 */
fun linearProgress(
    value: Int? = null,
    color: String = "#2196f3",
    modifier: Modifier = Modifier()
): Progress = Progress(
    modifier = modifier,
    type = ProgressType.LINEAR,
    value = value,
    color = color
)

/**
 * Creates a circular spinner or progress indicator.
 * @param value The current progress value (0-100 or null for indeterminate)
 * @param size The size of the spinner (small, medium, large)
 * @param color The color of the spinner
 * @param modifier The modifier to apply to this composable
 */
fun circularProgress(
    value: Int? = null,
    size: String = "medium",
    color: String = "#2196f3",
    modifier: Modifier = Modifier()
): Progress = Progress(
    modifier = modifier,
    type = ProgressType.CIRCULAR,
    value = value,
    size = size,
    color = color
)

/**
 * Creates an indeterminate loading spinner.
 * @param type The type of indeterminate indicator (LINEAR or CIRCULAR)
 * @param color The color of the spinner
 * @param modifier The modifier to apply to this composable
 */
fun loading(
    type: ProgressType = ProgressType.CIRCULAR,
    color: String = "#2196f3",
    modifier: Modifier = Modifier()
): Progress = Progress(
    modifier = modifier,
    type = ProgressType.INDETERMINATE,
    value = null,
    color = color,
    animation = ProgressAnimation.PULSE
)

/**
 * Creates a linear progress indicator that represents the progress of an operation.
 *
 * LinearProgress provides a simplified interface for creating horizontal progress bars.
 * It's ideal for showing completion progress of tasks with known duration or steps.
 *
 * ## Features
 * - **Simplified API**: Easy-to-use interface for common linear progress needs
 * - **Standard styling**: Consistent appearance across your application
 * - **Accessibility**: Built-in ARIA attributes and screen reader support
 * - **Performance optimized**: Efficient rendering through platform renderers
 *
 * ## Basic Usage
 * ```kotlin
 * // Determinate progress (0.0 to 1.0)
 * LinearProgress(progress = 0.65f)
 *
 * // Indeterminate progress
 * LinearProgress(progress = null)
 * ```
 *
 * ## Integration with State
 * ```kotlin
 * @Composable
 * fun DownloadProgress() {
 *     var downloadProgress by remember { mutableStateOf(0f) }
 *
 *     LaunchedEffect(Unit) {
 *         // Simulate download progress
 *         for (i in 0..100) {
 *             downloadProgress = i / 100f
 *             delay(50)
 *         }
 *     }
 *
 *     LinearProgress(
 *         progress = downloadProgress,
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .height(Height.of("8px"))
 *     )
 * }
 * ```
 *
 * ## Common Use Cases
 * - File upload/download progress
 * - Form completion indicators
 * - Loading progress with known steps
 * - Page loading indicators
 * - Data processing progress
 *
 * @param progress Value representing the progress between 0.0 and 1.0. If null, shows indeterminate progress.
 * @param modifier Modifier to be applied to the progress indicator for styling and layout.
 *
 * @see Progress for full-featured progress component
 * @see CircularProgress for circular progress indicators
 * @sample code.yousef.summon.samples.feedback.ProgressSamples.linearProgressExample
 * @since 1.0.0
 */
@Composable
fun LinearProgress(
    progress: Float? = null,
    modifier: Modifier = Modifier()
) {
    LocalPlatformRenderer.current.renderProgress(progress, ProgressType.LINEAR, modifier)
}

/**
 * Creates a circular progress indicator that represents the progress of an operation.
 *
 * CircularProgress provides a compact, circular progress indicator perfect for small spaces
 * and mobile interfaces. It's ideal for showing progress in buttons, cards, or any space-
 * constrained UI element.
 *
 * ## Features
 * - **Compact design**: Perfect for small UI elements and mobile interfaces
 * - **Standard styling**: Consistent circular appearance across platforms
 * - **Accessibility**: Built-in ARIA attributes and screen reader support
 * - **Performance optimized**: Smooth circular animations through platform renderers
 *
 * ## Basic Usage
 * ```kotlin
 * // Determinate circular progress
 * CircularProgress(progress = 0.75f)
 *
 * // Indeterminate spinning indicator
 * CircularProgress(progress = null)
 * ```
 *
 * ## Button Integration
 * ```kotlin
 * @Composable
 * fun SubmitButton() {
 *     var isSubmitting by remember { mutableStateOf(false) }
 *
 *     Button(
 *         onClick = {
 *             isSubmitting = true
 *             // Handle submission
 *         },
 *         modifier = Modifier().width(Width.FIT_CONTENT)
 *     ) {
 *         Row(
 *             modifier = Modifier()
 *                 .style("align-items", "center")
 *                 .style("gap", "8px")
 *         ) {
 *             if (isSubmitting) {
 *                 CircularProgress(
 *                     progress = null,
 *                     modifier = Modifier()
 *                         .width(Width.of("16px"))
 *                         .height(Height.of("16px"))
 *                 )
 *             }
 *             Text(if (isSubmitting) "Submitting..." else "Submit")
 *         }
 *     }
 * }
 * ```
 *
 * ## Dashboard Widget
 * ```kotlin
 * @Composable
 * fun StatsCard(title: String, value: Float) {
 *     Card(
 *         modifier = Modifier()
 *             .padding(Spacing.MD)
 *             .width(Width.of("200px"))
 *     ) {
 *         Column(
 *             modifier = Modifier()
 *                 .style("align-items", "center")
 *                 .padding(Spacing.LG)
 *         ) {
 *             CircularProgress(
 *                 progress = value,
 *                 modifier = Modifier()
 *                     .width(Width.of("64px"))
 *                     .height(Height.of("64px"))
 *             )
 *             Text(title)
 *             Text("${(value * 100).toInt()}%")
 *         }
 *     }
 * }
 * ```
 *
 * ## Common Use Cases
 * - Button loading states
 * - Compact progress indicators
 * - Dashboard widgets
 * - Mobile interface elements
 * - Inline loading spinners
 *
 * @param progress Value representing the progress between 0.0 and 1.0. If null, shows indeterminate spinner.
 * @param modifier Modifier to be applied to the progress indicator for sizing and styling.
 *
 * @see Progress for full-featured progress component
 * @see LinearProgress for linear progress bars
 * @sample code.yousef.summon.samples.feedback.ProgressSamples.circularProgressExample
 * @since 1.0.0
 */
@Composable
fun CircularProgress(
    progress: Float? = null,
    modifier: Modifier = Modifier()
) {
    LocalPlatformRenderer.current.renderProgress(progress, ProgressType.CIRCULAR, modifier)
}

/**
 * Creates an indeterminate progress indicator for operations with unknown duration.
 *
 * IndeterminateProgress is perfect for loading states where you cannot estimate completion
 * time or progress percentage. It provides continuous animation to indicate that work is
 * happening in the background.
 *
 * ## Features
 * - **Unknown duration support**: Perfect for unpredictable operations
 * - **Continuous animation**: Shows ongoing activity without specific progress
 * - **Multiple variants**: Linear and circular options for different contexts
 * - **Performance optimized**: Efficient animations that don't block the UI thread
 *
 * ## Basic Usage
 * ```kotlin
 * // Linear indeterminate progress
 * IndeterminateProgress(type = ProgressType.LINEAR)
 *
 * // Circular indeterminate progress (spinner)
 * IndeterminateProgress(type = ProgressType.CIRCULAR)
 * ```
 *
 * ## Loading State Pattern
 * ```kotlin
 * @Composable
 * fun DataLoader() {
 *     var isLoading by remember { mutableStateOf(true) }
 *     var data by remember { mutableStateOf<List<Item>?>(null) }
 *
 *     LaunchedEffect(Unit) {
 *         // Fetch data from API with unknown timing
 *         data = apiService.fetchData()
 *         isLoading = false
 *     }
 *
 *     if (isLoading) {
 *         Column(
 *             modifier = Modifier()
 *                 .style("align-items", "center")
 *                 .padding(Spacing.XL)
 *         ) {
 *             IndeterminateProgress(type = ProgressType.CIRCULAR)
 *             Text("Loading data...")
 *         }
 *     } else {
 *         data?.let { DataList(it) }
 *     }
 * }
 * ```
 *
 * ## App Initialization
 * ```kotlin
 * @Composable
 * fun AppInitializer() {
 *     var isInitializing by remember { mutableStateOf(true) }
 *
 *     LaunchedEffect(Unit) {
 *         // Initialize app services, load configuration, etc.
 *         initializeServices()
 *         isInitializing = false
 *     }
 *
 *     if (isInitializing) {
 *         Box(
 *             modifier = Modifier()
 *                 .width(Width.FULL)
 *                 .height(Height.FULL)
 *                 .style("display", "flex")
 *                 .style("align-items", "center")
 *                 .style("justify-content", "center")
 *         ) {
 *             Column(
 *                 modifier = Modifier().style("align-items", "center")
 *             ) {
 *                 IndeterminateProgress(
 *                     type = ProgressType.CIRCULAR,
 *                     modifier = Modifier().margin(Margin.bottom(Spacing.LG))
 *                 )
 *                 Text("Initializing application...")
 *             }
 *         }
 *     } else {
 *         MainApp()
 *     }
 * }
 * ```
 *
 * ## Common Use Cases
 * - Initial data loading
 * - Background processing
 * - Network requests with unknown timing
 * - File processing operations
 * - Authentication flows
 * - System initialization
 *
 * ## Design Guidelines
 * - Use linear for page-level loading states
 * - Use circular for component-level loading
 * - Provide context with descriptive text
 * - Consider timeout handling for long operations
 *
 * @param type The type of progress indicator to display (LINEAR or CIRCULAR).
 * @param modifier Modifier to be applied to the progress indicator for styling and layout.
 *
 * @see Progress for determinate progress indicators
 * @see LinearProgress for linear progress with values
 * @see CircularProgress for circular progress with values
 * @sample code.yousef.summon.samples.feedback.ProgressSamples.indeterminateExample
 * @since 1.0.0
 */
@Composable
fun IndeterminateProgress(
    type: ProgressType = ProgressType.LINEAR,
    modifier: Modifier = Modifier()
) {
    LocalPlatformRenderer.current.renderProgress(null, type, modifier)
} 