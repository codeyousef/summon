package codes.yousef.summon.cli.commands

import codes.yousef.summon.cli.generators.ProjectGenerator
import codes.yousef.summon.cli.templates.ProjectTemplate
import com.github.ajalt.clikt.core.BadParameterValue
import com.github.ajalt.clikt.core.parse
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.*

class InitCommandTest {

    private val tempRoots = mutableListOf<File>()

    @AfterTest
    fun cleanup() {
        tempRoots.forEach { it.deleteRecursively() }
        tempRoots.clear()
    }

    @Test
    fun `standalone mode uses js template without prompting`() {
        val root = createTempDirectory("init-command-standalone").toFile().also(tempRoots::add)
        val targetDir = File(root, "standalone")
        val executor = RecordingExecutor()

        val command = InitCommand(
            templateResolver = ::stubTemplate,
            generatorFactory = { executor },
            inputProvider = { error("No prompt expected for standalone mode") }
        )

        command.parse(
            arrayOf(
                "sample-standalone",
                "--mode=standalone",
                "--dir",
                targetDir.absolutePath
            )
        )

        val config = executor.lastConfig ?: fail("Generator was not invoked")
        assertEquals("js", config.templateType)
        assertEquals(targetDir.absoluteFile, config.targetDirectory)
    }

    @Test
    fun `fullstack mode with backend flag selects backend template`() {
        val root = createTempDirectory("init-command-fullstack-flag").toFile().also(tempRoots::add)
        val targetDir = File(root, "fullstack")
        val executor = RecordingExecutor()

        val command = InitCommand(
            templateResolver = ::stubTemplate,
            generatorFactory = { executor },
            inputProvider = { error("Backend flag should skip prompts") }
        )

        command.parse(
            arrayOf(
                "portal",
                "--mode=fullstack",
                "--backend=spring",
                "--dir",
                targetDir.absolutePath
            )
        )

        val config = executor.lastConfig ?: fail("Generator was not invoked")
        assertEquals("spring-boot", config.templateType)
        assertEquals(targetDir.absoluteFile, config.targetDirectory)
    }

    @Test
    fun `interactive prompts collect mode then backend`() {
        val root = createTempDirectory("init-command-interactive").toFile().also(tempRoots::add)
        val targetDir = File(root, "interactive")
        val executor = RecordingExecutor()
        val inputs = InputSequence(listOf("2", "3"))

        val command = InitCommand(
            templateResolver = ::stubTemplate,
            generatorFactory = { executor },
            inputProvider = { inputs.next() }
        )

        command.parse(
            arrayOf(
                "portal",
                "--dir",
                targetDir.absolutePath
            )
        )

        val config = executor.lastConfig ?: fail("Generator was not invoked")
        assertEquals("quarkus", config.templateType)
        assertTrue(inputs.exhausted, "Expected all prompt responses to be consumed")
    }

    @Test
    fun `invalid backend value throws immediately`() {
        val root = createTempDirectory("init-command-invalid").toFile().also(tempRoots::add)
        val targetDir = File(root, "invalid")
        val executor = RecordingExecutor()

        val command = InitCommand(
            templateResolver = ::stubTemplate,
            generatorFactory = { executor },
            inputProvider = { null }
        )

        assertFailsWith<BadParameterValue> {
            command.parse(
                arrayOf(
                    "broken",
                    "--mode=fullstack",
                    "--backend=unknown",
                    "--dir",
                    targetDir.absolutePath
                )
            )
        }

        assertNull(executor.lastConfig, "Generator should not run on invalid backend input")
    }

    private fun stubTemplate(type: String): ProjectTemplate = ProjectTemplate(
        name = type,
        description = "$type template",
        type = type
    )

    private class RecordingExecutor : InitCommand.ProjectExecutor {
        var lastConfig: ProjectGenerator.Config? = null

        override fun generate(config: ProjectGenerator.Config) {
            lastConfig = config
        }
    }

    private class InputSequence(values: List<String>) {
        private val iterator = values.iterator()

        fun next(): String? = if (iterator.hasNext()) iterator.next() else null

        val exhausted: Boolean
            get() = !iterator.hasNext()
    }
}
