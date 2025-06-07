# Summon JavaScript Example

This example demonstrates how to use Summon in a JavaScript/browser environment.

## Prerequisites

- JDK 17 or higher
- GitHub account with access to read packages
- Personal Access Token with `read:packages` permission

## Setup

### GitHub Packages Authentication

This example uses Summon from GitHub Packages, which requires authentication even for public packages.

1. **Create a Personal Access Token:**
   - Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
   - Generate a new token with `read:packages` permission
   - Copy the token value

2. **Configure authentication** (choose one method):

   **Method A: gradle.properties file (Recommended)**
   Create a `gradle.properties` file in this directory:
   ```properties
   gpr.user=YOUR_GITHUB_USERNAME
   gpr.key=YOUR_PERSONAL_ACCESS_TOKEN
   ```

   **Method B: Environment variables**
   Set these environment variables:
   ```bash
   export USERNAME=YOUR_GITHUB_USERNAME
   export TOKEN=YOUR_PERSONAL_ACCESS_TOKEN
   ```

3. **Run the example:**
   ```bash
   ./gradlew jsBrowserDevelopmentRun
   ```

## Available Gradle Tasks

- `./gradlew jsBrowserDevelopmentRun` - Start development server at http://localhost:8082
- `./gradlew jsBrowserProductionWebpack` - Build production bundle
- `./gradlew jsBrowserTest` - Run tests

## What's Included

- **Main.kt**: Entry point with routing setup
- **Components.kt**: Example Summon components
- **MinimalExample.kt**: Minimal component examples
- **index.html**: HTML page template
- **i18n/**: Internationalization resources

## Features Demonstrated

- Basic component composition
- State management with `remember` and `mutableStateOf`
- Event handling
- Styling with Summon modifiers
- Internationalization (i18n)
- Routing (basic setup)

## Development

The development server supports hot reload. Changes to Kotlin files will automatically rebuild and refresh the browser.

## Troubleshooting

### Build Issues

1. **Gradle wrapper permission error**: Run `chmod +x gradlew` on Unix systems
2. **Dependency resolution issues**: Make sure Summon is published to local Maven (`./gradlew publishToMavenLocal` from root)
3. **Port conflicts**: Change the port in `build.gradle.kts` if 8082 is in use

### Runtime Issues

1. Check browser console for JavaScript errors
2. Verify that all resources are loaded correctly in the Network tab
3. Ensure the development server is running and accessible

## Next Steps

- Explore the [Summon documentation](../../../README.md)
- Check out the [JVM Quarkus example](../../jvm/quarkus-example/)
- Review the [component API reference](../../../api-reference/components.md)