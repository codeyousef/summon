package codes.yousef.summon.runtime

/** Fixed messages only, with at most one message of each kind per reporter lifetime. */
internal class RendererDiagnostics(private val report: (String) -> Unit) {
    private var failed = false
    private var unsupported = false

    fun failure() {
        if (failed) return
        failed = true
        emit("Summon renderer operation failed")
    }

    fun unsupported() {
        if (unsupported) return
        unsupported = true
        emit("Summon renderer component is unsupported on this platform")
    }

    private fun emit(message: String) {
        // A missing/broken console must not replace the original operation failure.
        try { report(message) } catch (_: Throwable) { }
    }
}
