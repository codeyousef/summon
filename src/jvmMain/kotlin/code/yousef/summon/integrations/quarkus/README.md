# Summon Quarkus Integration

This module provides full integration between the Summon UI library and Quarkus framework. It enables you to use Summon components within Quarkus applications, including support for Qute templates, RESTEasy, CDI, and GraalVM native images.

## Features

1. **SummonQuteExtension**: Integration with Qute templates
2. **QuteTagExtension**: Custom tags for Qute templates
3. **QuarkusExtension**: Dedicated Quarkus extension
4. **ResteasyIntegration**: Integration with RESTEasy
5. **CDISupport**: Injection of beans in components
6. **NativeImageSupport**: Support for GraalVM native image

## Getting Started

### 1. Add Summon Quarkus Dependencies

In your Quarkus application's `build.gradle.kts` file:

```kotlin
dependencies {
    // Quarkus dependencies...
    
    // Summon core
    implementation("code.yousef:summon:0.1.0")
    
    // Summon Quarkus integration
    implementation("code.yousef:summon-quarkus:0.1.0")
}
```

### 2. Create Summon Components

Create your Summon components as usual:

```kotlin
class GreetingComponent(private val name: String) : Composable {
    override fun <T> compose(receiver: T): T {
        // Component implementation
    }
}
```

### 3. Use Components in REST Endpoints

```kotlin
@Path("/hello")
class HelloResource {
    
    @Inject
    lateinit var summonRenderer: QuarkusExtension.SummonRenderer
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun hello(@QueryParam("name") name: String): String {
        val component = GreetingComponent(name ?: "World")
        return summonRenderer.render(component)
    }
    
    // Alternative using Response
    @GET
    @Path("/alt")
    @Produces(MediaType.TEXT_HTML)
    fun helloAlt(@QueryParam("name") name: String): Response {
        val component = GreetingComponent(name ?: "World")
        return ResteasyIntegration.createResponse(component)
    }
    
    // Or return the component directly
    @GET
    @Path("/direct")
    @Produces(MediaType.TEXT_HTML)
    fun helloDirect(@QueryParam("name") name: String): Composable {
        return GreetingComponent(name ?: "World")
    }
}
```

### 4. Use Components in Qute Templates

First, register the Summon Qute extension:

```kotlin
@Singleton
class QuteConfig {
    @Produces
    fun configureSummonQuteExtension(): Consumer<EngineBuilder> {
        return SummonQuteExtension()
    }
}
```

Then, in your Qute template:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Summon + Qute</title>
</head>
<body>
    <!-- Option 1: Using the summon namespace -->
    {#let greeting=summon:component(component)}
        {greeting}
    {/let}
    
    <!-- Option 2: Using extension methods -->
    <div class="container">
        {component.render}
    </div>
</body>
</html>
```

And in your resource:

```kotlin
@Inject
lateinit var templates: Engine

@GET
@Path("/template")
@Produces(MediaType.TEXT_HTML)
fun template(@QueryParam("name") name: String): String {
    val component = GreetingComponent(name ?: "World")
    
    return templates.getTemplate("hello.html")
        .data("component", component)
        .render()
}
```

### 5. Using CDI in Components

```kotlin
@ApplicationScoped
class UserService {
    fun getCurrentUser(): String {
        return "John Doe"
    }
}

class UserProfileComponent : Composable {
    @Inject
    lateinit var userService: UserService
    
    override fun <T> compose(receiver: T): T {
        val userName = userService.getCurrentUser()
        // Use the userName in your component
    }
}

// When creating the component, use CDISupport:
val component = CDISupport.create(UserProfileComponent::class)
```

### 6. Native Image Support

For GraalVM native image compilation, annotate your components:

```kotlin
@NativeImageSupport.ReflectiveComponent
class DashboardComponent : Composable {
    // ...
}
```

Ensure your Quarkus application has the proper reflection configuration:

```kotlin
@BuildStep
fun registerForReflection(producer: BuildProducer<ReflectiveClassBuildItem>) {
    // Register your Summon components here
    producer.produce(
        ReflectiveClassBuildItem.builder(
            "com.example.DashboardComponent"
        ).methods(true).fields(true).build()
    )
}
```

## Complete Example

See the `QuarkusDemo.kt` file for a complete example of using Summon with Quarkus.

## Advanced Usage

### Custom Rendering Options

```kotlin
@ApplicationScoped
class CustomRenderer {
    @Inject
    lateinit var summonRenderer: QuarkusExtension.SummonRenderer
    
    fun renderWithCustomOptions(component: Composable): String {
        return summonRenderer.render(component, prettyPrint = true)
    }
}
```

### CDI-Aware Rendering

```kotlin
@ApplicationScoped
class CDIComponentRenderer {
    @Inject
    lateinit var cdiAwareRenderer: CDISupport.CDIAwareRenderer
    
    fun render(component: Composable): String {
        // This will automatically inject any CDI dependencies before rendering
        return cdiAwareRenderer.render(component)
    }
}
```

### Programmatic Native Image Configuration

```kotlin
fun generateNativeImageConfig() {
    val registry = NativeImageSupport.ModuleRegistry()
    
    // Register all your components
    registry.register(HomeComponent::class.java)
    registry.register(UserProfileComponent::class.java)
    registry.register(DashboardComponent::class.java)
    
    // Generate and write the configuration
    val config = registry.generateReflectionConfig()
    File("reflection-config.json").writeText(config)
}
``` 