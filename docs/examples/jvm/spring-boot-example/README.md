# Summon Spring Boot Integration Example

This example demonstrates how to use the Summon UI library with Spring Boot and Thymeleaf. It showcases the integration between these technologies to build a powerful, reactive web application using Kotlin.

## How to Run This Example

Before running this example, configure GitHub Packages authentication in `~/.gradle/gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_TOKEN
```

To properly set up and run this example, you need to:

1. **Prerequisites**
   - Java 17 or higher
   - Kotlin 2.1.20 or higher
   - Gradle 8.x or higher
   - GitHub Packages access for Summon library

2. **Clone and Setup**
   ```bash
   cd docs/examples/jvm/spring-boot-example
   ./gradlew build
   ```

3. **Run the Application**
   ```bash
   ./gradlew bootRun
   ```

4. **Access the Application**
   - Open your browser and navigate to `http://localhost:8083`
   - The application will be running on port 8083 (to avoid conflicts with other examples)

## Features Demonstrated

- **Summon Components Integration**: Seamless integration of Summon UI components with Spring Boot MVC
- **Thymeleaf Templates**: Server-side rendering with Thymeleaf template engine
- **Reactive UI Elements**: Interactive counters, live time updates, and dynamic content
- **REST API Integration**: AJAX endpoints for dynamic content updates
- **User Management**: Full CRUD operations with form handling and validation
- **Real-time Chat**: WebSocket implementation for real-time communication
- **Responsive Design**: Bootstrap-based responsive UI design
- **State Management**: Server-side and client-side state management

## Project Structure

The example project consists of:

```
src/
├── main/
│   ├── kotlin/
│   │   └── code/
│   │       └── yousef/
│   │           └── example/
│   │               └── springboot/
│   │                   ├── SpringBootExampleApplication.kt     # Main application class
│   │                   ├── SummonThymeleafIntegration.kt      # Summon-Thymeleaf integration
│   │                   ├── WebController.kt                   # MVC controllers
│   │                   ├── ApiController.kt                   # REST API endpoints
│   │                   ├── SummonComponents.kt                # UI components
│   │                   ├── UserService.kt                     # Service layer
│   │                   ├── User.kt                            # Data models
│   │                   └── ChatWebSocketHandler.kt            # WebSocket chat handler
│   └── resources/
│       ├── application.properties                             # Spring Boot config
│       ├── templates/
│       │   ├── layout.html                                    # Base template
│       │   ├── index.html                                     # Home page
│       │   ├── users.html                                     # User management
│       │   ├── dashboard.html                                 # Statistics dashboard
│       │   ├── contact.html                                   # Contact form
│       │   └── chat.html                                      # Real-time chat
│       └── static/
│           └── app.js                                         # Client-side JavaScript
```

## Components Used

### 1. **Summon UI Components**
- `HeroComponent` - Welcome section with call-to-action
- `FeatureCardsComponent` - Feature showcase with icons
- `CounterComponent` - Interactive counter with state management
- `UserTableComponent` - Data table for user management
- `ContactFormComponent` - Form handling with validation
- `DashboardComponent` - Statistics and activity display
- `CurrentTimeComponent` - Live time updates

### 2. **Spring Boot Features**
- **Spring MVC**: Controllers for page routing and form handling
- **Spring WebSocket**: Real-time chat functionality
- **Thymeleaf**: Server-side template rendering
- **Spring Boot DevTools**: Hot reload during development
- **REST API**: JSON endpoints for AJAX interactions

### 3. **Frontend Technologies**
- **Bootstrap 5**: Responsive CSS framework
- **JavaScript**: Client-side interactions and API calls
- **WebSocket**: Real-time communication
- **Thymeleaf Templates**: Server-side rendering

## Key Integration Points

### 1. Summon-Thymeleaf Integration

The `SummonThymeleafIntegration` class provides seamless integration between Summon components and Thymeleaf templates:

```kotlin
@Component
class SummonThymeleafRenderer {
    fun renderComponent(content: @Composable () -> Unit): String {
        val renderer = ServerPlatformRenderer()
        val composer = Composer(renderer)
        composer.compose(content)
        return renderer.render()
    }
}
```

### 2. Custom Thymeleaf Dialect

The `SummonDialect` allows components to be used directly in Thymeleaf templates:

```html
<div summon:component="heroComponent">
    <!-- Summon component rendered here -->
</div>
```

### 3. MVC Controller Integration

Controllers integrate Summon components with Spring MVC:

```kotlin
@GetMapping("/")
fun home(model: Model): String {
    val components = mapOf(
        "hero" to { HeroComponent("Spring Boot User") },
        "features" to { FeatureCardsComponent() }
    )
    model.addAttribute("summonComponents", components)
    return "index"
}
```

### 4. Real-time Features

- **Live Time Updates**: Server time displayed and updated every second
- **Interactive Counter**: State-managed counter with increment/decrement
- **WebSocket Chat**: Real-time messaging between users
- **Dynamic User Management**: AJAX-powered CRUD operations

## API Endpoints

### REST Endpoints
- `GET /api/time` - Current server time
- `GET /api/users` - List all users
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `POST /api/counter/increment` - Increment counter
- `POST /api/counter/decrement` - Decrement counter
- `GET /api/health` - Application health check

### WebSocket Endpoints
- `ws://localhost:8083/chat` - Real-time chat connection

## Configuration

### Application Properties
Key configuration options in `application.properties`:

```properties
# Server Configuration
server.port=8083

# Thymeleaf Configuration
spring.thymeleaf.cache=false
spring.thymeleaf.mode=HTML

# Development Tools
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true

# Logging
logging.level.code.yousef.example.springboot=DEBUG
```

### Dependencies
Key dependencies in `build.gradle.kts`:

```kotlin
dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    
    // Summon UI Library
    implementation("io.github.codeyousef:summon:0.2.8.6")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.12.0")
    
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
```

## Development Workflow

### 1. Running in Development Mode
```bash
./gradlew bootRun
```
- Automatic restart on code changes
- Live reload in browser
- Debug logging enabled

### 2. Building for Production
```bash
./gradlew build
java -jar build/libs/spring-boot-example-0.0.1-SNAPSHOT.jar
```

### 3. Testing
```bash
./gradlew test
```

## Comparison with Quarkus Example

| Feature | Spring Boot Example | Quarkus Example |
|---------|-------------------|----------------|
| Framework | Spring Boot 3.4.x | Quarkus 3.6.x |
| Template Engine | Thymeleaf | Qute |
| Dependency Injection | Spring | CDI |
| WebSocket | Spring WebSocket | Jakarta WebSocket |
| Configuration | application.properties | application.properties |
| Hot Reload | Spring DevTools | Quarkus Dev Mode |
| Port | 8083 | 8082 |

## Extending the Example

### Adding New Components
1. Create Summon composable in `SummonComponents.kt`
2. Add controller method to expose component
3. Create or update Thymeleaf template
4. Add API endpoints if needed

### Adding New Pages
1. Create controller method in `WebController.kt`
2. Create Thymeleaf template in `templates/`
3. Add navigation link in layout template
4. Update component integration

### Adding Real-time Features
1. Extend `ChatWebSocketHandler.kt` for new WebSocket functionality
2. Add client-side JavaScript for WebSocket communication
3. Update templates with real-time UI elements

## Troubleshooting

### Common Issues

1. **GitHub Packages Authentication**
   - Ensure `gpr.user` and `gpr.key` are set in `~/.gradle/gradle.properties`
   - Verify GitHub token has `read:packages` permission

2. **Port Conflicts**
   - Default port is 8083, change in `application.properties` if needed
   - Check for other services running on the same port

3. **Template Rendering Issues**
   - Verify Summon components are properly registered
   - Check Thymeleaf cache settings in development
   - Review console logs for rendering errors

4. **WebSocket Connection Issues**
   - Ensure WebSocket endpoint is accessible
   - Check browser console for connection errors
   - Verify CORS settings for cross-origin requests

### Getting Help

- Check the main Summon documentation
- Review the Quarkus example for comparison
- Submit issues to the Summon GitHub repository

## Next Steps

- Implement user authentication and authorization
- Add database persistence with Spring Data JPA
- Integrate with external APIs
- Add comprehensive testing suite
- Deploy to cloud platforms (AWS, Azure, GCP)
- Add monitoring and observability features

## Related Documentation

- [Summon Documentation](../../README.md)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Thymeleaf Documentation](https://www.thymeleaf.org/)
- [Quarkus Example](../quarkus-example/README.md)