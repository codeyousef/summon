# Summon Migration Guide: From Interface-Based to Annotation-Based Composition

## Purpose of Summon

Summon is a Kotlin Multiplatform UI library designed exclusively for web applications, targeting JVM (for server-side rendering) and JS (for client-side rendering) platforms. It produces SEO-friendly and accessibility-friendly HTML, JavaScript, and CSS while providing a consistent, type-safe API that aims to be 1-to-1 compatible with Jetbrains Compose. The library prioritizes semantic markup, proper ARIA attributes, and progressive enhancement techniques to ensure content is discoverable by search engines and usable by assistive technologies. By maintaining API parity with Compose, Summon allows developers to leverage existing Compose knowledge and potentially share code with Compose-based projects while targeting the web platform specifically.

## Migration Overview

We are transitioning from an interface-based composition model to a modern annotation-based model that uses the `@Composable` annotation, directly mirroring Jetbrains Compose's API patterns. This 1-to-1 compatibility with Compose will make the API more intuitive for developers familiar with Compose, improve type safety, and enable better tooling support. Our goal is to align with Compose's patterns so closely that developers can seamlessly move between Summon and other Compose-targeted platforms, with web-specific adaptations made only where absolutely necessary.

## Current State and Remaining Steps

We are in the middle of a major refactoring effort to convert the entire codebase to use the new annotation-based composition model. Currently:

1. ✅ Core runtime components have been updated with `@Composable` annotations
2. ✅ Basic modifier system has been migrated
3. ✅ Theme system has been updated to support the new model
4. 🔄 Component implementations are being updated (in progress)
5. ❌ Platform-specific renderers need updates
6. ❌ Routing system requires refactoring
7. ❌ SSR support needs to be adapted to the new model
8. ❌ HTML integration utilities need updating
9. ❌ Testing infrastructure must be adjusted

**Note: Examples and demos have been temporarily removed and will be reintroduced only after the core library is fully functional.** This will prevent confusion and ensure that all examples follow the new API patterns.

## Key Changes

1. All UI functions need to be annotated with `@Composable`
2. Content is now provided as lambdas instead of lists
3. Component parameter names have been standardized
4. Component nesting is now done via lambda blocks

## Migration Checklist

- [ ] Add `@Composable` annotation to all UI functions
- [ ] Update components to use lambda-based content instead of List<Component>
- [ ] Update input components to use controlled component pattern
- [ ] Refactor utility functions to create composable components
- [ ] Fix modifiers that have changed names or method signatures
- [ ] Update routing to support the new composition model
- [ ] Adapt SSR functionality to work with the new model
- [ ] Create new examples once the library is stable

## Parameter Name Changes

| Component Type | Old Parameter            | New Parameter               |
|----------------|--------------------------|----------------------------|
| Text           | `text: String`           | `text: String`             |
| Link           | `text: String`           | content lambda with Text   |
| TextField      | `state: MutableState<String>` | `value`/`onValueChange` |
| Checkbox       | `state`/`onSelectedChange` | `checked`/`onCheckedChange` |
| Select         | `state`/`onSelectedChange` | `value`/`onValueChange`   |
| Layout         | `content: List<Component>` | `content: @Composable () -> Unit` |
| Progress       | `value`/`type`           | `progress`/`type`         |

## Migration Examples

### 1. Migrating a Simple Text Component

**Before:**
```kotlin
fun createTextExample(): Column {
    return Column(
        modifier = Modifier().padding("16px"),
        content = listOf(
            Text(
                text = "Hello, World!",
                modifier = Modifier().fontSize("20px")
            ),
            Text(
                text = "This is a simple example.",
                modifier = Modifier().color("#555")
            )
        )
    )
}
```

**After:**
```kotlin
@Composable
fun CreateTextExample() {
    Column(
        modifier = Modifier().padding("16px")
    ) {
        Text(
            text = "Hello, World!",
            modifier = Modifier().fontSize("20px")
        )
        Text(
            text = "This is a simple example.",
            modifier = Modifier().color("#555")
        )
    }
}
```

### 2. Migrating a Form Component

**Before:**
```kotlin
fun createForm(): Form {
    val nameState = mutableStateOf("")
    val emailState = mutableStateOf("")
    
    return Form(
        content = listOf(
            TextField(
                state = nameState,
                label = "Name",
                placeholder = "Enter your name"
            ),
            TextField(
                state = emailState,
                label = "Email",
                placeholder = "Enter your email"
            ),
            Button(
                text = "Submit",
                onClick = { /* handle submission */ }
            )
        )
    )
}
```

**After:**
```kotlin
@Composable
fun CreateForm() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    
    Form {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = "Name",
            placeholder = "Enter your name"
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "Enter your email"
        )
        Button(
            onClick = { /* handle submission */ }
        ) {
            Text("Submit")
        }
    }
}
```

## Modifier Changes

Some modifiers have been updated to use the new API:

| Old Modifier              | New Modifier                                   |
|---------------------------|-----------------------------------------------|
| `.backgroundColor(val)`   | `.background(val)`                            |
| `.marginBottom(val)`      | `.style("margin-bottom", val)`                |
| `.transitions(val)`       | `.style("transition", val)`                   |
| `.flexWrap(val)`          | `.style("flex-wrap", val)`                    |
| `.display(val)`           | `.style("display", val)`                      |
| `.overflow(val)`          | `.style("overflow", val)`                     |

## Function Naming Conventions

With the move to a more Compose-like API, we've also adopted the Compose naming convention:

- Composable functions that return Unit should use UpperCamelCase (e.g., `MyComponent()` instead of `myComponent()`)
- Regular functions that return a value can continue to use lowerCamelCase (e.g., `getComponentData()`)

## Next Steps After Migration

Once the core migration is complete:

1. We will create a comprehensive suite of examples showcasing the new API
2. Complete documentation will be updated to reflect the new patterns
3. Additional testing utilities will be provided for the new composable model
4. Performance optimizations specific to the new composition model will be implemented 