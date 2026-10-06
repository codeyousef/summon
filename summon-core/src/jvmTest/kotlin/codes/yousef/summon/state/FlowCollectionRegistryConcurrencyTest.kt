package codes.yousef.summon.state

import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class FlowCollectionRegistryConcurrencyTest {
    @Test
    fun concurrentGetSharesOneActiveScope() {
        val executor = Executors.newFixedThreadPool(8)
        val ready = CountDownLatch(8)
        val start = CountDownLatch(1)
        try {
            val futures = (0 until 8).map {
                executor.submit(Callable {
                    ready.countDown()
                    check(start.await(10, TimeUnit.SECONDS))
                    List(1000) { FlowCollectionRegistry.getScope("concurrent-get") }
                })
            }
            assertTrue(ready.await(10, TimeUnit.SECONDS))
            start.countDown()
            val scopes = futures.flatMap { it.get(30, TimeUnit.SECONDS) }
            scopes.forEach { assertSame(scopes.first(), it) }
            FlowCollectionRegistry.cancelScope("concurrent-get")
            scopes.forEach { assertFalse(it.isActive) }
        } finally {
            start.countDown()
            executor.shutdownNow()
            FlowCollectionRegistry.cancelAll()
        }
    }

    @Test
    fun concurrentCancelAndGetLeaveNoUnownedScopes() {
        val executor = Executors.newFixedThreadPool(8)
        try {
            val futures = (0 until 8).map { worker ->
                executor.submit(Callable {
                    List(1000) { iteration ->
                        val key = "registry-race-${iteration % 4}"
                        val scope = FlowCollectionRegistry.getScope(key)
                        if ((worker + iteration) % 3 == 0) FlowCollectionRegistry.cancelAll()
                        else FlowCollectionRegistry.cancelScope(key)
                        scope
                    }
                })
            }
            val scopes = futures.flatMap { it.get(30, TimeUnit.SECONDS) }
            FlowCollectionRegistry.cancelAll()
            scopes.forEach { assertFalse(it.isActive) }
            val fresh = FlowCollectionRegistry.getScope("registry-race-0")
            assertTrue(fresh.isActive)
        } finally {
            executor.shutdownNow()
            FlowCollectionRegistry.cancelAll()
        }
    }

    @Test
    fun externallyCanceledScopeIsReplaced() {
        try {
            val old = FlowCollectionRegistry.getScope("external-cancel")
            old.cancel()
            val replacement = FlowCollectionRegistry.getScope("external-cancel")
            assertNotSame(old, replacement)
            assertTrue(replacement.isActive)
            FlowCollectionRegistry.cancelScope("external-cancel")
            assertFalse(replacement.isActive)
        } finally {
            FlowCollectionRegistry.cancelAll()
        }
    }
}
