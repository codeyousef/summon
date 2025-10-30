package code.yousef.summon.diagnostics

import code.yousef.summon.components.display.Text
import code.yousef.summon.integration.springboot.WebFluxSupport
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import reactor.blockhound.BlockHound
import reactor.blockhound.BlockingOperationError
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier
import java.util.concurrent.atomic.AtomicBoolean

class ReactiveBlockingDetectionTest {

    companion object {
        private val blockHoundInstalled = AtomicBoolean(false)
        private val blockHoundReady: Boolean = try {
            if (blockHoundInstalled.compareAndSet(false, true)) {
                BlockHound.builder()
                    .allowBlockingCallsInside("kotlinx.coroutines", "runBlocking")
                    .install()
            }
            true
        } catch (_: Throwable) {
            false
        }

        fun ensureBlockHound() {
            assumeTrue(blockHoundReady, "BlockHound instrumentation not available on this JVM")
        }
    }

    @AfterEach
    fun tearDown() {
        code.yousef.summon.runtime.CallbackRegistry.clear()
    }

    @Test
    fun `renderToFlux completes without blocking`() {
        ensureBlockHound()
        val flux = WebFluxSupport.renderToFlux(title = "diagnostics", chunkSize = 256) {
            Text("hydratable")
        }.subscribeOn(Schedulers.parallel())

        StepVerifier.create(flux)
            .thenConsumeWhile { it.isNotEmpty() }
            .verifyComplete()
    }

    @Test
    fun `blockhound detects blocking call for control`() {
        ensureBlockHound()
        val error = assertThrows(RuntimeException::class.java) {
            StepVerifier.create(
                Mono.fromCallable {
                    Thread.sleep(5L)
                    "oops"
                }.subscribeOn(Schedulers.parallel())
            ).expectNext("oops").verifyComplete()
        }
        // BlockHound wraps the blocking call inside a BlockingOperationError
        val cause = requireNotNull(error.cause) { "Expected BlockHound to flag a blocking call" }
        assertTrue(cause is BlockingOperationError)
    }
}
