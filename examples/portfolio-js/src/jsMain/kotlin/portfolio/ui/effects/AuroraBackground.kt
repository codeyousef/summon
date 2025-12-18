package portfolio.ui.effects

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.extensions.px
import codes.yousef.summon.extensions.vh
import codes.yousef.summon.extensions.vw
import codes.yousef.summon.modifier.*
import codes.yousef.summon.modifier.LayoutModifiers.left
import codes.yousef.summon.modifier.LayoutModifiers.top
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin

/**
 * Aurora background configuration.
 */
data class AuroraConfig(
    val canvasId: String = "aurora-canvas",
    val timeScale: Float = 1.0f
)

/**
 * Aurora color palette using IQ's palette formula coefficients.
 */
data class AuroraPalette(
    val name: String,
    val a: Triple<Float, Float, Float>,
    val b: Triple<Float, Float, Float>,
    val c: Triple<Float, Float, Float>,
    val d: Triple<Float, Float, Float>
)

object AuroraPalettes {
    val COSMIC = AuroraPalette(
        name = "Cosmic",
        a = Triple(0.12f, 0.25f, 0.18f),
        b = Triple(0.18f, 0.22f, 0.15f),
        c = Triple(1.0f, 1.0f, 0.5f),
        d = Triple(0.0f, 0.33f, 0.67f)
    )
    val DEFAULT = COSMIC
}

/**
 * Aurora background effect using Canvas2D.
 * Creates flowing aurora ribbons with IQ palette colors.
 */
@Composable
fun AuroraBackground(
    config: AuroraConfig = AuroraConfig(),
    palette: AuroraPalette = AuroraPalettes.DEFAULT
) {
    // Container for the canvas
    Box(
        modifier = Modifier()
            .position(Position.Fixed)
            .top(0.px)
            .left(0.px)
            .width(100.vw)
            .height(100.vh)
            .zIndex(0)
            .pointerEvents(PointerEvents.None)
    ) {
        // Create canvas using raw HTML
        val renderer = LocalPlatformRenderer.current
        renderer.renderRawHtml(
            """<canvas id="${config.canvasId}" style="width:100%;height:100%;display:block;"></canvas>"""
        )
    }
    
    // Initialize the canvas animation after render
    initAuroraCanvas(config.canvasId, palette, config.timeScale)
}

private var auroraInitialized = mutableSetOf<String>()

private fun initAuroraCanvas(canvasId: String, palette: AuroraPalette, timeScale: Float) {
    // Use setTimeout to ensure DOM is ready
    window.setTimeout({
        if (canvasId in auroraInitialized) return@setTimeout
        
        val canvas = document.getElementById(canvasId) as? HTMLCanvasElement
        if (canvas == null) {
            console.log("Aurora: Canvas not found, retrying...")
            window.setTimeout({ initAuroraCanvas(canvasId, palette, timeScale) }, 100)
            return@setTimeout
        }
        
        auroraInitialized.add(canvasId)
        console.log("Aurora: Initializing canvas $canvasId")
        
        val ctx = canvas.getContext("2d") as? CanvasRenderingContext2D
        if (ctx == null) {
            console.error("Aurora: Failed to get 2D context")
            return@setTimeout
        }
        
        // Start render loop
        var startTime = 0.0
        
        fun render(timestamp: Double) {
            if (startTime == 0.0) startTime = timestamp
            val time = ((timestamp - startTime) / 1000.0 * timeScale).toFloat()
            
            // Resize canvas to match display size
            val dpr = window.devicePixelRatio
            val displayWidth = (canvas.clientWidth * dpr).toInt()
            val displayHeight = (canvas.clientHeight * dpr).toInt()
            
            if (canvas.width != displayWidth || canvas.height != displayHeight) {
                canvas.width = displayWidth
                canvas.height = displayHeight
            }
            
            // Clear and draw
            renderAurora(ctx, canvas.width, canvas.height, time, palette)
            
            window.requestAnimationFrame(::render)
        }
        
        window.requestAnimationFrame(::render)
    }, 0)
}

private fun renderAurora(
    ctx: CanvasRenderingContext2D,
    width: Int,
    height: Int,
    time: Float,
    palette: AuroraPalette
) {
    // Dark sky background
    ctx.fillStyle = "#010103"
    ctx.fillRect(0.0, 0.0, width.toDouble(), height.toDouble())
    
    val aspect = width.toFloat() / height.toFloat()
    val t = time * 0.3f
    
    // Render aurora as horizontal lines with varying intensity
    for (y in 0 until height step 2) {
        val uy = y.toFloat() / height.toFloat()
        
        for (x in 0 until width step 4) {
            val ux = x.toFloat() / width.toFloat()
            val px = ux * aspect
            
            // Wave distortion
            val wave = sin(px * 2.0f + t) * 0.05f +
                      sin(px * 1.5f - t * 0.7f) * 0.07f +
                      sin(px * 4.0f + t * 1.3f) * 0.03f
            
            // Aurora band position
            val auroraY = 0.7f + wave
            val dist = uy - auroraY
            
            // Glow calculation
            var glow = exp(-dist * dist * 15.0f)
            val lowerGlow = exp(-(dist + 0.2f).pow(2) * 6.0f) * 0.5f
            glow += lowerGlow
            
            // Vertical rays
            var rays = sin(px * 15.0f + t * 2.0f) * 0.15f + 0.85f
            rays *= sin(px * 8.0f - t) * 0.1f + 0.9f
            glow *= rays
            
            // Edge fade
            glow *= smoothstep(0.0f, 0.2f, uy)
            glow *= smoothstep(1.0f, 0.9f, uy)
            
            if (glow > 0.01f) {
                // Color from palette
                val colorT = sin(px * 1.5f + t * 0.3f) * 0.3f + 0.5f + dist
                val color = iqPalette(colorT, palette)
                
                // Apply glow intensity
                val r = (color.first * glow * 0.7f * 255).toInt().coerceIn(0, 255)
                val g = (color.second * glow * 0.7f * 255).toInt().coerceIn(0, 255)
                val b = (color.third * glow * 0.7f * 255).toInt().coerceIn(0, 255)
                
                ctx.fillStyle = "rgb($r,$g,$b)"
                ctx.fillRect(x.toDouble(), y.toDouble(), 4.0, 2.0)
            }
        }
    }
}

private fun iqPalette(t: Float, p: AuroraPalette): Triple<Float, Float, Float> {
    val TAU = 6.283185f
    return Triple(
        p.a.first + p.b.first * cos(TAU * (p.c.first * t + p.d.first)),
        p.a.second + p.b.second * cos(TAU * (p.c.second * t + p.d.second)),
        p.a.third + p.b.third * cos(TAU * (p.c.third * t + p.d.third))
    )
}

private fun smoothstep(edge0: Float, edge1: Float, x: Float): Float {
    val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0.0f, 1.0f)
    return t * t * (3.0f - 2.0f * t)
}
