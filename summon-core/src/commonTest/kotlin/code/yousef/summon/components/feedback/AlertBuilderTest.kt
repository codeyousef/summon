package codes.yousef.summon.components.feedback

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.Composable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

/**
 * Tests for the AlertBuilder class
 */
class AlertBuilderTest {
    
    @Test
    fun testAlertBuilderDefaultValues() {
        // Create a new AlertBuilder
        val builder = AlertBuilder()

        // Verify default values
        assertEquals("", builder.message)
        assertNull(builder.title)
        assertEquals(AlertVariant.INFO, builder.variant)
        assertNull(builder.onDismiss)
        assertNull(builder.icon)
        assertNull(builder.actions)
        assertEquals(Modifier(), builder.modifier)
    }

    @Test
    fun testAlertBuilderCustomValues() {
        // Create a new AlertBuilder
        val builder = AlertBuilder()

        // Set custom values
        val customMessage = "Test message"
        val customTitle = "Test title"
        val customVariant = AlertVariant.SUCCESS
        val customOnDismiss: () -> Unit = {}
        val customIcon: @Composable () -> Unit = {}
        val customActions: @Composable () -> Unit = {}
        val customModifier = Modifier().background("red")

        builder.message = customMessage
        builder.title = customTitle
        builder.variant = customVariant
        builder.onDismiss = customOnDismiss
        builder.icon = customIcon
        builder.actions = customActions
        builder.modifier = customModifier

        // Verify custom values
        assertEquals(customMessage, builder.message)
        assertEquals(customTitle, builder.title)
        assertEquals(customVariant, builder.variant)
        assertSame(customOnDismiss, builder.onDismiss)
        assertSame(customIcon, builder.icon)
        assertSame(customActions, builder.actions)
        assertSame(customModifier, builder.modifier)
    }
}