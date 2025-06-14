package code.yousef.summon.components.feedback

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Progress animation types
 */
enum class ProgressAnimation {
    NONE,     // No animation
    SMOOTH,   // Smooth animation
    PULSE,    // Pulsing animation
    BOUNCE    // Bouncing animation
}

/**
 * Enum class defining the types of progress indicators available
 */
enum class ProgressType {
    /**
     * A horizontal progress bar
     */
    LINEAR,

    /**
     * A circular/radial progress indicator
     */
    CIRCULAR,

    /**
     * An indeterminate progress indicator that doesn't represent a specific value
     */
    INDETERMINATE
}

/**
 * A composable that displays a progress indicator for loading and progress tracking.
 *
 * @param modifier The modifier to apply to this composable
 * @param type The type of progress indicator
 * @param value The current progress value (0-100 or null for indeterminate)
 * @param maxValue The maximum progress value (default: 100)
 * @param color The color of the progress indicator
 * @param trackColor The color of the track behind the progress indicator
 * @param size The size of the progress indicator
 * @param thickness The thickness of the progress indicator
 * @param animation The animation style for the progress indicator
 * @param label Optional label to describe what is in progress
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
        val base = mapOf(
            "background-color" to trackColor
        )

        return when (type) {
            ProgressType.LINEAR -> base + mapOf(
                "height" to getSizeValue(),
                "width" to "100%"
            )

            ProgressType.CIRCULAR -> base + mapOf(
                "width" to getCircularSize(),
                "height" to getCircularSize(),
                "border-radius" to "50%"
            )

            ProgressType.INDETERMINATE -> when {
                type == ProgressType.CIRCULAR -> base + mapOf(
                    "width" to getCircularSize(),
                    "height" to getCircularSize(),
                    "border-radius" to "50%"
                )

                else -> base + mapOf(
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
            ProgressAnimation.SMOOTH -> mapOf(
                "transition" to "width 0.3s ease-in-out, transform 0.3s ease-in-out"
            )

            ProgressAnimation.PULSE -> mapOf(
                "animation" to "pulse 1.5s infinite"
            )

            ProgressAnimation.BOUNCE -> mapOf(
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
 * @param progress Value representing the progress between 0.0 and 1.0. If null, an indeterminate progress bar is shown.
 * @param modifier Modifier to be applied to the progress indicator
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
 * @param progress Value representing the progress between 0.0 and 1.0. If null, an indeterminate progress bar is shown.
 * @param modifier Modifier to be applied to the progress indicator
 */
@Composable
fun CircularProgress(
    progress: Float? = null,
    modifier: Modifier = Modifier()
) {
    LocalPlatformRenderer.current.renderProgress(progress, ProgressType.CIRCULAR, modifier)
}

/**
 * Creates an indeterminate progress indicator that represents an operation with unknown duration.
 *
 * @param type The type of progress indicator to display (LINEAR or CIRCULAR)
 * @param modifier Modifier to be applied to the progress indicator
 */
@Composable
fun IndeterminateProgress(
    type: ProgressType = ProgressType.LINEAR,
    modifier: Modifier = Modifier()
) {
    LocalPlatformRenderer.current.renderProgress(null, type, modifier)
} 