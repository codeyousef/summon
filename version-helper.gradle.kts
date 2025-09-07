// Helper script for example projects to read version information from version.properties
// This script can be applied by example projects to get the version information

// Function to find the root project directory containing version.properties
fun findRootDir(currentDir: File): File? {
    val versionFile = File(currentDir, "version.properties")
    if (versionFile.exists()) {
        return currentDir
    }
    
    val parentDir = currentDir.parentFile ?: return null
    return findRootDir(parentDir)
}

// Find the root directory
val rootDir = findRootDir(project.projectDir)
if (rootDir != null) {
    // Read version information from version.properties
    val versionPropsFile = File(rootDir, "version.properties")
    val versionProps = java.util.Properties()
    
    versionPropsFile.inputStream().use { versionProps.load(it) }
    
    val version = versionProps.getProperty("VERSION") ?: "0.0.0"
    val group = versionProps.getProperty("GROUP") ?: "io.github.codeyousef"
    val artifactId = versionProps.getProperty("ARTIFACT_ID") ?: "summon"
    
    // Make version information available to the project
    project.extra["summonVersion"] = version
    project.extra["summonGroup"] = group
    project.extra["summonArtifactId"] = artifactId
    project.extra["summonDependency"] = "$group:$artifactId:$version"
    project.extra["summonJvmDependency"] = "$group:$artifactId-jvm:$version"
    project.extra["summonJsDependency"] = "$group:$artifactId-js:$version"
    
    logger.lifecycle("Using Summon version $version from ${versionPropsFile.absolutePath}")
} else {
    logger.warn("Could not find version.properties in any parent directory. Using default version.")
    project.extra["summonVersion"] = "0.0.0"
    project.extra["summonGroup"] = "io.github.codeyousef"
    project.extra["summonArtifactId"] = "summon"
    project.extra["summonDependency"] = "io.github.codeyousef:summon:0.0.0"
    project.extra["summonJvmDependency"] = "io.github.codeyousef:summon-jvm:0.0.0"
    project.extra["summonJsDependency"] = "io.github.codeyousef:summon-js:0.0.0"
}