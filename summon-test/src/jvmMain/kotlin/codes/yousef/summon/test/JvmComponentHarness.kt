package codes.yousef.summon.test

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.CallbackRegistry
import codes.yousef.summon.runtime.PlatformRenderer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Mounts a simulated JVM component root using the actual SSR renderer and callback registry.
 * This adapter qualifies semantic rendering and callback ownership, not browser focus, DOM events,
 * storage, CSP, scrolling, or hydration.
 */
fun mountJvmComponentHarness(content: @Composable () -> Unit): ComponentHarness {
    val adapter = JvmSemanticAdapter(content)
    return ComponentHarness(adapter).awaitIdle()
}

private class JvmSemanticAdapter(
    private var content: (@Composable () -> Unit)?
) : SemanticHarnessAdapter {
    override val scheduler = HarnessScheduler()
    private val renderer = PlatformRenderer()
    private var document: Document? = null
    private var callbackIds = emptySet<String>()
    private var capability: String? = null
    private var generation = 0L
    private var disposed = false

    init {
        render()
    }

    override fun snapshot(): List<SemanticSnapshot> {
        check(!disposed) { "JVM component harness is disposed" }
        val root = requireNotNull(document).selectFirst("[data-summon-hydration=root]")
            ?: throw AssertionError("SSR harness root is missing")
        val snapshots = mutableListOf<SemanticSnapshot>()
        var localIdentity = 0L

        fun visit(element: Element, parentIdentity: Long?) {
            val identity = generation * 1_000_000L + localIdentity++
            val inert = element.ancestorsAndSelf(root).any { it.hasAttr("inert") }
            snapshots += SemanticSnapshot(
                identity = identity,
                parentIdentity = parentIdentity,
                elementName = element.tagName(),
                text = element.ownText().trim(),
                tag = element.attrOrNull(TEST_TAG_ATTRIBUTE),
                connected = true,
                displayed = element.isDisplayed(root),
                inert = inert,
                enabled = !inert && element.ancestorsAndSelf(root).none {
                    it.hasAttr("disabled") || it.attr("aria-disabled") == "true"
                },
                states = element.attributes().asList().mapNotNull { attribute ->
                    attribute.key.takeIf { it.startsWith(TEST_STATE_PREFIX) }
                        ?.removePrefix(TEST_STATE_PREFIX)
                        ?.let { it to attribute.value }
                }.toMap()
            )
            element.children().forEach { visit(it, identity) }
        }
        root.children().forEach { visit(it, null) }
        return snapshots
    }

    override fun click(identity: Long) {
        val element = requireElement(identity)
        val target = element.ancestorsAndSelf(requireNotNull(document).body())
            .firstOrNull { it.hasAttr("data-onclick-id") }
            ?: throw AssertionError("Node has no rendered click action")
        check(!target.hasAttr("disabled") && target.attr("aria-disabled") != "true") {
            "Cannot click a disabled node"
        }
        val callbackId = target.attr("data-onclick-id")
        check(CallbackRegistry.executeRemoteCallback(callbackId, capability)) {
            "Rendered click callback is unavailable"
        }
        render()
    }

    override fun textInput(identity: Long, value: String): Nothing = throw UnsupportedOperationException(
        "JVM harness is simulated: SSR does not expose browser input events; use mountBrowserComponentHarness"
    )

    override fun scrollTo(identity: Long): Nothing = throw UnsupportedOperationException(
        "JVM harness is simulated: scrolling requires mountBrowserComponentHarness"
    )

    override fun dispose() {
        if (disposed) return
        disposed = true
        revokeCallbacks()
        document = null
        content = null
    }

    private fun render() {
        val html = renderer.renderComposableRootWithHydration(requireNotNull(content))
        val parsed = Jsoup.parse(html)
        val hydration = parsed.selectFirst("#summon-hydration-data")?.data()
            ?: throw AssertionError("SSR hydration metadata is missing")
        val payload = Json.parseToJsonElement(hydration).jsonObject
        val nextIds = payload.getValue("callbacks").jsonArray.map { it.jsonPrimitive.content }.toSet()
        val nextCapability = payload.getValue("callbackContext").jsonPrimitive.content
        revokeCallbacks()
        document = parsed
        callbackIds = nextIds
        capability = nextCapability
        generation++
    }

    private fun revokeCallbacks() {
        CallbackRegistry.revokeRemoteCallbacks(callbackIds, capability)
        callbackIds = emptySet()
        capability = null
    }

    private fun requireElement(identity: Long): Element {
        val local = identity - generation * 1_000_000L
        if (local < 0) throw AssertionError("Semantic node handle is stale: SSR generation was replaced")
        val root = requireNotNull(document).selectFirst("[data-summon-hydration=root]")
            ?: throw AssertionError("SSR harness root is missing")
        return root.getAllElements().drop(1).getOrNull(local.toInt())
            ?: throw AssertionError("Semantic node handle is stale: SSR generation was replaced")
    }

    private fun Element.ancestorsAndSelf(boundary: Element): Sequence<Element> = sequence {
        var current: Element? = this@ancestorsAndSelf
        while (current != null) {
            yield(current)
            if (current === boundary) break
            current = current.parent()
        }
    }

    private fun Element.isDisplayed(boundary: Element): Boolean = ancestorsAndSelf(boundary).none { element ->
        element.hasAttr("hidden") ||
            element.attr("aria-hidden") == "true" ||
            element.styleProperty("display") == "none" ||
            element.styleProperty("visibility") in setOf("hidden", "collapse")
    }

    private fun Element.styleProperty(name: String): String? = attr("style")
        .split(';')
        .asSequence()
        .map { it.trim() }
        .firstOrNull { it.substringBefore(':').trim().equals(name, ignoreCase = true) }
        ?.substringAfter(':')
        ?.trim()
        ?.lowercase()

    private fun Element.attrOrNull(name: String): String? = attr(name).takeIf { hasAttr(name) }
}
