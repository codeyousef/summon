package codes.yousef.summon.extensions

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberExtensionsTest {
    
    @Test
    fun testPixelExtension() {
        // Test with integer
        assertEquals("10px", 10.px)
        
        // Test with double
        assertEquals("1.5px", 1.5.px)
        
        // Test with zero
        assertEquals("0px", 0.px)
        
        // Test with negative number
        assertEquals("-5px", (-5).px)
    }
    
    @Test
    fun testRemExtension() {
        // Test with integer
        assertEquals("2rem", 2.rem)
        
        // Test with double
        assertEquals("0.75rem", 0.75.rem)
        
        // Test with zero
        assertEquals("0rem", 0.rem)
        
        // Test with negative number
        assertEquals("-1.25rem", (-1.25).rem)
    }
    
    @Test
    fun testEmExtension() {
        // Test with integer
        assertEquals("1em", 1.em)
        
        // Test with double
        assertEquals("2.5em", 2.5.em)
        
        // Test with zero
        assertEquals("0em", 0.em)
        
        // Test with negative number
        assertEquals("-0.5em", (-0.5).em)
    }
    
    @Test
    fun testPercentExtension() {
        // Test with integer
        assertEquals("100%", 100.percent)
        
        // Test with double
        assertEquals("33.3%", 33.3.percent)
        
        // Test with zero
        assertEquals("0%", 0.percent)
        
        // Test with negative number
        assertEquals("-10%", (-10).percent)
    }
    
    @Test
    fun testViewportUnits() {
        // Test viewport width
        assertEquals("100vw", 100.vw)
        assertEquals("50.5vw", 50.5.vw)
        
        // Test viewport height
        assertEquals("100vh", 100.vh)
        assertEquals("75.25vh", 75.25.vh)
        
        // Test viewport min
        assertEquals("10vmin", 10.vmin)
        assertEquals("5.5vmin", 5.5.vmin)
        
        // Test viewport max
        assertEquals("90vmax", 90.vmax)
        assertEquals("45.75vmax", 45.75.vmax)
    }
    
    @Test
    fun testTypographyUnits() {
        // Test scale-independent pixels
        assertEquals("16sp", 16.sp)
        
        // Test character width
        assertEquals("2ch", 2.ch)
        
        // Test x-height
        assertEquals("3ex", 3.ex)
        
        // Test points
        assertEquals("12pt", 12.pt)
        
        // Test picas
        assertEquals("6pc", 6.pc)
    }
    
    @Test
    fun testTimeUnits() {
        // Test seconds
        assertEquals("2s", 2.s)
        assertEquals("0.5s", 0.5.s)
        
        // Test milliseconds
        assertEquals("500ms", 500.ms)
        assertEquals("16.7ms", 16.7.ms)
    }
}