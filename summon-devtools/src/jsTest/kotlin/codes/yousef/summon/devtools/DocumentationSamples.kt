package codes.yousef.summon.devtools

import kotlin.test.Test

fun browserErrorOverlayTeardownSample() {
    val overlay = installBrowserErrorOverlay(
        BrowserErrorOverlayConfig(
            buildId = "development-build",
            textPolicy = DevelopmentErrorTextPolicy.GENERIC_ONLY
        )
    )

    try {
        check(overlay.entries.isEmpty())
    } finally {
        overlay.dispose()
    }
}

class BrowserDocumentationSamplesTest {
    @Test
    fun browserErrorOverlayTeardown() {
        browserErrorOverlayTeardownSample()
    }
}
