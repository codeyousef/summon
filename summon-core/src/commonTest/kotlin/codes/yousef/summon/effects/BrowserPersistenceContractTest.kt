package codes.yousef.summon.effects

import kotlin.test.Test
import kotlin.test.assertFailsWith

class BrowserPersistenceContractTest {
    @Test
    fun configurationRejectsUnsafeNamesAndUnboundedTransactions() {
        assertFailsWith<IllegalArgumentException> {
            BrowserRecordStoreConfig("../private", "records", 1)
        }
        assertFailsWith<IllegalArgumentException> {
            BrowserRecordStoreConfig("private", "records", 0)
        }
        assertFailsWith<IllegalArgumentException> {
            BrowserRecordStoreConfig("private", "records", 1, maxRecordBytes = 64, maxTransactionBytes = 63)
        }
    }

    @Test
    fun opaqueIdentifiersRejectPayloadDelimitersAndUnboundedValues() {
        assertFailsWith<IllegalArgumentException> {
            OpaqueTabEvent(OpaqueTabEventType.LOGOUT, "account:plaintext")
        }
        assertFailsWith<IllegalArgumentException> {
            OpaqueTabEvent(OpaqueTabEventType.INVALIDATE, "x".repeat(129))
        }
    }


    @Test
    fun workerRejectsCrossOriginAndTraversalPathsBeforeCapabilityLookup() {
        assertFailsWith<IllegalArgumentException> { createBrowserWorker("https://example.test/worker.js") }
        assertFailsWith<IllegalArgumentException> { createBrowserWorker("/assets/../worker.js") }
    }
}
