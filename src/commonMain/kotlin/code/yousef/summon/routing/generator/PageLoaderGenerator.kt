package code.yousef.summon.routing.generator

import code.yousef.summon.routing.PageRegistry
import code.yousef.summon.routing.DefaultPageRegistry

/**
 * Generator for page loader code.
 * This simulates a build-time processor that would scan the source directories
 * for page files and generate the appropriate registration code.
 *
 * In a real implementation, this would be a Kotlin compiler plugin, annotation processor,
 * or build plugin that runs at compile time.
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
     * This simulates what would happen at build time.
     *
     * @return Generated Kotlin code as a string
     */
    fun generatePageLoaderCode(): String {
        // In a real implementation, this would scan the file system
        // For now, we'll simulate it with a hardcoded list
        val pageFiles = listOf(
            "/pages/Index.kt",
            "/pages/About.kt",
            "/pages/404.kt",
            "/pages/users/[id].kt",
            "/pages/users/Profile.kt",
            "/pages/blog/[id].kt"
        )
        
        return buildString {
            appendLine("package code.yousef.summon.routing")
            appendLine()
            appendLine("// This file is generated at build time - do not edit")
            appendLine()
            
            // Add imports
            appendLine("import code.yousef.summon.routing.pages.*")
            appendLine("import code.yousef.summon.routing.pages.users.*")
            appendLine("import code.yousef.summon.routing.pages.blog.*")
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
                
                val registry = DefaultPageRegistry()
                val routePath = registry.normalizePath(filePath)
                
                val handler = when {
                    // Home page
                    filePath.matches(Regex(".*/pages/(Index|Home)\\.kt")) -> 
                        "{ HomePage() }"
                        
                    // About page
                    filePath.matches(Regex(".*/pages/About\\.kt")) -> 
                        "{ AboutPage() }"
                        
                    // User profile with dynamic ID
                    filePath.matches(Regex(".*/pages/users/\\[id\\]\\.kt")) -> 
                        "{ params -> UserProfilePage(params[\"id\"]) }"
                        
                    // Blog post with dynamic ID
                    filePath.matches(Regex(".*/pages/blog/\\[id\\]\\.kt")) -> 
                        "{ params -> BlogPostPage(params[\"id\"]) }"
                        
                    // User profile page
                    filePath.matches(Regex(".*/pages/users/Profile\\.kt")) -> 
                        "{ UserProfilePage(\"profile\") }"
                        
                    // Default case (shouldn't hit this in practice)
                    else -> continue
                }
                
                appendLine("        registry.registerPage(\"$routePath\", $handler)")
            }
            
            // Close the function and object
            appendLine("    }")
            appendLine("}")
        }
    }
    
    /**
     * This would write the generated code to a file during the build process
     */
    fun generateAndWriteCode(outputDirectory: String) {
        val code = generatePageLoaderCode()
        // In a real implementation, this would write to a file
        println(code)
    }
} 