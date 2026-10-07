package codes.yousef.summon.devtools

/**
 * Stable timeline identity: a root-local structural component identity plus its registered field name.
 *
 * @property nodeId The node id value.
 * @property name Human-readable name.
 * @property typeName The type name value.
 */
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

/**
 * Represents state mutation.
 *
 * @property sequence The sequence value.
 * @property fieldId The field id value.
 * @property before The before value.
 * @property after The after value.
 * @property actionId The action id value.
 */
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

/** Supported timeline recording state values. */
enum class TimelineRecordingState { /** The stopped timeline recording state option. */
                                    STOPPED,
                                    /** The recording timeline recording state option. */
                                    RECORDING,
                                    /** The paused timeline recording state option. */
                                    PAUSED }

/** Supported debug action effect values. */
enum class DebugActionEffect {
    /** The pure UI debug action effect option. */
    PURE_UI,
    /** The network debug action effect option. */
    NETWORK,
    /** The file debug action effect option. */
    FILE,
    /** The storage debug action effect option. */
    STORAGE,
    /** The clipboard debug action effect option. */
    CLIPBOARD,
    /** The account debug action effect option. */
    ACCOUNT,
    /** The crypto debug action effect option. */
    CRYPTO,
    /** The send debug action effect option. */
    SEND,
    /** The purchase debug action effect option. */
    PURCHASE,
    /** The other external debug action effect option. */
    OTHER_EXTERNAL
}

/**
 * Represents timeline restore result.
 *
 * @property appliedFields The applied fields value.
 * @property unrestorableFields The unrestorable fields value.
 */
data class TimelineRestoreResult(
    val appliedFields: Int,
    val unrestorableFields: List<DebugFieldId>
) {
    /** The property declaration value. */
    val complete: Boolean get() = unrestorableFields.isEmpty()
}

/** Contract for timeline replay result. */
sealed interface TimelineReplayResult {
    /**
     * Represents completed.
     *
     * @property appliedEntries The applied entries value.
     */
    data class Completed(val appliedEntries: Int) : TimelineReplayResult
    /**
     * Represents failed.
     *
     * @property sequence The sequence value.
     * @property reason The reason value.
     */
    data class Failed(val sequence: Long, val reason: String) : TimelineReplayResult
}

/** Immutable, schema-validated import. Creating this plan never mutates application state. */
class DebugSessionPlan internal constructor(entries: List<StateMutation>) {
    /** The property declaration value. */
    val formatVersion: Int = DebugSessionCodec.FORMAT_VERSION
    /** The property declaration value. */
    val entries: List<StateMutation> = entries.toList()

    /**
     * Executes the equals operation.
     *
     * @param other The other value.
     * @return The resulting value.
     */
    override fun equals(other: Any?): Boolean = other is DebugSessionPlan && entries == other.entries
    /**
     * Returns whether this value has h code.
     *
     * @return The resulting value.
     */
    override fun hashCode(): Int = entries.hashCode()
    /**
     * Converts this value to string.
     *
     * @return The resulting value.
     */
    override fun toString(): String = "DebugSessionPlan(formatVersion=$formatVersion, entries=$entries)"
}
/** Contract for debug action result. */
sealed interface DebugActionResult {
    /** Provides applied operations. */
    data object Applied : DebugActionResult
    /**
     * Represents rejected.
     *
     * @property reason The reason value.
     */
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

 * @property access The access value.
 * @property capacity The capacity value.
 * @property onDispose Callback invoked when dispose.
 */
class StateTimeline internal constructor(
    private var access: TimelineFieldAccess?,
    val capacity: Int = DEFAULT_CAPACITY,
    private var onDispose: ((StateTimeline) -> Unit)? = null
) : InspectorDisposable {
    /** Provides state timeline factory and constant members. */
    companion object {
        /** The property declaration value. */
        const val DEFAULT_CAPACITY: Int = 200
        /** The property declaration value. */
        const val MAX_CAPACITY: Int = 10_000
    }

    private val mutableEntries = mutableListOf<StateMutation>()
    private val lastValues = linkedMapOf<DebugFieldId, DebugValue>()
    private var cursor = 0
    private var nextSequence = 1L
    private var suppressRecording = false
    private var disposed = false

    /** The property declaration value. */
    var recordingState: TimelineRecordingState = TimelineRecordingState.STOPPED
        private set

    /** The property declaration value. */
    val entries: List<StateMutation> get() = mutableEntries.toList()
    /** The property declaration value. */
    val appliedSequence: Long? get() = mutableEntries.getOrNull(cursor - 1)?.sequence

    init {
        require(capacity in 1..MAX_CAPACITY) { "Timeline capacity must be in 1..$MAX_CAPACITY" }
    }

    /** Starts or resumes recording after refreshing the current PUBLIC field baseline. */
    fun start() {
        checkOpen()
        if (recordingState == TimelineRecordingState.RECORDING) return
        refreshLastValues()
        recordingState = TimelineRecordingState.RECORDING
    }

    /** Pauses recording without discarding retained entries or the restore cursor. */
    fun pause() {
        checkOpen()
        if (recordingState == TimelineRecordingState.RECORDING) recordingState = TimelineRecordingState.PAUSED
    }

    /** Stops recording and releases the captured field baseline while retaining entries. */
    fun stop() {
        checkOpen()
        recordingState = TimelineRecordingState.STOPPED
        lastValues.clear()
    }

    /** Removes every retained entry and resets sequence numbering for this timeline. */
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

    /**
     * Restores registered writable fields to the state after [sequence], or to the pre-recording
     * baseline when [sequence] is `null`.
     *
     * Missing or read-only fields are reported in the result rather than exposing private values.
     *
     * @throws IllegalArgumentException if a non-null sequence is no longer retained
     * @sample codes.yousef.summon.devtools.inspectorPrivacyAndTimeTravelSample
     */
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

    /**
     * Executes the export session operation.
     *
     * @return The resulting value.
     */
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

    /** Stops recording and releases all entries and session references. Repeated calls are safe. */
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
