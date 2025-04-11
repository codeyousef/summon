# Focus Management API Reference

This document provides detailed information about the focus management APIs in the Summon library.

## Table of Contents

- [FocusManager](#focusmanager)
- [KeyboardNavigation](#keyboardnavigation)
- [Focus Modifiers](#focus-modifiers)

---

## FocusManager

The `FocusManager` class is responsible for handling focus control across components.

### Class Definition

```kotlin
package code.yousef.summon.focus

object FocusManager {
    // Current focused element
    val currentFocus: Element?
    
    // Request focus on an element
    fun requestFocus(element: Element)
    
    // Clear focus
    fun clearFocus()
    
    // Move focus to the next focusable element
    fun moveFocusNext()
    
    // Move focus to the previous focusable element
    fun moveFocusPrevious()
}
```

### Description

The `FocusManager` provides utilities to control focus in applications, which is crucial for keyboard navigation and accessibility.

### Example

```kotlin
Button(
    text = "Focus Next Input",
    onClick = {
        FocusManager.moveFocusNext()
    }
)
```

---

## KeyboardNavigation

The `KeyboardNavigation` class handles keyboard-based navigation through focusable elements.

### Class Definition

```kotlin
package code.yousef.summon.focus

class KeyboardNavigation {
    // Enable keyboard navigation for a container
    fun enableForContainer(container: Element)
    
    // Disable keyboard navigation
    fun disable()
    
    // Handle key events
    fun handleKeyEvent(event: KeyboardEvent): Boolean
}

@Composable
fun KeyboardNavigationWrapper(
    enabled: Boolean = true,
    content: @Composable () -> Unit
)
```

### Description

`KeyboardNavigation` allows users to navigate through focusable elements using keyboard keys like Tab, Shift+Tab, and arrow keys.

### Example

```kotlin
@Composable
fun FormWithKeyboardNavigation() {
    KeyboardNavigationWrapper {
        Column(
            modifier = Modifier.padding(16.px)
        ) {
            TextField(
                value = "Username",
                onValueChange = { /* handle change */ },
                modifier = Modifier.tabIndex(1)
            )
            
            TextField(
                value = "Password",
                onValueChange = { /* handle change */ },
                type = TextFieldType.Password,
                modifier = Modifier.tabIndex(2)
            )
            
            Button(
                text = "Login",
                onClick = { /* handle login */ },
                modifier = Modifier.tabIndex(3)
            )
        }
    }
}
```

---

## Focus Modifiers

Modifiers that control focus behavior of components.

### Modifier Functions

```kotlin
package code.yousef.summon.modifier

// Set tab index for keyboard navigation
fun Modifier.tabIndex(index: Int): Modifier

// Make an element focusable
fun Modifier.focusable(focusable: Boolean = true): Modifier

// Apply styles when element is focused
fun Modifier.focusedStyle(modifier: Modifier): Modifier

// Handle focus events
fun Modifier.onFocus(callback: () -> Unit): Modifier
fun Modifier.onBlur(callback: () -> Unit): Modifier
```

### Description

Focus modifiers allow controlling how components participate in focus management and how they appear when focused.

### Example

```kotlin
TextField(
    value = "Input with custom focus style",
    onValueChange = { /* handle change */ },
    modifier = Modifier
        .tabIndex(1)
        .focusable()
        .focusedStyle(
            Modifier
                .borderColor("#0077cc")
                .boxShadow("0 0 5px #0077cc")
        )
        .onFocus {
            println("Input focused")
        }
        .onBlur {
            println("Input blurred")
        }
)
``` 