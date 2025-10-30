package code.yousef.summon.diagnostics

import code.yousef.summon.runtime.CallbackRegistry
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.clearPlatformRenderer
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PlatformRendererThreadIsolationTest {

    @Test
    fun `setPlatformRenderer is thread-local`() {
        val rendererA = PlatformRenderer()
        val rendererB = PlatformRenderer()

        val executor = Executors.newFixedThreadPool(2)
        val ready = CountDownLatch(2)
        val done = CountDownLatch(2)

        executor.submit {
            try {
                setPlatformRenderer(rendererA)
                ready.countDown()
                assertSame(rendererA, getPlatformRenderer())
            } finally {
                CallbackRegistry.clear()
                clearPlatformRenderer()
                done.countDown()
            }
        }

        executor.submit {
            try {
                setPlatformRenderer(rendererB)
                ready.countDown()
                assertSame(rendererB, getPlatformRenderer())
            } finally {
                CallbackRegistry.clear()
                clearPlatformRenderer()
                done.countDown()
            }
        }

        require(ready.await(2, TimeUnit.SECONDS)) { "Threads failed to initialize renderers" }
        require(done.await(2, TimeUnit.SECONDS)) { "Threads failed to tear down renderers" }
        executor.shutdownNow()
    }
}
