package codes.yousef.summon.modifier

import codes.yousef.summon.core.style.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShadowConfigTest {

    @Test
    fun testShadowConfigCreate() {
        val shadow = ShadowConfig.create(
            horizontalOffset = 2,
            verticalOffset = 4,
            blurRadius = 6,
            spreadRadius = 1,
            color = Color.rgb(255, 0, 0),
            inset = false
        )
        
        assertEquals("2px 4px 6px 1px rgba(255, 0, 0, 1.0)", shadow.toCssString())
    }

    @Test
    fun testShadowConfigCreateWithInset() {
        val shadow = ShadowConfig.create(
            horizontalOffset = 0,
            verticalOffset = 0,
            blurRadius = 10,
            color = Color.rgb(0, 0, 255),
            inset = true
        )
        
        assertEquals("inset 0px 0px 10px rgba(0, 0, 255, 1.0)", shadow.toCssString())
    }

    @Test
    fun testShadowConfigCreateWithStringColor() {
        val shadow = ShadowConfig.create(
            horizontalOffset = 1,
            verticalOffset = 2,
            blurRadius = 3,
            color = Color.rgba(100, 150, 200, 0.5f)
        )
        
        // Note: 0.5f alpha gets converted to 127/255 ≈ 0.4980392...
        // Different platforms may have slightly different float precision
        val cssString = shadow.toCssString()
        assertTrue(cssString.startsWith("1px 2px 3px rgba(100, 150, 200, "))
        assertTrue(cssString.contains("0.498")) // Alpha should be approximately 0.498
        assertTrue(cssString.endsWith(")"))
    }

    @Test
    fun testShadowConfigGlow() {
        val shadow = ShadowConfig.glow(
            blurRadius = 15,
            color = Color.rgb(255, 255, 0)
        )
        
        assertEquals("0px 0px 15px rgba(255, 255, 0, 1.0)", shadow.toCssString())
    }

    @Test
    fun testShadowConfigGlowWithColorObject() {
        val shadow = ShadowConfig.glow(
            blurRadius = 8,
            color = Color.rgb(255, 0, 255)
        )
        
        assertEquals("0px 0px 8px rgba(255, 0, 255, 1.0)", shadow.toCssString())
    }

    @Test
    fun testShadowConfigInnerGlow() {
        val shadow = ShadowConfig.innerGlow(
            blurRadius = 12,
            color = Color.rgba(255, 255, 255, 128)
        )
        
        assertEquals("inset 0px 0px 12px rgba(255, 255, 255, 0.5019608)", shadow.toCssString())
    }


    @Test
    fun testShadowConfigEquality() {
        val shadow1 = ShadowConfig.create(2, 4, 6, color = Color.rgb(255, 0, 0))
        val shadow2 = ShadowConfig.create(2, 4, 6, color = Color.rgb(255, 0, 0))
        val shadow3 = ShadowConfig.create(3, 4, 6, color = Color.rgb(255, 0, 0))
        
        assertEquals(shadow1, shadow2)
        assertEquals(shadow1.hashCode(), shadow2.hashCode())
        assertTrue(shadow1 != shadow3)
    }

    @Test
    fun testShadowConfigToString() {
        val shadow = ShadowConfig.create(
            horizontalOffset = 2,
            verticalOffset = 4,
            blurRadius = 6,
            color = Color.rgb(255, 0, 0)
        )
        
        assertEquals("2px 4px 6px rgba(255, 0, 0, 1.0)", shadow.toCssString())
    }

    @Test
    fun testShadowConfigWithZeroValues() {
        val shadow = ShadowConfig.create(
            horizontalOffset = 0,
            verticalOffset = 0,
            blurRadius = 0,
            color = Color.rgb(0, 0, 0)
        )
        
        assertEquals("0px 0px 0px rgba(0, 0, 0, 1.0)", shadow.toCssString())
    }

    @Test
    fun testShadowConfigWithNegativeValues() {
        val shadow = ShadowConfig.create(
            horizontalOffset = -5,
            verticalOffset = -10,
            blurRadius = 15,
            spreadRadius = -2,
            color = Color.rgb(255, 255, 255)
        )
        
        assertEquals("-5px -10px 15px -2px rgba(255, 255, 255, 1.0)", shadow.toCssString())
    }
}