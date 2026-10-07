package codes.yousef.summon.components.feedback

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ToastManagerTest {
    @Test
    fun burstKeepsOnlyNewestConfiguredAnnouncements() {
        val manager = ToastManager(maxVisibleToasts = 3)

        repeat(5) { index -> manager.showInfo("status-$index", duration = 0) }

        assertEquals(listOf("status-2", "status-3", "status-4"), manager.toasts.map(ToastData::message))
    }

    @Test
    fun queueBoundMustBePositive() {
        assertFailsWith<IllegalArgumentException> { ToastManager(maxVisibleToasts = 0) }
    }
}
