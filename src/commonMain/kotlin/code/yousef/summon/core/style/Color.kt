package code.yousef.summon.core.style

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

/**
 * Simple color class that can be used across platforms.
 */
class Color(val value: UInt) {
    companion object {
        // Some basic colors
        val Black = Color(0xFF000000u)
        val White = Color(0xFFFFFFFFu)
        val Red = Color(0xFFFF0000u)
        val Green = Color(0xFF00FF00u)
        val Blue = Color(0xFF0000FFu)
        val Yellow = Color(0xFFFFFF00u)
        val Cyan = Color(0xFF00FFFFu)
        val Magenta = Color(0xFFFF00FFu)
        val Gray = Color(0xFF808080u)
        val LightGray = Color(0xFFD3D3D3u)
        val DarkGray = Color(0xFF404040u)
        val Transparent = Color(0x00000000u)
        
        /**
         * Create a color from RGB values (0-255)
         */
        fun rgb(r: Int, g: Int, b: Int): Color {
            return Color(((255u shl 24) or (r.toUInt() shl 16) or (g.toUInt() shl 8) or b.toUInt()))
        }
        
        /**
         * Create a color from RGBA values (0-255)
         */
        fun rgba(r: Int, g: Int, b: Int, a: Int): Color {
            return Color(((a.toUInt() shl 24) or (r.toUInt() shl 16) or (g.toUInt() shl 8) or b.toUInt()))
        }
    }
    
    /**
     * Get the alpha component of this color (0-255)
     */
    val alpha: Int
        get() = ((value shr 24) and 0xFFu).toInt()
    
    /**
     * Get the red component of this color (0-255)
     */
    val red: Int
        get() = ((value shr 16) and 0xFFu).toInt()
    
    /**
     * Get the green component of this color (0-255)
     */
    val green: Int
        get() = ((value shr 8) and 0xFFu).toInt()
    
    /**
     * Get the blue component of this color (0-255)
     */
    val blue: Int
        get() = (value and 0xFFu).toInt()
    
    /**
     * Get the color as a hex string (e.g. #RRGGBB)
     */
    fun toHexString(): String {
        return "#${red.toString(16).padStart(2, '0')}${green.toString(16).padStart(2, '0')}${blue.toString(16).padStart(2, '0')}"
    }
    
    /**
     * Get the color with a new alpha value
     */
    fun withAlpha(alpha: Int): Color {
        return Color(((alpha.toUInt() and 0xFFu) shl 24) or (value and 0x00FFFFFFu))
    }
} 
