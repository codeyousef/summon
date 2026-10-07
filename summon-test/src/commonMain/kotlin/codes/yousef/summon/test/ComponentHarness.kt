package codes.yousef.summon.test

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.RecompositionScheduler
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

/** Adds a stable semantic tag independent of visible text. */
fun Modifier.testTag(tag: String): Modifier {
    require(tag.isNotBlank() && tag.length <= 256) { "Test tag must be 1..256 characters" }
    return attribute(TEST_TAG_ATTRIBUTE, tag)
}

/** Adds a typed test-only state value addressable by [SemanticNodeHandle.assertState]. */
fun Modifier.testState(name: String, value: Any): Modifier {
    require(name.matches(Regex("[A-Za-z][A-Za-z0-9_-]{0,63}"))) { "Test state name is invalid" }
    require(value is String || value is Boolean || value is Long || value is Int || value is Double || value is Float) {
        "Test state must be String, Boolean, integral, or floating point"
    }
    val encoded = when (value) {
        is Float -> value.toDouble().also { require(it.isFinite()) }.toString()
        is Double -> value.also { require(it.isFinite()) }.toString()
        else -> value.toString()
    }
    return attribute("$TEST_STATE_PREFIX$name", encoded)
}

/** HTML attribute written by [Modifier.testTag]. */
const val TEST_TAG_ATTRIBUTE: String = "data-summon-test-tag"
/** HTML attribute prefix written by [Modifier.testState]. */
const val TEST_STATE_PREFIX: String = "data-summon-test-state-"

internal fun semanticRole(elementName: String, explicitRole: String?, inputType: String?): String =
    explicitRole?.takeIf { it.isNotBlank() } ?: when (elementName.lowercase()) {
        "button" -> "button"
        "a" -> "link"
        "img" -> "img"
        "textarea" -> "textbox"
        "input" -> when (inputType?.lowercase()) {
            "button", "submit", "reset" -> "button"
            "checkbox" -> "checkbox"
            "radio" -> "radio"
            "range" -> "slider"
            else -> "textbox"
        }
        "select" -> "combobox"
        "ul", "ol" -> "list"
        "li" -> "listitem"
        else -> "generic"
    }

internal data class SemanticSnapshot(
    val identity: Long,
    val parentIdentity: Long?,
    val elementName: String,
    val text: String,
    val tag: String?,
    val connected: Boolean,
    val displayed: Boolean,
    val role: String,
    val name: String,
    val inert: Boolean,
    val enabled: Boolean,
    val states: Map<String, String>
)

internal interface SemanticHarnessAdapter {
    val scheduler: HarnessScheduler
    fun snapshot(): List<SemanticSnapshot>
    fun click(identity: Long)
    fun textInput(identity: Long, value: String)
    fun scrollTo(identity: Long)
    fun dispose()
}

/** Deterministic, owner-scoped scheduler used by component harnesses. */
class HarnessScheduler : RecompositionScheduler {
    /** Provides harness scheduler factory and constant members. */
    companion object {
        /** Maximum queued recomposition callbacks before a harness rejects runaway work. */
        const val DEFAULT_MAX_PENDING_WORK: Int = 10_000
    }

    private val pending = ArrayDeque<() -> Unit>()
    private var closed = false

    /** Number of recomposition callbacks waiting to run. */
    val pendingWorkCount: Int get() = pending.size

    /** Queues [work] for deterministic execution by the owning harness. */
    override fun scheduleRecomposition(work: () -> Unit) {
        check(!closed) { "Harness scheduler is disposed" }
        check(pending.size < DEFAULT_MAX_PENDING_WORK) { "Harness pending-work capacity exceeded" }
        pending.addLast(work)
    }

    /** Cancels pending recomposition. */
    override fun cancelPendingRecomposition() {
        pending.clear()
    }

    internal fun drain(timeoutMillis: Long) {
        require(timeoutMillis in 1..60_000) { "Idle timeout must be in 1..60000 ms" }
        val deadline = TimeSource.Monotonic.markNow() + timeoutMillis.milliseconds
        var completed = 0
        while (pending.isNotEmpty()) {
            if (deadline.hasPassedNow()) {
                throw AssertionError(
                    "Harness did not become idle within ${timeoutMillis}ms; " +
                        "pending=${pending.size}, completed=$completed"
                )
            }
            pending.removeFirst().invoke()
            completed++
            if (completed > DEFAULT_MAX_PENDING_WORK) {
                throw AssertionError("Harness exceeded $DEFAULT_MAX_PENDING_WORK scheduled operations; pending=${pending.size}")
            }
        }
    }

    internal fun close() {
        if (closed) return
        closed = true
        pending.clear()
    }
}

/**
 * Owns one rendered component root and its scheduler. Use [withComponentHarness] so failures in
 * setup, actions, or assertions still dispose the mounted root.

 * @property adapter The adapter value.
 */
class ComponentHarness internal constructor(private var adapter: SemanticHarnessAdapter?) : AutoCloseable {
    /** Provides component harness factory and constant members. */
    companion object {
        /** Default upper bound for waiting until the harness becomes idle. */
        const val DEFAULT_IDLE_TIMEOUT_MS: Long = 1_000
        /** Maximum diagnostic tree description size. */
        const val MAX_TREE_DESCRIPTION_CHARS: Int = 8_192
        /** Maximum serialized semantic snapshot size. */
        const val MAX_SEMANTIC_SNAPSHOT_CHARS: Int = 1_048_576
    }

    private var disposed = false

    /** Returns the unique live node whose text matches [text]. */
    fun onNodeWithText(text: String, substring: Boolean = false): SemanticNodeHandle {
        require(text.isNotEmpty()) { "Text matcher must not be empty" }
        return unique("text '$text'") { node ->
            if (substring) node.text.contains(text) else node.text == text
        }
    }

    /** Returns the unique live node carrying [tag]. */
    fun onNodeWithTag(tag: String): SemanticNodeHandle {
        require(tag.isNotEmpty()) { "Tag matcher must not be empty" }
        return unique("tag '$tag'") { it.tag == tag }
    }

    /** Asserts that no live node has text matching [text]. */
    fun assertNoNodeWithText(text: String, substring: Boolean = false): ComponentHarness {
        val matches = snapshots().filter { if (substring) it.text.contains(text) else it.text == text }
        if (matches.isNotEmpty()) fail("Expected no node with text '$text', found ${matches.size}")
        return this
    }

    /** Asserts that no live node carries [tag]. */
    fun assertNoNodeWithTag(tag: String): ComponentHarness {
        val matches = snapshots().filter { it.tag == tag }
        if (matches.isNotEmpty()) fail("Expected no node with tag '$tag', found ${matches.size}")
        return this
    }

    /** Runs scheduled work until idle or throws after [timeoutMillis]. */
    fun awaitIdle(timeoutMillis: Long = DEFAULT_IDLE_TIMEOUT_MS): ComponentHarness {
        adapter().scheduler.drain(timeoutMillis)
        return this
    }

    /**
     * Serializes the live semantic tree using a versioned deterministic format.
     *
     * Runtime identities, callback implementations, timestamps, and renderer-generated IDs are
     * intentionally excluded. Output is bounded by [MAX_SEMANTIC_SNAPSHOT_CHARS].
     *
     * @throws AssertionError when the bounded snapshot limit is exceeded
     */
    fun semanticSnapshot(): String {
        val nodes = snapshots()
        val byParent = nodes.groupBy { it.parentIdentity }
        val snapshot = buildString {
            appendLine("summon-semantic-snapshot:v1")
            fun appendNode(node: SemanticSnapshot, path: String) {
                append('{')
                appendJsonField("path", path)
                append(',')
                appendJsonField("role", node.role)
                append(',')
                appendJsonField("name", node.name)
                append(',')
                appendJsonField("tag", node.tag)
                append(",\"visible\":").append(node.connected && node.displayed && !node.inert)
                append(",\"disabled\":").append(!node.enabled)
                append(",\"inert\":").append(node.inert)
                append(",\"text\":")
                appendJsonString(node.text)
                append(",\"states\":{")
                node.states.entries.sortedBy { it.key }.forEachIndexed { index, (name, value) ->
                    if (index > 0) append(',')
                    appendJsonField(name, value)
                }
                appendLine("}}")
                byParent[node.identity].orEmpty().forEachIndexed { index, child ->
                    appendNode(child, "$path/$index")
                }
            }
            byParent[null].orEmpty().forEachIndexed { index, node -> appendNode(node, index.toString()) }
        }
        if (snapshot.length > MAX_SEMANTIC_SNAPSHOT_CHARS) {
            throw AssertionError("Semantic snapshot exceeds $MAX_SEMANTIC_SNAPSHOT_CHARS characters")
        }
        return snapshot
    }

    /** Compares the live tree with [expected] without creating or modifying a golden. */
    fun assertSemanticSnapshot(expected: String): ComponentHarness {
        val actual = semanticSnapshot()
        if (actual != expected) throw AssertionError(semanticDiff(expected, actual))
        return this
    }

    /** Closes the operation. */
    override fun close() = dispose()

    /** Disposes the operation. */
    fun dispose() {
        if (disposed) return
        disposed = true
        val owned = adapter
        adapter = null
        try {
            owned?.dispose()
        } finally {
            owned?.scheduler?.close()
        }
    }

    internal fun resolve(identity: Long): SemanticSnapshot? = snapshots().firstOrNull { it.identity == identity }

    internal fun requireLive(identity: Long): SemanticSnapshot = resolve(identity)
        ?: throw AssertionError("Semantic node handle is stale: node was detached or replaced")

    internal fun perform(identity: Long, operation: SemanticHarnessAdapter.() -> Unit) {
        requireLive(identity)
        adapter().operation()
        awaitIdle()
    }

    internal fun describe(): String {
        val nodes = snapshots()
        val byParent = nodes.groupBy { it.parentIdentity }
        val result = buildString {
            fun appendNode(node: SemanticSnapshot, depth: Int) {
                append("  ".repeat(depth)).append('<').append(node.elementName)
                node.tag?.let { append(" tag=\"").append(it).append('"') }
                if (!node.displayed) append(" hidden")
                if (node.inert) append(" inert")
                if (!node.enabled) append(" disabled")
                append('>')
                if (node.text.isNotBlank()) append(' ').append(node.text.take(160))
                append('\n')
                byParent[node.identity].orEmpty().forEach { appendNode(it, depth + 1) }
            }
            byParent[null].orEmpty().forEach { appendNode(it, 0) }
        }
        return if (result.length <= MAX_TREE_DESCRIPTION_CHARS) result else {
            result.take(MAX_TREE_DESCRIPTION_CHARS) + "\n… tree description truncated"
        }
    }

    private fun semanticDiff(expected: String, actual: String): String {
        val expectedLines = expected.lines()
        val actualLines = actual.lines()
        val first = (0 until maxOf(expectedLines.size, actualLines.size)).firstOrNull { index ->
            expectedLines.getOrNull(index) != actualLines.getOrNull(index)
        } ?: 0
        val actualPath = actualLines.getOrNull(first)?.substringAfter("\"path\":\"", "")?.substringBefore('"')
        val expectedPath = expectedLines.getOrNull(first)?.substringAfter("\"path\":\"", "")?.substringBefore('"')
        val path = actualPath?.takeIf { it.isNotEmpty() } ?: expectedPath?.takeIf { it.isNotEmpty() } ?: "<header>"
        val from = maxOf(0, first - 2)
        val to = minOf(maxOf(expectedLines.size, actualLines.size), first + 3)
        return buildString {
            appendLine("Semantic snapshot differs at path $path (line ${first + 1})")
            for (index in from until to) {
                expectedLines.getOrNull(index)?.let { append("- ").appendLine(it) }
                actualLines.getOrNull(index)?.let { append("+ ").appendLine(it) }
            }
        }.take(MAX_TREE_DESCRIPTION_CHARS)
    }

    private fun unique(label: String, predicate: (SemanticSnapshot) -> Boolean): SemanticNodeHandle {
        val matches = snapshots().filter(predicate)
        if (matches.size != 1) fail("Expected exactly one node matching $label, found ${matches.size}")
        return SemanticNodeHandle(this, matches.single().identity)
    }

    private fun snapshots(): List<SemanticSnapshot> {
        check(!disposed) { "Component harness is disposed" }
        return adapter().snapshot()
    }

    private fun adapter(): SemanticHarnessAdapter = adapter ?: error("Component harness is disposed")

    private fun fail(message: String): Nothing = throw AssertionError("$message\nSemantic tree:\n${describe()}")
    private fun StringBuilder.appendJsonField(name: String, value: String?) {
        appendJsonString(name)
        append(':')
        if (value == null) append("null") else appendJsonString(value)
    }

    private fun StringBuilder.appendJsonString(value: String) {
        append('"')
        value.forEach { character ->
            when (character) {
                '"' -> append("\\\"")
                '\\' -> append("\\\\")
                '\b' -> append("\\b")
                '\u000C' -> append("\\f")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> if (character.code < 0x20) {
                    append("\\u").append(character.code.toString(16).padStart(4, '0'))
                } else {
                    append(character)
                }
            }
        }
        append('"')
    }

}

/**
 * Identity-bearing semantic handle. Any operation after detach, replacement, or disposal fails.
 *
 * @property harness owning component harness
 * @property identity stable semantic-node identity
 */
class SemanticNodeHandle internal constructor(
    private val harness: ComponentHarness,
    private val identity: Long
) {
    /** Asserts that this handle still resolves to a live node. */
    fun assertExists(): SemanticNodeHandle {
        harness.requireLive(identity)
        return this
    }

    /** Asserts that this handle no longer resolves to a live node. */
    fun assertDoesNotExist(): SemanticNodeHandle {
        if (harness.resolve(identity) != null) throw AssertionError("Expected semantic node to be detached")
        return this
    }

    /** Asserts that the node is connected, visible, and not inert. */
    fun assertIsDisplayed(): SemanticNodeHandle {
        val node = harness.requireLive(identity)
        if (!node.displayed || node.inert || !node.connected) {
            throw AssertionError("Expected node to be displayed; connected=${node.connected}, displayed=${node.displayed}, inert=${node.inert}")
        }
        return this
    }

    /** Asserts that the node's complete text equals [expected]. */
    fun assertTextEquals(expected: String): SemanticNodeHandle {
        val actual = harness.requireLive(identity).text
        if (actual != expected) throw AssertionError("Expected text '$expected', found '$actual'")
        return this
    }

    /** Asserts that the node is enabled and not inert. */
    fun assertEnabled(): SemanticNodeHandle {
        val node = harness.requireLive(identity)
        if (!node.enabled || node.inert) throw AssertionError("Expected node to be enabled")
        return this
    }

    /** Asserts the named string state equals [expected]. */
    fun assertState(name: String, expected: String): SemanticNodeHandle = assertStateEncoded(name, expected)
    /** Asserts the named Boolean state equals [expected]. */
    fun assertState(name: String, expected: Boolean): SemanticNodeHandle = assertStateEncoded(name, expected.toString())
    /** Asserts the named integral state equals [expected]. */
    fun assertState(name: String, expected: Long): SemanticNodeHandle = assertStateEncoded(name, expected.toString())
    /** Asserts the named finite floating-point state equals [expected]. */
    fun assertState(name: String, expected: Double): SemanticNodeHandle {
        require(expected.isFinite()) { "Expected state must be finite" }
        return assertStateEncoded(name, expected.toString())
    }

    /** Clicks the live node and drains resulting recompositions. */
    fun performClick(): SemanticNodeHandle {
        harness.perform(identity) { click(identity) }
        return this
    }

    /** Replaces editable content with [value] and drains resulting recompositions. */
    fun performTextInput(value: String): SemanticNodeHandle {
        harness.perform(identity) { textInput(identity, value) }
        return this
    }

    /** Scrolls the live node into view and drains resulting recompositions. */
    fun performScrollTo(): SemanticNodeHandle {
        harness.perform(identity) { scrollTo(identity) }
        return this
    }

    private fun assertStateEncoded(name: String, expected: String): SemanticNodeHandle {
        val actual = harness.requireLive(identity).states[name]
            ?: throw AssertionError("Node has no test state '$name'")
        if (actual != expected) throw AssertionError("Expected state '$name'='$expected', found '$actual'")
        return this
    }
}

/**
 * Runs [block] and always disposes [harness], including when setup, an action, or an assertion
 * fails.
 *
 */
inline fun <T> withComponentHarness(harness: ComponentHarness, block: (ComponentHarness) -> T): T =
    try {
        block(harness)
    } finally {
        harness.dispose()
    }
