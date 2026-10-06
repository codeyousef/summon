package codes.yousef.summon.i18n

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class JsI18nOwnershipTest {
    @Test
    fun canceledOwnerCannotStartOrCompleteResourceLoading() = runTest {
        val parent = Job(backgroundScope.coroutineContext[Job])
        parent.cancel()
        var completed = false

        val loading = JsI18nImplementation.loadLanguageResources(
            scope = CoroutineScope(backgroundScope.coroutineContext + parent),
            basePath = "/synthetic-i18n/",
            onComplete = { completed = true }
        )
        runCurrent()

        assertTrue(loading.isCancelled)
        assertFalse(completed)
    }
}
