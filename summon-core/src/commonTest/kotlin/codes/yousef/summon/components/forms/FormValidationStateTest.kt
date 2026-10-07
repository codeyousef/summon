package codes.yousef.summon.components.forms

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class FormValidationStateTest {
    @Test
    fun staleAsyncResultCannotReplaceLatestFieldResult() = runTest {
        val firstStarted = CompletableDeferred<Unit>()
        val releaseFirst = CompletableDeferred<Unit>()
        val state = FormValidationState().apply {
            registerAsyncValidator("alias", object : AsyncValidator {
                override suspend fun validate(value: String): String? {
                    if (value == "old") {
                        firstStarted.complete(Unit)
                        releaseFirst.await()
                        return "stale error"
                    }
                    return null
                }
            })
        }

        val oldResult = async { state.validateFieldAsync("alias", "old") }
        firstStarted.await()
        val latestResult = async { state.validateFieldAsync("alias", "latest") }
        runCurrent()
        assertNull(latestResult.await())
        assertNull(state.getError("alias"))

        releaseFirst.complete(Unit)
        assertNull(oldResult.await())
        assertNull(state.getError("alias"))
    }

    @Test
    fun clearingFieldInvalidatesPendingValidation() = runTest {
        val started = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val state = FormValidationState().apply {
            registerAsyncValidator("address", object : AsyncValidator {
                override suspend fun validate(value: String): String? {
                    started.complete(Unit)
                    release.await()
                    return "late error"
                }
            })
        }

        val result = async { state.validateFieldAsync("address", "value") }
        started.await()
        state.clearFieldError("address")
        release.complete(Unit)

        assertNull(result.await())
        assertNull(state.getError("address"))
        assertEquals(false, state.hasError("address"))
    }
}
