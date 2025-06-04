package code.yousef.summon.extensions

import code.yousef.summon.modifier.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ModifierExtensionsTest {

    // Helper function to verify that a style property was set correctly
    private fun verifyStyle(modifier: Modifier, property: String, expectedValue: String) {
        val styleString = modifier.toStyleString()
        val styles = styleString.split(";").map { it.trim() }
        val styleEntry = styles.find { it.startsWith("$property:") }
        assertNotNull(styleEntry, "Style property '$property' not found in: $styleString")
        assertEquals("$property:$expectedValue", styleEntry)
    }

    @Test
    fun testMarginExtensions() {
        // Test margin with Number
        val modifier1 = Modifier().margin(10)
        verifyStyle(modifier1, "margin", "10px")

        // Test margin with String
        val modifier2 = Modifier().margin("20px")
        verifyStyle(modifier2, "margin", "20px")

        // Test margin with vertical and horizontal Numbers
        val modifier3 = Modifier().margin(5, 10)
        verifyStyle(modifier3, "margin", "5px 10px")

        // Test marginTop with Number
        val modifier4 = Modifier().marginTop(15)
        verifyStyle(modifier4, "margin-top", "15px")

        // Test marginTop with String
        val modifier5 = Modifier().marginTop("1.5rem")
        verifyStyle(modifier5, "margin-top", "1.5rem")

        // Test marginRight with Number
        val modifier6 = Modifier().marginRight(20)
        verifyStyle(modifier6, "margin-right", "20px")

        // Test marginBottom with Number
        val modifier7 = Modifier().marginBottom(25)
        verifyStyle(modifier7, "margin-bottom", "25px")

        // Test marginLeft with Number
        val modifier8 = Modifier().marginLeft(30)
        verifyStyle(modifier8, "margin-left", "30px")
    }

    @Test
    fun testDeprecatedMarginExtensions() {
        // Test marginHorizontalAuto
        val modifier1 = Modifier().marginHorizontalAuto(5)
        verifyStyle(modifier1, "margin", "5px auto")

        // Test marginVerticalAuto
        val modifier2 = Modifier().marginVerticalAuto(10)
        verifyStyle(modifier2, "margin", "auto 10px")

        // Test marginAuto
        val modifier3 = Modifier().marginAuto()
        verifyStyle(modifier3, "margin", "auto")
    }

    @Test
    fun testPaddingExtensions() {
        // Test padding with Number
        val modifier1 = Modifier().padding(10)
        verifyStyle(modifier1, "padding", "10px")

        // Test padding with String
        val modifier2 = Modifier().padding("20px")
        verifyStyle(modifier2, "padding", "20px")

        // Test padding with vertical and horizontal Numbers
        val modifier3 = Modifier().padding(5, 10)
        verifyStyle(modifier3, "padding", "5px 10px")

        // Test paddingTop with Number
        val modifier4 = Modifier().paddingTop(15)
        verifyStyle(modifier4, "padding-top", "15px")

        // Test paddingRight with Number
        val modifier5 = Modifier().paddingRight(20)
        verifyStyle(modifier5, "padding-right", "20px")

        // Test paddingBottom with Number
        val modifier6 = Modifier().paddingBottom(25)
        verifyStyle(modifier6, "padding-bottom", "25px")

        // Test paddingLeft with Number
        val modifier7 = Modifier().paddingLeft(30)
        verifyStyle(modifier7, "padding-left", "30px")
    }

    @Test
    fun testSizeExtensions() {
        // Test width with Number
        val modifier1 = Modifier().width(100)
        verifyStyle(modifier1, "width", "100px")

        // Test height with Number
        val modifier2 = Modifier().height(200)
        verifyStyle(modifier2, "height", "200px")

        // Test minWidth with Number
        val modifier3 = Modifier().minWidth(50)
        verifyStyle(modifier3, "min-width", "50px")

        // Test minWidth with another Number
        val modifier4 = Modifier().minWidth(160)
        verifyStyle(modifier4, "min-width", "160px")

        // Test minHeight with Number
        val modifier5 = Modifier().minHeight(75)
        verifyStyle(modifier5, "min-height", "75px")

        // Test maxWidth with Number
        val modifier6 = Modifier().maxWidth(300)
        verifyStyle(modifier6, "max-width", "300px")

        // Test maxHeight with Number
        val modifier7 = Modifier().maxHeight(400)
        verifyStyle(modifier7, "max-height", "400px")

        // Test size with width and height
        val modifier8 = Modifier().size(150, 250)
        verifyStyle(modifier8, "width", "150px")
        verifyStyle(modifier8, "height", "250px")

        // Test size with single value
        val modifier9 = Modifier().size(200)
        verifyStyle(modifier9, "width", "200px")
        verifyStyle(modifier9, "height", "200px")
    }

    @Test
    fun testOtherExtensions() {
        // Test borderRadius with Number
        val modifier1 = Modifier().borderRadius(8)
        verifyStyle(modifier1, "border-radius", "8px")

        // Test fontSize with Number
        val modifier2 = Modifier().fontSize(16)
        verifyStyle(modifier2, "font-size", "16px")

        // Test fontSize with String
        val modifier3 = Modifier().fontSize("1.2rem")
        verifyStyle(modifier3, "font-size", "1.2rem")
    }
}
