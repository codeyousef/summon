plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "summon"

// Core modules - always included
include(":summon-core")
include(":summon-cli")
include(":summon-aether")

// Diagnostics suite
include(":diagnostics")
