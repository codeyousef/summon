rootProject.name = "hydration-test"

// Include the parent project to access summon-core
includeBuild("../..") {
    dependencySubstitution {
        substitute(module("codes.yousef.summon:summon-core")).using(project(":summon-core"))
    }
}
