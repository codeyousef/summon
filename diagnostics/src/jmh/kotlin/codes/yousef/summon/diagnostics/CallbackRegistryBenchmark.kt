package codes.yousef.summon.diagnostics

import codes.yousef.summon.runtime.CallbackRegistry
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

/**
 * JMH benchmarks for CallbackRegistry performance.
 *
 * Run with: ./gradlew :diagnostics:jmh
 *
 * These benchmarks measure:
 * - Callback registration overhead
 * - Callback invocation performance
 * - Registry lookup performance
 * - Cleanup overhead
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
open class CallbackRegistryBenchmark {

    private var registeredIds: MutableList<String> = mutableListOf()
    private var counter = 0

    @Setup(Level.Iteration)
    fun setup() {
        CallbackRegistry.clear()
        registeredIds.clear()
        counter = 0

        // Pre-register some callbacks for lookup benchmarks
        repeat(100) { _ ->
            val id = CallbackRegistry.registerCallback { counter++ }
            registeredIds.add(id)
        }
    }

    @TearDown(Level.Iteration)
    fun tearDown() {
        CallbackRegistry.clear()
    }

    /**
     * Measure callback registration overhead.
     */
    @Benchmark
    fun registerCallback(): String {
        return CallbackRegistry.registerCallback { counter++ }
    }
    /**
     * Measure secure one-shot callback invocation by ID and render capability.
     */
    @Benchmark
    fun executeRemoteCallback() {
        CallbackRegistry.beginRender()
        val id = CallbackRegistry.registerCallback { counter++ }
        val context = CallbackRegistry.finishRenderAndCollectCallbacks()
        CallbackRegistry.executeRemoteCallback(id, context.capability)
    }

    /**
     * Measure callback existence check.
     */
    @Benchmark
    fun checkCallbackExists(): Boolean {
        val id = registeredIds[counter % registeredIds.size]
        return CallbackRegistry.hasCallback(id)
    }

    /**
     * Measure the public secure cycle: render registration, collection, and execution.
     */
    @Benchmark
    fun fullRemoteCallbackCycle() {
        CallbackRegistry.beginRender()
        val id = CallbackRegistry.registerCallback { counter++ }
        val context = CallbackRegistry.finishRenderAndCollectCallbacks()
        CallbackRegistry.executeRemoteCallback(id, context.capability)
    }

    /**
     * Measure batch registration.
     */
    @Benchmark
    fun registerBatch10Callbacks(): List<String> {
        return (0 until 10).map {
            CallbackRegistry.registerCallback { counter++ }
        }
    }

    /**
     * Measure batch execution through one render capability.
     */
    @Benchmark
    fun executeBatch10RemoteCallbacks() {
        CallbackRegistry.beginRender()
        val ids = (0 until 10).map {
            CallbackRegistry.registerCallback { counter++ }
        }
        val context = CallbackRegistry.finishRenderAndCollectCallbacks()
        ids.forEach { id ->
            CallbackRegistry.executeRemoteCallback(id, context.capability)
        }
    }
}
