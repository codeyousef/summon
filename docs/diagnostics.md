# Diagnostics Suite

The `:diagnostics` module contains long-running safety and performance checks for the Summon runtime and backend
integrations.

## What is covered?

- **Callback leak detection** – verifies that hydration callbacks are cleared after every render.
- **Router isolation** – ensures router instances and renderers remain thread-local.
- **Reactive blocking guard** – installs BlockHound to fail tests when blocking APIs are invoked on Reactor threads.
- **Hydration stress tests** – runs repeated concurrent hydrated renders to surface race conditions.
- **Renderer benchmarks** – optional JMH benchmarks that capture the steady-state cost of server rendering.

## Running the suite

```bash
# Run the diagnostic tests (JUnit 5 + BlockHound)
./gradlew :diagnostics:test

# Run the JMH micro-benchmarks
./gradlew :diagnostics:jmh
```

> The benchmarking step is disabled by default; invoke the `jmh` task explicitly when you need throughput metrics.

The diagnostics module depends on `:summon-core`, so any runtime change will automatically flow into the safety and
performance checks above.
