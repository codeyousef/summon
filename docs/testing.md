# Component testing

The `summon-test` Kotlin Multiplatform artifact mounts one owned component root and queries the rendered semantics rather than an implementation tree.

```kotlin
// build.gradle.kts
val jvmTest by getting {
    dependencies { implementation("codes.yousef:summon-test:<summon-version>") }
}
val jsTest by getting {
    dependencies { implementation("codes.yousef:summon-test:<summon-version>") }
}
```

## JVM SSR semantics

The JVM adapter renders through the real SSR renderer and callback registry. It qualifies HTML semantics and click callbacks; it deliberately rejects text input and scrolling because those require a browser.

```kotlin
@Test
fun greetingIsRendered() {
    withComponentHarness(
        mountJvmComponentHarness {
            Greeting(Modifier().testTag("greeting"))
        }
    ) { harness ->
        harness.onNodeWithTag("greeting")
            .assertExists()
            .assertIsDisplayed()
            .assertTextEquals("مرحبا")
    }
}
```

## Browser DOM semantics

Run browser examples with the generated project's `jsBrowserTest` task. The browser adapter mounts through `mountComposableRoot`, dispatches the same bubbling click, input, change, and scroll events used by the renderer, and drains its owner-scoped scheduler after each action.

```kotlin
@Test
fun controlledInputUpdates() {
    val body = document.body ?: error("Browser test requires a document body")
    val root = document.createElement("main") as HTMLElement
    root.id = "component-test-root"
    body.appendChild(root)
    try {
        withComponentHarness(mountBrowserComponentHarness(root.id) { Editor() }) { harness ->
            harness.onNodeWithTag("title").performTextInput("سلام")
            harness.onNodeWithTag("preview").assertTextEquals("سلام")
        }
    } finally {
        root.parentNode?.removeChild(root)
    }
}
```

`withComponentHarness` disposes the mount even when setup, an action, or an assertion fails. A handle retains node identity: after replacement or detach, further actions fail as stale instead of silently matching another node.

## Queries and assertions

- `onNodeWithText(text, substring = false)` and `onNodeWithTag(tag)` require exactly one match. Failures include a bounded semantic tree.
- `assertExists`, `assertDoesNotExist`, `assertIsDisplayed`, `assertTextEquals`, and `assertEnabled` use live rendered state.
- `testState(name, value)` exposes an explicit typed test value; `assertState` supports strings, booleans, integral values, and finite floating-point values.
- Display checks include hidden ancestors, detached nodes, and inert subtrees. Enabled checks include disabled and inert ancestors.
- `awaitIdle(timeoutMillis)` is bounded and reports pending work instead of waiting indefinitely.

Use visible text for user-facing behavior and `testTag` only where stable visible text is not available. Never put secrets into tags or test state: both are DOM/HTML attributes.

## Semantic and visual goldens

`semanticSnapshot()` emits a versioned preorder tree with stable paths and fixed fields for role,
name, tag, visibility, disabled/inert state, text, and sorted typed state. JSON string escaping
preserves Unicode, whitespace, delimiters, and line breaks. Renderer identities, callback
implementations, generated DOM IDs, allocation addresses, and timestamps are excluded.

On JVM, `verifySemanticGolden(file)` is read-only and fails if the baseline is missing or differs.
The failure identifies the first semantic path and includes a bounded line diff.
`updateSemanticGolden(file)` is the separate explicit update operation and atomically replaces only
the requested file.

Browser images use Playwright's screenshot comparator with zero differing pixels. Baselines are
separate for JS/WASM and each pinned browser engine. The fixture fixes a 1280×800 viewport,
device scale 1, Arial font, `en-US`, light color scheme, reduced motion, and fixture-only animation
disablement; capture waits for `document.fonts.ready` and the explicit harness readiness marker.

```bash
# Verify all checked-in browser goldens without modifying them
./e2e-tests/run-harness-container.sh

# Explicitly update exactly one platform/engine baseline
./e2e-tests/update-harness-golden-container.sh js-chromium

# Prove changed and missing images fail while preserving baseline bytes
./e2e-tests/verify-harness-visual-failures-container.sh
```

## Merged JVM coverage gate

Kover 0.9.8 instruments the JVM tests for `summon-core`, `summon-cli`, `summon-aether`,
`summon-devtools`, `summon-test`, and `diagnostics`. Run the same gate used by required CI:

```bash
./gradlew :coverageCheck -x :summon-core:copyHydrationBundles
```

The gate generates `build/reports/kover/html/index.html`,
`build/reports/kover/report.xml`, and `build/reports/kover/modules.txt`, then rejects a missing or
empty report, a report with no classes or branches, or merged branch coverage below 80%. The
module manifest is part of the report artifact and records every merged JVM module and the
current exclusion policy.

Kover does not instrument Kotlin/JS or Kotlin/Wasm. Their browser results remain separate matrix
evidence and must not be described as part of the merged JVM percentage.

The CLI's generated-project integration tests launch nested Gradle toolchains. Under the Kover
agent they still generate every project shape and assert the emitted contracts, but omit only the
nested compilation because recursive toolchain instrumentation can deadlock or time out. Run the
complete generated-project builds without Kover through `:summon-cli:jvmTest`.

The controlled fixture intentionally leaves branches uncovered. This command must exit nonzero:

```bash
./gradlew -p e2e-tests/fixtures/coverage-gate-failure koverVerify
```

## Versioned API reference

Dokka 2.2.0 generates the aggregate reference for `summon-core`, `summon-cli`,
`summon-devtools`, `summon-test`, `diagnostics`, and the internal Aether compatibility module:

```bash
./gradlew :dokkaGenerate
```

The current release is written to `build/docs/api/<version>/`. Dokka compiles sample links from
each module's matching test source set and rejects undocumented public declarations, broken links,
or malformed KDoc by default. New public APIs, especially lifecycle- and privacy-sensitive
additions, require explicit ownership, disposal, exception, and platform contracts. Source
declarations default to the exact `v<version>` Git tag; CI sets `SUMMON_DOCS_SOURCE_REF` to its
immutable commit SHA while qualifying unreleased changes. The versioning plugin reads only real,
preserved outputs under `docs/api-versions`; generation never deletes that directory or publishes.

Sample functions are ordinary compiled tests. They exercise owned flow disposal, inspector
privacy and disposal, time-travel teardown, semantic snapshots, browser error-overlay teardown,
and the JVM/browser component harnesses. Run their source sets directly before generating docs:

```bash
./gradlew :summon-core:jvmTest :summon-devtools:jvmTest :summon-test:jvmTest \
  :summon-devtools:jsNodeTest :summon-test:jsNodeTest
```

Publication or GitHub Pages deployment is a separate, explicitly authorized operation.
