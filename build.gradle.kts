import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaExtension
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory
import java.util.Properties

plugins {
    base
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kover)
    alias(libs.plugins.dokka)
}

allprojects {
    group = "io.github.codeyousef"
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

val coverageProjects = listOf(
    project(":summon-core"),
    project(":summon-cli"),
    project(":summon-aether"),
    project(":summon-devtools"),
    project(":summon-test"),
    project(":diagnostics")
)

val documentationProjects = listOf(
    project(":summon-core"),
    project(":summon-cli"),
    project(":summon-aether"),
    project(":summon-devtools"),
    project(":summon-test"),
    project(":diagnostics")
)
val centralLibraryProjects = listOf(
    project(":summon-aether"),
    project(":summon-devtools"),
    project(":summon-test")
)

centralLibraryProjects.forEach { libraryProject ->
    libraryProject.pluginManager.withPlugin("maven-publish") {
        val javadocContent = libraryProject.layout.buildDirectory.file("generated/central-javadoc/README.md")
        val javadocJar = libraryProject.tasks.register<Jar>("centralJavadocJar") {
            archiveClassifier.set("javadoc")
            from(javadocContent)
            doFirst {
                javadocContent.get().asFile.apply {
                    parentFile.mkdirs()
                    writeText(
                        "${libraryProject.name} ${libraryProject.version} API documentation\n\n" +
                            "Versioned API documentation: https://codeyousef.github.io/summon/\n"
                    )
                }
            }
        }

        libraryProject.extensions.configure<PublishingExtension> {
            publications.withType(MavenPublication::class.java).configureEach {
                artifact(javadocJar)
                pom {
                    name.set(libraryProject.name)
                    description.set("Summon Kotlin Multiplatform ${libraryProject.name.removePrefix("summon-")} library")
                    url.set("https://github.com/codeyousef/summon")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            id.set("codeyousef")
                            name.set("codeyousef")
                            url.set("https://github.com/codeyousef/")
                        }
                    }
                    scm {
                        url.set("https://github.com/codeyousef/summon/")
                        connection.set("scm:git:git://github.com/codeyousef/summon.git")
                        developerConnection.set("scm:git:ssh://git@github.com/codeyousef/summon.git")
                    }
                }
            }
        }
    }
}

val documentationVersion = Properties().run {
    rootProject.file("version.properties").inputStream().use(::load)
    requireNotNull(getProperty("VERSION")).trim()
}
val documentationSourceTag = "v$documentationVersion"
val documentationSourceRef = providers.environmentVariable("SUMMON_DOCS_SOURCE_REF")
    .orElse(documentationSourceTag)
val previousDocumentationVersions = layout.projectDirectory.dir("docs/api-versions")
val reportUndocumentedApis = providers.gradleProperty("summon.dokka.reportUndocumented")
    .map(String::toBoolean)
    .orElse(true)

documentationProjects.forEach { documentationProject ->
    documentationProject.pluginManager.apply("org.jetbrains.dokka")
    documentationProject.extensions.configure<DokkaExtension> {
        moduleName.set(documentationProject.name)
        moduleVersion.set(documentationVersion)
        dokkaPublications.html {
            failOnWarning.set(true)
            suppressInheritedMembers.set(true)
            suppressObviousFunctions.set(true)
        }
        dokkaSourceSets.configureEach {
            // Release documentation rejects undocumented public declarations by default.
            // Local exploratory builds may temporarily opt out with
            // -Psummon.dokka.reportUndocumented=false.
            reportUndocumented.set(reportUndocumentedApis)
            val matchingTestSourceSet = name
                .removeSuffix("Main")
                .let { documentationProject.file("src/${it}Test/kotlin") }
            if (matchingTestSourceSet.isDirectory) samples.from(matchingTestSourceSet)
            sourceLink {
                localDirectory.set(documentationProject.layout.projectDirectory)
                val modulePath = documentationProject.projectDir.relativeTo(rootDir).invariantSeparatorsPath
                remoteUrl("https://github.com/codeyousef/summon/tree/${documentationSourceRef.get()}/$modulePath")
                remoteLineSuffix.set("#L")
            }
        }
    }
}

subprojects {
    pluginManager.apply("org.jetbrains.kotlinx.kover")
}

dependencies {
    coverageProjects.forEach { kover(it) }
    documentationProjects.forEach { dokka(it) }
    dokkaHtmlPlugin("org.jetbrains.dokka:versioning-plugin")
}

kover {
    reports {
        verify {
            rule("Merged branch coverage must remain at least 80%") {
                minBound(80, CoverageUnit.BRANCH)
            }
        }
    }
}

dokka {
    moduleName.set("Summon")
    moduleVersion.set(documentationVersion)
    dokkaPublications.html {
        outputDirectory.set(layout.buildDirectory.dir("docs/api/$documentationVersion"))
        includes.from("README.md")
        failOnWarning.set(true)
        suppressInheritedMembers.set(true)
        suppressObviousFunctions.set(true)
    }
    pluginsConfiguration {
        versioning {
            version.set(documentationVersion)
            olderVersionsDir.set(previousDocumentationVersions)
        }
    }
}

val coverageModuleManifest = layout.buildDirectory.file("reports/kover/modules.txt")
val writeCoverageModuleManifest by tasks.registering {
    description = "Records the JVM modules merged into the Kover report"
    outputs.file(coverageModuleManifest)
    doLast {
        val unsupportedProjects = coverageProjects.filterNot {
            it.pluginManager.hasPlugin("org.jetbrains.kotlinx.kover")
        }
        check(unsupportedProjects.isEmpty()) {
            "Kover is not applied to: ${unsupportedProjects.joinToString { it.path }}"
        }

        val missingJvmTests = coverageProjects.filter { coveredProject ->
            val expectedTask = if (coveredProject.path == ":diagnostics") "test" else "jvmTest"
            coveredProject.tasks.findByName(expectedTask) == null
        }
        check(missingJvmTests.isEmpty()) {
            "JVM test task is missing from: ${missingJvmTests.joinToString { it.path }}"
        }

        val manifest = buildString {
            appendLine("format=summon-kover-modules-v1")
            appendLine("engine=kover-0.9.8")
            appendLine("coverage=JVM-only")
            appendLine("exclusions=none")
            coverageProjects.forEach { appendLine("module=${it.path}") }
            appendLine("separate-browser-evidence=JS,WASM")
        }
        coverageModuleManifest.get().asFile.apply {
            parentFile.mkdirs()
            writeText(manifest)
        }
    }
}

val verifyMergedCoverageReport by tasks.registering {
    description = "Rejects missing, empty, malformed, or sub-threshold merged Kover XML"
    dependsOn("koverXmlReport", writeCoverageModuleManifest)
    val reportFile = layout.buildDirectory.file("reports/kover/report.xml")
    inputs.file(reportFile)
    inputs.file(coverageModuleManifest)
    doLast {
        val xml = reportFile.get().asFile
        check(xml.isFile && xml.length() > 0) { "Merged Kover XML is missing or empty: $xml" }

        val factory = DocumentBuilderFactory.newInstance().apply {
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
            setFeature("http://xml.org/sax/features/external-general-entities", false)
            setFeature("http://xml.org/sax/features/external-parameter-entities", false)
            isXIncludeAware = false
            isExpandEntityReferences = false
        }
        val report = factory.newDocumentBuilder().parse(xml).documentElement
        check(report.tagName == "report") { "Unexpected Kover XML root: ${report.tagName}" }
        check(report.getElementsByTagName("class").length > 0) {
            "Merged Kover XML contains no covered classes"
        }

        val branchCounter = (0 until report.childNodes.length)
            .mapNotNull { report.childNodes.item(it) as? Element }
            .singleOrNull {
                it.tagName == "counter" && it.getAttribute("type") == "BRANCH"
            } ?: error("Merged Kover XML has no aggregate BRANCH counter")
        val covered = branchCounter.getAttribute("covered").toLongOrNull()
            ?: error("Merged BRANCH covered count is invalid")
        val missed = branchCounter.getAttribute("missed").toLongOrNull()
            ?: error("Merged BRANCH missed count is invalid")
        val total = covered + missed
        check(total > 0) { "Merged Kover XML contains no branch units" }
        val percentage = covered * 100.0 / total
        check(percentage >= 80.0) {
            "Merged branch coverage is %.2f%% (%d/%d); required minimum is 80%%"
                .format(percentage, covered, total)
        }
        logger.lifecycle(
            "Merged JVM branch coverage: %.2f%% (%d/%d); modules: %s"
                .format(percentage, covered, total, coverageModuleManifest.get().asFile)
        )
    }
}

val coverageCheck by tasks.registering {
    group = "verification"
    description = "Runs merged JVM coverage, emits HTML/XML, and enforces the 80% branch gate"
    dependsOn("koverHtmlReport", "koverXmlReport", "koverVerify", verifyMergedCoverageReport)
}

tasks.named("check") {
    dependsOn(coverageCheck)
}

tasks.register("buildCore") {
    dependsOn(":summon-core:build")
    description = "Build core library only"
}

tasks.register("buildCli") {
    dependsOn(":summon-cli:build")
    description = "Build CLI tool only"
}

tasks.register("buildAll") {
    dependsOn(":summon-core:build", ":summon-cli:build", ":summon-devtools:build", ":summon-test:build")
    description = "Build all framework modules"
}

tasks.register("publishLocal") {
    dependsOn(
        ":summon-core:publishToMavenLocal",
        ":summon-cli:publishToMavenLocal",
        ":summon-devtools:publishToMavenLocal",
        ":summon-test:publishToMavenLocal"
    )
    description = "Publish all framework modules to the local Maven repository"
}

tasks.register("buildCliExecutables") {
    group = "build"
    description = "Build CLI tool native executables and Shadow JAR"
    dependsOn(":summon-cli:buildNativeExecutable", ":summon-cli:shadowJar")

    doLast {
        println("CLI tool executables built successfully!")
        println("Shadow JAR: summon-cli/build/libs/")
        println("Native executable: summon-cli/build/native/nativeCompile/")
        println("Use these artifacts for GitHub Releases")
    }
}

// Performance benchmarking tasks
tasks.register("benchmark") {
    group = "benchmark"
    description = "Run all JMH benchmarks (use ./gradlew :diagnostics:jmh)"
    dependsOn(":diagnostics:jmh")
}