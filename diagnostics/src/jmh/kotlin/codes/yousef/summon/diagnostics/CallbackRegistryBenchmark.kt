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
     * Measure callback invocation by ID.
     */
    @Benchmark
    fun executeCallback() {
        // Re-register since executeCallback removes the callback
        val id = CallbackRegistry.registerCallback { counter++ }
        CallbackRegistry.executeCallback(id)
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
     * Measure full cycle: register, execute (which removes).
     */
    @Benchmark
    fun fullCallbackCycle() {
        val id = CallbackRegistry.registerCallback { counter++ }
        CallbackRegistry.executeCallback(id)
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
     * Measure batch execution.
     */
    @Benchmark
    fun executeBatch10Callbacks() {
        // Register and execute 10 callbacks
        val ids = (0 until 10).map {
            CallbackRegistry.registerCallback { counter++ }
        }
        ids.forEach { id ->
            CallbackRegistry.executeCallback(id)
        }
    }
}
