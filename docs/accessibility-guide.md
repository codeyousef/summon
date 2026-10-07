# Accessibility Guide for Summon

This guide provides practical examples and best practices for making your Summon applications accessible to everyone, including people with disabilities.

## Why Accessibility Matters

- **Inclusion**: Ensures your application is usable by everyone, regardless of abilities
- **Legal compliance**: Many countries have laws requiring digital accessibility
- **Better UX for everyone**: Accessible applications are generally more usable for all users
- **SEO benefits**: Many accessibility features improve search engine rankings

## Core Accessibility Components

### AccessibleElement

The `AccessibleElement` component serves as a wrapper for adding accessibility attributes to UI elements:

```kotlin
// Basic usage
AccessibleElement(
    role = AccessibilityUtils.NodeRole.BUTTON,
    label = "Add to cart",
) {
    Box(modifier = Modifier.padding("10px").backgroundColor("#0066cc")) {
        Text("Add to cart")
    }
}

// With custom role
AccessibleElement(
    customRole = "banner",
    label = "Site header",
) {
    // Header content
}

// With relationships to other elements
AccessibleElement(
    role = AccessibilityUtils.NodeRole.TEXTBOX,
    label = "Email address",
    relations = mapOf(
        "describedby" to "email-hint",
        "errormessage" to "email-error"
    )
) {
    // Email input field
}
```

### Semantic HTML Components

Use these components to create a properly structured document that conveys meaning to assistive technologies:

```kotlin
Header {
    Heading(level = 1) {
        Text("My Application")
    }
    Nav {
        // Navigation links
    }
}

Main {
    Section {
        Heading(level = 2) {
            Text("Features")
        }
        Text("Features content")
    }
    
    Article {
        Heading(level = 2) {
            Text("Latest News")
        }
        Text("News content")
    }
}

Aside {
    Heading(level = 2) {
        Text("Related Information")
    }
    // Sidebar content
}

Footer {
    Text("© 2023 My Company")
}
```

## Accessibility Modifiers

### ARIA Attributes

ARIA (Accessible Rich Internet Applications) attributes provide additional semantics to make web content more accessible:

```kotlin
// Button with accessible label
Button(
    onClick = { /* handle click */ },
    modifier = Modifier
        .ariaLabel("Close dialog")
        .ariaControls("main-dialog")
)

// Checkbox with state
Checkbox(
    checked = isChecked,
    onCheckedChange = { isChecked = it },
    modifier = Modifier
        .ariaChecked(isChecked)
        .ariaDescribedBy("checkbox-hint")
)

// Custom tab implementation
Box(
    modifier = Modifier
        .role("tab")
        .ariaSelected(isSelected)
        .tabIndex(if (isSelected) 0 else -1)
)
```

### Focus Management

Proper keyboard focus management is essential for accessibility:

```kotlin
// Make an element focusable but not in tab order
Box(
    modifier = Modifier
        .focusable()
        .onClick { /* handle click */ }
)

// Make an element keyboard tabbable
Box(
    modifier = Modifier
        .tabbable()
        .onClick { /* handle click */ }
)

// Disable an element
Button(
    onClick = { /* handle click */ },
    enabled = false,
    modifier = Modifier.disabled()
)

// Auto-focus an element when it appears
TextField(
    value = text,
    onValueChange = { text = it },
    modifier = Modifier.autoFocus()
)
```

### Controlled input and dialog behavior

`TextField` and `TextArea` preserve the native selection while their DOM node is
reused. Composition events defer model writes until `compositionend`, so IME
pre-edit text is not overwritten by an intermediate recomposition. Password
values are passed through unchanged; trimming or clipboard/reveal policy belongs
in the application or approved broker UI.

`DatePicker` renders a native date input on JS, WASM, and SSR. Its controlled
value is an ISO `LocalDate`, so browser timezone changes cannot shift the chosen
calendar day; browser locale and the nearest `lang` attribute control the native
calendar presentation. Supply `label` for its accessible name and use
`minDate`/`maxDate` for the announced native range.

`Modal` renders a named `role="dialog"` with `aria-modal="true"`, traps Tab and
Shift+Tab in the topmost nested dialog, and makes the background inert. Escape
and backdrop dismissal are independently controlled:

```kotlin
Modal(
    isOpen = showSettings,
    onDismiss = { showSettings = false },
    ariaLabel = "Account settings",
    dismissOnEscape = true,
    dismissOnBackdropClick = false,
) {
    // Dialog controls
}
```

Focus returns to the live invoker after dismissal. If that element was removed,
focus falls back to the remaining parent dialog without retaining the removed
node. `Focusable` and `FocusableContainer` report real focus and blur changes;
callbacks are detached with their rendered owner.

Buttons have a 44 CSS-pixel minimum target and suppress their transition and
hover transform under `prefers-reduced-motion: reduce`. Toasts expose
`role="status"`/polite announcements, or `role="alert"`/assertive announcements
for errors. `ToastManager(maxVisibleToasts)` bounds concurrent announcements and
evicts the oldest item when the bound is reached.

For mixed-direction content, inherit `dir` at the component or root and isolate
protocol identifiers with `Bdi`:

```kotlin
Column(Modifier().attribute("dir", "rtl")) {
    Text("عنوان الحساب")
    Bdi(Modifier().attribute("dir", "ltr")) {
        Text("alice@example.test")
    }
}
```

Automated semantic checks do not replace manual keyboard, zoom, high-contrast,
screen-reader, OS text-scaling, or physical Safari qualification.

## Accessibility Utilities

The `AccessibilityUtils` object provides functions for working with accessibility attributes:

```kotlin
// Create a Modifier with a specific role
val buttonModifier = AccessibilityUtils.createRoleModifier(AccessibilityUtils.NodeRole.BUTTON)

// Create a Modifier with an accessible label
val labelModifier = AccessibilityUtils.createLabelModifier("Close dialog")

// Create a Modifier with relationship to another element
val relationModifier = AccessibilityUtils.createRelationshipModifier("describedby", "description-id")

// Inspect accessibility attributes on a Modifier
val modifier = Modifier()
    .role("button")
    .ariaLabel("Close")
    
val accessibilityAttrs = modifier.inspectAccessibility()
// Result: {"role": "button", "aria-label": "Close"}
```

## Accessibility Best Practices

### Keyboard Accessibility

- Ensure all interactive elements are keyboard accessible
- Maintain a logical tab order
- Provide visible focus indicators
- Implement keyboard shortcuts for common actions

```kotlin
// Visible focus indicator
Button(
    onClick = { /* handle click */ },
    modifier = Modifier
        .tabbable()
        .backgroundColor("#ffffff")
        .focusStyle(
            Modifier.border("2px", "solid", "#0066cc")
        )
)
```

### Screen Reader Support

- Provide descriptive labels for all interactive elements
- Use semantic HTML elements
- Implement ARIA attributes appropriately
- Test with screen readers

```kotlin
// Accessible image with alternative text
Image(
    src = "/assets/logo.png",
    alt = "Company Logo",
    modifier = Modifier.ariaHidden(false)
)

// Hidden decorative image
Image(
    src = "decoration.png",
    alt = "",
    modifier = Modifier.ariaHidden(true)
)
```

### Color and Contrast

- Ensure sufficient color contrast (minimum 4.5:1 for normal text, 3:1 for large text)
- Don't rely on color alone to convey information
- Provide visual indicators like icons in addition to color

```kotlin
// Error state with both color and icon
if (hasError) {
    Box(
        modifier = Modifier
            .color("#d32f2f") // Ensure this has proper contrast
            .ariaDescribedBy("error-message")
    ) {
        Icon("error")
        Text("This field is required", id = "error-message")
    }
}
```

### Forms and Validation

- Associate labels with form controls
- Provide clear error messages
- Group related form controls
- Indicate required fields

```kotlin
// Accessible form field with label and validation
FormField {
    Label(for = "email-input") {
        Text("Email address")
        if (isRequired) {
            Text("*", modifier = Modifier.ariaHidden(true))
            Text(" (required)", modifier = Modifier.className("visually-hidden"))
        }
    }
    
    TextField(
        value = email,
        onValueChange = { email = it },
        modifier = Modifier
            .id("email-input")
            .ariaRequired(isRequired)
            .ariaInvalid(hasError)
            .ariaDescribedBy(if (hasError) "email-error" else "email-hint")
    )
    
    if (hasError) {
        Text(
            "Please enter a valid email address",
            id = "email-error",
            modifier = Modifier
                .color("#d32f2f")
                .ariaLive("assertive")
        )
    } else {
        Text(
            "Enter your email address in format: name@example.com",
            id = "email-hint",
            modifier = Modifier.color("#666666")
        )
    }
}
```

## Testing Accessibility

1. **Keyboard Testing**: 
   - Navigate your application using only the keyboard
   - Ensure all interactive elements can be reached and activated

2. **Screen Reader Testing**:
   - Test with screen readers like NVDA, JAWS, or VoiceOver
   - Verify that all content is properly announced

3. **Automated Testing**:
   - Use accessibility testing tools
   - Integrate accessibility checks into your CI/CD pipeline

4. **Manual Testing**:
   - Conduct usability testing with people with disabilities
   - Review your application against WCAG guidelines

## Resources

- [Web Content Accessibility Guidelines (WCAG)](https://www.w3.org/WAI/standards-guidelines/wcag/)
- [WAI-ARIA Authoring Practices](https://www.w3.org/WAI/ARIA/apg/)
- [Accessible Rich Internet Applications (WAI-ARIA)](https://www.w3.org/WAI/standards-guidelines/aria/)
- [A11Y Project Checklist](https://www.a11yproject.com/checklist/) 
