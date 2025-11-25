# WebAssembly (WASM) Guide

Summon 0.5.2.0 introduces comprehensive WebAssembly support, bringing near-native performance to web applications while
maintaining full compatibility with server-side rendering and JavaScript fallbacks.

## Table of Contents

- [Overview](#overview)
- [Getting Started](#getting-started)
- [Project Setup](#project-setup)
- [Development Workflow](#development-workflow)
- [Performance Optimization](#performance-optimization)
- [Browser Compatibility](#browser-compatibility)
- [Production Deployment](#production-deployment)
- [Troubleshooting](#troubleshooting)
- [Migration Guide](#migration-guide)

## Overview

### What is WebAssembly?

WebAssembly (WASM) is a binary instruction format that runs at near-native performance in web browsers. Summon's WASM
implementation provides:

- **95%+ Native Performance**: Execute computationally intensive tasks at near-native speeds
- **Minimal Startup Time**: Sub-100ms initialization with optimized bootstrap
- **Memory Efficiency**: Precise garbage collection and memory management
- **Universal Compatibility**: Runs in all modern browsers with automatic fallbacks

### Benefits of WASM with Summon

1. **Performance**: Significant speed improvements for complex applications
2. **SEO Compatibility**: Full server-side rendering with WASM hydration
3. **Progressive Enhancement**: Graceful degradation to JavaScript when needed
4. **Type Safety**: Full Kotlin type system benefits in the browser
5. **Code Sharing**: Share business logic between server and client seamlessly

## Getting Started

### Prerequisites

- Kotlin 1.9.20 or later
- Gradle 8.0 or later
- Node.js 16.0 or later (for development server)
- Modern browser with WASM support

### Quick Start

Create a new WASM project:

```bash
# Download Summon CLI from GitHub Packages
# Visit: https://github.com/codeyousef/summon/packages
# Or use the native executable directly

# Create a new project (start from the standalone template)
java -jar summon-cli-0.5.2.0.jar init my-wasm-app --mode=standalone

# Navigate to project
cd my-wasm-app

# Run development server (after enabling the WASM target)
./gradlew wasmJsBrowserDevelopmentRun
```

### Your First WASM Component

```kotlin
// src/wasmJsMain/kotlin/App.kt
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.runtime.remember

@Composable
fun App() {
    val count = remember { mutableStateOf(0) }

    Column(modifier = Modifier()) {
        Text(
            text = "WASM Counter: ${count.value}",
            modifier = Modifier()
        )

        Button(
            onClick = { count.value++ },
            label = "Increment (+1)",
            modifier = Modifier()
        )

        Button(
            onClick = { count.value += 10 },
            label = "Fast Increment (+10)",
            modifier = Modifier()
        )
    }
}
```

## Project Setup

### Build Configuration

```kotlin
// build.gradle.kts
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    wasmJs {
        moduleName = "my-wasm-app"
        browser {
            commonWebpackConfig {
                outputFileName = "my-wasm-app.js"
                devServer = devServer?.copy(
                    port = 3000,
                    open = false
                )
            }
        }
        binaries.executable()
    }

    // JVM target for SSR
    jvm {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // New group ID (preferred)
                implementation("codes.yousef:summon:0.5.2.0")
                // Legacy (deprecated; supported only until 0.5.2.0)
                // implementation("io.github.codeyousef:summon:0.5.2.0")
            }
        }

        val wasmJsMain by getting {
            dependencies {
                // New group ID (preferred)
                implementation("codes.yousef:summon-wasm-js:0.5.2.0")
                // Legacy (deprecated; supported only until 0.5.2.0)
                // implementation("io.github.codeyousef:summon-wasm-js:0.5.2.0")
            }
        }

        val jvmMain by getting {
            dependencies {
                // New group ID (preferred)
                implementation("codes.yousef:summon-jvm:0.5.2.0")
                // Legacy (deprecated; supported only until 0.5.2.0)
                // implementation("io.github.codeyousef:summon-jvm:0.5.2.0")
            }
        }
    }
}
```

### Entry Point Setup

```kotlin
// src/wasmJsMain/kotlin/Main.kt
import code.yousef.summon.runtime.wasmMain

fun main() {
    wasmMain {
        App()
    }
}
```

### HTML Template

```html
<!-- src/wasmJsMain/resources/index.html -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My WASM App</title>
    <style>
        body {
            margin: 0;
            padding: 20px;
            font-family: system-ui;
        }

        #loading {
            text-align: center;
            padding: 50px;
        }
    </style>
</head>
<body>
<div id="root">
    <div id="loading">Loading WASM application...</div>
</div>

<script>
    // Progressive loading with fallback
    async function loadApp() {
        try {
            if ('WebAssembly' in window) {
                console.log('Loading WASM version...');
                await import('./my-wasm-app.js');
            } else {
                console.log('WASM not supported, loading JS fallback...');
                await import('./my-app-js-fallback.js');
            }
        } catch (error) {
            console.error('Failed to load application:', error);
            document.getElementById('loading').innerHTML =
                    'Failed to load application. Please refresh the page.';
        }
    }

    loadApp();
</script>
</body>
</html>
```

## Development Workflow

### Build Commands

```bash
# Development build with source maps
./gradlew wasmJsBrowserDevelopmentRun

# Production build optimized
./gradlew wasmJsBrowserProductionWebpack

# Run tests
./gradlew wasmJsTest

# Clean build
./gradlew clean wasmJsBrowserDistribution
```

### Hot Reload Development

```bash
# Start development server with hot reload
./gradlew wasmJsBrowserDevelopmentRun --continuous
```

### Debugging

Enable source maps for debugging:

```kotlin
// build.gradle.kts
kotlin {
    wasmJs {
        browser {
            commonWebpackConfig {
                devtool = "source-map"
                mode = "development"
            }
        }
    }
}
```

## Performance Optimization

### Bundle Size Optimization

```kotlin
// build.gradle.kts - Production optimizations
kotlin {
    wasmJs {
        browser {
            commonWebpackConfig {
                mode = "production"
                optimization {
                    minimize = true
                    splitChunks {
                        chunks = "all"
                    }
                }
            }
        }
    }
}
```

### Code Splitting

```kotlin
// Lazy loading for large components
@Composable
fun App() {
    var showHeavyComponent by remember { mutableStateOf(false) }

    Column(modifier = Modifier()) {
        Button(
            onClick = { showHeavyComponent = true },
            label = "Load Heavy Component",
            modifier = Modifier()
        )

        if (showHeavyComponent) {
            // This component will be lazy-loaded
            HeavyDataVisualizationComponent()
        }
    }
}
```

### Memory Management

```kotlin
// Use remember for expensive computations
@Composable
fun ExpensiveComponent(data: List<DataPoint>) {
    val processedData = remember(data) {
        // Expensive computation cached until data changes
        data.map { processComplexCalculation(it) }
    }

    DataVisualization(data = processedData)
}
```

## Browser Compatibility

### Feature Detection

Summon automatically detects WASM support and falls back gracefully:

```kotlin
// Browser compatibility is handled automatically
// Your code works the same across all targets

@Composable
fun UniversalComponent() {
    val platform = remember { detectPlatform() }

    Column(modifier = Modifier()) {
        Text("Running on: $platform", modifier = Modifier())
        // Component works on WASM, JS, and JVM
    }
}
```

### Supported Browsers

| Browser       | WASM Support | Performance | Notes                |
|---------------|--------------|-------------|----------------------|
| Chrome 69+    | ✅ Full       | Excellent   | Best performance     |
| Firefox 62+   | ✅ Full       | Excellent   | Good debugging tools |
| Safari 14+    | ✅ Full       | Good        | Some limitations     |
| Edge 79+      | ✅ Full       | Excellent   | Same as Chrome       |
| Mobile Chrome | ✅ Full       | Good        | Optimized for mobile |
| Mobile Safari | ⚠️ Limited   | Fair        | Fallback recommended |

### Polyfills and Fallbacks

```html
<!-- Automatic fallback configuration -->
<script>
    window.SummonConfig = {
        preferWasm: true,
        fallbackToJs: true,
        detectFeatures: true,
        loadingTimeout: 10000
    };
</script>
```

## Production Deployment

### Build for Production

```bash
# Create optimized production build
./gradlew wasmJsBrowserProductionWebpack

# Files will be in build/distributions/
ls build/distributions/
# -> my-wasm-app.js
# -> my-wasm-app.wasm
# -> index.html
```

### Server Configuration

#### Nginx Configuration

```nginx
# nginx.conf
server {
    listen 80;
    server_name yourdomain.com;

    # Enable gzip compression
    gzip on;
    gzip_types
        application/wasm
        application/javascript
        text/css
        text/html;

    # WASM MIME type
    location ~* \.wasm$ {
        add_header Content-Type application/wasm;
        add_header Cache-Control "public, max-age=31536000";
    }

    # JavaScript files
    location ~* \.js$ {
        add_header Content-Type application/javascript;
        add_header Cache-Control "public, max-age=31536000";
    }

    # Main application
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

#### CDN Deployment

```bash
# Upload to CDN with proper headers
aws s3 cp build/distributions/ s3://your-bucket/ --recursive \
  --metadata-directive REPLACE \
  --content-type "application/wasm" \
  --include "*.wasm" \
  --cache-control "public, max-age=31536000"
```

### Performance Monitoring

```kotlin
// Add performance monitoring
@Composable
fun MonitoredApp() {
    val performanceMetrics = remember { PerformanceTracker() }

    LaunchedEffect(Unit) {
        performanceMetrics.trackAppStart()
    }

    App()
}
```

## Troubleshooting

### Common Issues

#### 1. WASM Module Not Loading

**Problem**: `Failed to instantiate WebAssembly module`

**Solution**:

```html
<!-- Ensure proper MIME type -->
<script>
    // Check if WASM is properly served
    fetch('./my-app.wasm')
            .then(response => {
                console.log('WASM MIME type:', response.headers.get('content-type'));
                // Should be 'application/wasm'
            });
</script>
```

#### 2. Large Bundle Size

**Problem**: WASM bundle is too large (>1MB)

**Solutions**:

```kotlin
// 1. Enable dead code elimination
// build.gradle.kts
kotlin {
    wasmJs {
        browser {
            commonWebpackConfig {
                optimization {
                    usedExports = true
                    sideEffects = false
                }
            }
        }
    }
}

// 2. Use code splitting
@Composable
fun LazyLoadedFeature() {
    // Split heavy features into separate modules
}
```

#### 3. Memory Issues

**Problem**: Out of memory errors in WASM

**Solution**:

```kotlin
// Monitor memory usage
@Composable
fun MemoryAwareComponent() {
    DisposableEffect(Unit) {
        val cleanup = {
            // Cleanup heavy resources
            clearCaches()
            System.gc() // Suggest garbage collection
        }
        onDispose { cleanup() }
    }
}
```

### Debug Tools

```bash
# Enable verbose logging
export SUMMON_DEBUG=true
export WASM_DEBUG=true

# Run with detailed output
./gradlew wasmJsBrowserDevelopmentRun --info

# Browser debugging
# 1. Open Chrome DevTools
# 2. Go to Sources tab
# 3. Look for .wasm files
# 4. Set breakpoints in Kotlin source
```

## Migration Guide

### From JavaScript to WASM

1. **Update build.gradle.kts**:

```kotlin
// Add WASM target
kotlin {
    js(IR) { /* existing JS config */ }
    wasmJs { /* new WASM config */ }
}
```

2. **Move shared code to commonMain**:

```kotlin
// src/commonMain/kotlin/SharedComponents.kt
@Composable
fun SharedComponent() {
    // Works on both JS and WASM
}
```

3. **Create WASM-specific entry point**:

```kotlin
// src/wasmJsMain/kotlin/Main.kt
fun main() {
    wasmMain { App() }
}
```

4. **Update HTML loading**:

```html
<!-- Progressive enhancement -->
<script>
    if ('WebAssembly' in window) {
        import('./app-wasm.js');
    } else {
        import('./app-js.js');
    }
</script>
```

### Performance Migration Checklist

- [ ] Move heavy computations to WASM target
- [ ] Implement lazy loading for large components
- [ ] Add proper error boundaries
- [ ] Configure bundle splitting
- [ ] Set up performance monitoring
- [ ] Test on target browsers
- [ ] Optimize for mobile devices
- [ ] Configure proper caching headers

## Best Practices

### Code Organization

```
src/
├── commonMain/kotlin/          # Shared code
│   ├── components/            # UI components
│   ├── models/               # Data models
│   └── utils/                # Utilities
├── wasmJsMain/kotlin/         # WASM-specific code
│   ├── Main.kt               # Entry point
│   └── platform/             # Platform-specific implementations
└── jvmMain/kotlin/            # Server-side code
    └── ssr/                  # SSR implementations
```

### Performance Guidelines

1. **Use remember() for expensive computations**
2. **Implement proper component lifecycle management**
3. **Minimize DOM manipulation**
4. **Use lazy loading for heavy features**
5. **Monitor bundle size regularly**
6. **Test on various devices and browsers**

### Security Considerations

- Validate all inputs on both client and server
- Use HTTPS for all WASM deployments
- Implement proper Content Security Policy (CSP)
- Regular security audits of dependencies

## Conclusion

WebAssembly support in Summon provides a powerful platform for building high-performance web applications while
maintaining the developer experience and type safety that Kotlin developers expect. With proper setup and optimization,
WASM applications can deliver near-native performance in the browser while maintaining full SEO compatibility through
server-side rendering.

For more advanced topics and specific use cases, refer to the [API Reference](api-reference/wasm.md)
and [Examples Repository](https://github.com/codeyousef/summon-examples).
