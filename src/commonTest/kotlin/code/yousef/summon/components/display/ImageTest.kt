package code.yousef.summon.components.display

import code.yousef.summon.modifier.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Common tests for the Image component's basic properties.
 */
class ImageTest {

    @Test
    fun testBasicImageProperties() {
        // Arrange
        val src = "https://example.com/image.jpg"
        val alt = "Example image"
        val component = Image(src, alt)
        
        // Assert
        assertEquals(src, component.src, "Image should store the provided source URL")
        assertEquals(alt, component.alt, "Image should store the provided alt text")
    }
    
    @Test
    fun testLoadingStrategy() {
        // Arrange
        val component = Image(
            src = "https://example.com/image.jpg",
            alt = "Example image",
            loading = ImageLoading.EAGER
        )
        
        // Assert
        assertEquals(ImageLoading.EAGER, component.loading, "Image should use the specified loading strategy")
        assertEquals("eager", component.loading.value, "Loading strategy should map to correct attribute value")
    }
    
    @Test
    fun testOptionalProperties() {
        // Arrange
        val width = "400px"
        val height = "300px"
        val contentDescription = "A detailed description of the image content"
        
        val component = Image(
            src = "https://example.com/image.jpg",
            alt = "Example image",
            width = width,
            height = height,
            contentDescription = contentDescription
        )
        
        // Assert
        assertEquals(width, component.width, "Image should store the provided width")
        assertEquals(height, component.height, "Image should store the provided height")
        assertEquals(contentDescription, component.contentDescription, "Image should store the provided content description")
    }
    
    @Test
    fun testDefaultValues() {
        // Arrange
        val component = Image(
            src = "https://example.com/image.jpg",
            alt = "Example image"
        )
        
        // Assert
        assertEquals(ImageLoading.LAZY, component.loading, "Default loading strategy should be LAZY")
        assertNull(component.width, "Width should be null by default")
        assertNull(component.height, "Height should be null by default")
        assertNull(component.contentDescription, "Content description should be null by default")
    }
    
    @Test
    fun testModifierApplication() {
        // Arrange
        val modifier = Modifier()
            .borderRadius("8px")
            .border("1px", "solid", "#ccc")
            .maxWidth("100%")
            .boxShadow("0 2px 4px rgba(0,0,0,0.1)")
        
        val component = Image(
            src = "https://example.com/image.jpg",
            alt = "Example image",
            modifier = modifier
        )
        
        // Assert
        assertEquals(modifier, component.modifier, "Image should store the provided modifier")
    }
} 