# Summon Framework Integrations

This directory contains integration packages that help you embed Summon pages in popular JVM backends.

## Available Integrations

### Quarkus Integration

Key helpers:

- `RoutingContext.respondSummonHydrated` for one-line hydrated SSR responses
- `Router.summonRouter` to mount Summon's file-based router within Vert.x/Quarkus
- Qute and HTMX extensions in `code.yousef.summon.integration.quarkus`

Setup example:

```kotlin
dependencies {
    implementation("io.github.codeyousef:summon:$summonVersion")
    implementation("io.github.codeyousef:summon-quarkus:$summonVersion")

    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-vertx-web")
    implementation("io.quarkus:quarkus-kotlin")
}
```

Hydrated response helper:

```kotlin
@Route(path = "/hydrated")
fun hydrated(context: RoutingContext) {
    context.respondSummonHydrated {
        HomePage()
    }
}
```

Mount the file-based router when the application starts:

```kotlin
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import io.vertx.ext.web.Router

@ApplicationScoped
class SummonRoutes {

    @Inject
    lateinit var router: Router

    @PostConstruct
    fun install() {
        router.summonRouter(basePath = "/pages")
    }
}
```

Refer to `integration/quarkus/README.md` for Qute usage, HTMX helpers, and advanced configuration.

### Ktor Integration

Key helpers:

- `ApplicationCall.respondSummonHydrated` for hydrated SSR responses
- `Routing.summonRouter` to expose Summon's file-based router
- `KtorRenderer` utilities for HTML or streaming responses

Dependencies:

```kotlin
dependencies {
    implementation("io.ktor:ktor-server-core:3.3.1")
    implementation("io.ktor:ktor-server-netty:3.3.1")
    implementation("io.github.codeyousef:summon:$summonVersion")
    implementation("io.github.codeyousef:summon-ktor:$summonVersion")
}
```

Usage:

```kotlin
fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondSummonHydrated { HomePage() }
        }

        summonRouter(basePath = "/pages")
    }
}
```

See `integration/ktor/README.md` for streaming, custom templates, and hybrid HTML examples.

### Spring Boot Integration

Key helpers:

- `SpringBootRenderer.renderHydrated` / `renderHydratedToResponse` for Spring MVC
- `WebFluxRenderer.renderHydrated` for reactive handlers
- `WebFluxSupport.summonRouter` to expose Summon's file-based router
- Thymeleaf extensions and optional auto configuration

Dependencies:

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    implementation("io.github.codeyousef:summon:$summonVersion")
    implementation("io.github.codeyousef:summon-spring-boot:$summonVersion")
}
```

Spring MVC example:

```kotlin
@Controller
class MyController {

    private val renderer = SpringBootRenderer()

    @GetMapping("/")
    fun home(response: HttpServletResponse) {
        renderer.renderHydratedToResponse(response) {
            HomePage()
        }
    }
}
```

WebFlux router bridge:

```kotlin
@Configuration
class Routes {
    @Bean
    fun summonRoutes(): RouterFunction<ServerResponse> =
        WebFluxSupport.summonRouter(basePath = "/pages")
}
```

For more detail, see `integration/springboot/README.md`.
