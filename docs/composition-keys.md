# Composition keys (0.8.0)

`remember` and effects use sequential slots within the current composition group.
Use `codes.yousef.summon.runtime.key` around conditional branches and each dynamic list item.
Entering a child group does not advance the parent's slot cursor. A group's identity is its
parent path plus the immutable list of key values; moving a sibling does not change this identity.

Keys compare by equality, including null values. Supply at least one value and keep each value's
`equals`/`hashCode` stable. Duplicate explicit keys in one parent fail with a generic exception.
The same key is valid in different parents. Omission releases a group's effects and cached values;
re-entry starts fresh. Changing an account or route key starts fresh state and disposes old effects.
Named `Composer.rememberedValue` caches are also group-local; they persist while the group exists,
even when not read in a pass. Never use mutable collections as key values.

Composition keys and DOM modifier keys serve separate purposes. Use both for list items needing
remembered identity and DOM identity. Automatic compiler call-site grouping is still pending;
unkeyed conditional `remember` calls within the same group remain positional. TextField's own
internal groups isolate its slots, but multiple dynamic fields still need explicit caller keys.
`RouteContentHandler` keys by route path and copied parameters. Wrap separate route hosts and
account-owned content in their own keys so equal routes in different hosts/accounts stay isolated.

This example is compiled and exercised in the JVM/JS/WASM source consumer under
`e2e-tests/fixtures/private-suite`. Import these runtime APIs, rather than another library's `key`:

```kotlin
import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.TextField
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.runtime.key
import codes.yousef.summon.runtime.remember

@Composable
fun FixtureApp(session: FixtureSession) {
    Column {
        if (session.loggedIn.value) key("account", session.accountIdentity) {
            TextField(
                value = session.binding.state.value,
                onValueChange = { session.binding.state.value = it }
            )
        }
        key("trailing-probe", session.accountIdentity) {
            val text = remember { "generation-${++session.probeCalculations}" }
            Text(text)
        }
    }
}
```

`FixtureSession` is the synthetic fixture's caller-owned state, not an authentication API.
Applications supply their actual account identity, state and callbacks. Place remembered calls
inside the key block, not in default arguments evaluated before entry. The wrapper returns its
block's result and restores the parent in `finally`; uncaught root failures dispose root ownership.
With no active composer, it executes the block normally and does not create persistent state.

Build the runnable fixture with `./gradlew -p e2e-tests/fixtures/private-suite
jsBrowserDistribution wasmJsBrowserDistribution -x :summon:summon-core:copyHydrationBundles`.
Run `./e2e-tests/run-private-suite-container.sh` for the six-engine matrix. The fixture verifies
conditional input removal followed by a differently typed remembered value, keyed list reorder,
item removal/re-entry, and effect removal without losing trailing state.
