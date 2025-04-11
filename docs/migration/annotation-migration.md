# Composable Annotation Consolidation Guide

## Overview

This document outlines the consolidation of `@Composable` annotations in the Summon library.

## Previous State

The codebase previously had two separate annotation declarations:

1. `code.yousef.summon.annotation.Composable` - Used in many components and platform code
2. `code.yousef.summon.runtime.Composable` - Also used in various parts of the codebase

Both annotations were functionally similar and served the same purpose, but their duplication created confusion and maintenance issues.

## Current State (Consolidated)

We've consolidated to a single canonical annotation:

1. `code.yousef.summon.annotation.Composable` - The single source of truth
2. `code.yousef.summon.runtime.Composable` - Now a typealias pointing to the annotation package version

The consolidation was achieved by:
1. Creating a typealias in the runtime package that points to the annotation package
2. Removing the duplicate annotation class
3. Maintaining the StandardComposable alias for future code

## Using Composable Annotations

### For New Code

For new code, use the `StandardComposable` type alias which provides a stable abstraction:

```kotlin
import code.yousef.summon.annotation.StandardComposable as Composable

@Composable
fun MyComponent(text: String) {
    // Implementation
}
```

### For Existing Code

For existing code, you can continue using either import pattern as both now point to the same annotation:

```kotlin
// Either of these will work:
import code.yousef.summon.annotation.Composable
// or
import code.yousef.summon.runtime.Composable

@Composable
fun ExistingComponent() {
    // Implementation
}
```

### Recommended Pattern (For Consistency)

For consistency across the codebase, we recommend gradually migrating all code to use the StandardComposable pattern:

```kotlin
import code.yousef.summon.annotation.StandardComposable as Composable
```

## Best Practices

1. **For new code**: Always use the StandardComposable alias
2. **For existing code**: No immediate changes required, but consider migrating to StandardComposable during substantial updates
3. **Be consistent**: Within a single file, use the same import pattern throughout

## Benefits of Consolidation

1. **Simplified codebase**: Single source of truth for the Composable annotation
2. **Clearer semantics**: No confusion about which annotation to use
3. **Better maintainability**: Changes to annotation behavior only need to be made in one place
4. **Improved documentation**: References to @Composable now point to a single implementation 