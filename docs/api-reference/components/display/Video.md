# Video Component

Type-safe video element component with browser policy enforcement.

## Import

```kotlin
import codes.yousef.summon.components.media.Video
import codes.yousef.summon.components.media.VideoSource
```

## Overview

The `Video` component provides a type-safe video element that maps Kotlin booleans to proper HTML5 video attributes and enforces browser policies (e.g., autoplay requires muted).

## Function Signature

```kotlin
@Composable
fun Video(
    src: String? = null,
    sources: List<VideoSource> = emptyList(),
    poster: String? = null,
    autoplay: Boolean = false,
    muted: Boolean = false,
    loop: Boolean = false,
    controls: Boolean = true,
    playsInline: Boolean = true,
    preload: String = "metadata",
    crossorigin: String? = null,
    width: String? = null,
    height: String? = null,
    ariaLabel: String? = null,
    modifier: Modifier = Modifier()
)
```

## Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `src` | `String?` | `null` | Primary video source URL (ignored if sources is provided) |
| `sources` | `List<VideoSource>` | `emptyList()` | List of video sources for format fallback |
| `poster` | `String?` | `null` | URL to poster image displayed before playback |
| `autoplay` | `Boolean` | `false` | Whether to start playing automatically (requires muted) |
| `muted` | `Boolean` | `false` | Whether the video is muted |
| `loop` | `Boolean` | `false` | Whether to loop the video |
| `controls` | `Boolean` | `true` | Whether to show playback controls |
| `playsInline` | `Boolean` | `true` | Whether to play inline on mobile (not fullscreen) |
| `preload` | `String` | `"metadata"` | Preload strategy: "none", "metadata", or "auto" |
| `crossorigin` | `String?` | `null` | CORS setting: "anonymous" or "use-credentials" |
| `width` | `String?` | `null` | Video width (can also use modifier) |
| `height` | `String?` | `null` | Video height (can also use modifier) |
| `ariaLabel` | `String?` | `null` | Accessibility label for the video |
| `modifier` | `Modifier` | `Modifier()` | Styling modifier for the video element |

## Browser Autoplay Policy

Modern browsers require videos to be muted for autoplay to work. If you set `autoplay = true` with `muted = false`, the component will **automatically set `muted = true`** to comply with browser policies and log a warning.

## VideoSource

Data class for defining multiple video sources with format fallback:

```kotlin
data class VideoSource(
    val src: String,   // URL to the video file
    val type: String   // MIME type (e.g., "video/mp4", "video/webm")
)
```

## Examples

### Basic Usage

```kotlin
Video(
    src = "https://example.com/video.mp4",
    modifier = Modifier().width("100%").maxWidth("800px")
)
```

### Autoplay Hero Video

```kotlin
// Muted is auto-enforced for autoplay
Video(
    src = "https://example.com/hero.mp4",
    autoplay = true,
    loop = true,
    controls = false,
    poster = "https://example.com/poster.jpg"
)
```

### Multiple Sources for Format Fallback

```kotlin
Video(
    sources = listOf(
        VideoSource("video.webm", "video/webm"),
        VideoSource("video.mp4", "video/mp4")
    ),
    controls = true
)
```

### Responsive Video

```kotlin
Video(
    src = "video.mp4",
    modifier = Modifier()
        .responsiveMedia()
        .aspectRatio(16, 9)
)
```

### Pause on Scroll

```kotlin
Box(modifier = Modifier().pauseOnScroll()) {
    Video(
        src = "hero-video.mp4",
        autoplay = true,
        muted = true,
        loop = true
    )
}
```

## MIME Type Inference

When using `src` instead of `sources`, the component automatically infers the MIME type from the file extension:

| Extension | MIME Type |
|-----------|-----------|
| `.mp4` | `video/mp4` |
| `.webm` | `video/webm` |
| `.ogg` | `video/ogg` |
| `.mov` | `video/quicktime` |

## Accessibility

- Use `ariaLabel` to provide descriptive text for screen readers
- Always include `controls = true` unless the video is purely decorative
- Consider providing captions for videos with spoken content

## See Also

- [Audio Component](Audio.md)
- [Media Modifiers](../modifiers/media.md)
- [Accessibility Guide](../../accessibility-guide.md)
