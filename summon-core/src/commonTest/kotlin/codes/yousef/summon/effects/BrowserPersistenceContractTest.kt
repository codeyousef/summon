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
    fun configurationAcceptsBoundaryNamesAndRejectsEveryInvalidLimit() {
        BrowserRecordStoreConfig("A".repeat(64), "_", 1, maxRecordBytes = 1, maxTransactionBytes = 1)
        listOf("", "x".repeat(65), "has.dot", "has space", "café").forEach { database ->
            assertFailsWith<IllegalArgumentException> {
                BrowserRecordStoreConfig(database, "records", 1)
            }
        }
        listOf("", "x".repeat(65), "has.dot", "has space", "café").forEach { store ->
            assertFailsWith<IllegalArgumentException> {
                BrowserRecordStoreConfig("private", store, 1)
            }
        }
        assertFailsWith<IllegalArgumentException> {
            BrowserRecordStoreConfig("private", "records", 1, maxRecordBytes = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            BrowserRecordStoreConfig("private", "records", 1, maxRecordBytes = 1, maxTransactionBytes = 0)
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
    fun opaqueIdentifierBoundaryAllowsOnlyProtocolSafeCharacters() {
        OpaqueTabEvent(OpaqueTabEventType.LOCK, "A._-09")
        OpaqueTabEvent(OpaqueTabEventType.UPGRADE, "x".repeat(128))
        listOf("", "bad:value", "bad/value", "bad value", "é").forEach { id ->
            assertFailsWith<IllegalArgumentException> {
                OpaqueTabEvent(OpaqueTabEventType.INVALIDATE, id)
            }
            assertFailsWith<BrowserCapabilityError.InvalidKey> { validateOpaqueKey(id) }
        }
        validateOpaqueKey("safe._-09")
    }


    @Test
    fun workerRejectsCrossOriginAndTraversalPathsBeforeCapabilityLookup() {
        assertFailsWith<IllegalArgumentException> { createBrowserWorker("https://example.test/worker.js") }
        assertFailsWith<IllegalArgumentException> { createBrowserWorker("/assets/../worker.js") }
    }

    @Test
    fun workerConfigurationRejectsEveryCrossBoundaryPathAndLimit() {
        validateWorkerConfig("/worker.js", 1, 1)
        listOf("", "worker.js", "//host/worker.js", "/bad\\worker.js", "/worker.js?x", "/worker.js#x").forEach {
            assertFailsWith<IllegalArgumentException> { validateWorkerConfig(it, 1, 1) }
        }
        assertFailsWith<IllegalArgumentException> { validateWorkerConfig("/worker.js", 0, 1) }
        assertFailsWith<IllegalArgumentException> { validateWorkerConfig("/worker.js", 1, 0) }
    }
}
