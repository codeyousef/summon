package codes.yousef.summon.routing.generator

// Implementation of page loader code generation
/**
 * Generator for page loader code.
 * This is a build-time processor that scans the source directories
 * for page files and generates the appropriate registration code.
 *
 * In a real implementation, this would be integrated with the build system
 * as a Kotlin compiler plugin, annotation processor, or Gradle plugin.
 */
object PageLoaderGenerator {
    /**
     * The base package for page components
     */
    private const val BASE_PACKAGE = "code.yousef.summon.routing.pages"

    /**
     * The root directory where page files are stored
     */
    private const val PAGES_DIRECTORY = "src/commonMain/kotlin/code/yousef/summon/routing/pages"

    /**
     * Generate the code for page registration.
     *
     * @param pageFiles List of page file paths
     * @return Generated Kotlin code as a string
     */
    fun generatePageLoaderCode(pageFiles: List<String> = emptyList()): String {
        // If no page files are provided, use a default set
        val files = if (pageFiles.isEmpty()) {
            listOf(
                "/pages/Index.kt",
                "/pages/About.kt",
                "/pages/404.kt",
                "/pages/users/[id].kt",
                "/pages/users/Profile.kt",
                "/pages/blog/[id].kt"
            )
        } else {
            pageFiles
        }

        // Collect all unique package imports
        val packages = mutableSetOf<String>()
        files.forEach { filePath ->
            val packageName = getPackageNameFromPath(filePath)
            packages.add(packageName)
        }

        return buildString {
            appendLine("package codes.yousef.summon.routing")
            appendLine()
            appendLine("// This file is generated at build time - do not edit")
            appendLine()

            // Add imports
            packages.forEach { packageName ->
                appendLine("import $packageName.*")
            }
            appendLine()

            // Start the PageLoader object
            appendLine("object GeneratedPageLoader {")
            appendLine("    /**")
            appendLine("     * Register all discovered pages with the given registry.")
            appendLine("     */")
            appendLine("    fun registerPages(registry: PageRegistry) {")

            // Handle 404 page specially if it exists
            val notFoundPage = files.find { it.endsWith("/404.kt") }
            if (notFoundPage != null) {
                appendLine("        // Register 404 page")
                appendLine("        registry.registerNotFoundPage({ NotFoundPage() })")
                appendLine()
            }

            // Register each page
            appendLine("        // Register regular pages")
            for (filePath in files) {
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
     * Generate and write the code to a file
     *
     * @param outputDirectory The directory to write the file to
     * @param pageFiles List of page file paths
     */
    fun generateAndWriteCode(outputDirectory: String, pageFiles: List<String> = emptyList()) {
        val code = generatePageLoaderCode(pageFiles)

        try {
            // Ensure the output directory exists
            createOutputDirectory(outputDirectory)

            // Write the code to the file
            val outputFile = "$outputDirectory/GeneratedPageLoader.kt"
            writeToFile(outputFile, code)

            println("Successfully wrote generated code to $outputFile")
        } catch (e: Exception) {
            println("Error writing generated code: ${e.message}")
            // Log the code as a fallback
            println("Generated code:")
            println(code)
        }
    }

    /**
     * Create the output directory if it doesn't exist
     */
    private fun createOutputDirectory(path: String) {
        try {
            // In a real implementation, this would use platform-specific APIs
            // For Gradle plugin, this would use project.file(path).mkdirs()
            println("Creating directory: $path")
        } catch (e: Exception) {
            println("Error creating output directory: ${e.message}")
            throw e
        }
    }

    /**
     * Write content to a file
     */
    private fun writeToFile(path: String, content: String) {
        try {
            // In a real implementation, this would use platform-specific APIs
            // For Gradle plugin, this would use project.file(path).writeText(content)
            println("Writing to file: $path")
        } catch (e: Exception) {
            println("Error writing to file: ${e.message}")
            throw e
        }
    }

    /**
     * Get the package name from a file path
     *
     * @param filePath The path to the page file
     * @return The package name
     */
    private fun getPackageNameFromPath(filePath: String): String {
        val path = filePath.removePrefix("/pages")
        val packagePath = path.substringBeforeLast("/").replace("/", ".")
        return if (packagePath.isEmpty()) {
            BASE_PACKAGE
        } else {
            "$BASE_PACKAGE$packagePath"
        }
    }

    /**
     * Get the class name for a page file
     *
     * @param filePath The path to the page file
     * @return The class name
     */
    private fun getClassName(filePath: String): String {
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
     * @param className The class name
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
