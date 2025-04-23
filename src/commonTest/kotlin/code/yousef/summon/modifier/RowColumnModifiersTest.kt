package code.yousef.summon.modifier

import code.yousef.summon.components.layout.Alignment
import kotlin.test.Test
import kotlin.test.assertEquals

class RowColumnModifiersTest {

    @Test
    fun testVerticalAlignment() {
        val modifierTop = Modifier().verticalAlignment(Alignment.Vertical.Top)
        assertEquals(AlignItems.FlexStart.toString(), modifierTop.styles["align-items"], "Vertical.Top failed.")

        val modifierCenter = Modifier().verticalAlignment(Alignment.Vertical.CenterVertically)
        assertEquals(AlignItems.Center.toString(), modifierCenter.styles["align-items"], "Vertical.CenterVertically failed.")

        val modifierBottom = Modifier().verticalAlignment(Alignment.Vertical.Bottom)
        assertEquals(AlignItems.FlexEnd.toString(), modifierBottom.styles["align-items"], "Vertical.Bottom failed.")
    }

    @Test
    fun testHorizontalAlignment() {
        val modifierStart = Modifier().horizontalAlignment(Alignment.Horizontal.Start)
        assertEquals(AlignItems.FlexStart.toString(), modifierStart.styles["align-items"], "Horizontal.Start failed.")

        val modifierCenter = Modifier().horizontalAlignment(Alignment.Horizontal.CenterHorizontally)
        assertEquals(AlignItems.Center.toString(), modifierCenter.styles["align-items"], "Horizontal.CenterHorizontally failed.")

        val modifierEnd = Modifier().horizontalAlignment(Alignment.Horizontal.End)
        assertEquals(AlignItems.FlexEnd.toString(), modifierEnd.styles["align-items"], "Horizontal.End failed.")
    }
} 