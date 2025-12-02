package codes.yousef.summon.ssr

/**
 * Site bundling utilities for creating portable static site packages.
 *
 * Provides functionality to bundle rendered HTML and CSS into a compressed
 * archive that can be easily deployed or shared.
 *
 * ## Features
 *
 * - **Zip Creation**: Bundle site files into a single archive
 * - **File Structure**: Maintains proper directory structure
 * - **Asset Inclusion**: Include CSS, JS, and other assets
 *
 * ## Usage
 *
 * ```kotlin
 * // Bundle a simple site
 * val bundle = SiteBundler.bundleSite(
 *     html = renderedHtml,
 *     css = compiledCss
 * )
 *
 * // Write to file
 * File("site.zip").writeBytes(bundle)
 *
 * // Bundle with additional assets
 * val bundle = SiteBundler.bundleSite(
 *     files = mapOf(
 *         "index.html" to htmlContent.encodeToByteArray(),
 *         "styles/main.css" to cssContent.encodeToByteArray(),
 *         "scripts/app.js" to jsContent.encodeToByteArray()
 *     )
 * )
 * ```
 *
 * @since 1.0.0
 */
expect object SiteBundler {
    /**
     * Bundles HTML and CSS into a zip archive.
     *
     * Creates a zip containing:
     * - `index.html` with the provided HTML content
     * - `style.css` with the provided CSS content
     *
     * @param html The HTML content for index.html
     * @param css The CSS content for style.css
     * @return ByteArray containing the zip archive
     */
    fun bundleSite(html: String, css: String): ByteArray
    
    /**
     * Bundles multiple files into a zip archive.
     *
     * @param files Map of file paths to file contents
     * @return ByteArray containing the zip archive
     */
    fun bundleSite(files: Map<String, ByteArray>): ByteArray
}
