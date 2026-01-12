package codes.yousef.summon.test

import org.junit.jupiter.api.Tag

/**
 * Marker annotation for slow tests (stress tests, performance tests).
 * These are excluded from default test runs and run separately in CI.
 *
 * Tests annotated with @SlowTest will be:
 * - Excluded from the default `jvmTest` task
 * - Run only when explicitly invoking `slowTests` task
 * - Run in CI on a schedule (nightly builds) or manually triggered
 *
 * Use this annotation for:
 * - Stress tests with many iterations (100+ repeats)
 * - Performance benchmarks
 * - Tests with Thread.sleep or long delays
 * - Tests that require significant memory or CPU
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Tag("slow")
annotation class SlowTest

