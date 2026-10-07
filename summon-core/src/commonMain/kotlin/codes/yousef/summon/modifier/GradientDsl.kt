package codes.yousef.summon.modifier

import codes.yousef.summon.core.style.Color

/**
 * Represents a CSS gradient or image layer that can be composed via [backgroundLayers].
 */
sealed interface GradientLayer {
    /**
     * Executes the as CSS operation.
     *
     * @return The resulting value.
     */
    fun asCss(): String

    /**
     * Represents raw.
     *
     * @property value Value to process.
     */
    data class Raw(private val value: String) : GradientLayer {
        /**
         * Executes the as CSS operation.
         *
         * @return The resulting value.
         */
        override fun asCss(): String = value
    }
}

private fun formatColorStop(color: String, position: String?): String =
    if (position.isNullOrBlank()) color else "$color $position"

/**
 * Builder for linear gradient declarations.

 * @property repeating The repeating value.
 */
class LinearGradientBuilder internal constructor(private val repeating: Boolean) {
    private var heading: String? = null
    private val colorStops = mutableListOf<String>()

    /**
     * Executes the angle operation.
     *
     * @param degrees The degrees value.
     */
    fun angle(degrees: Number) {
        heading = "${degrees}deg"
    }

    /**
     * Executes the direction operation.
     *
     * @param value Value to process.
     */
    fun direction(value: String) {
        heading = value
    }

    /**
     * Executes the color stop operation.
     *
     * @param color The color value.
     * @param position The position value.
     */
    fun colorStop(color: String, position: String? = null) {
        colorStops += formatColorStop(color, position)
    }

    /**
     * Executes the color stop operation.
     *
     * @param color The color value.
     * @param position The position value.
     */
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

 * @property repeating The repeating value.
 */
class RadialGradientBuilder internal constructor(private val repeating: Boolean) {
    private var shape: String = "circle"
    private var size: String? = null
    private var position: String? = null
    private val colorStops = mutableListOf<String>()

    /**
     * Executes the shape operation.
     *
     * @param value Value to process.
     */
    fun shape(value: String) {
        shape = value
    }

    /**
     * Executes the shape operation.
     *
     * @param value Value to process.
     */
    fun shape(value: RadialGradientShape) {
        shape = value.toString()
    }

    /**
     * Executes the size operation.
     *
     * @param value Value to process.
     */
    fun size(value: String) {
        size = value
    }

    /**
     * Executes the size operation.
     *
     * @param horizontal The horizontal value.
     * @param vertical The vertical value.
     */
    fun size(horizontal: String, vertical: String) {
        size = "$horizontal $vertical"
    }

    /**
     * Executes the size operation.
     *
     * @param width The width value.
     * @param height The height value.
     * @param unit The unit value.
     */
    fun size(width: Number, height: Number, unit: String = "px") {
        size = "${width}$unit ${height}$unit"
    }

    /**
     * Executes the position operation.
     *
     * @param value Value to process.
     */
    fun position(value: String) {
        position = value
    }

    /**
     * Executes the position operation.
     *
     * @param x The x value.
     * @param y The y value.
     */
    fun position(x: String, y: String) {
        position = "$x $y"
    }

    /**
     * Executes the position operation.
     *
     * @param x The x value.
     * @param y The y value.
     * @param unit The unit value.
     */
    fun position(x: Number, y: Number, unit: String = "%") {
        position("${x}$unit", "${y}$unit")
    }

    /**
     * Executes the color stop operation.
     *
     * @param color The color value.
     * @param position The position value.
     */
    fun colorStop(color: String, position: String? = null) {
        colorStops += formatColorStop(color, position)
    }

    /**
     * Executes the color stop operation.
     *
     * @param color The color value.
     * @param position The position value.
     */
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
 * Builder for conic gradient declarations.

 * @property repeating The repeating value.
 */
class ConicGradientBuilder internal constructor(private val repeating: Boolean) {
    private var from: String? = null
    private var position: String? = null
    private val colorStops = mutableListOf<String>()

    /**
     * Executes the from operation.
     *
     * @param value Value to process.
     */
    fun from(value: String) {
        from = value
    }

    /**
     * Executes the from operation.
     *
     * @param degrees The degrees value.
     */
    fun from(degrees: Number) {
        from("${degrees}deg")
    }

    /**
     * Executes the position operation.
     *
     * @param value Value to process.
     */
    fun position(value: String) {
        position = value
    }

    /**
     * Executes the position operation.
     *
     * @param x The x value.
     * @param y The y value.
     */
    fun position(x: String, y: String) {
        position = "$x $y"
    }

    /**
     * Executes the color stop operation.
     *
     * @param color The color value.
     * @param position The position value.
     */
    fun colorStop(color: String, position: String? = null) {
        colorStops += formatColorStop(color, position)
    }

    /**
     * Executes the color stop operation.
     *
     * @param color The color value.
     * @param position The position value.
     */
    fun colorStop(color: Color, position: String? = null) =
        colorStop(color.toString(), position)

    internal fun build(): GradientLayer {
        require(colorStops.isNotEmpty()) { "conicGradient requires at least one color stop" }
        val parts = mutableListOf<String>()
        from?.let { parts += "from $it" }
        position?.let { parts += "at $it" }
        parts.addAll(colorStops)
        val fn = if (repeating) "repeating-conic-gradient" else "conic-gradient"
        return GradientLayer.Raw("$fn(${parts.joinToString(", ")})")
    }
}

/**
 * DSL scope that collects individual background layers.
 */
class GradientLayerScope internal constructor() {
    internal val layers = mutableListOf<GradientLayer>()

    /**
     * Executes the linear gradient operation.
     *
     * @param repeating The repeating value.
     * @param builder The builder value.
     */
    fun linearGradient(repeating: Boolean = false, builder: LinearGradientBuilder.() -> Unit) {
        val layer = LinearGradientBuilder(repeating).apply(builder).build()
        layers += layer
    }

    /**
     * Executes the radial gradient operation.
     *
     * @param repeating The repeating value.
     * @param builder The builder value.
     */
    fun radialGradient(repeating: Boolean = false, builder: RadialGradientBuilder.() -> Unit) {
        val layer = RadialGradientBuilder(repeating).apply(builder).build()
        layers += layer
    }

    /**
     * Executes the conic gradient operation.
     *
     * @param repeating The repeating value.
     * @param builder The builder value.
     */
    fun conicGradient(repeating: Boolean = false, builder: ConicGradientBuilder.() -> Unit) {
        val layer = ConicGradientBuilder(repeating).apply(builder).build()
        layers += layer
    }

    /**
     * Executes the image operation.
     *
     * @param value Value to process.
     */
    fun image(value: String) {
        layers += GradientLayer.Raw(value)
    }

    /**
     * Executes the URL operation.
     *
     * @param value Value to process.
     */
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
