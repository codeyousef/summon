# Getting Started with Summon

Summon is a Kotlin Multiplatform library for building user interfaces that work seamlessly across JavaScript and JVM platforms. It provides a unified API for creating components that can be rendered in browsers, desktop applications, and server-side environments.

## Installation

Summon is currently in active development and will be published to Maven Central soon. Until then, you'll need to build it locally.

### Local Development Setup

#### 1. Clone the Repository

```bash
git clone https://github.com/yebaital/summon.git
cd summon
```

#### 2. Build and Install to Local Maven Repository

```bash
./gradlew publishToMavenLocal
```

#### 3. Add the Local Repository to Your Project

In your project's `settings.gradle.kts` file:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        // other repositories
    }
}
```

#### 4. Include the Dependency in Your Project

```kotlin
// build.gradle.kts
kotlin {
    jvm()
    js(IR) {
        browser()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("code.yousef:summon:0.1.0-SNAPSHOT")
            }
        }
        
        val jvmMain by getting {
            dependencies {
                implementation("code.yousef:summon-jvm:0.1.0-SNAPSHOT")
            }
        }
        
        val jsMain by getting {
            dependencies {
                implementation("code.yousef:summon-js:0.1.0-SNAPSHOT")
            }
        }
    }
}
```

> **Note:** Once Summon is published to Maven Central, you will be able to include it directly without the local setup steps.

## Your First Component

Let's create a simple reusable component with Summon:

```kotlin
import code.yousef.summon.core.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.extensions.px
import code.yousef.summon.extensions.rem

class HelloWorld : Composable {
    override fun <T> compose(receiver: T): T {
        return Column(
            content = listOf(
                Text(
                    text = "Hello, World!",
                    modifier = Modifier()
                        .fontSize(2.rem)
                        .color("#0077cc")
                        .marginBottom(16.px)
                ),
                Text(
                    text = "Welcome to Summon - A Kotlin Multiplatform UI library!",
                    modifier = Modifier()
                        .color("#333333")
                        .padding(8.px)
                )
            ),
            modifier = Modifier()
                .maxWidth(600.px)
                .padding(24.px)
                .margin("0 auto")
        ).compose(receiver)
    }
}
```

Notice the use of handy numeric extensions like `16.px` and `2.rem` - these are Summon's CSS unit extensions that make your styling code more concise and type-safe.

## Platform-Specific Setup

### Browser (JS)

To render your component in a browser:

```kotlin
// jsMain/kotlin/YourApp.kt
import code.yousef.summon.renderComposable
import kotlinx.browser.document

fun main() {
    val container = document.getElementById("app") ?: error("Container not found")
    renderComposable(container, HelloWorld())
}
```

### JVM (Desktop or Server)

To render your component on the JVM:

```kotlin
// jvmMain/kotlin/YourApp.kt
import code.yousef.summon.renderToString

fun main() {
    val html = renderToString(HelloWorld())
    println(html)
    // Or use the html string in your server response
}
```

## Next Steps

After getting started with Summon, you might want to explore:

- [Components](./components.md) - Learn about Summon's built-in UI components
- [Routing](./routing.md) - Set up navigation in your application
- [State Management](./state-management.md) - Manage application state effectively
- [Styling](./styling.md) - Apply styles to your components
- [Integration Guides](./integration-guides.md) - Integrate with existing frameworks 