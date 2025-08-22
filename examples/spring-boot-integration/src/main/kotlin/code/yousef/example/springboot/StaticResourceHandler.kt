package code.yousef.example.springboot

import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.io.IOException

/**
 * Static resource handler for serving Summon hydration JavaScript and other static assets
 */
@Controller
class StaticResourceHandler {

    /**
     * Serves the Summon hydration JavaScript bundle
     * This is the compiled Kotlin/JS that handles client-side hydration
     */
    @GetMapping("/summon-hydration.js")
    @ResponseBody
    fun summonHydrationJs(): ResponseEntity<ByteArray> {
        return try {
            val resource = ClassPathResource("static/summon-hydration.js")
            if (resource.exists()) {
                val content = resource.inputStream.readAllBytes()
                val headers = HttpHeaders()
                headers.contentType = MediaType.valueOf("application/javascript")
                headers.set("Cache-Control", "public, max-age=3600")
                ResponseEntity.ok()
                    .headers(headers)
                    .body(content)
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: IOException) {
            ResponseEntity.status(500)
                .body("Error loading Summon hydration bundle: ${e.message}".toByteArray())
        }
    }
}