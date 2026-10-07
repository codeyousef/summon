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

    @Test
    fun `prompt aliases retry invalid input and EOF fails without generation`() {
        val root = createTempDirectory("init-command-prompts").toFile().also(tempRoots::add)
        val standalone = RecordingExecutor()
        val standaloneInputs = InputSequence(listOf("invalid", "s"))
        InitCommand(::stubTemplate, { standalone }, standaloneInputs::next).parse(
            arrayOf("site", "--dir", File(root, "site").absolutePath)
        )
        assertEquals("js", standalone.lastConfig?.templateType)

        val ktor = RecordingExecutor()
        val fullstackInputs = InputSequence(listOf("f", "invalid", "k"))
        InitCommand(::stubTemplate, { ktor }, fullstackInputs::next).parse(
            arrayOf("server", "--dir", File(root, "server").absolutePath)
        )
        assertEquals("ktor", ktor.lastConfig?.templateType)

        val eof = RecordingExecutor()
        assertFailsWith<com.github.ajalt.clikt.core.CliktError> {
            InitCommand(::stubTemplate, { eof }) { null }.parse(
                arrayOf("eof", "--dir", File(root, "eof").absolutePath)
            )
        }
        assertNull(eof.lastConfig)
    }

    @Test
    fun `target validation rejects files and nonempty directories unless forced`() {
        val root = createTempDirectory("init-command-targets").toFile().also(tempRoots::add)
        val file = File(root, "file").apply { writeText("occupied") }
        val fileExecutor = RecordingExecutor()
        InitCommand(::stubTemplate, { fileExecutor }) { "standalone" }.parse(
            arrayOf("file-project", "--mode=standalone", "--dir", file.absolutePath)
        )
        assertNull(fileExecutor.lastConfig)

        val occupied = File(root, "occupied").apply {
            mkdirs()
            resolve("existing.txt").writeText("occupied")
        }
        val rejected = RecordingExecutor()
        InitCommand(::stubTemplate, { rejected }) { "standalone" }.parse(
            arrayOf("occupied-project", "--mode=standalone", "--dir", occupied.absolutePath)
        )
        assertNull(rejected.lastConfig)

        val forced = RecordingExecutor()
        InitCommand(::stubTemplate, { forced }) { "standalone" }.parse(
            arrayOf("occupied-project", "--mode=standalone", "--dir", occupied.absolutePath, "--force")
        )
        assertEquals(true, forced.lastConfig?.overwrite)

        val here = RecordingExecutor()
        InitCommand(::stubTemplate, { here }) { "standalone" }.parse(
            arrayOf("here-project", "--mode=standalone", "--here", "--force")
        )
        assertEquals(File(".").absoluteFile, here.lastConfig?.targetDirectory)
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
