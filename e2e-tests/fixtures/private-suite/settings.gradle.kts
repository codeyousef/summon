pluginManagement { repositories { gradlePluginPortal(); mavenCentral() } }
rootProject.name = "summon-private-suite-fixture"
includeBuild("../../..") {
    dependencySubstitution {
        substitute(module("codes.yousef:summon")).using(project(":summon-core"))
    }
}
