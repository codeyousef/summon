package codes.yousef.summon.ssr

/**
 * JavaScript implementation of SiteBundler.
 *
 * In the browser environment, zip creation requires a library like JSZip.
 * This implementation provides a basic approach that works without external dependencies.
 */
actual object SiteBundler {
    /**
     * Bundles HTML and CSS into a simple archive format.
     *
     * Note: In browser JS, true zip creation requires a library like JSZip.
     * This implementation returns a simple concatenated format that can be
     * processed by compatible tools.
     */
    actual fun bundleSite(html: String, css: String): ByteArray {
        return bundleSite(mapOf(
            "index.html" to html.encodeToByteArray(),
            "style.css" to css.encodeToByteArray()
        ))
    }
    
    /**
     * Bundles multiple files.
     *
     * Note: For true zip support in browser JS, use JSZip library directly.
     * This implementation creates a simple archive format.
     */
    actual fun bundleSite(files: Map<String, ByteArray>): ByteArray {
        // Create a simple archive format: 
        // [4 bytes: file count][for each file: 4 bytes name length, name bytes, 4 bytes content length, content bytes]
        val output = mutableListOf<Byte>()
        
        // File count (4 bytes, big-endian)
        val count = files.size
        output.add((count shr 24 and 0xFF).toByte())
        output.add((count shr 16 and 0xFF).toByte())
        output.add((count shr 8 and 0xFF).toByte())
        output.add((count and 0xFF).toByte())
        
        files.forEach { (path, content) ->
            val nameBytes = path.encodeToByteArray()
            
            // Name length (4 bytes)
            val nameLen = nameBytes.size
            output.add((nameLen shr 24 and 0xFF).toByte())
            output.add((nameLen shr 16 and 0xFF).toByte())
            output.add((nameLen shr 8 and 0xFF).toByte())
            output.add((nameLen and 0xFF).toByte())
            
            // Name bytes
            output.addAll(nameBytes.toList())
            
            // Content length (4 bytes)
            val contentLen = content.size
            output.add((contentLen shr 24 and 0xFF).toByte())
            output.add((contentLen shr 16 and 0xFF).toByte())
            output.add((contentLen shr 8 and 0xFF).toByte())
            output.add((contentLen and 0xFF).toByte())
            
            // Content bytes
            output.addAll(content.toList())
        }
        
        return output.toByteArray()
    }
    
    /**
     * Extracts files from a bundle created by bundleSite.
     *
     * @param bundle The bundled archive
     * @return Map of file paths to contents
     */
    fun extractBundle(bundle: ByteArray): Map<String, ByteArray> {
        val result = mutableMapOf<String, ByteArray>()
        var offset = 0
        
        // Read file count
        val count = (bundle[offset++].toInt() and 0xFF shl 24) or
                   (bundle[offset++].toInt() and 0xFF shl 16) or
                   (bundle[offset++].toInt() and 0xFF shl 8) or
                   (bundle[offset++].toInt() and 0xFF)
        
        repeat(count) {
            // Read name length
            val nameLen = (bundle[offset++].toInt() and 0xFF shl 24) or
                         (bundle[offset++].toInt() and 0xFF shl 16) or
                         (bundle[offset++].toInt() and 0xFF shl 8) or
                         (bundle[offset++].toInt() and 0xFF)
            
            // Read name
            val nameBytes = bundle.sliceArray(offset until offset + nameLen)
            offset += nameLen
            val name = nameBytes.decodeToString()
            
            // Read content length
            val contentLen = (bundle[offset++].toInt() and 0xFF shl 24) or
                            (bundle[offset++].toInt() and 0xFF shl 16) or
                            (bundle[offset++].toInt() and 0xFF shl 8) or
                            (bundle[offset++].toInt() and 0xFF)
            
            // Read content
            val content = bundle.sliceArray(offset until offset + contentLen)
            offset += contentLen
            
            result[name] = content
        }
        
        return result
    }
}
