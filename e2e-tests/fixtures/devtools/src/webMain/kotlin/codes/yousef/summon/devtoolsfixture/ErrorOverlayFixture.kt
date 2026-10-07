package codes.yousef.summon.devtoolsfixture

import codes.yousef.summon.devtools.BrowserErrorOverlay
import codes.yousef.summon.devtools.BrowserErrorOverlayConfig
import codes.yousef.summon.devtools.DevelopmentErrorCategory
import codes.yousef.summon.devtools.DevelopmentErrorTextPolicy
import codes.yousef.summon.devtools.DevelopmentSourceLinkPolicy
import codes.yousef.summon.devtools.VerifiedSourceMapAsset
import codes.yousef.summon.devtools.installBrowserErrorOverlay
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

private const val BUILD_ID = "devtools-fixture"

@Suppress("REDUNDANT_CALL_OF_CONVERSION_METHOD")
@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
fun installErrorOverlayFixture() {
    val generatedAsset = "${window.location.origin}/fixture.js"
    window.fetch("$generatedAsset.map").then { response ->
        check(response.ok) { "Fixture source map was not served" }
        response.text().then { sourceMap ->
            try {
            var overlay: BrowserErrorOverlay? = null

            fun openOverlay() {
                overlay?.dispose()
                overlay = installBrowserErrorOverlay(
                    BrowserErrorOverlayConfig(
                        buildId = BUILD_ID,
                        sourceMaps = listOf(VerifiedSourceMapAsset(generatedAsset, BUILD_ID, sourceMap.toString())),
                        textPolicy = DevelopmentErrorTextPolicy.SYNTHETIC_PUBLIC,
                        links = DevelopmentSourceLinkPolicy(
                            editorProtocol = "vscode",
                            workspaceRoot = "/workspace/summon",
                            viewerOrigin = "https://source.example",
                            sourceRevision = BUILD_ID
                        )
                    )
                )
            }

            appendFixtureButton("open-error-overlay", "Open development error overlay") { openOverlay() }
            appendFixtureButton("report-mapped-error", "Report mapped boundary error") {
                try {
                    mappedFixtureFailure()
                } catch (error: Throwable) {
                    val current = overlay ?: return@appendFixtureButton
                    repeat(2) {
                        current.reportErrorBoundary(
                            error,
                            "Public failure <img src=x onerror=alert(1)>"
                        )
                    }
                }
            }
            appendFixtureButton("report-hostile-error", "Report hostile generated error") {
                overlay?.reportSynthetic(
                    DevelopmentErrorCategory.SCRIPT_ERROR,
                    "Hostile frame",
                    "at hostile (https://attacker.invalid/payload.js:7:9)"
                )
            }
            openOverlay()
            } catch (error: Throwable) {
                val output = document.createElement("output") as HTMLElement
                output.setAttribute("data-testid", "error-overlay-install-failure")
                output.textContent = error.message ?: error::class.simpleName ?: "Unknown source-map failure"
                document.body?.appendChild(output)
            }
            null
        }
        null
    }
}

private fun mappedFixtureFailure(): Nothing = error("private fixture exception body")

private fun appendFixtureButton(testId: String, label: String, action: () -> Unit) {
    val button = document.createElement("button") as HTMLElement
    button.setAttribute("type", "button")
    button.setAttribute("data-testid", testId)
    button.textContent = label
    button.addEventListener("click", { action() })
    document.body?.appendChild(button)
}
