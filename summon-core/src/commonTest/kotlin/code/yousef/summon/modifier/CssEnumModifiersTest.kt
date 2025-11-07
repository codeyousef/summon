package code.yousef.summon.modifier

// Import the specific extension functions if needed, or rely on package import
// Import Enums used

import kotlin.test.Test
import kotlin.test.assertEquals

class CssEnumModifiersTest {

    @Test
    fun testPositionEnumModifier() {
        val modifier = Modifier()
        val expectedValue = Position.Absolute.toString() // e.g., "absolute"

        val modified = modifier.position(Position.Absolute)

        assertEquals(expectedValue, modified.styles["position"], "Position enum modifier failed.")
    }

    @Test
    fun testDisplayEnumModifier() {
        val modifier = Modifier()
        val expectedValue = Display.Flex.toString() // e.g., "flex"

        val modified = modifier.display(Display.Flex)

        assertEquals(expectedValue, modified.styles["display"], "Display enum modifier failed.")

        val modifiedGrid = modifier.display(Display.Grid)
        assertEquals(Display.Grid.toString(), modifiedGrid.styles["display"], "Display enum modifier (GRID) failed.")
    }

    @Test
    fun testAlignItemsEnumModifier() {
        val modifier = Modifier()
        val expectedValue = AlignItems.Center.toString() // e.g., "center"

        val modified = modifier.alignItems(AlignItems.Center)

        assertEquals(expectedValue, modified.styles["align-items"], "AlignItems enum modifier failed.")

        val modifiedStart = modifier.alignItems(AlignItems.FlexStart)
        assertEquals(AlignItems.FlexStart.toString(), modifiedStart.styles["align-items"], "AlignItems enum modifier (FLEX_START) failed.")
    }

     @Test
    fun testJustifyContentEnumModifier() {
        val modifier = Modifier()
        val expectedValue = JustifyContent.SpaceBetween.toString()

        val modified = modifier.justifyContent(JustifyContent.SpaceBetween)

        assertEquals(expectedValue, modified.styles["justify-content"], "JustifyContent enum modifier failed.")
    }

    @Test
    fun testFlexDirectionEnumModifier() {
        val modifier = Modifier()
        val modified = modifier.flexDirection(FlexDirection.ColumnReverse)

        assertEquals(FlexDirection.ColumnReverse.toString(), modified.styles["flex-direction"])
    }

    @Test
    fun testVisibilityEnumModifier() {
        val modifier = Modifier()
        val modified = modifier.visibility(Visibility.Hidden)

        assertEquals(Visibility.Hidden.toString(), modified.styles["visibility"])
    }
}
