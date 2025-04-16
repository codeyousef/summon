package code.yousef.summon.core.style

/**
 * Represents an RGBA color
 */
class Color(val value: UInt) {
    /**
     * The red component of this color (0-255)
     */
    val red: Int
        get() = ((value shr 24) and 0xFFu).toInt()

    /**
     * The green component of this color (0-255)
     */
    val green: Int
        get() = ((value shr 16) and 0xFFu).toInt()

    /**
     * The blue component of this color (0-255)
     */
    val blue: Int
        get() = ((value shr 8) and 0xFFu).toInt()

    /**
     * The alpha component of this color (0-255)
     */
    val alpha: Int
        get() = (value and 0xFFu).toInt()

    /**
     * The alpha component as a float (0.0-1.0)
     */
    val alphaFloat: Float
        get() = alpha / 255f

    /**
     * Creates a new color with the specified alpha component
     *
     * @param alpha The alpha value (0.0-1.0)
     * @return A new Color with the updated alpha
     */
    fun withAlpha(alpha: Float): Color {
        val alphaByte = (alpha.coerceIn(0f, 1f) * 255).toInt()
        return Color((value and 0xFFFFFF00u) or alphaByte.toUInt())
    }

    /**
     * Returns this color as a CSS rgba() string
     */
    fun toRgbaString(): String {
        return "rgba($red, $green, $blue, ${alpha / 255f})"
    }

    /**
     * Returns this color as a CSS hex string
     */
    fun toHexString(): String {
        return "#${value.toString(16).padStart(8, '0')}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Color) return false
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return toRgbaString()
    }

    companion object {
        /**
         * Creates a Color from RGB values (0-255) with alpha=255
         */
        fun rgb(red: Int, green: Int, blue: Int): Color {
            return rgba(red, green, blue, 255)
        }

        /**
         * Creates a Color from RGBA values (0-255)
         */
        fun rgba(red: Int, green: Int, blue: Int, alpha: Int): Color {
            val r = red.coerceIn(0, 255)
            val g = green.coerceIn(0, 255)
            val b = blue.coerceIn(0, 255)
            val a = alpha.coerceIn(0, 255)
            return Color(
                (r.toUInt() shl 24) or
                        (g.toUInt() shl 16) or
                        (b.toUInt() shl 8) or
                        a.toUInt()
            )
        }

        /**
         * Creates a Color from a CSS hex string (#RRGGBB or #RRGGBBAA)
         */
        fun fromHex(hex: String): Color {
            // Remove # if present
            val hexVal = if (hex.startsWith("#")) hex.substring(1) else hex

            // Parse based on length
            return when (hexVal.length) {
                3 -> { // #RGB
                    val r = hexVal[0].toString().repeat(2).toInt(16)
                    val g = hexVal[1].toString().repeat(2).toInt(16)
                    val b = hexVal[2].toString().repeat(2).toInt(16)
                    rgb(r, g, b)
                }

                6 -> { // #RRGGBB
                    val value = hexVal.toUInt(16) shl 8 or 0xFFu
                    Color(value)
                }

                8 -> { // #RRGGBBAA
                    val value = hexVal.toUInt(16)
                    Color(value)
                }

                else -> throw IllegalArgumentException("Invalid hex color format: $hex")
            }
        }

        // Common color constants
        val BLACK = rgb(0, 0, 0)
        val WHITE = rgb(255, 255, 255)
        val RED = rgb(255, 0, 0)
        val GREEN = rgb(0, 255, 0)
        val BLUE = rgb(0, 0, 255)
        val YELLOW = rgb(255, 255, 0)
        val CYAN = rgb(0, 255, 255)
        val MAGENTA = rgb(255, 0, 255)
        val TRANSPARENT = rgba(0, 0, 0, 0)

        // Material Design colors
        val PRIMARY = fromHex("#2196F3") // Blue 500
        val PRIMARY_LIGHT = fromHex("#BBDEFB") // Blue 100
        val PRIMARY_DARK = fromHex("#1976D2") // Blue 700
        val SECONDARY = fromHex("#FF4081") // Pink A200
        val ERROR = fromHex("#F44336") // Red 500
        val WARNING = fromHex("#FFC107") // Amber 500
        val INFO = fromHex("#2196F3") // Blue 500
        val SUCCESS = fromHex("#4CAF50") // Green 500
    }
} 
