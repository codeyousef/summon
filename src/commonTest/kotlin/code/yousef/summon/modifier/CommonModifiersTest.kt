package code.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertEquals

class CommonModifiersTest {

    @Test
    fun testBackgroundColor() {
        val color = "#FF0000"
        // Need to use 'with' or import to access the extension function inside the object
        val modifier = with(CommonModifiers) {
            Modifier().backgroundColor(color)
        }
        assertEquals(color, modifier.styles["background-color"], "CommonModifiers.backgroundColor failed.")
    }
} 