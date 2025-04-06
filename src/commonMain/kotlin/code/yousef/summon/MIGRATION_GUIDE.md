# Summon Migration Guide: From Interface-Based to Annotation-Based Composition

This guide will help you migrate your existing Summon code from the interface-based composition model to the new annotation-based model that uses the `@Composable` annotation.

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

## Examples

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

### 3. Migrating a Card with Custom Style

**Before:**
```kotlin
fun createStyledCard(): Card {
    return Card(
        content = listOf(
            Text(
                text = "Card Title",
                modifier = Modifier().fontSize("18px").fontWeight("bold")
            ),
            Text(
                text = "Card content goes here...",
                modifier = Modifier().color("#555")
            )
        ),
        modifier = Modifier()
            .width("300px")
            .backgroundColor("#f5f5f5"),
        elevation = "2px",
        borderRadius = "4px"
    )
}
```

**After:**
```kotlin
@Composable
fun CreateStyledCard() {
    Card(
        modifier = Modifier()
            .width("300px")
            .background("#f5f5f5")
            .shadow("0px", "2px", "4px", "rgba(0,0,0,0.1)")
            .borderRadius("4px")
    ) {
        Text(
            text = "Card Title",
            modifier = Modifier().fontSize("18px").fontWeight("bold")
        )
        Text(
            text = "Card content goes here...",
            modifier = Modifier().color("#555")
        )
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

## Handling Linter Errors During Transition

During the transition period, you may encounter some linter errors due to duplicated definitions. 
This is particularly common with the `Checkbox` component. If you see an "Overload resolution ambiguity" error, 
you can work around it by using an explicitly typed lambda:

```kotlin
// Instead of this
Checkbox(
    checked = true,
    onCheckedChange = { isChecked -> /* Handle state change */ },
    modifier = Modifier()
)

// Try this
val checkHandler: (Boolean) -> Unit = { isChecked -> /* Handle state change */ }
Checkbox(
    checked = true,
    onCheckedChange = checkHandler,
    modifier = Modifier()
)
```

## Function Naming Conventions

With the move to a more Compose-like API, we've also adopted the Compose naming convention:

- Composable functions that return Unit should use UpperCamelCase (e.g., `MyComponent()` instead of `myComponent()`)
- Regular functions that return a value can continue to use lowerCamelCase (e.g., `getComponentData()`)

## Additional Resources

- For more detailed examples, refer to the `ComponentAPIExamples.kt` file.
- See the `README.md` file in the components directory for a detailed API reference.
- Look at the updated example files like `TextExample.kt` and `LinkExample.kt` for real-world examples. 