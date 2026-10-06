package codes.yousef.summon.runtime

import codes.yousef.summon.mountComposableRoot
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.*

class RuntimeContextIsolationTest {
    @Test fun concurrentSynchronousRootsDoNotShareComposerRendererOrRecomposer() {
        val executor = Executors.newFixedThreadPool(2)
        val barrier = CyclicBarrier(2)
        val local = CompositionLocal.compositionLocalOf("default")
        try {
            val tasks = (1..2).map { index -> executor.submit {
                val renderer = PlatformRenderer()
                // The legacy JVM constructor registers itself; establish the caller context explicitly.
                PlatformRendererStore.clear()
                val recomposer = Recomposer()
                withPlatformRenderer(renderer) {
                    withCompositionLocal(local, "request-$index") {
                        recomposer.composeInitial {
                            val composer = CompositionLocal.currentComposer
                            barrier.await(5, TimeUnit.SECONDS)
                            repeat(100) {
                                assertSame(renderer, PlatformRendererStore.get())
                                assertSame(renderer, LocalPlatformRenderer.current)
                                assertSame(recomposer, RecomposerHolder.current())
                                assertSame(composer, CompositionLocal.currentComposer)
                                assertEquals("request-$index", local.current)
                            }
                        }
                    }
                }
                assertNull(PlatformRendererStore.get())
                assertNull(CompositionLocal.currentComposer)
                assertEquals("default", local.current)
                recomposer.dispose()
            } }
            tasks.forEach { it.get(10, TimeUnit.SECONDS) }
        } finally { executor.shutdownNow() }
    }

    @Test fun jvmDomMountFailsBeforeRunningApplication() {
        var called = false
        assertFailsWith<UnsupportedOperationException> {
            mountComposableRoot("root") { called = true }
        }
        assertFalse(called)
    }
}
