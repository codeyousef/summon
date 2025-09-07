// Version management for Summon
// This file centralizes version information to avoid duplication

// Read version information from version.properties
val versionPropsFile = project.file("version.properties")
val versionProps = java.util.Properties()

if (versionPropsFile.exists()) {
    versionPropsFile.inputStream().use { versionProps.load(it) }
}

fun sanitize(input: String?, default: String = ""): String {
    val raw = input ?: default
    if (raw.isEmpty()) return raw
    // Remove BOM and zero-width characters, then trim and unquote
    val cleaned = raw
        .replace("\uFEFF", "") // BOM
        .replace("\u200B", "") // ZWSP
        .replace("\u200C", "") // ZWNJ
        .replace("\u200D", "") // ZWJ
        .trim()
    val unquoted = if (cleaned.length >= 2 && (
            (cleaned.startsWith('"') && cleaned.endsWith('"')) ||
            (cleaned.startsWith('\'') && cleaned.endsWith('\''))
        )) cleaned.substring(1, cleaned.length - 1).trim() else cleaned
    return unquoted
}

val versionNameRaw = versionProps.getProperty("VERSION") ?: "0.0.0"
val groupIdRaw = versionProps.getProperty("GROUP") ?: "io.github.codeyousef"
val artifactIdRaw = versionProps.getProperty("ARTIFACT_ID") ?: "summon"

val versionName = sanitize(versionNameRaw, "0.0.0")
val groupId = sanitize(groupIdRaw, "io.github.codeyousef")
val artifactId = sanitize(artifactIdRaw, "summon")

val isSnapshotVersion = versionName.uppercase().endsWith("-SNAPSHOT")
val isVersionBlank = versionName.isBlank()

// Optionally, a simple validity check: non-blank and no spaces
val versionValid = !isVersionBlank && !versionName.contains(' ')

// Make version information available to the project
project.extra["versionName"] = versionName
project.extra["groupId"] = groupId
project.extra["artifactId"] = artifactId
project.extra["isSnapshotVersion"] = isSnapshotVersion
project.extra["versionValid"] = versionValid

// Set project version and group
project.version = versionName
project.group = groupId

// Helper function to get the full dependency notation
fun getSummonDependency(): String {
    return "$groupId:$artifactId:$versionName"
}

// Make the helper function available to the project
project.extra["summonDependency"] = getSummonDependency()
