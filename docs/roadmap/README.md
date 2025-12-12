# Summon Roadmap

This document outlines the feature roadmap for Summon, evolving from a multi-platform foundation to a feature-complete web frontend framework.

**Design Principles**:
- All APIs are Compose-like and Kotlin-idiomatic.
- Features implemented in `commonMain` with `expect`/`actual` for platform specifics.
- Native platforms use direct platform UI bindings via Kotlin/Native's C interop (NOT Skiko/Skia).
- Desktop-only or web-only features are no-ops on unsupported platforms.

**Production Readiness Standards**:
To ensure high quality and stability, every feature in this roadmap must meet the following "Definition of Done" criteria:

1.  **Comprehensive E2E Testing**:
    -   All user interactions must be verified with **Playwright** tests running against both WASM and JS targets.
    -   Tests must cover critical paths on Chrome, Firefox, and Safari.
    -   Visual regression tests must be included where applicable.

2.  **Code Coverage & Quality**:
    -   We utilize **Kover** to enforce code coverage.
    -   Features must maintain a minimum of **80% branch coverage**.
    -   No new linting errors or warnings (Detekt/Ktlint).

3.  **Documentation**:
    -   Public APIs must be fully documented using KDoc.
    -   We utilize **Dokka** to generate the API reference.
    -   Each feature must include a runnable example in the `examples/` directory.

4.  **Accessibility**:
    -   Components must pass automated accessibility checks (axe-core).
    -   Manual verification of keyboard navigation and screen reader announcements is required.

5.  **Cross-Platform Verification**:
    -   Features must be verified to degrade gracefully or no-op correctly on unsupported platforms.

## Release Plan

### [v0.6.0 - Multi-Platform Foundation](v0.6.0.md)
Focus: Essential UI/UX primitives needed for mobile/desktop support.
Goal: Establish the core interaction and layout models that abstract away platform differences.

### [v0.7.0 - Advanced Web & Desktop Prep](v0.7.0.md)
Focus: Multi-window capabilities and desktop-specific features.
Goal: Enable complex desktop-class applications and advanced web workflows.

### [v0.8.0 - Developer Experience (DX)](v0.8.0.md)
Focus: Tooling to make Summon a joy to use.
Goal: Provide a world-class development environment comparable to React/Vue ecosystems.

### [v0.9.0 - Application Architecture](v0.9.0.md)
Focus: Robust patterns for data, forms, and state.
Goal: Standardize how applications handle data flow and side effects.

### [v1.0.0 - Production Ready](v1.0.0.md)
Focus: Stability, accessibility, and optimization.
Goal: Ensure the framework is ready for mission-critical, high-scale production use.
