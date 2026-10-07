package codes.yousef.summon.devtools

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.ErrorEvent
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLStyleElement
import org.w3c.dom.events.Event
import org.w3c.dom.url.URL

/**
 * Explicit debug-only configuration.
 *
 * [sourceMaps] must be trusted same-origin assets tied exactly to [buildId]. [links] is opt-in and
 * produces no source link unless its revision, origin, workspace root, and protocol all validate.
 * The overlay is supported only by browser JS/Wasm source sets.

 * @property buildId The build id value.
 * @property sourceMaps The source maps value.
 * @property textPolicy The text policy value.
 * @property links The links value.
 * @property styleNonce The style nonce value.
 */
data class BrowserErrorOverlayConfig(
    val buildId: String,
    val sourceMaps: List<VerifiedSourceMapAsset> = emptyList(),
    val textPolicy: DevelopmentErrorTextPolicy = DevelopmentErrorTextPolicy.GENERIC_ONLY,
    val links: DevelopmentSourceLinkPolicy = DevelopmentSourceLinkPolicy(null, null, null, null),
    val styleNonce: String? = null
)

/**
 * Installs removable `error` and `unhandledrejection` listeners and a CSP-compatible development
 * overlay. The host event is observed but never canceled. Dispose the returned owner before
 * releasing the debug root.
 *
 * @throws IllegalArgumentException if a source-map URL is cross-origin or embeds credentials
 */
fun installBrowserErrorOverlay(config: BrowserErrorOverlayConfig): BrowserErrorOverlay {
    config.sourceMaps.forEach { asset ->
        val assetUrl = URL(asset.generatedAssetUrl, window.location.href)
        require(
            assetUrl.origin == window.location.origin &&
                assetUrl.username.isEmpty() &&
                assetUrl.password.isEmpty()
        ) { "Source maps must belong to the current origin without credentials" }
    }
    return BrowserErrorOverlay(config, VerifiedSourceMapCatalog.parse(config.buildId, config.sourceMaps))
}

/**
 * Owned development error panel returned by [installBrowserErrorOverlay].
 *
 * @property config The config value.
 */
class BrowserErrorOverlay internal constructor(
    private val config: BrowserErrorOverlayConfig,
    catalog: VerifiedSourceMapCatalog
) : InspectorDisposable {
    /** Provides browser error overlay factory and constant members. */
    companion object {
        /** The property declaration value. */
        const val MAX_ERRORS: Int = 50
    }

    private var sourceMaps: VerifiedSourceMapCatalog? = catalog
    private val errors = ArrayDeque<DevelopmentError>()
    private val fingerprints = linkedSetOf<String>()
    private val panel = document.createElement("aside") as HTMLElement
    private val list = document.createElement("ol") as HTMLElement
    private val style = document.createElement("style") as HTMLStyleElement
    private var disposed = false
    private val errorListener: (Event) -> Unit = { event -> observeScriptError(event) }
    private val rejectionListener: (Event) -> Unit = {
        record(DevelopmentErrorCategory.UNHANDLED_REJECTION, null, null, synthetic = false)
    }

    /** Immutable snapshots currently rendered by the overlay. */
    val entries: List<DevelopmentError> get() = errors.toList()

    init {
        val nonce = config.styleNonce
            ?: document.querySelector("meta[name='summon-style-nonce']")?.getAttribute("content")
        nonce?.let { style.setAttribute("nonce", it) }
        style.setAttribute("data-summon-error-overlay-style", config.buildId)
        style.textContent = """
            [data-summon-error-overlay] { position: fixed; inset: 12px 12px auto auto; width: min(520px, calc(100vw - 24px)); max-height: calc(100vh - 24px); overflow: auto; z-index: 2147483647; color: #fff; background: #3f0d12; border: 1px solid #f87171; border-radius: 8px; padding: 12px; font: 13px/1.4 system-ui, sans-serif; }
            [data-summon-error-overlay] button, [data-summon-error-overlay] a { font: inherit; color: inherit; }
            [data-summon-error-entry] { margin: 8px 0; overflow-wrap: anywhere; }
            [data-summon-error-location] { display: block; color: #fecaca; }
        """.trimIndent()
        document.head?.appendChild(style)
        panel.setAttribute("data-summon-error-overlay", config.buildId)
        panel.setAttribute("role", "dialog")
        panel.setAttribute("aria-label", "Summon development errors")
        panel.setAttribute("aria-live", "assertive")
        panel.setAttribute("hidden", "")
        val header = document.createElement("header") as HTMLElement
        val title = document.createElement("strong") as HTMLElement
        title.textContent = "Development error"
        val close = document.createElement("button") as HTMLElement
        close.textContent = "Close"
        close.setAttribute("type", "button")
        close.setAttribute("aria-label", "Close development error overlay")
        close.addEventListener("click", { dispose() })
        header.appendChild(title)
        header.appendChild(close)
        panel.appendChild(header)
        panel.appendChild(list)
        document.body?.appendChild(panel)
        window.addEventListener("error", errorListener)
        window.addEventListener("unhandledrejection", rejectionListener)
    }

    /** Reports a caller-declared synthetic/public error, including an optional bounded stack. */
    fun reportSynthetic(
        category: DevelopmentErrorCategory,
        text: String,
        stack: String? = null
    ) {
        record(category, text, stack, synthetic = true)
    }

    /**
     * Records a caught Summon error-boundary failure without changing its recovery action.
     * Exception text is not rendered; only structural stack locations are parsed.
     */
    fun reportErrorBoundary(error: Throwable, publicText: String = "UI rendering failed") {
        reportSynthetic(DevelopmentErrorCategory.ERROR_BOUNDARY, publicText, error.stackTraceToString())
    }

    /** Removes listeners and DOM nodes and clears retained errors and source-map data. Idempotent. */
    override fun dispose() {
        if (disposed) return
        disposed = true
        window.removeEventListener("error", errorListener)
        window.removeEventListener("unhandledrejection", rejectionListener)
        panel.remove()
        style.remove()
        errors.clear()
        fingerprints.clear()
        sourceMaps = null
    }

    private fun observeScriptError(event: Event) {
        val error = event as? ErrorEvent
        val frame = error?.filename?.takeIf { it.isNotBlank() }?.let { asset ->
            val line = error.lineno
            val column = error.colno
            if (line > 0 && column > 0) "$asset:$line:$column" else null
        }
        record(DevelopmentErrorCategory.SCRIPT_ERROR, null, frame, synthetic = false)
    }

    private fun record(
        category: DevelopmentErrorCategory,
        text: String?,
        stack: String?,
        synthetic: Boolean
    ) {
        if (disposed) return
        val frames = DevelopmentStackParser.parse(stack).map { generated ->
            DevelopmentErrorFrame(generated, sourceMaps?.map(generated))
        }
        val display = DevelopmentStackParser.displayText(
            category,
            text.takeIf { synthetic },
            config.textPolicy
        )
        val fingerprint = "$category|$display|${frames.firstOrNull()?.generated}"
        if (!fingerprints.add(fingerprint)) return
        if (errors.size == MAX_ERRORS) {
            val removed = errors.removeFirst()
            fingerprints.remove("${removed.category}|${removed.text}|${removed.frames.firstOrNull()?.generated}")
        }
        errors.addLast(DevelopmentError(category, display, frames))
        render()
    }

    private fun render() {
        panel.removeAttribute("hidden")
        list.textContent = ""
        errors.forEach { error ->
            val item = document.createElement("li") as HTMLElement
            item.setAttribute("data-summon-error-entry", error.category.name)
            item.appendChild(document.createTextNode(error.text))
            if (error.frames.isEmpty()) {
                item.appendChild(createLocation(null))
            } else {
                error.frames.forEach { frame -> item.appendChild(createLocation(frame)) }
            }
            list.appendChild(item)
        }
    }

    private fun createLocation(frame: DevelopmentErrorFrame?): HTMLElement {
        val location = document.createElement("span") as HTMLElement
        location.setAttribute("data-summon-error-location", "")
        val mapped = frame?.mapped
        if (mapped == null) {
            location.setAttribute("data-mapped", "false")
            location.textContent = if (frame == null) {
                "Source unavailable"
            } else {
                "Generated location ${frame.generated.line}:${frame.generated.column} (source unavailable)"
            }
        } else {
            location.setAttribute("data-mapped", "true")
            location.textContent = "${mapped.source}:${mapped.line}:${mapped.column}"
            appendLink(location, "Editor", config.links.editorLink(mapped))
            appendLink(location, "Verified source", config.links.viewerLink(mapped))
        }
        return location
    }

    private fun appendLink(parent: HTMLElement, label: String, href: String?) {
        if (href == null) return
        parent.appendChild(document.createTextNode(" "))
        val link = document.createElement("a") as HTMLAnchorElement
        link.textContent = label
        link.href = href
        link.rel = "noopener noreferrer"
        parent.appendChild(link)
    }
}
