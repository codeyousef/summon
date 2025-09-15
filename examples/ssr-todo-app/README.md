# Summon SSR Todo App Example

A complete server-side rendered todo application built with the Summon Kotlin Multiplatform UI framework. This example demonstrates pure SSR without any client-side JavaScript, showcasing how to build SEO-friendly, fast-loading web applications with Summon.

## Features

- **Pure Server-Side Rendering**: No client-side JavaScript required
- **Form-based Interactions**: Traditional HTML forms with server-side processing  
- **In-memory State Management**: Simple CRUD operations with server-side storage
- **Component Architecture**: Reusable UI components built with Summon
- **SEO Optimized**: Proper HTML structure with meta tags
- **Responsive Design**: Mobile-friendly layout
- **Type-safe Styling**: Leverages Summon's modifier system for styling

## Architecture Overview

The application follows a clean architecture pattern:

```
src/main/kotlin/code/yousef/example/todo/
├── TodoApp.kt              # Main application entry point
├── models/
│   └── Todo.kt            # Data model for todo items
├── components/
│   ├── Layout.kt          # Base layout with SEO metadata
│   ├── TodoForm.kt        # Form for adding new todos
│   ├── TodoItem.kt        # Individual todo item component
│   └── TodoList.kt        # List container for all todos
└── routes/
    └── TodoRoutes.kt      # HTTP routes and business logic
```

## Key Concepts Demonstrated

### 1. Server-Side Rendering with Summon

```kotlin
@Composable
fun TodoPage(todos: List<Todo>) {
    Layout(
        title = "Summon SSR Todo App",
        description = "A server-side rendered todo application"
    ) {
        Column {
            TodoForm()
            TodoList(todos = todos)
        }
    }
}
```

### 2. Component Composition

Components are built using Summon's declarative syntax, similar to Jetpack Compose:

```kotlin
@Composable
fun TodoItem(todo: Todo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (todo.isCompleted) Color.Gray else Color.White)
            .padding(12)
    ) {
        Checkbox(checked = todo.isCompleted)
        Text(text = todo.text)
        Button(text = "Delete")
    }
}
```

### 3. Form-based State Management

Traditional HTML forms handle all user interactions:

```kotlin
// Add todo route
post("/add") {
    val params = call.receiveParameters()
    val todoText = params["todoText"]?.trim()
    
    if (!todoText.isNullOrBlank()) {
        TodoStorage.addTodo(todoText)
    }
    
    call.respondRedirect("/")
}
```

### 4. SEO Optimization

The application generates proper HTML with meta tags:

```kotlin
ServerSideRenderUtils.renderPageToString(
    rootComposable = { TodoPage(todos = todos) },
    includeHydrationScript = false // Pure SSR
)
```

## Running the Application

### Prerequisites

- Java 17 or later
- Kotlin 2.2.0-Beta1 or compatible version
- Summon framework 0.3.1.0

### Local Development

1. **Clone and navigate to the example**:
   ```bash
   cd examples/ssr-todo-app
   ```

2. **Build the project**:
   ```bash
   ./gradlew build
   ```

3. **Run the application**:
   ```bash
   ./gradlew run
   ```

4. **Open your browser**:
   Navigate to `http://localhost:8080`

### What You'll See

- A clean, responsive todo application interface
- Add new todos using the text input and "Add Todo" button
- Toggle todo completion by clicking the checkbox
- Delete todos using the delete button (🗑️)
- View completion statistics at the bottom

## Code Highlights

### Pure SSR Experience

This example demonstrates a **pure SSR approach** where:

- All rendering happens on the server
- No client-side JavaScript is loaded or executed
- Form submissions trigger full page reloads
- State is maintained entirely on the server
- SEO crawlers can fully index the content

### Summon Framework Features Used

- **Component System**: `@Composable` functions for UI building blocks
- **Layout Components**: `Column`, `Row`, `Box` for structure
- **Input Components**: `Button`, `TextField`, `Checkbox` for interactions
- **Display Components**: `Text` for content presentation
- **Modifier System**: Type-safe styling with method chaining
- **Color System**: Built-in color management
- **Typography**: Font weight, size, and alignment controls

### Server-Side State Management

The `TodoStorage` object provides a simple in-memory storage solution:

```kotlin
object TodoStorage {
    private val todos = mutableListOf<Todo>()
    private val idCounter = AtomicInteger(1)
    
    fun getAllTodos(): List<Todo> = todos.toList()
    fun addTodo(text: String): Todo { /* ... */ }
    fun toggleTodo(id: Int): Todo? { /* ... */ }
    fun deleteTodo(id: Int): Boolean { /* ... */ }
}
```

> **Note**: In a production application, you would replace this with a proper database solution.

## Performance Characteristics

- **Initial Page Load**: Very fast since HTML is pre-rendered on the server
- **SEO**: Excellent since search engines receive fully rendered HTML
- **JavaScript Bundle Size**: Zero - no client-side JavaScript needed
- **Time to Interactive**: Immediate - no client-side hydration required
- **Caching**: Server responses can be easily cached by CDNs

## Extending the Example

This example can be extended in several ways:

1. **Add Database Persistence**:
   - Replace `TodoStorage` with a database (H2, PostgreSQL, etc.)
   - Add data access layer with proper transaction management

2. **Add User Authentication**:
   - Integrate with Summon's security components
   - Add user-specific todo lists

3. **Enhanced UI Features**:
   - Add todo categories or tags
   - Implement sorting and filtering
   - Add due dates and priorities

4. **API Endpoints**:
   - Add REST API endpoints alongside the HTML routes
   - Support both web UI and API clients

5. **Deployment**:
   - Containerize with Docker
   - Deploy to cloud platforms (Heroku, AWS, etc.)

## Comparison with Client-Side Approaches

| Aspect | SSR (This Example) | SPA with Hydration |
|--------|-------------------|-------------------|
| Initial Load Speed | ⚡ Very Fast | 🐌 Slower |
| SEO | ✅ Excellent | ⚠️ Requires work |
| JavaScript Bundle | 🎯 Zero | 📦 Larger |
| Server Load | 🔄 Higher | ⚡ Lower |
| Interactivity | 🔄 Page reloads | ⚡ Instant |
| Offline Support | ❌ None | ✅ Possible |

## Learning Resources

- [Summon SSR Guide](../../docs/ssr-guide.md) - Comprehensive SSR implementation guide
- [Summon API Reference](../../docs/api-reference/ssr.md) - Complete API documentation
- [Component Reference](../../docs/api-reference/components.md) - All available components
- [Styling Guide](../../docs/api-reference/modifier.md) - Modifier system documentation

## Support

This example is part of the Summon framework. For questions or issues:

- Check the main [Summon documentation](../../README.md)
- Review the [SSR troubleshooting guide](../../docs/ssr-guide.md#troubleshooting)
- Open an issue on the [Summon GitHub repository](https://github.com/codeyousef/summon)

---

**Happy coding with Summon! 🚀**