# Hydration in Summon

Summon uses a "Resumable Server-Driven UI" architecture to provide instant interactivity and robust state management. This approach avoids the "Uncanny Valley" of hydration often found in other frameworks by ensuring that the client "resumes" the server's state rather than re-rendering it.

## Core Concepts

### 1. The Handshake (State Synchronization)
The server serializes the application state (`UiState`) into a secure, Base64-encoded JSON payload. This payload is injected into the HTML, ensuring that the client starts with the exact same state as the server.

### 2. The Map (Deterministic Identity)
Every interactive element is assigned a deterministic, path-based ID (`data-sid`) during server-side rendering. This ID is derived from the component tree structure (e.g., `root/div-1/button-2`), guaranteeing that the client can locate the correct DOM element without guessing.

### 3. The Ears (Event Delegation)
Instead of attaching thousands of event listeners to individual elements, Summon uses a single **Global Event Listener** on the document root. When a user interacts with the page (e.g., clicks a button), the event bubbles up to the global listener, which uses the `data-sid` to identify the target component.

### 4. The Voice (Action Dispatch)
Client-side actions are defined as polymorphic `UiAction` objects (e.g., `Navigate`, `ServerRpc`). These actions are serialized into `data-action` attributes on the DOM. When an event occurs, the client dispatches the action to the server or handles it locally.

### 5. The Bootloader
A tiny, inline JavaScript bootloader runs immediately after the HTML is parsed. It:
*   Parks the server state in memory.
*   Queues user interactions (clicks, inputs) that happen before the main framework loads.
*   Ensures zero interaction loss during the "hydration gap."

## Usage

### Defining State
Implement the `UiState` interface for your application state.

```kotlin
@Serializable
data class AppState(val count: Int) : UiState
```

### Defining Actions
Use `UiAction` to define user intents.

```kotlin
val incrementAction = UiAction.ServerRpc(
    endpoint = "increment",
    payload = buildJsonObject { put("amount", 1) }
)
```

### Rendering
The `PlatformRenderer` automatically handles hydration when you use `renderComposableRootWithHydration`.

```kotlin
renderer.renderComposableRootWithHydration(state) {
    Button(action = incrementAction) {
        Text("Count: ${state.count}")
    }
}
```

## Legacy Hydration (Deprecated)
The old `HydrationManager` and direct event binding mechanism are deprecated and will be removed in a future version. Please migrate to the new architecture.
