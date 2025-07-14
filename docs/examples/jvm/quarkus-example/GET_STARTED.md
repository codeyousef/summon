# Getting Started with Summon and Quarkus

This guide will help you get started with using Summon in a Quarkus application. Summon is a Kotlin Multiplatform UI library that enables you to build user interfaces using a Compose-like syntax, and Quarkus is a Kubernetes-native Java framework tailored for GraalVM and HotSpot.

## What is Summon with Quarkus?

Summon with Quarkus combines the declarative UI building capabilities of Summon with the high-performance, low-footprint runtime of Quarkus. This integration allows you to:

- Build server-rendered UIs using a Compose-like syntax
- Create reactive web applications with minimal boilerplate
- Leverage Quarkus's fast startup time and low memory footprint
- Integrate with Qute templates for hybrid rendering approaches
- Use HTMX for dynamic client-side interactions without writing JavaScript
- Deploy to native images for even better performance

The Summon-Quarkus integration provides a modern approach to building web applications, combining the type-safety and expressiveness of Kotlin with the efficiency of Quarkus.

## Prerequisites

Before you begin, make sure you have the following installed:

- JDK 17 or later
- Kotlin 2.0.0 or later
- Gradle 8.0+ or Maven 3.8.1+
- Quarkus CLI (optional but recommended)
- Git (to clone the Summon repository)

## Setting Up a New Quarkus Project with Summon

### Step 1: Create a New Quarkus Project

First, create a new Quarkus project using either the Quarkus CLI or Maven:

```bash
# Using Quarkus CLI
quarkus create app org.example:summon-quarkus-app:1.0.0-SNAPSHOT \
  --extension=resteasy-reactive,qute,kotlin,resteasy-reactive-jackson \
  --no-code

# Or using Maven
mvn io.quarkus.platform:quarkus-maven-plugin:3.6.5:create \
  -DprojectGroupId=org.example \
  -DprojectArtifactId=summon-quarkus-app \
  -DprojectVersion=1.0.0-SNAPSHOT \
  -DclassName="org.example.HelloResource" \
  -Dextensions="resteasy-reactive,qute,kotlin,resteasy-reactive-jackson"
```

### Step 2: Add Summon Dependencies

Add the Summon dependencies to your Quarkus project.

For Gradle (`build.gradle.kts`):
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

dependencies {
    // Existing Quarkus dependencies...

    // Summon dependencies
    implementation("io.github.codeyousef:summon:0.2.8.7")
    implementation("io.github.codeyousef:summon-jvm:0.2.8.7")

    // For HTML generation
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.12.0")
}
```

For Maven (`pom.xml`):
```xml
<repositories>
    <repository>
        <id>local-maven-repo</id>
        <url>file://${user.home}/.m2/repository</url>
    </repository>
</repositories>

<dependencies>
    <!-- Existing dependencies... -->

    <!-- Summon Dependencies -->
    <dependency>
        <groupId>io.github.codeyousef</groupId>
        <artifactId>summon</artifactId>
        <version>0.2.8.7</version>
    </dependency>
    <dependency>
        <groupId>io.github.codeyousef</groupId>
        <artifactId>summon-jvm</artifactId>
        <version>0.2.8.7</version>
    </dependency>
    <dependency>
        <groupId>org.jetbrains.kotlinx</groupId>
        <artifactId>kotlinx-html-jvm</artifactId>
        <version>0.12.0</version>
    </dependency>
</dependencies>
```

### Step 3: Create a Basic Renderer

Create a `SummonRenderer.kt` file to handle rendering Summon components:

```kotlin
@ApplicationScoped
class SummonRenderer {
    @Inject
    private lateinit var summonRenderer: EnhancedQuarkusExtension.EnhancedSummonRenderer

    fun render(title: String = "Summon with Quarkus", content: @Composable () -> Unit): String {
        return summonRenderer.renderTemplate(title, summonRenderer.renderToString(content))
    }

    fun render(content: @Composable () -> Unit): String {
        return summonRenderer.renderToString(content)
    }
}
```

### Step 4: Create a Simple Resource

Create a simple resource to test your Summon integration:

```kotlin
@Path("/")
class HelloResource {
    @Inject
    lateinit var summonRenderer: SummonRenderer

    @GET
    @Produces(MediaType.TEXT_HTML)
    fun home(): String {
        return summonRenderer.render(title = "Hello Summon") {
            HelloComponent("Quarkus User")
        }
    }
}

@Composable
fun HelloComponent(name: String) {
    Column(modifier = Modifier().style("style", "padding: 2rem;")) {
        Text(
            text = "Hello, $name!",
            modifier = Modifier().style("style", "font-size: 2rem; font-weight: bold;")
        )
        Text(
            text = "Welcome to Summon with Quarkus",
            modifier = Modifier().style("style", "font-size: 1.2rem; color: #666;")
        )
    }
}
```

### Step 5: Run Your Application

Run your Quarkus application:

```bash
# For Gradle
./gradlew quarkusDev

# For Maven
./mvnw quarkus:dev
```

Visit `http://localhost:8080` in your browser to see your Summon component rendered.

## Integrating with HTMX

[HTMX](https://htmx.org/) allows you to access modern browser features directly from HTML, without writing JavaScript. Summon's Quarkus integration includes built-in support for HTMX.

### Step 1: Add HTMX to Your Project

Add the HTMX script to your page template by modifying the `renderTemplate` method in your `SummonRenderer` class:

```kotlin
fun renderTemplate(title: String, content: String): String {
    return """
        <!DOCTYPE html>
        <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>$title</title>
                <script src="https://unpkg.com/htmx.org@1.9.12"></script>
                <style>
                    body { 
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 1200px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                </style>
            </head>
            <body>
                <div id="app">
                    $content
                </div>
            </body>
        </html>
    """.trimIndent()
}
```

### Step 2: Create HTMX-Enabled Components

Create components that use HTMX attributes:

```kotlin
@Composable
fun CounterComponent(initialValue: Int = 0) {
    Column(modifier = Modifier().style("style", "text-align: center; padding: 2rem;")) {
        Text(
            text = "Interactive Counter",
            modifier = Modifier().style("style", "font-size: 1.5rem; font-weight: bold;")
        )

        Row(modifier = Modifier().style("style", "display: flex; align-items: center; justify-content: center; gap: 1rem; margin: 1rem 0;")) {
            Button(
                label = "-",
                onClick = { /* HTMX will handle this */ },
                modifier = Modifier()
                    .style("style", "min-width: 40px;")
                    .style("hx-post", "/api/counter/decrement")
                    .style("hx-target", "#counter-value")
                    .style("hx-swap", "innerHTML")
            )

            Text(
                text = "$initialValue",
                modifier = Modifier()
                    .style("id", "counter-value")
                    .style("style", "font-size: 2rem; font-weight: bold; min-width: 40px; text-align: center;")
            )

            Button(
                label = "+",
                onClick = { /* HTMX will handle this */ },
                modifier = Modifier()
                    .style("style", "min-width: 40px;")
                    .style("hx-post", "/api/counter/increment")
                    .style("hx-target", "#counter-value")
                    .style("hx-swap", "innerHTML")
            )
        }
    }
}
```

### Step 3: Create API Endpoints for HTMX

Create endpoints that HTMX will call:

```kotlin
@POST
@Path("/api/counter/increment")
@Produces(MediaType.TEXT_PLAIN)
fun incrementCounter(): String {
    val newValue = counter.incrementAndGet()
    return newValue.toString()
}

@POST
@Path("/api/counter/decrement")
@Produces(MediaType.TEXT_PLAIN)
fun decrementCounter(): String {
    val newValue = counter.decrementAndGet()
    return newValue.toString()
}
```

## Integrating with Qute Templates

Qute is Quarkus's templating engine. You can integrate Summon components with Qute templates for a hybrid rendering approach.

### Step 1: Create a Qute Template

Create a template in `src/main/resources/templates/index.html`:

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>{title}</title>
    <script src="https://unpkg.com/htmx.org@1.9.12"></script>
    <style>
        body { 
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
    </style>
</head>
<body>
    <header>
        <h1>{title}</h1>
    </header>

    <main>
        {#if summonContent}
            {summonContent|raw}
        {/if}
    </main>

    <footer>
        <p>© 2025 Summon with Quarkus Example</p>
    </footer>
</body>
</html>
```

### Step 2: Create a Qute Template Resource

Create a resource that uses the Qute template:

```kotlin
@Path("/qute")
class QuteResource {
    @Inject
    lateinit var summonRenderer: SummonRenderer

    @Location("index.html")
    @Inject
    lateinit var indexTemplate: Template

    @GET
    @Produces(MediaType.TEXT_HTML)
    fun index(): TemplateInstance {
        val summonContent = summonRenderer.render {
            WelcomeComponent("Qute User")
        }

        return indexTemplate
            .data("title", "Summon with Qute")
            .data("summonContent", summonContent)
    }
}

@Composable
fun WelcomeComponent(name: String) {
    Column {
        Text("Welcome, $name!")
        Text("This component is rendered by Summon and embedded in a Qute template.")
    }
}
```

## Using Qute Templates in Summon Components

You can also use Qute templates to render parts of your Summon components:

### Step 1: Create a Qute Template for a Component

Create a template in `src/main/resources/templates/hero-component.html`:

```html
<div style="text-align: center; padding: 4rem 2rem; background: linear-gradient(135deg, #4695EB 0%, #2A5298 100%); color: white; border-radius: 10px; margin-bottom: 2rem;">
  <div style="align-items: center;">
    <span style="font-size: 2.5rem; margin-bottom: 1rem; font-weight: bold; display: block;">Welcome to Summon with Quarkus</span>
    <span style="font-size: 1.25rem; margin-bottom: 2rem; max-width: 800px; margin-left: auto; margin-right: auto; display: block;">
      Hello, {username}! This example showcases how to integrate Summon with Quarkus to build amazing web applications.
    </span>
    <div style="position: relative; display: inline-block;">
      <button class="btn" id="get-started-btn" 
              style="background: white; color: #4695EB; font-weight: 600; padding: 12px 24px; font-size: 1.1rem; min-width: 150px;"
              hx-get="/dashboard" 
              hx-target="body" 
              hx-swap="innerHTML">
        Get Started
      </button>
    </div>
  </div>
</div>
```

### Step 2: Create a Component Class to Use the Template

```kotlin
@ApplicationScoped
class QuteComponents {
    @Location("hero-component.html")
    @Inject
    lateinit var heroTemplate: Template
}

@Composable
fun QuteHeroComponent(username: String, quteComponents: QuteComponents) {
    QuteTemplate(
        template = quteComponents.heroTemplate,
        modifier = Modifier()
            .style("id", "hero-component-container")
            .style("data-component", "hero"),
        "username" to username
    )
}
```

### Step 3: Use the Component in Your Resource

```kotlin
@GET
@Path("/dashboard")
@Produces(MediaType.TEXT_HTML)
fun dashboard(): String {
    return summonRenderer.render(title = "Dashboard") {
        QuteHeroComponent("Dashboard User", quteComponents)
    }
}
```

## Common Patterns and Best Practices

### 1. Component Organization

Organize your components by feature or domain:

```
src/
└── main/
    └── kotlin/
        └── org/
            └── example/
                ├── components/
                │   ├── common/
                │   │   ├── HeaderComponent.kt
                │   │   └── FooterComponent.kt
                │   ├── dashboard/
                │   │   ├── DashboardComponent.kt
                │   │   └── StatisticsComponent.kt
                │   └── users/
                │       ├── UserListComponent.kt
                │       └── UserFormComponent.kt
                ├── resources/
                │   ├── DashboardResource.kt
                │   └── UserResource.kt
                └── services/
                    └── UserService.kt
```

### 2. State Management

Use Summon's state management for component-level state:

```kotlin
@Composable
fun StatefulComponent() {
    val count = remember { mutableStateOf(0) }

    Column {
        Text("Count: ${count.value}")
        Button(
            label = "Increment",
            onClick = { count.value++ }
        )
    }
}
```

For application-level state, use CDI beans:

```kotlin
@ApplicationScoped
class AppState {
    private val _themeState = MutableStateFlow("light")
    val themeState: StateFlow<String> = _themeState

    fun setTheme(theme: String) {
        _themeState.value = theme
    }
}
```

### 3. Reusable Layouts

Create reusable layout components:

```kotlin
@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) {
    Column {
        HeaderComponent(title)
        Box(modifier = Modifier().style("style", "padding: 1rem;")) {
            content()
        }
        FooterComponent()
    }
}
```

### 4. Error Handling

Implement proper error handling in your components:

```kotlin
@Composable
fun UserProfileComponent(userId: String?) {
    if (userId == null) {
        ErrorComponent("User ID is required")
        return
    }

    try {
        val user = userService.getUserById(userId)
        if (user == null) {
            ErrorComponent("User not found")
            return
        }

        // Render user profile
        UserDetails(user)
    } catch (e: Exception) {
        ErrorComponent("Error loading user: ${e.message}")
    }
}
```

### 5. Performance Optimization

For better performance, consider:

- Caching rendered components that don't change frequently
- Using lazy loading for components that are not immediately visible
- Minimizing the size of the rendered HTML by avoiding unnecessary nesting
- Using HTMX for partial updates instead of full page reloads

## Conclusion

This guide has covered the basics of using Summon with Quarkus, including:

- Setting up a new Quarkus project with Summon
- Creating basic Summon components
- Integrating with HTMX for dynamic interactions
- Using Qute templates with Summon components
- Common patterns and best practices

For more advanced topics, refer to the [PRODUCTION.md](PRODUCTION.md) guide, which covers performance optimization, security considerations, deployment, and monitoring.

For a complete example application, check out the source code in the [Summon repository](https://github.com/yebaital/summon/tree/main/docs/examples/jvm/quarkus-example).
