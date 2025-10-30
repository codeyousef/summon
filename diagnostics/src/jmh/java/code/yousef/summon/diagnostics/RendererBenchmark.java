package code.yousef.summon.diagnostics;

import code.yousef.summon.runtime.CallbackRegistry;
import code.yousef.summon.runtime.PlatformRenderer;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.util.concurrent.TimeUnit;

import static code.yousef.summon.runtime.PlatformRendererAccessorKt.clearPlatformRenderer;
import static code.yousef.summon.runtime.PlatformRendererAccessorKt.setPlatformRenderer;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class RendererBenchmark {

    private PlatformRenderer renderer;
    private final Function0<Unit> emptyComposable = () -> Unit.INSTANCE;

    @Setup
    public void setup() {
        renderer = new PlatformRenderer();
        setPlatformRenderer(renderer);
    }

    @TearDown
    public void tearDown() {
        CallbackRegistry.INSTANCE.clear();
        clearPlatformRenderer();
    }

    @Benchmark
    public String renderHtml() {
        return renderer.renderComposableRoot(emptyComposable);
    }

    @Benchmark
    public String renderHydratedHtml() {
        return renderer.renderComposableRootWithHydration(emptyComposable);
    }
}
