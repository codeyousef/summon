package codes.yousef.summon.modifier

import codes.yousef.summon.core.style.Color
import codes.yousef.summon.extensions.px

/**
 * Box shadow utilities extracted from the legacy `StylingModifiers` file. These helpers keep the
 * existing fluent API while the main styling file is trimmed down to smaller responsibilities.
 */
fun Modifier.boxShadow(value: String): Modifier =
    style("box-shadow", value)

fun Modifier.boxShadow(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: String,
    color: Color
): Modifier {
    val shadow = "$horizontalOffset $verticalOffset $blurRadius $color"
    return boxShadow(shadow)
}

fun Modifier.boxShadow(
    horizontalOffset: String,
    verticalOffset: String,
    blurRadius: String,
    color: Color
): Modifier {
    val shadow = "$horizontalOffset $verticalOffset $blurRadius $color"
    return boxShadow(shadow)
}

fun Modifier.boxShadow(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: String,
    spreadRadius: String,
    color: Color
): Modifier {
    val shadow = "$horizontalOffset $verticalOffset $blurRadius $spreadRadius $color"
    return boxShadow(shadow)
}

fun Modifier.boxShadow(
    horizontalOffset: String,
    verticalOffset: String,
    blurRadius: String,
    spreadRadius: String,
    color: Color
): Modifier {
    val shadow = "$horizontalOffset $verticalOffset $blurRadius $spreadRadius $color"
    return boxShadow(shadow)
}

fun Modifier.boxShadow(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: String,
    color: Color,
    inset: Boolean = false
): Modifier {
    val insetStr = if (inset) "inset " else ""
    val shadow = "$insetStr$horizontalOffset $verticalOffset $blurRadius $color"
    return boxShadow(shadow)
}

fun Modifier.boxShadow(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: String,
    spreadRadius: String,
    color: Color,
    inset: Boolean = false
): Modifier {
    val insetStr = if (inset) "inset " else ""
    val shadow = "$insetStr$horizontalOffset $verticalOffset $blurRadius $spreadRadius $color"
    return boxShadow(shadow)
}

fun Modifier.boxShadow(
    horizontalOffset: String,
    verticalOffset: String,
    blurRadius: String,
    color: Color,
    inset: Boolean = false
): Modifier {
    val insetStr = if (inset) "inset " else ""
    val shadow = "$insetStr$horizontalOffset $verticalOffset $blurRadius $color"
    return boxShadow(shadow)
}

fun Modifier.boxShadow(
    horizontalOffset: String,
    verticalOffset: String,
    blurRadius: String,
    spreadRadius: String,
    color: Color,
    inset: Boolean = false
): Modifier {
    val insetStr = if (inset) "inset " else ""
    val shadow = "$insetStr$horizontalOffset $verticalOffset $blurRadius $spreadRadius $color"
    return boxShadow(shadow)
}

fun Modifier.combineBackdropFilters(vararg filters: String): Modifier =
    backdropFilter(filters.joinToString(" "))

fun Modifier.glassMorphism(
    blurAmount: String = "10px",
    brightness: Double = 1.05
): Modifier =
    combineBackdropFilters("blur($blurAmount)", "brightness($brightness)")

fun Modifier.glassMorphism(
    blurPx: Number = 10,
    brightness: Double = 1.05
): Modifier =
    glassMorphism("${blurPx}px", brightness)

// Shadow configuration ----------------------------------------------------

data class ShadowConfig(
    val horizontalOffset: String,
    val verticalOffset: String,
    val blurRadius: String,
    val spreadRadius: String? = null,
    val color: Color,
    val inset: Boolean = false
) {
    fun toCssString(): String {
        val insetStr = if (inset) "inset " else ""
        return if (spreadRadius != null) {
            "$insetStr$horizontalOffset $verticalOffset $blurRadius $spreadRadius ${color.toCssString()}"
        } else {
            "$insetStr$horizontalOffset $verticalOffset $blurRadius ${color.toCssString()}"
        }
    }

    companion object {
        fun create(
            horizontalOffset: Number,
            verticalOffset: Number,
            blurRadius: Number,
            spreadRadius: Number? = null,
            color: Color,
            inset: Boolean = false
        ) = ShadowConfig(
            horizontalOffset.px,
            verticalOffset.px,
            blurRadius.px,
            spreadRadius?.px,
            color,
            inset
        )

        fun glow(
            blurRadius: Number,
            color: Color,
            intensity: Number = 0
        ) = ShadowConfig(
            0.px,
            0.px,
            blurRadius.px,
            if (intensity.toDouble() != 0.0) intensity.px else null,
            color,
            false
        )

        fun innerGlow(
            blurRadius: Number,
            color: Color
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
    color = Color.fromHex(color),
    inset = inset
)

fun shadowConfig(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: Number,
    spreadRadius: Number? = null,
    color: Color,
    inset: Boolean = false
): ShadowConfig = ShadowConfig(
    horizontalOffset.px,
    verticalOffset.px,
    blurRadius.px,
    spreadRadius?.px,
    color,
    inset
)

fun Modifier.multipleShadows(shadows: List<ShadowConfig>): Modifier {
    val shadowStrings = shadows.map { it.toCssString() }
    return boxShadow(shadowStrings.joinToString(", "))
}

fun Modifier.multipleShadows(vararg shadows: ShadowConfig): Modifier =
    multipleShadows(shadows.toList())

fun Modifier.addShadow(shadow: ShadowConfig): Modifier {
    val currentShadow = this.styles["box-shadow"]
    return if (currentShadow != null) {
        boxShadow("$currentShadow, ${shadow.toCssString()}")
    } else {
        boxShadow(shadow.toCssString())
    }
}

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

fun Modifier.addShadow(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: Number,
    spreadRadius: Number? = null,
    color: Color,
    inset: Boolean = false
): Modifier = addShadow(
    shadowConfig(horizontalOffset, verticalOffset, blurRadius, spreadRadius, color, inset)
)

fun Modifier.glow(
    color: String,
    intensity: Int = 2,
    size: Number = 20
): Modifier {
    val shadows = mutableListOf<ShadowConfig>()
    for (i in 1..intensity) {
        val blur = size.toDouble() * i
        shadows.add(shadowConfig(0, 0, blur.toInt(), color = color))
    }
    return multipleShadows(shadows)
}

fun Modifier.glow(
    color: Color,
    intensity: Int = 2,
    size: Number = 20
): Modifier = glow(color.toString(), intensity, size)

fun Modifier.innerGlow(
    color: String,
    intensity: Int = 2,
    size: Number = 10
): Modifier {
    val shadows = mutableListOf<ShadowConfig>()
    for (i in 1..intensity) {
        val blur = size.toDouble() * i
        shadows.add(shadowConfig(0, 0, blur.toInt(), color = color, inset = true))
    }
    return multipleShadows(shadows)
}

fun Modifier.innerGlow(
    color: Color,
    intensity: Int = 2,
    size: Number = 10
): Modifier = innerGlow(color.toString(), intensity, size)

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

fun Modifier.auroraGlow(baseSize: Number = 20): Modifier =
    auroraGlow(
        colors = listOf(
            "rgba(240, 249, 255, 0.8)",
            "rgba(56, 178, 172, 0.6)",
            "rgba(44, 82, 130, 0.7)",
            "rgba(155, 44, 44, 0.5)"
        ),
        baseSize = baseSize
    )
