/*
 * This is a sample build.gradle.kts file demonstrating how the page discovery plugin
 * would be configured in a real project. This is not meant to be actually used -
 * it's just to show the integration pattern.
 */

plugins {
    kotlin("multiplatform")
    // Include our custom page discovery plugin
    id("code.yousef.summon.page-discovery")
}

// Configure the page discovery plugin
summonPages {
    // Source directory for page files
    pagesDirectory = "src/commonMain/kotlin/code/yousef/summon/routing/pages"
    
    // Output directory for generated code
    outputDirectory = "build/generated/source/summon"
    
    // Options for page file pattern matching
    options {
        // File extensions to consider as page files
        pageExtensions = listOf(".kt", ".page.kt")
        
        // Directories to exclude
        excludeDirectories = listOf("components", "layouts", "lib")
    }
}

kotlin {
    sourceSets {
        val commonMain by getting {
            // Include the generated source directory
            kotlin.srcDir("build/generated/source/summon")
        }
    }
} 