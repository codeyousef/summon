package codes.yousef.summon.devtools

/** Sensitivity is explicit; fields are redacted unless the caller opts into public inspection. */
enum class DebugFieldSensitivity {
    PUBLIC,
    REDACTED
}

/** Values supported by the inspector. Arbitrary objects are intentionally not representable. */
sealed interface DebugValue {
    data object Null : DebugValue
    data class BooleanValue(val value: Boolean) : DebugValue
    data class StringValue(val value: String) : DebugValue
    data class LongValue(val value: Long) : DebugValue
    data class DoubleValue(val value: Double) : DebugValue {
        init {
            require(value.isFinite()) { "Debug floating-point values must be finite" }
        }
    }
}

/** Typed conversion boundary for public debug fields. */
interface DebugFieldCodec<T> {
    val typeName: String
    fun encode(value: T): DebugValue
    fun decode(value: DebugValue): T
}

/** Built-in codecs accepted by the inspector and timeline. */
object DebugFieldCodecs {
    const val MAX_STRING_UTF8_BYTES: Int = 4_096

    val nullableBoolean: DebugFieldCodec<Boolean?> = object : DebugFieldCodec<Boolean?> {
        override val typeName: String = "boolean?"
        override fun encode(value: Boolean?): DebugValue = value?.let(DebugValue::BooleanValue) ?: DebugValue.Null
        override fun decode(value: DebugValue): Boolean? = when (value) {
            DebugValue.Null -> null
            is DebugValue.BooleanValue -> value.value
            else -> throw IllegalArgumentException("Expected boolean or null")
        }
    }

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

    val nullableLong: DebugFieldCodec<Long?> = object : DebugFieldCodec<Long?> {
        override val typeName: String = "long?"
        override fun encode(value: Long?): DebugValue = value?.let(DebugValue::LongValue) ?: DebugValue.Null
        override fun decode(value: DebugValue): Long? = when (value) {
            DebugValue.Null -> null
            is DebugValue.LongValue -> value.value
            else -> throw IllegalArgumentException("Expected integral number or null")
        }
    }

    val nullableDouble: DebugFieldCodec<Double?> = object : DebugFieldCodec<Double?> {
        override val typeName: String = "double?"
        override fun encode(value: Double?): DebugValue = value?.let(DebugValue::DoubleValue) ?: DebugValue.Null
        override fun decode(value: DebugValue): Double? = when (value) {
            DebugValue.Null -> null
            is DebugValue.DoubleValue -> value.value
            else -> throw IllegalArgumentException("Expected finite number or null")
        }
    }

    fun requireBoundedString(value: String): String {
        require(value.encodeToByteArray().size <= MAX_STRING_UTF8_BYTES) {
            "Debug strings must not exceed $MAX_STRING_UTF8_BYTES UTF-8 bytes"
        }
        return value
    }
}

/** Stable renderer identity plus a root-local monotonic node identity. */
data class InspectorNodeId(val rendererId: String, val localId: Long) {
    init {
        require(rendererId.isNotBlank()) { "Renderer identity must not be blank" }
        require(localId >= 0) { "Node identity must not be negative" }
    }
}

/** Current actual renderer hierarchy. [children] order is render order, not identity. */
data class InspectorNode(
    val id: InspectorNodeId,
    val parentId: InspectorNodeId?,
    val label: String,
    val children: List<InspectorNodeId>,
    val connected: Boolean
)

fun interface InspectorTreeListener {
    fun onTreeChanged(nodes: List<InspectorNode>)
}

/** Platform renderer observation. Disposing must release platform and DOM references. */
interface InspectorTreeSource {
    val rendererId: String
    fun snapshot(): List<InspectorNode>
    fun observe(listener: InspectorTreeListener): InspectorDisposable
    fun highlight(nodeId: InspectorNodeId?): Boolean
    fun dispose()
}

fun interface InspectorDisposable {
    fun dispose()
}

data class InspectorField(
    val nodeId: InspectorNodeId,
    val name: String,
    val sensitivity: DebugFieldSensitivity,
    val typeName: String?,
    val editable: Boolean,
    val value: DebugValue?
) {
    companion object {
        const val REDACTION_MARKER: String = "<redacted>"
    }
}

sealed interface InspectorEditResult {
    data object Applied : InspectorEditResult
    data class Rejected(val reason: String) : InspectorEditResult
}

/**
 * Opt-in debug session for one mounted renderer root.
 *
 * Redacted getters are never invoked. Call [dispose] to detach tree observers, highlights, fields,
 * setters, listeners, and platform references. This API is shipped only by the separate devtools
 * artifact; applications must not add that artifact to production runtime configurations.
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
        val prepareWrite: ((DebugValue) -> (() -> Unit))?
    )

    private val fields = linkedMapOf<Pair<InspectorNodeId, String>, RegisteredField>()
    private val listeners = linkedSetOf<InspectorTreeListener>()
    private var nodes = treeSource.snapshot()
    private var disposed = false
    private val treeSubscription = treeSource.observe { updated ->
        if (!disposed) {
            nodes = updated
            val liveIds = updated.asSequence().map { it.id }.toSet()
            fields.keys.removeAll { (nodeId, _) -> nodeId !in liveIds }
            listeners.toList().forEach { it.onTreeChanged(updated) }
        }
    }

    val rendererId: String get() = treeSource.rendererId

    fun tree(): List<InspectorNode> {
        checkOpen()
        return nodes
    }

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

    fun registerRedactedField(nodeId: InspectorNodeId, name: String): InspectorDisposable =
        registerField<Any?>(
            nodeId = nodeId,
            name = name,
            sensitivity = DebugFieldSensitivity.REDACTED,
            codec = null,
            getter = null,
            setter = null
        )

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
            val action = prepareWrite(value)
            renderDispatcher(action)
            InspectorEditResult.Applied
        } catch (error: IllegalArgumentException) {
            InspectorEditResult.Rejected(error.message ?: "Invalid debug field value")
        }
    }

    fun highlight(nodeId: InspectorNodeId?): Boolean {
        checkOpen()
        if (nodeId != null && nodes.none { it.id == nodeId && it.connected }) return false
        return treeSource.highlight(nodeId)
    }

    fun dispose() {
        if (disposed) return
        disposed = true
        listeners.clear()
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

    private fun requireNode(nodeId: InspectorNodeId) {
        require(nodeId.rendererId == rendererId && nodes.any { it.id == nodeId }) {
            "Inspector node does not belong to this live renderer"
        }
    }

    private fun checkOpen() {
        check(!disposed) { "Inspector session is disposed" }
    }
}
