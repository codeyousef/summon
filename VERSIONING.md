# Version Management in Summon

This document explains how version management works in the Summon project.

## Single Source of Truth

The version information for Summon is centralized in a single file:

- **version.properties**: Contains the version number, group ID, and artifact ID

```properties
VERSION=0.2.7.2
GROUP=io.github.codeyousef
ARTIFACT_ID=summon
```

This file is the single source of truth for version information and is used by both the main project and example projects.

## How Version Information is Used

### Main Project

The main project uses the version information from `version.properties` through the `version.gradle.kts` script:

1. `version.gradle.kts` reads the version information from `version.properties`
2. It sets the project version and group
3. It makes the version information available through `project.extra`

To apply this in the main project, we use:

```kotlin
// Apply version management
apply(from = "version.gradle.kts")
```

### Example Projects

Example projects use the version information from `version.properties` through the `version-helper.gradle.kts` script:

1. `version-helper.gradle.kts` searches for `version.properties` in the current directory and parent directories
2. It reads the version information from the file
3. It makes the version information available through `project.extra`

To apply this in example projects, we use:

```kotlin
// Apply version helper to get Summon version information
apply(from = "../../../version-helper.gradle.kts")
```

And then reference the version information in dependencies:

```kotlin
implementation(project.extra["summonDependency"] as String)
```

## Updating the Version

To update the version of Summon, you only need to change the `VERSION` property in `version.properties`. All projects that reference this file will automatically use the new version.

## Benefits

This approach has several benefits:

1. **Single Source of Truth**: The version is defined in only one place
2. **Consistency**: All projects use the same version information
3. **Maintainability**: Updating the version requires changing only one file
4. **Flexibility**: The version information can be easily accessed and used in different ways

## Implementation Details

### version.gradle.kts

This script reads the version information from `version.properties` and makes it available to the main project:

```kotlin
// Read version information from version.properties
val versionPropsFile = project.file("version.properties")
val versionProps = java.util.Properties()

if (versionPropsFile.exists()) {
    versionPropsFile.inputStream().use { versionProps.load(it) }
}

val versionName = versionProps.getProperty("VERSION") ?: "0.0.0"
val groupId = versionProps.getProperty("GROUP") ?: "io.github.codeyousef"
val artifactId = versionProps.getProperty("ARTIFACT_ID") ?: "summon"

// Set project version and group
project.version = versionName
project.group = groupId
```

### version-helper.gradle.kts

This script helps example projects find and use the version information:

```kotlin
// Function to find the root project directory containing version.properties
fun findRootDir(currentDir: File): File? {
    val versionFile = File(currentDir, "version.properties")
    if (versionFile.exists()) {
        return currentDir
    }

    val parentDir = currentDir.parentFile ?: return null
    return findRootDir(parentDir)
}

// Find the root directory and read version information
val rootDir = findRootDir(project.projectDir)
if (rootDir != null) {
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
}
```
