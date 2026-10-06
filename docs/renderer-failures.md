# Browser renderer failures (0.8.0)

A browser mount owns its renderer, effects, collectors and DOM callbacks. A failure escaping its
root body releases that ownership and then propagates the original failure to the caller. This
also applies when the failure occurs inside WASM Row, Column, Box or Div content. Box now invokes
its content in DOM mode. These containers restore container and identity cursors in `finally`.
DOM modifier/text/input/style/append operations used by these paths check failure results rather
than returning a successful mount after a failed write.

`CancellationException` propagates unchanged after teardown. Applications must catch mount
failures at their own boundary and show their own unavailable/retry UI; Summon does not infer an
authentication or encryption response. This pattern avoids reporting the exception payload:

```kotlin
try {
    val owner = mountComposableRoot("root") { PrivateView() }
    // Keep owner in the host and call owner.dispose() when removing the view.
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (_: Throwable) {
    showUnavailableView() // Application-owned, with a generic user-facing message.
}
```

Renderer diagnostics emit fixed messages only. A renderer reports at most one generic operation
failure and one unsupported-component notice during its lifetime. WASM DOM interop and the shared
one-shot callback registry each have their own lifetime budget for generic failure reporting.
Routine rendering no longer logs controlled values, text, element IDs/keys, handlers, hydration
JSON or exception messages. Legacy string-render error HTML uses generic text rather than the
exception payload. This diagnostic change does not qualify legacy hydration or unsupported
components as complete; their broader work packages remain required.

The caller receives the original exception, which may contain private data. Application loggers,
explicit debug/metrics APIs and uncaught browser errors require their own privacy policy. This
change guarantees framework-emitted diagnostics for the qualified rendering paths; it does not
make arbitrary application exception reporting safe. No exception objects are passed to the
framework diagnostic reporter.

The runnable fixture is `e2e-tests/fixtures/private-suite`: open `/?failures=true` in its JS or WASM
production build, then use Fail Column/Row/Box/Div, Cancel Column and Mount healthy layout.
Synthetic markers exercise private text/value/key/error redaction. Playwright verifies resource
teardown, neighboring root identity, healthy remount/controlled input focus, cancellation, a native
input-setter fault and console/pageerror output across Chromium, Firefox and WebKit on both targets.
Actual Safari and JS/WASM binary API validation remain separate qualification gates.
