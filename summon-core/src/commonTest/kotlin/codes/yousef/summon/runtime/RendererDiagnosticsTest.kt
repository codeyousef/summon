package codes.yousef.summon.runtime

import kotlin.test.Test
import kotlin.test.assertEquals

class RendererDiagnosticsTest {
    @Test fun repeatedFailuresAndUnsupportedCallsHaveAFixedDiagnosticBudget() {
        val messages = mutableListOf<String>()
        val diagnostics = RendererDiagnostics(messages::add)
        repeat(1000) { diagnostics.failure(); diagnostics.unsupported() }
        assertEquals(listOf("Summon renderer operation failed", "Summon renderer component is unsupported on this platform"), messages)
    }

    @Test fun unavailableConsoleDoesNotReplaceTheOperationFailure() {
        var attempts = 0
        val diagnostics = RendererDiagnostics { attempts++; error("Synthetic console unavailable") }
        repeat(1000) { diagnostics.failure() }
        assertEquals(1, attempts)
    }
}
