package codes.yousef.summon.ssr

import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * JVM implementation of SiteBundler using java.util.zip.
 */
actual object SiteBundler {
    /**
     * Bundles HTML and CSS into a zip archive.
     */
    actual fun bundleSite(html: String, css: String): ByteArray {
        return bundleSite(mapOf(
            "index.html" to html.encodeToByteArray(),
            "style.css" to css.encodeToByteArray()
        ))
    }
    
    /**
     * Bundles multiple files into a zip archive.
     */
    actual fun bundleSite(files: Map<String, ByteArray>): ByteArray {
        val baos = ByteArrayOutputStream()
        
        ZipOutputStream(baos).use { zos ->
            files.forEach { (path, content) ->
                // Ensure directories exist in the zip
                val dirs = path.split("/").dropLast(1)
                var dirPath = ""
                dirs.forEach { dir ->
                    dirPath = if (dirPath.isEmpty()) "$dir/" else "$dirPath$dir/"
                    try {
                        zos.putNextEntry(ZipEntry(dirPath))
                        zos.closeEntry()
                    } catch (e: Exception) {
                        // Directory entry might already exist, ignore
                    }
                }
                
                // Add the file
                val entry = ZipEntry(path)
                entry.size = content.size.toLong()
                zos.putNextEntry(entry)
                zos.write(content)
                zos.closeEntry()
            }
        }
        
        return baos.toByteArray()
    }
}
