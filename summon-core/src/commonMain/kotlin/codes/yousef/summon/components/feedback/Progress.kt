package codes.yousef.summon.components.feedback

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.mapOfCompat
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.CompositionLocal
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Animation styles available for the Progress component.
 */
enum class ProgressAnimation {
    NONE,
    SMOOTH,
    PULSE,
    BOUNCE
}

/**
 * Visual types supported by the Progress component.
 */
enum class ProgressType {
    LINEAR,
    CIRCULAR,
    INDETERMINATE
}

/**
 * Core data class backing the Progress component. The heavy styling/animation
 * logic lives here so higher-level convenience APIs can stay lightweight.
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
    @Composable
    operator fun invoke() {
        val composer = CompositionLocal.currentComposer

        val styleModifier = Modifier()
            .then(Modifier(getTypeStyles()))
            .then(Modifier(getAnimationStyles()))

        val accessibilityModifier = getAccessibilityAttributes()
            .entries
            .fold(Modifier()) { acc, (key, value) -> acc.attribute(key, value) }

        val finalModifier = modifier.then(styleModifier).then(accessibilityModifier)

        composer?.startNode()
        if (composer?.inserting == true) {
            val renderer = LocalPlatformRenderer.current
            val progressValue = if (value != null) value.toFloat() / maxValue.toFloat() else null
            renderer.renderProgress(progressValue, type, finalModifier)
        }
        composer?.endNode()
    }

    internal fun isIndeterminate(): Boolean =
        value == null || type == ProgressType.INDETERMINATE

    internal fun getPercentage(): Int {
        if (isIndeterminate()) return 0
        val current = value ?: 0
        return ((current.toFloat() / maxValue.toFloat()) * 100).toInt().coerceIn(0, 100)
    }

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

    private fun getSizeValue(): String =
        when (size) {
            "small" -> "4px"
            "large" -> "8px"
            else -> thickness
        }

    private fun getCircularSize(): String =
        when (size) {
            "small" -> "24px"
            "large" -> "48px"
            else -> "36px"
        }

    internal fun getAnimationStyles(): Map<String, String> =
        when (animation) {
            ProgressAnimation.SMOOTH -> mapOfCompat(
                "transition" to "width 0.3s ease-in-out, transform 0.3s ease-in-out"
            )

            ProgressAnimation.PULSE -> mapOfCompat("animation" to "pulse 1.5s infinite")
            ProgressAnimation.BOUNCE -> mapOfCompat("animation" to "bounce 1s infinite")
            ProgressAnimation.NONE -> emptyMap()
        }

    internal fun getAnimationKeyframes(): String? =
        when (animation) {
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

        label?.let { attributes["aria-label"] = it }
        return attributes
    }
}

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
