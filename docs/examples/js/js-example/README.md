# Summon JS Example (Standalone)

This example demonstrates the standalone Summon implementation for Kotlin/JS applications.

## Features

- ✅ **No external dependencies required** - Uses standalone implementation
- ✅ **Type-safe CSS styling** with enums and unit extensions
- ✅ **Interactive components** with JavaScript interop
- ✅ **Working out-of-the-box** - No authentication needed

## Quick Start

### Option 1: Simple HTTP Server (Recommended)

1. **Build the project:**
   ```bash
   ./gradlew jsBrowserDevelopmentWebpack
   ```

2. **Copy the compiled JavaScript:**
   ```bash
   cp build/kotlin-webpack/js/developmentExecutable/js.js build/processedResources/js/main/
   ```

3. **Start HTTP server:**
   ```bash
   cd build/processedResources/js/main
   python3 -m http.server 8081
   ```

4. **Open browser:** http://localhost:8081

   **✅ Status**: This method is tested and working!

### Option 2: Gradle Development Server

1. **Run the development server:**
   ```bash
   ./gradlew jsBrowserDevelopmentRun
   ```

2. **Open browser:** http://localhost:8080

   **Note:** If you see "Cannot GET /" error, use Option 1 instead.

## Available Gradle Tasks

- `./gradlew jsBrowserDevelopmentWebpack` - Build for development
- `./gradlew jsBrowserProductionWebpack` - Build production bundle  
- `./gradlew jsBrowserDevelopmentRun` - Start development server
- `./gradlew compileKotlinJs` - Compile Kotlin to JavaScript

## What's Included

- **StandaloneMain.kt**: Complete standalone Summon implementation
- **index.html**: HTML entry point with proper setup
- **Type-safe CSS**: Enums for all CSS properties and units
- **Interactive demo**: Counter with JavaScript interop

## Features Demonstrated

- **Type-Safe Styling**: Use `Cursor.Pointer` instead of `"pointer"`
- **CSS Unit Extensions**: Use `16.px` instead of `"16px"`
- **Component Composition**: Composable functions with `@Composable`
- **State Management**: `mutableStateOf()` and `remember()` functions
- **Event Handling**: JavaScript interop for user interactions
- **Professional UI**: Modern design with cards, buttons, and layouts

## Code Examples

### Type-Safe CSS Styling
```kotlin
StandaloneModifier()
    .backgroundColor(Color.BLUE.toHexString())
    .padding(8.px, 16.px)
    .borderRadius(4.px)
    .cursor(Cursor.Pointer)
    .fontWeight(FontWeight.Bold)
```

### Component Structure  
```kotlin
@Composable
fun MainApp(): String {
    return Column(
        modifier = EmptyModifier().padding(16.px)
    ) {
        Text("Hello, Summon!") +
        Button("Click Me", EmptyModifier().onClick("alert('Hello!')"))
    }
}
```

## Troubleshooting

### "Cannot GET /" Error
- **Cause**: Missing `index.html` or `js.js` files in the served directory
- **Solution**: Follow Option 1 (Simple HTTP Server) from Quick Start
- **Check**: Verify files exist in `build/processedResources/js/main/`

### JavaScript Errors in Browser
- **Check browser console** for compilation or runtime errors
- **Verify compilation**: Run `./gradlew compileKotlinJs` first
- **Check file paths**: Ensure `js.js` matches the script src in `index.html`

### Build Issues
- **Clean rebuild**: `./gradlew clean jsBrowserDevelopmentWebpack`
- **Check Kotlin version**: Ensure compatibility with Kotlin 2.2.0-RC2
- **Verify dependencies**: No external Summon dependencies required

## Project Structure

```
src/jsMain/
├── kotlin/code/yousef/summon/examples/js/
│   └── StandaloneMain.kt          # Complete standalone implementation
├── resources/
│   └── index.html                 # HTML entry point
└── build/
    ├── kotlin-webpack/js/developmentExecutable/
    │   └── js.js                  # Compiled JavaScript
    └── processedResources/js/main/
        ├── index.html             # Processed HTML
        └── js.js                  # JavaScript for serving
```

## Next Steps

- **Explore the working example** - Use Option 1 to see the full demo
- **Modify components** - Edit `StandaloneMain.kt` and rebuild
- **Check other examples** - See the [Quarkus example](../../jvm/quarkus-example/) for server-side usage
- **Review documentation** - Visit the [main docs](../../../README.md) for complete API reference