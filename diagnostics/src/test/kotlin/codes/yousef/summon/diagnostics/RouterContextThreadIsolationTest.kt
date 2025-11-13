package codes.yousef.summon.diagnostics

import codes.yousef.summon.routing.RouterContext
import codes.yousef.summon.routing.createFileBasedServerRouter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
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
        val latch = CountDownLatch(2)
        val seen = mutableListOf<String>()

        executor.submit {
            RouterContext.withRouter(routerA) {
                seen += "A:${RouterContext.current?.currentPath}"
                latch.countDown()
                Thread.sleep(25)
            }
        }
        executor.submit {
            RouterContext.withRouter(routerB) {
                seen += "B:${RouterContext.current?.currentPath}"
                latch.countDown()
                Thread.sleep(25)
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        executor.shutdownNow()

        // Each thread should observe only its own router
        seen.sort()
        assertEquals(listOf("A:/", "B:/about"), seen)

        // RouterContext should be cleared outside the scoped blocks
        assertNull(RouterContext.current)
    }
}
