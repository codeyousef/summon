# Summon JavaScript Example

This example demonstrates a comprehensive showcase of Summon's features in a JavaScript environment. It implements a task management application with the following features:

## Features Demonstrated

- **Component-Based Architecture**: Reusable UI components for tasks, forms, and layout
- **Type-Safe Styling**: Using the modifier API for consistent styling
- **Reactive State Management**: Automatic UI updates when task data changes
- **Enhanced Styling System**: 
  - CSS properties with type safety
  - Numeric extensions for CSS units
  - Gradient support
  - Color system with Material Design palette
  - Class-based styling following Kobweb's approach
  - Centralized stylesheet for consistent styling
- **Flexible Layout System**:
  - Flexbox layout with Row and Column components
  - Grid layout for task organization
- **Lifecycle Management**: Side effects for data persistence
- **Animation and Transitions**: 
  - Animations for task completion
  - Transitions for UI state changes
- **Theme System**: 
  - Light/dark mode support
  - Type-safe theme properties
- **Internationalization**: Multi-language support with RTL layouts

## Project Structure

- `src/jsMain/kotlin/code/yousef/summon/examples/js/Main.kt`: Entry point and application setup
- `src/jsMain/kotlin/code/yousef/summon/examples/js/App.kt`: Main application component
- `components/`: Reusable UI components
  - `TaskItem.kt`: Individual task component
  - `TaskForm.kt`: Form for creating/editing tasks
  - `TaskList.kt`: List of tasks with filtering
  - `Header.kt`: Application header with theme toggle
- `theme/`: Theme definitions and utilities
- `i18n/`: Internationalization resources
- `state/`: State management for tasks

## Styling System

The example uses a class-based styling approach inspired by Kobweb:

- `src/jsMain/kotlin/code/yousef/summon/style/SummonStyleSheet.kt`: Centralized stylesheet that defines CSS classes for common components
- `src/jsMain/kotlin/code/yousef/summon/ModifierJs.kt`: JS-specific extensions for the Modifier class that apply CSS classes
- `src/jsMain/kotlin/code/yousef/summon/runtime/JsPlatformRenderer.kt`: Platform renderer that applies appropriate CSS classes to elements

This approach offers several advantages:
- Reduced inline styles for better performance
- Consistent styling across components
- Easier maintenance with centralized style definitions
- Better separation of concerns between styling and component logic

## Running the Example

The example has its own build.gradle.kts file, allowing it to be run independently:

1. Clone the repository
2. Navigate to the example directory: `cd docs/examples/js/js-example`
3. Run the example: `./gradlew jsBrowserDevelopmentRun`
4. Open your browser to `http://localhost:8080`

### Building for Production

To build the example for production:

```bash
./gradlew jsBrowserProductionWebpack
```

The output will be in the `build/distributions` directory.

## Implementation Notes

This example is designed to showcase real-world usage of Summon in a JavaScript environment. It demonstrates best practices for structuring a Summon application and leveraging the library's features for building interactive UIs.
