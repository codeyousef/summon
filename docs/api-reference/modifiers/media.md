# Media Modifiers

Modifier extensions for controlling video/audio behavior and responsive media.

## Import

```kotlin
import codes.yousef.summon.modifier.pauseOnScroll
import codes.yousef.summon.modifier.lazyLoad
import codes.yousef.summon.modifier.nativeLazyLoad
import codes.yousef.summon.modifier.aspectRatio
import codes.yousef.summon.modifier.responsiveMedia
```

## Overview

Media modifiers provide scroll-based media control, lazy loading, and responsive behavior for media elements.

---

## pauseOnScroll

Marks an element for pause-on-scroll behavior using IntersectionObserver.

### Signature

```kotlin
fun Modifier.pauseOnScroll(threshold: Float = 0.1f): Modifier
```

### Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `threshold` | `Float` | `0.1f` | Visibility threshold (0.0-1.0) at which to trigger |

### Description

When the element containing a `<video>` or `<audio>` child scrolls out of the viewport, the media will be paused. When it scrolls back into view, playback will resume if it was playing before.

This modifier adds data attributes that JavaScript observes using IntersectionObserver:
- `data-pause-on-scroll="true"`
- `data-pause-on-scroll-threshold="0.1"`

### Example

```kotlin
Box(modifier = Modifier().pauseOnScroll()) {
    Video(
        src = "hero-video.mp4",
        autoplay = true,
        muted = true,
        loop = true
    )
}

// With custom threshold (pause when 30% visible)
Box(modifier = Modifier().pauseOnScroll(threshold = 0.3f)) {
    Video(src = "content-video.mp4")
}
```

### JavaScript Integration

The framework automatically initializes IntersectionObserver for elements with `data-pause-on-scroll="true"`. Custom implementation:

```javascript
document.querySelectorAll('[data-pause-on-scroll="true"]').forEach(el => {
    const threshold = parseFloat(el.dataset.pauseOnScrollThreshold) || 0.1;
    const observer = new IntersectionObserver(entries => {
        entries.forEach(entry => {
            const video = el.querySelector('video');
            if (video) {
                if (entry.isIntersecting) {
                    if (video.dataset.wasPlaying === 'true') {
                        video.play();
                    }
                } else {
                    video.dataset.wasPlaying = !video.paused;
                    video.pause();
                }
            }
        });
    }, { threshold });
    observer.observe(el);
});
```

---

## lazyLoad

Marks an element for lazy loading when near the viewport.

### Signature

```kotlin
fun Modifier.lazyLoad(rootMargin: String = "200px"): Modifier
```

### Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `rootMargin` | `String` | `"200px"` | Distance from viewport to trigger loading |

### Description

Elements with this modifier will defer loading until they are within the specified distance of the viewport.

Adds data attributes:
- `data-lazy-load="true"`
- `data-lazy-load-margin="200px"`

### Example

```kotlin
// Lazy load images 200px before they enter viewport
Image(
    src = "large-image.jpg",
    modifier = Modifier().lazyLoad()
)

// Load 500px before viewport
Image(
    src = "hero-image.jpg",
    modifier = Modifier().lazyLoad(rootMargin = "500px")
)
```

---

## nativeLazyLoad

Enables native browser lazy loading using the `loading="lazy"` attribute.

### Signature

```kotlin
fun Modifier.nativeLazyLoad(): Modifier
```

### Description

Uses the browser's native lazy loading support. Simpler than IntersectionObserver-based lazy loading but with less control.

### Example

```kotlin
Image(
    src = "image.jpg",
    modifier = Modifier().nativeLazyLoad()
)
```

---

## aspectRatio

Sets the aspect ratio for responsive media containers.

### Signature

```kotlin
fun Modifier.aspectRatio(width: Int, height: Int): Modifier
```

### Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `width` | `Int` | Aspect width (e.g., 16) |
| `height` | `Int` | Aspect height (e.g., 9) |

### Description

Uses CSS `aspect-ratio` property for modern browsers. The element will maintain the specified ratio regardless of its actual content.

### Example

```kotlin
// 16:9 widescreen
Video(
    src = "video.mp4",
    modifier = Modifier().aspectRatio(16, 9)
)

// 4:3 standard
Image(
    src = "photo.jpg",
    modifier = Modifier().aspectRatio(4, 3)
)

// 1:1 square
Box(modifier = Modifier().aspectRatio(1, 1)) {
    // Content
}
```

---

## responsiveMedia

Makes media responsive while maintaining aspect ratio.

### Signature

```kotlin
fun Modifier.responsiveMedia(): Modifier
```

### Description

Applies styles for responsive media that fills its container width while maintaining correct height ratio:
- `width: 100%`
- `height: auto`
- `max-width: 100%`

### Example

```kotlin
// Responsive video
Video(
    src = "video.mp4",
    modifier = Modifier().responsiveMedia()
)

// Combine with aspect ratio
Video(
    src = "video.mp4",
    modifier = Modifier()
        .responsiveMedia()
        .aspectRatio(16, 9)
)
```

---

## Combining Modifiers

Media modifiers can be combined with other modifiers:

```kotlin
Box(
    modifier = Modifier()
        .width("100%")
        .maxWidth("800px")
        .pauseOnScroll(threshold = 0.5f)
) {
    Video(
        src = "content.mp4",
        autoplay = true,
        muted = true,
        modifier = Modifier()
            .responsiveMedia()
            .aspectRatio(16, 9)
            .borderRadius("8px")
    )
}
```

## See Also

- [Video Component](../components/display/Video.md)
- [Audio Component](../components/display/Audio.md)
- [Modifier API](../modifier.md)
