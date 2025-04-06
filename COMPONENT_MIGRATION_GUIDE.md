# Summon Component Migration Guide

This guide outlines the process for updating Summon UI components to use the new `getPlatformRenderer()` function instead of the older `PlatformRendererProvider.getRenderer()` approach.

## Why this change?

The transition from `PlatformRendererProvider.getRenderer()` to `getPlatformRenderer()` offers several benefits:

1. **Improved maintainability**: Centralizes renderer access logic, making it easier to modify in the future
2. **Better Compose pattern alignment**: Matches the pattern used in other Compose-based frameworks
3. **Simpler API**: Reduces the verbosity of getting platform renderers
4. **Preparation for CompositionLocal**: This is a stepping stone toward using proper CompositionLocal for renderer access

## How to update a component

To update a component to use the new renderer access method, follow these steps:

1. Replace the import statement:
   ```kotlin
   // Old
   import code.yousef.summon.core.PlatformRendererProvider
   
   // New
   import code.yousef.summon.core.getPlatformRenderer
   ```

2. Update the renderer access in the component code:
   ```kotlin
   // Old
   val renderer = PlatformRendererProvider.getRenderer()
   
   // New
   val renderer = getPlatformRenderer()
   ```

3. If there are any TODO comments mentioning `PlatformRendererProvider`, update them:
   ```kotlin
   // Old
   // TODO: Replace PlatformRendererProvider with CompositionLocal access
   
   // New
   // TODO: Replace getPlatformRenderer with CompositionLocal access
   ```

## Example

Here's an example of updating the `Text` component:

**Before:**
```kotlin
@Composable
fun Text(text: String, modifier: Modifier = Modifier()) {
    // Apply default text styling
    val finalModifier = Modifier()
        .fontFamily("sans-serif")
        .fontSize("1rem")
        .color("#000000")
        .then(modifier)
        
    // Get the renderer
    val renderer = PlatformRendererProvider.getRenderer()
    
    // Render the text
    renderer.renderText(value = text, modifier = finalModifier)
}
```

**After:**
```kotlin
@Composable
fun Text(text: String, modifier: Modifier = Modifier()) {
    // Apply default text styling
    val finalModifier = Modifier()
        .fontFamily("sans-serif")
        .fontSize("1rem")
        .color("#000000")
        .then(modifier)
        
    // Get the renderer
    val renderer = getPlatformRenderer()
    
    // Render the text
    renderer.renderText(value = text, modifier = finalModifier)
}
```

## Components Migration Status

### Display Components
- [x] Text - Updated to use getPlatformRenderer()
- [x] Image - Updated to use getPlatformRenderer()
- [x] Icon - Updated to use getPlatformRenderer()
- [ ] Avatar - Still using PlatformRendererProvider
- [ ] Chip - Still using PlatformRendererProvider

### Layout Components
- [x] Box - Updated to use getPlatformRenderer()
- [x] Row - Updated to use getPlatformRenderer()
- [x] Column - Updated to use getPlatformRenderer()
- [x] Card - Updated to use getPlatformRenderer()
- [x] Divider - Updated to use getPlatformRenderer()
- [x] LazyColumn - Updated to use getPlatformRenderer()
- [ ] LazyRow - Still using PlatformRendererProvider
- [ ] Grid - Still using PlatformRendererProvider
- [ ] AspectRatio - Still using PlatformRendererProvider
- [ ] Spacer - Still using PlatformRendererProvider

### Input Components
- [x] Button - Updated to use getPlatformRenderer()
- [x] TextField - Updated to use getPlatformRenderer()
- [x] Checkbox - Updated to use getPlatformRenderer()
- [x] RadioButton - Updated to use getPlatformRenderer()
- [x] Switch - Updated to use getPlatformRenderer()
- [ ] Select - Still using PlatformRendererProvider
- [ ] Slider - Still using PlatformRendererProvider
- [ ] DatePicker - Still using PlatformRendererProvider
- [ ] TimePicker - Still using PlatformRendererProvider
- [ ] FileUpload - Still using PlatformRendererProvider

### Feedback Components
- [x] Alert - Updated to use getPlatformRenderer()
- [x] Badge - Updated to use getPlatformRenderer()
- [x] ProgressBar - Updated to use getPlatformRenderer() with ProgressType.LINEAR
- [x] CircularProgress - Updated to use getPlatformRenderer() with ProgressType.CIRCULAR
- [x] Tooltip - Updated to use getPlatformRenderer()
- [ ] Snackbar - Still using PlatformRendererProvider

### Navigation Components
- [ ] Tabs - Still using PlatformRendererProvider
- [ ] NavigationBar - Still using PlatformRendererProvider
- [ ] Drawer - Still using PlatformRendererProvider
- [ ] Menu - Still using PlatformRendererProvider
- [ ] Link - Still using PlatformRendererProvider

## Common Issues and Solutions

### 1. Parameter Mismatches

**Issue**: The component function signature differs from what the renderer method expects.

**Example**: In the `ProgressBar` component, the renderer expects a `ProgressType`, but it wasn't initially included:

```kotlin
// Incorrect
renderer.renderProgressBar(progress, modifier)

// Correct
renderer.renderProgress(progress, ProgressType.LINEAR, modifier)
```

**Solution**: 
- Check the `PlatformRenderer` interface for the correct method signature
- Import any required enums or parameter types
- Update the component to match the renderer method signature

### 2. Missing Imports and Unresolved References

**Issue**: After updating components, you might see errors for unresolved references to modifiers or other types.

**Solution**:
- Check that all necessary imports are present
- For modifier-related issues, ensure the correct modifier extension functions are imported
- For component-related issues, check that the component exists and is imported

### 3. Deprecated Interfaces and Base Classes

**Issue**: Some components extend older interfaces like `TextComponent`, `ClickableComponent`, etc., which may be deprecated.

**Solution**:
- For newer composable functions, these interfaces are typically not needed
- Focus on updating the renderer access pattern first
- Address interface changes as a separate refactoring step

### 4. Conflicting Overloads

**Issue**: When updating components with multiple overloaded versions, you might encounter conflicting overload errors.

**Solution**:
- Update one method implementation at a time
- Ensure parameter names and types match exactly between overloads
- Consider consolidating overloads if they're causing issues

## Testing Your Changes

After updating a component, test it using these guidelines:

1. **Compile Check**: Rebuild the project to ensure there are no compilation errors
   ```bash
   ./gradlew build
   ```

2. **Functionality Test**: Check if the component still works correctly
   - Run any existing tests that use the component
   - Test the component in a sample application
   - Verify that callbacks (like onClick) still function properly

3. **Visual Verification**: Ensure the component renders correctly
   - Check that styling is preserved
   - Verify that state changes (like hover, focus) work as expected

## Next Steps After Migration

Once all components have been migrated to use `getPlatformRenderer()`, these additional steps are recommended:

1. **Update Documentation**: Ensure all code samples and documentation reflect the new pattern
2. **Remove Redundant TODOs**: Clean up any TO-DO comments that have been addressed
3. **Implement CompositionLocal**: Work toward a proper CompositionLocal-based renderer access solution

---

This migration is part of our broader effort to move the Summon framework toward an annotation-based composition system similar to Jetpack Compose. Moving to `getPlatformRenderer()` is a key step in that process. 