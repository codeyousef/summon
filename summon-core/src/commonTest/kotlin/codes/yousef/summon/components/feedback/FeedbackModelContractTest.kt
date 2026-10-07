package codes.yousef.summon.components.feedback

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FeedbackModelContractTest {
    @Test
    fun progressModelsCoverTypesSizesAnimationsAndAccessibilityBoundaries() {
        val linearSmall = Progress(type = ProgressType.LINEAR, value = -10, maxValue = 100, size = "small", animation = ProgressAnimation.NONE)
        assertFalse(linearSmall.isIndeterminate())
        assertEquals(0, linearSmall.getPercentage())
        assertEquals("4px", linearSmall.getTypeStyles()["height"])
        assertTrue(linearSmall.getAnimationStyles().isEmpty())
        assertNull(linearSmall.getAnimationKeyframes())
        assertEquals("-10", linearSmall.getAccessibilityAttributes()["aria-valuenow"])
        assertEquals("0%", linearSmall.getAccessibilityAttributes()["aria-valuetext"])

        val circularLarge = Progress(
            type = ProgressType.CIRCULAR,
            value = 120,
            maxValue = 100,
            size = "large",
            animation = ProgressAnimation.BOUNCE,
            label = "Upload",
        )
        assertEquals(100, circularLarge.getPercentage())
        assertEquals("48px", circularLarge.getTypeStyles()["width"])
        assertEquals("bounce 1s infinite", circularLarge.getAnimationStyles()["animation"])
        assertContains(circularLarge.getAnimationKeyframes()!!, "@keyframes bounce")
        assertEquals("Upload", circularLarge.getAccessibilityAttributes()["aria-label"])

        val circularDefault = circularProgress(value = 25)
        assertEquals("36px", circularDefault.getTypeStyles()["height"])
        assertEquals(25, circularDefault.getPercentage())
        val indeterminate = loading(type = ProgressType.INDETERMINATE)
        assertTrue(indeterminate.isIndeterminate())
        assertEquals(0, indeterminate.getPercentage())
        assertEquals("4px", indeterminate.getTypeStyles()["height"])
        assertEquals("Loading", indeterminate.getAccessibilityAttributes()["aria-valuetext"])
        assertFalse(indeterminate.getAccessibilityAttributes().containsKey("aria-valuenow"))

        val pulse = linearProgress(50).copy(animation = ProgressAnimation.PULSE, size = "large")
        assertEquals("8px", pulse.getTypeStyles()["height"])
        assertEquals("pulse 1.5s infinite", pulse.getAnimationStyles()["animation"])
        assertContains(pulse.getAnimationKeyframes()!!, "@keyframes pulse")
        assertTrue(Progress(value = null).isIndeterminate())
        assertEquals(0, Progress(value = null).getPercentage())
    }

    @Test
    fun snackbarVariantsExposeStableForegroundAndBackgroundPairs() {
        val expected = mapOf(
            SnackbarVariant.DEFAULT to ("#6b7280" to "#f3f4f6"),
            SnackbarVariant.INFO to ("#0284c7" to "#e0f2fe"),
            SnackbarVariant.SUCCESS to ("#16a34a" to "#dcfce7"),
            SnackbarVariant.WARNING to ("#d97706" to "#fef3c7"),
            SnackbarVariant.ERROR to ("#dc2626" to "#fee2e2"),
        )
        expected.forEach { (variant, colors) ->
            assertEquals(colors.first, variant.getColor())
            assertEquals(colors.second, variant.getBackgroundColor())
        }
    }
}
