package codes.yousef.summon.effects

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class BrowserPersistenceJvmTest {
    @Test
    fun opaqueTabChannelStopsDeliveryAfterClose() {
        val sender = OpaqueTabChannel("suite-test")
        val receiver = OpaqueTabChannel("suite-test")
        val events = mutableListOf<OpaqueTabEvent>()
        receiver.onEvent(events::add)

        sender.post(OpaqueTabEvent(OpaqueTabEventType.LOGOUT, "lock-generation-2"))
        assertEquals(listOf(OpaqueTabEvent(OpaqueTabEventType.LOGOUT, "lock-generation-2")), events)

        receiver.close()
        sender.post(OpaqueTabEvent(OpaqueTabEventType.INVALIDATE, "cursor-3"))
        assertEquals(1, events.size)
        sender.close()
    }

    @Test
    fun durabilityReportsUnavailableInsteadOfFallingBackToMemory() = runTest {
        val store = createBrowserRecordStore(BrowserRecordStoreConfig("suite-test", "records", 1))
        assertIs<BrowserCapabilityError.Unavailable>(assertFailsWith<BrowserCapabilityError> { store.open() })
    }
}
