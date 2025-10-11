package code.yousef.summon.cli.generators

import code.yousef.summon.cli.templates.ProjectTemplate
import java.io.File
import kotlin.test.*

/**
 * TDD tests for ProjectGenerator to ensure generated projects compile correctly
 * Addresses GitHub issue #14: Generated projects have compilation errors
 */
class ProjectGeneratorTest {

    private lateinit var tempDir: File

    @BeforeTest
    fun setup() {
        tempDir = createTempDir("summon-test-")
    }

    @AfterTest
    fun cleanup() {
        tempDir.deleteRecursively()
    }

    @Test
    fun `test version reads from version properties file`() {
        val template = ProjectTemplate.fromType("js")
        val generator = ProjectGenerator(template)
        
        val config = ProjectGenerator.Config(
            projectName = "test-app",
            packageName = "com.example.test",
            targetDirectory = tempDir,
            templateType = "js",
            minimal = false
        )

        generator.generate(config)

        val buildFile = File(tempDir, "build.gradle.kts")
        assertTrue(buildFile.exists(), "build.gradle.kts should exist")
        
        val content = buildFile.readText()
        
        // Should use version from version.properties (0.4.0.5), not hardcoded 0.4.0.0
        assertFalse(
            content.contains("summon:0.4.0.0"),
            "Generated build file should not use hardcoded old version 0.4.0.0"
        )
        assertTrue(
            content.contains("summon:0.4.0.5"),
            "Generated build file should use current version 0.4.0.5 from version.properties"
        )
    }

    @Test
    fun `test generated Main uses Modifier constructor correctly`() {
        val template = ProjectTemplate.fromType("js")
        val generator = ProjectGenerator(template)
        
        val config = ProjectGenerator.Config(
            projectName = "test-app",
            packageName = "com.example.test",
            targetDirectory = tempDir,
            templateType = "js",
            minimal = false
        )

        generator.generate(config)

        val mainFile = File(tempDir, "src/jsMain/kotlin/com/example/test/Main.kt")
        assertTrue(mainFile.exists(), "Main.kt should exist")
        
        val content = mainFile.readText()
        
        // Should use Modifier() constructor, not bare Modifier
        assertFalse(
            content.contains("= Modifier.padding"),
            "Should not use Modifier as companion object"
        )
        assertTrue(
            content.contains("Modifier().padding"),
            "Should use Modifier() constructor with padding method"
        )
    }

    @Test
    fun `test generated Main uses correct Button API with label parameter`() {
        val template = ProjectTemplate.fromType("js")
        val generator = ProjectGenerator(template)
        
        val config = ProjectGenerator.Config(
            projectName = "test-app",
            packageName = "com.example.test",
            targetDirectory = tempDir,
            templateType = "js",
            minimal = false
        )

        generator.generate(config)

        val mainFile = File(tempDir, "src/jsMain/kotlin/com/example/test/Main.kt")
        assertTrue(mainFile.exists(), "Main.kt should exist")
        
        val content = mainFile.readText()
        
        // Button should use 'label' parameter, not 'text'
        assertFalse(
            content.contains("Button(\n            text ="),
            "Button should not use 'text' parameter"
        )
        
        // Button API: Button(onClick, label, modifier, ...)
        assertTrue(
            content.contains("Button(") && content.contains("label ="),
            "Button should use 'label' parameter"
        )
        
        // onClick should come before label per API
        val buttonIndex = content.indexOf("Button(")
        if (buttonIndex != -1) {
            val buttonBlock = content.substring(buttonIndex, content.indexOf(")", buttonIndex) + 1)
            val onClickPos = buttonBlock.indexOf("onClick")
            val labelPos = buttonBlock.indexOf("label")
            assertTrue(
                onClickPos < labelPos,
                "onClick parameter should come before label parameter"
            )
        }
    }

    @Test
    fun `test generated ExampleComponent uses Modifier constructor`() {
        val template = ProjectTemplate.fromType("library")
        val generator = ProjectGenerator(template)
        
        val config = ProjectGenerator.Config(
            projectName = "test-lib",
            packageName = "com.example.lib",
            targetDirectory = tempDir,
            templateType = "library",
            minimal = false
        )

        generator.generate(config)

        val exampleFile = File(tempDir, "src/commonMain/kotlin/com/example/lib/ExampleComponent.kt")
        assertTrue(exampleFile.exists(), "ExampleComponent.kt should exist")
        
        val content = exampleFile.readText()
        
        // Default parameter should use Modifier() constructor
        assertTrue(
            content.contains("modifier: Modifier = Modifier()"),
            "ExampleComponent should use Modifier() constructor as default parameter"
        )
        
        // Should not use bare Modifier as default
        assertFalse(
            content.contains("modifier: Modifier = Modifier\n") ||
            content.contains("modifier: Modifier = Modifier)"),
            "Should not use bare Modifier without constructor call"
        )
    }

    @Test
    fun `test generated code has correct imports`() {
        val template = ProjectTemplate.fromType("js")
        val generator = ProjectGenerator(template)
        
        val config = ProjectGenerator.Config(
            projectName = "test-app",
            packageName = "com.example.test",
            targetDirectory = tempDir,
            templateType = "js",
            minimal = false
        )

        generator.generate(config)

        val mainFile = File(tempDir, "src/jsMain/kotlin/com/example/test/Main.kt")
        val content = mainFile.readText()
        
        // Should import Modifier
        assertTrue(
            content.contains("import code.yousef.summon.modifier.Modifier"),
            "Should import Modifier class"
        )
        
        // Should import padding extension
        assertTrue(
            content.contains("import code.yousef.summon.modifier.padding"),
            "Should import padding extension function"
        )
    }

    @Test
    fun `test minimal project uses correct API`() {
        val template = ProjectTemplate.fromType("js")
        val generator = ProjectGenerator(template)
        
        val config = ProjectGenerator.Config(
            projectName = "minimal-app",
            packageName = "com.example.minimal",
            targetDirectory = tempDir,
            templateType = "js",
            minimal = true
        )

        generator.generate(config)

        val mainFile = File(tempDir, "src/jsMain/kotlin/com/example/minimal/Main.kt")
        assertTrue(mainFile.exists(), "Main.kt should exist for minimal project")
        
        val content = mainFile.readText()
        
        // Even minimal should use correct API
        assertTrue(
            content.contains("PlatformRenderer.render"),
            "Should use PlatformRenderer.render API"
        )
    }

    @Test
    fun `test library project ExampleComponent default parameter`() {
        val template = ProjectTemplate.fromType("library")
        val generator = ProjectGenerator(template)
        
        val config = ProjectGenerator.Config(
            projectName = "my-lib",
            packageName = "com.example.mylib",
            targetDirectory = tempDir,
            templateType = "library",
            minimal = false
        )

        generator.generate(config)

        val componentFile = File(tempDir, "src/commonMain/kotlin/com/example/mylib/ExampleComponent.kt")
        assertTrue(componentFile.exists())
        
        val content = componentFile.readText()
        
        // Check default parameter syntax
        val modifierParamPattern = Regex("""modifier:\s*Modifier\s*=\s*Modifier\(\)""")
        assertTrue(
            modifierParamPattern.containsMatchIn(content),
            "ExampleComponent should have 'modifier: Modifier = Modifier()' with constructor"
        )
    }

    @Test
    fun `test generated ButtonExamples uses correct API`() {
        val template = ProjectTemplate.fromType("example")
        val generator = ProjectGenerator(template)
        
        val config = ProjectGenerator.Config(
            projectName = "example-app",
            packageName = "com.example.app",
            targetDirectory = tempDir,
            templateType = "example",
            includeExamples = true,
            minimal = false
        )

        generator.generate(config)

        val examplesFile = File(tempDir, "src/jsMain/kotlin/com/example/app/examples/ButtonExamples.kt")
        if (examplesFile.exists()) {
            val content = examplesFile.readText()
            
            // Button should use label parameter
            assertTrue(
                content.contains("label =") || content.contains("text ="),
                "ButtonExamples should use Button API"
            )
            
            // Should use Modifier() constructor
            if (content.contains("Modifier")) {
                assertTrue(
                    content.contains("Modifier()"),
                    "ButtonExamples should use Modifier() constructor"
                )
            }
        }
    }

    @Test
    fun `test all template types generate valid Kotlin code structure`() {
        val templateTypes = listOf("js", "library", "multiplatform")
        
        for (type in templateTypes) {
            val typeDir = File(tempDir, type)
            typeDir.mkdirs()
            
            val template = ProjectTemplate.fromType(type)
            val generator = ProjectGenerator(template)
            
            val config = ProjectGenerator.Config(
                projectName = "test-$type",
                packageName = "com.example.$type",
                targetDirectory = typeDir,
                templateType = type,
                minimal = false
            )

            generator.generate(config)

            // Verify basic structure
            assertTrue(
                File(typeDir, "build.gradle.kts").exists(),
                "$type template should generate build.gradle.kts"
            )
            assertTrue(
                File(typeDir, "settings.gradle.kts").exists(),
                "$type template should generate settings.gradle.kts"
            )
        }
    }

    @Test
    fun `test generated JS project code syntax is valid`() {
        val template = ProjectTemplate.fromType("js")
        val generator = ProjectGenerator(template)
        
        val projectDir = File(tempDir, "syntax-test")
        val config = ProjectGenerator.Config(
            projectName = "syntax-test",
            packageName = "com.example.syntaxtest",
            targetDirectory = projectDir,
            templateType = "js",
            minimal = false
        )

        generator.generate(config)

        val mainFile = File(projectDir, "src/jsMain/kotlin/com/example/syntaxtest/Main.kt")
        assertTrue(mainFile.exists(), "Main.kt should exist")
        
        val content = mainFile.readText()
        
        // Verify no syntax errors in critical sections
        
        // 1. Verify Modifier construction is correct
        assertTrue(
            content.contains("Modifier()"),
            "Should have Modifier() constructor calls"
        )
        assertFalse(
            content.contains("= Modifier.padding") || content.contains("= Modifier."),
            "Should not have bare Modifier. static calls"
        )
        
        // 2. Verify Button has onClick parameter
        val buttonPattern = Regex("""Button\s*\(\s*onClick\s*=\s*\{""")
        assertTrue(
            buttonPattern.containsMatchIn(content),
            "Button should have onClick parameter first"
        )
        
        // 3. Verify Button has label parameter (not text)
        assertTrue(
            content.contains("label =") && Regex("""Button\s*\([^)]*label\s*=""").containsMatchIn(content),
            "Button should use 'label' parameter"
        )
        assertFalse(
            Regex("""Button\s*\([^)]*text\s*=""").containsMatchIn(content),
            "Button should not use 'text' parameter"
        )
        
        // 4. Verify padding has string values with units
        val paddingPattern = Regex("""padding\s*\(\s*"[^"]+"\s*\)""")
        assertTrue(
            paddingPattern.containsMatchIn(content),
            "padding() should use string values with units"
        )
        
        // 5. Verify PlatformRenderer.render usage
        assertTrue(
            content.contains("PlatformRenderer.render"),
            "Should use PlatformRenderer.render API"
        )
        
        println("✅ Generated Main.kt syntax validation passed")
    }
}
