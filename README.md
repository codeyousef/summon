# Summon

**Summon** is a powerful, type-safe frontend framework for Kotlin Multiplatform that brings the elegance of Jetpack Compose to both browser and JVM environments. Build beautiful, responsive applications with a declarative syntax that feels natural to Kotlin developers.

> 🎨 **Type-safe styling** with an intuitive modifier API inspired by Compose.
> 
> 🧩 **Component-based architecture** for maximum reusability and maintainability.
> 
> 🔄 **Reactive state management** that automatically updates your UI when data changes.

Summon combines the best ideas from modern frontend frameworks like React, Vue, and Next.js with the declarative UI patterns of Jetpack Compose and SwiftUI, while leveraging Kotlin's powerful type system to catch errors at compile time rather than runtime. Whether you're building a simple website or a complex web application, Summon provides the tools you need to create polished, professional user interfaces with less code and fewer bugs.

## Features

- **Cross-Platform**: Build once, run on both JavaScript and JVM platforms
- **Component-Based**: Create reusable UI components with a declarative syntax
- **Type-Safe**: Leverage Kotlin's type system for safer UI development with compile-time checks
- **Enhanced Styling System**: 
  - Flexible and powerful styling using a comprehensive modifier API
  - Type-safe CSS properties with enum support for values like BorderStyle, Alignment, etc.
  - Numeric extensions for CSS units (px, rem, em, etc.) for cleaner code
  - Comprehensive gradient support with both linear and radial options
  - Extensive color system with Material Design and Catppuccin palettes
- **Flexible Layout System**:
  - Powerful flexbox layout with alignment controls
  - Row and Column components with intuitive alignment modifiers
  - Grid layout support with comprehensive controls
- **State Management**: Simple yet powerful reactive state management solutions
- **Next.js-Style Routing**: File-based routing with automatic page discovery and code generation
- **Lifecycle Aware**: Built-in lifecycle management with side effects and cleanup
- **Framework Interoperability**: Integrate with existing frameworks like Quarkus, Ktor, and Spring Boot
- **Security**: Comprehensive JWT authentication and role-based access control (RBAC)
- **Accessibility**: Built-in accessibility features with ARIA support for inclusive applications
- **Animation and Transitions**: 
  - Smooth animations with keyframes support
  - CSS transitions with type-safe timing functions and properties
  - Transform operations for scaling, rotation, and translation
- **SSR Support**: Server-side rendering capabilities for improved performance
- **Enhanced Theme System**: 
  - Flexible theming with dark mode support
  - Type-safe theme properties for typography, spacing, colors, and more
  - Easy theme switching at runtime
- **Internationalization**: Full i18n support with RTL layouts for languages like Arabic and Hebrew

## What's New

Recent enhancements to Summon include:

- **Enhanced Theme System** with typed theme classes for more type-safe access
- **Improved Modifier API** with type-safe CSS properties and enum support
- **Comprehensive Border API** with support for individual sides and properties
- **Enhanced Flexbox Layout** with alignment controls for Row and Column components
- **Extensive Color System** with Material Design and Catppuccin palettes
- **Gradient Support** for both linear and radial gradients with flexible options
- **Animation Enhancements** with keyframes and transition support
- **Improved Documentation** with comprehensive examples and API references

## Component Categories

Summon provides a comprehensive set of UI components organized into logical categories:

### Input Components
- **TextField** - Single-line text input
- **TextArea** - Multi-line text input
- **Button** - Clickable button with various styles
- **Checkbox** - Checkbox input with label
- **RadioButton** - Radio button for single selection
- **Switch** - Toggle switch component
- **Select** - Dropdown selection component
- **Slider** - Single value slider
- **RangeSlider** - Dual-handle range slider
- **DatePicker** - Date selection component
- **TimePicker** - Time selection component
- **FileUpload** - File upload with drag & drop support
- **Form** & **FormField** - Form management components

### Layout Components
- **Row** - Horizontal layout with alignment controls
- **Column** - Vertical layout with alignment controls
- **Box** - Container with positioning capabilities
- **Grid** - CSS Grid layout component
- **Spacer** - Flexible spacing component
- **Divider** - Visual separator
- **Card** - Elevated container with shadow
- **AspectRatio** - Maintains aspect ratio for content
- **ExpansionPanel** - Collapsible/expandable panel
- **LazyColumn** & **LazyRow** - Virtualized lists for performance
- **ResponsiveLayout** - Responsive layout utilities

### Display Components
- **Text** - Text display with styling
- **Image** - Image display component
- **Icon** - Icon component with various sources
- **Badge** - Small status indicator

### Feedback Components
- **Alert** - Dismissible alert messages
- **Snackbar** & **SnackbarHost** - Temporary notifications
- **Progress** & **ProgressBar** - Progress indicators
- **Tooltip** - Hover tooltips

### Navigation Components
- **Link** - Navigation links
- **TabLayout** - Tab-based navigation

### Utility Components
- **Div** - Basic container element
- **AccessibleElement** - Accessibility wrapper

## Inspiration

Summon is proudly inspired by [Kobweb](https://github.com/varabyte/kobweb), a modern framework for full stack web apps in Kotlin built upon Compose HTML. Kobweb's elegant API design and approach to creating web applications using Kotlin has been instrumental in shaping Summon's philosophy. We highly recommend checking out Kobweb if you're looking for a mature, feature-rich solution for Kotlin web development.

## Documentation

For detailed documentation, please check the [docs](docs/README.md) directory:

- [Components](docs/components.md) - Learn about Summon's built-in UI components
- [Routing](docs/routing.md) - Set up navigation in your application with Next.js-style file-based routing
- [File-Based Routing](docs/file-based-routing.md) - Next.js-style file-based routing system
- [State Management](docs/state-management.md) - Manage application state effectively
- [Styling](docs/styling.md) - Apply styles to your components
- [Integration Guides](docs/integration-guides.md) - Integrate with existing frameworks
- [Security](docs/security.md) - Authentication and authorization features
- [Accessibility and SEO](docs/accessibility-and-seo.md) - Build accessible and SEO-friendly applications
- [Internationalization](docs/i18n.md) - Add multi-language support with RTL layouts

### API Reference

Comprehensive API reference documentation is available in the [docs/api-reference](docs/api-reference) directory:

- [Core API](docs/api-reference/core.md) - Core interfaces and classes
- [Components API](docs/api-reference/components.md) - Built-in UI components
- [Modifier API](docs/api-reference/modifier.md) - Styling and layout modifiers
- [State API](docs/api-reference/state.md) - State management utilities
- [Routing API](docs/api-reference/routing.md) - Navigation and routing
- [Effects API](docs/api-reference/effects.md) - Side effects and lifecycle management
- [Events API](docs/api-reference/events.md) - Event handling and listeners
- [Animation API](docs/api-reference/animation.md) - Animations, transitions, and keyframes
- [Theme API](docs/api-reference/theme.md) - Theming and styling system
- [Color API](docs/api-reference/color.md) - Color system with Material Design and Catppuccin palettes
- [Focus Management API](docs/api-reference/focus.md) - Focus management and keyboard navigation
- [Validation API](docs/api-reference/validation.md) - Form validation and input validation
- [Security API](docs/api-reference/security.md) - Security features and access control
- [Authentication API](docs/api-reference/auth.md) - Authentication providers and JWT integration
- [Accessibility API](docs/api-reference/accessibility.md) - Accessibility features and ARIA support
- [SEO API](docs/api-reference/seo.md) - SEO features and meta tags
- [Internationalization API](docs/api-reference/i18n-api.md) - Multi-language and RTL layout support

## Publishing and CI/CD

Summon now includes automated publishing and continuous integration. The project supports publishing to both Maven Central and GitHub Packages with comprehensive testing.

### Automated Publishing

Publishing happens automatically through GitHub Actions:

- **On push to `main`**: 
  - Builds are automatically published to GitHub Packages
  - Bundle is created and uploaded to Maven Central Portal
  - Maven Central publication happens automatically after validation
  - Version is automatically changed to `X.X.X-SNAPSHOT` for snapshots
  - No manual action required - just push to main!
  
- **On GitHub release**:
  - Release builds are published to both GitHub Packages and Maven Central
  - Uses the version tag from the release
  - Automatic publication to Maven Central (no manual approval needed)
  
- **Pull request testing**: All tests run automatically on pull requests

### Setting Up Publishing

Publishing configuration includes:

- Maven Central setup (Sonatype OSSRH)
- GPG key generation and configuration
- GitHub Secrets configuration
- Local development setup

**Important:** Publishing to Maven Central requires authentication:

**For CI/CD (GitHub Actions):**
- Set up the following as GitHub repository secrets (Settings → Secrets and variables → Actions):
  - `CENTRAL_USERNAME` - Your Sonatype OSSRH username
  - `CENTRAL_PASSWORD` - Your Sonatype OSSRH password
  - `SIGNING_KEY_ID` - Your GPG key ID for signing artifacts
  - `SIGNING_PASSWORD` - Your GPG key passphrase
  - `SIGNING_SECRET_KEY` - Your GPG private key (base64 encoded)
- Publishing will run automatically on releases

**For Local Publishing to Maven Central Portal:**
- Export the required environment variables:
  ```bash
  export CENTRAL_USERNAME="your-central-portal-username"
  export CENTRAL_PASSWORD="your-central-portal-token"
  ```
- Run the publishing script:
  ```bash
  ./publish-to-central-portal.sh  # Linux/macOS
  # or
  publish-to-central-portal.bat   # Windows (requires manual upload step)
  ```
- This creates a bundle and uploads it to the Central Portal
- The bundle is automatically published to Maven Central after validation
- Monitor status at https://central.sonatype.com

**Publishing to GitHub Packages (currently active):**
- Ensure `GITHUB_TOKEN` is set with `write:packages` permission
- Run `./gradlew publishAllPublicationsToGitHubPackagesRepository`

**Troubleshooting Publishing Issues:**

**If you see errors about `publishJsPublicationToCentralRepository` or similar phantom tasks:**
- This is a Gradle cache issue where removed repository configurations are still cached
- Solution: Run `./clean-gradle-cache.sh` (Linux/macOS) or `clean-gradle-cache.bat` (Windows)
- This completely clears Gradle's cache and removes all stale configurations
- After cleaning, only `GitHubPackagesRepository` tasks will be available

**Maven Central Publishing Notes:**
- Maven Central now uses the Central Portal (https://central.sonatype.com) for new accounts
- Traditional OSSRH publishing no longer works for accounts created in 2024+
- Use the provided `publish-to-central-portal.sh` script for Maven Central
- The script handles .klib file exclusion and automatic publishing
- GitHub Packages publishing works immediately without additional configuration

### Quick Testing

Run the included test scripts to verify everything works locally:

**Linux/macOS:**
```bash
./run-tests.sh
```

**Windows:**
```cmd
run-tests.bat
```

These scripts will:
- ✅ Run all tests (JVM, JS with Chrome headless, and common)
- 🔨 Generate test reports in `build/reports/tests/`
- 📋 Show test results summary

**Note:** JS tests may fail due to a known issue with Kotlin 2.2.0-Beta1 and kotlinx-serialization. This is a temporary issue that doesn't affect the main code compilation.

For a full build including packaging:
```bash
./gradlew build -x jsTest -x jsBrowserTest
```

## Installation

Summon is available from both Maven Central and GitHub Packages.

### From Maven Central (Recommended)

Add Summon to your project dependencies:

```kotlin
repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.codeyousef:summon:0.2.6")
            }
        }

        // Platform-specific dependencies (optional)
        val jvmMain by getting {
            dependencies {
                implementation("io.github.codeyousef:summon-jvm:0.2.6")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("io.github.codeyousef:summon-js:0.2.6")
            }
        }
    }
}
```

### From GitHub Packages

If you prefer using GitHub Packages:

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/codeyousef/summon")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

// Dependencies same as above
```

**Note**: GitHub Packages requires authentication. Add your credentials to `~/.gradle/gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_TOKEN
```

Generate a GitHub token with `read:packages` permission at https://github.com/settings/tokens
