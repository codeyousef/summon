package code.yousef.summon.components.display

import code.yousef.summon.components.display.Image
import code.yousef.summon.components.display.ImageLoading
import code.yousef.summon.verifyProperties
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

/**
 * JS tests for the Image component
 */
class ImageJsTest {
    @Test
    fun testBasicProperty() {
        // Test basic properties
        val src = "test-image.jpg"
        val alt = "Test image"
        val component = Image(src, alt)
        
        assertTrue(component.verifyProperties(src, alt))
    }
    
    @Test
    fun testImageLoading() {
        val src = "test-image.jpg"
        val alt = "Test image"
        val component = Image(src, alt, loading = ImageLoading.LAZY)
        
        assertEquals(ImageLoading.LAZY, component.loading)
    }
    
    @Test
    fun testSizingAttributes() {
        val src = "test-image.jpg"
        val alt = "Test image"
        val width = "300"
        val height = "200"
        val component = Image(src, alt, width = width, height = height)
        
        assertTrue(component.verifyProperties(src, alt, width, height))
    }
    
    @Test
    fun testContentDescription() {
        val src = "test-image.jpg"
        val alt = "Test image"
        val description = "Detailed description of this image"
        val component = Image(src, alt, contentDescription = description)
        
        assertEquals(description, component.contentDescription)
    }
} 