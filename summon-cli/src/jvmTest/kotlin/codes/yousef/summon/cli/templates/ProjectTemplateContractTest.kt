package codes.yousef.summon.cli.templates

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ProjectTemplateContractTest {
    @Test
    fun everySupportedTemplateHasDependenciesVariablesAndRoundTrips() {
        val expectedTypes = listOf("js", "quarkus", "spring-boot", "ktor", "library", "example", "basic")
        val templates = expectedTypes.dropLast(1).map(ProjectTemplate::fromType) + ProjectTemplate.fromType("unknown")

        assertEquals(expectedTypes, templates.map { it.type })
        templates.forEach { template ->
            assertTrue(template.name.isNotBlank())
            assertTrue(template.description.isNotBlank())
            assertTrue(template.tags.isNotEmpty())
            assertTrue(template.variables.isNotEmpty())
            assertTrue(template.dependencies.summon.single().startsWith("codes.yousef:summon:"))
            assertTrue(template.postSetupInstructions.isNotEmpty())
            template.variables.values.forEach {
                assertEquals("string", it.type)
                assertTrue(it.required)
                assertTrue(it.defaultValue.isNotBlank())
            }
            val encoded = Json.encodeToString(template)
            assertEquals(template, Json.decodeFromString<ProjectTemplate>(encoded))
        }

        assertTrue(templates.single { it.type == "js" }.dependencies.npm.isNotEmpty())
        assertTrue(templates.single { it.type == "quarkus" }.dependencies.quarkus.isNotEmpty())
        assertTrue(templates.single { it.type == "spring-boot" }.dependencies.spring.isNotEmpty())
        assertTrue(templates.single { it.type == "ktor" }.dependencies.ktor.isNotEmpty())
    }

    @Test
    fun optionalTemplateModelsRetainExplicitFlags() {
        val file = TemplateFile("source", "target", processAsTemplate = false, executable = true)
        assertFalse(file.processAsTemplate)
        assertTrue(file.executable)
        val dependencies = TemplateDependencies(gradle = listOf("plugin"))
        assertEquals(listOf("plugin"), dependencies.gradle)
        val optional = TemplateVariable("optional", "string", "", required = false)
        assertFalse(optional.required)
    }

    @Test
    fun templateModelEqualityIncludesEveryContractField() {
        val template = ProjectTemplate(
            name = "name",
            description = "description",
            type = "type",
            tags = listOf("tag"),
            variables = mapOf("name" to TemplateVariable("description", "string", "default")),
            files = listOf(TemplateFile("source", "target")),
            dependencies = TemplateDependencies(kotlin = listOf("kotlin")),
            postSetupInstructions = listOf("instruction")
        )
        listOf(
            template.copy(name = "other"),
            template.copy(description = "other"),
            template.copy(type = "other"),
            template.copy(tags = emptyList()),
            template.copy(variables = emptyMap()),
            template.copy(files = emptyList()),
            template.copy(dependencies = TemplateDependencies()),
            template.copy(postSetupInstructions = emptyList()),
        ).forEach { assertNotEquals(template, it) }
        assertEquals(template, template.copy())
        assertEquals(template, template)

        val variable = TemplateVariable("description", "string", "default", required = true)
        listOf(
            variable.copy(description = "other"),
            variable.copy(type = "number"),
            variable.copy(defaultValue = "other"),
            variable.copy(required = false),
        ).forEach { assertNotEquals(variable, it) }
        assertEquals(variable, variable.copy())
        assertEquals(variable, variable)

        val file = TemplateFile("source", "target", processAsTemplate = true, executable = false)
        listOf(
            file.copy(sourcePath = "other"),
            file.copy(targetPath = "other"),
            file.copy(processAsTemplate = false),
            file.copy(executable = true),
        ).forEach { assertNotEquals(file, it) }
        assertEquals(file, file.copy())
        assertEquals(file, file)

        val dependencies = TemplateDependencies(
            kotlin = listOf("kotlin"),
            summon = listOf("summon"),
            quarkus = listOf("quarkus"),
            spring = listOf("spring"),
            ktor = listOf("ktor"),
            npm = listOf("npm"),
            gradle = listOf("gradle")
        )
        listOf(
            dependencies.copy(kotlin = emptyList()),
            dependencies.copy(summon = emptyList()),
            dependencies.copy(quarkus = emptyList()),
            dependencies.copy(spring = emptyList()),
            dependencies.copy(ktor = emptyList()),
            dependencies.copy(npm = emptyList()),
            dependencies.copy(gradle = emptyList()),
        ).forEach { assertNotEquals(dependencies, it) }
        assertEquals(dependencies, dependencies.copy())
        assertEquals(dependencies, dependencies)
    }
    @Test
    fun serializersApplyEveryOptionalModelDefault() {
        val template = Json.decodeFromString<ProjectTemplate>(
            """{"name":"n","description":"d","type":"t"}"""
        )
        assertTrue(template.tags.isEmpty())
        assertTrue(template.variables.isEmpty())
        assertTrue(template.files.isEmpty())
        assertEquals(TemplateDependencies(), template.dependencies)
        assertTrue(template.postSetupInstructions.isEmpty())

        val variable = Json.decodeFromString<TemplateVariable>(
            """{"description":"d","type":"string","defaultValue":""}"""
        )
        assertEquals("", variable.defaultValue)
        assertTrue(variable.required)

        val file = Json.decodeFromString<TemplateFile>(
            """{"sourcePath":"source","targetPath":"target"}"""
        )
        assertTrue(file.processAsTemplate)
        assertFalse(file.executable)

        assertEquals(
            TemplateFile("source", "target", processAsTemplate = false, executable = true),
            Json.decodeFromString<TemplateFile>(
                """{"sourcePath":"source","targetPath":"target","processAsTemplate":false,"executable":true}"""
            )
        )

        val fullDependencies = TemplateDependencies(
            kotlin = listOf("k"),
            summon = listOf("s"),
            quarkus = listOf("q"),
            spring = listOf("sp"),
            ktor = listOf("kt"),
            npm = listOf("n"),
            gradle = listOf("g"),
        )
        assertEquals(fullDependencies, Json.decodeFromString<TemplateDependencies>(Json.encodeToString(fullDependencies)))
        val dependencies = Json.decodeFromString<TemplateDependencies>("{}")
        assertEquals(TemplateDependencies(), dependencies)
    }

}
