plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "summon"

// Core modules - always included
include(":summon-core")
include(":summon-cli")

// Examples
include(":examples:ssr-todo-app")
include(":examples:wasm-seo-todo")
include(":examples:hello-world-js")
include(":examples:hello-world-wasm")

// Diagnostics suite
include(":diagnostics")
