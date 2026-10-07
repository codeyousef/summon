@file:JvmName("BrowserPersistenceJvm")

package codes.yousef.summon.effects

actual fun createBrowserRecordStore(
    config: BrowserRecordStoreConfig,
    onMigration: (BrowserMigrationEvent) -> Unit
): BrowserRecordStore = UnsupportedBrowserRecordStore

private object UnsupportedBrowserRecordStore : BrowserRecordStore {
    override suspend fun open(): Nothing = throw BrowserCapabilityError.Unavailable("indexed-db")
    override suspend fun read(key: String, maxBytes: Int): Nothing =
        throw BrowserCapabilityError.Unavailable("indexed-db")

    override fun beginTransaction(): Nothing = throw BrowserCapabilityError.Unavailable("indexed-db")
    override fun close() = Unit
}

actual fun createBrowserWorker(
    scriptPath: String,
    maxMessageBytes: Int,
    maxPendingRequests: Int
): BrowserWorker {
    validateWorkerConfig(scriptPath, maxMessageBytes, maxPendingRequests)
    throw BrowserCapabilityError.Unavailable("web-worker")
}
