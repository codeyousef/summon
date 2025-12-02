package codes.yousef.summon.ssr

/**
 * WebAssembly implementation of SiteBundler.
 *
 * Similar to the JS implementation, this uses a simple archive format
 * since true zip creation would require a library.
 */
actual object SiteBundler {
    /**
     * Bundles HTML and CSS into a simple archive format.
     */
    actual fun bundleSite(html: String, css: String): ByteArray {
        return bundleSite(mapOf(
            "index.html" to html.encodeToByteArray(),
            "style.css" to css.encodeToByteArray()
        ))
    }
    
    /**
     * Bundles multiple files.
     */
    actual fun bundleSite(files: Map<String, ByteArray>): ByteArray {
        // Create a simple archive format
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
}
