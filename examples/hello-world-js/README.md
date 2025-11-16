# Hello World - JavaScript

A minimal Summon example using JavaScript target.

## Quick Start

```bash
# Run development server
./gradlew jsBrowserDevelopmentRun

# Build for production
./gradlew jsBrowserProductionWebpack
```

The app will be available at `http://localhost:8080`

## What's included

- Simple counter app
- Button click handling
- State management with `remember` and `mutableStateOf`
- Styled components using Summon's modifier system

## Project Structure

```
hello-world-js/
├── build.gradle.kts              # Build configuration
├── settings.gradle.kts           # Project settings
└── src/jsMain/
    ├── kotlin/
    │   └── Main.kt              # Main application code
    └── resources/
        └── index.html           # HTML entry point
```

## Learn More

- [Summon Documentation](https://github.com/codeyousef/summon)
- [Kotlin/JS Documentation](https://kotlinlang.org/docs/js-overview.html)
