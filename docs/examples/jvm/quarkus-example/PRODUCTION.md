# Using Summon with Quarkus in Production

This guide provides best practices for using Summon with Quarkus in a production environment.

## Performance Optimization

### 1. Component Caching

For better performance, consider caching rendered components that don't change frequently:

```kotlin
@ApplicationScoped
class ComponentCache {
    private val cache = ConcurrentHashMap<String, String>()
    
    fun getOrRender(key: String, ttlSeconds: Long = 300, render: () -> String): String {
        val cachedValue = cache[key]
        if (cachedValue != null) {
            return cachedValue
        }
        
        val rendered = render()
        cache[key] = rendered
        
        // Schedule cache invalidation
        GlobalScope.launch {
            delay(ttlSeconds * 1000)
            cache.remove(key)
        }
        
        return rendered
    }
}
```

### 2. Server-Side Rendering Optimization

Optimize the server-side rendering by using Quarkus' reactive programming model:

```kotlin
@GET
@Path("/dashboard")
@Produces(MediaType.TEXT_HTML)
fun dashboard(@Context request: HttpServerRequest): Uni<String> {
    return Uni.createFrom().item { 
        summonRenderer.render {
            DashboardComponent(userId = request.getParam("userId"))
        }
    }
}
```

## Security Considerations

### 1. Input Validation

Always validate input that will be rendered in Summon components:

```kotlin
@Composable
fun UserProfileComponent(userId: String) {
    // Validate userId before using it
    if (!userId.matches(Regex("^[a-zA-Z0-9]+$"))) {
        Text("Invalid user ID")
        return
    }
    
    // Proceed with rendering
}
```

### 2. XSS Protection

Prevent cross-site scripting by escaping user input:

```kotlin
@Composable
fun CommentComponent(comment: String) {
    Text(
        text = HtmlEscapers.htmlEscaper().escape(comment),
        modifier = Modifier().padding("1rem")
    )
}
```

## Deployment

### 1. Native Image Support

For faster startup and lower memory usage, build a native image:

```bash
./gradlew build -Dquarkus.package.type=native
```

Add the following to your application.properties to optimize for Summon components:

```properties
quarkus.native.resources.includes=META-INF/resources/**
quarkus.native.additional-build-args=-H:ReflectionConfigurationFiles=reflection-config.json
```

Create a reflection-config.json file with your Summon components:

```json
[
  {
    "name": "code.yousef.example.quarkus.components.WelcomeComponent",
    "allDeclaredConstructors": true,
    "allPublicConstructors": true,
    "allDeclaredMethods": true,
    "allPublicMethods": true
  }
]
```

### 2. Container Deployment

Create a Dockerfile for your application:

```dockerfile
FROM registry.access.redhat.com/ubi8/ubi-minimal:8.4
WORKDIR /work/
COPY target/*-runner /work/application
RUN chmod 775 /work
EXPOSE 8080
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
```

## Monitoring

### 1. Add Metrics

Add MicroProfile Metrics to monitor component rendering performance:

```kotlin
@ApplicationScoped
class SummonMetrics {
    @Inject
    private lateinit var metrics: MetricRegistry
    
    fun timeComponentRendering(componentName: String, renderFunction: () -> String): String {
        val timer = metrics.timer("summon.render.$componentName")
        val context = timer.time()
        try {
            return renderFunction()
        } finally {
            context.stop()
        }
    }
}
```

### 2. Add Health Checks

Add a health check for your Summon renderer:

```kotlin
@ApplicationScoped
class SummonRendererHealthCheck : HealthCheck {
    @Inject
    private lateinit var summonRenderer: SummonRenderer
    
    override fun call(): HealthCheckResponse {
        try {
            // Try rendering a simple component
            summonRenderer.render {
                Text("Health check")
            }
            return HealthCheckResponse.up("summon-renderer")
        } catch (e: Exception) {
            return HealthCheckResponse.down("summon-renderer")
        }
    }
}
```

## Further Reading

- [Quarkus Performance Guide](https://quarkus.io/guides/performance-guide)
- [Quarkus Security Guide](https://quarkus.io/guides/security-guide)
- [Quarkus Native Applications](https://quarkus.io/guides/building-native-image)
- [Quarkus Monitoring](https://quarkus.io/guides/microprofile-metrics) 