package codes.yousef.summon.diagnostics

import codes.yousef.summon.state.SummonMutableState
import codes.yousef.summon.state.mutableStateOf
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

/**
 * JMH benchmarks for reactive state performance.
 *
 * Run with: ./gradlew :diagnostics:jmh
 *
 * These benchmarks measure:
 * - State creation overhead
 * - State read performance
 * - State update performance
 * - Multiple state coordination
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
open class StateBenchmark {

    private lateinit var singleState: SummonMutableState<Int>
    private lateinit var stringState: SummonMutableState<String>
    private lateinit var listState: SummonMutableState<List<Int>>
    private lateinit var states: List<SummonMutableState<Int>>

    @Setup(Level.Iteration)
    fun setup() {
        singleState = mutableStateOf(0)
        stringState = mutableStateOf("initial")
        listState = mutableStateOf(listOf(1, 2, 3, 4, 5))
        states = (0 until 100).map { mutableStateOf(it) }
    }

    /**
     * Measure state creation overhead.
     */
    @Benchmark
    fun createState(): SummonMutableState<Int> {
        return mutableStateOf(42)
    }

    /**
     * Measure state creation with complex type.
     */
    @Benchmark
    fun createStateWithList(): SummonMutableState<List<String>> {
        return mutableStateOf(listOf("a", "b", "c"))
    }

    /**
     * Measure state read performance.
     */
    @Benchmark
    fun readState(): Int {
        return singleState.value
    }

    /**
     * Measure state read with string type.
     */
    @Benchmark
    fun readStringState(): String {
        return stringState.value
    }

    /**
     * Measure state read with list type.
     */
    @Benchmark
    fun readListState(): List<Int> {
        return listState.value
    }

    /**
     * Measure state update performance.
     */
    @Benchmark
    fun updateState() {
        singleState.value = singleState.value + 1
    }

    /**
     * Measure state update with string.
     */
    @Benchmark
    fun updateStringState() {
        stringState.value = "updated-${System.nanoTime()}"
    }

    /**
     * Measure state update with list (immutable update).
     */
    @Benchmark
    fun updateListState() {
        listState.value = listState.value + listState.value.size
    }

    /**
     * Measure sequential reads from multiple states.
     */
    @Benchmark
    fun readMultipleStates(): Int {
        var sum = 0
        for (state in states) {
            sum += state.value
        }
        return sum
    }

    /**
     * Measure sequential updates to multiple states.
     */
    @Benchmark
    fun updateMultipleStates() {
        for (state in states) {
            state.value = state.value + 1
        }
    }

    /**
     * Measure creating and immediately reading state.
     */
    @Benchmark
    fun createAndReadState(): Int {
        val state = mutableStateOf(100)
        return state.value
    }

    /**
     * Measure creating, updating, and reading state.
     */
    @Benchmark
    fun createUpdateReadState(): Int {
        val state = mutableStateOf(0)
        state.value = 42
        return state.value
    }
}
