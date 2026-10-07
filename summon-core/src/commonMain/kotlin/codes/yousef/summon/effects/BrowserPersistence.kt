package codes.yousef.summon.effects

import codes.yousef.summon.desktop.communication.SummonBroadcastChannel
import codes.yousef.summon.desktop.communication.createBroadcastChannel

/**
 * Configuration for an opaque binary IndexedDB record store.
 *
 * @property databaseName Browser database name; ASCII letters, digits, `_`, and `-` only
 * @property storeName Object-store name under [databaseName]
 * @property schemaVersion Positive IndexedDB schema version
 * @property maxRecordBytes Maximum bytes accepted by one record
 * @property maxTransactionBytes Maximum aggregate bytes accepted by one transaction
 * @throws IllegalArgumentException when a name or byte/version limit is invalid
 */
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
    /**
     * Schema migration from [oldVersion] to [newVersion] is running.
     *
     * @property oldVersion The old version value.
     * @property newVersion The new version value.
     */
    data class Upgrade(val oldVersion: Int, val newVersion: Int) : BrowserMigrationEvent()
    /**
     * Another browser context blocks migration from [oldVersion] to [newVersion].
     *
     * @property oldVersion The old version value.
     * @property newVersion The new version value.
     */
    data class Blocked(val oldVersion: Int, val newVersion: Int) : BrowserMigrationEvent()
    /**
     * The store opened at [version].
     *
     * @property version The version value.
     */
    data class Ready(val version: Int) : BrowserMigrationEvent()
}

/** Safe failures for browser durability and worker capabilities. Payload bytes are never included. */
sealed class BrowserCapabilityError(message: String) : Exception(message) {
    /**
     * [capability] is unavailable on the active platform or browser.
     *
     * @property capability The capability value.
     */
    class Unavailable(val capability: String) : BrowserCapabilityError("Browser capability unavailable: $capability")
    /** An opaque record or correlation key failed validation. */
    class InvalidKey : BrowserCapabilityError("Invalid opaque record key")
    /**
     * A record exceeded [limitBytes].
     *
     * @property limitBytes The limit bytes value.
     */
    class RecordTooLarge(val limitBytes: Int) : BrowserCapabilityError("Record exceeds configured byte limit")
    /**
     * A transaction exceeded [limitBytes].
     *
     * @property limitBytes The limit bytes value.
     */
    class TransactionTooLarge(val limitBytes: Int) : BrowserCapabilityError("Transaction exceeds configured byte limit")
    /**
     * The worker reached its [limit] of pending requests.
     *
     * @property limit The limit value.
     */
    class TooManyRequests(val limit: Int) : BrowserCapabilityError("Worker pending request limit reached")
    /** Browser storage quota was exhausted. */
    class QuotaExceeded : BrowserCapabilityError("Browser storage quota exceeded")
    /** The browser evicted or otherwise lost durable data. */
    class DataEvicted : BrowserCapabilityError("Browser durable data is unavailable")
    /** Another browser context prevented an IndexedDB upgrade. */
    class UpgradeBlocked : BrowserCapabilityError("Browser database upgrade blocked")
    /** The opened database does not match the configured schema version. */
    class VersionMismatch : BrowserCapabilityError("Browser database version mismatch")
    /** The owning capability was closed before the operation completed. */
    class Closed : BrowserCapabilityError("Browser capability is closed")
    /** The caller or owner aborted the operation. */
    class Aborted : BrowserCapabilityError("Browser operation aborted")
    /** A worker or storage peer violated the bounded wire protocol. */
    class ProtocolError : BrowserCapabilityError("Browser capability protocol error")
    /** The browser API failed without exposing private implementation details. */
    class OperationFailed : BrowserCapabilityError("Browser capability operation failed")
}

/** Stages opaque record mutations and commits them in one IndexedDB transaction. */
interface BrowserRecordTransaction {
    /** Stages [value] under opaque [key]; bytes are copied by the platform implementation. */
    fun put(key: String, value: ByteArray)
    /** Stages deletion of opaque [key]. */
    fun delete(key: String)
    /** Atomically commits staged mutations, or throws [BrowserCapabilityError]. */
    suspend fun commit()
    /** Aborts staged mutations. Repeated calls have no effect. */
    fun abort()
}

/** Browser-only durable binary storage. It does not encrypt or interpret records. */
interface BrowserRecordStore {
    /** Opens the store and emits migration events to its configured callback. */
    suspend fun open()
    /** Reads [key], rejecting a result larger than [maxBytes], or returns `null` when absent. */
    suspend fun read(key: String, maxBytes: Int = Int.MAX_VALUE): ByteArray?
    /** Creates one caller-owned transaction. */
    fun beginTransaction(): BrowserRecordTransaction
    /** Releases the database connection and rejects subsequent operations. */
    fun close()
}

/**
 * Creates a real browser IndexedDB store.
 *
 * JVM targets return an owner whose [BrowserRecordStore.open] reports
 * [BrowserCapabilityError.Unavailable]. The caller must invoke [BrowserRecordStore.close].
 *
 * @param config Validated database names, schema version, and byte limits
 * @param onMigration Synchronous lifecycle observer; it never receives record bytes
 */
expect fun createBrowserRecordStore(
    config: BrowserRecordStoreConfig,
    onMigration: (BrowserMigrationEvent) -> Unit = {}
): BrowserRecordStore

/** Fixed cross-tab event kinds; arbitrary application event names are unsupported. */
enum class OpaqueTabEventType { /** The lock opaque tab event type option. */
                                LOCK,
                                /** The invalidate opaque tab event type option. */
                                INVALIDATE,
                                /** The logout opaque tab event type option. */
                                LOGOUT,
                                /** The upgrade opaque tab event type option. */
                                UPGRADE }

/**
 * Bounded cross-tab event carrying only a validated opaque identifier.
 *
 * @property type Fixed protocol event kind
 * @property opaqueId Non-sensitive ASCII identifier, at most 128 characters
 */
data class OpaqueTabEvent(val type: OpaqueTabEventType, val opaqueId: String) {
    init {
        require(opaqueId.isValidOpaqueId()) { "Invalid opaque event identifier" }
    }
}

/**
 * A bounded, fixed-protocol cross-tab channel.
 *
 * Arbitrary application payloads are intentionally unsupported. Call [close] when its owning
 * browser scope is disposed.
 */
class OpaqueTabChannel(channelId: String) {
    private val channel: SummonBroadcastChannel<String>
    private var closed = false

    init {
        require(channelId.isValidCapabilityName()) { "Invalid channel identifier" }
        channel = createBroadcastChannel("summon-opaque-$channelId")
    }

    /** Publishes [event] to other same-origin contexts. */
    fun post(event: OpaqueTabEvent) {
        check(!closed) { "Opaque tab channel is closed" }
        channel.postMessage("${event.type.name}:${event.opaqueId}")
    }

    /** Registers [handler] and returns an idempotent listener-removal callback. */
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

    /** Detaches the native channel and listeners. Repeated calls have no effect. */
    fun close() {
        if (closed) return
        closed = true
        channel.close()
    }
}

/** Same-origin browser worker request channel for bounded opaque binary messages. */
interface BrowserWorker : AutoCloseable {
    /** Sends one bounded [payload] under [correlationId] and awaits its copied response bytes. */
    suspend fun request(correlationId: String, payload: ByteArray): ByteArray
    /** Cancels the pending request identified by [correlationId]. */
    fun cancel(correlationId: String)
    /** Terminates the worker and fails all pending requests. */
    override fun close()
}

/**
 * Creates a caller-owned browser worker.
 *
 * JVM targets return an owner whose requests report [BrowserCapabilityError.Unavailable].
 *
 * @param scriptPath Root-relative same-origin worker script path without query or fragment
 * @param maxMessageBytes Positive request and response byte limit
 * @param maxPendingRequests Positive bound on concurrent requests
 * @throws IllegalArgumentException when the path or a bound is invalid
 */
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

private fun Char.isAsciiLetterOrDigit(): Boolean =
    this in 'a'..'z' || this in 'A'..'Z' || this in '0'..'9'

private fun String.isValidCapabilityName(): Boolean =
    length in 1..64 && all { it.isAsciiLetterOrDigit() || it == '-' || it == '_' }

internal fun String.isValidOpaqueId(): Boolean =
    length in 1..128 && all { it.isAsciiLetterOrDigit() || it == '-' || it == '_' || it == '.' }

private const val MAX_OPAQUE_MESSAGE_CHARS = 144
