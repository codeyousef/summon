plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "test-project"

includeBuild("../") {
    dependencySubstitution {
        substitute(module("codes.yousef:summon")).using(project(":summon-core"))
        substitute(module("codes.yousef:summon-jvm")).using(project(":summon-core"))
        substitute(module("codes.yousef:summon-js")).using(project(":summon-core"))
        substitute(module("codes.yousef:summon-wasm-js")).using(project(":summon-core"))
    }
}
