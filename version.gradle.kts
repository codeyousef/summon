// Version management for Summon
// This file centralizes version information to avoid duplication

// Read version information from version.properties
val versionPropsFile = project.file("version.properties")
val versionProps = java.util.Properties()

if (versionPropsFile.exists()) {
    versionPropsFile.inputStream().use { versionProps.load(it) }
}

val versionName = versionProps.getProperty("VERSION") ?: "0.0.0"
val groupId = versionProps.getProperty("GROUP") ?: "io.github.codeyousef"
val artifactId = versionProps.getProperty("ARTIFACT_ID") ?: "summon"

// Make version information available to the project
project.extra["versionName"] = versionName
project.extra["groupId"] = groupId
project.extra["artifactId"] = artifactId

// Set project version and group
project.version = versionName
project.group = groupId

// Helper function to get the full dependency notation
fun getSummonDependency(): String {
    return "$groupId:$artifactId:$versionName"
}

// Make the helper function available to the project
project.extra["summonDependency"] = getSummonDependency()
