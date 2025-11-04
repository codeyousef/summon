package code.yousef.summon.cli.commands

import com.github.ajalt.clikt.core.parse
import java.io.File
import java.nio.file.Files
import java.time.Duration
import java.time.Instant
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InitCommandIntegrationTest {

    private val repoRoot: File = File("..").canonicalFile
    private val localMavenRepo: File by lazy { publishSummonToLocalRepository() }
    private val repositoryInitScript: File by lazy { createRepositoryInitScript() }

    @Test
    fun `generated templates build without unexpected warnings`() {
        val workspaceRoot = createTempDirectory("summon-cli-it").toFile()
        val generatedProjects = mutableListOf<File>()

        val scenarios = listOf(
            TemplateScenario(
                projectName = "standalone-site",
                initArgs = arrayOf("--mode=standalone"),
                gradleTasks = listOf("build")
            ),
            TemplateScenario(
                projectName = "portal-ktor",
                initArgs = arrayOf("--mode=fullstack", "--backend=ktor"),
                gradleTasks = listOf("build")
            ),
            TemplateScenario(
                projectName = "portal-spring",
                initArgs = arrayOf("--mode=fullstack", "--backend=spring"),
                gradleTasks = listOf("build")
            ),
            TemplateScenario(
                projectName = "portal-quarkus",
                initArgs = arrayOf("--mode=fullstack", "--backend=quarkus"),
                gradleTasks = listOf("build", "unitTest"),
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

                assertEquals(
                    0,
                    gradleResult.exitCode,
                    buildString {
                        appendLine("Gradle command failed for ${scenario.projectName}")
                        appendLine("Command: ${gradleResult.command.joinToString(" ")}")
                        appendLine("Output:\n${gradleResult.output}")
                    }
                )

                val unexpectedWarnings = findUnexpectedWarnings(
                    output = gradleResult.output,
                    includeDeprecated = scenario.checkDeprecated
                )
                assertTrue(
                    unexpectedWarnings.isEmpty(),
                    buildString {
                        appendLine("Unexpected warnings detected for ${scenario.projectName}:")
                        appendLine(unexpectedWarnings.joinToString(separator = "\n"))
                        appendLine()
                        appendLine("Full Gradle output:")
                        appendLine(gradleResult.output)
                    }
                )
            }
        } finally {
            generatedProjects.reversed().forEach { it.deleteRecursively() }
            workspaceRoot.deleteRecursively()
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
        command += listOf("--init-script", repositoryInitScript.absolutePath)
        command += listOf("-Dmaven.repo.local=${localMavenRepo.absolutePath}")
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
        val output = process.inputStream.bufferedReader().use { it.readText() }
        val exitCode = process.waitFor()
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
            "rimraf@",
            "glob@",
            "inflight@",
            " warnings]",
            "The ProjectDependency.getDependencyProject() method has been deprecated",
            "detached configurations should not extend",
            "The ResolvedConfiguration.getFiles() method has been deprecated",
            "The BuildIdentifier.getName() method has been deprecated",
            "Deprecated Gradle features were used in this build"
        )
        val webpackWarningPattern = Regex("\\[\\d+ warnings?]")

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
                } || webpackWarningPattern.containsMatchIn(line)
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

    private fun publishSummonToLocalRepository(): File {
        val repoDir = Files.createTempDirectory("summon-m2").toFile().apply { deleteOnExit() }
        val command = listOf(
            "./gradlew",
            "--no-daemon",
            "--console=plain",
            "-x",
            "test",
            "publishToMavenLocal",
            "-Dmaven.repo.local=${repoDir.absolutePath}"
        )

        val process = ProcessBuilder(command)
            .directory(repoRoot)
            .redirectErrorStream(true)
            .apply { environment()["CI"] = "true" }
            .start()

        val output = process.inputStream.bufferedReader().use { it.readText() }
        val exitCode = process.waitFor()

        check(exitCode == 0) {
            buildString {
                appendLine("Failed to publish Summon artifacts to local repository (exit $exitCode)")
                appendLine("Command: ${command.joinToString(" ")}")
                appendLine("Output:\n$output")
            }
        }

        return repoDir
    }

    private fun createRepositoryInitScript(): File {
        val script = Files.createTempFile("summon-repo", ".gradle.kts").toFile().apply { deleteOnExit() }
        script.writeText(
            """
                allprojects {
                    repositories {
                        mavenLocal()
                    }
                }
            """.trimIndent()
        )
        return script
    }
}
