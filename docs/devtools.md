# Summon developer tools

`codes.yousef:summon-devtools` is a separate, opt-in Kotlin Multiplatform artifact. Add it only to debug and test configurations; production runtime configurations should depend on `summon-core` without `summon-devtools`.

## Browser component inspector

The inspector observes the actual connected DOM below one mounted Summon root. Node identity combines an isolated renderer identity with a root-local monotonic node ID. Text and sibling positions are never identities, so keyed moves retain identity while current parent and order remain live.

```kotlin
val session = createBrowserInspectorSession("root") { action ->
    // Dispatch through the application's render/UI dispatcher.
    action()
}
val root = session.tree().first()

// Registration defaults to REDACTED and exposes no getter.
val credentialRegistration = session.registerField(root.id, "credential")

var count = 0L
val countRegistration = session.registerPublicField(
    nodeId = root.id,
    name = "count",
    codec = DebugFieldCodecs.nullableLong,
    getter = { count },
    setter = { value -> count = requireNotNull(value) }
)

val overlay = BrowserInspectorOverlay(session)
// Closing the panel keeps the session available for reopening.
overlay.dispose()

// Root teardown must release registrations and the session.
countRegistration.dispose()
credentialRegistration.dispose()
session.dispose()
```

PUBLIC fields require an explicit typed codec. Supported values are null, Boolean, bounded String, integral numbers, and finite floating-point numbers. Decode and validation finish before the application setter is dispatched. Read-only PUBLIC fields omit the setter. REDACTED fields expose only their label and `<redacted>`; inspector, search, export, and display code have no getter to invoke.

The panel uses text nodes for labels and values, keyboard tree navigation, a noninteractive DOM highlight, and the page's `summon-style-nonce` when strict CSP is active. `BrowserInspectorOverlay.dispose()` removes its key listener, tree subscription, stylesheet, panel, and highlight. `InspectorSession.dispose()` additionally clears fields, setters, listeners, node snapshots, animation frames, and DOM references. Both operations are idempotent.

The source fixture under `e2e-tests/fixtures/devtools` demonstrates only synthetic PUBLIC fields. Run its pinned, no-network JS/WASM browser matrix with:

```bash
./e2e-tests/run-devtools-container.sh
```
