package codes.yousef.summon.devtools

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLStyleElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

private var nextRendererIdentity = 1L

/** Creates one isolated inspector session for an already mounted browser root. */
fun createBrowserInspectorSession(
    rootElementId: String,
    renderDispatcher: ((() -> Unit) -> Unit) = { action -> action() }
): InspectorSession {
    val root = document.getElementById(rootElementId) as? HTMLElement
        ?: throw IllegalArgumentException("Inspector root not found")
    val identity = "renderer-${nextRendererIdentity++}"
    return InspectorSession(DomInspectorTreeSource(root, identity), renderDispatcher)
}

private class DomInspectorTreeSource(
    private var root: HTMLElement?,
    override val rendererId: String
) : InspectorTreeSource {
    private val identities = mutableMapOf<Element, Long>()
    private val listeners = linkedSetOf<InspectorTreeListener>()
    private var nextIdentity = 0L
    private var nodes = scan()
    private var frameId: Int? = null
    private var highlightElement: HTMLElement? = null
    private var selectedId: InspectorNodeId? = null
    private var disposed = false

    init {
        scheduleFrame()
    }

    override fun snapshot(): List<InspectorNode> = nodes

    override fun observe(listener: InspectorTreeListener): InspectorDisposable {
        check(!disposed) { "Inspector tree source is disposed" }
        listeners += listener
        var active = true
        return InspectorDisposable {
            if (active) {
                active = false
                listeners -= listener
            }
        }
    }

    override fun highlight(nodeId: InspectorNodeId?): Boolean {
        if (disposed) return nodeId == null
        if (nodeId != null && nodeId.rendererId != rendererId) return false
        selectedId = nodeId
        if (nodeId == null) {
            removeHighlight()
            return true
        }
        val element = identities.entries.firstOrNull { (_, id) -> id == nodeId.localId }?.key
        if (element == null || !document.contains(element)) {
            removeHighlight()
            return false
        }
        val overlay = highlightElement ?: createHighlight().also { highlightElement = it }
        updateHighlight(overlay, element)
        return true
    }

    override fun dispose() {
        if (disposed) return
        disposed = true
        frameId?.let(window::cancelAnimationFrame)
        frameId = null
        removeHighlight()
        listeners.clear()
        identities.clear()
        nodes = emptyList()
        root = null
        selectedId = null
    }

    private fun scheduleFrame() {
        frameId = window.requestAnimationFrame {
            frameId = null
            if (!disposed) {
                val updated = scan()
                if (updated != nodes) {
                    nodes = updated
                    listeners.toList().forEach { it.onTreeChanged(updated) }
                }
                selectedId?.let(::highlight)
                scheduleFrame()
            }
        }
    }

    private fun scan(): List<InspectorNode> {
        val liveRoot = root ?: return emptyList()
        val result = mutableListOf<InspectorNode>()

        fun visit(element: Element, parentId: InspectorNodeId?) {
            val localId = identities.getOrPut(element) { nextIdentity++ }
            val id = InspectorNodeId(rendererId, localId)
            val childIds = buildList {
                for (index in 0 until element.children.length) {
                    val child = element.children.item(index) ?: continue
                    add(InspectorNodeId(rendererId, identities.getOrPut(child) { nextIdentity++ }))
                }
            }
            result += InspectorNode(
                id = id,
                parentId = parentId,
                label = element.inspectorLabel(),
                children = childIds,
                connected = document.contains(element)
            )
            for (index in 0 until element.children.length) {
                element.children.item(index)?.let { visit(it, id) }
            }
        }

        visit(liveRoot, null)
        val liveElements = result.mapTo(mutableSetOf()) { node -> node.id.localId }
        identities.entries.removeAll { (_, localId) -> localId !in liveElements }
        return result
    }

    private fun Element.inspectorLabel(): String {
        val explicit = getAttribute("data-summon-component")
        if (!explicit.isNullOrBlank()) return explicit.take(128)
        val testTag = getAttribute("data-testid")
        val tag = tagName.lowercase()
        return if (testTag.isNullOrBlank()) tag else "$tag[$testTag]".take(128)
    }

    private fun createHighlight(): HTMLElement {
        val element = document.createElement("div") as HTMLElement
        element.setAttribute("data-summon-inspector-highlight", rendererId)
        element.setAttribute("aria-hidden", "true")
        element.style.setProperty("position", "fixed")
        element.style.setProperty("pointer-events", "none")
        element.style.setProperty("z-index", "2147483646")
        element.style.setProperty("border", "2px solid #7c3aed")
        element.style.setProperty("background-color", "rgba(124, 58, 237, 0.12)")
        element.style.setProperty("box-sizing", "border-box")
        document.body?.appendChild(element)
        return element
    }

    private fun updateHighlight(overlay: HTMLElement, target: Element) {
        if (!document.contains(target)) {
            removeHighlight()
            return
        }
        val rect = target.getBoundingClientRect()
        overlay.style.setProperty("left", "${rect.left}px")
        overlay.style.setProperty("top", "${rect.top}px")
        overlay.style.setProperty("width", "${rect.width}px")
        overlay.style.setProperty("height", "${rect.height}px")
    }

    private fun removeHighlight() {
        highlightElement?.remove()
        highlightElement = null
    }
}

/** Owned, keyboard-accessible browser panel for one [InspectorSession]. */
class BrowserInspectorOverlay(
    private val session: InspectorSession,
    styleNonce: String? = null
) {
    private val panel = document.createElement("aside") as HTMLElement
    private val tree = document.createElement("div") as HTMLElement
    private val fields = document.createElement("div") as HTMLElement
    private val timelinePanel = document.createElement("section") as HTMLElement
    private val timelineEntries = document.createElement("div") as HTMLElement
    private val sessionText = document.createElement("textarea") as HTMLTextAreaElement
    private val status = document.createElement("div") as HTMLElement
    private val style = document.createElement("style") as HTMLStyleElement
    private val timeline = session.createTimeline()
    private val collapsed = mutableSetOf<InspectorNodeId>()
    private var selected: InspectorNodeId? = null
    private var importedPlan: DebugSessionPlan? = null
    private var timelineFrameId: Int? = null
    private var timelineSignature: String? = null
    private var disposed = false
    private val treeSubscription: InspectorDisposable
    private val keyListener: (Event) -> Unit = { event -> onKey(event as KeyboardEvent) }

    init {
        val resolvedStyleNonce = styleNonce
            ?: document.querySelector("meta[name='summon-style-nonce']")?.getAttribute("content")
        resolvedStyleNonce?.let { style.setAttribute("nonce", it) }
        style.setAttribute("data-summon-inspector-style", session.rendererId)
        style.textContent = """
            [data-summon-inspector-panel] { position: fixed; inset: 12px 12px auto auto; width: 340px; max-height: calc(100vh - 24px); overflow: auto; z-index: 2147483647; color: #f8fafc; background: #111827; border: 1px solid #475569; border-radius: 8px; padding: 12px; font: 13px/1.4 system-ui, sans-serif; }
            [data-summon-inspector-panel] button, [data-summon-inspector-panel] input { font: inherit; }
            [data-summon-inspector-tree] button { display: block; width: 100%; color: inherit; background: transparent; border: 0; padding: 3px 4px; text-align: left; }
            [data-summon-inspector-tree] button[aria-selected='true'] { background: #4338ca; }
            [data-summon-inspector-fields] { border-top: 1px solid #475569; margin-top: 8px; padding-top: 8px; }
            [data-summon-inspector-status] { color: #fca5a5; min-height: 1.4em; }
            [data-summon-inspector-timeline] { border-top: 1px solid #475569; margin-top: 8px; padding-top: 8px; }
            [data-summon-inspector-timeline] textarea { box-sizing: border-box; width: 100%; min-height: 54px; color: #111827; }
            [data-summon-inspector-timeline-entries] button { display: block; width: 100%; text-align: left; }
        """.trimIndent()
        document.head?.appendChild(style)
        panel.setAttribute("data-summon-inspector-panel", session.rendererId)
        panel.setAttribute("role", "dialog")
        panel.setAttribute("aria-label", "Summon component inspector")
        panel.tabIndex = -1

        val header = document.createElement("div") as HTMLElement
        val title = document.createElement("strong") as HTMLElement
        title.textContent = "Summon Inspector"
        val close = document.createElement("button") as HTMLElement
        close.textContent = "Close"
        close.setAttribute("type", "button")
        close.setAttribute("aria-label", "Close inspector")
        close.addEventListener("click", { dispose() })
        header.appendChild(title)
        header.appendChild(close)
        tree.setAttribute("data-summon-inspector-tree", "")
        tree.setAttribute("role", "tree")
        fields.setAttribute("data-summon-inspector-fields", "")
        createTimelinePanel()
        status.setAttribute("data-summon-inspector-status", "")
        status.setAttribute("role", "status")
        panel.appendChild(header)
        panel.appendChild(tree)
        panel.appendChild(fields)
        panel.appendChild(timelinePanel)
        panel.appendChild(status)
        panel.addEventListener("keydown", keyListener)
        document.body?.appendChild(panel)
        treeSubscription = session.observeTree { render() }
        panel.focus()
        scheduleTimelineFrame()
    }

    fun dispose() {
        if (disposed) return
        disposed = true
        timelineFrameId?.let(window::cancelAnimationFrame)
        timelineFrameId = null
        timeline.dispose()
        importedPlan = null
        sessionText.value = ""
        treeSubscription.dispose()
        panel.removeEventListener("keydown", keyListener)
        session.highlight(null)
        panel.remove()
        style.remove()
        selected = null
        collapsed.clear()
    }

    private fun render() {
        if (disposed) return
        tree.textContent = ""
        val nodes = session.tree()
        if (selected == null || nodes.none { it.id == selected }) selected = nodes.firstOrNull()?.id
        val byId = nodes.associateBy { it.id }

        fun append(node: InspectorNode, depth: Int) {
            val button = document.createElement("button") as HTMLElement
            button.setAttribute("type", "button")
            button.setAttribute("role", "treeitem")
            button.setAttribute("aria-level", (depth + 1).toString())
            button.setAttribute("aria-expanded", (node.id !in collapsed).toString())
            button.setAttribute("aria-selected", (node.id == selected).toString())
            button.style.setProperty("padding-left", "${depth * 16 + 4}px")
            button.textContent = (if (node.id in collapsed) "▸ " else "▾ ") + node.label
            button.addEventListener("click", {
                select(node.id)
                if (node.children.isNotEmpty()) {
                    if (!collapsed.add(node.id)) collapsed.remove(node.id)
                }
                render()
            })
            tree.appendChild(button)
            if (node.id !in collapsed) node.children.mapNotNull(byId::get).forEach { append(it, depth + 1) }
        }

        nodes.filter { it.parentId == null }.forEach { append(it, 0) }
        selected?.let(::renderFields)
    }

    private fun renderFields(nodeId: InspectorNodeId) {
        fields.textContent = ""
        session.fields(nodeId).forEach { field ->
            val row = document.createElement("div") as HTMLElement
            val label = document.createElement("label") as HTMLElement
            label.textContent = field.name + ": "
            row.appendChild(label)
            if (field.sensitivity == DebugFieldSensitivity.REDACTED) {
                val value = document.createElement("span") as HTMLElement
                value.textContent = InspectorField.REDACTION_MARKER
                row.appendChild(value)
            } else if (field.editable) {
                val input = document.createElement("input") as HTMLInputElement
                input.value = field.value.editText()
                input.setAttribute("aria-label", "Edit ${field.name}")
                label.appendChild(input)
                val apply = document.createElement("button") as HTMLElement
                apply.textContent = "Apply"
                apply.setAttribute("type", "button")
                apply.addEventListener("click", {
                    val parsed = parseEdit(field.typeName, input.value)
                    val result = parsed?.let { session.edit(nodeId, field.name, it) }
                        ?: InspectorEditResult.Rejected("Invalid ${field.typeName} value")
                    status.textContent = (result as? InspectorEditResult.Rejected)?.reason ?: ""
                    renderFields(nodeId)
                })
                row.appendChild(apply)
            } else {
                val value = document.createElement("span") as HTMLElement
                value.textContent = field.value.editText()
                row.appendChild(value)
            }
            fields.appendChild(row)
        }
    }

    private fun createTimelinePanel() {
        timelinePanel.setAttribute("data-summon-inspector-timeline", "")
        val heading = document.createElement("strong") as HTMLElement
        heading.textContent = "State timeline"
        timelinePanel.appendChild(heading)
        listOf(
            "Record" to {
                timeline.start()
                status.textContent = ""
            },
            "Pause" to { timeline.pause() },
            "Stop" to { timeline.stop() },
            "Clear timeline" to {
                timeline.clear()
                importedPlan = null
                sessionText.value = ""
            },
            "Replay timeline" to {
                status.textContent = when (val result = timeline.replay()) {
                    is TimelineReplayResult.Completed -> ""
                    is TimelineReplayResult.Failed -> "Replay stopped at #${result.sequence}: ${result.reason}"
                }
                selected?.let(::renderFields)
            },
            "Export session" to {
                sessionText.value = timeline.exportSession()
                importedPlan = null
                status.textContent = ""
            },
            "Validate import" to {
                try {
                    importedPlan = timeline.importSession(sessionText.value)
                    status.textContent = "Import validated; application state is unchanged"
                } catch (error: IllegalArgumentException) {
                    importedPlan = null
                    status.textContent = error.message ?: "Invalid debug session"
                }
            },
            "Apply import" to {
                val plan = importedPlan
                if (plan == null) {
                    status.textContent = "Validate an import before applying it"
                } else {
                    val result = timeline.apply(plan)
                    status.textContent = if (result.complete) "" else {
                        "${result.unrestorableFields.size} fields could not be restored"
                    }
                    selected?.let(::renderFields)
                }
            }
        ).forEach { (label, action) ->
            val button = document.createElement("button") as HTMLElement
            button.typeButton()
            button.textContent = label
            button.setAttribute("aria-label", label)
            button.addEventListener("click", {
                action()
                renderTimeline()
            })
            timelinePanel.appendChild(button)
        }
        sessionText.setAttribute("aria-label", "Debug session JSON")
        timelinePanel.appendChild(sessionText)
        timelineEntries.setAttribute("data-summon-inspector-timeline-entries", "")
        timelinePanel.appendChild(timelineEntries)
        renderTimeline()
    }

    private fun renderTimeline() {
        if (disposed) return
        val entries = timeline.entries
        val signature = "${timeline.recordingState}:${timeline.appliedSequence}:${entries.size}:${entries.lastOrNull()?.sequence}"
        if (signature == timelineSignature) return
        timelineSignature = signature
        timelineEntries.textContent = ""
        val state = document.createElement("div") as HTMLElement
        state.setAttribute("data-summon-timeline-state", timeline.recordingState.name.lowercase())
        state.textContent = "${timeline.recordingState.name.lowercase()} · ${entries.size} mutations"
        timelineEntries.appendChild(state)
        val baseline = document.createElement("button") as HTMLElement
        baseline.typeButton()
        baseline.textContent = "Restore recording start"
        baseline.addEventListener("click", {
            showRestoreResult(timeline.restoreTo(null))
            renderTimeline()
            selected?.let(::renderFields)
        })
        timelineEntries.appendChild(baseline)
        entries.forEach { entry ->
            val button = document.createElement("button") as HTMLElement
            button.typeButton()
            button.setAttribute("aria-label", "Restore mutation ${entry.sequence}")
            button.textContent = "#${entry.sequence} ${entry.fieldId.name}: ${entry.before.editText()} → ${entry.after.editText()}"
            button.addEventListener("click", {
                showRestoreResult(timeline.restoreTo(entry.sequence))
                renderTimeline()
                selected?.let(::renderFields)
            })
            timelineEntries.appendChild(button)
        }
    }

    private fun showRestoreResult(result: TimelineRestoreResult) {
        status.textContent = if (result.complete) "" else {
            "${result.unrestorableFields.size} fields could not be restored"
        }
    }

    private fun scheduleTimelineFrame() {
        if (disposed) return
        timelineFrameId = window.requestAnimationFrame {
            timelineFrameId = null
            timeline.sample()
            renderTimeline()
            scheduleTimelineFrame()
        }
    }

    private fun HTMLElement.typeButton() {
        setAttribute("type", "button")
    }

    private fun select(nodeId: InspectorNodeId) {
        selected = nodeId
        session.highlight(nodeId)
    }

    private fun onKey(event: KeyboardEvent) {
        val nodes = session.tree()
        if (nodes.isEmpty()) return
        val byId = nodes.associateBy { it.id }
        val visible = mutableListOf<InspectorNode>()
        fun appendVisible(node: InspectorNode) {
            visible += node
            if (node.id !in collapsed) node.children.mapNotNull(byId::get).forEach(::appendVisible)
        }
        nodes.filter { it.parentId == null }.forEach(::appendVisible)
        val index = visible.indexOfFirst { it.id == selected }.coerceAtLeast(0)
        val current = visible[index]
        val next = when (event.key) {
            "ArrowDown" -> visible[(index + 1).coerceAtMost(visible.lastIndex)].id
            "ArrowUp" -> visible[(index - 1).coerceAtLeast(0)].id
            "ArrowLeft" -> {
                if (current.children.isNotEmpty() && current.id !in collapsed) {
                    collapsed += current.id
                    current.id
                } else {
                    current.parentId ?: current.id
                }
            }
            "ArrowRight" -> {
                if (current.id in collapsed) {
                    collapsed -= current.id
                    current.id
                } else {
                    current.children.firstOrNull() ?: current.id
                }
            }
            "Escape" -> {
                dispose()
                return
            }
            else -> return
        }
        event.preventDefault()
        select(next)
        render()
    }

    private fun DebugValue?.editText(): String = when (this) {
        null, DebugValue.Null -> "null"
        is DebugValue.BooleanValue -> value.toString()
        is DebugValue.StringValue -> value
        is DebugValue.LongValue -> value.toString()
        is DebugValue.DoubleValue -> value.toString()
    }

    private fun parseEdit(typeName: String?, value: String): DebugValue? {
        if (value == "null" && typeName?.endsWith('?') == true) return DebugValue.Null
        return when (typeName) {
            "boolean?" -> value.toBooleanStrictOrNull()?.let(DebugValue::BooleanValue)
            "string?" -> runCatching {
                DebugFieldCodecs.requireBoundedString(value)
                DebugValue.StringValue(value)
            }.getOrNull()
            "long?" -> value.toLongOrNull()?.let(DebugValue::LongValue)
            "double?" -> value.toDoubleOrNull()?.takeIf(Double::isFinite)?.let(DebugValue::DoubleValue)
            else -> null
        }
    }
}
