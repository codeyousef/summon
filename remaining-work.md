## Remaining Work

- **Add regression coverage**
    - JS + JVM integration tests that click rendered buttons (enabled/disabled, modifier-driven handlers, form
      submissions).
    - Ensure WASM path exercises modifier-driven events once WASM tests are wired back up.

- **SSR callback/hydration follow-up**
    - Decide on the server contract for re-invoking callbacks (either implement `/summon/callback` or formally remove
      the stale markers).
    - If callbacks stay, persist `CallbackRegistry` entries across render requests or scope them per session.

- **Docs & migration notes**
    - Document modifier-based event usage now that handlers live in `Modifier.eventHandlers`.
    - Provide guidance for apps that relied on the legacy hydration POST to bridge to the new client-side wiring.

- **Accessibility audit**
    - Verify focus/ARIA behaviour for disabled buttons across platforms with assistive tech tooling.
