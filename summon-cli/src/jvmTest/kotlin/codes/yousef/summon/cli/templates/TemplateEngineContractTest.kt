package codes.yousef.summon.cli.templates

import java.nio.file.Files
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TemplateEngineContractTest {
    private val engine = TemplateEngine()

    @Test
    fun substitutionsSupportEveryDocumentedPlaceholderStyleAndCase() {
        val template = "{{name}} {{NAME}} ${'$'}{name} ${'$'}{NAME} __name__ __NAME__ {{missing}}"
        assertEquals("Summon Summon Summon Summon Summon Summon {{missing}}", engine.processTemplate(template, mapOf("name" to "Summon")))
        assertEquals("Summon.kt", engine.processFileName("{{NAME}}.kt", mapOf("name" to "Summon")))
        assertEquals("unchanged", engine.processTemplate("unchanged", emptyMap()))
    }

    @Test
    fun advancedTemplatesEvaluateFlagsVariablesLoopsAndPartials() {
        val template = """
            {{#if enabled}}enabled{{/if}}{{#if disabled}}hidden{{/if}}
            {{#unless disabled}}visible{{/unless}}{{#unless enabled}}hidden{{/unless}}
            {{#each names}}[{{this}}]{{/each}}{{#each missing}}x{{/each}}
            {{> header}} {{>missing}}
        """.trimIndent()
        val output = engine.processAdvancedTemplate(
            template,
            TemplateContext(
                variables = mapOf("enabled" to "yes", "disabled" to "0"),
                flags = mapOf("fallback" to true),
                arrays = mapOf("names" to listOf("Ada", "Lin")),
                partials = mapOf("header" to "Header")
            )
        )
        assertContains(output, "enabled")
        assertContains(output, "visible")
        assertContains(output, "[Ada][Lin]")
        assertContains(output, "Header")
        assertContains(output, "Partial 'missing' not found")
        assertTrue(!output.contains("hidden"))

        assertEquals("yes", engine.processAdvancedTemplate("{{#if flag}}yes{{/if}}", TemplateContext(flags = mapOf("flag" to true))))
        assertEquals("", engine.processAdvancedTemplate("{{#if flag}}yes{{/if}}", TemplateContext(flags = mapOf("flag" to false))))
        assertEquals("", engine.processAdvancedTemplate("{{#if value}}yes{{/if}}", TemplateContext(variables = mapOf("value" to "false"))))
    }

    @Test
    fun fileProcessingCreatesParentsAndPreservesExecutablePermission() {
        val root = Files.createTempDirectory("summon-template-")
        try {
            val source = root.resolve("source.sh")
            source.writeText("echo {{VALUE}}")
            source.toFile().setExecutable(true)
            val target = root.resolve("nested/output.sh")
            engine.processTemplateFile(source.toFile(), target.toFile(), mapOf("value" to "ready"))
            assertEquals("echo ready", target.readText())
            assertTrue(target.toFile().canExecute())
            assertFailsWith<IllegalArgumentException> {
                engine.processTemplateFile(root.resolve("missing").toFile(), root.resolve("out").toFile(), emptyMap())
            }
        } finally {
            root.toFile().deleteRecursively()
        }
    }

    @Test
    fun namingHelpersCoverAllConventionsAndAliases() {
        assertEquals("helloWorld", TemplateHelpers.transformName("HELLO-world", "camelCase"))
        assertEquals("HelloWorld", TemplateHelpers.transformName("hello_world", "pascalCase"))
        assertEquals("hello-world", TemplateHelpers.transformName("helloWorld", "kebabCase"))
        assertEquals("hello-world", TemplateHelpers.transformName("hello world", "kebab-case"))
        assertEquals("hello_world", TemplateHelpers.transformName("helloWorld", "snakeCase"))
        assertEquals("hello_world", TemplateHelpers.transformName("hello-world", "snake_case"))
        assertEquals("HELLO", TemplateHelpers.transformName("hello", "uppercase"))
        assertEquals("hello", TemplateHelpers.transformName("HELLO", "lowercase"))
        assertEquals("Same", TemplateHelpers.transformName("Same", "unknown"))
    }
}
