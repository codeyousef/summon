package codes.yousef.summon.effects

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@JsFun("(size) => new Uint8Array(size)")
private external fun persistenceBytes(size: Int): JsAny

@JsFun("(bytes, index, value) => { bytes[index] = value; }")
private external fun persistenceSetByte(bytes: JsAny, index: Int, value: Int)

@JsFun("(bytes) => bytes.length")
private external fun persistenceByteLength(bytes: JsAny): Int

@JsFun("(bytes, index) => bytes[index]")
private external fun persistenceByteAt(bytes: JsAny, index: Int): Int

@JsFun(
    """(name, storeName, version, onUpgrade, onBlocked, onReady, onFailure) => {
        if (typeof indexedDB === 'undefined') { onFailure('unavailable'); return null; }
        let request;
        try { request = indexedDB.open(name, version); }
        catch (error) { onFailure(error && error.name ? error.name : 'operation'); return null; }
        let settled = false;
        request.onupgradeneeded = event => {
            onUpgrade(event.oldVersion | 0);
            if (!request.result.objectStoreNames.contains(storeName)) request.result.createObjectStore(storeName);
        };
        request.onblocked = event => {
            onBlocked(event.oldVersion | 0);
            if (!settled) { settled = true; onFailure('blocked'); }
        };
        request.onerror = () => {
            if (!settled) { settled = true; onFailure(request.error ? request.error.name : 'operation'); }
        };
        request.onsuccess = () => {
            if (settled) { request.result.close(); return; }
            settled = true;
            request.result.onversionchange = () => request.result.close();
            onReady(request.result);
        };
        return { cancel: () => { settled = true; if (request.result) request.result.close(); } };
    }"""
)
private external fun wasmOpenRecordStore(
    name: String,
    storeName: String,
    version: Int,
    onUpgrade: (Int) -> Unit,
    onBlocked: (Int) -> Unit,
    onReady: (JsAny) -> Unit,
    onFailure: (String) -> Unit
): JsAny?

@JsFun("(handle) => { if (handle) handle.cancel(); }")
private external fun wasmCancelPersistence(handle: JsAny?)

@JsFun("(database) => database.close()")
private external fun wasmCloseDatabase(database: JsAny)

@JsFun(
    """(database, storeName, key, limit, onSuccess, onFailure) => {
        let transaction;
        try {
            transaction = database.transaction(storeName, 'readonly');
            const request = transaction.objectStore(storeName).get(key);
            let result = null;
            request.onsuccess = () => {
                if (request.result === undefined) return;
                const bytes = new Uint8Array(request.result);
                if (bytes.length > limit) { transaction.abort(); onFailure('too-large'); }
                else result = bytes;
            };
            request.onerror = () => onFailure(request.error ? request.error.name : 'operation');
            transaction.oncomplete = () => onSuccess(result);
            transaction.onerror = () => onFailure(transaction.error ? transaction.error.name : 'operation');
            transaction.onabort = () => onFailure('AbortError');
        } catch (error) { onFailure(error && error.name ? error.name : 'operation'); }
        return { cancel: () => { if (transaction) { try { transaction.abort(); } catch (_) {} } } };
    }"""
)
private external fun wasmReadRecord(
    database: JsAny,
    storeName: String,
    key: String,
    limit: Int,
    onSuccess: (JsAny?) -> Unit,
    onFailure: (String) -> Unit
): JsAny

@JsFun("() => ({ mutations: [], transaction: null, finished: false })")
private external fun wasmNewRecordTransaction(): JsAny

@JsFun("(transaction, key, bytes) => transaction.mutations.push({ kind: 'put', key, bytes })")
private external fun wasmStagePut(transaction: JsAny, key: String, bytes: JsAny)

@JsFun("(transaction, key) => transaction.mutations.push({ kind: 'delete', key })")
private external fun wasmStageDelete(transaction: JsAny, key: String)

@JsFun("(transaction) => { transaction.finished = true; transaction.mutations.length = 0; if (transaction.transaction) { try { transaction.transaction.abort(); } catch (_) {} } }")
private external fun wasmAbortRecordTransaction(transaction: JsAny)

@JsFun(
    """(database, storeName, handle, onSuccess, onFailure) => {
        if (handle.finished) { onFailure('closed'); return; }
        handle.finished = true;
        try {
            const transaction = database.transaction(storeName, 'readwrite');
            handle.transaction = transaction;
            const store = transaction.objectStore(storeName);
            for (const mutation of handle.mutations) {
                if (mutation.kind === 'put') store.put(mutation.bytes, mutation.key);
                else store.delete(mutation.key);
            }
            transaction.oncomplete = () => { handle.mutations.length = 0; onSuccess(); };
            transaction.onerror = () => onFailure(transaction.error ? transaction.error.name : 'operation');
            transaction.onabort = () => onFailure('AbortError');
        } catch (error) { onFailure(error && error.name ? error.name : 'operation'); }
    }"""
)
private external fun wasmCommitRecordTransaction(
    database: JsAny,
    storeName: String,
    handle: JsAny,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
)

actual fun createBrowserRecordStore(
    config: BrowserRecordStoreConfig,
    onMigration: (BrowserMigrationEvent) -> Unit
): BrowserRecordStore = WasmBrowserRecordStore(config, onMigration)

private class WasmBrowserRecordStore(
    private val config: BrowserRecordStoreConfig,
    private val onMigration: (BrowserMigrationEvent) -> Unit
) : BrowserRecordStore {
    private var database: JsAny? = null
    private var closed = false
    private val transactions = mutableSetOf<WasmBrowserRecordTransaction>()

    override suspend fun open() {
        if (closed) throw BrowserCapabilityError.Closed()
        if (database != null) return
        suspendCancellableCoroutine<Unit> { continuation ->
            var completed = false
            val request = wasmOpenRecordStore(
                config.databaseName,
                config.storeName,
                config.schemaVersion,
                { old -> onMigration(BrowserMigrationEvent.Upgrade(old, config.schemaVersion)) },
                { old -> onMigration(BrowserMigrationEvent.Blocked(old, config.schemaVersion)) },
                { opened ->
                    if (!completed && continuation.isActive) {
                        completed = true
                        database = opened
                        onMigration(BrowserMigrationEvent.Ready(config.schemaVersion))
                        continuation.resume(Unit)
                    } else wasmCloseDatabase(opened)
                },
                { kind ->
                    if (!completed && continuation.isActive) {
                        completed = true
                        continuation.resumeWithException(mapWasmCapabilityError(kind))
                    }
                }
            )
            continuation.invokeOnCancellation {
                completed = true
                wasmCancelPersistence(request)
            }
        }
    }

    override suspend fun read(key: String, maxBytes: Int): ByteArray? {
        validateOpaqueKey(key)
        require(maxBytes > 0) { "Read byte limit must be positive" }
        val db = requireDatabase()
        return suspendCancellableCoroutine { continuation ->
            var completed = false
            val request = wasmReadRecord(
                db,
                config.storeName,
                key,
                minOf(maxBytes, config.maxRecordBytes),
                { bytes ->
                    if (!completed && continuation.isActive) {
                        completed = true
                        continuation.resume(bytes?.toByteArray())
                    }
                },
                { kind ->
                    if (!completed && continuation.isActive) {
                        completed = true
                        continuation.resumeWithException(mapWasmCapabilityError(kind, minOf(maxBytes, config.maxRecordBytes)))
                    }
                }
            )
            continuation.invokeOnCancellation {
                completed = true
                wasmCancelPersistence(request)
            }
        }
    }

    override fun beginTransaction(): BrowserRecordTransaction {
        requireDatabase()
        return WasmBrowserRecordTransaction(this, config).also { transactions += it }
    }

    internal suspend fun commit(transaction: WasmBrowserRecordTransaction, handle: JsAny) {
        val db = requireDatabase()
        suspendCancellableCoroutine<Unit> { continuation ->
            var completed = false
            wasmCommitRecordTransaction(
                db,
                config.storeName,
                handle,
                {
                    transactions -= transaction
                    if (!completed && continuation.isActive) {
                        completed = true
                        continuation.resume(Unit)
                    }
                },
                { kind ->
                    transactions -= transaction
                    if (!completed && continuation.isActive) {
                        completed = true
                        continuation.resumeWithException(mapWasmCapabilityError(kind))
                    }
                }
            )
            continuation.invokeOnCancellation {
                completed = true
                wasmAbortRecordTransaction(handle)
                transactions -= transaction
            }
        }
    }

    internal fun forget(transaction: WasmBrowserRecordTransaction) {
        transactions -= transaction
    }

    override fun close() {
        if (closed) return
        closed = true
        transactions.toList().forEach { it.abort() }
        transactions.clear()
        database?.let(::wasmCloseDatabase)
        database = null
    }

    private fun requireDatabase(): JsAny {
        if (closed) throw BrowserCapabilityError.Closed()
        return database ?: throw BrowserCapabilityError.Unavailable("indexed-db-not-open")
    }
}

private class WasmBrowserRecordTransaction(
    private val owner: WasmBrowserRecordStore,
    private val config: BrowserRecordStoreConfig
) : BrowserRecordTransaction {
    private val handle = wasmNewRecordTransaction()
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
        wasmStagePut(handle, key, value.toJsBytes())
    }

    override fun delete(key: String) {
        checkActive()
        validateOpaqueKey(key)
        wasmStageDelete(handle, key)
    }

    override suspend fun commit() {
        checkActive()
        finished = true
        owner.commit(this, handle)
    }

    override fun abort() {
        if (finished) return
        finished = true
        wasmAbortRecordTransaction(handle)
        owner.forget(this)
    }

    private fun checkActive() {
        if (finished) throw BrowserCapabilityError.Closed()
    }
}

@JsFun(
    """(scriptPath, onResponse, onFailure) => {
        if (typeof Worker === 'undefined') return null;
        let worker;
        try { worker = new Worker(scriptPath, { type: 'module' }); }
        catch (_) { return null; }
        worker.onmessage = event => {
            const message = event.data;
            if (!message || message.kind !== 'response' || typeof message.id !== 'string') return;
            try { onResponse(message.id, new Uint8Array(message.payload)); }
            catch (_) { onFailure('protocol'); }
        };
        worker.onerror = () => onFailure('operation');
        worker.onmessageerror = () => onFailure('protocol');
        return worker;
    }"""
)
private external fun wasmCreateWorker(
    scriptPath: String,
    onResponse: (String, JsAny) -> Unit,
    onFailure: (String) -> Unit
): JsAny?

@JsFun("(worker, kind, id, payload) => worker.postMessage({ kind, id, payload })")
private external fun wasmPostWorker(worker: JsAny, kind: String, id: String, payload: JsAny)

@JsFun("(worker) => worker.terminate()")
private external fun wasmTerminateWorker(worker: JsAny)

actual fun createBrowserWorker(
    scriptPath: String,
    maxMessageBytes: Int,
    maxPendingRequests: Int
): BrowserWorker {
    validateWorkerConfig(scriptPath, maxMessageBytes, maxPendingRequests)
    return WasmBrowserWorker(scriptPath, maxMessageBytes, maxPendingRequests)
}

private class WasmBrowserWorker(
    scriptPath: String,
    private val maxMessageBytes: Int,
    private val maxPendingRequests: Int
) : BrowserWorker {
    private val pending = mutableMapOf<String, CancellableContinuation<ByteArray>>()
    private var closed = false
    private val worker: JsAny = wasmCreateWorker(
        scriptPath,
        { id, bytes -> receive(id, bytes) },
        { kind -> fail(mapWasmCapabilityError(kind)) }
    ) ?: throw BrowserCapabilityError.Unavailable("web-worker")

    override suspend fun request(correlationId: String, payload: ByteArray): ByteArray {
        if (closed) throw BrowserCapabilityError.Closed()
        if (!correlationId.isValidOpaqueId() || correlationId in pending) throw BrowserCapabilityError.ProtocolError()
        if (payload.size > maxMessageBytes) throw BrowserCapabilityError.RecordTooLarge(maxMessageBytes)
        if (pending.size >= maxPendingRequests) throw BrowserCapabilityError.TooManyRequests(maxPendingRequests)
        return suspendCancellableCoroutine { continuation ->
            pending[correlationId] = continuation
            try {
                wasmPostWorker(worker, "request", correlationId, payload.toJsBytes())
            } catch (_: Throwable) {
                pending.remove(correlationId)
                continuation.resumeWithException(BrowserCapabilityError.OperationFailed())
                return@suspendCancellableCoroutine
            }
            continuation.invokeOnCancellation {
                pending.remove(correlationId)
                postCancel(correlationId)
            }
        }
    }

    override fun cancel(correlationId: String) {
        if (!correlationId.isValidOpaqueId()) return
        pending.remove(correlationId)?.let { if (it.isActive) it.resumeWithException(BrowserCapabilityError.Aborted()) }
        if (!closed) postCancel(correlationId)
    }

    override fun close() {
        if (closed) return
        closed = true
        wasmTerminateWorker(worker)
        terminateWith(BrowserCapabilityError.Closed())
    }

    private fun receive(id: String, bytes: JsAny) {
        if (closed || !id.isValidOpaqueId()) return
        val continuation = pending.remove(id) ?: return
        if (persistenceByteLength(bytes) > maxMessageBytes) {
            continuation.resumeWithException(BrowserCapabilityError.RecordTooLarge(maxMessageBytes))
        } else continuation.resume(bytes.toByteArray())
    }

    private fun postCancel(id: String) {
        try { wasmPostWorker(worker, "cancel", id, persistenceBytes(0)) } catch (_: Throwable) { }
    }

    private fun fail(error: Throwable) {
        if (closed) return
        closed = true
        wasmTerminateWorker(worker)
        terminateWith(error)
    }

    private fun terminateWith(error: Throwable) {
        val continuations = pending.values.toList()
        pending.clear()
        continuations.forEach { if (it.isActive) it.resumeWithException(error) }
    }
}

private fun mapWasmCapabilityError(kind: String, limit: Int = 0): Throwable = when (kind) {
    "unavailable" -> BrowserCapabilityError.Unavailable("browser-capability")
    "blocked" -> BrowserCapabilityError.UpgradeBlocked()
    "QuotaExceededError" -> BrowserCapabilityError.QuotaExceeded()
    "VersionError" -> BrowserCapabilityError.VersionMismatch()
    "AbortError" -> BrowserCapabilityError.Aborted()
    "NotFoundError", "InvalidStateError" -> BrowserCapabilityError.DataEvicted()
    "too-large" -> BrowserCapabilityError.RecordTooLarge(limit)
    "closed" -> BrowserCapabilityError.Closed()
    "protocol" -> BrowserCapabilityError.ProtocolError()
    else -> BrowserCapabilityError.OperationFailed()
}

private fun ByteArray.toJsBytes(): JsAny {
    val bytes = persistenceBytes(size)
    for (index in indices) persistenceSetByte(bytes, index, this[index].toInt() and 0xff)
    return bytes
}

private fun JsAny.toByteArray(): ByteArray {
    val result = ByteArray(persistenceByteLength(this))
    for (index in result.indices) result[index] = persistenceByteAt(this, index).toByte()
    return result
}
