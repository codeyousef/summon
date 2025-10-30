package code.yousef.summon.diagnostics

import code.yousef.summon.components.display.Text
import code.yousef.summon.runtime.CallbackRegistry
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.clearPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class HydrationConcurrencyTest {

    @Test
    fun `hydration renders do not bleed callbacks across threads`() {
        val threads = 4
        val iterationsPerThread = 25
        val executor = Executors.newFixedThreadPool(threads)
        val latch = CountDownLatch(threads)

        repeat(threads) { threadIndex ->
            executor.submit {
                try {
                    repeat(iterationsPerThread) { iteration ->
                        val renderer = PlatformRenderer()
                        setPlatformRenderer(renderer)
                        try {
                            val html = renderer.renderComposableRootWithHydration {
                                Text("thread-$threadIndex-$iteration")
                            }
                            assertTrue(html.contains("summon-hydration"), "hydration script should be present")
                            check(CallbackRegistry.size() == 0) { "CallbackRegistry should be empty after render" }
                        } finally {
                            CallbackRegistry.clear()
                            clearPlatformRenderer()
                        }
                    }
                } finally {
                    latch.countDown()
                }
            }
        }

        require(latch.await(5, TimeUnit.SECONDS)) { "Hydration stress test did not complete in time" }
        executor.shutdownNow()
    }
}
