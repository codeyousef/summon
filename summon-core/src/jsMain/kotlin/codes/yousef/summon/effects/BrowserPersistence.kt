package codes.yousef.summon.effects

import kotlinx.browser.window
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import org.khronos.webgl.Uint8Array
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual fun createBrowserRecordStore(
    config: BrowserRecordStoreConfig,
    onMigration: (BrowserMigrationEvent) -> Unit
): BrowserRecordStore = JsBrowserRecordStore(config, onMigration)

private class JsBrowserRecordStore(
    private val config: BrowserRecordStoreConfig,
    private val onMigration: (BrowserMigrationEvent) -> Unit
) : BrowserRecordStore {
    private var database: dynamic = null
    private var closed = false
    private val activeTransactions = mutableListOf<Any>()

    override suspend fun open() {
        if (closed) throw BrowserCapabilityError.Closed()
        if (database != null) return
        val factory = window.asDynamic().indexedDB
        if (factory == null) throw BrowserCapabilityError.Unavailable("indexed-db")
        suspendCancellableCoroutine<Unit> { continuation ->
            val request = try {
                factory.open(config.databaseName, config.schemaVersion)
            } catch (_: Throwable) {
                continuation.resumeWithException(BrowserCapabilityError.OperationFailed())
                return@suspendCancellableCoroutine
            }
            var settled = false
            fun fail(error: Throwable) {
                if (settled || !continuation.isActive) return
                settled = true
                continuation.resumeWithException(error)
            }
            request.onupgradeneeded = { event: dynamic ->
                val db = request.result
                val oldVersion = (event.oldVersion as Number).toInt()
                onMigration(BrowserMigrationEvent.Upgrade(oldVersion, config.schemaVersion))
                if (!db.objectStoreNames.contains(config.storeName)) db.createObjectStore(config.storeName)
            }
            request.onblocked = { event: dynamic ->
                val oldVersion = (event.oldVersion as Number).toInt()
                onMigration(BrowserMigrationEvent.Blocked(oldVersion, config.schemaVersion))
                fail(BrowserCapabilityError.UpgradeBlocked())
            }
            request.onerror = { _: dynamic -> fail(mapBrowserError(request.error)) }
            request.onsuccess = { _: dynamic ->
                if (!settled && continuation.isActive) {
                    settled = true
                    database = request.result
                    database.onversionchange = { _: dynamic -> close() }
                    onMigration(BrowserMigrationEvent.Ready(config.schemaVersion))
                    continuation.resume(Unit)
                } else {
                    request.result?.close()
                }
            }
            continuation.invokeOnCancellation { request.result?.close() }
        }
    }

    override suspend fun read(key: String, maxBytes: Int): ByteArray? {
        validateOpaqueKey(key)
        require(maxBytes > 0) { "Read byte limit must be positive" }
        val db = requireOpenDatabase()
        return suspendCancellableCoroutine<ByteArray?> { continuation ->
            val transaction = try {
                db.transaction(config.storeName, "readonly")
            } catch (error: Throwable) {
                continuation.resumeWithException(mapBrowserError(error.asDynamic()))
                return@suspendCancellableCoroutine
            }
            activeTransactions.add(transaction.unsafeCast<Any>())
            val request = transaction.objectStore(config.storeName).get(key)
            var value: ByteArray? = null
            request.onsuccess = { _: dynamic ->
                val result = request.result
                if (result != null) {
                    val bytes = result.unsafeCast<Uint8Array>()
                    val effectiveLimit = minOf(maxBytes, config.maxRecordBytes)
                    if (bytes.length > effectiveLimit) {
                        try { transaction.abort() } catch (_: Throwable) { }
                        finishException(continuation, transaction, BrowserCapabilityError.RecordTooLarge(effectiveLimit))
                    } else {
                        value = bytes.toKotlinByteArray()
                    }
                }
            }
            request.onerror = { _: dynamic ->
                finishException(continuation, transaction, mapBrowserError(request.error))
            }
            transaction.oncomplete = { _: dynamic ->
                activeTransactions.remove(transaction.unsafeCast<Any>())
                if (continuation.isActive) continuation.resume(value)
            }
            transaction.onerror = { _: dynamic ->
                finishException(continuation, transaction, mapBrowserError(transaction.error))
            }
            transaction.onabort = { _: dynamic ->
                finishException(continuation, transaction, BrowserCapabilityError.Aborted())
            }
            continuation.invokeOnCancellation { try { transaction.abort() } catch (_: Throwable) { } }
        }
    }

    override fun beginTransaction(): BrowserRecordTransaction {
        requireOpenDatabase()
        return JsBrowserRecordTransaction(this, config)
    }

    internal suspend fun commit(mutations: List<RecordMutation>) {
        val db = requireOpenDatabase()
        suspendCancellableCoroutine<Unit> { continuation ->
            val transaction = try {
                db.transaction(config.storeName, "readwrite")
            } catch (error: Throwable) {
                continuation.resumeWithException(mapBrowserError(error.asDynamic()))
                return@suspendCancellableCoroutine
            }
            activeTransactions.add(transaction.unsafeCast<Any>())
            val store = transaction.objectStore(config.storeName)
            try {
                mutations.forEach { mutation ->
                    when (mutation) {
                        is RecordMutation.Put -> store.put(mutation.value.toUint8Array(), mutation.key)
                        is RecordMutation.Delete -> store.delete(mutation.key)
                    }
                }
            } catch (error: Throwable) {
                try { transaction.abort() } catch (_: Throwable) { }
                finishException(continuation, transaction, mapBrowserError(error.asDynamic()))
                return@suspendCancellableCoroutine
            }
            transaction.oncomplete = { _: dynamic ->
                activeTransactions.remove(transaction.unsafeCast<Any>())
                if (continuation.isActive) continuation.resume(Unit)
            }
            transaction.onerror = { _: dynamic ->
                finishException(continuation, transaction, mapBrowserError(transaction.error))
            }
            transaction.onabort = { _: dynamic ->
                finishException(continuation, transaction, BrowserCapabilityError.Aborted())
            }
            continuation.invokeOnCancellation { try { transaction.abort() } catch (_: Throwable) { } }
        }
    }

    override fun close() {
        if (closed) return
        closed = true
        activeTransactions.toList().forEach { transaction ->
            try { transaction.asDynamic().abort() } catch (_: Throwable) { }
        }
        activeTransactions.clear()
        database?.close()
        database = null
    }

    private fun requireOpenDatabase(): dynamic {
        if (closed) throw BrowserCapabilityError.Closed()
        return database ?: throw BrowserCapabilityError.Unavailable("indexed-db-not-open")
    }

    private fun <T> finishException(
        continuation: CancellableContinuation<T>,
        transaction: dynamic,
        error: Throwable
    ) {
        activeTransactions.remove(transaction.unsafeCast<Any>())
        if (continuation.isActive) continuation.resumeWithException(error)
    }
}

private sealed class RecordMutation {
    data class Put(val key: String, val value: ByteArray) : RecordMutation()
    data class Delete(val key: String) : RecordMutation()
}

private class JsBrowserRecordTransaction(
    private val owner: JsBrowserRecordStore,
    private val config: BrowserRecordStoreConfig
) : BrowserRecordTransaction {
    private val mutations = mutableListOf<RecordMutation>()
    private var totalBytes = 0
    private var finished = false

    override fun put(key: String, value: ByteArray) {
        checkActive()
        validateOpaqueKey(key)
        if (value.size > config.maxRecordBytes) throw BrowserCapabilityError.RecordTooLarge(config.maxRecordBytes)
        if (value.size > config.maxTransactionBytes - totalBytes) {
            throw BrowserCapabilityError.TransactionTooLarge(config.maxTransactionBytes)
        }
        totalBytes += value.size
        mutations += RecordMutation.Put(key, value.copyOf())
    }

    override fun delete(key: String) {
        checkActive()
        validateOpaqueKey(key)
        mutations += RecordMutation.Delete(key)
    }

    override suspend fun commit() {
        checkActive()
        finished = true
        owner.commit(mutations)
        mutations.clear()
    }

    override fun abort() {
        if (finished) return
        finished = true
        mutations.clear()
    }

    private fun checkActive() {
        if (finished) throw BrowserCapabilityError.Closed()
    }
}

private external class Worker(scriptPath: String, options: dynamic) {
    var onmessage: dynamic
    var onerror: dynamic
    var onmessageerror: dynamic
    fun postMessage(message: dynamic)
    fun terminate()
}

actual fun createBrowserWorker(
    scriptPath: String,
    maxMessageBytes: Int,
    maxPendingRequests: Int
): BrowserWorker {
    validateWorkerConfig(scriptPath, maxMessageBytes, maxPendingRequests)
    if (js("typeof Worker === 'undefined'") as Boolean) throw BrowserCapabilityError.Unavailable("web-worker")
    return JsBrowserWorker(scriptPath, maxMessageBytes, maxPendingRequests)
}

private class JsBrowserWorker(
    scriptPath: String,
    private val maxMessageBytes: Int,
    private val maxPendingRequests: Int
) : BrowserWorker {
    private val worker = Worker(scriptPath, js("({ type: 'module' })"))
    private val pending = mutableMapOf<String, CancellableContinuation<ByteArray>>()
    private var closed = false

    init {
        worker.onmessage = { event: dynamic -> receive(event.data) }
        worker.onerror = { _: dynamic -> terminateWith(BrowserCapabilityError.OperationFailed()) }
        worker.onmessageerror = { _: dynamic -> terminateWith(BrowserCapabilityError.ProtocolError()) }
    }

    override suspend fun request(correlationId: String, payload: ByteArray): ByteArray {
        if (closed) throw BrowserCapabilityError.Closed()
        if (!correlationId.isValidOpaqueId() || correlationId in pending) throw BrowserCapabilityError.ProtocolError()
        if (payload.size > maxMessageBytes) throw BrowserCapabilityError.RecordTooLarge(maxMessageBytes)
        if (pending.size >= maxPendingRequests) throw BrowserCapabilityError.TooManyRequests(maxPendingRequests)
        return suspendCancellableCoroutine { continuation ->
            pending[correlationId] = continuation
            val envelope = js("({})")
            envelope.kind = "request"
            envelope.id = correlationId
            envelope.payload = payload.toUint8Array()
            try {
                worker.postMessage(envelope)
            } catch (_: Throwable) {
                pending.remove(correlationId)
                continuation.resumeWithException(BrowserCapabilityError.OperationFailed())
                return@suspendCancellableCoroutine
            }
            continuation.invokeOnCancellation {
                pending.remove(correlationId)
                postControl("cancel", correlationId)
            }
        }
    }

    override fun cancel(correlationId: String) {
        if (!correlationId.isValidOpaqueId()) return
        pending.remove(correlationId)?.resumeWithException(BrowserCapabilityError.Aborted())
        if (!closed) postControl("cancel", correlationId)
    }

    override fun close() {
        if (closed) return
        closed = true
        worker.terminate()
        terminateWith(BrowserCapabilityError.Closed())
    }

    private fun receive(message: dynamic) {
        if (closed || message == null || message.kind != "response") return
        val id = message.id as? String ?: return
        if (!id.isValidOpaqueId()) return
        val continuation = pending.remove(id) ?: return
        try {
            val bytes = message.payload.unsafeCast<Uint8Array>()
            if (bytes.length > maxMessageBytes) continuation.resumeWithException(BrowserCapabilityError.RecordTooLarge(maxMessageBytes))
            else continuation.resume(bytes.toKotlinByteArray())
        } catch (_: Throwable) {
            continuation.resumeWithException(BrowserCapabilityError.ProtocolError())
        }
    }

    private fun postControl(kind: String, id: String) {
        val envelope = js("({})")
        envelope.kind = kind
        envelope.id = id
        try { worker.postMessage(envelope) } catch (_: Throwable) { }
    }

    private fun terminateWith(error: Throwable) {
        val continuations = pending.values.toList()
        pending.clear()
        continuations.forEach { if (it.isActive) it.resumeWithException(error) }
    }
}

private fun mapBrowserError(error: dynamic): Throwable = when (error?.name as? String) {
    "QuotaExceededError" -> BrowserCapabilityError.QuotaExceeded()
    "VersionError" -> BrowserCapabilityError.VersionMismatch()
    "AbortError" -> BrowserCapabilityError.Aborted()
    "NotFoundError", "InvalidStateError" -> BrowserCapabilityError.DataEvicted()
    else -> BrowserCapabilityError.OperationFailed()
}

private fun ByteArray.toUint8Array(): Uint8Array {
    val result = Uint8Array(size)
    for (index in indices) result.asDynamic()[index] = this[index]
    return result
}

private fun Uint8Array.toKotlinByteArray(): ByteArray {
    val result = ByteArray(length)
    for (index in result.indices) result[index] = this.asDynamic()[index]
    return result
}
