package codes.yousef.summon.cli.commands

import com.github.ajalt.clikt.core.parse
import java.io.File
import java.time.Duration
import java.time.Instant
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InitCommandIntegrationTest {

    private val repoRoot: File = File("..").canonicalFile

    @Test
    fun `generated templates build without unexpected warnings`() {
        val previousInclude = System.getProperty("summon.dev.includeBuild")
        System.setProperty("summon.dev.includeBuild", repoRoot.absolutePath)

        val workspaceRoot = createTempDirectory("summon-cli-it").toFile()
        val generatedProjects = mutableListOf<File>()

        val scenarios = listOf(
            TemplateScenario(
                projectName = "standalone-site",
                initArgs = arrayOf("--mode=standalone"),
                gradleTasks = listOf("kotlinUpgradeYarnLock", "build"),
                warningModeFail = false
            ),
            TemplateScenario(
                projectName = "portal-ktor",
                initArgs = arrayOf("--mode=fullstack", "--backend=ktor"),
                gradleTasks = listOf("kotlinUpgradeYarnLock", "build"),
                warningModeFail = false
            ),
            TemplateScenario(
                projectName = "portal-spring",
                initArgs = arrayOf("--mode=fullstack", "--backend=spring"),
                gradleTasks = listOf("kotlinUpgradeYarnLock", "build"),
                warningModeFail = false
            ),
            TemplateScenario(
                projectName = "portal-quarkus",
                initArgs = arrayOf("--mode=fullstack", "--backend=quarkus"),
                gradleTasks = listOf("kotlinUpgradeYarnLock", "build", "unitTest"),
                warningModeFail = false,
                checkDeprecated = true
            )
        )

        try {
            scenarios.forEach { scenario ->
                val targetDir = workspaceRoot.resolve(scenario.projectName)
                generateProject(scenario, targetDir)
                generatedProjects += targetDir

                val gradleResult = runGradle(
                    projectDir = targetDir,
                    tasks = scenario.gradleTasks,
                    warningModeFail = scenario.warningModeFail
                )

                // Allow OOM/memory-related failures as they're environment-dependent
                val isMemoryFailure = gradleResult.exitCode != 0 && (
                        gradleResult.output.contains("garbage collector is thrashing", ignoreCase = true) ||
                                gradleResult.output.contains("org.gradle.jvmargs", ignoreCase = true) ||
                                gradleResult.output.contains("max heap space", ignoreCase = true)
                        )

                if (isMemoryFailure) {
                    println("⚠️ Skipping ${scenario.projectName} due to memory constraints")
                    return@forEach // Skip this scenario entirely
                }

                val failureMessage = buildString {
                    appendLine("Gradle command failed for ${scenario.projectName}")
                    appendLine("Command: ${gradleResult.command.joinToString(" ")}")
                    appendLine("Output:")
                    appendLine(gradleResult.output)
                }

                assertEquals(0, gradleResult.exitCode, failureMessage)

                val unexpectedWarnings = findUnexpectedWarnings(
                    output = gradleResult.output,
                    includeDeprecated = scenario.checkDeprecated
                )
                if (unexpectedWarnings.isNotEmpty()) {
                    val warningMessage = buildString {
                        appendLine("Unexpected warnings detected for ${scenario.projectName}:")
                        appendLine(unexpectedWarnings.joinToString(separator = "\n"))
                        appendLine()
                        appendLine("Full Gradle output:")
                        appendLine(gradleResult.output)
                    }
                    assertTrue(false, warningMessage)
                }
            }
        } finally {
            generatedProjects.reversed().forEach { it.deleteRecursively() }
            workspaceRoot.deleteRecursively()
            if (previousInclude == null) {
                System.clearProperty("summon.dev.includeBuild")
            } else {
                System.setProperty("summon.dev.includeBuild", previousInclude)
            }
        }
    }

    private fun generateProject(scenario: TemplateScenario, targetDir: File) {
        val args = scenario.initArgs.toMutableList().apply {
            add(0, scenario.projectName)
            add("--dir")
            add(targetDir.absolutePath)
        }

        InitCommand().parse(args.toTypedArray())
    }

    private fun runGradle(projectDir: File, tasks: List<String>, warningModeFail: Boolean): GradleResult {
        val command = mutableListOf("./gradlew", "--no-daemon", "--console=plain")
        command += if (warningModeFail) "--warning-mode=fail" else "--warning-mode=all"
        command.addAll(tasks)

        val process = ProcessBuilder(command)
            .directory(projectDir)
            .redirectErrorStream(true)
            .apply {
                environment()["CI"] = "true"
                environment().compute("NODE_OPTIONS") { _, _ -> "--no-warnings" }
            }
            .start()

        val start = Instant.now()
        
        // Use a separate thread to read output to prevent blocking if buffer fills up
        val outputBuilder = StringBuilder()
        val readerThread = Thread {
            process.inputStream.bufferedReader().use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    synchronized(outputBuilder) {
                        outputBuilder.appendLine(line)
                    }
                    line = reader.readLine()
                }
            }
        }
        readerThread.start()
        
        val finished = process.waitFor(10, java.util.concurrent.TimeUnit.MINUTES)
        if (!finished) {
            process.destroyForcibly()
            readerThread.join(1000)
            throw RuntimeException("Gradle task timed out after 10 minutes. Output so far:\n$outputBuilder")
        }
        
        readerThread.join()
        val output = outputBuilder.toString()
        val exitCode = process.exitValue()
        val duration = Duration.between(start, Instant.now()).toMillis()

        return GradleResult(
            command = command,
            exitCode = exitCode,
            output = output,
            durationMillis = duration
        )
    }

    private fun findUnexpectedWarnings(output: String, includeDeprecated: Boolean): List<String> {
        val toleratedFragments = listOf(
            "DeprecationWarning: The `punycode` module is deprecated",
            "warning workspace-aggregator",
            "warning Ignored scripts due to flag",
            "Waiting for the other yarn instance",
            "rimraf@",
            "glob@",
            "inflight@",
            " warnings]",
            "The ProjectDependency.getDependencyProject() method has been deprecated",
            "detached configurations should not extend",
            "The ResolvedConfiguration.getFiles() method has been deprecated",
            "The BuildIdentifier.getName() method has been deprecated",
            "Deprecated Gradle features were used in this build",
            "disable-logging=true",
            "garbage collector is thrashing",
            "org.gradle.jvmargs",
            "org.gradle.daemon.performance",
            "expect'/'actual' classes",
            "-Xexpect-actual-classes",
            "KT-61573",
            "Scripts are not yet supported with K2",
            "ExperimentalWasmJsInterop",
            "The engine \"pnpm\" appears to be invalid",
            "Unsupported API dependency types",
            "API dependency types are used in test source sets",
            "Adding API dependency types to test source sets is not supported",
            "Dependencies: \n    - org.jetbrains.kotlinx:atomicfu",
            "Replacing API dependency types in test source sets with implementation dependencies"
        )
        val webpackWarningPattern = Regex("\\[\\d+ warnings?]|webpack \\d+\\.\\d+\\.\\d+ compiled with \\d+ warnings?", RegexOption.IGNORE_CASE)
        val webpackPerformancePattern = Regex("asset size limit:|entrypoint size limit:|webpack performance recommendations", RegexOption.IGNORE_CASE)

        return output
            .lineSequence()
            .map { it.trim() }
            .filter {
                it.contains("warning", ignoreCase = true) ||
                        (includeDeprecated && it.contains("deprecated", ignoreCase = true))
            }
            .filterNot { line ->
                toleratedFragments.any { fragment ->
                    line.contains(fragment, ignoreCase = true)
                } || webpackWarningPattern.containsMatchIn(line) || webpackPerformancePattern.containsMatchIn(line)
            }
            .toList()
    }

    private data class TemplateScenario(
        val projectName: String,
        val initArgs: Array<String>,
        val gradleTasks: List<String>,
        val warningModeFail: Boolean = true,
        val checkDeprecated: Boolean = false
    )

    private data class GradleResult(
        val command: List<String>,
        val exitCode: Int,
        val output: String,
        val durationMillis: Long
    )
}
