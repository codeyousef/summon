package codes.yousef.summon.fixture

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.effects.BrowserCapabilityError
import codes.yousef.summon.effects.BrowserMigrationEvent
import codes.yousef.summon.effects.BrowserRecordStore
import codes.yousef.summon.effects.BrowserRecordStoreConfig
import codes.yousef.summon.effects.BrowserWorker
import codes.yousef.summon.effects.OpaqueTabChannel
import codes.yousef.summon.effects.OpaqueTabEvent
import codes.yousef.summon.effects.OpaqueTabEventType
import codes.yousef.summon.effects.createBrowserRecordStore
import codes.yousef.summon.effects.createBrowserWorker
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.DisposableEffect
import codes.yousef.summon.state.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/** Synthetic opaque bytes only. Encryption and key management remain suite-owned. */
class PersistenceFixture {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val status = mutableStateOf("idle")
    private val migrations = mutableStateOf("none")
    private val receiver = OpaqueTabChannel("suite-fixture")
    private val publisher = OpaqueTabChannel("suite-fixture")
    private var store: BrowserRecordStore? = null
    private var worker: BrowserWorker? = null
    private var generation = 0
    private var disposed = false

    init {
        receiver.onEvent { event ->
            if (event.type == OpaqueTabEventType.LOGOUT) lock(event.opaqueId)
            else if (!disposed) status.value = "event:${event.type.name.lowercase()}"
        }
    }

    private fun runPersistence() {
        status.value = "running"
        scope.launch {
            try {
                closeCapabilities()
                val opened = newStore(version = 1)
                opened.open()
                val transaction = opened.beginTransaction()
                transaction.put("record-a", byteArrayOf(0x91.toByte(), 0x02, 0xa7.toByte(), 0x44))
                transaction.put("operation-a", byteArrayOf(0xc2.toByte(), 0x33, 0x17))
                transaction.put("cursor-a", byteArrayOf(0xe5.toByte(), 0x61, 0x08))
                transaction.commit()

                val interrupted = opened.beginTransaction()
                interrupted.put("partial-a", byteArrayOf(0xff.toByte(), 0x55))
                opened.close()
                interrupted.abort()

                val reopened = newStore(version = 1)
                reopened.open()
                val record = reopened.read("record-a", 8)
                val partial = reopened.read("partial-a", 8)
                val bounded = try {
                    reopened.beginTransaction().put("oversize-a", ByteArray(65))
                    false
                } catch (_: BrowserCapabilityError.RecordTooLarge) {
                    true
                }
                val activeWorker = createBrowserWorker("/suite-worker.js", maxMessageBytes = 64, maxPendingRequests = 2)
                worker = activeWorker
                val response = activeWorker.request("probe-1", byteArrayOf(1, 2, 3))
                store = reopened
                if (!disposed) {
                    status.value = "ready:atomic=${record?.size == 4 && partial == null};bounded=$bounded;worker=${response.contentEquals(byteArrayOf(3, 2, 1))}"
                }
            } catch (_: Throwable) {
                if (!disposed) status.value = "capability-error"
            }
        }
    }

    private fun probeVisibleFailures() {
        val activeStore = store ?: return
        scope.launch {
            val quotaVisible = try {
                val transaction = activeStore.beginTransaction()
                transaction.put("quota-a", byteArrayOf(1))
                transaction.commit()
                false
            } catch (_: BrowserCapabilityError.QuotaExceeded) {
                true
            }
            val evictionVisible = try {
                activeStore.read("record-a", 8)
                false
            } catch (_: BrowserCapabilityError.DataEvicted) {
                true
            }
            if (!disposed) status.value = "errors:quota=$quotaVisible;evicted=$evictionVisible"
        }
    }

    private fun upgradeSchema() {
        scope.launch {
            try {
                store?.close()
                val upgraded = newStore(version = 2)
                upgraded.open()
                store = upgraded
                if (!disposed) status.value = "upgraded"
            } catch (_: BrowserCapabilityError.UpgradeBlocked) {
                if (!disposed) status.value = "upgrade-blocked"
            } catch (_: Throwable) {
                if (!disposed) status.value = "capability-error"
            }
        }
    }

    private fun startLateWorker() {
        val activeWorker = worker ?: return
        val expectedGeneration = generation
        status.value = "worker-pending"
        scope.launch {
            try {
                activeWorker.request("late-1", byteArrayOf(0xff.toByte(), 1, 2, 3))
                if (!disposed && generation == expectedGeneration) status.value = "stale-worker-response"
            } catch (_: BrowserCapabilityError) {
                if (!disposed && generation == expectedGeneration) status.value = "worker-cancelled"
            }
        }
    }

    private fun publishLogout() {
        publisher.post(OpaqueTabEvent(OpaqueTabEventType.LOGOUT, "lock-${generation + 1}"))
    }

    private fun lock(opaqueId: String) {
        generation++
        closeCapabilities()
        if (!disposed) status.value = "locked:$opaqueId"
    }

    private fun newStore(version: Int): BrowserRecordStore = createBrowserRecordStore(
        BrowserRecordStoreConfig(
            databaseName = "summon-suite-fixture",
            storeName = "opaque-records",
            schemaVersion = version,
            maxRecordBytes = 64,
            maxTransactionBytes = 192
        )
    ) { event ->
        if (!disposed) migrations.value = when (event) {
            is BrowserMigrationEvent.Upgrade -> "upgrade:${event.oldVersion}->${event.newVersion}"
            is BrowserMigrationEvent.Blocked -> "blocked:${event.oldVersion}->${event.newVersion}"
            is BrowserMigrationEvent.Ready -> "ready:${event.version}"
        }
    }

    private fun closeCapabilities() {
        worker?.close()
        worker = null
        store?.close()
        store = null
    }

    fun dispose() {
        if (disposed) return
        disposed = true
        generation++
        closeCapabilities()
        receiver.close()
        publisher.close()
        scope.cancel()
    }

    @Composable
    fun Content() {
        DisposableEffect(this) { { dispose() } }
        Column {
            Text("Browser persistence qualification", Modifier().attribute("data-testid", "persistence-title"))
            Text(status.value, Modifier().attribute("data-testid", "persistence-status"))
            Text(migrations.value, Modifier().attribute("data-testid", "persistence-migration"))
            Button(onClick = ::runPersistence, label = "Run persistence probes")
            Button(onClick = ::probeVisibleFailures, label = "Probe visible storage failures")
            Button(onClick = ::upgradeSchema, label = "Upgrade persistence schema")
            Button(onClick = ::startLateWorker, label = "Start delayed worker")
            Button(onClick = ::publishLogout, label = "Broadcast logout")
        }
    }
}
