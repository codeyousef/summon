# Audio Component

Type-safe audio element component.

## Import

```kotlin
import codes.yousef.summon.components.media.Audio
```

## Overview

The `Audio` component provides a type-safe audio element for playing audio content.

## Function Signature

```kotlin
@Composable
fun Audio(
    src: String,
    autoplay: Boolean = false,
    muted: Boolean = false,
    loop: Boolean = false,
    controls: Boolean = true,
    preload: String = "metadata",
    ariaLabel: String? = null,
    modifier: Modifier = Modifier()
)
```

## Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `src` | `String` | Required | URL to the audio file |
| `autoplay` | `Boolean` | `false` | Whether to start playing automatically |
| `muted` | `Boolean` | `false` | Whether the audio is muted |
| `loop` | `Boolean` | `false` | Whether to loop the audio |
| `controls` | `Boolean` | `true` | Whether to show playback controls |
| `preload` | `String` | `"metadata"` | Preload strategy: "none", "metadata", or "auto" |
| `ariaLabel` | `String?` | `null` | Accessibility label for the audio |
| `modifier` | `Modifier` | `Modifier()` | Styling modifier for the audio element |

## Examples

### Basic Usage

```kotlin
Audio(
    src = "https://example.com/audio.mp3",
    controls = true
)
```

### Background Music (Looped)

```kotlin
Audio(
    src = "background.mp3",
    autoplay = true,
    loop = true,
    controls = false
)
```

### With Accessibility Label

```kotlin
Audio(
    src = "podcast-episode.mp3",
    controls = true,
    ariaLabel = "Podcast Episode 42: Introduction to Kotlin"
)
```

## Accessibility

- Use `ariaLabel` to provide descriptive text for screen readers
- Always include `controls = true` to allow users to control playback
- Consider providing transcripts for audio content

## See Also

- [Video Component](Video.md)
- [Media Modifiers](../modifiers/media.md)
