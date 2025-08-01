# Animation API Reference

This document provides detailed information about the animation APIs in the Summon library.

## Table of Contents

- [Animation](#animation)
- [AnimatedVisibility](#animatedvisibility)
- [AnimatedContent](#animatedcontent)
- [Transition Components](#transition-components)
- [Easing Functions](#easing-functions)
- [Animation Modifiers](#animation-modifiers)
- [Keyframes](#keyframes)
- [Custom Animations](#custom-animations)

---

## Animation

The core animation class that manages animations in the Summon framework.

### Class Definition

```kotlin
package code.yousef.summon.animation

class Animation<T>(
    val initialValue: T,
    val targetValue: T,
    val duration: Int = 300,
    val delay: Int = 0,
    val easing: EasingFunction = EasingFunctions.EaseInOut,
    val onUpdate: (T) -> Unit,
    val onComplete: () -> Unit = {}
) {
    fun start()
    fun pause()
    fun resume()
    fun stop()
    fun reverse()
}

// Factory function
fun <T> createAnimation(
    initialValue: T,
    targetValue: T,
    duration: Int = 300,
    delay: Int = 0,
    easing: EasingFunction = EasingFunctions.EaseInOut,
    onUpdate: (T) -> Unit,
    onComplete: () -> Unit = {}
): Animation<T>
```

### Description

The `Animation` class provides the core functionality for creating and controlling animations in Summon.

### Example

```kotlin
val animation = createAnimation(
    initialValue = 0f,
    targetValue = 1f,
    duration = 500,
    easing = EasingFunctions.EaseInOut,
    onUpdate = { value ->
        element.style.opacity = value.toString()
    },
    onComplete = {
        println("Animation completed")
    }
)

Button(
    text = "Start Animation",
    onClick = {
        animation.start()
    }
)
```

---

## AnimatedVisibility

A component that animates the appearance and disappearance of its content.

### Function Definition

```kotlin
package code.yousef.summon.animation

@Composable
fun AnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    content: @Composable () -> Unit
)
```

### Description

`AnimatedVisibility` wraps content and applies enter/exit animations when the visibility changes.

### Example

```kotlin
var isVisible by remember { mutableStateOf(false) }

Button(
    text = if (isVisible) "Hide" else "Show",
    onClick = { isVisible = !isVisible }
)

AnimatedVisibility(
    visible = isVisible,
    enter = slideInHorizontally() + fadeIn(),
    exit = slideOutHorizontally() + fadeOut()
) {
    Card(
        modifier = Modifier
            .padding(16.px)
            .backgroundColor("#f0f0f0")
    ) {
        Text("This content animates in and out")
    }
}
```

---

## AnimatedContent

A component that animates between different content states.

### Function Definition

```kotlin
package code.yousef.summon.animation

@Composable
fun <T> AnimatedContent(
    targetState: T,
    modifier: Modifier = Modifier,
    transitionSpec: ContentTransitionSpec<T> = defaultContentTransitionSpec(),
    content: @Composable (T) -> Unit
)
```

### Description

`AnimatedContent` animates between different content based on the target state.

### Example

```kotlin
var currentPage by remember { mutableStateOf(0) }

Row {
    Button(
        text = "Previous",
        onClick = { currentPage = (currentPage - 1).coerceAtLeast(0) }
    )
    
    Button(
        text = "Next",
        onClick = { currentPage = (currentPage + 1).coerceAtMost(2) }
    )
}

AnimatedContent(
    targetState = currentPage,
    transitionSpec = {
        if (targetState > initialState) {
            // Moving forward
            slideInHorizontally(initialOffsetX = { it }) with
                slideOutHorizontally(targetOffsetX = { -it })
        } else {
            // Moving backward
            slideInHorizontally(initialOffsetX = { -it }) with
                slideOutHorizontally(targetOffsetX = { it })
        }
    }
) { page ->
    when (page) {
        0 -> IntroPage()
        1 -> DetailsPage()
        2 -> SummaryPage()
    }
}
```

---

## Transition Components

Components for creating transitions between different states.

### Function Definitions

```kotlin
package code.yousef.summon.animation

// Create transitions
fun <T> createTransition(initialState: T): Transition<T>

// Update transition state
fun <T> Transition<T>.updateTargetState(targetState: T)

// Transition specifications
fun fadeIn(initialAlpha: Float = 0f, duration: Int = 300): EnterTransition
fun fadeOut(targetAlpha: Float = 0f, duration: Int = 300): ExitTransition
fun slideInHorizontally(initialOffsetX: (width: Int) -> Int = { -it }, duration: Int = 300): EnterTransition
fun slideOutHorizontally(targetOffsetX: (width: Int) -> Int = { -it }, duration: Int = 300): ExitTransition
fun slideInVertically(initialOffsetY: (height: Int) -> Int = { -it }, duration: Int = 300): EnterTransition
fun slideOutVertically(targetOffsetY: (height: Int) -> Int = { -it }, duration: Int = 300): ExitTransition
fun expandIn(expandFrom: AnimationDirection = AnimationDirection.Center, duration: Int = 300): EnterTransition
fun shrinkOut(shrinkTowards: AnimationDirection = AnimationDirection.Center, duration: Int = 300): ExitTransition
```

### Description

Transition components provide a high-level API for creating animated transitions between different states.

### Example

```kotlin
enum class BoxState { Small, Large }

val transition = createTransition(BoxState.Small)
var currentState by remember { mutableStateOf(BoxState.Small) }

Button(
    text = "Toggle Size",
    onClick = {
        currentState = when (currentState) {
            BoxState.Small -> BoxState.Large
            BoxState.Large -> BoxState.Small
        }
        transition.updateTargetState(currentState)
    }
)

Box(
    modifier = Modifier
        .backgroundColor("#0077cc")
        .size(
            transition.animateDp(
                { state ->
                    when (state) {
                        BoxState.Small -> 100.dp
                        BoxState.Large -> 200.dp
                    }
                }
            ),
            transition.animateDp(
                { state ->
                    when (state) {
                        BoxState.Small -> 100.dp
                        BoxState.Large -> 200.dp
                    }
                }
            )
        )
)
```

---

## Easing Functions

Predefined easing functions for controlling animation timing.

### Constants and Functions

```kotlin
package code.yousef.summon.animation

object EasingFunctions {
    val Linear: EasingFunction
    val EaseIn: EasingFunction
    val EaseOut: EasingFunction
    val EaseInOut: EasingFunction
    val EaseInQuad: EasingFunction
    val EaseOutQuad: EasingFunction
    val EaseInOutQuad: EasingFunction
    val EaseInCubic: EasingFunction
    val EaseOutCubic: EasingFunction
    val EaseInOutCubic: EasingFunction
    val EaseInQuart: EasingFunction
    val EaseOutQuart: EasingFunction
    val EaseInOutQuart: EasingFunction
    val EaseInQuint: EasingFunction
    val EaseOutQuint: EasingFunction
    val EaseInOutQuint: EasingFunction
    val EaseInElastic: EasingFunction
    val EaseOutElastic: EasingFunction
    val EaseInOutElastic: EasingFunction
    val EaseInBounce: EasingFunction
    val EaseOutBounce: EasingFunction
    val EaseInOutBounce: EasingFunction
}

typealias EasingFunction = (t: Float) -> Float

// Custom cubic bezier easing function
fun cubicBezier(x1: Float, y1: Float, x2: Float, y2: Float): EasingFunction
```

### Description

Easing functions control the rate of change of an animation's progress over time, giving animations a more natural feel.

### Example

```kotlin
val animation = createAnimation(
    initialValue = 0f,
    targetValue = 1f,
    duration = 1000,
    easing = EasingFunctions.EaseOutBounce,
    onUpdate = { value ->
        element.style.transform = "scale($value)"
    }
)

// Custom easing function
val customEasing = cubicBezier(0.17, 0.67, 0.83, 0.67)
```

---

## Animation Modifiers

Modifiers for applying animations to components.

### Modifier Functions

```kotlin
package code.yousef.summon.modifier

// Animate CSS properties
fun Modifier.animate(
    property: String,
    duration: Int = 300,
    delay: Int = 0,
    easing: EasingFunction = EasingFunctions.EaseInOut
): Modifier

// Animate transitions
fun Modifier.transition(
    properties: List<String>,
    duration: Int = 300,
    delay: Int = 0,
    easing: EasingFunction = EasingFunctions.EaseInOut
): Modifier

// Apply keyframe animations
fun Modifier.keyframes(
    name: String,
    duration: Int = 1000,
    delay: Int = 0,
    iterationCount: Int = 1,
    direction: AnimationDirection = AnimationDirection.Normal,
    fillMode: AnimationFillMode = AnimationFillMode.None,
    playState: AnimationPlayState = AnimationPlayState.Running
): Modifier
```

### Description

Animation modifiers provide a convenient way to apply animations to components using modifiers.

### Example

```kotlin
Text(
    text = "Hover Me",
    modifier = Modifier
        .transition(
            properties = listOf("transform", "color"),
            duration = 300,
            easing = EasingFunctions.EaseOut
        )
        .hoverStyle(
            Modifier
                .transform("scale(1.1)")
                .color("#0077cc")
        )
)

Box(
    modifier = Modifier
        .size(100.px)
        .backgroundColor("#0077cc")
        .keyframes(
            name = "pulse",
            duration = 2000,
            iterationCount = AnimationIterationCount.Infinite
        )
)
```

---

## Keyframes

API for defining keyframe animations.

### Functions and Classes

```kotlin
package code.yousef.summon.animation

class KeyframesBuilder {
    fun at(percentage: Int, block: KeyframeBuilder.() -> Unit)
    fun from(block: KeyframeBuilder.() -> Unit)
    fun to(block: KeyframeBuilder.() -> Unit)
}

fun defineKeyframes(name: String, builder: KeyframesBuilder.() -> Unit): String

class KeyframeBuilder {
    fun property(name: String, value: String)
    fun transform(value: String)
    fun opacity(value: Float)
    fun translateX(value: CSSSize)
    fun translateY(value: CSSSize)
    fun scale(value: Float)
    fun rotate(value: String)
    // ... other properties
}
```

### Description

The keyframes API allows defining complex animations with multiple steps or stages.

### Example

```kotlin
// Define keyframes animation
val bounceAnimation = defineKeyframes("bounce") {
    at(0) {
        transform("translateY(0)")
    }
    at(20) {
        transform("translateY(-30px)")
    }
    at(40) {
        transform("translateY(0)")
    }
    at(60) {
        transform("translateY(-15px)")
    }
    at(80) {
        transform("translateY(0)")
    }
    at(100) {
        transform("translateY(0)")
    }
}

// Apply the animation
Box(
    modifier = Modifier
        .size(100.px)
        .backgroundColor("#0077cc")
        .keyframes(
            name = bounceAnimation,
            duration = 2000,
            iterationCount = AnimationIterationCount.Infinite
        )
)
```

---

## Custom Animations

API for creating custom animations.

### Functions

```kotlin
package code.yousef.summon.animation

// Create a custom animation
fun createCustomAnimation(builder: CustomAnimationBuilder.() -> Unit): CustomAnimation

class CustomAnimationBuilder {
    fun property(name: String, from: String, to: String)
    fun transform(from: String, to: String)
    fun opacity(from: Float, to: Float)
    fun translateX(from: CSSSize, to: CSSSize)
    fun translateY(from: CSSSize, to: CSSSize)
    fun scale(from: Float, to: Float)
    fun rotate(from: String, to: String)
    // ... other properties
    
    fun duration(milliseconds: Int)
    fun delay(milliseconds: Int)
    fun easing(easing: EasingFunction)
    fun iterations(count: Int)
    fun direction(direction: AnimationDirection)
    fun fillMode(mode: AnimationFillMode)
}
```

### Description

Custom animations provide a way to define reusable animations that can be applied to different components.

### Example

```kotlin
val fadeAndScale = createCustomAnimation {
    opacity(from = 0f, to = 1f)
    scale(from = 0.8f, to = 1f)
    duration(500)
    easing(EasingFunctions.EaseOutCubic)
}

Box(
    modifier = Modifier
        .size(100.px)
        .backgroundColor("#0077cc")
        .applyAnimation(fadeAndScale)
)
```

---

## Animation Extensions

Convenient extension functions for common animation scenarios.

### Extension Functions

```kotlin
package code.yousef.summon.animation

// Animate numeric properties
@Composable
fun animateFloatAsState(
    targetValue: Float,
    animationSpec: AnimationSpec<Float> = spring(),
    visibilityThreshold: Float = 0.01f,
    label: String = "FloatAnimation"
): State<Float>

@Composable
fun animateIntAsState(
    targetValue: Int,
    animationSpec: AnimationSpec<Int> = spring(),
    label: String = "IntAnimation"
): State<Int>

// Animate size properties
@Composable
fun animateDpAsState(
    targetValue: Dp,
    animationSpec: AnimationSpec<Dp> = spring(),
    label: String = "DpAnimation"
): State<Dp>

// Animate color
@Composable
fun animateColorAsState(
    targetValue: Color,
    animationSpec: AnimationSpec<Color> = spring(),
    label: String = "ColorAnimation"
): State<Color>

// Animation specs
fun <T> spring(
    dampingRatio: Float = Spring.DampingRatioNoBouncy,
    stiffness: Float = Spring.StiffnessMedium,
    visibilityThreshold: T? = null
): SpringSpec<T>

fun <T> tween(
    durationMillis: Int = 300,
    delayMillis: Int = 0,
    easing: Easing = FastOutSlowInEasing
): TweenSpec<T>

fun <T> repeatable(
    iterations: Int,
    animation: DurationBasedAnimationSpec<T>,
    repeatMode: RepeatMode = RepeatMode.Restart
): RepeatableSpec<T>

fun <T> infiniteRepeatable(
    animation: DurationBasedAnimationSpec<T>,
    repeatMode: RepeatMode = RepeatMode.Restart
): InfiniteRepeatableSpec<T>
```

### Example

```kotlin
@Composable
fun AnimatedComponent() {
    var expanded by remember { mutableStateOf(false) }
    
    // Animate size
    val width by animateDpAsState(
        targetValue = if (expanded) 300.dp else 100.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    // Animate color
    val backgroundColor by animateColorAsState(
        targetValue = if (expanded) Color.Blue else Color.Red,
        animationSpec = tween(durationMillis = 500)
    )
    
    Box(
        modifier = Modifier
            .width(width)
            .height(100.dp)
            .backgroundColor(backgroundColor)
            .clickable { expanded = !expanded }
    )
}
```

---

## Animation Utils

Utility functions and classes for working with animations.

### Functions

```kotlin
package code.yousef.summon.animation

// Create animated values
fun Animatable(
    initialValue: Float,
    visibilityThreshold: Float = 0.01f
): Animatable<Float, AnimationVector1D>

// Animate to target value
suspend fun <T, V : AnimationVector> Animatable<T, V>.animateTo(
    targetValue: T,
    animationSpec: AnimationSpec<T> = spring(),
    initialVelocity: T? = null,
    block: (Animatable<T, V>.() -> Unit)? = null
): AnimationResult<T, V>

// Snap to value immediately
suspend fun <T, V : AnimationVector> Animatable<T, V>.snapTo(targetValue: T)

// Stop animation
fun <T, V : AnimationVector> Animatable<T, V>.stop()

// Animation state
data class AnimationState<T, V : AnimationVector>(
    val value: T,
    val velocity: V,
    val isRunning: Boolean,
    val finishedTimeNanos: Long
)
```

### Example

```kotlin
@Composable
fun DraggableComponent() {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    
    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .size(100.dp)
            .backgroundColor(Color.Blue)
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    coroutineScope.launch {
                        offsetX.animateTo(
                            targetValue = offsetX.value + delta,
                            animationSpec = spring(stiffness = Spring.StiffnessHigh)
                        )
                    }
                }
            )
    )
}
```

---

## Keyframes Generator

Advanced keyframe generation utilities.

### Functions

```kotlin
package code.yousef.summon.animation

// Generate keyframes from a sequence
fun generateKeyframes(
    name: String,
    frames: List<KeyframeDefinition>
): String

// Keyframe definition
data class KeyframeDefinition(
    val percentage: Int,
    val properties: Map<String, String>
)

// Keyframe builder DSL
fun keyframeSequence(
    name: String,
    builder: KeyframeSequenceBuilder.() -> Unit
): String

class KeyframeSequenceBuilder {
    fun frame(percentage: Int, builder: KeyframeBuilder.() -> Unit)
    fun frames(vararg percentages: Int, builder: KeyframeBuilder.() -> Unit)
    fun interpolate(
        from: Int,
        to: Int,
        property: String,
        fromValue: String,
        toValue: String,
        easing: EasingFunction = EasingFunctions.Linear
    )
}
```

### Example

```kotlin
// Complex keyframe animation
val complexAnimation = keyframeSequence("complexMove") {
    frame(0) {
        transform("translate(0, 0) scale(1)")
        opacity(1f)
    }
    
    // Interpolate between frames
    interpolate(
        from = 0,
        to = 50,
        property = "transform",
        fromValue = "translate(0, 0)",
        toValue = "translate(100px, 0)",
        easing = EasingFunctions.EaseInOut
    )
    
    frame(50) {
        transform("translate(100px, 0) scale(1.5)")
        backgroundColor("#ff0000")
    }
    
    frames(75, 100) {
        transform("translate(0, 0) scale(1)")
        opacity(0.5f)
    }
}
``` 