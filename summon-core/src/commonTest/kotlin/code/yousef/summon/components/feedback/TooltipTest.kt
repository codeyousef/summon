package code.yousef.summon.components.feedback

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TooltipTest {

    @Test
    fun testGetPlacementStyles() {
        TooltipPlacement.values().forEach { placement ->
            val styles = getPlacementStyles(placement)
            // Basic checks - ensure transform is present and position keys vary
            assertTrue(styles.containsKey("transform"), "Placement styles for $placement missing transform.")
            when (placement) {
                TooltipPlacement.TOP -> {
                    assertEquals("100%", styles["bottom"])
                    assertEquals("50%", styles["left"])
                    assertEquals("translateX(-50%)", styles["transform"])
                }
                TooltipPlacement.RIGHT -> {
                    assertEquals("100%", styles["left"])
                    assertEquals("50%", styles["top"])
                    assertEquals("translateY(-50%)", styles["transform"])
                }
                TooltipPlacement.BOTTOM -> {
                    assertEquals("100%", styles["top"])
                    assertEquals("50%", styles["left"])
                    assertEquals("translateX(-50%)", styles["transform"])
                }
                TooltipPlacement.LEFT -> {
                    assertEquals("100%", styles["right"])
                    assertEquals("50%", styles["top"])
                    assertEquals("translateY(-50%)", styles["transform"])
                }
            }
        }
    }

    @Test
    fun testGetArrowStyles() {
        TooltipPlacement.values().forEach { placement ->
            val styles = getArrowStyles(placement)
            // Basic checks - ensure base styles + specific border styles are present
            assertEquals("absolute", styles["position"], "Arrow styles for $placement missing position.")
            assertEquals("solid", styles["border-style"], "Arrow styles for $placement missing border-style.")
            assertTrue(styles.containsKey("border-width"), "Arrow styles for $placement missing border-width.")
            assertTrue(styles.containsKey("border-color"), "Arrow styles for $placement missing border-color.")
            assertTrue(styles.containsKey("transform"), "Arrow styles for $placement missing transform.")
            
            // Check specific positioning based on placement
             when (placement) {
                TooltipPlacement.TOP -> {
                    assertTrue(styles["bottom"] == "-6px" || styles["top"] == "100%", "Incorrect top/bottom for $placement arrow")
                    assertTrue(styles["border-color"]!!.contains("#333 transparent transparent transparent"), "Incorrect border-color for $placement arrow")
                }
                TooltipPlacement.RIGHT -> {
                    assertTrue(styles["left"] == "-6px" || styles["right"] == "100%", "Incorrect left/right for $placement arrow")
                     assertTrue(styles["border-color"]!!.contains("transparent #333 transparent transparent"), "Incorrect border-color for $placement arrow")
                }
                TooltipPlacement.BOTTOM -> {
                    assertTrue(styles["top"] == "-6px" || styles["bottom"] == "100%", "Incorrect top/bottom for $placement arrow")
                    assertTrue(styles["border-color"]!!.contains("transparent transparent #333 transparent"), "Incorrect border-color for $placement arrow")
                }
                TooltipPlacement.LEFT -> {
                    assertTrue(styles["right"] == "-6px" || styles["left"] == "100%", "Incorrect left/right for $placement arrow")
                     assertTrue(styles["border-color"]!!.contains("transparent transparent transparent #333"), "Incorrect border-color for $placement arrow")
                }
            }
        }
    }

    @Test
    fun testGetAccessibilityAttributes() {
        val attrs = getAccessibilityAttributes()
        assertEquals("tooltip", attrs["role"])
        assertEquals("true", attrs["aria-hidden"])
    }

    @Test
    fun testGetTriggerAttributes() {
        val placement = TooltipPlacement.BOTTOM
        val showArrow = false
        val showOnClick = true
        val showDelay = 500
        val hideDelay = 0
        
        val attrs = getTriggerAttributes(placement, showArrow, showOnClick, showDelay, hideDelay)

        assertEquals("tooltip-content", attrs["aria-describedby"])
        assertEquals("true", attrs["data-summon-tooltip"])
        assertEquals(placement.name.lowercase(), attrs["data-tooltip-placement"])
        assertEquals(showArrow.toString(), attrs["data-tooltip-show-arrow"])
        assertEquals(showOnClick.toString(), attrs["data-tooltip-show-on-click"])
        assertEquals(showDelay.toString(), attrs["data-tooltip-show-delay"])
        assertEquals(hideDelay.toString(), attrs["data-tooltip-hide-delay"])
    }
} 