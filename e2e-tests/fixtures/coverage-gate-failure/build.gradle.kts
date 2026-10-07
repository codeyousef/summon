import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    kotlin("jvm") version "2.3.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.8"
}

kotlin { jvmToolchain(17) }

dependencies {
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
}

tasks.test { useJUnitPlatform() }

kover {
    reports {
        verify {
            rule("Controlled below-threshold branch coverage") {
                minBound(80, CoverageUnit.BRANCH)
            }
        }
    }
}
