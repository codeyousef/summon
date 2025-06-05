# Summon Quarkus Integration Example

This example demonstrates how to use the Summon UI library with Quarkus. It showcases the integration between these technologies to build a powerful, reactive web application using Kotlin.

## How to Run This Example

Before running this example, configure GitHub Packages authentication in `~/.gradle/gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_TOKEN
```

To properly set up and run this example, you need to:

1. Create a new Quarkus project using the Quarkus CLI or Maven:

```bash
# Using Quarkus CLI
quarkus create app code.yousef.example:quarkus-example:1.0.0-SNAPSHOT \
  --extension=resteasy-reactive,qute,kotlin,resteasy-reactive-jackson \
  --no-code

# Or using Maven
mvn io.quarkus.platform:quarkus-maven-plugin:3.8.3:create \
  -DprojectGroupId=code.yousef.example \
  -DprojectArtifactId=quarkus-example \
  -DprojectVersion=1.0.0-SNAPSHOT \
  -DclassName="code.yousef.example.quarkus.HelloResource" \
  -Dextensions="resteasy-reactive,qute,kotlin,resteasy-reactive-jackson"
```

2. Add Summon dependencies to your project's build file:

```kotlin
// In build.gradle.kts
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
    // Existing Quarkus dependencies...

    // Summon dependencies
    implementation("io.github.codeyousef:summon:0.2.6")

    // For HTML generation
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.12.0")
}
```

3. Copy the example files from this directory to your project
4. Run the Quarkus application in development mode:

```bash
# Using Gradle
./gradlew quarkusDev

# Using Maven
./mvnw quarkus:dev
```

5. Open your browser and navigate to `http://localhost:8080`

> **Note:** The current Quarkus integration has some known issues with route handling. We recommend using absolute paths for all route definitions to ensure consistent navigation. This issue is being investigated and will be fixed in a future release.

## Features Demonstrated

- Summon components integration with Quarkus
- Qute template integration with Summon components
- Reactive UI with state management
- Form handling and validation
- Data tables and dynamic content
- Styling using Summon's modifier system

## Project Structure

The example project consists of:

```
src/
├── main/
│   ├── kotlin/
│   │   └── code/
│   │       └── yousef/
│   │           └── example/
│   │               └── quarkus/
│   │                   ├── SummonQuarkusApplication.kt    # Main application class
│   │                   ├── SummonQuteExtension.kt         # Qute integration
│   │                   ├── SummonComponents.kt            # UI components
│   │                   ├── UserService.kt                 # Service layer
│   │                   └── WebResource.kt                 # Web resources/controllers
│   └── resources/
│       ├── application.properties                        # Quarkus config
│       └── templates/
│           ├── base.html                                 # Base template
│           └── index.html                                # Home page template
```

## Components Used

1. **WelcomeComponent** - A personalized greeting with a call-to-action button
2. **FeatureCardsComponent** - Display of key features with icons and descriptions
3. **CounterComponent** - Interactive counter with state management
4. **UserTableComponent** - Data table showing users from a backend service
5. **ContactFormComponent** - Form handling with validation and state feedback

## Key Integration Points

### 1. Summon Component Registry

The `SummonComponentRegistry` class provides a way to register components that can be used in Qute templates:

```kotlin
componentRegistry.register("welcome") { props ->
    val name = props["name"] as? String ?: "Guest"
    {
        WelcomeComponent(name)
    }
}
```

### 2. Qute Template Extension

The `SummonComponentTemplateExtension` allows components to be rendered from Qute templates:

```html
<div id="welcome">
    {summonComponent:render('welcome', name=userName)}
</div>
```

### 3. State Management

Summon's state management works seamlessly with Quarkus server-side rendering:

```kotlin
@Composable
fun CounterComponent(initialCount: Int = 0) {
    val count = remember { mutableStateOf(initialCount) }

    // Component UI that uses and updates state
}
```

## Next Steps

- Add WebSocket support for real-time UI updates
- Implement server-side validation for forms
- Add routing for a multi-page application
- Implement authentication and authorization

## Related Documentation

- [Summon Documentation](../../README.md)
- [Quarkus Documentation](https://quarkus.io/guides/) 
