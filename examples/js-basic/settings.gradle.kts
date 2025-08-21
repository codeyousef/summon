rootProject.name = "js"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

// Include the summon-core project from the parent directory
includeBuild("../../") {
    dependencySubstitution {
        substitute(module("io.github.codeyousef:summon-js")).using(project(":summon-core"))
    }
}
