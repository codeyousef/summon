package code.yousef.summon.routing.generator

/**
 * Implementation of a build plugin for page discovery.
 * This plugin scans the source directories for page files and generates
 * code to register these pages with the router.
 *
 * The plugin:
 * 1. Scans the source directories for page files
 * 2. Generates code to register these pages
 * 3. Includes the generated code in the build
 */
object PageDiscoveryPlugin {
    /**
     * The root directory where page files are stored
     */
    private const val PAGES_DIRECTORY = "src/commonMain/kotlin/code/yousef/summon/routing/pages"

    /**
     * Output directory for generated code
     */
    private const val OUTPUT_DIRECTORY = "build/generated/source/summon"

    /**
     * Package name for generated code
     */
    private const val PACKAGE_NAME = "code.yousef.summon.routing"

    /**
     * Apply the plugin during the build process
     */
    fun apply() {
        // 1. Scan the source directories for page files
        val pageFiles = scanForPageFiles()

        // 2. Generate the page loader code
        val code = generatePageLoaderCode(pageFiles)

        // 3. Write the generated code to a file
        writeGeneratedCode(code)
    }

    /**
     * Scan the source directories for page files
     * 
     * @return List of page file paths relative to the pages directory
     */
    private fun scanForPageFiles(): List<String> {
        // In a real implementation, this would use the build system's file APIs
        // to scan the source directories for page files.

        // For now, we'll simulate it with a predefined list of common page patterns
        val result = mutableListOf(
            "/pages/Index.kt",
            "/pages/About.kt",
            "/pages/404.kt",
            "/pages/users/[id].kt",
            "/pages/users/Profile.kt",
            "/pages/blog/[id].kt",
            "/pages/blog/List.kt",
            "/pages/contact/Index.kt",
            "/pages/products/[id].kt",
            "/pages/products/Category.kt"
        )

        println("Found ${result.size} page files:")
        result.forEach { println("  $it") }

        return result
    }

    /**
     * Generate the page loader code
     * 
     * @param pageFiles List of page file paths
     * @return Generated Kotlin code as a string
     */
    private fun generatePageLoaderCode(pageFiles: List<String>): String {
        // Build the code manually since we can't use the PageLoaderGenerator directly
        return buildString {
            appendLine("package $PACKAGE_NAME")
            appendLine()
            appendLine("// This file is generated at build time - do not edit")
            appendLine()

            // Add imports
            appendLine("import $PACKAGE_NAME.pages.*")
            appendLine("import $PACKAGE_NAME.pages.users.*")
            appendLine("import $PACKAGE_NAME.pages.blog.*")
            appendLine("import $PACKAGE_NAME.pages.contact.*")
            appendLine("import $PACKAGE_NAME.pages.products.*")
            appendLine()

            // Start the PageLoader object
            appendLine("object GeneratedPageLoader {")
            appendLine("    /**")
            appendLine("     * Register all discovered pages with the given registry.")
            appendLine("     */")
            appendLine("    fun registerPages(registry: PageRegistry) {")

            // Handle 404 page specially
            appendLine("        // Register 404 page")
            appendLine("        registry.registerNotFoundPage({ NotFoundPage() })")
            appendLine()

            // Register each page
            appendLine("        // Register regular pages")
            for (filePath in pageFiles) {
                if (filePath.endsWith("/404.kt")) continue // Already handled

                val routePath = normalizePath(filePath)
                val className = getClassName(filePath)
                val handler = getPageHandler(filePath, className)

                appendLine("        registry.registerPage(\"$routePath\", $handler)")
            }

            // Close the function and object
            appendLine("    }")
            appendLine("}")
        }
    }

    /**
     * Write the generated code to a file
     * 
     * @param code The generated code
     */
    private fun writeGeneratedCode(code: String) {
        // In a real implementation, this would write to a file in the build directory
        // and configure the build system to include it in the compilation.

        // For now, we'll just log the code
        println("Generated code would be written to $OUTPUT_DIRECTORY/GeneratedPageLoader.kt")
        println(code)
    }

    /**
     * Get the package name for a page file
     * 
     * @param filePath The path to the page file
     * @return The package name for the page
     */
    fun getPackageName(filePath: String): String {
        val path = filePath.removePrefix("/pages")
        val packagePath = path.substringBeforeLast("/").replace("/", ".")
        return if (packagePath.isEmpty()) {
            "$PACKAGE_NAME.pages"
        } else {
            "$PACKAGE_NAME.pages$packagePath"
        }
    }

    /**
     * Get the class name for a page file
     * 
     * @param filePath The path to the page file
     * @return The class name for the page
     */
    fun getClassName(filePath: String): String {
        val fileName = filePath.substringAfterLast("/").removeSuffix(".kt")

        // Handle special cases
        return when (fileName) {
            "Index" -> "HomePage"
            "404" -> "NotFoundPage"
            else -> {
                // Handle dynamic route parameters
                if (fileName.startsWith("[") && fileName.endsWith("]")) {
                    val paramName = fileName.substring(1, fileName.length - 1)
                    val baseName = when (paramName) {
                        "id" -> {
                            // Determine the entity type from the parent directory
                            val parentDir = filePath.substringBeforeLast("/").substringAfterLast("/")
                            when (parentDir) {
                                "users" -> "UserProfile"
                                "blog" -> "BlogPost"
                                "products" -> "Product"
                                else -> "Detail"
                            }
                        }
                        "slug" -> "Detail"
                        else -> "Dynamic"
                    }
                    "${baseName}Page"
                } else {
                    // Regular page
                    "${fileName}Page"
                }
            }
        }
    }

    /**
     * Get the page handler for a page file
     * 
     * @param filePath The path to the page file
     * @param className The class name for the page
     * @return The page handler code
     */
    private fun getPageHandler(filePath: String, className: String): String {
        // Check if the page has dynamic parameters
        return if (filePath.contains("[") && filePath.contains("]")) {
            // Dynamic page with parameters
            "{ params -> $className(params[\"${getDynamicParamName(filePath)}\"]) }"
        } else {
            // Static page
            "{ $className() }"
        }
    }

    /**
     * Get the dynamic parameter name from a file path
     * 
     * @param filePath The path to the page file
     * @return The dynamic parameter name
     */
    private fun getDynamicParamName(filePath: String): String {
        val fileName = filePath.substringAfterLast("/")
        if (fileName.startsWith("[") && fileName.endsWith("].kt")) {
            return fileName.substring(1, fileName.length - 4)
        }
        return "id" // Default parameter name
    }

    /**
     * Normalize a file path to a route path
     * 
     * @param filePath The path to the page file
     * @return The normalized route path
     */
    private fun normalizePath(filePath: String): String {
        // Remove the "/pages" prefix and ".kt" suffix
        var path = filePath.removePrefix("/pages").removeSuffix(".kt")

        // Handle index files
        if (path.endsWith("/Index")) {
            path = path.removeSuffix("/Index")
        }

        // Handle dynamic parameters
        path = path.replace(Regex("\\[(.*?)\\]")) { matchResult ->
            val paramName = matchResult.groupValues[1]
            ":{$paramName}"
        }

        // Ensure the path starts with a slash
        if (!path.startsWith("/")) {
            path = "/$path"
        }

        // Handle root path
        if (path == "/") {
            return path
        }

        return path
    }
}
