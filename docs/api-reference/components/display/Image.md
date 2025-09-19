# Image Component

The Image component provides a cross-platform solution for displaying images in Summon applications. It offers
comprehensive support for accessibility, loading strategies, responsive design, and error handling.

## Overview

Images are fundamental visual elements in modern web applications. The Summon Image component provides:

- **Accessibility First**: Built-in alt text and ARIA labels
- **Performance Optimization**: Lazy loading and multiple loading strategies
- **Responsive Design**: Flexible sizing and aspect ratio control
- **Error Handling**: Graceful fallbacks for failed image loads
- **Cross-Platform**: Consistent behavior across browser and JVM environments

## Basic Usage

### Simple Image

```kotlin
import code.yousef.summon.components.display.Image

@Composable
fun SimpleImageExample() {
    Image(
        src = "https://example.com/photo.jpg",
        alt = "A beautiful landscape",
        modifier = Modifier()
            .width("300px")
            .height("200px")
    )
}
```

### Responsive Image

```kotlin
@Composable
fun ResponsiveImageExample() {
    Image(
        src = "https://example.com/hero-image.jpg",
        alt = "Hero banner image",
        modifier = Modifier()
            .width("100%")
            .maxWidth("800px")
            .height("auto")
            .objectFit("cover")
    )
}
```

### Image with Loading Strategy

```kotlin
@Composable
fun LazyImageExample() {
    Image(
        src = "https://example.com/large-image.jpg",
        alt = "Large detailed image",
        loading = ImageLoading.LAZY,
        modifier = Modifier()
            .width("100%")
            .aspectRatio("16:9")
    )
}
```

## API Reference

### Image

The main image component with comprehensive configuration options.

```kotlin
@Composable
fun Image(
    src: String,
    alt: String,
    modifier: Modifier = Modifier(),
    contentDescription: String? = null,
    loading: ImageLoading = ImageLoading.LAZY,
    width: String? = null,
    height: String? = null,
    onLoad: (() -> Unit)? = null,
    onError: (() -> Unit)? = null
)
```

#### Parameters

| Parameter            | Type            | Default             | Description                             |
|----------------------|-----------------|---------------------|-----------------------------------------|
| `src`                | `String`        | Required            | Image source URL or path                |
| `alt`                | `String`        | Required            | Alternative text for accessibility      |
| `modifier`           | `Modifier`      | `Modifier()`        | Styling and layout modifier             |
| `contentDescription` | `String?`       | `null`              | Detailed description for screen readers |
| `loading`            | `ImageLoading`  | `ImageLoading.LAZY` | Loading strategy for the image          |
| `width`              | `String?`       | `null`              | Width in CSS units (prefer modifier)    |
| `height`             | `String?`       | `null`              | Height in CSS units (prefer modifier)   |
| `onLoad`             | `(() -> Unit)?` | `null`              | Callback when image loads successfully  |
| `onError`            | `(() -> Unit)?` | `null`              | Callback when image fails to load       |

### ImageLoading

Enum defining different image loading strategies.

```kotlin
enum class ImageLoading(val value: String) {
    LAZY("lazy"),    // Load when entering viewport
    EAGER("eager"),  // Load immediately
    AUTO("auto")     // Browser decides
}
```

## Advanced Examples

### Image Gallery with Error Handling

```kotlin
@Composable
fun ImageGallery(images: List<ImageData>) {
    LazyRow(
        modifier = Modifier()
            .fillMaxWidth()
            .padding("16px"),
        horizontalSpacing = "12px"
    ) {
        items(images) { imageData ->
            var hasError by remember { mutableStateOf(false) }
            var isLoading by remember { mutableStateOf(true) }

            Box(
                modifier = Modifier()
                    .width("200px")
                    .height("150px")
                    .borderRadius("8px")
                    .backgroundColor("#F5F5F5")
                    .position("relative")
            ) {
                if (!hasError) {
                    Image(
                        src = imageData.src,
                        alt = imageData.alt,
                        loading = ImageLoading.LAZY,
                        modifier = Modifier()
                            .fillSize()
                            .borderRadius("8px")
                            .objectFit("cover"),
                        onLoad = { isLoading = false },
                        onError = {
                            hasError = true
                            isLoading = false
                        }
                    )
                }

                // Error state
                if (hasError) {
                    Box(
                        modifier = Modifier()
                            .fillSize()
                            .backgroundColor("#EEEEEE")
                            .display("flex")
                            .alignItems("center")
                            .justifyContent("center")
                    ) {
                        Column(
                            horizontalAlignment = "center"
                        ) {
                            Icon(
                                name = "broken_image",
                                size = "32px",
                                color = "#BDBDBD"
                            )
                            Text(
                                text = "Failed to load",
                                fontSize = "12px",
                                color = "#757575"
                            )
                        }
                    }
                }

                // Loading state
                if (isLoading && !hasError) {
                    Box(
                        modifier = Modifier()
                            .fillSize()
                            .backgroundColor("rgba(255, 255, 255, 0.8)")
                            .display("flex")
                            .alignItems("center")
                            .justifyContent("center")
                    ) {
                        LoadingSpinner(size = "24px")
                    }
                }
            }
        }
    }
}
```

### Hero Image with Overlay

```kotlin
@Composable
fun HeroSection(
    imageUrl: String,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier()
            .width("100%")
            .height("400px")
            .position("relative")
            .overflow("hidden")
    ) {
        // Background image
        Image(
            src = imageUrl,
            alt = "Hero background",
            loading = ImageLoading.EAGER,
            modifier = Modifier()
                .fillSize()
                .objectFit("cover")
                .objectPosition("center")
        )

        // Dark overlay for text readability
        Box(
            modifier = Modifier()
                .fillSize()
                .backgroundColor("rgba(0, 0, 0, 0.4)")
                .position("absolute")
                .top("0")
                .left("0")
        )

        // Text content
        Column(
            modifier = Modifier()
                .position("absolute")
                .top("50%")
                .left("50%")
                .transform("translate(-50%, -50%)")
                .textAlign("center"),
            verticalSpacing = "16px"
        ) {
            Text(
                text = title,
                fontSize = "3rem",
                fontWeight = "bold",
                color = "white",
                lineHeight = "1.2"
            )
            Text(
                text = subtitle,
                fontSize = "1.25rem",
                color = "rgba(255, 255, 255, 0.9)",
                lineHeight = "1.4"
            )
        }
    }
}
```

### Progressive Image Loading

```kotlin
@Composable
fun ProgressiveImage(
    lowResSrc: String,
    highResSrc: String,
    alt: String,
    modifier: Modifier = Modifier()
) {
    var highResLoaded by remember { mutableStateOf(false) }
    var showLowRes by remember { mutableStateOf(true) }

    Box(modifier = modifier.position("relative")) {
        // Low resolution placeholder
        if (showLowRes) {
            Image(
                src = lowResSrc,
                alt = alt,
                loading = ImageLoading.EAGER,
                modifier = Modifier()
                    .fillSize()
                    .filter("blur(5px)")
                    .transition("opacity 0.3s ease"),
                onLoad = {
                    // Start loading high-res image
                }
            )
        }

        // High resolution image
        Image(
            src = highResSrc,
            alt = alt,
            loading = ImageLoading.LAZY,
            modifier = Modifier()
                .fillSize()
                .opacity(if (highResLoaded) "1" else "0")
                .transition("opacity 0.5s ease"),
            onLoad = {
                highResLoaded = true
                showLowRes = false
            }
        )
    }
}
```

### Avatar Component

```kotlin
@Composable
fun Avatar(
    imageUrl: String?,
    name: String,
    size: AvatarSize = AvatarSize.MEDIUM,
    modifier: Modifier = Modifier()
) {
    val sizeValue = when (size) {
        AvatarSize.SMALL -> "32px"
        AvatarSize.MEDIUM -> "48px"
        AvatarSize.LARGE -> "64px"
        AvatarSize.EXTRA_LARGE -> "96px"
    }

    Box(
        modifier = modifier
            .width(sizeValue)
            .height(sizeValue)
            .borderRadius("50%")
            .overflow("hidden")
            .backgroundColor("#E0E0E0")
            .display("flex")
            .alignItems("center")
            .justifyContent("center")
    ) {
        if (imageUrl != null) {
            var imageError by remember { mutableStateOf(false) }

            if (!imageError) {
                Image(
                    src = imageUrl,
                    alt = "$name's avatar",
                    loading = ImageLoading.LAZY,
                    modifier = Modifier()
                        .fillSize()
                        .objectFit("cover"),
                    onError = { imageError = true }
                )
            } else {
                // Fallback to initials
                AvatarFallback(name = name, size = size)
            }
        } else {
            // No image provided, show initials
            AvatarFallback(name = name, size = size)
        }
    }
}

@Composable
private fun AvatarFallback(name: String, size: AvatarSize) {
    val initials = name.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull() }
        .joinToString("")
        .uppercase()

    val fontSize = when (size) {
        AvatarSize.SMALL -> "14px"
        AvatarSize.MEDIUM -> "18px"
        AvatarSize.LARGE -> "24px"
        AvatarSize.EXTRA_LARGE -> "36px"
    }

    Text(
        text = initials,
        fontSize = fontSize,
        fontWeight = "medium",
        color = "#757575",
        userSelect = "none"
    )
}

enum class AvatarSize {
    SMALL, MEDIUM, LARGE, EXTRA_LARGE
}
```

## Accessibility Guidelines

### Alternative Text Best Practices

```kotlin
// Good - Descriptive alt text
Image(
    src = "chart.png",
    alt = "Sales increased 25% from Q1 to Q2 2024"
)

// Bad - Redundant or non-descriptive
Image(
    src = "chart.png",
    alt = "Chart" // Too generic
)

// Decorative images
Image(
    src = "decoration.svg",
    alt = "", // Empty alt for decorative images
    role = "presentation"
)
```

### Content Descriptions

```kotlin
// Complex images need detailed descriptions
Image(
    src = "infographic.jpg",
    alt = "2024 Climate Report Summary",
    contentDescription = """
        Infographic showing global temperature rise of 1.2°C since 1880,
        with renewable energy adoption at 30% worldwide and carbon
        emissions reduced by 15% in developed countries.
    """.trimIndent()
)
```

### Interactive Images

```kotlin
// Clickable images need proper semantics
@Composable
fun ClickableImage(
    src: String,
    alt: String,
    onClick: () -> Unit
) {
    Image(
        src = src,
        alt = alt,
        modifier = Modifier()
            .cursor("pointer")
            .role("button")
            .tabIndex(0)
            .onKeyDown { event ->
                if (event.key == "Enter" || event.key == " ") {
                    onClick()
                }
            }
            .onClick { onClick() }
    )
}
```

## Performance Optimization

### Loading Strategies

```kotlin
// Critical images - load immediately
Image(
    src = "logo.svg",
    alt = "Company Logo",
    loading = ImageLoading.EAGER
)

// Below-the-fold images - lazy load
Image(
    src = "article-image.jpg",
    alt = "Article illustration",
    loading = ImageLoading.LAZY
)

// Let browser decide
Image(
    src = "profile-picture.jpg",
    alt = "User profile",
    loading = ImageLoading.AUTO
)
```

### Responsive Images

```kotlin
@Composable
fun ResponsiveImage(
    baseUrl: String,
    alt: String,
    modifier: Modifier = Modifier()
) {
    val breakpoint = LocalBreakpoint.current

    val imageSrc = when (breakpoint) {
        Breakpoint.MOBILE -> "${baseUrl}_mobile.jpg"
        Breakpoint.TABLET -> "${baseUrl}_tablet.jpg"
        Breakpoint.DESKTOP -> "${baseUrl}_desktop.jpg"
    }

    Image(
        src = imageSrc,
        alt = alt,
        loading = ImageLoading.LAZY,
        modifier = modifier
    )
}
```

### Image Optimization Tips

1. **Use WebP format** when possible for better compression
2. **Implement srcset-like behavior** for different screen densities
3. **Lazy load** images below the fold
4. **Provide fallbacks** for unsupported formats
5. **Optimize image dimensions** to match display size

## Platform-Specific Behavior

### Browser Platform

- Supports all modern image formats (WebP, AVIF, etc.)
- Native lazy loading with Intersection Observer
- Full CSS styling and effects support
- Event handling for load/error states

### JVM Platform

- Image rendering depends on output format
- May convert images for compatibility
- Limited styling options in some contexts
- Error handling varies by renderer

## Common Patterns

### Image Cards

```kotlin
@Composable
fun ImageCard(
    imageUrl: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier()
            .width("300px")
            .cursor("pointer")
            .elevation("2px")
            .borderRadius("8px")
            .onClick { onClick() }
    ) {
        Column {
            Image(
                src = imageUrl,
                alt = title,
                loading = ImageLoading.LAZY,
                modifier = Modifier()
                    .width("100%")
                    .height("200px")
                    .objectFit("cover")
            )

            Column(
                modifier = Modifier().padding("16px"),
                verticalSpacing = "8px"
            ) {
                Text(
                    text = title,
                    fontSize = "18px",
                    fontWeight = "semibold"
                )
                Text(
                    text = description,
                    fontSize = "14px",
                    color = "#666666",
                    lineClamp = 2
                )
            }
        }
    }
}
```

### Image Carousel

```kotlin
@Composable
fun ImageCarousel(
    images: List<String>,
    modifier: Modifier = Modifier()
) {
    var currentIndex by remember { mutableStateOf(0) }

    Box(modifier = modifier.position("relative")) {
        Image(
            src = images[currentIndex],
            alt = "Image ${currentIndex + 1} of ${images.size}",
            loading = ImageLoading.EAGER,
            modifier = Modifier()
                .width("100%")
                .aspectRatio("16:9")
                .objectFit("cover")
        )

        // Navigation buttons
        Row(
            modifier = Modifier()
                .position("absolute")
                .bottom("16px")
                .left("50%")
                .transform("translateX(-50%)")
                .backgroundColor("rgba(0, 0, 0, 0.5)")
                .borderRadius("20px")
                .padding("8px"),
            horizontalSpacing = "8px"
        ) {
            Button(
                text = "Previous",
                enabled = currentIndex > 0,
                onClick = { currentIndex-- }
            )

            Text(
                text = "${currentIndex + 1} / ${images.size}",
                color = "white",
                fontSize = "14px"
            )

            Button(
                text = "Next",
                enabled = currentIndex < images.size - 1,
                onClick = { currentIndex++ }
            )
        }
    }
}
```

## Testing

### Unit Testing

```kotlin
@Test
fun testImageRendering() {
    val mockRenderer = MockPlatformRenderer()

    CompositionLocal.provideComposer(MockComposer()) {
        LocalPlatformRenderer.provides(mockRenderer) {
            Image(
                src = "test-image.jpg",
                alt = "Test image"
            )

            assertTrue(mockRenderer.renderImageCalled)
            assertEquals("test-image.jpg", mockRenderer.lastImageSrcRendered)
            assertEquals("Test image", mockRenderer.lastImageAltRendered)
        }
    }
}
```

### Accessibility Testing

```kotlin
@Test
fun testImageAccessibility() {
    val mockRenderer = MockPlatformRenderer()

    CompositionLocal.provideComposer(MockComposer()) {
        LocalPlatformRenderer.provides(mockRenderer) {
            Image(
                src = "complex-chart.svg",
                alt = "Sales chart",
                contentDescription = "Detailed sales data for Q1-Q4"
            )

            assertEquals("Sales chart", mockRenderer.lastImageAltRendered)
            // Verify contentDescription is handled appropriately
        }
    }
}
```

## Migration Guide

### From HTML img tag

```html
<!-- Before: HTML -->
<img src="image.jpg" alt="Description" width="300" height="200" loading="lazy">
```

```kotlin
// After: Summon Image
Image(
    src = "image.jpg",
    alt = "Description",
    loading = ImageLoading.LAZY,
    modifier = Modifier()
        .width("300px")
        .height("200px")
)
```

### From React/Next.js Image

```jsx
// React/Next.js
<Image
  src="/image.jpg"
  alt="Description"
  width={300}
  height={200}
  loading="lazy"
/>
```

```kotlin
// Summon equivalent
Image(
    src = "/image.jpg",
    alt = "Description",
    loading = ImageLoading.LAZY,
    modifier = Modifier()
        .width("300px")
        .height("200px")
)
```

## Best Practices

1. **Always provide alt text** - Essential for accessibility
2. **Use appropriate loading strategies** - Lazy load below-the-fold images
3. **Handle error states** - Provide fallbacks for failed loads
4. **Optimize image sizes** - Match image dimensions to display size
5. **Consider performance** - Use modern formats and compression
6. **Test accessibility** - Verify with screen readers
7. **Implement responsive design** - Adapt to different screen sizes

The Image component provides a robust foundation for displaying images across your Summon application while maintaining
performance, accessibility, and cross-platform compatibility.