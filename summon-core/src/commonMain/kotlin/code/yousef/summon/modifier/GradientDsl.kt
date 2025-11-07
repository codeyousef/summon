package code.yousef.summon.modifier

import code.yousef.summon.core.style.Color

/**
 * Represents a CSS gradient or image layer that can be composed via [backgroundLayers].
 */
sealed interface GradientLayer {
    fun asCss(): String

    data class Raw(private val value: String) : GradientLayer {
        override fun asCss(): String = value
    }
}

private fun formatColorStop(color: String, position: String?): String =
    if (position.isNullOrBlank()) color else "$color $position"

/**
 * Builder for linear gradient declarations.
 */
class LinearGradientBuilder internal constructor(private val repeating: Boolean) {
    private var heading: String? = null
    private val colorStops = mutableListOf<String>()

    fun angle(degrees: Number) {
        heading = "${degrees}deg"
    }

    fun direction(value: String) {
        heading = value
    }

    fun colorStop(color: String, position: String? = null) {
        colorStops += formatColorStop(color, position)
    }

    fun colorStop(color: Color, position: String? = null) =
        colorStop(color.toString(), position)

    internal fun build(): GradientLayer {
        require(colorStops.isNotEmpty()) { "linearGradient requires at least one color stop" }
        val parts = mutableListOf<String>()
        heading?.let { parts += it }
        parts.addAll(colorStops)
        val args = parts.joinToString(", ")
        val fn = if (repeating) "repeating-linear-gradient" else "linear-gradient"
        return GradientLayer.Raw("$fn($args)")
    }
}

/**
 * Builder for radial gradient declarations.
 */
class RadialGradientBuilder internal constructor(private val repeating: Boolean) {
    private var shape: String = "circle"
    private var size: String? = null
    private var position: String? = null
    private val colorStops = mutableListOf<String>()

    fun shape(value: String) {
        shape = value
    }

    fun shape(value: RadialGradientShape) {
        shape = value.toString()
    }

    fun size(value: String) {
        size = value
    }

    fun size(horizontal: String, vertical: String) {
        size = "$horizontal $vertical"
    }

    fun size(width: Number, height: Number, unit: String = "px") {
        size = "${width}$unit ${height}$unit"
    }

    fun position(value: String) {
        position = value
    }

    fun position(x: String, y: String) {
        position = "$x $y"
    }

    fun position(x: Number, y: Number, unit: String = "%") {
        position("${x}$unit", "${y}$unit")
    }

    fun colorStop(color: String, position: String? = null) {
        colorStops += formatColorStop(color, position)
    }

    fun colorStop(color: Color, position: String? = null) =
        colorStop(color.toString(), position)

    internal fun build(): GradientLayer {
        require(colorStops.isNotEmpty()) { "radialGradient requires at least one color stop" }
        val descriptorParts = mutableListOf<String>()
        descriptorParts += shape
        size?.let { descriptorParts += it }
        position?.let { descriptorParts += "at $it" }
        val descriptor = descriptorParts.joinToString(" ")

        val argsParts = mutableListOf<String>()
        if (descriptor.isNotBlank()) {
            argsParts += descriptor
        }
        argsParts.addAll(colorStops)
        val args = argsParts.joinToString(", ")

        val fn = if (repeating) "repeating-radial-gradient" else "radial-gradient"
        return GradientLayer.Raw("$fn($args)")
    }
}

/**
 * DSL scope that collects individual background layers.
 */
class GradientLayerScope internal constructor() {
    internal val layers = mutableListOf<GradientLayer>()

    fun linearGradient(repeating: Boolean = false, builder: LinearGradientBuilder.() -> Unit) {
        val layer = LinearGradientBuilder(repeating).apply(builder).build()
        layers += layer
    }

    fun radialGradient(repeating: Boolean = false, builder: RadialGradientBuilder.() -> Unit) {
        val layer = RadialGradientBuilder(repeating).apply(builder).build()
        layers += layer
    }

    fun image(value: String) {
        layers += GradientLayer.Raw(value)
    }

    fun url(value: String) {
        image("url($value)")
    }
}

/**
 * Applies multiple background layers (gradients, images) in a type-safe way.
 */
fun Modifier.backgroundLayers(vararg layers: GradientLayer): Modifier {
    require(layers.isNotEmpty()) { "backgroundLayers requires at least one layer" }
    return backgroundImage(layers.joinToString(", ") { it.asCss() })
}

/**
 * DSL overload for [backgroundLayers] that collects gradient and image layers.
 */
fun Modifier.backgroundLayers(builder: GradientLayerScope.() -> Unit): Modifier {
    val scope = GradientLayerScope().apply(builder)
    return backgroundLayers(*scope.layers.toTypedArray())
}
