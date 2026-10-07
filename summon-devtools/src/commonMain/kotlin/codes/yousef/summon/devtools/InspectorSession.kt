package codes.yousef.summon.devtools

/** Sensitivity is explicit; fields are redacted unless the caller opts into public inspection. */
enum class DebugFieldSensitivity {
    /** The public debug field sensitivity option. */
    PUBLIC,
    /** The redacted debug field sensitivity option. */
    REDACTED
}

/** Values supported by the inspector. Arbitrary objects are intentionally not representable. */
sealed interface DebugValue {
    /** Provides null operations. */
    data object Null : DebugValue
    /**
     * Represents boolean value.
     *
     * @property value Value to process.
     */
    data class BooleanValue(val value: Boolean) : DebugValue
    /**
     * Represents string value.
     *
     * @property value Value to process.
     */
    data class StringValue(val value: String) : DebugValue
    /**
     * Represents long value.
     *
     * @property value Value to process.
     */
    data class LongValue(val value: Long) : DebugValue
    /**
     * Represents double value.
     *
     * @property value Value to process.
     */
    data class DoubleValue(val value: Double) : DebugValue {
        init {
            require(value.isFinite()) { "Debug floating-point values must be finite" }
        }
    }
}

/** Typed conversion boundary for public debug fields. */
interface DebugFieldCodec<T> {
    /** The property declaration value. */
    val typeName: String
    /**
     * Executes the encode operation.
     *
     * @param value Value to process.
     * @return The resulting value.
     */
    fun encode(value: T): DebugValue
    /**
     * Executes the decode operation.
     *
     * @param value Value to process.
     * @return The resulting value.
     */
    fun decode(value: DebugValue): T
}

/** Built-in codecs accepted by the inspector and timeline. */
object DebugFieldCodecs {
    /** The property declaration value. */
    const val MAX_STRING_UTF8_BYTES: Int = 4_096

    /** The property declaration value. */
    val nullableBoolean: DebugFieldCodec<Boolean?> = object : DebugFieldCodec<Boolean?> {
        override val typeName: String = "boolean?"
        override fun encode(value: Boolean?): DebugValue = value?.let(DebugValue::BooleanValue) ?: DebugValue.Null
        override fun decode(value: DebugValue): Boolean? = when (value) {
            DebugValue.Null -> null
            is DebugValue.BooleanValue -> value.value
            else -> throw IllegalArgumentException("Expected boolean or null")
        }
    }

    /** The property declaration value. */
    val nullableString: DebugFieldCodec<String?> = object : DebugFieldCodec<String?> {
        override val typeName: String = "string?"
        override fun encode(value: String?): DebugValue = value?.let {
            requireBoundedString(it)
            DebugValue.StringValue(it)
        } ?: DebugValue.Null

        override fun decode(value: DebugValue): String? = when (value) {
            DebugValue.Null -> null
            is DebugValue.StringValue -> requireBoundedString(value.value)
            else -> throw IllegalArgumentException("Expected string or null")
        }
    }

    /** The property declaration value. */
    val nullableLong: DebugFieldCodec<Long?> = object : DebugFieldCodec<Long?> {
        override val typeName: String = "long?"
        override fun encode(value: Long?): DebugValue = value?.let(DebugValue::LongValue) ?: DebugValue.Null
        override fun decode(value: DebugValue): Long? = when (value) {
            DebugValue.Null -> null
            is DebugValue.LongValue -> value.value
            else -> throw IllegalArgumentException("Expected integral number or null")
        }
    }

    /** The property declaration value. */
    val nullableDouble: DebugFieldCodec<Double?> = object : DebugFieldCodec<Double?> {
        override val typeName: String = "double?"
        override fun encode(value: Double?): DebugValue = value?.let(DebugValue::DoubleValue) ?: DebugValue.Null
        override fun decode(value: DebugValue): Double? = when (value) {
            DebugValue.Null -> null
            is DebugValue.DoubleValue -> value.value
            else -> throw IllegalArgumentException("Expected finite number or null")
        }
    }

    /**
     * Executes the require bounded string operation.
     *
     * @param value Value to process.
     * @return The resulting value.
     */
    fun requireBoundedString(value: String): String {
        require(value.encodeToByteArray().size <= MAX_STRING_UTF8_BYTES) {
            "Debug strings must not exceed $MAX_STRING_UTF8_BYTES UTF-8 bytes"
        }
        return value
    }
}

/**
 * Stable renderer identity plus a root-local monotonic node identity.
 *
 * @property rendererId The renderer id value.
 * @property localId The local id value.
 */
data class InspectorNodeId(val rendererId: String, val localId: Long) {
    init {
        require(rendererId.isNotBlank()) { "Renderer identity must not be blank" }
        require(localId >= 0) { "Node identity must not be negative" }
    }
}

/**
 * Current actual renderer hierarchy. [children] order is render order, not identity.
 *
 * @property id Stable identifier.
 * @property parentId The parent id value.
 * @property label The label value.
 * @property children The children value.
 * @property connected The connected value.
 */
data class InspectorNode(
    val id: InspectorNodeId,
    val parentId: InspectorNodeId?,
    val label: String,
    val children: List<InspectorNodeId>,
    val connected: Boolean
)

/** Contract for inspector tree listener. */
fun interface InspectorTreeListener {
    /**
     * Handles tree changed.
     *
     * @param nodes The nodes value.
     */
    fun onTreeChanged(nodes: List<InspectorNode>)
}

/** Platform renderer observation. Disposing must release platform and DOM references. */
interface InspectorTreeSource {
    /** The property declaration value. */
    val rendererId: String
    /**
     * Executes the snapshot operation.
     *
     * @return The resulting value.
     */
    fun snapshot(): List<InspectorNode>
    /**
     * Executes the observe operation.
     *
     * @param listener The listener value.
     * @return The resulting value.
     */
    fun observe(listener: InspectorTreeListener): InspectorDisposable
    /**
     * Executes the highlight operation.
     *
     * @param nodeId The node id value.
     * @return The resulting value.
     */
    fun highlight(nodeId: InspectorNodeId?): Boolean
    /** Disposes the operation. */
    fun dispose()
}

/** Contract for inspector disposable. */
fun interface InspectorDisposable {
    /** Disposes the operation. */
    fun dispose()
}

/**
 * Represents inspector field.
 *
 * @property nodeId The node id value.
 * @property name Human-readable name.
 * @property sensitivity The sensitivity value.
 * @property typeName The type name value.
 * @property editable The editable value.
 * @property value Value to process.
 */
data class InspectorField(
    val nodeId: InspectorNodeId,
    val name: String,
    val sensitivity: DebugFieldSensitivity,
    val typeName: String?,
    val editable: Boolean,
    val value: DebugValue?
) {
    /** Provides inspector field factory and constant members. */
    companion object {
        /** The property declaration value. */
        const val REDACTION_MARKER: String = "<redacted>"
    }
}

/** Contract for inspector edit result. */
sealed interface InspectorEditResult {
    /** Provides applied operations. */
    data object Applied : InspectorEditResult
    /**
     * Represents rejected.
     *
     * @property reason The reason value.
     */
    data class Rejected(val reason: String) : InspectorEditResult
}

/**
 * Opt-in debug session for one mounted renderer root.
 *
 * Redacted getters are never invoked. Call [dispose] to detach tree observers, highlights, fields,
 * setters, listeners, and platform references. This API is shipped only by the separate devtools
 * artifact; applications must not add that artifact to production runtime configurations.

 * @property treeSource The tree source value.
 * @property renderDispatcher The render dispatcher value.
 */
class InspectorSession(
    private val treeSource: InspectorTreeSource,
    private val renderDispatcher: ((() -> Unit) -> Unit) = { action -> action() }
) {
    private data class RegisteredField(
        val nodeId: InspectorNodeId,
        val name: String,
        val sensitivity: DebugFieldSensitivity,
        val typeName: String?,
        val editable: Boolean,
        val read: (() -> DebugValue)?,
        val validate: ((DebugValue) -> Unit)?,
        val prepareWrite: ((DebugValue) -> (() -> Unit))?
    )
    private data class RegisteredAction(
        val nodeId: InspectorNodeId,
        val id: String,
        val invoke: () -> Unit
    )


    private val fields = linkedMapOf<Pair<InspectorNodeId, String>, RegisteredField>()
    private val actions = linkedMapOf<String, RegisteredAction>()
    private val timelines = linkedSetOf<StateTimeline>()
    private val listeners = linkedSetOf<InspectorTreeListener>()
    private var nodes = treeSource.snapshot()
    private var disposed = false
    private var activeActionId: String? = null
    private val timelineAccess = object : TimelineFieldAccess {
        override fun capturePublicFields(): List<CapturedDebugField> =
            fields.values.asSequence()
                .filter { it.sensitivity == DebugFieldSensitivity.PUBLIC }
                .map { field ->
                    val value = field.read!!.invoke()
                    validateDebugValue(value)
                    CapturedDebugField(field.timelineId(), value, field.editable)
                }
                .sortedWith(compareBy({ it.id.nodeId }, { it.id.name }, { it.id.typeName }))
                .toList()

        override fun writeField(id: DebugFieldId, value: DebugValue): TimelineWriteResult {
            val field = fields.values.firstOrNull {
                it.sensitivity == DebugFieldSensitivity.PUBLIC && it.timelineId() == id
            } ?: return TimelineWriteResult.Rejected("Debug field is no longer registered")
            val prepareWrite = field.prepareWrite
                ?: return TimelineWriteResult.Rejected("Debug field is read-only")
            return try {
                renderDispatcher(prepareWrite(value))
                TimelineWriteResult.Applied
            } catch (_: IllegalArgumentException) {
                TimelineWriteResult.Rejected("Invalid debug field value")
            } catch (_: Throwable) {
                TimelineWriteResult.Rejected("Debug field setter failed")
            }
        }

        override fun hasField(id: DebugFieldId): Boolean =
            fields.values.any { it.sensitivity == DebugFieldSensitivity.PUBLIC && it.timelineId() == id }
        override fun validateFieldValue(id: DebugFieldId, value: DebugValue): Boolean {
            val field = fields.values.firstOrNull {
                it.sensitivity == DebugFieldSensitivity.PUBLIC && it.timelineId() == id
            } ?: return false
            return try {
                validateDebugValue(value)
                field.validate!!.invoke(value)
                true
            } catch (_: IllegalArgumentException) {
                false
            }
        }


        override fun hasAction(id: String): Boolean = id in actions

        override fun invokeAction(id: String): TimelineWriteResult {
            val action = actions[id] ?: return TimelineWriteResult.Rejected("Debug action is no longer registered")
            return try {
                activeActionId = id
                renderDispatcher(action.invoke)
                TimelineWriteResult.Applied
            } catch (_: Throwable) {
                TimelineWriteResult.Rejected("Debug action failed")
            } finally {
                activeActionId = null
            }
        }
    }
    private val treeSubscription = treeSource.observe { updated ->
        if (!disposed) {
            nodes = updated
            val liveIds = updated.asSequence().map { it.id }.toSet()
            fields.keys.removeAll { (nodeId, _) -> nodeId !in liveIds }
            actions.entries.removeAll { (_, action) -> action.nodeId !in liveIds }
            listeners.toList().forEach { it.onTreeChanged(updated) }
        }
    }

    /** The property declaration value. */
    val rendererId: String get() = treeSource.rendererId

    /**
     * Executes the tree operation.
     *
     * @return The resulting value.
     */
    fun tree(): List<InspectorNode> {
        checkOpen()
        return nodes
    }

    /**
     * Executes the observe tree operation.
     *
     * @param listener The listener value.
     * @return The resulting value.
     */
    fun observeTree(listener: InspectorTreeListener): InspectorDisposable {
        checkOpen()
        listeners += listener
        listener.onTreeChanged(nodes)
        var active = true
        return InspectorDisposable {
            if (active) {
                active = false
                listeners -= listener
            }
        }
    }

    /**
     * Registers a field with safe redacted defaults.
     *
     * PUBLIC registration requires [registerPublicField], which forces an explicit codec and getter.
     */
    fun registerField(
        nodeId: InspectorNodeId,
        name: String,
        sensitivity: DebugFieldSensitivity = DebugFieldSensitivity.REDACTED
    ): InspectorDisposable {
        require(sensitivity == DebugFieldSensitivity.REDACTED) {
            "Public debug fields require registerPublicField with an explicit codec"
        }
        return registerRedactedField(nodeId, name)
    }

    /**
     * Registers redacted field.
     *
     * @param nodeId The node id value.
     * @param name Human-readable name.
     * @return The resulting value.
     */
    fun registerRedactedField(nodeId: InspectorNodeId, name: String): InspectorDisposable =
        registerField<Any?>(
            nodeId = nodeId,
            name = name,
            sensitivity = DebugFieldSensitivity.REDACTED,
            codec = null,
            getter = null,
            setter = null
        )

    /**
     * Explicitly exposes one typed field to development tooling.
     *
     * Fields remain private unless registered through this method. Credential-like names are
     * rejected, values must pass [codec], and omitting [setter] makes the field read-only.
     * Dispose the returned registration before the node or backing state is released.
     *
     * @throws IllegalArgumentException if the node is not live, the name is unsafe, or registration
     * is duplicated
     * @sample codes.yousef.summon.devtools.inspectorPrivacyAndTimeTravelSample
     */
    fun <T> registerPublicField(
        nodeId: InspectorNodeId,
        name: String,
        codec: DebugFieldCodec<T>,
        getter: () -> T,
        setter: ((T) -> Unit)? = null
    ): InspectorDisposable = registerField(
        nodeId = nodeId,
        name = name,
        sensitivity = DebugFieldSensitivity.PUBLIC,
        codec = codec,
        getter = getter,
        setter = setter
    )
    /**
     * Registers a deterministic action that is safe to replay. External-effect actions are rejected
     * rather than retained as callable references.
     */
    fun registerDebugAction(
        nodeId: InspectorNodeId,
        id: String,
        effect: DebugActionEffect,
        action: () -> Unit
    ): InspectorDisposable {
        checkOpen()
        requireNode(nodeId)
        requireDebugIdentifier(id, "Action")
        require(effect == DebugActionEffect.PURE_UI) {
            "Only PURE_UI debug actions may be registered for replay"
        }
        require(id !in actions) { "Debug action '$id' is already registered" }
        actions[id] = RegisteredAction(nodeId, id, action)
        var active = true
        return InspectorDisposable {
            if (active) {
                active = false
                actions.remove(id)
            }
        }
    }

    /**
     * Creates a bounded, session-owned state timeline.
     *
     * Only explicitly registered PUBLIC fields are recorded. Disposing this session also disposes
     * the timeline; callers may dispose it earlier.
     *
     * @throws IllegalArgumentException when [capacity] is outside the supported range
     * @sample codes.yousef.summon.devtools.inspectorPrivacyAndTimeTravelSample
     */
    fun createTimeline(capacity: Int = StateTimeline.DEFAULT_CAPACITY): StateTimeline {
        checkOpen()
        val timeline = StateTimeline(timelineAccess, capacity) { disposedTimeline ->
            timelines -= disposedTimeline
        }
        timelines += timeline
        return timeline
    }


    /**
     * Executes the fields operation.
     *
     * @param nodeId The node id value.
     * @return The resulting value.
     */
    fun fields(nodeId: InspectorNodeId): List<InspectorField> {
        checkOpen()
        requireNode(nodeId)
        return fields.values.asSequence()
            .filter { it.nodeId == nodeId }
            .map { field ->
                if (field.sensitivity == DebugFieldSensitivity.REDACTED) {
                    InspectorField(field.nodeId, field.name, field.sensitivity, null, false, null)
                } else {
                    InspectorField(
                        field.nodeId,
                        field.name,
                        field.sensitivity,
                        field.typeName,
                        field.editable,
                        field.read!!.invoke()
                    )
                }
            }
            .toList()
    }

    /**
     * Executes the edit operation.
     *
     * @param nodeId The node id value.
     * @param name Human-readable name.
     * @param value Value to process.
     * @return The resulting value.
     */
    fun edit(nodeId: InspectorNodeId, name: String, value: DebugValue): InspectorEditResult {
        checkOpen()
        val field = fields[nodeId to name]
            ?: return InspectorEditResult.Rejected("Unknown debug field")
        if (field.sensitivity != DebugFieldSensitivity.PUBLIC) {
            return InspectorEditResult.Rejected("Redacted debug fields cannot be edited")
        }
        val prepareWrite = field.prepareWrite
            ?: return InspectorEditResult.Rejected("Debug field is read-only")
        return try {
            validateDebugValue(value)
            val before = field.read!!.invoke()
            val action = prepareWrite(value)
            renderDispatcher(action)
            val after = field.read.invoke()
            val id = field.timelineId()
            timelines.toList().forEach {
                it.observeMutation(id, before, after, activeActionId)
            }
            InspectorEditResult.Applied
        } catch (error: IllegalArgumentException) {
            InspectorEditResult.Rejected(error.message ?: "Invalid debug field value")
        }
    }

    /**
     * Executes the highlight operation.
     *
     * @param nodeId The node id value.
     * @return The resulting value.
     */
    fun highlight(nodeId: InspectorNodeId?): Boolean {
        checkOpen()
        if (nodeId != null && nodes.none { it.id == nodeId && it.connected }) return false
        return treeSource.highlight(nodeId)
    }

    /**
     * Releases tree observers, field/action registrations, timelines, and platform references.
     * Repeated calls have no effect.
     */
    fun dispose() {
        if (disposed) return
        disposed = true
        listeners.clear()
        timelines.toList().forEach(StateTimeline::dispose)
        timelines.clear()
        actions.clear()
        fields.clear()
        treeSubscription.dispose()
        treeSource.highlight(null)
        treeSource.dispose()
        nodes = emptyList()
    }

    private fun <T> registerField(
        nodeId: InspectorNodeId,
        name: String,
        sensitivity: DebugFieldSensitivity,
        codec: DebugFieldCodec<T>?,
        getter: (() -> T)?,
        setter: ((T) -> Unit)?
    ): InspectorDisposable {
        checkOpen()
        requireNode(nodeId)
        require(name.isNotBlank() && name.length <= 128) { "Debug field name must be 1..128 characters" }
        val key = nodeId to name
        require(key !in fields) { "Debug field '$name' is already registered for this node" }
        if (sensitivity == DebugFieldSensitivity.PUBLIC) {
            require(codec != null && getter != null) { "Public debug fields require an explicit codec and getter" }
            val normalizedName = name.lowercase()
            require(SENSITIVE_FIELD_NAME_PARTS.none(normalizedName::contains)) {
                "Authentication and credential state cannot be registered as a public debug field"
            }
        }
        if (sensitivity == DebugFieldSensitivity.REDACTED) {
            require(setter == null) { "Redacted debug fields cannot be writable" }
        }
        val registered = RegisteredField(
            nodeId = nodeId,
            name = name,
            sensitivity = sensitivity,
            typeName = codec?.typeName,
            editable = setter != null,
            read = if (sensitivity == DebugFieldSensitivity.PUBLIC) {
                { codec!!.encode(getter!!.invoke()) }
            } else {
                null
            },
            validate = if (sensitivity == DebugFieldSensitivity.PUBLIC) {
                { encoded ->
                    codec!!.decode(encoded)
                    Unit
                }
            } else {
                null
            },
            prepareWrite = if (sensitivity == DebugFieldSensitivity.PUBLIC && setter != null) {
                { encoded ->
                    val decoded = codec!!.decode(encoded)
                    val action: () -> Unit = { setter(decoded) }
                    action
                }
            } else {
                null
            }
        )
        fields[key] = registered
        var active = true
        return InspectorDisposable {
            if (active) {
                active = false
                fields.remove(key)
            }
        }
    }
    private fun RegisteredField.timelineId(): DebugFieldId =
        DebugFieldId(nodeId.localId, name, requireNotNull(typeName))
    private companion object {
        val SENSITIVE_FIELD_NAME_PARTS = listOf(
            "password",
            "passwd",
            "credential",
            "secret",
            "token",
            "cookie",
            "authorization",
            "privatekey"
        )
    }



    private fun requireNode(nodeId: InspectorNodeId) {
        require(nodeId.rendererId == rendererId && nodes.any { it.id == nodeId }) {
            "Inspector node does not belong to this live renderer"
        }
    }

    private fun checkOpen() {
        check(!disposed) { "Inspector session is disposed" }
    }
}
