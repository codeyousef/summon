package codes.yousef.summon.devtoolsfixture

import codes.yousef.summon.devtools.BrowserInspectorOverlay
import codes.yousef.summon.devtools.DebugFieldCodec
import codes.yousef.summon.devtools.DebugFieldCodecs
import codes.yousef.summon.devtools.DebugValue
import codes.yousef.summon.devtools.createBrowserInspectorSession
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

private val intCodec = object : DebugFieldCodec<Int> {
    override val typeName: String = "long?"
    override fun encode(value: Int): DebugValue = DebugValue.LongValue(value.toLong())
    override fun decode(value: DebugValue): Int {
        val number = (value as? DebugValue.LongValue)?.value
            ?: throw IllegalArgumentException("Expected integral number")
        require(number in 0..Int.MAX_VALUE.toLong()) { "Count must be in Int range and nonnegative" }
        return number.toInt()
    }
}

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
fun installInspector(first: InspectorFixture, second: InspectorFixture) {
    window.setTimeout({
        val firstSession = createBrowserInspectorSession("root")
        val firstRoot = firstSession.tree().first()
        firstSession.registerPublicField(firstRoot.id, "count", intCodec, { first.count.value }) {
            first.count.value = it
        }
        firstSession.registerPublicField(firstRoot.id, "rootName", DebugFieldCodecs.nullableString, { "first" })
        firstSession.registerPublicField(firstRoot.id, "version", intCodec, { 1 })
        firstSession.registerRedactedField(firstRoot.id, "credential")

        val secondSession = createBrowserInspectorSession("second-root")
        val secondRoot = secondSession.tree().first()
        secondSession.registerPublicField(secondRoot.id, "count", intCodec, { second.count.value }) {
            second.count.value = it
        }
        secondSession.registerPublicField(secondRoot.id, "rootName", DebugFieldCodecs.nullableString, { "second" })
        secondSession.registerRedactedField(secondRoot.id, "privateToken")

        val redactedReads = document.createElement("output") as HTMLElement
        redactedReads.setAttribute("data-testid", "redacted-getter-reads")
        redactedReads.textContent = "0"
        document.body?.appendChild(redactedReads)

        var firstOverlay: BrowserInspectorOverlay? = null
        var secondOverlay: BrowserInspectorOverlay? = null
        fun openFirst() {
            firstOverlay?.dispose()
            firstOverlay = BrowserInspectorOverlay(firstSession)
        }
        fun openSecond() {
            secondOverlay?.dispose()
            secondOverlay = BrowserInspectorOverlay(secondSession)
        }

        val reopenFirst = document.createElement("button") as HTMLElement
        reopenFirst.setAttribute("type", "button")
        reopenFirst.setAttribute("data-testid", "open-first-inspector")
        reopenFirst.textContent = "Open first inspector"
        reopenFirst.addEventListener("click", { openFirst() })
        document.body?.appendChild(reopenFirst)

        val reopenSecond = document.createElement("button") as HTMLElement
        reopenSecond.setAttribute("type", "button")
        reopenSecond.setAttribute("data-testid", "open-second-inspector")
        reopenSecond.textContent = "Open second inspector"
        reopenSecond.addEventListener("click", { openSecond() })
        document.body?.appendChild(reopenSecond)
        openFirst()
        installErrorOverlayFixture()
        null
    }, 0)
}
