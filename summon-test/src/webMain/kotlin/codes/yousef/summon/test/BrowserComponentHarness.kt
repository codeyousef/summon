package codes.yousef.summon.test

import codes.yousef.summon.MountedComposition
import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.mountComposableRoot
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.Node

/** Mounts one actual JS/WASM DOM root. The caller supplies an existing empty root element ID. */
fun mountBrowserComponentHarness(
    rootElementId: String,
    content: @Composable () -> Unit
): ComponentHarness {
    val root = document.getElementById(rootElementId) as? HTMLElement
        ?: throw IllegalArgumentException("Harness root '$rootElementId' was not found")
    val scheduler = HarnessScheduler()
    val mount = mountComposableRoot(rootElementId, scheduler, content)
    return ComponentHarness(BrowserSemanticAdapter(root, mount, scheduler)).awaitIdle()
}

private class BrowserSemanticAdapter(
    private var root: HTMLElement?,
    private var mount: MountedComposition?,
    override val scheduler: HarnessScheduler
) : SemanticHarnessAdapter {
    private val identities = mutableMapOf<Element, Long>()
    private var nextIdentity = 1L
    private var disposed = false

    override fun snapshot(): List<SemanticSnapshot> {
        check(!disposed) { "Browser component harness is disposed" }
        val ownedRoot = requireNotNull(root)
        val live = linkedSetOf<Element>()
        val snapshots = mutableListOf<SemanticSnapshot>()

        fun visit(element: Element, parentIdentity: Long?) {
            live += element
            val identity = identities.getOrPut(element) { nextIdentity++ }
            val inert = element.hasInertAncestor(ownedRoot)
            snapshots += SemanticSnapshot(
                identity = identity,
                parentIdentity = parentIdentity,
                elementName = element.tagName.lowercase(),
                text = element.directText(),
                tag = element.getAttribute(TEST_TAG_ATTRIBUTE),
                connected = element.isConnected,
                displayed = element.isActuallyDisplayed(ownedRoot),
                role = semanticRole(
                    elementName = element.tagName,
                    explicitRole = element.getAttribute("role"),
                    inputType = element.getAttribute("type")
                ),
                name = element.getAttribute("aria-label")
                    ?: element.getAttribute("alt")
                    ?: element.textContent.orEmpty().trim(),
                inert = inert,
                enabled = !inert && !element.hasDisabledAncestor(ownedRoot),
                states = element.testStates()
            )
            var child = element.firstElementChild
            while (child != null) {
                visit(child, identity)
                child = child.nextElementSibling
            }
        }

        var child = ownedRoot.firstElementChild
        while (child != null) {
            visit(child, null)
            child = child.nextElementSibling
        }
        identities.keys.removeAll { it !in live }
        return snapshots
    }

    override fun click(identity: Long) {
        val element = requireElement(identity)
        check(!element.hasDisabledAncestor(requireNotNull(root))) { "Cannot click a disabled or inert node" }
        element.dispatchBubbling("pointerdown")
        element.dispatchBubbling("pointerup")
        element.dispatchBubbling("click")
    }

    override fun textInput(identity: Long, value: String) {
        val element = requireElement(identity)
        check(!element.hasDisabledAncestor(requireNotNull(root))) { "Cannot type into a disabled or inert node" }
        when (element) {
            is HTMLInputElement -> {
                element.focus()
                element.value = value
            }
            is HTMLTextAreaElement -> {
                element.focus()
                element.value = value
            }
            else -> throw AssertionError("Text input requires an input or textarea node")
        }
        element.dispatchBubbling("input")
        element.dispatchBubbling("change")
    }

    override fun scrollTo(identity: Long) {
        (requireElement(identity) as? HTMLElement)?.scrollIntoView()
            ?: throw AssertionError("Scroll requires an HTML element")
        requireElement(identity).dispatchBubbling("scroll")
    }

    override fun dispose() {
        if (disposed) return
        disposed = true
        try {
            mount?.dispose()
        } finally {
            mount = null
            identities.clear()
            root?.textContent = ""
            root = null
        }
    }

    private fun requireElement(identity: Long): Element =
        identities.entries.firstOrNull { it.value == identity && it.key.isConnected }?.key
            ?: throw AssertionError("Semantic node handle is stale: node was detached or replaced")

    private fun Element.dispatchBubbling(type: String) {
        val event = document.createEvent("Event")
        event.initEvent(type, true, true)
        dispatchEvent(event)
    }

    private fun Element.directText(): String {
        val text = buildString {
            var child: Node? = firstChild
            while (child != null) {
                if (child.nodeType == Node.TEXT_NODE) append(child.nodeValue.orEmpty())
                child = child.nextSibling
            }
        }
        return text
    }

    private fun Element.isActuallyDisplayed(boundary: Element): Boolean {
        var current: Element? = this
        while (current != null) {
            if (current.hasAttribute("hidden") || current.getAttribute("aria-hidden") == "true") return false
            val style = window.getComputedStyle(current)
            if (style.display == "none" || style.visibility == "hidden" || style.visibility == "collapse") return false
            if (current === boundary) break
            current = current.parentElement
        }
        return isConnected
    }

    private fun Element.hasInertAncestor(boundary: Element): Boolean {
        var current: Element? = this
        while (current != null) {
            if (current.hasAttribute("inert")) return true
            if (current === boundary) break
            current = current.parentElement
        }
        return false
    }

    private fun Element.hasDisabledAncestor(boundary: Element): Boolean {
        var current: Element? = this
        while (current != null) {
            if (current.hasAttribute("disabled") || current.getAttribute("aria-disabled") == "true" || current.hasAttribute("inert")) {
                return true
            }
            if (current === boundary) break
            current = current.parentElement
        }
        return false
    }

    private fun Element.testStates(): Map<String, String> = buildMap {
        val attributes = this@testStates.attributes
        for (index in 0 until attributes.length) {
            val attribute = attributes.item(index) ?: continue
            if (attribute.name.startsWith(TEST_STATE_PREFIX)) {
                put(attribute.name.removePrefix(TEST_STATE_PREFIX), attribute.value)
            }
        }
    }
}
