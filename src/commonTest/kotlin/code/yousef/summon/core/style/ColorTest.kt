package code.yousef.summon.core.style

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class ColorTest {

    @Test
    fun testColorConstructorAndProperties() {
        // Test with a simple color (red)
        val red = Color(0xFF0000FFu) // RGBA: 255, 0, 0, 255
        assertEquals(255, red.red, "Red component should be 255")
        assertEquals(0, red.green, "Green component should be 0")
        assertEquals(0, red.blue, "Blue component should be 0")
        assertEquals(255, red.alpha, "Alpha component should be 255")
        assertEquals(1.0f, red.alphaFloat, "Alpha as float should be 1.0")

        // Test with a color with partial alpha
        val semiTransparentBlue = Color(0x0000FF80u) // RGBA: 0, 0, 255, 128
        assertEquals(0, semiTransparentBlue.red, "Red component should be 0")
        assertEquals(0, semiTransparentBlue.green, "Green component should be 0")
        assertEquals(255, semiTransparentBlue.blue, "Blue component should be 255")
        assertEquals(128, semiTransparentBlue.alpha, "Alpha component should be 128")
        assertEquals(0.5019608f, semiTransparentBlue.alphaFloat, 0.0001f, "Alpha as float should be approximately 0.5")
    }

    @Test
    fun testWithAlpha() {
        val red = Color(0xFF0000FFu) // RGBA: 255, 0, 0, 255

        // Test changing alpha to 50%
        val semiTransparentRed = red.withAlpha(0.5f)
        assertEquals(255, semiTransparentRed.red, "Red component should remain 255")
        assertEquals(0, semiTransparentRed.green, "Green component should remain 0")
        assertEquals(0, semiTransparentRed.blue, "Blue component should remain 0")
        assertEquals(127, semiTransparentRed.alpha, "Alpha component should be 127")

        // Test changing alpha to 0 (fully transparent)
        val transparentRed = red.withAlpha(0f)
        assertEquals(255, transparentRed.red, "Red component should remain 255")
        assertEquals(0, transparentRed.alpha, "Alpha component should be 0")

        // Test changing alpha to 1 (fully opaque)
        val opaqueRed = semiTransparentRed.withAlpha(1f)
        assertEquals(255, opaqueRed.alpha, "Alpha component should be 255")

        // Test with out-of-range values (should be clamped)
        val clampedNegative = red.withAlpha(-0.5f)
        assertEquals(0, clampedNegative.alpha, "Negative alpha should be clamped to 0")

        val clampedPositive = red.withAlpha(1.5f)
        assertEquals(255, clampedPositive.alpha, "Alpha > 1 should be clamped to 255")
    }

    @Test
    fun testToRgbaString() {
        val red = Color(0xFF0000FFu) // RGBA: 255, 0, 0, 255
        assertEquals("rgba(255, 0, 0, 1.0)", red.toRgbaString(), "RGBA string should match expected format")

        val semiTransparentBlue = Color(0x0000FF80u) // RGBA: 0, 0, 255, 128
        assertEquals("rgba(0, 0, 255, 0.5019608)", semiTransparentBlue.toRgbaString(), "RGBA string with alpha should match expected format")
    }

    @Test
    fun testToHexString() {
        val red = Color(0xFF0000FFu) // RGBA: 255, 0, 0, 255
        assertEquals("#ff0000ff", red.toHexString(), "Hex string should match expected format")

        val semiTransparentBlue = Color(0x0000FF80u) // RGBA: 0, 0, 255, 128
        assertEquals("#0000ff80", semiTransparentBlue.toHexString(), "Hex string with alpha should match expected format")
    }

    @Test
    fun testEqualsAndHashCode() {
        val color1 = Color(0xFF0000FFu)
        val color2 = Color(0xFF0000FFu)
        val color3 = Color(0x00FF00FFu)

        // Test equals
        assertEquals(color1, color2, "Colors with same value should be equal")
        assertNotEquals(color1, color3, "Colors with different values should not be equal")
        assertFalse(color1.equals("not a color"), "Color should not equal non-Color object")

        // Test hashCode
        assertEquals(color1.hashCode(), color2.hashCode(), "Equal colors should have same hash code")
        assertNotEquals(color1.hashCode(), color3.hashCode(), "Different colors should have different hash codes")
    }

    @Test
    fun testToString() {
        val red = Color(0xFF0000FFu) // RGBA: 255, 0, 0, 255
        assertEquals("rgba(255, 0, 0, 1.0)", red.toString(), "toString should return RGBA string")
    }

    @Test
    fun testRgbCreation() {
        // Test using companion object method
        val red1 = Color.rgb(255, 0, 0)
        assertEquals(255, red1.red, "Red component should be 255")
        assertEquals(0, red1.green, "Green component should be 0")
        assertEquals(0, red1.blue, "Blue component should be 0")
        assertEquals(255, red1.alpha, "Alpha component should be 255")

        // Test using RGB helper class
        val red2 = Color.rgb(255, 0, 0)
        assertEquals(red1, red2, "Both RGB creation methods should produce the same color")
    }

    @Test
    fun testRgbaCreation() {
        // Test using companion object method with int alpha
        val semiRed1 = Color.rgba(255, 0, 0, 128)
        assertEquals(255, semiRed1.red, "Red component should be 255")
        assertEquals(128, semiRed1.alpha, "Alpha component should be 128")

        // Test using companion object method with float alpha
        val semiRed2 = Color.rgba(255, 0, 0, 0.5f)
        assertEquals(127, semiRed2.alpha, "Alpha component should be 127")

        // Test using RGBA helper class
        val semiRed3 = Color.rgba(255, 0, 0, 128)
        val semiRed4 = Color.rgba(255, 0, 0, 0.5f)
        assertEquals(semiRed1, semiRed3, "Both RGBA creation methods should produce the same color")
        assertEquals(semiRed2.red, semiRed4.red, "Red component should be the same")
        assertEquals(semiRed2.alpha, semiRed4.alpha, "Alpha component should be the same")

        // Test value clamping
        val clampedColor = Color.rgba(300, -10, 1000, 2f)
        assertEquals(255, clampedColor.red, "Red should be clamped to 255")
        assertEquals(0, clampedColor.green, "Green should be clamped to 0")
        assertEquals(255, clampedColor.blue, "Blue should be clamped to 255")
        assertEquals(255, clampedColor.alpha, "Alpha should be clamped to 255")
    }

    @Test
    fun testFromHex() {
        // Test 6-digit hex (RRGGBB)
        val red1 = Color.fromHex("#FF0000")
        assertEquals(255, red1.red, "Red component should be 255")
        assertEquals(0, red1.green, "Green component should be 0")
        assertEquals(0, red1.blue, "Blue component should be 0")
        assertEquals(255, red1.alpha, "Alpha component should be 255")

        // Test 8-digit hex (RRGGBBAA)
        val semiRed = Color.fromHex("#FF000080")
        assertEquals(255, semiRed.red, "Red component should be 255")
        assertEquals(128, semiRed.alpha, "Alpha component should be 128")

        // Test 3-digit hex (RGB)
        val red2 = Color.fromHex("#F00")
        assertEquals(255, red2.red, "Red component should be 255")
        assertEquals(0, red2.green, "Green component should be 0")
        assertEquals(0, red2.blue, "Blue component should be 0")

        // Test without # prefix
        val blue = Color.fromHex("0000FF")
        assertEquals(0, blue.red, "Red component should be 0")
        assertEquals(0, blue.green, "Green component should be 0")
        assertEquals(255, blue.blue, "Blue component should be 255")

        // Test using HEX helper class
        val green = Color.hex("#00FF00")
        assertEquals(0, green.red, "Red component should be 0")
        assertEquals(255, green.green, "Green component should be 255")
        assertEquals(0, green.blue, "Blue component should be 0")

        // Test invalid format
        assertFailsWith<IllegalArgumentException>("Invalid hex format should throw exception") {
            Color.fromHex("#XYZ")
        }

        assertFailsWith<IllegalArgumentException>("Invalid hex length should throw exception") {
            Color.fromHex("#12345")
        }
    }

    @Test
    fun testColorConstants() {
        // Test basic color constants
        assertEquals(Color.rgb(0, 0, 0), Color.BLACK, "BLACK should be rgb(0,0,0)")
        assertEquals(Color.rgb(255, 255, 255), Color.WHITE, "WHITE should be rgb(255,255,255)")
        assertEquals(Color.rgb(255, 0, 0), Color.RED, "RED should be rgb(255,0,0)")
        assertEquals(Color.rgb(0, 255, 0), Color.GREEN, "GREEN should be rgb(0,255,0)")
        assertEquals(Color.rgb(0, 0, 255), Color.BLUE, "BLUE should be rgb(0,0,255)")
        assertEquals(Color.rgba(0, 0, 0, 0), Color.TRANSPARENT, "TRANSPARENT should be rgba(0,0,0,0)")

        // Test Material Design colors
        assertEquals(Color.fromHex("#2196F3"), Color.PRIMARY, "PRIMARY should match expected hex value")
        assertEquals(Color.fromHex("#F44336"), Color.ERROR, "ERROR should match expected hex value")
    }
}
