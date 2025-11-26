# Hamburger Menu Hydration Fix - Remaining Work

## Summary
This branch contains fixes for the hamburger menu toggle functionality in SSR (Server-Side Rendered) pages. The core issue was that `data-action` based toggles weren't being handled properly by the WASM hydration client.

## What Was Fixed

### 1. WASM GlobalEventListener - Event Bubbling Logic
**File:** `summon-core/src/wasmJsMain/kotlin/codes/yousef/summon/hydration/GlobalEventListener.kt`

**Problem:** The event handler was stopping at the first element with `data-sid` (which the inner SPAN icon has), instead of continuing to bubble up to find `data-action` on the parent DIV (hamburger button).

**Fix:** Changed to first look specifically for `data-action`, then separately look for `data-sid`:
```kotlin
// First, look specifically for data-action (for toggle menus, etc.)
var current: Element? = target
while (current != null && !current.hasAttribute("data-action")) {
    current = current.parentElement
}
// ... then separately handle data-sid
```

### 2. JS GlobalEventListener - Same Fix
**File:** `summon-core/src/jsMain/kotlin/codes/yousef/summon/hydration/GlobalEventListener.kt`

Applied the same event bubbling fix for consistency.

### 3. WASM Main.kt - Missing GlobalEventListener.init()
**File:** `summon-core/src/wasmJsMain/kotlin/codes/yousef/summon/Main.kt`

**Problem:** The WASM entry point wasn't calling `GlobalEventListener.init()`, so SSR pages loading the WASM hydration client had no event listeners.

**Fix:** Added `GlobalEventListener.init()` call in the `main()` function.

### 4. Removed Duplicate Event Handler
**File:** `summon-core/src/wasmJsMain/kotlin/codes/yousef/summon/hydration/GlobalEventListener.kt`

**Problem:** Both `window.onclick` AND `document.addEventListener("click", ...)` were being registered, causing the toggle to fire twice (open then immediately close).

**Fix:** Removed the `window.onclick` handler, keeping only the `document.addEventListener` approach.

### 5. JVM Bootloader - Inline JavaScript Fallback
**File:** `summon-core/src/jvmMain/kotlin/codes/yousef/summon/runtime/JvmPlatformRenderer.kt`

**Added:** Inline JavaScript in the bootloader that handles `data-action` toggles immediately without waiting for WASM/JS hydration to load. This provides instant interactivity even before the hydration script loads.

## Files Modified
1. `summon-core/src/wasmJsMain/kotlin/codes/yousef/summon/hydration/GlobalEventListener.kt`
2. `summon-core/src/wasmJsMain/kotlin/codes/yousef/summon/hydration/ClientDispatcher.kt` (created)
3. `summon-core/src/wasmJsMain/kotlin/codes/yousef/summon/Main.kt`
4. `summon-core/src/wasmJsMain/kotlin/codes/yousef/summon/WasmApi.kt`
5. `summon-core/src/jsMain/kotlin/codes/yousef/summon/hydration/GlobalEventListener.kt`
6. `summon-core/src/jsMain/kotlin/codes/yousef/summon/JsApi.kt`
7. `summon-core/src/jvmMain/kotlin/codes/yousef/summon/runtime/JvmPlatformRenderer.kt`

## Files Created (Test Infrastructure)
1. `examples/ssr-hydration-test/` - SSR test app with HamburgerMenu for JVM testing
2. `e2e-tests/tests/ssr-hydration.spec.ts` - Playwright E2E tests for SSR hydration

## Current Test Status

### Passing ✅
- All 6 SSR Hydration tests pass (`e2e-tests/tests/ssr-hydration.spec.ts`)
- Hamburger menu toggle works correctly on mobile viewport

### Test Output (Last Run)
```
Running 6 tests using 4 workers
✓ page loads with SSR content
✓ hydration script loads and initializes  
✓ hamburger button is visible on mobile
✓ hamburger menu toggle works after hydration
✓ menu can be closed after opening
✓ data-action attribute is present on hamburger button
6 passed
```

## Remaining Tasks

### 1. Clean Up Debug Logging (IMPORTANT)
The WASM `ClientDispatcher.kt` and `GlobalEventListener.kt` still have verbose debug logging that should be removed or reduced for production:

**ClientDispatcher.kt** - Remove these lines:
```kotlin
wasmConsoleLog("[Summon WASM] ClientDispatcher.dispatch() - Parsing action: $actionJson")
wasmConsoleLog("[Summon WASM] Successfully parsed action: $action")
wasmConsoleLog("[Summon WASM] toggleElementVisibility() - targetId: $targetId")
wasmConsoleLog("[Summon WASM] Element '$targetId' - currentDisplay: $currentDisplay, newDisplay: $newDisplay")
```

**GlobalEventListener.kt** - Remove these lines:
```kotlin
wasmConsoleLog("[Summon WASM] handleEventInternal() - event type: ${event.type}")
wasmConsoleLog("[Summon WASM] Event target tagName: ${target.tagName}, has data-action: ${target.hasAttribute("data-action")}")
wasmConsoleLog("[Summon WASM] Found data-action on element: ${current.tagName}, action: $actionJson")
wasmConsoleLog("[Summon WASM] Found data-sid on element: ${current.tagName}, sid: $sid")
```

Keep only essential logs like:
- `GlobalEventListener.init()` confirmation
- Error messages

### 2. Rebuild After Cleanup
After removing debug logs:
```bash
./gradlew wasmJsBrowserProductionWebpack jsBrowserProductionWebpack copyHydrationBundles
```

### 3. Run Full Test Suite
```bash
# SSR Hydration tests
cd e2e-tests && BASE_URL=http://localhost:8080 npx playwright test tests/ssr-hydration.spec.ts

# JS Hydration tests (requires JS example app running on port 8081)
cd e2e-tests && BASE_URL=http://localhost:8081 npx playwright test tests/hydration.spec.ts
```

### 4. Final Verification
1. Start the SSR test app:
   ```bash
   cd examples/ssr-hydration-test && ../gradlew run
   ```
2. Open http://localhost:8080 in browser
3. Resize to mobile width (< 768px)
4. Click hamburger menu icon - should toggle open/closed
5. Check console for `[Summon]` logs (should be minimal after cleanup)

### 5. Commit and Push to Main
Once verified:
```bash
git add -A
git commit -m "fix: hamburger menu toggle now works on SSR pages with WASM hydration"
git push origin main
```

## Architecture Notes

### SSR Hydration Flow
1. JVM renders HTML with `data-action` attributes on interactive elements
2. Browser loads page (content visible immediately - SEO friendly)
3. Bootloader script runs (provides instant toggle via inline JS)
4. WASM hydration client loads asynchronously
5. `GlobalEventListener.init()` registers document-level event listeners
6. Clicks bubble up, find `data-action`, call `ClientDispatcher.dispatch()`
7. `ClientDispatcher` parses UiAction JSON and toggles element visibility

### Key Files for Future Reference
- **HamburgerMenu Component:** `summon-core/src/commonMain/kotlin/codes/yousef/summon/components/navigation/HamburgerMenu.kt`
- **UiAction Types:** `summon-core/src/commonMain/kotlin/codes/yousef/summon/action/UiAction.kt`
- **WASM Entry Point:** `summon-core/src/wasmJsMain/kotlin/codes/yousef/summon/Main.kt`
- **JVM SSR Renderer:** `summon-core/src/jvmMain/kotlin/codes/yousef/summon/runtime/JvmPlatformRenderer.kt`

## How to Continue

1. Open this branch: `git checkout fix/hamburger-menu-hydration`
2. Clean up debug logging (see section above)
3. Rebuild: `./gradlew wasmJsBrowserProductionWebpack copyHydrationBundles`
4. Test: Run the SSR test app and E2E tests
5. Merge to main when ready

## Contact
All work done in this session. The hamburger menu toggle is functional - just needs debug log cleanup before merging.
