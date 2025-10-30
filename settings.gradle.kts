rootProject.name = "summon"

// Core modules - always included
include(":summon-core")
include(":summon-cli")

// Examples
include(":examples:ssr-todo-app")
include(":examples:wasm-seo-todo")

// Diagnostics suite
include(":diagnostics")
