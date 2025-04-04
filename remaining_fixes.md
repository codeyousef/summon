# Remaining Fixes for Refactoring

After initial refactoring, there are several import and reference issues that need to be resolved. Here's a plan to fix them:

## 1. Create or Update State Classes

The following files need to be created or updated in the `state` package:

- `src/commonMain/kotlin/code/yousef/summon/state/MutableState.kt`
- `src/commonMain/kotlin/code/yousef/summon/state/State.kt` 

These should define:
- `interface State<T>`
- `interface MutableState<T> : State<T>`
- `fun <T> mutableStateOf(initialValue: T): MutableState<T>`

## 2. Create Validator in Input Package

Create `src/commonMain/kotlin/code/yousef/summon/components/input/Validator.kt` that defines:
- `interface Validator`
- `data class ValidationResult(val valid: Boolean, val errorMessage: String?)`
- `fun Validator.validate(value: Any?): ValidationResult`

## 3. Update Icon Class Reference

Create or ensure the Icon class exists at `src/commonMain/kotlin/code/yousef/summon/components/display/Icon.kt`, 
making sure it includes the necessary constants like `Icon.Download` used in examples.

## 4. Update Composable Interface Location

Move the Composable interface to `src/commonMain/kotlin/code/yousef/summon/core/Composable.kt` 
and update imports in all files that use it.

## 5. Update PlatformRendererProvider

Make sure PlatformRendererProvider is correctly defined in the core package:
- `src/commonMain/kotlin/code/yousef/summon/core/PlatformRendererProvider.kt`

## 6. Ensure Components Reference the Correct Interfaces

Make sure all component interfaces are correctly defined in the components package and properly imported.

## Priority Order for Fixes

1. State package classes
2. Validator interface
3. Icon class updates
4. Composable interface location
5. PlatformRendererProvider
6. Update all imports

Once these fixes are applied, the refactored code should compile and maintain the original functionality. 