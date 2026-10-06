# Private suite source consumer fixture

This separate Gradle consumer resolves `codes.yousef:summon:0.7.0.4` and
`codes.yousef:summon-aether:0.7.0.4` to this checkout's source projects through
explicit composite-build substitution. It resolves the published
`codes.yousef.aether:aether-core:0.4.2.1` JVM artifact from Maven Central.
The audited source baselines are Summon `b967d88badd5b15162b6facf8915b5aaec6a451c`
and Aether `e1a3be4f00013fd0285ee5c16b782e5109ff42dd`; record the tested dirty-tree
diff or successor commit with verification output. Kotlin 2.3.0, coroutines
1.10.2, JDK 21, and JVM bytecode target 17 are pinned here. The fixture contains
only synthetic text, never production accounts or credentials.

From the Summon repository root:

```bash
./gradlew -p e2e-tests/fixtures/private-suite --no-daemon --max-workers=1 renderPublicShell jsBrowserDistribution wasmJsBrowserDistribution
```

Serve each generated distribution's directory on loopback and run the
`private-suite-fixture.spec.ts` Playwright tests with its URL in `BASE_URL`:

```bash
cd e2e-tests
BASE_URL=http://127.0.0.1:8877 ./node_modules/.bin/playwright test --config=playwright.private-suite.config.ts
```

The dedicated configuration runs Chromium, Firefox and WebKit sequentially.
WebKit coverage does not replace the roadmap's real Safari release check.
The fixture checks real Text, Button, controlled TextField, state recomposition,
flow binding, JS/WASM browser routing, guarded private deep links, history,
malformed-path rejection, dirty encrypted-draft decisions, keyed node
identity/order/focus/selection, latest-model callbacks through stable event
wrappers, stale asynchronous result rejection, safe unavailable states,
lifecycle pause/resume/destroy behavior, stable owner-local coroutine scopes,
100 lifecycle-owner cycles with zero host listener leaks, logout disposal,
effect key/removal cleanup, mount-only/dependency-aware effects, independent
synthetic roots and tabs, explicit root replacement/disposal, 100 mount cycles,
100 route swaps with stable node/listener/effect counts, detached callback
cleanup, failed-mount cleanup/context restoration, nullable/named remembered
caches, and JS microtask scheduling/disposal. Its JVM smoke path serves the
public shell through the Summon Aether adapter and a synthetic Aether exchange.
It does not qualify encryption, real account isolation, real Aether providers,
native clients, or real Safari. Those requirements remain incomplete in the PRD.

After building both distributions, the default configuration starts and stops
two loopback public-shell fallback servers and runs 168 target/browser rows:
161 execute and seven are explicit skips: six JS-specific microtask rows on
WASM, plus the two-tab WASM-WebKit row because Playwright WebKit crashes when
the bounded container instantiates its second WASM tab. JS and WASM single-tab
browser routing execute on Chromium, Firefox, and automated WebKit. Use Node 22 or 24
for browser installation; the host Node 26 installer stalled during archive
extraction in the development environment.

```bash
cd e2e-tests
npm ci
npm run test:private-suite
```

On a host without Playwright's supported system libraries, use the pinned
Playwright 1.56.1 Ubuntu container. It serves only the mounted synthetic fixtures,
has networking disabled, mounts the test source read-only, and writes failures
under the repository's ignored `.gradle/private-suite/browser-artifacts/matrix`.
Docker must be available to the current user. The image digest is frozen in the
script; its version must match the locked Playwright package.

```bash
./e2e-tests/run-private-suite-container.sh
```

Gradle JVM/JS/WASM classpaths are frozen in `gradle.lockfile`; both npm target
graphs are frozen in the fixture's `kotlin-js-store` lockfiles. Updating locks
is an explicit dependency change, not a normal verification step. Record the
repository commit and diff alongside test output when qualifying dirty source.
