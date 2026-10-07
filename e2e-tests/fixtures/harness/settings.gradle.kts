pluginManagement { repositories { gradlePluginPortal(); mavenCentral() } }
rootProject.name = "summon-harness-fixture"
includeBuild("../../..") {
    dependencySubstitution {
        substitute(module("codes.yousef:summon")).using(project(":summon-core"))
        substitute(module("codes.yousef:summon-test")).using(project(":summon-test"))
    }
}
