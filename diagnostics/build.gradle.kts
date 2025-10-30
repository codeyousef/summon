plugins {
    id("org.jetbrains.kotlin.jvm")
    id("me.champeau.jmh") version "0.7.2"
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":summon-core"))

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation(libs.reactor.test)
    testImplementation(libs.blockhound)

    jmhImplementation(project(":summon-core"))
    jmhImplementation(kotlin("stdlib"))
}

tasks.test {
    useJUnitPlatform()
    systemProperty("junit.jupiter.execution.parallel.enabled", "false")
}
