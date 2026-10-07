package codes.yousef.summon.effects

import codes.yousef.summon.desktop.communication.SummonBroadcastChannel
import codes.yousef.summon.desktop.communication.createBroadcastChannel

/** Configuration for an opaque binary IndexedDB record store. */
data class BrowserRecordStoreConfig(
    val databaseName: String,
    val storeName: String,
    val schemaVersion: Int,
    val maxRecordBytes: Int = 4 * 1024 * 1024,
    val maxTransactionBytes: Int = 16 * 1024 * 1024
) {
    init {
        require(databaseName.isValidCapabilityName()) { "Invalid database name" }
        require(storeName.isValidCapabilityName()) { "Invalid store name" }
        require(schemaVersion > 0) { "Schema version must be positive" }
        require(maxRecordBytes > 0) { "Record byte limit must be positive" }
        require(maxTransactionBytes >= maxRecordBytes) {
            "Transaction byte limit must be at least the record byte limit"
        }
    }
}

/** Lifecycle information emitted while opening a browser record store. */
sealed class BrowserMigrationEvent {
    data class Upgrade(val oldVersion: Int, val newVersion: Int) : BrowserMigrationEvent()
    data class Blocked(val oldVersion: Int, val newVersion: Int) : BrowserMigrationEvent()
    data class Ready(val version: Int) : BrowserMigrationEvent()
}

/** Safe failures for browser durability and worker capabilities. Payload bytes are never included. */
sealed class BrowserCapabilityError(message: String) : Exception(message) {
    class Unavailable(val capability: String) : BrowserCapabilityError("Browser capability unavailable: $capability")
    class InvalidKey : BrowserCapabilityError("Invalid opaque record key")
    class RecordTooLarge(val limitBytes: Int) : BrowserCapabilityError("Record exceeds configured byte limit")
    class TransactionTooLarge(val limitBytes: Int) : BrowserCapabilityError("Transaction exceeds configured byte limit")
    class TooManyRequests(val limit: Int) : BrowserCapabilityError("Worker pending request limit reached")
    class QuotaExceeded : BrowserCapabilityError("Browser storage quota exceeded")
    class DataEvicted : BrowserCapabilityError("Browser durable data is unavailable")
    class UpgradeBlocked : BrowserCapabilityError("Browser database upgrade blocked")
    class VersionMismatch : BrowserCapabilityError("Browser database version mismatch")
    class Closed : BrowserCapabilityError("Browser capability is closed")
    class Aborted : BrowserCapabilityError("Browser operation aborted")
    class ProtocolError : BrowserCapabilityError("Browser capability protocol error")
    class OperationFailed : BrowserCapabilityError("Browser capability operation failed")
}

/** Stages opaque record mutations and commits them in one IndexedDB transaction. */
interface BrowserRecordTransaction {
    fun put(key: String, value: ByteArray)
    fun delete(key: String)
    suspend fun commit()
    fun abort()
}

/** Browser-only durable binary storage. It does not encrypt or interpret records. */
interface BrowserRecordStore {
    suspend fun open()
    suspend fun read(key: String, maxBytes: Int = Int.MAX_VALUE): ByteArray?
    fun beginTransaction(): BrowserRecordTransaction
    fun close()
}

/** Creates a real browser IndexedDB store. JVM targets report [BrowserCapabilityError.Unavailable]. */
expect fun createBrowserRecordStore(
    config: BrowserRecordStoreConfig,
    onMigration: (BrowserMigrationEvent) -> Unit = {}
): BrowserRecordStore

enum class OpaqueTabEventType { LOCK, INVALIDATE, LOGOUT, UPGRADE }

data class OpaqueTabEvent(val type: OpaqueTabEventType, val opaqueId: String) {
    init {
        require(opaqueId.isValidOpaqueId()) { "Invalid opaque event identifier" }
    }
}

/** A bounded, fixed-protocol cross-tab channel. Arbitrary application payloads are intentionally unsupported. */
class OpaqueTabChannel(channelId: String) {
    private val channel: SummonBroadcastChannel<String>
    private var closed = false

    init {
        require(channelId.isValidCapabilityName()) { "Invalid channel identifier" }
        channel = createBroadcastChannel("summon-opaque-$channelId")
    }

    fun post(event: OpaqueTabEvent) {
        check(!closed) { "Opaque tab channel is closed" }
        channel.postMessage("${event.type.name}:${event.opaqueId}")
    }

    fun onEvent(handler: (OpaqueTabEvent) -> Unit): () -> Unit = channel.onMessage { encoded ->
        if (closed || encoded.length > MAX_OPAQUE_MESSAGE_CHARS) return@onMessage
        val separator = encoded.indexOf(':')
        if (separator <= 0) return@onMessage
        val type = OpaqueTabEventType.entries.firstOrNull { it.name == encoded.substring(0, separator) }
            ?: return@onMessage
        val opaqueId = encoded.substring(separator + 1)
        if (!opaqueId.isValidOpaqueId()) return@onMessage
        handler(OpaqueTabEvent(type, opaqueId))
    }

    fun close() {
        if (closed) return
        closed = true
        channel.close()
    }
}

/** Same-origin browser worker request channel for bounded opaque binary messages. */
interface BrowserWorker : AutoCloseable {
    suspend fun request(correlationId: String, payload: ByteArray): ByteArray
    fun cancel(correlationId: String)
    override fun close()
}

/** Creates a browser Worker. [scriptPath] must be root-relative and same-origin. */
expect fun createBrowserWorker(
    scriptPath: String,
    maxMessageBytes: Int = 4 * 1024 * 1024,
    maxPendingRequests: Int = 32
): BrowserWorker

internal fun validateWorkerConfig(scriptPath: String, maxMessageBytes: Int, maxPendingRequests: Int) {
    require(scriptPath.startsWith('/') && !scriptPath.startsWith("//") && !scriptPath.contains('\\')) {
        "Worker script path must be root-relative"
    }
    require(!scriptPath.contains("..") && !scriptPath.contains('?') && !scriptPath.contains('#')) {
        "Worker script path must not contain traversal, query, or fragment components"
    }
    require(maxMessageBytes > 0) { "Worker message byte limit must be positive" }
    require(maxPendingRequests > 0) { "Pending request limit must be positive" }
}

internal fun validateOpaqueKey(key: String) {
    if (!key.isValidOpaqueId()) throw BrowserCapabilityError.InvalidKey()
}

private fun String.isValidCapabilityName(): Boolean =
    length in 1..64 && all { it.isLetterOrDigit() || it == '-' || it == '_' }

internal fun String.isValidOpaqueId(): Boolean =
    length in 1..128 && all { it.isLetterOrDigit() || it == '-' || it == '_' || it == '.' }

private const val MAX_OPAQUE_MESSAGE_CHARS = 144
