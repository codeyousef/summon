# Ktor Todo App with Summon Framework

A full-featured todo application built with Ktor and the Summon UI framework, demonstrating real-time collaboration, authentication, and reactive UI components.

## Features

- **Real-time collaboration** via WebSockets - see other users' changes instantly
- **Session-based authentication** with user registration and login
- **Multi-language support** (English, Spanish, French)
- **Theme switching** (Light/Dark mode)
- **Reactive UI** built with pure Summon components (no raw HTML/CSS/JS)
- **SQLite database** with Exposed ORM
- **Server-side rendering** with Summon's KtorRenderer
- **RESTful API** for todo operations

## Tech Stack

- **Backend**: Ktor 3.0.2 with Netty
- **UI Framework**: Summon (Kotlin Multiplatform)
- **Database**: SQLite with Exposed ORM
- **Real-time**: WebSockets
- **Authentication**: Session-based with BCrypt password hashing
- **Serialization**: kotlinx.serialization

## Quick Start

1. **Build and run**:
   ```bash
   ./gradlew run
   ```

2. **Access the application**:
   - Open http://localhost:8080
   - Demo credentials: username `demo`, password `demo`

3. **Try the features**:
   - Add, edit, and delete todos
   - Toggle between light/dark themes
   - Switch languages (EN/ES/FR)
   - Open multiple browser tabs to see real-time updates

## Project Structure

```
src/main/kotlin/code/yousef/summon/examples/ktor/
├── Application.kt              # Main Ktor application setup
├── database/
│   └── DatabaseConfig.kt      # Database initialization and repositories
├── i18n/
│   └── Translations.kt        # Multi-language translations
├── models/
│   └── Models.kt              # Data models, DTOs, and entities
├── pages/
│   ├── AuthPage.kt            # Login/register page (Summon components)
│   └── TodoPage.kt            # Main todo page (Summon components)
├── routing/
│   ├── AuthRoutes.kt          # Authentication API endpoints
│   ├── PageRoutes.kt          # HTML page routes with SSR
│   ├── TodoRoutes.kt          # Todo API endpoints
│   └── WebSocketRoutes.kt     # WebSocket connection handling
└── websocket/
    └── WebSocketManager.kt    # Real-time connection management
```

## API Endpoints

### Authentication
- `POST /auth/login` - User login
- `POST /auth/register` - User registration  
- `POST /auth/logout` - User logout
- `GET /auth/session` - Check session validity
- `POST /auth/settings` - Update user settings (language/theme)

### Todos
- `GET /api/todos` - Get user's todos
- `POST /api/todos` - Create new todo
- `PUT /api/todos/{id}` - Update todo
- `DELETE /api/todos/{id}` - Delete todo
- `DELETE /api/todos/completed` - Clear completed todos

### WebSocket
- `WS /ws` - Real-time updates for todo changes

### Pages
- `GET /` - Home (redirects to /todos or /auth)
- `GET /auth` - Authentication page
- `GET /todos` - Main todo application

## Real-time Features

The application uses WebSockets to provide real-time collaboration:

- **User connections**: See when users join/leave
- **Todo updates**: Instantly see todos added, edited, or deleted by other users
- **Automatic reconnection**: WebSocket automatically reconnects if connection is lost

## Internationalization

Supports three languages with complete translations:
- **English** (en) - Default
- **Spanish** (es) - Español  
- **French** (fr) - Français

Language settings are persisted in the user session.

## Theming

Two themes available:
- **Light theme** - Default Material Design light colors
- **Dark theme** - Material Design dark colors

Theme preference is saved in the user session.

## Database Schema

**Users Table**:
- id (Primary Key)
- username (Unique)
- email (Unique) 
- passwordHash (BCrypt)
- createdAt

**Todos Table**:
- id (Primary Key)
- userId (Foreign Key)
- text
- completed
- createdAt
- updatedAt

## Development

The application uses Summon's server-side rendering capabilities to generate HTML with Summon components, then enhances the client-side with JavaScript for interactivity and real-time updates.

**Key architectural decisions**:
- Pure Summon components (no raw HTML/CSS/JS in components)
- Session-based authentication for simplicity
- WebSocket broadcasting for real-time collaboration
- Server-side rendering for better SEO and initial load performance

## Production Deployment

For production deployment:

1. **Build fat JAR**:
   ```bash
   ./gradlew buildFatJar
   ```

2. **Run**:
   ```bash
   java -jar build/libs/ktor-todo-app.jar
   ```

3. **Docker** (optional):
   ```bash
   ./gradlew buildImage
   docker run -p 8080:8080 ktor-todo-app
   ```

**Production considerations**:
- Set `cookie.secure = true` for HTTPS
- Configure proper CORS origins
- Use a production database (PostgreSQL, MySQL)
- Set up proper logging and monitoring
- Configure environment-specific settings