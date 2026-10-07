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

const val TEST_TAG_ATTRIBUTE: String = "data-summon-test-tag"
const val TEST_STATE_PREFIX: String = "data-summon-test-state-"

internal data class SemanticSnapshot(
    val identity: Long,
    val parentIdentity: Long?,
    val elementName: String,
    val text: String,
    val tag: String?,
    val connected: Boolean,
    val displayed: Boolean,
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
    companion object {
        const val DEFAULT_MAX_PENDING_WORK: Int = 10_000
    }

    private val pending = ArrayDeque<() -> Unit>()
    private var closed = false

    val pendingWorkCount: Int get() = pending.size

    override fun scheduleRecomposition(work: () -> Unit) {
        check(!closed) { "Harness scheduler is disposed" }
        check(pending.size < DEFAULT_MAX_PENDING_WORK) { "Harness pending-work capacity exceeded" }
        pending.addLast(work)
    }

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
 */
class ComponentHarness internal constructor(private var adapter: SemanticHarnessAdapter?) : AutoCloseable {
    companion object {
        const val DEFAULT_IDLE_TIMEOUT_MS: Long = 1_000
        const val MAX_TREE_DESCRIPTION_CHARS: Int = 8_192
    }

    private var disposed = false

    fun onNodeWithText(text: String, substring: Boolean = false): SemanticNodeHandle {
        require(text.isNotEmpty()) { "Text matcher must not be empty" }
        return unique("text '$text'") { node ->
            if (substring) node.text.contains(text) else node.text == text
        }
    }

    fun onNodeWithTag(tag: String): SemanticNodeHandle {
        require(tag.isNotEmpty()) { "Tag matcher must not be empty" }
        return unique("tag '$tag'") { it.tag == tag }
    }

    fun assertNoNodeWithText(text: String, substring: Boolean = false): ComponentHarness {
        val matches = snapshots().filter { if (substring) it.text.contains(text) else it.text == text }
        if (matches.isNotEmpty()) fail("Expected no node with text '$text', found ${matches.size}")
        return this
    }

    fun assertNoNodeWithTag(tag: String): ComponentHarness {
        val matches = snapshots().filter { it.tag == tag }
        if (matches.isNotEmpty()) fail("Expected no node with tag '$tag', found ${matches.size}")
        return this
    }

    fun awaitIdle(timeoutMillis: Long = DEFAULT_IDLE_TIMEOUT_MS): ComponentHarness {
        adapter().scheduler.drain(timeoutMillis)
        return this
    }

    override fun close() = dispose()

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
}

/** Identity-bearing semantic handle. Any operation after detach, replacement, or disposal fails. */
class SemanticNodeHandle internal constructor(
    private val harness: ComponentHarness,
    private val identity: Long
) {
    fun assertExists(): SemanticNodeHandle {
        harness.requireLive(identity)
        return this
    }

    fun assertDoesNotExist(): SemanticNodeHandle {
        if (harness.resolve(identity) != null) throw AssertionError("Expected semantic node to be detached")
        return this
    }

    fun assertIsDisplayed(): SemanticNodeHandle {
        val node = harness.requireLive(identity)
        if (!node.displayed || node.inert || !node.connected) {
            throw AssertionError("Expected node to be displayed; connected=${node.connected}, displayed=${node.displayed}, inert=${node.inert}")
        }
        return this
    }

    fun assertTextEquals(expected: String): SemanticNodeHandle {
        val actual = harness.requireLive(identity).text
        if (actual != expected) throw AssertionError("Expected text '$expected', found '$actual'")
        return this
    }

    fun assertEnabled(): SemanticNodeHandle {
        val node = harness.requireLive(identity)
        if (!node.enabled || node.inert) throw AssertionError("Expected node to be enabled")
        return this
    }

    fun assertState(name: String, expected: String): SemanticNodeHandle = assertStateEncoded(name, expected)
    fun assertState(name: String, expected: Boolean): SemanticNodeHandle = assertStateEncoded(name, expected.toString())
    fun assertState(name: String, expected: Long): SemanticNodeHandle = assertStateEncoded(name, expected.toString())
    fun assertState(name: String, expected: Double): SemanticNodeHandle {
        require(expected.isFinite()) { "Expected state must be finite" }
        return assertStateEncoded(name, expected.toString())
    }

    fun performClick(): SemanticNodeHandle {
        harness.perform(identity) { click(identity) }
        return this
    }

    fun performTextInput(value: String): SemanticNodeHandle {
        harness.perform(identity) { textInput(identity, value) }
        return this
    }

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

/** Runs [block] and always disposes [harness], including when setup, actions, or assertions fail. */
inline fun <T> withComponentHarness(harness: ComponentHarness, block: (ComponentHarness) -> T): T =
    try {
        block(harness)
    } finally {
        harness.dispose()
    }
