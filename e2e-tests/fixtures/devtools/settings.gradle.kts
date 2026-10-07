pluginManagement { repositories { gradlePluginPortal(); mavenCentral() } }
rootProject.name = "summon-devtools-fixture"
includeBuild("../../..") {
    dependencySubstitution {
        substitute(module("codes.yousef:summon")).using(project(":summon-core"))
        substitute(module("codes.yousef:summon-devtools")).using(project(":summon-devtools"))
    }
}
