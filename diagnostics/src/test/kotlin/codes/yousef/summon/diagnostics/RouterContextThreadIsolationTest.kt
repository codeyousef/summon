package codes.yousef.summon.diagnostics

import codes.yousef.summon.routing.RouterContext
import codes.yousef.summon.routing.createFileBasedServerRouter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class RouterContextThreadIsolationTest {

    @AfterEach
    fun cleanUp() {
        RouterContext.clear()
    }

    @Test
    fun `router context is isolated per thread`() {
        val routerA = createFileBasedServerRouter("/")
        val routerB = createFileBasedServerRouter("/about")

        val executor = Executors.newFixedThreadPool(2)
        val entered = CountDownLatch(2)
        val release = CountDownLatch(1)
        val seen = ConcurrentLinkedQueue<String>()

        try {
            executor.submit {
                RouterContext.withRouter(routerA) {
                    seen += "A:${RouterContext.current?.currentPath}"
                    entered.countDown()
                    release.await(2, TimeUnit.SECONDS)
                }
            }
            executor.submit {
                RouterContext.withRouter(routerB) {
                    seen += "B:${RouterContext.current?.currentPath}"
                    entered.countDown()
                    release.await(2, TimeUnit.SECONDS)
                }
            }

            assertTrue(entered.await(2, TimeUnit.SECONDS), "Both router scopes must overlap")
            release.countDown()
            executor.shutdown()
            assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS), "Router workers must finish")
        } finally {
            release.countDown()
            executor.shutdownNow()
        }

        // Each thread should observe only its own router.
        assertEquals(listOf("A:/", "B:/about"), seen.sorted())

        // RouterContext should be cleared outside the scoped blocks
        assertNull(RouterContext.current)
    }
}
