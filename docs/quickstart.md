# Quickstart Guide

Get started with Summon in just a few minutes! This guide will help you set up your first Summon project.

## Prerequisites

- Kotlin 1.9+ or 2.0+
- Gradle 8.0+
- JDK 17+

## Configure GitHub Packages Authentication

First, configure authentication to access Summon from GitHub Packages. Add to `~/.gradle/gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_TOKEN
```

Note: You need a GitHub token with `read:packages` permission. You can create one at https://github.com/settings/tokens

## Create a New Project

### Option 1: Kotlin Multiplatform Project

```kotlin
// build.gradle.kts
plugins {
    kotlin("multiplatform") version "2.2.0-RC2"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/codeyousef/summon")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    
    jvm()
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.codeyousef:summon:0.2.6")
            }
        }
    }
}
```

### Option 2: JVM-Only Project

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "2.2.0-RC2"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/codeyousef/summon")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("io.github.codeyousef:summon-jvm:0.2.6")
}
```

### Option 3: JS-Only Project

```kotlin
// build.gradle.kts
plugins {
    kotlin("js") version "2.2.0-RC2"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/codeyousef/summon")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
}

dependencies {
    implementation("io.github.codeyousef:summon-js:0.2.6")
}
```

## Your First Summon Application

### 1. Create the Main File

For **JS** projects, create `src/jsMain/kotlin/App.kt`:

```kotlin
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.core.renderComposable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.padding
import code.yousef.summon.modifier.gap
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember
import kotlinx.browser.document

@Composable
fun App() {
    val count = remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .padding("20px")
            .gap("10px")
    ) {
        Text("Hello, Summon! 🎉")
        Text("Count: ${count.value}")
        
        Button(
            onClick = { count.value++ }
        ) {
            Text("Click me!")
        }
    }
}

fun main() {
    val root = document.getElementById("root")
        ?: error("Root element not found")
    
    renderComposable(root) {
        App()
    }
}
```

### 2. Create the HTML File

Create `src/jsMain/resources/index.html`:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Summon App</title>
</head>
<body>
    <div id="root"></div>
    <script src="your-app-name.js"></script>
</body>
</html>
```

### 3. Run Your Application

```bash
# For JS projects
./gradlew jsBrowserDevelopmentRun

# For JVM projects (with Ktor example)
./gradlew run
```

## Using with Popular Frameworks

### Ktor Integration

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.github.codeyousef:summon-jvm:0.2.6")
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-html-builder:2.3.7")
}
```

```kotlin
// src/main/kotlin/Application.kt
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.display.Text
import code.yousef.summon.core.renderToString
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@Composable
fun HomePage() {
    Column {
        Text("Welcome to Summon with Ktor!")
    }
}

fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                val html = renderToString { HomePage() }
                call.respondText(html, ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}
```

## Common Patterns

### State Management

```kotlin
@Composable
fun Counter() {
    // Local state
    val count = remember { mutableStateOf(0) }
    
    Column {
        Text("Count: ${count.value}")
        Button(onClick = { count.value++ }) {
            Text("Increment")
        }
    }
}
```

### Styling with Modifiers

```kotlin
@Composable
fun StyledComponent() {
    Box(
        modifier = Modifier
            .width("200px")
            .height("100px")
            .backgroundColor("#f0f0f0")
            .borderRadius("8px")
            .padding("16px")
            .boxShadow("0 2px 4px rgba(0,0,0,0.1)")
    ) {
        Text("Styled content")
    }
}
```

### Lists and Loops

```kotlin
@Composable
fun ItemList() {
    val items = remember { mutableStateOf(listOf("Apple", "Banana", "Orange")) }
    
    LazyColumn {
        items(items.value) { item ->
            Card(modifier = Modifier.margin("8px")) {
                Text(item)
            }
        }
    }
}
```

## Using GitHub Packages (Alternative)

If you prefer GitHub Packages over Maven Central:

1. Generate a GitHub token with `read:packages` permission
2. Add to `~/.gradle/gradle.properties`:
   ```properties
   gpr.user=YOUR_GITHUB_USERNAME
   gpr.key=YOUR_GITHUB_TOKEN
   ```

3. Update your `build.gradle.kts`:
   ```kotlin
   repositories {
       mavenCentral()
       maven {
           url = uri("https://maven.pkg.github.com/codeyousef/summon")
           credentials {
               username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
               password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
           }
       }
   }
   ```

## Next Steps

- Explore the [Component Catalog](components.md)
- Learn about [State Management](state-management.md)
- Understand [Routing](routing.md)
- Check out [Integration Examples](integration-guides.md)

## Troubleshooting

### Can't find Summon dependency?

Make sure you're using:
- Version: `0.2.6`
- Group ID: `io.github.codeyousef`
- Repository: `mavenCentral()`

### Build errors?

Ensure you have:
- Kotlin 1.9+ (2.2.0-RC2 recommended)
- JDK 17+
- Gradle 8.0+

### Need help?

- Check the [documentation](README.md)
- Browse [examples](examples/)
- Report issues on [GitHub](https://github.com/codeyousef/summon/issues)