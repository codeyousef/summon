package codes.yousef.summon.components

import codes.yousef.summon.components.display.FetchPriority
import codes.yousef.summon.components.display.ImageDecoding
import codes.yousef.summon.components.display.ImageLoading
import codes.yousef.summon.components.display.ImageSource
import codes.yousef.summon.components.display.Picture
import codes.yousef.summon.components.display.ResponsiveImage
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.html.Audio as HtmlAudio
import codes.yousef.summon.components.html.Caption
import codes.yousef.summon.components.html.Col
import codes.yousef.summon.components.html.Colgroup
import codes.yousef.summon.components.html.Embed
import codes.yousef.summon.components.html.Figcaption
import codes.yousef.summon.components.html.Figure
import codes.yousef.summon.components.html.Iframe
import codes.yousef.summon.components.html.Meter
import codes.yousef.summon.components.html.ObjectTag
import codes.yousef.summon.components.html.Param
import codes.yousef.summon.components.html.Source
import codes.yousef.summon.components.html.Table
import codes.yousef.summon.components.html.Tbody
import codes.yousef.summon.components.html.Td
import codes.yousef.summon.components.html.Tfoot
import codes.yousef.summon.components.html.Th
import codes.yousef.summon.components.html.Thead
import codes.yousef.summon.components.html.Tr
import codes.yousef.summon.components.html.Track
import codes.yousef.summon.components.media.Audio as MediaAudio
import codes.yousef.summon.components.media.Video
import codes.yousef.summon.components.media.VideoSource
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.id
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

class SemanticMediaRenderingTest {
    @Test
    fun semanticTablesAndEmbeddedMediaRetainAllOptionalAttributes() {
        val html = PlatformRenderer().renderComposableRoot {
            Table(Modifier().id("results")) {
                Caption { Text("Results") }
                Colgroup(span = 2) {
                    Col(span = 1)
                    Col()
                }
                Thead {
                    Tr { Th("col", 2, 1, "head", "Hdr") { Text("Header") } }
                }
                Tbody {
                    Tr { Td(2, 1, "head") { Text("Value") } }
                    Tr { Td { Text("Default") } }
                }
                Tfoot { Tr { Td { Text("Footer") } } }
            }
            Figure {
                Iframe("/frame", "Frame", "640", "480", "allow-scripts", "fullscreen", "lazy", "no-referrer")
                Embed("/document.pdf", "application/pdf", "320", "240")
                ObjectTag("/object", "image/svg+xml", "10", "20", "preview", "form-id") {
                    Param("quality", "high")
                    Text("Fallback")
                }
                Figcaption { Text("Media caption") }
            }
            Iframe("/minimal", "Minimal")
            Embed("/minimal", "text/plain")
            ObjectTag { Text("Object fallback") }
            Source("/movie.mp4", "video/mp4", "image-2x.png 2x", "100vw", "(min-width: 40rem)")
            Source()
            Track("/captions.vtt", "captions", "en", "English", true)
            Track("/default.vtt")
            HtmlAudio("/audio.ogg", controls = true, autoplay = true, loop = true, muted = true, preload = "auto") {
                Source(src = "/audio.mp3", type = "audio/mpeg")
            }
            HtmlAudio(controls = false)
            Meter(0.75, 0.0, 1.0, 0.2, 0.8, 0.5) { Text("75 percent") }
            Meter(0.5)
        }

        listOf("<table", "<thead", "<tbody", "<tfoot", "<th", "<td", "<figure", "<figcaption", "<iframe", "<embed", "<object", "<param", "<source", "<track", "<audio", "<meter")
            .forEach { assertContains(html, it) }
        assertContains(html, "sandbox=\"allow-scripts\"")
        assertContains(html, "referrerpolicy=\"no-referrer\"")
        assertContains(html, "colspan=\"2\"")
        assertTrue(Regex("""<track[^>]*\bdefault(?:="[^"]*")?""").containsMatchIn(html))
        assertContains(html, "optimum=\"0.5\"")
    }

    @Test
    fun responsivePicturesVideoAndAudioRenderFullAndMinimalVariants() {
        val html = PlatformRenderer().renderComposableRoot {
            Picture(
                sources = listOf(
                    ImageSource("hero.avif 1x", "image/avif", "(min-width: 60rem)"),
                    ImageSource("hero.webp 1x")
                ),
                fallbackSrc = "hero.jpg",
                alt = "Hero",
                width = "800",
                height = "600",
                loading = ImageLoading.EAGER,
                fetchPriority = FetchPriority.HIGH,
                decoding = ImageDecoding.ASYNC
            )
            Picture(emptyList(), "fallback.jpg", "Fallback")
            ResponsiveImage(
                baseSrc = "/images/card",
                extension = "png",
                alt = "Card",
                widths = listOf(320, 640),
                sizes = "50vw",
                width = "640",
                height = "480",
                loading = ImageLoading.AUTO,
                fetchPriority = FetchPriority.LOW,
                includeAvif = true,
                includeWebp = true
            )
            ResponsiveImage(
                baseSrc = "/images/plain",
                extension = "jpg",
                alt = "Plain",
                includeAvif = false,
                includeWebp = false
            )
            Video(
                src = "ignored.mp4",
                sources = listOf(VideoSource("movie.webm", "video/webm"), VideoSource("movie.mp4", "video/mp4")),
                poster = "poster.jpg",
                autoplay = true,
                muted = false,
                loop = true,
                controls = false,
                playsInline = true,
                preload = "auto",
                crossorigin = "anonymous",
                width = "640",
                height = "360",
                ariaLabel = "Movie"
            )
            Video(src = "single.mp4", playsInline = false)
            Video()
            MediaAudio("sound.mp3", autoplay = true, muted = true, loop = true, controls = false, preload = "auto", ariaLabel = "Sound")
            MediaAudio("minimal.mp3")
        }

        assertContains(html, "hero.avif 1x")
        assertContains(html, "fetchpriority=\"high\"")
        assertContains(html, "/images/card-640.avif 640w")
        assertContains(html, "/images/plain-")
        assertContains(html, "movie.webm")
        assertTrue(Regex("""<video[^>]*\bmuted(?:="[^"]*")?""").containsMatchIn(html))
        assertContains(html, "aria-label=\"Movie\"")
        assertContains(html, "sound.mp3")
    }
}
