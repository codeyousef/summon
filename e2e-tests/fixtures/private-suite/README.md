# Private suite source consumer fixture

This separate Gradle consumer resolves `codes.yousef:summon:0.7.0.4` to this
checkout's `summon-core` through explicit composite-build substitution. It uses
Kotlin 2.3.0, coroutines 1.10.2 and JVM target 17, matching the audited source.
It contains only synthetic text, never production accounts or credentials.

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
flow binding, keyed node identity/order, logout disposal, effect key/removal cleanup
mount-only/dependency-aware effects, independent synthetic roots, explicit root replacement/disposal,
100 mount cycles, detached callback cleanup, failed-mount cleanup/context restoration, nullable/named remembered caches, and JS microtask scheduling/disposal. It does not yet qualify routing, all lifecycle adapters,
Aether SSR integration, encryption, real account isolation or native clients.
Those requirements remain incomplete in the PRD.

After building both distributions, the default configuration starts and stops
its own two loopback servers and runs 60 shared target/browser cases plus six
JS-specific microtask cases. The six WASM microtask rows are explicitly skipped
because MicrotaskScheduler is a JS-specific API. Use Node 22
or 24 for browser installation; the host Node 26 installer stalled during
archive extraction in the development environment.

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
