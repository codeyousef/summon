package codes.yousef.summon.devtools

/** Stable timeline identity: a root-local structural component identity plus its registered field name. */
data class DebugFieldId(
    val nodeId: Long,
    val name: String,
    val typeName: String
) {
    init {
        require(nodeId >= 0) { "Field node identity must not be negative" }
        require(name.isNotBlank() && name.length <= 128) { "Field name must be 1..128 characters" }
        require(typeName.isNotBlank() && typeName.length <= 128) { "Field codec name must be 1..128 characters" }
    }
}

data class StateMutation(
    val sequence: Long,
    val fieldId: DebugFieldId,
    val before: DebugValue,
    val after: DebugValue,
    val actionId: String? = null
) {
    init {
        require(sequence > 0) { "Mutation sequence must be positive" }
        actionId?.let { requireDebugIdentifier(it, "Action") }
        require(before != after) { "Equal values are not mutations" }
        validateDebugValue(before)
        validateDebugValue(after)
    }
}

enum class TimelineRecordingState { STOPPED, RECORDING, PAUSED }

enum class DebugActionEffect {
    PURE_UI,
    NETWORK,
    FILE,
    STORAGE,
    CLIPBOARD,
    ACCOUNT,
    CRYPTO,
    SEND,
    PURCHASE,
    OTHER_EXTERNAL
}

data class TimelineRestoreResult(
    val appliedFields: Int,
    val unrestorableFields: List<DebugFieldId>
) {
    val complete: Boolean get() = unrestorableFields.isEmpty()
}

sealed interface TimelineReplayResult {
    data class Completed(val appliedEntries: Int) : TimelineReplayResult
    data class Failed(val sequence: Long, val reason: String) : TimelineReplayResult
}

/** Immutable, schema-validated import. Creating this plan never mutates application state. */
class DebugSessionPlan internal constructor(entries: List<StateMutation>) {
    val formatVersion: Int = DebugSessionCodec.FORMAT_VERSION
    val entries: List<StateMutation> = entries.toList()

    override fun equals(other: Any?): Boolean = other is DebugSessionPlan && entries == other.entries
    override fun hashCode(): Int = entries.hashCode()
    override fun toString(): String = "DebugSessionPlan(formatVersion=$formatVersion, entries=$entries)"
}
sealed interface DebugActionResult {
    data object Applied : DebugActionResult
    data class Rejected(val reason: String) : DebugActionResult
}


internal data class CapturedDebugField(
    val id: DebugFieldId,
    val value: DebugValue,
    val writable: Boolean
)

internal sealed interface TimelineWriteResult {
    data object Applied : TimelineWriteResult
    data class Rejected(val reason: String) : TimelineWriteResult
}

internal interface TimelineFieldAccess {
    fun capturePublicFields(): List<CapturedDebugField>
    fun writeField(id: DebugFieldId, value: DebugValue): TimelineWriteResult
    fun hasField(id: DebugFieldId): Boolean
    fun validateFieldValue(id: DebugFieldId, value: DebugValue): Boolean
    fun hasAction(id: String): Boolean
    fun invokeAction(id: String): TimelineWriteResult
}

/**
 * Bounded state recorder for one [InspectorSession]. Recording is opt-in and contains only PUBLIC,
 * typed debug fields. Call [sample] after application-driven updates; browser tooling does this once
 * per animation frame while recording.
 */
class StateTimeline internal constructor(
    private var access: TimelineFieldAccess?,
    val capacity: Int = DEFAULT_CAPACITY,
    private var onDispose: ((StateTimeline) -> Unit)? = null
) : InspectorDisposable {
    companion object {
        const val DEFAULT_CAPACITY: Int = 200
        const val MAX_CAPACITY: Int = 10_000
    }

    private val mutableEntries = mutableListOf<StateMutation>()
    private val lastValues = linkedMapOf<DebugFieldId, DebugValue>()
    private var cursor = 0
    private var nextSequence = 1L
    private var suppressRecording = false
    private var disposed = false

    var recordingState: TimelineRecordingState = TimelineRecordingState.STOPPED
        private set

    val entries: List<StateMutation> get() = mutableEntries.toList()
    val appliedSequence: Long? get() = mutableEntries.getOrNull(cursor - 1)?.sequence

    init {
        require(capacity in 1..MAX_CAPACITY) { "Timeline capacity must be in 1..$MAX_CAPACITY" }
    }

    fun start() {
        checkOpen()
        if (recordingState == TimelineRecordingState.RECORDING) return
        refreshLastValues()
        recordingState = TimelineRecordingState.RECORDING
    }

    fun pause() {
        checkOpen()
        if (recordingState == TimelineRecordingState.RECORDING) recordingState = TimelineRecordingState.PAUSED
    }

    fun stop() {
        checkOpen()
        recordingState = TimelineRecordingState.STOPPED
        lastValues.clear()
    }

    fun clear() {
        checkOpen()
        mutableEntries.clear()
        lastValues.clear()
        cursor = 0
        nextSequence = 1L
        if (recordingState != TimelineRecordingState.STOPPED) refreshLastValues()
    }

    /** Captures application-driven changes since the previous sample. Equal writes add no entry. */
    fun sample() {
        sample(actionId = null)
    }

    /** Runs one registered PURE_UI action and associates its resulting mutations with that action. */
    fun performAction(actionId: String): DebugActionResult {
        checkOpen()
        requireDebugIdentifier(actionId, "Action")
        return when (val result = accessOrThrow().invokeAction(actionId)) {
            TimelineWriteResult.Applied -> {
                sample(actionId)
                DebugActionResult.Applied
            }
            is TimelineWriteResult.Rejected -> DebugActionResult.Rejected(result.reason)
        }
    }

    fun restoreTo(sequence: Long?): TimelineRestoreResult {
        checkOpen()
        val target = if (sequence == null) 0 else {
            val index = mutableEntries.indexOfFirst { it.sequence == sequence }
            require(index >= 0) { "Unknown retained mutation sequence" }
            index + 1
        }
        val unrestorable = linkedSetOf<DebugFieldId>()
        var applied = 0
        suppressRecording = true
        try {
            if (target < cursor) {
                for (index in cursor - 1 downTo target) {
                    when (accessOrThrow().writeField(mutableEntries[index].fieldId, mutableEntries[index].before)) {
                        TimelineWriteResult.Applied -> applied++
                        is TimelineWriteResult.Rejected -> unrestorable += mutableEntries[index].fieldId
                    }
                }
            } else if (target > cursor) {
                for (index in cursor until target) {
                    when (accessOrThrow().writeField(mutableEntries[index].fieldId, mutableEntries[index].after)) {
                        TimelineWriteResult.Applied -> applied++
                        is TimelineWriteResult.Rejected -> unrestorable += mutableEntries[index].fieldId
                    }
                }
            }
            cursor = target
            refreshLastValues()
        } finally {
            suppressRecording = false
        }
        return TimelineRestoreResult(applied, unrestorable.toList())
    }

    /** Replays retained field writes and explicitly registered PURE_UI actions, stopping on failure. */
    fun replay(): TimelineReplayResult {
        checkOpen()
        val reset = restoreTo(null)
        if (!reset.complete) {
            return TimelineReplayResult.Failed(
                mutableEntries.firstOrNull()?.sequence ?: 1L,
                "One or more fields are no longer writable"
            )
        }
        var index = 0
        suppressRecording = true
        try {
            while (index < mutableEntries.size) {
                val entry = mutableEntries[index]
                val actionId = entry.actionId
                val result = if (actionId == null) {
                    accessOrThrow().writeField(entry.fieldId, entry.after)
                } else {
                    accessOrThrow().invokeAction(actionId)
                }
                if (result is TimelineWriteResult.Rejected) {
                    cursor = index
                    refreshLastValues()
                    return TimelineReplayResult.Failed(entry.sequence, result.reason)
                }
                index++
                if (actionId != null) {
                    val seenFields = linkedSetOf(entry.fieldId)
                    while (
                        index < mutableEntries.size &&
                        mutableEntries[index].actionId == actionId &&
                        seenFields.add(mutableEntries[index].fieldId)
                    ) {
                        index++
                    }
                }
                cursor = index
            }
            refreshLastValues()
            return TimelineReplayResult.Completed(mutableEntries.size)
        } finally {
            suppressRecording = false
        }
    }

    fun exportSession(): String {
        checkOpen()
        return DebugSessionCodec.encode(DebugSessionPlan(mutableEntries))
    }

    /** Parses and validates without invoking any live setter or action. */
    fun importSession(json: String): DebugSessionPlan {
        checkOpen()
        return DebugSessionCodec.decode(json, accessOrThrow())
    }

    /** Explicitly installs and applies an already validated plan. */
    fun apply(plan: DebugSessionPlan): TimelineRestoreResult {
        checkOpen()
        require(plan.entries.size <= capacity) {
            "Imported session exceeds this timeline's capacity of $capacity entries"
        }
        validatePlanAgainstLiveSession(plan, accessOrThrow())
        clear()
        mutableEntries += plan.entries
        nextSequence = (plan.entries.maxOfOrNull { it.sequence } ?: 0L) + 1L
        cursor = 0
        return restoreTo(plan.entries.lastOrNull()?.sequence)
    }

    internal fun observeMutation(id: DebugFieldId, before: DebugValue, after: DebugValue, actionId: String?) {
        if (disposed || suppressRecording || recordingState != TimelineRecordingState.RECORDING || before == after) return
        append(id, before, after, actionId)
        lastValues[id] = after
    }

    internal fun sample(actionId: String?) {
        if (disposed || suppressRecording || recordingState != TimelineRecordingState.RECORDING) return
        val current = accessOrThrow().capturePublicFields().associate { it.id to it.value }
        current.keys.sortedWith(fieldIdComparator).forEach { id ->
            val before = lastValues[id]
            val after = current.getValue(id)
            if (before != null && before != after) append(id, before, after, actionId)
        }
        lastValues.clear()
        lastValues.putAll(current)
    }

    override fun dispose() {
        if (disposed) return
        disposed = true
        recordingState = TimelineRecordingState.STOPPED
        mutableEntries.clear()
        lastValues.clear()
        cursor = 0
        access = null
        onDispose?.invoke(this)
        onDispose = null
    }

    private fun append(id: DebugFieldId, before: DebugValue, after: DebugValue, actionId: String?) {
        validateDebugValue(before)
        validateDebugValue(after)
        if (cursor < mutableEntries.size) mutableEntries.subList(cursor, mutableEntries.size).clear()
        mutableEntries += StateMutation(nextSequence++, id, before, after, actionId)
        cursor = mutableEntries.size
        if (mutableEntries.size > capacity) {
            mutableEntries.removeAt(0)
            cursor--
        }
    }


    private fun refreshLastValues() {
        lastValues.clear()
        accessOrThrow().capturePublicFields().forEach { lastValues[it.id] = it.value }
    }

    private fun accessOrThrow(): TimelineFieldAccess = access ?: error("Timeline is disposed")
    private fun checkOpen() = check(!disposed) { "Timeline is disposed" }
}

internal val fieldIdComparator = compareBy<DebugFieldId>({ it.nodeId }, { it.name }, { it.typeName })

internal fun validateDebugValue(value: DebugValue) {
    when (value) {
        DebugValue.Null, is DebugValue.BooleanValue, is DebugValue.LongValue -> Unit
        is DebugValue.DoubleValue -> require(value.value.isFinite()) { "Debug floating-point values must be finite" }
        is DebugValue.StringValue -> {
            DebugFieldCodecs.requireBoundedString(value.value)
            require(!value.value.containsCredentialUrl()) { "Credential-bearing URLs cannot be recorded" }
        }
    }
}

internal fun requireDebugIdentifier(value: String, label: String) {
    require(value.isNotBlank() && value.length <= 128 && value.all { it.isLetterOrDigit() || it in "._-:" }) {
        "$label identifier must be 1..128 safe characters"
    }
}

private fun String.containsCredentialUrl(): Boolean {
    val scheme = indexOf("://")
    if (scheme < 1) return false
    val authorityStart = scheme + 3
    val authorityEnd = indexOf('/', authorityStart).let { if (it < 0) length else it }
    return substring(authorityStart, authorityEnd).contains('@')
}

internal fun validatePlanAgainstLiveSession(plan: DebugSessionPlan, access: TimelineFieldAccess) {
    plan.entries.forEach { entry ->
        require(access.hasField(entry.fieldId)) { "Unknown debug field in imported session" }
        require(access.validateFieldValue(entry.fieldId, entry.before)) { "Invalid before value for debug field" }
        require(access.validateFieldValue(entry.fieldId, entry.after)) { "Invalid after value for debug field" }
        entry.actionId?.let { require(access.hasAction(it)) { "Unknown debug action in imported session" } }
    }
}
