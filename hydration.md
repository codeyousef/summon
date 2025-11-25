# Hydration in Summon

Summon uses a **Resumable Server-Driven UI** architecture to provide instant interactivity and robust state management. Unlike traditional frameworks that suffer from the "Uncanny Valley" of hydration (where the UI looks ready but isn't), Summon ensures that the client "resumes" the server's state immediately.

## The New Hydration Architecture (v0.5.0+)

Starting with version 0.5.0.0, Summon introduces a first-class hydration system that eliminates manual setup and ensures perfect synchronization between server and client.

### Key Features

1.  **Automatic Asset Management**: Summon Core now embeds all necessary hydration bundles (`summon-hydration.js`, `summon-hydration.wasm`). You no longer need to manually copy or configure Webpack for these assets.
2.  **Resumable State**: The server serializes the exact application state (`UiState`) into the HTML. The client boots up with this state, meaning no initial network requests are needed to fetch data.
3.  **Deterministic Identity**: Every component gets a stable, path-based ID (`data-sid`) during server rendering. The client uses these IDs to attach event listeners without re-rendering the DOM.
4.  **Global Event Delegation**: A single global event listener handles interactions, reducing memory usage and ensuring events are captured even before the framework fully loads.

---

## Migration Guide

If you are upgrading from an older version of Summon (pre-0.4.8), follow these steps to migrate to the new hydration API.

### 1. Update Dependencies

Ensure you are using the latest version of Summon.

```kotlin
// build.gradle.kts
implementation("codes.yousef:summon:0.5.2.1")
```

### 2. Server-Side Changes

#### Using Ktor / Spring / Quarkus Helpers (Recommended)

If you are using one of the integration libraries, switch to the hydrated response helpers. These automatically inject the necessary scripts and state.

**Ktor:**

```kotlin
// OLD (Deprecated)
call.respondText(renderer.renderComposableRoot { App() }, ContentType.Text.Html)

// NEW
call.respondSummonHydrated { App() }
```

**Spring Boot:**

```kotlin
// NEW
@GetMapping("/")
fun index(model: Model): String {
    return renderer.renderHydrated(model) { App() }
}
```

#### Manual Implementation (Advanced)

If you are manually rendering HTML, use `renderComposableRootWithHydration`.

```kotlin
// OLD
val html = renderer.renderComposableRoot { App() }

// NEW
val html = renderer.renderComposableRootWithHydration { App() }
```

*Note: This method automatically injects the `<script src="/summon-hydration.js"></script>` tag and the serialized state.*

### 3. Client-Side Changes (Kotlin/JS or Wasm)

Update your client entry point (`Main.kt`) to use `hydrateComposableRoot` instead of `renderComposable`.

**OLD (Deprecated):**
```kotlin
fun main() {
    // This would destroy server HTML and re-render
    renderComposable("root") { 
        App() 
    }
}
```

**NEW:**
```kotlin
fun main() {
    // This attaches listeners to existing server HTML
    hydrateComposableRoot("root") {
        App()
    }
}
```

### 4. Remove Legacy Assets

You can safely remove any manual `initialize-summon.js` or `JsEnvironmentSetup.js` files from your project. The new `summon-core` artifact handles the bootloader automatically.

---

## How It Works Under the Hood

1.  **Server Render**: The server renders the full HTML tree. It assigns a unique `data-sid` to every interactive element (e.g., `root/column-1/button-2`).
2.  **State Injection**: The server serializes the `UiState` and injects it into a `<script type="application/json" id="summon-state">` tag.
3.  **Client Boot**: The `summon-hydration.js` script loads. It reads the state from the script tag and initializes the `PlatformRenderer` with this pre-loaded state.
4.  **Hydration**: `hydrateComposableRoot` traverses the virtual component tree. Instead of creating new DOM nodes, it looks up existing nodes using their `data-sid` and attaches the necessary event listeners.

## Troubleshooting

### "Button clicks are ignored"
*   **Cause**: The hydration script hasn't loaded or the IDs don't match.
*   **Fix**: Ensure you are using `renderComposableRootWithHydration` on the server. Check the browser console for "Hydration mismatch" errors.

### "Hydration Mismatch" Warning
*   **Cause**: The component tree rendered on the client differs from the server.
*   **Fix**: Ensure your `App()` composable is deterministic. Avoid using `Math.random()` or `Date.now()` directly in composition without `remember` or `SideEffect`.

### 404 on `/summon-hydration.js`
*   **Cause**: Your server isn't serving static resources from the classpath.
*   **Fix**: Ensure your server framework is configured to serve resources from `/static` or the root classpath. Summon embeds these files in the JAR.
