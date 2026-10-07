@file:JvmName("BrowserPersistenceJvm")

package codes.yousef.summon.effects

/**
 * Creates browser record store.
 *
 * @param config The config value.
 * @param onMigration Callback invoked when migration.
 * @return The resulting value.
 */
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

/**
 * Creates browser worker.
 *
 * @param scriptPath The script path value.
 * @param maxMessageBytes The max message bytes value.
 * @param maxPendingRequests The max pending requests value.
 * @return The resulting value.
 */
actual fun createBrowserWorker(
    scriptPath: String,
    maxMessageBytes: Int,
    maxPendingRequests: Int
): BrowserWorker {
    validateWorkerConfig(scriptPath, maxMessageBytes, maxPendingRequests)
    throw BrowserCapabilityError.Unavailable("web-worker")
}
