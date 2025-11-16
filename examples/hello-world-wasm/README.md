# Hello World - WebAssembly

A minimal Summon example using WebAssembly (WASM) target.

## Quick Start

```bash
# Run development server
./gradlew wasmJsBrowserDevelopmentRun

# Build for production
./gradlew wasmJsBrowserProductionWebpack
```

The app will be available at `http://localhost:8080`

## What's included

- Simple WASM app with Summon
- Button click handling
- Direct DOM manipulation for WASM
- Styled components using Summon's modifier system

## Project Structure

```
hello-world-wasm/
├── build.gradle.kts              # Build configuration
├── settings.gradle.kts           # Project settings
└── src/wasmJsMain/
    ├── kotlin/
    │   └── Main.kt              # Main application code
    └── resources/
        └── index.html           # HTML entry point
```

## Browser Requirements

- Chrome 119+
- Firefox 120+
- Safari 16+
- Edge 119+

## Performance

WebAssembly provides 15-30% better performance compared to JavaScript for complex UI operations.

## Learn More

- [Summon WASM Guide](../../docs/wasm-guide.md)
- [Kotlin/Wasm Documentation](https://kotlinlang.org/docs/wasm-overview.html)
