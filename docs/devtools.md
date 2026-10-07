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

## Bounded state timeline

Each inspector session can create isolated timelines. Recording includes only explicitly registered
PUBLIC fields and their codec values; REDACTED fields have no getter path. Authentication or
credential-like field names, credential-bearing URLs, nonfinite numbers, and strings larger than
4,096 UTF-8 bytes are rejected.

```kotlin
val timeline = session.createTimeline() // 200 entries; configurable up to 10,000
timeline.start()

// BrowserInspectorOverlay calls this once per animation frame while recording.
// Non-browser hosts call it after application-driven state changes.
timeline.sample()
timeline.pause()

val firstChange = timeline.entries.first()
val restore = timeline.restoreTo(firstChange.sequence)
check(restore.complete)

val json = timeline.exportSession()
val plan = timeline.importSession(json) // validation only; invokes no setter or action
timeline.apply(plan)                    // explicit local mutation

timeline.clear()
timeline.stop()
timeline.dispose()
```

Entries contain a monotonic sequence, structural node ID plus registered field name and codec,
typed before/after values, and an optional registered action ID. Equal writes are omitted. Capacity
evicts the oldest entries. Restoring never records its own writes; the next real mutation after a
restore truncates the future branch. Read-only or removed fields are reported as unrestorable.

Replay accepts only actions explicitly registered with `DebugActionEffect.PURE_UI`. Network, file,
storage, clipboard, account, cryptographic, send, purchase, and other external-effect actions are
rejected during registration. Replay stops at the first missing, destroyed, invalid, or failing
field/action. It cannot recreate destroyed components or unregistered external state.

Session JSON uses `formatVersion=1` and is bounded to 1 MiB before parsing. Import rejects unknown
versions or properties, duplicate/out-of-order sequences, missing or unknown fields/actions,
oversized strings, invalid typed values, and nonfinite numbers before any live mutation. Import
returns an immutable validated plan; only `apply` changes state. Closing the browser overlay
disposes its timeline and clears imported text, entries, and live timeline references. Disposal
does not claim perfect erasure from managed-runtime memory.

The browser panel exposes Record, Pause, Stop, Clear, restore points, Export, Validate import, and
Apply import. Its animation-frame sampler captures application-driven updates; direct inspector
edits are recorded synchronously.


## Development error overlay

Install the browser error overlay only from a debug source set or debug dependency configuration.
Source maps are caller-supplied data: the overlay performs no fetches and accepts generated assets
only from the current origin with the configured build identity.

```kotlin
val overlay = installBrowserErrorOverlay(
    BrowserErrorOverlayConfig(
        buildId = buildId,
        sourceMaps = listOf(
            VerifiedSourceMapAsset(
                generatedAssetUrl = "${window.location.origin}/app.js",
                buildId = buildId,
                json = preloadedSourceMap
            )
        ),
        links = DevelopmentSourceLinkPolicy(
            editorProtocol = "vscode",
            workspaceRoot = "/workspace/project",
            viewerOrigin = "https://source.example",
            sourceRevision = buildId
        )
    )
)

SummonErrorBoundary.withErrorBoundary("profile") {
    renderProfile()
}.onError { error, _ ->
    // Reporting does not alter the boundary's recovery decision.
    overlay.reportErrorBoundary(error)
}

overlay.dispose()
```

Ordinary `error` and `unhandledrejection` events show generic categories. In particular, rejection
reasons are never read or stringified. `SYNTHETIC_PUBLIC` text is an explicit debug policy for
caller-declared text; it remains bounded to 16 KiB. Stacks are parsed structurally and capped at 100
frames. Rendering uses text nodes, so error text cannot inject markup.

Source-map paths are normalized before use. Unmapped or invalid locations stay labeled as generated
locations. Editor links require a configured non-web protocol, traversal-free absolute workspace
root, exact build identity, and valid line/column. Verified-source links require a configured HTTPS
origin and exact build/source identity. Missing validation produces no link. `dispose()` removes both
global listeners, the panel, its stylesheet, retained errors, and source-map references.

Webpack development builds must emit an external map, for example:

```kotlin
browser {
    commonWebpackConfig {
        devtool = "source-map"
    }
}
```

Do not add `summon-devtools`, development source maps, workspace paths, viewer credentials, or upload
tokens to production runtime configurations or bundles.

The source fixture under `e2e-tests/fixtures/devtools` demonstrates only synthetic PUBLIC fields. Run its pinned, no-network JS/WASM browser matrix with:

```bash
./e2e-tests/run-devtools-container.sh
```
