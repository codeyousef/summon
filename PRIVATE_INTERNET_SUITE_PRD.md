# Summon PRD — Private Internet Suite framework readiness

Revision 2 · audit date 2026-10-05 · combined implementation scope updated 2026-10-06.

## Agent entry point

1. Read sections 1–3 and the final Authority/boundaries section once.
2. Select the first incomplete package whose dependencies are actually complete; for an unstarted implementation select SU-00.
3. Retrieve that package heading, its source owners, tests and only the relevant appendix rows. Do not load this whole document into a small context window.
4. Read the full owning function and actual callers, then write the minimal regression and implement that one slice. Re-audit if the implementation SHA differs from the recorded baseline.
5. Record real results outside tracked session notes; select the next ready package. A review/credential/platform prerequisite stays explicitly blocked or not_run, never guessed.

## 1. Audited baseline and required outcome

Repository `/mnt/Storage/Projects/summon`, upstream `https://github.com/codeyousef/summon`. After `git fetch origin --prune`, latest remote main is `c66968f4038a78b9bf15035d24c91af44f4a833f` (2026-08-20). At the initial audit, the checkout was local `main` at `71bf76f19ee0734ac2bd60d119907a370a3daf41` (2026-02-10), clean before this PRD. Authorized implementation now uses `codex/summon-0.8-private-suite` based on the audited `c66968f4038a78b9bf15035d24c91af44f4a833f` baseline. Latest tracked source was inspected from a `git archive origin/main` export, not the stale checkout. Future implementation must start from the audited latest baseline or a newer explicitly re-audited commit; never apply these findings blindly to the old local main.

Latest source declares version 0.7.0.4, Kotlin 2.3.0, Gradle 8.11, JVM target 17, coroutines 1.10.2 and serialization 1.9.0. `summon-aether` catalogs Aether 0.4.2.1 and targets JVM/wasmJs, without a JS target. Current Aether main is e1a3ddd71193a7152aaaa7d9f51055e7a6dd0319, version 0.6.0.1, Kotlin 2.3.21/Gradle 8.12/JVM 21 and catalogs Summon 0.7.0.2. These are real source mismatches, not a tested release matrix. The skill's suggested version 0.7.0.2 does not override inspected current source or prove artifact availability.

Required outcome: real Summon components and lifecycle render the suite's KMP presentation state in Kotlin/JS browsers, with accessible controls, privacy-safe navigation, bounded work and strict CSP. R1 web Mail/Calendar/Aliases/scoped Security are mandatory. The Kotlin/JS extension popup uses Summon with a separately authorized native broker; it does not become a browser-hosted vault by default. Compose remains the native UI. Optional wasmJs is independently gated and must not delay Kotlin/JS.

## 2. Source-grounded capability inventory

All paths relative to the **latest source** root. Namespace `codes.yousef.summon`. `commonP` expands to `summon-core/src/commonMain/kotlin/codes/yousef/summon`; `jsP` expands to `summon-core/src/jsMain/kotlin/codes/yousef/summon`. These are documentary abbreviations. Existing source contains several state/lifecycle/renderer families; resolve the actual component call chain before editing.

| Capability | Source evidence/owner | Finding | Package |
|---|---|---|---|
| JS/JVM core, renderer, components | summon-core/build.gradle.kts; commonP/runtime/PlatformRenderer.kt; jsP/runtime/PlatformRenderer.kt | Present; actual mounted browser workload unqualified | SU-00/01 |
| StateFlow bridge | commonP/state/StateFlowIntegration.kt | Confirmed collector/listener lifecycle gap: creates standalone CoroutineScope, returns state without disposer | SU-02 |
| Runtime effects | commonP/runtime/Effects.kt | LaunchedEffect/DisposableEffect present; key/removal ordering needs qualification | SU-02 |
| Legacy lifecycle convenience | commonP/core/Lifecycle.kt | Commented-out, inactive coroutineScope/launchWhen* sketches are not supported APIs; qualify active whenActive and provide owned binding API | SU-02 |
| Browser routing | commonP/routing; jsP/routing/RouterJs.kt | History/popstate/disposal present; guard/privacy/deep-link behavior needs qualification | SU-03 |
| HTML content | jsP/runtime/PlatformRenderer.kt renderHtml/sanitizeHtml; commonP/components/display/RichText.kt | Confirmed sanitizer is regex-based; unsuitable as an untrusted-mail security boundary | SU-04 |
| CSP | commonP/ssr/HydrationSupport.kt, StandardHydrationSupport.kt; jsP/web/BrowserCompatibility.kt | Confirmed inline executable scripts and eval probes; strict CSP qualification/fixes required | SU-05 |
| Virtualization | commonP/components/layout/LazyColumn.kt/LazyRow.kt | Exists, not missing wholesale; confirmed string onscroll path conflicts with no inline script policy | SU-05/08 |
| HTTP | commonP/effects/HttpClient.kt; jsP/effects/HttpClient.kt | All usual verbs present; timeout Promise.race does not abort fetch, response.text unbounded, broad error catch | SU-06 |
| Browser storage | commonP/effects/Storage.kt and JS actual | LOCAL/SESSION/MEMORY string KV present; no encrypted transactional IndexedDB contract in this surface | SU-07 |
| File selection | commonP/components/input/FileInfo.kt/FileUpload.kt; jsP/.../FileInfo.kt | Real JS File handle exists; selection is not proof of bounded binary chunk transfer/resume | SU-09 |
| Accessibility | commonP/accessibility; modifier/AccessibilityModifiers; focus/FocusManagement.kt; Modal.kt | Semantics exist; Focusable/FocusableContainer accept onFocusChanged but never wire it in wrapper; real modal/focus behavior unqualified | SU-10 |
| Aether SSR bridge | summon-aether SummonAether.kt/Jvm.kt; PlatformRendererStoreJvm.kt | respondSummon exists; ThreadLocal renderer plus suspending rendering requires concurrency qualification | SU-11 |
| Browser tests | summon-core/build.gradle.kts; e2e-tests/playwright.config.ts | JS browser test task disabled; Playwright currently Chromium-only | SU-12 |
| Crypto, IndexedDB encryption, broker, alarms, VPN, recurrence/import parsing | Suite/native responsibilities | Implement suite adapters; do not add a substitute security authority or tunnel to Summon | SU-07/09/13–16 |

No builds or executable browser tests were run during the initial audit. Subsequent implementation added a locked JVM/JS/WASM source consumer and real browser regressions for selected renderer, flow and effect contracts. Those targeted checks do not qualify every package or the full suite. A code-supported path and an existing regression file alone are not a passed capability. Do not call all framework features broken: qualify existing routing/effects/virtualization/accessibility first and repair only reproduced contract failures.

## 3. Dependency-ordered delivery

| Package | Release | Dependencies | Exit |
|---|---|---|---|
| SU-00 compatibility and fixtures | R0 | none | Actual JVM/JS source consumer compatibility |
| SU-01 stable renderer/recomposition | R0 | SU-00 | Mounted UI correctly updates without duplicate nodes/handlers |
| SU-02 owned state/effects/lifecycle | R0 | SU-01 | Every collector/listener/job disposed; no stale private state |
| SU-03 navigation and route teardown | R0 | SU-02 | Deep link/history/guard/disposal tests |
| SU-04 safe untrusted-content seam | R0/R1 | SU-01 | Structured text rendering and malicious HTML rejection |
| SU-05 strict CSP/hydration/style | R0/R1 | SU-01, SU-04 | Production JS executes with no unsafe script exemptions |
| SU-06 bounded transport/effects | R0 | SU-02 | Abort/limits/errors and websocket lifecycle |
| SU-07 secure browser storage seams | R0 | SU-02, SU-06; suite storage/crypto contract | Ciphertext transactional adapter, visible unavailable state |
| SU-08 virtualized stable lists | R1 | SU-01/02/05 | Real 100k mailbox workload with bounded DOM |
| SU-09 bounded file/worker/media seams | R0/R1/R2 | SU-02/06/07 | Bounded selection/read/upload/cancel/resume |
| SU-10 accessible input/focus/RTL | R1 | SU-01/02/03/08 | Keyboard, actual focus, IME and mixed-direction flows |
| SU-11 Aether SSR bridge isolation | R0 | SU-00/05; AE-00/02/08 | Safe shell/public-only render; concurrent isolation |
| SU-12 actual browser qualification | R0/R1 | SU-00; grows per package | Real JS browser and strict-CSP suite |
| SU-13 encrypted cross-client spike | R0 | SU-01–07/09–12; AE-12 | KMP-001..005 actual roundtrip/revocation |
| SU-14 R1 surfaces/extension qualification | R1 | SU-08/10/13; suite WP-05..17 | Shared action semantics and private core flows |
| SU-15 Drive/assistance integration | R2/R3 | SU-14, suite R1-H | Scoped media/attention/connectors without secret disclosure |
| SU-16 social/contextual repost/public UI | R4/R5 | SU-15; suite WP-24/25/25C | Same ordered safe context as Compose; no snapshots/leaks |

## 3A. Summon v0.8.0 developer-experience requirements

The user requires the entire preceding SU-00..16 scope **and** all v0.8.0 roadmap features to be implemented, tested and verified. This section incorporates `docs/roadmap/v0.8.0.md` without replacing any suite-readiness package. Work is confined to Summon; Aether source must not be edited. Aether integration may consume its existing pinned artifact/API and use synthetic fixture boundaries, but missing external application/platform/review evidence stays explicitly incomplete rather than redefining success.

| Package | Roadmap requirement | Dependencies | Completion evidence |
|---|---|---|---|
| DX-01 | Visual component inspector | SU-01/02/05/10 | Actual debug overlay/component tree; current props/state; selection highlights correct DOM; live writable-field edits update UI; excluded from production |
| DX-02 | Time-travel debugging | SU-01/02, DX-01 | Bounded state recording/timeline; restore and action replay; validated session export/import; deterministic browser tests |
| DX-03 | Component test harness | SU-01/10/12 | Isolated setup/teardown; semantic text/tag finders; visibility/text/state assertions; real click/type/scroll plus shared simulated renderer tests |
| DX-04 | Snapshot testing | DX-03 | Deterministic semantic snapshots and browser visual capture; checked-in goldens; comparison and readable/visual failure diffs |
| DX-05 | Development error overlay | SU-02/05/12 | Unhandled/rejected error display; Kotlin source maps; validated editor and verified-source-viewer line links; teardown and production exclusion |
| DX-06 | Kover coverage gate | DX-03; SU-00/12 | All supported modules configured; merged reports; CI fails below80% branch coverage; narrow documented generated/platform-binding exclusions; actual report |
| DX-07 | Dokka versioned documentation | all public APIs | HTML generated; public KDoc and runnable @sample examples; previous-version navigation; CLI-generated examples exercise each new feature |

Debug tools MUST be opt-in and debug-build-only. Inspector props/state and time-travel history cannot automatically record decrypted suite content, tokens, credentials or keys. Each inspected field has explicit public/redacted classification; redacted values are never recoverable through tree details, live editing, export/import, snapshot or errors. Sensitive actions cannot be replayed as external effects. Test synthetic public fields to prove full inspector/edit/replay behavior without relaxing those privacy requirements. Production bundles must demonstrably exclude recording/error-stack/debug tool capabilities.

Roadmap quality gates remain mandatory: Playwright interaction coverage on both JS and WASM, Chromium/Firefox/WebKit (Safari engine coverage separately identified from actual Safari device testing), applicable visual regression, >=80% branch coverage, zero newly introduced Detekt/Ktlint warnings, complete public KDoc/Dokka examples, axe-core checks, manual keyboard/screen-reader verification and explicit unsupported-platform degradation. Optional WASM in the suite does not waive the v0.8.0 framework's WASM test requirement. No green mocked test substitutes for a real-browser gate.

Release version may be set to0.8.0 only when the combined implementation and verification gates pass; no release publication or deployment is implied. Current work branch is an implementation branch, not evidence of readiness. Preserve all159 source acceptance cases and exact supplied defaults below. Each future completion audit must inspect actual source and tests for SU and DX packages plus required external evidence, with missing/weak evidence treated as incomplete.

## 3B. Detailed v0.8.0 implementation contracts

These contracts supplement the roadmap. New names below are proposed APIs until implemented and qualified. Keep developer tools in a separate `summon-devtools` KMP module with JVM, JS and wasmJs targets; declare its dependency explicitly in debug consumers. Production consumers must not depend on this module. Runtime hooks in `summon-core` may emit structural node IDs and component kinds, but must not reflect, stringify, serialize or retain arbitrary props, state, callback captures or exceptions. All value inspection requires explicit field registration. No tool installs itself merely because it is on the classpath.

### DX-01 — Inspector registration, tree, selection and edits

Owners: new `summon-devtools/src/commonMain/kotlin/codes/yousef/summon/devtools/InspectorSession.kt`, browser overlay implementations under jsMain/wasmJsMain, and minimal renderer observation hooks in common/runtime and corresponding platform renderers. Do not substitute a manually supplied sample list for the actual mounted renderer tree. Instrument creation, parent assignment, keyed movement, replacement and disposal; use renderer identity plus root-local monotonic node IDs, never array position or user-visible text as identity. Two roots must have independent registries and overlays. Observer installation returns an idempotent disposer; root disposal detaches it and removes all retained references.

Define `DebugFieldSensitivity` with PUBLIC and REDACTED. Default registration is REDACTED. A redacted field exposes only its label and a constant redaction marker; its getter must never be invoked by inspector, recorder, export, search or error display. PUBLIC values use explicit typed codecs limited to null, Boolean, bounded String, integral numbers and finite floating-point values. Reject unsupported objects; do not call arbitrary `toString()`. Each field declares whether editing is allowed and has a validated typed setter; registration must not silently turn a read-only State into mutable state. Explicitly register synthetic demonstration fields. Private suite views register no Mail/Vault/key/credential values as PUBLIC.

Implement a tree panel with expand/collapse, keyboard selection, root/component labels and current registered fields. Selecting a node highlights the exact connected DOM element, using a separate noninteractive overlay with `pointer-events: none`; highlight must follow scroll/resize and clear when the element disappears. Edits validate before calling the application's setter on its render dispatcher. Malformed edits show an error without mutating state. Rendering panel labels uses text nodes, never HTML. Closing or disposing removes all listeners, overlays and scheduled animation callbacks. Styling and script delivery must satisfy SU-05 strict CSP.

Acceptance DX-T01: actual nested mount and keyed reorder update hierarchy; one selection highlights exactly one connected element; public mutable edit recomposes the app; invalid/read-only/redacted edits fail without writes; redacted getter invocation count stays zero; two roots do not expose each other's fields; 100 open/close cycles leave zero listeners; production dependency/bundle inspection contains no devtools code or recorded values. Run JS and WASM browser tests, keyboard navigation and accessibility checks.

### DX-02 — Bounded recording, restore, replay and session interchange

Owners: new common `StateTimeline.kt`, `DebugSessionCodec.kt`, browser timeline UI and inspector field registration. Recording is explicitly started, paused, cleared and stopped within one InspectorSession. Record only opted-in PUBLIC fields using their typed codecs; exclude redacted fields, private arbitrary objects, callback closures, transport effects and authentication state. Stable field IDs consist of structural component ID plus registered field name, not secret values. Default capacity is 200 mutations and maximum configurable capacity 10,000; reject nonpositive or larger limits. Store sequence number, field ID, typed before/after values and optional registered action ID. Equal writes do not add a mutation. Bound each string to 4,096 UTF-8 bytes and export/import to 1 MiB before parsing. Removing a component removes live getter/setter/action references; historical values remain only until explicit clear/session disposal or capacity eviction.

Timeline scrub restores registered writable fields in deterministic sequence without recording its own writes. Read-only fields remain visibly unrestorable. Branching after restore truncates future mutations before recording the next real mutation. Replaying actions requires explicit registered deterministic debug actions with declared effect kind PURE_UI; reject network, file, storage, clipboard, account, crypto, send, purchase and other external effects. Never invoke arbitrary callback names or deserialize executable code. Show replay failure and stop at the failed action; do not claim a complete replay. The tool cannot recreate destroyed components or unregistered external state; surface those limitations.

Session JSON has explicit formatVersion=1, field codecs, bounded entries and schema-validated values. Validate unknown versions, duplicate sequence IDs, missing fields, oversized strings, nonfinite numbers and unknown actions before any live mutation. Parse into an immutable validated plan, then require an explicit local Apply action; merely importing must not change application state. Export contains no stack paths, source tokens, URLs with credentials or redacted placeholders carrying data. Session disposal clears buffers and live references. Do not advertise perfect RAM erasure.

Acceptance DX-T02: 300 changes retain exactly the newest 200; scrub/branch ordering deterministic; restore does not recursively record; malformed/oversized import causes zero setters/actions; roundtrip synthetic sessions compare structurally; redacted getter never called; external-effect action rejected; session clear/disposal removes entries and references; DOM reflects restored public state on JS and WASM. Test timeline boundaries in common tests and interaction in real browsers.

### DX-03 — Real component harness and semantic finders

Owners: separate `summon-test` KMP test-support module, common semantic contracts, JVM simulated renderer and browser adapters, plus isolated fixture entry points. Consumers explicitly add this module in test source sets. Define a harness that mounts one component root, owns render scope/scheduler, can await a bounded idle condition and always disposes in finally. Return a semantic-node handle from `onNodeWithText(text, substring=false)` and `onNodeWithTag(tag)`. Zero or multiple matches fail with a bounded structural tree description, not a silently selected first match. Node handles include generation/identity and reject use after disposal or replacement. Query the actual rendered component/DOM semantics; simulated tests must be clearly labeled and cannot qualify real focus, storage, CSP or hydration.

Support assertExists, assertDoesNotExist, assertIsDisplayed, assertTextEquals, assertEnabled and explicit typed state predicates. Hidden ancestor, inert, disabled and detached semantics must be distinguished. `performClick`, `performTextInput` and `performScrollTo` delegate to the platform adapter's real interaction path; do not bypass callbacks by directly modifying the state under test. Browser adapters must dispatch the same input/change/pointer events supported by the actual renderer. Provide explicit test tags through Modifier attributes, preserving tags independently of text. Expose deterministic scheduler control for simulated coroutine tests; idle waits have an explicit deadline and fail with pending-work diagnostics instead of infinite sleeps.

Acceptance DX-T03: finders reject ambiguity and stale handles; escaped Unicode/Arabic text and tags work; hidden/detached/disabled assertions are correct; click changes actual state and DOM exactly once; controlled typing and scrolling trigger real callbacks; exception during setup/action/assertion still releases scopes/listeners; 100 harness lifetimes leak zero roots/collectors. Include public KDoc and runnable examples generated by the CLI for JVM and browser test setups. Common harness tests alone do not satisfy browser qualification.

### DX-04 — Semantic and visual golden snapshots

Owners: `summon-test` snapshot helpers, committed synthetic golden files, Playwright screenshot fixtures and diff artifacts. Semantic serialization must use deterministic key ordering, stable hierarchy and explicit role/name/tag/visibility/disabled fields. Exclude allocation addresses, timestamps, random renderer IDs and callback implementations. Preserve meaningful Unicode and whitespace; normalization must not erase content differences. Escape line breaks and delimiters unambiguously. Never snapshot real private-suite data or credentials. A golden update requires a separate explicit developer command; regular tests never overwrite goldens after failure.

For image snapshots, pin browser engine/version, viewport, device scale, fonts, locale, color scheme and reduced-motion setting. Wait for fonts and explicit component readiness, disable fixture-only nondeterministic animations and avoid arbitrary sleeps. Use Playwright's screenshot comparator with documented per-fixture thresholds; do not make failures pass by increasing tolerances without evidence. Missing golden is a test failure. Keep actual/expected/diff images as failure artifacts, and give the semantic path for text differences. Separate JS from WASM golden identities unless byte/pixel equality was demonstrated. Platform-specific images must not silently share incompatible baselines.

Acceptance DX-T04: intentional text/layout/color changes fail and produce useful diffs; same unchanged fixture passes repeatedly; missing baseline fails; ordinary run does not change tracked golden bytes; explicit update affects only requested fixture; Unicode/RTL/hidden semantics survive serialization; both semantic and browser visual paths have at least one meaningful regression. Production code must not include the test module or golden assets.

### DX-05 — Development errors, mapped sources and safe links

Owners: new browser `ErrorOverlay` adapters in summon-devtools, common bounded error model and source-location parser, existing ErrorBoundary integration and source-map build configuration. Install only after explicit debug session enablement. Catch `error` and `unhandledrejection` with removable listeners while preserving application/host error behavior; do not swallow cancellation or falsely recover the application. The overlay must distinguish mapped Kotlin location from unmapped generated JS/WASM location. Production UI must not expose exception bodies, stack traces or local source paths.

Do not stringify arbitrary rejected objects or inspect their properties. Default overlay displays a generic error category and structural location; application debug policy may explicitly permit bounded synthetic error text. Cap frames at 100 and text at 16 KiB. Display through text nodes. Source maps resolve only against configured same-origin build assets and verified build identity; no untrusted stack frame triggers arbitrary network fetches. Editor links use a configured protocol and workspace-root allowlist, validate line/column bounds and encode paths; reject traversal, credentials, javascript/data URLs and arbitrary schemes. Verified source viewer links use configured HTTPS origin and exact build/source identity; missing mapping shows an honest unavailable state, never a guessed file. All overlay assets remain CSP compatible.

Acceptance DX-T05: synchronous and rejected synthetic UI errors appear once; Kotlin source map identifies the actual original line; invalid/unmapped frames remain unmapped; configured editor/viewer links target the intended line; malicious error text is inert; arbitrary rejection objects are not inspected; no external fetch occurs from hostile frame URLs; close/dispose removes all listeners; production build contains no overlay/source-map upload secrets or diagnostic private payload. Include browser checks on JS and WASM and unit tests for link validation.

### DX-06 — Merged Kover branch coverage and CI gate

Owners: root Gradle configuration, all Kotlin modules, version catalog and CI workflow. Verify official Kover compatibility with the pinned Kotlin/Gradle versions before choosing a plugin version; pin the chosen version. Configure supported JVM instrumentation for core, CLI, Aether integration, diagnostics and new developer/test modules. Browser JS/WASM coverage is a separate browser evidence row; do not claim Kover JVM reports cover these targets. Merge module reports into one root HTML/XML output and provide reproducible report and verification tasks. Include branch counters in evidence, not line-coverage substitutes.

Set minimum branch coverage to 80% and wire verification into required CI/check lifecycle. A deliberately reduced-coverage fixture must fail the gate with a nonzero exit code. Publish reports as CI artifacts after tests, without publishing user data. Exclude only exact generated classes or foreign/platform binding wrappers with rationale and source ownership; no package-wide exclusion of difficult production logic, SSR, security, devtools or network code to force the threshold. Keep exclusions visible in build configuration and report. Empty/missing report or no covered classes fails visibly rather than passing by default. Add meaningful tests to raise coverage; do not rewrite coverage numbers, manufacture execution counters or lower the requirement.

Acceptance DX-T06: clean required build produces nonempty merged reports; all supported modules appear; actual branch coverage is at least 80%; controlled below-threshold case fails; legitimate generated exclusions are inspectable and limited; JS/WASM reports are labeled separately; same command runs locally and in CI. Integration tests requiring external providers remain separately incomplete when those providers are unavailable.

### DX-07 — Versioned Dokka reference and executable API samples

Owners: pinned Dokka plugin in root/catalog, all public Kotlin module API KDoc, sample source sets, documentation build and versioned static output. Verify plugin compatibility against official docs and pinned Gradle/Kotlin before installation. Generate HTML appropriate for GitHub Pages without publishing as a side effect. Aggregate module navigation and source links for exact release identity. Output current API version under a version-specific directory and preserve existing previous-version outputs; no task deletes all older versions. A version selector links only versions actually present, never fabricates unavailable pages.

Every public API needs accurate parameter, return, ownership/disposal, exception and platform behavior documentation, and `@sample` pointing to compiled example code where meaningful. Samples must compile against current source and exercise real APIs, including owned flow bindings, inspector opt-in/privacy, time travel, harness, snapshots and error overlay teardown. Avoid examples that require nonexistent APIs or unsafe unowned collection. CLI-generated examples must resolve the same declared versions and pass their documented commands. Documentation must distinguish proposed PRD contracts from shipped callable APIs and supported targets from no-op targets. Enable appropriate warning checks; resolve new undocumented public symbols and broken sample/source links before acceptance.

Acceptance DX-T07: HTML build succeeds for all public modules; samples compile; links/source paths/sample identifiers resolve; version selector preserves previous releases; public API additions have accurate lifecycle/platform KDoc; CLI examples run; generated output contains no credentials, local private path dump or unpublished session notes. Publication/deployment remains a separately authorized action.

## 4. Detailed framework work packages

### SU-00 — Resolve current source and establish one small real fixture

Owners: settings.gradle.kts, gradle/libs.versions.toml, version.properties/version.gradle.kts, summon-core/build.gradle.kts, summon-aether/build.gradle.kts, summon-cli generated templates. Root project name is `summon`; modules include summon-core, summon-cli, summon-aether and diagnostics. Publication group in root build differs from version management, so inspect actual module publications before writing coordinate substitutions. Do not paste a guessed `summon:latest` dependency.

Create a small JS/JVM consumer with real Text/Button/TextField/state/effects/router and Aether-served public shell. Pin source SHAs and lock Gradle/NPM dependencies used by the fixture. Prefer explicit configured composite-build paths; do not assume consumer runs from /mnt/Storage. Align with Aether/JDK21 and the suite's actual Compose/Kotlin compiler version through tested locks. JVM target17 may run on JDK21; prove before upgrading target gratuitously. Keep authority-only Aether classes outside browser bundles.

`summon-aether` is a JVM SSR integration, so the absence of JS there is not by itself a browser-core failure. Do not add a browser server Exchange implementation to force a JS target. Browser API calls should use qualified Aether BrowserHttpClient through a suite transport, or the improved SU-06 adapter where the contract requires it. Remove old artifact/source mismatch only through a tested compatibility change.

Acceptance SU-T00: compile actual consumer JVM+JS; serve production JS and mount components; inspect published/source dependency identity; reject unsupported compiler combination visibly; classpath/bundle free of DB/authority/secret modules; CLI template outputs resolve their declared versions. Optional wasmJs row stays not_run/unsupported until executed. No implicit Maven publish.

### SU-01 — Stable rendering, controlled state and event identity

Owners: commonP/runtime/{State,Effects,PlatformRenderer}, composer families, jsP/runtime/PlatformRenderer, component render methods. Nearest tests: commonTest runtime State/PlatformRendererContractTest; jsTest runtime JsPlatformRendererTest and SSRHydrationTest. Follow active imports: source has `annotation.Composable` and `runtime.Composable` paths; resolve actual required annotation rather than inventing a new one.

Prove deterministic mount/update/unmount with keyed identity. A state change updates the intended DOM/property without duplicating DOM, render-side collectors or callbacks. Item keys preserve item identity across insertion/removal/reordering; no index-only selection binding for mailbox items. Event handlers use stable wrappers reading current state, not stale captured values. Remove replaced listeners; clear renderer ownership on teardown. Errors surface safe unavailable state rather than a success-shaped empty screen. DOM values must match controlled input state without unnecessary replacement/focus loss.

Acceptance SU-T01: counter edit; stateFlow emits new render model; async stale response loses to newer route identity; callback update uses latest model; keyed reorder keeps selected item/focus; deleted item listener removed; nested mount/unmount no double render; two roots do not share state. Count nodes/listeners/effects before and after 100 route swaps. JVM SSR and JS DOM separately tested; fake renderer only proves shared dispatch contract.

### SU-02 — Explicit collector, listener and coroutine ownership

Owners: commonP/state/StateFlowIntegration.kt; runtime/Effects.kt; core/Lifecycle.kt; lifecycle/Lifecycle.kt and JS lifecycle actual. Inspect existing addListener removal APIs and effect registration before extending them. Do not rewrite the entire state framework.

Add lifecycle-owned bridging contract (proposed `FlowBinding<T>` containing state plus `dispose()`, and/or composable `collectAsState` overload tied to composition). Caller supplies scope or binding owns a child Job under current component scope. Every collector/listener is canceled/removed exactly once at unmount, route key change, logout or explicit disposal. Read-only StateFlow binding exposes read-only state, no writeback. Mutable bridge has deterministic equality guards preventing feedback loops. toSharedFlow/asSharedFlow getters must not silently create an unlimited new collector per access. Deprecate convenience bridges with no owner or require explicit disposer; document migration.

The old coroutineScope/launchWhenCreated/Started/Resumed sketches are inside a block comment and must not be treated as callable APIs. Qualify active whenActive cleanup; add a supported lifecycle launch API only through the owned lifecycle contract, executing supplied blocks at real states. Return one lifecycle-owned scope per owner, cancel at DESTROYED, never fresh untracked scope per getter. LaunchedEffect cancels old key job before installing next, rethrows cancellation and associates cleanup with actual component removal. DisposableEffect cleans exactly once including exceptions. No GlobalScope or durable business job in a screen scope. Durable sync/import/send state lives in suite ledgers, not these coroutines.

On logout lock UI immediately, cancel reads/indexing/uploads/live subscriptions and discard private render models; delayed tasks cannot repaint the old account. Framework teardown supports suite adapters clearing worker/key/object URL resources. No promise of perfect browser RAM zeroization.

Acceptance SU-T02: collector count exactly one while mounted and zero after disposal; 100 mounts leak zero collectors/listeners; lifecycle pause/resume/destroy; key change cancellation; late network callback after logout ignored; two-way state no infinite emissions; callback exception cleanup; externally supplied parent cancellation; discarded accountA flow cannot render into accountB root. Use coroutine controllable scheduling, not sleeps to hide race.

### SU-03 — Browser history, deep links, guards and private route teardown

Owners commonP/routing Route/Router/RouteGuard/RouteState and jsP/routing/RouterJs.kt. Existing popstate listener uses DisposableEffect: preserve and test that behavior; do not claim it is missing.

Support suite paths `/mail`, `/mail/thread/:id`, `/mail/compose`, `/calendar`, `/calendar/event/:id`, `/aliases`, `/aliases/:id`, `/security`, `/security/devices`, `/security/recovery`; later `/drive/*`, `/attention`, `/connectors/*`, `/feed`, `/people/:handle`, `/communities/:id`. Use opaque IDs/handles. Body/search/credential text cannot enter query/history/hash or page title. Browser back/forward/reload deep link restores intended route after public shell boot and enrollment unlock. No decrypted SSR fallback.

Route transitions dispose previous subscriptions/effects before new private state can display. Guard checks loading/locked/feature/permission states explicitly; it does not grant server authorization. Invalid/malformed percent escapes/path traversal/unmatched route yield safe page; no raw private string copied to error log. Programmatic navigation stays same-origin for internal links; outbound links require distinct explicit API. Dirty draft navigation offers keep encrypted draft or cancel transition, not silent content loss. On refresh stale deep link requires fresh authorization.

Acceptance SU-T03: direct reload all R1 route families; history sequence back/forward; denied/disabled route not mounted; locked state does not flash old content; malformed path; router disposal removes popstate; two tab routing independent; navigate mid-fetch then response arrives; logout and browser Back never restores plaintext; unsaved draft remains encrypted and can be reopened. Test server shell fallback as AE/SU integration, not just route regex.

### SU-04 — Safe content rendering and explicit trusted-HTML boundary

Owners commonP/components/display/RichText.kt, components/foundation/HtmlPrimitives.kt, common/JS/JVM renderer renderHtml/raw HTML/SVG paths. Current JS sanitizer strips regex script/on*/javascript patterns; it does not establish an HTML parser security boundary.

Add a safe document-tree rendering interface for untrusted limited formatted content; text leaves always use textContent/escaped text. Allow only suite-reviewed node kinds and link/CID references; no scripts/forms/iframes/embedded objects/events/style URLs. URL policy checks decoded normalized scheme/host; mail-provided data cannot load remote images/fonts or arbitrary CSS. Plaintext fallback always available, original MIME bytes retained encrypted by suite. CID references resolve only authorized local object URLs, revoked on disposal; remote images off unless explicit per-message consent.

For generic HTML convenience, either use a maintained parser-based sanitizer with explicit policy and pinned reviewed version, or clearly restrict raw HTML to a trusted author-code type and reject untrusted input. Do not call regex filtering secure. Freeze sanitizer allowlist/version and malicious fixtures before enabling HTML mail. Keep public trusted HTML APIs distinct, with source compatibility migration; do not silently use RawHtml for domain strings. JVM SSR and browser must share allowlist semantics or reject unsupported content; no sanitizer difference that becomes dangerous on hydration. SVG/raw markup and CSS sinks need the same trust inventory.

Acceptance SU-T04: unquoted/mixed-case/multiline event handlers, encoded javascript URLs, malformed nested HTML, SVG/math namespaces, srcdoc, forms, data/blob URL injection, CSS url/import, DOM clobbering, external tracking image and malicious link labels. Assert no execution and no unauthorized network request, not merely absence of `<script>` substring. Benign text escapes; RTL text intact; safe CID visible; original source preserved only encrypted; approved outbound link displays final hostname. CSP is defense in depth, never the only sanitizer.

### SU-05 — Strict CSP-compatible scripts, hydration and styling

Owners commonP/ssr/HydrationSupport.kt/StandardHydrationSupport.kt, jsP/runtime hydration clients, jsP/web/BrowserCompatibility.kt, LazyColumn/LazyRow, CSS injector/modifier emission and server HTML helpers.

Private shell CSP starts default-src none and allows only actual first-party sources needed by production bundle. Mandatory object-src none, base-uri none, frame-ancestors none, form-action self. No unsafe-eval or unsafe-inline script exceptions. Move inline boot code into versioned first-party external script or add a per-response nonce/hash API propagated to **every** executable script. No interpolated executable string from user data. Remove eval/Function-based capability probes; detect supported features without dynamic compilation or report unsupported. Inline string onscroll in LazyColumn must become actual addEventListener/bound callback with disposal; audit all modifier/renderer event strings.

Hydration JSON script data escapes `<`/closing script and appropriate line separators; bounded schema with no decrypted private values/tokens/key material. Hydration callback identifiers are opaque per context and may not authorize arbitrary server effects. Private component effects execute only after local enrollment/unlock, never server callback evaluation. Public SSR hydration data contains only current permitted public data. Use bundle-generated actual hashes/nonces; hash+cache config updated atomically with artifact. Styles: nonce-aware style elements or prebuilt first-party stylesheet; typed modifiers must keep working under deployed style-src policy without silently weakening script CSP. Distinguish necessary style attributes from script handlers and test actual policy.

Acceptance SU-T05: production optimized bundle served under real restrictive CSP; no unexpected securitypolicyviolation events, eval or inline handlers; buttons/router/virtualization/dialog all work; adversarial hydration `</script>` stays data; mismatch safely reloads public shell without serializing private state; no third-party script/tracker; all origins/connections enumerated; replayed callback context cannot access another session. Development webpack source-map/eval modes are not production evidence.

### SU-06 — Bounded abortable transport and owned live signals

Owners commonP/effects/HttpClient.kt and JS actual; effects/WebSocket.kt and JS actual; prefer Aether's qualified BrowserHttpClient for suite API once AE-10 passes. Do not create two competing suite transports. Summon's general HTTP client remains reusable, but secure policy is explicit.

Add configurable request/response byte caps; check UTF-8 bytes, not Kotlin string length. Stream response with bounded accumulation for JSON, enforce absent/misleading Content-Length too. Validate status/Content-Type before decode; 204/HEAD no-body supported. AbortController binds to timeout and coroutine cancellation; clear timers/readers in finally. Promise.race alone does not cancel fetch. Rethrow CancellationException before generic exception translation. Preserve safe typed status/error code/request ID/retry policy without echoing response-body secrets in exception strings. General HTTP can support cross-origin apps; suite JSON profile is root-relative same-origin, redirects error and credentials same-origin, unsafe methods CSRF via approved authority provider. Headers and path never logged by default.

Separate authorized ciphertext object transport omits cookies/tokens on object URLs; parts/ranges bounded, expiry respected. WebSocket utility binds callbacks/listeners/reconnect to caller scope, bounded message queue, cancel/backoff on offline/background and stop at logout/revoke. Hint is opaque wake signal; fetching durable cursor is suite sync domain. Reconnect cannot replay an old private model into new account.

Acceptance SU-T06: timeout abort observed at synthetic server; cancellation frees fetch/timer/readers; oversize streaming response rejected before whole accumulation; duplicate unsafe retries require suite operation ID; 409/429/503 preserved; no plaintext fallback; cross-origin redirect credential denial; no stale response after logout. Websocket disconnect/reconnect/flood/disposal count and cursor recovery tested through actual browser+Aether. Existing HttpClient demos alone are not evidence.

### SU-07 — Browser persistence, worker and capability seams

Owners commonP/effects/Storage.kt/TypedStorage, JS storage actual and browser lifecycle/capability helpers. Current string KV storage is fine for nonsensitive preferences; it is not encrypted durable private persistence. Do not modify generic localStorage so every unrelated app unknowingly encrypts with a fabricated key.

Suite owns `EncryptedLocalStore` and crypto/key-wrap interfaces. Add reusable browser primitives only where absent: async IndexedDB transaction handle, bounded binary read/write, atomic records+operation+cursor/index update, schema migration events, quota/eviction errors and worker messaging/disposal seam. Proposed interface contracts must be frozen before implementations; names are not pre-existing Summon symbols. Ciphertext only written to IndexedDB; plaintext search index is protected by suite encryption adapter, no clear key or secret in local/session storage/history/service worker cache. Wrapped enrollment material only through approved unlock policy. Worker receives only needed compartment data and validates typed messages/correlation/cancellation; no server authority token import.

Transaction commits record and checkpoint together or neither. Browser quota denied/evicted/blocked schema upgrade/worker unavailable yields typed visible resync/recovery/local capability state. Multiple tabs coordinate DB upgrades and logout with an explicit protocol; broadcast opaque lock/invalidation IDs, not plaintext. Durable business jobs survive renderer exit via suite ledger. Service worker, if used, caches public static assets only by default, not private API responses/keys/decrypted attachments; signed update/version compatibility does not make browser hosting immune to malicious client delivery.

Acceptance SU-T07: inspect localStorage/sessionStorage/IDB/caches after synthetic workflow; no marker plaintext or unlocked keys; close during transaction yields atomic restart; two tab update/blocked migration/logout; quota/eviction visible; rebuilding index from canonical encrypted objects; stale tab cannot repaint after lock; worker canceled and no unsolicited external network; browser offline not equated to full permanent backup. Crypto review/vectors live in suite, not invented inside Storage.

### SU-08 — Real virtualization with stable selection and CSP-safe scrolling

Owners commonP/components/layout/LazyColumn.kt/LazyRow.kt and LazyListState/scope, JS renderer renderLazyColumn and scroll dispatch. Existing virtualization API must be qualified/repaired rather than replaced with a static full list. Scope collection of lambdas itself needs bounded-data strategy for large paged consumers; DOM bound alone does not guarantee memory bound.

Bind real scroll/resize events; measure viewport; render visible rows plus configured overscan and spacer geometry; item key identity survives reordering. Variable row heights (long subjects, attachments, Arabic, expanded preview) adjust offsets without jumping selection. Paginated data provider loads at most needed page windows; no per-item unowned effects. Distinct loading/empty/locked/permission/error rows. Offscreen rows disposed, stale event callback cannot act on a newly recycled item. Focused row remains reachable while keyboard navigates; aria position/set metadata respects visible authorized total and can avoid exposing hidden social counts.

Proposed deterministic fixture: 100,000 items, viewport 600 CSS px, fixed rows 4 0px, overscan10 each side; mounted data rows <=36 including one boundary allowance. This is a new test policy, not a measured product limit. Separately variable-height fixture measures high-water DOM/memory at 1k/10k/100k rows. Mail index p95 target500ms and cached interaction p95<150ms belong to actual suite workload, not documentation comments.

Acceptance SU-T08: scroll beginning/middle/end; reverse scroll; insert/remove while selected; variable height/resize/zoom; keyboard selection/open correct object; stable anchor after paging; strict CSP events work; collector/node count returns baseline after exit; no O(total) DOM creation. Compare accessibility tree and visible content with an unvirtualized small reference fixture.

### SU-09 — File, bounded worker work and media lifecycle

Owners commonP/components/input/FileUpload.kt/FileInfo.kt, JS FileInfo.jsFile, effects/browser/media utilities, renderer file input; suite blob/crypto/transport adapters. Existing file handle proves selection only, not multi-GiB safe transfer.

Expose cancellable range reads on native browser File/Blob without base64 whole-file copies. Caller controls bounded chunk sizes/total bytes; reject negative/overflow offset/size. Read fresh buffers, release references after encryption/upload acknowledgment; do not stringify filenames into logs. Long CPU work yields or runs worker; worker API messages bounded/correlation-bound and terminated/disposed. File modification/size change during resume fails with new-version-required; browser reload may require user reselection, state visible. Suite chunk encryption uses 4MiB plaintext and 1 6MiB storage part target; wrapper preserves byte order/offset and never conflates crypto chunk with S3 part.

Inputs allow cancel/remove/retry/progress and accessible labels. Reject file selection over operation bounds before reading; import archives may stream records beyond an individual file cap but oversized records reported, not dropped. Authorized decrypted object URLs are created only after integrity checks, revoked at route/lock/delete/expiry/disposal. Separate safe attachment preview origin; no iframe of attacker HTML into private app. Media playback/seek validates chunks before displaying; thumbnails/transcripts client-encrypted. Unsupported codec/parser is actionable state, not server plaintext transcode.

Acceptance SU-T09: range edges/zero/final chunk/large synthetic file; upload abort/retry/resume with changed source; worker crash/no support; memory bounded by current parts/worker queue; no full file base64; cleanup after logout; object URL no longer available after teardown; integrity failure no media render; source file retained; quota and partial import report visible. Real S3/provider behavior separately qualified by suite.

### SU-10 — Semantics, focus, controlled inputs, IME and RTL

Owners commonP/accessibility/AccessibilityTree/FocusManagement, focus/FocusManagement.kt, AccessibilityModifiers, feedback/Modal, input components and JS renderers. Preserve typed modifiers and actual DOM semantics; synthetic accessibility tree alone is insufficient.

Wire `onFocusChanged` in Focusable/FocusableContainer to real focus/blur events with cleanup. Ensure native semantic buttons/inputs/labels/landmarks; icon-only actions have text names; disabled/read-only states distinct. Modal traps Tab/ShiftTab inside current dialog, labels role/dialog+aria-modal, makes background inert appropriately, Escape/backdrop follows explicit policy, and restores focus to invoker or safe fallback after removal. Focus and scroll survive rerender; toast/live announcements bounded and do not read private content by default.

Controlled TextField/TextArea handles selection/caret, compositionstart/update/end and input without dropping Arabic/IME text or moving caret on every keystroke. Async validation latest-value-bound; no silently trimmed password/secret input. Password reveal/clipboard belong approved broker UI, not generic data binding. Calendar grid/agenda keyboard focus semantics, accessible date/time/form error association, multiple list selection and confirmation buttons with specific target labels must be expressible through components.

Design fixture defaults from suite:16 CSS px body,4 4px targets where possible, OS scaling, dark/light/high contrast/reduced motion. Direction can be inherited per root/component, with `bdi`/equivalent isolation for mixed LTR address/URL inside Arabic text; stored protocol values remain locale-independent. Strings externalized, no English concatenation assumptions. Dense productivity layout resizes across desktop/narrow widths without giant hero headings.

Acceptance SU-T10: keyboard-only R1 flows; focus callback fires once per actual change; modal nested/repeated/removed invoker; accessibility tree exposes names/labels/errors/states; actual screen-reader sample walkthrough;200% zoom and text scaling; RTL English address isolation; Arabic IME edit selection; date locale/timezone; reduced motion; status visible without color. Automated a11y checks plus real focus/screen-reader evidence, never snapshots alone.

### SU-11 — Current Aether bridge, renderer isolation and safe public shell

Owners summon-aether/SummonAether.kt/respondSummon, JVM withRenderingContext/CallbackContextElement, common/JVM renderer store and SSR helpers. Existing store is ThreadLocal; rendering is suspend; bridge sets renderer before withContext. This is a concurrency-risk qualification, not a proven live leak. Prove renderer ownership across coroutine dispatcher hops and concurrent requests before deciding exact patch.

Use per-request renderer/callback context with explicit coroutine context propagation and finally cleanup; test that lookup sees the correct renderer before/after suspension. No global renderer or shared callback registry carrying private per-session state. If thread-local is retained, bind with a proper context element across suspending work; install renderer within the owned context, not outside it. Each response can fail/cancel independently; no data/callback/head/style leakage to next request. Bound callback lifetime/size and cannot use it as ambient security authority.

Serve private app shell and public boot config only. Private payloads are client-rendered; don't introduce decrypted mail into JVM SSR to simplify auth or screenshot tests. Public profiles/posts validate signed public payload/current visibility/expiry before rendering and use independent public cache policy. Summon helper must preserve Aether no-store/CSP headers and not inject unqualified inline scripts. Aether integration upgrade uses current pinned source/actual API `respondSummon`, not a guessed helper named respondSummonPage.

Acceptance SU-T11: concurrent A/B synthetic public marker rendering with controlled dispatcher switches/yields; abort A mid-render then B clean; callbacks only resolve in their own valid context; private marker absent in shell/head/hydration/HTML/OG/error; strict CSP shell boots; revoked/expired public source not rendered; route deep-link fallback; dependency matrix resolved without duplicate old Aether types. JVM SSR tests and real HTTP/browser integration both required.

### SU-12 — Restore actual browser verification and report evidence by level

Owners summon-core/build.gradle.kts browser test task, e2e-tests/package.json/playwright.config.ts and existing hydration/SSR/minified/keyboard tests. Existing JS browser task is disabled; Node happy-dom is useful but cannot qualify actual focus, CSP, navigation, File API, IndexedDB, worker, media or WebAuthn.

Provide explicit runnable browser test task/profile; missing browser binary yields not_run/dependency failure, never success through enabled=false. Keep fast Node tests and add real browser projects Chromium+Firefox for R1 web/extension; optional WebKit tests qualify later coverage without claiming all platforms. Pin actual fixture build, use local synthetic Aether/PG/storage where appropriate, verify endpoint before test; do not point tests at real owner accounts. Production bundle+CSP and source development bundle are separate runs. Limit workers/resources from current machine; avoid test config auto-consuming half cores in shared workspace.

Record exact commit, package/version locks, browser version, environment, command, exit code, assertions, high-water metrics, pass/fail/not_run and evidence paths. Existing Playwright traces/screenshots can capture private data; synthetic only, redacted opt-in artifacts for production diagnosis. Fixture tests new here proposed under e2e-tests/tests/private-suite/*.spec.ts; write them before invoking. Tests must fail on original bug, not just mirror new implementation.

Acceptance SU-T12: real browser run mounts production bundle and exercises SU-T01..11 appropriate portions; deliberate sanitizer/CSP/cleanup regressions caught; browsers matrix independent results; Node green not mislabeled browser green; required suite test reports never generated from a mock fixture. No actual suite release acceptance is completed by writing this PRD.

## 5. Suite workloads and ownership mapping

### SU-13 — Required real encrypted cross-client spike (R0 / WP-00F)

Implement in suite, with reusable regressions in Summon/Aether. Flow: real Summon JS component receives shared immutable model/action; client adapter encrypts a synthetic Mail object with fixed conformance vectors; qualified HTTP transport stores ciphertext through Aether+PG/blob adapter; packaged Compose client fetches and decrypts exact payload; Android/shared tests compare canonical/AAD/chunk bytes; revoke browser device; new access fails without deleting historical recovery keys. Real button/event/effect/render pipeline required, not raw static HTML screenshot. No real mailbox/domain/AWS needed.

Shared interfaces are suite-owned conceptual PlatformCrypto, SecureKeyStore, EncryptedLocalStore, SuiteTransport, SyncSignalTransport, BackgroundScheduler, AlarmScheduler, CredentialProviderAdapter, VpnPlatformAdapter, BrokerClient and later SocialContextResolver. Never import server runtime/driver/AWS credentials into common/browser code. R1 browser core cannot unlock vault by account login. Independently show auth session valid vs content device enrolled vs historical recovery/backup available.

Acceptance SU-T13: KMP-001..005 with real browser/Aether/native evidence, tampered/unknown/duplicate-key vectors reject, server only ciphertext, logout clears mounted private view, epoch revoke denies new fetch/write, cancellation/reconnect and persisted cursor recover. Crypto/S3 mocks do not satisfy actual roundtrip or AWS policy tests. Public shell has no secret hydration.

### SU-14 — R1 surfaces and extension requirements using shared state

The suite owns these screens/state machines; Summon packages above must make their behavior possible. Each view renders loading, locked, offline/cached, denial, expired, partial-import, pending-confirmation and failed dependency as distinct states. No denial presented as successful empty result. Native Compose and Summon dispatch same authorized actions and conflict semantics, not necessarily identical layout. R1 navigation enables Mail/Calendar/Aliases/Security only for authorized web capabilities; Drive/tasks/attention/social/organizations disappear until enabled.

| Surface | Deterministic suite behavior | Framework dependencies |
|---|---|---|
| Mail list/thread/reader | Keyed virtualized list; References/In-Reply-To thread identity; alias/sender distinctions; safe plain/document-tree HTML; CID attachments; remote images/read receipts off; local search labels cache coverage | SU-01–10 |
| Composer/drafts | From alias, To/Cc/Bcc, Reply/All/Forward; per-recipient protection; encrypted autosave within 2s pause; offline draft; explicit conflicting versions;1 0s Undo Send before dispatch; partial failures/unknown outcome visible;2 0MiB encoded outbound bound | SU-02/03/06/07/09/10 |
| Calendar | Day/week/month/agenda; encrypted event multiple calendars; timezone/all-day/recurrence/exceptions; revision conflict; invitation sequence warnings; local ICS original preservation; no historical RSVP effects | SU-01/02/03/07/10 |
| Aliases | Create/label/link/unlink, type/state/expiry/sender-policy controls, native Mail+Password shared aliasID; disabled addresses never recycled; imported provider reference not active route; recovery-linked disabling warns; domain diagnostic states | SU-03/06/10 |
| Security | Device request/explicit signed approval/scoped compartments/revoke; compare full fingerprint/QR request; recovery kit save+reimport proof; separate login reset/data decrypt/backup badges; visible out-of-suite bootstrap fallback | SU-02/03/06/10/11 |
| Migration/reconciliation | Local preview/record ledger/pause/resume/errors/capability report; preserve all originals/unknown fields/attachments/TOTP; no fabricated passkeys/routable provider alias; no password overwrite; encrypted manifest only | SU-06/07/09/10 |
| Notification/activity | Generic lock-screen text, opaque push IDs, quiet hours/mutes, encrypted audit; security action opens explicit target confirmation; receipt != external read receipt | SU-02/06/10 |
| Extension popup | Summon Kotlin/JS in actual Chromium/Firefox extension; native host BrokerClient with origin/RP/item/request binding and explicit approved action; no reusable broker admin token or unprompted secret autofill | SU-00/02/05/06/10/12 |

Native calendar alarms/reboot reconciliation, Autofill/CredentialProvider/TOTP assertions, clipboard policies and actual VPN tunnel/kill switch remain platform adapters. Browser tab cannot promise always-on alarms or tunnel traffic. Browser mail/calendar common semantics must still match encrypted native data. Feature availability diagnostics show actual platform support rather than a green button.

Acceptance SU-T14: actual R1 browser workflows across keyboard/RTL/strict CSP, two tabs/reload/offline/lock/account switch; shared action parity; real native broker message validation and extension origin matching; malicious page cannot request secret directly; exact origin/RP test, actual fill on supported browser/provider; no secret sent to remote AI; source-format tests retain unsupported records/partial warnings. Real SMTP external invites, Android alarms/autofill and VPN packet captures remain independent not_run until exercised. No cancel-old-subscription recommendation before R1-H.

### SU-15 — Drive, media, sharing and assistance (R2/R3)

Drive virtualized encrypted folders/files/version history/trash/offline/selective-sync conflict copies reuse SU-08/09; filename/media/thumbnails stay encrypted. Current permitted references and retention rights differ: deleting sender object cannot delete recipient Save my copy. Link viewer separate origin; decryption key in fragment is readable by page script and provider if sent in ordinary mail. Never claim link secrecy from its email transport. Downloads cap access expiry and explain previously retained copies; watermark/view-only button isn't DRM. Large files resume exact encrypted bytes, changed file new version/key. Unsupported codec offers local download, not plaintext server transcoding.

Attention runtime enrolled device with explicit compartments; locked/offline shows last processed/backlog, core apps continue. Evidence spans, supported/ambiguous/insufficient status and chunk coverage visible; no fabricated cousin answer/deadline. Local model download requires consent, no automatic cloud fallback. User-facing precision/recall are measured, not self-confidence. Model cannot authorize send/share/delete/reset/recovery. Summon renders user approval digest target/recipient/resources/expiry/connector, not prose-only approve all.

Connector disclosure modes local_only/summary/excerpt/full_content distinct; summarize still discloses personal data. Grants explicit object/collection/actions/byte limits/expiry; tools never arbitrary password/SQL/shell/fetch. Provider capabilities verified independently; no simulated unsolicited ChatGPT/Grok notification. Display exact disclosure audit and revoke effects, plus residual provider-retention limits. Worker/IndexedDB/framework scopes don't themselves authorize decrypt.

Acceptance SU-T15: client-made rendition encrypted; object URL cleanup on revocation/expiry; signed URL lifetime bound; permission changes while viewer mounted; offline retention caveat; unsupported provider/runtime clearly unavailable; prompt injection proposals cannot open confirmation-free effect; disclosure view no recovery/key/TOTP/session secrets; source span/coverage correct in long message; external recipient preview actually bound to approved operation. Suite owns inference/MCP/OAuth/security review.

### SU-16 — Trusted social, contextual reposts and future public surfaces

Suite owns signed graph/resolver/authorization; Summon renders its authenticated safe output. Public optional profile/handle separate from mailbox/login/recovery; Message creates request, accept doesn't reveal address. Follows/circles/list/community/private-history audiences independent from DM permissions. Feed default Following/Latest, no ads; expiry in ordinary feed, no separate Stories rail. Typed cards declarative, no arbitrary post/email JS; mentions/blocks/mutes enforced by suite. Public discovery opt-in, never private contact inference. Later org/inbox/newsletter/payment flows use explicit reviewed contracts; none delays R1.

Context menu: reply only, with context, add commentary. Nested reply default with_context; root-only remains one card. Every publish requires current preview confirmation. Ordered unique root/direct_parent/target; parent=root two cards; deeper chain shows authorized earlier-reply count or generic unavailable separator. Selected target emphasized; originals' reactions remain attached to originals; repost own reactions separate. Expand uses real ancestry, not siblings/cherry-picking; max128 edges failure offers reply-only, never pretends truncated node is root.

Render current per-viewer resolver result only. Unauthorized run becomes one generic gap with output slot; no hidden private IDs/counts/authors/URLs/media/reasons/positions in DOM attributes, React-like keys, hydration, accessibility labels, network/prefetch, logs or analytics. Private encrypted claims still require publish audience authorization for every private ancestor; placeholder isn't permission to send hidden IDs. Audience intersection, not union; read!=reshare; no new follower gets old history. Private root/public child cannot expose private graph. Encrypted payload verified against signed claims before display; public signed source certificate/projection verified, public_signed badge insufficient.

Preview token <=1 2 0s and source expiry, actor/audience/references/policy/revisions-bound. Preview changes between confirmation and publish -> CONTEXT_CHANGED and new confirmation, not automatic re-publish. Offline draft encrypted, no publish from cached preview. Edits mark Edited since repost; delete/expiry unavailable, no stored source text thumbnail snapshot. Effective reply expiry <= all ancestors; expired exchange removed but separately authorized commentary may remain with generic placeholder. Clear rendered/derived/media state within connected target 6 0s, while server denies at exact expiry. Browser offline copies can't be recalled.

Compose and Summon consume same filtered ordered node list. Each source opens only with fresh permission; no cached private stale body under unauthorized placeholder. External email/push/OG/screenshot generation default notice/link, never flattened private/temporary thread. Text commentary is reposter's text, not authenticated original quote. Bookmarks don't make permanent source snapshot. Public SSR tests separate from private client rendered tests.

Acceptance SU-T16: root target/parent=root/deep thread; omitted authorized gaps and multiple hidden ancestors; cycles/forged/cross-thread source; target denied; deleted root/parent/target; expiry clock advance while open; block after publish; audience change preview→publish; optional commentary; changed source; shared media grant; stale offline tab; several reposts; restore old backup; malicious payload claims mismatch. Assert same permitted cards/gaps/action result as Compose, zero hidden metadata in DOM/HTML/network/diagnostics, no source resurrection. All CTX acceptance rows remain required; reference-policy Python alone doesn't implement this UI.

## 6. Source-module coverage and ownership

This matrix prevents a local agent from interpreting a suite feature as a missing generic Summon product.

| Suite module | Summon work | Other implementation owner |
|---|---|---|
| 1 product/releases | Capability/feature-gated routes and ready states, SU-03/14 | Suite release sequencing |
| 2 architecture/trust | Safe renderer/CSP/shell/transport, SU-04–07/11 | Broker/crypto/SMTP boundaries |
| 3 keys/recovery | Scoped confirmations/locked teardown, SU-02/10/14 | Reviewed crypto/enrollment/recovery service |
| 4 objects/sync/policies | Byte-preserving adapters, errors, stable state, SU-06/07/13 | Suite domain + improved Aether transactions |
| 5 Mail/aliases | Virtualization/input/safe document tree, SU-04/08/10/14 | Mail transports, sender/routing/crypto policy |
| 6 Calendar/contacts | Accessible view/form primitives, SU-10/14 | Shared recurrence/ICS/native scheduling |
| 7 Passwords | Extension popup/broker UI seams, SU-14 | Secure broker/vault/OS autofill; R1 web vault off |
| 8 VPN | Honest unavailable/status view only | Native tunnel/node/network policy |
| 9 Migration | Bounded handles/progress/partial state, SU-09/14 | Local importer ledgers/format fidelity |
| 10 Drive/media | Transfer/object URL/virtualization, SU-08/09/15 | Chunk crypto/retention/storage services |
| 11 Assistance | State/worker/scoped action/disclosure UI, SU-15 | Enrolled runtime/policy/inference/MCP |
| 12 Social/context | Safe ordered renderer/expiry, SU-16 | Signed graph/resolver/Aether policy |
| 13 UX/accessibility | Focus/RTL/shared action semantics, SU-10/14 | Shared immutable presentation state |
| 14 Operations | Bundle/pin/test/diagnostic boundaries, SU-00/12 | Suite release/restore/security review |
| 15 Acceptance | All browser-affecting registry cases below | Native/provider/cloud tests retained separately |
| 16 Work protocol | Small package/dependency/evidence rules | Suite implementation-status/task system |
| 17 Decisions | Respect confirmed stack, author defaults/open inputs | Operator region/budget/domains; OPEN-CALENDAR-01 remains open |
| 18 Primary references | Freeze dependency version from actual source/official docs at implementation | No invented SDK versions |
| 19 Dogfood contract | All SU-00..14; real KMP-001..005 | Compose/Android/Aether spike |
| 20 AWS | No cookie on ciphertext URLs, no private SSR/cache, SU-05/06/11 | Separate AWS roles/topology/OpenTofu/SMTP/VPN |

## 7. Validation commands and completion rules

Audit performed: git status/branch/log/ref enumeration, `git fetch origin --prune`, `git archive origin/main`, Gradle/config/source/test reads and bounded symbol searches. Fetch/archive succeeded. No Gradle/runtime/browser/crypto/network/cloud test executed. PRD structural validation is not framework acceptance.

Use `./gradlew tasks --all` to confirm actual target tasks before execution. Scoped source candidates:

```bash
./gradlew --no-daemon --max-workers=1 :summon-core:jvmTest
./gradlew --no-daemon --max-workers=1 :summon-core:jsNodeTest
./gradlew --no-daemon --max-workers=1 :summon-aether:jvmTest
```

The current disabled JS browser task cannot establish browser evidence. SU-12 must provide a runnable browser profile and actual fixture server. After implemented and dependencies pinned, run selected new `e2e-tests/tests/private-suite/*.spec.ts` cases via the repository's Playwright runner against synthetic fixture origin; verify exact command from package.json instead of guessing build tasks. Do not report an absent test/task passed.

Derive aggregate memory/process cap from current host availability before builds; constrain Kotlin/test child JVMs and browser processes as well as Gradle. Override existing maxParallelForks half-core policy for the bounded task. Serial workers alone do not enforce total memory. Do not clear caches broadly, modify global Gradle installation, terminate user models or launch paid infrastructure. Missing device/browser/provider remains not_run with exact dependency.

Package done: implemented reusable behavior, focused regression fails before fix and passes after, documentation/migration examples, actual target evidence and suite original-workload regression. Security review remains independent. Source compatibility and browser capability differences explicit. R1-H cannot accept an unknown required framework capability, mock-only browser workflow or deliberately disabled test.

## 7A. Proposed bridge API contracts and regression placement

The following signatures define the collector ownership contract without requiring a local model to guess the API. These symbols are now introduced in `OwnedFlowBinding.kt`; they do not by themselves complete the broader effect, lifecycle, legacy-migration and root-isolation requirements in SU-02. Use the existing `codes.yousef.summon.state.State`/`SummonMutableState` and coroutine types; place in `summon-core/src/commonMain/kotlin/codes/yousef/summon/state/OwnedFlowBinding.kt`.

```kotlin
interface FlowBinding<T> {
    val state: State<T>
    fun dispose()
}
interface MutableFlowBinding<T> : FlowBinding<T> {
    override val state: SummonMutableState<T>
}
fun <T> bindStateFlow(
    flow: StateFlow<T>, scope: CoroutineScope
): FlowBinding<T>
fun <T> bindMutableStateFlow(
    flow: MutableStateFlow<T>, scope: CoroutineScope
): MutableFlowBinding<T>
fun <T> bindSharedFlow(
    flow: SharedFlow<T>, initialValue: T, scope: CoroutineScope
): FlowBinding<T>
```

Returned bindings own child collector jobs and every registered listener, not the caller's parent scope. `dispose` is idempotent and removes listeners synchronously, cancels owned jobs and prevents any subsequent state update; it never cancels unrelated parent work. Initial StateFlow value is available synchronously. bindSharedFlow uses exactly the supplied initial value until first event. Read-only binding has no reverse writeback. Mutable two-way binding suppresses equal feedback and removes its listener on disposal. A canceled supplied scope cannot start a hidden replacement scope. JS DOM updates execute on the qualified renderer dispatch path; no direct background DOM mutation.

Composable wrappers may call these APIs and register DisposableEffect cleanup keyed by flow/scope identity. Provide migration examples for every old stateFrom*/toSharedFlow convenience that creates unowned jobs, and preserve explicitly documented legacy behavior only where safe. One component root owns one scope; destruction cancels children. Getter properties must not create repeated new subscriptions unnoticed. Implement/test precise event ordering before routing private presentation models through the bridge.

New shared tests: `summon-core/src/commonTest/kotlin/codes/yousef/summon/state/OwnedFlowBindingTest.kt` and lifecycle `OwnedLifecycleScopeTest.kt`. New JS tests beside existing runtime tests: `ControlledInputLifecycleTest.kt`, `SafeContentRenderingTest.kt`, `CspEventBindingTest.kt`. Proposed real browser files under `e2e-tests/tests/private-suite/`: `lifecycle.spec.ts`, `routing.spec.ts`, `csp.spec.ts`, `content-security.spec.ts`, `virtual-mailbox.spec.ts`, `focus-rtl.spec.ts`, `files-storage.spec.ts`, `encrypted-roundtrip.spec.ts`, `contextual-repost.spec.ts`. Enable fixture setup and browser binaries explicitly before these are runnable. Existing `jsNodeTest` does not run Playwright or qualify IndexedDB/focus/security behavior.

For binary files/storage/worker APIs, start with one suite-owned platform interface and an actual browser implementation, then extract a reusable Summon primitive only if multiple consumers need it. Freeze its typed messages/cancellation/limits in that slice before adding public API; do not invent encryption/enrollment/key custody inside Summon. Sanitizer dependency version/policy is frozen by reviewed parser tests; an unavailable safe parser renders plain text and marks rich preview unsupported, not raw unsafe HTML.

## 8. Source specification appendices

Copied defaults and complete original acceptance registry follow for self-contained retrieval. They include suite/native/provider work outside this repository; assign using sections5–6 and the companion Aether PRD. All cases start specified_not_executed. A local model should read only the package and relevant rows, not load the entire document for each patch. Original source numbering retained.

# Appendix B. Machine-readable default configuration

```yaml
specification_revision: '2.0'
release: R1
registration:
  mode: invite_only
  owner_bootstrap_one_time: true
features:
  mail: true
  calendar: true
  passwords: true
  vpn: true
  drive: false
  tasks: false
  connectors: false
  attention: false
  social: false
  communities: false
  public_discovery: false
  organizations: false
  monetization: false
privacy:
  external_ai_disclosure_default: local_only
  push_content: opaque_ids_only
  lock_screen_previews: false
  remote_mail_images: false
  read_receipts: false
  presence: false
  typing_indicators: false
  plaintext_source_uploads: false
  cross_account_deduplication: false
  public_contact_graph: false
  provider_only_recovery_escrow: false
  vpn_destination_logging: false
  telemetry_default: disabled
crypto:
  object_cipher: XCHACHA20_POLY1305_V1
  key_bytes: 32
  nonce_bytes: 24
  signature: ED25519
  key_wrapping: LIBSODIUM_SEALED_BOX
  canonical_json: RFC8785_JCS
  passphrase_kdf:
    algorithm: ARGON2ID
    memory_bytes: 67108864
    operations_limit: 3
    salt_bytes: 16
    output_bytes: 32
  recovery_key_bytes: 32
  recovery_auth_kdf_context: REC_AUTH
  recovery_auth_kdf_subkey_id: 1
api:
  json_body_max_bytes: 1048576
  list_page_default: 50
  list_page_max: 200
  sync_page_default: 200
  access_token_ttl_seconds: 600
  refresh_idle_ttl_seconds: 2592000
  revocation_cache_max_seconds: 15
security:
  challenge_ttl_seconds: 600
  vault_inactivity_lock_seconds: 300
  vault_background_lock_seconds: 30
  clipboard_clear_seconds: 30
  contact_recovery_wait_seconds: 172800
  require_offline_kit_for_cutover: true
  require_independent_backup_for_cutover: true
storage:
  account_quota_bytes: 107374182400
  reserved_inbound_mail_bytes: 2147483648
  max_file_plaintext_bytes: 10737418240
  crypto_chunk_bytes: 4194304
  storage_upload_part_target_bytes: 16777216
  incomplete_upload_idle_seconds: 86400
  orphan_upload_cleanup_grace_seconds: 86400
  signed_download_url_max_seconds: 120
  trash_retention_days: 30
  sync_tombstone_retention_days: 90
  file_versions_min_count: 20
  file_version_age_days: 30
mail:
  outbound_encoded_mime_max_bytes: 20971520
  inbound_encoded_mime_max_bytes: 52428800
  undo_send_seconds: 10
  deferred_queue_max_seconds: 259200
  completed_plaintext_queue_retention_seconds: 0
  new_account_outbound_recipients_per_day: 200
  new_account_outbound_recipients_per_minute: 30
aliases:
  random_bits: 128
  max_per_account: 1000
  default_sender_policy: open_with_filtering
  default_catch_all: false
  temporary_alias_ttl_seconds: 86400
  recycle_addresses: false
calendar:
  timed_event_default_alarm_minutes: 10
  max_alarms_per_event: 5
  recurrence_cache_past_days: 90
  recurrence_cache_future_days: 365
  max_expanded_occurrences_per_query: 10000
  booking_slot_minutes: 30
  booking_buffer_minutes: 10
  booking_horizon_days: 30
  booking_min_notice_hours: 24
  booking_hold_seconds: 300
passwords:
  generated_password_length: 24
  generated_symbols_default: false
  generated_passphrase_words: 6
  autofill_default_origin_policy: exact_origin
  autofill_auto_submit: false
  history_min_versions: 20
vpn:
  peer_lease_seconds: 300
  node_reconcile_seconds: 60
  revocation_healthy_target_seconds: 60
  unsupported_ipv6_policy: block
  lan_access_default: false
  captive_portal_bypass_max_seconds: 300
  mail_exit_ip_must_differ: true
jobs:
  lease_seconds: 60
  heartbeat_seconds: 20
  retry_delays_seconds:
  - 5
  - 30
  - 120
  - 600
  - 3600
  retry_budget_seconds: 86400
migrations:
  local_only_source_parsing: true
  source_auto_delete: false
  source_mutation: false
  gmail_fetch_concurrency: 4
  historical_invitation_side_effects: false
connectors:
  grant_default_ttl_days: 30
  excerpt_max_bytes: 2048
  default_max_source_objects_per_request: 5
  default_max_disclosure_bytes_per_request: 16384
  default_max_disclosure_bytes_per_day: 1048576
  webhook_replay_window_seconds: 300
  secret_read_tools: false
attention:
  enabled: false
  default_model_id: Qwen/Qwen3-4B-Instruct-2507
  default_runtime: local_llama_cpp
  quantization_candidate: Q4_K_M
  context_tokens: 8192
  max_output_tokens: 1024
  temperature: 0
  cpu_concurrency: 1
  automatic_cloud_fallback: false
  require_model_download_consent: true
social:
  default_feed: following_latest
  default_post_retention: keep
  default_dm_request_chars: 2000
  new_requests_per_day: 10
  temporary_durations_seconds:
  - 3600
  - 21600
  - 86400
  - 259200
  - 604800
  temporary_custom_max_seconds: 2592000
  connected_expiry_ui_target_seconds: 60
  exclusive_blob_cleanup_target_seconds: 86400
  contextual_repost:
    enabled_with_social: true
    default_for_nested_reply: with_context
    max_ancestor_depth: 128
    max_visible_unique_posts: 3
    require_publish_preview: true
    allow_text_snapshots: false
    default_reshare_public: allowed
    default_reshare_private: disabled
    max_resolver_response_bytes: 1048576
    viewer_revalidation_required: true
    external_notice_mode: link_only
operations:
  logs_retention_days: 7
  security_metadata_retention_days: 30
  encrypted_user_audit_retention_days: 90
  backup_rpo_target_hours: 24
  restore_target_hours: 4
  daily_backup_count: 7
  weekly_backup_count: 4
  periodic_restore_test_days: 30
architecture:
  project: kotlin_multiplatform
  desktop_ui: compose_desktop
  android_ui: compose_native
  web_ui: summon
  backend: aether_jvm
  cloud: aws
  web_primary_target: kotlin_js
  web_wasm_enabled: false
  broker: isolated_kotlin_jvm
  framework_substitution_allowed: false
  java_toolchain_candidate: 21
  r1_web_surfaces:
  - mail
  - calendar
  - aliases
  - security
  web_password_vault_enabled: false
aws:
  deployment_profile: personal_ec2_v1
  region: null
  account_id: null
  data_residency_approved: false
  cost_budget_usd_month: null
  cost_approval_required: true
  control_instance_type_candidate: t3.large
  mail_instance_type_candidate: t3.small
  vpn_instance_type_candidate: t3.small
  purchase_model: on_demand
  cpu_credit_mode: standard
  public_ssh: false
  smtp_egress_mode: direct_mx
  ses_relay_enabled: false
  cloud_ai_enabled: false
  automatic_cross_region_replication: false
  s3_private_bucket_public_access_block: true
  s3_presigned_ttl_seconds: 120
  vpn_port: 51820
  framework_server_port: 8080
  database_port: 5432
  private_lmtp_port: 8024
  client_side_encryption_required: true
  ephemeral_bucket_versioning: false
```


## 15.1 Required case registry

| Case | Release | Given / when | Required result | Module |
|---|---|---|---|---|
| KEY-001 | R0 | Change account password on a trusted device | Oldest and newest Mail, Calendar, and Vault sentinels decrypt; no content-key replacement | 03 |
| KEY-002 | R0 | Authenticate on a new device without enrollment | Ciphertext may sync; plaintext and keys remain unavailable | 03 |
| KEY-003 | R0 | Use saved recovery kit on a clean device | Catalog authenticates and all required historical sentinels decrypt before commit | 03 |
| KEY-004 | R0 | Crash after every recovery-state transition | Retry is idempotent; original recoverable state survives pre-commit failure | 03 |
| KEY-005 | R0 | Lose every recovery route | Existing ciphertext remains intact; no empty catalog overwrites it | 03 |
| KEY-006 | R0 | Replace a device certificate or key envelope with attacker bytes | Signature/context verification rejects enrollment and reports integrity failure | 03 |
| KEY-007 | R0 | Tamper ciphertext/header/compartment/version/nonce | Authenticated parsing fails before any private content is rendered | 03 |
| KEY-008 | R0 | Retry encryption upload then edit content | Retry uses identical ciphertext; edit creates a new key and nonce | 03 |
| KEY-009 | R0 | Force low-memory KDF failure | No silent insecure KDF downgrade or plaintext storage fallback | 03 |
| KEY-010 | R0 | Use Mail client grant to access Vault data | Broker and API reject; no vault result or secret enters general process | 02 |
| KEY-011 | R1 | Revoke enrolled phone then attempt refresh/sync | Future auth/grants denied within documented cache window; offline-copy limitation displayed | 03 |
| KEY-012 | R1 | Try recovery replay against another origin/account/challenge | Proof is rejected because context and challenge binding differ | 03 |
| KEY-013 | R1 | Recover without original deployment reachable | Independent backup opens in standalone tool and exports verified source data | 03 |
| KEY-014 | R2 | Request recovery with only one recovery-contact participant | Insufficient shares/authority; no catalog access or premature release | 03 |
| SYNC-001 | R0 | Repeat same client operation and digest | One user-visible mutation and same committed result | 04 |
| SYNC-002 | R0 | Reuse operation ID with different digest | IDEMPOTENCY_CONFLICT; no overwrite | 04 |
| SYNC-003 | R0 | Concurrent edits to the same vault item | Both revisions retained with explicit conflict; no silent password loss | 04 |
| SYNC-004 | R0 | Reconnect after sync tombstone retention | Snapshot resync; deleted or expired objects are not resurrected | 04 |
| SYNC-005 | R0 | Commit object while a referenced upload is incomplete | UPLOAD_INCOMPLETE; object is not advertised as fully available | 04 |
| SYNC-006 | R0 | Read another account object with a guessed UUID | PERMISSION_DENIED or non-enumerating not-found behavior; no data | 04 |
| SYNC-007 | R0 | Crash after DB commit before job acknowledgment | Replay produces no duplicate mail/action effect | 04 |
| MAIL-001 | R1 | Receive ordinary SMTP mail and inspect stores | Private archive ciphertext persists; documented transient edge exposure only; no body logs | 05 |
| MAIL-002 | R1 | Make object storage fail during inbound LMTP | Temporary delivery failure, not successful acceptance followed by loss | 05 |
| MAIL-003 | R1 | Send native encrypted mail and external plain-protocol mail | Recipient-specific protection badges and packages are correct | 05 |
| MAIL-004 | R1 | Use Bcc and mixed encrypted/external recipients | Other recipients cannot read Bcc identities from visible recipient manifests | 05 |
| MAIL-005 | R1 | Render hostile HTML with scripts/forms/remote images | No code runs, no unsolicited fetch, no secure-broker IPC access | 05 |
| MAIL-006 | R1 | Import unusual MIME/Unicode/large attachment message | Original bytes and attachments preserved; unsupported parsing shown | 05 |
| MAIL-007 | R1 | Receive identical subjects from different conversations | Unrelated messages are not merged solely by subject | 05 |
| MAIL-008 | R1 | Cancel during undo-send then after SMTP acceptance | First cancels dispatch; second cannot falsely claim recall | 05 |
| MAIL-009 | R1 | SMTP acknowledgment lost after remote acceptance | Status may be unknown; system does not claim exact inbox delivery | 05 |
| MAIL-010 | R1 | Reply to an alias invitation and ordinary alias thread | From/Reply-To/organizer use intended alias; private account identity absent | 05 |
| MAIL-011 | R1 | Open key-changed OpenPGP contact | Unexpected change warns and requires verification/approval | 05 |
| MAIL-012 | R1 | Inspect SPF/DKIM/DMARC/PTR and send to real test providers | Recorded raw evidence and actual delivery results; mocked SMTP is insufficient | 05 |
| ALIAS-001 | R1 | Create repeated aliases and force collision | Cryptographic random generation; uniqueness retry without reassigning an old alias | 05 |
| ALIAS-002 | R1 | Disable one service alias | That route stops according to state; other aliases and history still work | 05 |
| ALIAS-003 | R1 | Spoof an allowed From display name/address | Sender policy checks authentication evidence and forwarding limitations | 05 |
| ALIAS-004 | R1 | Import provider-owned Proton alias | Historical/external state, not active on destination; forwarding dependency shown | 09 |
| ALIAS-005 | R1 | Inspect outbound raw headers across reply/all/group cases | No hidden-account identifier leakage; consistent selected group identity | 05 |
| ALIAS-006 | R1 | Rotate credential-linked email alias | Website update remains pending until verified; no false automatic migration | 07 |
| ALIAS-007 | R4 | Accept a public-profile DM request | Relationship opens without exposing SMTP aliases | 12 |
| CAL-001 | R1 | Expand 09:00 recurring event across DST | Local intended time stays correct; UTC offsets change appropriately | 06 |
| CAL-002 | R1 | View all-day event after device-zone change | Calendar dates and exclusive end remain correct | 06 |
| CAL-003 | R1 | Edit one occurrence and this-and-following | Correct overrides/split series; past occurrences preserved | 06 |
| CAL-004 | R1 | Receive old/replayed/forged scheduling update | Sequence/identity checks prevent unauthorized overwrite | 06 |
| CAL-005 | R1 | Import historical REQUEST/REPLY/CANCEL objects | Zero outbound scheduling messages | 06 |
| CAL-006 | R1 | Import unsupported recurrence property | Original retained and unsupported coverage disclosed, not silently flattened | 06 |
| CAL-007 | R1 | Schedule alarm across reboot/Doze/time-zone change | Actual device behavior recorded; diagnostics identify missing permissions | 06 |
| CAL-008 | R1 | Accept external invitation through alias | Compatible response reaches sender and correct event state persists | 06 |
| CAL-009 | R2 | Book same slot concurrently | At most one confirmed booking after hold and freshness checks | 06 |
| CAL-010 | R2 | Enable Android native calendar bridge | Explicit selected-calendar disclosure warning; no accidental Google sync | 06 |
| PASS-001 | R1 | Autofill deceptive host and cross-origin iframe | Unauthorized origins receive no secret | 07 |
| PASS-002 | R1 | Generate password then crash before signup save | Encrypted draft still contains generated credential; no plaintext log | 07 |
| PASS-003 | R1 | Lock OS while vault is open | Vault locks and in-flight access follows bounded transaction rules | 07 |
| PASS-004 | R1 | Copy password then replace clipboard with unrelated text | Timeout does not erase unrelated clipboard contents | 07 |
| PASS-005 | R1 | Run official TOTP vectors including SHA variants | Exact expected codes and timing boundaries | 07 |
| PASS-006 | R1 | Use actual Android Autofill/CredentialProvider surface | Target origin/app association verified; successful real fill/assertion | 07 |
| PASS-007 | R1 | Attempt fill from fake extension/native-message caller | Broker rejects unauthorized identity/session | 07 |
| PASS-008 | R1 | Import passkey record without usable key material | Marked needs_user_action/unsupported, never migrated-and-working | 09 |
| PASS-009 | R1 | Search passport while vault locked | No vault title/secret leakage beyond explicitly allowed locked UI | 13 |
| PASS-010 | R3 | Agent requests all vault passwords or recovery material | No exposed tool; policy denies and records attempt | 11 |
| VPN-001 | R1 | Inspect packets before/after tunnel startup | No unauthorized traffic outside configured protection state | 08 |
| VPN-002 | R1 | Kill app and tunnel process mid-transfer | Kill switch remains blocking; status is not Protected | 08 |
| VPN-003 | R1 | Switch Wi-Fi/mobile and IPv4/IPv6 networks | Reconnect without DNS/IPv6 escape; unsupported routes block | 08 |
| VPN-004 | R1 | Revoke active device with healthy control path | Node acknowledgment and peer removal within selected 60-second target | 08 |
| VPN-005 | R1 | Disconnect VPN node from control plane | Peer removed at lease expiry within five minutes | 08 |
| VPN-006 | R1 | Start alongside another Android VPN | OS conflict and explicit replacement consent shown | 08 |
| VPN-007 | R1 | Choose split routing plus incompatible lockdown | Unsafe/unsupported combination refused or clearly mediated | 08 |
| VPN-008 | R1 | Inspect operational data across all services | No browsing/DNS/destination history enters account AI/social/logging pipeline | 08 |
| MIG-001 | R1 | Import Proton export with EML/JSON and labels | Version-tested mapping and exact raw message retention | 09 |
| MIG-002 | R1 | Pause/resume importer and repeat same snapshot | No unexplained duplicates/loss; reconciliation equation balances | 09 |
| MIG-003 | R1 | Import Gmail MBOX with multi-label and escaped From lines | One source message with correct labels; intact body bytes | 09 |
| MIG-004 | R1 | Import same Message-ID with different bodies | Both messages retained as separate records | 09 |
| MIG-005 | R1 | Invalidate Gmail history cursor | Documented rescan path with source-ID deduplication | 09 |
| MIG-006 | R1 | Revoke OAuth midway through import | waiting_for_auth and resumable checkpoint; no false completion | 09 |
| MIG-007 | R1 | Import encrypted Proton Pass rich export | Local decryption only; all supported fields mapped and omissions listed | 09 |
| MIG-008 | R1 | Import Bitwarden portable export with attachment archive | Attachments/types counted, protected correctly, and available | 09 |
| MIG-009 | R1 | Select account-restricted encrypted Bitwarden export | Actionable re-export error instead of fake decryption | 09 |
| MIG-010 | R1 | Import 1PUX versus limited CSV | Capability/completeness differences visible; no desktop passkey assumption | 09 |
| MIG-011 | R1 | Import KDBX plus key file and unsupported database variant | Supported version decrypts locally; unsupported version preserved/reported | 09 |
| MIG-012 | R1 | Import CSV containing custom fields/newline/quotes/Unicode | Preview mapping correct; all unmapped fields preserved or explicitly excluded | 09 |
| MIG-013 | R1 | Feed zip traversal/bomb and malicious MIME | Resource limits and path confinement hold without total-job data loss | 09 |
| MIG-014 | R1 | Complete import with failed or unsupported critical item | Replacement gate remains incomplete and item has remediation | 09 |
| MIG-015 | R1 | Rollback custom-domain migration | DNS snapshot and destination-only data export work without forwarding loop | 09 |
| FILE-001 | R0 | Alter/reorder/truncate encrypted file chunks | Manifest/AEAD validation rejects before returning incorrect plaintext | 10 |
| FILE-002 | R0 | Resume upload after local source changed | New file version required; no mixed ciphertext object | 10 |
| FILE-003 | R0 | Run multipart upload against actual selected S3 adapter | Part limits satisfied and byte-exact restored file | 10 |
| FILE-004 | R2 | Save received attachment then sender deletes theirs | Independent recipient retention survives with correct quota accounting | 10 |
| FILE-005 | R2 | Seek into large encrypted video | Only authenticated necessary chunks released; no whole-file-tag shortcut | 10 |
| FILE-006 | R2 | Send fragment decryption link in normal email | UI explains email-provider/link-holder access; no misleading E2EE claim | 10 |
| FILE-007 | R2 | Race final reference creation with garbage collection | Retained referenced blob is not deleted | 10 |
| FILE-008 | R2 | View-only/watermark/download-button policies | UI does not promise copying or screenshots are impossible | 10 |
| AI-001 | R3 | Receive Maria dessert question with no email-category rules | Evidence-linked personal attention item created without invented preference | 11 |
| AI-002 | R3 | Receive renewal mail with ambiguous date and phishing URL | Uncertainty/authentication context shown; no automatic purchase or URL follow | 11 |
| AI-003 | R3 | Take every trusted runtime offline | Work queued; last processed time shown; no server-side secret decryption fallback | 11 |
| AI-004 | R3 | Email orders agent to exfiltrate vault or change permissions | No authority gained, forbidden disclosure/action prevented | 11 |
| AI-005 | R3 | External agent queries objects outside allowed collection | Denied before search snippets or content leave runtime | 11 |
| AI-006 | R3 | Approve share then alter recipient/content | Operation digest mismatch invalidates approval | 11 |
| AI-007 | R3 | Provider has MCP but no proactive trigger capability | Suite notification works; provider capability accurately marked unavailable | 11 |
| AI-008 | R3 | Long message exceeds context size | Coverage markers and chunked evidence; no claim unseen content was analyzed | 11 |
| AI-009 | R3 | Revoke connector after previous disclosure | Future calls denied; prior data-copy limitation displayed | 11 |
| SOC-001 | R4 | Inspect public profile and discovery APIs | No private email/alias/login/recovery/contact graph leakage | 12 |
| SOC-002 | R4 | Add new follower to existing private-post audience | No automatic historical decryption grant | 12 |
| SOC-003 | R4 | Expire post while cleanup worker is paused | Authorization denies new access by timestamp; no dependency on cleanup tick | 12 |
| SOC-004 | R4 | Expire post with comments/reactions/bookmarks/quotes | Dependent visibility ends and references show placeholder | 12 |
| SOC-005 | R4 | Send temporary post to external email provider | Only notice/link sent, not full supposedly disappearing content | 12 |
| SOC-006 | R4 | Replay offline edit after expiry | No resurrection; republishing requires new object | 12 |
| SOC-007 | R4 | Block/mute/restrict then mention/request from actor | Chosen permission semantics hold; no mention-based bypass | 12 |
| SOC-008 | R4 | Private-content abuse report | Only explicitly selected evidence is disclosed to moderators | 12 |
| OPS-001 | R1 | Restore backup with original infrastructure unavailable | Independent keys/data/tooling work; no network sending during restore | 14 |
| OPS-002 | R1 | Scan logs/crash artifacts with synthetic secret canaries | No forbidden plaintext beyond explicitly scoped edge processing | 14 |
| OPS-003 | R1 | Install unsigned/downgraded/incompatible update | Rejected safely; encrypted data remains recoverable | 14 |
| OPS-004 | R1 | Run schema migration interruption/rollback rehearsal | No silent vault downgrade/loss; version and backup checks enforced | 14 |
| OPS-005 | R1 | Run keyboard/screenreader/RTL/large-font core journeys | Actionable readable UI without inaccessible destructive controls | 13 |
| OPS-006 | R1 | Attempt subscription replacement before critical tests/review | Dashboard stays incomplete with exact outstanding evidence | 14 |
| OPS-007 | R5 | Enable public registration without moderation readiness | Feature gate refuses deployment promotion | 14 |


### Revision-2 implementation cases

| Case | Release | Given / when | Required result | Module |
|---|---|---|---|---|
| KMP-001 | R0 | Resolve Summon and Aether at recorded source baselines | Real source/API compatibility report; no invented artifact version or verified flag | 19 |
| KMP-002 | R0 | Build shared models on JVM, Android and Kotlin/JS | Identical schema/serialization fixtures; no server-only dependency in browser | 19 |
| KMP-003 | R0 | Create encrypted object in Summon and read in Compose/Android | Ciphertext-only Aether persistence; exact authenticated payload round-trip | 19 |
| KMP-004 | R0 | A required framework API is absent or broken | Minimal failing test and framework gap record; no silent replacement framework | 19 |
| KMP-005 | R0 | Use Aether generic session to attempt device-admin action | Denied without actual identity authority and signed suite grant | 19 |
| KMP-006 | R1 | Install packaged Compose app on reference Linux host | Mail/Calendar/broker/native host work with bundled JVM and no UI webview requirement | 19 |
| KMP-007 | R1 | Render private web route and inspect HTML/SSR/hydration/cache | No decrypted private source or user keys in server HTML or persistent plaintext state | 19 |
| KMP-008 | R1 | Expire browser grant and attempt websocket/API replay | New access denied; opaque hints trigger reauthorization, never bypass it | 19 |
| KMP-009 | R1 | Navigate away, suspend tab and reconnect during sync/upload | Lifecycle cancellation correct; durable jobs resume without duplicate mutation | 19 |
| KMP-010 | R1 | Inspect browser service worker and logout across tabs | No plaintext private cache; wrapped-key state invalidated under policy | 19 |
| KMP-011 | R1 | Attempt unauthorized desktop broker native message | Peer/session/caller validation rejects without revealing vault data | 19 |
| KMP-012 | R1 | Run recurrence and crypto corpora on every supported target | Same bytes/occurrences/errors; library differences never silently change format | 19 |
| KMP-013 | R1 | Use web UI to enable VPN without native adapter | Reports unsupported tunnel capability; never displays Protected | 19 |
| KMP-014 | R1 | Inspect Summon/Aether dogfood evidence at cutover | All required capabilities have actual build/test evidence and reusable gap fixes | 19 |
| AWS-001 | R0 | Run infra-plan without account/region/budget approval | Explicit input failure; no real AWS resource creation | 20 |
| AWS-002 | R1 | Attempt VPN-role access to private S3 or mail database | IAM/network deny with actual test evidence | 20 |
| AWS-003 | R1 | Inspect ordinary mail queue snapshots and logs | No plaintext body backups or logging; durable archive is client-encrypted | 20 |
| AWS-004 | R1-H | Attempt direct SMTP without outbound-port approval | Clear blocked-external state; no silent SES substitution or successful-cutover claim | 20 |
| AWS-005 | R1-H | Inspect public mail/VPN endpoints and PTR/sender tests | Distinct IPs and actual provider delivery evidence | 20 |
| AWS-006 | R1 | Revoke object access after issuing an S3 URL | New issuance denied; UI documents bounded old URL/in-flight/copy limits | 20 |
| AWS-007 | R4 | Expire content while S3 deletion is delayed | API denies at deadline; no dependence on lifecycle job timing | 20 |
| AWS-008 | R1-H | Lose original cloud account and use independent backup | Offline recovery opens historical records without original AWS/API authority | 20 |
| AWS-009 | R1 | Inspect AWS logs/metrics/AI context after browsing through VPN | No destination or DNS history disclosure; only permitted operational aggregates | 20 |
| AWS-010 | R1 | Deploy update with unsafe schema rollback | Refuse corrupting downgrade; preserve verified backup and repair path | 20 |
| CTX-001 | R4 | Repost grandchild funny reply with context | Root, direct parent and target in order; target emphasized | 12 |
| CTX-002 | R4 | Repost direct reply or root itself | Two cards or one card respectively; no duplicate root | 12 |
| CTX-003 | R4 | Repost deep descendant | Root/parent/target collapsed view with explicit authorized intermediate gap | 12 |
| CTX-004 | R4 | Forge parent/root edge or create ancestor cycle | INVALID_CONTEXT_CHAIN; no guessed ancestry or infinite loop | 12 |
| CTX-005 | R4 | Exceed 128 ancestor edges | CONTEXT_DEPTH_LIMIT and explicit reply-only alternative | 12 |
| CTX-006 | R4 | Attempt public contextual repost of a private root | No audience broadening, new key grants or source-reference leakage; narrow or block, never bypass with placeholders | 12 |
| CTX-007 | R4 | Change audience/source version after preview | CONTEXT_CHANGED; re-preview required before publish | 12 |
| CTX-008 | R4 | Block viewer or remove source permission after publication | Per-viewer resolution with safe placeholder, no private IDs/names/counts | 12 |
| CTX-009 | R4 | Expire root while descendant target has later own expiry | Effective ancestor expiry hides target and embedded exchange | 12 |
| CTX-010 | R4 | Delete parent while target is otherwise still authorized | Unavailable-parent placeholder; no stored text snapshot | 12 |
| CTX-011 | R4 | Edit original after contextual repost | Current permitted version shown with Edited since repost label | 12 |
| CTX-012 | R4 | Add commentary then delete the original target | Independent commentary may remain; source exchange is unavailable | 12 |
| CTX-013 | R4 | Publish identical repost operation twice | One repost; idempotency result stable | 12 |
| CTX-014 | R4 | Reconnect offline stale preview after source expiry | Fresh validation rejects expired reference; no resurrection | 12 |
| CTX-015 | R4 | Send temporary contextual repost by external email | Minimal notice/link only, no flattened source or private OG preview | 12 |
| CTX-016 | R4 | Try using ordinary private mail as contextual ancestor | Rejected as non-social; requires separate sensitive disclosure workflow | 12 |
| CTX-017 | R4 | Compare Compose and Summon rendering for same viewer | Same ordered visible references, target highlight and honest gaps | 12 |
| CTX-018 | R4 | Tamper private context claims independently of encrypted payload | Signed header claims digest and decrypted payload consistency reject | 12 |
| CTX-019 | R4 | Restore pre-expiry database/objects from backup | Expiry/tombstones replay before any feed/media/key access | 12 |
| CTX-020 | R4 | Request context using a forged viewer identity | Server derives viewer from authenticated session and denies excess access | 12 |


## Authority, boundaries, and execution rules

This is an implementation PRD, not a passing test report or security audit. The user explicitly requested these root-level PRDs and waived a Linear issue for this audit. The initial audit performed no implementation, deployment, publication or upstream push and did not change checked-out branches. The subsequent user authorization expands Summon work to implementation and verification of this combined PRD and v0.8.0; it does not authorize Aether source changes or deployment/publication. Existing unrelated files must remain untouched.

The suite revision 2 mandates one Kotlin Multiplatform multi-module application, Compose Desktop/Android native UI, Summon browser UI, Aether Kotlin/JVM backend, and AWS. R0 is foundation; R1 must include Mail, Calendar, Passwords, VPN, aliases and migrations together. R1-H requires actual recovery/restore, provider, device, network and independent security evidence. R2 adds Drive/tasks/sharing; R3 assistance/connectors; R4 trusted social including contextual reposts; R5 public/organization functions. Later features remain disabled until their contracts and tests pass. Framework improvements belong here; product-specific encryption envelopes, ACL policy, calendar recurrence, SMTP, WireGuard, imports and UI screens belong in the suite. Do not replace the selected frameworks, build a new SMTP/VPN engine, or move content keys into the ordinary server.

Terms: **confirmed gap** means current source explicitly lacks the required behavior or contains a contradicting implementation; **qualification** means a capability exists but no actual workload test was run; **suite-owned** means the requirement does not justify adding that product to this framework. Tests below are specified, not executed. A test name introduced here is a proposed new file/class, not a claim it exists.

New API names in unimplemented work packages are **proposed contracts**. The owned binding symbols in section 7A have been introduced; inspect their source and actual target evidence before using them. A proposed signature elsewhere is not proof that it is a callable or qualified API. Keep source compatibility using new capability interfaces/overloads where possible; deprecate unsafe convenience APIs with a migration guide rather than silently changing unrelated applications. Target the active implementation and its callers, not a similarly named dormant wrapper. No generic test, README claim, mock, parser success, or compilation-only result establishes production readiness.

Work one package at a time. Read its section, owning functions and nearest regression only; reproduce the first failing acceptance case; implement one coherent slice; run its checks; inspect the actual failure before retrying. Store generated fixtures/output in ignored directories. Report command, exit status, runtime/target, source commit and passed/failed/not-run cases. Never turn a missing external dependency into simulated success. Keep session reasoning/handoffs out of tracked files; this user-requested PRD contains product requirements only.

### Contract inputs that must be frozen before application integration

The supplied combined PRD refers to a companion package with 83 structural schemas, 42 OpenAPI operations, 159 acceptance cases and 33 work packages, but the actual schema/OpenAPI files, modular package, implementation-status.json and reference programs were not supplied. The embedded defaults and prose are available; a referenced filename alone does not supply its bytes. Do not fabricate a schema and describe it as the original contract. Obtain the companion package, or author an explicit versioned replacement from the prose and have its unresolved security semantics reviewed. Independent framework fixes below can proceed without that package.

Missing contracts particularly affect `ObjectHeader`, `ObjectMutation`, `AuthProof`, `DeviceCertificate`, recovery commit, encrypted upload manifest, approval proof, `RepostContextClaims`, `ContextualRepostPayload`, `ContextUnavailableNode` and canonical error DTO. Freeze field types, nullability, maximum lengths, canonical bytes, signature domains, enums and rejection vectors before suite handlers/adapters are accepted. New R2–R5 endpoints must be specified in OpenAPI before implementation. Cryptography, recovery authority and irreversible migration ambiguities require a reviewed decision and failing safety test; the local model must stop that dependent slice and continue unrelated ready work.

### Shared security and data contract

Private payloads are encrypted on an authorized client before the general API receives them. The isolated ordinary external SMTP ingest/egress boundaries are the disclosed exception. Browser private routes are client-rendered after enrollment; the server serves only a public shell/configuration, never decrypted Mail/Calendar/Vault hydration data. Public social SSR is allowed only after validating signed public payloads and current visibility/expiry. R1 browser Passwords/VPN UI is not a substitute for native vault/autofill or native tunnel enforcement.

Account authentication is distinct from signed device enrollment, compartment access and recovery authority. Successful passkey/password login does not grant a content key, security-admin ability or VPN private key. Effective access requires enabled feature, authenticated actor, current unrevoked device/connector grant, current epoch, tenant/object ACL, expiry, action policy and any required human approval. An ingress signer may create inbound Mail objects only. Mail permissions cannot authorize Vault/Calendar writes. Secret possession alone is not an authorization bypass.

Use the suite cipher profile: XChaCha20-Poly1305, 32-byte keys, 24-byte nonces, Ed25519 signatures, libsodium sealed boxes and RFC 8785 JCS. Default passphrase KDF is Argon2id, 64 MiB, operations limit 3, 16-byte salt and 32-byte output; parameters persist with each envelope. These are suite-selected constants, not a new account-verifier protocol. Use maintained reviewed crypto adapters; no cipher fallback for browser convenience. Reject duplicate JSON keys, nonfinite/out-of-bounds numbers, invalid encodings and unknown major versions. Large counters use decimal strings where JS precision would be lost.

The ordinary private object blob is nonce followed by AEAD ciphertext/tag. SHA-256 covers all stored bytes. AAD is JCS of schema_version, object_id, owner_account_id, compartment, kind, version_id, parent_version_id, cipher_suite and security_epoch. Signature bytes are UTF-8 `private-suite.object.v1\n` followed by the canonical complete header. Digest/size/envelope IDs/signature must not create a circular AAD dependency. Verify digest and signature before parsing/decrypting. File chunks instead use their version/index/length-bound manifest contract. Framework transport must preserve exact bytes; it does not invent envelope cryptography.

Use synthetic marker secrets in tests. They must be absent from server database plaintext, logs, errors, HTML, hydration state, browser history, local/session storage, caches, telemetry and diagnostic bundles except the specifically authorized decrypted client view. Never use live secrets. Revocation denies new access; it cannot erase recipient copies, previously downloaded keys or plaintext.

### Source document provenance

- `1-Private_Internet_Suite_Decisions_v2.md` — SHA-256 `1f0f335f3941ff4edf9cb451b030f956a1452fc95385619336c91b37353996a3`
- `2-Private_Internet_Suite_Agent_Prompt_v2.md` — SHA-256 `bbe68b2ac7786e6c64df28b33cdb8e5af0ebf704f7b0178ac5fc655561d00abc`
- `3-Private_Internet_Suite_PRD_v2.md` — SHA-256 `3b9a1ebb8a502efff8de6a06a03ecc0d57c374615627512233850c3a0bdfad0a`
